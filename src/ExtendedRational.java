package src;

import org.jetbrains.annotations.NotNull;

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
        ///TODO: This may break shit I hope it wont
        //data.put(BasisSet.EMPTY, o);
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
        prune();
    }

    ///\brief Creates root from extended rational
    public static ExtendedRational root(ExtendedRational o, BigInteger b){
        if(o.isRationalCastable()){
            return new ExtendedRational((Rational) o, b);
        }

        ExtendedRational innerPart = (ExtendedRational) o.clone();
        Rational times = new Rational(Rational.ONE);

        if(!((Rational)o).equals(Rational.ZERO)) {
            Iterator<Map.Entry<BasisSet, Rational>> e = o.data.entrySet().iterator();
            ArrayList<BigInteger> num = factor(o.numerator);
            ArrayList<BigInteger> den = factor(o.denominator);

            BigInteger[] nums = multiplyAndRemainder(num, b);
            BigInteger[] dens = multiplyAndRemainder(den, b);

            times = new Rational(nums[0], dens[0]).simplify();

            for (Map.Entry<BasisSet, Rational> entry: innerPart.data.entrySet()) {
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
    public static BigInteger[] multiplyAndRemainder(ArrayList<BigInteger> num, BigInteger b) {
        BigInteger[] dens = num.stream().distinct().map((distinct) -> {

            BigInteger finalDistinct = distinct;

            long count = num.stream().filter((bigint) -> bigint.equals(finalDistinct)).count();
            BigInteger remaining = distinct.pow((int) count / b.intValue());
            BigInteger backfill = distinct.pow((int) count % b.intValue());

            return new BigInteger[]{remaining, backfill};

        }).reduce((b1, b2) -> new BigInteger[]{b1[0].multiply(b2[0]), b1[1].multiply(b2[1])}).orElse(null);
        return dens;
    }


    ///Check that its not called with shallow copies
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

        boolean first = true;

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (;;) {
            Map.Entry<BasisSet,Rational> e = i.next();
            BasisSet key = e.getKey();
            Rational value = e.getValue();
            if(!value.equals(Rational.ZERO)) {
                if(first){
                    first = false;
                }
                else{
                    sb.append(',').append(' ');
                }
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

    public ExtendedRational multiplyByBasis(BasisSet basis){
        ExtendedRational a = new ExtendedRational();
        a.put(basis, this);
        for (Map.Entry<BasisSet, Rational> entry: this.data.entrySet()) {
         BasisSet f = entry.getKey().multiply(basis);
         ExtendedRational r =  f.useRemainder();
         if(r.isRationalCastable()){
             if(a.data.containsKey(f)){
                 a.data.replace(f, entry.getValue().multiply((Rational)r).add(a.get(f)));
             }
             else{
                 a.put(f, entry.getValue().multiply(r));
             }
         }
         else{
            a = a.add(r.multiplyByBasis(f));
         }
        }
        return a;
    }


    ///\brief Adds two extended rationals together
    public ExtendedRational add(ExtendedRational src){
        ExtendedRational sum = (ExtendedRational) this.clone();
        src.data.forEach(new BiConsumer<BasisSet, Rational>() {
            @Override
            public void accept(BasisSet bases, Rational rational) {
                if(!sum.data.containsKey(bases)){
                    sum.data.put(bases, rational);
                }else {
                    sum.data.replace(bases, sum.data.get(bases).add(rational));
                }
            }
        });

        Rational s = ((Rational) this).add(src);
        sum.numerator = s.numerator;
        sum.denominator = s.denominator;

        sum.prune();

        return sum;
    }

    public ExtendedRational add(Rational src){
        ExtendedRational o = (ExtendedRational) this.clone();
        Rational r = super.add(src);
        o.numerator = r.numerator;
        o.denominator = r.denominator;

        return o;
    }

    private void prune(){
        Iterator<Map.Entry<BasisSet, Rational>> it = data.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<BasisSet, Rational> entry = it.next();
            if(entry.getValue().equals(Rational.ZERO)){
                it.remove();
            }
        }
    }

    //Creates new ExtendedRational
    //TODO: empty BasisSets don't work
    // Maybe redo the rational part?
    //TODO: test equality
    public ExtendedRational multiply(@NotNull ExtendedRational src){

        ///This should mitigate infinite loops
        if(src.isRationalCastable()) {
            return multiplyRational(src);
        }

        ExtendedRational product = new ExtendedRational();
        data.putIfAbsent(new BasisSet(BasisSet.EMPTY), new Rational(numerator, denominator));
        src.data.putIfAbsent(new BasisSet(BasisSet.EMPTY), new Rational(src.numerator, src.denominator));


        for (Map.Entry<BasisSet, Rational> outerEntry: data.entrySet()){
            BasisSet outerBasis = outerEntry.getKey();
            for (Map.Entry<BasisSet, Rational> innerEntry: src.data.entrySet()){
                BasisSet innerBasis = innerEntry.getKey();
                Rational rat = outerEntry.getValue().multiply(innerEntry.getValue());
                BasisSet nSet = outerBasis.multiply(innerBasis);
                Rational old = product.data.getOrDefault(nSet, new Rational(Rational.ONE));
                //.replace doesn't work TODO: fix this
                // RATIONALS POINT TO SAME OBJECT... AGAIN...
                ExtendedRational remainder = nSet.useRemainder();
                if(remainder.isRationalCastable()) {
                    if (product.data.containsKey(nSet)) {
                        //maybe add?
                        product.data.replace(nSet, rat.multiply(remainder.add(old)));
                    } else {
                        //Nset remainder broken, needs fixing
                        product.data.put(nSet, rat.multiply(old).multiply(remainder));
                    }
                }
                else{
                    product = product.add(remainder.multiplyByBasis(nSet));
                }
            }
        }

//        for (BasisSet outerBasis: data.keySet()) {
//            for (BasisSet innerBasis: src.data.keySet()){
//                Rational rat = data.get(outerBasis).multiply(src.get(innerBasis));
//                BasisSet nSet = outerBasis.multiply(innerBasis);
//                Rational old = product.data.getOrDefault(nSet, new Rational(Rational.ONE));
//                //.replace doesn't work TODO: fix this
//                // RATIONALS POINT TO SAME OBJECT... AGAIN...
//                ExtendedRational remainder = nSet.useRemainder();
//                if(remainder.isRationalCastable()) {
//                    if (product.data.containsKey(nSet)) {
//                        //maybe add?
//                        product.data.replace(nSet, rat.multiply(nSet.useRemainder()).add(old));
//                    } else {
//                        //Nset remainder broken, needs fixing
//                        product.data.put(nSet, rat.multiply(old).multiply(nSet.useRemainder()));
//                    }
//                }
//                else{
//                    product = product.add(remainder.multiplyByBasis(nSet));
//                }
//            }
//        }

        data.remove(new BasisSet());
        src.data.remove(new BasisSet());
        ///Potential Fucky Wucky Rational part added twice
        Rational newRational = Rational.ZERO.add(product.data.getOrDefault(new BasisSet(), Rational.ZERO));
        product.numerator = newRational.numerator;
        product.denominator = newRational.denominator;
        product.data.remove(new BasisSet());
        product.prune();
        return product;
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

    //divider
    public ExtendedRational divide(ExtendedRational src){
        ExtendedRational minv = (ExtendedRational) src.clone();
        for (Map.Entry<BasisSet, Rational> entry: minv.data.entrySet()) {
            //Todo: setvalue modifies the maps
            entry.setValue(entry.getValue().inverse());
            Iterator<IBasisPart> it = entry.getKey().iterator();
            while(it.hasNext()){
                IBasisPart b = it.next();
                b.addSilently(b.getValue().multiply(new Rational(BigInteger.valueOf(-2),BigInteger.ONE)));
            }
        }
        return multiply(minv);
    }

    ///brief: Megkönnyíti a teszelést, intekből csinál egyszerű er-t
    static public ExtendedRational fromSimple(int a, int b){
        return new ExtendedRational(new Rational(BigInteger.valueOf(a), BigInteger.valueOf(b)));
    }
}
