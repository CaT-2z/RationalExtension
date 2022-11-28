package src.ui;

import src.ExtendedRational;

import java.io.Serializable;

/**
 * Egy pont a rajztáblán
 */
public class MyDot implements Serializable {

    /**
     * X koordináta
     */
    ExtendedRational x;

    /**
     * Y koordináta
     */
    ExtendedRational y;

    /**
     * Konstruktor
     * @param x X
     * @param y Y
     */
    public MyDot(ExtendedRational x, ExtendedRational y){
        this.x = x;
        this.y = y;
    }
}
