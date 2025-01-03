package com.example.easyfit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class EditProfileFragment extends Fragment {

    private EditText nameEditText, usernameEditText, emailEditText, genderEditText, dobEditText,
            phoneEditText, countryEditText;
    private TextView EditPasswordClick;
    private String profileImageURL;

    private Button updateProfileButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    public static EditProfileFragment newInstance() {
        return new EditProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize EditText views
        nameEditText = view.findViewById(R.id.nameEditText);
        usernameEditText = view.findViewById(R.id.usernameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        genderEditText = view.findViewById(R.id.genderEditText);
        dobEditText = view.findViewById(R.id.dobEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        countryEditText = view.findViewById(R.id.countryEditText);

        EditPasswordClick = view.findViewById(R.id.EditPasswordClick);
        updateProfileButton = view.findViewById(R.id.updateProfileButton);

        // Fetch the current user's data from Firebase and populate the EditText fields
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get user profile data
                    Profile profile = dataSnapshot.getValue(Profile.class);
                    if (profile != null) {
                        // Set the EditText fields with existing user data
                        nameEditText.setText(profile.getName());
                        usernameEditText.setText(profile.getUsername());
                        emailEditText.setText(profile.getEmail());
                        genderEditText.setText(profile.getGender());
                        dobEditText.setText(profile.getDob());
                        phoneEditText.setText(profile.getPhone());
                        countryEditText.setText(profile.getCountry());
                        profileImageURL = profile.getprofileImageURL();

                    }
                } else {
                    Toast.makeText(getContext(), "No user data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });

        EditPasswordClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();

                // Begin the FragmentTransaction to replace the current fragment with the new one
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, changePasswordFragment) // replace container with ChangePasswordFragment
                        .addToBackStack(null) // Optional: this allows the user to navigate back to the previous fragment
                        .commit();
            }
        });

        // On Click Listener for Update Profile Button
        updateProfileButton.setOnClickListener(v -> {
            // Get the input data
            String name = nameEditText.getText().toString().trim();
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String gender = genderEditText.getText().toString().trim();
            String dob = dobEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String country = countryEditText.getText().toString().trim();

            // Validate input fields
            if (name.isEmpty() || username.isEmpty() || email.isEmpty() || gender.isEmpty() ||
                    dob.isEmpty() || phone.isEmpty() || country.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a Profile object with the updated data
            Profile updatedProfile = new Profile(name, username, email, gender, dob, phone, country, profileImageURL);

            // Save the updated profile to Firebase Realtime Database
            mDatabase.child("users").child(userId).setValue(updatedProfile)
                    .addOnSuccessListener(aVoid -> {
                        // Show success message
                        Toast.makeText(getContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show();

                        // Navigate back to ProfileFragment
                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, new ProfileFragment());
                        transaction.addToBackStack(null); // So user can navigate back
                        transaction.commit();
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                    });
        });
        return view;
    }
}