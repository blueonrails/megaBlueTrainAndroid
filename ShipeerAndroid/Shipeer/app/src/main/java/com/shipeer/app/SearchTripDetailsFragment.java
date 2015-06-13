package com.shipeer.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.method.ScrollingMovementMethod;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.android.Utils;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import async.interfaces.OnRouteFromToTaskCompleted;
import async.RouteFromToTask;
import model.CircleTransform;
import model.City;
import model.MySimpleDateFormat;
import model.Trip;

/**
 * Created by mifercre on 08/04/15.
 */
public class SearchTripDetailsFragment extends Fragment implements OnRouteFromToTaskCompleted, View.OnClickListener {

    private static final String VIEW_NAME = "Search Trip Details Fragment";
    private static View view;

    private TextView tripDateTextView;
    private TextView tripCityFromTextView;
    private TextView tripCityToTextView;

    private TextView priceSmallTextView;
    private TextView priceMediumTextView;
    private TextView priceBigTextView;
    private TextView priceExtraBigTextView;

    private TextView descriptionTextView;

    private RelativeLayout driverDetailsRelativeLayout;
    private TextView driverNameTextView;
    private ImageView driverProfilePicImageView;

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    private Marker markerFrom;
    private Marker markerTo;
    private Polyline pathFromTo;

    private City cityFrom;
    private City cityTo;

    private Trip trip;

    //private TextView bookTextView;
    private LinearLayout callDriverLinearLayout;
    private LinearLayout whatsappDriverLinearLayout;

    private Cloudinary cloudinary;

    public SearchTripDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity.setCurrentFragmentShown(MainActivity.TRIP_DETAILS);

        sendAnalyticsView();
        
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_search_trip_details, container, false);
        } catch (InflateException e) {
            e.printStackTrace();
        }

        trip = MainActivity.getTripClicked();

        if (trip == null) {
            trip = new Trip();
            trip.setDepartureDate(getArguments().getString("tripDate", ""));
            trip.setCarrierName(getArguments().getString("tripCarrierName", ""));
            trip.setCarrierEmail(getArguments().getString("tripCarrierEmail", ""));
            trip.setCarrierPhone(getArguments().getString("tripCarrierPhone", ""));
            trip.setCarrierPictureId(getArguments().getString("tripCarrierProfilePicId", ""));
            trip.setCarrierPictureVersion(getArguments().getString("tripCarrierProfilePicVersion", ""));
            trip.setCarrierHasFacebook(getArguments().getBoolean("tripCarrierHasFacebook", false));

            trip.setPriceSmall(getArguments().getString("tripPriceSmall", ""));
            trip.setPriceMedium(getArguments().getString("tripPriceMedium", ""));
            trip.setPriceBig(getArguments().getString("tripPriceBig", ""));
            trip.setPriceExtraBig(getArguments().getString("tripPriceExtraBig", ""));

            cityFrom = new City();
            cityFrom.setName(getArguments().getString("cityFromName", ""));
            cityFrom.setLat(getArguments().getDouble("cityFromLat", 0));
            cityFrom.setLng(getArguments().getDouble("cityFromLng", 0));

            cityTo = new City();
            cityTo.setName(getArguments().getString("cityToName", ""));
            cityTo.setLat(getArguments().getDouble("cityToLat", 0));
            cityTo.setLng(getArguments().getDouble("cityToLng", 0));

            trip.setCityFrom(cityFrom);
            trip.setCityTo(cityTo);
        }

        tripDateTextView = (TextView) view.findViewById(R.id.trip_date_textView);
        Date androidDate = MySimpleDateFormat.parseAndroidDate(trip.getDepartureDateAndroid());
        tripDateTextView.setText(MySimpleDateFormat.formatAndroidDateTime(androidDate));

        tripCityFromTextView = (TextView) view.findViewById(R.id.trip_city_from_textView);
        tripCityFromTextView.setText(trip.getCityFrom().getName());

        tripCityToTextView = (TextView) view.findViewById(R.id.trip_city_to_textView);
        tripCityToTextView.setText(trip.getCityTo().getName());

        priceSmallTextView = (TextView) view.findViewById(R.id.price_small_package_textView);
        priceSmallTextView.setText(trip.getPriceSmall() + "€");

        priceMediumTextView = (TextView) view.findViewById(R.id.price_medium_package_textView);
        priceMediumTextView.setText(trip.getPriceMedium() + "€");

        priceBigTextView = (TextView) view.findViewById(R.id.price_big_package_textView);
        priceBigTextView.setText(trip.getPriceBig() + "€");

        priceExtraBigTextView = (TextView) view.findViewById(R.id.price_extra_big_package_textView);
        priceExtraBigTextView.setText(trip.getPriceExtraBig() + "€");

        descriptionTextView = (TextView) view.findViewById(R.id.trip_description_textView);
        descriptionTextView.setMovementMethod(new ScrollingMovementMethod());
        descriptionTextView.setText(trip.getDescription() + "");

        driverDetailsRelativeLayout = (RelativeLayout) view.findViewById(R.id.trip_driver_details_relativeLayout);
        driverDetailsRelativeLayout.setOnClickListener(this);
        driverNameTextView = (TextView) view.findViewById(R.id.driver_name_textView);
        driverNameTextView.setText(trip.getCarrierName());

        driverProfilePicImageView = (ImageView) view.findViewById(R.id.driver_profile_pic_imageView);

        String picId = trip.getCarrierPictureId();
        String picVersion = trip.getCarrierPictureVersion();
        if(picId != null && !picId.isEmpty() && picVersion != null && !picVersion.isEmpty()) {
            cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(this.getActivity()));
            String url = cloudinary.url().transformation(new Transformation().width(200).height(200).crop("fill")).version(picVersion).generate(picId);
            Picasso.with(this.getActivity()).load(url).transform(new CircleTransform()).into(driverProfilePicImageView);
        }

        FragmentManager fm = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_trip_details);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.getUiSettings().setAllGesturesEnabled(false);

                markerFrom = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(trip.getCityFrom().getLat(), trip.getCityFrom().getLng()))
                        .title(trip.getCityFrom().getName()));

                markerTo = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(trip.getCityTo().getLat(), trip.getCityTo().getLng()))
                        .title(trip.getCityTo().getName()));

                RouteFromToTask routeTask = new RouteFromToTask(SearchTripDetailsFragment.this, trip.getCityFrom(), trip.getCityTo());
                routeTask.execute();
            }
        });

        callDriverLinearLayout = (LinearLayout) view.findViewById(R.id.call_driver_linearLayout);
        callDriverLinearLayout.setOnClickListener(this);

        whatsappDriverLinearLayout = (LinearLayout) view.findViewById(R.id.whatsapp_driver_linearLayout);
        whatsappDriverLinearLayout.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.setCurrentFragmentShown(MainActivity.TRIP_DETAILS);
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
        if(polyLineOptions != null && pathFromTo != null && mMap != null) pathFromTo = mMap.addPolyline(polyLineOptions);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(markerFrom.getPosition());
        builder.include(markerTo.getPosition());

        LatLngBounds bounds = builder.build();
        int padding = 50; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.animateCamera(cu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.trip_driver_details_relativeLayout:
                Bundle bundle = new Bundle();
                bundle.putString("driverName", trip.getCarrierName());
                bundle.putString("driverEmail", trip.getCarrierEmail());
                bundle.putString("driverPhone", trip.getCarrierPhone());
                bundle.putString("driverProfilePictureId", trip.getCarrierPictureId());
                bundle.putString("driverProfilePictureVersion", trip.getCarrierPictureVersion());
                bundle.putBoolean("driverHasFacebook", trip.getCarrierHasFacebook());

                DriverPublicProfileFragment driverPublicProfileFragment = new DriverPublicProfileFragment();
                driverPublicProfileFragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right_to_left, R.anim.slide_out_left, R.anim.slide_in_left_to_right, R.anim.slide_out_right)
                        .replace(R.id.main_fragment_container_frameLayout, driverPublicProfileFragment)
                        .addToBackStack("driverPublicProfileFragment")
                        .commit();
                break;
            case R.id.call_driver_linearLayout:
                sendAnalyticsBooking();
                Intent dial = new Intent();
                dial.setAction("android.intent.action.DIAL");
                dial.setData(Uri.parse("tel:" + trip.getCarrierPhone()));
                startActivity(dial);
                break;
            case R.id.whatsapp_driver_linearLayout:
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

                intent.putExtra(ContactsContract.Intents.Insert.NAME, "Shipeer " + trip.getCarrierName());
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, trip.getCarrierPhone());
                //intent.putExtra(ContactsContract.Intents.Insert.EMAIL, trip.getCarrierEmail());

                this.getActivity().startActivity(intent);
                break;
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

    public void sendAnalyticsBooking() {
        // Get tracker.
        Tracker t = ((GlobalState) getActivity().getApplication()).getTracker(
                GlobalState.TrackerName.APP_TRACKER);
        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder()
                .setCategory(VIEW_NAME)
                .setAction(getString(R.string.contact) + " -> " + trip.getTripId())
                .build());
    }
}
