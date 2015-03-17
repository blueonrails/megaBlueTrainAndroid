package com.shipeer.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mifercre on 17/03/15.
 */
public class TravelPriceCommentFragment extends Fragment {

    public TravelPriceCommentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_travel_price_comment, container, false);

        return view;
    }
}
