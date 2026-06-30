package com.maryam.smartexpensetracker.notifications;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MonthlyReportWorker extends Worker {

    public MonthlyReportWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        NotificationHelper.showNotification(
                getApplicationContext(),
                "Monthly Report Ready",
                "Your monthly report is ready to generate. Tap to view your spending summary!",
                1003
        );
        return Result.success();
    }
}