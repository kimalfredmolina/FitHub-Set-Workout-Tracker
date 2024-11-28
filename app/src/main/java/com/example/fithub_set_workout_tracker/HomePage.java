package com.example.fithub_set_workout_tracker;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Initialize the toolbar
        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        // Set navigation icon click listener
        toolbar.setNavigationOnClickListener(v ->
                Toast.makeText(HomePage.this, "Navigation icon clicked!", Toast.LENGTH_SHORT).show()
        );

        // Initialize the CalendarView
        CalendarView calendarView = findViewById(R.id.calendarView);

        // Set a listener for date selection
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            Toast.makeText(HomePage.this, "Selected Date: " + selectedDate, Toast.LENGTH_SHORT).show();
        });

        // Initialize the BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Set up navigation item selected listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_sets) {
                Toast.makeText(this, "Sets Selected", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_home) {
                Toast.makeText(this, "Home Selected", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_user) {
                Toast.makeText(this, "User Selected", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        });
    }
}
