package com.smartfinance.pro.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

/**
 * Entity class representing a budget limit per category.
 * Maps to the 'budgets' table in Room Database.
 */
@Entity(tableName = "budgets")
public class Budget {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "limit_amount")
    private double limitAmount;

    @ColumnInfo(name = "month")  // Format: "2024-01"
    private String month;

    @ColumnInfo(name = "is_notified")
    private boolean isNotified;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    // ─── Constructors ─────────────────────────────────────────────────
    public Budget() {}

    public Budget(String category, double limitAmount, String month) {
        this.category = category;
        this.limitAmount = limitAmount;
        this.month = month;
        this.isNotified = false;
        this.createdAt = System.currentTimeMillis();
    }

    // ─── Getters & Setters ────────────────────────────────────────────
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getLimitAmount() { return limitAmount; }
    public void setLimitAmount(double limitAmount) { this.limitAmount = limitAmount; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public boolean isNotified() { return isNotified; }
    public void setNotified(boolean notified) { isNotified = notified; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
