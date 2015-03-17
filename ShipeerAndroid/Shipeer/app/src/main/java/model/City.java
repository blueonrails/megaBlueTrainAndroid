package model;

/**
 * Created by mifercre on 12/03/15.
 */
public class City {

    private String name;
    private String placeId;
    private double lat;
    private double lng;

    public City(String name, String placeId) {
        this.name = name;
        this.placeId = placeId;
    }

    public City(String name, String placeId, double lat, double lng) {
        this.name = name;
        this.placeId = placeId;
        this.lat = lat;
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
