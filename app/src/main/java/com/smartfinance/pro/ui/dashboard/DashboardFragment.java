package com.smartfinance.pro.ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.smartfinance.pro.R;
import com.smartfinance.pro.databinding.FragmentDashboardBinding;
import com.smartfinance.pro.ui.common.TransactionAdapter;
import com.smartfinance.pro.utils.FormatUtils;
import com.smartfinance.pro.viewmodel.DashboardViewModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Dashboard Fragment — main home screen.
 * Shows balance card, income/expense summary, bar chart, recent transactions.
 */
public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardViewModel viewModel;
    private TransactionAdapter recentAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        setupRecyclerView();
        setupChart();
        observeData();
        setupSwipeRefresh();
    }

    private void setupRecyclerView() {
        recentAdapter = new TransactionAdapter(transaction -> {
            // Navigate to transaction detail / edit
            Bundle args = new Bundle();
            args.putLong("transaction_id", transaction.getId());
        });
        binding.rvRecentTransactions.setLayoutManager(
            new LinearLayoutManager(requireContext()));
        binding.rvRecentTransactions.setAdapter(recentAdapter);
        binding.rvRecentTransactions.setNestedScrollingEnabled(false);
    }

    private void setupChart() {
        BarChart chart = binding.barChart;
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setHighlightFullBarEnabled(false);
        chart.getLegend().setEnabled(true);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);
        chart.animateY(800);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.parseColor("#666666"));

        chart.getAxisLeft().setDrawGridLines(true);
        chart.getAxisLeft().setTextColor(Color.parseColor("#666666"));
        chart.getAxisRight().setEnabled(false);
    }

    private void observeData() {
        // Balance card
        viewModel.getBalance().observe(getViewLifecycleOwner(), balance -> {
            binding.tvBalance.setText(FormatUtils.formatCurrency(balance != null ? balance : 0));
        });

        viewModel.getTotalIncome().observe(getViewLifecycleOwner(), income -> {
            binding.tvIncome.setText(FormatUtils.formatCurrencyShort(income != null ? income : 0));
        });

        viewModel.getTotalExpense().observe(getViewLifecycleOwner(), expense -> {
            binding.tvExpense.setText(FormatUtils.formatCurrencyShort(expense != null ? expense : 0));
        });

        // Recent transactions
        viewModel.getRecentTransactions().observe(getViewLifecycleOwner(), transactions -> {
            if (transactions != null && !transactions.isEmpty()) {
                recentAdapter.submitList(transactions);
                binding.tvNoTransactions.setVisibility(View.GONE);
                binding.rvRecentTransactions.setVisibility(View.VISIBLE);
            } else {
                binding.tvNoTransactions.setVisibility(View.VISIBLE);
                binding.rvRecentTransactions.setVisibility(View.GONE);
            }
        });

        // Chart
        viewModel.getChartLabels().observe(getViewLifecycleOwner(), labels -> {
            float[] income  = viewModel.getChartIncomeData().getValue();
            float[] expense = viewModel.getChartExpenseData().getValue();
            if (labels != null && income != null && expense != null) {
                updateChart(labels, income, expense);
            }
        });
        viewModel.getChartIncomeData().observe(getViewLifecycleOwner(), income -> {
            String[] labels  = viewModel.getChartLabels().getValue();
            float[] expense  = viewModel.getChartExpenseData().getValue();
            if (labels != null && income != null && expense != null) {
                updateChart(labels, income, expense);
            }
        });

        // Loading
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            binding.swipeRefresh.setRefreshing(loading != null && loading);
        });

        // Month label
        binding.tvCurrentMonth.setText(
            FormatUtils.formatMonthYear(System.currentTimeMillis()));
    }

    private void updateChart(String[] labels, float[] income, float[] expense) {
        List<BarEntry> incomeEntries  = new ArrayList<>();
        List<BarEntry> expenseEntries = new ArrayList<>();

        for (int i = 0; i < labels.length; i++) {
            incomeEntries.add(new BarEntry(i, income[i]));
            expenseEntries.add(new BarEntry(i, expense[i]));
        }

        BarDataSet incomeSet = new BarDataSet(incomeEntries, "Pemasukan");
        incomeSet.setColor(Color.parseColor("#2E7D32"));
        incomeSet.setDrawValues(false);

        BarDataSet expenseSet = new BarDataSet(expenseEntries, "Pengeluaran");
        expenseSet.setColor(Color.parseColor("#E53935"));
        expenseSet.setDrawValues(false);

        BarData barData = new BarData(incomeSet, expenseSet);
        float groupSpace = 0.3f, barSpace = 0.02f, barWidth = 0.34f;
        barData.setBarWidth(barWidth);

        BarChart chart = binding.barChart;
        chart.setData(barData);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.getXAxis().setAxisMinimum(0f);
        chart.getXAxis().setAxisMaximum(barData.getGroupWidth(groupSpace, barSpace) * labels.length);
        chart.groupBars(0f, groupSpace, barSpace);
        chart.invalidate();
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.refresh());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
