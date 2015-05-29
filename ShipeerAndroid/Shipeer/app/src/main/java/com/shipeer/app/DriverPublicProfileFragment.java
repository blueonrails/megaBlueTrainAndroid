package com.shipeer.app;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.android.Utils;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import model.CircleTransform;


/**
 * A simple {@link Fragment} subclass.
 */
public class DriverPublicProfileFragment extends Fragment {

    public static final String VIEW_NAME = "Driver Public Profile Fragment";

    private String driverName;
    private String driverEmail;
    private String driverPhone;
    private String driverProfilePictureId;
    private String driverProfilePictureVersion;
    private boolean driverHasFacebook;

    private TextView driverNameTextView;
    private TextView driverEmailTextView;
    private TextView driverPhoneTextView;

    private RelativeLayout facebookValidatedRelativeLayout;

    private ImageView driverProfilePictureImageview;

    private Cloudinary cloudinary;

    public DriverPublicProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendAnalyticsView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity.setCurrentFragmentShown(MainActivity.TRIP_DRIVER_DETAILS);
        View view = inflater.inflate(R.layout.fragment_driver_public_profile, container, false);

        Bundle data = getArguments();
        driverName = data.getString("driverName", getString(R.string.no_name));
        driverEmail = data.getString("driverEmail", getString(R.string.no_email));
        driverPhone = data.getString("driverPhone", getString(R.string.no_phone));

        driverProfilePictureId = data.getString("driverProfilePictureId", null);
        driverProfilePictureVersion = data.getString("driverProfilePictureVersion", null);

        driverHasFacebook = data.getBoolean("driverHasFacebook", false);

        driverProfilePictureImageview = (ImageView) view.findViewById(R.id.profile_picture_circularimageview);

        if (driverProfilePictureId != null && !driverProfilePictureId.isEmpty() && driverProfilePictureVersion != null && !driverProfilePictureVersion.isEmpty()) {
            cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(this.getActivity().getApplicationContext()));
            String url = cloudinary.url().transformation(new Transformation().width(200).height(200).crop("fill")).version(driverProfilePictureVersion).generate(driverProfilePictureId);
            Log.d("URL", url);
            driverProfilePictureImageview.setImageResource(0);
            driverProfilePictureImageview.setImageDrawable(null);
            Picasso.with(this.getActivity()).load(url).transform(new CircleTransform()).into(driverProfilePictureImageview);
        } else if (driverProfilePictureId != null) {
            cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(this.getActivity().getApplicationContext()));
            String url = cloudinary.url().transformation(new Transformation().width(200).height(200).crop("fill")).generate(driverProfilePictureId);
            Log.d("URL", url);
            driverProfilePictureImageview.setImageResource(0);
            driverProfilePictureImageview.setImageDrawable(null);
            Picasso.with(this.getActivity()).load(url).transform(new CircleTransform()).into(driverProfilePictureImageview);
        } else {
            //profilePictureHeader.setImageDrawable(getResources().getDrawable(R.drawable.logo_kazan_150));
            Picasso.with(this.getActivity()).load(R.drawable.logo_kazan_150).transform(new CircleTransform()).into(driverProfilePictureImageview);
        }

        driverNameTextView = (TextView) view.findViewById(R.id.complete_name_textview);
        driverEmailTextView = (TextView) view.findViewById(R.id.email_textView);
        driverPhoneTextView = (TextView) view.findViewById(R.id.phone_textView);

        if(driverName != null && !driverName.isEmpty()) driverNameTextView.setText(driverName);
        if(driverEmail != null && !driverEmail.isEmpty()) driverEmailTextView.setText(driverEmail);
        if(driverPhone != null && !driverPhone.isEmpty()) driverPhoneTextView.setText(driverPhone);

        facebookValidatedRelativeLayout = (RelativeLayout) view.findViewById(R.id.facebook_row);
        if(!driverHasFacebook) facebookValidatedRelativeLayout.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.setCurrentFragmentShown(MainActivity.TRIP_DRIVER_DETAILS);
        MainActivity.setActionBarTitle(getString(R.string.search_trunk));
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
