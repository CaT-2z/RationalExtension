package src;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

//TODO: 2x5x3 is not the same as 2x5x3 with other fractions
public class BasisSet extends AbstractSet<Basis>
    implements Set<Basis>, Cloneable, java.io.Serializable, Comparable<BasisSet>, ICalculable{

    @java.io.Serial
    static final long serialVersionUID = -4924742326767321676L;

    private transient HashMap<BigInteger,Basis> map;

    private Rational remainder;

    private static final Object PRESENT = new Object();

    public BasisSet(){
        map = new HashMap<BigInteger,Basis>();
        remainder = Rational.ONE;
    }

    public BasisSet(BasisSet o) {
        map = new HashMap<BigInteger, Basis>(o.map);
        remainder = o.remainder;
    }


    //Creates new BasisSet, the Rational part is in the remainder, uses both parents remainder, don't forget to purge remainder
    //CREATES NEW!!!! BASIS SET!!!
    public BasisSet multiply(BasisSet o) {
        if (!remainder.equals(Rational.ONE) || !o.remainder.equals(Rational.ONE) ) {
            System.out.println("WARNING: multiplication with dirty BasisSets");
        }
        BasisSet a = new BasisSet(this);
        a.remainder = remainder.multiply(o.remainder);
        for (Basis b: o.map.values()) {
            a.remainder = a.remainder.multiply(a.addTo(b));
        }
        return a;
    }

    public Rational useRemainder(){
        Rational ret = new Rational(remainder);
        remainder = Rational.ONE;
        return ret;
    }


    @Override
    public Iterator<Basis> iterator() {
        return map.values().iterator();
    }

    @Override
    public void forEach(Consumer<? super Basis> action) {
        map.forEach(new BiConsumer<BigInteger, Basis>() {
            @Override
            public void accept(BigInteger o, Basis basis) {
                action.accept(basis);
            }
        });
    }

    //TODO: Serialization

    @Override
    public <Basis> Basis[] toArray(IntFunction<Basis[]> generator) {
        return (Basis[])map.keySet().toArray();
    }

    @Override
    public Stream<Basis> stream() {
        return super.stream();
    }

    @Override
    public int size()  {
        return map.size();
    }

    //Adds exactly once, overrides parent, only use for creation
    public boolean add(Basis src){
        if(map.containsKey(src.getBase())){
            return false;
        }
        else map.put(src.getBase(), src);
        return true;
    }

    //Adds exactly once, overrides parent
    public boolean add(BigInteger k){
        if(map.containsKey(k)){
            return false;
        }
        else map.put(k, new Basis(k, Rational.ZERO));
        return true;
    }


    //Adds the basis' value to the basis in the map, returns remainder. If the key doesn't exist, it assumes (Rational) src in [0, 1[
    public Rational addTo(@NotNull Basis src){
        if(map.containsKey(src.getBase())){
            return map.get(src.getBase()).add((Rational) src);
        }
        else map.put(src.getBase(), src);
        return Rational.ONE;
    }

    public Basis get(Basis src){
        return map.get(src.getBase());
    }

    public Basis get(BigInteger b){
        return map.get(b);
    }

    //The set doesn't contain itself (obviously)
     public String toString(){
            Iterator<Map.Entry<BigInteger,Basis>> i = map.entrySet().iterator();
            if (! i.hasNext())
                return "{}";

            StringBuilder sb = new StringBuilder();
            sb.append("[");
            Map.Entry<BigInteger,Basis> e = i.next();
            BigInteger key = e.getKey();
            Basis value = e.getValue();
            if(!value.equals(Rational.ZERO)) {
                sb.append(key);
                sb.append("^(");
                sb.append(value);
                sb.append(')');
            }
            for (;;) {
                e = i.next();
                key = e.getKey();
                value = e.getValue();
                if(!value.equals(Rational.ZERO)) {
                    sb.append(',').append(' ');
                    sb.append(key);
                    sb.append("^(");
                    sb.append(value);
                    sb.append(')');
                }
                if (! i.hasNext())
                    return sb.append(']').toString();
            }
        }

    public boolean isNone(){
        for (BigInteger i: map.keySet()){
            if(map.get(i).numerator.compareTo(BigInteger.ZERO) == 0) continue;
            return false;
        }
        return true;
    }


    //Overrides equals function, only use on BasisSet
    @Override
    public boolean equals(Object src){
        if(src instanceof BasisSet){
            BasisSet set = new BasisSet((BasisSet)src);
            for (BigInteger i: map.keySet()) {
                Rational value = (Rational)set.map.getOrDefault(i, new Basis(i, Rational.ZERO));
                if(!value.equals( (Rational)map.get(i) )) return false;
                set.map.remove(i);
            }
            for (BigInteger i: set.map.keySet()){
                if(!((Rational)set.map.get(i)).equals(Rational.ZERO)) return false;
            }
            return true;
        }
        return false;
    }

    //TODO: Fix toString first
    @Override
    public int compareTo(@NotNull BasisSet o) {
        return equals(o) ? 0 : 1;
    }
}
