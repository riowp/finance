package com.smartfinance.pro.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.smartfinance.pro.data.model.Budget;
import com.smartfinance.pro.data.repository.BudgetRepository;
import com.smartfinance.pro.data.repository.TransactionRepository;
import com.smartfinance.pro.utils.FormatUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ViewModel for Budget management screen.
 */
public class BudgetViewModel extends AndroidViewModel {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Double>> spentAmounts = new MutableLiveData<>(new HashMap<>());

    private String currentMonth;

    public BudgetViewModel(@NonNull Application application) {
        super(application);
        budgetRepository    = new BudgetRepository(application);
        transactionRepository = new TransactionRepository(application);
        currentMonth = FormatUtils.getCurrentYearMonth();
    }

    // ─── CRUD ─────────────────────────────────────────────────────────

    public void saveBudget(String category, double limit) {
        budgetRepository.getBudgetByCategoryAndMonth(category, currentMonth, existing -> {
            if (existing != null) {
                existing.setLimitAmount(limit);
                budgetRepository.update(existing, v ->
                    statusMessage.postValue("Budget " + category + " diperbarui"));
            } else {
                Budget budget = new Budget(category, limit, currentMonth);
                budgetRepository.insert(budget, id ->
                    statusMessage.postValue("Budget " + category + " disimpan"));
            }
        });
    }

    public void deleteBudget(Budget budget) {
        budgetRepository.delete(budget, v ->
            statusMessage.postValue("Budget dihapus"));
    }

    // ─── Queries ──────────────────────────────────────────────────────

    public LiveData<List<Budget>> getCurrentMonthBudgets() {
        return budgetRepository.getBudgetsByMonth(currentMonth);
    }

    public void loadSpentAmounts(List<Budget> budgets) {
        if (budgets == null || budgets.isEmpty()) return;
        long[] range = FormatUtils.getCurrentMonthRange();
        Map<String, Double> map = new HashMap<>();

        int[] counter = {0};
        for (Budget b : budgets) {
            transactionRepository.getExpenseAmountByCategorySync(
                b.getCategory(), range[0], range[1],
                amount -> {
                    map.put(b.getCategory(), amount);
                    counter[0]++;
                    if (counter[0] == budgets.size()) {
                        spentAmounts.postValue(new HashMap<>(map));
                    }
                }
            );
        }
    }

    public double getBudgetProgress(Budget budget) {
        Map<String, Double> spent = spentAmounts.getValue();
        if (spent == null) return 0;
        Double amount = spent.get(budget.getCategory());
        if (amount == null || budget.getLimitAmount() == 0) return 0;
        return (amount / budget.getLimitAmount()) * 100.0;
    }

    public double getSpentAmount(String category) {
        Map<String, Double> spent = spentAmounts.getValue();
        if (spent == null) return 0;
        Double amount = spent.get(category);
        return amount != null ? amount : 0;
    }

    // ─── Getters ──────────────────────────────────────────────────────

    public LiveData<String> getStatusMessage()        { return statusMessage; }
    public LiveData<Map<String, Double>> getSpentAmounts() { return spentAmounts; }
    public String getCurrentMonth()                   { return currentMonth; }
}
