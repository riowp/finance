package com.smartfinance.pro.ui.insight;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.smartfinance.pro.databinding.FragmentInsightBinding;
import com.smartfinance.pro.viewmodel.InsightViewModel;

/**
 * Fragment showing AI-generated financial insight cards.
 */
public class InsightFragment extends Fragment {

    private FragmentInsightBinding binding;
    private InsightViewModel viewModel;
    private InsightAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentInsightBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(InsightViewModel.class);

        adapter = new InsightAdapter();
        binding.rvInsights.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvInsights.setAdapter(adapter);

        binding.btnRefreshInsight.setOnClickListener(v -> viewModel.analyze());

        observeData();
    }

    private void observeData() {
        viewModel.getInsights().observe(getViewLifecycleOwner(), insights -> {
            adapter.submitList(insights);
            if (insights == null || insights.isEmpty()) {
                binding.tvNoInsight.setVisibility(View.VISIBLE);
                binding.rvInsights.setVisibility(View.GONE);
            } else {
                binding.tvNoInsight.setVisibility(View.GONE);
                binding.rvInsights.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getQuickSummary().observe(getViewLifecycleOwner(), summary -> {
            binding.tvQuickSummary.setText(summary);
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            binding.progressInsight.setVisibility(loading != null && loading ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
