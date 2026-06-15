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
import com.example.truyenmoingay.adapters.ChapterAdapter;
import com.example.truyenmoingay.models.Chapter;
import com.example.truyenmoingay.utils.WalletManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComicDetailActivity extends AppCompatActivity {

    private WalletManager wallet;
    private ChapterAdapter chapterAdapter;
    private List<Chapter> chapters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_detail);

        wallet = WalletManager.getInstance(this);

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
        chapters = getMockChapters();

        RecyclerView rvChapters = findViewById(R.id.rvChapters);
        rvChapters.setLayoutManager(new LinearLayoutManager(this));
        chapterAdapter = new ChapterAdapter(chapters, this::onChapterClick);
        rvChapters.setAdapter(chapterAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại trạng thái khóa/mở khi quay lại màn (vd: sau khi nạp xu)
        if (chapterAdapter != null) {
            chapterAdapter.notifyDataSetChanged();
        }
    }

    private void onChapterClick(Chapter chapter) {
        boolean locked = chapter.isLocked && !wallet.isChapterUnlocked(chapter.id);

        if (!locked) {
            openReader(chapter.id, chapter.title);
            return;
        }

        int balance = wallet.getBalance();

        if (wallet.canAfford(chapter.coinCost)) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Mở khóa chương")
                    .setMessage("Dùng " + chapter.coinCost + " xu để mở \"" + chapter.title + "\"?\n\n"
                            + "Số dư hiện tại: " + balance + " xu")
                    .setPositiveButton("Mở khóa", (d, w) -> {
                        boolean success = wallet.unlockChapter(chapter.id, chapter.coinCost);
                        if (success) {
                            chapterAdapter.notifyDataSetChanged();
                            Toast.makeText(this,
                                    "Đã mở khóa! Còn lại " + wallet.getBalance() + " xu",
                                    Toast.LENGTH_SHORT).show();
                            openReader(chapter.id, chapter.title);
                        } else {
                            Toast.makeText(this, "Không đủ xu!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        } else {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Không đủ xu")
                    .setMessage("Chương này cần " + chapter.coinCost + " xu, bạn còn " + balance + " xu.\n\n"
                            + "Nạp thêm xu để mở khóa chương này?")
                    .setPositiveButton("Nạp xu", (d, w) ->
                            startActivity(new Intent(this, TopUpActivity.class)))
                    .setNegativeButton("Hủy", null)
                    .show();
        }
    }

    // ── Mock Data ──────────────────────────────────────────
    private List<Chapter> getMockChapters() {
        return new ArrayList<>(Arrays.asList(
                new Chapter(1,  "Chương 1: Khởi đầu",        false, 0,  "01/01/2024"),
                new Chapter(2,  "Chương 2: Cuộc gặp gỡ",     false, 0,  "05/01/2024"),
                new Chapter(3,  "Chương 3: Bí ẩn hé lộ",     false, 0,  "10/01/2024"),
                new Chapter(4,  "Chương 4: Trận chiến đầu",  false, 0,  "15/01/2024"),
                new Chapter(5,  "Chương 5: Kẻ thù xuất hiện",true,  5,  "20/01/2024"),
                new Chapter(6,  "Chương 6: Sức mạnh mới",    true,  5,  "25/01/2024"),
                new Chapter(7,  "Chương 7: Đỉnh điểm",       true,  10, "30/01/2024"),
                new Chapter(8,  "Chương 8: Kết cục bất ngờ", true,  10, "05/02/2024")
        ));
    }

    private void openReader(int chapterId, String chapterTitle) {
        Intent intent = new Intent(this, ReaderActivity.class);
        intent.putExtra("chapter_id",    chapterId);
        intent.putExtra("chapter_title", chapterTitle);
        startActivity(intent);
    }
}
