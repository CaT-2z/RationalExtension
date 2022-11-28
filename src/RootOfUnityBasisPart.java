package src;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

///Root of unity, value can be [0,1/2[

/**
 * Komplex térben való elforgatást reprezentál
 */
public class RootOfUnityBasisPart implements IBasisPart{

    /**
     * A hatvány értéke
     */
    private Rational value;

    /**
     * Konstruktor
     * @param e érték
     */
    public RootOfUnityBasisPart(Rational e){
        value = e;
    }

    /**
     * Üres konstruktor
     */
    public RootOfUnityBasisPart(){
        value = Rational.ZERO;
    }

    /**
     * Visszaadja a bázis kulcsát (-1)
     * @return -1
     */
    @Override
    public BasisPartKey getKey() {
        return new BasisPartKey(BigInteger.valueOf(-1));
    }

    /**
     * Visszaadja a hatványértéket
     * @return érték
     */
    @Override
    public Rational getValue() {
        return value;
    }

    //Adds e to the fraction, returns with the rational leftover value, fraction stays within [0,1/2[

    /**
     * Hozzáad az értékhez e-t, normalizál, visszaadja a maradékot
     * @param e hozzáadandó
     * @return maradék
     */
    public ExtendedRational addAndRemainder(Rational e){
        Rational sum = value.add(e);
        BigInteger[] div = sum.getNumerator().multiply(BigInteger.TWO).divideAndRemainder(sum.getDenominator());
        value.denominator = sum.denominator;
        value.numerator = div[1];
        value = value.multiply(Rational.HALF);
        if(div[0].compareTo(BigInteger.ZERO) != -1 && div[1].compareTo(BigInteger.ZERO) != -1){
            return new ExtendedRational(new Rational( BigInteger.valueOf(-1).pow(div[0].intValue()), BigInteger.ONE));
        }else{
            value.numerator = value.numerator.add(value.denominator);
            value.multiply(Rational.HALF);
            return new ExtendedRational(new Rational(BigInteger.ONE, BigInteger.valueOf(-1).pow(div[0].abs().add(BigInteger.ONE).intValue())) );
        }
    }

    /**
     * Hozzáad, de nem normalizál, nincs maradék
     * @param e hozzáadandó
     */
    @Override
    public void addSilently(Rational e) {
        value = value.add(e);
    }


    /**
     * Klónozó
     * @return klón
     */
    @Override
    public Object clone() {
        RootOfUnityBasisPart ret = new RootOfUnityBasisPart();
        ret.value = (Rational) value.clone();
        return ret;
    }

    /**
     * Errort dob, ezzel a fajta bázissal ez nem történhet meg
     * @return null
     */
    @Override
    public ExtendedRational toExtendedRational() {
        throw new RuntimeException("This shouldn't happen");
    }

    /**
     * toString
     * @return Stringé reprezentáció
     */
    public String toString(){
        return String.format("-1^" + value.divide(Rational.HALF));
    }

    /**
     * Összehasonlító
     * @param o the object to be compared.
     * @return összehasonlítás
     */
    @Override
    public int compareTo(@NotNull IBasisPart o) {
        if(o instanceof RootOfUnityBasisPart){
            return value.compareTo(((RootOfUnityBasisPart) o).value);
        }
        return -1;
    }

    /**
     * Double reprezentáció
     * @return double érték
     */
    public double toDouble(){
        return value.compareTo(Rational.HALF)>0 ? -1 : 1;
    }
}
