package com.shipeer.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import async.OnPlacesTaskCompleted;
import async.PlacesTask;
import drawer.DrawerArrowDrawable;
import drawer.NavigationAdapter;
import model.City;

import static android.view.Gravity.START;

public class HomeFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, OnPlacesTaskCompleted {

    //Google Maps stuff
    private static ArrayList<City> cities = null;

    private AutoCompleteTextView autoCompViewFrom;
    private AutoCompleteTextView autoCompViewTo;

    //Drawer layout objects
    private final DrawerLayout drawer = null;
    private DrawerArrowDrawable drawerArrowDrawable;
    private float offset;
    private boolean flipped;

    private ListView NavList;
    private ArrayList<String> NavItms;
    private NavigationAdapter NavAdapter;

    //View variables
    private LinearLayout flipView;
    private ImageView imageView;

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

        flipView = (LinearLayout) view.findViewById(R.id.flip_view);
        flipView.setOnClickListener(this);

        autoCompViewFrom = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextViewFrom);
        autoCompViewFrom.setAdapter(new PlacesAutoCompleteAdapter(this.getActivity(), R.layout.maps_citie));

        autoCompViewFrom.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                PlacesTask placesTask = new PlacesTask(HomeFragment.this);
                placesTask.execute(s.toString());
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
                PlacesTask placesTask = new PlacesTask(HomeFragment.this);
                placesTask.execute(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
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
        imageView = (ImageView) view.findViewById(R.id.drawer_indicator);
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
            @Override
            public void onClick(View v) {
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

        SharedPreferences preferences = GlobalState.getSharedPreferences();

        TextView headerName = (TextView) header.findViewById(R.id.name_header);
        ImageView profilePictureHeader = (ImageView) header.findViewById(R.id.profile_picture_header);

        String userFirstName = preferences.getString("BaseUserFirstName", null);
        String baseUserSurname = preferences.getString("BaseUserSurname", null);
        //String baseUserProfilePicture = preferences.getString("BaseUserProfilePicture", null);

        if(userFirstName != null) headerName.setText(userFirstName);
        if(baseUserSurname != null) headerName.setText(headerName.getText() + " " + baseUserSurname);
        /**if(baseUserProfilePicture != null) {
            File file = new File(baseUserProfilePicture);
            profilePictureHeader.setImageDrawable(Drawable.createFromPath(file.getAbsolutePath()));
        }**/

        String userID = preferences.getString("FacebookUserId", null);
        Picasso.with(getActivity()).load("https://graph.facebook.com/" + userID+ "/picture?type=large").into(profilePictureHeader);

        NavList.addHeaderView(header);

        NavItms = new ArrayList<String>();
        NavItms.add(getString(R.string.publish_travel));
        NavItms.add(getString(R.string.my_travels));

        NavAdapter= new NavigationAdapter(getActivity(), NavItms);
        NavList.setAdapter(NavAdapter);

        NavList.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View header = inflater.inflate(R.layout.drawer_layout_header, null);
        header.setEnabled(false);

        SharedPreferences preferences = GlobalState.getSharedPreferences();

        TextView headerName = (TextView) header.findViewById(R.id.name_header);
        ImageView profilePictureHeader = (ImageView) header.findViewById(R.id.profile_picture_header);

        String userFirstName = preferences.getString("BaseUserFirstName", null);
        String baseUserSurname = preferences.getString("BaseUserSurname", null);
        String baseUserProfilePicture = preferences.getString("BaseUserProfilePicture", null);

        if(userFirstName != null) headerName.setText(userFirstName);
        if(baseUserSurname != null) headerName.setText(headerName.getText() + " " + baseUserSurname);

        Picasso.with(getActivity()).load(baseUserProfilePicture).into(profilePictureHeader);

        /**if(baseUserProfilePicture != null) {
            Log.e("pic", baseUserProfilePicture);
            File file = new File(baseUserProfilePicture);
            Picasso.with(getActivity()).load(file)
                    .resize(50, 50)
                    .centerCrop().into(profilePictureHeader);
            //profilePictureHeader.setImageDrawable(Drawable.createFromPath(file.getAbsolutePath()));
        }**/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flip_view:
                String aux = autoCompViewFrom.getText().toString();
                autoCompViewFrom.setText(autoCompViewTo.getText().toString() + "");
                autoCompViewTo.setText(aux + "");
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: //Header
                Intent profileIntent = new Intent(getActivity(), UserProfileActivity.class);
                startActivity(profileIntent);
                this.getActivity().overridePendingTransition(R.anim.right_to_left_slide_in, R.anim.zoom_out);
                break;
            case 1: //Publish travel
                Intent publishTravelIntent = new Intent(getActivity(), PublishTravelActivity.class);
                startActivity(publishTravelIntent);
                this.getActivity().overridePendingTransition(R.anim.right_to_left_slide_in, R.anim.zoom_out);
                break;
            case 2: //My travels
                /**Intent myTravelsIntent = new Intent(getActivity(), UserProfileActivity.class);
                startActivity(myTravelsIntent);
                this.getActivity().overridePendingTransition(R.anim.right_to_left_slide_in, R.anim.zoom_out);**/
                break;
        }
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
            return resultList.size();
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
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = PlacesTask.autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        HomeFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                notifyDataSetChanged();
                            }
                        });
                        notifyDataSetChanged();
                    }
                    else {
                        notifyDataSetInvalidated();
                    }
                }};
            return filter;
        }
    }
}

