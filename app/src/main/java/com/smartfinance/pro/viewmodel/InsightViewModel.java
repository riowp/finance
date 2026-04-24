package com.smartfinance.pro.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.smartfinance.pro.data.model.CategorySummary;
import com.smartfinance.pro.data.model.Transaction;
import com.smartfinance.pro.data.repository.TransactionRepository;
import com.smartfinance.pro.service.AIInsightEngine;
import com.smartfinance.pro.utils.FormatUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ViewModel for AI Financial Insight screen.
 * Coordinates data loading and triggers AI engine analysis.
 */
public class InsightViewModel extends AndroidViewModel {

    private final TransactionRepository repository;
    private final MutableLiveData<List<AIInsightEngine.Insight>> insights = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> quickSummary = new MutableLiveData<>("");

    // Collected raw data
    private List<Transaction> currentTransactions  = new ArrayList<>();
    private List<Transaction> previousTransactions = new ArrayList<>();
    private List<CategorySummary> currentSummaries  = new ArrayList<>();
    private List<CategorySummary> previousSummaries = new ArrayList<>();
    private double currentIncome  = 0;
    private double currentExpense = 0;

    public InsightViewModel(@NonNull Application application) {
        super(application);
        repository = new TransactionRepository(application);
        analyze();
    }

    public void analyze() {
        isLoading.setValue(true);
        long[] current  = FormatUtils.getCurrentMonthRange();
        long[] previous = FormatUtils.getPreviousMonthRange();

        // Track async completions — need 6 calls to finish
        AtomicInteger pending = new AtomicInteger(6);

        Runnable checkDone = () -> {
            if (pending.decrementAndGet() == 0) {
                List<AIInsightEngine.Insight> result = AIInsightEngine.generateInsights(
                    currentTransactions, previousTransactions,
                    currentSummaries, previousSummaries,
                    currentIncome, currentExpense
                );
                insights.postValue(result);
                quickSummary.postValue(AIInsightEngine.generateQuickSummary(currentIncome, currentExpense));
                isLoading.postValue(false);
            }
        };

        repository.getTransactionsByDateRangeSync(current[0], current[1], list -> {
            currentTransactions = list != null ? list : new ArrayList<>();
            checkDone.run();
        });
        repository.getTransactionsByDateRangeSync(previous[0], previous[1], list -> {
            previousTransactions = list != null ? list : new ArrayList<>();
            checkDone.run();
        });
        repository.getExpenseSummaryByCategorySync(current[0], current[1], list -> {
            currentSummaries = list != null ? list : new ArrayList<>();
            checkDone.run();
        });
        repository.getExpenseSummaryByCategorySync(previous[0], previous[1], list -> {
            previousSummaries = list != null ? list : new ArrayList<>();
            checkDone.run();
        });
        repository.getTotalIncomeByDateRangeSync(current[0], current[1], inc -> {
            currentIncome = inc;
            checkDone.run();
        });
        repository.getTotalExpenseByDateRangeSync(current[0], current[1], exp -> {
            currentExpense = exp;
            checkDone.run();
        });
    }

    public LiveData<List<AIInsightEngine.Insight>> getInsights() { return insights; }
    public LiveData<Boolean> getIsLoading()                      { return isLoading; }
    public LiveData<String> getQuickSummary()                    { return quickSummary; }
}
