package com.smartfinance.pro.utils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for formatting currency, dates, and numbers.
 * Optimized for Indonesian Rupiah (IDR) formatting.
 */
public class FormatUtils {

    private static final Locale ID_LOCALE = new Locale("id", "ID");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(ID_LOCALE);

    // ─── Currency Formatting ──────────────────────────────────────────

    public static String formatCurrency(double amount) {
        // Format as Rp 1.000.000
        String formatted = CURRENCY_FORMAT.format(amount);
        // Remove cents if not needed
        return formatted.replace(",00", "");
    }

    public static String formatCurrencyShort(double amount) {
        if (amount >= 1_000_000_000) {
            return String.format(ID_LOCALE, "Rp %.1fM", amount / 1_000_000_000);
        } else if (amount >= 1_000_000) {
            return String.format(ID_LOCALE, "Rp %.1fjt", amount / 1_000_000);
        } else if (amount >= 1_000) {
            return String.format(ID_LOCALE, "Rp %.0frb", amount / 1_000);
        }
        return formatCurrency(amount);
    }

    public static String formatAmount(double amount) {
        return String.format(ID_LOCALE, "%.0f", amount);
    }

    // ─── Date Formatting ──────────────────────────────────────────────

    public static String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", ID_LOCALE);
        return sdf.format(new Date(timestamp));
    }

    public static String formatDateFull(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", ID_LOCALE);
        return sdf.format(new Date(timestamp));
    }

    public static String formatDateShort(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", ID_LOCALE);
        return sdf.format(new Date(timestamp));
    }

    public static String formatMonthYear(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", ID_LOCALE);
        return sdf.format(new Date(timestamp));
    }

    public static String formatYearMonth(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", ID_LOCALE);
        return sdf.format(new Date(timestamp));
    }

    public static String formatYearMonth(int year, int month) {
        return String.format(ID_LOCALE, "%04d-%02d", year, month + 1);
    }

    // ─── Date Range Helpers ───────────────────────────────────────────

    public static long[] getCurrentMonthRange() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startDate = cal.getTimeInMillis();

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        long endDate = cal.getTimeInMillis();

        return new long[]{startDate, endDate};
    }

    public static long[] getMonthRange(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startDate = cal.getTimeInMillis();

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        long endDate = cal.getTimeInMillis();

        return new long[]{startDate, endDate};
    }

    public static long[] getPreviousMonthRange() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        return getMonthRange(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
    }

    public static String getCurrentYearMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        return sdf.format(new Date());
    }

    // ─── Percentage ───────────────────────────────────────────────────

    public static String formatPercent(double value) {
        return String.format(ID_LOCALE, "%.1f%%", value);
    }

    public static double calculatePercentage(double part, double total) {
        if (total == 0) return 0;
        return (part / total) * 100.0;
    }

    // ─── Number ───────────────────────────────────────────────────────

    public static double parseAmount(String text) {
        try {
            String cleaned = text.replaceAll("[^0-9.,]", "").replace(",", ".");
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
