package com.smartexpenseanalyzer.dto;

import java.util.List;

public class AiAnalysisResponse {

    private String overview;
    private String topSpendingCategory;

    private List<String> savingRecommendations;
    private List<String> budgetAdvice;
    private List<String> riskAlerts;

    private String generatedBy;

    public AiAnalysisResponse() {
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getTopSpendingCategory() {
        return topSpendingCategory;
    }

    public void setTopSpendingCategory(String topSpendingCategory) {
        this.topSpendingCategory = topSpendingCategory;
    }

    public List<String> getSavingRecommendations() {
        return savingRecommendations;
    }

    public void setSavingRecommendations(List<String> savingRecommendations) {
        this.savingRecommendations = savingRecommendations;
    }

    public List<String> getBudgetAdvice() {
        return budgetAdvice;
    }

    public void setBudgetAdvice(List<String> budgetAdvice) {
        this.budgetAdvice = budgetAdvice;
    }

    public List<String> getRiskAlerts() {
        return riskAlerts;
    }

    public void setRiskAlerts(List<String> riskAlerts) {
        this.riskAlerts = riskAlerts;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
    }
}