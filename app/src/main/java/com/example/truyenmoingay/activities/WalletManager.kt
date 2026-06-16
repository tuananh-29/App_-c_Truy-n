package com.example.truyenmoingay.activities

import android.content.Context

class WalletManager private constructor(context: Context) {

    // Giả lập hàm lấy số xu giống dòng 51
    val balance: Int
        get() = 50

    companion object {
        private var instance: WalletManager? = null

        // Giả lập hàm getInstance giống dòng 21
        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): WalletManager {
            if (instance == null) {
                instance = WalletManager(context)
            }
            return instance!!
        }
    }
}