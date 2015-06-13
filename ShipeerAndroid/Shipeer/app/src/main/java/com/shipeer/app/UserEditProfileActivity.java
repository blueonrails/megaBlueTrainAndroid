package com.shipeer.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.android.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import async.EditUserProfileTask;
import async.interfaces.OnEditUserProfileTaskCompleted;
import async.interfaces.OnProfilePictureUploaderTaskCompleted;
import async.ProfilePictureUploaderTask;
import model.CircleTransform;


public class UserEditProfileActivity extends Activity implements View.OnClickListener, OnProfilePictureUploaderTaskCompleted, OnEditUserProfileTaskCompleted {

    private static int RESULT_LOAD_IMAGE = 1;
    private static int REQUEST_TAKE_PHOTO = 0;

    private String selectedImagePath;

    private TextView actionBarTitleTextView;
    private ImageView backImageView;

    private ImageView profilePictureCircularImageView;
    private Button changeProfilePicturButton;
    private ProgressBar profilePictureProgressBar;
    private File photoCameraFile;

    private EditText nameEditText;
    private EditText surnameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private Button saveButton;

    private String name = "";
    private String surname = "";
    private String email = "";
    private String phone = "";

    private Uri uriImage;
    private Cloudinary cloudinary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_profile);

        actionBarTitleTextView = (TextView) findViewById(R.id.second_action_bar_title_textview);
        actionBarTitleTextView.setText(getString(R.string.edit_profile));

        backImageView = (ImageView) findViewById(R.id.drawer_indicator);
        backImageView.setOnClickListener(this);

        name = GlobalState.getBaseUserFirstName();
        surname = GlobalState.getBaseUserSurname();
        email = GlobalState.getBaseUserEmail();
        phone = GlobalState.getBaseUserPhone();

        profilePictureCircularImageView = (ImageView) findViewById(R.id.profile_picture_edit_circularImageView);

        String baseUserProfilePictureId = GlobalState.getBaseUserProfilePictureId();
        String baseUserProfilePictureVersion = GlobalState.getBaseUserProfilePictureVersion();

        cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(this));
        if (baseUserProfilePictureId != null && !baseUserProfilePictureId.isEmpty() && baseUserProfilePictureVersion != null && !baseUserProfilePictureVersion.isEmpty()) {
            String url = cloudinary.url().transformation(new Transformation().width(200).height(200).crop("fill")).version(baseUserProfilePictureVersion).generate(baseUserProfilePictureId);
            Log.d("URL", url);
            profilePictureCircularImageView.setImageResource(0);
            profilePictureCircularImageView.setImageDrawable(null);
            Picasso.with(this).load(url).transform(new CircleTransform()).into(profilePictureCircularImageView);
        } else {
            Picasso.with(this).load(R.drawable.default_profile_pic).resize(200, 200).centerCrop().transform(new CircleTransform()).into(profilePictureCircularImageView);
        }

        changeProfilePicturButton = (Button) findViewById(R.id.change_profile_picture_button);
        changeProfilePicturButton.setOnClickListener(this);

        profilePictureProgressBar = (ProgressBar) findViewById(R.id.profile_picture_progressBar);

        nameEditText = (EditText) findViewById(R.id.name_editText);
        surnameEditText = (EditText) findViewById(R.id.surname_editText);
        emailEditText = (EditText) findViewById(R.id.email_editText);
        phoneEditText = (EditText) findViewById(R.id.phone_editText);

        nameEditText.setText(name);
        surnameEditText.setText(surname);
        emailEditText.setText(email);
        phoneEditText.setText(phone);

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
                saveProfileIntoServer();
                break;
            case R.id.drawer_indicator:
                this.onBackPressed();
                break;
        }
    }

    private void saveProfileIntoServer() {
        String userId = GlobalState.getBaseUserId();
        String userKey = GlobalState.getBaseUserKey();
        String userSecret = GlobalState.getBaseUserSecret();

        if (userId != null && userKey != null && userSecret != null) {
            String[] form = new String[10];
            form[0] = userId; //User id
            form[1] = userKey; //User key
            form[2] = userSecret; //User secret
            form[3] = nameEditText.getText().toString(); //User firstName
            form[4] = surnameEditText.getText().toString(); //User lastName
            form[5] = phoneEditText.getText().toString(); //User phone
            form[6] = 1 + ""; //User gender
            form[7] = emailEditText.getText().toString(); //User email
            form[8] = GlobalState.getBaseUserProfilePictureId();
            form[9] = GlobalState.getBaseUserProfilePictureVersion();

            EditUserProfileTask editUserProfileTask = new EditUserProfileTask(this);
            editUserProfileTask.execute(form);
        }
    }

    private void changeProfilePicture() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.choose_profile_pic)
                .items(R.array.profile_pic_options)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (which == 0) {
                            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
                                // Create the File where the photo should go
                                photoCameraFile = null;
                                try {
                                    photoCameraFile = createImageFile();
                                } catch (IOException ex) {
                                    // Error occurred while creating the File
                                }
                                // Continue only if the File was successfully created
                                if (photoCameraFile != null) {
                                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                            Uri.fromFile(photoCameraFile));
                                    startActivityForResult(takePhotoIntent, REQUEST_TAKE_PHOTO);
                                }
                            }

                        } else if (which == 1) {
                            // Create the File where the photo should go
                            photoCameraFile = null;
                            try {
                                photoCameraFile = createImageFile();
                            } catch (IOException ex) {
                                // Error occurred while creating the File
                            }
                            // Continue only if the File was successfully created
                            if (photoCameraFile != null) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoCameraFile));
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent,
                                        "Select Picture"), RESULT_LOAD_IMAGE);

                                /**Intent pickImageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                 pickImageIntent.setType("image/*");
                                 pickImageIntent.putExtra("crop", "true");
                                 //pickImageIntent.putExtra("outputX", 200);
                                 //pickImageIntent.putExtra("outputY", 200);
                                 //pickImageIntent.putExtra("aspectX", 1);
                                 //pickImageIntent.putExtra("aspectY", 1);
                                 //pickImageIntent.putExtra("scale", true);
                                 pickImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoCameraFile));
                                 //pickImageIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                                 startActivityForResult(pickImageIntent, RESULT_LOAD_IMAGE);**/
                            }
                        }
                    }
                }).build();

        dialog.show();
    }

    private void saveProfile() {
        name = nameEditText.getText().toString();
        if (name != null && !name.isEmpty()) {
            GlobalState.saveBaseUserFirstName(name);
        }
        surname = surnameEditText.getText().toString();
        if (surname != null && !surname.isEmpty()) {
            GlobalState.saveBaseUserSurname(surname);
        }
        email = emailEditText.getText().toString();
        if (email != null && !email.isEmpty() && isEmailValid(email)) {
            GlobalState.saveBaseUserEmail(email);
        }
        phone = phoneEditText.getText().toString();
        if (phone != null && !phone.isEmpty()) {
            GlobalState.saveBaseUserPhone(phone);
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
        if (requestCode == RESULT_LOAD_IMAGE) {
            if (resultCode != RESULT_CANCELED) {
                if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
                    Uri selectedImageUri = data.getData();

                    if (selectedImageUri != null) {
                        // try to retrieve the image from the media store first
                        // this will only work for images selected from gallery
                        String[] projection = {MediaStore.Images.Media.DATA};
                        Cursor cursor = managedQuery(selectedImageUri, projection, null, null, null);
                        if (cursor != null) {
                            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                            cursor.moveToFirst();
                            selectedImagePath = cursor.getString(column_index);
                        } else {
                            // this is our fallback here
                            selectedImagePath = selectedImageUri.getPath();
                        }

                        if (checkInternetConnection()) {
                            profilePictureProgressBar.setVisibility(View.VISIBLE);
                            profilePictureProgressBar.setProgress(50);

                            String userId = GlobalState.getBaseUserId();

                            if (userId != null && selectedImagePath != null && !selectedImagePath.isEmpty()) {
                                Long timestamp = System.currentTimeMillis();
                                String timeStampString = timestamp.toString();

                                ProfilePictureUploaderTask profilePictureUploaderTask = new ProfilePictureUploaderTask(this, cloudinary);
                                profilePictureUploaderTask.execute(selectedImagePath, userId, timeStampString);

                                saveProfileIntoServer();
                            }
                        } else {
                            showNoInternetConnectionError();
                        }
                    }
                }
            }
        } else if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                if (checkInternetConnection()) {
                    profilePictureProgressBar.setVisibility(View.VISIBLE);
                    profilePictureProgressBar.setProgress(50);

                    String userId = GlobalState.getBaseUserId();

                    if (userId != null && !userId.isEmpty()) {
                        Long timestamp = System.currentTimeMillis();
                        String timeStampString = timestamp.toString();

                        if(photoCameraFile != null) Log.d("CAMERA PATH", photoCameraFile.getAbsolutePath());
                        ProfilePictureUploaderTask profilePictureUploaderTask = new ProfilePictureUploaderTask(this, cloudinary);
                        profilePictureUploaderTask.execute(photoCameraFile.getAbsolutePath(), userId, timeStampString);
                    }
                } else {
                    showNoInternetConnectionError();
                }
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
                //finish();
            }
        }
    }

    @Override
    public void onProfilePictureUploaderTaskCompleted(String[] res) {
        if (res != null && res.length == 2) {
            String picId = res[0];
            String picVersion = res[1];
            Log.d("IMAGE", "UPLOADED ID ->" + picId);

            String url = cloudinary.url().transformation(new Transformation().width(200).height(200).crop("fill")).version(picVersion).generate(picId);
            //String url = cloudinary.url().transformation(new Transformation().width(200).height(200).crop("fill")).generate(res) + ".jpg";
            Log.d("URL", url);

            profilePictureCircularImageView.setImageResource(0);
            profilePictureCircularImageView.setImageDrawable(null);
            Picasso.with(this).load(url).transform(new CircleTransform()).into(profilePictureCircularImageView);

            Picasso.with(this).load(url).into(profilePictureCircularImageView, new Callback() {
                @Override
                public void onSuccess() {
                    profilePictureProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    profilePictureProgressBar.setVisibility(View.GONE);
                }
            });

            saveProfileIntoServer();
        } else {
            Log.d("IMAGE", " NOT UPLOADED");
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "shipeer_pic";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                null,//".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    public boolean checkInternetConnection() {
        ConnectivityManager conMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
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
        Toast.makeText(this, getString(R.string.no_internet_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditUserProfileTaskCompleted(String[] result) {
        Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
        this.onBackPressed();
    }
}
