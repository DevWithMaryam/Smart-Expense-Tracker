package com.maryam.smartexpensetracker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.view.View;
import com.maryam.smartexpensetracker.databinding.ActivityAiSuggestionsBinding;
import com.maryam.smartexpensetracker.model.Budget;
import com.maryam.smartexpensetracker.model.Expense;
import com.maryam.smartexpensetracker.viewmodel.AIViewModel;
import com.maryam.smartexpensetracker.viewmodel.AuthViewModel;
import com.maryam.smartexpensetracker.viewmodel.BudgetViewModel;
import com.maryam.smartexpensetracker.viewmodel.ExpenseViewModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AISuggestionsActivity extends AppCompatActivity {

    private ActivityAiSuggestionsBinding binding;
    private AIViewModel aiViewModel;
    private ExpenseViewModel expenseViewModel;
    private BudgetViewModel budgetViewModel;
    private AuthViewModel authViewModel;

    private String userId;
    private String currentMonth;
    private List<Expense> currentExpenses = new ArrayList<>();
    private double currentBudget = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAiSuggestionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        aiViewModel = new ViewModelProvider(this).get(AIViewModel.class);
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        budgetViewModel = new ViewModelProvider(this).get(BudgetViewModel.class);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        userId = authViewModel.getCurrentUser().getUid();

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());

        observeData();
        setupButtons();
        observeAIResult();
    }

    private void observeData() {
        expenseViewModel.getExpensesByMonth(userId, currentMonth).observe(this, expenses -> {
            currentExpenses = expenses != null ? expenses : new ArrayList<>();
        });

        budgetViewModel.getBudgetByMonth(userId, currentMonth).observe(this, budget -> {
            if (budget != null) currentBudget = budget.getBudgetAmount();
        });
    }

    private void setupButtons() {
        binding.btnGetInsights.setOnClickListener(v -> requestInsights());
        binding.btnRefresh.setOnClickListener(v -> requestInsights());
    }

    private void requestInsights() {
        if (currentExpenses.isEmpty()) {
            showError("No expenses found for this month. Add some expenses first to get AI insights.");
            return;
        }

        if (currentBudget == 0) {
            showError("Please set a monthly budget first to get accurate AI insights.");
            return;
        }

        double totalSpent = 0;
        for (Expense e : currentExpenses) totalSpent += e.getAmount();

        showLoading();
        aiViewModel.getSpendingInsights(currentExpenses, currentBudget, totalSpent);
    }

    private void observeAIResult() {
        aiViewModel.suggestionResult.observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    showLoading();
                    break;
                case SUCCESS:
                    showResults();
                    binding.tvSpendingAnalysis.setText(resource.data.getSpendingAnalysis());
                    binding.tvSavingTips.setText(resource.data.getSavingTips());
                    binding.tvBudgetAdvice.setText(resource.data.getBudgetAdvice());
                    break;
                case ERROR:
                    showError(resource.message);
                    break;
            }
        });
    }

    private void showLoading() {
        binding.layoutInitial.setVisibility(View.GONE);
        binding.layoutLoading.setVisibility(View.VISIBLE);
        binding.layoutResults.setVisibility(View.GONE);
        binding.tvError.setVisibility(View.GONE);
    }

    private void showResults() {
        binding.layoutInitial.setVisibility(View.GONE);
        binding.layoutLoading.setVisibility(View.GONE);
        binding.layoutResults.setVisibility(View.VISIBLE);
        binding.tvError.setVisibility(View.GONE);
    }

    private void showError(String message) {
        binding.layoutInitial.setVisibility(View.VISIBLE);
        binding.layoutLoading.setVisibility(View.GONE);
        binding.layoutResults.setVisibility(View.GONE);
        binding.tvError.setVisibility(View.VISIBLE);
        binding.tvError.setText(message);
    }
}