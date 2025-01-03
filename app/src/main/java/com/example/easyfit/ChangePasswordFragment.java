package com.example.easyfit;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;

public class ChangePasswordFragment extends Fragment {

    private EditText emailEditText, currentPasswordEditText, newPasswordEditText, reenterNewPasswordEditText;
    private Button changePasswordButton;

    private FirebaseAuth mAuth;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        emailEditText = view.findViewById(R.id.emailEditText);
        currentPasswordEditText = view.findViewById(R.id.ETCurrentPassword);
        newPasswordEditText = view.findViewById(R.id.ETNewPassword);
        reenterNewPasswordEditText = view.findViewById(R.id.ETReenterNewPassword);
        changePasswordButton = view.findViewById(R.id.BtnChangePassword);

        // Set onClickListener for the change password button
        changePasswordButton.setOnClickListener(v -> changePassword());

        return view;
    }

    private void changePassword() {
        // Get the user input
        String email = emailEditText.getText().toString().trim();
        String currentPassword = currentPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String reenteredPassword = reenterNewPasswordEditText.getText().toString().trim();

        // Check if the fields are empty
        if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(reenteredPassword)) {
            Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the new password and re-entered password match
        if (!newPassword.equals(reenteredPassword)) {
            Toast.makeText(getContext(), "New passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        //Aunthenticate with email first
        if (email.equals(user.getEmail())){

            // Reauthenticate the user with the current password
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Once reauthenticated, update the password
                    user.updatePassword(newPassword).addOnCompleteListener(passwordUpdateTask -> {
                        if (passwordUpdateTask.isSuccessful()) {
                            // Password successfully updated
                            Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // Failed to update password
                            Toast.makeText(getContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Reauthentication failed (wrong old password)
                    Toast.makeText(getContext(), "Reauthentication failed. Please check your old password.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Wrong email!", Toast.LENGTH_SHORT).show();
        }

    }
}
