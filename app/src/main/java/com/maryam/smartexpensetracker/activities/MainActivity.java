package com.maryam.smartexpensetracker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import com.maryam.smartexpensetracker.databinding.ActivityMainBinding;
import com.maryam.smartexpensetracker.viewmodel.AuthViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        setSupportActionBar(binding.toolbar);
        setupClickListeners();
        setWelcomeMessage();
    }

    private void setWelcomeMessage() {
        if (authViewModel.getCurrentUser() != null) {
            String name = authViewModel.getCurrentUser().getDisplayName();
            if (name != null && !name.isEmpty()) {
                binding.toolbar.setSubtitle("Welcome, " + name + "!");
            }
        }
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