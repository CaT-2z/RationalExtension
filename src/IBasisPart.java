package src;

public interface IBasisPart extends Cloneable, Comparable<IBasisPart> {
    public ExtendedRational addAndRemainder(Rational e);

    public void addSilently(Rational e);

    public BasisPartKey getKey();

    public Rational getValue();

    public Object clone();
}

