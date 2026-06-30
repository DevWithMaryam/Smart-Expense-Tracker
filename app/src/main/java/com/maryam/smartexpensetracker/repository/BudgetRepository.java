package com.maryam.smartexpensetracker.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.maryam.smartexpensetracker.dao.BudgetDAO;
import com.maryam.smartexpensetracker.database.AppDatabase;
import com.maryam.smartexpensetracker.model.Budget;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BudgetRepository {

    private final BudgetDAO budgetDAO;
    private final ExecutorService executor;

    public BudgetRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        budgetDAO = db.budgetDAO();
        executor = Executors.newSingleThreadExecutor();
    }

    public void insertBudget(Budget budget) {
        executor.execute(() -> budgetDAO.insertBudget(budget));
    }

    public void updateBudget(Budget budget) {
        executor.execute(() -> budgetDAO.updateBudget(budget));
    }

    public LiveData<Budget> getBudgetByMonth(String userId, String month) {
        return budgetDAO.getBudgetByMonth(userId, month);
    }

    public void deleteBudgetByMonth(String userId, String month) {
        executor.execute(() -> budgetDAO.deleteBudgetByMonth(userId, month));
    }
}