package com.example.fithub_set_workout_tracker;

import android.content.Intent; // Import Intent for navigation
import android.os.Bundle;
import android.view.View; // Import View for the listener
import android.widget.Button; // Import Button class

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

        // Apply system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find the button and set its click listener
        Button splashButton = findViewById(R.id.splash);
        splashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginPage(); // Call the function to navigate
            }
        });
    }

    // Function to navigate to the login page
    private void goToLoginPage() {
        Intent intent = new Intent(SplashScreenForm.this, LoginForm.class); // Replace LoginForm with your login activity
        startActivity(intent);
    }
}
