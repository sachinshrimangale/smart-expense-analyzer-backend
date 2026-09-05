package com.smartexpenseanalyzer.repository;

import com.smartexpenseanalyzer.entity.Budget;
import com.smartexpenseanalyzer.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository
        extends JpaRepository<Budget, Long> {

    List<Budget> findByUserOrderByStartDateDesc(User user);

    Optional<Budget> findByIdAndUser(Long id, User user);
}