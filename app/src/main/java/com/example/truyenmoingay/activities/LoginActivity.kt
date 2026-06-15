package com.example.truyenmoingay.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.truyenmoingay.R

class LoginActivity : AppCompatActivity() {

    // Khai báo các biến giao diện và lớp quản lý SharedPreferences
    private lateinit var edtUsername: EditText
    private lateinit var edtPassword: EditText
    private lateinit var chkRemember: CheckBox
    private lateinit var btnLogin: Button
    private lateinit var prefManager: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // Nối với file giao diện XML của bạn

        // 1. Khởi tạo đối tượng quản lý SharedPreferences
        prefManager = SharedPrefManager(this)

        // --- ĐOẠN KIỂM TRA TỰ ĐỘNG ĐĂNG NHẬP (SKIP LOGIN) ---
        if (prefManager.getLoginStatus()) {
            Toast.makeText(this, "Tự động đăng nhập thành công!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, HomeActivity::class.java) // Chuyển thẳng vào HomeActivity của nhóm
            startActivity(intent)
            finish() // Đóng màn hình Login lại
            return // Dừng không chạy các lệnh ánh xạ ở dưới nữa
        }

        // 2. Ánh xạ các nút bấm từ giao diện XML sang code Kotlin
        edtUsername = findViewById(R.id.edtUsername)
        edtPassword = findViewById(R.id.edtPassword)
        chkRemember = findViewById(R.id.chkRemember)
        btnLogin = findViewById(R.id.btnLogin)

        // 3. Xử lý sự kiện khi click vào nút Đăng Nhập
        btnLogin.setOnClickListener {
            val user = edtUsername.text.toString().trim()
            val pass = edtPassword.text.toString().trim()

            // Kiểm tra tài khoản mật khẩu mẫu (Tú có thể đổi theo ý mình)
            if (user == "admin" && pass == "123") {

                // Nếu người dùng có TÍCH CHỌN vào Checkbox "Nhớ tài khoản"
                if (chkRemember.isChecked) {
                    prefManager.saveLoginStatus(true) // Lưu trạng thái true vào SharedPreferences
                } else {
                    prefManager.saveLoginStatus(false) // Ngược lại lưu false
                }

                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()

                // Chuyển sang màn hình chính của nhóm (HomeActivity)
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish() // Đóng màn hình Login
            } else {
                Toast.makeText(this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}