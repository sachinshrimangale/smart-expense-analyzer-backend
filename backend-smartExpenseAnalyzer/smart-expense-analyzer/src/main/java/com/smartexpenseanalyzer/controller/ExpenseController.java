package com.smartexpenseanalyzer.controller;

import com.smartexpenseanalyzer.dto.ExpenseRequest;
import com.smartexpenseanalyzer.dto.ExpenseResponse;
import com.smartexpenseanalyzer.dto.ExpenseSearchRequest;
import com.smartexpenseanalyzer.service.ExpenseService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(
            ExpenseService expenseService) {

        this.expenseService = expenseService;
    }

    // =========================================================
    // CREATE EXPENSE
    // =========================================================

    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(
            @Valid @RequestBody ExpenseRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                expenseService.createExpense(
                        request,
                        email
                )
        );
    }

    // =========================================================
    // GET ALL EXPENSES
    // =========================================================

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getAllExpenses(
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                expenseService.getAllExpenses(email)
        );
    }

    // =========================================================
    // SEARCH / FILTER / SORT
    // =========================================================

    @GetMapping("/search")
    public ResponseEntity<List<ExpenseResponse>> searchExpenses(
            ExpenseSearchRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                expenseService.searchExpenses(
                        request,
                        email
                )
        );
    }

    // =========================================================
    // GET EXPENSE BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getExpense(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                expenseService.getExpense(
                        id,
                        email
                )
        );
    }

    // =========================================================
    // UPDATE EXPENSE
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                expenseService.updateExpense(
                        id,
                        request,
                        email
                )
        );
    }

    // =========================================================
    // DELETE EXPENSE
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExpense(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        expenseService.deleteExpense(id, email);

        return ResponseEntity.ok(
                "Expense deleted successfully"
        );
    }
}