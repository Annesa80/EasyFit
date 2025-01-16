package com.example.easyfit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.easyfit.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.perf.FirebasePerformance;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ActivityMainBinding binding;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Firebase initialization
        FirebaseApp.initializeApp(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Log.d("FirebaseTest", "Firebase initialized: " + database.getReference().toString());

        // Authentication check
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            redirectToLogin();
        }

        // Edge-to-Edge display
        EdgeToEdge.enable(this);

        // Inflate the layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Default fragment
        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
            updateToolbar("Home", false);
        }

        // Handle Bottom Navigation
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            String toolbarTitle = "Home"; // Default title

            if (item.getItemId() == R.id.home) {
                selectedFragment = new HomeFragment();
                toolbarTitle = "Home";
            } else if (item.getItemId() == R.id.wellness_hub) {
                selectedFragment = new WellnessHubFragment();
                toolbarTitle = "Wellness Hub";
            } else if (item.getItemId() == R.id.profile) {
                selectedFragment = new ProfileFragment();
                toolbarTitle = "Profile";
            }

            // Replace the current fragment with the selected one
            if (selectedFragment != null) {
                replaceFragment(selectedFragment);
                updateToolbar(toolbarTitle, false);
            }
            return true;
        });

        // Edge-to-Edge system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Replace fragments dynamically
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        updateToolbarForFragment(fragment);
    }

    // Update the toolbar title and back button visibility
    public void updateToolbar(String title, boolean showBackButton) {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
    }

    public void updateToolbarForFragment(Fragment fragment) {
        if (getSupportActionBar() == null) return;

        if (fragment instanceof HomeFragment) {
            getSupportActionBar().setTitle("Home");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false); // No back button
        } else if (fragment instanceof WellnessHubFragment) {
            getSupportActionBar().setTitle("Wellness Hub");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false); // No back button
        }else if (fragment instanceof ProfileFragment) {
            getSupportActionBar().setTitle("Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false); // No back button
        } else if (fragment instanceof AddPostFragment) {
            getSupportActionBar().setTitle("Add Post");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Back button enabled
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        } else if (fragment instanceof CommentFragment) {
            getSupportActionBar().setTitle("Comments");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Back button enabled
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }



    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();

            // Update the toolbar title and back button dynamically
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
            if (currentFragment != null) {
                updateToolbarForFragment(currentFragment);
            }

        } else {
            super.onBackPressed(); // If no fragments, exit the app
        }
    }
    // Redirect to LoginActivity if user is not logged in
    public void redirectToLogin() {
        Intent intent = new Intent(this, login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // End MainActivity so the user cannot return without logging in
    }
}