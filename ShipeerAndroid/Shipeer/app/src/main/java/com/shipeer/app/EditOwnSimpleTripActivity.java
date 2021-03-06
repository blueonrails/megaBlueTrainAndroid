package com.shipeer.app;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.timessquare.CalendarPickerView;

import net.steamcrafted.loadtoast.LoadToast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import async.DeleteOwnTripTask;
import async.EditOwnTripTask;
import async.PublishTripTask;
import async.interfaces.OnDeleteOwnTripTaskCompleted;
import async.interfaces.OnEditOwnTripTaskCompleted;
import async.interfaces.OnPlacesTaskCompleted;
import async.PlacesTask;
import discreteSeekBar.DiscreteSeekBar;
import model.City;
import model.MySimpleDateFormat;
import model.Trip;


public class EditOwnSimpleTripActivity extends Activity implements View.OnClickListener, DiscreteSeekBar.OnProgressChangeListener, AdapterView.OnItemClickListener, OnPlacesTaskCompleted, OnEditOwnTripTaskCompleted, OnDeleteOwnTripTaskCompleted {

    private static final String VIEW_NAME = "Edit Own Simple Trip Activity";

    private Trip currentTrip;

    private TextView actionBarTitleTextView;
    private ImageView backImageView;
    private RelativeLayout deleteOwnTripRelativeLayout;

    private AutoCompleteTextView autoCompViewFrom;
    private AutoCompleteTextView autoCompViewTo;

    private LinearLayout flipView;
    private ImageView flipArrows;

    private ArrayList<City> cities = null;
    private City cityFrom;
    private City cityTo;

    private boolean isGoAndBackTrip = false;

    private static TextView timeGoTextView;
    private static TextView dateGoTextView;
    private TextView goTextTextView;
    private CardView dateGoCardView;
    private CardView timeGoCardView;

    private RelativeLayout backDateAndTimeRelativeLayout;
    private static TextView timeBackTextView;
    private static TextView dateBackTextView;
    private TextView backTextTextView;
    private CardView dateBackCardView;
    private CardView timeBackCardView;

    private CalendarPickerView calendarGoDialogPickerView;

    private Date goDate;
    private Date backDate;

    private TextView goTripFromToTextView;
    private TextView backTripFromToTextView;

    private DiscreteSeekBar smallPriceDiscreteSeekBar;
    private DiscreteSeekBar mediumPriceDiscreteSeekBar;
    private DiscreteSeekBar bigPriceDiscreteSeekBar;
    private DiscreteSeekBar extraBigPriceDiscreteSeekBar;

    private TextView smallPriceTextView;
    private TextView mediumPriceTextView;
    private TextView bigPriceTextView;
    private TextView extraBigPriceTextView;

    private EditText descriptionEditText;

    private int priceSmall;
    private int priceMedium;
    private int priceBig;
    private int priceExtraBig;

    private String description;

    private TextView saveEditOwnTripTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_own_simple_trip);

        sendAnalyticsView();

        actionBarTitleTextView = (TextView) findViewById(R.id.edit_own_trip_action_bar_title_textview);
        actionBarTitleTextView.setText(getString(R.string.edit_trip));

        backImageView = (ImageView) findViewById(R.id.drawer_indicator);
        backImageView.setOnClickListener(this);

        deleteOwnTripRelativeLayout = (RelativeLayout) findViewById(R.id.delete_action_relativeLayout);
        deleteOwnTripRelativeLayout.setOnClickListener(this);

        Bundle arguments = getIntent().getExtras();

        currentTrip = new Trip();
        currentTrip.setTripId(arguments.getString("tripId", ""));

        City cityFrom = new City();
        cityFrom.setName(arguments.getString("cityFromName", ""));
        City cityTo = new City();
        cityTo.setName(arguments.getString("cityToName", ""));


        currentTrip.setCityFrom(cityFrom);
        currentTrip.setCityTo(cityTo);
        currentTrip.setType(arguments.getInt("tripType", -1));
        currentTrip.setDepartureDate(arguments.getString("tripDepartureDatetime"));
        currentTrip.setReturnDate(arguments.getString("tripReturnDatetime"));
        currentTrip.setPriceSmall(arguments.getString("tripPriceSmall", "0"));
        currentTrip.setPriceMedium(arguments.getString("tripPriceMedium", "0"));
        currentTrip.setPriceBig(arguments.getString("tripPriceBig", "0"));
        currentTrip.setPriceExtraBig(arguments.getString("tripPriceExtraBig", "0"));
        currentTrip.setDescription(arguments.getString("tripDescription", ""));

        /** FROM TO AUTOCOMPLETE **/
        flipView = (LinearLayout) findViewById(R.id.flip_view);
        flipView.setOnClickListener(this);

        flipArrows = (ImageView) findViewById(R.id.flip_arrows_imageView);

        autoCompViewFrom = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewFrom);
        autoCompViewFrom.setText(currentTrip.getCityFrom().getName());
        PlacesAutoCompleteAdapter adapterFrom = new PlacesAutoCompleteAdapter(this, R.layout.maps_citie);
        adapterFrom.setType(PlacesAutoCompleteAdapter.TYPE_FROM);
        autoCompViewFrom.setAdapter(adapterFrom);
        autoCompViewFrom.setOnItemClickListener(this);
        autoCompViewFrom.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /**if (!cityFromToAlreadySet) {
                 Log.d(TAG, "DISABLING NEXT BUTTON");
                 publishNextTextView.setEnabled(false);
                 publishNextTextView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                 } **/

                if (checkInternetConnection()) {
                    PlacesTask placesTask = new PlacesTask(EditOwnSimpleTripActivity.this);
                    placesTask.execute(s.toString());
                } else {
                    showNoInternetConnectionError();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        autoCompViewTo = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewTo);
        autoCompViewTo.setText(currentTrip.getCityTo().getName());
        PlacesAutoCompleteAdapter adapterTo = new PlacesAutoCompleteAdapter(this, R.layout.maps_citie);
        adapterTo.setType(PlacesAutoCompleteAdapter.TYPE_TO);
        autoCompViewTo.setAdapter(adapterTo);
        autoCompViewTo.setOnItemClickListener(this);
        autoCompViewTo.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /**if (!cityFromToAlreadySet) {
                 Log.d(TAG, "DISABLING NEXT BUTTON");
                 publishNextTextView.setEnabled(false);
                 publishNextTextView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                 }**/

                if (checkInternetConnection()) {
                    PlacesTask placesTask = new PlacesTask(EditOwnSimpleTripActivity.this);
                    placesTask.execute(s.toString());
                } else {
                    showNoInternetConnectionError();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        /** DATES AND TIMES GO AND BACK **/
        dateGoCardView = (CardView) findViewById(R.id.date_go_cardView);
        dateGoCardView.setOnClickListener(this);
        dateGoTextView = (TextView) findViewById(R.id.trip_go_date_select_textView);

        timeGoCardView = (CardView) findViewById(R.id.go_time_selection_cardView);
        timeGoCardView.setOnClickListener(this);
        timeGoTextView = (TextView) findViewById(R.id.trip_go_time_select_textView);

        goTextTextView = (TextView) findViewById(R.id.go_trip_from_to_textView);

        backDateAndTimeRelativeLayout = (RelativeLayout) findViewById(R.id.back_date_and_time_relativeLayout);

        dateBackCardView = (CardView) findViewById(R.id.date_back_cardView);
        dateBackCardView.setOnClickListener(this);
        dateBackTextView = (TextView) findViewById(R.id.trip_back_date_select_textView);

        timeBackCardView = (CardView) findViewById(R.id.back_time_selection_cardView);
        timeBackCardView.setOnClickListener(this);
        timeBackTextView = (TextView) findViewById(R.id.trip_back_time_select_textView);

        backTextTextView = (TextView) findViewById(R.id.back_trip_from_to_textView);

        goTripFromToTextView = (TextView) findViewById(R.id.go_trip_from_to_textView);
        backTripFromToTextView = (TextView) findViewById(R.id.back_trip_from_to_textView);

        if (currentTrip.getType() == 0) { //GO TRIP
            isGoAndBackTrip = false;
            backDateAndTimeRelativeLayout.setVisibility(View.GONE);

            goTextTextView.setText(currentTrip.getCityFrom().getName() + " - " + currentTrip.getCityTo().getName());

            Log.d("DEPARTURE DATE", currentTrip.getDepartureDateAndroid());
            goDate = MySimpleDateFormat.parseAndroidDate(currentTrip.getDepartureDateAndroid());
            dateGoTextView.setText(MySimpleDateFormat.formatAndroidOnlyDate(goDate));
            timeGoTextView.setText(String.format("%02d:%02d", goDate.getHours(), goDate.getMinutes()));

        } else if (currentTrip.getType() == 1) { //GO AND BACK TRIP
            isGoAndBackTrip = true;

            goTextTextView.setText(currentTrip.getCityFrom().getName() + " - " + currentTrip.getCityTo().getName());
            backTextTextView.setText(currentTrip.getCityTo().getName() + " - " + currentTrip.getCityFrom().getName());

            goDate = MySimpleDateFormat.parseAndroidDate(currentTrip.getDepartureDateAndroid());
            dateGoTextView.setText(MySimpleDateFormat.formatAndroidOnlyDate(goDate));
            timeGoTextView.setText(String.format("%02d:%02d", goDate.getHours(), goDate.getMinutes()));

            backDate = MySimpleDateFormat.parseAndroidDate(currentTrip.getReturnDateAndroid());
            dateBackTextView.setText(MySimpleDateFormat.formatAndroidOnlyDate(backDate));
            timeBackTextView.setText(String.format("%02d:%02d", backDate.getHours(), backDate.getMinutes()));

        }

        /** PRICE AND DESCRIPTION **/
        priceSmall = Integer.parseInt(currentTrip.getPriceSmall());
        priceMedium = Integer.parseInt(currentTrip.getPriceMedium());
        priceBig = Integer.parseInt(currentTrip.getPriceBig());
        priceExtraBig = Integer.parseInt(currentTrip.getPriceExtraBig());

        smallPriceDiscreteSeekBar = (DiscreteSeekBar) findViewById(R.id.price_small_discreteSeekBar);
        smallPriceDiscreteSeekBar.setOnProgressChangeListener(this);
        smallPriceDiscreteSeekBar.setProgress(priceSmall);

        mediumPriceDiscreteSeekBar = (DiscreteSeekBar) findViewById(R.id.price_medium_discreteSeekBar);
        mediumPriceDiscreteSeekBar.setOnProgressChangeListener(this);
        mediumPriceDiscreteSeekBar.setProgress(priceMedium);

        bigPriceDiscreteSeekBar = (DiscreteSeekBar) findViewById(R.id.price_big_discreteSeekBar);
        bigPriceDiscreteSeekBar.setOnProgressChangeListener(this);
        bigPriceDiscreteSeekBar.setProgress(priceBig);

        extraBigPriceDiscreteSeekBar = (DiscreteSeekBar) findViewById(R.id.price_extra_big_discreteSeekBar);
        extraBigPriceDiscreteSeekBar.setOnProgressChangeListener(this);
        extraBigPriceDiscreteSeekBar.setProgress(priceExtraBig);

        smallPriceTextView = (TextView) findViewById(R.id.price_small_textView);
        smallPriceTextView.setText(getString(R.string.small) + " " + smallPriceDiscreteSeekBar.getProgress() + "€");

        mediumPriceTextView = (TextView) findViewById(R.id.price_medium_textView);
        mediumPriceTextView.setText(getString(R.string.medium) + " " + mediumPriceDiscreteSeekBar.getProgress() + "€");

        bigPriceTextView = (TextView) findViewById(R.id.price_big_textView);
        bigPriceTextView.setText(getString(R.string.big) + " " + bigPriceDiscreteSeekBar.getProgress() + "€");

        extraBigPriceTextView = (TextView) findViewById(R.id.price_extra_big_textView);
        extraBigPriceTextView.setText(getString(R.string.extra_big) + " " + extraBigPriceDiscreteSeekBar.getProgress() + "€");

        description = currentTrip.getDescription();
        descriptionEditText = (EditText) findViewById(R.id.comment_editText);
        descriptionEditText.setText(description);

        saveEditOwnTripTextView = (TextView) findViewById(R.id.save_edit_own_trip_textView);
        saveEditOwnTripTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_edit_own_trip_textView:
                saveOwnTrip();
                break;
            case R.id.drawer_indicator:
                this.onBackPressed();
                break;
            case R.id.delete_action_relativeLayout:
                showDeleteAlertDialog();
                break;
            case R.id.flip_view:
                animateFlipView();
                flipFromTo();
                break;
            case R.id.date_go_cardView:
                showGoDatePickerDialog();
                break;
            case R.id.date_back_cardView:
                showBackDatePickerDialog();
                break;
            case R.id.go_time_selection_cardView:
                TimePickerDialog mTimePickerGo;
                mTimePickerGo = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        timeGoTextView.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                        goDate.setHours(selectedHour);
                        goDate.setMinutes(selectedMinute);
                        currentTrip.setDepartureDate(goDate.toString());
                    }
                }, goDate.getHours(), goDate.getMinutes(), true);//Yes 24 hour time
                mTimePickerGo.setTitle(getString(R.string.time));
                mTimePickerGo.show();
                break;
            case R.id.back_time_selection_cardView:
                TimePickerDialog mTimePickerBack;
                mTimePickerBack = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        timeBackTextView.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                        backDate.setHours(selectedHour);
                        backDate.setMinutes(selectedMinute);
                        currentTrip.setReturnDate(backDate.toString());
                        Log.d("BACK DATE", backDate.toString() + "");
                        Log.d("CURRENT TRIP BACK DATE", currentTrip.getReturnDateAndroid()+"");
                    }
                }, backDate.getHours(), backDate.getMinutes(), true);//Yes 24 hour time
                mTimePickerBack.setTitle(getString(R.string.time));
                mTimePickerBack.show();
                break;
        }
    }

    private void saveOwnTrip() {
        if (checkInternetConnection()) {

            String[] form = null;
            switch (currentTrip.getType()) {
                case PublishTripTask.SIMPLE_GO_TRIP:
                    form = setSimpleGoTripForm();
                    break;
                case PublishTripTask.SIMPLE_GO_AND_BACK_TRIP:
                    form = setSimpleGoAndBackTripForm();
                    break;
            }

            for (int i = 0; i < form.length; i++) {
                //Log.d("FORM " + i, form[i]);
            }
            //asyncProgressBar.setVisibility(View.VISIBLE);
            //asyncProgressBar.setProgress(50);
            saveEditOwnTripTextView.setEnabled(false);
            EditOwnTripTask editOwnTripTask = new EditOwnTripTask(this);
            editOwnTripTask.execute(form);
        } else {
            showNoInternetConnectionError();
        }

    }

    private String[] setSimpleGoAndBackTripForm() {
        String[] form = new String[11];
        form[0] = currentTrip.getTripId();
        form[1] = currentTrip.getType() + "";
        form[2] = currentTrip.getCityFrom().getName();
        form[3] = currentTrip.getCityTo().getName();
        form[4] = smallPriceDiscreteSeekBar.getProgress() + ""; //price 1
        form[5] = mediumPriceDiscreteSeekBar.getProgress() + ""; //price 2
        form[6] = bigPriceDiscreteSeekBar.getProgress() + ""; //price 3
        form[7] = extraBigPriceDiscreteSeekBar.getProgress() + ""; //price 4
        form[8] = currentTrip.getDepartureDateAndroid(); //Go date
        form[9] = currentTrip.getReturnDateAndroid(); //Back date
        form[10] = descriptionEditText.getText().toString(); //Description

        for(int i=0; i<form.length; i++) {
            Log.d("i=" + i, form[i] + "");
        }
        return form;
    }

    private String[] setSimpleGoTripForm() {
        String[] form = new String[10];
        form[0] = currentTrip.getTripId();
        form[1] = currentTrip.getType() + "";
        form[2] = currentTrip.getCityFrom().getName();
        form[3] = currentTrip.getCityTo().getName();
        form[4] = smallPriceDiscreteSeekBar.getProgress() + ""; //price 1
        form[5] = mediumPriceDiscreteSeekBar.getProgress() + ""; //price 2
        form[6] = bigPriceDiscreteSeekBar.getProgress() + ""; //price 3
        form[7] = extraBigPriceDiscreteSeekBar.getProgress() + ""; //price 4
        //form[8] = currentTrip.getDepartureDateMyFormatDateTime(); //Go date
        form[8] = currentTrip.getDepartureDateAndroid(); //Go date
        form[9] = descriptionEditText.getText().toString(); //Description

        for(int i=0; i<form.length; i++) {
            Log.d("i=" + i, form[i] + "");
        }
        return form;
    }

    private void showDeleteAlertDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.delete_trip)
                .content(R.string.confirm_delete_trip)
                .positiveText(R.string.delete)
                .negativeText(R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                    }

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        String [] form = {currentTrip.getTripId()};
                        DeleteOwnTripTask deleteOwnTripTask = new DeleteOwnTripTask(EditOwnSimpleTripActivity.this);
                        deleteOwnTripTask.execute(form);
                    }
                }).build();

        dialog.show();
    }

    private void showGoDatePickerDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.date_selection)
                .customView(R.layout.dialog_date_selection, true)
                .positiveText(R.string.accept)
                .negativeText(R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        Date dateSelected = calendarGoDialogPickerView.getSelectedDate();
                        Log.d("DATE SELECTED", dateSelected.toString());

                        goDate = dateSelected;

                        //String dateTime = MySimpleDateFormat.formatDateTime(dateSelected);
                        //Log.d("DATETIME", dateTime);
                        //goDate = MySimpleDateFormat.parseDateTime(dateTime);
                        //Log.d("GO DATE", goDate.toString());

                        currentTrip.setDepartureDate(goDate.toString());
                        dateGoTextView.setText(MySimpleDateFormat.formatAndroidOnlyDate(goDate));
                        Log.d("DATE TO SHOW", MySimpleDateFormat.formatAndroidOnlyDate(goDate));
                        if (isGoAndBackTrip) {
                            if (goDate.compareTo(backDate) > 0) {
                                backDate = goDate;
                                currentTrip.setReturnDate(backDate.toString());
                                dateBackTextView.setText(MySimpleDateFormat.formatAndroidOnlyDate(backDate));
                            }
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {

                    }
                }).build();

        calendarGoDialogPickerView = (CalendarPickerView) dialog.getCustomView().findViewById(R.id.calendar_view);

        Date today = new Date();
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        if (goDate != null) {
            calendarGoDialogPickerView.init(today, nextYear.getTime())
                    .inMode(CalendarPickerView.SelectionMode.SINGLE).withSelectedDate(goDate);
        }
        calendarGoDialogPickerView.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
            }

            @Override
            public void onDateUnselected(Date date) {
            }
        });

        dialog.show();
    }

    private void showBackDatePickerDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.date_selection)
                .customView(R.layout.dialog_date_selection, true)
                .positiveText(R.string.accept)
                .negativeText(R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        Date dateSelected = calendarGoDialogPickerView.getSelectedDate();

                        //String dateTime = MySimpleDateFormat.formatDateTime(dateSelected);
                        backDate = dateSelected;

                        currentTrip.setReturnDate(backDate.toString());
                        dateBackTextView.setText(MySimpleDateFormat.formatAndroidOnlyDate(backDate));
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {

                    }
                }).build();

        calendarGoDialogPickerView = (CalendarPickerView) dialog.getCustomView().findViewById(R.id.calendar_view);

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        if (backDate != null) {
            calendarGoDialogPickerView.init(goDate, nextYear.getTime())
                    .inMode(CalendarPickerView.SelectionMode.SINGLE).withSelectedDate(backDate);
        }
        calendarGoDialogPickerView.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
            }

            @Override
            public void onDateUnselected(Date date) {
            }
        });

        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void animateFlipView() {
        RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.rotate_180);
        flipArrows.startAnimation(rotate);//setAnimation(rotate);
    }

    private void flipFromTo() {
        if (!autoCompViewFrom.getText().toString().isEmpty() && !autoCompViewTo.getText().toString().isEmpty()) {
            City cityAux = cityFrom;
            cityFrom = cityTo;
            cityTo = cityAux;

            String aux = autoCompViewFrom.getText().toString();
            autoCompViewFrom.setFocusable(false);
            autoCompViewTo.setFocusable(false);

            autoCompViewFrom.setEnabled(false);
            autoCompViewTo.setEnabled(false);

            autoCompViewFrom.setText(autoCompViewTo.getText().toString() + "");
            autoCompViewTo.setText(aux + "");

            autoCompViewFrom.setFocusable(true);
            autoCompViewTo.setFocusable(true);

            autoCompViewFrom.setFocusableInTouchMode(true);
            autoCompViewTo.setFocusableInTouchMode(true);

            autoCompViewFrom.setEnabled(true);
            autoCompViewTo.setEnabled(true);

            if (cityFrom != null && cityFrom.getLat() != 0 && cityTo != null && cityTo.getLat() != 0) {
                /**publishNextTextView.setEnabled(true);
                 publishNextTextView.setBackgroundColor(getResources().getColor(R.color.shipeer_kazan));**/
            }
        }
    }

    public boolean checkInternetConnection() {
        ConnectivityManager conMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
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
        Toast.makeText(this, getString(R.string.no_internet_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.zoom_in, R.anim.slide_in_left_to_right);
    }

    @Override
    public void onProgressChanged(DiscreteSeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.price_small_discreteSeekBar:
                if (smallPriceTextView != null)
                    smallPriceTextView.setText(getString(R.string.small) + " " + progress + "€");
                break;
            case R.id.price_medium_discreteSeekBar:
                if (mediumPriceTextView != null)
                    mediumPriceTextView.setText(getString(R.string.medium) + " " + progress + "€");
                break;
            case R.id.price_big_discreteSeekBar:
                if (bigPriceTextView != null)
                    bigPriceTextView.setText(getString(R.string.big) + " " + progress + "€");
                break;
            case R.id.price_extra_big_discreteSeekBar:
                if (extraBigPriceTextView != null)
                    extraBigPriceTextView.setText(getString(R.string.extra_big) + " " + progress + "€");
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (cities != null && cities.size() > position && cities.get(position) != null) {
            Log.d("onItemClick", position + ", " + cities.get(position));
            PlacesAutoCompleteAdapter adapter = (PlacesAutoCompleteAdapter) parent.getAdapter();
            if (adapter != null && adapter.getType() != 0) {
                Log.d("onItemClick", adapter.getType() + "");

                if (adapter.getType() == PlacesAutoCompleteAdapter.TYPE_FROM) {
                    if (cityFrom == null || !cityFrom.getName().equalsIgnoreCase(cities.get(position).getName())) {
                        cityFrom = new City(cities.get(position).getName(), cities.get(position).getPlaceId());
                        currentTrip.setCityFrom(cityFrom);
                    }
                } else if (adapter.getType() == PlacesAutoCompleteAdapter.TYPE_TO) {
                    if (cityTo == null || !cityTo.getName().equalsIgnoreCase(cities.get(position).getName())) {
                        cityTo = new City(cities.get(position).getName(), cities.get(position).getPlaceId());
                        currentTrip.setCityFrom(cityTo);
                    }
                }
            }
        }
    }

    @Override
    public void onPlacesTaskCompleted(ArrayList<City> result) {
        cities = result;
    }

    @Override
    public void onEditOwnTripTaskCompleted(String[] result) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", result);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onDeleteOwnTripTaskCompleted(String[] result) {
        if(result == null) Toast.makeText(this, getString(R.string.error_delete_trip), Toast.LENGTH_SHORT).show();
        else {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", result);
            setResult(RESULT_OK, returnIntent);
            finish();
        }
    }

    /**
     * List adapter to show cities from Google maps.
     */
    private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        public static final int TYPE_FROM = 1;
        public static final int TYPE_TO = 2;
        private int type;

        private ArrayList<City> resultList;

        public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        @Override
        public int getCount() {
            if (resultList != null) {
                return resultList.size();
            } else {
                resultList = new ArrayList<>();
                return 0;
            }
        }

        @Override
        public String getItem(int index) {
            if (index < resultList.size()) return resultList.get(index).getName();
            else return "";
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    ArrayList<City> queryResults;
                    if (constraint != null || constraint.length() == 0) {
                        queryResults = PlacesTask.autocomplete(constraint.toString());
                    } else {
                        queryResults = new ArrayList<City>(); // empty list/no suggestions showing if there's no valid constraint
                    }
                    filterResults.values = queryResults;
                    filterResults.count = queryResults.size();

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    // update the data with the new set of suggestions
                    resultList = (ArrayList<City>) results.values;
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    public void sendAnalyticsView() {
        // Get tracker.
        Tracker t = ((GlobalState) getApplication()).getTracker(
                GlobalState.TrackerName.APP_TRACKER);

        // Set screen name.
        // Where path is a String representing the screen name.
        t.setScreenName(VIEW_NAME);

        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());
    }

}
