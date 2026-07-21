package com.example.truyenmoingay.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.truyenmoingay.R
import com.example.truyenmoingay.utils.WalletManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {

    private var tvCoinBalance: TextView? = null
    private var tvUserName: TextView? = null
    private var tvUserEmail: TextView? = null
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
        tvUserName = findViewById(R.id.tvUserName) // Cần ánh xạ thêm View này
        tvUserEmail = findViewById(R.id.tvUserEmail) // Cần ánh xạ thêm View này
        
        updateUI()

        // Nút nạp xu
        findViewById<View>(R.id.btnTopUp).setOnClickListener {
            startActivity(Intent(this, TopUpActivity::class.java))
        }

        // Nút đăng xuất
        findViewById<View>(R.id.btnLogout).setOnClickListener {
            // Xóa dữ liệu đăng nhập
            val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()
            prefManager.saveLoginStatus(false)
            
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
            recreate() // Reload activity
        }

        // Nút đăng nhập / đăng ký (layout id="btnLogin" trong XML)
        findViewById<View>(R.id.btnLogin).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
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
        updateUI()
    }

    private fun updateUI() {
        val isLoggedIn = prefManager.getLoginStatus() // Gọi phương thức đúng của Java class
        val layoutLoggedIn = findViewById<View>(R.id.layoutLoggedIn)
        val btnLoginLayout = findViewById<View>(R.id.btnLogin)
        val btnLogout = findViewById<View>(R.id.btnLogout)

        if (isLoggedIn) {
            layoutLoggedIn.visibility = View.VISIBLE
            btnLogout.visibility = View.VISIBLE
            btnLoginLayout.visibility = View.GONE
            
            // Lấy thông tin từ SharedPreferences (AppPrefs)
            val sharedPrefs = getSharedPreferences("AppPrefs", MODE_PRIVATE)
            val email = sharedPrefs.getString("USER_EMAIL", "ngdung@email.com")
            val name = sharedPrefs.getString("USER_NAME", "Người dùng")
            
            tvUserName?.text = name
            tvUserEmail?.text = email
            
            if (wallet != null && tvCoinBalance != null) {
                tvCoinBalance!!.text = "${wallet!!.balance} xu"
            }
        } else {
            layoutLoggedIn.visibility = View.GONE
            btnLogout.visibility = View.GONE
            btnLoginLayout.visibility = View.VISIBLE
            
            tvUserName?.text = "Khách"
            tvUserEmail?.text = "Chưa đăng nhập"
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
