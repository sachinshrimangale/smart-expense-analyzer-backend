package com.smartexpenseanalyzer.dto;

import java.math.BigDecimal;

public class PaymentSummaryResponse {

    private String paymentMethod;
    private BigDecimal totalAmount;

    public PaymentSummaryResponse() {
    }

    public PaymentSummaryResponse(
            String paymentMethod,
            BigDecimal totalAmount) {

        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}