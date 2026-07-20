package com.example.truyenmoingay.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.truyenmoingay.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {

    private var tvCoinBalance: TextView? = null
    private var wallet: WalletManager? = null
    private lateinit var prefManager: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // ✅ BƯỚC 1: Khởi tạo prefManager
        prefManager = SharedPrefManager(this)

        // ✅ BƯỚC 2: Áp dụng Dark Mode TRƯỚC super.onCreate
        if (prefManager.getDarkModeStatus()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        // ✅ BƯỚC 3: Gọi super và setContentView
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // ✅ BƯỚC 4: Khởi tạo các view
        wallet = WalletManager.getInstance(this)
        tvCoinBalance = findViewById(R.id.tvCoinBalance)
        updateCoinBalance()

        // Nút nạp xu
        findViewById<View>(R.id.btnTopUp).setOnClickListener {
            startActivity(Intent(this, TopUpActivity::class.java))
        }

        // Nút đăng xuất
        findViewById<View>(R.id.btnLogout).setOnClickListener {
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
        }

        // ✅ BƯỚC 5: Toggle Dark Mode - thêm recreate() để reload giao diện
        findViewById<View>(R.id.tvGiaoDienClick).setOnClickListener {
            val newDarkModeState = !prefManager.getDarkModeStatus()

            // Lưu trạng thái mới
            prefManager.saveDarkModeStatus(newDarkModeState)

            // Áp dụng theme mới
            if (newDarkModeState) {
                Toast.makeText(this, "Đã BẬT Giao diện tối (Dark Mode)!", Toast.LENGTH_SHORT).show()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                Toast.makeText(this, "Đã TẮT Giao diện tối, chuyển về chế độ Sáng!", Toast.LENGTH_SHORT).show()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            // ✅ QUAN TRỌNG: Reload Activity để giao diện thay đổi ngay lập tức
            recreate()
        }

        setupBottomNav()
    }

    override fun onResume() {
        super.onResume()
        updateCoinBalance()
    }

    private fun updateCoinBalance() {
        if (wallet != null && tvCoinBalance != null) {
            tvCoinBalance!!.text = "${wallet!!.balance} xu"
        }
    }

    private fun setupBottomNav() {
        val nav = findViewById<BottomNavigationView>(R.id.bottomNav)
        nav.selectedItemId = R.id.nav_profile
        nav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_hashtag -> {
                    startActivity(Intent(this, HashtagActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_following -> {
                    startActivity(Intent(this, FollowingActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_explore -> {
                    startActivity(Intent(this, ExploreActivity::class.java))
                    finish()
                    true
                }
                else -> true
            }
        }
    }
}