package src.ui;

import src.ExtendedRational;

import java.io.Serializable;

/**
 * Rajzolt objektumok interfésze a rajztáblához.
 */
public interface IDrawnObj extends Serializable {

    /**
     * Megtalálja a mettszéspontukat.
     * @param obj Az objektum, amivel mettszéspontokat keres.
     * @return A mettszéspontok.
     */
    public ExtendedRational[] getIntersection(IDrawnObj obj);
}
