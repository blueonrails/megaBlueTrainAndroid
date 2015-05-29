package com.shipeer.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.android.Utils;
import com.facebook.Session;
import com.rey.material.widget.FloatingActionButton;
import com.squareup.picasso.Picasso;

import model.CircleTransform;

/**
 * Created by mifercre on 30/03/15.
 */
public class UserProfileFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "User Profile Fragment";
    private static final int EDIT_PROFILE_RESULT = 69;

    private TextView completeNameTextView;
    private ImageView profilePictureImageView;

    private RelativeLayout facebookValidatedRelativeLayout;
    private RelativeLayout logOutRelativeLayout;

    private TextView emailTextView;
    private TextView phoneTextView;
    private FloatingActionButton editProfileFloatingActionButton;

    private Cloudinary cloudinary;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity.setCurrentFragmentShown(MainActivity.USER_PROFILE);
        MainActivity.setActionBarTitle(getString(R.string.profile));
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        String userFirstName = GlobalState.getBaseUserFirstName();
        String baseUserSurname = GlobalState.getBaseUserSurname();

        completeNameTextView = (TextView) view.findViewById(R.id.complete_name_textview);

        if(userFirstName != null && !userFirstName.isEmpty()) completeNameTextView.setText(userFirstName);
        if(baseUserSurname != null && !baseUserSurname.isEmpty()) completeNameTextView.setText(completeNameTextView.getText() + " " + baseUserSurname);

        profilePictureImageView = (ImageView) view.findViewById(R.id.profile_picture_circularimageview);

        String baseUserProfilePictureId = GlobalState.getBaseUserProfilePictureId();
        String baseUserProfilePictureVersion = GlobalState.getBaseUserProfilePictureVersion();

        if(baseUserProfilePictureId != null && !baseUserProfilePictureId.isEmpty() && baseUserProfilePictureVersion != null && !baseUserProfilePictureVersion.isEmpty()) {
            cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(this.getActivity()));
            String url = cloudinary.url().transformation(new Transformation().width(200).height(200).crop("fill")).version(baseUserProfilePictureVersion).generate(baseUserProfilePictureId);
            Log.d("URL", url);
            profilePictureImageView.setImageResource(0);
            profilePictureImageView.setImageDrawable(null);
            Picasso.with(this.getActivity()).load(url).transform(new CircleTransform()).into(profilePictureImageView);
        }

        emailTextView = (TextView) view.findViewById(R.id.email_textView);
        String email = GlobalState.getBaseUserEmail();
        if(email != null && !email.isEmpty()) emailTextView.setText(email);

        phoneTextView = (TextView) view.findViewById(R.id.phone_textView);
        String phone = GlobalState.getBaseUserPhone();
        if(phone != null && !phone.isEmpty()) phoneTextView.setText(phone);

        editProfileFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.edit_profile_FloatingActionButton);
        editProfileFloatingActionButton.setOnClickListener(this);

        facebookValidatedRelativeLayout = (RelativeLayout) view.findViewById(R.id.facebook_row);

        String facebookId = GlobalState.getFacebookId();
        String facebookToken = GlobalState.getFacebookToken();
        if(facebookId != null && !facebookId.isEmpty() && facebookToken != null && !facebookToken.isEmpty()) {
            facebookValidatedRelativeLayout.setVisibility(View.VISIBLE);
        }
        else facebookValidatedRelativeLayout.setVisibility(View.GONE);

        logOutRelativeLayout = (RelativeLayout) view.findViewById(R.id.card_view_log_out);
        logOutRelativeLayout.setOnClickListener(this);

        cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(this.getActivity().getApplicationContext()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.setCurrentFragmentShown(MainActivity.USER_PROFILE);
        MainActivity.setActionBarTitle(getString(R.string.profile));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_profile_FloatingActionButton:
                /**this.getActivity().getSupportFragmentManager().addOnBackStackChangedListener(
                        new FragmentManager.OnBackStackChangedListener() {
                            public void onBackStackChanged() {
                                // Update your UI here.
                                Log.d("UserProfileFragment", "onBackStackChanged");
                                if(MainActivity.getCurrentFragmentShown() == MainActivity.USER_PROFILE) {
                                    updateUI();
                                }
                            }
                        });**/
                Intent intent1 = new Intent(this.getActivity(), UserEditProfileActivity.class);
                startActivityForResult(intent1, EDIT_PROFILE_RESULT);
                break;
            case R.id.card_view_log_out:
                logOut();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == EDIT_PROFILE_RESULT) {
            updateUI();
        }
    }

    private void updateUI() {
        String userFirstName = GlobalState.getBaseUserFirstName();
        String baseUserSurname = GlobalState.getBaseUserSurname();

        if(userFirstName != null && !userFirstName.isEmpty()) completeNameTextView.setText(userFirstName);
        if(baseUserSurname != null && !baseUserSurname.isEmpty()) completeNameTextView.setText(completeNameTextView.getText() + " " + baseUserSurname);

        String baseUserProfilePictureId = GlobalState.getBaseUserProfilePictureId();
        String baseUserProfilePictureVersion = GlobalState.getBaseUserProfilePictureVersion();

        if(baseUserProfilePictureId != null && !baseUserProfilePictureId.isEmpty() && baseUserProfilePictureVersion != null && !baseUserProfilePictureVersion.isEmpty()) {
            //cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(UserProfileFragment.this.getActivity()));
            String url = cloudinary.url().transformation(new Transformation().width(200).height(200).crop("fill")).version(baseUserProfilePictureVersion).version(baseUserProfilePictureVersion).generate(baseUserProfilePictureId);
            Log.d("URL", url);
            profilePictureImageView.setImageResource(0);
            profilePictureImageView.setImageDrawable(null);
            Picasso.with(UserProfileFragment.this.getActivity()).load(url).transform(new CircleTransform()).into(profilePictureImageView);
        }

        String email = GlobalState.getBaseUserEmail();
        if(email != null && !email.isEmpty()) emailTextView.setText(email);

        String phone = GlobalState.getBaseUserPhone();
        if(phone != null && !phone.isEmpty()) phoneTextView.setText(phone);
    }

    private void logOut() {
        SharedPreferences preferences = GlobalState.getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("FacebookAccessToken", null);
        editor.putString("BaseUserKey", null);
        editor.clear();
        boolean done = editor.commit();
        Log.d(TAG, "DONE: " + done);

        if(Session.getActiveSession() != null) {
            Log.d(TAG, "LOGGING OUT FROM FACEBOOK");
            Session.getActiveSession().close();
            Session.getActiveSession().closeAndClearTokenInformation();
        }

        Intent intent = new Intent(this.getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
