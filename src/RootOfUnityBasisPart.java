package src;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

///Root of unity, value can be [0,1/2[
public class RootOfUnityBasisPart implements IBasisPart{

    private Rational value;
    public RootOfUnityBasisPart(Rational e){
        value = e;
    }

    public RootOfUnityBasisPart(){
        value = Rational.ZERO;
    }

    @Override
    public BasisPartKey getKey() {
        return new BasisPartKey(BigInteger.valueOf(-1));
    }

    @Override
    public Rational getValue() {
        return null;
    }

    //Adds e to the fraction, returns with the rational leftover value, fraction stays within [0,1/2[
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

    @Override
    public void addSilently(Rational e) {
        value = value.add(e);
    }


    @Override
    public Object clone() {
        RootOfUnityBasisPart ret = new RootOfUnityBasisPart();
        ret.value = value;
        return ret;
    }

    @Override
    public int compareTo(@NotNull IBasisPart o) {
        if(o instanceof RootOfUnityBasisPart){
            return value.compareTo(((RootOfUnityBasisPart) o).value);
        }
        return -1;
    }
}
