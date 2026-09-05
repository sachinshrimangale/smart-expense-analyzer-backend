package com.smartexpenseanalyzer.repository;

import com.smartexpenseanalyzer.entity.Expense;
import com.smartexpenseanalyzer.entity.User;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository
        extends JpaRepository<Expense, Long>,
                JpaSpecificationExecutor<Expense> {

    // =========================================================
    // GET ALL EXPENSES
    // =========================================================

    @Query("""
        SELECT e
        FROM Expense e
        JOIN FETCH e.category
        WHERE e.user = :user
        ORDER BY e.expenseDate DESC
    """)
    List<Expense> findByUserOrderByExpenseDateDesc(
            @Param("user") User user
    );

    // =========================================================
    // GET EXPENSE BY ID
    // =========================================================

    @Query("""
        SELECT e
        FROM Expense e
        JOIN FETCH e.category
        WHERE e.id = :id
        AND e.user = :user
    """)
    Optional<Expense> findByIdAndUser(
            @Param("id") Long id,
            @Param("user") User user
    );

    // =========================================================
    // SEARCH / FILTER / SORT
    // =========================================================

    @Override
    List<Expense> findAll(
            Specification<Expense> specification,
            Sort sort
    );

    // =========================================================
    // TOTAL SPENDING FOR PERIOD
    // =========================================================

    @Query("""
        SELECT COALESCE(SUM(e.amount), 0)
        FROM Expense e
        WHERE e.user = :user
        AND e.expenseDate BETWEEN :startDate AND :endDate
    """)
    BigDecimal getTotalSpentForPeriod(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}

//package com.smartexpenseanalyzer.repository;
//
//import com.smartexpenseanalyzer.entity.Expense;
//import com.smartexpenseanalyzer.entity.User;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
//import org.springframework.data.jpa.repository.Query;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//public interface ExpenseRepository
//extends JpaRepository<Expense, Long>,
//JpaSpecificationExecutor<Expense> {
//
//    List<Expense> findByUserOrderByExpenseDateDesc(User user);
//
//    Optional<Expense> findByIdAndUser(Long id, User user);
//
//    @Query("""
//        SELECT COALESCE(SUM(e.amount), 0)
//        FROM Expense e
//        WHERE e.user = :user
//        AND e.expenseDate BETWEEN :startDate AND :endDate
//    """)
//    BigDecimal getTotalSpentForPeriod(
//            User user,
//            LocalDate startDate,
//            LocalDate endDate);
//}