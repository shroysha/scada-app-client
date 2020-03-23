package dev.shroysha.scada.app.client.view;

import dev.shroysha.scada.ejb.ScadaSite;
import lombok.Getter;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.util.ArrayList;


public class ScadaJTree extends JTree {

    @Getter
    private final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Sites");
    private final DefaultTreeModel siteModel = new DefaultTreeModel(root);

    public ScadaJTree() {
        super();
        init();
    }

    private void init() {
        this.setModel(siteModel);
        this.setCellRenderer(new ScadaCellRenderer());
        this.setFocusable(true);
    }

    public void updateScadaSites(ArrayList<ScadaSite> sites) {

        for (ScadaSite site : sites) {

            boolean found = false;

            for (int i = 0; i < root.getChildCount() && !found; i++) {
                TreeNode node = root.getChildAt(i);

                if (node instanceof ScadaNode) {
                    ScadaNode sNode = (ScadaNode) node;
                    if (sNode.getSite().getId() == site.getId()) {
                        siteModel.reload(node);
                        found = true;
                    }
                }
            }

            if (!found) {
                addSite(site);
            }
        }
    }

    public void addSite(ScadaSite site) {
        root.add(new ScadaNode(site));
    }


    ScadaNode getScadaNode(ScadaSite site) {
        // searches the entire tree, leafs and all

        return searchParent(root, site);
    }

    protected ScadaNode searchParent(
            DefaultMutableTreeNode node,
            ScadaSite site) { // returns the scadanode belonging to the site or null if not found

        if (!node.isLeaf()) { // if is a directory
            DefaultMutableTreeNode[] nodes = new DefaultMutableTreeNode[node.getChildCount()];
            for (int i = 0; i < nodes.length; i++) {
                nodes[i] = (DefaultMutableTreeNode) node.getChildAt(i);
                ScadaNode siteNode = searchParent(nodes[i], site);
                if (siteNode != null) {
                    return siteNode;
                }
            }
        } else {
            if (node instanceof ScadaNode) {
                ScadaNode sNode = (ScadaNode) node;
                if (sNode.getSite().getId() == site.getId()) {
                    return sNode;
                }
            }
        }

        return null;
    }

    public ScadaSite getSelected() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();

        if (node instanceof ScadaNode) {
            return ((ScadaNode) node).getSite();
        } else {
            return null;
        }
    }

    static class ScadaNode extends DefaultMutableTreeNode {

        private final ScadaSite site;

        public ScadaNode(ScadaSite site) {
            this.site = site;
        }

        public ScadaSite getSite() {
            return site;
        }
    }

    private class ScadaCellRenderer implements TreeCellRenderer {


        public Component getTreeCellRendererComponent(
                JTree jtree,
                Object value,
                boolean selected,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {
            JLabel label = new JLabel();

            if (value instanceof ScadaNode) {
                ScadaNode node = (ScadaNode) value;
                ScadaSite site = node.getSite();
                label.setText(site.getName());

                if (site.isCritical()) {
                    label.setForeground(Color.red);
                } else if (site.isWarning()) {
                    label.setForeground(Color.orange);
                } else {
                    label.setForeground(Color.black);
                }

                if (selected) {
                    if (!hasFocus) {
                        label.setBackground(Color.gray.brighter());
                    } else {
                        label.setBackground(Color.cyan);
                    }
                }

            } else if (value instanceof DefaultMutableTreeNode) {
                label.setText(value.toString());
            } else {
                label.setText("Invalid argument passed");
            }

            return label;
        }
    }
}
