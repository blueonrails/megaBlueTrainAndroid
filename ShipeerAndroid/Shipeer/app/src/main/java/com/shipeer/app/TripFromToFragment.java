package com.shipeer.app;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.RadioButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import async.interfaces.OnPlacesDetailTaskCompleted;
import async.interfaces.OnPlacesTaskCompleted;
import async.interfaces.OnRouteFromToTaskCompleted;
import async.PlacesDetailTask;
import async.PlacesTask;
import async.RouteFromToTask;
import model.City;

public class TripFromToFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, OnRouteFromToTaskCompleted, OnPlacesDetailTaskCompleted, OnPlacesTaskCompleted, CompoundButton.OnCheckedChangeListener {

    //Google Analytics stuff
    private static final String VIEW_NAME = "Publish Trip From To (1/3)";

    private static final String TAG = "Trip From To Fragment";
    private static View view;

    private AutoCompleteTextView autoCompViewFrom;
    private AutoCompleteTextView autoCompViewTo;

    private LinearLayout flipView;
    private ImageView flipArrows;

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    private Marker markerFrom;
    private Marker markerTo;
    private Polyline pathFromTo;

    private ArrayList<City> cities = null;
    private City cityFrom;
    private City cityTo;

    private boolean cityFromToAlreadySet;

    private ScrollView fromToScrollView;
    private CheckBox simpleGoAndBackCheckBox;
    private CheckBox recurrentGoAndBackCheckBox;

    private TextView publishNextTextView;

    private RadioButton simpleTripRadioButton;
    private RadioButton recurrentTripRadioButton;

    //private MapView mapView;

    public static TripFromToFragment newInstance() {
        TripFromToFragment fragment = new TripFromToFragment();
        return fragment;
    }

    public TripFromToFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sendAnalyticsView();

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_trip_from_to, container, false);
        } catch (InflateException e) {
            e.printStackTrace();
        }

        //View view = inflater.inflate(R.layout.fragment_trip_from_to, null);

        fromToScrollView = (ScrollView) view.findViewById(R.id.from_to_scrollView);

        flipView = (LinearLayout) view.findViewById(R.id.flip_view);
        flipView.setOnClickListener(this);

        flipArrows = (ImageView) view.findViewById(R.id.flip_arrows_imageView);

        FragmentManager fm = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_publish_trip);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.getUiSettings().setAllGesturesEnabled(false);
            }
        });

        simpleGoAndBackCheckBox = (CheckBox) view.findViewById(R.id.simple_go_and_back_checkBox);
        simpleGoAndBackCheckBox.setOnCheckedChangeListener(this);

        recurrentGoAndBackCheckBox = (CheckBox) view.findViewById(R.id.recurrent_go_and_back_checkBox);
        recurrentGoAndBackCheckBox.setOnCheckedChangeListener(this);

        publishNextTextView = (TextView) view.findViewById(R.id.publishNext);
        publishNextTextView.setOnClickListener(this);
        //publishNextTextView.setEnabled(false);
        publishNextTextView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

        autoCompViewFrom = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextViewFrom);
        PlacesAutoCompleteAdapter adapterFrom = new PlacesAutoCompleteAdapter(this.getActivity(), R.layout.maps_citie);
        adapterFrom.setType(PlacesAutoCompleteAdapter.TYPE_FROM);
        autoCompViewFrom.setAdapter(adapterFrom);
        autoCompViewFrom.setOnItemClickListener(this);
        autoCompViewFrom.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!cityFromToAlreadySet) {
                    Log.d(TAG, "DISABLING NEXT BUTTON");
                    publishNextTextView.setEnabled(false);
                    publishNextTextView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                }

                if (checkInternetConnection()) {
                    PlacesTask placesTask = new PlacesTask(TripFromToFragment.this);
                    placesTask.execute(s.toString());
                } else {
                    showNoInternetConnectionError();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        autoCompViewTo = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextViewTo);
        PlacesAutoCompleteAdapter adapterTo = new PlacesAutoCompleteAdapter(this.getActivity(), R.layout.maps_citie);
        adapterTo.setType(PlacesAutoCompleteAdapter.TYPE_TO);
        autoCompViewTo.setAdapter(adapterTo);
        autoCompViewTo.setOnItemClickListener(this);
        autoCompViewTo.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!cityFromToAlreadySet) {
                    Log.d(TAG, "DISABLING NEXT BUTTON");
                    publishNextTextView.setEnabled(false);
                    publishNextTextView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                }

                if (checkInternetConnection()) {
                    PlacesTask placesTask = new PlacesTask(TripFromToFragment.this);
                    placesTask.execute(s.toString());
                } else {
                    showNoInternetConnectionError();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        City cityAuxFrom = PublishTripActivity.getCityFrom();
        if (cityAuxFrom != null && cityAuxFrom.getMarker() != null) {
            cityFrom = cityAuxFrom;
            //upgradeLocation(PlacesDetailTask.TYPE_FROM);
        }

        City cityAuxTo = PublishTripActivity.getCityTo();
        if (cityAuxTo != null && cityAuxTo.getMarker() != null) {
            cityTo = cityAuxTo;
            //upgradeLocation(PlacesDetailTask.TYPE_TO);
        }

        Log.d("ON CREATE VIEW", "ON");
        simpleTripRadioButton = (RadioButton) view.findViewById(R.id.simple_trip_radioButton);
        recurrentTripRadioButton = (RadioButton) view.findViewById(R.id.recurrent_trip_radioButton);

        if (PublishTripActivity.isRecurrentTrip()) {
            simpleGoAndBackCheckBox.setVisibility(View.GONE);
            simpleTripRadioButton.setCheckedImmediately(false);
            recurrentTripRadioButton.setCheckedImmediately(true);
            if (PublishTripActivity.isGoAndBackTrip()) {
                recurrentGoAndBackCheckBox.setCheckedImmediately(true);
            } else {
                recurrentGoAndBackCheckBox.setCheckedImmediately(false);
            }
        } else {
            recurrentGoAndBackCheckBox.setVisibility(View.GONE);
            recurrentTripRadioButton.setCheckedImmediately(false);
            simpleGoAndBackCheckBox.setCheckedImmediately(true);
            if (PublishTripActivity.isGoAndBackTrip()) {
                simpleGoAndBackCheckBox.setCheckedImmediately(true);
            } else {
                simpleGoAndBackCheckBox.setCheckedImmediately(false);
            }
        }

        if (cityAuxFrom != null && cityAuxFrom.getMarker() != null && cityAuxTo != null && cityAuxTo.getMarker() != null) {
            cityFromToAlreadySet = true;
            publishNextTextView.setEnabled(true);
            publishNextTextView.setBackgroundColor(getResources().getColor(R.color.shipeer_kazan));
        }

        simpleTripRadioButton.setOnCheckedChangeListener(this);
        recurrentTripRadioButton.setOnCheckedChangeListener(this);

        return view;
    }

    @Override
    public void onPlacesTaskCompleted(ArrayList<City> result) {
        cities = result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.publishNext:
                Log.d("go next", "now starting");
                if (cityFrom != null && cityTo != null) {
                    PublishTripActivity.setCityFrom(cityFrom);
                    PublishTripActivity.setCityTo(cityTo);

                    if (recurrentTripRadioButton.isChecked()) {
                        PublishTripActivity.setIsRecurrentTrip(true);
                        if (recurrentGoAndBackCheckBox.isChecked()) {
                            PublishTripActivity.setTripType(3);
                            PublishTripActivity.setIsGoAndBackTrip(true);
                        }
                        else {
                            PublishTripActivity.setTripType(2);
                            PublishTripActivity.setIsGoAndBackTrip(false);
                        }

                        TripRecurrentDateFragment tripRecurrentDateFragment = new TripRecurrentDateFragment();
                        getFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right_to_left, R.anim.slide_out_left, R.anim.slide_in_left_to_right, R.anim.slide_out_right)
                                .replace(R.id.publish_fragment_container, tripRecurrentDateFragment)
                                .addToBackStack(null)
                                .commit();
                    } else { //SIMPLE TRIP
                        PublishTripActivity.setIsRecurrentTrip(false);
                        if (simpleGoAndBackCheckBox.isChecked()) {
                            PublishTripActivity.setTripType(1);
                            PublishTripActivity.setIsGoAndBackTrip(true);
                        }
                        else {
                            PublishTripActivity.setTripType(0);
                            PublishTripActivity.setIsGoAndBackTrip(false);
                        }

                        TripDateFragment tripDateFragment = new TripDateFragment();
                        getFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right_to_left, R.anim.slide_out_left, R.anim.slide_in_left_to_right, R.anim.slide_out_right)
                                .replace(R.id.publish_fragment_container, tripDateFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                }
                break;
            case R.id.flip_view:
                animateFlipView();
                flipFromTo();
                break;
        }
    }

    private void animateFlipView() {
        RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(this.getActivity(), R.anim.rotate_180);
        flipArrows.startAnimation(rotate);//setAnimation(rotate);
    }

    private void flipFromTo() {
        if (!autoCompViewFrom.getText().toString().isEmpty() && !autoCompViewTo.getText().toString().isEmpty()) {
            City cityAux = cityFrom;
            cityFrom = cityTo;
            cityTo = cityAux;

            Marker markerAux = markerFrom;
            markerFrom = markerTo;
            markerTo = markerAux;

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

            if (cityFrom != null && cityFrom.getLat() != 0 && cityTo != null && cityTo.getLat() != 0) {
                publishNextTextView.setEnabled(true);
                publishNextTextView.setBackgroundColor(getResources().getColor(R.color.shipeer_kazan));
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (cities != null && cities.size() > position && cities.get(position) != null) {
            Log.d("onItemClick", position + ", " + cities.get(position));
            PlacesAutoCompleteAdapter adapter = (PlacesAutoCompleteAdapter) parent.getAdapter();
            if (adapter != null && adapter.getType() != 0) {
                Log.d("onItemClick", adapter.getType() + "");
                PlacesDetailTask detailsTask = null;

                if (adapter.getType() == PlacesAutoCompleteAdapter.TYPE_FROM) {
                    if (cityFrom == null || !cityFrom.getName().equalsIgnoreCase(cities.get(position).getName())) {

                        Log.e("DetailsTask lauched", "Details - FROM - " + cities.get(position).getName());
                        if (cityFrom != null) Log.e("Current FROM", cityFrom.getName() + "");
                        else Log.e("Current FROM", "NULL");

                        cityFrom = new City(cities.get(position).getName(), cities.get(position).getPlaceId());
                        detailsTask = new PlacesDetailTask(this, cityFrom);
                    }
                } else if (adapter.getType() == PlacesAutoCompleteAdapter.TYPE_TO) {
                    if (cityTo == null || !cityTo.getName().equalsIgnoreCase(cities.get(position).getName())) {

                        Log.e("DetailsTask lauched", "Details - TO - " + cities.get(position).getName());
                        if (cityTo != null) Log.e("Current TO", cityTo.getName() + "");
                        else Log.e("Current TO", "NULL");

                        cityTo = new City(cities.get(position).getName(), cities.get(position).getPlaceId());
                        detailsTask = new PlacesDetailTask(this, cityTo);
                    }
                }

                if (detailsTask != null) {
                    detailsTask.setType(adapter.getType());
                    if (checkInternetConnection()) {
                        detailsTask.execute(cities.get(position).getPlaceId());
                    } else {
                        showNoInternetConnectionError();
                    }
                }
            }
        }
    }

    @Override
    public void onPlacesDetailTaskCompleted(int type, City city) {
        if (type == PlacesDetailTask.TYPE_FROM) {
            if (cityFrom != null) {
                cityFrom.setLat(city.getLat());
                cityFrom.setLng(city.getLng());
            }
        } else if (type == PlacesDetailTask.TYPE_TO) {
            if (cityTo != null) {
                cityTo.setLat(city.getLat());
                cityTo.setLng(city.getLng());
            }
        }
        upgradeLocation(type);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.simple_trip_radioButton:
                simpleTripRadioButton.setCheckedImmediately(isChecked);
                recurrentTripRadioButton.setCheckedImmediately(!isChecked);
                simpleGoAndBackCheckBox.setVisibility(View.VISIBLE);
                recurrentGoAndBackCheckBox.setVisibility(View.GONE);
                break;
            case R.id.recurrent_trip_radioButton:
                recurrentTripRadioButton.setCheckedImmediately(isChecked);
                simpleTripRadioButton.setCheckedImmediately(!isChecked);
                simpleGoAndBackCheckBox.setVisibility(View.GONE);
                recurrentGoAndBackCheckBox.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * List adapter to show cities from Google maps.
     */
    private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        public static final int TYPE_FROM = 1;
        public static final int TYPE_TO = 2;
        private int type;

        private ArrayList<City> resultList;

        public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
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
            if (index < resultList.size()) return resultList.get(index).getName();
            else return "";
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    ArrayList<City> queryResults;
                    if (constraint != null || constraint.length() == 0) {
                        queryResults = PlacesTask.autocomplete(constraint.toString());
                    } else {
                        queryResults = new ArrayList<City>(); // empty list/no suggestions showing if there's no valid constraint
                    }
                    filterResults.values = queryResults;
                    filterResults.count = queryResults.size();


                    /**if (constraint != null || constraint.length() == 0) {
                     // Retrieve the autocomplete results.
                     cities = PlacesTask.autocomplete(constraint.toString());

                     // Assign the data to the FilterResults
                     filterResults.values = cities;
                     filterResults.count = cities.size();
                     }**/
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    // update the data with the new set of suggestions
                    resultList = (ArrayList<City>) results.values;
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                    /**if (results != null && results.count > 0) {
                     TripFromToFragment.this.getActivity().runOnUiThread(new Runnable() {
                     public void run() {
                     notifyDataSetChanged();
                     }
                     });
                     }
                     else {
                     notifyDataSetInvalidated();
                     }**/
                }
            };
            return filter;
        }
    }

    private void upgradeLocation(int type) {
        if (mMap == null) {
            FragmentManager fm = getChildFragmentManager();
            mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_publish_trip);
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    mMap.getUiSettings().setAllGesturesEnabled(false);
                }
            });
        }
        //Log.d("New details", "lat " + latFrom + ", lng " + lngFrom);
        if (type == PlacesDetailTask.TYPE_FROM) {
            if (markerFrom != null) markerFrom.remove();
            markerFrom = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(cityFrom.getLat(), cityFrom.getLng()))
                    .title(cityFrom.getName()));
            cityFrom.setMarker(markerFrom);
        } else if (type == PlacesDetailTask.TYPE_TO) {
            if (markerTo != null) markerTo.remove();
            markerTo = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(cityTo.getLat(), cityTo.getLng()))
                    .title(cityTo.getName()));
            cityTo.setMarker(markerTo);
        }

        if (cityFrom != null && cityFrom.getLat() != 0 && cityTo != null && cityTo.getLat() != 0) {
            publishNextTextView.setEnabled(true);
            publishNextTextView.setBackgroundColor(getResources().getColor(R.color.shipeer_kazan));

            if (pathFromTo != null) pathFromTo.remove();

            if (checkInternetConnection()) {
                RouteFromToTask routeTask = new RouteFromToTask(this, cityFrom, cityTo);
                routeTask.execute();
            } else {
                showNoInternetConnectionError();
            }
        }
    }

    @Override
    public void onRouteFromToTaskCompleted(List<List<HashMap<String, String>>> routes) {
        ArrayList<LatLng> points = null;
        PolylineOptions polyLineOptions = null;

        // traversing through routes
        for (int i = 0; i < routes.size(); i++) {
            points = new ArrayList<LatLng>();
            polyLineOptions = new PolylineOptions();
            List<HashMap<String, String>> path = routes.get(i);

            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            polyLineOptions.addAll(points);
            polyLineOptions.width(5);
            polyLineOptions.color(Color.BLUE);
        }

        if (pathFromTo != null) pathFromTo.remove();
        pathFromTo = mMap.addPolyline(polyLineOptions);

        if (markerFrom != null && markerFrom.getPosition() != null && markerTo != null && markerTo.getPosition() != null) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(markerFrom.getPosition());
            builder.include(markerTo.getPosition());

            LatLngBounds bounds = builder.build();
            int padding = 50; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            mMap.animateCamera(cu);
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
}
