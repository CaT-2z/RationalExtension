package src;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Algoritmusok, amik nem tartoznak specifikusan semelyik osztályhoz
 */
public class Utils {
    // Pollard-Strassen algorithm for prime factorizationn

    /**
     * Kiszámítja egy szám egy prímtényezőjét Pollard-Strassen algoritmus segítségével
     * @param n A szám
     * @return prímtényező
     */
    static BigInteger findFactor(BigInteger n) {
        if (n.intValue() < 16 && n.intValue() > 2 && n.intValue() % 2 == 0) {
            return BigInteger.TWO;
        }
        ;
        if (n.sqrt().multiply(n.sqrt()).compareTo(n) == 0) {
            return n.sqrt();
        }
        int c = n.sqrt().add(BigInteger.ONE).sqrt().intValue();
        BigInteger[] f = new BigInteger[c];
        for (int i = 0; i < c; i++) {
            f[i] = BigInteger.ONE;
            BigInteger jmin = BigInteger.valueOf(c).multiply(BigInteger.valueOf(i)).add(BigInteger.ONE);
            BigInteger jmax = jmin.add(BigInteger.valueOf(c)).subtract(BigInteger.ONE);
            for (BigInteger j = jmin; j.compareTo(jmax) != 1; j = j.add(BigInteger.ONE)) {
                f[i] = f[i].multiply(j).mod(n);
            }
        }
        for (int i = 0; i < c; i++) {
            var factor = f[i].gcd(n);
            if (factor.compareTo(BigInteger.ONE) == 1) {
                return factor;
            }
        }
        return BigInteger.ONE;
    }

    /**
     * Kiszámítja egy szám összes prímtényezőjét
     * @param n A szám
     * @return A prímtényezői
     */
    static ArrayList<BigInteger> factor(BigInteger n) {
        ArrayList<BigInteger> factors = new ArrayList<BigInteger>();
        if(n.bitLength() > 32){
            n = n.shiftRight(n.bitLength() - 32);
        }
        while (true) {
            BigInteger i = findFactor(n);
            if (i.compareTo(BigInteger.ONE) == 0) {
                factors.add(n);
                break;
            }
            factors.addAll(factor(i));
            n = n.divide(i);
        }
        return factors;
    }

    /**
     * Kiszámítja egy algebrai szám n-edik gyökét
     * @param o a szám
     * @param b hanyadik gyöke
     * @return a gyök
     */
    ///\brief Creates root from extended rational
    public static ExtendedRational root(ExtendedRational o, BigInteger b) {
        if (o.isRationalCastable()) {
            return new ExtendedRational((Rational) o, b);
        }

        ExtendedRational innerPart = (ExtendedRational) o.clone();
        Rational times = new Rational(Rational.ONE);

        if (!((Rational) o).equals(Rational.ZERO)) {
            Iterator<Map.Entry<BasisSet, Rational>> e = o.data.entrySet().iterator();
            ArrayList<BigInteger> num = factor(o.numerator);
            ArrayList<BigInteger> den = factor(o.denominator);

            BigInteger[] nums = multiplyAndRemainder(num, b);
            BigInteger[] dens = multiplyAndRemainder(den, b);

            times = new Rational(nums[0], dens[0]).simplify();

            for (Map.Entry<BasisSet, Rational> entry : innerPart.data.entrySet()) {
                entry.setValue(entry.getValue().divide(times.pow(b.intValue())));
            }

            innerPart.numerator = nums[1];
            innerPart.denominator = dens[1];
        }

        ComplexBasisPart part = new ComplexBasisPart(innerPart, new Rational(BigInteger.ONE, b));

        ExtendedRational returner = new ExtendedRational();

        ///TODO: Will you merge ComplexBasis and Basis or Do smg else?

        BasisSet outer = new BasisSet();
        outer.addAdditive(part);

        returner.data.put(outer, times);

        return returner;

    }

    ///\brief Helper function for root, Multiplies through list and takes the products root.
    ///\returns Biginteger array, first value: The rational part of the root, second value: the remainder.

    /**
     * Segítő függvény a gyökszámítóhoz, prímtényezők listájának számítja a gyökét
     * @param num prímlista
     * @param b hanyadik gyök
     * @return [racionális rész, maradék]
     */
    static BigInteger[] multiplyAndRemainder(ArrayList<BigInteger> num, BigInteger b) {
        BigInteger[] dens = num.stream().distinct().map((distinct) -> {

            BigInteger finalDistinct = distinct;

            long count = num.stream().filter((bigint) -> bigint.equals(finalDistinct)).count();
            BigInteger remaining = distinct.pow((int) count / b.intValue());
            BigInteger backfill = distinct.pow((int) count % b.intValue());

            return new BigInteger[]{remaining, backfill};

        }).reduce((b1, b2) -> new BigInteger[]{b1[0].multiply(b2[0]), b1[1].multiply(b2[1])}).orElse(null);
        return dens;
    }
}
