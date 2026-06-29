package com.example.truyenmoingay;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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

            RetrofitClient.getApiService().registerUser(name, email, password).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công! Hãy đăng nhập", Toast.LENGTH_SHORT).show();
                        // Đăng ký xong tự quay về màn hình Login để người dùng nhập lại tài khoản
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Email đã tồn tại hoặc không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Lỗi kết nối Server", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Bấm chữ Đăng nhập -> Quay lại LoginActivity
        tvGoToLogin.setOnClickListener(v -> {
            finish(); // Tắt màn hình này đi sẽ tự quay về màn hình Login trước đó
        });
    }
}