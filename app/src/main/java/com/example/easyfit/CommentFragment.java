package com.example.easyfit;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyfit.adapters.AdapterComments;
import com.example.easyfit.models.ModelComment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentFragment extends Fragment {
    private String myUid, myEmail, myName, myDp, pId;
    private EditText ETComment;
    private ImageButton IBSend;
    private RecyclerView recyclerViewComments;
    private ProgressBar progressBar;

    List<ModelComment> commentList;
    AdapterComments adapterComments;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) requireActivity()).updateToolbar("Comments", true);

        View view = inflater.inflate(R.layout.fragment_comment, container, false);

        recyclerViewComments = view.findViewById(R.id.recyclerView_comments);
        ETComment = view.findViewById(R.id.ETComment);
        IBSend = view.findViewById(R.id.IBSend);
        progressBar = new ProgressBar(requireContext());

        recyclerViewComments.setLayoutManager(new LinearLayoutManager(requireContext()));

        if (getArguments() != null) {
            pId = getArguments().getString("pId");
        }

        checkUserStatus();
        loadComments();

        IBSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });

        return view;
    }

    private void checkUserStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            myUid = user.getUid();
            myEmail = user.getEmail();
            myName = (user.getDisplayName() != null) ? user.getDisplayName() : "Unknown";
            myDp = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : "default_image_url";
        }
    }

    private void loadComments() {
        if (pId == null || pId.isEmpty()) {
            Toast.makeText(getContext(), "Post ID is missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        commentList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(pId).child("Comments");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelComment modelComment = ds.getValue(ModelComment.class);
                    commentList.add(modelComment);
                }

                adapterComments = new AdapterComments(getContext(), commentList);
                recyclerViewComments.setAdapter(adapterComments);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to load comments: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postComment() {
        String comment = ETComment.getText().toString().trim();

        if (TextUtils.isEmpty(comment)) {
            Toast.makeText(getContext(), "Comment is empty...", Toast.LENGTH_SHORT).show();
            return;
        }

        String timeStamp = String.valueOf(System.currentTimeMillis());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(pId).child("Comments");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("cid", timeStamp);
        hashMap.put("comment", comment);
        hashMap.put("uid", myUid);
        hashMap.put("uEmail", myEmail);
        hashMap.put("uName", myName);
        hashMap.put("uDp", myDp);

        ref.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Comment Added...", Toast.LENGTH_SHORT).show();
                    ETComment.setText("");
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to add comment: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}