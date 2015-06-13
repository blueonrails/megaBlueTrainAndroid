package com.shipeer.app;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OnBoardingFragment extends Fragment implements View.OnClickListener {

    private int position;

    private RelativeLayout nextRelativeLayout;

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
        View view = null;
        Log.d("ON BOARDING", "POSITION -> " + position);
        switch (position) {
            case 0:
                view = inflater.inflate(R.layout.fragment_on_boarding_0, container, false);
                break;
            case 1:
                view = inflater.inflate(R.layout.fragment_on_boarding_1, container, false);
                break;
            case 2:
                view = inflater.inflate(R.layout.fragment_on_boarding_2, container, false);
                break;
            case 3:
                view = inflater.inflate(R.layout.fragment_on_boarding_3, container, false);
                break;
        }
        nextRelativeLayout = (RelativeLayout) view.findViewById(R.id.on_boarding_next_relativeLayout);
        nextRelativeLayout.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        ((LoginActivity) getActivity()).selectPage(position+1);
    }
}
