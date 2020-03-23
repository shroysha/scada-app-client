package dev.shroysha.scada.app.client.view.googlestaticmaps;

import dev.shroysha.scada.ejb.ScadaSite;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GoogleMapPanel extends JPanel {

    private static final int siteCircleDiameter = 9;
    private final int width = 1000;
    private final int height = 600;
    private final double Y_POINT_ADJUST = 39.879741552001455;
    private final double X_POINT_ADJUST = -78.597212047634912;
    private final double X_DISTANCE_TOTAL = 1.125403047634912;
    private final double Y_DISTANCE_TOTAL = 0.556531552001455;
    private final double LON_RATIOX = 1000 / this.X_DISTANCE_TOTAL;
    private final double LAT_RATIOY = 478 / this.Y_DISTANCE_TOTAL;
    BufferedImage background;
    boolean alarmLabelOn;
    private File soundFile;
    private AudioInputStream audioStream;
    private SourceDataLine sourceLine;
    private ArrayList<ScadaSite> sitePoints;
    private boolean firstAlarm;

    public GoogleMapPanel() {
        sitePoints = new ArrayList<>();

        firstAlarm = true;

        this.setPreferredSize(new Dimension(1000, 600));
        this.setBackground(Color.white);
        try {
            background = ImageIO.read(new File("WashCoClient.jpg"));
        } catch (Exception e) {
            System.out.println("Could not find image background.");
        }
    }

    public void setSitePoints(ArrayList<ScadaSite> aSitePoints) {
        this.sitePoints = aSitePoints;
        System.out.println("The size of passed sitepoints: " + aSitePoints.size());
        repaint();
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g.drawImage(background, 0, 0, 1000, 475, this);

        g.setFont(new Font("Calibri", Font.BOLD, 20));
        g.setColor(Color.red);
        int LEFT_INDENT = 15;
        int BOTTOM_OFFSET = 15;
        g.drawString(
                "Washington County Scada System Monitor", LEFT_INDENT, this.getHeight() - BOTTOM_OFFSET);

        g.setFont(new Font("Calibri", Font.BOLD, 60));

        for (ScadaSite sp : sitePoints) {
            String toDisplay;

            if (sp.isWarning()) {
                g2.setColor(Color.ORANGE);
                toDisplay = "WARNING";
            } else if (sp.isCritical()) {
                g2.setColor(Color.RED);
                toDisplay = "CRITICAL";

                if (firstAlarm) {
                    this.playSound();
                    firstAlarm = false;
                }
            } else {
                g2.setColor(Color.GREEN.darker());
                toDisplay = "Normal";
            }

            int x = (int) sp.getLon();
            int y = (int) sp.getLat();

            g2.fillOval(x, y, siteCircleDiameter, siteCircleDiameter);
            g2.drawString(toDisplay, 10, 250);
        }

    }

    public void playSound() {
        String strFilename = "beep.wav";

        try {
            soundFile = new File(strFilename);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            audioStream = AudioSystem.getAudioInputStream(soundFile);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        AudioFormat audioFormat = audioStream.getFormat();

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try {
            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(audioFormat);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        sourceLine.start();

        int nBytesRead = 0;
        int BUFFER_SIZE = 128000;
        byte[] abData = new byte[BUFFER_SIZE];
        while (nBytesRead != -1) {
            try {
                nBytesRead = audioStream.read(abData, 0, abData.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (nBytesRead >= 0) {
                @SuppressWarnings("unused")
                int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
            }
        }

        sourceLine.drain();
        sourceLine.close();
    }

    public void mouseClick() {
    }

    public void buttonClick() {
    }
}
