package com.example.musictimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static long mStartTimeInMillis;
    private long EndTime;
    private EditText mEditTextInput;
    private TextView mTextviewCountDown;
    private Button mButtonSet;
    private Button mButtonStartPause;
    private Button mButtonReset;

    private CountDownTimer mCountDownTimer;

    private boolean mTimerRunning;

    private long mTimeLeftInMillis;
    private AudioManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditTextInput = findViewById(R.id.text_input);
        mButtonSet = findViewById(R.id.button_set);
        mTextviewCountDown = findViewById(R.id.text_view_countdown);
        mButtonStartPause = findViewById(R.id.button_start_pause);
        mButtonReset = findViewById(R.id.button_reset);


        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTimerRunning){
                    pauseTimer();
                }else{
                    startTimer();
                }
            }
        });

        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
        mButtonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = mEditTextInput.getText().toString();
                if (input.length() == 0){
                    Toast.makeText(MainActivity.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                long millisInput = Long.parseLong(input) * 60000;
                if(millisInput == 0){
                    Toast.makeText(MainActivity.this, "Enter positive number", Toast.LENGTH_SHORT).show();
                    return;
                }
                setTime(millisInput);


            }
        });

    }

    private void setTime(long milliSeconds){
        mStartTimeInMillis = milliSeconds;
        resetTimer();
    }

    private void startTimer() {

        EndTime = System.currentTimeMillis() + mStartTimeInMillis;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                updateButtons();

            }
        }.start();

        mButtonStartPause.setText("Pause");
        mTimerRunning = true;
        updateButtons();
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        updateButtons();
    }

    private void resetTimer(){
        mTimeLeftInMillis = mStartTimeInMillis;
        updateCountDownText();
        updateButtons();

    }

    private void updateCountDownText(){
        int hours = (int) (mTimeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((mTimeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted;
        if(hours > 0){
            timeLeftFormatted = String.format(Locale.getDefault(), "%d:%02d:%02d",hours,minutes,seconds);
        }else{
            timeLeftFormatted = String.format("%02d:%02d",minutes,seconds);
        }

        mTextviewCountDown.setText(timeLeftFormatted);
        if(minutes==00 && seconds ==00){
            sendOutBroadcast();
        }
    }

    private void updateButtons(){
        if(mTimerRunning){
            mEditTextInput.setVisibility(View.INVISIBLE);
            mButtonSet.setVisibility(View.INVISIBLE);
            mButtonReset.setVisibility(View.INVISIBLE);
            mButtonStartPause.setText("Pause");
        }else{
            mEditTextInput.setVisibility(View.VISIBLE);
            mButtonSet.setVisibility(View.VISIBLE);
            mButtonStartPause.setText("Start");
            mButtonReset.setVisibility(View.VISIBLE);
            if (mTimeLeftInMillis < 1000){
                mButtonStartPause.setVisibility(View.INVISIBLE);

            }else {
                mButtonStartPause.setVisibility(View.VISIBLE);
            }
            if(mTimeLeftInMillis < mStartTimeInMillis){
                mButtonReset.setVisibility(View.VISIBLE);
            }else {
                mButtonReset.setVisibility(View.INVISIBLE);
            }

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("millisLeft",mTimeLeftInMillis);
        outState.putBoolean("timerRunning",mTimerRunning);
        outState.putLong("endTime", EndTime);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mTimeLeftInMillis = savedInstanceState.getLong("millisLeft");
        mTimerRunning = savedInstanceState.getBoolean("timerRunning");
        updateCountDownText();
        updateButtons();

        if(mTimerRunning){
            EndTime = savedInstanceState.getLong("endTime");
            mTimeLeftInMillis = EndTime - System.currentTimeMillis();
            startTimer();

        }
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
//        mStartTimeInMillis = prefs.getLong("startTimeinMillis",600000);
//        mTimeLeftInMillis = prefs.getLong("millisLeft", mStartTimeInMillis);
//        mTimerRunning = prefs.getBoolean("timerRunning", false);
//
//        updateCountDownText();
//        updateButtons();
//
//        if(mTimerRunning){
//            EndTime = prefs.getLong("endTime", 0);
//            mTimeLeftInMillis = EndTime - System.currentTimeMillis();
//
//            if (mTimeLeftInMillis < 0){
//                mTimeLeftInMillis = 0;
//                mTimerRunning = false;
//                updateCountDownText();
//                updateButtons();
//            }else{
//                startTimer();
//            }
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//
//        editor.putLong("startTimeInMillis", mStartTimeInMillis);
//        editor.putLong("millisLeft", mTimeLeftInMillis);
//        editor.putBoolean("timerRunning", mTimerRunning);
//        editor.putLong("endTime", EndTime);
//
//        editor.apply();
//
//        if(mCountDownTimer != null){
//            mCountDownTimer.cancel();
//        }
//    }

    public void sendOutBroadcast() {
        manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        manager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }


}
