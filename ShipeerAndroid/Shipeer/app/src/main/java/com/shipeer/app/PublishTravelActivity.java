package com.shipeer.app;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Date;

import model.City;

/**
 * Created by mifercre on 10/03/15.
 */
public class PublishTravelActivity extends FragmentActivity {

    private TravelFromToFragment travelFromToFragment;

    private static City cityFrom;
    private static City cityTo;
    private static ArrayList<Date> dates;
    private static int smallPrice;
    private static int mediumPrice;
    private static int bigPrice;
    private static String comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        travelFromToFragment = new TravelFromToFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(android.R.id.content, travelFromToFragment)
                .commit();
    }

    public static City getCityFrom() {
        return cityFrom;
    }

    public static void setCityFrom(City cityFrom) {
        PublishTravelActivity.cityFrom = cityFrom;
    }

    public static String getComment() {
        return comment;
    }

    public static void setComment(String comment) {
        PublishTravelActivity.comment = comment;
    }

    public static int getBigPrice() {
        return bigPrice;
    }

    public static void setBigPrice(int bigPrice) {
        PublishTravelActivity.bigPrice = bigPrice;
    }

    public static int getMediumPrice() {
        return mediumPrice;
    }

    public static void setMediumPrice(int mediumPrice) {
        PublishTravelActivity.mediumPrice = mediumPrice;
    }

    public static int getSmallPrice() {
        return smallPrice;
    }

    public static void setSmallPrice(int smallPrice) {
        PublishTravelActivity.smallPrice = smallPrice;
    }

    public static ArrayList<Date> getDates() {
        return dates;
    }

    public static void setDates(ArrayList<Date> dates) {
        PublishTravelActivity.dates = dates;
    }

    public static City getCityTo() {
        return cityTo;
    }

    public static void setCityTo(City cityTo) {
        PublishTravelActivity.cityTo = cityTo;
    }
}
