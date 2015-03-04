package com.shipeer.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.facebook.AppEventsLogger;

public class MainActivity extends FragmentActivity {

    private static final String TAG_ONCREATE = "MainActivity.OnCreate";

    private LoginFragment loginFragment;

    /**
     * Checks if the user is already loged in. In case it is not, it shows the login view. Other case, it directly shows
     * the common user profile view.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Start", "Start");

        SharedPreferences preferences = GlobalState.getSharedPreferences();
        if(preferences == null) preferences = getSharedPreferences("ShipeerApp", Context.MODE_PRIVATE);

        String facebookAccessToken = preferences.getString("FacebookAccessToken", null);
        Log.d("FacebookAccessToken", facebookAccessToken + "");

        if (facebookAccessToken != null) {
            String fbUserId = preferences.getString("FacebookUserId", "");
            String fbUsername = preferences.getString("FacebookUsername", "");
            String fbUserGender = preferences.getString("FacebookUserGender", "");
            String fbUserFirstName = preferences.getString("FacebookUserFirstName", "");
            String fbUserMiddleName = preferences.getString("FacebookUserMiddleName", "");
            String fbUserLastName = preferences.getString("FacebookUserLastName", "");
            String fbUserBirthday = preferences.getString("FacebookUserBirthday", "");
            String fbUserEmail = preferences.getString("FacebookUserEmail", "");

            Log.d(TAG_ONCREATE, "FB id: " + fbUserId);
            Log.d(TAG_ONCREATE, "FB username: " + fbUsername);
            Log.d(TAG_ONCREATE, "FB gender: " + fbUserGender);
            Log.d(TAG_ONCREATE, "FB first name: " + fbUserFirstName);
            Log.d(TAG_ONCREATE, "FB middle name: " + fbUserMiddleName);
            Log.d(TAG_ONCREATE, "FB last name: " + fbUserLastName);
            Log.d(TAG_ONCREATE, "FB birthday: " + fbUserBirthday);
            Log.d(TAG_ONCREATE, "FB email: " + fbUserEmail);


            HomeFragment homeFragment = new HomeFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, homeFragment)
                    .commit();
        } else {
            if (savedInstanceState == null) {
                Log.d(TAG_ONCREATE, "SavedInstanceState = null");
                // Add the fragment as a new one
                loginFragment = new LoginFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(android.R.id.content, loginFragment)
                        .commit();
            } else {
                Log.d(TAG_ONCREATE, "SavedInstanceState != null");
                // Set the fragment from the restored state
                loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Facebook analytics. Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Facebook analytics. Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
}
