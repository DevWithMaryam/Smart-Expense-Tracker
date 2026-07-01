package com.maryam.smartexpensetracker.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.maryam.smartexpensetracker.dao.BudgetDAO;
import com.maryam.smartexpensetracker.dao.ExpenseDAO;
import com.maryam.smartexpensetracker.model.Budget;
import com.maryam.smartexpensetracker.model.Expense;
import com.maryam.smartexpensetracker.utils.Constants;

@Database(entities = {Expense.class, Budget.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract ExpenseDAO expenseDAO();
    public abstract BudgetDAO budgetDAO();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            Constants.DATABASE_NAME                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}