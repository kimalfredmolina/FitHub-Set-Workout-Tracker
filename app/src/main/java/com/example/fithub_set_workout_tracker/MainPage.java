package com.example.fithub_set_workout_tracker;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

public class MainPage extends AppCompatActivity {

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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tabLayout = findViewById(R.id.tablayout);
        viewPager2 = findViewById(R.id.view_pager);
        viewAdapter = new ViewAdapter(this);
        viewPager2.setAdapter(viewAdapter);

        //for bottom navbar icons
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setIcon(tabIcons[0]);
                    tab.setText(R.string.nav_sets);
                    break;
                case 1:
                    tab.setIcon(tabIcons[1]);
                    tab.setText(R.string.nav_home);
                    break;
                case 2:
                    tab.setIcon(tabIcons[2]);
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
                super.onPageSelected(position);
                Objects.requireNonNull(tabLayout.getTabAt(position)).select();
            }
        });
    }
}
