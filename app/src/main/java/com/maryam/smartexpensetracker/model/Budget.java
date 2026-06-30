package com.maryam.smartexpensetracker.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "budgets")
public class Budget {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private double budgetAmount;
    private String month;
    private String userId;

    public Budget() {}

    public Budget(double budgetAmount, String month, String userId) {
        this.budgetAmount = budgetAmount;
        this.month = month;
        this.userId = userId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getBudgetAmount() { return budgetAmount; }
    public void setBudgetAmount(double budgetAmount) { this.budgetAmount = budgetAmount; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}