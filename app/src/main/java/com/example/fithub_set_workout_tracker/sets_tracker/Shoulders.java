package com.example.fithub_set_workout_tracker.sets_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fithub_set_workout_tracker.R;

public class Shoulders extends AppCompatActivity {

    private final String[] shoulders = {"Dumbbell Lateral Raises", "Military Press", "Shoulder Dumbbell Press", "Upright Rows" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shoulders);

        ListView listView = findViewById(R.id.Workout_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, shoulders);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedExercise = shoulders[position];
                Intent resultIntent = new Intent(Shoulders.this, AddExercise.class);
                resultIntent.putExtra("selectedExercise", selectedExercise);

                startActivity(resultIntent);
                finish();
            }
        });
    }
}
