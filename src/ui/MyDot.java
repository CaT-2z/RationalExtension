package src.ui;

import src.ExtendedRational;

import java.io.Serializable;

public class MyDot implements Serializable {

    ExtendedRational x;

    ExtendedRational y;

    public MyDot(ExtendedRational x, ExtendedRational y){
        this.x = x;
        this.y = y;
    }
}
