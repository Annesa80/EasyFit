package com.example.easyfit;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AnswerQuizViewHolder extends RecyclerView.ViewHolder {
    TextView questionText, explanationText, TVCorrectAlpha;
    RadioGroup radioGroup;
    RadioButton option1, option2, option3, option4;
    LinearLayout explanationContainer;
    Button btnCheckMyAnswer;
    ImageView ic_check;

    public AnswerQuizViewHolder(@NonNull View itemView) {
        super(itemView);

        questionText = itemView.findViewById(R.id.TVQuestion);
        radioGroup = itemView.findViewById(R.id.RGOptions);
        option1 = itemView.findViewById(R.id.RBOpt1);
        option2 = itemView.findViewById(R.id.RBOpt2);
        option3 = itemView.findViewById(R.id.RBOpt3);
        option4 = itemView.findViewById(R.id.RBOpt4);
        btnCheckMyAnswer = itemView.findViewById(R.id.BtnCheckMyAnswer);
        explanationContainer = itemView.findViewById(R.id.LLExplanationContainer); // Ensure ID matches
        TVCorrectAlpha = itemView.findViewById(R.id.TVCorrectAlpha);
        explanationText = itemView.findViewById(R.id.TVAnsExplanation);
        ic_check = itemView.findViewById(R.id.IVIconCheck);
    }
}