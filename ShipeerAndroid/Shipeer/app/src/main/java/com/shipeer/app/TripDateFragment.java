package com.shipeer.app;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.timessquare.CalendarPickerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import model.MySimpleDateFormat;


/**
 * A simple {@link Fragment} subclass.
 */
public class TripDateFragment extends Fragment implements View.OnClickListener {

    //Google Analytics stuff
    private static final String VIEW_NAME = "Publish Trip Date (2/3)";

    private static TextView timeGoTextView;
    private static TextView dateGoTextView;
    private TextView goTextTextView;
    private CardView dateGoCardView;
    private CardView timeGoCardView;

    private static TextView timeBackTextView;
    private static TextView dateBackTextView;
    private TextView backTextTextView;
    private CardView dateBackCardView;
    private CardView timeBackCardView;

    private TextView publishNextTextView;

    //private CalendarPickerView calendarPickerView;
    private CalendarPickerView calendarGoDialogPickerView;

    //private ArrayList<Date> datesSelected;
    private static Date goDate;
    private static Date backDate;

    private TextView goTripFromToTextView;
    private TextView backTripFromToTextView;

    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    public TripDateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendAnalyticsView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trip_date, container, false);

        dateGoCardView = (CardView) view.findViewById(R.id.date_go_cardView);
        dateGoCardView.setOnClickListener(this);
        dateGoTextView = (TextView) view.findViewById(R.id.trip_go_date_select_textView);

        timeGoCardView = (CardView) view.findViewById(R.id.go_time_selection_cardView);
        timeGoCardView.setOnClickListener(this);
        timeGoTextView = (TextView) view.findViewById(R.id.trip_go_time_select_textView);

        goTextTextView = (TextView) view.findViewById(R.id.go_text_textView);

        dateBackCardView = (CardView) view.findViewById(R.id.date_back_cardView);
        dateBackCardView.setOnClickListener(this);
        dateBackTextView = (TextView) view.findViewById(R.id.trip_back_date_select_textView);

        timeBackCardView = (CardView) view.findViewById(R.id.back_time_selection_cardView);
        timeBackCardView.setOnClickListener(this);
        timeBackTextView = (TextView) view.findViewById(R.id.trip_back_time_select_textView);

        backTextTextView = (TextView) view.findViewById(R.id.back_text_textView);

        goTripFromToTextView = (TextView) view.findViewById(R.id.go_trip_from_to_textView);
        backTripFromToTextView = (TextView) view.findViewById(R.id.back_trip_from_to_textView);

        publishNextTextView = (TextView) view.findViewById(R.id.publishNext);
        publishNextTextView.setOnClickListener(this);
        publishNextTextView.setBackgroundColor(getResources().getColor(R.color.shipeer_kazan));
        publishNextTextView.setEnabled(true);

        goTripFromToTextView.setText(PublishTripActivity.getCityFrom().getName() + " - " + PublishTripActivity.getCityTo().getName());

        if (PublishTripActivity.getGoDate() != null) {
            //datesSelected = PublishTripActivity.getDates();

            goDate = PublishTripActivity.getGoDate();
            dateGoTextView.setText(MySimpleDateFormat.formatAndroidOnlyDate(goDate));
            timeGoTextView.setText(String.format("%02d:%02d", goDate.getHours(), goDate.getMinutes()));
        } else {
            //datesSelected = new ArrayList<Date>();

            Date today = new Date();
            //datesSelected.add(today);
            goDate = today;
            goDate.setHours(goDate.getHours()+1);
            goDate.setMinutes(0);
            PublishTripActivity.setGoDate(goDate);

            dateGoTextView.setText(MySimpleDateFormat.formatAndroidOnlyDate(goDate));
            timeGoTextView.setText(String.format("%02d:%02d", goDate.getHours(), goDate.getMinutes()));
        }


        if (PublishTripActivity.isGoAndBackTrip()) { //GO AND BACK TRIP
            backTripFromToTextView.setText(PublishTripActivity.getCityTo().getName() + " - " + PublishTripActivity.getCityFrom().getName());
            if (PublishTripActivity.getBackDate() != null) {
                backDate = PublishTripActivity.getBackDate();
                dateBackTextView.setText(MySimpleDateFormat.formatAndroidOnlyDate(backDate));
                timeBackTextView.setText(String.format("%02d:%02d", backDate.getHours(), backDate.getMinutes()));
            } else {
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE, +1);
                Date tomorrow = c.getTime();
                backDate = tomorrow;
                backDate.setHours(backDate.getHours()+1);
                backDate.setMinutes(0);
                PublishTripActivity.setBackDate(backDate);

                dateBackTextView.setText(MySimpleDateFormat.formatAndroidOnlyDate(backDate));
                timeBackTextView.setText(String.format("%02d:%02d", backDate.getHours(), backDate.getMinutes()));
            }
        } else {
            dateBackCardView.setVisibility(View.GONE);
            timeBackCardView.setVisibility(View.GONE);
            backTripFromToTextView.setVisibility(View.GONE);
            backTextTextView.setVisibility(View.GONE);
        }

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.publishNext:
                /**if (!PublishTripActivity.isGoAndBackTrip()) { //JUST GO TRIP
                    if (goDate == null) return;
                    else {
                        PublishTripActivity.setGoDate(goDate);
                    }
                } else { //GO AND BACK TRIP
                    if (goDate == null || backDate == null) return;
                    else {
                        PublishTripActivity.setGoDate(goDate);
                        PublishTripActivity.setBackDate(backDate);
                    }
                }**/

                //PublishTripActivity.setTime(timeGoTextView.getText().toString());
                TripPriceCommentFragment tripPriceCommentFragment = new TripPriceCommentFragment();
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right_to_left, R.anim.slide_out_left, R.anim.slide_in_left_to_right, R.anim.slide_out_right)
                        .replace(R.id.publish_fragment_container, tripPriceCommentFragment)
                        .addToBackStack(null)
                        .commit();

                break;
            case R.id.date_go_cardView:
                showGoDatePickerDialog();
                break;
            case R.id.date_back_cardView:
                showBackDatePickerDialog();
                break;
            case R.id.go_time_selection_cardView:
                TimePickerDialog mTimePickerGo;
                mTimePickerGo = new TimePickerDialog(this.getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        timeGoTextView.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                        goDate.setHours(selectedHour);
                        goDate.setMinutes(selectedMinute);
                        PublishTripActivity.setGoDate(goDate);
                    }
                }, goDate.getHours(), goDate.getMinutes(), true);//Yes 24 hour time
                mTimePickerGo.setTitle(getString(R.string.time));
                mTimePickerGo.show();
                break;
            case R.id.back_time_selection_cardView:
                TimePickerDialog mTimePickerBack;
                mTimePickerBack = new TimePickerDialog(this.getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        timeBackTextView.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                        backDate.setHours(selectedHour);
                        backDate.setMinutes(selectedMinute);
                        PublishTripActivity.setBackDate(backDate);
                        Log.d("BACK DATE", backDate.toString() + "");
                        //Log.d("CURRENT TRIP BACK DATE", currentTrip.getReturnDateAndroid()+"");
                    }
                }, backDate.getHours(), backDate.getMinutes(), true);//Yes 24 hour time
                mTimePickerBack.setTitle(getString(R.string.time));
                mTimePickerBack.show();
                break;
        }
    }

    private void showGoDatePickerDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this.getActivity())
                .title(R.string.date_selection)
                .customView(R.layout.dialog_date_selection, true)
                .positiveText(R.string.accept)
                .negativeText(R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        goDate = calendarGoDialogPickerView.getSelectedDate();
                        PublishTripActivity.setGoDate(goDate);

                        dateGoTextView.setText(MySimpleDateFormat.formatAndroidOnlyDate(goDate));

                        if(PublishTripActivity.isGoAndBackTrip()) {
                            if (goDate.compareTo(backDate) > 0) {
                                backDate = goDate;
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
        MaterialDialog dialog = new MaterialDialog.Builder(this.getActivity())
                .title(R.string.date_selection)
                .customView(R.layout.dialog_date_selection, true)
                .positiveText(R.string.accept)
                .negativeText(R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        backDate = calendarGoDialogPickerView.getSelectedDate();
                        PublishTripActivity.setBackDate(backDate);

                        dateBackTextView.setText(MySimpleDateFormat.formatAndroidOnlyDate(backDate));
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {

                    }
                }).build();

        calendarGoDialogPickerView = (CalendarPickerView) dialog.getCustomView().findViewById(R.id.calendar_view);

        Date today = new Date();
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

    /**public static class TimePickerFragment extends DialogFragment
     implements TimePickerDialog.OnTimeSetListener {

     private int type;

     public TimePickerFragment() {}

     @SuppressLint("ValidFragment")
     public TimePickerFragment(int type) {
     this.type = type;
     }

     @Override
     public Dialog onCreateDialog(Bundle savedInstanceState) {
     // Use the current time as the default values for the picker
     final Calendar c = Calendar.getInstance();
     int hour = c.get(Calendar.HOUR_OF_DAY);

     // Create a new instance of TimePickerDialog and return it
     return new TimePickerDialog(getActivity(), this, (hour + 1), 0,
     DateFormat.is24HourFormat(getActivity()));
     }

     public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
     if(type == 1) {
     timeGoTextView.setText(String.format("%02d:%02d", hourOfDay, minute));
     goDate = PublishTripActivity.getGoDate();
     goDate.setHours(hourOfDay);
     goDate.setMinutes(minute);
     PublishTripActivity.setGoDate(goDate);
     //PublishTripActivity.setTime(String.format("%02d:%02d", hourOfDay, minute));
     }
     else if(type == 2) {
     timeBackTextView.setText(String.format("%02d:%02d", hourOfDay, minute));
     backDate = PublishTripActivity.getBackDate();
     backDate.setHours(hourOfDay);
     backDate.setMinutes(minute);
     PublishTripActivity.setBackDate(backDate);
     //PublishTripActivity.setTime(String.format("%02d:%02d", hourOfDay, minute));
     }
     }
     }**/
}
