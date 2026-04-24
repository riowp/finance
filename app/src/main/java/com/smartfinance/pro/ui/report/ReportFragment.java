package com.smartfinance.pro.ui.report;

import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.smartfinance.pro.R;
import com.smartfinance.pro.databinding.FragmentReportBinding;
import com.smartfinance.pro.data.model.CategorySummary;
import com.smartfinance.pro.utils.Categories;
import com.smartfinance.pro.utils.FormatUtils;
import com.smartfinance.pro.viewmodel.DashboardViewModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Monthly Report fragment showing pie chart breakdown and summary stats.
 */
public class ReportFragment extends Fragment {

    private FragmentReportBinding binding;
    private DashboardViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentReportBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        setupPieChart();
        observeData();
    }

    private void setupPieChart() {
        PieChart chart = binding.pieChart;
        chart.getDescription().setEnabled(false);
        chart.setUsePercentValues(true);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setHoleRadius(50f);
        chart.setTransparentCircleRadius(55f);
        chart.getLegend().setEnabled(true);
        chart.setEntryLabelColor(Color.BLACK);
        chart.setEntryLabelTextSize(12f);
        chart.animateY(1000);
    }

    private void observeData() {
        long[] range = viewModel.getCurrentMonthRange();

        binding.tvReportMonth.setText(FormatUtils.formatMonthYear(range[0]));

        viewModel.getTotalIncome().observe(getViewLifecycleOwner(), income -> {
            binding.tvReportIncome.setText(FormatUtils.formatCurrency(income != null ? income : 0));
            Double expense = viewModel.getTotalExpense().getValue();
            if (expense != null && income != null) {
                binding.tvReportBalance.setText(FormatUtils.formatCurrency(income - expense));
                double savingsRate = income > 0 ? ((income - expense) / income) * 100 : 0;
                binding.tvSavingsRate.setText(FormatUtils.formatPercent(savingsRate));
            }
        });

        viewModel.getTotalExpense().observe(getViewLifecycleOwner(), expense -> {
            binding.tvReportExpense.setText(FormatUtils.formatCurrency(expense != null ? expense : 0));
        });

        viewModel.getCategorySummaries().observe(getViewLifecycleOwner(), summaries -> {
            if (summaries != null && !summaries.isEmpty()) {
                updatePieChart(summaries);
                binding.tvNoCategoryData.setVisibility(View.GONE);
            } else {
                binding.tvNoCategoryData.setVisibility(View.VISIBLE);
            }
        });
    }

    private void updatePieChart(List<CategorySummary> summaries) {
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors  = new ArrayList<>();

        for (CategorySummary cs : summaries) {
            if (cs.getTotalAmount() > 0) {
                entries.add(new PieEntry((float) cs.getTotalAmount(), cs.getCategory()));
                colors.add(Categories.getColorForCategory(cs.getCategory()));
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "Kategori Pengeluaran");
        dataSet.setColors(colors);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setValueFormatter(new PercentFormatter(binding.pieChart));
        dataSet.setValueTextSize(11f);
        dataSet.setValueTextColor(Color.BLACK);

        PieData data = new PieData(dataSet);
        binding.pieChart.setData(data);
        binding.pieChart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
