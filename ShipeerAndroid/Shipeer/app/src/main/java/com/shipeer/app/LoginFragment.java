package com.shipeer.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import java.util.Arrays;


public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private static final String TAG_ONCREATE = "LoginFragment.OnCreate";

    private GlobalState globalState;
    private LoginButton loginButton;
    private UiLifecycleHelper uiHelper;

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);

        Log.i(TAG_ONCREATE, "Session state: " );
        Log.i(TAG_ONCREATE, "Session accessToken: " );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        loginButton = (LoginButton) view.findViewById(R.id.authButton);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday"));
        loginButton.setFragment(this);

        return view;
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            final SharedPreferences preferences = GlobalState.getSharedPreferences();
            final SharedPreferences.Editor editor = preferences.edit();
            editor.putString("FacebookAccessToken", session.getAccessToken());
            editor.putString("FacebookAccessTokenExpirationDate", session.getExpirationDate().toString());
            editor.commit();

            Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        Log.i(TAG, "Logged in... response:" + response.getRawResponse());

                        String baseUsername = preferences.getString("BaseUsername", null);
                        if(baseUsername == null) editor.putString("BaseUsername", user.getName());

                        String baseUserGender = preferences.getString("BaseUserGender", null);
                        if(baseUserGender == null) editor.putString("BaseUserGender", user.getProperty("gender").toString());

                        String baseUserFirstName = preferences.getString("BaseUserFirstName", null);
                        if(baseUserFirstName == null) editor.putString("BaseUserFirstName", user.getFirstName());

                        String baseUserSurname = preferences.getString("BaseUserSurname", null);
                        if(baseUserSurname == null) editor.putString("BaseUserSurname", user.getLastName());

                        String baseUserBirthday = preferences.getString("BaseUserBirthday", null);
                        if(baseUserBirthday == null) editor.putString("BaseUserBirthday", user.getBirthday());

                        String baseUserEmail = preferences.getString("BaseUserEmail", null);
                        if(baseUserEmail == null) editor.putString("BaseUserEmail", user.getProperty("email").toString());

                        String baseUserProfilePicture = preferences.getString("BaseUserProfilePicture", null);
                        if(baseUserProfilePicture == null) editor.putString("BaseUserProfilePicture", "https://graph.facebook.com/" + user.getId() + "/picture?type=large");

                        editor.putString("FacebookUserId", user.getId());
                        editor.putString("FacebookUserGender", user.getProperty("gender").toString());
                        editor.putString("FacebookUsername", user.getUsername());
                        editor.putString("FacebookUserFirstName", user.getFirstName());
                        editor.putString("FacebookUserMiddleName", user.getMiddleName());
                        editor.putString("FacebookUserLastName", user.getLastName());
                        editor.putString("FacebookUserBirthday", user.getBirthday());
                        editor.putString("FacebookUserEmail", user.getProperty("email").toString());
                        editor.commit();

                        goToUserProfileFragment();
                    }
                }
            }).executeAsync();
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private void goToUserProfileFragment() {
        FragmentManager fragmentManager = getFragmentManager();

        if (fragmentManager != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            HomeFragment fragment = new HomeFragment();
            fragmentTransaction.replace(android.R.id.content, fragment).commit();
        }
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
}
