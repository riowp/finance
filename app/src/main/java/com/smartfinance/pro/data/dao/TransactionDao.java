package com.smartfinance.pro.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.smartfinance.pro.data.model.CategorySummary;
import com.smartfinance.pro.data.model.Transaction;
import java.util.List;

/**
 * Data Access Object for Transaction entity.
 * Provides all SQL operations needed by the app.
 */
@Dao
public interface TransactionDao {

    // ─── INSERT / UPDATE / DELETE ─────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Transaction transaction);

    @Update
    void update(Transaction transaction);

    @Delete
    void delete(Transaction transaction);

    @Query("DELETE FROM transactions WHERE id = :id")
    void deleteById(long id);

    @Query("DELETE FROM transactions")
    void deleteAll();

    // ─── BASIC QUERIES ────────────────────────────────────────────────

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    LiveData<List<Transaction>> getAllTransactions();

    @Query("SELECT * FROM transactions WHERE id = :id")
    Transaction getTransactionById(long id);

    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit")
    LiveData<List<Transaction>> getRecentTransactions(int limit);

    // ─── FILTER BY DATE RANGE ─────────────────────────────────────────

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    LiveData<List<Transaction>> getTransactionsByDateRange(long startDate, long endDate);

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    List<Transaction> getTransactionsByDateRangeSync(long startDate, long endDate);

    // ─── FILTER BY TYPE ───────────────────────────────────────────────

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    LiveData<List<Transaction>> getTransactionsByType(String type);

    @Query("SELECT * FROM transactions WHERE type = :type AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    LiveData<List<Transaction>> getTransactionsByTypeAndDate(String type, long startDate, long endDate);

    // ─── FILTER BY CATEGORY ───────────────────────────────────────────

    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY date DESC")
    LiveData<List<Transaction>> getTransactionsByCategory(String category);

    @Query("SELECT * FROM transactions WHERE category = :category AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    LiveData<List<Transaction>> getTransactionsByCategoryAndDate(String category, long startDate, long endDate);

    // ─── AGGREGATION QUERIES ──────────────────────────────────────────

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'INCOME'")
    LiveData<Double> getTotalIncome();

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'EXPENSE'")
    LiveData<Double> getTotalExpense();

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'INCOME' AND date BETWEEN :startDate AND :endDate")
    LiveData<Double> getTotalIncomeByDateRange(long startDate, long endDate);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'EXPENSE' AND date BETWEEN :startDate AND :endDate")
    LiveData<Double> getTotalExpenseByDateRange(long startDate, long endDate);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'INCOME' AND date BETWEEN :startDate AND :endDate")
    double getTotalIncomeByDateRangeSync(long startDate, long endDate);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'EXPENSE' AND date BETWEEN :startDate AND :endDate")
    double getTotalExpenseByDateRangeSync(long startDate, long endDate);

    // ─── CATEGORY SUMMARY ─────────────────────────────────────────────

    @Query("SELECT category, SUM(amount) as totalAmount, COUNT(*) as transactionCount " +
           "FROM transactions WHERE type = 'EXPENSE' AND date BETWEEN :startDate AND :endDate " +
           "GROUP BY category ORDER BY totalAmount DESC")
    LiveData<List<CategorySummary>> getExpenseSummaryByCategory(long startDate, long endDate);

    @Query("SELECT category, SUM(amount) as totalAmount, COUNT(*) as transactionCount " +
           "FROM transactions WHERE type = 'EXPENSE' AND date BETWEEN :startDate AND :endDate " +
           "GROUP BY category ORDER BY totalAmount DESC")
    List<CategorySummary> getExpenseSummaryByCategorySync(long startDate, long endDate);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions " +
           "WHERE type = 'EXPENSE' AND category = :category AND date BETWEEN :startDate AND :endDate")
    double getExpenseAmountByCategorySync(String category, long startDate, long endDate);

    // ─── MONTHLY DATA FOR CHARTS ──────────────────────────────────────

    @Query("SELECT * FROM transactions WHERE strftime('%Y-%m', date/1000, 'unixepoch') = :yearMonth ORDER BY date ASC")
    List<Transaction> getTransactionsByMonth(String yearMonth);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions " +
           "WHERE type = :type AND strftime('%Y-%m', date/1000, 'unixepoch') = :yearMonth")
    double getMonthlyTotalByType(String type, String yearMonth);

    // ─── SEARCH ───────────────────────────────────────────────────────

    @Query("SELECT * FROM transactions WHERE title LIKE '%' || :query || '%' OR " +
           "note LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY date DESC")
    LiveData<List<Transaction>> searchTransactions(String query);

    // ─── COUNT ────────────────────────────────────────────────────────

    @Query("SELECT COUNT(*) FROM transactions")
    int getTransactionCount();

    @Query("SELECT COUNT(*) FROM transactions WHERE date BETWEEN :startDate AND :endDate")
    int getTransactionCountByDateRange(long startDate, long endDate);
}
