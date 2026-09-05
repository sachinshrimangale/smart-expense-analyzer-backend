package com.smartexpenseanalyzer.controller;

import com.smartexpenseanalyzer.dto.CategorySummaryResponse;
import com.smartexpenseanalyzer.dto.DashboardSummaryResponse;
import com.smartexpenseanalyzer.dto.PaymentSummaryResponse;
import com.smartexpenseanalyzer.service.DashboardService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
            DashboardService dashboardService) {

        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse>
    getSummary(Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                dashboardService.getSummary(email)
        );
    }

    @GetMapping("/category-summary")
    public ResponseEntity<List<CategorySummaryResponse>>
    getCategorySummary(Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                dashboardService.getCategorySummary(email)
        );
    }

    @GetMapping("/payment-summary")
    public ResponseEntity<List<PaymentSummaryResponse>>
    getPaymentSummary(Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                dashboardService.getPaymentSummary(email)
        );
    }
}