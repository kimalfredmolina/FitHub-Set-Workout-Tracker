package com.example.fithub_set_workout_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpForm extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText UserName, PassWord;
    private Button LoginButton;
    private TextView clickhere;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_form);

        auth = FirebaseAuth.getInstance();
        UserName = findViewById(R.id.username);
        PassWord = findViewById(R.id.password);
        clickhere = findViewById(R.id.loginhere);
        LoginButton = findViewById(R.id.loginButton);





        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = UserName.getText().toString().trim();
                String pass = PassWord.getText().toString().trim();

                if (user.isEmpty()) {
                    UserName.setError("Email cannot be Empty");
                }
                if (pass.isEmpty()){
                    PassWord.setError("Password cant be empty");
                } else{
                    auth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(SignUpForm.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpForm.this, LoginForm.class));
                            } else{
                                Toast.makeText(SignUpForm.this, "SignUp Failed" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }
            }
        });
        clickhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpForm.this, LoginForm.class));
            }
        });
    }
}