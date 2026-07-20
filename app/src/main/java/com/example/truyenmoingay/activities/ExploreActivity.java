package com.example.truyenmoingay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreActivity extends AppCompatActivity {

    private RecyclerView rvRanking;
    private ComicAdapter adapter;
    private List<Comic> comicList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        rvRanking = findViewById(R.id.rvRanking);
        rvRanking.setLayoutManager(new LinearLayoutManager(this));

        loadComics("trending", 1);

        setupBottomNav();
    }

    private void loadComics(String type, int page) {
        RetrofitClient.getApiService().getList(type, page).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonStr = response.body().string();
                        JSONObject root = new JSONObject(jsonStr);
                        
                        // Giả sử API trả về { "status": "success", "data": [...] } hoặc trực tiếp mảng
                        // Ở đây ta giả định cấu trúc từ HomeActivity: { "data": { "items": [...] } }
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
                                
                                // Thay thế IP 127.0.0.1 thành 10.0.2.2 cho link ảnh
                                if (cover != null) {
                                    cover = cover.replace("127.0.0.1", "10.0.2.2");
                                }

                                Comic comic = new Comic(i, title, author, 0, 5.0f);
                                comic.coverUrl = cover;
                                comicList.add(comic);
                            }

                            if (adapter == null) {
                                adapter = new ComicAdapter(comicList, comic -> {
                                    Intent intent = new Intent(ExploreActivity.this, ComicDetailActivity.class);
                                    intent.putExtra("comic_title", comic.title);
                                    intent.putExtra("comic_author", comic.author);
                                    // Thêm slug nếu cần
                                    startActivity(intent);
                                });
                                rvRanking.setAdapter(adapter);
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                        }

                    } catch (Exception e) {
                        Log.e("ExploreActivity", "Error parsing: " + e.getMessage());
                        Toast.makeText(ExploreActivity.this, "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ExploreActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setSelectedItemId(R.id.nav_explore);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class)); finish(); return true;
            } else if (id == R.id.nav_hashtag) {
                startActivity(new Intent(this, HashtagActivity.class)); finish(); return true;
            } else if (id == R.id.nav_following) {
                startActivity(new Intent(this, FollowingActivity.class)); finish(); return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class)); finish(); return true;
            }
            return true;
        });
    }
}