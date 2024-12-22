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
import com.example.fithub_set_workout_tracker.SignUpForm;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import com.example.fithub_set_workout_tracker.R;
import com.google.android.material.appbar.MaterialToolbar;

public class AccountPage extends Fragment {

    private TextView signedEmail;
    private Button signOut;
    FirebaseAuth mAuth;





    public AccountPage() {
        // Default constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // for inflate the layout for this fragment
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

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null){
            String email = currentUser.getEmail();
            signedEmail.setText("Signed in as: " + email);

            Uri photoUrl = currentUser.getPhotoUrl();
            if (photoUrl != null) {
                Glide.with(requireContext()).load(photoUrl).into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.fithub_logo);
            }

        } else {
            signedEmail.setText("No Current email signed in");
            profileImage.setImageResource(R.drawable.fithub_logo);
        }

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                signOutUser();
            }
        });

        MaterialToolbar toolbar = view.findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> {
        });
    }

    private void signOutUser() {
        Intent mainAct = new Intent(getActivity(),LoginForm.class);
        mainAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainAct);
        requireActivity().finish();

    }
}
