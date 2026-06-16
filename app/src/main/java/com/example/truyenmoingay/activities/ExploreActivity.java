package com.example.truyenmoingay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.truyenmoingay.R;
import com.example.truyenmoingay.adapters.ComicAdapter;
import com.example.truyenmoingay.models.Comic;
import com.example.truyenmoingay.viewmodels.ExploreViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExploreActivity extends AppCompatActivity {

    private ExploreViewModel viewModel;
    private ComicAdapter adapter;
    private List<Comic> allComics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(ExploreViewModel.class);

        // Danh sách gốc
        allComics = getMockRanking();

        // Setup RecyclerView
        RecyclerView rv = findViewById(R.id.rvRanking);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // Khôi phục danh sách từ ViewModel nếu có
        List<Comic> restoredList = viewModel.getFilteredList();
        adapter = new ComicAdapter(
                restoredList != null ? restoredList : allComics,
                comic -> {
                    Intent i = new Intent(this, ComicDetailActivity.class);
                    i.putExtra("comic_id", comic.id);
                    i.putExtra("comic_title", comic.title);
                    i.putExtra("comic_author", comic.author);
                    startActivity(i);
                }
        );
        rv.setAdapter(adapter);

        // Khôi phục từ khóa tìm kiếm từ ViewModel
        EditText etSearch = findViewById(R.id.etSearch);
        String savedKeyword = viewModel.getSearchKeyword();
        if (!savedKeyword.isEmpty()) {
            etSearch.setText(savedKeyword);
            etSearch.setSelection(savedKeyword.length());
        }

        // Lắng nghe thay đổi tìm kiếm
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();
                viewModel.setSearchKeyword(keyword);
                filterComics(keyword);
            }
        });

        setupBottomNav();
    }

    private void filterComics(String keyword) {
        List<Comic> filtered = new ArrayList<>();
        for (Comic comic : allComics) {
            if (comic.title.toLowerCase().contains(keyword.toLowerCase())) {
                filtered.add(comic);
            }
        }
        viewModel.setFilteredList(filtered);
        adapter.updateData(filtered);
    }

    private List<Comic> getMockRanking() {
        return Arrays.asList(
                new Comic(1, "Kiếm Đạo Độc Tôn",  "Lê Văn C",    445, 4.9f),
                new Comic(2, "Võ Lâm Tranh Bá",    "Hoàng Văn E", 310, 4.7f),
                new Comic(3, "Hành Trình Tu Tiên", "Nguyễn Văn A", 230, 4.8f)
        );
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