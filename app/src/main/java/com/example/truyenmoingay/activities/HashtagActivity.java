package com.example.truyenmoingay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyenmoingay.R;
import com.example.truyenmoingay.RetrofitClient;
import com.example.truyenmoingay.adapters.ComicAdapter;
import com.example.truyenmoingay.models.Comic;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HashtagActivity extends AppCompatActivity {

    private TextView tvHashtagTitle;
    private RecyclerView rvComics;
    private ComicAdapter comicAdapter;
    private List<Comic> comicList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hashtag);

        tvHashtagTitle = findViewById(R.id.tvHashtagTitle);
        rvComics = findViewById(R.id.rvComics);
        rvComics.setLayoutManager(new LinearLayoutManager(this));

        // 1. Nhận tên hashtag từ Intent
        String hashtag = getIntent().getStringExtra("hashtag_name");
        if (hashtag != null) {
            tvHashtagTitle.setText("#" + hashtag.replace("#", ""));
            // 2. Gọi API lấy danh sách truyện
            loadComicsByHashtag(hashtag);
        }

        setupBottomNav();
    }

    private void loadComicsByHashtag(String tag) {
        RetrofitClient.getApiService().getComicsByHashtag(tag).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful() && responseBody != null) {
                        String jsonStr = responseBody.string();
                        JSONObject root = new JSONObject(jsonStr);
                        
                        JSONArray items;
                        if (root.has("data") && root.get("data") instanceof JSONObject) {
                            items = root.getJSONObject("data").optJSONArray("items");
                        } else {
                            items = root.optJSONArray("data");
                        }

                        if (items != null) {
                            comicList.clear();
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject obj = items.getJSONObject(i);
                                
                                String title = obj.optString("name", obj.optString("title"));
                                String author = obj.optString("author", "Đang cập nhật");
                                String cover = obj.optString("thumb_url", obj.optString("cover"));
                                
                                // 3. Thay thế IP 127.0.0.1 thành 10.0.2.2 cho link ảnh
                                String fixedCover = cover.replace("127.0.0.1", "10.0.2.2").replace("localhost", "10.0.2.2");

                                Comic comic = new Comic(i, title, author, 0, 5.0f);
                                comic.coverUrl = fixedCover;
                                comicList.add(comic);
                            }

                            // 4. Đưa danh sách vào ComicAdapter và gán cho RecyclerView
                            if (comicAdapter == null) {
                                comicAdapter = new ComicAdapter(comicList, comic -> {
                                    Intent intent = new Intent(HashtagActivity.this, ComicDetailActivity.class);
                                    intent.putExtra("comic_title", comic.title);
                                    intent.putExtra("comic_author", comic.author);
                                    startActivity(intent);
                                });
                                rvComics.setAdapter(comicAdapter);
                            } else {
                                comicAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Toast.makeText(HashtagActivity.this, "Không tìm thấy truyện cho hashtag này", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("HashtagActivity", "Error parsing: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(HashtagActivity.this, "Lỗi kết nối Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setSelectedItemId(R.id.nav_hashtag);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class)); finish(); return true;
            } else if (id == R.id.nav_following) {
                startActivity(new Intent(this, FollowingActivity.class)); finish(); return true;
            } else if (id == R.id.nav_explore) {
                startActivity(new Intent(this, ExploreActivity.class)); finish(); return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class)); finish(); return true;
            }
            return true;
        });
    }
}