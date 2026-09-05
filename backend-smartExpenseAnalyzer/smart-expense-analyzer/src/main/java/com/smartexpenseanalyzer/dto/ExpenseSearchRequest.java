package com.smartexpenseanalyzer.dto;

import com.smartexpenseanalyzer.entity.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseSearchRequest {

    private String keyword;
    private Long categoryId;
    private PaymentMethod paymentMethod;
    private LocalDate fromDate;
    private LocalDate toDate;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private String sortBy;
    private String direction;

    public ExpenseSearchRequest() {
    }

    public String getKeyword() {
        return keyword;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public String getSortBy() {
        return sortBy;
    }

    public String getDirection() {
        return direction;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}