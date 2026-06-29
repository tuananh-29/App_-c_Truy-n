package com.example.truyenmoingay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.truyenmoingay.R;
import com.example.truyenmoingay.adapters.ComicAdapter; // SỬA LẠI ĐƯỜNG DẪN NÀY
import com.example.truyenmoingay.models.Comic;
import com.example.truyenmoingay.utils.ComicDBHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class FollowingActivity extends AppCompatActivity {

    private ComicDBHelper db;
    private RecyclerView rvFollowing;
    private ComicAdapter adapter;
    private TextView tvFollowCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        db = ComicDBHelper.getInstance(this);
        tvFollowCount = findViewById(R.id.tvFollowCount);
        rvFollowing = findViewById(R.id.rvFollowing);
        rvFollowing.setLayoutManager(new LinearLayoutManager(this));

        setupBottomNav();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void loadFavorites() {
        List<Comic> favList = db.getAllFavorites();
        tvFollowCount.setText(favList.size() + " truyện đang theo dõi");

        if (adapter == null) {
            adapter = new ComicAdapter(favList, comic -> {
                Intent i = new Intent(this, ComicDetailActivity.class);
                i.putExtra("comic_id", comic.id);
                i.putExtra("comic_title", comic.title);
                i.putExtra("comic_author", comic.author);
                i.putExtra("comic_chapter_count", comic.chapterCount);
                i.putExtra("comic_rating", comic.rating);
                startActivity(i);
            });
            rvFollowing.setAdapter(adapter);
        } else {
            adapter.updateData(favList);
        }
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