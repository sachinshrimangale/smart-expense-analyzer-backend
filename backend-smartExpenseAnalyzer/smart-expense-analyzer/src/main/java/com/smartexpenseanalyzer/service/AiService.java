package com.smartexpenseanalyzer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartexpenseanalyzer.dto.AiAnalysisResponse;
import com.smartexpenseanalyzer.dto.BudgetResponse;
import com.smartexpenseanalyzer.dto.CategorySummaryResponse;
import com.smartexpenseanalyzer.dto.DashboardSummaryResponse;
import com.smartexpenseanalyzer.dto.PaymentSummaryResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    private final RestClient restClient;
    private final DashboardService dashboardService;
    private final BudgetService budgetService;
    private final ObjectMapper objectMapper;

    @Value("${ai.api-url:}")
    private String apiUrl;

    @Value("${ai.api-key:}")
    private String apiKey;

    @Value("${ai.model:}")
    private String model;

    public AiService(
            RestClient restClient,
            DashboardService dashboardService,
            BudgetService budgetService,
            ObjectMapper objectMapper) {

        this.restClient = restClient;
        this.dashboardService = dashboardService;
        this.budgetService = budgetService;
        this.objectMapper = objectMapper;
    }

    public AiAnalysisResponse analyzeSpending(String email) {

        DashboardSummaryResponse summary =
                dashboardService.getSummary(email);

        List<CategorySummaryResponse> categories =
                dashboardService.getCategorySummary(email);

        List<PaymentSummaryResponse> payments =
                dashboardService.getPaymentSummary(email);

        List<BudgetResponse> budgets =
                budgetService.getBudgets(email);

        if (apiUrl == null ||
                apiUrl.isBlank() ||
                apiKey == null ||
                apiKey.isBlank()) {

            return createFallbackResponse(
                    summary,
                    categories,
                    payments,
                    budgets
            );
        }

        try {

            return callAiApi(
                    summary,
                    categories,
                    payments,
                    budgets
            );

        } catch (Exception e) {

            return createFallbackResponse(
                    summary,
                    categories,
                    payments,
                    budgets
            );
        }
    }

    private AiAnalysisResponse callAiApi(
            DashboardSummaryResponse summary,
            List<CategorySummaryResponse> categories,
            List<PaymentSummaryResponse> payments,
            List<BudgetResponse> budgets) throws Exception {

        String prompt = buildPrompt(
                summary,
                categories,
                payments,
                budgets
        );

        String systemMessage = """
                You are a personal financial analysis assistant.

                Analyze the user's expense data.

                Give practical and realistic financial advice.

                Return ONLY valid JSON.

                Do not use markdown.
                Do not use code fences.

                The JSON must have exactly these fields:

                {
                  "overview": "short overview",
                  "topSpendingCategory": "category name",
                  "savingRecommendations": [
                    "recommendation 1",
                    "recommendation 2",
                    "recommendation 3"
                  ],
                  "budgetAdvice": [
                    "budget advice 1",
                    "budget advice 2"
                  ],
                  "riskAlerts": [
                    "risk alert 1"
                  ]
                }
                """;

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of(
                                "role",
                                "system",
                                "content",
                                systemMessage
                        ),
                        Map.of(
                                "role",
                                "user",
                                "content",
                                prompt
                        )
                ),
                "temperature", 0.3
        );

        String response = restClient
                .post()
                .uri(apiUrl)
                .header(
                        "Authorization",
                        "Bearer " + apiKey
                )
                .header(
                        "Content-Type",
                        "application/json"
                )
                .body(requestBody)
                .retrieve()
                .body(String.class);

        JsonNode root =
                objectMapper.readTree(response);

        String content =
                root.path("choices")
                        .path(0)
                        .path("message")
                        .path("content")
                        .asText();

        return parseAiResponse(content);
    }

    private String buildPrompt(
            DashboardSummaryResponse summary,
            List<CategorySummaryResponse> categories,
            List<PaymentSummaryResponse> payments,
            List<BudgetResponse> budgets) {

        return """
                Analyze the following financial information.

                Total spending:
                %s

                Monthly spending:
                %s

                Weekly spending:
                %s

                Today's spending:
                %s

                Category spending:
                %s

                Payment method spending:
                %s

                Budgets:
                %s

                Provide useful financial insights.
                """.formatted(
                summary.getTotalExpenses(),
                summary.getMonthlyExpenses(),
                summary.getWeeklyExpenses(),
                summary.getTodayExpenses(),
                categories,
                payments,
                budgets
        );
    }

    private AiAnalysisResponse parseAiResponse(
            String content) throws Exception {

        // Remove accidental markdown code fences.
        content = content
                .replace("```json", "")
                .replace("```", "")
                .trim();

        JsonNode json =
                objectMapper.readTree(content);

        AiAnalysisResponse response =
                new AiAnalysisResponse();

        response.setOverview(
                json.path("overview").asText(
                        "No overview available."
                )
        );

        response.setTopSpendingCategory(
                json.path("topSpendingCategory").asText(
                        "Not available"
                )
        );

        response.setSavingRecommendations(
                readStringArray(
                        json.path("savingRecommendations")
                )
        );

        response.setBudgetAdvice(
                readStringArray(
                        json.path("budgetAdvice")
                )
        );

        response.setRiskAlerts(
                readStringArray(
                        json.path("riskAlerts")
                )
        );

        response.setGeneratedBy(
                "AI"
        );

        return response;
    }

    private List<String> readStringArray(
            JsonNode node) {

        List<String> result =
                new ArrayList<>();

        if (node.isArray()) {

            for (JsonNode item : node) {

                if (!item.asText().isBlank()) {
                    result.add(item.asText());
                }
            }
        }

        return result;
    }

    private AiAnalysisResponse createFallbackResponse(
            DashboardSummaryResponse summary,
            List<CategorySummaryResponse> categories,
            List<PaymentSummaryResponse> payments,
            List<BudgetResponse> budgets) {

        AiAnalysisResponse response =
                new AiAnalysisResponse();

        response.setGeneratedBy(
                "Smart Expense Analyzer"
        );

        response.setOverview(
                "Your current total spending is ₹"
                        + summary.getTotalExpenses()
                        + ". Review your highest spending categories "
                        + "and active budgets regularly to improve your "
                        + "financial planning."
        );

        if (categories != null &&
                !categories.isEmpty()) {

            response.setTopSpendingCategory(
                    categories.get(0).getCategoryName()
            );
        } else {

            response.setTopSpendingCategory(
                    "No spending data"
            );
        }

        List<String> saving =
                new ArrayList<>();

        saving.add(
                "Review your highest spending category every week."
        );

        saving.add(
                "Set realistic budgets for your major expenses."
        );

        saving.add(
                "Avoid unnecessary purchases when spending approaches your budget limit."
        );

        response.setSavingRecommendations(
                saving
        );

        List<String> budget =
                new ArrayList<>();

        if (budgets == null ||
                budgets.isEmpty()) {

            budget.add(
                    "Create a budget to start tracking your spending."
            );

        } else {

            budgets.forEach(b -> {

                budget.add(
                        "Budget from "
                                + b.getStartDate()
                                + " to "
                                + b.getEndDate()
                                + " is currently "
                                + b.getStatus()
                                + "."
                );

            });
        }

        response.setBudgetAdvice(
                budget
        );

        List<String> risks =
                new ArrayList<>();

        if (summary.getMonthlyExpenses()
                .compareTo(summary.getTotalExpenses()) > 0) {

            risks.add(
                    "Review your monthly spending carefully."
            );
        }

        if (risks.isEmpty()) {

            risks.add(
                    "No major spending risk was detected from the available data."
            );
        }

        response.setRiskAlerts(
                risks
        );

        return response;
    }
}

//package com.smartexpenseanalyzer.service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.smartexpenseanalyzer.dto.AiAnalysisResponse;
//import com.smartexpenseanalyzer.dto.BudgetResponse;
//import com.smartexpenseanalyzer.dto.CategorySummaryResponse;
//import com.smartexpenseanalyzer.dto.DashboardSummaryResponse;
//import com.smartexpenseanalyzer.dto.PaymentSummaryResponse;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestClient;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class AiService {
//
//    private final RestClient restClient;
//    private final DashboardService dashboardService;
//    private final BudgetService budgetService;
//    private final ObjectMapper objectMapper;
//
//    @Value("${ai.api-url:}")
//    private String apiUrl;
//
//    @Value("${ai.api-key:}")
//    private String apiKey;
//
//    @Value("${ai.model:}")
//    private String model;
//
//    public AiService(
//            RestClient restClient,
//            DashboardService dashboardService,
//            BudgetService budgetService,
//            ObjectMapper objectMapper) {
//
//        this.restClient = restClient;
//        this.dashboardService = dashboardService;
//        this.budgetService = budgetService;
//        this.objectMapper = objectMapper;
//    }
//
//    public AiAnalysisResponse analyzeSpending(String email) {
//
//        DashboardSummaryResponse summary =
//                dashboardService.getSummary(email);
//
//        List<CategorySummaryResponse> categories =
//                dashboardService.getCategorySummary(email);
//
//        List<PaymentSummaryResponse> payments =
//                dashboardService.getPaymentSummary(email);
//
//        List<BudgetResponse> budgets =
//                budgetService.getBudgets(email);
//
//        if (apiUrl == null ||
//                apiUrl.isBlank() ||
//                apiKey == null ||
//                apiKey.isBlank()) {
//
//            return createFallbackResponse(
//                    summary,
//                    categories,
//                    payments,
//                    budgets
//            );
//        }
//
//        try {
//
//            return callAiApi(
//                    summary,
//                    categories,
//                    payments,
//                    budgets
//            );
//
//        } catch (Exception e) {
//
//            return createFallbackResponse(
//                    summary,
//                    categories,
//                    payments,
//                    budgets
//            );
//        }
//    }
//
//    private AiAnalysisResponse callAiApi(
//            DashboardSummaryResponse summary,
//            List<CategorySummaryResponse> categories,
//            List<PaymentSummaryResponse> payments,
//            List<BudgetResponse> budgets) throws Exception {
//
//        String prompt = buildPrompt(
//                summary,
//                categories,
//                payments,
//                budgets
//        );
//
//        String systemMessage = """
//                You are a personal financial analysis assistant.
//
//                Analyze the user's expense data.
//
//                Give practical and realistic financial advice.
//
//                Return ONLY valid JSON.
//
//                Do not use markdown.
//                Do not use code fences.
//
//                The JSON must have exactly these fields:
//
//                {
//                  "overview": "short overview",
//                  "topSpendingCategory": "category name",
//                  "savingRecommendations": [
//                    "recommendation 1",
//                    "recommendation 2",
//                    "recommendation 3"
//                  ],
//                  "budgetAdvice": [
//                    "budget advice 1",
//                    "budget advice 2"
//                  ],
//                  "riskAlerts": [
//                    "risk alert 1"
//                  ]
//                }
//                """;
//
//        Map<String, Object> requestBody = Map.of(
//                "model", model,
//                "messages", List.of(
//                        Map.of(
//                                "role",
//                                "system",
//                                "content",
//                                systemMessage
//                        ),
//                        Map.of(
//                                "role",
//                                "user",
//                                "content",
//                                prompt
//                        )
//                ),
//                "temperature", 0.3
//        );
//
//        String response = restClient
//                .post()
//                .uri(apiUrl)
//                .header(
//                        "Authorization",
//                        "Bearer " + apiKey
//                )
//                .header(
//                        "Content-Type",
//                        "application/json"
//                )
//                .body(requestBody)
//                .retrieve()
//                .body(String.class);
//
//        JsonNode root =
//                objectMapper.readTree(response);
//
//        String content =
//                root.path("choices")
//                        .path(0)
//                        .path("message")
//                        .path("content")
//                        .asText();
//
//        return parseAiResponse(content);
//    }
//
//    private String buildPrompt(
//            DashboardSummaryResponse summary,
//            List<CategorySummaryResponse> categories,
//            List<PaymentSummaryResponse> payments,
//            List<BudgetResponse> budgets) {
//
//        return """
//                Analyze the following financial information.
//
//                Total spending:
//                %s
//
//                Monthly spending:
//                %s
//
//                Weekly spending:
//                %s
//
//                Today's spending:
//                %s
//
//                Category spending:
//                %s
//
//                Payment method spending:
//                %s
//
//                Budgets:
//                %s
//
//                Provide useful financial insights.
//                """.formatted(
//                summary.getTotalExpenses(),
//                summary.getMonthlyExpenses(),
//                summary.getWeeklyExpenses(),
//                summary.getTodayExpenses(),
//                categories,
//                payments,
//                budgets
//        );
//    }
//
//    private AiAnalysisResponse parseAiResponse(
//            String content) throws Exception {
//
//        // Remove accidental markdown code fences.
//        content = content
//                .replace("```json", "")
//                .replace("```", "")
//                .trim();
//
//        JsonNode json =
//                objectMapper.readTree(content);
//
//        AiAnalysisResponse response =
//                new AiAnalysisResponse();
//
//        response.setOverview(
//                json.path("overview").asText(
//                        "No overview available."
//                )
//        );
//
//        response.setTopSpendingCategory(
//                json.path("topSpendingCategory").asText(
//                        "Not available"
//                )
//        );
//
//        response.setSavingRecommendations(
//                readStringArray(
//                        json.path("savingRecommendations")
//                )
//        );
//
//        response.setBudgetAdvice(
//                readStringArray(
//                        json.path("budgetAdvice")
//                )
//        );
//
//        response.setRiskAlerts(
//                readStringArray(
//                        json.path("riskAlerts")
//                )
//        );
//
//        response.setGeneratedBy(
//                "AI"
//        );
//
//        return response;
//    }
//
//    private List<String> readStringArray(
//            JsonNode node) {
//
//        List<String> result =
//                new ArrayList<>();
//
//        if (node.isArray()) {
//
//            for (JsonNode item : node) {
//
//                if (!item.asText().isBlank()) {
//                    result.add(item.asText());
//                }
//            }
//        }
//
//        return result;
//    }
//
//    private AiAnalysisResponse createFallbackResponse(
//            DashboardSummaryResponse summary,
//            List<CategorySummaryResponse> categories,
//            List<PaymentSummaryResponse> payments,
//            List<BudgetResponse> budgets) {
//
//        AiAnalysisResponse response =
//                new AiAnalysisResponse();
//
//        response.setGeneratedBy(
//                "Smart Expense Analyzer"
//        );
//
//        response.setOverview(
//                "Your current total spending is ₹"
//                        + summary.getTotalExpenses()
//                        + ". Review your highest spending categories "
//                        + "and active budgets regularly to improve your "
//                        + "financial planning."
//        );
//
//        if (categories != null &&
//                !categories.isEmpty()) {
//
//            response.setTopSpendingCategory(
//                    categories.get(0).getCategoryName()
//            );
//        } else {
//
//            response.setTopSpendingCategory(
//                    "No spending data"
//            );
//        }
//
//        List<String> saving =
//                new ArrayList<>();
//
//        saving.add(
//                "Review your highest spending category every week."
//        );
//
//        saving.add(
//                "Set realistic budgets for your major expenses."
//        );
//
//        saving.add(
//                "Avoid unnecessary purchases when spending approaches your budget limit."
//        );
//
//        response.setSavingRecommendations(
//                saving
//        );
//
//        List<String> budget =
//                new ArrayList<>();
//
//        if (budgets == null ||
//                budgets.isEmpty()) {
//
//            budget.add(
//                    "Create a budget to start tracking your spending."
//            );
//
//        } else {
//
//            budgets.forEach(budget -> {
//
//                budget.add(
//                        "Budget from "
//                                + budget.getStartDate()
//                                + " to "
//                                + budget.getEndDate()
//                                + " is currently "
//                                + budget.getStatus()
//                                + "."
//                );
//
//            });
//        }
//
//        response.setBudgetAdvice(
//                budget
//        );
//
//        List<String> risks =
//                new ArrayList<>();
//
//        if (summary.getMonthlyExpenses()
//                .compareTo(summary.getTotalExpenses()) > 0) {
//
//            risks.add(
//                    "Review your monthly spending carefully."
//            );
//        }
//
//        if (risks.isEmpty()) {
//
//            risks.add(
//                    "No major spending risk was detected from the available data."
//            );
//        }
//
//        response.setRiskAlerts(
//                risks
//        );
//
//        return response;
//    }
//}