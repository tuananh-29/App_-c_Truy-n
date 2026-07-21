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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
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
        ((TextView) findViewById(R.id.tvChapterTitle)).setText(chapterTitle != null ? chapterTitle : "Đang đọc");

        // Nút back
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Ánh xạ RecyclerView
        rvPages = findViewById(R.id.rvPages);
        rvPages.setLayoutManager(new LinearLayoutManager(this));

        // Tap vào nội dung → ẩn/hiện thanh trên/dưới
        rvPages.setOnClickListener(v -> toggleBars());

        // Các nút hành động ở BottomBar
        View btnLike = findViewById(R.id.btnLike);
        if (btnLike != null) btnLike.setOnClickListener(v -> Toast.makeText(this, "Đã thích truyện!", Toast.LENGTH_SHORT).show());

        View btnDislike = findViewById(R.id.btnDislike);
        if (btnDislike != null) btnDislike.setOnClickListener(v -> Toast.makeText(this, "Không thích", Toast.LENGTH_SHORT).show());

        View btnFollow = findViewById(R.id.btnFollow);
        if (btnFollow != null) btnFollow.setOnClickListener(v -> Toast.makeText(this, "Đã thêm vào danh sách Theo dõi", Toast.LENGTH_SHORT).show());

        View btnComment = findViewById(R.id.btnComment);
        if (btnComment != null) btnComment.setOnClickListener(v -> Toast.makeText(this, "Mở khu vực bình luận", Toast.LENGTH_SHORT).show());

        // Nút chuyển chương
        findViewById(R.id.btnPrev).setOnClickListener(v ->
                Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show()
        );
        findViewById(R.id.btnNext).setOnClickListener(v ->
                Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show()
        );

        // Gọi API lấy dữ liệu chương TỪ SERVER
        if (slug != null && !slug.isEmpty()) {
            loadChapterData(slug, chapterId);
        } else {
            // Nếu không có slug, báo lỗi và đóng trang ngay lập tức
            Toast.makeText(this, "Lỗi: Không tìm thấy dữ liệu truyện!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadChapterData(String slug, int chapter) {
        RetrofitClient.getApiService().getChapter(slug, chapter).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful() && responseBody != null) {
                        String json = responseBody.string();
                        Log.d("JSON_READER", json); // In ra để debug xem API trả về cái gì

                        JSONObject root = new JSONObject(json);
                        pageList.clear();

                        JSONObject dataObj = root.optJSONObject("data");

                        // TRƯỜNG HỢP 1: Cấu trúc gốc của OTruyen
                        if (dataObj != null && dataObj.has("item")) {
                            JSONObject itemObj = dataObj.optJSONObject("item");
                            if (itemObj != null) {
                                JSONArray chapterImageArray = itemObj.optJSONArray("chapter_image");
                                if (chapterImageArray != null && chapterImageArray.length() > 0) {
                                    String domainCdn = dataObj.optString("domain_cdn", "https://sv1.otruyencdn.com");
                                    String chapterPath = itemObj.optString("chapter_path", "");

                                    for (int i = 0; i < chapterImageArray.length(); i++) {
                                        JSONObject imgObj = chapterImageArray.optJSONObject(i);
                                        if (imgObj != null) {
                                            String imageFile = imgObj.optString("image_file", "");
                                            // Ghép 3 phần lại thành link ảnh hoàn chỉnh
                                            String fullUrl = domainCdn + "/" + chapterPath + "/" + imageFile;
                                            pageList.add(fullUrl);
                                        }
                                    }
                                }
                            }
                        }

                        // TRƯỜNG HỢP 2: Nếu API backend tự xử lý và trả về mảng trực tiếp
                        if (pageList.isEmpty()) {
                            JSONArray imagesArray = null;
                            if (dataObj != null && dataObj.has("images")) {
                                imagesArray = dataObj.optJSONArray("images");
                            } else if (root.has("images")) {
                                imagesArray = root.optJSONArray("images");
                            }

                            if (imagesArray != null && imagesArray.length() > 0) {
                                for (int i = 0; i < imagesArray.length(); i++) {
                                    String url = imagesArray.optString(i);
                                    if (url != null && !url.isEmpty()) {
                                        // Sửa lỗi hiển thị ảnh từ localhost trên máy ảo Android
                                        String fixedUrl = url.replace("127.0.0.1", "10.0.2.2").replace("localhost", "10.0.2.2");
                                        pageList.add(fixedUrl);
                                    }
                                }
                            }
                        }

                        // Kiểm tra kết quả bóc tách và đưa lên giao diện
                        if (!pageList.isEmpty()) {
                            adapter = new ReaderPageAdapter(pageList);
                            rvPages.setAdapter(adapter);
                        } else {
                            Toast.makeText(ReaderActivity.this, "Chương này chưa có ảnh", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(ReaderActivity.this, "Không thể tải nội dung chương", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("ReaderActivity", "Lỗi xử lý dữ liệu JSON: " + e.getMessage(), e);
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

    private void toggleBars() {
        barsVisible = !barsVisible;
        int vis = barsVisible ? View.VISIBLE : View.GONE;
        findViewById(R.id.layoutTopBar).setVisibility(vis);
        findViewById(R.id.layoutBottomBar).setVisibility(vis);
    }
}