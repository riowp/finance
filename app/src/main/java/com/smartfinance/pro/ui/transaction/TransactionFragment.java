package com.smartfinance.pro.ui.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.smartfinance.pro.R;
import com.smartfinance.pro.data.model.Transaction;
import com.smartfinance.pro.databinding.FragmentTransactionBinding;
import com.smartfinance.pro.ui.common.TransactionAdapter;
import com.smartfinance.pro.utils.Categories;
import com.smartfinance.pro.utils.FormatUtils;
import com.smartfinance.pro.viewmodel.TransactionViewModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment showing full transaction list with filter chips + search.
 */
public class TransactionFragment extends Fragment {

    private FragmentTransactionBinding binding;
    private TransactionViewModel viewModel;
    private TransactionAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTransactionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        setupRecyclerView();
        setupFilterChips();
        setupSearch();
        observeData();
    }

    private void setupRecyclerView() {
        adapter = new TransactionAdapter(transaction -> {
            Intent intent = new Intent(requireContext(), AddTransactionActivity.class);
            intent.putExtra("transaction_id", transaction.getId());
            intent.putExtra("edit_mode", true);
            startActivity(intent);
        });
        binding.rvTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvTransactions.setAdapter(adapter);

        // Swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv,
                                  @NonNull RecyclerView.ViewHolder vh,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int dir) {
                int pos = vh.getAdapterPosition();
                Transaction t = adapter.getCurrentList().get(pos);
                viewModel.deleteTransaction(t);
                Snackbar.make(binding.getRoot(), "Transaksi dihapus", Snackbar.LENGTH_LONG)
                    .setAction("Undo", v -> viewModel.insertTransaction(t))
                    .show();
            }
        }).attachToRecyclerView(binding.rvTransactions);
    }

    private void setupFilterChips() {
        // Type filter
        binding.chipAll.setOnClickListener(v -> viewModel.setFilterType("ALL"));
        binding.chipIncome.setOnClickListener(v -> viewModel.setFilterType("INCOME"));
        binding.chipExpense.setOnClickListener(v -> viewModel.setFilterType("EXPENSE"));

        // Category filter button
        binding.btnFilterCategory.setOnClickListener(v -> showCategoryFilter());
    }

    private void showCategoryFilter() {
        List<String> cats = new ArrayList<>();
        cats.add("Semua Kategori");
        cats.addAll(Categories.getExpenseCategories());
        cats.addAll(Categories.getIncomeCategories());

        String[] arr = cats.toArray(new String[0]);
        new AlertDialog.Builder(requireContext())
            .setTitle("Filter Kategori")
            .setItems(arr, (dialog, which) -> {
                if (which == 0) {
                    viewModel.setFilterCategory("ALL");
                    binding.btnFilterCategory.setText("Kategori");
                } else {
                    String selected = arr[which];
                    viewModel.setFilterCategory(selected);
                    binding.btnFilterCategory.setText(selected);
                }
                refreshTransactions();
            })
            .show();
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String q = s.toString().trim();
                if (q.isEmpty()) {
                    refreshTransactions();
                } else {
                    viewModel.searchTransactions(q).observe(getViewLifecycleOwner(),
                        transactions -> updateList(transactions));
                }
            }
        });
    }

    private void observeData() {
        refreshTransactions();
        viewModel.getFilterType().observe(getViewLifecycleOwner(), type -> refreshTransactions());
    }

    private void refreshTransactions() {
        viewModel.getFilteredTransactions().observe(getViewLifecycleOwner(),
            this::updateList);
    }

    private void updateList(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            binding.tvEmpty.setVisibility(View.VISIBLE);
            binding.rvTransactions.setVisibility(View.GONE);
        } else {
            binding.tvEmpty.setVisibility(View.GONE);
            binding.rvTransactions.setVisibility(View.VISIBLE);
            adapter.submitList(transactions);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
