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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.timessquare.CalendarPickerView;

import java.util.Calendar;
import java.util.Date;

import model.MySimpleDateFormat;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class TripRecurrentDateFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    //Google Analytics stuff
    private static final String VIEW_NAME = "Publish Trip Recurrent Date (2/3)";

    private RelativeLayout recurrentTripBackRelativeLayout;

    private ToggleButton goMondayButton;
    private ToggleButton goTuesdayButton;
    private ToggleButton goWednesdayButton;
    private ToggleButton goThursdayButton;
    private ToggleButton goFridayButton;
    private ToggleButton goSaturdayButton;
    private ToggleButton goSundayButton;

    private ToggleButton backMondayButton;
    private ToggleButton backTuesdayButton;
    private ToggleButton backWednesdayButton;
    private ToggleButton backThursdayButton;
    private ToggleButton backFridayButton;
    private ToggleButton backSaturdayButton;
    private ToggleButton backSundayButton;

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

    private boolean[] goDays = new boolean[7];
    private boolean[] backDays = new boolean[7];


    private Date goDate;
    private Date backDate;

    private CalendarPickerView calendarGoDialogPickerView;

    public TripRecurrentDateFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trip_recurrent_date, container, false);

        goMondayButton = (ToggleButton) view.findViewById(R.id.monday_go_button);
        goTuesdayButton = (ToggleButton) view.findViewById(R.id.tuesday_go_button);
        goWednesdayButton = (ToggleButton) view.findViewById(R.id.wednesday_go_button);
        goThursdayButton = (ToggleButton) view.findViewById(R.id.thursday_go_button);
        goFridayButton = (ToggleButton) view.findViewById(R.id.friday_go_button);
        goSaturdayButton = (ToggleButton) view.findViewById(R.id.saturday_go_button);
        goSundayButton = (ToggleButton) view.findViewById(R.id.sunday_go_button);

        goMondayButton.setOnCheckedChangeListener(this);
        goTuesdayButton.setOnCheckedChangeListener(this);
        goWednesdayButton.setOnCheckedChangeListener(this);
        goThursdayButton.setOnCheckedChangeListener(this);
        goFridayButton.setOnCheckedChangeListener(this);
        goSaturdayButton.setOnCheckedChangeListener(this);
        goSundayButton.setOnCheckedChangeListener(this);

        recurrentTripBackRelativeLayout = (RelativeLayout) view.findViewById(R.id.recurrent_trip_back_relativeLayout);

        backMondayButton = (ToggleButton) view.findViewById(R.id.monday_back_button);
        backTuesdayButton = (ToggleButton) view.findViewById(R.id.tuesday_back_button);
        backWednesdayButton = (ToggleButton) view.findViewById(R.id.wednesday_back_button);
        backThursdayButton = (ToggleButton) view.findViewById(R.id.thursday_back_button);
        backFridayButton = (ToggleButton) view.findViewById(R.id.friday_back_button);
        backSaturdayButton = (ToggleButton) view.findViewById(R.id.saturday_back_button);
        backSundayButton = (ToggleButton) view.findViewById(R.id.sunday_back_button);

        if (PublishTripActivity.isGoAndBackTrip()) {
            backMondayButton.setOnCheckedChangeListener(this);
            backTuesdayButton.setOnCheckedChangeListener(this);
            backWednesdayButton.setOnCheckedChangeListener(this);
            backThursdayButton.setOnCheckedChangeListener(this);
            backFridayButton.setOnCheckedChangeListener(this);
            backSaturdayButton.setOnCheckedChangeListener(this);
            backSundayButton.setOnCheckedChangeListener(this);
        } else {
            recurrentTripBackRelativeLayout.setVisibility(View.GONE);
            backMondayButton.setVisibility(View.GONE);
            backTuesdayButton.setVisibility(View.GONE);
            backWednesdayButton.setVisibility(View.GONE);
            backThursdayButton.setVisibility(View.GONE);
            backFridayButton.setVisibility(View.GONE);
            backSaturdayButton.setVisibility(View.GONE);
            backSundayButton.setVisibility(View.GONE);
        }

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
            PublishTripActivity.setGoDate(today);

            dateGoTextView.setText(MySimpleDateFormat.formatAndroidOnlyDate(goDate));
            timeGoTextView.setText(String.format("%02d:%02d", goDate.getHours()+1, 0));
        }

        if (PublishTripActivity.getBackDate() != null) {
            backDate = PublishTripActivity.getBackDate();
            if(goDate.compareTo(backDate) > 0) {
                backDate = goDate;
            }
            dateBackTextView.setText(MySimpleDateFormat.formatAndroidOnlyDate(backDate));
            timeBackTextView.setText(String.format("%02d:%02d", backDate.getHours(), backDate.getMinutes()));
        } else {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, +1);
            Date tomorrow = c.getTime();
            backDate = tomorrow;
            PublishTripActivity.setBackDate(tomorrow);

            dateBackTextView.setText(MySimpleDateFormat.formatAndroidOnlyDate(backDate));
            timeBackTextView.setText(String.format("%02d:%02d", backDate.getHours()+1, 0));
        }

        publishNextTextView = (TextView) view.findViewById(R.id.publishNext);
        publishNextTextView.setOnClickListener(this);
        publishNextTextView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        publishNextTextView.setEnabled(false);

        return view;
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        buttonView.setChecked(isChecked);
        switch (buttonView.getId()) {
            case R.id.monday_go_button:
                goDays[0] = isChecked;
                break;
            case R.id.tuesday_go_button:
                goDays[1] = isChecked;
                break;
            case R.id.wednesday_go_button:
                goDays[2] = isChecked;
                break;
            case R.id.thursday_go_button:
                goDays[3] = isChecked;
                break;
            case R.id.friday_go_button:
                goDays[4] = isChecked;
                break;
            case R.id.saturday_go_button:
                goDays[5] = isChecked;
                break;
            case R.id.sunday_go_button:
                goDays[6] = isChecked;
                break;
            case R.id.monday_back_button:
                backDays[0] = isChecked;
                break;
            case R.id.tuesday_back_button:
                backDays[1] = isChecked;
                break;
            case R.id.wednesday_back_button:
                backDays[2] = isChecked;
                break;
            case R.id.thursday_back_button:
                backDays[3] = isChecked;
                break;
            case R.id.friday_back_button:
                backDays[4] = isChecked;
                break;
            case R.id.saturday_back_button:
                backDays[5] = isChecked;
                break;
            case R.id.sunday_back_button:
                backDays[6] = isChecked;
                break;
        }
        if (PublishTripActivity.isGoAndBackTrip()) {
            if (goDaysIsAllFalse() || backDaysIsAllFalse()) {
                publishNextTextView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                publishNextTextView.setEnabled(false);
            } else {
                publishNextTextView.setEnabled(true);
                publishNextTextView.setBackgroundColor(getResources().getColor(R.color.shipeer_kazan));
            }
        } else {
            if (goDaysIsAllFalse()) {
                publishNextTextView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                publishNextTextView.setEnabled(false);
            } else {
                publishNextTextView.setEnabled(true);
                publishNextTextView.setBackgroundColor(getResources().getColor(R.color.shipeer_kazan));
            }
        }
    }

    private boolean backDaysIsAllFalse() {
        for (int i = 0; i < backDays.length; i++) {
            if (backDays[i]) return false;
        }
        return true;
    }

    private boolean goDaysIsAllFalse() {
        for (int i = 0; i < goDays.length; i++) {
            if (goDays[i]) return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.publishNext:
                if (!PublishTripActivity.isGoAndBackTrip()) { //JUST GO TRIP
                    PublishTripActivity.setWeekGoDays(goDays);
                    if (goDate == null) return;
                    else {
                        PublishTripActivity.setGoDate(goDate);
                    }
                } else { //GO AND BACK TRIP
                    PublishTripActivity.setWeekGoDays(goDays);
                    PublishTripActivity.setWeekBackDays(backDays);
                    if (goDate == null || backDate == null) return;
                    else {
                        PublishTripActivity.setGoDate(goDate);
                        PublishTripActivity.setBackDate(backDate);
                    }
                }

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
                DialogFragment goFragment = new TimePickerFragment(1);
                goFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
                break;
            case R.id.back_time_selection_cardView:
                DialogFragment backFragment = new TimePickerFragment(2);
                backFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
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

                        if (goDate.compareTo(backDate) > 0) {
                            backDate = goDate;
                            dateBackTextView.setText(MySimpleDateFormat.formatAndroidOnlyDate(backDate));
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

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        private int type;

        public TimePickerFragment() {
        }

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
            String hour = hourOfDay + "";
            String min = minute + "";
            if (hourOfDay < 10) hour = "0" + hourOfDay;
            if (minute < 10) min = "0" + minute;
            if (type == 1) {
                timeGoTextView.setText(hour + ":" + min);
                Date goDate = PublishTripActivity.getGoDate();
                goDate.setHours(hourOfDay);
                goDate.setMinutes(minute);
                PublishTripActivity.setGoDate(goDate);
            } else if (type == 2) {
                timeBackTextView.setText(hour + ":" + min);
                Date backDate = PublishTripActivity.getBackDate();
                backDate.setHours(hourOfDay);
                backDate.setMinutes(minute);
                PublishTripActivity.setBackDate(backDate);
            }
        }
    }
}
