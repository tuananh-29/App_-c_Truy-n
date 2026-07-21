package com.example.truyenmoingay.activities;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyenmoingay.R;
import com.example.truyenmoingay.RetrofitClient;
import com.example.truyenmoingay.adapters.ReaderPageAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReaderActivity extends AppCompatActivity {

    private boolean barsVisible = true;
    private RecyclerView rvPages;
    private ReaderPageAdapter adapter;
    private List<String> pageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        String slug = getIntent().getStringExtra("comic_slug");
        int chapterId = getIntent().getIntExtra("chapter_id", 1);
        String chapterTitle = getIntent().getStringExtra("chapter_title");

        // Gán tiêu đề
        ((TextView) findViewById(R.id.tvChapterTitle)).setText(chapterTitle);

        // Nút back
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Tap vào nội dung → ẩn/hiện thanh trên/dưới
        rvPages = findViewById(R.id.rvPages);
        // Note: RecyclerView clicks often need to be handled in the adapter/holder
        // but keeping existing logic as requested.
        rvPages.setOnClickListener(v -> toggleBars());

        rvPages.setLayoutManager(new LinearLayoutManager(this));

        // Gọi API lấy dữ liệu chương
        if (slug != null) {
            loadChapterData(slug, chapterId);
        } else {
            // Fallback to mock if no slug
            rvPages.setAdapter(new ReaderPageAdapter(getMockPages()));
        }

        // Nút chuyển chương
        findViewById(R.id.btnPrev).setOnClickListener(v ->
                Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show()
        );
        findViewById(R.id.btnNext).setOnClickListener(v ->
                Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show()
        );
    }

    private void loadChapterData(String slug, int chapter) {
        RetrofitClient.getApiService().getChapter(slug, chapter).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful() && responseBody != null) {
                        String json = responseBody.string();
                        JsonObject root = new Gson().fromJson(json, JsonObject.class);

                        // Giả sử JSON có dạng: { "data": { "images": ["url1", "url2"] } }
                        // Hoặc trực tiếp mảng images nếu API đơn giản hơn.
                        // Dựa vào project này, thường có wrap trong "data"
                        JsonArray images;
                        if (root.has("data") && root.get("data").isJsonObject()) {
                            images = root.getAsJsonObject("data").getAsJsonArray("images");
                        } else {
                            images = root.getAsJsonArray("images");
                        }

                        if (images != null) {
                            pageList.clear();
                            for (int i = 0; i < images.size(); i++) {
                                String url = images.get(i).getAsString();
                                // Thay thế IP localhost bằng 10.0.2.2 cho máy ảo Android
                                String fixedUrl = url.replace("127.0.0.1", "10.0.2.2")
                                        .replace("localhost", "10.0.2.2");
                                pageList.add(fixedUrl);
                            }

                            adapter = new ReaderPageAdapter(pageList);
                            rvPages.setAdapter(adapter);
                        }
                    } else {
                        Toast.makeText(ReaderActivity.this, "Không thể tải nội dung chương", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("ReaderActivity", "Error parsing JSON", e);
                    Toast.makeText(ReaderActivity.this, "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ReaderActivity", "API call failed", t);
                Toast.makeText(ReaderActivity.this, "Lỗi kết nối Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ── Mock Data: URL ảnh placeholder để xem layout ──────
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