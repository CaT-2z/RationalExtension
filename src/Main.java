package src;

import java.math.BigInteger;

public class Main {
    public static void main(String[] args){
        ExtendedRational n = new ExtendedRational(new Rational(new BigInteger("91"), new BigInteger("25")).simplify(), new BigInteger("2"));
        ExtendedRational b = new ExtendedRational(new Rational(new BigInteger("91"), new BigInteger("25")).simplify(), new BigInteger("3"));
        System.out.println(n.toString());
        System.out.println(b.toString());
        ExtendedRational r = n.multiply(b);
        System.out.println(r.toString());

    }
}
