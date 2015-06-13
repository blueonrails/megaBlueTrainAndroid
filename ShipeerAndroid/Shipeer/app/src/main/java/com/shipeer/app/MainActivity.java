package com.shipeer.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.android.Utils;
import com.facebook.AppEventsLogger;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import async.EmailLoginTask;
import async.FacebookLoginTask;
import async.interfaces.OnEmailLoginTaskCompleted;
import async.interfaces.OnFacebookLoginTaskCompleted;
import async.interfaces.OnPublicUserProfileTaskCompleted;
import async.PublicUserProfileTask;
import drawer.DrawerArrowDrawable;
import drawer.NavigationAdapter;
import model.CircleTransform;
import model.Trip;

import static android.view.Gravity.START;

public class MainActivity extends FragmentActivity implements AdapterView.OnItemClickListener, View.OnClickListener, OnEmailLoginTaskCompleted, OnFacebookLoginTaskCompleted, OnPublicUserProfileTaskCompleted {

    private static final String TAG = "MainActivity";
    private static final String TAG_ONCREATE = "MainActivity.OnCreate";

    private ListView NavList;
    private ArrayList<String> NavItms;
    private ArrayList<Integer> NavIcons;
    private NavigationAdapter NavAdapter;
    private ImageView imageView;

    private static TextView actionBarTitle;
    private RelativeLayout publishTripIconRelativeLayout;

    private View header;
    private ImageView profilePictureHeader;
    private TextView headerName;

    private DrawerLayout drawer = null;
    private DrawerArrowDrawable drawerArrowDrawable;
    private float offset;
    private boolean flipped;

    private UserProfileFragment userProfileFragment;
    private UserOwnTripsFragment userOwnTripsFragment;
    private HomeFragment homeFragment;
    private LoginFragment loginFragment;

    public static final int USER_PROFILE = 0;
    public static final int HOME_FRAGMENT = 1;
    public static final int USER_TRIPS = 3;
    public static final int SEARCH_RESULTS = 4;
    public static final int TRIP_DETAILS = 5;
    public static final int TRIP_DRIVER_DETAILS = 6;

    private static int currentFragmentShown = 0;

    private Cloudinary cloudinary;

    private static ArrayList<Trip> tripsFound;
    private static Trip tripClicked;

    private static ArrayList<Trip> userOwnTripsResults;

    /**
     * Checks if the user is already loged in. In case it is not, it shows the login view. Other case, it directly shows
     * the common user profile view.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        SharedPreferences preferences = GlobalState.getSharedPreferences();
        if (preferences == null)
            preferences = getSharedPreferences("ShipeerApp", Context.MODE_PRIVATE);

        String facebookAccessToken = preferences.getString("FacebookAccessToken", null);
        Log.d("FacebookAccessToken", facebookAccessToken + "");

        String baseUserKey = preferences.getString("BaseUserKey", null);

        if (facebookAccessToken != null || baseUserKey != null) {
            setContentView(R.layout.activity_main);

            checkTokenExpireDate();
            downloadUserProfile();

            actionBarTitle = (TextView) findViewById(R.id.indicator_style);

            publishTripIconRelativeLayout = (RelativeLayout) findViewById(R.id.publish_trip_icon_relativeLayout);
            publishTripIconRelativeLayout.setOnClickListener(this);
            //Drawer Layout
            configureDrawerLayout();

            /**String fbUserId = preferences.getString("FacebookUserId", null);
            String fbUsername = preferences.getString("FacebookUsername", null);
            String fbUserGender = preferences.getString("FacebookUserGender", null);
            String fbUserFirstName = preferences.getString("FacebookUserFirstName", null);
            String fbUserMiddleName = preferences.getString("FacebookUserMiddleName", null);
            String fbUserLastName = preferences.getString("FacebookUserLastName", null);
            String fbUserBirthday = preferences.getString("FacebookUserBirthday", null);
            String fbUserEmail = preferences.getString("FacebookUserEmail", null);

            Log.d(TAG_ONCREATE, "FB id: " + fbUserId);
            Log.d(TAG_ONCREATE, "FB username: " + fbUsername);
            Log.d(TAG_ONCREATE, "FB gender: " + fbUserGender);
            Log.d(TAG_ONCREATE, "FB first name: " + fbUserFirstName);
            Log.d(TAG_ONCREATE, "FB middle name: " + fbUserMiddleName);
            Log.d(TAG_ONCREATE, "FB last name: " + fbUserLastName);
            Log.d(TAG_ONCREATE, "FB birthday: " + fbUserBirthday);
            Log.d(TAG_ONCREATE, "FB email: " + fbUserEmail);

            Log.d(TAG_ONCREATE, "BaseUserKey: " + baseUserKey); **/

            currentFragmentShown = 1;
            HomeFragment homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container_frameLayout, homeFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void downloadUserProfile() {
        String userId = GlobalState.getBaseUserId();
        String key = GlobalState.getBaseUserKey();
        String secret = GlobalState.getBaseUserSecret();
        if(userId != null && !userId.isEmpty() && key != null && !key.isEmpty() && secret != null && !secret.isEmpty()) {
            PublicUserProfileTask publicUserProfileTask = new PublicUserProfileTask(this);
            publicUserProfileTask.execute(userId, key, secret);
        }
    }

    public static void setActionBarTitle(String title) {
        if (actionBarTitle != null) actionBarTitle.setText(title);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Facebook analytics. Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    private void configureDrawerLayout() {
        final DrawerLayout drawerFinal = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer = drawerFinal;
        imageView = (ImageView) findViewById(R.id.drawer_indicator);
        final Resources resources = getResources();

        drawerArrowDrawable = new DrawerArrowDrawable(resources);
        drawerArrowDrawable.setStrokeColor(resources.getColor(android.R.color.white));
        imageView.setImageDrawable(drawerArrowDrawable);

        drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
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
                Log.d(TAG, "DRAWER CLICKED");
                if (drawerFinal.isDrawerVisible(START)) {
                    drawerFinal.closeDrawer(START);
                } else {
                    drawerFinal.openDrawer(START);
                }
            }
        });

        NavList = (ListView) findViewById(R.id.drawer_items_listView);

        LayoutInflater inflater = LayoutInflater.from(this);
        header = inflater.inflate(R.layout.drawer_layout_header, null);
        header.setEnabled(false);

        headerName = (TextView) header.findViewById(R.id.name_header);
        profilePictureHeader = (ImageView) header.findViewById(R.id.profile_picture_header);

        String userFirstName = GlobalState.getBaseUserFirstName();
        String baseUserSurname = GlobalState.getBaseUserSurname();

        if (userFirstName != null) headerName.setText(userFirstName);
        if (baseUserSurname != null)
            headerName.setText(headerName.getText() + " " + baseUserSurname);

        String baseUserProfilePictureId = GlobalState.getBaseUserProfilePictureId();
        String baseUserProfilePictureVersion = GlobalState.getBaseUserProfilePictureVersion();

        if (baseUserProfilePictureId != null && !baseUserProfilePictureId.isEmpty() && baseUserProfilePictureVersion != null && !baseUserProfilePictureVersion.isEmpty()) {
            Log.d("PictureId", baseUserProfilePictureId);
            Log.d("PictureVersion", baseUserProfilePictureVersion);

            cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(this.getApplicationContext()));
            String url = cloudinary.url().transformation(new Transformation().width(200).height(200).crop("fill")).version(baseUserProfilePictureVersion).generate(baseUserProfilePictureId);
            Log.d("URL", url);
            profilePictureHeader.setImageResource(0);
            profilePictureHeader.setImageDrawable(null);
            Picasso.with(this).load(url).transform(new CircleTransform()).into(profilePictureHeader);
        } else if (baseUserProfilePictureId != null) {
            cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(this.getApplicationContext()));
            String url = cloudinary.url().transformation(new Transformation().width(200).height(200).crop("fill")).generate(baseUserProfilePictureId);
            Log.d("URL", url);
            profilePictureHeader.setImageResource(0);
            profilePictureHeader.setImageDrawable(null);
            Picasso.with(this).load(url).transform(new CircleTransform()).into(profilePictureHeader);
        } else {
            Picasso.with(this).load(R.drawable.default_profile_pic).resize(200,200).centerCrop().transform(new CircleTransform()).into(profilePictureHeader);
        }

        NavList.addHeaderView(header);

        NavItms = new ArrayList<String>();
        NavItms.add(getString(R.string.search));
        NavItms.add(getString(R.string.publish_trip));
        NavItms.add(getString(R.string.my_trips));
        NavItms.add(getString(R.string.contact));

        /**NavIcons = new ArrayList<Integer>();
        NavIcons.add(android.R.drawable.ic_menu_search);
        NavIcons.add(R.drawable.publish_trip_black);
        NavIcons.add(R.drawable.square_facebook_128);
        NavIcons.add(android.R.drawable.ic_dialog_info);**/

        NavAdapter = new NavigationAdapter(this, NavItms);//, NavIcons);
        NavList.setAdapter(NavAdapter);

        NavList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: //Header
                drawer.closeDrawer(START);
                if (currentFragmentShown != USER_PROFILE) {
                    currentFragmentShown = USER_PROFILE;
                    setActionBarTitle(getString(R.string.profile));
                    Log.d("FRAGMENT TRANS", "GOING TO USER PROFILE");
                    userProfileFragment = new UserProfileFragment();

                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.main_fragment_container_frameLayout, userProfileFragment)
                            .addToBackStack("userProfileFragment")
                            .commit();
                }
                /**Intent profileIntent = new Intent(this, UserProfileActivity.class);
                 startActivity(profileIntent);
                 this.overridePendingTransition(R.anim.slide_in_right_to_left, R.anim.zoom_out);**/
                break;
            case 1:
                drawer.closeDrawer(START);
                if (currentFragmentShown != HOME_FRAGMENT) {
                    currentFragmentShown = HOME_FRAGMENT;
                    setActionBarTitle(getString(R.string.search_trunk));
                    Log.d("FRAGMENT TRANS", "GOING TO HOME");
                    homeFragment = new HomeFragment();

                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    /**getSupportFragmentManager().beginTransaction()
                     .replace(R.id.main_fragment_container_frameLayout, homeFragment)
                     .commit();**/
                }
                break;
            case 2: //Publish trip
                drawer.closeDrawer(START);
                sendAnalyticsPublishDrawerItemClicked();
                Intent publishTripIntent = new Intent(this, PublishTripActivity.class);
                startActivity(publishTripIntent);
                this.overridePendingTransition(R.anim.slide_in_right_to_left, R.anim.zoom_out);
                break;
            case 3: //My trips
                drawer.closeDrawer(START);
                if (currentFragmentShown != USER_TRIPS) {
                    currentFragmentShown = USER_TRIPS;
                    setActionBarTitle(getString(R.string.my_trips));
                    Log.d("FRAGMENT TRANS", "GOING TO USER TRIPS");
                    userOwnTripsFragment = new UserOwnTripsFragment();

                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.main_fragment_container_frameLayout, userOwnTripsFragment)
                            .addToBackStack("userOwnTripsFragment")
                            .commit();
                }
                break;
            case 4:
                drawer.closeDrawer(START);
                MaterialDialog dialog = new MaterialDialog.Builder(this)
                        .title(R.string.contact)
                        .items(R.array.contact_options)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if (which == 0) {
                                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                            "mailto","hola@shipeer.com", null));
                                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Soporte Shipeer");
                                    startActivity(Intent.createChooser(emailIntent, "Enviar email..."));
                                } else if (which == 1) {
                                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                    }
                                }
                            }
                        }).build();

                dialog.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        // Facebook analytics. Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);

        String facebookAccessToken = GlobalState.getFacebookAccessToken();
        Log.d("FacebookAccessToken", facebookAccessToken + "");

        String baseUserKey = GlobalState.getBaseUserKey();

        if (facebookAccessToken != null || baseUserKey != null) {
            /**LayoutInflater inflater = this.getLayoutInflater();
             View header = inflater.inflate(R.layout.drawer_layout_header, null);
             header.setEnabled(false);**/

            TextView headerName = (TextView) header.findViewById(R.id.name_header);
            ImageView profilePictureHeader = (ImageView) header.findViewById(R.id.profile_picture_header);

            String userFirstName = GlobalState.getBaseUserFirstName();
            String baseUserSurname = GlobalState.getBaseUserSurname();

            if (userFirstName != null) headerName.setText(userFirstName);
            if (baseUserSurname != null)
                headerName.setText(headerName.getText() + " " + baseUserSurname);

            String baseUserProfilePictureId = GlobalState.getBaseUserProfilePictureId();
            String baseUserProfilePictureVersion = GlobalState.getBaseUserProfilePictureVersion();

            if (baseUserProfilePictureId != null && !baseUserProfilePictureId.isEmpty() && baseUserProfilePictureVersion != null && !baseUserProfilePictureVersion.isEmpty()) {
                cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(this.getApplicationContext()));
                String url = cloudinary.url().transformation(new Transformation().width(200).height(200).crop("fill")).version(baseUserProfilePictureVersion).generate(baseUserProfilePictureId);
                Log.d("URL", url);
                profilePictureHeader.setImageResource(0);
                profilePictureHeader.setImageDrawable(null);
                Picasso.with(this).load(url).transform(new CircleTransform()).into(profilePictureHeader);
            } else if (baseUserProfilePictureId != null) {
                cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(this.getApplicationContext()));
                String url = cloudinary.url().transformation(new Transformation().width(200).height(200).crop("fill")).generate(baseUserProfilePictureId);
                Log.d("URL", url);
                profilePictureHeader.setImageResource(0);
                profilePictureHeader.setImageDrawable(null);
                Picasso.with(this).load(url).transform(new CircleTransform()).into(profilePictureHeader);
            } else {
                //profilePictureHeader.setImageDrawable(getResources().getDrawable(R.drawable.logo_kazan_150));
                Picasso.with(this).load(R.drawable.default_profile_pic).resize(200,200).centerCrop().transform(new CircleTransform()).into(profilePictureHeader);
            }

            /**if(baseUserProfilePicture != null) {
             Log.e("pic", baseUserProfilePicture);
             File file = new File(baseUserProfilePicture);
             Picasso.with(getActivity()).load(file)
             .resize(50, 50)
             .centerCrop().into(profilePictureHeader);
             //profilePictureHeader.setImageDrawable(Drawable.createFromPath(file.getAbsolutePath()));
             }**/
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void checkTokenExpireDate() {
        String facebookAccessToken = GlobalState.getFacebookAccessToken();

        Date dateExpires = GlobalState.getDateExpires();
        Calendar c = Calendar.getInstance();
        Date currentDate = c.getTime();


        if (dateExpires != null && currentDate != null && currentDate.compareTo(dateExpires) > 0) {
            Log.d("TOKEN EXPIRE DATE", "" + dateExpires.toString());
            Log.d("CURRENT DATE", "" + currentDate.toString());

            Log.d("SHIPEER TOKEN EXPIRED", "GETTING A NEW SHIPEER TOKEN");
            if (facebookAccessToken == null) { //EMAIL LOGIN
                String email = GlobalState.getBaseUserEmail();
                String password = GlobalState.getBaseUserPassword();
                EmailLoginTask emailLoginTask = new EmailLoginTask(this);
                emailLoginTask.execute(email, password);
            } else { //FACEBOOK LOGIN
                String facebookId = GlobalState.getFacebookId();
                String facebookToken = GlobalState.getFacebookToken();
                FacebookLoginTask facebookLoginTask = new FacebookLoginTask(this);
                facebookLoginTask.execute(facebookId, facebookToken);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.publish_trip_icon_relativeLayout:
                sendAnalyticsPublishIconClicked();
                Intent publishTripIntent = new Intent(this, PublishTripActivity.class);
                startActivity(publishTripIntent);
                this.overridePendingTransition(R.anim.slide_in_right_to_left, R.anim.zoom_out);
                break;
        }
    }

    public static void setSearchResults(ArrayList<Trip> tripsFound) {
        MainActivity.tripsFound = tripsFound;
    }

    public static ArrayList<Trip> getSearchResults() {
        return MainActivity.tripsFound;
    }

    public static Trip getTripClicked() {
        return tripClicked;
    }

    public static void setTripClicked(Trip tripClicked) {
        MainActivity.tripClicked = tripClicked;
    }

    public static ArrayList<Trip> getUserOwnTripsResults() {
        return MainActivity.userOwnTripsResults;
    }

    public static void setUserOwnTripsResults(ArrayList<Trip> userOwnTripsResults) {
        MainActivity.userOwnTripsResults = userOwnTripsResults;
    }

    public void sendAnalyticsPublishIconClicked() {
        // Get tracker.
        Tracker t = ((GlobalState) getApplication()).getTracker(
                GlobalState.TrackerName.APP_TRACKER);
        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder()
                .setCategory("Home Fragment")
                .setAction(getString(R.string.publish_trip))
                .setLabel("Top Right Publish Icon clicked")
                .build());
    }

    public void sendAnalyticsPublishDrawerItemClicked() {
        // Get tracker.
        Tracker t = ((GlobalState) getApplication()).getTracker(
                GlobalState.TrackerName.APP_TRACKER);
        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder()
                .setCategory("Home Fragment")
                .setAction(getString(R.string.publish_trip))
                .setLabel("Drawer Item Publish Row clicked")
                .build());
    }

    public static int getCurrentFragmentShown() {
        return currentFragmentShown;
    }

    public static void setCurrentFragmentShown(int currentFragmentShown) {
        MainActivity.currentFragmentShown = currentFragmentShown;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //moveTaskToBack(true);
        //finish();
        int backSize = getSupportFragmentManager().getBackStackEntryCount();
        Log.d("BACK STACK", "SIZE=" + backSize);

        if (backSize == 0) {
            setCurrentFragmentShown(HOME_FRAGMENT);
            setActionBarTitle(getString(R.string.search_trunk));
        } else {
            FragmentManager.BackStackEntry backStackEntry = getSupportFragmentManager().getBackStackEntryAt(backSize - 1);
            String backName = backStackEntry.getName();
            Log.d("BACK STACK", "NAME=" + backName);
            if (backName.equalsIgnoreCase("userProfileFragment")) {
                setCurrentFragmentShown(USER_PROFILE);
                setActionBarTitle(getString(R.string.profile));
            } else if (backName.equalsIgnoreCase("userOwnTripsFragment")) {
                setCurrentFragmentShown(USER_TRIPS);
                setActionBarTitle(getString(R.string.my_trips));
            } else if (backName.equalsIgnoreCase("searchResultsFragment")) {
                setCurrentFragmentShown(SEARCH_RESULTS);
                setActionBarTitle(getString(R.string.search_trunk));
            } else if (backName.equalsIgnoreCase("searchTripDetailsFragment")) {
                setCurrentFragmentShown(TRIP_DETAILS);
                setActionBarTitle(getString(R.string.search_trunk));
            } else if (backName.equalsIgnoreCase("driverPublicProfileFragment")) {
                setCurrentFragmentShown(TRIP_DRIVER_DETAILS);
                setActionBarTitle(getString(R.string.search_trunk));
            }
        }
    }

    @Override
    public void onEmailLoginTaskCompleted(String[] result) {

    }

    @Override
    public void onFacebookLoginTaskCompleted(String[] result) {

    }

    @Override
    public void onPublicUserProfileTaskCompleted(String[] result) {
        String userFirstName = GlobalState.getBaseUserFirstName();
        String baseUserSurname = GlobalState.getBaseUserSurname();
        if (userFirstName != null) headerName.setText(userFirstName);
        if (baseUserSurname != null)
            headerName.setText(headerName.getText() + " " + baseUserSurname);

        String baseUserProfilePictureId = GlobalState.getBaseUserProfilePictureId();
        String baseUserProfilePictureVersion = GlobalState.getBaseUserProfilePictureVersion();

        if (baseUserProfilePictureId != null && !baseUserProfilePictureId.isEmpty() && baseUserProfilePictureVersion != null && !baseUserProfilePictureVersion.isEmpty()) {
            cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(this.getApplicationContext()));
            String url = cloudinary.url().transformation(new Transformation().width(200).height(200).crop("fill")).version(baseUserProfilePictureVersion).generate(baseUserProfilePictureId);
            Log.d("URL", url);
            profilePictureHeader.setImageResource(0);
            profilePictureHeader.setImageDrawable(null);
            Picasso.with(this).load(url).transform(new CircleTransform()).into(profilePictureHeader);
        } else if (baseUserProfilePictureId != null) {
            cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(this.getApplicationContext()));
            String url = cloudinary.url().transformation(new Transformation().width(200).height(200).crop("fill")).generate(baseUserProfilePictureId);
            Log.d("URL", url);
            profilePictureHeader.setImageResource(0);
            profilePictureHeader.setImageDrawable(null);
            Picasso.with(this).load(url).transform(new CircleTransform()).into(profilePictureHeader);
        } else {
            //profilePictureHeader.setImageDrawable(getResources().getDrawable(R.drawable.logo_kazan_150));
            Picasso.with(this).load(R.drawable.default_profile_pic).resize(200,200).centerCrop().transform(new CircleTransform()).into(profilePictureHeader);
        }
    }
}
