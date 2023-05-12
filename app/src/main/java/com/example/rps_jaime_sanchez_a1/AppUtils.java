package com.example.rps_jaime_sanchez_a1;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class AppUtils {

    private static final String LAST_INTERACTION_TIME_KEY = "last_interaction_time";

    public static long getLastInteractionTime(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getLong(LAST_INTERACTION_TIME_KEY, 0);
    }

    public static void setLastInteractionTime(Context context, long time) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(LAST_INTERACTION_TIME_KEY, time);
        editor.apply();
    }
}