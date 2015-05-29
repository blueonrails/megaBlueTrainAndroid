package com.shipeer.app;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

import model.City;

/**
 * Created by mifercre on 10/03/15.
 */
public class PublishTripActivity extends FragmentActivity implements View.OnClickListener {

    private TripFromToFragment tripFromToFragment;

    private TextView actionBarTitleTextView;
    private ImageView backImageView;

    private static City cityFrom;
    private static City cityTo;

    private static Date goDate;
    private static Date backDate;

    private static boolean[] weekGoDays;
    private static boolean[] weekBackDays;

    private static String time;
    private static int smallPrice;
    private static int mediumPrice;
    private static int bigPrice;
    private static int extraBigPrice;
    private static String comment;

    private static boolean isGoAndBackTrip = true;
    private static boolean isRecurrentTrip = false;
    private static int tripType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_trip);

        actionBarTitleTextView = (TextView) findViewById(R.id.second_action_bar_title_textview);
        actionBarTitleTextView.setText(getString(R.string.publish_trip));

        backImageView = (ImageView) findViewById(R.id.drawer_indicator);
        backImageView.setOnClickListener(this);

        tripFromToFragment = new TripFromToFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.publish_fragment_container, tripFromToFragment)
                .commit();
    }

    public static int getExtraBigPrice() {
        return extraBigPrice;
    }

    public static void setExtraBigPrice(int extraBigPrice) {
        PublishTripActivity.extraBigPrice = extraBigPrice;
    }

    public static int getTripType() {
        return tripType;
    }

    public static void setTripType(int tripType) {
        PublishTripActivity.tripType = tripType;
    }

    /**public static String getTime() {
        return time;
    }

    public static void setTime(String time) {
        PublishTripActivity.time = time;
    }
**/
    public static City getCityFrom() {
        return cityFrom;
    }

    public static void setCityFrom(City cityFrom) {
        PublishTripActivity.cityFrom = cityFrom;
    }

    public static String getComment() {
        return comment;
    }

    public static void setComment(String comment) {
        PublishTripActivity.comment = comment;
    }

    public static int getBigPrice() {
        return bigPrice;
    }

    public static void setBigPrice(int bigPrice) {
        PublishTripActivity.bigPrice = bigPrice;
    }

    public static int getMediumPrice() {
        return mediumPrice;
    }

    public static void setMediumPrice(int mediumPrice) {
        PublishTripActivity.mediumPrice = mediumPrice;
    }

    public static int getSmallPrice() {
        return smallPrice;
    }

    public static void setSmallPrice(int smallPrice) {
        PublishTripActivity.smallPrice = smallPrice;
    }

    public static City getCityTo() {
        return cityTo;
    }

    public static void setCityTo(City cityTo) {
        PublishTripActivity.cityTo = cityTo;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.drawer_indicator:
                this.onBackPressed();
                break;
        }
    }

    public static boolean isRecurrentTrip() {
        return isRecurrentTrip;
    }

    public static void setIsRecurrentTrip(boolean isRecurrentTrip) {
        PublishTripActivity.isRecurrentTrip = isRecurrentTrip;
    }

    public static boolean isGoAndBackTrip() {
        return isGoAndBackTrip;
    }

    public static void setIsGoAndBackTrip(boolean isGoAndBackTrip) {
        PublishTripActivity.isGoAndBackTrip = isGoAndBackTrip;
    }

    public static Date getGoDate() {
        return goDate;
    }

    public static void setGoDate(Date goDate) {
        PublishTripActivity.goDate = goDate;
    }

    public static Date getBackDate() {
        return backDate;
    }

    public static void setBackDate(Date backDate) {
        PublishTripActivity.backDate = backDate;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static void setWeekGoDays(boolean[] goDays) {
        PublishTripActivity.weekGoDays = goDays;
    }

    public static void setWeekBackDays(boolean[] backDays) {
        PublishTripActivity.weekBackDays = backDays;
    }

    public static boolean[] getWeekGoDays() {
        return weekGoDays;
    }

    public static boolean[] getWeekBackDays() {
        return weekBackDays;
    }
}
