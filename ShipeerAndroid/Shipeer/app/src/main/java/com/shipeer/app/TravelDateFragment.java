package com.shipeer.app;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.squareup.timessquare.CalendarPickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class TravelDateFragment extends Fragment implements View.OnClickListener, CalendarPickerView.OnDateSelectedListener {

    private TextView actionBarTitleTextView;
    private ImageView backImageView;

    private TextView travelDateTextView;
    private static TextView travelTimeTextView;
    private CardView travelTimeCardView;

    private TextView publishNextTextView;

    private CalendarPickerView calendarPickerView;

    private String date;
    private ArrayList<Date> datesSelected;

    public TravelDateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_travel_date, container, false);

        actionBarTitleTextView = (TextView) view.findViewById(R.id.second_action_bar_title_textview);
        actionBarTitleTextView.setText(getString(R.string.publish_travel));

        backImageView = (ImageView) view.findViewById(R.id.drawer_indicator);
        backImageView.setOnClickListener(this);

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        calendarPickerView = (CalendarPickerView) view.findViewById(R.id.calendar_view);
        Date today = new Date();
        calendarPickerView.init(today, nextYear.getTime())
                .withSelectedDate(today).inMode(CalendarPickerView.SelectionMode.MULTIPLE);
        calendarPickerView.setOnDateSelectedListener(this);

        datesSelected = new ArrayList<Date>();

        travelDateTextView = (TextView) view.findViewById(R.id.travel_date_textView);
        travelTimeTextView = (TextView) view.findViewById(R.id.travel_date_select_textView);
        travelTimeCardView = (CardView) view.findViewById(R.id.card_view_profile_settings);
        travelTimeCardView.setOnClickListener(this);

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        travelTimeTextView.setText((hour+1) + ":00");

        publishNextTextView = (TextView) view.findViewById(R.id.publishNext);
        publishNextTextView.setOnClickListener(this);
        publishNextTextView.setBackgroundColor(getResources().getColor(R.color.shipeer));
        publishNextTextView.setEnabled(true);

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.drawer_indicator:
                this.getActivity().onBackPressed();
                break;
            case R.id.publishNext:
                if(date != null && !date.isEmpty()) {
                    PublishTravelActivity.setDates(datesSelected);
                    TravelPriceCommentFragment travelPriceCommentFragment = new TravelPriceCommentFragment();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.travel_date_fragment, travelPriceCommentFragment)
                            .addToBackStack(null)
                            .commit();
                }
                break;
            case R.id.card_view_profile_settings:
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
                break;
        }
    }

    @Override
    public void onDateSelected(Date dateSelected) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        date = sdf.format(dateSelected);

        datesSelected.add(dateSelected);
        publishNextTextView.setBackgroundColor(getResources().getColor(R.color.shipeer));
        publishNextTextView.setEnabled(true);
    }

    @Override
    public void onDateUnselected(Date dateUnselected) {
        datesSelected.remove(dateUnselected);

        if(datesSelected.size() <= 0) {
            publishNextTextView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            publishNextTextView.setEnabled(false);
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, 0,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String hour = hourOfDay + "";
            String min = minute + "";
            if(hourOfDay < 10) hour = "0" + hourOfDay;
            if(minute < 10) min = "0" + minute;
            travelTimeTextView.setText(hour + ":" + min);
        }
    }
}
