package com.smartfinance.pro.ui.budget;

import android.graphics.Color;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.smartfinance.pro.R;
import com.smartfinance.pro.data.model.Budget;
import com.smartfinance.pro.utils.FormatUtils;
import com.smartfinance.pro.viewmodel.BudgetViewModel;

/**
 * RecyclerView adapter for Budget cards with progress bars.
 */
public class BudgetAdapter extends ListAdapter<Budget, BudgetAdapter.ViewHolder> {

    public interface OnEditListener  { void onEdit(Budget budget);   }
    public interface OnDeleteListener{ void onDelete(Budget budget); }

    private final OnEditListener   editListener;
    private final OnDeleteListener deleteListener;
    private final BudgetViewModel  viewModel;

    public BudgetAdapter(OnEditListener editListener,
                         OnDeleteListener deleteListener,
                         BudgetViewModel viewModel) {
        super(DIFF_CALLBACK);
        this.editListener   = editListener;
        this.deleteListener = deleteListener;
        this.viewModel      = viewModel;
    }

    private static final DiffUtil.ItemCallback<Budget> DIFF_CALLBACK =
        new DiffUtil.ItemCallback<Budget>() {
            @Override
            public boolean areItemsTheSame(@NonNull Budget a, @NonNull Budget b) {
                return a.getId() == b.getId();
            }
            @Override
            public boolean areContentsTheSame(@NonNull Budget a, @NonNull Budget b) {
                return a.getLimitAmount() == b.getLimitAmount();
            }
        };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_budget, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), editListener, deleteListener, viewModel);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView     tvCategory, tvLimit, tvSpent, tvRemaining, tvPercent;
        ProgressBar  progressBar;
        ImageButton  btnEdit, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvCategory  = itemView.findViewById(R.id.tv_budget_category);
            tvLimit     = itemView.findViewById(R.id.tv_budget_limit);
            tvSpent     = itemView.findViewById(R.id.tv_budget_spent);
            tvRemaining = itemView.findViewById(R.id.tv_budget_remaining);
            tvPercent   = itemView.findViewById(R.id.tv_budget_percent);
            progressBar = itemView.findViewById(R.id.progress_budget);
            btnEdit     = itemView.findViewById(R.id.btn_edit_budget);
            btnDelete   = itemView.findViewById(R.id.btn_delete_budget);
        }

        void bind(Budget budget, OnEditListener editL, OnDeleteListener deleteL,
                  BudgetViewModel vm) {
            tvCategory.setText(budget.getCategory());
            tvLimit.setText("Limit: " + FormatUtils.formatCurrency(budget.getLimitAmount()));

            double spent     = vm.getSpentAmount(budget.getCategory());
            double remaining = budget.getLimitAmount() - spent;
            double progress  = vm.getBudgetProgress(budget);

            tvSpent.setText("Digunakan: " + FormatUtils.formatCurrency(spent));
            tvRemaining.setText("Sisa: " + FormatUtils.formatCurrency(Math.max(0, remaining)));
            tvPercent.setText(FormatUtils.formatPercent(progress));

            int progressInt = (int) Math.min(100, progress);
            progressBar.setProgress(progressInt);

            // Color based on usage
            int color;
            if (progress >= 100) {
                color = Color.parseColor("#E53935");
            } else if (progress >= 75) {
                color = Color.parseColor("#FB8C00");
            } else {
                color = Color.parseColor("#2E7D32");
            }
            tvPercent.setTextColor(color);

            // Tint progress bar
            android.content.res.ColorStateList csl =
                android.content.res.ColorStateList.valueOf(color);
            progressBar.setProgressTintList(csl);

            btnEdit.setOnClickListener(v -> { if (editL != null) editL.onEdit(budget); });
            btnDelete.setOnClickListener(v -> { if (deleteL != null) deleteL.onDelete(budget); });
        }
    }
}
