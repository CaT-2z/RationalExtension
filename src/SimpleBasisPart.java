package src;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

/**
 * Egyszerű bázis rész reprezentációja.
 */
public class SimpleBasisPart implements IBasisPart {

    /**
     *  A bázisrész hatványértéke
     */
    private Rational value;

    /**
     * A bázisrész alapja
     */
    private BigInteger base;

    /**
     * Visszaadja a hatványértékét
     * @return érték
     */
    public Rational getValue(){
        return value;
    }

    /**
     * Konstruktor
     * @param b alap
     * @param e érték
     */
    public SimpleBasisPart(BigInteger b, Rational e){
        value = e;
        base = b;
    }

    /**
     * Hozzáad, de nem normalizál.
     * @param e hozzáadandó
     */
    @Override
    public void addSilently(Rational e) {
        value = value.add(e);
    }

    /**
     * Visszaadja a kulcsát
     * @return kulcs
     */
    @Override
    public BasisPartKey getKey() {
        return new BasisPartKey(base);
    }

    //Adds e to the fraction, returns with the rational leftover value, fraction stays within [0,1[
    //DOESNT RETURN WITH THE SUM OF THE FRACTIONS
    //TODO: Maybe rename this to avoid confusion?

    /**
     * Hozzáad, normalizál 1-re, visszaadja a maradékot.
     * @param e hozzáadandó
     * @return maradék
     */
    public ExtendedRational addAndRemainder(Rational e){
        ///Adds
        Rational sum = value.add(e);
        ///Divides
        BigInteger[] div = sum.getNumerator().divideAndRemainder(sum.getDenominator());
        ///knows the denominator will be the same
        value.denominator = sum.denominator;
        if(div[0].compareTo(BigInteger.ZERO) != -1 && div[1].compareTo(BigInteger.ZERO) != -1){
            /// Modulo operation has negatives, we dont want that
            value.numerator = div[1];
            return new ExtendedRational(new Rational(base.pow(div[0].intValue()), BigInteger.ONE));
        }else{
            value.numerator = div[1].add(value.denominator);
            return new ExtendedRational(new Rational(BigInteger.ONE, base.pow(div[0].abs().add(BigInteger.ONE).intValue())));
        }
    }

    /// implements cloneable, has immutable data fields.

    /**
     * Klónozó
     * @return klón
     */
    public Object clone(){
        SimpleBasisPart p = new SimpleBasisPart(base, (Rational) value.clone());
        return p;
    }

    /**
     * Átváltja az alapot egy algebraikus számmá
     * @return számérték
     */
    @Override
    public ExtendedRational toExtendedRational() {
        return ExtendedRational.fromSimple(base.intValue(), 1);
    }

    /**
     * Összehasonlító
     * @param o the object to be compared.
     * @return összehasonlítás
     */
    @Override
    public int compareTo(@NotNull IBasisPart o) {
        if(o instanceof ComplexBasisPart) return -1;
        if(o  instanceof RootOfUnityBasisPart) return 1;
        SimpleBasisPart other = (SimpleBasisPart) o;
        if(base != other.base) return base.compareTo(other.base);
        return value.compareTo(other.value);
    }

    /**
     * Stringgé formattáló
     * @return string reprezentáció
     */
    @Override
    public String toString(){
        return String.format(base.toString() + "^" + value.toString());
    }

    /**
     * Kiszámítja a double értékét
     * @return double érték
     */
    @Override
    public double toDouble() {
        return Math.pow(base.doubleValue(), value.toDouble());
    }
}
