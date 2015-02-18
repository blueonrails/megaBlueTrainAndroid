package com.shipeer.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Used to keep the same context so every time we can access the same SharedPreferences.
 *
 * Created by mifercre on 10/02/15.
 */
public class GlobalState extends Application {

    private static SharedPreferences preferences = null;

    @Override
    public void onCreate() {
        super.onCreate();
        if(preferences == null) {
            preferences = getSharedPreferences("ShipeerApp", Context.MODE_PRIVATE);
            Log.i("Application", "onCreate, preferences: " + preferences.toString());
        } else Log.i("Application", "onCreate, preferences: " + preferences.toString());
    }

    public static SharedPreferences getSharedPreferences() {
        return preferences;
    }

    public boolean userIsLogedIn() {
        return preferences.getBoolean("userIsLogedIn", false);
    }
}
