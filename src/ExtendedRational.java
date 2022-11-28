package src;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.*;
import java.util.function.BiConsumer;

// TODO Rational to BasisSet()
/// Rational with a map of basis-value pairs.

/**
 * Egy algebrai szám ábrázolása.
 */
public class ExtendedRational extends Rational implements Cloneable{

    /**
     * A bázis-érték párok.
     */
    public HashMap<BasisSet, Rational> data;

    /**
     * Konstruktor
     */
    public ExtendedRational(){
        super(BigInteger.ZERO, BigInteger.ONE);
        data = new HashMap<BasisSet, Rational>();
    }

    /**
     * Konstruktor egy egyszerű racionális számból
     * @param o racionális szám
     */
    public ExtendedRational(Rational o){
        super(o);
        data = new HashMap<BasisSet, Rational>();
        ///TODO: This may break shit I hope it wont
        //data.put(BasisSet.EMPTY, o);
    }


    //This assumes rational already in GCD form,
    //Creates nth root of rational

    /**
     * Konstruktor egy raconális számból, és hogy hanyadik gyökét vesszük.
     * @param o racinális alap
     * @param b hanyadik gyök
     */
    public ExtendedRational(Rational o, BigInteger b){
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

    /**
     * A szám n-edik gyöke.
     * @param n hanyadik gyök
     * @return A gyök
     */
    public ExtendedRational root(int n){
        return Utils.root(this, BigInteger.valueOf(n));
    }


    ///Check that its not called with shallow copies

    /**
     * Konstruktor
     * @param o A racionális rész
     * @param hash A bázis, érték párok
     */
    public ExtendedRational(Rational o, HashMap<BasisSet, Rational> hash){
        super(o);
        data = new HashMap<BasisSet, Rational>(hash);
    }

    /**
     * Belerak egy új bázis érték párt
     * @param s bázis
     * @param l érték
     */
    public void put(BasisSet s, Rational l){
        data.put(s, l);
    }

    /**
     * Megtalálja a bázis értékét
     * @param s bázis
     * @return érték
     */
    public Rational get(BasisSet s){
        return data.get(s);
    }

    /**
     * ToString
     * @return String reprezentáció
     */
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

    /**
     * Összeszorozza magát egy racionális számmal
     * @param src racionális szám
     * @return szorzat
     */
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

    /**
     * Megszorozza magát egy bázissal
     * @param basis bázis
     * @return szorzat
     */
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

    /**
     * Összead két számot
     * @param src összeadandó
     * @return összeg
     */
    public ExtendedRational add(ExtendedRational src){
        ExtendedRational sum = (ExtendedRational) this.clone();
        src.data.forEach((bases, rational) -> {
            if (!sum.data.containsKey(bases)) {
                sum.data.put((BasisSet) bases.clone(), rational);
            } else {
                sum.data.replace(bases, sum.data.get(bases).add(rational));
            }
        });

        Rational s = ((Rational) this).add(src);
        sum.numerator = s.numerator;
        sum.denominator = s.denominator;

        sum.prune();

        return sum;
    }

    /**
     * Összeadja magát egy racionális számmal
     * @param src racionális szám
     * @return összeg
     */
    public ExtendedRational add(Rational src){
        ExtendedRational o = (ExtendedRational) this.clone();
        Rational r = super.add(src);
        o.numerator = r.numerator;
        o.denominator = r.denominator;

        return o;
    }

    /**
     * Egyszerüsíti az értékét: Kidobja a 0 értékű bázisokat.
     */
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

    /**
     * Összeszoroz két számot, visszaadja a szorzatukat.
     * @param src szorzandó
     * @return szorzat
     */
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

    /**
     * Visszaadja, hogy egyenlő e a szám a racionális részével
     * @return egyenlő e
     */
    public boolean isRationalCastable(){
        if(data.isEmpty()) return true;
        Iterator<Map.Entry<BasisSet, Rational>> it = data.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<BasisSet, Rational> en = it.next();
            if(!en.getValue().equals(Rational.ZERO) && !en.getKey().equals(BasisSet.EMPTY)) return false;
        }
        return true;
    }

    /// Gets negate

    /**
     * Visszaadja az additív inverzét
     * @return inverz.
     */
    public ExtendedRational negate(){
        Rational ratPart = super.negate();
        ExtendedRational a = new ExtendedRational(ratPart);
        for (Map.Entry<BasisSet, Rational> entry: data.entrySet()) {
            a.data.put(entry.getKey(), entry.getValue().negate());
        }
        return a;
    }

    /**
     * Klónozza a számot
     * @return klón
     */
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

    /**
     * Leosztja a számot a paraméterben kapott számmal.
     * @param src osztó
     * @return eredmény
     */
    public ExtendedRational divide(ExtendedRational src){
        if(src.isRationalCastable()) {
            return multiplyRational(((Rational) src).inverse());
        }
        if(src.data.size() == 1 && ((Rational)src).equals(Rational.ZERO) ){
            ExtendedRational minv = (ExtendedRational) src.clone();
            for (Map.Entry<BasisSet, Rational> entry: minv.data.entrySet()) {
                entry.setValue(entry.getValue().inverse());
                for (IBasisPart iBasisPart : entry.getKey()) {
                    Iterator<IBasisPart> it = entry.getKey().iterator();
                    while(it.hasNext()){
                        IBasisPart b = it.next();
                        b.addSilently(b.getValue().multiply(new Rational(BigInteger.valueOf(-2),BigInteger.ONE)));
                    }
                }
                return multiply(minv);
            }
        }

        ComplexBasisPart comp = new ComplexBasisPart(src, Rational.ONE);
        ExtendedRational inv = comp.getInverse().multiplyRational(comp.getinvScalar().inverse());
        return multiply(inv);
    }

    ///brief: Megkönnyíti a teszelést, intekből csinál egyszerű er-t

    /**
     * Létrehoz egy algebrai számot a paraméterek alapján
     * @param a A racionális rész numerátora
     * @param b A racionális rész denominátora
     * @return A létrejött szám
     */
    static public ExtendedRational fromSimple(int a, int b){
        return new ExtendedRational(new Rational(a, b));
    }

    /**
     * A monitor pixelkoordinátáiból számít algebrai számot.
     * @param start a rajztábla min X-je
     * @param end a rajztábla max X-je
     * @param size a tábla mérete pixelben
     * @param offs a választott pixel
     * @return A létrejött szám
     */
    static public ExtendedRational fromScreenSpace(Rational start, Rational end, int size, int offs){
        Rational point = end.add(start.negate()).multiply(new Rational(offs, size)).add(start);
        return new ExtendedRational(point);
    }

    ///Gets double representation of rational

    /**
     * Kiszámolja a double értékét
     * @return A double érték
     */
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

    /**
     * Számból pixelt számol
     * @param startRat a tábla kezdőértéke
     * @param endRat a tábla végső értéke
     * @param size a tábla mérete pixelben
     * @return a hozzá tartozó pixel
     */
    public int toScreenSpace(Rational startRat, Rational endRat, int size){
        double val = this.toDouble();
        val -= startRat.toDouble();
        val /= endRat.toDouble() - startRat.toDouble();
        return (int) (val * size);
    }
}
