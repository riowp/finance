package com.smartfinance.pro.ui.insight;

import android.graphics.Color;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.smartfinance.pro.R;
import com.smartfinance.pro.service.AIInsightEngine;

/**
 * Adapter for AI insight cards. Each card has title, message, suggestion, color level.
 */
public class InsightAdapter extends ListAdapter<AIInsightEngine.Insight, InsightAdapter.ViewHolder> {

    public InsightAdapter() {
        super(DIFF);
    }

    private static final DiffUtil.ItemCallback<AIInsightEngine.Insight> DIFF =
        new DiffUtil.ItemCallback<AIInsightEngine.Insight>() {
            @Override
            public boolean areItemsTheSame(@NonNull AIInsightEngine.Insight a,
                                           @NonNull AIInsightEngine.Insight b) {
                return a.title.equals(b.title);
            }
            @Override
            public boolean areContentsTheSame(@NonNull AIInsightEngine.Insight a,
                                              @NonNull AIInsightEngine.Insight b) {
                return a.message.equals(b.message);
            }
        };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_insight, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvEmoji, tvTitle, tvMessage, tvSuggestion;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView     = itemView.findViewById(R.id.card_insight);
            tvEmoji      = itemView.findViewById(R.id.tv_insight_emoji);
            tvTitle      = itemView.findViewById(R.id.tv_insight_title);
            tvMessage    = itemView.findViewById(R.id.tv_insight_message);
            tvSuggestion = itemView.findViewById(R.id.tv_insight_suggestion);
        }

        void bind(AIInsightEngine.Insight insight) {
            tvEmoji.setText(insight.emoji);
            tvTitle.setText(insight.title);
            tvMessage.setText(insight.message);

            if (insight.suggestion != null && !insight.suggestion.isEmpty()) {
                tvSuggestion.setText("💡 " + insight.suggestion);
                tvSuggestion.setVisibility(View.VISIBLE);
            } else {
                tvSuggestion.setVisibility(View.GONE);
            }

            // Card accent color based on level
            int strokeColor;
            int bgColor;
            switch (insight.level) {
                case AIInsightEngine.LEVEL_DANGER:
                    strokeColor = Color.parseColor("#E53935");
                    bgColor     = Color.parseColor("#FFF8F8");
                    break;
                case AIInsightEngine.LEVEL_WARNING:
                    strokeColor = Color.parseColor("#FB8C00");
                    bgColor     = Color.parseColor("#FFFBF5");
                    break;
                case AIInsightEngine.LEVEL_SUCCESS:
                    strokeColor = Color.parseColor("#2E7D32");
                    bgColor     = Color.parseColor("#F5FBF5");
                    break;
                default:
                    strokeColor = Color.parseColor("#1565C0");
                    bgColor     = Color.parseColor("#F5F8FF");
            }
            cardView.setCardBackgroundColor(bgColor);
            tvTitle.setTextColor(strokeColor);
        }
    }
}
