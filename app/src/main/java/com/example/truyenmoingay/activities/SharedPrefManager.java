package com.example.truyenmoingay.activities; // Giữ nguyên package theo đúng cấu trúc nhóm bạn đang dùng

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    // 1. Khai báo tên file và các từ khóa (Key) quen thuộc giống hệt trên lớp học
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_REMEMBER = "remember_login";
    private static final String KEY_DARKMODE = "dark_mode";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    // 2. Hàm khởi tạo nhận vào Context (Môi trường hệ thống) để tạo file
    public SharedPrefManager(Context context) {
        // Tạo file XML lưu thông tin ở chế độ riêng tư (chỉ App này đọc được)
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // --- PHẦN 1: XỬ LÝ NHỚ ĐĂNG NHẬP ---
    // Hàm lưu cờ "Nhớ đăng nhập" khi user tích chọn checkbox và Login thành công
    public void saveLoginStatus(boolean status) {
        editor.putBoolean(KEY_REMEMBER, status);
        editor.apply(); // Giải thích với thầy: Lưu bất đồng bộ để chống đơ màn hình (lỗi ANR)
    }

    // Hàm kiểm tra trạng thái khi vừa mở App lên
    public boolean getLoginStatus() {
        return sharedPreferences.getBoolean(KEY_REMEMBER, false); // Mặc định chưa tích là false
    }


    // --- PHẦN 2: XỬ LÝ CẤU HÌNH GIAO DIỆN TỐI (DARK MODE) ---
    // Hàm lưu trạng thái khi người dùng bật/tắt Switch cài đặt giao diện
    public void saveDarkModeStatus(boolean isDark) {
        editor.putBoolean(KEY_DARKMODE, isDark);
        editor.apply();
    }

    // Hàm đọc trạng thái cấu hình màu sắc để áp dụng lúc mở màn hình
    public boolean getDarkModeStatus() {
        return sharedPreferences.getBoolean(KEY_DARKMODE, false);
    }
}