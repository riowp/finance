package com.smartfinance.pro.service;

import com.smartfinance.pro.data.model.CategorySummary;
import com.smartfinance.pro.data.model.Transaction;
import com.smartfinance.pro.utils.Categories;
import com.smartfinance.pro.utils.FormatUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI Financial Insight Engine — Rule-Based Implementation.
 *
 * Analyzes user spending patterns and generates personalized financial
 * insights without any external API call. Uses heuristic rules and
 * statistical analysis on historical data.
 *
 * Insight types:
 *   - Spending spike detection (category increased vs. last month)
 *   - Budget burn rate warnings
 *   - Savings opportunity suggestions
 *   - Income vs. expense ratio analysis
 *   - Dominant category identification
 *   - Positive reinforcement when user is doing well
 */
public class AIInsightEngine {

    // ─── Insight severity levels ──────────────────────────────────────
    public static final int LEVEL_INFO    = 0;
    public static final int LEVEL_WARNING = 1;
    public static final int LEVEL_DANGER  = 2;
    public static final int LEVEL_SUCCESS = 3;

    // ─── Thresholds ───────────────────────────────────────────────────
    private static final double SPIKE_THRESHOLD_PERCENT    = 20.0; // >20% increase = spike
    private static final double HIGH_EXPENSE_RATIO         = 0.80; // expense > 80% income = warning
    private static final double CRITICAL_EXPENSE_RATIO     = 0.95;
    private static final double DOMINANT_CATEGORY_RATIO    = 0.40; // one category > 40% total
    private static final double BUDGET_WARNING_THRESHOLD   = 0.75; // 75% of budget used = warn
    private static final double SAVINGS_OPPORTUNITY_MIN    = 100_000; // min Rp to flag

    /**
     * Data class representing one AI-generated insight card.
     */
    public static class Insight {
        public String title;
        public String message;
        public String suggestion;
        public int level;      // LEVEL_INFO / WARNING / DANGER / SUCCESS
        public String emoji;
        public String category;

        public Insight(String title, String message, String suggestion,
                       int level, String emoji, String category) {
            this.title = title;
            this.message = message;
            this.suggestion = suggestion;
            this.level = level;
            this.emoji = emoji;
            this.category = category;
        }
    }

    // ─── Main Analysis Method ─────────────────────────────────────────

    /**
     * Generate insights based on current and previous month data.
     *
     * @param currentMonthTransactions  transactions from current month
     * @param previousMonthTransactions transactions from previous month
     * @param currentCategorySummaries  category breakdown for current month
     * @param previousCategorySummaries category breakdown for previous month
     * @param currentIncome             total income this month
     * @param currentExpense            total expense this month
     * @return list of insight cards, ordered by severity desc
     */
    public static List<Insight> generateInsights(
            List<Transaction> currentMonthTransactions,
            List<Transaction> previousMonthTransactions,
            List<CategorySummary> currentCategorySummaries,
            List<CategorySummary> previousCategorySummaries,
            double currentIncome,
            double currentExpense
    ) {
        List<Insight> insights = new ArrayList<>();

        // Build category maps for easy lookup
        Map<String, Double> currentCatMap  = buildCategoryMap(currentCategorySummaries);
        Map<String, Double> previousCatMap = buildCategoryMap(previousCategorySummaries);

        // ── 1. Income vs Expense Ratio ────────────────────────────────
        analyzeIncomeExpenseRatio(insights, currentIncome, currentExpense);

        // ── 2. Spending spikes per category ──────────────────────────
        analyzeSpendingSpikes(insights, currentCatMap, previousCatMap, currentExpense);

        // ── 3. Dominant category ──────────────────────────────────────
        analyzeDominantCategory(insights, currentCatMap, currentExpense);

        // ── 4. Savings opportunity ────────────────────────────────────
        analyzeSavingsOpportunity(insights, currentIncome, currentExpense, currentCatMap);

        // ── 5. Positive reinforcement ─────────────────────────────────
        analyzePositiveBehavior(insights, currentIncome, currentExpense,
                                currentCatMap, previousCatMap);

        // ── 6. No transaction warning ─────────────────────────────────
        if (currentMonthTransactions == null || currentMonthTransactions.isEmpty()) {
            insights.add(new Insight(
                "Mulai Pencatatan!",
                "Belum ada transaksi bulan ini. Mulai catat keuanganmu sekarang!",
                "Catat minimal 1 transaksi per hari untuk analisis yang akurat.",
                LEVEL_INFO, "📝", null
            ));
        }

        return insights;
    }

    // ─── Analysis Sub-routines ────────────────────────────────────────

    private static void analyzeIncomeExpenseRatio(List<Insight> insights,
                                                   double income, double expense) {
        if (income <= 0) return;

        double ratio = expense / income;
        double savings = income - expense;

        if (ratio >= CRITICAL_EXPENSE_RATIO) {
            insights.add(new Insight(
                "⚠️ Pengeluaran Kritis!",
                String.format("Pengeluaran mencapai %s dari total pemasukan bulan ini.",
                              FormatUtils.formatPercent(ratio * 100)),
                "Segera kurangi pengeluaran non-esensial. Anda hampir tidak punya tabungan!",
                LEVEL_DANGER, "🚨", null
            ));
        } else if (ratio >= HIGH_EXPENSE_RATIO) {
            insights.add(new Insight(
                "Rasio Pengeluaran Tinggi",
                String.format("Pengeluaran Anda %s dari pemasukan. Sisa: %s",
                              FormatUtils.formatPercent(ratio * 100),
                              FormatUtils.formatCurrency(savings)),
                "Coba sisihkan minimal 20% penghasilan sebelum belanja.",
                LEVEL_WARNING, "⚠️", null
            ));
        } else if (ratio <= 0.60 && savings > 0) {
            insights.add(new Insight(
                "Keuangan Sehat! 🎉",
                String.format("Anda berhasil menabung %s bulan ini (%s dari pemasukan).",
                              FormatUtils.formatCurrency(savings),
                              FormatUtils.formatPercent((1 - ratio) * 100)),
                "Pertahankan kebiasaan baik ini! Pertimbangkan untuk menginvestasikan tabungan Anda.",
                LEVEL_SUCCESS, "✅", null
            ));
        }
    }

    private static void analyzeSpendingSpikes(List<Insight> insights,
                                               Map<String, Double> current,
                                               Map<String, Double> previous,
                                               double totalExpense) {
        for (Map.Entry<String, Double> entry : current.entrySet()) {
            String category = entry.getKey();
            double currentAmount = entry.getValue();
            Double previousAmount = previous.get(category);

            if (previousAmount == null || previousAmount == 0) continue;

            double changePercent = ((currentAmount - previousAmount) / previousAmount) * 100;

            if (changePercent >= SPIKE_THRESHOLD_PERCENT) {
                String emoji = changePercent >= 50 ? "📈" : "↗️";
                int level = changePercent >= 50 ? LEVEL_WARNING : LEVEL_INFO;

                insights.add(new Insight(
                    emoji + " Lonjakan: " + category,
                    String.format("Pengeluaran %s naik %.0f%% dibanding bulan lalu.\nSekarang: %s | Bulan lalu: %s",
                                  category, changePercent,
                                  FormatUtils.formatCurrency(currentAmount),
                                  FormatUtils.formatCurrency(previousAmount)),
                    generateSpikeSuggestion(category, currentAmount, previousAmount),
                    level, emoji, category
                ));
            } else if (changePercent <= -SPIKE_THRESHOLD_PERCENT) {
                insights.add(new Insight(
                    "↘️ Hemat: " + category,
                    String.format("Pengeluaran %s turun %.0f%% dari bulan lalu. Hemat %s!",
                                  category, Math.abs(changePercent),
                                  FormatUtils.formatCurrency(previousAmount - currentAmount)),
                    "Pertahankan penghematan ini!",
                    LEVEL_SUCCESS, "✅", category
                ));
            }
        }
    }

    private static void analyzeDominantCategory(List<Insight> insights,
                                                 Map<String, Double> catMap,
                                                 double totalExpense) {
        if (totalExpense <= 0) return;
        for (Map.Entry<String, Double> entry : catMap.entrySet()) {
            double ratio = entry.getValue() / totalExpense;
            if (ratio >= DOMINANT_CATEGORY_RATIO) {
                insights.add(new Insight(
                    "Kategori Dominan",
                    String.format("%s mengambil %s dari total pengeluaran (%s).",
                                  entry.getKey(),
                                  FormatUtils.formatPercent(ratio * 100),
                                  FormatUtils.formatCurrency(entry.getValue())),
                    "Periksa apakah pengeluaran " + entry.getKey() + " sudah efisien.",
                    LEVEL_INFO, "📊", entry.getKey()
                ));
                break; // Only flag top dominant category
            }
        }
    }

    private static void analyzeSavingsOpportunity(List<Insight> insights,
                                                   double income,
                                                   double expense,
                                                   Map<String, Double> catMap) {
        double potentialSavings = 0;
        String targetCategory = null;
        double targetAmount = 0;

        // Find the largest discretionary expense category
        String[] discretionary = {
            Categories.ENTERTAINMENT, Categories.SHOPPING, Categories.FOOD
        };

        for (String cat : discretionary) {
            Double amount = catMap.get(cat);
            if (amount != null && amount > SAVINGS_OPPORTUNITY_MIN) {
                double tenPercent = amount * 0.10;
                if (tenPercent > potentialSavings) {
                    potentialSavings = tenPercent;
                    targetCategory = cat;
                    targetAmount = amount;
                }
            }
        }

        if (targetCategory != null && potentialSavings > 0) {
            insights.add(new Insight(
                "💡 Peluang Hemat",
                String.format("Jika Anda kurangi %s sebesar 10%%, Anda bisa hemat %s per bulan.",
                              targetCategory, FormatUtils.formatCurrency(potentialSavings)),
                String.format("Coba batasi pengeluaran %s menjadi %s bulan depan.",
                              targetCategory,
                              FormatUtils.formatCurrency(targetAmount * 0.90)),
                LEVEL_INFO, "💡", targetCategory
            ));
        }
    }

    private static void analyzePositiveBehavior(List<Insight> insights,
                                                 double income,
                                                 double expense,
                                                 Map<String, Double> current,
                                                 Map<String, Double> previous) {
        // Check if overall expense decreased
        double currentTotal = 0, previousTotal = 0;
        for (double v : current.values()) currentTotal += v;
        for (double v : previous.values()) previousTotal += v;

        if (previousTotal > 0 && currentTotal < previousTotal) {
            double saved = previousTotal - currentTotal;
            if (saved > SAVINGS_OPPORTUNITY_MIN) {
                insights.add(new Insight(
                    "Pengeluaran Menurun!",
                    String.format("Total pengeluaran Anda turun %s dibanding bulan lalu.",
                                  FormatUtils.formatCurrency(saved)),
                    "Luar biasa! Alokasikan penghematan ini ke tabungan atau investasi.",
                    LEVEL_SUCCESS, "🎉", null
                ));
            }
        }
    }

    // ─── Helper Methods ───────────────────────────────────────────────

    private static Map<String, Double> buildCategoryMap(List<CategorySummary> summaries) {
        Map<String, Double> map = new HashMap<>();
        if (summaries == null) return map;
        for (CategorySummary cs : summaries) {
            map.put(cs.getCategory(), cs.getTotalAmount());
        }
        return map;
    }

    private static String generateSpikeSuggestion(String category,
                                                   double current,
                                                   double previous) {
        double excess = current - previous;
        switch (category) {
            case Categories.FOOD:
                return String.format("Coba meal prep di rumah. Potensi hemat: %s/bulan.",
                                     FormatUtils.formatCurrency(excess * 0.5));
            case Categories.TRANSPORT:
                return "Pertimbangkan transportasi umum atau carpool untuk hemat biaya.";
            case Categories.SHOPPING:
                return "Buat daftar belanja sebelumnya dan hindari impulse buying.";
            case Categories.ENTERTAINMENT:
                return "Batasi hiburan berbayar. Cari alternatif gratis yang menyenangkan.";
            default:
                return String.format("Evaluasi pengeluaran %s dan cari cara untuk menguranginya.",
                                     category);
        }
    }

    // ─── Quick summary text for dashboard ────────────────────────────

    public static String generateQuickSummary(double income, double expense) {
        if (income == 0 && expense == 0) return "Belum ada data keuangan bulan ini.";
        double savings = income - expense;
        if (savings > 0) {
            return String.format("Bulan ini Anda hemat %s 💰", FormatUtils.formatCurrencyShort(savings));
        } else if (savings < 0) {
            return String.format("Pengeluaran melebihi pemasukan %s ⚠️",
                                 FormatUtils.formatCurrencyShort(Math.abs(savings)));
        }
        return "Pemasukan dan pengeluaran seimbang bulan ini.";
    }
}
