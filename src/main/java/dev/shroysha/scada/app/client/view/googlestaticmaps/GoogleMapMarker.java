package dev.shroysha.scada.app.client.view.googlestaticmaps;


public class GoogleMapMarker {

    private final GoogleMap map;
    private double longitude, latitude;
    private String color;

    public GoogleMapMarker(GoogleMap map, double latitude, double longitude, String color) {
        this.map = map;
        this.longitude = longitude;
        this.latitude = latitude;
        this.color = color;
    }

}
