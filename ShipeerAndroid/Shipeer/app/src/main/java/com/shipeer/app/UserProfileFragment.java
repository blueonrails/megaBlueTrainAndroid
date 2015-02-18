package com.shipeer.app;

import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import drawer.NavigationAdapter;

public class UserProfileFragment extends Fragment {

    //Drawer layout objects
    private DrawerLayout NavDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView NavList;
    private ArrayList<String> NavItms;
    private NavigationAdapter NavAdapter;

    public static UserProfileFragment newInstance() {
        UserProfileFragment fragment = new UserProfileFragment();
        return fragment;
    }

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        //Drawer Layout
        NavDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        NavList = (ListView) view.findViewById(R.id.lista);

        View header = inflater.inflate(R.layout.drawer_layout_header, null);
        header.setEnabled(false);
        NavList.addHeaderView(header);

        NavItms = new ArrayList<String>();
        NavItms.add("Perfil");
        NavItms.add("Viajes");
        NavItms.add("Envios");

        NavAdapter= new NavigationAdapter(getActivity(), NavItms);
        NavList.setAdapter(NavAdapter);

        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),
                NavDrawerLayout,
                R.drawable.ic_drawer,
                R.string.app_name,
                R.string.hello_world
        );

        NavDrawerLayout.setDrawerListener(mDrawerToggle);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        NavList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                Toast.makeText(getActivity(), "position + " + position + " clicked", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

}
