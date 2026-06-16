package com.example.truyenmoingay.adapters;

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

    private List<Comic> data;        // bỏ final để updateData() hoạt động
    private final OnClick listener;

    public ComicAdapter(List<Comic> data, OnClick listener) {
        this.data = data;
        this.listener = listener;
    }

    // Thêm method này để tìm kiếm hoạt động
    public void updateData(List<Comic> newList) {
        this.data = newList;
        notifyDataSetChanged();
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
        h.itemView.setOnClickListener(v -> listener.onClick(c));
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSub;
        ViewHolder(@NonNull View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvSub   = v.findViewById(R.id.tvSub);
        }
    }
}