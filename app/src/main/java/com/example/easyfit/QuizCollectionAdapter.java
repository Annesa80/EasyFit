package com.example.easyfit;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class QuizCollectionAdapter extends RecyclerView.Adapter<QuizCollectionAdapter.QuizViewHolder> {
    private Context context;
    private List<Quiz> quizList;
    private OnQuizClickListener listener;

    // Interface for handling click events
    public interface OnQuizClickListener {
        void onQuizClick(String quizId);
    }

    public QuizCollectionAdapter(Context context, List<Quiz> quizList, OnQuizClickListener listener) {
        this.context = context;
        this.quizList = quizList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.quiz_item, parent, false);
        return new QuizViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizList.get(position);
        holder.quizTitleTextView.setText(quiz.getTitle()); // Set quiz title
        holder.TVNumberOfQuestions.setText("Number of questions: " + Integer.toString(quiz.getNumberOfQuestions())); // Set number of questions
        // Handle click event for the title
        holder.quizTitleTextView.setOnClickListener(v -> {
            // Pass the quizId to the listener (which will be handled by the activity)
            listener.onQuizClick(quiz.getId());
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView quizTitleTextView;
        TextView TVNumberOfQuestions;

        public QuizViewHolder(View itemView) {
            super(itemView);
            quizTitleTextView = itemView.findViewById(R.id.TVQuizTitles); // TextView for quiz title
            TVNumberOfQuestions = itemView.findViewById(R.id.TVNumberOfQuestions); // TextView for number of questions
        }
    }
}
