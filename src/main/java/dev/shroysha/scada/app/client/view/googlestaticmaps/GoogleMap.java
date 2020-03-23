package dev.shroysha.scada.app.client.view.googlestaticmaps;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GoogleMap {

    private final Stack<GoogleMapMarker> markers = new Stack<>();
    private final Stack<GoogleMapUpdateListener> updateListeners = new Stack<>();
    private int width = 480, height = 480;
    private boolean imageNeedsUpdate = true;
    private Image mapImage;

    public GoogleMap() {
        super();
    }

    public static void main(String[] args) {
        final GoogleMap map = new GoogleMap();
        GoogleMapMarker marker = new GoogleMapMarker(map, 62.107733, -145.5419, "blue");
        GoogleMapMarker marker2 = new GoogleMapMarker(map, 63.259591, -144.667969, "red");

        map.addMarker(marker);
        map.addMarker(marker2);
        System.out.println(map.generateWebsite());
        JFrame frame = new JFrame();
        ImageIcon icon = new ImageIcon(map.getImage());
        final JLabel label = new JLabel(icon);
        frame.add(label);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        map.updateListeners.add(
                () -> {
                    System.out.println("okay");
                    label.setIcon(new ImageIcon(map.getImage()));
                });
        Scanner scanner = new Scanner(System.in);
        //noinspection InfiniteLoopStatement
        while (true) {

            String line = scanner.nextLine();
            String[] cut = line.split(" ");
            double latitude = Double.parseDouble(cut[0]);
            double longitude = Double.parseDouble(cut[1]);
            String color = cut[2];
            GoogleMapMarker marker3 = new GoogleMapMarker(map, latitude, longitude, color);
            map.addMarker(marker3);
        }
    }

    public void addMarker(GoogleMapMarker marker) {
        markers.add(marker);
        notifyMapListeners();
    }

    public void removeMarker(GoogleMapMarker marker) {
        markers.remove(marker);
        notifyMapListeners();
    }

    public GoogleMapMarker[] getMarkers() {
        GoogleMapMarker[] marks = new GoogleMapMarker[markers.size()];

        for (int i = 0; i < marks.length; i++) {
            marks[i] = markers.get(i);
        }

        return marks;
    }

    private void notifyMapListeners() {
        imageNeedsUpdate = true;
        for (GoogleMapUpdateListener listener : updateListeners) {
            listener.onUpdate();
        }
    }

    private String generateWebsite() {
        String website = "";
        String BASE = "http://maps.googleapis.com/maps/api/staticmap?";
        website += BASE;

        website += "size=" + width + "x" + height;

        StringBuilder markerText = new StringBuilder();
        for (GoogleMapMarker marker : markers) {
            markerText.append("&markers=").append(marker.toString());
        }

        website += markerText;

        website += "&sensor=false";

        return website;
    }


    public String toString() {
        return generateWebsite();
    }


    public Image getImage() {
        try {
            if (imageNeedsUpdate) {
                mapImage = ImageIO.read(new URL(generateWebsite()));
                imageNeedsUpdate = false;
            }

            return mapImage;
        } catch (IOException ex) {
            Logger.getLogger(GoogleMap.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
