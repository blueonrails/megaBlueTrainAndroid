package com.shipeer.app;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

/**
 * Created by mifercre on 10/03/15.
 */
public class PublishTravelActivity extends FragmentActivity {

    private TravelFromToFragment travelFromToFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        travelFromToFragment = new TravelFromToFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(android.R.id.content, travelFromToFragment)
                .commit();
    }
}
