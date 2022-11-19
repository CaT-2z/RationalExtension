package src.ui;

///Todo: Rewrite this to ER

import src.ExtendedRational;

class Line implements IDrawnObj{
    ExtendedRational x1;
    ExtendedRational x2;
    ExtendedRational y1;
    ExtendedRational y2;

    public Line(ExtendedRational a, ExtendedRational b, ExtendedRational c, ExtendedRational d) {
        if(a.toDouble() < c.toDouble()) {
            x1 = a;
            x2 = c;
            y1 = b;
            y2 = d;
        } else {
            x1 = c;
            x2 = a;
            y1 = d;
            y2 = b;
        }
    }

    ExtendedRational[] slope(){
        ExtendedRational xran = x2.add(x1.negate());
        ExtendedRational yran = y2.add(y1.negate());
        ExtendedRational slope = yran.divide(xran);
        ExtendedRational offset = slope.multiply(x1).add(y1.negate());
        return new ExtendedRational[] {slope, offset};
    }


    @Override
    public ExtendedRational[] getIntersection(IDrawnObj obj) {
        if(obj instanceof Line){
            Line l = (Line) obj;
            ExtendedRational[] can1 = slope();
            ExtendedRational[] can2 = l.slope();
            ExtendedRational x = can2[1].add(can1[1]);
            ExtendedRational z = can1[0].add(can2[0].negate());
            x = x.divide(z);
            double d = x.toDouble();
            if( d >= x1.toDouble() && d <= x2.toDouble() && d >= l.x1.toDouble() && d <= l.x2.toDouble()){
                return new ExtendedRational[] {x, can1[0].multiply(x).add(can1[1])};
            } else return null;
        }
        return null;
    }
}
