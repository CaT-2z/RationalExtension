package src;

import java.math.BigInteger;

public class Main {

    public static void main(String[] args){
        ExtendedRational a = ExtendedRational.fromSimple(5, 1);
        a = ExtendedRational.root(a, BigInteger.TWO);
        a = a.add(Rational.ONE).add(ExtendedRational.root(ExtendedRational.fromSimple(3,1),BigInteger.TWO));
        ComplexBasisPart b = new ComplexBasisPart(a, Rational.HALF);
        ExtendedRational c = b.getInverse();
        System.out.println(c);
        System.out.println(a);
        System.out.println(c.multiply(a));
    }
}
