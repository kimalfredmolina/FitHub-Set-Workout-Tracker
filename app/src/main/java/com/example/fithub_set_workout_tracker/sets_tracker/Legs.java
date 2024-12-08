package com.example.fithub_set_workout_tracker.sets_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fithub_set_workout_tracker.R;

public class Legs extends AppCompatActivity {

    private final String[] legs = {"Squats", "Lunges", "Calf Raises", "Front Squat", "Leg Curls", "Leg Extensions", "Leg Press", "Straight Leg Deadlifts"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.legs);

        ListView listView = findViewById(R.id.Workout_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, legs);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedExercise = legs[position];
                Intent resultIntent = new Intent(Legs.this, AddExercise.class);
                resultIntent.putExtra("selectedExercise", selectedExercise);

                startActivity(resultIntent);
                finish();
            }
        });
    }
}
