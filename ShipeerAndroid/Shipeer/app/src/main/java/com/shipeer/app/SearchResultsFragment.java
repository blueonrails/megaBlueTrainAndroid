package com.shipeer.app;

import android.content.Context;
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

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import async.interfaces.OnTripSearchTaskCompleted;
import async.TripSearchTask;
import model.SearchResultsListAdapter;
import model.Trip;

/**
 * Created by mifercre on 30/03/15.
 */
public class SearchResultsFragment extends Fragment implements OnTripSearchTaskCompleted, AdapterView.OnItemClickListener {

    private static final String VIEW_NAME = "Search Trip Result List";

    private String cityFromName;
    private String cityToName;

    private ListView searchResultsListView;
    private TextView noResultsTextView;
    private ProgressBar searchResultsProgressBar;

    private ArrayList<Trip> tripsFound;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity.setCurrentFragmentShown(MainActivity.SEARCH_RESULTS);

        sendAnalyticsView();
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);

        cityFromName = getArguments().getString("cityFrom");
        cityToName = getArguments().getString("cityTo");

        searchResultsListView = (ListView) view.findViewById(R.id.search_results_listView);
        searchResultsListView.setDividerHeight(0);
        searchResultsListView.setOnItemClickListener(this);

        noResultsTextView = (TextView) view.findViewById(R.id.no_results_textView);
        searchResultsProgressBar = (ProgressBar) view.findViewById(R.id.search_results_progressBar);

        if(MainActivity.getSearchResults() != null) {
            Log.d("SAVED INSTANCE STATE", "RESTORING RESULTS FROM PREVIOUS SEARCH");
            // Restore last state for checked position.
            tripsFound = MainActivity.getSearchResults();
            if(tripsFound != null) {
                SearchResultsListAdapter adapter = new SearchResultsListAdapter(this.getActivity(), tripsFound);
                searchResultsListView.setAdapter(adapter);
            }
        }
        else {
            if(searchResultsListView.getAdapter() == null) {
                if(cityFromName != null && !cityFromName.isEmpty() && cityToName != null && !cityToName.isEmpty()) {

                    String[] form = {cityFromName, cityToName};
                    TripSearchTask tripSearchTask = new TripSearchTask(this);
                    tripSearchTask.execute(form);

                    searchResultsProgressBar.setVisibility(View.VISIBLE);
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
        MainActivity.setCurrentFragmentShown(MainActivity.SEARCH_RESULTS);
    }

    @Override
    public void onTripSearchTaskCompleted(ArrayList<Trip> result) {
        searchResultsProgressBar.setVisibility(View.GONE);
        if(result != null && !result.isEmpty()) {
            noResultsTextView.setVisibility(View.GONE);

            tripsFound = result;
            MainActivity.setSearchResults(result);
            SearchResultsListAdapter adapter = new SearchResultsListAdapter(this.getActivity(), result);
            searchResultsListView.setAdapter(adapter);
        }
        else {
            noResultsTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MainActivity.setSearchResults(tripsFound);
        MainActivity.setTripClicked(tripsFound.get(position));

        Bundle bundle = new Bundle();
        Trip tripClicked = tripsFound.get(position);

        bundle.putString("tripId", tripClicked.getTripId());
        bundle.putString("tripDepartureDatetime", tripClicked.getDepartureDateAndroid());
        bundle.putString("tripReturnDatetime", tripClicked.getReturnDateAndroid());

        bundle.putString("tripCarrierName", tripClicked.getCarrierName());
        bundle.putString("tripCarrierEmail", tripClicked.getCarrierEmail());
        bundle.putString("tripCarrierPhone", tripClicked.getCarrierPhone());
        bundle.putString("tripCarrierProfilePicId", tripClicked.getCarrierPictureId());
        bundle.putString("tripCarrierProfilePicVersion", tripClicked.getCarrierPictureVersion());
        bundle.putBoolean("tripCarrierHasFacebook", tripClicked.getCarrierHasFacebook());

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

        SearchTripDetailsFragment searchTripDetailsFragment = new SearchTripDetailsFragment();
        searchTripDetailsFragment.setArguments(bundle);

        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right_to_left, R.anim.slide_out_left, R.anim.slide_in_left_to_right, R.anim.slide_out_right)
                .replace(R.id.main_fragment_container_frameLayout, searchTripDetailsFragment)
                .addToBackStack("searchTripDetailsFragment")
                .commit();
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
}
