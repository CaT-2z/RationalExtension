package src;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.math.BigInteger;

//TODO: gcd with Rational.ZERO is weird;

/**
 * Racionális szám rreprezentáló
 */
public class Rational implements Cloneable, Serializable {
    /**
     * számláló
     */
    protected BigInteger numerator;
    /**
     * nevező
     */
    protected BigInteger denominator;

    /**
     * visszaadja a számlálót
     * @return számláló
     */
    public BigInteger getNumerator(){
        return numerator;
    }

    /**
     * visszaadja a nevezőt
     * @return nevező
     */
    public BigInteger getDenominator(){
        return denominator;
    }

    /**
     * Az 1-vel egyenlő racionális szám
     */
    public static final Rational ONE = new Rational(BigInteger.ONE,BigInteger.ONE);
    /**
     * Az 0-vel egyenlő racionális szám
     */
    public static final Rational ZERO = new Rational(BigInteger.ZERO, BigInteger.ONE);
    /**
     * Az 0.5-vel egyenlő racionális szám
     */
    public static final Rational HALF = new Rational(BigInteger.ONE, BigInteger.TWO);
    /**
     * Az 0.25-vel egyenlő racionális szám
     */
    public static final Rational QUARTER = new Rational(BigInteger.ONE, BigInteger.valueOf(4));

    /**
     * Konstruktor
     * @param numerate számláló
     * @param denominate nevező
     */
    Rational(BigInteger numerate, @NotNull BigInteger denominate){
        if(denominate.compareTo(BigInteger.ZERO) == 0){
            throw new ArithmeticException("0 in denominator exception");
        }
        numerator = numerate;
        denominator = denominate;
    }

    /**
     * konstruktor intigerekkel
     * @param numerate számláló
     * @param denominate nevező
     */
    public Rational(int numerate, @NotNull int denominate){
        if(denominate == 0){
            throw new ArithmeticException("0 in denominator exception");
        }
        numerator = BigInteger.valueOf(numerate);
        denominator = BigInteger.valueOf(denominate);
    }

    /**
     * Másoló konstruktor
     * @param o másolandó
     */
    Rational(Rational o){
        numerator = o.numerator;
        denominator = o.denominator;
    }

    /**
     * toString
     * @return String reprezentáció
     */
    public String toString(){
        return String.format("%d / %d", numerator, denominator);
    }

    /**
     * Összeszoroz két számot
     * @param src szorzandó
     * @return szorzat
     */
    public Rational multiply(@NotNull Rational src){
        BigInteger newnum = numerator.multiply(src.numerator);
        BigInteger newden = denominator.multiply(src.denominator);
        return new Rational(newnum, newden).simplify();
    }

    /**
     * Összead két számot
     * @param src összeadandó
     * @return összeg
     */
    // a/b + c/d = (ad+cb)/bd
    public Rational add(@NotNull Rational src){
        BigInteger newnum = numerator.multiply(src.denominator).add(src.numerator.multiply(denominator));
        BigInteger newden = denominator.multiply(src.denominator);
        return new Rational(newnum, newden).simplify();
    }

    //Returns Rational with value = -1*this

    /**
     * A szám additív inverze
     * @return this*-1
     */
    public Rational negate(){
        return new Rational(numerator.negate(), denominator);
    }

    //Returns Rational with value = this^-1

    /**
     * A szám multiplikatív inverze
     * @return 1/this
     */
    public Rational inverse(){
        if(numerator.compareTo(new BigInteger("0")) == 0){
            throw new ArithmeticException("inverse of 0 exception");
        }
        return new Rational(denominator, numerator);
    }

    /**
     * Elosztja a számot a paraméterben kapottal
     * @param src osztó
     * @return eredmény
     */
    public Rational divide(@NotNull Rational src){
        return multiply(src.inverse());
    }

    /**
     * Összehasonlító
     * @param src összehasonlítandó
     * @return összehasonlítás
     */
    public int compareTo(Rational src){
        if(src == null) return 1;
        return(numerator.multiply(src.denominator).compareTo(src.numerator.multiply(denominator)));
    }

    /**
     * Egyszerusíti relatív prímekre a számlálót és a nevezőt
     * @return egyszerű alak
     */
    public Rational simplify(){
        BigInteger newnum = numerator;
        BigInteger newdet = denominator;

        if(numerator.bitLength() > 32 && denominator.bitLength() > 32){
            double val = numerator.doubleValue()/denominator.doubleValue();
            val *= 100000;
            newnum = BigInteger.valueOf((long)val);
            newdet = BigInteger.valueOf(100000);
        }


        BigInteger gcd;
        if(newnum.compareTo(BigInteger.ZERO) == 0){
            return Rational.ZERO;
        } else{
            gcd = newnum.gcd(newdet);
        }
        return new Rational(newnum.divide(gcd), newdet.divide(gcd));
    }

    /**
     * Egyenlőség tesztelő
     * @param src másik szám
     * @return egyenlő e
     */
    public boolean equals(Rational src){
        if(src == null) return false;
        return compareTo(src) == 0;
    }

    ///\brief clones, Bigintiger is immutable.
    ///\return cloned object.

    /**
     * Klónozó
     * @return klón
     */
    @Override
    public Object clone(){
        return new Rational( numerator,denominator);
    }

    /**
     * Hatványozó
     * @param b egész számú hatvány
     * @return hatvány
     */
    public Rational pow(int b){
        return new Rational(numerator.pow(b),denominator.pow(b));
    }

    /**
     * Kiszámolja a double értékét a számnak
     * @return érték
     */
    public double toDouble(){
        return numerator.doubleValue() / denominator.doubleValue();
    }

}
