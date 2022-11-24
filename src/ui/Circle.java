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
        if(obj instanceof Line){
            return getLineIntersection((Line) obj);
        }
        if(obj instanceof Circle){
            return getCircleIntersection((Circle) obj);
        }
        return null;
    }

    /// y = mx + b
    /// (x-x1)^2 + (y-y1)^2 = d^2
    /// (x-x1)^2 + (mx + b - y1)^2 = d^2
    /// x^2 - 2xx_1 + x1^2 + mx^2 + 2mx(b-y1) + (b-y1)^2 - d^2 = 0
    /// x^2 + mx^2 | 2xx_1 + 2mx(b-y1) | + x1^2 + (b-y1)^2 - d^2
    /// x^2 - 2xx_1 + mx^2 + 2mx(b-y1) = d^2 - (b-y1)^2 - x1^2
    /// (m^2 + 1)x^2 + (2m(b-y1) - 2x1)x + ( - d^2 + (b-y1)^2 + x1^2) = 0
    /// b +- sqrt(b^2 - 4ac) ...
    public ExtendedRational[] getLineIntersection(Line l){

        double[] sanity = cheating(l.slope());

        ExtendedRational[] canon = l.slope();
        return getLineIntFromSlope(canon);
    }

    public ExtendedRational[] getLineIntFromSlope(ExtendedRational[] canon){
        ExtendedRational m = canon[0];
        ExtendedRational c = canon[1];
        ExtendedRational p = X;
        ExtendedRational q = Y;

        ExtendedRational A = m.multiply(m).add(Rational.ONE);
        ExtendedRational B = (m.multiply(c).add(m.multiply(q).negate())
                .add(p.negate())).multiplyRational(new Rational(2,1));
        ExtendedRational C = q.multiply(q).add(r.multiply(r).negate()).add(p.multiply(p))
                .add(c.multiply(q).multiplyRational(new Rational(2,1)).negate())
                .add(c.multiply(c));

        ExtendedRational D = B.multiply(B).add(A.multiply(C).multiplyRational(new Rational(4,1)).negate());

        if(D.toDouble() < 0) return null;

        ExtendedRational x1 = (B.negate().add(D.root(2)).divide(A.multiplyRational(new Rational(2,1))));
        ExtendedRational x2 = (B.negate().add(D.root(2).negate()).divide(A.multiplyRational(new Rational(2,1))));

        return new ExtendedRational[] {x1, Line.yAt(canon, x1), x2, Line.yAt(canon, x2)};
    }

    public double[] circleSanity(Circle obj){
        double a = X.toDouble();
        double b = Y.toDouble();
        double d = obj.X.toDouble();
        double e = obj.Y.toDouble();

        double q = d - a;
        double p = e - b;

        double C = r.toDouble()*r.toDouble() - obj.r.toDouble()*obj.r.toDouble() + p*p + q*q;
        double B = -2*p;
        double A = -2*q;

        return new double[] {a,b,d,e,q,p,A,B,C};
    }

    ///(x-a) + (y-b) = d
    ///     q x2        p y2
    ///(x-(d-a)) + (y -(e-b)) = d -> (x-q) + (y-p) = d
    /// x1 + y1 = r1
    /// x** - 2qx + q** + y** - 2py + p** = d**
    /// -2qx -2py + (r** - d** + q** + p**) = 0
    /// (q/p)x + y + (C/-2p) = 0
    ///
    public ExtendedRational[] getCircleIntersection(Circle c){

        double[] sanity = circleSanity(c);

        /// Remember to add back thhese at the end
        ExtendedRational q = c.X.add(X.negate());
        ExtendedRational p = c.Y.add(Y.negate());

        /// C = (x1** + x2** + r1** - r2**)
        ExtendedRational C = (r.multiply(r).add(    c.r.multiply(c.r).negate()  ))
                .add(q.multiply(q)).add(p.multiply(p));

        /// A = -2x2
        ExtendedRational A = q.multiplyRational(new Rational(-2,1));

        /// B = -2y2
        ExtendedRational B = p.multiplyRational(new Rational(-2,1));

        /// Ax + By = C
        /// By = -Ax + C
        /// y = -(A/B)x + (C/B)
        ExtendedRational[] canon = new ExtendedRational[2];
        canon[0] = (A.divide(B).negate());
        ///magic subtraction
        canon[1] = (C.divide(B).negate().add(canon[0].multiply(X).negate()).add(Y));

        return getLineIntFromSlope(canon);
    }


    ///(x-x1)^2 + (mx + b-y1)^2 = d^2
    ///x^2 - 2x*x_1 + x1^2
    public double[] cheating(ExtendedRational[] sl ){
        double m = sl[0].toDouble();
        double c = sl[1].toDouble();
        double p = X.toDouble();
        double q = Y.toDouble();
        double r = this.r.toDouble();

        double A = m*m + 1;
        double B = 2*(m*c - m*q - p);
        double C = q*q - r*r + p*p - 2*c*q + c*c;

        double d = B*B - 4*A*C;

        if(d < 0) return null;
        double val = (-B+Math.sqrt(d))/(2*A);
        return new double[] {m, c, p, q, r, A, B, C, d, val, val*m+c };
    }
}
