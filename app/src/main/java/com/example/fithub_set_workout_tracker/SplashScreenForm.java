package com.example.fithub_set_workout_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashScreenForm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen_form);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find ImageViews
        ImageView splashLine = findViewById(R.id.imageView);
        ImageView splashTrack = findViewById(R.id.imageView2);
        ImageView splashSleep = findViewById(R.id.imageView3);
        ImageView splashRepeat = findViewById(R.id.imageView4);

        // Show elements in sequence
        showElementWithDelay(splashLine, 0);
        showElementWithDelay(splashTrack, 1000);
        showElementWithDelay(splashSleep, 1500);
        showElementWithDelay(splashRepeat, 2000);

        // Transition to LoginForm after all elements are visible
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreenForm.this, LoginForm.class);
            startActivity(intent);
            finish(); // Close the splash screen
        }, 3000);
    }

    private void showElementWithDelay(ImageView imageView, long delay) {
        new Handler().postDelayed(() -> imageView.setVisibility(ImageView.VISIBLE), delay);
    }
}
