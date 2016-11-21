package client.updater;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class WhatsNew extends JFrame {

    private static final long serialVersionUID = -8477889375636358881L;
    private JEditorPane infoPane;
    private JScrollPane scp;
    private JButton ok;
    private JButton cancel;
    private JPanel pan1;
    private JPanel pan2;

    public WhatsNew(String info) {
        initComponents();
        infoPane.setText(info);
    }

    private void initComponents() {

        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.setTitle("New Update Found");
        this.setResizable(false);

        pan1 = new JPanel();
        pan1.setLayout(new BorderLayout());

        pan2 = new JPanel();
        pan2.setLayout(new FlowLayout());

        infoPane = new JEditorPane();
        infoPane.setContentType("text/html");

        scp = new JScrollPane();
        scp.setViewportView(infoPane);

        ok = new JButton("Update");
        ok.addActionListener((ActionEvent e) -> {
            update();
            WhatsNew.this.dispose();
        });

        cancel = new JButton("Cancel");
        cancel.addActionListener((ActionEvent e) -> {
            WhatsNew.this.dispose();
        });

        pan2.add(ok);
        pan2.add(cancel);
        pan1.add(pan2, BorderLayout.SOUTH);
        pan1.add(scp, BorderLayout.CENTER);
        this.add(pan1);
        pack();
        setVisible(true);
        this.setSize(300, 200);
        this.setLocationRelativeTo(null);
    }

    private void update() {
        new Start().setVisible(true);
    }

}