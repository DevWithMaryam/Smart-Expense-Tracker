package com.maryam.smartexpensetracker.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "expenses")
public class Expense {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private double amount;
    private String category;
    private String date;
    private String notes;
    private String userId;

    public Expense() {}

    public Expense(String title, double amount, String category, String date, String notes, String userId) {
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.notes = notes;
        this.userId = userId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}