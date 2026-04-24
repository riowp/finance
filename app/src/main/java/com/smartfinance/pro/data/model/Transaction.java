package com.smartfinance.pro.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

/**
 * Entity class representing a financial transaction.
 * Maps to the 'transactions' table in Room Database.
 */
@Entity(tableName = "transactions")
public class Transaction {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "amount")
    private double amount;

    @ColumnInfo(name = "type")  // "INCOME" or "EXPENSE"
    private String type;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "note")
    private String note;

    @ColumnInfo(name = "date")
    private long date; // Unix timestamp in milliseconds

    @ColumnInfo(name = "created_at")
    private long createdAt;

    // ─── Constructors ─────────────────────────────────────────────────
    public Transaction() {}

    public Transaction(String title, double amount, String type,
                       String category, String note, long date) {
        this.title = title;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.note = note;
        this.date = date;
        this.createdAt = System.currentTimeMillis();
    }

    // ─── Getters & Setters ────────────────────────────────────────────
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    // ─── Helper methods ───────────────────────────────────────────────
    public boolean isIncome() {
        return "INCOME".equalsIgnoreCase(type);
    }

    public boolean isExpense() {
        return "EXPENSE".equalsIgnoreCase(type);
    }

    // Type constants
    public static final String TYPE_INCOME = "INCOME";
    public static final String TYPE_EXPENSE = "EXPENSE";
}
