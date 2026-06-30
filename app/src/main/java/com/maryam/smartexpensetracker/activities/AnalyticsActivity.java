package com.maryam.smartexpensetracker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.maryam.smartexpensetracker.R;
import com.maryam.smartexpensetracker.databinding.ActivityAnalyticsBinding;
import com.maryam.smartexpensetracker.model.Expense;
import com.maryam.smartexpensetracker.viewmodel.AuthViewModel;
import com.maryam.smartexpensetracker.viewmodel.ExpenseViewModel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AnalyticsActivity extends AppCompatActivity {

    private ActivityAnalyticsBinding binding;
    private ExpenseViewModel expenseViewModel;
    private AuthViewModel authViewModel;
    private String userId;
    private String currentMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnalyticsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        userId = authViewModel.getCurrentUser().getUid();

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());

        observeData();
    }

    private void observeData() {
        expenseViewModel.getExpensesByMonth(userId, currentMonth).observe(this, expenses -> {
            if (expenses == null || expenses.isEmpty()) {
                binding.tvNoData.setVisibility(View.VISIBLE);
                binding.layoutCategoryBreakdown.setVisibility(View.GONE);
                binding.tvMonthlyTotal.setText("Rs. 0");
                binding.tvDailyAverage.setText("Rs. 0");
                binding.tvWeeklyTotal.setText("Rs. 0");
                return;
            }

            binding.tvNoData.setVisibility(View.GONE);
            binding.layoutCategoryBreakdown.setVisibility(View.VISIBLE);

            calculateSummary(expenses);
            calculateCategoryBreakdown(expenses);
        });
    }

    private void calculateSummary(List<Expense> expenses) {
        double monthlyTotal = 0;
        double weeklyTotal = 0;

        Calendar weekAgo = Calendar.getInstance();
        weekAgo.add(Calendar.DAY_OF_YEAR, -7);
        String weekAgoDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(weekAgo.getTime());

        for (Expense e : expenses) {
            monthlyTotal += e.getAmount();
            if (e.getDate().compareTo(weekAgoDate) >= 0) {
                weeklyTotal += e.getAmount();
            }
        }

        Calendar cal = Calendar.getInstance();
        int daysInMonth = cal.get(Calendar.DAY_OF_MONTH);
        double dailyAverage = daysInMonth > 0 ? monthlyTotal / daysInMonth : 0;

        binding.tvMonthlyTotal.setText(String.format("Rs. %.0f", monthlyTotal));
        binding.tvWeeklyTotal.setText(String.format("Rs. %.0f", weeklyTotal));
        binding.tvDailyAverage.setText(String.format("Rs. %.0f", dailyAverage));
    }

    private void calculateCategoryBreakdown(List<Expense> expenses) {
        binding.layoutCategoryBreakdown.removeAllViews();

        Map<String, Double> categoryTotals = new HashMap<>();
        double grandTotal = 0;

        for (Expense e : expenses) {
            categoryTotals.put(e.getCategory(),
                    categoryTotals.getOrDefault(e.getCategory(), 0.0) + e.getAmount());
            grandTotal += e.getAmount();
        }

        LayoutInflater inflater = LayoutInflater.from(this);

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            View itemView = inflater.inflate(R.layout.item_category_breakdown, binding.layoutCategoryBreakdown, false);

            View colorDot = itemView.findViewById(R.id.colorDot);
            android.widget.TextView tvName = itemView.findViewById(R.id.tvCategoryName);
            android.widget.TextView tvAmount = itemView.findViewById(R.id.tvCategoryAmount);
            LinearProgressIndicator progress = itemView.findViewById(R.id.progressCategory);

            tvName.setText(entry.getKey());
            tvAmount.setText(String.format("Rs. %.0f", entry.getValue()));

            int percentage = grandTotal > 0 ? (int) ((entry.getValue() / grandTotal) * 100) : 0;
            progress.setProgress(percentage);

            int color = getCategoryColor(entry.getKey());
            colorDot.setBackgroundColor(color);
            progress.setIndicatorColor(color);

            binding.layoutCategoryBreakdown.addView(itemView);
        }
    }

    private int getCategoryColor(String category) {
        switch (category) {
            case "Food": return getColor(R.color.chart_food);
            case "Transport": return getColor(R.color.chart_transport);
            case "Shopping": return getColor(R.color.chart_shopping);
            case "Health": return getColor(R.color.chart_health);
            case "Entertainment": return getColor(R.color.chart_entertainment);
            default: return getColor(R.color.chart_other);
        }
    }
}