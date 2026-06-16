package com.example.truyenmoingay.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.truyenmoingay.R;
import com.example.truyenmoingay.models.adapters.ComicAdapter;
import com.example.truyenmoingay.models.Comic;
import com.example.truyenmoingay.utils.ComicDBHelper;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Lịch sử đọc truyện");
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView rvHistory = findViewById(R.id.rvHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        ComicDBHelper db = ComicDBHelper.getInstance(this);
        List<Comic> historyList = db.getAllHistory();

        // BẮT SỰ KIỆN CLICK: MỞ THẲNG VÀO MÀN ĐỌC TRUYỆN
        ComicAdapter adapter = new ComicAdapter(historyList, comic -> {
            Intent i = new Intent(this, ReaderActivity.class); // Đi thẳng vào Reader

            // Truyền dữ liệu chương đã lưu lần trước
            i.putExtra("chapter_id", comic.lastChapterId);
            i.putExtra("chapter_title", comic.lastChapterTitle);

            // Truyền dữ liệu truyện để ReaderActivity xử lý nút Thích/Đọc tiếp
            i.putExtra("comic_id", comic.id);
            i.putExtra("comic_title", comic.title);
            i.putExtra("comic_author", comic.author);
            i.putExtra("comic_chapter_count", comic.chapterCount);
            i.putExtra("comic_rating", comic.rating);
            startActivity(i);
        });
        rvHistory.setAdapter(adapter);
    }
}