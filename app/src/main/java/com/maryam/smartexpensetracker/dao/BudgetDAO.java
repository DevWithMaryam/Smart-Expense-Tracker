package com.maryam.smartexpensetracker.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.maryam.smartexpensetracker.model.Budget;

@Dao
public interface BudgetDAO {

    @Insert
    void insertBudget(Budget budget);

    @Update
    void updateBudget(Budget budget);

    @Query("SELECT * FROM budgets WHERE userId = :userId AND month = :month LIMIT 1")
    LiveData<Budget> getBudgetByMonth(String userId, String month);

    @Query("DELETE FROM budgets WHERE userId = :userId AND month = :month")
    void deleteBudgetByMonth(String userId, String month);
}