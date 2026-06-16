package com.example.truyenmoingay.models.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyenmoingay.R;
import com.example.truyenmoingay.models.Comic;

import java.util.List;

public class ComicAdapter extends RecyclerView.Adapter<ComicAdapter.ViewHolder> {

    public interface OnClick { void onClick(Comic comic); }
    public interface OnLongClick { void onLongClick(Comic comic); } // Thêm Interface LongClick

    private final List<Comic> data;
    private final OnClick listener;
    private OnLongClick longListener; // Biến nhận sự kiện nhấn giữ

    public ComicAdapter(List<Comic> data, OnClick listener) {
        this.data = data;
        this.listener = listener;
    }

    // Hàm để truyền sự kiện nhấn giữ từ Activity vào
    public void setLongListener(OnLongClick longListener) {
        this.longListener = longListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comic, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Comic c = data.get(position);
        h.tvTitle.setText(c.title);
        h.tvSub.setText(c.chapterCount + " chương  ⭐ " + c.rating);

        // Bấm thường: Mở chi tiết truyện
        h.itemView.setOnClickListener(v -> listener.onClick(c));

        // NHẤN GIỮ: Gọi sự kiện huỷ theo dõi (nếu có)
        if (longListener != null) {
            h.itemView.setOnLongClickListener(v -> {
                longListener.onLongClick(c);
                return true; // True để ngăn sự kiện onClick chạy cùng lúc
            });
        }
    }

    @Override
    public int getItemCount() { return data.size(); }

    public void updateData(List<Comic> newData) {
        data.clear();
        data.addAll(newData);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSub;
        ViewHolder(@NonNull View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvSub   = v.findViewById(R.id.tvSub);
        }
    }
}