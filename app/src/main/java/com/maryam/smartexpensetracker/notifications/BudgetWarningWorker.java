package com.maryam.smartexpensetracker.notifications;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.maryam.smartexpensetracker.database.AppDatabase;
import com.maryam.smartexpensetracker.model.Budget;
import com.maryam.smartexpensetracker.model.Expense;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BudgetWarningWorker extends Worker {

    public BudgetWarningWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            String userId = getInputData().getString("userId");
            if (userId == null) return Result.failure();

            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            String currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());

            Budget budget = db.budgetDAO().getBudgetByMonth(userId, currentMonth).getValue();
            List<Expense> expenses = db.expenseDAO().getExpensesByMonth(userId, currentMonth).getValue();

            if (budget != null && expenses != null) {
                double total = 0;
                for (Expense e : expenses) total += e.getAmount();

                double percentage = (total / budget.getBudgetAmount()) * 100;

                if (percentage >= 90) {
                    NotificationHelper.showNotification(
                            getApplicationContext(),
                            "Budget Alert!",
                            "You've used " + (int) percentage + "% of your monthly budget!",
                            1002
                    );
                }
            }

            return Result.success();
        } catch (Exception e) {
            return Result.failure();
        }
    }
}