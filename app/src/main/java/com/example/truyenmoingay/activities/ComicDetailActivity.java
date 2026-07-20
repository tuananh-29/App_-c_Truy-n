package com.example.truyenmoingay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.truyenmoingay.R;
import com.example.truyenmoingay.RetrofitClient;
import com.example.truyenmoingay.adapters.ChapterAdapter;
import com.example.truyenmoingay.models.Chapter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComicDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvAuthor, tvDescription;
    private ImageView imgCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_detail);

        // Ánh xạ View
        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvDescription = findViewById(R.id.tvDescription);
        imgCover = findViewById(R.id.imgCover);

        // Nhận dữ liệu từ HomeActivity
        String slug = getIntent().getStringExtra("comic_slug");
        String title = getIntent().getStringExtra("comic_title");
        String author = getIntent().getStringExtra("comic_author");

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title != null ? title : "Chi tiết truyện");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Hiển thị dữ liệu tạm thời từ Intent (nếu có)
        if (title != null) tvTitle.setText(title);
        if (author != null) tvAuthor.setText("Tác giả: " + author);

        // Gọi API lấy chi tiết
        if (slug != null) {
            loadComicDetail(slug);
        }

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

    private void loadComicDetail(String slug) {
        RetrofitClient.getApiService().getDetail(slug).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful() && responseBody != null) {
                        String json = responseBody.string();
                        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);

                        // Giả sử cấu trúc JSON trả về có các trường: title, author, description, cover
                        // LƯU Ý: Nếu API Laravel của bạn trả về key khác (ví dụ ten_truyen), hãy đổi chữ "title" thành "ten_truyen" nhé
                        String title = jsonObject.has("title") ? jsonObject.get("title").getAsString() : "Không rõ tiêu đề";
                        String author = jsonObject.has("author") ? jsonObject.get("author").getAsString() : "Không rõ tác giả";
                        String description = jsonObject.has("description") ? jsonObject.get("description").getAsString() : "Không có mô tả";
                        String coverUrl = jsonObject.has("cover") ? jsonObject.get("cover").getAsString() : "";

                        // Cập nhật UI
                        tvTitle.setText(title);
                        tvAuthor.setText("Tác giả: " + author);
                        tvDescription.setText(description);
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle(title);
                        }

                        // Load ảnh bìa bằng Glide và fix lỗi IP máy ảo
                        if (!coverUrl.isEmpty()) {
                            String fixedCoverUrl = coverUrl.replace("127.0.0.1", "10.0.2.2").replace("localhost", "10.0.2.2");

                            Glide.with(ComicDetailActivity.this)
                                    .load(fixedCoverUrl)
                                    .placeholder(R.drawable.bg_cover_placeholder) // Đã đổi sang placeholder chuẩn của project
                                    .error(R.drawable.ic_launcher_background)
                                    .into(imgCover);
                        }
                    } else {
                        Toast.makeText(ComicDetailActivity.this, "Không thể tải thông tin truyện", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("ComicDetail", "Error parsing JSON", e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ComicDetail", "API call failed", t);
                Toast.makeText(ComicDetailActivity.this, "Lỗi kết nối Server", Toast.LENGTH_SHORT).show();
            }
        });
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