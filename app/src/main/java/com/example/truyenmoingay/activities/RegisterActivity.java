package com.example.truyenmoingay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.truyenmoingay.AuthResponse;
import com.example.truyenmoingay.R;
import com.example.truyenmoingay.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtRegisterName, edtRegisterEmail, edtRegisterPassword;
    private Button btnRegister;
    private TextView tvGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtRegisterName = findViewById(R.id.edtRegisterName);
        edtRegisterEmail = findViewById(R.id.edtRegisterEmail);
        edtRegisterPassword = findViewById(R.id.edtRegisterPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        // Bấm nút Đăng ký -> Gọi API post dữ liệu lên Laravel
        btnRegister.setOnClickListener(v -> {
            String name = edtRegisterName.getText().toString().trim();
            String email = edtRegisterEmail.getText().toString().trim();
            String password = edtRegisterPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Vui lòng điền đủ các trường", Toast.LENGTH_SHORT).show();
                return;
            }

            // (Tùy chọn) Kiểm tra độ dài mật khẩu ngay trên Android cho nhanh
            if (password.length() < 6) {
                Toast.makeText(RegisterActivity.this, "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
                return;
            }

            RetrofitClient.getApiService().registerUser(name, email, password, password).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công! Hãy đăng nhập", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        // BẮT LỖI THỰC SỰ TỪ SERVER
                        String realError = "Lỗi không xác định";
                        try {
                            if (response.errorBody() != null) {
                                realError = response.errorBody().string();
                                Log.e("API_REGISTER_ERROR", "Lỗi Server trả về: " + realError);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(RegisterActivity.this, "Đăng ký thất bại! Kiểm tra Logcat.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Lỗi kết nối Server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("API_REGISTER_FAIL", "Lỗi mạng: " + t.getMessage());
                }
            });
        });

        tvGoToLogin.setOnClickListener(v -> finish());
    }
}