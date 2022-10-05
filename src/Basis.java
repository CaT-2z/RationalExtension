package src;

import java.math.BigInteger;
import java.util.HashSet;

public class Basis extends Rational{
    private BigInteger base;

    public BigInteger getBase(){
        return base;
    }

    public Basis(BigInteger b, Rational e){
        super(e);
        base = b;
    }

    //Adds e to the fraction, returns with the rational leftover value, fraction stays within [0,1[
    //DOESNT RETURN WITH THE SUM OF THE FRACTIONS
    //TODO: Maybe rename this to avoid confusion?
    @Override
    public Rational add(Rational e){
        Rational sum = super.add(e);
        BigInteger[] div = sum.getNumerator().divideAndRemainder(sum.getDenominator());
        denominator = sum.denominator;
        if(div[0].compareTo(BigInteger.ZERO) != -1 && div[1].compareTo(BigInteger.ZERO) != -1){
            numerator = div[1];
            return new Rational(base.pow(div[0].intValue()), BigInteger.ONE);
        }else{
            numerator = div[1].add(denominator);
            return new Rational(BigInteger.ONE, base.pow(div[0].abs().add(BigInteger.ONE).intValue()));
        }
    }

}
