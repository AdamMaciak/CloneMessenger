package com.example.clonemessenger;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.clonemessenger.Models.UserModel;
import com.example.clonemessenger.Models.UserSharedPref;
import com.google.firebase.firestore.auth.User;

public class SharedPrefUser {
    private static final String SHARED_PREF_NAME = "userData"; // może krzaczyć
    private static final String KEY_NAME = "keyname";
    private static final String KEY_PHOTOPATH = "keyphotopath";
    private static final String KEY_ID = "keyid";
    private static SharedPrefUser mInstance;
    private static Context mCtx;

    private SharedPrefUser(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefUser getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefUser(context);
        }
        return mInstance;
    }


    public void userLogin(UserSharedPref userSharedPref) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ID, userSharedPref.getId());
        editor.putString(KEY_NAME, userSharedPref.getName());
        editor.putString(KEY_PHOTOPATH, userSharedPref.getImagePath());
        editor.apply();
    }

    public static boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ID, null) != null;
    }


    public UserSharedPref getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new UserSharedPref(
                sharedPreferences.getString(KEY_NAME, null),
                sharedPreferences.getString(KEY_PHOTOPATH, null),
                sharedPreferences.getString(KEY_ID, null));
    }


    public static String getUserName() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_NAME, null);
    }
    public static String getUserId() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ID, null);
    }
    public static String getPhotopath() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_PHOTOPATH, null);
    }
    public static void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
