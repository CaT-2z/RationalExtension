package src.ui;

import src.ExtendedRational;

public class Circle implements IDrawnObj{
    ExtendedRational X;
    ExtendedRational Y;

    ExtendedRational r;

    public Circle(ExtendedRational x, ExtendedRational y, ExtendedRational r){
        this.X = x;
        this.Y = y;
        this.r = r;
    }

    @Override
    public ExtendedRational[] getIntersection(IDrawnObj obj) {
        return null;
    }
}
