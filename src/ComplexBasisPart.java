package src;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.*;

/// Complex basis component that is made up of an extended rational.
public class ComplexBasisPart implements IBasisPart{

    private Rational value;
    private ExtendedRational base;
    private ExtendedRational inverse;

    public ExtendedRational getInverse() {
        if(inverse == null){
            findInverse();
        }
        return inverse;
    }

    private Rational invScalar;

    ComplexBasisPart(){
        value = Rational.ZERO;
        inverse = null;
    }
    ComplexBasisPart(ExtendedRational b, Rational o){
        value = (Rational) o.clone();
        base = b;
        inverse = null;
    }

    @Override
    public ExtendedRational addAndRemainder(Rational e) {
        Rational sum = value.add(e);
        BigInteger[] div = sum.getNumerator().divideAndRemainder(sum.getDenominator());
        value.denominator = sum.denominator;
        // if positive
        if(div[0].compareTo(BigInteger.ZERO) != -1 && div[1].compareTo(BigInteger.ZERO) != -1){
            value.numerator = div[1];
            ExtendedRational er = new ExtendedRational(Rational.ONE);
            for(int i = 0; i < div[0].intValue(); i++){
                er = er.multiply(base);
            }
            return er;
            // if negative, oh boy
        }else{
            value.numerator = div[1].add(value.denominator);
            ExtendedRational er = new ExtendedRational(Rational.ONE);
            if(inverse == null) findInverse();
            for(int i = 0; i < div[0].abs().add(BigInteger.ONE).intValue(); i++){
                er = er.multiply(inverse);
            }
            return er;
        }
    }

    @Override
    public void addSilently(Rational e) {
        value = value.add(e);
    }

    @Override
    public BasisPartKey getKey() {
        Iterator<BasisSet> it = base.data.keySet().iterator();
        ArrayList<BasisPartKey> keys = new ArrayList<BasisPartKey>();
        while(it.hasNext()){
            keys.add(it.next().getKey());
        }
        return new BasisPartKey(keys);
    }

    @Override
    public Rational getValue() {
        return value;
    }

    ///\brief Finds the algebraic conjugate of the extended rational
    ///\return Changes the inverse field of the object to the result. Only called when needed, because its expensive.
    /// Monoid -> monoidSubscriber ->
    public void findInverse(){
        /// Goes over the vector, gets each basissets,
        /// The Extended Rational it will change over and over
        ExtendedRational multiplier = (ExtendedRational) base.clone();

        /// List of "root" bases permutations
        /// Because Set doesn't have a getter
        HashMap<BasisPartKey, Monoid> monoids = new HashMap<BasisPartKey, Monoid>();

        ///Passes by reference, hopefully will be able to change value without hiccups.
        ///Puts monoids into the list
        Iterator<BasisSet> it = multiplier.data.keySet().iterator();
        recursiveMonoidSubscriber(it, monoids);


        ///Showtime
        inverse = new ExtendedRational(Rational.ONE);
        isStarting = true;
        ListIterator<Monoid> monoidIterator = monoids.values().stream().toList().listIterator();
        recursiveMonoidMultiplier(monoidIterator, multiplier);

        ///May need to simplify by RootofUnity
        ///TODO: Put root of unity here



        /// This won't need a root of unity check I think-> It (should) all cancel out, but do check the edge case where E(zeta) = -1*rational

        
    }

    private void simplifyExtendedRational(ExtendedRational er){
        LinkedList<Map.Entry<BasisSet, Rational>> entries = new LinkedList<Map.Entry<BasisSet, Rational>>(er.data.entrySet());
        for(int i = 0; i < entries.size(); i++){
            RootOfUnityBasisPart root = (RootOfUnityBasisPart) entries.get(i).getKey().get(new BasisPartKey(BigInteger.valueOf(-1)));
            if(root != null){
                ///TODO: YOU WERE HERE
            }
        }
    }

    ///\brief Helper function for inverse finder: finds the rational conjugate of the algebraic number
    /// WTF was I doing here?? How is this supposed to work??
    /// The last one doesnt step forward... shouldnt step back
    private boolean isStarting;
    private void recursiveMonoidMultiplier(ListIterator<Monoid> it, ExtendedRational multiplier){
        if(it.hasNext()){
            Monoid monoid = it.next();
            for(int i = 0; i < monoid.searcher.getValue().denominator.intValue(); i++){
                /// First go in, then change - permutations with null values
                recursiveMonoidMultiplier(it, multiplier);
                monoid.changeSubscriber();
            }
            it.previous();
        }
        else{
            ///TODO: START IS LOCAL, DOES NOT CHANGE
            if(!isStarting){
                /// multiply
                /// TODO: mutable ExtendedRational multiplier OR wrapper wtf?? WTF DID I MEAN BY THIS??
                /// What am I multiplying and how does it get here
                inverse = inverse.multiply(multiplier);
            }else{
                isStarting = false;
            }
        }
    }

    private void recursiveMonoidSubscriber(Iterator<BasisSet> it, HashMap<BasisPartKey, Monoid> monoids){
        while(it.hasNext()){
            BasisSet b = it.next();
            Iterator<IBasisPart> baseIt = b.iterator();
            while(baseIt.hasNext()){

                IBasisPart part = baseIt.next();
                if(part.getValue().equals(Rational.ZERO)) continue;
                ///Subscribes/creates right monoid
                ///Assumes there are no empty BasisSets
                if(!monoids.containsKey(part.getKey())){
                    monoids.put(part.getKey(), new Monoid(part));
                }else{
                    monoids.get(part.getKey()).addToPart(part);
                }
                monoids.get(part.getKey()).addSubscriber(b);
                ///recurses if the base is also a ComplexBasisPart
                if(part instanceof ComplexBasisPart){
                    recursiveMonoidSubscriber(((ComplexBasisPart) part).base.data.keySet().iterator(), monoids);
                }
            }
        }
    }

    public String toString(){
        return String.format(base.toString() + "^" + value.toString());
    }

    /// implements clone, checks if inverse exists
    public Object clone(){
        ComplexBasisPart ret = new ComplexBasisPart();
        ret.value = (Rational) value.clone();
        ret.base = (ExtendedRational) base.clone();
        if(inverse == null){
            ret.inverse = null;
        }else{
            ret.inverse = (ExtendedRational) inverse.clone();
        }
        return ret;
    }

    @Override
    public int compareTo(@NotNull IBasisPart o) {
        if(o instanceof SimpleBasisPart || o instanceof RootOfUnityBasisPart) {
            return 1;
        }
        else{
            ComplexBasisPart other = (ComplexBasisPart) o;
            if(base.data.size() != other.base.data.size()) return base.data.size() > other.base.data.size() ? 1 : -1;
            Map.Entry<BasisSet, Rational>[] A = (Map.Entry<BasisSet, Rational>[])base.data.entrySet().stream().sorted().toArray();
            Map.Entry<BasisSet, Rational>[] B = (Map.Entry<BasisSet, Rational>[])other.base.data.entrySet().stream().sorted().toArray();
            for(int i = 0; i < A.length; i++){
                if(A[i].getKey() != B[i].getKey()) return A[i].getKey().compareTo(B[i].getKey());
                if(A[i].getValue() != B[i].getValue()) return A[i].getValue().compareTo(B[i].getValue());
            }
            return 0;
        }
    }

    ///\brief Helper class, helps permute through the gallois group of the Extended Rational (with redundancy).
    private class Monoid implements Comparable<Monoid> {
        private ArrayList<BasisSet> subscriber;
        IBasisPart searcher;

        Rational val;

        Monoid(IBasisPart n){
            searcher = n;
            val = new Rational(Rational.ONE);
            val.denominator = n.getValue().denominator;
            subscriber = new ArrayList<>();
        }

        void addToPart(IBasisPart src){
            BigInteger uj = val.denominator.multiply(src.getValue().denominator).divide(val.denominator.gcd(src.getValue().denominator));
            val.denominator = uj;
        }

        ///\brief Adds a BasisSet to the list of subscibers.
        public void addSubscriber(BasisSet s){
            subscriber.add(s);
        }

        ///\brief Adds RootofUnity to the BasisSet equal to the 1/denominator of the subscribed basis. I wonder if this would still work with starter values in C
        public void changeSubscriber(){
            for (BasisSet b: subscriber){
                b.addMultSilently(new RootOfUnityBasisPart(new Rational(BigInteger.ONE, searcher.getValue().denominator)));
            }
        }

        @Override
        public int compareTo(@NotNull ComplexBasisPart.Monoid o) {
            int val = searcher.getKey().compareTo(o.searcher.getKey());
            if(val != 0){
                return val;
            }
            return searcher.getValue().denominator.compareTo(o.searcher.getValue().denominator);
        }

    }

}
