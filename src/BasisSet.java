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

/**
 * Egy bázist reprezentáló osztály.
 */
public class BasisSet extends AbstractSet<IBasisPart>
    implements Set<IBasisPart>, Cloneable, java.io.Serializable, Comparable<BasisSet>{


    /**
     * Az "üres" bázis: az 1-el való szorzást jelképezi.
     */
    final static BasisSet EMPTY = new BasisSet();

//    @java.io.Serial
//    static final long serialVersionUID = -4924742326767321676L;

    //What constitutes as equal depends on whether its addition or multiplication, therefore it uses a map that.. what??
    //Yes so one BasisSet is one base, but the naming convention is crap I know

    /**
     * A bázisrészeket tartalmazó gyűjtemény
     */
    private transient HashMap<BasisPartKey, IBasisPart> map;

    /**
     * Egy szorzás utáni racionálisrész maradék.
     */
    private ExtendedRational remainder;

    /**
     * Üres konstruktor
     */
    public BasisSet(){
        map = new HashMap<BasisPartKey, IBasisPart>();
        remainder = new ExtendedRational(Rational.ONE);
    }

    /// fixed shallow copy

    /**
     * Másoló konstruktor
     * @param o másolandó
     */
    public BasisSet(BasisSet o) {
        map = (HashMap<BasisPartKey, IBasisPart>) o.map.clone();
        remainder = o.remainder;
    }


    //Creates new BasisSet, the Rational part is in the remainder, uses both parents remainder, don't forget to purge remainder
    //CREATES NEW!!!! BASIS SET!!!

    /**
     * Szorzó függvény. Vér, verejték és az Isteni jóindulat tartja össze.
     * @param o szorzandó
     * @return szorzat
     */
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

    /**
     * Visszaadja, hogy tartalmaz-e komplex bázist.
     * @return tartalmaz-e.
     */
    public ComplexBasisPart isComplex(){
        for(IBasisPart b: map.values()){
            if(b instanceof ComplexBasisPart) return (ComplexBasisPart) b;
        }
        return null;
    }


    /**
     * A szorzás során felhalmozódott racionális maradékot elhasználja.
     * @return A maradék.
     */
    public ExtendedRational useRemainder(){
        ExtendedRational ret = (ExtendedRational) remainder.clone();
        remainder = new ExtendedRational(Rational.ONE);
        return ret;
    }


    /**
     * Iterátor
     * @return iterator
     */
    @Override
    public Iterator<IBasisPart> iterator() {
        return map.values().iterator();
    }

    /**
     * Foreach függvény.
     * @param action The action to be performed for each element
     */
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

    /**
     * Mezei toArray függvény
     * @param generator a function which produces a new array of the desired
     *                  type and the provided length
     * @return Bázis array
     * @param <IBasis> Egy bázis
     */
    @Override
    public <IBasis> IBasis[] toArray(IntFunction<IBasis[]> generator) {
        return (IBasis[])map.keySet().toArray();
    }

    /**
     * Boilerplate removeIf
     * @param filter a predicate which returns {@code true} for elements to be
     *        removed
     * @return QED
     */
    @Override
    public boolean removeIf(Predicate<? super IBasisPart> filter) {
        return super.removeIf(filter);
    }

    /**
     * Boilerplate stream
     * @return QED
     */
    @Override
    public Stream<IBasisPart> stream() {
        return super.stream();
    }

    /**
     * Boilderplate parallelStream
     * @return QED
     */
    @Override
    public Stream<IBasisPart> parallelStream() {
        return super.parallelStream();
    }

    /**
     * Boilerplate size
     * @return map.size
     */
    @Override
    public int size()  {
        return map.size();
    }

    //Adds exactly once, overrides parent, only use for creation

    /**
     * Hozzáad és felülír egy bázisrészt a bázishoz
     * @param src
     * @return sikerült-e.
     */
    public boolean addAdditive(IBasisPart src){
        if(map.containsKey(src.getKey())){
            return false;
        }
        else map.put(src.getKey(), (IBasisPart) src.clone());
        return true;
    }

    //Adds exactly once, overrides parent
    ///\brief adds a SimpleBasisPart

    /**
     * Hozzáad egy egyszerű bázisrészt kulcs alapján.
     * @param k
     * @return sikerült-e.
     */
    public boolean addAdditive(BigInteger k){
        if(map.containsKey(new BasisPartKey(k))){
            return false;
        }
        else map.put(new BasisPartKey(k), new SimpleBasisPart(k, new Rational(Rational.ZERO)));
        return true;
    }

    //Don't forget, Rationals are immutable by default, need to do it right
    ///\brief Like multiplicative adding, except it won't normalise the value, used in rationalizing an extended rational.

    /**
     * Hozzászoroz egy bázisrészt az eredetihez, nem generál maradékot.
     * @param src
     */
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

    /**
     * Hozzászoroz egy bázisrészt az eredetihez, generál maradékot.
     * @param src
     * @return maradék
     */
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

    /**
     * Kulcs alapján ad bázisrészt
     * @param b kulcs
     * @return bázisrész
     */
    public IBasisPart get(BasisPartKey b){
        return map.get(b);
    }
    
    ///use for simplebasispart

    /**
     * Biginteger alapján visszaad egy egyszerű részt.
     * @param k kulcs
     * @return rész
     */
    public SimpleBasisPart getSimpleBasis(BigInteger k) { return (SimpleBasisPart) map.get(new BasisPartKey(k)); }

    //The set doesn't contain itself (obviously)

    /**ToString
     * @return A bázis részei és értékeik
     */
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

    /**
     * Egyenlő-e az üres bázissal.
     * @return egyenlő-e
     */
    public boolean isNone(){
        Iterator<IBasisPart> it = map.values().iterator();
        while(it.hasNext()){
            if(it.next().getValue().numerator.compareTo(BigInteger.ZERO) == 0) continue;
            return false;
        }
        return true;
    }


    //Overrides equals function, only use on BasisSet

    /**
     * Equals függvény, két bázis egyenlő, ha a nem nulla értékű részeik megegyeznek.
     * @param src object to be compared for equality with this set
     * @return egyenlők-e
     */
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
                if(!(set.map.get(i)).getValue().equals(Rational.ZERO)) return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Boilerplate spliterator
     * @return spliterator
     */
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

    /**
     * Mély klónozó
     * @return klón
     */
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

    /**
     * Visszaadja az egészéhez tartozó komplex kulcsot.
     * @return kulcs
     */
    public BasisPartKey getKey(){
        return new BasisPartKey(new ArrayList<BasisPartKey>(map.keySet()));
    }

    /**
     * Összehasonlító. Elsődlegesen méret szerint, aztán egyenként összemér.
     * @param o the object to be compared.
     * @return comparison
     */
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

    /**
     * HashCode függvény implementáció. A HashCode a nemnulla elemek mennyisége.
     */
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

    ///Do Base first

    /**
     * A bázis értéke double-ben
     * @return
     */
    public double toDouble(){
        double ret = 1;
        for (IBasisPart part: map.values()) {
            ret *= part.toDouble();
        }
        return ret;
    }
}
