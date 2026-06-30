package com.maryam.smartexpensetracker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseUser;
import com.maryam.smartexpensetracker.databinding.ActivityMainBinding;
import com.maryam.smartexpensetracker.viewmodel.AuthViewModel;
import com.maryam.smartexpensetracker.viewmodel.BudgetViewModel;
import com.maryam.smartexpensetracker.viewmodel.ExpenseViewModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AuthViewModel authViewModel;
    private ExpenseViewModel expenseViewModel;
    private BudgetViewModel budgetViewModel;
    private String userId;
    private String today;
    private String currentMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        budgetViewModel = new ViewModelProvider(this).get(BudgetViewModel.class);

        setSupportActionBar(binding.toolbar);

        FirebaseUser user = authViewModel.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        userId = user.getUid();
        today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());

        String name = user.getDisplayName();
        if (name != null && !name.isEmpty()) {
            binding.toolbar.setSubtitle("Welcome, " + name + "!");
        }

        observeDashboardData();
        setupClickListeners();
    }

    private void observeDashboardData() {
        expenseViewModel.getTodayTotal(userId, today).observe(this, total -> {
            double amount = total != null ? total : 0.0;
            binding.tvTodayExpense.setText(String.format("Rs. %.0f", amount));
        });

        expenseViewModel.getMonthlyTotal(userId, currentMonth).observe(this, total -> {
            double monthlyTotal = total != null ? total : 0.0;
            binding.tvMonthlyExpense.setText(String.format("Rs. %.0f", monthlyTotal));

            budgetViewModel.getBudgetByMonth(userId, currentMonth).observe(this, budget -> {
                if (budget != null) {
                    double remaining = budget.getBudgetAmount() - monthlyTotal;
                    binding.tvRemainingBudget.setText(String.format("Rs. %.0f", remaining));
                } else {
                    binding.tvRemainingBudget.setText("Set your budget");
                }
            });
        });
    }

    private void setupClickListeners() {
        binding.cardAddExpense.setOnClickListener(v ->
                startActivity(new Intent(this, AddExpenseActivity.class)));
        binding.cardExpenseHistory.setOnClickListener(v ->
                startActivity(new Intent(this, ExpenseHistoryActivity.class)));
        binding.cardBudgetPlanner.setOnClickListener(v ->
                startActivity(new Intent(this, BudgetPlannerActivity.class)));
        binding.cardAnalytics.setOnClickListener(v ->
                startActivity(new Intent(this, AnalyticsActivity.class)));
        binding.cardCharts.setOnClickListener(v ->
                startActivity(new Intent(this, ChartsActivity.class)));
        binding.cardAiSuggestions.setOnClickListener(v ->
                startActivity(new Intent(this, AISuggestionsActivity.class)));
        binding.cardReports.setOnClickListener(v ->
                startActivity(new Intent(this, ReportsActivity.class)));
        binding.cardProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }
}