package src;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

//TODO: gcd with Rational.ZERO is weird;
public class Rational implements Cloneable{
    protected BigInteger numerator;
    protected BigInteger denominator;

    public BigInteger getNumerator(){
        return numerator;
    }
    public BigInteger getDenominator(){
        return denominator;
    }

    public static final Rational ONE = new Rational(BigInteger.ONE,BigInteger.ONE);
    public static final Rational ZERO = new Rational(BigInteger.ZERO, BigInteger.ONE);

    public static final Rational HALF = new Rational(BigInteger.ONE, BigInteger.TWO);

    Rational(BigInteger numerate, @NotNull BigInteger denominate){
        if(denominate.compareTo(BigInteger.ZERO) == 0){
            throw new ArithmeticException("0 in denominator exception");
        }
        numerator = numerate;
        denominator = denominate;
    }

    Rational(Rational o){
        numerator = o.numerator;
        denominator = o.denominator;
    }

    public String toString(){
        return String.format("%d / %d", numerator, denominator);
    }

    public Rational multiply(@NotNull Rational src){
        BigInteger newnum = numerator.multiply(src.numerator);
        BigInteger newden = denominator.multiply(src.denominator);
        return new Rational(newnum, newden).simplify();
    }

    // a/b + c/d = (ad+cb)/bd
    public Rational add(@NotNull Rational src){
        BigInteger newnum = numerator.multiply(src.denominator).add(src.numerator.multiply(denominator));
        BigInteger newden = denominator.multiply(src.denominator);
        return new Rational(newnum, newden).simplify();
    }

    //Returns Rational with value = -1*this
    public Rational negate(){
        return new Rational(numerator.negate(), denominator);
    }

    //Returns Rational with value = this^-1
    public Rational inverse(){
        if(numerator.compareTo(new BigInteger("0")) == 0){
            throw new ArithmeticException("inverse of 0 exception");
        }
        return new Rational(denominator, numerator);
    }

    public Rational divide(@NotNull Rational src){
        return multiply(src.inverse());
    }

    public int compareTo(Rational src){
        if(src == null) return 1;
        return(numerator.multiply(src.denominator).compareTo(src.numerator.multiply(denominator)));
    }

    public Rational simplify(){
        BigInteger gcd;
        if(numerator.compareTo(BigInteger.ZERO) == 0){
            return Rational.ZERO;
        } else{
            gcd = numerator.gcd(denominator);
        }
        return new Rational(numerator.divide(gcd), denominator.divide(gcd));
    }

    public boolean equals(Rational src){
        if(src == null) return false;
        return compareTo(src) == 0;
    }

    ///\brief clones, Bigintiger is immutable.
    ///\return cloned object.
    @Override
    public Object clone(){
        return new Rational( numerator,denominator);
    }

}
