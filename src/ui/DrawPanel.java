package src.ui;

import src.ExtendedRational;
import src.Rational;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *  A rajzfelület
 **/
class DrawPanel extends JPanel implements Serializable{

    /**
     *  Kitörli a kiválaszott elemet a felületről.
     */
    public void deleteThis() {
        lines.remove(Selected);
        circles.remove(Selected);
        points.remove(Selected);
        GoBack();
    }

    /**
     *  Unselect függvény. Visszavált a JFrame normális alakjába.
     */
    public void GoBack() {
        Selected = null;
        selectionListener.accept();
        repaint();
    }

    /**
     *  State enum, az állapotfüggvényt valósítja meg.
     */
    enum State {
        NEUTRAL,
        LINESTART,
        LINEEND,
        CIRCLESTART,
        CIRCLEEND,
        SELECT,
        INTERSECT
    }

    /**
     * Az éppen kiválasztott objektum
     */
    IDrawnObj Selected = null;

    /**
     * Jelenlegi állapot
     */
    State state;

    /**
     * A kijelző kező X koordinátája
     */
    Rational X1;
    /**
     * A kijelző végső X koordinátája
     */
    Rational X2;
    /**
     * A kijelző kező Y koordinátája
     */
    Rational Y1;
    /**
     * A kijelző végső Y koordinátája
     */
    Rational Y2;


    /**
     * Az interfész amin keresztül tud updatet küldeni a panel a framenek.
     */
    interface voidFunc{
        public void accept();
    }

    /**
     * A JFrame szelekciós függvénye.
     */
    transient voidFunc selectionListener;

    /**
     *  Összekapcsolja a Panelt a szülőjével.
     * @param e a szülő által adott függvény.
     */
    public void addSelectionEventListener(voidFunc e){
        selectionListener = e;
    }

    /**
     * Konstruktor
     */
    public DrawPanel(){
        X1 = new Rational(0, 1);
        X2 = new Rational(5, 1);
        Y1 = new Rational(0, 1);
        Y2 = new Rational(5, 1);

        setSize(500, 500);
        state = State.NEUTRAL;

    }

    /**
     *  Kiválasztja a monitor pontján levő objektumot, beállítja a Selected-et arra.
     * @param x X koordináta
     * @param y Y koordináta
     * @return Ki lett e választva bármi.
     */
    public boolean SelectAt(int x, int y){
        for (Line l:lines) {
         int ran = l.x2.toScreenSpace(X1,X2, getWidth()) - l.x1.toScreenSpace(X1, X2, getWidth());
         int ls = x - l.x1.toScreenSpace(X1, X2, getWidth());
         float at = ((float)ls)/ran;

         int yran = l.y2.toScreenSpace(Y1, Y2, getHeight()) - l.y1.toScreenSpace(Y1, Y2, getHeight());
         int diff = (int) (at*yran + l.y1.toScreenSpace(Y1, Y2, getHeight()) - y);
         if(Math.abs(diff) < 5){
             Selected = l;
             return true;
         }
        }

        double xVal = ExtendedRational.fromScreenSpace(X1, X2, getWidth(), x).toDouble();
        double yVal = ExtendedRational.fromScreenSpace(Y1, Y2, getHeight(), y).toDouble();

        for(Circle c: circles){
            double dist = (xVal - c.X.toDouble())*(xVal - c.X.toDouble()) + (yVal - c.Y.toDouble())*(yVal - c.Y.toDouble());
            if(Math.abs(dist - c.r.toDouble()*c.r.toDouble()) < (X2.toDouble() - X1.toDouble())*10/getWidth()){
                Selected = c;
                return true;
            }
        }

        return false;
    }

    /**
     * A képernyőn levő vonalak
     */
    public ArrayList<Line> lines = new ArrayList<>();

    /**
     * A képernyőn levő pontok
     */
    public ArrayList<MyDot> points = new ArrayList<>();
    /**
     * A képernyőn levő körök
     */
    public ArrayList<Circle> circles = new ArrayList<>();


    /**
     * Kirajzoló függvény
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));
        double Xstart = X1.getNumerator().doubleValue() / X1.getDenominator().doubleValue();
        double Xend = X2.getNumerator().doubleValue() / X2.getDenominator().doubleValue();
        double Ystart = Y1.getNumerator().doubleValue() / Y1.getDenominator().doubleValue();
        double Yend = Y2.getNumerator().doubleValue() / Y2.getDenominator().doubleValue();



        int start = (int)((Xend - (int)Xend)/(Xend - Xstart)*getWidth());
        for(int i = 0; i < (int)(Xend - Xstart + 2); i++){
            int X = start + (int)(i/(Xend - Xstart)*getWidth());

            ///Text
            char[] str = new Double(Xstart + i).toString().toCharArray();
            int len = str.length > 4 ? 4 : str.length;
            g.drawChars(str, 0,len, X, getHeight()/3);
            g.setColor(Color.BLACK);
            g.drawLine(X, 0, X, getHeight());
        }
        if(1/(Xend-Xstart)*getWidth() > 50){
            for(int i = 0; i < (int)(Xend - Xstart + 3); i++){
                int X = start + (int)((i - 0.5)/(Xend - Xstart)*getWidth());
                g.setColor(Color.GRAY);
                g.drawLine(X, 0, X, getHeight());
            }
        }

        start = (int)((Yend - (int) Yend)/(Yend - Ystart)*getHeight());
        for(int i = 0; i < (int)(Yend - Ystart + 2); i++){
            int Y = start + (int)(i/(Yend - Ystart)*getHeight());
            g.setColor(Color.BLACK);
            char[] str = new Double(Ystart + i).toString().toCharArray();
            int len = str.length > 4 ? 4 : str.length;
            g.drawChars(str, 0,len, getWidth()/3, Y);
            g.drawLine(0, Y, getHeight() + 20, Y);
        }
        if(1/(Yend-Ystart)*getHeight() > 50){
            for(int i = 0; i < (int)(Yend - Ystart + 3); i++){
                int Y = start + (int)((i - 0.5)/(Yend - Ystart)*getHeight());
                g.setColor(Color.GRAY);
                g.drawLine(0, Y, getHeight() + 20, Y);
            }
        }

        g2.setStroke(new BasicStroke(2));
        g.setColor(Color.RED);
        for (Line l: lines) {
            if(l.equals(Selected)) g.setColor(Color.BLUE);
            g.drawLine(l.x1.toScreenSpace(X1, X2, getWidth()), l.y1.toScreenSpace(Y1, Y2, getHeight())
                    , l.x2.toScreenSpace(X1, X2, getWidth()), l.y2.toScreenSpace(Y1, Y2, getHeight()));
            g.setColor(Color.RED);
        }
        for (Circle c: circles){
            if(c.equals(Selected)) g.setColor(Color.BLUE);
            int x = c.X.toScreenSpace(X1, X2, getWidth());
            int y = c.Y.toScreenSpace(Y1, Y2, getHeight());
            int rad = c.r.add(X1).toScreenSpace(X1, X2, getWidth());
            g.drawOval(x-rad, y-rad
                    , rad*2, rad*2);
            g.setColor(Color.RED);
        }

        for(MyDot dot: points){
            g.setColor(Color.MAGENTA);
            if(dot == selectedPoint) g.setColor(Color.ORANGE);
            int x = dot.x.toScreenSpace(X1, X2, getWidth());
            int y = dot.y.toScreenSpace(Y1, Y2, getHeight());
            g.fillOval(x-5, y-5, 10, 10);
        }


    }


    MyDot selectedPoint = null;

    /**
     * Kiválaszt egy pontot, ha nincs epszilon közelségben pont, visszaadja a kattintott koordinátát.
     * @param X X koordináta
     * @param Y Y koordináta
     * @return A kiválasztott koordináták
     */
    ExtendedRational[] simpleSelect(int X, int Y){
        for (MyDot point: points) {
            int realX = point.x.toScreenSpace(X1, X2, getWidth());
            int realY = point.y.toScreenSpace(Y1, Y2, getHeight());
            if(Math.abs(realX - X) < 6 && Math.abs(realY - Y) < 6){
                selectedPoint = point;
                return new ExtendedRational[] {point.x, point.y};
            }
        }
        return new ExtendedRational[] {ExtendedRational.fromScreenSpace(X1, X2, getWidth(), X), ExtendedRational.fromScreenSpace(Y1, Y2, getHeight(), Y)};
    }

}
