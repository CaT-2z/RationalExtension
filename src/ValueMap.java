package src;

import java.util.*;

public class ValueMap extends HashMap<ICalculable, Rational> implements ICalculable{
    ValueMap(){
        super();
    }
    ValueMap(ValueMap o){
        super(o);
    }
    ValueMap(HashMap<ICalculable, Rational> o){
        super(o);
    }
}
