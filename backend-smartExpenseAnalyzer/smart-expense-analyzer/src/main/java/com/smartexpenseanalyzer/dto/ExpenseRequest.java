package com.smartexpenseanalyzer.dto;

import com.smartexpenseanalyzer.entity.PaymentMethod;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(
        value = "0.01",
        message = "Amount must be greater than zero"
    )
    private BigDecimal amount;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Expense date is required")
    private LocalDate expenseDate;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    private String notes;

    public ExpenseRequest() {
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public String getNotes() {
        return notes;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

//package com.smartexpenseanalyzer.dto;
//
//import com.smartexpenseanalyzer.entity.PaymentMethod;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//
//public class ExpenseRequest {
//
//    private BigDecimal amount;
//    private String description;
//    private LocalDate expenseDate;
//    private Long categoryId;
//    private PaymentMethod paymentMethod;
//    private String notes;
//
//    public ExpenseRequest() {
//    }
//
//    public BigDecimal getAmount() {
//        return amount;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public LocalDate getExpenseDate() {
//        return expenseDate;
//    }
//
//    public Long getCategoryId() {
//        return categoryId;
//    }
//
//    public PaymentMethod getPaymentMethod() {
//        return paymentMethod;
//    }
//
//    public String getNotes() {
//        return notes;
//    }
//
//    public void setAmount(BigDecimal amount) {
//        this.amount = amount;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public void setExpenseDate(LocalDate expenseDate) {
//        this.expenseDate = expenseDate;
//    }
//
//    public void setCategoryId(Long categoryId) {
//        this.categoryId = categoryId;
//    }
//
//    public void setPaymentMethod(PaymentMethod paymentMethod) {
//        this.paymentMethod = paymentMethod;
//    }
//
//    public void setNotes(String notes) {
//        this.notes = notes;
//    }
//}