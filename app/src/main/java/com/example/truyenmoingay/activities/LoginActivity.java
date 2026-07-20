package com.example.truyenmoingay.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.truyenmoingay.ApiService;
import com.example.truyenmoingay.AuthResponse;
import com.example.truyenmoingay.R;
import com.example.truyenmoingay.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;
    private SharedPreferences sharedPreferences;
    private SharedPrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefManager = new SharedPrefManager(this);
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        // Nếu đã có JWT token lưu sẵn → tự động vào thẳng Home
        String savedToken = sharedPreferences.getString("JWT_TOKEN", null);
        if (savedToken != null) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
            return;
        }

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        btnLogin.setOnClickListener(v -> {
            String email = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            // 1. Kiểm tra không được rỗng
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Gọi hàm loginUser từ ApiService
            RetrofitClient.getApiService().loginUser(email, password).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    // 3. Nếu API trả về thành công (HTTP 200) và có dữ liệu
                    if (response.isSuccessful() && response.body() != null) {
                        AuthResponse authResponse = response.body();

                        // Lưu JWT token vào SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("JWT_TOKEN", authResponse.getToken());
                        editor.apply();

                        // Sử dụng SharedPrefManager để lưu trạng thái đăng nhập
                        prefManager.saveLoginStatus(true);

                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                        // 4. Mở HomeActivity và finish()
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        // Thất bại: HTTP lỗi hoặc dữ liệu không khớp
                        Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {
                    // Lỗi kết nối hệ thống
                    Toast.makeText(LoginActivity.this, "Lỗi kết nối hệ thống Backend: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        tvGoToRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }
}
