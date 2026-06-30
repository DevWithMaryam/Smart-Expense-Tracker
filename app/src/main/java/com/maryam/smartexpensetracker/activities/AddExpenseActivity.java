package com.maryam.smartexpensetracker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.maryam.smartexpensetracker.R;
import com.maryam.smartexpensetracker.databinding.ActivityAddExpenseBinding;
import com.maryam.smartexpensetracker.model.Expense;
import com.maryam.smartexpensetracker.viewmodel.AuthViewModel;
import com.maryam.smartexpensetracker.viewmodel.ExpenseViewModel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {

    private ActivityAddExpenseBinding binding;
    private ExpenseViewModel expenseViewModel;
    private AuthViewModel authViewModel;
    private String userId;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        userId = authViewModel.getCurrentUser().getUid();

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        setupCategoryDropdown();
        setupDatePicker();
        setupSaveButton();

        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        binding.etDate.setText(selectedDate);
    }

    private void setupCategoryDropdown() {
        String[] categories = getResources().getStringArray(R.array.expense_categories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        binding.actvCategory.setAdapter(adapter);
    }

    private void setupDatePicker() {
        binding.etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(this,
                    (view, year, month, day) -> {
                        Calendar selected = Calendar.getInstance();
                        selected.set(year, month, day);
                        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selected.getTime());
                        binding.etDate.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });
    }

    private void setupSaveButton() {
        binding.btnSaveExpense.setOnClickListener(v -> {
            String title = binding.etTitle.getText().toString().trim();
            String amountStr = binding.etAmount.getText().toString().trim();
            String category = binding.actvCategory.getText().toString().trim();
            String notes = binding.etNotes.getText().toString().trim();

            if (!validateInputs(title, amountStr, category)) return;

            double amount = Double.parseDouble(amountStr);
            Expense expense = new Expense(title, amount, category, selectedDate, notes, userId);
            expenseViewModel.insertExpense(expense);

            Toast.makeText(this, "Expense added successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private boolean validateInputs(String title, String amount, String category) {
        boolean isValid = true;

        if (title.isEmpty()) {
            binding.tilTitle.setError("Title is required");
            isValid = false;
        } else {
            binding.tilTitle.setError(null);
        }

        if (amount.isEmpty()) {
            binding.tilAmount.setError("Amount is required");
            isValid = false;
        } else if (Double.parseDouble(amount.isEmpty() ? "0" : amount) <= 0) {
            binding.tilAmount.setError("Enter a valid amount");
            isValid = false;
        } else {
            binding.tilAmount.setError(null);
        }

        if (category.isEmpty()) {
            binding.tilCategory.setError("Select a category");
            isValid = false;
        } else {
            binding.tilCategory.setError(null);
        }

        return isValid;
    }
}