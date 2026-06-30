package com.maryam.smartexpensetracker.notifications;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class DailyReminderWorker extends Worker {

    public DailyReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        NotificationHelper.showNotification(
                getApplicationContext(),
                "Don't forget to log your expenses!",
                "Add today's expenses to keep your budget on track.",
                1001
        );
        return Result.success();
    }
}