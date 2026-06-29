package com.maryam.smartexpensetracker.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.maryam.smartexpensetracker.R;
import com.maryam.smartexpensetracker.databinding.ActivityLoginBinding;
import com.maryam.smartexpensetracker.viewmodel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        setupClickListeners();
        observeViewModel();
    }

    private void setupClickListeners() {
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (validateInputs(email, password)) {
                authViewModel.login(email, password);
            }
        });

        binding.tvForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());

        binding.tvSignup.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });
    }

    private void observeViewModel() {
        authViewModel.loginResult.observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.btnLogin.setEnabled(false);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnLogin.setEnabled(true);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnLogin.setEnabled(true);
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }

    private boolean validateInputs(String email, String password) {
        boolean isValid = true;

        if (email.isEmpty()) {
            binding.tilEmail.setError("Email is required");
            isValid = false;
        } else {
            binding.tilEmail.setError(null);
        }

        if (password.isEmpty()) {
            binding.tilPassword.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            binding.tilPassword.setError("Password must be at least 6 characters");
            isValid = false;
        } else {
            binding.tilPassword.setError(null);
        }

        return isValid;
    }

    private void showForgotPasswordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot_password, null);
        TextInputEditText etEmail = dialogView.findViewById(R.id.etEmail);

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Send Reset Link", (dialog, which) -> {
                    String email = etEmail.getText().toString().trim();
                    if (!email.isEmpty()) {
                        authViewModel.forgotPassword(email);
                        observeForgotPassword();
                    } else {
                        Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void observeForgotPassword() {
        authViewModel.forgotPasswordResult.observe(this, resource -> {
            switch (resource.status) {
                case SUCCESS:
                    Toast.makeText(this, resource.data, Toast.LENGTH_LONG).show();
                    break;
                case ERROR:
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }
}