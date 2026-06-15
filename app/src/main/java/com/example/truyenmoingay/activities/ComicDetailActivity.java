package com.example.truyenmoingay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.truyenmoingay.R;
import com.example.truyenmoingay.adapters.ChapterAdapter;
import com.example.truyenmoingay.models.Chapter;
import com.example.truyenmoingay.models.Comic;
import com.example.truyenmoingay.utils.ComicDBHelper;
import java.util.Arrays;
import java.util.List;

public class ComicDetailActivity extends AppCompatActivity {

    private Comic currentComic;
    private ComicDBHelper db;
    private ImageButton btnFavorite;
    private boolean isFav = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_detail);

        db = ComicDBHelper.getInstance(this);

        // Nhận dữ liệu Comic từ Intent
        int comicId = getIntent().getIntExtra("comic_id", 0);
        String title = getIntent().getStringExtra("comic_title");
        String author = getIntent().getStringExtra("comic_author");
        int chapterCount = getIntent().getIntExtra("comic_chapter_count", 0);
        float rating = getIntent().getFloatExtra("comic_rating", 0f);

        currentComic = new Comic(comicId, title, author, chapterCount, rating);

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
                "Đây là mô tả của bộ truyện " + title + ". Nội dung hấp dẫn, nhiều tình tiết bất ngờ."
        );

        // XỬ LÝ NÚT YÊU THÍCH
        btnFavorite = findViewById(R.id.btnFavorite);
        isFav = db.isFavorite(comicId);
        updateFavButton();

        btnFavorite.setOnClickListener(v -> {
            if (isFav) {
                db.removeFavorite(currentComic.id);
                Toast.makeText(this, "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
            } else {
                db.addFavorite(currentComic);
                Toast.makeText(this, "Đã thêm vào yêu thích ❤️", Toast.LENGTH_SHORT).show();
            }
            isFav = !isFav;
            updateFavButton();
        });

        // Nút Đọc ngay & LƯU LỊCH SỬ
        Button btnRead = findViewById(R.id.btnReadNow);
        btnRead.setOnClickListener(v -> {
            db.addOrUpdateHistory(currentComic);
            openReader(1, "Chương 1: Khởi đầu");
        });

        // Danh sách chương mock
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
                db.addOrUpdateHistory(currentComic);
                openReader(chapter.id, chapter.title);
            }
        }));
    }

    private void updateFavButton() {
        if (isFav) {
            btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            btnFavorite.setImageResource(android.R.drawable.btn_star_big_off);
        }
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
        Intent intent = new Intent(this, ReaderActivity.class);
        intent.putExtra("chapter_id",    chapterId);
        intent.putExtra("chapter_title", chapterTitle);
        startActivity(intent);
    }
}