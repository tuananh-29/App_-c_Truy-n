package com.example.truyenmoingay.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.truyenmoingay.R;
import com.example.truyenmoingay.utils.WalletManager;

public class TopUpActivity extends AppCompatActivity {

    private WalletManager wallet;
    private TextView tvBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup);

        wallet = WalletManager.getInstance(this);
        tvBalance = findViewById(R.id.tvBalance);
        updateBalance();

        int[] packs = {50, 100, 200, 500};
        int[] btnIds = {R.id.btn50, R.id.btn100, R.id.btn200, R.id.btn500};

        for (int i = 0; i < btnIds.length; i++) {
            final int amount = packs[i];
            Button btn = findViewById(btnIds[i]);
            if (btn != null) {
                btn.setOnClickListener(v -> {
                    wallet.addBalance(amount);
                    updateBalance();
                    Toast.makeText(this, "Nạp thành công " + amount + " coin!", Toast.LENGTH_SHORT).show();
                });
            }
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void updateBalance() {
        tvBalance.setText("Số dư: " + wallet.getBalance() + " coin");
    }
}