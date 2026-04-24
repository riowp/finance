package com.smartfinance.pro.ui.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.smartfinance.pro.R;
import com.smartfinance.pro.data.model.Transaction;
import com.smartfinance.pro.utils.Categories;
import com.smartfinance.pro.utils.FormatUtils;

/**
 * RecyclerView Adapter for Transaction list.
 * Uses ListAdapter + DiffUtil for efficient updates.
 */
public class TransactionAdapter extends ListAdapter<Transaction, TransactionAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Transaction transaction);
    }

    private final OnItemClickListener listener;

    public TransactionAdapter(OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Transaction> DIFF_CALLBACK =
        new DiffUtil.ItemCallback<Transaction>() {
            @Override
            public boolean areItemsTheSame(@NonNull Transaction a, @NonNull Transaction b) {
                return a.getId() == b.getId();
            }
            @Override
            public boolean areContentsTheSame(@NonNull Transaction a, @NonNull Transaction b) {
                return a.getAmount() == b.getAmount()
                    && a.getTitle().equals(b.getTitle())
                    && a.getDate() == b.getDate();
            }
        };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView  ivCategoryIcon;
        private final TextView   tvTitle;
        private final TextView   tvCategory;
        private final TextView   tvDate;
        private final TextView   tvAmount;
        private final View       vCategoryDot;

        ViewHolder(View itemView) {
            super(itemView);
            ivCategoryIcon = itemView.findViewById(R.id.iv_category_icon);
            tvTitle        = itemView.findViewById(R.id.tv_transaction_title);
            tvCategory     = itemView.findViewById(R.id.tv_transaction_category);
            tvDate         = itemView.findViewById(R.id.tv_transaction_date);
            tvAmount       = itemView.findViewById(R.id.tv_transaction_amount);
            vCategoryDot   = itemView.findViewById(R.id.v_category_dot);
        }

        void bind(Transaction t, OnItemClickListener listener) {
            tvTitle.setText(t.getTitle());
            tvCategory.setText(t.getCategory());
            tvDate.setText(FormatUtils.formatDate(t.getDate()));

            // Amount color: green for income, red for expense
            String amountStr;
            if (t.isIncome()) {
                amountStr = "+ " + FormatUtils.formatCurrency(t.getAmount());
                tvAmount.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorIncome));
            } else {
                amountStr = "- " + FormatUtils.formatCurrency(t.getAmount());
                tvAmount.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorExpense));
            }
            tvAmount.setText(amountStr);

            // Category icon
            ivCategoryIcon.setImageResource(Categories.getIconForCategory(t.getCategory()));

            // Category dot color
            int color = Categories.getColorForCategory(t.getCategory());
            vCategoryDot.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(color));

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(t);
            });
        }
    }
}
