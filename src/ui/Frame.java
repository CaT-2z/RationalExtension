package src.ui;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {

    JTextField position = new JTextField(20);

    public Frame(){
        super("Extended Rationals");
        setSize(500, 500);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initComponents();
    }

    enum state {
        LINESTART,
        LINEEND,
        CIRCLESTART,
        CIRCLEEND,
    }


    private void initComponents(){

        this.setLayout(new BorderLayout());
        DrawPanel panel = new DrawPanel();
        panel.add(position, BorderLayout.SOUTH);
        add(panel, BorderLayout.CENTER);
        position.setEnabled(false);
        position.setDisabledTextColor(Color.BLACK);
        panel.addMouseListener(new MyMouseListener(this));
        JPanel buttonsPanel = new JPanel();
        JButton lineButt = new JButton("Line");
        buttonsPanel.add(lineButt, BorderLayout.WEST);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

}
