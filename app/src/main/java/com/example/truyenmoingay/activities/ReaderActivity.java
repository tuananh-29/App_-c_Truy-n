package com.example.truyenmoingay.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyenmoingay.R;
import com.example.truyenmoingay.adapters.ReaderPageAdapter;
import com.example.truyenmoingay.models.Comic;
import com.example.truyenmoingay.utils.ComicDBHelper;

import java.util.Arrays;
import java.util.List;

public class ReaderActivity extends AppCompatActivity {

    private boolean barsVisible = true;
    private Comic currentComic;
    private ComicDBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        db = ComicDBHelper.getInstance(this);

        // Nhận dữ liệu Comic từ ComicDetailActivity truyền sang
        int comicId = getIntent().getIntExtra("comic_id", 0);
        String title = getIntent().getStringExtra("comic_title");
        String author = getIntent().getStringExtra("comic_author");
        int chapterCount = getIntent().getIntExtra("comic_chapter_count", 0);
        float rating = getIntent().getFloatExtra("comic_rating", 0f);
        currentComic = new Comic(comicId, title, author, chapterCount, rating);

        String chapterTitle = getIntent().getStringExtra("chapter_title");

        ((TextView) findViewById(R.id.tvChapterTitle)).setText(chapterTitle);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        RecyclerView rvPages = findViewById(R.id.rvPages);
        rvPages.setOnClickListener(v -> toggleBars());

        rvPages.setLayoutManager(new LinearLayoutManager(this));
        rvPages.setAdapter(new ReaderPageAdapter(getMockPages()));

        findViewById(R.id.btnPrev).setOnClickListener(v ->
                Toast.makeText(this, "Đây là chương đầu tiên", Toast.LENGTH_SHORT).show()
        );
        findViewById(R.id.btnNext).setOnClickListener(v ->
                Toast.makeText(this, "Chuyển chương tiếp theo", Toast.LENGTH_SHORT).show()
        );

        // ── XỬ LÝ 4 NÚT HÀNH ĐỘNG ──
        setupActionButtons();
    }

    private void setupActionButtons() {
        Button btnLike = findViewById(R.id.btnLike);
        Button btnDislike = findViewById(R.id.btnDislike);
        Button btnFollow = findViewById(R.id.btnFollow);
        Button btnComment = findViewById(R.id.btnComment);

        // Cập nhật trạng thái nút lần đầu mở
        updateActionButtonText(btnLike, db.isFavorite(currentComic.id) ? "❤️ Đã thích" : "🤍 Thích");
        updateActionButtonText(btnDislike, db.isDisliked(currentComic.id) ? "👎 Đã bỏ qua" : "👎 Ko thích");
        updateActionButtonText(btnFollow, db.isFollowed(currentComic.id) ? "🔔 Đã theo dõi" : "Theo dõi");

        btnLike.setOnClickListener(v -> {
            if (db.isFavorite(currentComic.id)) {
                db.removeFavorite(currentComic.id);
                updateActionButtonText(btnLike, "🤍 Thích");
                Toast.makeText(this, "Đã bỏ thích", Toast.LENGTH_SHORT).show();
            } else {
                db.addFavorite(currentComic);
                db.removeDislike(currentComic.id); // Thích thì xóa ko thích
                updateActionButtonText(btnLike, "❤️ Đã thích");
                updateActionButtonText(btnDislike, "👎 Ko thích");
                Toast.makeText(this, "Đã thích truyện", Toast.LENGTH_SHORT).show();
            }
        });

        btnDislike.setOnClickListener(v -> {
            if (db.isDisliked(currentComic.id)) {
                db.removeDislike(currentComic.id);
                updateActionButtonText(btnDislike, "👎 Ko thích");
                Toast.makeText(this, "Đã hủy bỏ qua", Toast.LENGTH_SHORT).show();
            } else {
                db.addDislike(currentComic);
                db.removeFavorite(currentComic.id); // Ko thích thì xóa thích
                updateActionButtonText(btnDislike, "👎 Đã bỏ qua");
                updateActionButtonText(btnLike, "🤍 Thích");
                Toast.makeText(this, "Đã thêm vào không thích", Toast.LENGTH_SHORT).show();
            }
        });

        btnFollow.setOnClickListener(v -> {
            if (db.isFollowed(currentComic.id)) {
                db.removeFollow(currentComic.id);
                updateActionButtonText(btnFollow, "Theo dõi");
                Toast.makeText(this, "Đã hủy theo dõi", Toast.LENGTH_SHORT).show();
            } else {
                db.addFollow(currentComic);
                updateActionButtonText(btnFollow, "🔔 Đã theo dõi");
                Toast.makeText(this, "Đã theo dõi truyện", Toast.LENGTH_SHORT).show();
            }
        });

        btnComment.setOnClickListener(v ->
                Toast.makeText(this, "Chức năng bình luận đang phát triển!", Toast.LENGTH_SHORT).show()
        );
    }

    private void updateActionButtonText(Button btn, String text) {
        btn.setText(text);
    }

    private List<String> getMockPages() {
        return Arrays.asList(
                "https://picsum.photos/seed/p1/400/600",
                "https://picsum.photos/seed/p2/400/600",
                "https://picsum.photos/seed/p3/400/600",
                "https://picsum.photos/seed/p4/400/600",
                "https://picsum.photos/seed/p5/400/600",
                "https://picsum.photos/seed/p6/400/600"
        );
    }

    private void toggleBars() {
        barsVisible = !barsVisible;
        int vis = barsVisible ? View.VISIBLE : View.GONE;
        findViewById(R.id.layoutTopBar).setVisibility(vis);
        findViewById(R.id.layoutBottomBar).setVisibility(vis);
    }
}