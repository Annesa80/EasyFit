package com.example.easyfit;

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

public class forgetPassword extends AppCompatActivity {

    private EditText emailEditText;
    private Button sendLinkButton;
    private FirebaseAuth mAuth;
    private TextView RememberPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Get references to views
        emailEditText = findViewById(R.id.EmailAddress);
        sendLinkButton = findViewById(R.id.sentlinkButton);
        RememberPassword = findViewById(R.id.RememberPassword);

        // Set click listener for the "Send Link" button
        sendLinkButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();

            // Validate the email input
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(forgetPassword.this, "Please enter your email address.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Send password reset email
            sendPasswordResetLink(email);
        });

        RememberPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void sendPasswordResetLink(String email) {
        // Send password reset email using Firebase Authentication
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Successfully sent the reset link
                        Toast.makeText(forgetPassword.this, "Password reset link sent to your email.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Failed to send the reset link
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Error sending password reset email.";
                        Toast.makeText(forgetPassword.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
