package com.maryam.smartexpensetracker.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.maryam.smartexpensetracker.model.AISuggestion;
import com.maryam.smartexpensetracker.model.Expense;
import com.maryam.smartexpensetracker.repository.AIRepository;
import com.maryam.smartexpensetracker.utils.Resource;
import java.util.List;

public class AIViewModel extends ViewModel {

    private final AIRepository aiRepository;
    public MutableLiveData<Resource<AISuggestion>> suggestionResult = new MutableLiveData<>();

    public AIViewModel() {
        aiRepository = new AIRepository();
    }

    public void getSpendingInsights(List<Expense> expenses, double budget, double totalSpent) {
        aiRepository.getSpendingInsights(expenses, budget, totalSpent, suggestionResult);
    }
}