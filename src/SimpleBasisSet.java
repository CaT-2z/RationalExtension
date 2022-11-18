//package src;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.math.BigInteger;
//import java.util.*;
//
/////This will hurt
//public class SimpleBasisSet implements Cloneable{
//
//    final static SimpleBasisSet EMPTY = new SimpleBasisSet();
//    private HashMap<Integer, SimpleBasisPart> map;
//    private RootOfUnityBasisPart unity;
//
//    private ExtendedRational remainder;
//
//    public SimpleBasisSet(){
//        map = new HashMap<>();
//        unity = new RootOfUnityBasisPart();
//    }
//
//    ///\brief Shallow copy
//    public SimpleBasisSet(SimpleBasisSet p){
//        map = new HashMap<>(p.map);
//        unity = new RootOfUnityBasisPart(p.unity.getValue());
//    }
//
//
//    ///Equals function
//    @Override
//    public boolean equals(Object src){
//        if(src == null) return false;
//        if(src instanceof SimpleBasisSet){
//            ///Hope it is a shallow copy
//            SimpleBasisSet set = new SimpleBasisSet((SimpleBasisSet) src);
//            for (Integer i: map.keySet()) {
//
//                SimpleBasisPart part = set.map.get(i);
//                Rational value = part == null ? Rational.ZERO : part.getValue();
//
//                if(!value.equals( map.get(i).getValue() )) return false;
//                set.map.remove(i);
//            }
//            for (SimpleBasisPart i: set.map.values()){
//                if(!i.getValue().equals(Rational.ZERO)) return false;
//            }
//            return true;
//
//        }
//        else{return false;}
//    }
//
//
//    ///multiply function: returns new BasisSet
//    public IBasisSet multiply(IBasisSet o){
//        if(o instanceof ComplexBasisSet){
//            return o.multiply(this);
//        }
//
//        return null;
//    }
//
//    public ExtendedRational useRemainder(){
//        return null;
//    }
//
//
//
//    ///hashCode function
//
//    @Override
//    public int hashCode(){
//        int result = 17;
//        int a = 0;
//        for (IBasisPart b: map.values()){
//            if(!b.getValue().equals(Rational.ZERO)) a++;
//        }
//        result = 31*result + a;
//        return result;
//    }
//
//    @Override
//    public Object clone(){
//        SimpleBasisSet ret = new SimpleBasisSet();
//        Iterator<Map.Entry<Integer, SimpleBasisPart>> it = map.entrySet().iterator();
//        while(it.hasNext()){
//            Map.Entry<Integer,SimpleBasisPart> ent = it.next();
//            ret.map.put((Integer) ent.getKey(),(SimpleBasisPart) ent.getValue().clone());
//        }
//        ret.unity = (RootOfUnityBasisPart) unity.clone();
//        return ret;
//    }
//
//}
