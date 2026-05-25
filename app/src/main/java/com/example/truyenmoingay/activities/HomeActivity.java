package com.example.truyenmoingay.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyenmoingay.R;
import com.example.truyenmoingay.adapters.ComicAdapter;
import com.example.truyenmoingay.models.Comic;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        List<Comic> mockData = getMockComics();

        // Lưới 2 cột - Mới cập nhật
        RecyclerView rvGrid = findViewById(R.id.rvGrid);
        rvGrid.setLayoutManager(new GridLayoutManager(this, 2));
        rvGrid.setAdapter(new ComicAdapter(mockData, this::openDetail));

        // Danh sách dọc - Top truyện
        RecyclerView rvList = findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(new ComicAdapter(mockData, this::openDetail));

        // BottomNav — phải nằm BÊN TRONG onCreate
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setSelectedItemId(R.id.nav_home);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_hashtag) {
                startActivity(new Intent(this, HashtagActivity.class)); finish(); return true;
            } else if (id == R.id.nav_following) {
                startActivity(new Intent(this, FollowingActivity.class)); finish(); return true;
            } else if (id == R.id.nav_explore) {
                startActivity(new Intent(this, ExploreActivity.class)); finish(); return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class)); finish(); return true;
            }
            return true;
        });
    }

    private List<Comic> getMockComics() {
        return Arrays.asList(
                new Comic(1, "One Piece",          "Oda Eiichiro",   1100, 4.9f),
                new Comic(2, "Naruto",             "Masashi Kishi",  700,  4.8f),
                new Comic(3, "Demon Slayer",       "Koyoharu G.",    205,  4.8f),
                new Comic(4, "Attack on Titan",    "Hajime Isayama", 139,  4.9f),
                new Comic(5, "My Hero Academia",   "Kōhei Horikoshi",430,  4.7f),
                new Comic(6, "Dragon Ball",        "Akira Toriyama", 519,  4.8f)
        );
    }

    private void openDetail(Comic comic) {
        Intent intent = new Intent(this, ComicDetailActivity.class);
        intent.putExtra("comic_id",     comic.id);
        intent.putExtra("comic_title",  comic.title);
        intent.putExtra("comic_author", comic.author);
        startActivity(intent);
    }
}