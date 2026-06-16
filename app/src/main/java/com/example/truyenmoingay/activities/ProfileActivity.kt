package com.example.truyenmoingay.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
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
        prefManager = SharedPrefManager(this)

        if (prefManager.getDarkModeStatus()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        wallet = WalletManager.getInstance(this)
        tvCoinBalance = findViewById(R.id.tvCoinBalance)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val layoutLoggedIn = findViewById<View>(R.id.layoutLoggedIn)   // nhóm view chỉ hiện khi đã login

        // Hiển thị UI theo trạng thái đăng nhập
        if (prefManager.getLoginStatus()) {
            // Đã đăng nhập
            layoutLoggedIn?.visibility = View.VISIBLE
            btnLogin?.visibility = View.GONE
            btnLogout?.visibility = View.VISIBLE
            updateCoinBalance()
        } else {
            // Chưa đăng nhập
            layoutLoggedIn?.visibility = View.GONE
            btnLogin?.visibility = View.VISIBLE
            btnLogout?.visibility = View.GONE
        }

        // Nút Đăng nhập → mở LoginActivity
        btnLogin?.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Nút Đăng xuất
        btnLogout?.setOnClickListener {
            prefManager.saveLoginStatus(false)
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
            recreate() // reload lại màn hình profile
        }

        // Nút nạp xu
        findViewById<View>(R.id.btnTopUp)?.setOnClickListener {
            startActivity(Intent(this, TopUpActivity::class.java))
        }

        // Toggle Dark Mode
        findViewById<View>(R.id.tvGiaoDienClick)?.setOnClickListener {
            val newState = !prefManager.getDarkModeStatus()
            prefManager.saveDarkModeStatus(newState)
            if (newState) {
                Toast.makeText(this, "Đã BẬT Giao diện tối!", Toast.LENGTH_SHORT).show()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                Toast.makeText(this, "Đã TẮT Giao diện tối!", Toast.LENGTH_SHORT).show()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            recreate()
        }

        setupBottomNav()
    }

    override fun onResume() {
        super.onResume()
        // Khi quay lại từ LoginActivity, reload lại UI
        recreate()
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
                R.id.nav_home -> { startActivity(Intent(this, HomeActivity::class.java)); finish(); true }
                R.id.nav_hashtag -> { startActivity(Intent(this, HashtagActivity::class.java)); finish(); true }
                R.id.nav_following -> { startActivity(Intent(this, FollowingActivity::class.java)); finish(); true }
                R.id.nav_explore -> { startActivity(Intent(this, ExploreActivity::class.java)); finish(); true }
                else -> true
            }
        }
    }
}