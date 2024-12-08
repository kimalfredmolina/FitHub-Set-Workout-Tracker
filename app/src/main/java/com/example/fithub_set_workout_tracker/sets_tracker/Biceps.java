package com.example.fithub_set_workout_tracker.sets_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fithub_set_workout_tracker.R;

public class Biceps extends AppCompatActivity {

    private final String[] biceps = {"Barbell Bicep Curls", "Hammer Curls", "Concentration Curls", "Dumbbell Biceps Curls"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biceps);

        ListView listView = findViewById(R.id.Workout_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, biceps);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedExercise = biceps[position];

                Intent resultIntent = new Intent(Biceps.this, AddExercise.class);
                resultIntent.putExtra("selectedExercise", selectedExercise);

                startActivity(resultIntent);
                finish();
            }
        });
    }
}
