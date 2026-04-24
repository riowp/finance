package com.smartfinance.pro.ui.transaction;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.smartfinance.pro.R;
import com.smartfinance.pro.data.model.Transaction;
import com.smartfinance.pro.databinding.ActivityAddTransactionBinding;
import com.smartfinance.pro.utils.Categories;
import com.smartfinance.pro.utils.FormatUtils;
import com.smartfinance.pro.viewmodel.TransactionViewModel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Activity for adding or editing a transaction.
 * Launched from FAB (add) or transaction item click (edit).
 */
public class AddTransactionActivity extends AppCompatActivity {

    private ActivityAddTransactionBinding binding;
    private TransactionViewModel viewModel;

    private long selectedDate = System.currentTimeMillis();
    private boolean isEditMode = false;
    private long editTransactionId = -1;
    private Transaction existingTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        setupToolbar();
        setupTypeToggle();
        setupCategoryDropdown();
        setupDatePicker();
        setupSaveButton();
        checkEditMode();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(isEditMode ? "Edit Transaksi" : "Tambah Transaksi");
        }
    }

    private void setupTypeToggle() {
        binding.toggleType.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                updateCategoryList();
            }
        });
        // Default: expense
        binding.btnExpense.setChecked(true);
        updateCategoryList();
    }

    private void updateCategoryList() {
        boolean isIncome = binding.btnIncome.isChecked();
        List<String> categories = isIncome
            ? Categories.getIncomeCategories()
            : Categories.getExpenseCategories();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this, android.R.layout.simple_dropdown_item_1line, categories);
        binding.actvCategory.setAdapter(adapter);
        binding.actvCategory.setText("", false);
    }

    private void setupCategoryDropdown() {
        updateCategoryList();
    }

    private void setupDatePicker() {
        updateDateText();
        binding.btnPickDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(selectedDate);
            new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    cal.set(year, month, dayOfMonth);
                    selectedDate = cal.getTimeInMillis();
                    updateDateText();
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show();
        });
    }

    private void updateDateText() {
        binding.tvSelectedDate.setText(FormatUtils.formatDate(selectedDate));
    }

    private void setupSaveButton() {
        binding.btnSave.setOnClickListener(v -> saveTransaction());
    }

    private void checkEditMode() {
        isEditMode = getIntent().getBooleanExtra("edit_mode", false);
        editTransactionId = getIntent().getLongExtra("transaction_id", -1);

        if (isEditMode && editTransactionId != -1) {
            getSupportActionBar().setTitle("Edit Transaksi");
            // Load transaction data from DB asynchronously
            new Thread(() -> {
                // We use a simple approach: get it via repository
                // For simplicity load from existing list
            }).start();
        }
    }

    private void saveTransaction() {
        // ── Validation ────────────────────────────────────────────────
        String title    = binding.etTitle.getText().toString().trim();
        String amountStr= binding.etAmount.getText().toString().trim();
        String category = binding.actvCategory.getText().toString().trim();
        String note     = binding.etNote.getText().toString().trim();

        if (title.isEmpty()) {
            binding.tilTitle.setError("Judul wajib diisi");
            return;
        }
        if (amountStr.isEmpty()) {
            binding.tilAmount.setError("Jumlah wajib diisi");
            return;
        }
        if (category.isEmpty()) {
            binding.tilCategory.setError("Pilih kategori");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr.replace(",", "").replace(".", ""));
        } catch (NumberFormatException e) {
            binding.tilAmount.setError("Format jumlah tidak valid");
            return;
        }

        if (amount <= 0) {
            binding.tilAmount.setError("Jumlah harus lebih dari 0");
            return;
        }

        // Clear errors
        binding.tilTitle.setError(null);
        binding.tilAmount.setError(null);
        binding.tilCategory.setError(null);

        // ── Build Transaction ──────────────────────────────────────────
        String type = binding.btnIncome.isChecked()
            ? Transaction.TYPE_INCOME
            : Transaction.TYPE_EXPENSE;

        Transaction transaction = new Transaction(title, amount, type, category, note, selectedDate);

        if (isEditMode && editTransactionId != -1) {
            transaction.setId(editTransactionId);
            viewModel.updateTransaction(transaction);
        } else {
            viewModel.insertTransaction(transaction);
        }

        // ── Observe result ─────────────────────────────────────────────
        viewModel.getOperationSuccess().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, isEditMode ? "Transaksi diperbarui" : "Transaksi ditambahkan",
                    Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        viewModel.getStatusMessage().observe(this, msg -> {
            if (msg != null && msg.contains("Gagal")) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
