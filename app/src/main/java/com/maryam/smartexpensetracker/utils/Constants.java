package com.maryam.smartexpensetracker.utils;

public class Constants {

    // Notification IDs
    public static final int NOTIFICATION_DAILY_REMINDER = 1001;
    public static final int NOTIFICATION_BUDGET_WARNING = 1002;
    public static final int NOTIFICATION_MONTHLY_REPORT = 1003;

    // WorkManager tags
    public static final String WORK_DAILY_REMINDER = "daily_reminder";
    public static final String WORK_BUDGET_WARNING = "budget_warning_check";
    public static final String WORK_MONTHLY_REPORT = "monthly_report_reminder";

    // Intent keys
    public static final String KEY_EXPENSE_ID = "expense_id";
    public static final String KEY_USER_ID = "userId";

    // Room DB
    public static final String DATABASE_NAME = "smart_expense_tracker_db";

    // PDF
    public static final String REPORTS_FOLDER = "Reports";

    // Gemini
    public static final String GEMINI_MODEL = "gemini-2.0-flash";
}