package com.example.rps_jaime_sanchez_a1;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class StatsActivity extends AppCompatActivity {

    private TextView txtWins;
    private TextView txtLoses;
    private TextView txtTies;
    private TextView btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        btnReset = findViewById(R.id.btnReset);
        txtWins = findViewById(R.id.txtWins);
        txtLoses = findViewById(R.id.txtLoses);
        txtTies = findViewById(R.id.txtTies);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GameApplication) getApplication()).resetGameStats();
                updateFields();
            }
        });
        ((GameApplication) getApplication()).updateResults();
        this.updateFields();


    }


    private void updateFields() {

        txtWins.setText("" + ((GameApplication) getApplication()).getWins());
        txtLoses.setText("" + ((GameApplication) getApplication()).getLoses());
        txtTies.setText("" + ((GameApplication) getApplication()).getTies());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            //go back to your activity / exiting the SettingsActivity
            onBackPressed();
        }
        return true;
    }


}
