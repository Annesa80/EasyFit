package com.example.easyfit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class balancedMealCheck extends AppCompatActivity {
    private String selectedportion_Carb;
    private String selectedportion_Pro;
    private String selectedportion_Fib;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_balanced_meal_check);

        Toast.makeText(this,
                "Welcome! Please click the portion of each category (Protein, Carbohydrate, Fiber) based on your meal intake.",
                Toast.LENGTH_LONG).show();

        TextView showResult = findViewById(R.id.result);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //jump to reference page
        Button checkRef = findViewById(R.id.checkReference);
        checkRef.setOnClickListener(view -> {
            Intent intent = new Intent(balancedMealCheck.this,eatingplatereference.class);
            startActivity(intent);
            finish();
        });

        //Button for Portion in Carbohydrate Group
        Button buttonCarb1 = findViewById(R.id.carb1);
        Button buttonCarb1_2 = findViewById(R.id.carb1_2);
        Button buttonCarb1_4 = findViewById(R.id.carb1_4);
        Button buttonCarb1_8 = findViewById(R.id.carb1_8);
        Button buttonCarb0 = findViewById(R.id.carb0);

        //Button for Portion in Protein Group
        Button buttonPro1 = findViewById(R.id.pro1);
        Button buttonPro1_2 = findViewById(R.id.pro1_2);
        Button buttonPro1_4 = findViewById(R.id.pro1_4);
        Button buttonPro1_8 = findViewById(R.id.pro1_8);
        Button buttonPro0 = findViewById(R.id.pro0);

        //Button for Portion in Fiber Group
        Button buttonFib1 = findViewById(R.id.fib1);
        Button buttonFib1_2 = findViewById(R.id.fib1_2);
        Button buttonFib1_4 = findViewById(R.id.fib1_4);
        Button buttonFib1_8 = findViewById(R.id.fib1_8);
        Button buttonFib0 = findViewById(R.id.fib0);

        //Define each button
        //Carbohydrate
        buttonCarb1.setOnClickListener(view -> selectedportion_Carb = "1");
        buttonCarb1_2.setOnClickListener(view -> selectedportion_Carb = "1/2");
        buttonCarb1_4.setOnClickListener(view -> selectedportion_Carb = "1/4");
        buttonCarb1_8.setOnClickListener(view -> selectedportion_Carb = "1/8");
        buttonCarb0.setOnClickListener(view -> selectedportion_Carb = "0");

        //Protein
        buttonPro1.setOnClickListener(view -> {
            selectedportion_Pro = "1";
//            buttonPro1.setBackgroundColor(Color.parseColor("FFFFFF"));
        });
        buttonPro1_2.setOnClickListener(view -> selectedportion_Pro = "1/2");
        buttonPro1_4.setOnClickListener(view -> selectedportion_Pro = "1/4");
        buttonPro1_8.setOnClickListener(view -> selectedportion_Pro = "1/8");
        buttonPro0.setOnClickListener(view -> selectedportion_Pro = "0");

        //Fiber
        buttonFib1.setOnClickListener(view -> selectedportion_Fib = "1");
        buttonFib1_2.setOnClickListener(view -> selectedportion_Fib = "1/2");
        buttonFib1_4.setOnClickListener(view -> selectedportion_Fib = "1/4");
        buttonFib1_8.setOnClickListener(view -> selectedportion_Fib = "1/8");
        buttonFib0.setOnClickListener(view -> selectedportion_Fib = "0");

        //check balanced meal of user
        Button submit = findViewById(R.id.submitMeal);
        submit.setOnClickListener(view -> {
            // Define standard balanced meal
            String standardCarbohydrate = "1/4";
            String standardProtein = "1/4";
            String standardFiber = "1/2";

            // Compare user choices with standard
            if (selectedportion_Carb.equals(standardCarbohydrate) &&
                    selectedportion_Pro.equals(standardProtein) &&
                    selectedportion_Fib.equals(standardFiber)) {
                // Show success message
                showResult.setText("Result: Balanced Diet!\uD83E\uDD73");
                showResult.setVisibility(View.VISIBLE);

                Toast.makeText(this, "Great! Your meal is balanced.", Toast.LENGTH_SHORT).show();
            } else {
                // Show feedback
                showResult.setText("Result: Not Balanced!\uD83D\uDE14 \nRecommended: Carbs: 1/4, Protein: 1/4, Fiber: 1/2.");
                showResult.setVisibility(View.VISIBLE);
            }
        });



    }
}