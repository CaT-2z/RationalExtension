package src;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * Egy BasisPartot multiplicitás szerint reprezentáló kulcs
 */
public class BasisPartKey implements Comparable<BasisPartKey>, Cloneable {

        /**
         * A Basis Part bázisa
         */
        private BigInteger n;

        /**
         * Visszaadja a bázist, ha egyszerű bázis
         * @return basis
         */
        public BigInteger getInt() {
                return isInt ? n : null;
        }

        /**
         * A bázis komplex kulcsa, ha nem egyszerű.
         */
        private ArrayList<BasisPartKey> data;

        /**
         * A bázis komplex kulcsa, ha komplex.
         * @return A komplex bázis kulcsai
         */
        public ArrayList<BasisPartKey> getData() {
                return isInt ? null : data;
        }

        /**
         * Egyszerű e a kulcs.
         */
        private boolean isInt;

        /**
         * Konstruktor egyszerű bázishoz
         * @param b A bázis
         */
        BasisPartKey(BigInteger b){
                n = b;
                isInt = true;
        }


        /**
         * Üres konstruktor
         */
        private BasisPartKey(){

        }

        /**
         * Konstruktor komplex bázishoz
         * @param d a komplex bázis
         */
        BasisPartKey(ArrayList<BasisPartKey> d){
                data = d;
                isInt = false;
        }


        //Recursive compareto, don't make a part with an array with itself in it;

        /**
         * Összehasonlító függvény
         * @param o the object to be compared.
         * @return a > b ?
         */
        @Override
        public int compareTo(@NotNull BasisPartKey o) {
                if (o == null) {
                        return 1;
                }
                if (isInt != o.isInt) {
                        return isInt ? -1 : 1;
                }
                if (isInt) {
                        return n.compareTo(o.n);
                } else {
                        if (data.size() != o.data.size()) {
                                return data.size() > o.data.size() ? 1 : -1;
                        }
                        ArrayList<BasisPartKey> a = data;
                        ArrayList<BasisPartKey> b = o.data;
                        for (int i = 0; i < a.size(); i++) {
                                if (a.get(i).compareTo(b.get(i)) != 0) return a.get(i).compareTo(b.get(i));
                        }
                        return 0;

                }
        }

        /// guess who forgot what the function should look like?

        /**
         * Egyenlőség függvény
         * @param src comparee
         * @return Ezeknél miért nincs implicit érték? (a = b?)
         */
        @Override
        public boolean equals(Object src){
                if(src instanceof BasisPartKey){
                        return compareTo((BasisPartKey) src) == 0;
                }
                return false;
        }

        /**
         * Klónozó fgv
         * @return Mély klón (nagyon mély)
         */
        public Object clone(){
                BasisPartKey key = null;
                if(isInt){
                        key = new BasisPartKey(n);
                }else{
                        key = new BasisPartKey();
                        key.isInt = false;
                        key.n = null;
                        key.data = new ArrayList<BasisPartKey>();
                        Iterator<BasisPartKey> it = data.iterator();
                        while(it.hasNext()){
                                key.data.add((BasisPartKey) it.next().clone());
                        }
                }
                return key;
        }

        ///Into the darkness we go

        /**
         * Hash függvény, mert kell a HashMaphez
         * @return hash
         */
        @Override
        public int hashCode(){
                return Objects.hash(n,data);
        }
}
