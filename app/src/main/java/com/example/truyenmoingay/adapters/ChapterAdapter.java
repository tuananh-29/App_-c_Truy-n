package com.example.truyenmoingay.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyenmoingay.R;
import com.example.truyenmoingay.models.Chapter;

import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ViewHolder> {

    public interface OnChapterClickListener {
        void onClick(Chapter chapter);
    }

    private List<Chapter> data;
    private OnChapterClickListener listener;

    public ChapterAdapter(List<Chapter> data, OnChapterClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Chapter c = data.get(position);

        // ✅ Dùng getter thay vì truy cập field trực tiếp
        h.tvTitle.setText(c.getTitle());
        h.tvDate.setText(c.getDate());

        if (c.isLocked()) {
            h.tvLock.setVisibility(View.VISIBLE);
            h.tvLock.setText("🔒 " + c.getCoinCost() + " coin");
        } else {
            h.tvLock.setVisibility(View.GONE);
        }

        h.itemView.setOnClickListener(v -> listener.onClick(c));
    }

    @Override
    public int getItemCount() { return data.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvLock; // ✅ Thêm tvDate và tvLock

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_chapter_title);
            tvDate  = itemView.findViewById(R.id.tv_chapter_date);   // ✅
            tvLock  = itemView.findViewById(R.id.tv_chapter_lock);   // ✅
        }
    }
}