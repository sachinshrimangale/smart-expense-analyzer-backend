package com.smartexpenseanalyzer.repository;

import com.smartexpenseanalyzer.entity.Expense;
import com.smartexpenseanalyzer.entity.PaymentMethod;
import com.smartexpenseanalyzer.entity.User;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseSpecification {

    public static Specification<Expense> hasUser(User user) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("user"),
                        user
                );
    }

    public static Specification<Expense> hasKeyword(
            String keyword) {

        return (root, query, criteriaBuilder) -> {

            if (keyword == null || keyword.isBlank()) {
                return null;
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(
                            root.get("description")
                    ),
                    "%" + keyword.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Expense> hasCategory(
            Long categoryId) {

        return (root, query, criteriaBuilder) -> {

            if (categoryId == null) {
                return null;
            }

            return criteriaBuilder.equal(
                    root.get("category").get("id"),
                    categoryId
            );
        };
    }

    public static Specification<Expense> hasPaymentMethod(
            PaymentMethod paymentMethod) {

        return (root, query, criteriaBuilder) -> {

            if (paymentMethod == null) {
                return null;
            }

            return criteriaBuilder.equal(
                    root.get("paymentMethod"),
                    paymentMethod
            );
        };
    }

    public static Specification<Expense> dateAfterOrEqual(
            LocalDate fromDate) {

        return (root, query, criteriaBuilder) -> {

            if (fromDate == null) {
                return null;
            }

            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get("expenseDate"),
                    fromDate
            );
        };
    }

    public static Specification<Expense> dateBeforeOrEqual(
            LocalDate toDate) {

        return (root, query, criteriaBuilder) -> {

            if (toDate == null) {
                return null;
            }

            return criteriaBuilder.lessThanOrEqualTo(
                    root.get("expenseDate"),
                    toDate
            );
        };
    }

    public static Specification<Expense> amountGreaterThanOrEqual(
            BigDecimal minAmount) {

        return (root, query, criteriaBuilder) -> {

            if (minAmount == null) {
                return null;
            }

            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get("amount"),
                    minAmount
            );
        };
    }

    public static Specification<Expense> amountLessThanOrEqual(
            BigDecimal maxAmount) {

        return (root, query, criteriaBuilder) -> {

            if (maxAmount == null) {
                return null;
            }

            return criteriaBuilder.lessThanOrEqualTo(
                    root.get("amount"),
                    maxAmount
            );
        };
    }
}