package com.example.easyfit;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.media.MediaPlayer;
public class AnswerQuizActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private DatabaseReference databaseReference;
    private ImageView IVBtnBack, IVAudio, IVMute;
    private TextView TVToolbarTitle;
    private RecyclerView recyclerView;
    private AnswerQuizAdapter answerQuizAdapter;
    private ProgressBar progressBar;
    private List<Quiz.Question> questionList;
    private String quizId;
    private Button btnCheckMyAnswer;
    private String isExplanationVisible = "";
    private List<String> userAnswers = new ArrayList<>();


    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_quiz);

        // Initialize MediaPlayer with the audio file from raw resources
        mediaPlayer = MediaPlayer.create(this, R.raw.christmas_spirit);

        // Start playing the music
        Log.d("MediaPlayer", "Playing background music...");
        mediaPlayer.start();

        // Optionally, set loop to true if you want it to play repeatedly
        mediaPlayer.setLooping(true);

        // Back button click
        IVBtnBack = findViewById(R.id.IVBtnBack);  // Reference to the back icon in the toolbar
        IVAudio = findViewById(R.id.IVAudio);  // Audio button
        IVMute = findViewById(R.id.IVMute);  // Mute button

        IVBtnBack.setOnClickListener(v -> {
            onBackPressed(); // This simulates the back button press, returning to the previous activity
        });


        // Get quizId from the Intent
        quizId = getIntent().getStringExtra("quizId");

        // Initialize Firebase reference using the quizId dynamically
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("quizzes").child(quizId);  // Dynamically use quizId from Intent

        // Set up Title on Toolbar
        TVToolbarTitle = findViewById(R.id.TVToolbarTitle);
        fetchQuizTitle();


        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        btnCheckMyAnswer = findViewById(R.id.BtnCheckMyAnswer);
        progressBar = findViewById(R.id.progressBar);  // Initialize the ProgressBar
        questionList = new ArrayList<>();

        // Set up RecyclerView & Adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        answerQuizAdapter = new AnswerQuizAdapter(questionList);
        recyclerView.setAdapter(answerQuizAdapter);


        // Fetch quiz questions
        fetchQuizQuestions();


        // Set onClickListener for the "Check My Answer" button
        btnCheckMyAnswer.setOnClickListener(v -> { checkAnswers();
            if (userAnswers.size() != 0) {

                int x = 0;
                for (Quiz.Question question : questionList) {
                    if (question.getCorrectOption().equals(userAnswers.get(x)))
                        isExplanationVisible = "Correct";
                    else
                        isExplanationVisible = "Wrong";
                    question.setExplanationVisible(isExplanationVisible);
                    x++;
                }

                answerQuizAdapter.notifyDataSetChanged();
            }
        });

        // IVAudio click listener (to pause the music and show IVMute)
        IVAudio.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause(); // Pause the music
                IVAudio.setVisibility(View.GONE); // Hide IVAudio
                IVMute.setVisibility(View.VISIBLE); // Show IVMute
            }
        });

        // IVMute click listener (to resume the music and show IVAudio)
        IVMute.setOnClickListener(v -> {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start(); // Start the music
                IVAudio.setVisibility(View.VISIBLE); // Show IVAudio
                IVMute.setVisibility(View.GONE); // Hide IVMute
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause the music when the activity goes into the background
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume playing the music if the activity comes back to the foreground
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release the media player when the activity is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    private void fetchQuizTitle() {
        // Fetch the title of the quiz from Firebase using the dynamic quizId
        databaseReference.child("title").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String quizTitle = dataSnapshot.getValue(String.class);  // Retrieve the quiz title
                if (quizTitle != null) {
                    TVToolbarTitle.setText(quizTitle);  // Set the title on the TextView in the toolbar
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error (you can show a Toast or log the error)
                Log.e("FirebaseError", "Failed to load quiz title", databaseError.toException());
            }
        });
    }

    private void fetchQuizQuestions() {
        // Show progress bar while loading
        progressBar.setVisibility(View.VISIBLE);

        // Fetch questions dynamically from Firebase based on quizId
        databaseReference.child("questions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                questionList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Quiz.Question question = snapshot.getValue(Quiz.Question.class);
                    if (question != null) {
                        questionList.add(question);
                    }
                }
                answerQuizAdapter.notifyDataSetChanged();  // Notify the adapter that data has changed

                // Hide progress bar after loading data
                progressBar.setVisibility(View.GONE);  // This was missing in the original code
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
                progressBar.setVisibility(View.GONE);  // Ensure progress bar hides in case of an error
            }
        });
    }


    private void checkAnswers() {
        Map<Integer, Integer> selectedAnswers = answerQuizAdapter.getSelectedAnswers();

        // Check if all questions have been answered
        for (int i = 0; i < questionList.size(); i++) {
            int selectedOption = selectedAnswers.getOrDefault(i, -1);
            Log.d("fast a", "fass " + selectedOption);
            // If any question is unanswered (selectedOption == -1), show a toast and exit
            if (selectedOption == -1) {
                Toast.makeText(this, "Please answer all questions!", Toast.LENGTH_SHORT).show();
                return;  // Exit the method so the answers are not checked
            }
        }


        // Check answers (You can compare with the correct answers here)
        boolean allCorrect = true;
        for (int i = 0; i < questionList.size(); i++) {
            Quiz.Question question = questionList.get(i);
            int selectedOption = selectedAnswers.getOrDefault(i, -1);

            // Map the selected option (integer) to the corresponding string value
            String selectedOptionString = mapSelectedOptionToString(selectedOption);

            // Compare the selected option string with the correct option string from Firebase
            if (selectedOption == -1 || !selectedOptionString.equals(question.getCorrectOption())) {
                allCorrect = false;
            }

            // Store all user answers in List Array
            userAnswers.add(selectedOptionString);
        }

        // Show result
        if (allCorrect) {
            Toast.makeText(this, "All Answers are Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Some Answers are Incorrect!", Toast.LENGTH_SHORT).show();
        }
    }

    private String mapSelectedOptionToString(int selectedOption) {
        switch (selectedOption) {
            case 0:
                return "1";
            case 1:
                return "2";
            case 2:
                return "3";
            case 3:
                return "4";
            default:
                return "";
        }
    }
}