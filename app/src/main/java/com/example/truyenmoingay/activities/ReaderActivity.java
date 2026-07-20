package com.example.truyenmoingay.activities;

import com.example.truyenmoingay.adapters.ReaderPageAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyenmoingay.R;

import java.util.Arrays;
import java.util.List;

public class ReaderActivity extends AppCompatActivity {

    private boolean barsVisible = true;
    private boolean hasRestored = false; // chống gọi restore nhiều lần khi xoay liên tiếp

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        String chapterTitle = getIntent().getStringExtra("chapter_title");
        ((TextView) findViewById(R.id.tvChapterTitle)).setText(chapterTitle);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        RecyclerView rvPages = findViewById(R.id.rvPages);
        rvPages.setOnClickListener(v -> toggleBars());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvPages.setLayoutManager(layoutManager);

        ReaderViewModel viewModel = new ViewModelProvider(this).get(ReaderViewModel.class);

        ReaderPageAdapter adapter = new ReaderPageAdapter(getMockPages());
        rvPages.setAdapter(adapter);

        // Khôi phục vị trí an toàn: dùng addOnLayoutChangeListener
        // thay vì post() để tránh crash khi xoay liên tiếp nhanh
        int savedPosition = viewModel.getScrollPosition();
        if (savedPosition > 0) {
            rvPages.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    // Chỉ chạy 1 lần, tự remove sau khi restore xong
                    rvPages.removeOnLayoutChangeListener(this);

                    // Chỉ restore nếu chưa restore trong lần onCreate này
                    if (!hasRestored) {
                        hasRestored = true;
                        try {
                            layoutManager.scrollToPositionWithOffset(savedPosition, 0);
                        } catch (Exception e) {
                            // Bỏ qua nếu layout chưa sẵn sàng, tránh crash
                        }
                    }
                }
            });
        }

        // Lưu vị trí liên tục khi người dùng cuộn
        rvPages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int firstVisible = layoutManager.findFirstCompletelyVisibleItemPosition();
                if (firstVisible != RecyclerView.NO_POSITION) {
                    viewModel.setScrollPosition(firstVisible);
                } else {
                    int firstPartial = layoutManager.findFirstVisibleItemPosition();
                    if (firstPartial != RecyclerView.NO_POSITION) {
                        viewModel.setScrollPosition(firstPartial);
                    }
                }
            }
        });

        findViewById(R.id.btnPrev).setOnClickListener(v ->
                Toast.makeText(this, "Đây là chương đầu tiên", Toast.LENGTH_SHORT).show()
        );
        findViewById(R.id.btnNext).setOnClickListener(v ->
                Toast.makeText(this, "Chuyển chương tiếp theo", Toast.LENGTH_SHORT).show()
        );
    }

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