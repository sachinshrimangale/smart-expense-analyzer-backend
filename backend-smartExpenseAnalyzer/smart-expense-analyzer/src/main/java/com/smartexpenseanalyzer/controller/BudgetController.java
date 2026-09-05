package com.smartexpenseanalyzer.controller;

import com.smartexpenseanalyzer.dto.BudgetRequest;
import com.smartexpenseanalyzer.dto.BudgetResponse;
import com.smartexpenseanalyzer.service.BudgetService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@CrossOrigin
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(
            BudgetService budgetService) {

        this.budgetService = budgetService;
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(
            @Valid @RequestBody BudgetRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                budgetService.createBudget(
                        request,
                        email
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getBudgets(
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                budgetService.getBudgets(email)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponse> getBudget(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                budgetService.getBudget(
                        id,
                        email
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> updateBudget(
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                budgetService.updateBudget(
                        id,
                        request,
                        email
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBudget(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        budgetService.deleteBudget(id, email);

        return ResponseEntity.ok(
                "Budget deleted successfully"
        );
    }
}