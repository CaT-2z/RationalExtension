package src.ui;

import src.ExtendedRational;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Az egérkezelő
 */
class MyMouseListener implements MouseListener {

    /**
     * A program kerete
     */
    private final Frame frame;

    /**
     * a kiválasztott x1 koordináta
     */
    ExtendedRational x1;

    /**
     * A kiválasztott y1 koordináta
     */
    ExtendedRational y1;

    /**
     * Konstruktor
     * @param frame a keret
     */
    public MyMouseListener(Frame frame) {
        this.frame = frame;
    }

    /**
     * Gombnyomás kezelő, a rajztábla állapotától függően mást hív meg.
     * @param e the event to be processed
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        DrawPanel panel = (DrawPanel) e.getComponent();
        ExtendedRational xpos = ExtendedRational.fromScreenSpace(panel.X1, panel.X2, panel.getWidth(), x);
        ExtendedRational ypos = ExtendedRational.fromScreenSpace(panel.Y1, panel.Y2, panel.getHeight(), y);
        frame.position.setText("{" + xpos + "},{" + ypos + "}");


        switch (((DrawPanel)e.getComponent()).state){
            case LINESTART -> onLineStart(e);
            case LINEEND -> onLineEnd(e);
            case CIRCLESTART -> onCircleStart(e);
            case CIRCLEEND -> onCircleEnd(e);
            case SELECT -> onSelect(e);
            case INTERSECT -> onIntersection(e);
        }
        e.getComponent().requestFocus();
    }

    /**
     * Rádiusz számol x1 y1 és e ből, majd belerakja a kört a listába.
     * @param e event
     */
    private void onCircleEnd(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        DrawPanel panel = (DrawPanel) e.getComponent();
        ExtendedRational xpos = ExtendedRational.fromScreenSpace(panel.X1, panel.X2, panel.getWidth(), x);
        ExtendedRational ypos = ExtendedRational.fromScreenSpace(panel.Y1, panel.Y2, panel.getHeight(), y);

        ExtendedRational xDist = x1.negate().add(xpos);
        ExtendedRational yDist = y1.negate().add(ypos);
        ExtendedRational distance = xDist.multiply(xDist).add(yDist.multiply(yDist)).root(2);

        ((DrawPanel) e.getComponent()).circles.add(new Circle(x1, y1, distance));
        ((DrawPanel) e.getComponent()).selectedPoint = null;

        e.getComponent().repaint();

        ((DrawPanel)e.getComponent()).state = DrawPanel.State.NEUTRAL;
    }

    /**
     * Elmenti az x1, y1-et, majd CircleEndbe rakja a status-t
     * @param e event
     */
    private void onCircleStart(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        DrawPanel panel = (DrawPanel) e.getComponent();
        ExtendedRational[] points = panel.simpleSelect(x, y);
        ExtendedRational xpos = points[0];
        ExtendedRational ypos = points[1];
        x1 = xpos;
        y1 = ypos;
        frame.position.setText("{" + xpos + "},{" + ypos + "}");

        e.getComponent().repaint();

        ((DrawPanel)e.getComponent()).state = DrawPanel.State.CIRCLEEND;

    }


    /**
     * unused implement boilerplate
     *
     */
    @Override
    public void mousePressed(MouseEvent e) {

    }

    /**
     * unused implement boilerplate
     *
     */
    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * unused implement boilerplate
     *
     */
    @Override
    public void mouseEntered(MouseEvent e) {

    }

    /**
     * unused implement boilerplate
     *
     */
    @Override
    public void mouseExited(MouseEvent e) {

    }

    /**
     * Elmenti az x1, y1-et, átvált LineEndbe
     * @param e event
     */
    public void onLineStart(MouseEvent e){
        int x = e.getX();
        int y = e.getY();
        DrawPanel panel = (DrawPanel) e.getComponent();
        ExtendedRational[] points = panel.simpleSelect(x, y);
        ExtendedRational xpos = points[0];
        ExtendedRational ypos = points[1];
        x1 = xpos;
        y1 = ypos;
        frame.position.setText("{" + xpos + "},{" + ypos + "}");

        e.getComponent().repaint();

        ((DrawPanel)e.getComponent()).state = DrawPanel.State.LINEEND;
    }

    /**
     * Vonalat számol x1 y1 e ből, belerakja a listába.
     * @param e event
     */
    public void onLineEnd(MouseEvent e){
        int x = e.getX();
        int y = e.getY();
        DrawPanel panel = (DrawPanel) e.getComponent();
        ExtendedRational xpos = ExtendedRational.fromScreenSpace(panel.X1, panel.X2, panel.getWidth(), x);
        ExtendedRational ypos = ExtendedRational.fromScreenSpace(panel.Y1, panel.Y2, panel.getHeight(), y);

            ((DrawPanel) e.getComponent()).lines.add(new Line(x1, y1, xpos, ypos));
        ((DrawPanel) e.getComponent()).selectedPoint = null;

        e.getComponent().repaint();

        ((DrawPanel)e.getComponent()).state = DrawPanel.State.NEUTRAL;
    }

    /**
     * megpróbál e szerint kiválasztani valamit a táblából, ha sikerül, átlép kontextus módba.
     * @param e event
     */
    public void onSelect(MouseEvent e){
        DrawPanel panel = (DrawPanel) e.getComponent();
        if(panel.SelectAt(e.getX(), e.getY())){
            panel.repaint();
            ((DrawPanel)e.getComponent()).selectionListener.accept();
            ((DrawPanel)e.getComponent()).state = DrawPanel.State.NEUTRAL;
        }
    }

    /**
     * Megpróbál mettszéspontot számolni, továbbküldi a rajztáblának.
     * @param e event
     */
    public void onIntersection(MouseEvent e){
        DrawPanel panel = (DrawPanel) e.getComponent();
        IDrawnObj sel = panel.Selected;
        if(panel.SelectAt(e.getX(), e.getY())){
            ExtendedRational[] inter = sel.getIntersection(panel.Selected);
            if(inter != null){
                frame.position.setText("{" + inter[0] + "},{" + inter[1] + "}");
                for(int i = 0; i < inter.length/2; i++){
                    panel.points.add(new MyDot(inter[2*i], inter[2*i+1]));
                }
                panel.Selected = null;
                panel.repaint();
                panel.state = DrawPanel.State.NEUTRAL;
                panel.selectionListener.accept();
            }
            else{
                panel.Selected = sel;
                frame.position.setText("Not Found");
            }
        }else{
            frame.position.setText("Nothing selected");
        }

    }

}
