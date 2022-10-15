package src;

import java.math.BigInteger;
import java.util.*;
import java.util.function.BiConsumer;

// TODO Rational to BasisSet()
/// Rational with a map of basis-value pairs.
public class ExtendedRational extends Rational implements Cloneable{
    public HashMap<BasisSet, Rational> data;
    public ExtendedRational(){
        super(BigInteger.ZERO, BigInteger.ONE);
        data = new HashMap<BasisSet, Rational>();
    }
    public ExtendedRational(Rational o){
        super(o);
        data = new HashMap<BasisSet, Rational>();
        data.put(BasisSet.EMPTY, o);
    }

    // Pollard-Strassen algorithm for prime factorizationn
    private static BigInteger findFactor(BigInteger n){
        if(n.intValue() < 16 && n.intValue() > 2 && n.intValue()%2 == 0){ return BigInteger.TWO; };
        if(n.sqrt().multiply(n.sqrt()).compareTo(n) == 0){ return n.sqrt(); }
        int c = n.sqrt().add(BigInteger.ONE).sqrt().intValue();
        BigInteger[] f = new BigInteger[c];
        for (int i = 0; i < c; i++){
            f[i] = BigInteger.ONE;
            BigInteger jmin = BigInteger.valueOf(c).multiply(BigInteger.valueOf(i)).add(BigInteger.ONE);
            BigInteger jmax = jmin.add(BigInteger.valueOf(c)).subtract(BigInteger.ONE);
            for(BigInteger j = jmin; j.compareTo(jmax) != 1; j = j.add(BigInteger.ONE) ) {
                f[i] = f[i].multiply(j).mod(n);
            }
        }
        for (int i = 0; i < c; i++)
        {
            var factor = f[i].gcd(n);
            if (factor.compareTo(BigInteger.ONE) == 1)
            {
                return factor;
            }
        }
        return BigInteger.ONE;
    }

    public static ArrayList<BigInteger> factor(BigInteger n){
        ArrayList<BigInteger> factors = new ArrayList<BigInteger>();
        while(true){
            BigInteger i = findFactor(n);
            if(i.compareTo(BigInteger.ONE) == 0){
                factors.add(n);
                break;
            }
            factors.addAll(factor(i));
            n = n.divide(i);
        }
        return factors;
    }

    //This assumes rational already in GCD form,
    //Creates nth root of rational
    ExtendedRational(Rational o, BigInteger b){
        super(Rational.ZERO);
        ArrayList<BigInteger> numfactors = factor(o.getNumerator());
        ArrayList<BigInteger> denfactors = factor(o.getDenominator());
        Rational newnum = Rational.ONE;
        BasisSet fin = new BasisSet();

        for (BigInteger k: numfactors) {
            if(k.compareTo(BigInteger.ONE) == 0) continue;
            fin.addAdditive(k);
            newnum = newnum.multiply(fin.getSimpleBasis(k).addAndRemainder(new Rational(BigInteger.ONE, b)));
        }
        for (BigInteger k: denfactors) {
            if(k.compareTo(BigInteger.ONE) == 0) continue;
            fin.addAdditive(k);
            newnum = newnum.multiply(fin.getSimpleBasis(k).addAndRemainder(new Rational(BigInteger.ONE.negate(), b)));
        }
        data = new HashMap<BasisSet, Rational>();
        if(fin.isNone()){
            numerator = newnum.numerator;
            denominator = newnum.denominator;
        }
        else {
            data.put(fin, newnum);
        }
    }

//    public ExtendedRational root(ExtendedRational o, BigInteger b){
//        Iterator<Map.Entry<BasisSet, Rational>> e = o.data.entrySet().iterator();
//        while(e.hasNext()){
//
//        }
//    }

    public ExtendedRational(Rational o, HashMap<BasisSet, Rational> hash){
        super(o);
        data = new HashMap<BasisSet, Rational>(hash);
    }

    public void put(BasisSet s, Rational l){
        data.put(s, l);
    }

    public Rational get(BasisSet s){
        return data.get(s);
    }

    public String toString(){
        Iterator<Map.Entry<BasisSet,Rational>> i = data.entrySet().iterator();
        if (!i.hasNext())
            return super.toString();

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (;;) {
            Map.Entry<BasisSet,Rational> e = i.next();
            BasisSet key = e.getKey();
            Rational value = e.getValue();
            if(!value.equals(Rational.ZERO)) {
                sb.append(',').append(' ');
                sb.append(key);
                sb.append("=>");
                sb.append(value);
            }
            if (!i.hasNext())
                return String.format("%s, %s", super.toString(), sb.append('}').toString());
        }

    }



    // multiplies rational with extended rational
    public ExtendedRational multiplyRational(Rational src){
        ExtendedRational a = new ExtendedRational();
        Rational s = ((Rational) this).multiply(src);
        a.numerator = s.numerator;
        a.denominator = s.denominator;
        for (Map.Entry<BasisSet, Rational> entry : data.entrySet()){
            a.put((BasisSet) entry.getKey().clone(), entry.getValue().multiply(src));
        }

        return a;
    }

    public ExtendedRational add(ExtendedRational src){
        HashMap<BasisSet, Rational> hash = new HashMap<BasisSet, Rational>(data);
        src.data.forEach(new BiConsumer<BasisSet, Rational>() {
            @Override
            public void accept(BasisSet bases, Rational rational) {
                if(!hash.containsKey(bases)){
                    hash.put(bases, rational);
                }else{
                    hash.replace(bases, hash.get(bases).add(rational));
                }
            }
        });
        return new ExtendedRational(add((Rational)src), hash);
    }

//    private Rational prune(){
//        Rational r = Rational.ZERO;
//        for (BasisSet b: data.keySet()) {
//            if(b.isNone()){
//                r = r.add(data.get(b));
//            }
//        }
//    }

    //Creates new ExtendedRational
    //TODO: empty BasisSets don't work
    // Maybe redo the rational part?
    //TODO: test equality
    public ExtendedRational multiply(ExtendedRational src){

        ///This should mitigate infinite loops
        if(src.isRationalCastable()) {
            return multiplyRational(src);
        }

        HashMap<BasisSet, Rational> hash = new HashMap<BasisSet, Rational>();
        data.putIfAbsent(new BasisSet(BasisSet.EMPTY), new Rational(numerator, denominator));
        src.data.putIfAbsent(new BasisSet(BasisSet.EMPTY), new Rational(src.numerator, src.denominator));

        for (BasisSet outerBasis: data.keySet()) {
            for (BasisSet innerBasis: src.data.keySet()){
                Rational rat = data.get(outerBasis).multiply(src.get(innerBasis));
                BasisSet nSet = outerBasis.multiply(innerBasis);
                Rational old = hash.getOrDefault(nSet, new Rational(Rational.ONE));
                //.replace doesn't work TODO: fix this
                // RATIONALS POINT TO SAME OBJECT... AGAIN...
                if(hash.containsKey(nSet)){
                    //maybe add?
                    hash.replace(nSet, rat.multiply(nSet.useRemainder()).add(old));
                }
                else{
                    //Nset remainder broken, needs fixing
                    hash.put(nSet, rat.multiply(old).multiply(nSet.useRemainder()));
                }
            }
        }

        data.remove(new BasisSet());
        src.data.remove(new BasisSet());
        Rational newRational = multiplyRational((Rational) src);
        newRational = newRational.add(hash.getOrDefault(new BasisSet(), Rational.ZERO));
        hash.remove(new BasisSet());
        return new ExtendedRational(newRational, hash);
    }

    // returns true if there are no irrational values in it
    public boolean isRationalCastable(){
        if(data.isEmpty()) return true;
        Iterator<Map.Entry<BasisSet, Rational>> it = data.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<BasisSet, Rational> en = it.next();
            if(!en.getValue().equals(Rational.ZERO) && !en.getKey().equals(BasisSet.EMPTY)) return false;
        }
        return true;
    }

    @Override
    public Object clone(){
        ExtendedRational rat = new ExtendedRational();
        rat.numerator = numerator;
        rat.denominator = denominator;
        Iterator<Map.Entry<BasisSet, Rational>> it = data.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<BasisSet, Rational> ent = it.next();
            rat.data.put((BasisSet) ent.getKey().clone(),(Rational) ent.getValue().clone());
        }
        return rat;
    }
}
