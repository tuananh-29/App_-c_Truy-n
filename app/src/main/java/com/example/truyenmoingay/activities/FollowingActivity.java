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
import com.example.truyenmoingay.utils.ComicDBHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowingActivity extends AppCompatActivity {

    private ComicDBHelper db;
    private RecyclerView rvFollowing;
    private ComicAdapter adapter;
    private TextView tvFollowCount;
    private SharedPrefManager prefManager;
    private List<Comic> comicList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        prefManager = new SharedPrefManager(this);
        db = ComicDBHelper.getInstance(this);
        tvFollowCount = findViewById(R.id.tvFollowCount);
        rvFollowing = findViewById(R.id.rvFollowing);
        rvFollowing.setLayoutManager(new LinearLayoutManager(this));

        setupBottomNav();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 1. Kiểm tra đăng nhập
        if (!prefManager.getLoginStatus()) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem truyện đang theo dõi", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        loadFollowingComics();
    }

    private void loadFollowingComics() {
        // 2. Gọi API lấy danh sách truyện theo dõi
        RetrofitClient.getApiService().getFollowingComics().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful() && responseBody != null) {
                        String jsonStr = responseBody.string();
                        JSONObject root = new JSONObject(jsonStr);

                        // 3. Parse JSON thành danh sách Comic
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

                                // Fix lỗi IP ảnh '127.0.0.1' -> '10.0.2.2'
                                String fixedCover = cover.replace("127.0.0.1", "10.0.2.2").replace("localhost", "10.0.2.2");

                                Comic comic = new Comic(i, title, author, 0, 5.0f);
                                comic.coverUrl = fixedCover;
                                comicList.add(comic);
                            }

                            tvFollowCount.setText(comicList.size() + " truyện đang theo dõi");

                            // 4. Khởi tạo ComicAdapter, gán vào RecyclerView
                            if (adapter == null) {
                                adapter = new ComicAdapter(comicList, comic -> {
                                    Intent intent = new Intent(FollowingActivity.this, ComicDetailActivity.class);
                                    intent.putExtra("comic_title", comic.title);
                                    intent.putExtra("comic_author", comic.author);
                                    startActivity(intent);
                                });
                                rvFollowing.setAdapter(adapter);
                            } else {
                                adapter.updateData(comicList);
                            }
                        }
                    } else {
                        Toast.makeText(FollowingActivity.this, "Không thể tải danh sách theo dõi", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("FollowingActivity", "Error: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(FollowingActivity.this, "Lỗi kết nối Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setSelectedItemId(R.id.nav_following);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class)); finish(); return true;
            } else if (id == R.id.nav_hashtag) {
                startActivity(new Intent(this, HashtagActivity.class)); finish(); return true;
            } else if (id == R.id.nav_explore) {
                startActivity(new Intent(this, ExploreActivity.class)); finish(); return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class)); finish(); return true;
            }
            return true;
        });
    }
}