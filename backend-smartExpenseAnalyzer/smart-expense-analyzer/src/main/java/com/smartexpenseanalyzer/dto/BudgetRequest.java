package com.smartexpenseanalyzer.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BudgetRequest {

    @NotNull(message = "Budget amount is required")
    @DecimalMin(
            value = "0.01",
            message = "Budget amount must be greater than zero"
    )
    private BigDecimal amount;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    public BudgetRequest() {
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}