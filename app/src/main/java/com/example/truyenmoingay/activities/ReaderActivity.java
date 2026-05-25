package com.example.truyenmoingay.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyenmoingay.R;
import com.example.truyenmoingay.adapters.ReaderPageAdapter;

import java.util.Arrays;
import java.util.List;

public class ReaderActivity extends AppCompatActivity {

    private boolean barsVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        String chapterTitle = getIntent().getStringExtra("chapter_title");

        // Gán tiêu đề
        ((TextView) findViewById(R.id.tvChapterTitle)).setText(chapterTitle);

        // Nút back
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Tap vào nội dung → ẩn/hiện thanh trên/dưới
        RecyclerView rvPages = findViewById(R.id.rvPages);
        rvPages.setOnClickListener(v -> toggleBars());

        // Load ảnh mock (dùng URL placeholder công khai)
        rvPages.setLayoutManager(new LinearLayoutManager(this));
        rvPages.setAdapter(new ReaderPageAdapter(getMockPages()));

        // Nút chuyển chương
        findViewById(R.id.btnPrev).setOnClickListener(v ->
                Toast.makeText(this, "Đây là chương đầu tiên", Toast.LENGTH_SHORT).show()
        );
        findViewById(R.id.btnNext).setOnClickListener(v ->
                Toast.makeText(this, "Chuyển chương tiếp theo", Toast.LENGTH_SHORT).show()
        );
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