package model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by mifercre on 12/03/15.
 */
public class City implements Parcelable {

    private String name;
    private String placeId;
    private double lat;
    private double lng;
    private Marker marker;

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

    public City() {

    }

    public City(Parcel in) {
        readFromParcel(in);
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

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(placeId);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }

    private void readFromParcel(Parcel in) {
        name = in.readString();
        placeId = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
    }

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public City createFromParcel(Parcel in) {
                    return new City(in);
                }

                public City[] newArray(int size) {
                    return new City[size];
                }
            };
}
