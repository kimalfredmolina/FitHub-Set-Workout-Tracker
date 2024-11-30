package com.example.fithub_set_workout_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AccountPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_page);

        // Initialize views
        Button signOutButton = findViewById(R.id.sign_out_button);
        TextView sendFeedback = findViewById(R.id.send_feedback);
        TextView helpSupport = findViewById(R.id.help_support);
        TextView followCbz = findViewById(R.id.follow_cbz);
        TextView editExercise = findViewById(R.id.edit_exercise);
        TextView editCategories = findViewById(R.id.edit_categories);

        ImageView navSets = findViewById(R.id.nav_sets);
        ImageView navHome = findViewById(R.id.nav_home);
        ImageView navUser = findViewById(R.id.nav_user);

        // Sign Out button logic
        signOutButton.setOnClickListener(view -> {
            // Example logic for signing out
            Toast.makeText(AccountPage.this, "Signing out...", Toast.LENGTH_SHORT).show();
            // Redirect to a login screen or perform other sign-out actions
            Intent intent = new Intent(AccountPage.this, LoginForm.class);
            startActivity(intent);
            finish();
        });

        // Send Feedback logic
        sendFeedback.setOnClickListener(view -> {
            // Example logic for feedback
            Toast.makeText(AccountPage.this, "Opening feedback...", Toast.LENGTH_SHORT).show();
            // Redirect to a feedback activity or email
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@cbz.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
            startActivity(Intent.createChooser(intent, "Send Feedback"));
        });

        // Help and Support logic
        helpSupport.setOnClickListener(view -> {
            Toast.makeText(AccountPage.this, "Opening help & support...", Toast.LENGTH_SHORT).show();
            // Redirect to help activity
            //Intent intent = new Intent(AccountActivity.this, HelpSupportActivity.class);
            //startActivity(intent);
        });

        // Follow CBZ logic
        followCbz.setOnClickListener(view -> {
            Toast.makeText(AccountPage.this, "Follow us on social media!", Toast.LENGTH_SHORT).show();
            // Redirect to a website or social media page
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(android.net.Uri.parse("https://www.twitter.com/cbz"));
            startActivity(intent);
        });

        // Edit Exercise logic
        editExercise.setOnClickListener(view -> {
            Toast.makeText(AccountPage.this, "Editing exercises...", Toast.LENGTH_SHORT).show();
            // Redirect to edit exercise activity
            //Intent intent = new Intent(AccountActivity.this, EditExerciseActivity.class);
            //startActivity(intent);
        });

        // Edit Categories logic
        editCategories.setOnClickListener(view -> {
            Toast.makeText(AccountPage.this, "Editing categories...", Toast.LENGTH_SHORT).show();
            // Redirect to edit categories activity
            //Intent intent = new Intent(AccountActivity.this, EditCategoriesActivity.class);
            //startActivity(intent);
        });

        // Bottom Navigation logic
        navSets.setOnClickListener(view -> {
            Toast.makeText(AccountPage.this, "Opening Sets...", Toast.LENGTH_SHORT).show();
            // Redirect to Sets screen
            //Intent intent = new Intent(AccountActivity.this, SetsActivity.class);
            //startActivity(intent);
        });

        navHome.setOnClickListener(view -> {
            Toast.makeText(AccountPage.this, "Opening Home...", Toast.LENGTH_SHORT).show();
            // Redirect to Home screen
            Intent intent = new Intent(AccountPage.this, HomePage.class);
            startActivity(intent);
        });

        navUser.setOnClickListener(view -> Toast.makeText(AccountPage.this, "You're already on the User page!", Toast.LENGTH_SHORT).show());
    }
}
