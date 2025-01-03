package com.example.easyfit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.squareup.picasso.Picasso;

public class UploadProfilePicFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button choosePictureButton, uploadPictureButton;
    private ImageView profilePicturePlaceholder;
    private ProgressBar PBProfile;
    private Uri imageUri;
    private FirebaseStorage mStorage;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_profile_pic, container, false);

        choosePictureButton = view.findViewById(R.id.choosePictureButton);
        uploadPictureButton = view.findViewById(R.id.uploadPictureButton);
        profilePicturePlaceholder = view.findViewById(R.id.profilePicturePlaceholder);
        PBProfile = view.findViewById(R.id.PBProfile);

        mStorage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Load the current profile image from Firebase
        loadCurrentProfileImage();

        // Choose Picture Button: Open gallery to pick an image
        choosePictureButton.setOnClickListener(v -> openGallery());

        // Upload Picture Button: Upload image to Firebase
        uploadPictureButton.setOnClickListener(v -> uploadImageToFirebase());

        return view;
    }

    private void loadCurrentProfileImage() {
        PBProfile.setVisibility(View.VISIBLE);
        // Get the current user's UID from Firebase Auth
        String currentUserId = mAuth.getCurrentUser().getUid();

        // Reference to the 'users' node in Firebase Realtime Database
        DatabaseReference userRef = mDatabase.getReference("users").child(currentUserId);

        // Retrieve the user's details from the database
        userRef.get().addOnSuccessListener(snapshot -> {
            // Check if the snapshot exists
            if (snapshot.exists()) {

                String name = snapshot.child("name").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);

                // You can also load the profile image if needed
                String profileImageUrl = snapshot.child("profileImageURL").getValue(String.class);
                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    Picasso.get().load(profileImageUrl).into(profilePicturePlaceholder);
                } else {
                    Picasso.get().load(R.drawable.profilepic).into(profilePicturePlaceholder); // Default image
                }
            } else {
                // Handle case where user data doesn't exist or is incomplete
                Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
            }
            PBProfile.setVisibility(View.GONE);
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to load profile image", Toast.LENGTH_SHORT).show();
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(profilePicturePlaceholder); // Display selected image
        }
    }

    private void uploadImageToFirebase() {
        PBProfile.setVisibility(View.VISIBLE);
        if (imageUri != null) {
            StorageReference fileReference = mStorage.getReference("users")
                    .child(mAuth.getCurrentUser().getUid() + ".jpg");

            fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Get image URL
                    String imageUrl = uri.toString();
                    saveProfileImageToDatabase(imageUrl);
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to get image URL", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProfileImageToDatabase(String imageUrl) {
        DatabaseReference userRef = mDatabase.getReference("users")
                .child(mAuth.getCurrentUser().getUid());

        // Update the profileImageURL in Firebase
        userRef.child("profileImageURL").setValue(imageUrl).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                PBProfile.setVisibility(View.GONE);
                navigateToProfileFragment();
            } else {
                Toast.makeText(getContext(), "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToProfileFragment() {
        // Replace the current fragment with the ProfileFragment
        getFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, new ProfileFragment()) // Ensure ProfileFragment is the correct class
                .addToBackStack(null)  // Optional: If you want to add this transaction to the back stack
                .commit();
    }
}