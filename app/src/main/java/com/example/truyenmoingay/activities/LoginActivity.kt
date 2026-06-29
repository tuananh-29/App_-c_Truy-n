package com.example.truyenmoingay.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.truyenmoingay.R

class LoginActivity : AppCompatActivity() {

    private lateinit var edtUsername: EditText
    private lateinit var edtPassword: EditText
    private lateinit var chkRemember: CheckBox
    private lateinit var btnLogin: Button
    private lateinit var tvGoToRegister: TextView  // ✅ THÊM
    private lateinit var prefManager: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        prefManager = SharedPrefManager(this)

        if (prefManager.getLoginStatus()) {
            Toast.makeText(this, "Tự động đăng nhập thành công!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        edtUsername    = findViewById(R.id.edtUsername)
        edtPassword    = findViewById(R.id.edtPassword)
        chkRemember    = findViewById(R.id.chkRemember)
        btnLogin       = findViewById(R.id.btnLogin)
        tvGoToRegister = findViewById(R.id.tvGoToRegister)  // ✅ THÊM

        btnLogin.setOnClickListener {
            val user = edtUsername.text.toString().trim()
            val pass = edtPassword.text.toString().trim()

            if (user == "admin" && pass == "123") {
                prefManager.saveLoginStatus(chkRemember.isChecked)
                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show()
            }
        }

        // ✅ THÊM: Bấm "Đăng ký" → mở RegisterActivity
        tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}