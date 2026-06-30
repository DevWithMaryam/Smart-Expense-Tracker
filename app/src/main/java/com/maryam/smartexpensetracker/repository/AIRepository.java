package com.maryam.smartexpensetracker.repository;

import androidx.lifecycle.MutableLiveData;
import com.maryam.smartexpensetracker.BuildConfig;
import com.maryam.smartexpensetracker.model.AISuggestion;
import com.maryam.smartexpensetracker.model.Expense;
import com.maryam.smartexpensetracker.network.GeminiApiService;
import com.maryam.smartexpensetracker.network.GeminiRequest;
import com.maryam.smartexpensetracker.network.RetrofitClient;
import com.maryam.smartexpensetracker.utils.Resource;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AIRepository {

    private final GeminiApiService apiService;
    private static final String MODEL = "gemini-2.0-flash";

    // 🔧 TEMP TOGGLE: set to true to use mock data instead of real Gemini API
    private static final boolean USE_MOCK_DATA = true;

    public AIRepository() {
        apiService = RetrofitClient.getGeminiService();
    }

    public void getSpendingInsights(List<Expense> expenses, double budget, double totalSpent,
                                    MutableLiveData<Resource<AISuggestion>> result) {

        result.setValue(Resource.loading());

        if (USE_MOCK_DATA) {
            generateMockInsights(expenses, budget, totalSpent, result);
            return;
        }

        String prompt = buildPrompt(expenses, budget, totalSpent);
        GeminiRequest request = new GeminiRequest(prompt);

        apiService.generateContent(MODEL, BuildConfig.GEMINI_API_KEY, request)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                String responseText = response.body().string();
                                AISuggestion suggestion = parseGeminiResponse(responseText);
                                result.setValue(Resource.success(suggestion));
                            } catch (Exception e) {
                                result.setValue(Resource.error("Failed to parse AI response: " + e.getMessage()));
                            }
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                result.setValue(Resource.error("AI request failed: " + errorBody));
                            } catch (Exception e) {
                                result.setValue(Resource.error("AI request failed"));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        result.setValue(Resource.error("Network error: " + t.getMessage()));
                    }
                });
    }

    /**
     * Generates realistic, data-driven insights WITHOUT calling any external API.
     * This is a temporary local fallback. Swap USE_MOCK_DATA to false once
     * a billed Gemini API key is available, and this method becomes unused.
     */
    private void generateMockInsights(List<Expense> expenses, double budget, double totalSpent,
                                      MutableLiveData<Resource<AISuggestion>> result) {

        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {

            Map<String, Double> categoryTotals = new HashMap<>();
            for (Expense e : expenses) {
                categoryTotals.put(e.getCategory(),
                        categoryTotals.getOrDefault(e.getCategory(), 0.0) + e.getAmount());
            }

            String topCategory = "Other";
            double topAmount = 0;
            for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
                if (entry.getValue() > topAmount) {
                    topAmount = entry.getValue();
                    topCategory = entry.getKey();
                }
            }

            double percentUsed = budget > 0 ? (totalSpent / budget) * 100 : 0;

            String spendingAnalysis = String.format(
                    "You've spent Rs. %.0f so far this month across %d transactions. Your highest spending category is %s at Rs. %.0f, making up %.0f%% of your total expenses.",
                    totalSpent, expenses.size(), topCategory, topAmount,
                    totalSpent > 0 ? (topAmount / totalSpent) * 100 : 0
            );

            String savingTips = String.format(
                    "Consider setting a weekly limit for %s to control your biggest expense category. Track small recurring purchases, they add up over time. Try comparing prices before purchases in this category to find better deals.",
                    topCategory
            );

            String budgetAdvice;
            if (percentUsed >= 90) {
                budgetAdvice = String.format("You've used %.0f%% of your monthly budget. You're very close to your limit, consider pausing non-essential spending for the rest of the month.", percentUsed);
            } else if (percentUsed >= 70) {
                budgetAdvice = String.format("You've used %.0f%% of your monthly budget. You're on track but should monitor spending closely for the remaining days.", percentUsed);
            } else {
                budgetAdvice = String.format("You've used only %.0f%% of your monthly budget. You're managing your finances well this month, keep it up!", percentUsed);
            }

            AISuggestion suggestion = new AISuggestion(spendingAnalysis, savingTips, budgetAdvice);
            result.setValue(Resource.success(suggestion));

        }, 1500); // simulate network delay for realistic UX
    }

    private String buildPrompt(List<Expense> expenses, double budget, double totalSpent) {
        StringBuilder expenseList = new StringBuilder();
        for (Expense e : expenses) {
            expenseList.append("- ").append(e.getCategory()).append(": Rs. ")
                    .append(e.getAmount()).append(" (").append(e.getTitle()).append(")\n");
        }

        return "You are a financial advisor analyzing a user's monthly expenses. " +
                "Monthly Budget: Rs. " + budget + "\n" +
                "Total Spent So Far: Rs. " + totalSpent + "\n" +
                "Expenses:\n" + expenseList.toString() + "\n\n" +
                "Respond ONLY in this exact JSON format with no markdown, no backticks, just raw JSON:\n" +
                "{\n" +
                "  \"spendingAnalysis\": \"A 2-3 sentence analysis of their spending pattern\",\n" +
                "  \"savingTips\": \"2-3 practical, specific saving tips based on their categories\",\n" +
                "  \"budgetAdvice\": \"1-2 sentences of advice on whether they're on track with their budget\"\n" +
                "}";
    }

    private AISuggestion parseGeminiResponse(String rawResponse) throws Exception {
        JSONObject root = new JSONObject(rawResponse);
        JSONArray candidates = root.getJSONArray("candidates");
        JSONObject firstCandidate = candidates.getJSONObject(0);
        JSONObject content = firstCandidate.getJSONObject("content");
        JSONArray parts = content.getJSONArray("parts");
        String text = parts.getJSONObject(0).getString("text");

        text = text.replace("```json", "").replace("```", "").trim();

        JSONObject suggestionJson = new JSONObject(text);

        return new AISuggestion(
                suggestionJson.optString("spendingAnalysis", "No analysis available"),
                suggestionJson.optString("savingTips", "No tips available"),
                suggestionJson.optString("budgetAdvice", "No advice available")
        );
    }
}