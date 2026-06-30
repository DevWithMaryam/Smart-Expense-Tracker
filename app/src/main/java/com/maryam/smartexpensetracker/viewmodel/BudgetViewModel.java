package com.maryam.smartexpensetracker.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.maryam.smartexpensetracker.model.Budget;
import com.maryam.smartexpensetracker.repository.BudgetRepository;

public class BudgetViewModel extends AndroidViewModel {

    private final BudgetRepository budgetRepository;

    public BudgetViewModel(Application application) {
        super(application);
        budgetRepository = new BudgetRepository(application);
    }

    public void insertBudget(Budget budget) {
        budgetRepository.insertBudget(budget);
    }

    public void updateBudget(Budget budget) {
        budgetRepository.updateBudget(budget);
    }

    public LiveData<Budget> getBudgetByMonth(String userId, String month) {
        return budgetRepository.getBudgetByMonth(userId, month);
    }

    public void deleteBudgetByMonth(String userId, String month) {
        budgetRepository.deleteBudgetByMonth(userId, month);
    }
}