package com.maryam.smartexpensetracker.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseUser;
import com.maryam.smartexpensetracker.databinding.ActivityProfileBinding;
import com.maryam.smartexpensetracker.model.Expense;
import com.maryam.smartexpensetracker.viewmodel.AuthViewModel;
import com.maryam.smartexpensetracker.viewmodel.ExpenseViewModel;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private AuthViewModel authViewModel;
    private ExpenseViewModel expenseViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        loadUserProfile();
        loadStats();
        setupClickListeners();
    }

    private void loadUserProfile() {
        FirebaseUser user = authViewModel.getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName() != null ? user.getDisplayName() : "User";
            String email = user.getEmail() != null ? user.getEmail() : "";

            binding.tvUserName.setText(name);
            binding.tvUserEmail.setText(email);
            binding.tvAvatarInitial.setText(name.substring(0, 1).toUpperCase());

            binding.layoutVerifiedBadge.setVisibility(
                    user.isEmailVerified() ? View.VISIBLE : View.GONE);
        }
    }

    private void loadStats() {
        String userId = authViewModel.getCurrentUser().getUid();
        expenseViewModel.getAllExpenses(userId).observe(this, expenses -> {
            if (expenses != null) {
                binding.tvTotalExpensesCount.setText(String.valueOf(expenses.size()));
                double total = 0;
                for (Expense e : expenses) total += e.getAmount();
                binding.tvTotalSpentAllTime.setText(String.format("Rs. %.0f", total));
            }
        });
    }

    private void setupClickListeners() {

        binding.optionChangePassword.setOnClickListener(v -> {
            String email = authViewModel.getCurrentUser().getEmail();
            authViewModel.forgotPassword(email);
            authViewModel.forgotPasswordResult.observe(this, resource -> {
                switch (resource.status) {
                    case SUCCESS:
                        Toast.makeText(this, "Password reset email sent!", Toast.LENGTH_LONG).show();
                        break;
                    case ERROR:
                        Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show();
                        break;
                }
            });
        });

        binding.optionAbout.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("About Smart Expense Tracker")
                        .setMessage("Version 1.0\n\nA smart expense tracking app with AI-powered spending insights.\n\nBuilt with:\n• MVVM Architecture\n• Room Database\n• Firebase Authentication\n• Gemini AI\n• MPAndroidChart\n\nDeveloped by Maryam.")
                        .setPositiveButton("OK", null)
                        .show());

        binding.btnLogout.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Logout", (dialog, which) -> performLogout())
                        .setNegativeButton("Cancel", null)
                        .show());

        binding.btnDeleteAccount.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("⚠️ Delete Account")
                        .setMessage("This will PERMANENTLY delete your account and ALL your data including:\n\n• All expenses\n• Budget settings\n• Reports\n\nThis action CANNOT be undone. Are you absolutely sure?")
                        .setPositiveButton("Yes, Delete Everything", (dialog, which) -> performDeleteAccount())
                        .setNegativeButton("Cancel", null)
                        .show());
    }

    private void performLogout() {
        authViewModel.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void performDeleteAccount() {
        authViewModel.deleteAccount();
        authViewModel.deleteAccountResult.observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    binding.btnDeleteAccount.setEnabled(false);
                    binding.btnDeleteAccount.setText("Deleting...");
                    break;
                case SUCCESS:
                    Toast.makeText(this,
                            "Account deleted successfully.",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    break;
                case ERROR:
                    binding.btnDeleteAccount.setEnabled(true);
                    binding.btnDeleteAccount.setText("DELETE ACCOUNT");
                    Toast.makeText(this, "Error: " + resource.message, Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }
}