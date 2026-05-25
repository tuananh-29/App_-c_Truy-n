package com.example.truyenmoingay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

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

        // 1. Lưới 2 cột - Mới cập nhật (Kiểm tra null)
        RecyclerView rvGrid = findViewById(R.id.rvGrid);
        if (rvGrid != null) {
            rvGrid.setLayoutManager(new GridLayoutManager(this, 2));
            rvGrid.setAdapter(new ComicAdapter(mockData, this::openDetail));
        }

        // 2. Danh sách dọc - Top truyện (Kiểm tra null)
        RecyclerView rvList = findViewById(R.id.rvList);
        if (rvList != null) {
            rvList.setLayoutManager(new LinearLayoutManager(this));
            rvList.setAdapter(new ComicAdapter(mockData, this::openDetail));
        }

        // 3. BottomNav — (Kiểm tra null)
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        if (nav != null) {
            nav.setSelectedItemId(R.id.nav_home);
            nav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_hashtag) {
                    startActivity(new Intent(this, HashtagActivity.class));
                } else if (id == R.id.nav_following) {
                    startActivity(new Intent(this, FollowingActivity.class));
                } else if (id == R.id.nav_explore) {
                    startActivity(new Intent(this, ExploreActivity.class));
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(this, ProfileActivity.class));
                }
                return true; // Bỏ các lệnh finish() đi để người dùng có thể bấm Back quay lại được
            });
        } else {
            // Cảnh báo nhỏ hiện lên màn hình để bạn biết XML đang thiếu BottomNav
            Toast.makeText(this, "Chưa tìm thấy bottomNav trong XML!", Toast.LENGTH_SHORT).show();
        }
    }

    private List<Comic> getMockComics() {
        return Arrays.asList(
                new Comic(1, "One Piece",          "Oda Eiichiro",   1100, 4.9f),
                new Comic(2, "Naruto",             "Masashi Kishi",  700,  4.8f),
                new Comic(3, "Demon Slayer",       "Koyoharu G.",    205,  4.8f),
                new Comic(4, "Attack on Titan",    "Hajime Isayama", 139,  4.9f),
                new Comic(5, "My Hero Academia",   "Kōhei Horikoshi", 430,  4.7f),
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