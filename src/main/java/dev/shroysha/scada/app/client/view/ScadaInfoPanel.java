package dev.shroysha.scada.app.client.view;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;


public class ScadaInfoPanel extends JPanel {

    private final JPanel buttonPanel = new JPanel();
    private final JButton alarmsButton = new JButton("Alarms");
    private final DefaultMutableTreeNode displayNodes = new DefaultMutableTreeNode("Top");
    private final JButton sitesButton = new JButton("Sites");
    private final JTree displayTree = new JTree(displayNodes);
    private final JScrollPane treeView = new JScrollPane(displayTree);

    public ScadaInfoPanel() {
        this.setPreferredSize(new Dimension(250, 500));
        this.setLayout(new BorderLayout());

        buttonPanel.setPreferredSize(new Dimension(250, 40));

        buttonPanel.add(sitesButton, BorderLayout.NORTH);
        buttonPanel.add(alarmsButton, BorderLayout.NORTH);

        createSiteTree();

        treeView.setPreferredSize(new Dimension(225, 425));

        this.add(treeView, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.NORTH);
    }

  /*public void setSiteList(ArrayList<ScadaSite> aSites)
  {
      sites = aSites;
      this.createSiteTree();
  }*/

    private void createSiteTree() {
        DefaultMutableTreeNode category = new DefaultMutableTreeNode("Alarms");

        category.add(new DefaultMutableTreeNode("Site 1"));
        category.add(new DefaultMutableTreeNode("Site 4"));
        category.add(new DefaultMutableTreeNode("Site 3"));
        category.add(new DefaultMutableTreeNode("Site 8"));

        displayNodes.add(category);

        category = new DefaultMutableTreeNode("Warnings");

        category.add(new DefaultMutableTreeNode("Site 21"));
        category.add(new DefaultMutableTreeNode("Site 24"));
        category.add(new DefaultMutableTreeNode("Site 23"));
        category.add(new DefaultMutableTreeNode("Site 28"));

        displayNodes.add(category);
    }

    public void createAlarmTree() {
        DefaultMutableTreeNode category;
        category = new DefaultMutableTreeNode("Alarms");

        category.add(new DefaultMutableTreeNode("Site 1"));
        category.add(new DefaultMutableTreeNode("Site 4"));
        category.add(new DefaultMutableTreeNode("Site 3"));
        category.add(new DefaultMutableTreeNode("Site 8"));

        displayNodes.add(category);

        category = new DefaultMutableTreeNode("Warnings");

        category.add(new DefaultMutableTreeNode("Site 21"));
        category.add(new DefaultMutableTreeNode("Site 24"));
        category.add(new DefaultMutableTreeNode("Site 23"));
        category.add(new DefaultMutableTreeNode("Site 28"));

        displayNodes.add(category);
    }
}
