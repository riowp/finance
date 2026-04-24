package com.smartfinance.pro.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import com.smartfinance.pro.data.model.CategorySummary;
import com.smartfinance.pro.data.model.Transaction;
import com.smartfinance.pro.data.repository.TransactionRepository;
import com.smartfinance.pro.utils.FormatUtils;
import java.util.List;

/**
 * ViewModel for Dashboard screen.
 * Exposes LiveData for all dashboard widgets.
 */
public class DashboardViewModel extends AndroidViewModel {

    private final TransactionRepository repository;

    // Current month date range
    private long[] currentMonthRange;
    private String currentYearMonth;

    // LiveData exposed to UI
    private final MutableLiveData<Double> totalIncome = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> totalExpense = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> balance = new MutableLiveData<>(0.0);
    private final MutableLiveData<List<CategorySummary>> categorySummaries = new MutableLiveData<>();
    private final MutableLiveData<List<Transaction>> recentTransactions = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    // Chart data: last 6 months income/expense
    private final MutableLiveData<float[]> chartIncomeData = new MutableLiveData<>();
    private final MutableLiveData<float[]> chartExpenseData = new MutableLiveData<>();
    private final MutableLiveData<String[]> chartLabels = new MutableLiveData<>();

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        repository = new TransactionRepository(application);
        currentMonthRange = FormatUtils.getCurrentMonthRange();
        currentYearMonth = FormatUtils.getCurrentYearMonth();
        loadDashboardData();
    }

    public void loadDashboardData() {
        isLoading.setValue(true);

        // Load income
        repository.getTotalIncomeByDateRangeSync(
            currentMonthRange[0], currentMonthRange[1],
            income -> {
                totalIncome.postValue(income);
                Double expense = totalExpense.getValue();
                if (expense != null) balance.postValue(income - expense);
            }
        );

        // Load expense
        repository.getTotalExpenseByDateRangeSync(
            currentMonthRange[0], currentMonthRange[1],
            expense -> {
                totalExpense.postValue(expense);
                Double income = totalIncome.getValue();
                if (income != null) balance.postValue(income - expense);
                isLoading.postValue(false);
            }
        );

        // Category summaries
        repository.getExpenseSummaryByCategorySync(
            currentMonthRange[0], currentMonthRange[1],
            summaries -> categorySummaries.postValue(summaries)
        );

        // Recent transactions (last 10)
        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
            repository.getRecentTransactions(10).observeForever(transactions ->
                recentTransactions.setValue(transactions)
            );
        });

        // Chart data
        loadChartData();
    }

    private void loadChartData() {
        float[] incomeArr  = new float[6];
        float[] expenseArr = new float[6];
        String[] labels    = new String[6];

        java.util.Calendar cal = java.util.Calendar.getInstance();
        int[] monthsDone = {0};

        for (int i = 5; i >= 0; i--) {
            java.util.Calendar mc = (java.util.Calendar) cal.clone();
            mc.add(java.util.Calendar.MONTH, -i);
            int year  = mc.get(java.util.Calendar.YEAR);
            int month = mc.get(java.util.Calendar.MONTH);
            String ym = FormatUtils.formatYearMonth(year, month);

            // Month label abbreviated
            java.text.SimpleDateFormat fmt =
                new java.text.SimpleDateFormat("MMM", new java.util.Locale("id","ID"));
            labels[5 - i] = fmt.format(mc.getTime());

            final int idx = 5 - i;
            long[] range = FormatUtils.getMonthRange(year, month);

            repository.getTotalIncomeByDateRangeSync(range[0], range[1], income ->
                incomeArr[idx] = income.floatValue()
            );
            repository.getTotalExpenseByDateRangeSync(range[0], range[1], expense ->
                expenseArr[idx] = expense.floatValue()
            );

            monthsDone[0]++;
            if (monthsDone[0] == 6) {
                chartIncomeData.postValue(incomeArr);
                chartExpenseData.postValue(expenseArr);
                chartLabels.postValue(labels);
            }
        }
    }

    public void refresh() {
        currentMonthRange = FormatUtils.getCurrentMonthRange();
        currentYearMonth  = FormatUtils.getCurrentYearMonth();
        loadDashboardData();
    }

    // ─── Getters ──────────────────────────────────────────────────────
    public LiveData<Double> getTotalIncome()      { return totalIncome; }
    public LiveData<Double> getTotalExpense()     { return totalExpense; }
    public LiveData<Double> getBalance()          { return balance; }
    public LiveData<List<CategorySummary>> getCategorySummaries() { return categorySummaries; }
    public LiveData<List<Transaction>> getRecentTransactions()    { return recentTransactions; }
    public LiveData<Boolean> getIsLoading()       { return isLoading; }
    public LiveData<float[]> getChartIncomeData() { return chartIncomeData; }
    public LiveData<float[]> getChartExpenseData(){ return chartExpenseData; }
    public LiveData<String[]> getChartLabels()    { return chartLabels; }
    public long[] getCurrentMonthRange()          { return currentMonthRange; }
    public String getCurrentYearMonth()           { return currentYearMonth; }
}
