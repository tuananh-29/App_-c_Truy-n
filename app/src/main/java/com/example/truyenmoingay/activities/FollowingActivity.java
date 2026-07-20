package com.example.truyenmoingay.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.truyenmoingay.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FollowingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        setupBottomNav();
    }

    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setSelectedItemId(R.id.nav_following);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class)); finish(); return true;
            } else if (id == R.id.nav_hashtag) {
                startActivity(new Intent(this, HashtagActivity.class)); finish(); return true;
            } else if (id == R.id.nav_explore) {
                startActivity(new Intent(this, ExploreActivity.class)); finish(); return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class)); finish(); return true;
            }
            return true;
        });
    }
}