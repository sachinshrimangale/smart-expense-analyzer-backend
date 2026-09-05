package com.smartexpenseanalyzer.repository;

import com.smartexpenseanalyzer.entity.Expense;
import com.smartexpenseanalyzer.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface DashboardRepository
        extends JpaRepository<Expense, Long> {

    @Query("""
        SELECT COALESCE(SUM(e.amount), 0)
        FROM Expense e
        WHERE e.user = :user
    """)
    BigDecimal getTotalExpenses(User user);

    @Query("""
        SELECT COALESCE(SUM(e.amount), 0)
        FROM Expense e
        WHERE e.user = :user
        AND e.expenseDate BETWEEN :startDate AND :endDate
    """)
    BigDecimal getExpensesBetween(
            User user,
            LocalDate startDate,
            LocalDate endDate);

    @Query("""
        SELECT e.category.name, SUM(e.amount)
        FROM Expense e
        WHERE e.user = :user
        GROUP BY e.category.name
        ORDER BY SUM(e.amount) DESC
    """)
    List<Object[]> getCategorySummary(User user);

    @Query("""
        SELECT e.paymentMethod, SUM(e.amount)
        FROM Expense e
        WHERE e.user = :user
        GROUP BY e.paymentMethod
        ORDER BY SUM(e.amount) DESC
    """)
    List<Object[]> getPaymentSummary(User user);
}