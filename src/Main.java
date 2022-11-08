package src;

import java.math.BigInteger;

public class Main {

    public static void main(String[] args){
        ExtendedRational a = ExtendedRational.fromSimple(4, 5);
        a = ExtendedRational.root(a, BigInteger.TWO);
        a = a.add(Rational.ONE);
        System.out.println(a);
        ComplexBasisPart b = new ComplexBasisPart(a, Rational.HALF);
        ExtendedRational c = b.getInverse();
        System.out.println(c);
    }
}
