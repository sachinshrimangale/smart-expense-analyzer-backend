package com.smartexpenseanalyzer.dto;

import java.math.BigDecimal;

public class CategorySummaryResponse {

    private String categoryName;
    private BigDecimal totalAmount;

    public CategorySummaryResponse() {
    }

    public CategorySummaryResponse(
            String categoryName,
            BigDecimal totalAmount) {

        this.categoryName = categoryName;
        this.totalAmount = totalAmount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}