package com.smartfinance.pro.utils;

import com.smartfinance.pro.R;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for expense/income categories.
 * Centralized category definitions used across the app.
 */
public class Categories {

    // ─── Expense Categories ───────────────────────────────────────────
    public static final String FOOD = "Makanan & Minuman";
    public static final String TRANSPORT = "Transportasi";
    public static final String SHOPPING = "Belanja";
    public static final String HEALTH = "Kesehatan";
    public static final String ENTERTAINMENT = "Hiburan";
    public static final String EDUCATION = "Pendidikan";
    public static final String BILLS = "Tagihan";
    public static final String HOUSING = "Rumah";
    public static final String FAMILY = "Keluarga";
    public static final String OTHER_EXPENSE = "Lainnya";

    // ─── Income Categories ────────────────────────────────────────────
    public static final String SALARY = "Gaji";
    public static final String BUSINESS = "Bisnis";
    public static final String INVESTMENT = "Investasi";
    public static final String FREELANCE = "Freelance";
    public static final String BONUS = "Bonus";
    public static final String OTHER_INCOME = "Pendapatan Lain";

    public static List<String> getExpenseCategories() {
        return Arrays.asList(
            FOOD, TRANSPORT, SHOPPING, HEALTH, ENTERTAINMENT,
            EDUCATION, BILLS, HOUSING, FAMILY, OTHER_EXPENSE
        );
    }

    public static List<String> getIncomeCategories() {
        return Arrays.asList(
            SALARY, BUSINESS, INVESTMENT, FREELANCE, BONUS, OTHER_INCOME
        );
    }

    /**
     * Returns a Material icon resource for a given category name.
     */
    public static int getIconForCategory(String category) {
        if (category == null) return R.drawable.ic_category_other;
        switch (category) {
            case FOOD:          return R.drawable.ic_category_food;
            case TRANSPORT:     return R.drawable.ic_category_transport;
            case SHOPPING:      return R.drawable.ic_category_shopping;
            case HEALTH:        return R.drawable.ic_category_health;
            case ENTERTAINMENT: return R.drawable.ic_category_entertainment;
            case EDUCATION:     return R.drawable.ic_category_education;
            case BILLS:         return R.drawable.ic_category_bills;
            case HOUSING:       return R.drawable.ic_category_housing;
            case SALARY:        return R.drawable.ic_category_salary;
            case BUSINESS:      return R.drawable.ic_category_business;
            case INVESTMENT:    return R.drawable.ic_category_investment;
            default:            return R.drawable.ic_category_other;
        }
    }

    /**
     * Returns a color resource int for chart/badge rendering per category.
     */
    public static int getColorForCategory(String category) {
        if (category == null) return 0xFF9E9E9E;
        switch (category) {
            case FOOD:          return 0xFFFF5722;
            case TRANSPORT:     return 0xFF2196F3;
            case SHOPPING:      return 0xFFE91E63;
            case HEALTH:        return 0xFF4CAF50;
            case ENTERTAINMENT: return 0xFF9C27B0;
            case EDUCATION:     return 0xFF3F51B5;
            case BILLS:         return 0xFFFF9800;
            case HOUSING:       return 0xFF795548;
            case FAMILY:        return 0xFFFF4081;
            case SALARY:        return 0xFF00BCD4;
            case BUSINESS:      return 0xFF009688;
            case INVESTMENT:    return 0xFF8BC34A;
            case FREELANCE:     return 0xFFFFEB3B;
            case BONUS:         return 0xFFCDDC39;
            default:            return 0xFF9E9E9E;
        }
    }
}
