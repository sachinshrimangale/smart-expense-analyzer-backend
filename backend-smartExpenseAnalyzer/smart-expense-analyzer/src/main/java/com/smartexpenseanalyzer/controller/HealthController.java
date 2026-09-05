package com.smartexpenseanalyzer.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public Map<String, String> health() {

        return Map.of(
                "status", "UP",
                "message", "Smart Expense Analyzer API is running"
        );
    }

    @GetMapping("/test")
    public Map<String, String> test() {

        return Map.of(
                "message",
                "You are authenticated"
        );
    }
}