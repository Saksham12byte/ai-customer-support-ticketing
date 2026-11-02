package com.skillwallet.ticketing.controller;

import com.skillwallet.ticketing.dto.AiAnalyzeRequestDto;
import com.skillwallet.ticketing.dto.AiSuggestionDto;
import com.skillwallet.ticketing.service.AiSupportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiController {

    private final AiSupportService aiSupportService;

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    public AiController(AiSupportService aiSupportService) {
        this.aiSupportService = aiSupportService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<AiSuggestionDto> analyzeTicket(@Valid @RequestBody AiAnalyzeRequestDto request) {
        AiSuggestionDto suggestion = aiSupportService.analyzeAndDraft(
                request.getSubject(),
                request.getDescription(),
                request.getCustomerName()
        );
        return ResponseEntity.ok(suggestion);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getAiStatus() {
        boolean hasKey = geminiApiKey != null && !geminiApiKey.trim().isEmpty() && !geminiApiKey.contains("your_api_key");
        return ResponseEntity.ok(Map.of(
                "activeMode", hasKey ? "GEMINI_LIVE" : "RULE_BASED_DEMO",
                "modeDescription", hasKey
                        ? "Google Gemini 1.5 Flash Live API connected."
                        : "High-Accuracy Rule-Based NLP Demo Engine active (offline & zero-latency).",
                "liveApiConfigured", hasKey
        ));
    }
}
