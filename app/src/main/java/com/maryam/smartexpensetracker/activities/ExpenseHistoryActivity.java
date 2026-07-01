package com.maryam.smartexpensetracker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import com.maryam.smartexpensetracker.adapter.ExpenseAdapter;
import com.maryam.smartexpensetracker.databinding.ActivityExpenseHistoryBinding;
import com.maryam.smartexpensetracker.model.Expense;
import com.maryam.smartexpensetracker.utils.Constants;
import com.maryam.smartexpensetracker.viewmodel.AuthViewModel;
import com.maryam.smartexpensetracker.viewmodel.ExpenseViewModel;
import java.util.ArrayList;
import java.util.List;

public class ExpenseHistoryActivity extends AppCompatActivity {

    private ActivityExpenseHistoryBinding binding;
    private ExpenseViewModel expenseViewModel;
    private AuthViewModel authViewModel;
    private ExpenseAdapter adapter;
    private List<Expense> fullExpenseList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExpenseHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        setupRecyclerView();
        observeExpenses();
        setupSearch();
    }

    private void setupRecyclerView() {
        adapter = new ExpenseAdapter(expense -> {
            Intent intent = new Intent(this, ExpenseDetailsActivity.class);
            intent.putExtra(Constants.KEY_EXPENSE_ID, expense.getId());            startActivity(intent);
        });
        binding.rvExpenses.setLayoutManager(new LinearLayoutManager(this));
        binding.rvExpenses.setAdapter(adapter);
    }

    private void observeExpenses() {
        String userId = authViewModel.getCurrentUser().getUid();
        expenseViewModel.getAllExpenses(userId).observe(this, expenses -> {
            fullExpenseList = expenses;
            adapter.setExpenseList(expenses);
            toggleEmptyState(expenses.isEmpty());
        });
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterExpenses(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterExpenses(String query) {
        if (query.isEmpty()) {
            adapter.setExpenseList(fullExpenseList);
            toggleEmptyState(fullExpenseList.isEmpty());
            return;
        }

        List<Expense> filtered = new ArrayList<>();
        for (Expense e : fullExpenseList) {
            if (e.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    e.getCategory().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(e);
            }
        }
        adapter.setExpenseList(filtered);
        toggleEmptyState(filtered.isEmpty());
    }

    private void toggleEmptyState(boolean isEmpty) {
        binding.tvEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.rvExpenses.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}