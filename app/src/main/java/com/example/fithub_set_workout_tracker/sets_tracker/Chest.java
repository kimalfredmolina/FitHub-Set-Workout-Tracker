package com.example.fithub_set_workout_tracker.sets_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fithub_set_workout_tracker.R;

public class Chest extends AppCompatActivity {

    private final String[] chest = {"Bench Press", "Push-Ups", "Cable Crossovers", "Dumbbell flies", "Incline Bench Press", "Declined Dumbbell Bench Press"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chest);

        ListView listView = findViewById(R.id.Workout_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, chest);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedExercise = chest[position];
                Intent resultIntent = new Intent(Chest.this, AddExercise.class);
                resultIntent.putExtra("selectedExercise", selectedExercise);

                startActivity(resultIntent);
                finish();
            }
        });
    }
}
