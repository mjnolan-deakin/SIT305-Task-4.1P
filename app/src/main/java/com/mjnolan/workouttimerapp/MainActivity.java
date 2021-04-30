package com.mjnolan.workouttimerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private int seconds = 0;

    private boolean isRunning;

    private boolean wasRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            seconds = savedInstanceState.getInt("seconds");
            isRunning = savedInstanceState.getBoolean("isRunning");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
        }

        /*
        [Shared Prefs]
        string :: lastWorkout -- complete string of last workout
         */
        SharedPreferences readSharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String lastWorkout = readSharedPref.getString("lastWorkout", "You spent 00:00 on [no name] last time.");

        TextView tvLastWorkout = (TextView) findViewById(R.id.tvLastWorkout);
        tvLastWorkout.setText(lastWorkout);

        startTimer();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("seconds", seconds);
        savedInstanceState.putBoolean("isRunning", isRunning);
        savedInstanceState.putBoolean("wasRunning", wasRunning);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        wasRunning = isRunning;
        isRunning = false;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (wasRunning) {
            isRunning = true;
        }
    }

    public void btnStartClicked(View view)
    {
        isRunning = true;
    }

    public void btnPauseClick(View view)
    {
        isRunning = false;
    }

    public void btnStopClick(View view)
    {
        isRunning = false;

        EditText etWorkoutName = (EditText) findViewById(R.id.etWorkoutName);
        TextView tvLastWorkout = (TextView) findViewById(R.id.tvLastWorkout);
        TextView tvTime = (TextView) findViewById(R.id.tvTime);

        String lastWorkout;
        if (TextUtils.isEmpty(etWorkoutName.getText().toString())) {
            // Is Empty
            lastWorkout = "You spent " + tvTime.getText().toString() + " on [no name] last time.";
        } else {
            lastWorkout = "You spent " + tvTime.getText().toString() + " on " + etWorkoutName.getText().toString() + " last time.";
        }

        tvLastWorkout.setText(lastWorkout);

        SharedPreferences writeSharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = writeSharedPref.edit();
        editor.putString("lastWorkout", lastWorkout);
        editor.apply();

        seconds = 0;
        tvTime.setText("00:00");
    }

    private void startTimer() {
        final TextView tvTime = (TextView)findViewById(R.id.tvTime);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                String time = String.format(Locale.getDefault(), "%02d:%02d", minutes, secs);

                // Set the text view text.
                tvTime.setText(time);

                // If running is true, increment the
                // seconds variable.
                if (isRunning) {
                    seconds++;
                }

                // Post the code again
                // with a delay of 1 second.
                handler.postDelayed(this, 1000);
            }
        });
    }
}