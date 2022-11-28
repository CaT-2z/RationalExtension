package src.ui;

import src.Rational;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * A billentyűkezelő
 */
class MyKeyListener implements KeyListener {


    /**
     * Nem használt, KeyListener miatt van.
     */
    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * Nyílbillentyűkre figyel: mozgatja a rajztáblát.
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        DrawPanel panel = (DrawPanel)   e.getComponent();
        switch (e.getKeyCode()){
            case 37:
                panel.X1 = panel.X1.add(Rational.QUARTER.negate());
                panel.X2 = panel.X2.add(Rational.QUARTER.negate());
                panel.repaint();
                break;
            case 38:
                panel.Y1 = panel.Y1.add(Rational.QUARTER.negate());
                panel.Y2 = panel.Y2.add(Rational.QUARTER.negate());
                panel.repaint();
                break;
            case 39:
                panel.X1 = panel.X1.add(Rational.QUARTER);
                panel.X2 = panel.X2.add(Rational.QUARTER);
                panel.repaint();
                break;
            case 40:
                panel.Y1 = panel.Y1.add(Rational.QUARTER);
                panel.Y2 = panel.Y2.add(Rational.QUARTER);
                panel.repaint();
                break;
        }


    }

    /**
     * Nem használt, KeyListener miatt van.
     */
    @Override
    public void keyReleased(KeyEvent e) {

    }
}
