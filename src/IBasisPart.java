package src;

/**
 * Egy bázis rész interfésze
 */
public interface IBasisPart extends Cloneable, Comparable<IBasisPart> {
    /**
     * Hozzáad e-t az értékhez és visszaadja a maradékot.
     * @param e hozzáadandó
     * @return maradék
     */
    public ExtendedRational addAndRemainder(Rational e);

    /**
     * Hozzáad, de nem vesz maradékot
     * @param e hozzáadandó
     */
    public void addSilently(Rational e);

    /**
     * Visszaadja a hozzátartozó kulcsot
     * @return kulcs
     */
    public BasisPartKey getKey();

    /**
     * Visszaadja a bázis jelenlegi értékét
     * @return érték
     */
    public Rational getValue();

    /**
     * Klónozó
     * @return klón
     */
    public Object clone();

    /**
     * Kiszámolja a double értékét
     * @return érték
     */
    public double toDouble();

    /**
     * a bázisát algebrai számmá alakítja
     * @return szám
     */
    public ExtendedRational toExtendedRational();
}

