package com.example.truyenmoingay.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView; // Đã thêm import ImageView
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Đã thêm import Glide
import com.example.truyenmoingay.R;
import com.example.truyenmoingay.models.Comic;

import java.util.List;

public class ComicAdapter extends RecyclerView.Adapter<ComicAdapter.ViewHolder> {

    public interface OnClick { void onClick(Comic comic); }

    private final List<Comic> data;
    private final OnClick listener;

    public ComicAdapter(List<Comic> data, OnClick listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comic, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Lấy dữ liệu từ biến "data" (không phải comicList)
        Comic comic = data.get(position);

        // Gán tên truyện (truy cập trực tiếp biến public "title", không dùng hàm getTitle)
        holder.tvTitle.setText(comic.title);

        // Lấy link ảnh từ biến public "coverUrl" (không dùng getThumbnail)
        String rawImageUrl = comic.coverUrl;

        // Kiểm tra link ảnh không rỗng trước khi load
        if (rawImageUrl != null && !rawImageUrl.isEmpty()) {
            String fixedImageUrl = rawImageUrl.replace("127.0.0.1", "10.0.2.2").replace("localhost", "10.0.2.2");

            Glide.with(holder.itemView.getContext())
                    .load(fixedImageUrl)
                    .placeholder(R.drawable.bg_cover_placeholder)
                    .into(holder.imageViewCover);
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
        ImageView imageViewCover; // Khai báo thêm biến ảnh để hết báo đỏ

        ViewHolder(@NonNull View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvSub   = v.findViewById(R.id.tvSub);

            // Ánh xạ ImageView. Lưu ý: Đảm bảo ID "imageViewCover" khớp với ID trong file item_comic.xml
            imageViewCover = v.findViewById(R.id.imageViewCover);
        }
    }
}