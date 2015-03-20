package com.shipeer.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Session;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URI;

import model.RoundedImageView;


public class UserProfileActivity extends Activity implements View.OnClickListener {

    private TextView actionBarTitleTextView;
    private ImageView backImageView;

    private TextView completeNameTextView;
    private ImageView profilePictureImageView;

    private CardView logOutCardView;

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

        logOutCardView = (CardView) findViewById(R.id.card_view_log_out);
        logOutCardView.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = GlobalState.getSharedPreferences();
        String userFirstName = preferences.getString("BaseUserFirstName", null);
        String baseUserSurname = preferences.getString("BaseUserSurname", null);
        String baseUserProfilePicture = preferences.getString("BaseUserProfilePicture", null);

        if(userFirstName != null) completeNameTextView.setText(userFirstName);
        if(baseUserSurname != null) completeNameTextView.setText(completeNameTextView.getText() + " " + baseUserSurname);
        if(baseUserProfilePicture != null) {
            Log.d("picture", baseUserProfilePicture);
            //File file = new File(baseUserProfilePicture);
            Picasso.with(this).load(baseUserProfilePicture).into(profilePictureImageView);
            //profilePictureImageView.setImageDrawable(Drawable.createFromPath(file.getAbsolutePath()));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.drawer_indicator:
                this.onBackPressed();
                break;
            case R.id.complete_name_textview:
                Intent intent = new Intent(this, UserEditProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.card_view_log_out:
                logOut();
                break;
        }
    }

    private void logOut() {
        SharedPreferences preferences = GlobalState.getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("FacebookAccessToken");
        editor.commit();

        Session.getActiveSession().closeAndClearTokenInformation();

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
        overridePendingTransition(R.anim.zoom_in, R.anim.left_to_right_slide_out);
    }

}
