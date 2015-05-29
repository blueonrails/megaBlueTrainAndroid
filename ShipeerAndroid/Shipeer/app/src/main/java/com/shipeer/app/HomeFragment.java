package com.shipeer.app;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import async.interfaces.OnPlacesTaskCompleted;
import async.interfaces.OnVersionCheckTaskCompleted;
import async.PlacesTask;
import async.VersionCheckTask;
import model.City;

public class HomeFragment extends Fragment implements View.OnClickListener, OnPlacesTaskCompleted, OnVersionCheckTaskCompleted {

    //Google Analytics stuff
    private static final String VIEW_NAME = "Home Fragment";
    //Google Maps stuff
    private static ArrayList<City> cities = null;

    private AutoCompleteTextView autoCompViewFrom;
    private AutoCompleteTextView autoCompViewTo;
    private Button searchButton;

    //View variables
    private LinearLayout flipView;
    private ImageView flipArrows;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendAnalyticsView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity.setCurrentFragmentShown(MainActivity.HOME_FRAGMENT);
        MainActivity.setActionBarTitle(getString(R.string.search_trunk));


        View view = inflater.inflate(R.layout.home_view, container, false);

        MainActivity.setSearchResults(null);
        MainActivity.setTripClicked(null);

        flipView = (LinearLayout) view.findViewById(R.id.flip_view);
        flipView.setOnClickListener(this);

        flipArrows = (ImageView) view.findViewById(R.id.flip_arrows_imageView);

        autoCompViewFrom = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextViewFrom);
        autoCompViewFrom.setAdapter(new PlacesAutoCompleteAdapter(this.getActivity(), R.layout.maps_citie));

        autoCompViewFrom.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(checkInternetConnection()) {
                    PlacesTask placesTask = new PlacesTask(HomeFragment.this);
                    placesTask.execute(s.toString());
                }
                else {
                    showNoInternetConnectionError();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        autoCompViewTo = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextViewTo);
        autoCompViewTo.setAdapter(new PlacesAutoCompleteAdapter(this.getActivity(), R.layout.maps_citie));

        autoCompViewTo.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(checkInternetConnection()) {
                    PlacesTask placesTask = new PlacesTask(HomeFragment.this);
                    placesTask.execute(s.toString());
                }
                else {
                    showNoInternetConnectionError();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        searchButton = (Button) view.findViewById(R.id.search_button);
        searchButton.setOnClickListener(this);

        checkAppVersionInstalled();
        return view;
    }

    private void checkAppVersionInstalled() {
        if(checkInternetConnection()) {
            Long lastDate = GlobalState.getLastDateVersionChecked();
            Long millisNow = System.currentTimeMillis();

            if(millisNow - lastDate > 259200000) { //259200000 millis = 3 days
                GlobalState.saveLastDateVersionChecked(millisNow);
                VersionCheckTask versionCheckTask = new VersionCheckTask(this);
                versionCheckTask.execute();
            }
        }
        else {
            showNoInternetConnectionError();
        }
    }

    /**@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MainActivity.setCurrentFragmentShown(MainActivity.HOME_FRAGMENT);
        MainActivity.setActionBarTitle(getString(R.string.search_trunk));
    }**/

    @Override
    public void onPause() {
        super.onPause();
        //MainActivity.setCurrentFragmentShown(MainActivity.HOME_FRAGMENT);
        //MainActivity.setActionBarTitle(getString(R.string.search_trunk));
    }

    @Override
    public void onVersionCheckTaskCompleted(Integer result) {
        if(result != null) {
            int versionCode = BuildConfig.VERSION_CODE;
            Log.d("VERSION", "INSTALLED=" + versionCode + ", NEWEST=" + result.intValue());
            if(result.intValue() > versionCode) {
                //SEND TO GOOGLE PLAY
                new MaterialDialog.Builder(this.getActivity())
                        .title(R.string.upgrade_app)
                        .content(R.string.upgrade_app_content)
                        .positiveText(R.string.download)
                        .negativeText(R.string.cancel)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flip_view:
                animateFlipView();
                flipFromToSearch();
                break;
            case R.id.search_button:
                searchTrips();
                break;
        }
    }

    private void animateFlipView() {
        RotateAnimation rotate= (RotateAnimation) AnimationUtils.loadAnimation(this.getActivity(), R.anim.rotate_180);
        flipArrows.startAnimation(rotate);//setAnimation(rotate);
    }

    private void searchTrips() {
        String cityFrom = autoCompViewFrom.getText().toString();
        String cityTo = autoCompViewTo.getText().toString();

        if(cityFrom != null && !cityFrom.isEmpty() && cityTo != null && !cityTo.isEmpty()) {
            sendAnalyticsSearch(cityFrom, cityTo);

            // SEARCH FOR TRIPS USING API
            Bundle bundle = new Bundle();
            bundle.putString("cityFrom", cityFrom);
            bundle.putString("cityTo", cityTo);

            SearchResultsFragment searchResultsFragment = new SearchResultsFragment();
            searchResultsFragment.setArguments(bundle);

            getFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container_frameLayout, searchResultsFragment)
                    .addToBackStack("searchResultsFragment")
                    .commit();
        } else {
            Toast.makeText(getActivity(), getString(R.string.missing_info), Toast.LENGTH_SHORT).show();
        }
    }

    private void flipFromToSearch() {
        String aux = autoCompViewFrom.getText().toString();
        autoCompViewFrom.setFocusable(false);
        autoCompViewTo.setFocusable(false);

        autoCompViewFrom.setEnabled(false);
        autoCompViewTo.setEnabled(false);

        autoCompViewFrom.setText(autoCompViewTo.getText().toString() + "");
        autoCompViewTo.setText(aux + "");

        autoCompViewFrom.setFocusable(true);
        autoCompViewTo.setFocusable(true);

        autoCompViewFrom.setFocusableInTouchMode(true);
        autoCompViewTo.setFocusableInTouchMode(true);

        autoCompViewFrom.setEnabled(true);
        autoCompViewTo.setEnabled(true);
    }

    @Override
    public void onPlacesTaskCompleted(ArrayList<City> result) {
        cities = result;
    }

    /**
     * List adapter to show cities from Google maps.
     */
    private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<City> resultList;

        public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            if (resultList != null) {
                return resultList.size();
            } else {
                resultList = new ArrayList<>();
                return 0;
            }
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index).getName();
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    ArrayList<City> queryResults;
                    //if (constraint != null || constraint.length() == 0) {
                    if (constraint != null && constraint.length() != 0) {
                        queryResults = PlacesTask.autocomplete(constraint.toString());
                    } else {
                        queryResults = new ArrayList<City>(); // empty list/no suggestions showing if there's no valid constraint
                    }
                    filterResults.values = queryResults;
                    filterResults.count = queryResults.size();

                    /**FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = PlacesTask.autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }**/
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    resultList = (ArrayList<City>) results.values;
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }

                    /**if (results != null && results.count > 0) {
                        HomeFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                notifyDataSetChanged();
                            }
                        });
                        notifyDataSetChanged();
                    }
                    else {
                        notifyDataSetInvalidated();
                    }**/
                }};
            return filter;
        }
    }

    public boolean checkInternetConnection() {
        ConnectivityManager conMgr = (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null)
            return false;
        if (!netInfo.isConnected())
            return false;
        if (!netInfo.isAvailable())
            return false;
        return true;
    }

    private void showNoInternetConnectionError() {
        Toast.makeText(this.getActivity(), getString(R.string.no_internet_error), Toast.LENGTH_SHORT).show();
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

    public void sendAnalyticsSearch(String from, String to) {
        // Get tracker.
        Tracker t = ((GlobalState) getActivity().getApplication()).getTracker(
                GlobalState.TrackerName.APP_TRACKER);
        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder()
                .setCategory(VIEW_NAME)
                .setAction(getString(R.string.search))
                .setLabel("From: " + from + ", To: " + to)
                .build());
    }
}

