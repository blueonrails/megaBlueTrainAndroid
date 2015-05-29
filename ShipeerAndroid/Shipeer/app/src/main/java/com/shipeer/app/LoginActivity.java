package com.shipeer.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import model.ColorAnimationView;

/**
 * Created by mifercre on 31/03/15.
 */
public class LoginActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MyFragmentStatePager adapter = new MyFragmentStatePager(getSupportFragmentManager());
        //ColorAnimationView colorAnimationView = (ColorAnimationView) findViewById(R.id.ColorAnimationView);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(0);
        viewPager.setAdapter(adapter);

    }

    public class MyFragmentStatePager extends FragmentStatePagerAdapter {

        public MyFragmentStatePager(FragmentManager fm) {
            super(fm);
        }

        @Override public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    sendAnalyticsView("On Boarding (" + (position+1) + "/5)");
                    OnBoardingFragment onBoardingFragment1 = new OnBoardingFragment(position);
                    return onBoardingFragment1;
                case 1:
                case 2:
                case 3:
                    sendAnalyticsView("On Boarding (" + (position+1) + "/5)");
                    OnBoardingFragment onBoardingFragment2 = new OnBoardingFragment(position);
                    return onBoardingFragment2;
                case 4:
                    sendAnalyticsView("On Boarding Login (5/5)");
                    return new LoginFragment();
            }
            return null;
        }

        @Override public int getCount() {
            return 5;
        }

        public void sendAnalyticsView(String viewName) {
            // Get tracker.
            Tracker t = ((GlobalState) getApplication()).getTracker(
                    GlobalState.TrackerName.APP_TRACKER);

            // Set screen name.
            // Where path is a String representing the screen name.
            t.setScreenName(viewName);

            // Send a screen view.
            t.send(new HitBuilders.AppViewBuilder().build());
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("LoginActivity", "popping backstack");
            fm.popBackStack();
        } else {
            Log.i("LoginActivity", "nothing on backstack, calling super");
            //super.onBackPressed();
            moveTaskToBack(true);
            finish();
        }
    }
}
