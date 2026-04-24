package com.smartfinance.pro.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.smartfinance.pro.data.dao.BudgetDao;
import com.smartfinance.pro.data.database.AppDatabase;
import com.smartfinance.pro.data.model.Budget;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository layer for Budget data.
 */
public class BudgetRepository {

    private final BudgetDao budgetDao;
    private final ExecutorService executor;

    public BudgetRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        this.budgetDao = db.budgetDao();
        this.executor = Executors.newFixedThreadPool(2);
    }

    public void insert(Budget budget, TransactionRepository.OnCompleteListener<Long> listener) {
        executor.execute(() -> {
            long id = budgetDao.insert(budget);
            if (listener != null) listener.onComplete(id);
        });
    }

    public void update(Budget budget, TransactionRepository.OnCompleteListener<Void> listener) {
        executor.execute(() -> {
            budgetDao.update(budget);
            if (listener != null) listener.onComplete(null);
        });
    }

    public void delete(Budget budget, TransactionRepository.OnCompleteListener<Void> listener) {
        executor.execute(() -> {
            budgetDao.delete(budget);
            if (listener != null) listener.onComplete(null);
        });
    }

    public void deleteById(long id, TransactionRepository.OnCompleteListener<Void> listener) {
        executor.execute(() -> {
            budgetDao.deleteById(id);
            if (listener != null) listener.onComplete(null);
        });
    }

    public LiveData<List<Budget>> getAllBudgets() {
        return budgetDao.getAllBudgets();
    }

    public LiveData<List<Budget>> getBudgetsByMonth(String month) {
        return budgetDao.getBudgetsByMonth(month);
    }

    public void getBudgetsByMonthSync(String month,
                                       TransactionRepository.OnCompleteListener<List<Budget>> listener) {
        executor.execute(() -> {
            List<Budget> list = budgetDao.getBudgetsByMonthSync(month);
            if (listener != null) listener.onComplete(list);
        });
    }

    public void getBudgetByCategoryAndMonth(String category, String month,
                                             TransactionRepository.OnCompleteListener<Budget> listener) {
        executor.execute(() -> {
            Budget budget = budgetDao.getBudgetByCategoryAndMonth(category, month);
            if (listener != null) listener.onComplete(budget);
        });
    }

    public LiveData<Budget> getBudgetByCategoryAndMonthLive(String category, String month) {
        return budgetDao.getBudgetByCategoryAndMonthLive(category, month);
    }

    public void markAsNotified(long id) {
        executor.execute(() -> budgetDao.markAsNotified(id));
    }
}
