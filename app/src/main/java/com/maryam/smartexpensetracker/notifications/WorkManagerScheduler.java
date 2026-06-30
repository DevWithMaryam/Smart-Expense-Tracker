package com.maryam.smartexpensetracker.notifications;

import android.content.Context;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

public class WorkManagerScheduler {

    public static void scheduleDailyReminder(Context context) {
        PeriodicWorkRequest dailyRequest = new PeriodicWorkRequest.Builder(
                DailyReminderWorker.class, 1, TimeUnit.DAYS)
                .setInitialDelay(12, TimeUnit.HOURS)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "daily_reminder",
                ExistingPeriodicWorkPolicy.KEEP,
                dailyRequest
        );
    }

    public static void scheduleBudgetWarningCheck(Context context, String userId) {
        Data inputData = new Data.Builder()
                .putString("userId", userId)
                .build();

        PeriodicWorkRequest budgetCheckRequest = new PeriodicWorkRequest.Builder(
                BudgetWarningWorker.class, 1, TimeUnit.DAYS)
                .setInputData(inputData)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "budget_warning_check",
                ExistingPeriodicWorkPolicy.KEEP,
                budgetCheckRequest
        );
    }

    public static void scheduleMonthlyReportReminder(Context context) {
        PeriodicWorkRequest monthlyRequest = new PeriodicWorkRequest.Builder(
                MonthlyReportWorker.class, 30, TimeUnit.DAYS)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "monthly_report_reminder",
                ExistingPeriodicWorkPolicy.KEEP,
                monthlyRequest
        );
    }

    public static void cancelAllWork(Context context) {
        WorkManager.getInstance(context).cancelAllWork();
    }
}