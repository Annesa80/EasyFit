package com.example.easyfit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AddPostFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    EditText descriptionEt;
    ImageView imageIv;
    Uri imageUri = null;
    String uid, name, email, dp; // User data fields
    ProgressDialog pd;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_post, container, false);

        //Toolbar
        ((MainActivity) requireActivity()).updateToolbar("Post", true);

        descriptionEt = view.findViewById(R.id.editText_caption);
        imageIv = view.findViewById(R.id.IVPost);
        pd = new ProgressDialog(getContext());

        // Initialize Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Posts");

        view.findViewById(R.id.button_add_image).setOnClickListener(v -> showImagePickDialog());
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_submit_post) {
            String description = descriptionEt.getText().toString().trim();
            if (TextUtils.isEmpty(description)) {
                Toast.makeText(getContext(), "Enter Description", Toast.LENGTH_SHORT).show();
                return false;
            }
            uploadData(description);
            return true;
        }
        if (item.getItemId() == android.R.id.home) { // This is the back button ID in the toolbar
            getParentFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showImagePickDialog() {
        // Options for the dialog
        String[] options = {"Camera", "Gallery"};

        // Create an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose Image From");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Camera option selected
                pickFromCamera();
            } else if (which == 1) {
                // Gallery option selected
                pickFromGallery();
            }
        });
        builder.create().show();
    }

    private void pickFromCamera() {
        // Create an intent to capture an image from the camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        imageUri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, 100); // 100 is the request code for camera
    }

    private void pickFromGallery() {
        // Create an intent to pick an image from the gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 200); // 200 is the request code for gallery
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100) {
                // Image captured from camera
                imageIv.setImageURI(imageUri);
            } else if (requestCode == 200) {
                // Image selected from gallery
                imageUri = data.getData();
                imageIv.setImageURI(imageUri);
            }
        }
    }

    private void uploadData(String description) {
        pd.setMessage("Publishing post...");
        pd.show();

        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Posts/post_" + timeStamp;

        if (imageUri != null) {
            // Upload image to Firebase Storage
            StorageReference ref = FirebaseStorage.getInstance().getReference(filePathAndName);
            ref.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    savePostToRealtimeDatabase(description, downloadUri.toString(), timeStamp);
                }).addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(getContext(), "Failed to get image URL", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                pd.dismiss();
                Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
            });
        } else {
            savePostToRealtimeDatabase(description, "noImage", timeStamp);
        }
    }

    private void savePostToRealtimeDatabase(String description, String imageUrl, String timeStamp) {
//        //temporary
//        String uid = "user001"; // Replace with FirebaseAuth UID later
//        String email = "gemininorawit@gmail.com"; // Replace with FirebaseAuth email later
//        String name = "GN"; // Replace with FirebaseAuth name later
//        String dp = "drawable/gmn_profile.png"; // Replace with FirebaseAuth profile picture URL later
        // Get user details from FirebaseAuth
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String profilePic = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString() : "noImage";

        // Prepare data to save
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("uid", uid); // User ID
        postMap.put("uName", name); // User Name
        postMap.put("uEmail", email); // User Email
        postMap.put("uDp", dp); // User Profile Picture (if available, "noImage" otherwise)
        postMap.put("pId", timeStamp); // Post ID (based on timestamp)
        postMap.put("pDescr", description); // Post Description
        postMap.put("pImage", imageUrl); // Post Image URL (or "noImage" if no image)

        // Save to Realtime Database
        databaseReference.child(timeStamp).setValue(postMap)
                .addOnSuccessListener(aVoid -> {
                    pd.dismiss();
                    Toast.makeText(getContext(), "Post Published", Toast.LENGTH_SHORT).show();
                    descriptionEt.setText("");
                    imageIv.setImageURI(null);
                    imageUri = null;
                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(getContext(), "Failed to publish post", Toast.LENGTH_SHORT).show();
                });
    }
}