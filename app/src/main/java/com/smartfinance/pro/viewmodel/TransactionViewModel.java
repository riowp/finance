package com.smartfinance.pro.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.smartfinance.pro.data.model.Transaction;
import com.smartfinance.pro.data.repository.TransactionRepository;
import com.smartfinance.pro.utils.FormatUtils;
import java.util.List;

/**
 * ViewModel for Transaction list and Add/Edit screens.
 */
public class TransactionViewModel extends AndroidViewModel {

    private final TransactionRepository repository;
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> operationSuccess = new MutableLiveData<>();

    // Filter state
    private final MutableLiveData<String> filterType     = new MutableLiveData<>("ALL");
    private final MutableLiveData<String> filterCategory = new MutableLiveData<>("ALL");
    private final MutableLiveData<long[]> filterDateRange;

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        repository = new TransactionRepository(application);
        long[] currentRange = FormatUtils.getCurrentMonthRange();
        filterDateRange = new MutableLiveData<>(currentRange);
    }

    // ─── CRUD Operations ──────────────────────────────────────────────

    public void insertTransaction(Transaction transaction) {
        // Auto-set createdAt
        transaction.setCreatedAt(System.currentTimeMillis());
        repository.insert(transaction, id -> {
            if (id > 0) {
                statusMessage.postValue("Transaksi berhasil ditambahkan");
                operationSuccess.postValue(true);
            } else {
                statusMessage.postValue("Gagal menyimpan transaksi");
                operationSuccess.postValue(false);
            }
        });
    }

    public void updateTransaction(Transaction transaction) {
        repository.update(transaction, v -> {
            statusMessage.postValue("Transaksi berhasil diperbarui");
            operationSuccess.postValue(true);
        });
    }

    public void deleteTransaction(Transaction transaction) {
        repository.delete(transaction, v -> {
            statusMessage.postValue("Transaksi dihapus");
            operationSuccess.postValue(true);
        });
    }

    public void deleteTransactionById(long id) {
        repository.deleteById(id, v -> {
            statusMessage.postValue("Transaksi dihapus");
            operationSuccess.postValue(true);
        });
    }

    // ─── Queries ──────────────────────────────────────────────────────

    public LiveData<List<Transaction>> getAllTransactions() {
        return repository.getAllTransactions();
    }

    public LiveData<List<Transaction>> getRecentTransactions(int limit) {
        return repository.getRecentTransactions(limit);
    }

    public LiveData<List<Transaction>> getTransactionsByDateRange(long start, long end) {
        return repository.getTransactionsByDateRange(start, end);
    }

    public LiveData<List<Transaction>> getFilteredTransactions() {
        long[] range = filterDateRange.getValue();
        String type  = filterType.getValue();
        String cat   = filterCategory.getValue();

        if (range == null) range = FormatUtils.getCurrentMonthRange();

        if ("ALL".equals(type) && "ALL".equals(cat)) {
            return repository.getTransactionsByDateRange(range[0], range[1]);
        } else if (!"ALL".equals(type) && "ALL".equals(cat)) {
            return repository.getTransactionsByTypeAndDate(type, range[0], range[1]);
        } else if ("ALL".equals(type) && !"ALL".equals(cat)) {
            return repository.getTransactionsByCategory(cat);
        } else {
            return repository.getTransactionsByTypeAndDate(type, range[0], range[1]);
        }
    }

    public LiveData<List<Transaction>> searchTransactions(String query) {
        return repository.searchTransactions(query);
    }

    // ─── Filter setters ───────────────────────────────────────────────

    public void setFilterType(String type) {
        filterType.setValue(type);
    }

    public void setFilterCategory(String category) {
        filterCategory.setValue(category);
    }

    public void setFilterDateRange(long start, long end) {
        filterDateRange.setValue(new long[]{start, end});
    }

    // ─── Getters ──────────────────────────────────────────────────────

    public LiveData<String> getStatusMessage()    { return statusMessage; }
    public LiveData<Boolean> getOperationSuccess(){ return operationSuccess; }
    public LiveData<String> getFilterType()       { return filterType; }
    public LiveData<String> getFilterCategory()   { return filterCategory; }
    public LiveData<long[]> getFilterDateRange()  { return filterDateRange; }

    public void clearStatus() {
        statusMessage.setValue(null);
        operationSuccess.setValue(null);
    }
}
