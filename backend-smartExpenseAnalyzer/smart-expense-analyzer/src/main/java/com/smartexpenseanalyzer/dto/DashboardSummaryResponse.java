package com.smartexpenseanalyzer.dto;

import java.math.BigDecimal;

public class DashboardSummaryResponse {

    private BigDecimal totalExpenses;
    private BigDecimal monthlyExpenses;
    private BigDecimal weeklyExpenses;
    private BigDecimal todayExpenses;

    public DashboardSummaryResponse() {
    }

    public DashboardSummaryResponse(
            BigDecimal totalExpenses,
            BigDecimal monthlyExpenses,
            BigDecimal weeklyExpenses,
            BigDecimal todayExpenses) {

        this.totalExpenses = totalExpenses;
        this.monthlyExpenses = monthlyExpenses;
        this.weeklyExpenses = weeklyExpenses;
        this.todayExpenses = todayExpenses;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public BigDecimal getMonthlyExpenses() {
        return monthlyExpenses;
    }

    public BigDecimal getWeeklyExpenses() {
        return weeklyExpenses;
    }

    public BigDecimal getTodayExpenses() {
        return todayExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public void setMonthlyExpenses(BigDecimal monthlyExpenses) {
        this.monthlyExpenses = monthlyExpenses;
    }

    public void setWeeklyExpenses(BigDecimal weeklyExpenses) {
        this.weeklyExpenses = weeklyExpenses;
    }

    public void setTodayExpenses(BigDecimal todayExpenses) {
        this.todayExpenses = todayExpenses;
    }
}