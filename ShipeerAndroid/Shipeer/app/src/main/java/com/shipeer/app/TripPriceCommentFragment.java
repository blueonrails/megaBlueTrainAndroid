package com.shipeer.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import net.steamcrafted.loadtoast.LoadToast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import async.EditUserProfileTask;
import async.interfaces.OnEditUserProfileTaskCompleted;
import async.interfaces.OnPublishTripTaskTaskCompleted;
import async.PublishTripTask;
import discreteSeekBar.DiscreteSeekBar;

/**
 * Created by mifercre on 17/03/15.
 */
public class TripPriceCommentFragment extends Fragment implements View.OnClickListener, DiscreteSeekBar.OnProgressChangeListener, OnPublishTripTaskTaskCompleted, OnEditUserProfileTaskCompleted {

    //Google Analytics stuff
    private static final String VIEW_NAME = "Publish Trip Price Comment (3/3)";

    private DiscreteSeekBar smallPriceDiscreteSeekBar;
    private DiscreteSeekBar mediumPriceDiscreteSeekBar;
    private DiscreteSeekBar bigPriceDiscreteSeekBar;
    private DiscreteSeekBar extraBigPriceDiscreteSeekBar;

    private TextView smallPriceTextView;
    private TextView mediumPriceTextView;
    private TextView bigPriceTextView;
    private TextView extraBigPriceTextView;

    private EditText commentEditText;

    private TextView emailTextView;
    private TextView phoneTextView;

    private EditText emailEditText;
    private EditText phoneEditText;

    private TextView publishTripTextView;

    private RelativeLayout publishNextRelativeLayout;
    private ProgressBar asyncProgressBar;

    public TripPriceCommentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendAnalyticsView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trip_price_comment, container, false);

        smallPriceDiscreteSeekBar = (DiscreteSeekBar) view.findViewById(R.id.price_small_discreteSeekBar);
        smallPriceDiscreteSeekBar.setOnProgressChangeListener(this);

        mediumPriceDiscreteSeekBar = (DiscreteSeekBar) view.findViewById(R.id.price_medium_discreteSeekBar);
        mediumPriceDiscreteSeekBar.setOnProgressChangeListener(this);

        bigPriceDiscreteSeekBar = (DiscreteSeekBar) view.findViewById(R.id.price_big_discreteSeekBar);
        bigPriceDiscreteSeekBar.setOnProgressChangeListener(this);

        extraBigPriceDiscreteSeekBar = (DiscreteSeekBar) view.findViewById(R.id.price_extra_big_discreteSeekBar);
        extraBigPriceDiscreteSeekBar.setOnProgressChangeListener(this);

        smallPriceTextView = (TextView) view.findViewById(R.id.price_small_textView);
        smallPriceTextView.setText(getString(R.string.small) + " " + smallPriceDiscreteSeekBar.getProgress() + "€");

        mediumPriceTextView = (TextView) view.findViewById(R.id.price_medium_textView);
        mediumPriceTextView.setText(getString(R.string.medium) + " " + mediumPriceDiscreteSeekBar.getProgress() + "€");

        bigPriceTextView = (TextView) view.findViewById(R.id.price_big_textView);
        bigPriceTextView.setText(getString(R.string.big) + " " + bigPriceDiscreteSeekBar.getProgress() + "€");

        extraBigPriceTextView = (TextView) view.findViewById(R.id.price_extra_big_textView);
        extraBigPriceTextView.setText(getString(R.string.extra_big) + " " + extraBigPriceDiscreteSeekBar.getProgress() + "€");

        commentEditText = (EditText) view.findViewById(R.id.comment_editText);

        asyncProgressBar = (ProgressBar) view.findViewById(R.id.async_progress_progressBar);

        publishNextRelativeLayout = (RelativeLayout) view.findViewById(R.id.publishNext);
        publishNextRelativeLayout.setOnClickListener(this);

        publishTripTextView = (TextView) view.findViewById(R.id.publish_next_textView);

        emailTextView = (TextView) view.findViewById(R.id.email_textView);
        phoneTextView = (TextView) view.findViewById(R.id.phone_textView);

        emailEditText = (EditText) view.findViewById(R.id.email_editText);
        phoneEditText = (EditText) view.findViewById(R.id.phone_editText);

        //SharedPreferences preferences = GlobalState.getSharedPreferences();
        String baseUserEmail = GlobalState.getBaseUserEmail();//preferences.getString("BaseUserEmail", null);
        String baseUserPhone = GlobalState.getBaseUserPhone();//preferences.getString("BaseUserPhone", null);

        if(baseUserEmail != null && !baseUserEmail.isEmpty()) {
            emailEditText.setVisibility(View.GONE);
            emailTextView.setVisibility(View.GONE);
        }

        if(baseUserPhone != null && !baseUserPhone.isEmpty()) {
            phoneEditText.setVisibility(View.GONE);
            phoneTextView.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.publishNext:
                if(checkForm()) {
                    if(checkInternetConnection()) {
                        String[] form = null;
                        switch (PublishTripActivity.getTripType()) {
                            case PublishTripTask.SIMPLE_GO_TRIP:
                                form = setSimpleGoTripForm();
                                break;
                            case PublishTripTask.SIMPLE_GO_AND_BACK_TRIP:
                                form = setSimpleGoAndBackTripForm();
                                break;
                            case PublishTripTask.RECURRENT_GO_TRIP:
                                form = setRecurrentGoTripForm();
                                break;
                            case PublishTripTask.RECURRENT_GO_AND_BACK_TRIP:
                                form = setRecurrentGoAndBackTripForm();
                                break;
                        }

                        for(int i=0; i<form.length; i++) {
                            Log.d("FORM " + i, form[i]);
                        }
                        asyncProgressBar.setVisibility(View.VISIBLE);
                        asyncProgressBar.setProgress(50);
                        publishNextRelativeLayout.setEnabled(false);
                        PublishTripTask publishTripTask = new PublishTripTask(this);
                        publishTripTask.execute(form);
                    }
                    else {
                        showNoInternetConnectionError();
                    }
                }
                break;
        }
    }

    private String[] setRecurrentGoAndBackTripForm() {
        String[] form = new String[26];
        form[0] = PublishTripActivity.getCityFrom().getName(); //City from
        form[1] = PublishTripActivity.getCityTo().getName(); //City to
        form[2] = PublishTripActivity.getTripType() + ""; //Trip type
        form[3] = smallPriceDiscreteSeekBar.getProgress() + ""; //price 1
        form[4] = mediumPriceDiscreteSeekBar.getProgress() + ""; //price 2
        form[5] = bigPriceDiscreteSeekBar.getProgress() + ""; //price 3
        form[6] = extraBigPriceDiscreteSeekBar.getProgress() + ""; //price 4

        form[7] = PublishTripActivity.getGoDate().toString(); //Departure date
        form[8] = PublishTripActivity.getBackDate().toString(); //Return date
        form[9] = PublishTripActivity.getGoDate().toString(); //Starting date
        form[10] = PublishTripActivity.getBackDate().toString(); //Finishing date

        form[11] = commentEditText.getText().toString() + ""; //Description

        boolean[] departureDays = PublishTripActivity.getWeekGoDays();
        form[12] = String.valueOf(departureDays[0]); //Monday true/false
        form[13] = String.valueOf(departureDays[1]); //Tuesday true/false
        form[14] = String.valueOf(departureDays[2]); //Wednesday true/false
        form[15] = String.valueOf(departureDays[3]); //Thursday true/false
        form[16] = String.valueOf(departureDays[4]); //Friday true/false
        form[17] = String.valueOf(departureDays[5]); //Saturday true/false
        form[18] = String.valueOf(departureDays[6]); //Sunday true/false

        boolean[] returnDays = PublishTripActivity.getWeekBackDays();
        form[19] = String.valueOf(returnDays[0]); //Monday true/false
        form[20] = String.valueOf(returnDays[1]); //Tuesday true/false
        form[21] = String.valueOf(returnDays[2]); //Wednesday true/false
        form[22] = String.valueOf(returnDays[3]); //Thursday true/false
        form[23] = String.valueOf(returnDays[4]); //Friday true/false
        form[24] = String.valueOf(returnDays[5]); //Saturday true/false
        form[25] = String.valueOf(returnDays[6]); //Sunday true/false
        return form;
    }

    private String[] setRecurrentGoTripForm() {
        String[] form = new String[18];
        form[0] = PublishTripActivity.getCityFrom().getName(); //City from
        form[1] = PublishTripActivity.getCityTo().getName(); //City to
        form[2] = PublishTripActivity.getTripType() + ""; //Trip type
        form[3] = smallPriceDiscreteSeekBar.getProgress() + ""; //price 1
        form[4] = mediumPriceDiscreteSeekBar.getProgress() + ""; //price 2
        form[5] = bigPriceDiscreteSeekBar.getProgress() + ""; //price 3
        form[6] = extraBigPriceDiscreteSeekBar.getProgress() + ""; //price 4

        form[7] = PublishTripActivity.getGoDate().toString(); //Departure date
        form[8] = PublishTripActivity.getGoDate().toString(); //Starting date
        form[9] = PublishTripActivity.getBackDate().toString(); //Finishing date

        form[10] = commentEditText.getText().toString() + ""; //Description

        boolean[] departureDays = PublishTripActivity.getWeekGoDays();
        form[11] = String.valueOf(departureDays[0]); //Monday true/false
        form[12] = String.valueOf(departureDays[1]); //Tuesday true/false
        form[13] = String.valueOf(departureDays[2]); //Wednesday true/false
        form[14] = String.valueOf(departureDays[3]); //Thursday true/false
        form[15] = String.valueOf(departureDays[4]); //Friday true/false
        form[16] = String.valueOf(departureDays[5]); //Saturday true/false
        form[17] = String.valueOf(departureDays[6]); //Sunday true/false
        return form;
    }

    private String[] setSimpleGoAndBackTripForm() {
        String[] form = new String[10];
        form[0] = PublishTripActivity.getCityFrom().getName(); //City from
        form[1] = PublishTripActivity.getCityTo().getName(); //City to
        form[2] = PublishTripActivity.getTripType() + ""; //Trip type
        form[3] = smallPriceDiscreteSeekBar.getProgress() + ""; //price 1
        form[4] = mediumPriceDiscreteSeekBar.getProgress() + ""; //price 2
        form[5] = bigPriceDiscreteSeekBar.getProgress() + ""; //price 3
        form[6] = extraBigPriceDiscreteSeekBar.getProgress() + ""; //price 4
        form[7] = PublishTripActivity.getGoDate().toString(); //Go date
        form[8] = PublishTripActivity.getBackDate().toString(); //Back date
        form[9] = commentEditText.getText().toString() + ""; //Description
        return form;
    }

    private String[] setSimpleGoTripForm() {
        String[] form = new String[9];
        form[0] = PublishTripActivity.getCityFrom().getName(); //City from
        form[1] = PublishTripActivity.getCityTo().getName(); //City to
        form[2] = PublishTripActivity.getTripType() + ""; //Trip type
        form[3] = smallPriceDiscreteSeekBar.getProgress() + ""; //price 1
        form[4] = mediumPriceDiscreteSeekBar.getProgress() + ""; //price 2
        form[5] = bigPriceDiscreteSeekBar.getProgress() + ""; //price 3
        form[6] = extraBigPriceDiscreteSeekBar.getProgress() + ""; //price 4
        form[7] = PublishTripActivity.getGoDate().toString(); //Starting at
        form[8] = commentEditText.getText().toString() + ""; //Description
        return form;
    }

    private boolean checkForm() {
        String email = null;
        String phone = null;

        boolean isUserProfileChanging = false;
        if(emailEditText.getVisibility() == View.VISIBLE) {
            if(emailEditText.getText().toString().isEmpty() || !isEmailValid(emailEditText.getText().toString())) {
                emailEditText.setError(getString(R.string.missing_email));
                return false;
            }
            else {
                email = emailEditText.getText().toString();
                isUserProfileChanging = true;
            }
        }
        if(phoneEditText.getVisibility() == View.VISIBLE) {
            if(phoneEditText.getText().toString().isEmpty()) {
                phoneEditText.setError(getString(R.string.missing_phone));
                return false;
            }
            else {
                phone = phoneEditText.getText().toString();
                isUserProfileChanging = true;
            }
        }

        if(isUserProfileChanging) {
            GlobalState.saveBaseUserPhone(phone);
            GlobalState.saveBaseUserEmail(email);

            String userId = GlobalState.getBaseUserId();
            String key = GlobalState.getBaseUserKey();
            String secret = GlobalState.getBaseUserSecret();

            String [] form = {userId, key, secret, null, null, phone, null, email, null, null};
            EditUserProfileTask editUserProfileTask = new EditUserProfileTask(this);
            editUserProfileTask.execute(form);
        }

        return true;
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    @Override
    public void onProgressChanged(DiscreteSeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.price_small_discreteSeekBar:
                if(smallPriceTextView != null) smallPriceTextView.setText(getString(R.string.small) + " " + progress + "€");
                break;
            case R.id.price_medium_discreteSeekBar:
                if(mediumPriceTextView != null) mediumPriceTextView.setText(getString(R.string.medium) + " " + progress + "€");
                break;
            case R.id.price_big_discreteSeekBar:
                if(bigPriceTextView != null)  bigPriceTextView.setText(getString(R.string.big) + " " + progress + "€");
                break;
            case R.id.price_extra_big_discreteSeekBar:
                if(extraBigPriceTextView != null)  extraBigPriceTextView.setText(getString(R.string.extra_big) + " " + progress + "€");
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

    }

    public boolean checkInternetConnection() {
        ConnectivityManager conMgr = (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null)
            return false;
        if (!netInfo.isConnected())
            return false;
        if (!netInfo.isAvailable())
            return false;
        return true;
    }

    private void showNoInternetConnectionError() {
        Toast.makeText(this.getActivity(), getString(R.string.no_internet_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPublishTripTaskCompleted(String[] result) {
        asyncProgressBar.setVisibility(View.GONE);
        publishNextRelativeLayout.setEnabled(true);

        if(result != null) {
            sendAnalyticsNewTripCreated();

            Toast.makeText(this.getActivity(), getString(R.string.trip_created), Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
        else {
            Toast.makeText(this.getActivity(), getString(R.string.publish_trip_error), Toast.LENGTH_SHORT).show();
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

    public void sendAnalyticsNewTripCreated() {
        // Get tracker.
        Tracker t = ((GlobalState) getActivity().getApplication()).getTracker(
                GlobalState.TrackerName.APP_TRACKER);
        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder()
                .setCategory(VIEW_NAME)
                .setAction(getString(R.string.publish_trip))
                .setLabel("New Trip Created")
                .build());
    }

    @Override
    public void onEditUserProfileTaskCompleted(String[] result) {

    }
}
