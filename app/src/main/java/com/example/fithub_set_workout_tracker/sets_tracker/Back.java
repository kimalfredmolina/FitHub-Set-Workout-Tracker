package com.example.fithub_set_workout_tracker.sets_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fithub_set_workout_tracker.R;

public class Back extends AppCompatActivity {

    private final String[] back = {"Pull-Ups", "Deadlifts", "Chin-Ups", "Barbell Row", "Cable Row", "Dumbbell Row", "HyperExtentions", "Pull Downs", };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.back);

        ListView listView = findViewById(R.id.Workout_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, back);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedExercise = back[position];

                Intent resultIntent = new Intent(Back.this, AddExercise.class);
                resultIntent.putExtra("selectedExercise", selectedExercise);
                startActivity(resultIntent);
                finish();
            }
        });
    }
}
