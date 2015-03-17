package com.shipeer.app;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import async.OnPlacesDetailTaskCompleted;
import async.OnPlacesTaskCompleted;
import async.OnRouteFromToTaskCompleted;
import async.PlacesDetailTask;
import async.PlacesTask;
import async.RouteFromToTask;
import model.City;

public class TravelFromToFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, OnRouteFromToTaskCompleted, OnPlacesDetailTaskCompleted, OnPlacesTaskCompleted {

    private AutoCompleteTextView autoCompViewFrom;
    private AutoCompleteTextView autoCompViewTo;

    private TextView actionBarTitleTextView;
    private ImageView backImageView;

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    private Marker markerFrom;
    private Marker markerTo;
    private Polyline pathFromTo;

    private ArrayList<City> cities = null;
    private City cityFrom;
    private City cityTo;

    private ScrollView fromToScrollView;
    private TextView publishNextTextView;

    public static TravelFromToFragment newInstance() {
        TravelFromToFragment fragment = new TravelFromToFragment();
        return fragment;
    }

    public TravelFromToFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_travel_from_to, container, false);

        fromToScrollView = (ScrollView) view.findViewById(R.id.from_to_scrollView);

        actionBarTitleTextView = (TextView) view.findViewById(R.id.second_action_bar_title_textview);
        actionBarTitleTextView.setText(getString(R.string.publish_travel));

        backImageView = (ImageView) view.findViewById(R.id.drawer_indicator);
        backImageView.setOnClickListener(this);

        //mMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map_publish)).getMap();
        //mMap = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map_publish)).getMap();

        FragmentManager fm = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_publish);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.getUiSettings().setAllGesturesEnabled(false);
            }
        });

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
                publishNextTextView.setEnabled(false);
                publishNextTextView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

                PlacesTask placesTask = new PlacesTask(TravelFromToFragment.this);
                placesTask.execute(s.toString());
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
                publishNextTextView.setEnabled(false);
                publishNextTextView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

                PlacesTask placesTask = new PlacesTask(TravelFromToFragment.this);
                placesTask.execute(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    @Override
    public void onPlacesTaskCompleted(ArrayList<City> result) {
        cities = result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.drawer_indicator:
                this.getActivity().onBackPressed();
                break;
            case R.id.publishNext:
                Log.d("go next", "now starting");
                //if(cityFrom != null && cityTo != null) {
                    TravelDateFragment travelDateFragment = new TravelDateFragment();
                    getFragmentManager().beginTransaction()
                        .replace(R.id.travel_from_to_fragment, travelDateFragment)
                        .addToBackStack(null)
                        .commit();
                //}
                break;
            case R.id.flip_view:
                String aux = autoCompViewFrom.getText().toString();
                autoCompViewFrom.setText(autoCompViewTo.getText().toString() + "");
                autoCompViewTo.setText(aux + "");
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("onItemClick", position + ", " + cities.get(position));
        PlacesAutoCompleteAdapter adapter = (PlacesAutoCompleteAdapter) parent.getAdapter();
        if(adapter != null && adapter.getType() != 0) {
            Log.d("onItemClick", adapter.getType() + "");
            PlacesDetailTask detailsTask = null;

            if(adapter.getType() == PlacesAutoCompleteAdapter.TYPE_FROM) {
                cityFrom = new City(cities.get(position).getName(), cities.get(position).getPlaceId());
                detailsTask = new PlacesDetailTask(this, cityFrom);
            } else if(adapter.getType() == PlacesAutoCompleteAdapter.TYPE_TO) {
                cityTo = new City(cities.get(position).getName(), cities.get(position).getPlaceId());
                detailsTask = new PlacesDetailTask(this, cityTo);
            }

            if(detailsTask != null) {
                detailsTask.setType(adapter.getType());
                detailsTask.execute(cities.get(position).getPlaceId());
            }
        }
    }

    @Override
    public void onPlacesDetailTaskCompleted(int type, City city) {
        if(type == PlacesDetailTask.TYPE_FROM) {
            if(cityFrom != null) {
                cityFrom.setLat(city.getLat());
                cityFrom.setLng(city.getLng());
            }
        } else if(type == PlacesDetailTask.TYPE_TO) {
            if(cityTo != null) {
                cityTo.setLat(city.getLat());
                cityTo.setLng(city.getLng());
            }
        }
        upgradeLocation(type);
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
            return cities.size();
        }

        @Override
        public String getItem(int index) {
            return cities.get(index).getName();
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        cities = PlacesTask.autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = cities;
                        filterResults.count = cities.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        TravelFromToFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                notifyDataSetChanged();
                            }
                        });
                    }
                    else {
                        notifyDataSetInvalidated();
                    }
                }};
            return filter;
        }
    }

    private void upgradeLocation(int type) {
        if(mMap == null) {
            FragmentManager fm = getChildFragmentManager();
            mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_publish);
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    mMap.getUiSettings().setAllGesturesEnabled(false);
                }
            });
        }
        //Log.d("New details", "lat " + latFrom + ", lng " + lngFrom);
        if(type == PlacesDetailTask.TYPE_FROM) {
            if(markerFrom != null) markerFrom.remove();
            markerFrom = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(cityFrom.getLat(), cityFrom.getLng()))
                    .title(cityFrom.getName()));
        } else if(type == PlacesDetailTask.TYPE_TO) {
            if(markerTo != null) markerTo.remove();
            markerTo = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(cityTo.getLat(), cityTo.getLng()))
                    .title(cityTo.getName()));
        }
        if(cityFrom != null && cityFrom.getLat() != 0 && cityTo != null && cityTo.getLat() != 0) {
            publishNextTextView.setEnabled(true);
            publishNextTextView.setBackgroundColor(getResources().getColor(R.color.shipeer));

            RouteFromToTask routeTask = new RouteFromToTask(this, cityFrom, cityTo);
            routeTask.execute();
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

        if(pathFromTo != null) pathFromTo.remove();
        pathFromTo = mMap.addPolyline(polyLineOptions);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(markerFrom.getPosition());
        builder.include(markerTo.getPosition());

        LatLngBounds bounds = builder.build();
        int padding = 50; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.animateCamera(cu);
    }
}
