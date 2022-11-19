package src.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Frame extends JFrame {

    DrawPanel panel;
    JTextField position;

    JPanel buttonsPanel;

    JPanel backPanel;

    public Frame(){
        super("Extended Rationals");
        setSize(500, 500);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initComponents();
    }

    boolean isSelected = false;
    public void selectionPanel(){
        if(isSelected){
            remove(backPanel);
            add(buttonsPanel, BorderLayout.SOUTH);
            this.repaint();
            validate();
        }
        else{
            remove(buttonsPanel);
            add(backPanel, BorderLayout.SOUTH);
            this.repaint();
            validate();
        }
        isSelected = !isSelected;
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
        panel.setFocusable(true);
        panel.setRequestFocusEnabled(true);
        panel.addKeyListener(new MyKeyListener());
        panel.addSelectionEventListener(this::selectionPanel);

        buttonsPanel = new JPanel();

        JButton lineButt = new JButton("Line");
        lineButt.addActionListener((e) -> panel.state = DrawPanel.State.LINESTART);

        JButton circButt = new JButton("Circle");
        circButt.addActionListener((e) -> panel.state = DrawPanel.State.CIRCLESTART);

        JButton selButt = new JButton("Select");
        selButt.addActionListener((e) -> panel.state = DrawPanel.State.SELECT);


        buttonsPanel.add(lineButt, BorderLayout.WEST);
        buttonsPanel.add(circButt, BorderLayout.CENTER);
        buttonsPanel.add(selButt);
        add(buttonsPanel, BorderLayout.SOUTH);


        backPanel = new JPanel();
        JButton revert = new JButton("Go back");
        JButton inters = new JButton("Intersection");
        revert.addActionListener((e) -> selectionPanel());
        inters.addActionListener((e) -> panel.state = DrawPanel.State.INTERSECT);
        backPanel.add(revert, BorderLayout.CENTER);
        backPanel.add(inters);

    }

}
