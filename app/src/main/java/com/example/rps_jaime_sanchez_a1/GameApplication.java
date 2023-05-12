package com.example.rps_jaime_sanchez_a1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class GameApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private static final String DB_NAME = "db_rps";
    private static final int DB_VERSION = 1;
    private SQLiteOpenHelper helper;
    private boolean isForeground = true;

    private MainActivity mainActivity;

    public void setMainActivity(MainActivity activity) {
        this.mainActivity = activity;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }


    private int wins = 0;
    private int loses = 0;
    private int ties = 0;


    public int getWins() {
        return wins;
    }

    public int getLoses() {
        return loses;
    }

    public int getTies() {
        return ties;
    }


    @Override
    public void onCreate() {

        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        helper = new SQLiteOpenHelper(this, DB_NAME, null, DB_VERSION) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE IF NOT EXISTS tb_game_results (" +
                        "result TEXT, created_at TEXT)");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };

    }

    public void addGameResult(String result) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("INSERT INTO tb_game_results (result, created_at) " +
                "VALUES ('" + result + "', datetime('now'))");

    }

    public void updateResults() {
        SQLiteDatabase db = helper.getReadableDatabase();
        String selectSql = "SELECT result, COUNT(*) AS count FROM tb_game_results GROUP BY result";
        Cursor cursor = db.rawQuery(selectSql, null);
        Map<MainActivity.GameResult, Integer> counts = new HashMap<>();
        while (cursor.moveToNext()) {
            MainActivity.GameResult result = MainActivity.GameResult.valueOf(cursor.getString(cursor.getColumnIndex("result")));
            int count = cursor.getInt(cursor.getColumnIndex("count"));
            counts.put(result, count);
        }

        this.wins = counts.getOrDefault(MainActivity.GameResult.WIN, 0);
        this.loses = counts.getOrDefault(MainActivity.GameResult.LOSE, 0);
        this.ties = counts.getOrDefault(MainActivity.GameResult.TIE, 0);

        // Close the database connection
        cursor.close();
        db.close();
    }

    public void resetGameStats() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("DELETE FROM tb_game_results");
        wins = 0;
        loses = 0;
        ties = 0;
        MainActivity mainActivity = getMainActivity();
        mainActivity.performReset();

    }

    @Override
    public void onActivityResumed(Activity activity) {
        // The app is in the foreground
        isForeground = true;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        // The app is in the background
        isForeground = false;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        // The app is no longer visible to the user
        if (!isForeground) {
            // Start the service when the app is in the background
            Intent startIntent = new Intent(getApplicationContext(), NotificationService.class);
            startService(startIntent);
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        // The app is visible to the user
        // Stop the service when the app is in the foreground
        Intent stopIntent = new Intent(getApplicationContext(), NotificationService.class);
        stopService(stopIntent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // Check if the app is in landscape mode
        super.onConfigurationChanged(newConfig);
        stopService(new Intent(getApplicationContext(), NotificationService.class));
    }


}
