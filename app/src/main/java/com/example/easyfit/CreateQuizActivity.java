package com.example.easyfit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.easyfit.databinding.ActivityCreateQuizBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CreateQuizActivity extends AppCompatActivity {

    private ActivityCreateQuizBinding binding;
    private DatabaseReference mDatabase;
    private FirebaseDatabase db;
    private LinearLayout questionsLayout;  // Layout to hold all questions
    private View lastQuestionView;  // Track the last question template added
    private Button createQuizButton;  // Button to create the quiz
    private ImageView IVBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        // Initialize Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get reference to the questions layout
        questionsLayout = findViewById(R.id.questionsLayout);

        // Get reference to the create quiz button
        createQuizButton = findViewById(R.id.BtnCreateQuiz);

        // Add the first question
        addQuestion();

        // Set up click listener for the create quiz button
        createQuizButton.setOnClickListener(v -> saveQuizData());

        // Initialize views
        IVBtnBack = findViewById(R.id.IVBtnBack);

        // Set OnClickListener for the back button
        IVBtnBack.setOnClickListener(v -> {
            // Finish the current activity and return to the previous one
            onBackPressed(); // This is the same as calling finish()
        });
    }

    // Method to add a new question dynamically
    private void addQuestion() {
        // Inflate the question item layout (question_template.xml)
        LayoutInflater inflater = LayoutInflater.from(this);
        View questionView = inflater.inflate(R.layout.question_template, null);

        // Get references to the EditText (for question text) and RadioGroup (for options)
        EditText ETExplanation = questionView.findViewById(R.id.ETExplanation);
        EditText ETQuestion = questionView.findViewById(R.id.ETQuestion);
        RadioGroup answerGroup = questionView.findViewById(R.id.RGOpt);

        // Set up the add button functionality
        ImageView addIcon = questionView.findViewById(R.id.IVAdd);
        addIcon.setOnClickListener(v -> addQuestion());  // Add a new question when the add button is pressed

        // Add a TextWatcher to listen for text changes in the EditText
        ETExplanation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                // Only add a new question if there is text entered
                if (charSequence.length() == 1) {
                    // Only create a new question if it's the most recent one
                    if (questionView == lastQuestionView) {
                        addQuestion();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // No action needed here
            }
        });

        // Set up the delete button
        ImageView deleteIcon = questionView.findViewById(R.id.IVDelete);
        deleteIcon.setOnClickListener(v -> {
            // Remove the current question view from the layout
            questionsLayout.removeView(questionView);

            // If there are no questions left, hide the delete icon in the remaining questions
            updateDeleteIconVisibility();
        });

        // Add the question layout (with options) to the questionsLayout
        questionsLayout.addView(questionView);

        // Update the reference to the last added question
        lastQuestionView = questionView;

        // Update delete icon visibility
        updateDeleteIconVisibility();
    }

    // Method to update the visibility of delete icons based on the number of questions
    private void updateDeleteIconVisibility() {
        // Check if there is more than one question
        int childCount = questionsLayout.getChildCount();

        // Loop through all the questions and set visibility of delete icon
        for (int i = 0; i < childCount; i++) {
            View questionView = questionsLayout.getChildAt(i);
            ImageView deleteIcon = questionView.findViewById(R.id.IVDelete);

            if (childCount > 1) {
                deleteIcon.setVisibility(View.VISIBLE);  // Show delete icon if more than 1 question
            } else {
                deleteIcon.setVisibility(View.GONE);  // Hide delete icon if only 1 question
            }
        }
    }

    // Method to save the quiz data to Firebase
    private void saveQuizData() {
        // Get the quiz title
        EditText quizTitleEditText = findViewById(R.id.TVQuizTitle);
        String quizTitle = quizTitleEditText.getText().toString();

        // Collect the questions, options, and correct options from the UI
        List<Quiz.Question> questions = new ArrayList<>();
        boolean isValid = true;

        for (int i = 0; i < questionsLayout.getChildCount(); i++) {
            View questionView = questionsLayout.getChildAt(i);

            EditText questionEditText = questionView.findViewById(R.id.ETQuestion);
            RadioGroup optionsGroup = questionView.findViewById(R.id.RGOpt);
            EditText explanationEditText = questionView.findViewById(R.id.ETExplanation);

            String questionText = questionEditText.getText().toString();
            String explanation = explanationEditText.getText().toString();

            // Collect the 4 options from the EditText fields (not radio buttons)
            EditText option1EditText = questionView.findViewById(R.id.ETOpt1);
            EditText option2EditText = questionView.findViewById(R.id.ETOpt2);
            EditText option3EditText = questionView.findViewById(R.id.ETOpt3);
            EditText option4EditText = questionView.findViewById(R.id.ETOpt4);

            // Get the text of each option entered by the user
            List<String> options = new ArrayList<>();
            options.add(option1EditText.getText().toString());
            options.add(option2EditText.getText().toString());
            options.add(option3EditText.getText().toString());
            options.add(option4EditText.getText().toString());

            // Get the selected radio button to determine the correct option
            int selectedRadioButtonId = optionsGroup.getCheckedRadioButtonId();
            if (selectedRadioButtonId == -1) {
                // No radio button is selected, so the question is invalid
                isValid = false;
                break;  // Exit the loop if any question is missing a selected answer
            }

            RadioButton selectedRadioButton = questionView.findViewById(selectedRadioButtonId);
            String correctOption = selectedRadioButton.getText().toString();  // Get the text of the selected radio button

            // Create a new Question object and add it to the list
            Quiz.Question question = new Quiz.Question(questionText, options, correctOption, explanation);
            questions.add(question);
        }

        // If any question is missing a selected answer, show an error message
        if (!isValid) {
            Toast.makeText(this, "Please select a correct option for all questions.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the number of questions
        int numberOfQuestions = questions.size();

        // Create a new Quiz object
        Quiz quiz = new Quiz(quizTitle, numberOfQuestions, questions);

        // Save the quiz to Firebase with title, questions, and numberOfQuestions as separate fields
        String quizId = mDatabase.push().getKey(); // Generate a unique ID for the quiz

        if (quizId == null) {
            Log.e("Firebase", "Failed to generate unique quiz ID.");
            return;
        }

        // Save the quiz title, number of questions, and questions under the quizId node
        DatabaseReference quizRef = mDatabase.child("quizzes").child(quizId);

        // Save the quiz title first
        quizRef.child("numberOfQuestions").setValue(quiz.getNumberOfQuestions())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Save the number of questions
                        quizRef.child("title").setValue(quiz.getTitle())
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        // Save the questions under the 'questions' node
                                        quizRef.child("questions").setValue(quiz.getQuestions())
                                                .addOnCompleteListener(task2 -> {
                                                    if (task2.isSuccessful()) {
                                                        Log.d("Firebase", "Quiz data saved successfully.");
                                                        showQuizCreatedDialog();
                                                    } else {
                                                        Log.e("Firebase", "Failed to save quiz questions", task2.getException());
                                                        Toast.makeText(CreateQuizActivity.this, "Failed to create quiz. Try again.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        Log.e("Firebase", "Failed to save numberOfQuestions", task1.getException());
                                        Toast.makeText(CreateQuizActivity.this, "Failed to create quiz. Try again.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Log.e("Firebase", "Failed to save quiz title", task.getException());
                        Toast.makeText(CreateQuizActivity.this, "Failed to create quiz. Try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // Show a dialog after the quiz has been created
    private void showQuizCreatedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Quiz Created")
                .setMessage("Your quiz has been successfully created!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Create an intent to go back to the MainActivity
                        Intent intent = new Intent(CreateQuizActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear all activities on the stack before launching MainActivity
                        startActivity(intent); // Start MainActivity
                        finish();  // Close the CreateQuizActivity
                    }
                })
                .setCancelable(false)
                .show();
    }
}
