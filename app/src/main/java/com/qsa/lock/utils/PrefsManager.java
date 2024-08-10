package com.qsa.lock.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.qsa.lock.LockApplication;
import com.qsa.lock.model.Theme;

public class PrefsManager {
    private static Object theme;
    static SharedPreferences preferences;
    static SharedPreferences.Editor editor;

    public PrefsManager() {
        preferences = LockApplication.context.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static Theme  getBackGroundImage() {

        return new Gson().fromJson(preferences.getString("background",""),Theme.class);
    }
    public static void setBackGroundImage(Theme theme) {
        editor.putString("background",new Gson().toJson(theme)).commit();

    }
}
