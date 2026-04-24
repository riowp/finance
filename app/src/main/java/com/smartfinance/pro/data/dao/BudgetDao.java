package com.smartfinance.pro.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.smartfinance.pro.data.model.Budget;
import java.util.List;

/**
 * Data Access Object for Budget entity.
 */
@Dao
public interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Budget budget);

    @Update
    void update(Budget budget);

    @Delete
    void delete(Budget budget);

    @Query("DELETE FROM budgets WHERE id = :id")
    void deleteById(long id);

    @Query("SELECT * FROM budgets ORDER BY category ASC")
    LiveData<List<Budget>> getAllBudgets();

    @Query("SELECT * FROM budgets WHERE month = :month ORDER BY category ASC")
    LiveData<List<Budget>> getBudgetsByMonth(String month);

    @Query("SELECT * FROM budgets WHERE month = :month ORDER BY category ASC")
    List<Budget> getBudgetsByMonthSync(String month);

    @Query("SELECT * FROM budgets WHERE category = :category AND month = :month LIMIT 1")
    Budget getBudgetByCategoryAndMonth(String category, String month);

    @Query("SELECT * FROM budgets WHERE category = :category AND month = :month LIMIT 1")
    LiveData<Budget> getBudgetByCategoryAndMonthLive(String category, String month);

    @Query("UPDATE budgets SET is_notified = 1 WHERE id = :id")
    void markAsNotified(long id);

    @Query("SELECT COUNT(*) FROM budgets WHERE month = :month")
    int getBudgetCountForMonth(String month);
}
