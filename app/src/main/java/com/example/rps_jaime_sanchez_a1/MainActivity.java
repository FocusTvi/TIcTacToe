package com.example.rps_jaime_sanchez_a1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    private ImageButton btnRock;
    private ImageButton btnPaper;
    private ImageButton btnScissors;
    private ImageView imgPlayer;
    private ImageView imgCpu;
    private TextView tvScorePlayer;
    private TextView tvScoreCpu;
    private TextView tvMessage;
    private int player_img;
    private int cpu_img;
    private Handler handler = new Handler();
    private SharedPreferences sharedPreferences;

    public enum GameResult {
        WIN, LOSE, TIE
    }

    enum Winner {
        CPU,
        PLAYER,
        TIE,

    }

    enum Images {
        ROCK(R.drawable.ic_rock),
        PAPER(R.drawable.ic_paper),
        SCISSORS(R.drawable.ic_scissors);

        public final int value;

        private Images(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //We set the mainactivity into the gameapplication variable to be called
        GameApplication gameApplication = (GameApplication) getApplication();
        gameApplication.setMainActivity(this);

        int[] images = new int[]{R.drawable.ic_rock, R.drawable.ic_paper, R.drawable.ic_scissors};

//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setContentView(R.layout.activity_main);
//        } else {
//            setContentView(R.layout.activity_main);
//        }

        setContentView(R.layout.activity_main);
        btnRock = findViewById(R.id.btnRock);
        btnPaper = findViewById(R.id.btnPaper);
        btnScissors = findViewById(R.id.btnScissors);
        imgPlayer = findViewById(R.id.imgPlayer);
        imgCpu = findViewById(R.id.imgCpu);
        tvScorePlayer = findViewById(R.id.tvScorePlayer);
        tvScoreCpu = findViewById(R.id.tvScoreCpu);
        tvMessage = findViewById(R.id.tvMessage);


        View.OnClickListener viewListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvMessage.setText("");

                //Select the image according to the pressed button
                if (view.getId() == btnRock.getId()) {
                    player_img = Images.ROCK.value;
                } else if (view.getId() == btnPaper.getId()) {
                    player_img = Images.PAPER.value;
                } else {
                    player_img = Images.SCISSORS.value;
                }

                //Setting visibility to the images
                imgPlayer.setVisibility(View.VISIBLE);
                imgCpu.setVisibility(View.VISIBLE);


                //getting the random image
                int random_image = new Random().nextInt(images.length);
                cpu_img = images[random_image];

                //setting the new images
                imgCpu.setImageResource(cpu_img);
                imgPlayer.setImageResource(player_img);
                imgCpu.setAlpha(0f);
                imgCpu.animate().alpha(1f).setDuration(1500);

                imgPlayer.setAlpha(0f);
                imgPlayer.animate().alpha(1f).setDuration(1500).setListener(
                        new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                GameResult result = null;
                                //get the winner
                                if (getWinner() == Winner.PLAYER) {
                                    int score = Integer.parseInt(tvScorePlayer.getText().toString()) + 1;
                                    tvScorePlayer.setText(score + "");
                                    tvMessage.setText("You win this match!");
                                    result = GameResult.WIN;

                                } else if (getWinner() == Winner.CPU) {
                                    int score = Integer.parseInt(tvScoreCpu.getText().toString()) + 1;
                                    tvScoreCpu.setText(score + "");
                                    tvMessage.setText("CPU wins this match!");
                                    result = GameResult.LOSE;
                                } else {
                                    tvMessage.setText("It's a tie!");
                                    result = GameResult.TIE;
                                }
                                ((GameApplication) getApplication()).addGameResult(result.name());
                            }
                        }
                );;



            }
        };


        //setting the listener to the buttons
        btnRock.setOnClickListener(viewListener);
        btnPaper.setOnClickListener(viewListener);
        btnScissors.setOnClickListener(viewListener);

        sharedPreferences = getSharedPreferences("LastInput", MODE_PRIVATE);
    }


    //Method to get the winner based on the images
    protected Winner getWinner() {
        if (player_img == cpu_img)
            return Winner.TIE;
        else if (
                (player_img == Images.ROCK.value && cpu_img == Images.SCISSORS.value) ||
                        (player_img == Images.PAPER.value && cpu_img == Images.ROCK.value) ||
                        (player_img == Images.SCISSORS.value && cpu_img == Images.PAPER.value)
        ) {
            return Winner.PLAYER;
        }

        return Winner.CPU;


    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            long lastInteractionTime = AppUtils.getLastInteractionTime(MainActivity.this);
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastInteractionTime >= 3000) {
                Toast.makeText(MainActivity.this, "Hey, you are doing great... Keep playing!", Toast.LENGTH_SHORT).show();
            }
            handler.postDelayed(this, 3000);
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Update the last interaction time whenever the user interacts with the app
        AppUtils.setLastInteractionTime(this, System.currentTimeMillis());

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Update the last interaction time whenever the user interacts with the app
        AppUtils.setLastInteractionTime(this, System.currentTimeMillis());

        return super.dispatchKeyEvent(event);
    }


    public void performReset() {
        tvMessage.setText("");
        tvScoreCpu.setText("0");
        tvScorePlayer.setText("0");
        imgPlayer.setVisibility(View.GONE);
        imgCpu.setVisibility(View.GONE);
        //save preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("tvMessage", tvMessage.getText().toString());
        editor.putString("tvScoreCpu", tvScoreCpu.getText().toString());
        editor.putString("tvScorePlayer", tvScorePlayer.getText().toString());
        editor.putInt("player_img", player_img);
        editor.putInt("cpu_img", cpu_img);
        editor.commit();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tvMessage", tvMessage.getText().toString());
        outState.putString("tvScoreCpu", tvScoreCpu.getText().toString());
        outState.putString("tvScorePlayer", tvScorePlayer.getText().toString());
        outState.putInt("player_img", player_img);
        outState.putInt("cpu_img", cpu_img);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tvMessage.setText(savedInstanceState.getString("tvMessage"));
        tvScoreCpu.setText(savedInstanceState.getString("tvScoreCpu"));
        tvScorePlayer.setText(savedInstanceState.getString("tvScorePlayer"));
        cpu_img = savedInstanceState.getInt("cpu_img");
        player_img = savedInstanceState.getInt("player_img");
        if (player_img > 0) {
            imgPlayer.setVisibility(View.VISIBLE);
            imgCpu.setVisibility(View.VISIBLE);
            imgCpu.setImageResource(cpu_img);
            imgPlayer.setImageResource(player_img);
        }
    }


    @Override
    protected void onPause() {

        //save preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("tvMessage", tvMessage.getText().toString());
        editor.putString("tvScoreCpu", tvScoreCpu.getText().toString());
        editor.putString("tvScorePlayer", tvScorePlayer.getText().toString());
        editor.putInt("player_img", player_img);
        editor.putInt("cpu_img", cpu_img);
        editor.commit();

        super.onPause();

        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onResume() {

        super.onResume();


        handler.postDelayed(runnable, 3000);

        //get values from shared preferences
        tvMessage.setText(sharedPreferences.getString("tvMessage", ""));
        tvScoreCpu.setText(sharedPreferences.getString("tvScoreCpu", "0"));
        tvScorePlayer.setText(sharedPreferences.getString("tvScorePlayer", "0"));
        cpu_img = sharedPreferences.getInt("cpu_img", cpu_img);
        player_img = sharedPreferences.getInt("player_img", player_img);
//        if (player_img > 0) {
//            imgPlayer.setVisibility(View.VISIBLE);
//            imgCpu.setVisibility(View.VISIBLE);
//            imgCpu.setImageResource(cpu_img);
//            imgPlayer.setImageResource(player_img);
//        }
    }

    //TO show the menu in the app we have to inflate
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;

    }


    //this is to manage the
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_stats:
                 intent = new Intent(this, StatsActivity.class);
                startActivity(intent);
                break;
            default:
                break;

        }
        return true;
    }

    //This method is called when the user has deactivated the app or moved outside the app by pressing home button
    @Override
    protected void onStop() {
//        startService(new Intent(getApplicationContext(), NotificationService.class));
        super.onStop();
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//
//        // Check if the orientation of the device has changed to landscape
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            stopService(new Intent(this, NotificationService.class));
//        }
//    }


}