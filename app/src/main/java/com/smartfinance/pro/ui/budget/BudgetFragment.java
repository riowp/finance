package com.smartfinance.pro.ui.budget;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.smartfinance.pro.R;
import com.smartfinance.pro.databinding.FragmentBudgetBinding;
import com.smartfinance.pro.utils.Categories;
import com.smartfinance.pro.viewmodel.BudgetViewModel;

/**
 * Fragment for Budget management — set limits per category, view progress.
 */
public class BudgetFragment extends Fragment {

    private FragmentBudgetBinding binding;
    private BudgetViewModel viewModel;
    private BudgetAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBudgetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(BudgetViewModel.class);

        setupRecyclerView();
        setupFab();
        observeData();
    }

    private void setupRecyclerView() {
        adapter = new BudgetAdapter(
            budget -> showEditBudgetDialog(budget.getCategory(), budget.getLimitAmount()),
            budget -> {
                new AlertDialog.Builder(requireContext())
                    .setTitle("Hapus Budget")
                    .setMessage("Hapus budget untuk " + budget.getCategory() + "?")
                    .setPositiveButton("Hapus", (d, w) -> viewModel.deleteBudget(budget))
                    .setNegativeButton("Batal", null)
                    .show();
            },
            viewModel
        );
        binding.rvBudgets.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvBudgets.setAdapter(adapter);
    }

    private void setupFab() {
        binding.fabAddBudget.setOnClickListener(v -> showAddBudgetDialog());
    }

    private void showAddBudgetDialog() {
        showBudgetDialog(null, 0);
    }

    private void showEditBudgetDialog(String category, double currentLimit) {
        showBudgetDialog(category, currentLimit);
    }

    private void showBudgetDialog(String preCategory, double preLimit) {
        View dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_budget, null);

        AutoCompleteTextView actvCategory = dialogView.findViewById(R.id.actv_budget_category);
        EditText etLimit = dialogView.findViewById(R.id.et_budget_limit);

        // Populate categories
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(
            requireContext(), android.R.layout.simple_dropdown_item_1line,
            Categories.getExpenseCategories());
        actvCategory.setAdapter(catAdapter);

        if (preCategory != null) {
            actvCategory.setText(preCategory, false);
            actvCategory.setEnabled(false);
            etLimit.setText(String.valueOf((long) preLimit));
        }

        new AlertDialog.Builder(requireContext())
            .setTitle(preCategory == null ? "Tambah Budget" : "Edit Budget")
            .setView(dialogView)
            .setPositiveButton("Simpan", (dialog, which) -> {
                String cat   = actvCategory.getText().toString().trim();
                String limStr = etLimit.getText().toString().trim();
                if (cat.isEmpty() || limStr.isEmpty()) {
                    Toast.makeText(requireContext(), "Isi semua field", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    double limit = Double.parseDouble(limStr);
                    viewModel.saveBudget(cat, limit);
                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(), "Format angka tidak valid", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Batal", null)
            .show();
    }

    private void observeData() {
        viewModel.getCurrentMonthBudgets().observe(getViewLifecycleOwner(), budgets -> {
            if (budgets == null || budgets.isEmpty()) {
                binding.tvNoBudget.setVisibility(View.VISIBLE);
                binding.rvBudgets.setVisibility(View.GONE);
            } else {
                binding.tvNoBudget.setVisibility(View.GONE);
                binding.rvBudgets.setVisibility(View.VISIBLE);
                adapter.submitList(budgets);
                viewModel.loadSpentAmounts(budgets);
            }
        });

        viewModel.getSpentAmounts().observe(getViewLifecycleOwner(), amounts -> {
            adapter.notifyDataSetChanged();
        });

        viewModel.getStatusMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
