package src.ui;

import src.Rational;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

class DrawPanel extends JPanel {

    Rational X1;
    Rational X2;
    Rational Y1;
    Rational Y2;

    public DrawPanel(){
        X1 = new Rational(-3, 1);
        X2 = new Rational(-1, 1);
        Y1 = new Rational(-1, 1);
        Y2 = new Rational(1, 1);

        setSize(500, 500);

    }

    public ArrayList<Line> lines = new ArrayList<>();

    private void drawWireFrame(Graphics g){
        ///Not important
        ///Gets the order of magnitude
//        double Xmagnitude = Math.log(Xend - Xstart)/Math.log(4);

    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
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
        g.setColor(Color.RED);
        for (Line l: lines) {
            g.drawLine(l.x1.toScreenSpace(X1, X2, getWidth()), l.y1.toScreenSpace(Y1, Y2, getHeight())
                    , l.x2.toScreenSpace(X1, X2, getWidth()), l.y2.toScreenSpace(Y1, Y2, getHeight()));
        }

    }

}
