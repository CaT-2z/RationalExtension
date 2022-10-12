package src;

import java.math.BigInteger;

public class SimpleBasisPart implements IBasisPart {

    Rational value;
    private BigInteger base;

    public Rational getValue(){
        return value;
    }

    public SimpleBasisPart(BigInteger b, Rational e){
        value = e;
        base = b;
    }

    @Override
    public BasisPartKey getKey() {
        return new BasisPartKey(base);
    }

    ///\brief Adds the rational to the value, and doesn't normalise
    public void addSilent(Rational e){
        value = value.add(e);
    }

    //Adds e to the fraction, returns with the rational leftover value, fraction stays within [0,1[
    //DOESNT RETURN WITH THE SUM OF THE FRACTIONS
    //TODO: Maybe rename this to avoid confusion?
    public Rational addAndRemainder(Rational e){
        Rational sum = value.add(e);
        BigInteger[] div = sum.getNumerator().divideAndRemainder(sum.getDenominator());
        value.denominator = sum.denominator;
        if(div[0].compareTo(BigInteger.ZERO) != -1 && div[1].compareTo(BigInteger.ZERO) != -1){
            value.numerator = div[1];
            return new Rational(base.pow(div[0].intValue()), BigInteger.ONE);
        }else{
            value.numerator = div[1].add(value.denominator);
            return new Rational(BigInteger.ONE, base.pow(div[0].abs().add(BigInteger.ONE).intValue()));
        }
    }

    /// implements cloneable, has immutable data fields.
    public Object clone(){
        SimpleBasisPart p = new SimpleBasisPart();
        p.value = (Rational) value.clone();
        p.base = base;
        return p;
    }

}
