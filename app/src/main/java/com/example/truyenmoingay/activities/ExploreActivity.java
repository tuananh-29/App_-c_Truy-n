package com.example.truyenmoingay.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.truyenmoingay.R;
import com.example.truyenmoingay.models.adapters.ComicAdapter;
import com.example.truyenmoingay.models.Comic;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Arrays;
import java.util.List;

public class ExploreActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        RecyclerView rv = findViewById(R.id.rvRanking);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new ComicAdapter(getMockRanking(), this::openDetail));

        setupBottomNav();
    }

    private List<Comic> getMockRanking() {
        return Arrays.asList(
                new Comic(1, "Kiếm Đạo Độc Tôn",  "Lê Văn C",    445, 4.9f),
                new Comic(2, "Võ Lâm Tranh Bá",    "Hoàng Văn E", 310, 4.7f),
                new Comic(3, "Hành Trình Tu Tiên", "Nguyễn Văn A",230, 4.8f)
        );
    }

    private void openDetail(Comic comic) {
        Intent i = new Intent(this, ComicDetailActivity.class);
        i.putExtra("comic_id", comic.id);
        i.putExtra("comic_title", comic.title);
        i.putExtra("comic_author", comic.author);
        i.putExtra("comic_chapter_count", comic.chapterCount); // Thêm dòng này
        i.putExtra("comic_rating", comic.rating);             // Thêm dòng này
        startActivity(i);
    }

    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setSelectedItemId(R.id.nav_explore);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class)); finish(); return true;
            } else if (id == R.id.nav_hashtag) {
                startActivity(new Intent(this, HashtagActivity.class)); finish(); return true;
            } else if (id == R.id.nav_following) {
                startActivity(new Intent(this, FollowingActivity.class)); finish(); return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class)); finish(); return true;
            }
            return true;
        });
    }
}