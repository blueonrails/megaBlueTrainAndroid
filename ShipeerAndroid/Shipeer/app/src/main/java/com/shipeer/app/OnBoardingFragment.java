package com.shipeer.app;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class OnBoardingFragment extends Fragment {

    private int position;

    private TextView titleTextView;
    private TextView textTextView;

    private TextView nextTextView;

    public OnBoardingFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public OnBoardingFragment(int position) {
        this.position = position;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_on_boarding, container, false);

        titleTextView = (TextView) view.findViewById(R.id.on_boarding_title_textView);
        textTextView = (TextView) view.findViewById(R.id.on_boarding_text_textView);

        nextTextView = (TextView) view.findViewById(R.id.on_boarding_next_textView);

        switch (position) {
            case 1:
                titleTextView.setText(getString(R.string.publish_your_trip));
                textTextView.setText(getString(R.string.on_boarding_1));
                break;
            case 2:
                titleTextView.setText(getString(R.string.save_money_sending));
                textTextView.setText(getString(R.string.on_boarding_2));
                break;
            case 3:
                titleTextView.setText(getString(R.string.search_a_driver));
                textTextView.setText(getString(R.string.on_boarding_2));
                break;
        }
        return view;
    }


}
