package com.example.truyenmoingay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyenmoingay.R;
import com.example.truyenmoingay.adapters.ChapterAdapter;
import com.example.truyenmoingay.models.Chapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Arrays;
import java.util.List;

public class ComicDetailActivity extends AppCompatActivity {

    private List<Chapter> chapterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_detail);

        // 1. Nhận dữ liệu từ HomeActivity truyền sang
        String title  = getIntent().getStringExtra("comic_title");
        String author = getIntent().getStringExtra("comic_author");

        // Gán giá trị mặc định nếu lỡ test trực tiếp màn hình này mà không truyền Intent
        if (title == null) title = "Chú Thuật Hồi Chiến";
        if (author == null) author = "Gege Akutami";

        // 2. Ánh xạ View theo ĐÚNG ID CỦA FILE XML MỚI
        TextView tvComicTitle = findViewById(R.id.tvComicTitle);
        TextView tvAuthor = findViewById(R.id.tvAuthor);
        Button btnReadNow = findViewById(R.id.btnReadNow);
        TextView tvChapterLabel = findViewById(R.id.tvChapterLabel);
        RecyclerView rvChapters = findViewById(R.id.rvChapters);

        // Hiển thị thông tin
        if (tvComicTitle != null) tvComicTitle.setText(title);
        if (tvAuthor != null) tvAuthor.setText("Tác giả: " + author);

        // Lấy danh sách chương
        chapterList = getMockChapters();

        // 3. Cài đặt danh sách chương trên màn hình chính
        if (rvChapters != null) {
            rvChapters.setLayoutManager(new LinearLayoutManager(this));
            rvChapters.setAdapter(new ChapterAdapter(chapterList, this::handleChapterClick));
        }

        // 4. Bắt sự kiện nút "Đọc ngay"
        if (btnReadNow != null) {
            btnReadNow.setOnClickListener(v -> openReader(1, "Chương 1: Khởi đầu"));
        }

        // 5. Bắt sự kiện bấm vào chữ "Danh sách chương" để hiện BottomSheet
        if (tvChapterLabel != null) {
            tvChapterLabel.setOnClickListener(v -> showBottomSheetChapters());
        }
    }

    // ── HÀM HIỂN THỊ BẢNG TRƯỢT (BOTTOM SHEET) ─────────────────────────
    private void showBottomSheetChapters() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.layout_bottom_sheet_chapters);

        // Nút tắt bảng (X)
        ImageView btnClose = bottomSheetDialog.findViewById(R.id.btnCloseSheet);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> bottomSheetDialog.dismiss());
        }

        // Đổ danh sách chương vào RecyclerView của cái bảng
        RecyclerView rvChaptersSheet = bottomSheetDialog.findViewById(R.id.rvChaptersSheet);
        if (rvChaptersSheet != null) {
            rvChaptersSheet.setLayoutManager(new LinearLayoutManager(this));
            rvChaptersSheet.setAdapter(new ChapterAdapter(chapterList, chapter -> {
                // Tự động đóng bảng lại khi user chọn xong 1 chương
                bottomSheetDialog.dismiss();
                handleChapterClick(chapter);
            }));
        }

        bottomSheetDialog.show();
    }

    // ── LOGIC XỬ LÝ KHI BẤM VÀO CHƯƠNG (Dùng chung cho cả 2 chỗ) ────────
    private void handleChapterClick(Chapter chapter) {
        if (chapter.isLocked) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Chương bị khóa")
                    .setMessage("Cần " + chapter.coinCost + " coin để mở chương này.")
                    .setPositiveButton("OK", null)
                    .show();
        } else {
            openReader(chapter.id, chapter.title);
        }
    }

    // ── DỮ LIỆU ẢO (MOCK DATA) ──────────────────────────────────────────
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