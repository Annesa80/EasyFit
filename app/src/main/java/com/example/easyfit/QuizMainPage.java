package com.example.easyfit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class QuizMainPage extends AppCompatActivity {

    private ImageView IVBtnBack;
    private Button btnCreateQuiz;
    private Button btnAnswerQuiz;
    private TextView Title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_main_page);

        // Back button click
        IVBtnBack = findViewById(R.id.IVBtnBack);  // Reference to the back icon in the toolbar

        IVBtnBack.setOnClickListener(v -> {
            onBackPressed(); // This simulates the back button press, returning to the previous activity
        });

        // Initialize buttons
        btnCreateQuiz = findViewById(R.id.BtnCreateQuiz);
        btnAnswerQuiz = findViewById(R.id.btnAnswerQuiz);

        // Initialize Text View
        TextView TVTitle = findViewById(R.id.TVToolbarTitle);
        TVTitle.setText("Quizzes");

        // Set onClickListener for "Create Quiz" button
        btnCreateQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch CreateQuizActivity when the button is clicked
                Intent intent = new Intent(QuizMainPage.this, CreateQuizActivity.class);
                startActivity(intent);
            }
        });

        // Set onClickListener for "Answer Quiz" button
        btnAnswerQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch QuizCollectionActivity when the button is clicked
                Intent intent = new Intent(QuizMainPage.this, QuizCollectionActivity.class);
                startActivity(intent);
            }
        });
    }
}
