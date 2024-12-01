package com.example.smdassignment4;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    private EditText fullNameField, emailField, passwordField;
    private Button registerButton;
    private TextView loginRedirectText;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize UI components
        fullNameField = findViewById(R.id.etFullName);
        emailField = findViewById(R.id.etEmail);
        passwordField = findViewById(R.id.etPassword);
        registerButton = findViewById(R.id.btnRegister);
        loginRedirectText = findViewById(R.id.tvRedirect);

        // Register Button Click Listener
        registerButton.setOnClickListener(view -> {
            String fullName = fullNameField.getText().toString().trim();
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (TextUtils.isEmpty(fullName)) {
                fullNameField.setError("Full Name is required");
                return;
            }
            if (TextUtils.isEmpty(email)) {
                emailField.setError("Email is required");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                passwordField.setError("Password is required");
                return;
            }
            if (password.length() < 6) {
                passwordField.setError("Password must be at least 6 characters");
                return;
            }

            // Register user with Firebase Auth
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Automatically sign in the user after successful registration
                            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(signInTask -> {
                                        if (signInTask.isSuccessful()) {
                                            // Redirect to ShoppingListActivity
                                            Toast.makeText(SignupActivity.this, "Registration and Login Successful", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SignupActivity.this, ShoppingListActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(SignupActivity.this, "Login Error: " + signInTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(SignupActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // Redirect to Login Screen
        loginRedirectText.setOnClickListener(view -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
