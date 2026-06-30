package com.maryam.smartexpensetracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.maryam.smartexpensetracker.R;
import com.maryam.smartexpensetracker.databinding.ItemExpenseBinding;
import com.maryam.smartexpensetracker.model.Expense;
import java.util.ArrayList;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenseList = new ArrayList<>();
    private final OnExpenseClickListener listener;

    public interface OnExpenseClickListener {
        void onExpenseClick(Expense expense);
    }

    public ExpenseAdapter(OnExpenseClickListener listener) {
        this.listener = listener;
    }

    public void setExpenseList(List<Expense> expenseList) {
        this.expenseList = expenseList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemExpenseBinding binding = ItemExpenseBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ExpenseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.bind(expense);
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private final ItemExpenseBinding binding;

        public ExpenseViewHolder(ItemExpenseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Expense expense) {
            binding.tvTitle.setText(expense.getTitle());
            binding.tvCategoryDate.setText(expense.getCategory() + " • " + expense.getDate());
            binding.tvAmount.setText(String.format("Rs. %.0f", expense.getAmount()));

            int color = getCategoryColor(expense.getCategory());
            binding.categoryIndicator.setBackgroundColor(color);

            binding.getRoot().setOnClickListener(v -> listener.onExpenseClick(expense));
        }

        private int getCategoryColor(String category) {
            switch (category) {
                case "Food": return binding.getRoot().getContext().getColor(R.color.chart_food);
                case "Transport": return binding.getRoot().getContext().getColor(R.color.chart_transport);
                case "Shopping": return binding.getRoot().getContext().getColor(R.color.chart_shopping);
                case "Health": return binding.getRoot().getContext().getColor(R.color.chart_health);
                case "Entertainment": return binding.getRoot().getContext().getColor(R.color.chart_entertainment);
                default: return binding.getRoot().getContext().getColor(R.color.chart_other);
            }
        }
    }
}