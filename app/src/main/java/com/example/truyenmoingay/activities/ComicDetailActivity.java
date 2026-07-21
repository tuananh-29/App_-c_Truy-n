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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComicDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvAuthor, tvDescription;
    private ImageView imgCover;
    private RecyclerView rvChapters;

    // Danh sách chương thật lấy từ API, song song với chapterApiDataList theo cùng index
    // (Chapter.id được gán = vị trí trong list, để không cần sửa model Chapter)
    private final List<Chapter> chapterList = new ArrayList<>();
    private final List<String> chapterApiDataList = new ArrayList<>();

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

        // RecyclerView danh sách chương - khởi tạo rỗng, điền dữ liệu thật sau khi API trả về
        rvChapters = findViewById(R.id.rvChapters);
        rvChapters.setLayoutManager(new LinearLayoutManager(this));
        rvChapters.setAdapter(new ChapterAdapter(chapterList, chapter -> {
            if (chapter.isLocked()) {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Chương bị khóa")
                        .setMessage("Cần " + chapter.getCoinCost() + " coin để mở chương này.")
                        .setPositiveButton("OK", null)
                        .show();
            } else {
                int index = chapter.getId();
                if (index >= 0 && index < chapterApiDataList.size()) {
                    openReader(chapterApiDataList.get(index), chapter.getTitle());
                } else {
                    Toast.makeText(this, "Không tìm thấy dữ liệu chương", Toast.LENGTH_SHORT).show();
                }
            }
        }));

        // Nút Đọc ngay → mở chương đầu tiên (chỉ hoạt động sau khi danh sách chương đã tải xong)
        Button btnRead = findViewById(R.id.btnReadNow);
        btnRead.setOnClickListener(v -> {
            if (!chapterApiDataList.isEmpty()) {
                openReader(chapterApiDataList.get(0), chapterList.get(0).getTitle());
            } else {
                Toast.makeText(this, "Danh sách chương đang tải, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }
        });

        // Gọi API lấy chi tiết + danh sách chương
        if (slug != null) {
            loadComicDetail(slug);
        }
    }

    private void loadComicDetail(String slug) {
        RetrofitClient.getApiService().getDetail(slug).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful() && responseBody != null) {
                        String json = responseBody.string();
                        JsonObject root = new Gson().fromJson(json, JsonObject.class);

                        // Cấu trúc THẬT của OTruyen API cho truyện:
                        // {
                        //   "data": {
                        //     "item": {
                        //       "name": "...", "content": "<p>...</p>", "thumb_url": "xxx-thumb.jpg",
                        //       "author": ["...", "..."],
                        //       "chapters": [ { "server_name": "...", "server_data": [
                        //           { "chapter_name": "1", "chapter_title": "", "chapter_api_data": "https://..." }
                        //       ] } ]
                        //     },
                        //     "APP_DOMAIN_CDN_IMAGE": "https://img.otruyenapi.com"
                        //   }
                        // }
                        if (!root.has("data") || !root.get("data").isJsonObject()) {
                            Toast.makeText(ComicDetailActivity.this, "Dữ liệu truyện không hợp lệ", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        JsonObject data = root.getAsJsonObject("data");

                        String cdnImage = data.has("APP_DOMAIN_CDN_IMAGE") && !data.get("APP_DOMAIN_CDN_IMAGE").isJsonNull()
                                ? data.get("APP_DOMAIN_CDN_IMAGE").getAsString() : "";

                        if (!data.has("item") || !data.get("item").isJsonObject()) {
                            Toast.makeText(ComicDetailActivity.this, "Không tìm thấy thông tin truyện", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        JsonObject item = data.getAsJsonObject("item");

                        String name = item.has("name") && !item.get("name").isJsonNull()
                                ? item.get("name").getAsString() : "Không rõ tiêu đề";

                        String authorStr = "Không rõ tác giả";
                        if (item.has("author") && item.get("author").isJsonArray()) {
                            JsonArray authorArr = item.getAsJsonArray("author");
                            List<String> authors = new ArrayList<>();
                            for (JsonElement el : authorArr) {
                                String a = el.getAsString();
                                if (a != null && !a.trim().isEmpty()) authors.add(a.trim());
                            }
                            if (!authors.isEmpty()) authorStr = String.join(", ", authors);
                        }

                        String contentHtml = item.has("content") && !item.get("content").isJsonNull()
                                ? item.get("content").getAsString() : "";
                        // Bỏ thẻ HTML đơn giản (API trả content dạng "<p>...</p>")
                        String description = contentHtml.replaceAll("<[^>]*>", "").trim();
                        if (description.isEmpty()) description = "Không có mô tả";

                        String thumbUrl = item.has("thumb_url") && !item.get("thumb_url").isJsonNull()
                                ? item.get("thumb_url").getAsString() : "";
                        String coverUrl = "";
                        if (!cdnImage.isEmpty() && !thumbUrl.isEmpty()) {
                            coverUrl = cdnImage + "/uploads/comics/" + thumbUrl;
                        }

                        // Cập nhật UI
                        tvTitle.setText(name);
                        tvAuthor.setText("Tác giả: " + authorStr);
                        tvDescription.setText(description);
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle(name);
                        }

                        if (!coverUrl.isEmpty()) {
                            String fixedCoverUrl = coverUrl.replace("127.0.0.1", "10.0.2.2").replace("localhost", "10.0.2.2");
                            Glide.with(ComicDetailActivity.this)
                                    .load(fixedCoverUrl)
                                    .placeholder(R.drawable.bg_cover_placeholder)
                                    .error(R.drawable.ic_launcher_background)
                                    .into(imgCover);
                        }

                        // ── Danh sách chương thật ─────────────────────────
                        chapterList.clear();
                        chapterApiDataList.clear();

                        if (item.has("chapters") && item.get("chapters").isJsonArray()) {
                            JsonArray serversArr = item.getAsJsonArray("chapters");
                            int index = 0;
                            for (JsonElement serverEl : serversArr) {
                                if (!serverEl.isJsonObject()) continue;
                                JsonObject serverObj = serverEl.getAsJsonObject();
                                if (!serverObj.has("server_data") || !serverObj.get("server_data").isJsonArray()) continue;

                                JsonArray chapArr = serverObj.getAsJsonArray("server_data");
                                for (JsonElement chapEl : chapArr) {
                                    if (!chapEl.isJsonObject()) continue;
                                    JsonObject chapObj = chapEl.getAsJsonObject();

                                    String chapterName = chapObj.has("chapter_name") && !chapObj.get("chapter_name").isJsonNull()
                                            ? chapObj.get("chapter_name").getAsString() : "?";
                                    String chapterTitle = chapObj.has("chapter_title") && !chapObj.get("chapter_title").isJsonNull()
                                            ? chapObj.get("chapter_title").getAsString() : "";
                                    String chapterApiData = chapObj.has("chapter_api_data") && !chapObj.get("chapter_api_data").isJsonNull()
                                            ? chapObj.get("chapter_api_data").getAsString() : "";

                                    if (chapterApiData.isEmpty()) continue; // không có link thì bỏ qua, không cho mở

                                    String displayTitle = "Chương " + chapterName
                                            + (chapterTitle != null && !chapterTitle.trim().isEmpty() ? ": " + chapterTitle.trim() : "");

                                    // id = vị trí trong list (dùng để tra chapterApiDataList khi bấm)
                                    chapterList.add(new Chapter(index, displayTitle, false, 0, ""));
                                    chapterApiDataList.add(chapterApiData);
                                    index++;
                                }
                            }
                        }

                        rvChapters.setAdapter(new ChapterAdapter(chapterList, chapter -> {
                            int idx = chapter.getId();
                            if (idx >= 0 && idx < chapterApiDataList.size()) {
                                openReader(chapterApiDataList.get(idx), chapter.getTitle());
                            } else {
                                Toast.makeText(ComicDetailActivity.this, "Không tìm thấy dữ liệu chương", Toast.LENGTH_SHORT).show();
                            }
                        }));

                        if (chapterList.isEmpty()) {
                            Toast.makeText(ComicDetailActivity.this, "Truyện chưa có chương nào", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ComicDetailActivity.this, "Không thể tải thông tin truyện", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("ComicDetail", "Error parsing JSON", e);
                    Toast.makeText(ComicDetailActivity.this, "Lỗi xử lý dữ liệu truyện", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ComicDetail", "API call failed", t);
                Toast.makeText(ComicDetailActivity.this, "Lỗi kết nối Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // chapterApiData: URL đầy đủ lấy từ item.chapters[].server_data[].chapter_api_data
    private void openReader(String chapterApiData, String chapterTitle) {
        Intent intent = new Intent(this, ReaderActivity.class);
        intent.putExtra("comic_slug", getIntent().getStringExtra("comic_slug"));
        intent.putExtra("chapter_api_data", chapterApiData);
        intent.putExtra("chapter_title", chapterTitle);
        startActivity(intent);
    }
}