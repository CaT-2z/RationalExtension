package src.ui;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {

    DrawPanel panel;
    JTextField position;

    public Frame(){
        super("Extended Rationals");
        setSize(500, 500);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initComponents();
    }


    private void initComponents(){

        this.setLayout(new BorderLayout());
        panel = new DrawPanel();

        position = new JTextField(20);

        panel.add(position, BorderLayout.SOUTH);
        add(panel, BorderLayout.CENTER);
        position.setEnabled(false);
        position.setDisabledTextColor(Color.BLACK);
        panel.addMouseListener(new MyMouseListener(this));
        JPanel buttonsPanel = new JPanel();

        JButton lineButt = new JButton("Line");
        lineButt.addActionListener((e) -> panel.state = DrawPanel.State.LINESTART);

        JButton circButt = new JButton("Circle");
        circButt.addActionListener((e) -> panel.state = DrawPanel.State.CIRCLESTART);


        buttonsPanel.add(lineButt, BorderLayout.WEST);
        buttonsPanel.add(circButt, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

}
