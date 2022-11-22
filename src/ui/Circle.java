package src.ui;

import src.ExtendedRational;
import src.Rational;

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

    /// y = mx + b
    /// (x-x1)^2 + (y-y1)^2 = d^2
    /// (x-x1)^2 + (mx + b - y1)^2 = d^2
    /// x^2 - 2xx1 + x1^2 + mx^2 + 2mx(b-y1) + (b-y1)^2 = d^2
    /// x^2 - 2xx_1 + mx^2 + 2mx(b-y1) = d^2 - (b-y1)^2 - x1^2
    /// (m^2 + 1)x^2 + (2m(b-y1) - 2x1)x + ( - d^2 + (b-y1)^2 + x1^2) = 0
    /// b +- sqrt(b^2 - 4ac) ...
    public ExtendedRational[] getLineIntersection(Line l){
        ExtendedRational[] canon = l.slope();
        ExtendedRational m = canon[0];

        ExtendedRational a = m.multiply(m).add(Rational.ONE);

        ExtendedRational sub = canon[1].add(Y.negate());

        ExtendedRational b = m.multiplyRational(new Rational(2,1)).multiply(sub)
                .add(X.multiplyRational(new Rational(2,1)).negate());

        ExtendedRational c = r.multiply(r);
        c = c.negate().add(sub.multiply(sub)).add(X.multiply(X));

        ExtendedRational D = b.multiply(b).add(a.multiply(c)
                .multiplyRational(new Rational(4, 1)).negate());
        if(D.toDouble() < 0){
            return null;
        }

        ExtendedRational x1 = b.add(D.root(2).negate()).divide(a.multiplyRational(new Rational(2,1)));
        ExtendedRational x2 = b.add(D.root(2)).divide(a.multiplyRational(new Rational(2,1)));

        /// One return
        return new ExtendedRational[] {x1, l.yAt(x1)};

    }
}
