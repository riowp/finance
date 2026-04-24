package com.smartfinance.pro.data.model;

/**
 * Helper model for category-based expense summary.
 * Used for dashboard charts and AI insight analysis.
 * Not a Room entity — used as a query result holder.
 */
public class CategorySummary {

    private String category;
    private double totalAmount;
    private int transactionCount;

    public CategorySummary() {}

    public CategorySummary(String category, double totalAmount, int transactionCount) {
        this.category = category;
        this.totalAmount = totalAmount;
        this.transactionCount = transactionCount;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public int getTransactionCount() { return transactionCount; }
    public void setTransactionCount(int transactionCount) { this.transactionCount = transactionCount; }
}
