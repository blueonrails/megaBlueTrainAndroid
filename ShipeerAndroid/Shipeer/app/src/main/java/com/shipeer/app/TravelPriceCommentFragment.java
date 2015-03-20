package com.shipeer.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by mifercre on 17/03/15.
 */
public class TravelPriceCommentFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private TextView actionBarTitleTextView;
    private ImageView backImageView;

    private SeekBar smallPriceSeekBar;
    private SeekBar mediumPriceSeekBar;
    private SeekBar bigPriceSeekBar;

    private TextView smallPriceTextView;
    private TextView mediumPriceTextView;
    private TextView bigPriceTextView;

    private TextView publishTravelTextView;

    public TravelPriceCommentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_travel_price_comment, container, false);

        actionBarTitleTextView = (TextView) view.findViewById(R.id.second_action_bar_title_textview);
        actionBarTitleTextView.setText(getString(R.string.publish_travel));

        backImageView = (ImageView) view.findViewById(R.id.drawer_indicator);
        backImageView.setOnClickListener(this);

        smallPriceSeekBar = (SeekBar) view.findViewById(R.id.price_small_seekBar);
        smallPriceSeekBar.setOnSeekBarChangeListener(this);
        smallPriceSeekBar.setProgress(smallPriceSeekBar.getMax() / 2);

        mediumPriceSeekBar = (SeekBar) view.findViewById(R.id.price_medium_seekBar);
        mediumPriceSeekBar.setOnSeekBarChangeListener(this);
        mediumPriceSeekBar.setProgress(mediumPriceSeekBar.getMax() / 2);

        bigPriceSeekBar = (SeekBar) view.findViewById(R.id.price_big_seekBar);
        bigPriceSeekBar.setOnSeekBarChangeListener(this);
        bigPriceSeekBar.setProgress(bigPriceSeekBar.getMax() / 2);

        smallPriceTextView = (TextView) view.findViewById(R.id.price_small_textView);
        smallPriceTextView.setText(getString(R.string.small) + " " + smallPriceSeekBar.getProgress() + "€");

        mediumPriceTextView = (TextView) view.findViewById(R.id.price_medium_textView);
        mediumPriceTextView.setText(getString(R.string.medium) + " " + mediumPriceSeekBar.getProgress() + "€");

        bigPriceTextView = (TextView) view.findViewById(R.id.price_big_textView);
        bigPriceTextView.setText(getString(R.string.big) + " " + bigPriceSeekBar.getProgress() + "€");

        publishTravelTextView = (TextView) view.findViewById(R.id.publishNext);
        publishTravelTextView.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.drawer_indicator:
                this.getActivity().onBackPressed();
                break;
            case R.id.publishNext:

                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.price_small_seekBar:
                smallPriceTextView.setText(getString(R.string.small) + " " + progress + "€");
                break;
            case R.id.price_medium_seekBar:
                mediumPriceTextView.setText(getString(R.string.medium) + " " + progress + "€");
                break;
            case R.id.price_big_seekBar:
                bigPriceTextView.setText(getString(R.string.big) + " " + progress + "€");
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}
