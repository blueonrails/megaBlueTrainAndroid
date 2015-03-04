package com.shipeer.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import drawer.DrawerArrowDrawable;
import drawer.NavigationAdapter;

import static android.view.Gravity.START;

public class HomeFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    //Google Maps stuff
    private static final String LOG_TAG = "HomeFragment";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    private static final String API_KEY = "AIzaSyDwI3RVBYFxq-goRpk_MuFgh5Jvm-U0Bzc";

    private AutoCompleteTextView autoCompViewFrom;
    private AutoCompleteTextView autoCompViewTo;

    //Drawer layout objects
    private DrawerArrowDrawable drawerArrowDrawable;
    private float offset;
    private boolean flipped;

    private ListView NavList;
    private ArrayList<String> NavItms;
    private NavigationAdapter NavAdapter;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_view, container, false);

        //Drawer Layout
        configureDrawerLayout(view, inflater);

        autoCompViewFrom = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextViewFrom);
        autoCompViewFrom.setAdapter(new PlacesAutoCompleteAdapter(this.getActivity(), R.layout.maps_citie));

        autoCompViewFrom.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                PlacesTask placesTask = new PlacesTask();
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
        autoCompViewTo.setAdapter(new PlacesAutoCompleteAdapter(this.getActivity(), R.layout.maps_citie));

        autoCompViewTo.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                PlacesTask placesTask = new PlacesTask();
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

        /**ApiRequest request = (ApiRequest) new ApiRequest() {
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Log.d("AsynTask", "finish");
            }
        }.execute("http://google.es");**/

        return view;
    }

    private void configureDrawerLayout(View view, LayoutInflater inflater) {
        final DrawerLayout drawer = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        final ImageView imageView = (ImageView) view.findViewById(R.id.drawer_indicator);
        final Resources resources = getResources();

        drawerArrowDrawable = new DrawerArrowDrawable(resources);
        drawerArrowDrawable.setStrokeColor(resources.getColor(android.R.color.white));
        imageView.setImageDrawable(drawerArrowDrawable);

        drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override public void onDrawerSlide(View drawerView, float slideOffset) {
                offset = slideOffset;

                // Sometimes slideOffset ends up so close to but not quite 1 or 0.
                if (slideOffset >= .995) {
                    flipped = true;
                    drawerArrowDrawable.setFlip(flipped);
                } else if (slideOffset <= .005) {
                    flipped = false;
                    drawerArrowDrawable.setFlip(flipped);
                }

                drawerArrowDrawable.setParameter(offset);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (drawer.isDrawerVisible(START)) {
                    drawer.closeDrawer(START);
                } else {
                    drawer.openDrawer(START);
                }
            }
        });

        /**final TextView styleButton = (TextView) view.findViewById(R.id.indicator_style);
        styleButton.setOnClickListener(new View.OnClickListener() {
            boolean rounded = false;

            @Override public void onClick(View v) {
                styleButton.setText(rounded //
                        ? resources.getString(R.string.rounded) //
                        : resources.getString(R.string.squared));

                rounded = !rounded;

                drawerArrowDrawable = new DrawerArrowDrawable(resources, rounded);
                drawerArrowDrawable.setParameter(offset);
                drawerArrowDrawable.setFlip(flipped);
                drawerArrowDrawable.setStrokeColor(resources.getColor(R.color.light_gray));

                imageView.setImageDrawable(drawerArrowDrawable);
            }
        });**/

        NavList = (ListView) view.findViewById(R.id.lista);

        View header = inflater.inflate(R.layout.drawer_layout_header, null);
        header.setEnabled(false);

        NavList.addHeaderView(header);

        NavItms = new ArrayList<String>();
        NavItms.add("Perfil");
        NavItms.add("Viajes");
        NavItms.add("Envios");

        NavAdapter= new NavigationAdapter(getActivity(), NavItms);
        NavList.setAdapter(NavAdapter);

        NavList.setOnItemClickListener(this);
    }

    private ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;
        //https://maps.googleapis.com/maps/api/place/autocomplete/json?input=Vict&types=(cities)&language=pt_BR&key=API_KEY
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?sensor=false&key=" + API_KEY);
            sb.append("&components=country:es");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            sb.append("&types(cities)&language=es_ES");

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                startActivity(intent);
                break;
        }
    }


    /**
     * List adapter to show cities from Google maps.
     */
    private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;

        public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    }
                    else {
                        notifyDataSetInvalidated();
                    }
                }};
            return filter;
        }
    }

    // Fetches all places from GooglePlaces AutoComplete Web Service
    private class PlacesTask extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... place) {
            return autocomplete(place[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            super.onPostExecute(result);
        }
    }
}

