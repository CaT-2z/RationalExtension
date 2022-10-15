package src;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class BasisPartKey implements Comparable<BasisPartKey>, Cloneable {
        private BigInteger n;

        public BigInteger getInt() {
                return isInt ? n : null;
        }
        private ArrayList<BasisPartKey> data;

        public ArrayList<BasisPartKey> getData() {
                return isInt ? null : data;
        }

        private boolean isInt;
        BasisPartKey(BigInteger b){
                n = b;
                isInt = true;
        }

        private BasisPartKey(){

        }

        BasisPartKey(ArrayList<BasisPartKey> d){
                data = d;
                isInt = false;
        }


        //Recursive compareto, don't make a part with an array with itself in it;
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
                        BasisPartKey[] a = (BasisPartKey[]) data.toArray();
                        BasisPartKey[] b = (BasisPartKey[]) o.data.toArray();
                        for (int i = 0; i < a.length; i++) {
                                if (a[i].compareTo(b[i]) != 0) return a[i].compareTo(b[i]);
                        }
                        return 0;

                }
        }

        /// guess who forgot what the function should look like?
        @Override
        public boolean equals(Object src){
                if(src instanceof BasisPartKey){
                        return compareTo((BasisPartKey) src) == 0;
                }
                return false;
        }

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
        @Override
        public int hashCode(){
                return Objects.hash(n,data);
        }
}
