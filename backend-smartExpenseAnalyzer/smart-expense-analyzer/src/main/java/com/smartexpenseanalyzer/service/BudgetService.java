package com.smartexpenseanalyzer.service;

import com.smartexpenseanalyzer.dto.BudgetRequest;
import com.smartexpenseanalyzer.dto.BudgetResponse;
import com.smartexpenseanalyzer.entity.Budget;
import com.smartexpenseanalyzer.entity.User;
import com.smartexpenseanalyzer.repository.BudgetRepository;
import com.smartexpenseanalyzer.repository.ExpenseRepository;
import com.smartexpenseanalyzer.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public BudgetService(
            BudgetRepository budgetRepository,
            ExpenseRepository expenseRepository,
            UserRepository userRepository) {

        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    public BudgetResponse createBudget(
            BudgetRequest request,
            String email) {

        validateDates(
                request.getStartDate(),
                request.getEndDate()
        );

        User user = getUser(email);

        Budget budget = new Budget();

        budget.setAmount(request.getAmount());
        budget.setStartDate(request.getStartDate());
        budget.setEndDate(request.getEndDate());
        budget.setUser(user);
        budget.setCreatedAt(LocalDateTime.now());
        budget.setUpdatedAt(LocalDateTime.now());

        Budget savedBudget =
                budgetRepository.save(budget);

        return buildResponse(savedBudget);
    }

    public List<BudgetResponse> getBudgets(
            String email) {

        User user = getUser(email);

        List<Budget> budgets =
                budgetRepository
                        .findByUserOrderByStartDateDesc(user);

        List<BudgetResponse> responses =
                new ArrayList<>();

        for (Budget budget : budgets) {
            responses.add(buildResponse(budget));
        }

        return responses;
    }

    public BudgetResponse getBudget(
            Long id,
            String email) {

        User user = getUser(email);

        Budget budget =
                budgetRepository
                        .findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Budget not found"));

        return buildResponse(budget);
    }

    public BudgetResponse updateBudget(
            Long id,
            BudgetRequest request,
            String email) {

        validateDates(
                request.getStartDate(),
                request.getEndDate()
        );

        User user = getUser(email);

        Budget budget =
                budgetRepository
                        .findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Budget not found"));

        budget.setAmount(request.getAmount());
        budget.setStartDate(request.getStartDate());
        budget.setEndDate(request.getEndDate());
        budget.setUpdatedAt(LocalDateTime.now());

        Budget updatedBudget =
                budgetRepository.save(budget);

        return buildResponse(updatedBudget);
    }

    public void deleteBudget(
            Long id,
            String email) {

        User user = getUser(email);

        Budget budget =
                budgetRepository
                        .findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Budget not found"));

        budgetRepository.delete(budget);
    }

    private BudgetResponse buildResponse(
            Budget budget) {

        BigDecimal spent =
                expenseRepository.getTotalSpentForPeriod(
                        budget.getUser(),
                        budget.getStartDate(),
                        budget.getEndDate()
                );

        if (spent == null) {
            spent = BigDecimal.ZERO;
        }

        BigDecimal remaining =
                budget.getAmount().subtract(spent);

        double percentageUsed =
                spent
                        .divide(
                                budget.getAmount(),
                                4,
                                RoundingMode.HALF_UP
                        )
                        .doubleValue() * 100;

        String status;

        if (percentageUsed >= 100) {
            status = "EXCEEDED";
        } else if (percentageUsed >= 80) {
            status = "WARNING";
        } else {
            status = "ON_TRACK";
        }

        return new BudgetResponse(
                budget.getId(),
                budget.getAmount(),
                spent,
                remaining,
                percentageUsed,
                status,
                budget.getStartDate(),
                budget.getEndDate()
        );
    }

    private void validateDates(
            LocalDate startDate,
            LocalDate endDate) {

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "End date cannot be before start date"
            );
        }
    }

    private User getUser(String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"));
    }
}