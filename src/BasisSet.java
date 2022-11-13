package src;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;


/// Maybe I should remove remainder based stuff
/// Remainder is needed because
public class BasisSet extends AbstractSet<IBasisPart>
    implements Set<IBasisPart>, Cloneable, java.io.Serializable, Comparable<BasisSet>{


    final static BasisSet EMPTY = new BasisSet();

//    @java.io.Serial
//    static final long serialVersionUID = -4924742326767321676L;

    //What constitutes as equal depends on whether its addition or multiplication, therefore it uses a map that.. what??
    //Yes so one BasisSet is one base, but the naming convention is crap I know
    private transient HashMap<BasisPartKey, IBasisPart> map;

    private ExtendedRational remainder;

    private static final Object PRESENT = new Object();

    public BasisSet(){
        map = new HashMap<BasisPartKey, IBasisPart>();
        remainder = new ExtendedRational(Rational.ONE);
    }

    /// fixed shallow copy
    public BasisSet(BasisSet o) {
        map = (HashMap<BasisPartKey, IBasisPart>) o.map.clone();
        remainder = o.remainder;
    }


    //Creates new BasisSet, the Rational part is in the remainder, uses both parents remainder, don't forget to purge remainder
    //CREATES NEW!!!! BASIS SET!!!
    public BasisSet multiply(BasisSet o) {
        BasisSet a = (BasisSet) this.clone();
        if (!remainder.equals(Rational.ONE) || !o.remainder.equals(Rational.ONE) ) {
            a.remainder = remainder.multiply(o.remainder);
            System.out.println("WARNING: multiplication with dirty BasisSets");
        }else{
            // Rationals pointing to same object
            a.remainder = new ExtendedRational(new Rational(Rational.ONE));
        }

        ComplexBasisPart comp;

        ///If I ever refactor this shit Im going to fucking piss myself.
        if((comp = a.isComplex()) != null){
            for(IBasisPart b: o.map.values()){
                if(!(b instanceof RootOfUnityBasisPart)){
                    comp = comp.multiplyByPart(b);
                } else {
                    a.remainder = a.remainder.multiply(a.addMultiplicative(b));
                }
            }
            a.addMultiplicative(comp);
        }

        ///TODO: maybe remove 0s here
        for (IBasisPart b: o.map.values()) {
            a.remainder = a.remainder.multiply(a.addMultiplicative(b));
        }

        return a;
    }

    public ComplexBasisPart isComplex(){
        for(IBasisPart b: map.values()){
            if(b instanceof ComplexBasisPart) return (ComplexBasisPart) b;
        }
        return null;
    }


    public ExtendedRational useRemainder(){
        ExtendedRational ret = (ExtendedRational) remainder.clone();
        remainder = new ExtendedRational(Rational.ONE);
        return ret;
    }


    @Override
    public Iterator<IBasisPart> iterator() {
        return map.values().iterator();
    }

    @Override
    public void forEach(Consumer<? super IBasisPart> action) {
        map.forEach(new BiConsumer<BasisPartKey, IBasisPart>() {
            @Override
            public void accept(BasisPartKey o, IBasisPart basis) {
                action.accept(basis);
            }
        });
    }

    //TODO: Serialization

    @Override
    public <IBasis> IBasis[] toArray(IntFunction<IBasis[]> generator) {
        return (IBasis[])map.keySet().toArray();
    }

    @Override
    public boolean removeIf(Predicate<? super IBasisPart> filter) {
        return super.removeIf(filter);
    }

    @Override
    public Stream<IBasisPart> stream() {
        return super.stream();
    }

    @Override
    public Stream<IBasisPart> parallelStream() {
        return super.parallelStream();
    }

    @Override
    public int size()  {
        return map.size();
    }

    //Adds exactly once, overrides parent, only use for creation
    public boolean addAdditive(IBasisPart src){
        if(map.containsKey(src.getKey())){
            return false;
        }
        else map.put(src.getKey(), (IBasisPart) src.clone());
        return true;
    }

    //Adds exactly once, overrides parent
    ///\brief adds a SimpleBasisPart
    public boolean addAdditive(BigInteger k){
        if(map.containsKey(new BasisPartKey(k))){
            return false;
        }
        else map.put(new BasisPartKey(k), new SimpleBasisPart(k, new Rational(Rational.ZERO)));
        return true;
    }

    //Don't forget, Rationals are immutable by default, need to do it right
    ///\brief Like multiplicative adding, except it won't normalise the value, used in rationalizing an extended rational.
    public void addMultSilently(@NotNull IBasisPart src){
        if(map.containsKey(src.getKey())){
            map.get(src.getKey()).addSilently(src.getValue());
        }
        else{
            if(src.getValue().equals(Rational.ZERO)) return;
            map.put(src.getKey(), src);
        }
    }


    //Adds the basis' value to the basis in the map, returns remainder. If the key doesn't exist, it assumes (Rational) src in [0, 1[
    public ExtendedRational addMultiplicative(@NotNull IBasisPart src){
        if(map.containsKey(src.getKey())){
            ExtendedRational rem = map.get(src.getKey()).addAndRemainder(src.getValue());
            if(map.get(src.getKey()).getValue().equals(Rational.ZERO)) map.remove(src.getKey());
            return rem;
        }
        else{

            IBasisPart clone = (IBasisPart) src.clone();
             if(clone.getKey().equals(new BasisPartKey(BigInteger.valueOf(-1)))){
                 /// Potentially poop
                 ExtendedRational rem = clone.addAndRemainder(Rational.ZERO);
                 if(!clone.getValue().equals(Rational.ZERO)) map.put(clone.getKey(), clone);
                 return rem;
             }
             if(!clone.getValue().equals(Rational.ZERO)) map.put(clone.getKey(), clone);
        }
        return new ExtendedRational(Rational.ONE);
    }

    public IBasisPart get(BasisPartKey b){
        return map.get(b);
    }
    
    ///use for simplebasispart
    public SimpleBasisPart getSimpleBasis(BigInteger k) { return (SimpleBasisPart) map.get(new BasisPartKey(k)); }

    //The set doesn't contain itself (obviously)
     public String toString(){
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            Iterator<Map.Entry<BasisPartKey, IBasisPart>> i = map.entrySet().iterator();
            if (! i.hasNext())
                return "{}";
            boolean first = true;
            for (;;) {
                Map.Entry<BasisPartKey, IBasisPart> e = i.next();
                BasisPartKey key = e.getKey();
                IBasisPart value = e.getValue();
                if(!value.getValue().equals(Rational.ZERO)) {
                    if(first) {
                        first = false;
                    }
                    else{
                        sb.append(',').append(' ');
                    }
                    sb.append(value);
                }
                if (! i.hasNext())
                    return sb.append(']').toString();
            }
        }

    public boolean isNone(){
        Iterator<IBasisPart> it = map.values().iterator();
        while(it.hasNext()){
            if(it.next().getValue().numerator.compareTo(BigInteger.ZERO) == 0) continue;
            return false;
        }
        return true;
    }


    //Overrides equals function, only use on BasisSet
    @Override
    public boolean equals(Object src){
        if(src instanceof BasisSet){
            BasisSet set = new BasisSet((BasisSet)src);
            for (BasisPartKey i: map.keySet()) {
                Rational value = set.map.getOrDefault(i, new SimpleBasisPart(BigInteger.TWO, Rational.ZERO)).getValue(); // normally you should create a new Rational, but this is immutable;
                if(!value.equals( map.get(i).getValue() )) return false;
                set.map.remove(i);
            }
            for (BasisPartKey i: set.map.keySet()){
                if(!((Rational)set.map.get(i)).equals(Rational.ZERO)) return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public Spliterator<IBasisPart> spliterator() {
        return map.values().spliterator();
    }

//    BasisSet multiplynew(BasisSet o){
//        BasisSet a = new BasisSet(this);
//        for (IBasisPart part : o.map.values()
//             ) {
//            if(part instanceof SimpleBasisPart){
//                a.remainder = a.remainder.multiply(a.addTo(b));
//            }
//            else if(part instanceof ComplexBasisPart){
//
//            }
//        }
//    }

    @Override
    public Object clone(){
        BasisSet ret = new BasisSet();
        Iterator<Map.Entry<BasisPartKey, IBasisPart>> it = map.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<BasisPartKey,IBasisPart> ent = it.next();
            ret.map.put((BasisPartKey) ent.getKey().clone(),(IBasisPart) ent.getValue().clone());
        }
        return ret;
    }

    public BasisPartKey getKey(){
        return new BasisPartKey(new ArrayList<BasisPartKey>(map.keySet()));
    }

    @Override
    public int compareTo(@NotNull BasisSet o) {
        if(map.size() == o.map.size()){
            Map.Entry<BasisPartKey, IBasisPart>[] A = (Map.Entry<BasisPartKey, IBasisPart>[]) map.entrySet().stream().sorted().toArray();
            Map.Entry<BasisPartKey, IBasisPart>[] B = (Map.Entry<BasisPartKey, IBasisPart>[]) o.map.entrySet().stream().sorted().toArray();
            for(int i = 0; i < A.length; i++){
                if(!A[i].getValue().equals(B[i].getValue())) return A[i].getValue().compareTo(B[i].getValue());
            }
            return 0;
        }
        return map.size() > o.map.size() ? 1 : -1;
    }

    @Override
    public int hashCode(){
        int result = 17;
        int a = 0;
        for (IBasisPart b: map.values()){
            if(!b.getValue().equals(Rational.ZERO)) a++;
        }
        result = 31*result + a;
        return result;
    }
}
