package dev.shroysha.scada.app.client.view;

import dev.shroysha.scada.ejb.ScadaSite;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

/**
 * Just a copy of the previous jtree, however it has different nodes for critical and warning
 */
public class ScadaJTreePrioritized extends ScadaJTree {

    private DefaultMutableTreeNode critical, warning;

    public ScadaJTreePrioritized() {
        super();
        init();
    }

    private void init() {
        critical = new DefaultMutableTreeNode("Critical");
        warning = new DefaultMutableTreeNode("Warning");

        this.getRoot().add(critical);
        this.getRoot().add(warning);

        this.setRootVisible(false);
    }

  /*

  public void updateScadaSites(ArrayList<ScadaSite> sites) {
      DefaultMutableTreeNode root = new DefaultMutableTreeNode();
      DefaultMutableTreeNode critical = new DefaultMutableTreeNode("Critical");
      DefaultMutableTreeNode warning = new DefaultMutableTreeNode("Warning");

      root.add(critical);
      root.add(warning);



      TreeModel siteModel = new DefaultTreeModel(root);

      for(ScadaSite site: sites) {
          if(site.getAlarm()) {
              critical.add(new ScadaNode(site));
          }
          if(site.getWarning()) {
              warning.add(new ScadaNode(site));
          }
      }
      warning.

      this.setModel(siteModel);

      for (int i = 0; i < this.getRowCount(); i++)
      {
       this.expandRow(i);
      }

  }
  */


    public void updateScadaSites(ArrayList<ScadaSite> sites) {

        for (ScadaSite site : sites) {
            ScadaNode node = getScadaNode(site);
            if (node != null) {
                node.removeFromParent();
                placeIntoCorrectTree(node);
            } else { // if the site isn't found
                placeIntoCorrectTree(new ScadaNode(site));
            }
        }
    }

    private void placeIntoCorrectTree(ScadaNode node) {
        if (node.getSite().isCritical()) {
            critical.add(node);
        } else if (node.getSite().isWarning()) {
            warning.add(node);
        }
    }

    public ScadaSite getSelected() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();

        if (node instanceof ScadaNode) {
            return ((ScadaNode) node).getSite();
        } else {
            return null;
        }
    }
}
