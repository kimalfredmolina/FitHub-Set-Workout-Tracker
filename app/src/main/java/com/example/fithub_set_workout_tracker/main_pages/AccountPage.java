package com.example.fithub_set_workout_tracker.main_pages;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fithub_set_workout_tracker.LoginForm;
import com.example.fithub_set_workout_tracker.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.material.appbar.MaterialToolbar;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AccountPage extends Fragment {

    private TextView signedEmail;
    private Button signOut;
    private FirebaseAuth mAuth;
    private BarChart bodyWeightChart;

    public AccountPage() {
        // Default constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account_page, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        signedEmail = view.findViewById(R.id.signed_in_as);
        signOut = view.findViewById(R.id.sign_out_button);
        ImageView profileImage = view.findViewById(R.id.profile_image);
        bodyWeightChart = (BarChart) view.findViewById(R.id.body_weight_chart);// Initialize chart

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String email = currentUser.getEmail();
            signedEmail.setText("Signed in as: " + email);

            Uri photoUrl = currentUser.getPhotoUrl();
            if (photoUrl != null) {
                Glide.with(requireContext()).load(photoUrl).into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.fithub_logo);
            }

            // Fetch and populate graph data
            fetchBodyWeightData(currentUser.getUid());
        } else {
            signedEmail.setText("No Current email signed in");
            profileImage.setImageResource(R.drawable.fithub_logo);
        }

        signOut.setOnClickListener(v -> {
            mAuth.signOut();
            signOutUser();
        });

        MaterialToolbar toolbar = view.findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> {
            // Handle navigation if needed
        });
    }

    private void signOutUser() {
        Intent mainAct = new Intent(getActivity(), LoginForm.class);
        mainAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainAct);
        requireActivity().finish();
    }

    private void fetchBodyWeightData(String uid) {
        DatabaseReference bodyWeightRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("profile")
                .child("bodyWeight");

        bodyWeightRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<BarEntry> entries = new ArrayList<>();

                int groupIndex = 0;
                for (DataSnapshot monthSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot daySnapshot : monthSnapshot.getChildren()) {
                        for (DataSnapshot yearSnapshot : daySnapshot.getChildren()) {
                            String year = yearSnapshot.getKey();
                            float weight = yearSnapshot.getValue(Float.class);

                            String formattedDate = monthSnapshot.getKey() + "." + daySnapshot.getKey();
                            float dateValue = Float.parseFloat(formattedDate);

                            entries.add(new BarEntry(groupIndex++, weight));
                        }
                    }
                }

                if (!entries.isEmpty()) {
                    BarDataSet dataSet = new BarDataSet(entries, "Body Weight Progress");
                    dataSet.setColor(requireContext().getColor(R.color.light_blue));
                    dataSet.setValueTextColor(requireContext().getColor(R.color.textcolor));
                    dataSet.setValueTextSize(10f);

                    BarData barData = new BarData(dataSet);
                    bodyWeightChart.setData(barData);
                    bodyWeightChart.getDescription().setText("Bodyweight Over Time");
                    bodyWeightChart.getXAxis().setDrawGridLines(false);
                    bodyWeightChart.animateY(1000);
                    bodyWeightChart.invalidate();
                } else {
                    bodyWeightChart.clear();
                    bodyWeightChart.setNoDataText("No chart data available.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }


    private float parseDateToFloat(String dateKey) {
        String[] parts = dateKey.split("-");
        return Float.parseFloat(parts[0] + "." + parts[1] + parts[2]);
    }
}
