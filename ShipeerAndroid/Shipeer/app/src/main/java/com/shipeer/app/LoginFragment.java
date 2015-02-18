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

        Session session = Session.getActiveSession();
        Log.i(TAG_ONCREATE, "Session state: " + session.getState());
        Log.i(TAG_ONCREATE, "Session accessToken: " + session.getAccessToken());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        return view;
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            final SharedPreferences preferences = GlobalState.getSharedPreferences();
            boolean isFacebookLogedIn = preferences.getBoolean("FacebookLogedIn", false);

            if(!isFacebookLogedIn) {
                Request.newMeRequest(session, new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        if (user != null) {
                            Log.i(TAG, "Logged in... response:" + response.getRawResponse());

                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("FacebookLogedIn", true);
                            editor.putString("FacebookUserId", user.getId());
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
            } else {
                goToUserProfileFragment();
            }
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
            SharedPreferences preferences = GlobalState.getSharedPreferences();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("FacebookLogedIn", false);
            editor.commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null && (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }

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

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private void goToUserProfileFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        UserProfileFragment fragment = new UserProfileFragment();
        fragmentTransaction.replace(android.R.id.content, fragment).commit();
    }
}
