package com.shipeer.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import model.MySimpleDateFormat;

/**
 * Used to keep the same context so every time we can access the same SharedPreferences.
 * <p/>
 * Created by mifercre on 10/02/15.
 */
public class GlobalState extends Application {

    private static final String PROPERTY_ID = "UA-62475575-1";
    private static SharedPreferences preferences = null;

    public enum TrackerName {
        APP_TRACKER // Tracker used only in this app.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    @Override
    public void onCreate() {
        super.onCreate();
        if (preferences == null) {
            preferences = getSharedPreferences("ShipeerApp", Context.MODE_PRIVATE);
            Log.i("Application", "onCreate, preferences: " + preferences.toString());
        } else Log.i("Application", "onCreate, preferences: " + preferences.toString());
    }

    public static SharedPreferences getSharedPreferences() {
        return preferences;
    }

    public boolean userIsLogedIn() {
        return preferences.getBoolean("userIsLogedIn", false);
    }

    public static boolean saveFacebookData(String id, String gender, String username, String firstName, String middleName, String lastName, String birthday, String email) {
        SharedPreferences.Editor editor = preferences.edit();
        if (id != null && !id.isEmpty()) editor.putString("FacebookUserId", id);
        if (gender != null && !gender.isEmpty()) editor.putString("FacebookUserGender", gender);
        if (username != null && !username.isEmpty()) editor.putString("FacebookUsername", username);
        if (firstName != null && !firstName.isEmpty())
            editor.putString("FacebookUserFirstName", firstName);
        if (middleName != null && !middleName.isEmpty())
            editor.putString("FacebookUserMiddleName", middleName);
        if (lastName != null && !lastName.isEmpty())
            editor.putString("FacebookUserLastName", lastName);
        if (birthday != null && !birthday.isEmpty())
            editor.putString("FacebookUserBirthday", birthday);
        if (email != null && !email.isEmpty()) editor.putString("FacebookUserEmail", email);
        return editor.commit();
    }

    public static boolean saveBaseUserEmail(String email) {
        SharedPreferences.Editor editor = preferences.edit();
        if (email != null && !email.isEmpty()) editor.putString("BaseUserEmail", email);
        return editor.commit();
    }

    public static void saveBaseUserFacebookId(String facebookId) {
        SharedPreferences.Editor editor = preferences.edit();
        if (facebookId != null && !facebookId.isEmpty())
            editor.putString("FacebookUserId", facebookId);
        editor.commit();
    }

    public static void saveBaseUserFacebookToken(String facebookToken) {
        SharedPreferences.Editor editor = preferences.edit();
        if (facebookToken != null && !facebookToken.isEmpty())
            editor.putString("FacebookAccessToken", facebookToken);
        editor.commit();
    }

    public static boolean saveBaseUserData(String name, String gender, String firstName, String lastName, String birthday, String email, String profilePicId, String profilePicVersion) {
        SharedPreferences.Editor editor = preferences.edit();
        if (name != null && !name.isEmpty()) editor.putString("BaseUsername", name);
        if (gender != null && !gender.isEmpty()) editor.putString("BaseUserGender", gender);
        if (firstName != null && !firstName.isEmpty())
            editor.putString("BaseUserFirstName", firstName);
        if (lastName != null && !lastName.isEmpty()) editor.putString("BaseUserSurname", lastName);
        if (birthday != null && !birthday.isEmpty()) editor.putString("BaseUserBirthday", birthday);
        if (email != null && !email.isEmpty()) editor.putString("BaseUserEmail", email);
        if (profilePicId != null && !profilePicId.isEmpty())
            editor.putString("BaseUserProfilePictureId", profilePicId);
        if (profilePicVersion != null && !profilePicVersion.isEmpty())
            editor.putString("BaseUserProfilePictureVersion", profilePicVersion);
        return editor.commit();
    }

    public static boolean saveBaseUserPhone(String phone) {
        SharedPreferences.Editor editor = preferences.edit();
        if (phone != null && !phone.isEmpty()) editor.putString("BaseUserPhone", phone);
        return editor.commit();
    }

    public static boolean saveFacebookToken(String accessToken, String expirationDate) {
        SharedPreferences.Editor editor = preferences.edit();
        if (accessToken != null && !accessToken.isEmpty())
            editor.putString("FacebookAccessToken", accessToken);
        if (expirationDate != null && !expirationDate.isEmpty())
            editor.putString("FacebookAccessTokenExpirationDate", expirationDate);
        return editor.commit();
    }

    public static boolean saveBaseUserTokens(String baseUserId, String baseUserKey, String baseUserSecret, String baseUserExpire) {
        SharedPreferences.Editor editor = preferences.edit();
        if (baseUserId != null && !baseUserId.isEmpty()) editor.putString("BaseUserId", baseUserId);
        if (baseUserKey != null && !baseUserKey.isEmpty())
            editor.putString("BaseUserKey", baseUserKey);
        if (baseUserSecret != null && !baseUserSecret.isEmpty())
            editor.putString("BaseUserSecret", baseUserSecret);
        if (baseUserExpire != null && !baseUserExpire.isEmpty())
            editor.putString("BaseUserExpire", baseUserExpire);
        return editor.commit();
    }

    public static boolean saveBaseUserEmailAndPass(String emailDialogStr, String passwordDialogStr) {
        SharedPreferences.Editor editor = preferences.edit();
        if (emailDialogStr != null && !emailDialogStr.isEmpty())
            editor.putString("BaseUserEmail", emailDialogStr);
        if (passwordDialogStr != null && !passwordDialogStr.isEmpty())
            editor.putString("BaseUserPassword", passwordDialogStr);
        return editor.commit();
    }

    public static boolean saveLastDateVersionChecked(Long lastDate) {
        SharedPreferences.Editor editor = preferences.edit();
        if (lastDate != null) editor.putLong("LastDateVersionChecked", lastDate);
        return editor.commit();
    }

    public static Long getLastDateVersionChecked() {
        return preferences.getLong("LastDateVersionChecked", 0);
    }

    public static boolean saveProfilePicture(String id, String version) {
        SharedPreferences.Editor editor = preferences.edit();
        if (id != null && !id.isEmpty()) editor.putString("BaseUserProfilePictureId", id);
        if (version != null && !version.isEmpty())
            editor.putString("BaseUserProfilePictureVersion", version);
        return editor.commit();
    }

    public static Date getDateExpires() {
        String expires = preferences.getString("BaseUserExpire", null);

        String androidDateString = MySimpleDateFormat.convertFromISOtoAndroid(expires);
        Date date = MySimpleDateFormat.parseAndroidDate(androidDateString);
        return date;
    }

    public static String getFacebookAccessToken() {
        return preferences.getString("FacebookAccessToken", null);
    }

    public static String getBaseUserKey() {
        return preferences.getString("BaseUserKey", null);
    }

    public static String getBaseUserProfilePicture() {
        return preferences.getString("BaseUserProfilePicture", null);
    }

    public static String getBaseUserSurname() {
        return preferences.getString("BaseUserSurname", null);
    }

    public static String getBaseUserFirstName() {
        return preferences.getString("BaseUserFirstName", null);
    }

    public static String getBaseUserEmail() {
        return preferences.getString("BaseUserEmail", null);
    }

    public static String getBaseUserPassword() {
        return preferences.getString("BaseUserPassword", null);
    }

    public static String getFacebookId() {
        return preferences.getString("FacebookUserId", null);
    }

    public static String getFacebookToken() {
        return preferences.getString("FacebookAccessToken", null);
    }

    public static String getBaseUserProfilePictureId() {
        return preferences.getString("BaseUserProfilePictureId", null);
    }

    public static String getBaseUserProfilePictureVersion() {
        return preferences.getString("BaseUserProfilePictureVersion", null);
    }

    public static String getBaseUserPhone() {
        return preferences.getString("BaseUserPhone", null);
    }

    public static String getBaseUserId() {
        return preferences.getString("BaseUserId", null);
    }

    public static String getBaseUserSecret() {
        return preferences.getString("BaseUserSecret", null);
    }

    synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = null;
            if (trackerId == TrackerName.APP_TRACKER) {
                t = analytics.newTracker(PROPERTY_ID);
            }
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }
}