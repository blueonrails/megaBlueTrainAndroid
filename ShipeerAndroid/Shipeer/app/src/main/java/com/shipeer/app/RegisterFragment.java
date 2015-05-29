package com.shipeer.app;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import async.interfaces.OnRegisterNewUserTaskCompleted;
import async.RegisterNewUserTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener, OnRegisterNewUserTaskCompleted {

    public static final String VIEW_NAME = "Register Fragment";

    private EditText nameEditText;
    private EditText surnameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    //private EditText birthdayEditText;

    private String name;
    private String surname;
    private String email;
    private String password;
    //private String birthday;

    private Button registerButton;
    private ProgressBar registeringProgressBar;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendAnalyticsView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //View view = inflater.inflate(R.layout.fragment_register, container, false);
        View view = inflater.inflate(R.layout.fragment_register, null);

        nameEditText = (EditText) view.findViewById(R.id.name_editText);
        surnameEditText = (EditText) view.findViewById(R.id.surname_editText);
        emailEditText = (EditText) view.findViewById(R.id.email_editText);
        passwordEditText = (EditText) view.findViewById(R.id.password_editText);
        //birthdayEditText = (EditText) view.findViewById(R.id.birthday_editText);

        registerButton = (Button) view.findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);

        registeringProgressBar = (ProgressBar) view.findViewById(R.id.registering_progressBar);
        registeringProgressBar.setVisibility(View.GONE);

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_button:
                checkInputs();
                break;
        }
    }

    private void checkInputs() {
        name = nameEditText.getText().toString();
        surname = surnameEditText.getText().toString();
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        //birthday = birthdayEditText.getText().toString();

        if (name == null || name.isEmpty()) {
            nameEditText.setError(getString(R.string.missing_name));
        } else if (surname == null || surname.isEmpty()) {
            surnameEditText.setError(getString(R.string.missing_surname));
        } else if (email == null || email.isEmpty()) {
            emailEditText.setError(getString(R.string.missing_email));
        } else if (!isEmailValid(email)) {
            emailEditText.setError(getString(R.string.incorrect_email));
        } else if (password == null || password.isEmpty()) {
            passwordEditText.setError(getString(R.string.missing_password));
            /**} else if(birthday == null || birthday.isEmpty() || birthdayEditText.getText().toString().length() < 4) {
             birthdayEditText.setError(getString(R.string.missing_birthday));**/
        } else {
            //2008-01-10T00:00:00.000Z
            //birthday = birthday.concat("-01-10T00:00:00.000Z");
            //Log.d("birth", birthday);
            //String[] form = {name, surname, birthday, email, password};

            String[] form = {name, surname, email, password};
            registerUser(form);
        }
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    private void registerUser(String[] form) {
        if (checkInternetConnection()) {
            if (registeringProgressBar != null) {
                registeringProgressBar.setVisibility(View.VISIBLE);
                registeringProgressBar.setProgress(50);
            }
            registerButton.setEnabled(false);

            RegisterNewUserTask registerNewUserTask = new RegisterNewUserTask(this);
            registerNewUserTask.execute(form);
        } else {
            showNoInternetConnectionError();
        }
    }

    @Override
    public void onRegisterNewUserTaskCompleted(String[] result) {
        if (registeringProgressBar != null) registeringProgressBar.setVisibility(View.GONE);
        registerButton.setEnabled(true);

        if (result == null || result.length == 0) {
            Toast.makeText(this.getActivity(), getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
        } else if (result.length == 1) {
            if (result[0].equalsIgnoreCase("userAlreadyRegistered"))
                Toast.makeText(this.getActivity(), getString(R.string.user_already_registered), Toast.LENGTH_SHORT).show();
            else if (result[0].equalsIgnoreCase("badRequest"))
                Toast.makeText(this.getActivity(), getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences preferences = GlobalState.getSharedPreferences();
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString("BaseUserFirstName", name);
            editor.putString("BaseUserSurname", surname);
            //editor.putString("BaseUserBirthday", birthday);
            editor.putString("BaseUserEmail", email);
            editor.putString("BaseUserPassword", password);

            editor.commit();

            Toast.makeText(this.getActivity(), getString(R.string.register_succed), Toast.LENGTH_SHORT).show();

            sendAnalyticsNewUserRegistered();

            goToHomeFragment();
        }
    }

    private void goToHomeFragment() {
        Intent intent = new Intent(this.getActivity(), MainActivity.class);
        startActivity(intent);
        this.getActivity().finish();
    }

    public boolean checkInternetConnection() {
        ConnectivityManager conMgr = (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null)
            return false;
        if (!netInfo.isConnected())
            return false;
        if (!netInfo.isAvailable())
            return false;
        return true;
    }

    private void showNoInternetConnectionError() {
        Toast.makeText(this.getActivity(), getString(R.string.no_internet_error), Toast.LENGTH_SHORT).show();
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

    public void sendAnalyticsNewUserRegistered() {
        // Get tracker.
        Tracker t = ((GlobalState) getActivity().getApplication()).getTracker(
                GlobalState.TrackerName.APP_TRACKER);
        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder()
                .setCategory(VIEW_NAME)
                .setAction(getString(R.string.finish_register))
                .setLabel("New User Registered By Email")
                .build());
    }
}
