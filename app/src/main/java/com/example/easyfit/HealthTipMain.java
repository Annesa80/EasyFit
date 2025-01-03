package com.example.easyfit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HealthTipMain extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HealthTipAdapter adapter;
    private List<String> healthTips;

    private FirebaseFirestore firestore;
    private FirebaseDatabase realtimeDatabase;
    private DatabaseReference realtimeRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_tip_main);

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance();
        realtimeDatabase = FirebaseDatabase.getInstance();
        realtimeRef = realtimeDatabase.getReference("healthTips");

        // Initialize RecyclerView and FloatingActionButton
        recyclerView = findViewById(R.id.recyclerView_health_tips);
        FloatingActionButton fabAddTip = findViewById(R.id.fab_add_tip);

        // Initialize data
        healthTips = new ArrayList<>();
        adapter = new HealthTipAdapter(healthTips);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Fetch health tips from Firebase Realtime Database
        fetchHealthTipsFromRealtimeDatabase();

        // Handle Add Button Click
        fabAddTip.setOnClickListener(v -> showAddTipDialog());
    }

    private void showAddTipDialog() {
        // Inflate dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_tip, null);
        EditText inputTip = dialogView.findViewById(R.id.input_tip);

        // Build dialog
        new AlertDialog.Builder(this)
                .setTitle("Add Health Tip")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String tip = inputTip.getText().toString().trim();
                    if (!tip.isEmpty()) {
                        addHealthTip(tip);
                    } else {
                        Toast.makeText(this, "Please enter a valid tip!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addHealthTip(String tip) {
        // Add tip to local list
        healthTips.add(tip);
        adapter.notifyItemInserted(healthTips.size() - 1);

        // Save to Firestore
        saveToFirestore(tip);

        // Save to Realtime Database
        saveToRealtimeDatabase(tip);
    }
    private void fetchHealthTipsFromRealtimeDatabase() {
        // Attach a listener to read the healthTips node
        realtimeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                healthTips.clear();  // Clear existing data in the list

                // Loop through the children of the healthTips node
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    healthTips tip = snapshot.getValue(healthTips.class);  // Convert snapshot to healthTips object
                    if (tip != null) {
                        healthTips.add(tip.getTip());  // Add the health tip text to the list
                    }
                }

                // Notify the adapter that data has changed
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HealthTipMain.this, "Failed to retrieve Health Tips", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void saveToFirestore(String tip) {
        // Create a new HealthTip object
        healthTips healthTip = new healthTips(tip);

        // Save to Firestore
        firestore.collection("healthTips")
                .add(healthTip)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Health Tip added to Firestore", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to add tip to Firestore", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveToRealtimeDatabase(String tip) {
        // Create a new HealthTip object
        String tipId = realtimeRef.push().getKey();  // Generate a unique key for the tip
        healthTips healthTip = new healthTips(tip);

        // Save to Realtime Database
        if (tipId != null) {
            realtimeRef.child(tipId).setValue(healthTip)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Health Tip added to Realtime Database", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to add tip to Realtime Database", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}