package src.ui;

import src.ExtendedRational;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Frame extends JFrame {

    JTextField position = new JTextField(20);

    public Frame(){
        super("Extended Rationals");
        setSize(500, 500);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initComponents();
    }

    class line{
        int x1;
        int x2;
        int y1;
        int y2;

        public line(int a, int b, int c, int d){
            x1 = a; y1 = b; x2 = c; y2 = d;
        }
    }


    private void initComponents(){

        this.setLayout(new BorderLayout());
        DrawPanel panel = new DrawPanel();
        panel.add(position, BorderLayout.SOUTH);
        add(panel, BorderLayout.CENTER);
        position.setEnabled(false);
        position.setDisabledTextColor(Color.BLACK);
        panel.addMouseListener(new MouseListener() {
            private boolean status = true;
            int x1;
            int y1;

            @Override
            public void mouseClicked(MouseEvent e) {
                int x=e.getX();
                int y=e.getY();
                DrawPanel panel = (DrawPanel) e.getComponent();
                ExtendedRational xpos = ExtendedRational.fromScreenSpace(panel.getX1(),panel.getX2(), panel.getWidth(), x);
                ExtendedRational ypos = ExtendedRational.fromScreenSpace(panel.getY1(),panel.getY2(), panel.getHeight(), y);
                position.setText(xpos+","+ypos);
                if(status){
                    x1 = x;
                    y1 = y;
                }else{
                    ((DrawPanel)e.getComponent()).lines.add(new line(x1,y1,x,y));
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
        });
    }
}
