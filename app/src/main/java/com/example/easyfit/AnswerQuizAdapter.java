package com.example.easyfit;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswerQuizAdapter extends RecyclerView.Adapter<AnswerQuizViewHolder> {

    private List<Quiz.Question> questionList;
    private Map<Integer, Integer> selectedAnswers = new HashMap<>(); // Stores selected answer position for each question
    private boolean showAnswers = false; // Flag to show answers after "Check My Answer" button is clicked


    public AnswerQuizAdapter(List<Quiz.Question> questionList) {
        this.questionList = questionList;
    }


    @NonNull
    @Override
    public AnswerQuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for each question
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.answer_template, parent, false);  // The XML layout you provided
        return new AnswerQuizViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull AnswerQuizViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Quiz.Question question = questionList.get(position);

        // Set the question text
        holder.questionText.setText(question.getQuestionText());

        // Set the options for the radio buttons
        holder.option1.setText(question.getOptions().get(0));
        holder.option2.setText(question.getOptions().get(1));
        holder.option3.setText(question.getOptions().get(2));
        holder.option4.setText(question.getOptions().get(3));


        // Set the previously selected answer (if any)
        int selectedAnswer = selectedAnswers.getOrDefault(position, -1); // -1 means no answer selected
        if (selectedAnswer != -1) {
            RadioButton selectedRadioButton = (RadioButton) holder.radioGroup.getChildAt(selectedAnswer);
            selectedRadioButton.setChecked(true);
        } else {
            holder.radioGroup.clearCheck(); // Clear selection if no answer is selected
        }

        // Handle answer selection
        holder.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int selectedOption = -1; // Default if no option is selected

            // Using if-else instead of switch
            if (checkedId == R.id.RBOpt1) {
                selectedOption = 0; // Option 1 selected
            } else if (checkedId == R.id.RBOpt2) {
                selectedOption = 1; // Option 2 selected
            } else if (checkedId == R.id.RBOpt3) {
                selectedOption = 2; // Option 3 selected
            } else if (checkedId == R.id.RBOpt4) {
                selectedOption = 3; // Option 4 selected
            }

            // Store the selected answer in a map
            selectedAnswers.put(position, selectedOption);
        });


        // Explanation handling
        if (question.isExplanationVisible() == "Correct" || question.isExplanationVisible() == "Wrong") {
            holder.explanationContainer.setVisibility(View.VISIBLE);
            holder.explanationText.setVisibility((View.VISIBLE));
            holder.explanationText.setText(question.getExplanation());
            holder.TVCorrectAlpha.setVisibility(View.VISIBLE);
            holder.TVCorrectAlpha.setText(mapCorrectOptionToAlpha(question.getCorrectOption()));
            holder.ic_check.setVisibility(View.VISIBLE);

            if (question.isExplanationVisible() == "Correct"){
                holder.ic_check.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_check));
            } else {
                holder.ic_check.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_cross));
            }

        } else {
            holder.explanationContainer.setVisibility(View.GONE);
            holder.explanationText.setVisibility(View.GONE);
            holder.TVCorrectAlpha.setVisibility(View.GONE);
        }
    }

    private String mapCorrectOptionToAlpha(String correctOption) {
        switch (correctOption) {
            case "1":
                return "A";
            case "2":
                return "B";
            case "3":
                return "C";
            case "4":
                return "D";
            default:
                return "";
        }
    }


    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public Map<Integer, Integer> getSelectedAnswers() {
        return selectedAnswers;
    }

    public void showCorrectAnswers() {
        this.showAnswers = true; // Enable showing correct answers and explanations
        notifyDataSetChanged(); // Refresh the adapter to display answers
    }

}