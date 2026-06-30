package com.maryam.smartexpensetracker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.tabs.TabLayout;
import com.maryam.smartexpensetracker.R;
import com.maryam.smartexpensetracker.databinding.ActivityChartsBinding;
import com.maryam.smartexpensetracker.model.Expense;
import com.maryam.smartexpensetracker.viewmodel.AuthViewModel;
import com.maryam.smartexpensetracker.viewmodel.ExpenseViewModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class ChartsActivity extends AppCompatActivity {

    private ActivityChartsBinding binding;
    private ExpenseViewModel expenseViewModel;
    private AuthViewModel authViewModel;
    private List<Expense> currentExpenses = new ArrayList<>();

    private final int[] chartColors = {
            Color.parseColor("#FF5722"),
            Color.parseColor("#2196F3"),
            Color.parseColor("#9C27B0"),
            Color.parseColor("#4CAF50"),
            Color.parseColor("#FF9800"),
            Color.parseColor("#607D8B"),
            Color.parseColor("#E91E63"),
            Color.parseColor("#00BCD4")
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChartsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        setupTabs();
        observeExpenses();
    }

    private void observeExpenses() {
        String userId = authViewModel.getCurrentUser().getUid();
        String currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());

        expenseViewModel.getExpensesByMonth(userId, currentMonth).observe(this, expenses -> {
            currentExpenses = expenses != null ? expenses : new ArrayList<>();

            if (currentExpenses.isEmpty()) {
                binding.tvChartNoData.setVisibility(View.VISIBLE);
                binding.pieChart.setVisibility(View.GONE);
                binding.barChart.setVisibility(View.GONE);
                binding.lineChart.setVisibility(View.GONE);
                return;
            }

            binding.tvChartNoData.setVisibility(View.GONE);
            setupPieChart();
            setupBarChart();
            setupLineChart();
        });
    }

    private void setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.pieChart.setVisibility(View.GONE);
                binding.barChart.setVisibility(View.GONE);
                binding.lineChart.setVisibility(View.GONE);

                if (currentExpenses.isEmpty()) return;

                switch (tab.getPosition()) {
                    case 0: binding.pieChart.setVisibility(View.VISIBLE); break;
                    case 1: binding.barChart.setVisibility(View.VISIBLE); break;
                    case 2: binding.lineChart.setVisibility(View.VISIBLE); break;
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupPieChart() {
        PieChart pieChart = binding.pieChart;

        Map<String, Double> categoryTotals = new HashMap<>();
        for (Expense e : currentExpenses) {
            categoryTotals.put(e.getCategory(),
                    categoryTotals.getOrDefault(e.getCategory(), 0.0) + e.getAmount());
        }

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(chartColors[0], chartColors[1], chartColors[2], chartColors[3],
                chartColors[4], chartColors[5], chartColors[6], chartColors[7]);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(10f);
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.getLegend().setEnabled(true);
        pieChart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        pieChart.animateY(800);
        pieChart.invalidate();
    }

    private void setupBarChart() {
        BarChart barChart = binding.barChart;

        Map<String, Double> categoryTotals = new TreeMap<>();
        for (Expense e : currentExpenses) {
            categoryTotals.put(e.getCategory(),
                    categoryTotals.getOrDefault(e.getCategory(), 0.0) + e.getAmount());
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue().floatValue()));
            labels.add(entry.getKey());
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Spending by Category");
        dataSet.setColors(chartColors);
        dataSet.setValueTextSize(11f);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f);

        barChart.setData(data);
        barChart.getDescription().setEnabled(false);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.animateY(800);
        barChart.invalidate();
    }

    private void setupLineChart() {
        LineChart lineChart = binding.lineChart;

        Map<String, Double> dailyTotals = new TreeMap<>();
        for (Expense e : currentExpenses) {
            String day = e.getDate().substring(8, 10);
            dailyTotals.put(day, dailyTotals.getOrDefault(day, 0.0) + e.getAmount());
        }

        List<com.github.mikephil.charting.data.Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, Double> entry : dailyTotals.entrySet()) {
            entries.add(new com.github.mikephil.charting.data.Entry(index, entry.getValue().floatValue()));
            labels.add(entry.getKey());
            index++;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Daily Spending");
        dataSet.setColor(Color.parseColor("#2196F3"));
        dataSet.setCircleColor(Color.parseColor("#2196F3"));
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleRadius(4f);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#2196F3"));
        dataSet.setFillAlpha(40);

        LineData data = new LineData(dataSet);
        lineChart.setData(data);
        lineChart.getDescription().setEnabled(false);
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setGranularity(1f);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.animateX(800);
        lineChart.invalidate();
    }
}