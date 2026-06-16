package com.example.truyenmoingay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyenmoingay.R;
import com.example.truyenmoingay.SearchViewModel; // 1. IMPORT FILE VIEWMODEL VÀO ĐÂY
import com.example.truyenmoingay.models.Comic;
import com.example.truyenmoingay.models.adapters.ComicAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ExploreActivity extends AppCompatActivity {

    private final OkHttpClient client = new OkHttpClient(); // Client phục vụ kết nối API

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        // Khởi tạo ô nhập etSearch và ViewModel chuẩn chỉnh
        EditText etSearch = findViewById(R.id.etSearch);
        final SearchViewModel searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        // Nếu có từ khóa cũ do xoay màn hình, tự động đổ lại vào ô nhập để giữ trạng thái
        if (!searchViewModel.getSearchKeyword().isEmpty()) {
            etSearch.setText(searchViewModel.getSearchKeyword());
        }

        // Lắng nghe người dùng gõ chữ trực quan để lưu trạng thái và gọi API Laragon
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int afternoon) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();
                searchViewModel.setSearchKeyword(keyword); // Lưu trạng thái vào ViewModel ngay khi gõ

                if (!keyword.isEmpty()) {
                    // 2. GỌI HÀM KẾT NỐI ĐẾN SERVER LARAGON CỦA ÔNG
                    fetchStoriesFromAPI(keyword);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        RecyclerView rv = findViewById(R.id.rvRanking);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new ComicAdapter(getMockRanking(), comic -> {
            Intent i = new Intent(this, ComicDetailActivity.class);
            i.putExtra("comic_id", comic.id);
            i.putExtra("comic_title", comic.title);
            i.putExtra("comic_author", comic.author);
            startActivity(i);
        }));

        setupBottomNav();
    }

    // 3. HÀM GỌI API TÌM KIẾM TRUYỆN TỪ HOST LARAGON
    private void fetchStoriesFromAPI(String keyword) {
        // Thay địa chỉ IP mạng LAN này bằng đúng IP máy tính ông đang bật Laragon nhé!
        String url = "http://192.168.100.156/story-api/search_story.php?keyword=" + keyword;

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace(); // Xử lý khi mất kết nối mạng hoặc server Laragon sập
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResult = response.body().string();

                    // Dữ liệu danh sách truyện dạng JSON trả về từ file PHP của ông nằm ở chuỗi jsonResult này!
                    // Ông có thể dùng thư viện Gson để chuyển chuỗi này thành List<Comic> rồi cập nhật lên rv
                }
            }
        });
    }

    private List<Comic> getMockRanking() {
        return Arrays.asList(
                new Comic(1, "Kiếm Đạo Độc Tôn",  "Lê Văn C",    445, 4.9f),
                new Comic(2, "Võ Lâm Tranh Bá",    "Hoàng Văn E", 310, 4.7f),
                new Comic(3, "Hành Trình Tu Tiên", "Nguyễn Văn A",230, 4.8f)
        );
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