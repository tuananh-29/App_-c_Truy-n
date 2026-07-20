package com.example.truyenmoingay.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.truyenmoingay.R;
import com.example.truyenmoingay.utils.WalletManager;

/**
 * Màn hình nạp xu (mock, không tích hợp cổng thanh toán thật).
 * Người dùng chọn 1 trong các gói cố định để cộng thẳng vào số dư.
 */
public class TopUpActivity extends AppCompatActivity {

    private TextView tvBalance;
    private WalletManager wallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup);

        wallet = WalletManager.getInstance(this);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        tvBalance = findViewById(R.id.tvBalance);
        updateBalanceDisplay();

        Button btn50 = findViewById(R.id.btn50);
        Button btn100 = findViewById(R.id.btn100);
        Button btn200 = findViewById(R.id.btn200);
        Button btn500 = findViewById(R.id.btn500);

        btn50.setOnClickListener(v -> doTopUp(50));
        btn100.setOnClickListener(v -> doTopUp(100));
        btn200.setOnClickListener(v -> doTopUp(200));
        btn500.setOnClickListener(v -> doTopUp(500));
    }

    private void doTopUp(int amount) {
        wallet.addBalance(amount);
        updateBalanceDisplay();
        Toast.makeText(this, "Nạp thành công " + amount + " coin!", Toast.LENGTH_SHORT).show();
    }

    private void updateBalanceDisplay() {
        tvBalance.setText("Số dư: " + wallet.getBalance() + " coin");
    }
}