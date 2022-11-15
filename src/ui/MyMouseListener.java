package src.ui;

import src.ExtendedRational;

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
        int x = e.getX();
        int y = e.getY();
        DrawPanel panel = (DrawPanel) e.getComponent();
        ExtendedRational xpos = ExtendedRational.fromScreenSpace(panel.X1, panel.X2, panel.getWidth(), x);
        ExtendedRational ypos = ExtendedRational.fromScreenSpace(panel.Y1, panel.Y2, panel.getHeight(), y);
        frame.position.setText("{" + xpos + "},{" + ypos + "}");
        if (status) {
            x1 = xpos;
            y1 = ypos;
        } else {
            ((DrawPanel) e.getComponent()).lines.add(new Line(x1, y1, xpos, ypos));
        }
        status = !status;

        e.getComponent().repaint();
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
}
