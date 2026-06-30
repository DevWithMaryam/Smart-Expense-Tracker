package com.maryam.smartexpensetracker.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.maryam.smartexpensetracker.dao.ExpenseDAO;
import com.maryam.smartexpensetracker.database.AppDatabase;
import com.maryam.smartexpensetracker.model.Expense;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExpenseRepository {

    private final ExpenseDAO expenseDAO;
    private final ExecutorService executor;

    public ExpenseRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        expenseDAO = db.expenseDAO();
        executor = Executors.newSingleThreadExecutor();
    }

    public void insertExpense(Expense expense) {
        executor.execute(() -> expenseDAO.insertExpense(expense));
    }

    public void updateExpense(Expense expense) {
        executor.execute(() -> expenseDAO.updateExpense(expense));
    }

    public void deleteExpense(Expense expense) {
        executor.execute(() -> expenseDAO.deleteExpense(expense));
    }

    public LiveData<List<Expense>> getAllExpenses(String userId) {
        return expenseDAO.getAllExpenses(userId);
    }

    public LiveData<List<Expense>> getExpensesByMonth(String userId, String month) {
        return expenseDAO.getExpensesByMonth(userId, month);
    }

    public LiveData<Double> getTodayTotal(String userId, String date) {
        return expenseDAO.getTodayTotal(userId, date);
    }

    public LiveData<Double> getMonthlyTotal(String userId, String month) {
        return expenseDAO.getMonthlyTotal(userId, month);
    }

    public LiveData<Double> getCategoryTotal(String userId, String month, String category) {
        return expenseDAO.getCategoryTotal(userId, month, category);
    }

    public LiveData<Expense> getExpenseById(int id) {
        return expenseDAO.getExpenseById(id);
    }
}