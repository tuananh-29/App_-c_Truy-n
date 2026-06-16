package com.example.truyenmoingay.viewmodels;

import androidx.lifecycle.ViewModel;

/**
 * ViewModel cho ReaderActivity.
 *
 * Mục đích (Chương 4.3 - Lưu trạng thái):
 * Lưu lại vị trí trang (vị trí cuộn) mà người dùng đang đọc trong RecyclerView.
 *
 * ViewModel được gắn với ViewModelStore của Activity. Khi Activity bị huỷ và
 * tạo lại do thay đổi cấu hình (ví dụ: xoay màn hình từ dọc sang ngang),
 * instance ViewModel này KHÔNG bị huỷ -> dữ liệu currentPagePosition vẫn còn,
 * giúp ReaderActivity khôi phục đúng vị trí đang đọc mà không cần load lại
 * từ trang đầu tiên.
 */
public class ReaderViewModel extends ViewModel {

    // Vị trí item (trang) hiện tại mà người dùng đang xem trong RecyclerView
    private int currentPagePosition = 0;

    public int getCurrentPagePosition() {
        return currentPagePosition;
    }

    public void setCurrentPagePosition(int position) {
        this.currentPagePosition = position;
    }
}