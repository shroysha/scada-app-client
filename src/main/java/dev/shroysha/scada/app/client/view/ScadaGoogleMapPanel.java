package dev.shroysha.scada.app.client.view;

import dev.shroysha.scada.app.client.view.googlestaticmaps.GoogleMap;
import dev.shroysha.scada.app.client.view.googlestaticmaps.GoogleMapMarker;
import dev.shroysha.scada.ejb.ScadaSite;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class ScadaGoogleMapPanel extends JPanel {

    private final GoogleMap googleMap;
    private File soundFile;
    private AudioInputStream audioStream;
    private SourceDataLine sourceLine;


    public ScadaGoogleMapPanel() {
        super();
        googleMap = new GoogleMap();
    }

    public void setScadaSites(ArrayList<ScadaSite> sites) {
        // remove all previous markers
        GoogleMapMarker[] markers = googleMap.getMarkers();
        for (GoogleMapMarker marker : markers) {
            googleMap.removeMarker(marker);
        }

        // then add the new ones
        markers = new GoogleMapMarker[sites.size()];
        for (int i = 0; i < markers.length; i++) {
            ScadaSite site = sites.get(i);

            String color;

            boolean firstAlarm = true;
            if (site.isCritical()) {
                color = "red";
                playSound();
            } else if (site.isWarning()) {
                color = "orange";
            } else {
                color = "green";
            }

            GoogleMapMarker marker = new GoogleMapMarker(googleMap, site.getLat(), site.getLon(), color);
            googleMap.addMarker(marker);
        }

        repaint();
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
}
