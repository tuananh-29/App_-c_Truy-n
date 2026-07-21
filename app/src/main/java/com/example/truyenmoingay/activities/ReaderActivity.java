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

        // URL "chapter_api_data" lấy từ response detail() của truyện, KHÔNG phải slug/số chương
        // (endpoint cũ dạng /truyen-tranh/{slug}/chuong-{chapter} không tồn tại trên OTruyen API)
        String chapterApiData = getIntent().getStringExtra("chapter_api_data");
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
        if (chapterApiData != null && !chapterApiData.isEmpty()) {
            loadChapterData(chapterApiData);
        } else {
            // Fallback to mock nếu không có chapter_api_data (ví dụ Activity gọi thiếu extra)
            Log.w("ReaderActivity", "Thiếu extra 'chapter_api_data', dùng mock data");
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

    // chapterApiData: URL đầy đủ lấy từ response detail() của truyện,
    // trường item.chapters[].server_data[].chapter_api_data
    // (ví dụ: "https://sv1.otruyencdn.com/v1/api/chapter/691d4ff2a2ca9f8cba5a0b1d")
    private void loadChapterData(String chapterApiData) {
        // Gọi qua backend Laravel: GET /api/chuong-noi-dung?url=<chapterApiData>
        // Backend sẽ proxy sang đúng domain CDN thật (sv1.otruyencdn.com, sv2..., v.v.)
        RetrofitClient.getApiService().getChapterContent(chapterApiData).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful() && responseBody != null) {
                        String json = responseBody.string();
                        JsonObject root = new Gson().fromJson(json, JsonObject.class);

                        // Cấu trúc THẬT đã xác nhận từ chapter_api_data:
                        // {
                        //   "status": "success",
                        //   "data": {
                        //     "domain_cdn": "https://sv1.otruyencdn.com",
                        //     "item": {
                        //       "chapter_path": "uploads/20251119/xxx/chapter_1",
                        //       "chapter_image": [ { "image_page": 0, "image_file": "page_0.jpg" }, ... ]
                        //     }
                        //   }
                        // }
                        JsonArray images = null;
                        String domainCdn = null;
                        String chapterPath = null;

                        if (root.has("data") && root.get("data").isJsonObject()) {
                            JsonObject data = root.getAsJsonObject("data");

                            if (data.has("domain_cdn") && !data.get("domain_cdn").isJsonNull()) {
                                domainCdn = data.get("domain_cdn").getAsString();
                            }

                            if (data.has("item") && data.get("item").isJsonObject()) {
                                JsonObject item = data.getAsJsonObject("item");

                                if (item.has("chapter_path") && !item.get("chapter_path").isJsonNull()) {
                                    chapterPath = item.get("chapter_path").getAsString();
                                }
                                if (item.has("chapter_image") && item.get("chapter_image").isJsonArray()) {
                                    images = item.getAsJsonArray("chapter_image");
                                }
                            }
                        }

                        if (images != null && domainCdn != null && chapterPath != null && images.size() > 0) {
                            pageList.clear();
                            for (int i = 0; i < images.size(); i++) {
                                JsonObject img = images.get(i).getAsJsonObject();
                                String imageFile = img.has("image_file") && !img.get("image_file").isJsonNull()
                                        ? img.get("image_file").getAsString() : "";
                                if (imageFile.isEmpty()) continue;

                                // domain_cdn + "/" + chapter_path + "/" + image_file
                                String fullUrl = domainCdn + "/" + chapterPath + "/" + imageFile;

                                // Thay thế IP localhost bằng 10.0.2.2 (chỉ áp dụng nếu backend local trả về, thường không cần ở đây)
                                fullUrl = fullUrl.replace("127.0.0.1", "10.0.2.2")
                                        .replace("localhost", "10.0.2.2");
                                pageList.add(fullUrl);
                            }

                            if (!pageList.isEmpty()) {
                                adapter = new ReaderPageAdapter(pageList);
                                rvPages.setAdapter(adapter);
                            } else {
                                Toast.makeText(ReaderActivity.this, "Không có ảnh nào để hiển thị", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ReaderActivity.this, "Không có dữ liệu ảnh chương", Toast.LENGTH_SHORT).show();
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