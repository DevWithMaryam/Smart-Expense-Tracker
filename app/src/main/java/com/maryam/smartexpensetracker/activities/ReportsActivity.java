package com.maryam.smartexpensetracker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.maryam.smartexpensetracker.databinding.ActivityReportsBinding;
import com.maryam.smartexpensetracker.model.Budget;
import com.maryam.smartexpensetracker.model.Expense;
import com.maryam.smartexpensetracker.reports.ReportGenerator;
import com.maryam.smartexpensetracker.viewmodel.AuthViewModel;
import com.maryam.smartexpensetracker.viewmodel.BudgetViewModel;
import com.maryam.smartexpensetracker.viewmodel.ExpenseViewModel;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportsActivity extends AppCompatActivity {

    private ActivityReportsBinding binding;
    private ExpenseViewModel expenseViewModel;
    private BudgetViewModel budgetViewModel;
    private AuthViewModel authViewModel;
    private String userId;
    private String currentMonth;
    private String monthName;
    private List<Expense> currentExpenses;
    private double currentBudget = 0;
    private File generatedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        budgetViewModel = new ViewModelProvider(this).get(BudgetViewModel.class);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        userId = authViewModel.getCurrentUser().getUid();

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
        monthName = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(new Date());
        binding.tvCurrentMonth.setText(monthName);

        observeData();
        setupButtons();
    }

    private void observeData() {
        expenseViewModel.getExpensesByMonth(userId, currentMonth).observe(this, expenses -> {
            currentExpenses = expenses;
        });

        budgetViewModel.getBudgetByMonth(userId, currentMonth).observe(this, budget -> {
            if (budget != null) currentBudget = budget.getBudgetAmount();
        });
    }

    private void setupButtons() {
        binding.btnGenerateReport.setOnClickListener(v -> generateReport());
        binding.btnShareReport.setOnClickListener(v -> shareReport());
    }

    private void generateReport() {
        if (currentExpenses == null || currentExpenses.isEmpty()) {
            Toast.makeText(this, "No expenses found for this month", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnGenerateReport.setEnabled(false);

        try {
            String userName = authViewModel.getCurrentUser().getDisplayName();
            if (userName == null) userName = "User";

            generatedFile = ReportGenerator.generateMonthlyReport(
                    this, currentExpenses, monthName, currentBudget, userName);

            binding.progressBar.setVisibility(View.GONE);
            binding.btnGenerateReport.setEnabled(true);
            binding.btnShareReport.setVisibility(View.VISIBLE);

            Toast.makeText(this, "Report generated successfully!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnGenerateReport.setEnabled(true);
            Toast.makeText(this, "Error generating report: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void shareReport() {
        if (generatedFile == null || !generatedFile.exists()) {
            Toast.makeText(this, "Please generate a report first", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri fileUri = FileProvider.getUriForFile(this,
                getPackageName() + ".provider", generatedFile);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/pdf");
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, "Share Report"));
    }
}