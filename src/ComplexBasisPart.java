package src;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/// Complex basis component that is made up of an extended rational.
public class ComplexBasisPart implements IBasisPart{

    Rational value;
    private ExtendedRational base;
    private ExtendedRational inverse;

    ComplexBasisPart(){
        value = Rational.ZERO;
    }
    ComplexBasisPart(ExtendedRational b, Rational o){
        value = (Rational) o.clone();
        base = b;
    }

    ///\brief Finds the algebraic conjugate of the extended rational
    ///\return Changes the inverse field of the object to the result. Only called when needed, because its expensive.
    /// Monoid -> monoidSubscriber ->
    public void findInverse(){
        /// Goes over the vector, gets each basissets,
        /// The Extended Rational it will change over and over
        ExtendedRational multiplier = (ExtendedRational) base.clone();

        /// List of "root" bases permutations
        LinkedList<Monoid> monoids = new LinkedList<Monoid>();

        ///Passes by reference, hopefully will be able to change value without hiccups.
        Iterator<BasisSet> it = multiplier.data.keySet().iterator();
        while(it.hasNext()){
            //TODO: YOU WERE HERE
            Iterator<IBasisPart> baseIt = it.next().iterator();
        }

        /// List of SimpleBasisParts by reference I hope
        /// OR we can bank on the fact they'll be multiplied either way
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

    ///\brief Helper class, helps permute through the gallois group of the Extended Rational (with redundancy).
    public class Monoid implements Comparable<Monoid> {
        private ArrayList<BasisSet> subscriber;
        IBasisPart searcher;

        Monoid(IBasisPart n){
            searcher = n;
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
