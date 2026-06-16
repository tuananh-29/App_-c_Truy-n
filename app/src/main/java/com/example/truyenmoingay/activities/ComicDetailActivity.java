package com.example.truyenmoingay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyenmoingay.R;
import com.example.truyenmoingay.adapters.ChapterAdapter;
import com.example.truyenmoingay.models.Chapter;

import java.util.Arrays;
import java.util.List;

public class ComicDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_detail);

        // Nhận dữ liệu từ HomeActivity
        String title  = getIntent().getStringExtra("comic_title");
        String author = getIntent().getStringExtra("comic_author");

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Hiển thị thông tin
        ((TextView) findViewById(R.id.tvTitle)).setText(title);
        ((TextView) findViewById(R.id.tvAuthor)).setText("Tác giả: " + author);
        ((TextView) findViewById(R.id.tvDescription)).setText(
                "Đây là mô tả của bộ truyện " + title + ". " +
                        "Nội dung hấp dẫn, nhiều tình tiết bất ngờ, " +
                        "thu hút hàng triệu độc giả trên toàn thế giới."
        );

        // Nút Đọc ngay → mở chương 1
        Button btnRead = findViewById(R.id.btnReadNow);
        btnRead.setOnClickListener(v -> openReader(1, "Chương 1: Khởi đầu"));

        // Danh sách chương mock
        RecyclerView rvChapters = findViewById(R.id.rvChapters);
        rvChapters.setLayoutManager(new LinearLayoutManager(this));
        rvChapters.setAdapter(new ChapterAdapter(getMockChapters(), chapter -> {
            if (chapter.isLocked()) {
                // Hiện thông báo đơn giản
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Chương bị khóa")
                        .setMessage("Cần " + chapter.getCoinCost() + " coin để mở chương này.")
                        .setPositiveButton("OK", null)
                        .show();
            } else {
                openReader(chapter.getId(), chapter.getTitle());
            }
        }));
    }

    // ── Mock Data ──────────────────────────────────────────
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
        Intent intent = new Intent(this, ReaderActivity.class);
        intent.putExtra("chapter_id",    chapterId);
        intent.putExtra("chapter_title", chapterTitle);
        startActivity(intent);
    }
}