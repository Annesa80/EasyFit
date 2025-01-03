package com.example.easyfit;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.Context;

public class signup extends AppCompatActivity {

    EditText editTextname,editTextusername,editTextEmailAddress,editTextphoneNumber,editTextcountry,editTextPassword,editTextpasswordAgain;
    TextView signinLink;
    Button regButton;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    private DatabaseReference mDatabase;


    public void OnStart(){
        super.onStart();
        //Check if user is signed in and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        progressBar = findViewById(R.id.progressBar);
        editTextname = findViewById(R.id.name);
        editTextusername = findViewById(R.id.username);
        editTextEmailAddress = findViewById(R.id.EmailAddress);
        editTextphoneNumber = findViewById(R.id.phoneNumber);
        editTextcountry = findViewById(R.id.country);
        editTextPassword = findViewById(R.id.Password);
        editTextpasswordAgain = findViewById(R.id.passwordAgain);
        regButton = findViewById(R.id.createAccountButton);
        signinLink = findViewById(R.id.signInLink);

        //When user click sign in jump to page sign in
        signinLink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), login.class);
                startActivity(intent);
                finish();
            }
        });



        //When User click register button, enable them to register
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);
                String name = String.valueOf(editTextname.getText());
                String username = String.valueOf(editTextusername.getText());
                String email = String.valueOf(editTextEmailAddress.getText());
                String phoneNo = String.valueOf(editTextphoneNumber.getText());
                String country = String.valueOf(editTextcountry.getText());
                String password = String.valueOf(editTextPassword.getText());
                String confirmPassword = String.valueOf(editTextpasswordAgain.getText());
                String gender = "";
                String dob = "";
                String ProfileImageURL = "null";


                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(signup.this, "Enter Name : ", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(username)) {
                    Toast.makeText(signup.this, "Enter Username : ", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(email)) {
                    Toast.makeText(signup.this, "Enter Email : ", Toast.LENGTH_SHORT).show();
                    return;
                }

                Profile newUser = new Profile(name, username, email, gender, dob, phoneNo, country, ProfileImageURL);

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            //Signed in success, update ui with the user information
                            Toast.makeText(signup.this,"Account Created",Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(), login.class);
                            startActivity(intent);
                            finish();

                        }else{
                            //Sign In failed, display messages to user
                            Toast.makeText(signup.this,"Authentication failed.",Toast.LENGTH_SHORT).show();}

                    }
                });
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}