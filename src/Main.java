package src;

import java.math.BigInteger;

public class Main {

    public static void main(String[] args){
        ExtendedRational n = new ExtendedRational(new Rational(new BigInteger("16"), new BigInteger("25")).simplify(), new BigInteger("2"));
        ExtendedRational b = new ExtendedRational(new Rational(new BigInteger("16"), new BigInteger("25")).simplify(), new BigInteger("3"));
        ExtendedRational a = b.add(n);
        System.out.println(a);
        ExtendedRational root = ExtendedRational.root(a,BigInteger.TWO);
        System.out.println(root);
    }
}
