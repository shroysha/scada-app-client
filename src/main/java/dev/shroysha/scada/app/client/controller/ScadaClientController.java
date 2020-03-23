package dev.shroysha.scada.app.client.controller;

import dev.shroysha.scada.app.client.view.*;
import dev.shroysha.scada.ejb.ScadaSite;
import org.springframework.boot.SpringApplication;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScadaClientController extends JFrame implements Runnable {

    public static final int CLICK_DISTANCE = 10;
    public static final int FRAME_TITLE_OFFSET = 23;
    private static final Logger log = Logger.getGlobal();
    private static final int FRAME_WIDTH = 1300;
    private static final int FRAME_HEIGHT = 800;
    private static final int MAP_WIDTH = 1000;
    private static final int MAP_HEIGHT = 600;
    // private MapPanel mp;
    private final ScadaGoogleMapPanel map;
    private final ScadaSitePanel sp;
    private final ArrayList<ScadaSite> sites;
    private final JTabbedPane jtreeTabbed;
    private final ScadaJTree scadaTree;
    private final ScadaJTreePrioritized scadaPrioritizedTree;
    private boolean newDataIncomming = true;
    private boolean initSites = false;
    private boolean initStream = true;
    private int numSites = 0;
    private ObjectInputStream in = null;
    // private boolean gotSitesOnce = false;
    private Scanner fileIn = null;
    private int atSite;
    private ScadaSite siteToMon = null;
    private Thread monitor;

    public ScadaClientController() {
        sites = new ArrayList<>();

        setSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        this.setLayout(new BorderLayout());
        setTitle("Washington County Scada System");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);

        monitor = new Thread(this);

        map = new ScadaGoogleMapPanel();
        jtreeTabbed = new JTabbedPane();
        jtreeTabbed.setPreferredSize(new Dimension(500, 500));
        scadaTree = new ScadaJTree();
        scadaPrioritizedTree = new ScadaJTreePrioritized();

        TreeListener tl = new TreeListener();

        scadaTree.addTreeSelectionListener(tl);
        scadaPrioritizedTree.addTreeSelectionListener(tl);

        makeTabbedPane();

        sp = new ScadaSitePanel();
        ScadaInfoPanel infop = new ScadaInfoPanel();

        this.addMouseListener(new ClickListener());
        this.add(map, BorderLayout.CENTER);
        // this.add(controls, BorderLayout.EAST);
        this.add(sp, BorderLayout.SOUTH);
        this.add(jtreeTabbed, BorderLayout.EAST);
        // this.add(infop, BorderLayout.EAST);
        monitor = new Thread(ScadaClientController.this);
    }

    public static void main(String[] args) {
        SpringApplication.run(ScadaClientController.class, args);
    }

    static void dispatch(String[] args) {
        for (String s : args) {
            s = s.replaceAll("-", "");
            char command = s.charAt(0);

            switch (command) {
                case 'v':
                    log.setLevel(Level.ALL);
                    try {
                        FileHandler fh = new FileHandler("clientlog.xml");
                        log.addHandler(fh);
                    } catch (IOException ex) {
                        Logger.getGlobal().info(ex.toString());
                    } catch (SecurityException ex) {
                        Logger.getGlobal().info(ex.toString());
                    }
                default:
            }
        }
    }

    private void makeTabbedPane() {
        JScrollPane treeScroll1 = new JScrollPane(scadaTree);
        JScrollPane treeScroll2 = new JScrollPane(scadaPrioritizedTree);

        jtreeTabbed.addTab("All", treeScroll1);
        jtreeTabbed.addTab("Alarms", treeScroll2);
    }

    private void makeControlPanel() {
        JButton monitorButton = new JButton("Monitor");
        monitorButton.addActionListener(new StartListener());
        JButton user = new JButton("Admin*");
        monitorButton.setBackground(Color.GREEN);
        JPanel controls = new JPanel();
        controls.setPreferredSize(new Dimension(100, 600));
        controls.setLayout(new GridLayout(2, 1));
        controls.add(monitorButton);
        controls.add(user);
    }

    public void start() {
        monitor.start();
    }


    public void run() {

        if (initStream) {
            atSite = 0;

            File serverInfo = new File("main.java.server.ini");

            try {
                fileIn = new Scanner(serverInfo);
            } catch (FileNotFoundException ex) {
                log.log(Level.SEVERE, "Configuration File: main.java.server.ini not found.");
                JOptionPane.showMessageDialog(null, "Configuration File: main.java.server.ini not found.");
            }

            try {
                Socket scadaConnection = new Socket(fileIn.nextLine().trim(), 10000);
                ObjectOutputStream out = new ObjectOutputStream(scadaConnection.getOutputStream());
                in = new ObjectInputStream(scadaConnection.getInputStream());
                initStream = false;
                initSites = true;
                log.log(Level.INFO, "Made connection to: {0}", scadaConnection.getInetAddress().toString());
            } catch (UnknownHostException e) {
                log.log(
                        Level.WARNING,
                        "Unknown Host.  Check ini file.  Contact your administrator is this persists.");
                JOptionPane.showMessageDialog(
                        this, "Unknown Host.  Check ini file.  Contact your administrator is this persists.");
            } catch (IOException e) {
                log.log(
                        Level.WARNING,
                        "Had trouble connecting to main.java.server.  Contact your administrator is this persists.");
                JOptionPane.showMessageDialog(
                        this,
                        "Had trouble connecting to main.java.server.  Contact your administrator is this persists.");
            }
        }

        initSites();

        log.log(Level.INFO, "Going into monitoring mode.");
        monitor();
    }

    public void initSites() {
        while (initSites) {
            try {
                try {
                    Object temp = in.readObject();
                    if (temp instanceof String) {
                        map.setScadaSites(sites);
                        scadaTree.updateScadaSites(sites);
                        scadaPrioritizedTree.updateScadaSites(sites);
                        sp.clearText();
                        sp.setText("Monitor Initialized.");
                        return;
                    } else {
                        numSites++;
                        ScadaSite tSite = (ScadaSite) temp;
                        sites.add(tSite);

                        int alarmInt = 0;
                        if (tSite.isCritical()) {
                            alarmInt = 2;
                        } else if (tSite.isWarning()) {
                            alarmInt = 1;
                        }
                    }
                } catch (ClassNotFoundException ex) {
                    log.log(Level.SEVERE, "Error processing Sites.");
                }

            } catch (IOException ex) {
                log.log(Level.SEVERE, ex.getMessage());
            }
        }
    }

    public void monitor() {
        boolean monitoring = false;

        log.log(Level.SEVERE, "Monitor thread ended.");
    }

    private static class StartListener implements ActionListener {


        public void actionPerformed(ActionEvent ae) {
      /*if(initSites)
        {
            monitor = new Thread(WashCoScadaMonitor.this);
            monitor.start();
            monitorButton.setText("Stop");
            monitorButton.setBackground(Color.RED);
        }
        else if(!monitoring)
        {
            monitor = new Thread(WashCoScadaMonitor.this);
            monitor.start();
            monitoring = true;
            monitorButton.setText("Stop");
            monitorButton.setBackground(Color.RED);
        }
        else
        {
            System.out.println("Stopped!");
            monitoring = false;
            monitorButton.setText("Start");
            monitorButton.setBackground(Color.GREEN);
        }
      mp.buttonClick(ae);*/
        }
    }

    private class ClickListener extends MouseAdapter {


        public void mouseClicked(MouseEvent me) {
            int xClick = me.getX();
            int yClick = me.getY();

            for (ScadaSite ss : sites) {
                /*
        System.out.println("Site X: " + ss.getLon());
        System.out.println("Site Y: " + ss.getLat());
        System.out.println("Your X: " + xClick);
        System.out.println("Your Y: " + (yClick - WashCoScadaConstants.FRAME_TITLE_OFFSET));
        */
                if (Math.abs(xClick - ss.getLon()) < CLICK_DISTANCE
                        && Math.abs(yClick - ss.getLat() - FRAME_TITLE_OFFSET) < CLICK_DISTANCE) {

                    siteToMon = ss;
                    sp.setText(ss.getStatus());
                }
            }
        }
    }


    class TreeListener implements TreeSelectionListener {


        public void valueChanged(TreeSelectionEvent e) {
            ScadaSite ss = null;
            if (e.getSource().equals(scadaTree)) {
                ss = scadaTree.getSelected();
            }

            if (e.getSource().equals(scadaPrioritizedTree)) {
                ss = scadaPrioritizedTree.getSelected();
            }

            if (ss != null) {
                siteToMon = ss;
                sp.setText(ss.getStatus());
            }
        }
    }
}
