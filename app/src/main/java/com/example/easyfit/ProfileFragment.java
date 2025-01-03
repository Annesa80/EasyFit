package com.example.easyfit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileFragment extends Fragment {

    private TextView nameValue, usernameValue, emailValue, genderValue, dobValue, phoneValue, countryValue, greeting;
    private ImageView profileImage, btnEdit, buttonLogout;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseStorage mStorage;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase Auth, Database, and Storage
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance();

        // Initialize TextViews to display profile data
        nameValue = view.findViewById(R.id.nameValue);
        usernameValue = view.findViewById(R.id.usernameValue);
        emailValue = view.findViewById(R.id.emailValue);
        genderValue = view.findViewById(R.id.genderValue);
        dobValue = view.findViewById(R.id.dobValue);
        phoneValue = view.findViewById(R.id.phoneValue);
        countryValue = view.findViewById(R.id.countryValue);
        greeting = view.findViewById(R.id.greeting);
        buttonLogout = view.findViewById(R.id.buttonLogout);

        // Initialize profile image ImageView
        profileImage = view.findViewById(R.id.profileImage);

        // Fetch the user profile from Firebase Realtime Database
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Profile userProfile = dataSnapshot.getValue(Profile.class);

                    // Set profile data in TextViews
                    nameValue.setText(userProfile.getName());
                    usernameValue.setText(userProfile.getUsername());
                    emailValue.setText(userProfile.getEmail());
                    genderValue.setText(userProfile.getGender());
                    dobValue.setText(userProfile.getDob());
                    phoneValue.setText(userProfile.getPhone());
                    countryValue.setText(userProfile.getCountry());
                    greeting.setText("Hi, " + userProfile.getName());

                    // Fetch the profile image URL and set it
                    String profileImageUrl = userProfile.getprofileImageURL() + ".jpg"; // Get the profile image URL from Profile object

                    Log.d("ProfileFragment", "Profile Image URL: " + profileImageUrl);  // Debugging log to verify the URL

                    if (!profileImageUrl.equals("null.jpg") && !profileImageUrl.isEmpty()) {
                        Glide.with(getContext())
                                .load(profileImageUrl) // Load the profile image from the URL
                                .into(profileImage); // Set the image into the ImageView
                    } else {
                        profileImage.setImageResource(R.drawable.profilepic); // Set a default image if URL is null
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
    });

        // Edit Profile button click to navigate to EditProfileFragment
        btnEdit = view.findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(v -> {
            // Replace the current fragment with EditProfileFragment
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, new EditProfileFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Set click listener to allow the user to upload a new profile picture
        View editProfileImage = view.findViewById(R.id.editProfileImage);
        editProfileImage.setOnClickListener(v -> {
            // Replace the current fragment with UploadProfilePicFragment
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, new UploadProfilePicFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        return view;
    }
}


