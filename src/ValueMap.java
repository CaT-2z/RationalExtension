package src;

import java.util.*;

public class ValueMap extends HashMap<IBasisPart, Rational> implements IBasisPart {
    ValueMap(){
        super();
    }
    ValueMap(ValueMap o){
        super(o);
    }
    ValueMap(HashMap<IBasisPart, Rational> o){
        super(o);
    }
}
