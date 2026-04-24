package com.smartfinance.pro.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.smartfinance.pro.data.dao.TransactionDao;
import com.smartfinance.pro.data.database.AppDatabase;
import com.smartfinance.pro.data.model.CategorySummary;
import com.smartfinance.pro.data.model.Transaction;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository layer for Transaction data.
 * Abstracts database operations from ViewModel.
 * All DB operations run on background threads.
 */
public class TransactionRepository {

    private final TransactionDao transactionDao;
    private final ExecutorService executor;

    public TransactionRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        this.transactionDao = db.transactionDao();
        this.executor = Executors.newFixedThreadPool(4);
    }

    // ─── Write Operations ─────────────────────────────────────────────

    public void insert(Transaction transaction, OnCompleteListener<Long> listener) {
        executor.execute(() -> {
            long id = transactionDao.insert(transaction);
            if (listener != null) listener.onComplete(id);
        });
    }

    public void update(Transaction transaction, OnCompleteListener<Void> listener) {
        executor.execute(() -> {
            transactionDao.update(transaction);
            if (listener != null) listener.onComplete(null);
        });
    }

    public void delete(Transaction transaction, OnCompleteListener<Void> listener) {
        executor.execute(() -> {
            transactionDao.delete(transaction);
            if (listener != null) listener.onComplete(null);
        });
    }

    public void deleteById(long id, OnCompleteListener<Void> listener) {
        executor.execute(() -> {
            transactionDao.deleteById(id);
            if (listener != null) listener.onComplete(null);
        });
    }

    // ─── Read Operations (LiveData) ───────────────────────────────────

    public LiveData<List<Transaction>> getAllTransactions() {
        return transactionDao.getAllTransactions();
    }

    public LiveData<List<Transaction>> getRecentTransactions(int limit) {
        return transactionDao.getRecentTransactions(limit);
    }

    public LiveData<List<Transaction>> getTransactionsByDateRange(long startDate, long endDate) {
        return transactionDao.getTransactionsByDateRange(startDate, endDate);
    }

    public LiveData<List<Transaction>> getTransactionsByTypeAndDate(String type, long startDate, long endDate) {
        return transactionDao.getTransactionsByTypeAndDate(type, startDate, endDate);
    }

    public LiveData<List<Transaction>> getTransactionsByCategory(String category) {
        return transactionDao.getTransactionsByCategory(category);
    }

    public LiveData<List<Transaction>> searchTransactions(String query) {
        return transactionDao.searchTransactions(query);
    }

    // ─── Aggregation (LiveData) ───────────────────────────────────────

    public LiveData<Double> getTotalIncome() {
        return transactionDao.getTotalIncome();
    }

    public LiveData<Double> getTotalExpense() {
        return transactionDao.getTotalExpense();
    }

    public LiveData<Double> getTotalIncomeByDateRange(long startDate, long endDate) {
        return transactionDao.getTotalIncomeByDateRange(startDate, endDate);
    }

    public LiveData<Double> getTotalExpenseByDateRange(long startDate, long endDate) {
        return transactionDao.getTotalExpenseByDateRange(startDate, endDate);
    }

    public LiveData<List<CategorySummary>> getExpenseSummaryByCategory(long startDate, long endDate) {
        return transactionDao.getExpenseSummaryByCategory(startDate, endDate);
    }

    // ─── Synchronous (for AI analysis / reports) ─────────────────────

    public void getTransactionsByDateRangeSync(long startDate, long endDate,
                                                OnCompleteListener<List<Transaction>> listener) {
        executor.execute(() -> {
            List<Transaction> list = transactionDao.getTransactionsByDateRangeSync(startDate, endDate);
            if (listener != null) listener.onComplete(list);
        });
    }

    public void getExpenseSummaryByCategorySync(long startDate, long endDate,
                                                 OnCompleteListener<List<CategorySummary>> listener) {
        executor.execute(() -> {
            List<CategorySummary> list = transactionDao.getExpenseSummaryByCategorySync(startDate, endDate);
            if (listener != null) listener.onComplete(list);
        });
    }

    public void getMonthlyTotalByType(String type, String yearMonth,
                                       OnCompleteListener<Double> listener) {
        executor.execute(() -> {
            double total = transactionDao.getMonthlyTotalByType(type, yearMonth);
            if (listener != null) listener.onComplete(total);
        });
    }

    public void getTransactionsByMonth(String yearMonth,
                                        OnCompleteListener<List<Transaction>> listener) {
        executor.execute(() -> {
            List<Transaction> list = transactionDao.getTransactionsByMonth(yearMonth);
            if (listener != null) listener.onComplete(list);
        });
    }

    public void getTotalIncomeByDateRangeSync(long startDate, long endDate,
                                               OnCompleteListener<Double> listener) {
        executor.execute(() -> {
            double total = transactionDao.getTotalIncomeByDateRangeSync(startDate, endDate);
            if (listener != null) listener.onComplete(total);
        });
    }

    public void getTotalExpenseByDateRangeSync(long startDate, long endDate,
                                                OnCompleteListener<Double> listener) {
        executor.execute(() -> {
            double total = transactionDao.getTotalExpenseByDateRangeSync(startDate, endDate);
            if (listener != null) listener.onComplete(total);
        });
    }

    public void getExpenseAmountByCategorySync(String category, long startDate, long endDate,
                                                OnCompleteListener<Double> listener) {
        executor.execute(() -> {
            double amount = transactionDao.getExpenseAmountByCategorySync(category, startDate, endDate);
            if (listener != null) listener.onComplete(amount);
        });
    }

    // ─── Callback interface ───────────────────────────────────────────

    public interface OnCompleteListener<T> {
        void onComplete(T result);
    }
}
