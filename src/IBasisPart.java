package src;

public interface IBasisPart extends Cloneable {
    public Rational addAndRemainder(Rational e);

    public BasisPartKey getKey();

    public Rational getValue();

    public Object clone();
}

