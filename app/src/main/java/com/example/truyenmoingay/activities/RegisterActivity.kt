package com.example.truyenmoingay.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.truyenmoingay.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvGoToLogin: TextView
    private lateinit var prefManager: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        prefManager = SharedPrefManager(this)

        edtName     = findViewById(R.id.edtRegisterName)
        edtEmail    = findViewById(R.id.edtRegisterEmail)
        edtPassword = findViewById(R.id.edtRegisterPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvGoToLogin = findViewById(R.id.tvGoToLogin)

        btnRegister.setOnClickListener {
            val name  = edtName.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val pass  = edtPassword.text.toString().trim()

            // Kiểm tra trống
            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Kiểm tra độ dài mật khẩu
            if (pass.length < 6) {
                Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Đăng ký thành công → tự động đăng nhập luôn
            prefManager.saveLoginStatus(true)
            Toast.makeText(this, "Đăng ký thành công! Chào mừng $name 🎉", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }

        // Bấm "Đã có tài khoản?" → quay lại LoginActivity
        tvGoToLogin.setOnClickListener {
            finish()
        }
    }
}