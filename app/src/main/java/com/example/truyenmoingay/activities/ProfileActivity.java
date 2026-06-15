package com.example.truyenmoingay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.truyenmoingay.R;
import com.example.truyenmoingay.utils.ComicDBHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private ComicDBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = ComicDBHelper.getInstance(this);

        findViewById(R.id.btnLogout).setOnClickListener(v ->
                Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
        );

        setupBottomNav();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStats();
    }

    private void updateStats() {
        // LẦN NÀY TỪ 4 BẢNG HOÀN TOÀN TÁCH BIỆT
        int favCount = db.getAllFavorites().size();     // Bảng Yêu thích
        int dislikeCount = db.getAllDislikes().size();  // Bảng Không thích
        int followCount = db.getAllFollows().size();    // Bảng Theo dõi
        int historyCount = db.getAllHistory().size();   // Bảng Lịch sử

        // Cập nhật Thư viện
        TextView tvFavCount = findViewById(R.id.tvFavCount);
        TextView tvDislikeCount = findViewById(R.id.tvDislikeCount);
        TextView tvFollowCountProfile = findViewById(R.id.tvFollowCountProfile);
        TextView tvHistoryCount = findViewById(R.id.tvHistoryCount);
        TextView tvFollowCountTop = findViewById(R.id.tvFollowCountTop);

        tvFavCount.setText(String.valueOf(favCount));
        tvDislikeCount.setText(String.valueOf(dislikeCount));
        tvFollowCountProfile.setText(String.valueOf(followCount));
        tvFollowCountTop.setText(String.valueOf(followCount)); // Hàng ngang phía trên cũng là Theo dõi
        tvHistoryCount.setText(String.valueOf(historyCount));
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

        findViewById(R.id.layoutHistory).setOnClickListener(v -> {
            startActivity(new Intent(this, HistoryActivity.class));
        });
    }
}