package com.maryam.smartexpensetracker.model;

public class AISuggestion {
    private String spendingAnalysis;
    private String savingTips;
    private String budgetAdvice;

    public AISuggestion() {}

    public AISuggestion(String spendingAnalysis, String savingTips, String budgetAdvice) {
        this.spendingAnalysis = spendingAnalysis;
        this.savingTips = savingTips;
        this.budgetAdvice = budgetAdvice;
    }

    public String getSpendingAnalysis() { return spendingAnalysis; }
    public void setSpendingAnalysis(String spendingAnalysis) { this.spendingAnalysis = spendingAnalysis; }

    public String getSavingTips() { return savingTips; }
    public void setSavingTips(String savingTips) { this.savingTips = savingTips; }

    public String getBudgetAdvice() { return budgetAdvice; }
    public void setBudgetAdvice(String budgetAdvice) { this.budgetAdvice = budgetAdvice; }
}