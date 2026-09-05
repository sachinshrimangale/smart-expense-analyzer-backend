package com.smartexpenseanalyzer.controller;

import com.smartexpenseanalyzer.dto.AiAnalysisResponse;
import com.smartexpenseanalyzer.service.AiService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:5173")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<AiAnalysisResponse> analyzeSpending(
            Authentication authentication) {

        String email = authentication.getName();

        AiAnalysisResponse response =
                aiService.analyzeSpending(email);

        return ResponseEntity.ok(response);
    }
}