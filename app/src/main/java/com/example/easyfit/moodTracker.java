package com.example.easyfit;



import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;


public class moodTracker extends AppCompatActivity {

    private RadioGroup radioGroup;
    private Button btnSaveMood;
    private CalendarView calendarView;
    private RecyclerView recyclerView;
    private String selectedDate = "";
    private DatabaseReference databaseReference;
    private ArrayList<mood> moodList;
    private moodAdapter moodAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moodtracker);

        radioGroup = findViewById(R.id.radioGroup);
        btnSaveMood = findViewById(R.id.btn_save_mood);
        calendarView = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.recyclerView);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Moods");

        // Set default date as today's date
        selectedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());


        // Update selected date on calendar change
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) ->
                selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year)
        );

        // RecyclerView setup
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        moodList = new ArrayList<>();
        moodAdapter = new moodAdapter(moodList);
        recyclerView.setAdapter(moodAdapter);
        RecyclerView.ItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

        // Save mood to Firebase
        btnSaveMood.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(moodTracker.this, "Please select a mood", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedMoodButton = findViewById(selectedId);
            String selectedMood = selectedMoodButton.getText().toString();

            String id = databaseReference.push().getKey();
            mood Mood = new mood(selectedMood, selectedDate);

            if (id != null) {
                databaseReference.child(id).setValue(Mood)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(moodTracker.this, "Mood saved successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(moodTracker.this, "Failed to save mood.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Fetch and display moods
        fetchMoods();
    }

    private void fetchMoods() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                moodList.clear();
                for (DataSnapshot moodSnapshot : snapshot.getChildren()) {
                    mood Mood = moodSnapshot.getValue(mood.class);
                    if (Mood != null) {
                        moodList.add(Mood);
                    }
                }

                // Sort moods by date (latest to oldest)
                Collections.sort(moodList, (mood1, mood2) -> mood2.getDate().compareTo(mood1.getDate()));

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Collections.sort(moodList, (mood1, mood2) -> {
                    try {
                        Date date1 = sdf.parse(mood1.getDate());
                        Date date2 = sdf.parse(mood2.getDate());
                        return date1.compareTo(date2); // Latest date first

                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0;
                    }
                });

                // Notify adapter about data changes
                moodAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(moodTracker.this, "Failed to load moods.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
