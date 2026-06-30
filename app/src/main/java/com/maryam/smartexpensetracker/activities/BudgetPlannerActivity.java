package com.maryam.smartexpensetracker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.widget.Toast;
import com.maryam.smartexpensetracker.databinding.ActivityBudgetPlannerBinding;
import com.maryam.smartexpensetracker.model.Budget;
import com.maryam.smartexpensetracker.viewmodel.AuthViewModel;
import com.maryam.smartexpensetracker.viewmodel.BudgetViewModel;
import com.maryam.smartexpensetracker.viewmodel.ExpenseViewModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BudgetPlannerActivity extends AppCompatActivity {

    private ActivityBudgetPlannerBinding binding;
    private BudgetViewModel budgetViewModel;
    private ExpenseViewModel expenseViewModel;
    private AuthViewModel authViewModel;
    private String userId;
    private String currentMonth;
    private Budget existingBudget;
    private double monthlySpent = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBudgetPlannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        budgetViewModel = new ViewModelProvider(this).get(BudgetViewModel.class);
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        userId = authViewModel.getCurrentUser().getUid();

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
        String monthDisplay = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(new Date());
        binding.tvCurrentMonth.setText(monthDisplay);

        observeBudgetData();
        setupSaveButton();
    }

    private void observeBudgetData() {
        budgetViewModel.getBudgetByMonth(userId, currentMonth).observe(this, budget -> {
            existingBudget = budget;
            if (budget != null) {
                binding.tvTotalBudget.setText(String.format("Rs. %.0f", budget.getBudgetAmount()));
                binding.etBudgetAmount.setText(String.valueOf(budget.getBudgetAmount()));
                updateProgress(budget.getBudgetAmount());
            }
        });

        expenseViewModel.getMonthlyTotal(userId, currentMonth).observe(this, total -> {
            monthlySpent = total != null ? total : 0;
            if (existingBudget != null) {
                updateProgress(existingBudget.getBudgetAmount());
            }
        });
    }

    private void updateProgress(double budgetAmount) {
        double remaining = budgetAmount - monthlySpent;
        int progress = budgetAmount > 0 ? (int) ((monthlySpent / budgetAmount) * 100) : 0;
        binding.progressBudget.setProgress(Math.min(progress, 100));

        binding.tvSpentRemaining.setText(String.format(
                "Spent: Rs. %.0f | Remaining: Rs. %.0f", monthlySpent, remaining));
    }

    private void setupSaveButton() {
        binding.btnSaveBudget.setOnClickListener(v -> {
            String amountStr = binding.etBudgetAmount.getText().toString().trim();

            if (amountStr.isEmpty()) {
                binding.tilBudgetAmount.setError("Enter budget amount");
                return;
            }
            binding.tilBudgetAmount.setError(null);

            double amount = Double.parseDouble(amountStr);

            if (existingBudget != null) {
                existingBudget.setBudgetAmount(amount);
                budgetViewModel.updateBudget(existingBudget);
            } else {
                Budget newBudget = new Budget(amount, currentMonth, userId);
                budgetViewModel.insertBudget(newBudget);
            }

            binding.tvTotalBudget.setText(String.format("Rs. %.0f", amount));
            updateProgress(amount);
            Toast.makeText(this, "Budget saved successfully!", Toast.LENGTH_SHORT).show();
        });
    }
}