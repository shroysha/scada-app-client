package dev.shroysha.scada.app.client.view;

import javax.swing.*;
import java.awt.*;

public class ScadaSitePanel extends JPanel {

    private final JTextArea status;

    public ScadaSitePanel() {
        setPreferredSize(new Dimension(1100, 300));
        setBackground(Color.DARK_GRAY);
        status = new JTextArea(17, 80);
        status.setEditable(false);

        JScrollPane scrollStatus = new JScrollPane(status);
        scrollStatus.setPreferredSize(new Dimension(1050, 290));
        scrollStatus.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollStatus.setAutoscrolls(true);

        add(scrollStatus);
    }

    public void setText(String text) {
        status.setText(text);
    }

    public void clearText() {
        status.setText("");
    }
}
