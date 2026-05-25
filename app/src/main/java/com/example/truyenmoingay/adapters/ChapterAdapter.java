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

    public interface OnClick { void onClick(Chapter chapter); }

    private final List<Chapter> data;
    private final OnClick listener;

    public ChapterAdapter(List<Chapter> data, OnClick listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chapter, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Chapter c = data.get(position);
        h.tvTitle.setText(c.title);
        h.tvDate.setText(c.date);

        if (c.isLocked) {
            h.tvLock.setVisibility(View.VISIBLE);
            h.tvLock.setText("🔒 " + c.coinCost + " coin");
        } else {
            h.tvLock.setVisibility(View.GONE);
        }

        h.itemView.setOnClickListener(v -> listener.onClick(c));
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvLock;
        ViewHolder(@NonNull View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvDate  = v.findViewById(R.id.tvDate);
            tvLock  = v.findViewById(R.id.tvLock);
        }
    }
}