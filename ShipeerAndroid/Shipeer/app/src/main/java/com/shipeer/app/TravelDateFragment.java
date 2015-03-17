package com.shipeer.app;


import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;


/**
 * A simple {@link Fragment} subclass.
 */
public class TravelDateFragment extends Fragment implements View.OnClickListener, CalendarView.OnDateChangeListener {

    private TextView actionBarTitleTextView;
    private ImageView backImageView;

    private TextView travelDateTextView;
    private CalendarView calendarView;
    private TextView publishNextTextView;

    private String date;

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

        calendarView = (CalendarView) view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(this);

        travelDateTextView = (TextView) view.findViewById(R.id.travel_date_textView);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        travelDateTextView.setText(getString(R.string.date) + " " + sdf.format(calendarView.getDate()));
        date = sdf.format(calendarView.getDate());

        publishNextTextView = (TextView) view.findViewById(R.id.publishNext);
        publishNextTextView.setOnClickListener(this);
        publishNextTextView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        publishNextTextView.setEnabled(false);

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.drawer_indicator:
                this.getActivity().onBackPressed();
                break;
            case R.id.publishNext:
                //if(cityFrom != null && cityTo != null) {
                TravelPriceCommentFragment travelPriceCommentFragment = new TravelPriceCommentFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.travel_date_fragment, travelPriceCommentFragment)
                        .addToBackStack(null)
                        .commit();
                //}
                break;
        }
    }

    @Override
    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
        travelDateTextView.setText(getString(R.string.date) + " " + dayOfMonth + "/" + month + "/" + year);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        date = sdf.format(view.getDate());

        publishNextTextView.setBackgroundColor(getResources().getColor(R.color.shipeer));
        publishNextTextView.setEnabled(true);
    }
}
