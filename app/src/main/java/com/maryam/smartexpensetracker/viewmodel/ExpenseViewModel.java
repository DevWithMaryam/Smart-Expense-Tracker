package com.maryam.smartexpensetracker.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.maryam.smartexpensetracker.model.Expense;
import com.maryam.smartexpensetracker.repository.ExpenseRepository;
import java.util.List;

public class ExpenseViewModel extends AndroidViewModel {

    private final ExpenseRepository expenseRepository;

    public ExpenseViewModel(Application application) {
        super(application);
        expenseRepository = new ExpenseRepository(application);
    }

    public void insertExpense(Expense expense) {
        expenseRepository.insertExpense(expense);
    }

    public void updateExpense(Expense expense) {
        expenseRepository.updateExpense(expense);
    }

    public void deleteExpense(Expense expense) {
        expenseRepository.deleteExpense(expense);
    }

    public LiveData<List<Expense>> getAllExpenses(String userId) {
        return expenseRepository.getAllExpenses(userId);
    }

    public LiveData<List<Expense>> getExpensesByMonth(String userId, String month) {
        return expenseRepository.getExpensesByMonth(userId, month);
    }

    public LiveData<Double> getTodayTotal(String userId, String date) {
        return expenseRepository.getTodayTotal(userId, date);
    }

    public LiveData<Double> getMonthlyTotal(String userId, String month) {
        return expenseRepository.getMonthlyTotal(userId, month);
    }

    public LiveData<Double> getCategoryTotal(String userId, String month, String category) {
        return expenseRepository.getCategoryTotal(userId, month, category);
    }

    public LiveData<Expense> getExpenseById(int id) {
        return expenseRepository.getExpenseById(id);
    }
}