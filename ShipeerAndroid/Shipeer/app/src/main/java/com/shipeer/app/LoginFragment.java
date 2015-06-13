package com.shipeer.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import async.EmailLoginTask;
import async.FacebookLoginTask;
import async.interfaces.OnEmailLoginTaskCompleted;
import async.interfaces.OnFacebookLoginTaskCompleted;
import async.interfaces.OnPublicUserProfileTaskCompleted;
import async.PublicUserProfileTask;


public class LoginFragment extends Fragment implements View.OnClickListener, OnEmailLoginTaskCompleted, OnFacebookLoginTaskCompleted, OnPublicUserProfileTaskCompleted {

    private static final String TAG = "Login Fragment";
    private static final String TAG_ONCREATE = "LoginFragment.OnCreate";

    private GlobalState globalState;
    private LoginButton loginButton;
    private UiLifecycleHelper uiHelper;

    private Button emailLoginButton;
    private TextView registerTextView;

    private EditText emailDialogEdittext;
    private EditText passwordDialogEdittext;
    private View positiveAction;

    private String emailDialogStr;
    private String passwordDialogStr;

    private ProgressBar asyncLoadProgressBar;

    //private SignInButton googlePlusButton;

    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
    private boolean pendingPublishReauthorization = true;


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
        Log.i(TAG, "onCreate");

        sendAnalyticsView();

        if (Session.getActiveSession() != null) {
            Session.getActiveSession().close();
            Session.getActiveSession().closeAndClearTokenInformation();
        }

        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);

        Log.i(TAG_ONCREATE, "Session state: ");
        Log.i(TAG_ONCREATE, "Session accessToken: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.fragment_login, container, false);
        View view = inflater.inflate(R.layout.fragment_login, null);

        loginButton = (LoginButton) view.findViewById(R.id.fb_login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday"));
        loginButton.setFragment(this);

        emailLoginButton = (Button) view.findViewById(R.id.email_login_button);
        registerTextView = (TextView) view.findViewById(R.id.register_textview);

        //googlePlusButton = (SignInButton) view.findViewById(R.id.google_plus_login_button);

        asyncLoadProgressBar = (ProgressBar) view.findViewById(R.id.async_load_progressBar);

        emailLoginButton.setOnClickListener(this);
        registerTextView.setOnClickListener(this);
        //googlePlusButton.setOnClickListener(this);

        if (savedInstanceState != null) {
            pendingPublishReauthorization = savedInstanceState.getBoolean(PENDING_PUBLISH_KEY, false);
        }

        return view;
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (exception != null) Log.d("EXCEPTION", exception.toString() + " .");
        if (state != null) Log.d("STATE", state.toString() + " .");
        if (state.isOpened()) {
            /**if (pendingPublishReauthorization) {
                 pendingPublishReauthorization = false;
                 if (session != null) {

                     // Check for publish permissions
                     List<String> permissions = session.getPermissions();
                     if (!isSubsetOf(PERMISSIONS, permissions)) {
                         pendingPublishReauthorization = true;
                         Session.NewPermissionsRequest newPermissionsRequest = new Session
                            .NewPermissionsRequest(this, PERMISSIONS);
                         session.requestNewPublishPermissions(newPermissionsRequest);
                         //return;
                     }
                 }
             }**/

            GlobalState.saveFacebookToken(session.getAccessToken(), session.getExpirationDate().toString());

            Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        Log.i(TAG, "Logged in... response:" + response.getRawResponse());

                        String gender = "";
                        if (user.getProperty("gender") != null)
                            gender = user.getProperty("gender").toString();

                        GlobalState.saveBaseUserData(
                                user.getName(),
                                gender,
                                user.getFirstName(),
                                user.getLastName(),
                                user.getBirthday(),
                                user.getProperty("email").toString(),
                                "https://graph.facebook.com/" + user.getId() + "/picture?type=large",
                                null
                        );

                        //GlobalState.saveBaseUserEmail(user.getProperty("email").toString());

                        GlobalState.saveFacebookData(
                                user.getId(),
                                gender,
                                user.getUsername(),
                                user.getFirstName(),
                                user.getMiddleName(),
                                user.getLastName(),
                                user.getBirthday(),
                                user.getProperty("email").toString()
                        );

                        SharedPreferences preferences = GlobalState.getSharedPreferences();
                        String[] form = {user.getId(), preferences.getString("FacebookAccessToken", null)};
                        if (checkInternetConnection()) {
                            asyncLoadProgressBar.setVisibility(View.VISIBLE);
                            asyncLoadProgressBar.setProgress(50);
                            loginButton.setEnabled(false);
                            FacebookLoginTask fbLoginTask = new FacebookLoginTask(LoginFragment.this);
                            fbLoginTask.execute(form);
                        } else {
                            showNoInternetConnectionError();
                        }
                    }
                }
            }).executeAsync();

        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }

    @Override
    public void onFacebookLoginTaskCompleted(String[] result) {
        asyncLoadProgressBar.setVisibility(View.GONE);
        loginButton.setEnabled(true);
        emailLoginButton.setEnabled(true);
        if (result != null) {
            if (result.length == 1) {
                Toast.makeText(this.getActivity(), getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
                if (Session.getActiveSession() != null) {
                    Session.getActiveSession().closeAndClearTokenInformation();
                }
            } else if (result.length == 4) {
                GlobalState.saveBaseUserTokens(
                        result[0],
                        result[1],
                        result[2],
                        result[3]
                );

                //Toast.makeText(this.getActivity(), getString(R.string.register_succed), Toast.LENGTH_SHORT).show();

                sendAnalyticsUserLogedBy("Facebook");

                goToHomeFragment();
            }
        } else {
            Toast.makeText(this.getActivity(), getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
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

    private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

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
        outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**case R.id.google_plus_login_button:

             break;**/
            case R.id.email_login_button:
                emailLogin();
                break;
            case R.id.register_textview:
                RegisterFragment registerFragment = new RegisterFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_down_to_top, R.anim.slide_out_top, R.anim.slide_in_top_to_down, R.anim.slide_out_down)
                        .replace(R.id.login_view_fragment_container, registerFragment)
                        .addToBackStack("registerFragment")
                        .commit();
                break;
        }
    }

    private void emailLogin() {
        MaterialDialog dialog = new MaterialDialog.Builder(this.getActivity())
                .title(R.string.email_log_in)
                .customView(R.layout.dialog_email_login, true)
                .positiveText(R.string.sign_in)
                .negativeText(R.string.register)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        Log.d("Email login", "Email: " + emailDialogEdittext.getText().toString() + ", Password: " + passwordDialogEdittext.getText().toString());
                        emailDialogStr = emailDialogEdittext.getText().toString();
                        passwordDialogStr = passwordDialogEdittext.getText().toString();

                        if (emailDialogStr == null || emailDialogStr.isEmpty()) {
                            Log.d(TAG, "EMAIL INCORRECT");
                            emailDialogEdittext.setError(getString(R.string.missing_email));
                            Toast.makeText(LoginFragment.this.getActivity(), getString(R.string.missing_email), Toast.LENGTH_SHORT).show();
                        } else if (passwordDialogStr == null || passwordDialogStr.isEmpty()) {
                            Log.d(TAG, "PASSWORD INCORRECT");
                            passwordDialogEdittext.setError(getString(R.string.missing_password));
                            Toast.makeText(LoginFragment.this.getActivity(), getString(R.string.missing_password), Toast.LENGTH_SHORT).show();
                        } else {
                            String[] form = {emailDialogStr, passwordDialogStr};
                            if (checkInternetConnection()) {
                                asyncLoadProgressBar.setVisibility(View.VISIBLE);
                                asyncLoadProgressBar.setProgress(50);
                                emailLoginButton.setEnabled(false);
                                loginButton.setEnabled(false);
                                EmailLoginTask emailLoginTask = new EmailLoginTask(LoginFragment.this);
                                emailLoginTask.execute(form);
                            } else {
                                showNoInternetConnectionError();
                            }
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        RegisterFragment registerFragment = new RegisterFragment();
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_down_to_top, R.anim.slide_out_top, R.anim.slide_in_top_to_down, R.anim.slide_out_down)
                                .replace(R.id.login_view_fragment_container, registerFragment)
                                .addToBackStack("registerFragment")
                                .commit();
                    }
                }).build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);

        emailDialogEdittext = (EditText) dialog.getCustomView().findViewById(R.id.email_dialog_editText);
        passwordDialogEdittext = (EditText) dialog.getCustomView().findViewById(R.id.password_dialog_editText);

        dialog.show();
    }

    @Override
    public void onEmailLoginTaskCompleted(String[] result) {
        asyncLoadProgressBar.setVisibility(View.GONE);
        emailLoginButton.setEnabled(true);
        loginButton.setEnabled(true);
        if (result != null) {
            if (result.length == 1) {
                Toast.makeText(this.getActivity(), getString(R.string.email_or_password_incorrect), Toast.LENGTH_SHORT).show();
            } else if (result.length == 4) {
                GlobalState.saveBaseUserEmailAndPass(
                        emailDialogStr,
                        passwordDialogStr
                );

                GlobalState.saveBaseUserTokens(
                        result[0],
                        result[1],
                        result[2],
                        result[3]
                );

                if (checkInternetConnection()) {
                    asyncLoadProgressBar.setVisibility(View.VISIBLE);
                    asyncLoadProgressBar.setProgress(50);
                    emailLoginButton.setEnabled(false);
                    loginButton.setEnabled(false);

                    sendAnalyticsUserLogedBy("Email");

                    PublicUserProfileTask publicUserProfileTask = new PublicUserProfileTask(this);
                    publicUserProfileTask.execute(result[0], result[1], result[2]);
                } else {
                    showNoInternetConnectionError();
                }
            } else {
                Toast.makeText(this.getActivity(), getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this.getActivity(), getString(R.string.connection_problem), Toast.LENGTH_SHORT).show();
        }
    }

    private void goToHomeFragment() {
        /**FragmentManager fragmentManager = getFragmentManager();

         if (fragmentManager != null) {
         FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

         HomeFragment fragment = new HomeFragment();
         fragmentTransaction.replace(R.id.main_fragment_container_frameLayout, fragment).commit();
         }**/

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

    @Override
    public void onPublicUserProfileTaskCompleted(String[] result) {
        asyncLoadProgressBar.setVisibility(View.GONE);
        emailLoginButton.setEnabled(true);
        loginButton.setEnabled(true);

        goToHomeFragment();
    }

    public void sendAnalyticsView() {
        // Get tracker.
        Tracker t = ((GlobalState) getActivity().getApplication()).getTracker(
                GlobalState.TrackerName.APP_TRACKER);

        // Set screen name.
        // Where path is a String representing the screen name.
        t.setScreenName(TAG);

        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    public void sendAnalyticsUserLogedBy(String loginMethod) {
        // Get tracker.
        Tracker t = ((GlobalState) getActivity().getApplication()).getTracker(
                GlobalState.TrackerName.APP_TRACKER);
        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder()
                .setCategory(TAG)
                .setAction(getString(R.string.publish_trip))
                .setLabel("User Loged In By " + loginMethod)
                .build());
    }
}
