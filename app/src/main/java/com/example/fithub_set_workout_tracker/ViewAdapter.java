package com.example.fithub_set_workout_tracker;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.fithub_set_workout_tracker.main_pages.AccountPage;
import com.example.fithub_set_workout_tracker.main_pages.HomePage;
import com.example.fithub_set_workout_tracker.main_pages.SetTrackerPage;

public class ViewAdapter extends FragmentStateAdapter {

    private TextView signedEmail;
    public ViewAdapter(@NonNull MainPage fragment) {
        super(fragment);
    }




    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new SetTrackerPage();
            case 1:
                return new HomePage();
            case 2:
                return new AccountPage();
            default:
                return new HomePage();

        }
    }
    @Override
    public int getItemCount() {
        return 3;
    }


}
