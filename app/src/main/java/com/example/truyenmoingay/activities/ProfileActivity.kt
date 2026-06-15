package com.example.truyenmoingay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.truyenmoingay.R;
import com.example.truyenmoingay.utils.WalletManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvCoinBalance;
    private WalletManager wallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        wallet = WalletManager.getInstance(this);

        tvCoinBalance = findViewById(R.id.tvCoinBalance);
        updateCoinBalance();

        findViewById(R.id.btnTopUp).setOnClickListener(v ->
                startActivity(new Intent(this, TopUpActivity.class))
        );

        findViewById(R.id.btnLogout).setOnClickListener(v ->
                Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
        );

        setupBottomNav();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCoinBalance();
    }

    private void updateCoinBalance() {
        tvCoinBalance.setText(wallet.getBalance() + " xu");
    }

    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setSelectedItemId(R.id.nav_profile);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class)); finish(); return true;
            } else if (id == R.id.nav_hashtag) {
                startActivity(new Intent(this, HashtagActivity.class)); finish(); return true;
            } else if (id == R.id.nav_following) {
                startActivity(new Intent(this, FollowingActivity.class)); finish(); return true;
            } else if (id == R.id.nav_explore) {
                startActivity(new Intent(this, ExploreActivity.class)); finish(); return true;
            }
            return true;
        });
    }
}
