package src.ui;

import src.ExtendedRational;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

class MyMouseListener implements MouseListener {
    private final Frame frame;
    private boolean status = true;
    ExtendedRational x1;
    ExtendedRational y1;

    public MyMouseListener(Frame frame) {
        this.frame = frame;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        switch (((DrawPanel)e.getComponent()).state){
            case LINESTART -> onLineStart(e);
            case LINEEND -> onLineEnd(e);
            case CIRCLESTART -> onCircleStart(e);
            case CIRCLEEND -> onCircleEnd(e);
        }
    }

    private void onCircleEnd(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        DrawPanel panel = (DrawPanel) e.getComponent();
        ExtendedRational xpos = ExtendedRational.fromScreenSpace(panel.X1, panel.X2, panel.getWidth(), x);
        ExtendedRational ypos = ExtendedRational.fromScreenSpace(panel.Y1, panel.Y2, panel.getHeight(), y);
        frame.position.setText("{" + xpos + "},{" + ypos + "}");

        ExtendedRational xDist = x1.negate().add(xpos);
        ExtendedRational yDist = y1.negate().add(ypos);
        ExtendedRational distance = xDist.multiply(xDist).add(yDist.multiply(yDist)).root(2);

        ((DrawPanel) e.getComponent()).circles.add(new Circle(x1, y1, distance));
        e.getComponent().repaint();

        ((DrawPanel)e.getComponent()).state = DrawPanel.State.NEUTRAL;
    }

    private void onCircleStart(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        DrawPanel panel = (DrawPanel) e.getComponent();
        ExtendedRational xpos = ExtendedRational.fromScreenSpace(panel.X1, panel.X2, panel.getWidth(), x);
        ExtendedRational ypos = ExtendedRational.fromScreenSpace(panel.Y1, panel.Y2, panel.getHeight(), y);
        frame.position.setText("{" + xpos + "},{" + ypos + "}");
        x1 = xpos;
        y1 = ypos;

        ((DrawPanel)e.getComponent()).state = DrawPanel.State.CIRCLEEND;

    }


    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public void onLineStart(MouseEvent e){
        int x = e.getX();
        int y = e.getY();
        DrawPanel panel = (DrawPanel) e.getComponent();
        ExtendedRational xpos = ExtendedRational.fromScreenSpace(panel.X1, panel.X2, panel.getWidth(), x);
        ExtendedRational ypos = ExtendedRational.fromScreenSpace(panel.Y1, panel.Y2, panel.getHeight(), y);
        frame.position.setText("{" + xpos + "},{" + ypos + "}");
        x1 = xpos;
        y1 = ypos;

        ((DrawPanel)e.getComponent()).state = DrawPanel.State.LINEEND;
    }

    public void onLineEnd(MouseEvent e){
        int x = e.getX();
        int y = e.getY();
        DrawPanel panel = (DrawPanel) e.getComponent();
        ExtendedRational xpos = ExtendedRational.fromScreenSpace(panel.X1, panel.X2, panel.getWidth(), x);
        ExtendedRational ypos = ExtendedRational.fromScreenSpace(panel.Y1, panel.Y2, panel.getHeight(), y);
        frame.position.setText("{" + xpos + "},{" + ypos + "}");

            ((DrawPanel) e.getComponent()).lines.add(new Line(x1, y1, xpos, ypos));

        e.getComponent().repaint();

        ((DrawPanel)e.getComponent()).state = DrawPanel.State.NEUTRAL;
    }
}
