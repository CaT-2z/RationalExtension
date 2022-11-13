package src;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

public class SimpleBasisPart implements IBasisPart {

    private Rational value;
    private BigInteger base;

    public Rational getValue(){
        return value;
    }

    public SimpleBasisPart(BigInteger b, Rational e){
        value = e;
        base = b;
    }

    @Override
    public void addSilently(Rational e) {
        value = value.add(e);
    }

    @Override
    public BasisPartKey getKey() {
        return new BasisPartKey(base);
    }

    //Adds e to the fraction, returns with the rational leftover value, fraction stays within [0,1[
    //DOESNT RETURN WITH THE SUM OF THE FRACTIONS
    //TODO: Maybe rename this to avoid confusion?
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
    public Object clone(){
        SimpleBasisPart p = new SimpleBasisPart(base, (Rational) value.clone());
        return p;
    }

    @Override
    public ExtendedRational toExtendedRational() {
        return ExtendedRational.fromSimple(base.intValue(), 1);
    }

    @Override
    public int compareTo(@NotNull IBasisPart o) {
        if(o instanceof ComplexBasisPart) return -1;
        if(o  instanceof RootOfUnityBasisPart) return 1;
        SimpleBasisPart other = (SimpleBasisPart) o;
        if(base != other.base) return base.compareTo(other.base);
        return value.compareTo(other.value);
    }

    @Override
    public String toString(){
        return String.format(base.toString() + "^" + value.toString());
    }
}
