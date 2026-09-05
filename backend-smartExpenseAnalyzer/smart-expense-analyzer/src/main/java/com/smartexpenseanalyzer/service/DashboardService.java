package com.smartexpenseanalyzer.service;

import com.smartexpenseanalyzer.dto.CategorySummaryResponse;
import com.smartexpenseanalyzer.dto.DashboardSummaryResponse;
import com.smartexpenseanalyzer.dto.PaymentSummaryResponse;
import com.smartexpenseanalyzer.entity.User;
import com.smartexpenseanalyzer.repository.DashboardRepository;
import com.smartexpenseanalyzer.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final UserRepository userRepository;

    public DashboardService(
            DashboardRepository dashboardRepository,
            UserRepository userRepository) {

        this.dashboardRepository = dashboardRepository;
        this.userRepository = userRepository;
    }

    public DashboardSummaryResponse getSummary(
            String email) {

        User user = getUser(email);

        LocalDate today = LocalDate.now();

        LocalDate monthStart =
                today.withDayOfMonth(1);

        LocalDate weekStart =
                today.with(DayOfWeek.MONDAY);

        BigDecimal total =
                dashboardRepository
                        .getTotalExpenses(user);

        BigDecimal monthly =
                dashboardRepository
                        .getExpensesBetween(
                                user,
                                monthStart,
                                today
                        );

        BigDecimal weekly =
                dashboardRepository
                        .getExpensesBetween(
                                user,
                                weekStart,
                                today
                        );

        BigDecimal todayExpenses =
                dashboardRepository
                        .getExpensesBetween(
                                user,
                                today,
                                today
                        );

        return new DashboardSummaryResponse(
                total,
                monthly,
                weekly,
                todayExpenses
        );
    }

    public List<CategorySummaryResponse>
    getCategorySummary(String email) {

        User user = getUser(email);

        List<Object[]> results =
                dashboardRepository
                        .getCategorySummary(user);

        List<CategorySummaryResponse> response =
                new ArrayList<>();

        for (Object[] result : results) {

            String categoryName =
                    (String) result[0];

            BigDecimal amount =
                    (BigDecimal) result[1];

            response.add(
                    new CategorySummaryResponse(
                            categoryName,
                            amount
                    )
            );
        }

        return response;
    }

    public List<PaymentSummaryResponse>
    getPaymentSummary(String email) {

        User user = getUser(email);

        List<Object[]> results =
                dashboardRepository
                        .getPaymentSummary(user);

        List<PaymentSummaryResponse> response =
                new ArrayList<>();

        for (Object[] result : results) {

            String paymentMethod =
                    result[0].toString();

            BigDecimal amount =
                    (BigDecimal) result[1];

            response.add(
                    new PaymentSummaryResponse(
                            paymentMethod,
                            amount
                    )
            );
        }

        return response;
    }

    private User getUser(String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"));
    }
}