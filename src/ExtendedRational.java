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


    //This assumes rational already in GCD form,
    //Creates nth root of rational
    ExtendedRational(Rational o, BigInteger b){
        super(Rational.ZERO);
        ArrayList<BigInteger> numfactors = Utils.factor(o.getNumerator());
        ArrayList<BigInteger> denfactors = Utils.factor(o.getDenominator());
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

    public ExtendedRational root(int n){
        return Utils.root(this, BigInteger.valueOf(n));
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


    ///Multiplies Extendedrational by a BasisSet
    public ExtendedRational multiplyByBasis(BasisSet basis){
        ExtendedRational a = new ExtendedRational();
        a.put(basis, new Rational(this));
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
                    sum.data.put((BasisSet) bases.clone(), rational);
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
                ///ITT
                BasisSet nSet = outerBasis.multiply(innerBasis);
                Rational old = product.data.getOrDefault(nSet, new Rational(Rational.ONE));
                //.replace doesn't work TODO: fix this
                // RATIONALS POINT TO SAME OBJECT... AGAIN...
                ExtendedRational remainder = nSet.useRemainder();
                if(remainder.isRationalCastable()) {
                    if (product.data.containsKey(nSet)) {
                        //maybe add?
                        product.data.replace(nSet, old.add(rat.multiply(remainder)));
                    } else {
                        //Nset remainder broken, needs fixing
                        product.data.put(nSet, rat.multiply(remainder));
                    }
                }
                else{
                    product = product.add(remainder.multiplyByBasis(nSet));
                }
            }
        }

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
        return new ExtendedRational(new Rational(a, b));
    }

    static public ExtendedRational fromScreenSpace(Rational start, Rational end, int size, int offs){
        Rational point = end.add(start.negate()).multiply(new Rational(offs, size)).add(start);
        return new ExtendedRational(point);
    }

    ///Gets double representation of rational
    public double toDouble(){
        double ret = 0;
        for (Map.Entry<BasisSet, Rational> entry: data.entrySet()){
            ret += entry.getValue().toDouble()*entry.getKey().toDouble();
        }
        ret += super.toDouble();
        return ret;
    }
    ///Returns relative screen coordinates
    ///Maybe this needs to be in a different place
    public int toScreenSpace(Rational startRat, Rational endRat, int size){
        double val = this.toDouble();
        val -= startRat.toDouble();
        val /= endRat.toDouble() - startRat.toDouble();
        return (int) (val * size);
    }
}
