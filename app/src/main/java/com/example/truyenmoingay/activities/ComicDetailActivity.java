package com.example.truyenmoingay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyenmoingay.R;
import com.example.truyenmoingay.models.adapters.ChapterAdapter;
import com.example.truyenmoingay.models.Chapter;
import com.example.truyenmoingay.models.Comic;
import com.example.truyenmoingay.utils.ComicDBHelper;

import java.util.Arrays;
import java.util.List;

public class ComicDetailActivity extends AppCompatActivity {

    private Comic currentComic;
    private ComicDBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_detail);

        db = ComicDBHelper.getInstance(this);

        int comicId = getIntent().getIntExtra("comic_id", 0);
        String title = getIntent().getStringExtra("comic_title");
        String author = getIntent().getStringExtra("comic_author");
        int chapterCount = getIntent().getIntExtra("comic_chapter_count", 0);
        float rating = getIntent().getFloatExtra("comic_rating", 0f);

        currentComic = new Comic(comicId, title, author, chapterCount, rating);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);
        toolbar.setNavigationOnClickListener(v -> finish());

        ((TextView) findViewById(R.id.tvTitle)).setText(title);
        ((TextView) findViewById(R.id.tvAuthor)).setText("Tác giả: " + author);
        ((TextView) findViewById(R.id.tvDescription)).setText(
                "Đây là mô tả của bộ truyện " + title + ". Nội dung hấp dẫn, nhiều tình tiết bất ngờ."
        );

        // ĐÃ XÓA HẲN PHẦN CODE XỬ LÝ NÚT NGÔI SAO (btnFavorite) Ở ĐÂY

        Button btnRead = findViewById(R.id.btnReadNow);
        btnRead.setOnClickListener(v -> {
            openReader(1, "Chương 1: Khởi đầu");
        });

        RecyclerView rvChapters = findViewById(R.id.rvChapters);
        rvChapters.setLayoutManager(new LinearLayoutManager(this));
        rvChapters.setAdapter(new ChapterAdapter(getMockChapters(), chapter -> {
            if (chapter.isLocked) {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Chương bị khóa")
                        .setMessage("Cần " + chapter.coinCost + " coin để mở chương này.")
                        .setPositiveButton("OK", null)
                        .show();
            } else {
                openReader(chapter.id, chapter.title);
            }
        }));
    }

    private List<Chapter> getMockChapters() {
        return Arrays.asList(
                new Chapter(1,  "Chương 1: Khởi đầu",        false, 0,  "01/01/2024"),
                new Chapter(2,  "Chương 2: Cuộc gặp gỡ",     false, 0,  "05/01/2024"),
                new Chapter(3,  "Chương 3: Bí ẩn hé lộ",     false, 0,  "10/01/2024"),
                new Chapter(4,  "Chương 4: Trận chiến đầu",  false, 0,  "15/01/2024"),
                new Chapter(5,  "Chương 5: Kẻ thù xuất hiện",true,  5,  "20/01/2024"),
                new Chapter(6,  "Chương 6: Sức mạnh mới",    true,  5,  "25/01/2024"),
                new Chapter(7,  "Chương 7: Đỉnh điểm",       true,  10, "30/01/2024"),
                new Chapter(8,  "Chương 8: Kết cục bất ngờ", true,  10, "05/02/2024")
        );
    }

    private void openReader(int chapterId, String chapterTitle) {
        // Lưu vào DB: Truyền thêm chapterId và chapterTitle
        db.addOrUpdateHistory(currentComic, chapterId, chapterTitle);

        Intent intent = new Intent(this, ReaderActivity.class);
        intent.putExtra("chapter_id",    chapterId);
        intent.putExtra("chapter_title", chapterTitle);
        intent.putExtra("comic_id", currentComic.id);
        intent.putExtra("comic_title", currentComic.title);
        intent.putExtra("comic_author", currentComic.author);
        intent.putExtra("comic_chapter_count", currentComic.chapterCount);
        intent.putExtra("comic_rating", currentComic.rating);
        startActivity(intent);
    }
}