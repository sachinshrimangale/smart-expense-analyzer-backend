package com.smartexpenseanalyzer.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BudgetResponse {

    private Long id;
    private BigDecimal budgetAmount;
    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;
    private double percentageUsed;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;

    public BudgetResponse() {
    }

    public BudgetResponse(
            Long id,
            BigDecimal budgetAmount,
            BigDecimal spentAmount,
            BigDecimal remainingAmount,
            double percentageUsed,
            String status,
            LocalDate startDate,
            LocalDate endDate) {

        this.id = id;
        this.budgetAmount = budgetAmount;
        this.spentAmount = spentAmount;
        this.remainingAmount = remainingAmount;
        this.percentageUsed = percentageUsed;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getBudgetAmount() {
        return budgetAmount;
    }

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public double getPercentageUsed() {
        return percentageUsed;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setBudgetAmount(BigDecimal budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public void setSpentAmount(BigDecimal spentAmount) {
        this.spentAmount = spentAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public void setPercentageUsed(double percentageUsed) {
        this.percentageUsed = percentageUsed;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}