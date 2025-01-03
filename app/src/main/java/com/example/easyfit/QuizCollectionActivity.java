package com.example.easyfit;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuizCollectionActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private QuizCollectionAdapter quizAdapter;
    private ProgressBar progressBar;
    private List<Quiz> quizList;
    private DatabaseReference databaseReference;
    private ImageView IVBtnBack;
    private ImageView IVBtnSearch;
    private EditText ETSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_collection);

        // Initialize views
        recyclerView = findViewById(R.id.RVQuizCollection);
        progressBar = findViewById(R.id.PBQuizCollection);
        IVBtnBack = findViewById(R.id.IVBtnBack);
        IVBtnSearch = findViewById(R.id.IVBtnSearch);
        ETSearch = findViewById(R.id.ETSearch);

        // Initialize the list to store quizzes
        quizList = new ArrayList<>();

        // Initialize the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and set the OnClickListener
        quizAdapter = new QuizCollectionAdapter(this, quizList, new QuizCollectionAdapter.OnQuizClickListener() {
            @Override
            public void onQuizClick(String quizId) {
                // When a quiz title is clicked, pass the quizId to the next activity
                Intent intent = new Intent(QuizCollectionActivity.this, AnswerQuizActivity.class);
                intent.putExtra("quizId", quizId); // Pass the quizId to the next activity
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(quizAdapter);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("quizzes");

        // Fetch quiz data
        fetchQuizData();

        // Set OnClickListener for the back button
        IVBtnBack.setOnClickListener(v -> onBackPressed());

        // Inside onCreate method
        IVBtnSearch.setOnClickListener(v -> {
            if (ETSearch.getVisibility() == View.GONE) {
                // Make the EditText visible
                ETSearch.setVisibility(View.VISIBLE);

                // Optionally, you can also focus the EditText
                ETSearch.requestFocus();

                // You may want to scroll the RecyclerView to top or make space for the EditText
                // If your layout isn't shifting correctly, use the following code to scroll the RecyclerView
                recyclerView.smoothScrollToPosition(0);
            } else {
                // If it's already visible, just hide it
                ETSearch.setVisibility(View.GONE);
            }
        });

        EditText ETSearch = findViewById(R.id.ETSearch);
        ETSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Optional: You can implement behavior before the text changes, if needed.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Trigger the search method every time the text changes.
                String query = charSequence.toString().trim();
                if (!query.isEmpty()) {
                    // Call the method to search for quizzes matching the query
                    searchQuizzes(query);
                } else {
                    // If the query is empty, show all quizzes (or reset the results)
                    fetchQuizData();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Optional: Any behavior you want after the text has been changed.
            }
        });

    }

    private void fetchQuizData() {
        // Show progress bar while loading
        progressBar.setVisibility(View.VISIBLE);

        // Fetch all quiz data from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear the list before adding new data
                quizList.clear();

                // Iterate through each quiz entry
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get the Quiz object from the snapshot
                    Quiz quiz = snapshot.getValue(Quiz.class);

                    // Check if the Quiz object is not null
                    if (quiz != null) {
                        // Set the quizId to the Firebase key
                        quiz.setId(snapshot.getKey());

                        // Add the quiz to the list
                        quizList.add(quiz);
                    }
                }

                // Hide progress bar after loading data
                progressBar.setVisibility(View.GONE);

                // Notify the adapter that data has changed
                quizAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hide progress bar if there's an error
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void searchQuizzes(String searchQuery) {
        // Show progress bar while searching
        progressBar.setVisibility(View.VISIBLE);

        // Create a query to search for quizzes that match the search title
        Query searchQueryRef = databaseReference.orderByChild("title")
                .startAt(searchQuery)
                .endAt(searchQuery + "\uf8ff");

        // Fetch filtered data from Firebase
        searchQueryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear the list before adding new data
                quizList.clear();

                // Iterate through each quiz entry
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get the Quiz object from the snapshot
                    Quiz quiz = snapshot.getValue(Quiz.class);

                    // Check if the Quiz object is not null
                    if (quiz != null) {
                        // Set the quizId to the Firebase key
                        quiz.setId(snapshot.getKey());

                        // Add the quiz to the list
                        quizList.add(quiz);
                    }
                }

                // Hide progress bar after fetching data
                progressBar.setVisibility(View.GONE);

                // Notify the adapter that data has changed
                quizAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hide progress bar if there's an error
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
