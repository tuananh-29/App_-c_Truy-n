package com.example.truyenmoingay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyenmoingay.R;
import com.example.truyenmoingay.RetrofitClient;
import com.example.truyenmoingay.adapters.ComicAdapter;
import com.example.truyenmoingay.models.Comic;
import com.example.truyenmoingay.utils.WalletManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private TextView tvHeaderCoinBalance;
    private WalletManager wallet;
    private RecyclerView rvGrid;
    private RecyclerView rvList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        wallet = WalletManager.getInstance(this);

        tvHeaderCoinBalance = findViewById(R.id.tvHeaderCoinBalance);
        updateCoinBalance();

        findViewById(R.id.btnCoinBalance).setOnClickListener(v ->
                startActivity(new Intent(this, TopUpActivity.class))
        );

        rvGrid = findViewById(R.id.rvGrid);
        if (rvGrid != null) {
            rvGrid.setLayoutManager(new GridLayoutManager(this, 2));
        }

        rvList = findViewById(R.id.rvList);
        if (rvList != null) {
            rvList.setLayoutManager(new LinearLayoutManager(this));
        }

        loadComicsFromApi();

        BottomNavigationView nav = findViewById(R.id.bottomNav);
        if (nav != null) {
            nav.setSelectedItemId(R.id.nav_home);
            nav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_hashtag) {
                    startActivity(new Intent(this, HashtagActivity.class));
                } else if (id == R.id.nav_following) {
                    startActivity(new Intent(this, FollowingActivity.class));
                } else if (id == R.id.nav_explore) {
                    startActivity(new Intent(this, ExploreActivity.class));
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(this, ProfileActivity.class));
                }
                return true;
            });
        } else {
            Toast.makeText(this, "Chưa tìm thấy bottomNav trong XML!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCoinBalance();
    }

    private void updateCoinBalance() {
        tvHeaderCoinBalance.setText(String.valueOf(wallet.getBalance()));
    }

    // ── Gọi API lấy danh sách truyện mới ──────────────────────────────
    private void loadComicsFromApi() {
        RetrofitClient.getApiService().getHome().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(HomeActivity.this, "Không tải được dữ liệu truyện (mã lỗi: " + response.code() + ")", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    String rawJson = response.body().string();
                    List<Comic> comics = parseComicsFromJson(rawJson);

                    if (comics.isEmpty()) {
                        Toast.makeText(HomeActivity.this, "Danh sách truyện trống", Toast.LENGTH_SHORT).show();
                    }

                    if (rvGrid != null) {
                        rvGrid.setAdapter(new ComicAdapter(comics, HomeActivity.this::openDetail));
                    }
                    if (rvList != null) {
                        rvList.setAdapter(new ComicAdapter(comics, HomeActivity.this::openDetail));
                    }
                } catch (Exception e) {
                    Toast.makeText(HomeActivity.this, "Lỗi xử lý dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Lỗi kết nối tới server: " + t.getMessage(), Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    /**
     * Parse JSON trả về từ backend Laravel (proxy OTruyen API).
     * Cấu trúc thật: { "data": { "items": [...], "APP_DOMAIN_CDN_IMAGE": "https://img.otruyenapi.com" } }
     * Link ảnh đầy đủ = APP_DOMAIN_CDN_IMAGE + "/uploads/comics/" + thumb_url
     */
    private List<Comic> parseComicsFromJson(String rawJson) throws Exception {
        List<Comic> result = new ArrayList<>();

        JSONObject root = new JSONObject(rawJson);
        JSONObject data = root.optJSONObject("data");
        if (data == null) return result;

        JSONArray items = data.optJSONArray("items");
        if (items == null) return result;

        // Domain CDN ảnh — dùng để ghép thành link ảnh đầy đủ
        String cdnDomain = data.optString("APP_DOMAIN_CDN_IMAGE", "https://img.otruyenapi.com");

        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);

            String title = item.optString("name", "Chưa rõ tên");
            String slug = item.optString("slug", "");
            String thumbFile = item.optString("thumb_url", "");
            String fullThumbUrl = thumbFile.isEmpty() ? "" : cdnDomain + "/uploads/comics/" + thumbFile;

            // Lấy tên chương mới nhất để hiển thị thay cho "tác giả" tạm thời (JSON không có field author)
            String latestChapter = "Đang cập nhật";
            JSONArray chaptersLatest = item.optJSONArray("chaptersLatest");
            if (chaptersLatest != null && chaptersLatest.length() > 0) {
                JSONObject lastChap = chaptersLatest.getJSONObject(chaptersLatest.length() - 1);
                latestChapter = "Chương " + lastChap.optString("chapter_name", "?");
            }

            Comic comic = new Comic(
                    i, // id tạm dùng index để hiển thị; slug mới là khóa thật để mở chi tiết truyện
                    title,
                    latestChapter,
                    0,
                    5.0f
            );
            comic.coverUrl = fullThumbUrl;

            // ĐÃ THÊM DÒNG NÀY ĐỂ LƯU SLUG
            comic.slug = slug;

            result.add(comic);
        }

        return result;
    }

    private void openDetail(Comic comic) {
        Intent intent = new Intent(this, ComicDetailActivity.class);
        intent.putExtra("comic_id", comic.id);
        intent.putExtra("comic_title", comic.title);
        intent.putExtra("comic_author", comic.author);

        // ĐÃ THÊM DÒNG NÀY ĐỂ TRUYỀN SLUG SANG MÀN HÌNH CHI TIẾT
        intent.putExtra("comic_slug", comic.slug);

        startActivity(intent);
    }
}