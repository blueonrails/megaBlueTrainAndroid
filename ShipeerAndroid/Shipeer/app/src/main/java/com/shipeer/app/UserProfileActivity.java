package com.shipeer.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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


public class UserProfileActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "UserProfileActivity";

    private TextView actionBarTitleTextView;
    private ImageView backImageView;

    private TextView completeNameTextView;
    private ImageView profilePictureImageView;

    private RelativeLayout logOutRelativeLayout;

    private TextView emailTextView;
    private TextView phoneTextView;
    private FloatingActionButton editProfileFloatingActionButton;

    private Cloudinary cloudinary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        actionBarTitleTextView = (TextView) findViewById(R.id.second_action_bar_title_textview);
        actionBarTitleTextView.setText(getString(R.string.profile));

        backImageView = (ImageView) findViewById(R.id.drawer_indicator);
        backImageView.setOnClickListener(this);

        completeNameTextView = (TextView) findViewById(R.id.complete_name_textview);
        completeNameTextView.setOnClickListener(this);

        profilePictureImageView = (ImageView) findViewById(R.id.profile_picture_circularimageview);

        emailTextView = (TextView) findViewById(R.id.email_textView);
        phoneTextView = (TextView) findViewById(R.id.phone_textView);
        editProfileFloatingActionButton = (FloatingActionButton) findViewById(R.id.edit_profile_FloatingActionButton);
        editProfileFloatingActionButton.setOnClickListener(this);

        logOutRelativeLayout = (RelativeLayout) findViewById(R.id.card_view_log_out);
        logOutRelativeLayout.setOnClickListener(this);

        cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(this.getApplicationContext()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = GlobalState.getSharedPreferences();
        String userFirstName = preferences.getString("BaseUserFirstName", null);
        String baseUserSurname = preferences.getString("BaseUserSurname", null);
        String baseUserProfilePicture = preferences.getString("BaseUserProfilePicture", null);
        String baseUserEmail = preferences.getString("BaseUserEmail", null);
        String baseUserPhone = preferences.getString("BaseUserPhone", null);

        if(userFirstName != null) completeNameTextView.setText(userFirstName);
        if(baseUserSurname != null) completeNameTextView.setText(completeNameTextView.getText() + " " + baseUserSurname);
        if(baseUserProfilePicture != null) {
            Log.d(TAG, "picture: " + baseUserProfilePicture);

            String url = cloudinary.url().transformation(new Transformation().width(200).height(200).crop("fill")).generate(baseUserProfilePicture);
            Log.d(TAG, "URL: " + url);
            Picasso.with(this).load(url).transform(new CircleTransform()).into(profilePictureImageView);
        }
        if(baseUserEmail != null) emailTextView.setText(baseUserEmail);
        if(baseUserPhone != null) phoneTextView.setText(baseUserPhone);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.drawer_indicator:
                this.onBackPressed();
                break;
            case R.id.edit_profile_FloatingActionButton:
                Intent intent1 = new Intent(this, UserEditProfileActivity.class);
                startActivity(intent1);
                break;
            case R.id.complete_name_textview:
                Intent intent2 = new Intent(this, UserEditProfileActivity.class);
                startActivity(intent2);
                break;
            case R.id.card_view_log_out:
                logOut();
                break;
        }
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

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.zoom_in, R.anim.slide_in_left_to_right);
    }

}
