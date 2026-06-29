package com.example.truyenmoingay.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class WalletManager {
    private static WalletManager instance;
    private static final String PREF_NAME = "wallet";
    private static final String KEY_BALANCE = "balance";
    private final SharedPreferences prefs;

    private WalletManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static WalletManager getInstance(Context context) {
        if (instance == null) {
            instance = new WalletManager(context);
        }
        return instance;
    }

    public int getBalance() {
        return prefs.getInt(KEY_BALANCE, 100); // mặc định 100 coin
    }

    public void addBalance(int amount) {
        prefs.edit().putInt(KEY_BALANCE, getBalance() + amount).apply();
    }

    public boolean spend(int amount) {
        if (getBalance() < amount) return false;
        prefs.edit().putInt(KEY_BALANCE, getBalance() - amount).apply();
        return true;
    }
}