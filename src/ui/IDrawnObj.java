package src.ui;

import src.ExtendedRational;

import java.io.Serializable;

public interface IDrawnObj extends Serializable {
    public ExtendedRational[] getIntersection(IDrawnObj obj);
}
