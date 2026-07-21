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
import com.example.truyenmoingay.utils.WalletManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComicDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvAuthor, tvDescription;
    private ImageView imgCover;
    private RecyclerView rvChapters; // Chuyển lên làm biến toàn cục

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_detail);

        // Ánh xạ View
        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvDescription = findViewById(R.id.tvDescription);
        imgCover = findViewById(R.id.imgCover);
        rvChapters = findViewById(R.id.rvChapters);
        rvChapters.setLayoutManager(new LinearLayoutManager(this)); // Chỉ khởi tạo Layout, chưa gắn Adapter

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

        // Gọi API lấy chi tiết
        if (slug != null) {
            loadComicDetail(slug);
        }

        // Nút Đọc ngay → mặc định mở chương 1
        Button btnRead = findViewById(R.id.btnReadNow);
        btnRead.setOnClickListener(v -> openReader(1, "Chương 1"));
    }

    private void loadComicDetail(String slug) {
        RetrofitClient.getApiService().getDetail(slug).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful() && responseBody != null) {
                        String json = responseBody.string();
                        JSONObject root = new JSONObject(json);

                        JSONObject dataObj = root.optJSONObject("data");
                        if (dataObj != null) {
                            JSONObject itemObj = dataObj.optJSONObject("item");

                            if (itemObj != null) {
                                // 1. BÓC TÁCH THÔNG TIN TRUYỆN
                                String title = itemObj.optString("name", "Đang cập nhật");

                                String author = "Đang cập nhật";
                                JSONArray authorArray = itemObj.optJSONArray("author");
                                if (authorArray != null && authorArray.length() > 0 && !authorArray.optString(0).isEmpty()) {
                                    author = authorArray.optString(0);
                                }

                                String description = itemObj.optString("content", "Không có mô tả");
                                description = description.replace("<p>", "").replace("</p>", "").replace("<br>", "\n").replace("<em>", "").replace("</em>", "");

                                String thumbFile = itemObj.optString("thumb_url", "");
                                String cdnDomain = "https://img.otruyenapi.com";
                                String coverUrl = thumbFile.isEmpty() ? "" : cdnDomain + "/uploads/comics/" + thumbFile;

                                // 2. BÓC TÁCH DANH SÁCH CHƯƠNG THẬT
                                List<Chapter> realChapters = new ArrayList<>();
                                JSONArray chaptersArray = itemObj.optJSONArray("chapters");
                                if (chaptersArray != null && chaptersArray.length() > 0) {
                                    JSONObject serverObj = chaptersArray.optJSONObject(0); // Lấy Server #1
                                    if (serverObj != null) {
                                        JSONArray serverData = serverObj.optJSONArray("server_data");
                                        if (serverData != null) {
                                            for (int i = 0; i < serverData.length(); i++) {
                                                JSONObject chapObj = serverData.optJSONObject(i);
                                                String chapName = chapObj.optString("chapter_name", String.valueOf(i + 1));

                                                // Logic thu phí: Từ chương 6 trở lên cần 5 coin để mở
                                                boolean isLocked = i >= 5;
                                                int coinCost = isLocked ? 5 : 0;

                                                Chapter chapter = new Chapter(
                                                        i + 1,
                                                        "Chương " + chapName,
                                                        isLocked,
                                                        coinCost,
                                                        "Mới cập nhật"
                                                );
                                                realChapters.add(chapter);
                                            }
                                        }
                                    }
                                }

                                // 3. ĐƯA DỮ LIỆU LÊN GIAO DIỆN
                                tvTitle.setText(title);
                                tvAuthor.setText("Tác giả: " + author);
                                tvDescription.setText(description);
                                if (getSupportActionBar() != null) {
                                    getSupportActionBar().setTitle(title);
                                }

                                if (!coverUrl.isEmpty()) {
                                    Glide.with(ComicDetailActivity.this)
                                            .load(coverUrl)
                                            .placeholder(R.drawable.bg_cover_placeholder)
                                            .into(imgCover);
                                }

                                // Gắn danh sách chương thật vào Adapter
                                rvChapters.setAdapter(new ChapterAdapter(realChapters, chapter -> {
                                    if (chapter.isLocked()) {
                                        WalletManager wallet = WalletManager.getInstance(ComicDetailActivity.this);
                                        if (wallet.spend(chapter.getCoinCost())) {
                                            Toast.makeText(ComicDetailActivity.this, "Mua chương thành công!", Toast.LENGTH_SHORT).show();
                                            openReader(chapter.getId(), chapter.getTitle());
                                        } else {
                                            Toast.makeText(ComicDetailActivity.this, "Không đủ xu, vui lòng nạp thêm", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        openReader(chapter.getId(), chapter.getTitle());
                                    }
                                }));
                            }
                        }
                    } else {
                        Toast.makeText(ComicDetailActivity.this, "Không thể tải thông tin truyện", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("ComicDetail", "Lỗi bóc tách JSON: " + e.getMessage(), e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ComicDetail", "Lỗi mạng: " + t.getMessage(), t);
                Toast.makeText(ComicDetailActivity.this, "Lỗi kết nối Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openReader(int chapterId, String chapterTitle) {
        Intent intent = new Intent(this, ReaderActivity.class);
        intent.putExtra("comic_slug",    getIntent().getStringExtra("comic_slug"));
        intent.putExtra("chapter_id",    chapterId);
        intent.putExtra("chapter_title", chapterTitle);
        startActivity(intent);
    }
}