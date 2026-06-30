package com.maryam.smartexpensetracker.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.maryam.smartexpensetracker.R;
import com.maryam.smartexpensetracker.databinding.ActivityExpenseDetailsBinding;
import com.maryam.smartexpensetracker.model.Expense;
import com.maryam.smartexpensetracker.viewmodel.ExpenseViewModel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ExpenseDetailsActivity extends AppCompatActivity {

    private ActivityExpenseDetailsBinding binding;
    private ExpenseViewModel expenseViewModel;
    private Expense currentExpense;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExpenseDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        setupCategoryDropdown();
        loadExpenseData();
        setupDatePicker();
        setupButtons();
    }

    private void setupCategoryDropdown() {
        String[] categories = getResources().getStringArray(R.array.expense_categories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        binding.actvCategory.setAdapter(adapter);
    }

    private void loadExpenseData() {
        int expenseId = getIntent().getIntExtra("expense_id", -1);
        if (expenseId == -1) {
            finish();
            return;
        }

        expenseViewModel.getExpenseById(expenseId).observe(this, expense -> {
            if (expense != null) {
                currentExpense = expense;
                binding.etTitle.setText(expense.getTitle());
                binding.etAmount.setText(String.valueOf(expense.getAmount()));
                binding.actvCategory.setText(expense.getCategory(), false);
                binding.etDate.setText(expense.getDate());
                binding.etNotes.setText(expense.getNotes());
                selectedDate = expense.getDate();
            }
        });
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

    private void setupButtons() {
        binding.btnUpdate.setOnClickListener(v -> {
            if (currentExpense == null) return;

            String title = binding.etTitle.getText().toString().trim();
            String amountStr = binding.etAmount.getText().toString().trim();
            String category = binding.actvCategory.getText().toString().trim();
            String notes = binding.etNotes.getText().toString().trim();

            if (title.isEmpty() || amountStr.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            currentExpense.setTitle(title);
            currentExpense.setAmount(Double.parseDouble(amountStr));
            currentExpense.setCategory(category);
            currentExpense.setDate(selectedDate);
            currentExpense.setNotes(notes);

            expenseViewModel.updateExpense(currentExpense);
            Toast.makeText(this, "Expense updated!", Toast.LENGTH_SHORT).show();
            finish();
        });

        binding.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Expense")
                    .setMessage("Are you sure you want to delete this expense?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        expenseViewModel.deleteExpense(currentExpense);
                        Toast.makeText(this, "Expense deleted!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
}