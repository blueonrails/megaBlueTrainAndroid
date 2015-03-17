package com.shipeer.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import model.RoundedImageView;


public class UserEditProfileActivity extends Activity implements View.OnClickListener {

    private static int RESULT_LOAD_IMAGE = 1;

    private TextView actionBarTitleTextView;
    private ImageView backImageView;

    private ImageView profilePictureCircularImageView;
    private Button changeProfilePicturButton;

    private EditText nameEditText;
    private EditText surnameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private Button saveButton;

    private String name = "";
    private String surname = "";
    private String email = "";
    private String phone = "";
    private String baseUserProfilePicture = "";

    private Uri uriImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_profile);

        actionBarTitleTextView = (TextView) findViewById(R.id.second_action_bar_title_textview);
        actionBarTitleTextView.setText(getString(R.string.edit_profile));

        backImageView = (ImageView) findViewById(R.id.drawer_indicator);
        backImageView.setOnClickListener(this);

        SharedPreferences preferences = GlobalState.getSharedPreferences();
        if (preferences != null) {
            name = preferences.getString("BaseUserFirstName", "");
            surname = preferences.getString("BaseUserSurname", "");
            email = preferences.getString("BaseUserEmail", "");
            phone = preferences.getString("BaseUserPhone", "");
            baseUserProfilePicture = preferences.getString("BaseUserProfilePicture", null);
        }

        profilePictureCircularImageView = (ImageView) findViewById(R.id.profile_picture_edit_circularImageView);
        changeProfilePicturButton = (Button) findViewById(R.id.change_profile_picture_button);
        changeProfilePicturButton.setOnClickListener(this);

        nameEditText = (EditText) findViewById(R.id.name_editText);
        surnameEditText = (EditText) findViewById(R.id.surname_editText);
        emailEditText = (EditText) findViewById(R.id.email_editText);
        phoneEditText = (EditText) findViewById(R.id.phone_editText);

        nameEditText.setText(name);
        surnameEditText.setText(surname);
        emailEditText.setText(email);
        phoneEditText.setText(phone);

        if (baseUserProfilePicture != null) {
            Log.d("picture", baseUserProfilePicture);
            //File file = new File(baseUserProfilePicture);
            //profilePictureCircularImageView.setImageDrawable(Drawable.createFromPath(file.getAbsolutePath()));
            Picasso.with(this).load(baseUserProfilePicture).into(profilePictureCircularImageView);
        }

        saveButton = (Button) findViewById(R.id.save_profile_edit_button);
        saveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_profile_picture_button:
                changeProfilePicture();
                break;
            case R.id.save_profile_edit_button:
                saveProfile();
                break;
            case R.id.drawer_indicator:
                this.onBackPressed();
                break;
        }
    }

    private void changeProfilePicture() {
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        pickImageIntent.setType("image/*");
        pickImageIntent.putExtra("crop", "true");
        /*pickImageIntent.putExtra("outputX", 200);
        pickImageIntent.putExtra("outputY", 200);*/
        pickImageIntent.putExtra("aspectX", 1);
        pickImageIntent.putExtra("aspectY", 1);
        //pickImageIntent.putExtra("scale", true);
        //pickImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriWhereToStore);
        //pickImageIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(pickImageIntent, RESULT_LOAD_IMAGE);
    }

    private void saveProfile() {
        SharedPreferences preferences = GlobalState.getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();

        name = nameEditText.getText().toString();
        if (name != null && !name.isEmpty()) {
            editor.putString("BaseUserFirstName", name);
        }
        surname = surnameEditText.getText().toString();
        if (surname != null && !surname.isEmpty()) {
            editor.putString("BaseUserSurname", surname);
        }
        email = emailEditText.getText().toString();
        if (email != null && !email.isEmpty()) {
            editor.putString("BaseUserEmail", email);
        }
        phone = phoneEditText.getText().toString();
        if (phone != null && !phone.isEmpty()) {
            editor.putString("BaseUserPhone", phone);
        }
        editor.commit();

        Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();

        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        profilePictureCircularImageView = (ImageView) findViewById(R.id.profile_picture_edit_circularImageView);
        changeProfilePicturButton = (Button) findViewById(R.id.change_profile_picture_button);
        nameEditText = (EditText) findViewById(R.id.name_editText);
        surnameEditText = (EditText) findViewById(R.id.surname_editText);
        emailEditText = (EditText) findViewById(R.id.email_editText);
        phoneEditText = (EditText) findViewById(R.id.phone_editText);
        saveButton = (Button) findViewById(R.id.save_profile_edit_button);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Uri uri = data.getData();
                    Log.d("TAG", "datae" + uri);

                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    cursor.moveToFirst();

                    int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    String path = cursor.getString(idx);
                    Toast.makeText(this, path, Toast.LENGTH_SHORT).show();

                    saveProfilePicture(path);
                    copyProfilePictureToShipeerFolder(path);

                    Bitmap photo = extras.getParcelable("data");
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);
                    // The stream to write to a file or directly using the
                    ImageView imageView = (ImageView) findViewById(R.id.profile_picture_edit_circularImageView);
                    imageView.setImageBitmap(photo);
                }
            }
        }
    }

    private void copyProfilePictureToShipeerFolder(String path) {
        try {
            File sdFile = Environment.getExternalStorageDirectory();
            File dataFile = Environment.getDataDirectory();
            if (sdFile.canWrite()) {
                String sourceImagePath = path;
                String destinationImagePath = sdFile.getAbsolutePath() + "/shipeer/profilePics/profile.jpg";
                File source = new File(dataFile, sourceImagePath);
                File destination = new File(sdFile, destinationImagePath);
                destination.mkdirs();
                if (source.exists()) {
                    FileChannel src = new FileInputStream(source).getChannel();
                    FileChannel dst = new FileOutputStream(destination).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveProfilePicture(String path) {
        SharedPreferences preferences = GlobalState.getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("BaseUserProfilePicture", path);
        editor.commit();
    }
}
