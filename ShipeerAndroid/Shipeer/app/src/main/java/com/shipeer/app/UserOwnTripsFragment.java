package com.shipeer.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.rey.material.widget.Button;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import async.interfaces.OnUserTripsTaskCompleted;
import async.UserTripsTask;
import model.MySimpleDateFormat;
import model.OwnTripsListAdapter;
import model.Trip;

/**
 * Created by mifercre on 30/03/15.
 */
public class UserOwnTripsFragment extends Fragment implements View.OnClickListener, OnUserTripsTaskCompleted, AdapterView.OnItemClickListener {

    private static final String VIEW_NAME = "User Own Trips Fragment";

    private ListView ownTripsListView;
    private ProgressBar ownTripsProgressBar;

    private TextView noTripsTextView;
    private Button publishFirstTripButton;

    private ArrayList<Trip> ownTripsFound;

    public UserOwnTripsFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendAnalyticsView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity.setCurrentFragmentShown(3);
        MainActivity.setActionBarTitle(getString(R.string.my_trips));
        View view = inflater.inflate(R.layout.fragment_user_own_trips, container, false);

        ownTripsListView = (ListView) view.findViewById(R.id.user_own_trips_listView);
        ownTripsListView.setDividerHeight(0);
        ownTripsListView.setOnItemClickListener(this);

        ownTripsProgressBar = (ProgressBar) view.findViewById(R.id.user_own_trips_progressBar);

        noTripsTextView = (TextView) view.findViewById(R.id.no_own_trips_textView);
        publishFirstTripButton = (Button) view.findViewById(R.id.publish_first_trip_button);
        publishFirstTripButton.setOnClickListener(this);

        if(MainActivity.getUserOwnTripsResults() != null) {
            Log.d("SAVED INSTANCE STATE", "RESTORING USER TRIPS");
            // Restore last state for checked position.
            ownTripsFound = MainActivity.getUserOwnTripsResults();
            if(ownTripsFound != null) {
                OwnTripsListAdapter adapter = new OwnTripsListAdapter(this.getActivity(), ownTripsFound);
                ownTripsListView.setAdapter(adapter);
            }
        }
        else {
            if(ownTripsListView.getAdapter() == null) {
                String baseUserId = GlobalState.getBaseUserId();
                if(baseUserId != null && !baseUserId.isEmpty()) {

                    UserTripsTask userTripsTask = new UserTripsTask(this);
                    userTripsTask.execute(baseUserId);

                    ownTripsProgressBar.setVisibility(View.VISIBLE);
                    ownTripsProgressBar.setProgress(50);
                } else {
                    getFragmentManager().popBackStack();
                }
            }
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.setCurrentFragmentShown(3);
        MainActivity.setActionBarTitle(getString(R.string.my_trips));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.publish_first_trip_button:
                Intent publishTripIntent = new Intent(this.getActivity(), PublishTripActivity.class);
                startActivity(publishTripIntent);
                this.getActivity().overridePendingTransition(R.anim.slide_in_right_to_left, R.anim.zoom_out);
                break;
        }
    }

    @Override
    public void onUserTripsTaskCompleted(ArrayList<Trip> result) {
        ownTripsProgressBar.setVisibility(View.GONE);
        if(result != null && !result.isEmpty()) {
            noTripsTextView.setVisibility(View.GONE);
            publishFirstTripButton.setVisibility(View.GONE);

            ownTripsFound = result;
            //MainActivity.setUserOwnTripsResults(result);
            OwnTripsListAdapter adapter = new OwnTripsListAdapter(this.getActivity(), result);
            ownTripsListView.setAdapter(adapter);
        }
        else {
            noTripsTextView.setVisibility(View.VISIBLE);
            publishFirstTripButton.setVisibility(View.VISIBLE);
        }
    }

    public void sendAnalyticsView() {
        // Get tracker.
        Tracker t = ((GlobalState) getActivity().getApplication()).getTracker(
                GlobalState.TrackerName.APP_TRACKER);

        // Set screen name.
        // Where path is a String representing the screen name.
        t.setScreenName(VIEW_NAME);

        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        Trip tripClicked = ownTripsFound.get(position);

        Calendar cal = Calendar.getInstance();

        Date androidDate = null;
        if(tripClicked.getType() == 0) androidDate = MySimpleDateFormat.parseAndroidDate(tripClicked.getDepartureDateAndroid());
        else androidDate = MySimpleDateFormat.parseAndroidDate(tripClicked.getReturnDateAndroid());

        if(androidDate != null && androidDate.compareTo(cal.getTime()) > 0) {
            bundle.putString("tripId", tripClicked.getTripId());

            int tripType = tripClicked.getType();
            bundle.putInt("tripType", tripType);

            if(tripType == 0 || tripType == 2) { //GO TRIP
                bundle.putString("tripDepartureDatetime", tripClicked.getDepartureDateAndroid());
            }
            else if(tripType == 1 || tripType == 3) { //GO AND BACK TRIP
                bundle.putString("tripDepartureDatetime", tripClicked.getDepartureDateAndroid());
                bundle.putString("tripReturnDatetime", tripClicked.getReturnDateAndroid());
            }

            bundle.putString("cityFromName", tripClicked.getCityFrom().getName());
            bundle.putDouble("cityFromLat", tripClicked.getCityFrom().getLat());
            bundle.putDouble("cityFromLng", tripClicked.getCityFrom().getLng());

            bundle.putString("cityToName", tripClicked.getCityTo().getName());
            bundle.putDouble("cityToLat", tripClicked.getCityTo().getLat());
            bundle.putDouble("cityToLng", tripClicked.getCityTo().getLng());

            bundle.putString("tripPriceSmall", tripClicked.getPriceSmall());
            bundle.putString("tripPriceMedium", tripClicked.getPriceMedium());
            bundle.putString("tripPriceBig", tripClicked.getPriceBig());
            bundle.putString("tripPriceExtraBig", tripClicked.getPriceExtraBig());

            bundle.putString("tripDescription", tripClicked.getDescription());

            Intent intent = null;
            if(tripType == 0 || tripType == 1) {
                intent = new Intent(this.getActivity(), EditOwnSimpleTripActivity.class);
            } else {
                if(tripType == 2) bundle.putBooleanArray("tripRecurrentGoDays", tripClicked.getRecurrentGoDays());
                else if(tripType == 3) {
                    bundle.putBooleanArray("tripRecurrentGoDays", tripClicked.getRecurrentGoDays());
                    bundle.putBooleanArray("tripRecurrentBackDays", tripClicked.getRecurrentBackDays());
                }
                intent = new Intent(this.getActivity(), EditOwnRecurrentTripActivity.class);
            }
            intent.putExtras(bundle);
            startActivityForResult(intent, 2);
            this.getActivity().overridePendingTransition(R.anim.slide_in_right_to_left, R.anim.zoom_out);
        }
        else {
            Toast.makeText(this.getActivity(), getString(R.string.past_trip), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ON ACTIVITY RESULT", "ON ACTIVITY RESULT");

        String baseUserId = GlobalState.getBaseUserId();
        if(baseUserId != null && !baseUserId.isEmpty()) {

            UserTripsTask userTripsTask = new UserTripsTask(this);
            userTripsTask.execute(baseUserId);

            ownTripsProgressBar.setVisibility(View.VISIBLE);
            ownTripsProgressBar.setProgress(50);
        } else {
            getFragmentManager().popBackStack();
        }
    }
}
