package com.maryam.smartexpensetracker.reports;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import com.maryam.smartexpensetracker.model.Expense;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportGenerator {

    private static final int PAGE_WIDTH = 595;
    private static final int PAGE_HEIGHT = 842;

    public static File generateMonthlyReport(Context context, List<Expense> expenses,
                                             String monthName, double totalBudget, String userName) throws IOException {

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        android.graphics.Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        int y = 50;

        // Title
        paint.setTextSize(22);
        paint.setColor(Color.parseColor("#2196F3"));
        paint.setFakeBoldText(true);
        canvas.drawText("Smart Expense Tracker", 40, y, paint);

        y += 30;
        paint.setTextSize(16);
        paint.setColor(Color.BLACK);
        canvas.drawText("Monthly Expense Report - " + monthName, 40, y, paint);

        y += 25;
        paint.setTextSize(12);
        paint.setColor(Color.GRAY);
        paint.setFakeBoldText(false);
        canvas.drawText("Generated for: " + userName, 40, y, paint);

        y += 30;
        paint.setColor(Color.LTGRAY);
        canvas.drawLine(40, y, PAGE_WIDTH - 40, y, paint);

        // Summary Section
        y += 30;
        double totalSpent = 0;
        for (Expense e : expenses) totalSpent += e.getAmount();
        double remaining = totalBudget - totalSpent;

        paint.setTextSize(13);
        paint.setColor(Color.BLACK);
        paint.setFakeBoldText(true);
        canvas.drawText("Summary", 40, y, paint);

        y += 22;
        paint.setFakeBoldText(false);
        canvas.drawText("Total Budget: Rs. " + String.format("%.0f", totalBudget), 40, y, paint);
        y += 20;
        canvas.drawText("Total Spent: Rs. " + String.format("%.0f", totalSpent), 40, y, paint);
        y += 20;
        canvas.drawText("Remaining: Rs. " + String.format("%.0f", remaining), 40, y, paint);

        y += 30;
        paint.setColor(Color.LTGRAY);
        canvas.drawLine(40, y, PAGE_WIDTH - 40, y, paint);

        // Category Breakdown
        y += 30;
        paint.setColor(Color.BLACK);
        paint.setFakeBoldText(true);
        paint.setTextSize(13);
        canvas.drawText("Category Breakdown", 40, y, paint);

        Map<String, Double> categoryTotals = new HashMap<>();
        for (Expense e : expenses) {
            categoryTotals.put(e.getCategory(),
                    categoryTotals.getOrDefault(e.getCategory(), 0.0) + e.getAmount());
        }

        y += 22;
        paint.setFakeBoldText(false);
        paint.setTextSize(12);
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            canvas.drawText(entry.getKey() + ": Rs. " + String.format("%.0f", entry.getValue()), 40, y, paint);
            y += 18;
        }

        y += 15;
        paint.setColor(Color.LTGRAY);
        canvas.drawLine(40, y, PAGE_WIDTH - 40, y, paint);

        // Expense List Table
        y += 30;
        paint.setColor(Color.BLACK);
        paint.setFakeBoldText(true);
        paint.setTextSize(13);
        canvas.drawText("All Expenses", 40, y, paint);

        y += 20;
        paint.setTextSize(11);
        paint.setFakeBoldText(true);
        canvas.drawText("Date", 40, y, paint);
        canvas.drawText("Title", 130, y, paint);
        canvas.drawText("Category", 320, y, paint);
        canvas.drawText("Amount", 470, y, paint);

        y += 5;
        paint.setColor(Color.LTGRAY);
        canvas.drawLine(40, y, PAGE_WIDTH - 40, y, paint);

        y += 18;
        paint.setFakeBoldText(false);
        paint.setColor(Color.DKGRAY);

        for (Expense e : expenses) {
            if (y > PAGE_HEIGHT - 60) break; // simple single-page limit for beginner version

            canvas.drawText(e.getDate(), 40, y, paint);
            String title = e.getTitle().length() > 25 ? e.getTitle().substring(0, 25) + "..." : e.getTitle();
            canvas.drawText(title, 130, y, paint);
            canvas.drawText(e.getCategory(), 320, y, paint);
            canvas.drawText("Rs. " + String.format("%.0f", e.getAmount()), 470, y, paint);
            y += 18;
        }

        document.finishPage(page);

        File reportsDir = new File(context.getExternalFilesDir(null), "Reports");
        if (!reportsDir.exists()) reportsDir.mkdirs();

        File file = new File(reportsDir, "Expense_Report_" + monthName.replace(" ", "_") + ".pdf");
        FileOutputStream outputStream = new FileOutputStream(file);
        document.writeTo(outputStream);
        document.close();
        outputStream.close();

        return file;
    }
}