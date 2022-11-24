package src.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

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

        JButton loadButt = new JButton("Load");
        loadButt.addActionListener((e) -> loadConf());

        JButton lineButt = new JButton("Line");
        lineButt.addActionListener((e) -> panel.state = DrawPanel.State.LINESTART);

        JButton circButt = new JButton("Circle");
        circButt.addActionListener((e) -> panel.state = DrawPanel.State.CIRCLESTART);

        JButton selButt = new JButton("Select");
        selButt.addActionListener((e) -> panel.state = DrawPanel.State.SELECT);

        JButton savButt = new JButton("Save");
        savButt.addActionListener((e) -> saveConf());


        buttonsPanel.add(savButt);
        buttonsPanel.add(loadButt);
        buttonsPanel.add(lineButt, BorderLayout.WEST);
        buttonsPanel.add(circButt, BorderLayout.CENTER);
        buttonsPanel.add(selButt);
        add(buttonsPanel, BorderLayout.SOUTH);


        backPanel = new JPanel();
        JButton revert = new JButton("Go back");
        revert.addActionListener((e) -> panel.fuckGoBack());

        JButton inters = new JButton("Intersection");
        inters.addActionListener((e) -> panel.state = DrawPanel.State.INTERSECT);

        JButton deler = new JButton("Delete");
        deler.addActionListener((e) -> panel.deleteThis() );

        backPanel.add(revert, BorderLayout.CENTER);

        backPanel.add(inters);

        backPanel.add(deler);

    }

    private void saveConf(){
        FileOutputStream os = null;
        try {
            os = new FileOutputStream("write.piss");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        ObjectOutputStream obj = null;
        try {
            obj = new ObjectOutputStream(os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            obj.writeObject(panel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            obj.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadConf(){
        FileInputStream is = null;
        try {
            is = new FileInputStream("write.piss");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        ObjectInputStream obj = null;
        try {
            obj = new ObjectInputStream(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        remove(panel);
        try {
            panel = (DrawPanel) obj.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        add(panel, BorderLayout.CENTER);
        panel.addSelectionEventListener(this::selectionPanel);
        panel.repaint();
        repaint();
    }

}
