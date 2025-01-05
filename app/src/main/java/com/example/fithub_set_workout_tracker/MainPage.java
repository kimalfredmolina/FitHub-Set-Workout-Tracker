package com.example.fithub_set_workout_tracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View; // Import View
import android.widget.Switch;
import android.widget.TextView; // Import TextView
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainPage extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ViewAdapter viewAdapter;

    private final int[] tabIcons = {
            R.drawable.ic_sets,
            R.drawable.ic_home,
            R.drawable.ic_user
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        NotificationUtils.createNotificationChannel(this);
        NotificationUtils.scheduleDailyReminder(this);

        // Request notification permission (for Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.nav);

        // *** Firebase Authentication and Email Display ***
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        View headerView = navigationView.getHeaderView(0);
        TextView emailTextView = headerView.findViewById(R.id.email);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            emailTextView.setText(email);
        } else {
            emailTextView.setText("No User Signed In");
        }
        // *** End of Firebase and Email Display Code ***

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open,
                R.string.close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Initialize Night Mode Switch
        MenuItem nightModeItem = navigationView.getMenu().findItem(R.id.navmode);
        Switch nightModeSwitch = (Switch) nightModeItem.getActionView();
        if (nightModeSwitch != null) {
            nightModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            });
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            handleNavigation(item);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        tabLayout = findViewById(R.id.tablayout);
        viewPager2 = findViewById(R.id.view_pager);
        viewAdapter = new ViewAdapter(this);
        viewPager2.setAdapter(viewAdapter);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            tab.setIcon(tabIcons[position]);
            switch (position) {
                case 0:
                    tab.setText(R.string.nav_sets);
                    break;
                case 1:
                    tab.setText(R.string.nav_home);
                    break;
                case 2:
                    tab.setText(R.string.nav_user);
                    break;
            }
        }).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                Objects.requireNonNull(tabLayout.getTabAt(position)).select();
            }
        });
    }

    private void handleNavigation(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.navabout) {
            // Log to check if the method is triggered
            Log.d("Navigation", "About Us clicked");

            // Open external link for About Us
            String url = "https://fithub-website-three.vercel.app/"; // your desired URL
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(android.net.Uri.parse(url));
            startActivity(intent);
        } else if (itemId == R.id.navtc) {
            // Log to check if the method is triggered
            Log.d("Navigation", "Terms and Conditions clicked");

            // Open external link for Terms and Conditions
            String url = "https://fithub-website-three.vercel.app/#terms";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(android.net.Uri.parse(url));
            startActivity(intent);
        } else if (itemId == R.id.navfeedback) {
            // Log to check if the method is triggered
            Log.d("Navigation", "Feedback clicked");

            // Open external link for Feedback
            String url = "https://fithub-website-three.vercel.app/#feedback";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(android.net.Uri.parse(url));
            startActivity(intent);
        } else if (itemId == R.id.navlogout) {
            // Handle logout functionality
            logOutUser();
        } else {
            Log.d("Navigation", "Unhandled menu item clicked");
        }
    }

    /**
     * Logs out the user and redirects to the login screen.
     */
    private void logOutUser() {
        // Show a confirmation dialog before logging out
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Perform logout operations here (e.g., clearing user session, shared preferences, etc.)
                    Log.d("Navigation", "User confirmed logout");

                    // Redirect to the login screen
                    Intent intent = new Intent(MainPage.this, LoginForm.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the back stack
                    startActivity(intent);

                    // Show a toast message
                    Toast.makeText(MainPage.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Dismiss the dialog if the user cancels
                    Log.d("Navigation", "User canceled logout");
                    dialog.dismiss();
                })
                .show();
    }
}