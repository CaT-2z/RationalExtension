package src;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Stream;

public class BasisSet extends AbstractSet<IBasisPart>
    implements Set<IBasisPart>, Cloneable, java.io.Serializable, Comparable<BasisSet>{


    final static BasisSet EMPTY = new BasisSet();

    @java.io.Serial
    static final long serialVersionUID = -4924742326767321676L;

    //What constitutes as equal depends on whether its addition or multiplication, therefore it uses a map that.. what??
    //Yes so one BasisSet is one base, but the naming convention is crap I know
    private transient HashMap<BasisPartKey, IBasisPart> map;

    private ExtendedRational remainder;

    private static final Object PRESENT = new Object();

    public BasisSet(){
        map = new HashMap<BasisPartKey, IBasisPart>();
        remainder = Rational.ONE;
    }

    public BasisSet(BasisSet o) {
        map = new HashMap<BasisPartKey, IBasisPart>(o.map);
        remainder = o.remainder;
    }


    //Creates new BasisSet, the Rational part is in the remainder, uses both parents remainder, don't forget to purge remainder
    //TODO: New version breaks this fix it
    //CREATES NEW!!!! BASIS SET!!!
    public BasisSet multiply(BasisSet o) {
        BasisSet a = new BasisSet(this);
        if (!remainder.equals(Rational.ONE) || !o.remainder.equals(Rational.ONE) ) {
            a.remainder = remainder.multiply(o.remainder);
            System.out.println("WARNING: multiplication with dirty BasisSets");
        }else{
            a.remainder = new ExtendedRational();
        }
        for (IBasisPart b: o.map.values()) {
            a.remainder = a.remainder.multiply(a.addMultiplicative(b));
        }
        return a;
    }




    //TODO: Breaks this too fix this
    public Rational useRemainder(){
        Rational ret = new Rational(remainder);
        remainder = Rational.ONE;
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
    public Stream<IBasisPart> stream() {
        return super.stream();
    }

    @Override
    public int size()  {
        return map.size();
    }

    //Adds exactly once, overrides parent, only use for creation

    //TODO: Fix this, IBasisPart should probably have its key in attribute.
    public boolean addAdditive(IBasisPart src){
        if(map.containsKey(src.getBase())){
            return false;
        }
        else map.put(src.getBase(), src);
        return true;
    }

    //Adds exactly once, overrides parent
    //TODO: Fix this
    ///\brief adds a SimpleBasisPart
    public boolean addAdditive(BigInteger k){
        if(map.containsKey(k)){
            return false;
        }
        else map.put(new BasisPartKey(k), new SimpleBasisPart(k, Rational.ZERO));
        return true;
    }

    //Don't forget, Rationals are immutable by default, need to do it right
    ///\brief Like multiplicative adding, except it won't normalise the value, used in rationalizing an extended rational.
    public void addMultSilently(@NotNull IBasisPart src){
        if(map.containsKey(src.getKey())){
            map.get(src.getKey())
        }
        else map.put(src.getKey(), src);
    }


    //Adds the basis' value to the basis in the map, returns remainder. If the key doesn't exist, it assumes (Rational) src in [0, 1[
    //TODO: Fix this, will return ExtendedRational (or will have two implementations)
    public Rational addMultiplicative(@NotNull IBasisPart src){
        if(map.containsKey(src.getKey())){
            return map.get(src.getKey()).addAndRemainder((Rational) src);
        }
        else map.put(src.getKey(), src);
        return Rational.ONE;
    }

    public IBasisPart get(BasisPartKey b){
        return map.get(b);
    }

    //The set doesn't contain itself (obviously)
     public String toString(){
            Iterator<Map.Entry<BigInteger,Basis>> i = map.entrySet().iterator();
            if (! i.hasNext())
                return "{}";

            StringBuilder sb = new StringBuilder();
            sb.append("[");
            Map.Entry<BigInteger,Basis> e = i.next();
            BigInteger key = e.getKey();
            Basis value = e.getValue();
            if(!value.equals(Rational.ZERO)) {
                sb.append(key);
                sb.append("^(");
                sb.append(value);
                sb.append(')');
            }
            for (;;) {
                e = i.next();
                key = e.getKey();
                value = e.getValue();
                if(!value.equals(Rational.ZERO)) {
                    sb.append(',').append(' ');
                    sb.append(key);
                    sb.append("^(");
                    sb.append(value);
                    sb.append(')');
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
            for (BigInteger i: map.keySet()) {
                Rational value = (Rational)set.map.getOrDefault(i, new Basis(i, Rational.ZERO));
                if(!value.equals( (Rational)map.get(i) )) return false;
                set.map.remove(i);
            }
            for (BigInteger i: set.map.keySet()){
                if(!((Rational)set.map.get(i)).equals(Rational.ZERO)) return false;
            }
            return true;
        }
        return false;
    }

    //TODO: Fix toString first
    @Override
    public int compareTo(@NotNull BasisSet o) {
        return equals(o) ? 0 : 1;
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
}
