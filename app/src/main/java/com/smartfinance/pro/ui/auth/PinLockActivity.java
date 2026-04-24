package com.smartfinance.pro.ui.auth;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.smartfinance.pro.databinding.ActivityPinLockBinding;

/**
 * PIN Lock screen — shown on app open if PIN is enabled.
 * Simple 4-digit PIN entry with visual dot indicators.
 */
public class PinLockActivity extends AppCompatActivity {

    private ActivityPinLockBinding binding;
    private StringBuilder pinInput = new StringBuilder();
    private static final int PIN_LENGTH = 4;
    private String savedPin;
    private boolean isSetupMode = false;
    private String firstPin = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPinLockBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences prefs = getSharedPreferences("smart_finance_prefs", MODE_PRIVATE);
        savedPin = prefs.getString("pin_code", null);
        isSetupMode = (savedPin == null);

        if (isSetupMode) {
            binding.tvPinTitle.setText("Buat PIN Baru");
            binding.tvPinSubtitle.setText("Masukkan 4 digit PIN");
        } else {
            binding.tvPinTitle.setText("Masukkan PIN");
            binding.tvPinSubtitle.setText("Masukkan PIN Anda untuk melanjutkan");
        }

        setupNumpad();
    }

    private void setupNumpad() {
        int[] numBtnIds = {
            com.smartfinance.pro.R.id.btn_0, com.smartfinance.pro.R.id.btn_1,
            com.smartfinance.pro.R.id.btn_2, com.smartfinance.pro.R.id.btn_3,
            com.smartfinance.pro.R.id.btn_4, com.smartfinance.pro.R.id.btn_5,
            com.smartfinance.pro.R.id.btn_6, com.smartfinance.pro.R.id.btn_7,
            com.smartfinance.pro.R.id.btn_8, com.smartfinance.pro.R.id.btn_9
        };

        for (int i = 0; i < numBtnIds.length; i++) {
            final int digit = i;
            binding.getRoot().findViewById(numBtnIds[i]).setOnClickListener(v -> appendDigit(digit));
        }

        binding.btnBackspace.setOnClickListener(v -> {
            if (pinInput.length() > 0) {
                pinInput.deleteCharAt(pinInput.length() - 1);
                updateDots();
            }
        });
    }

    private void appendDigit(int digit) {
        if (pinInput.length() >= PIN_LENGTH) return;
        pinInput.append(digit);
        updateDots();
        if (pinInput.length() == PIN_LENGTH) {
            processPin(pinInput.toString());
        }
    }

    private void updateDots() {
        View[] dots = {
            binding.dot1, binding.dot2, binding.dot3, binding.dot4
        };
        for (int i = 0; i < dots.length; i++) {
            dots[i].setSelected(i < pinInput.length());
        }
    }

    private void processPin(String pin) {
        if (isSetupMode) {
            if (firstPin == null) {
                firstPin = pin;
                pinInput.setLength(0);
                updateDots();
                binding.tvPinSubtitle.setText("Konfirmasi PIN Anda");
            } else {
                if (firstPin.equals(pin)) {
                    SharedPreferences prefs = getSharedPreferences("smart_finance_prefs", MODE_PRIVATE);
                    prefs.edit()
                        .putString("pin_code", pin)
                        .putBoolean("pin_enabled", true)
                        .apply();
                    Toast.makeText(this, "PIN berhasil dibuat!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "PIN tidak cocok, coba lagi", Toast.LENGTH_SHORT).show();
                    firstPin = null;
                    pinInput.setLength(0);
                    updateDots();
                    binding.tvPinSubtitle.setText("Masukkan 4 digit PIN");
                }
            }
        } else {
            if (pin.equals(savedPin)) {
                finish(); // Correct PIN → go to app
            } else {
                Toast.makeText(this, "PIN salah!", Toast.LENGTH_SHORT).show();
                pinInput.setLength(0);
                updateDots();
                // Vibrate for wrong PIN
                binding.getRoot().performHapticFeedback(
                    android.view.HapticFeedbackConstants.REJECT);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!isSetupMode) {
            finishAffinity(); // Can't skip PIN verification
        } else {
            super.onBackPressed();
        }
    }
}
