package com.maryam.smartexpensetracker.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.maryam.smartexpensetracker.model.Expense;
import java.util.List;

@Dao
public interface ExpenseDAO {

    @Insert
    void insertExpense(Expense expense);

    @Update
    void updateExpense(Expense expense);

    @Delete
    void deleteExpense(Expense expense);

    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC")
    LiveData<List<Expense>> getAllExpenses(String userId);

    @Query("SELECT * FROM expenses WHERE userId = :userId AND date = :date ORDER BY id DESC")
    LiveData<List<Expense>> getExpensesByDate(String userId, String date);

    @Query("SELECT * FROM expenses WHERE userId = :userId AND date LIKE :month || '%' ORDER BY date DESC")
    LiveData<List<Expense>> getExpensesByMonth(String userId, String month);

    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId AND date = :date")
    LiveData<Double> getTodayTotal(String userId, String date);

    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId AND date LIKE :month || '%'")
    LiveData<Double> getMonthlyTotal(String userId, String month);

    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId AND date LIKE :month || '%' AND category = :category")
    LiveData<Double> getCategoryTotal(String userId, String month, String category);

    @Query("SELECT * FROM expenses WHERE id = :id")
    LiveData<Expense> getExpenseById(int id);
}