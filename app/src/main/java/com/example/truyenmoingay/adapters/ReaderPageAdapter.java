package com.example.truyenmoingay.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.truyenmoingay.R;

import java.util.List;

public class ReaderPageAdapter extends RecyclerView.Adapter<ReaderPageAdapter.ViewHolder> {

    private final List<String> urls;

    public ReaderPageAdapter(List<String> urls) {
        this.urls = urls;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reader_page, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Glide.with(h.ivPage.getContext())
                .load(urls.get(position))
                .into(h.ivPage);
    }

    @Override
    public int getItemCount() { return urls.size(); }

    // Giải phóng Glide khi view bị recycle → tránh OOM
    @Override
    public void onViewRecycled(@NonNull ViewHolder h) {
        super.onViewRecycled(h);
        Glide.with(h.ivPage.getContext()).clear(h.ivPage);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPage;
        ViewHolder(@NonNull View v) {
            super(v);
            ivPage = v.findViewById(R.id.iv_page);
        }
    }
}