package com.example.fithub_set_workout_tracker;

import static com.example.fithub_set_workout_tracker.R.id.back_btn;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.fithub_set_workout_tracker.main_pages.SetTrackerPage;

public class Alarm extends AppCompatActivity {

    private TextView timerText;
    private NumberPicker minutePicker;
    private Button startButton;
    private Button stopButton;
    private CountDownTimer countDownTimer;
    private MediaPlayer mediaPlayer;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alarm);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        timerText = findViewById(R.id.timerText);
        minutePicker = findViewById(R.id.minutePicker);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        back = findViewById(back_btn);

        minutePicker.setMinValue(1);
        minutePicker.setMaxValue(10);
        minutePicker.setWrapSelectorWheel(true);

        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound);

        startButton.setOnClickListener(v -> startTimer());
        stopButton.setOnClickListener(v -> stopTimer());

        back.setOnClickListener(v -> {
            Intent intent = new Intent(Alarm.this, MainPage.class);
            startActivity(intent);
            finish();
        });
    }

    private void startTimer() {
        int selectedMinutes = minutePicker.getValue();
        long countdownTime = selectedMinutes * 60 * 1000;

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(countdownTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                String timeFormatted = String.format("%02d:%02d", minutes, seconds);
                timerText.setText(timeFormatted);
            }

            @Override
            public void onFinish() {
                timerText.setText("00:00");
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
            }
        }.start();
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        timerText.setText("00:00");

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
