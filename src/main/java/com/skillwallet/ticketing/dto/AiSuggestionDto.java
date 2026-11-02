package com.skillwallet.ticketing.dto;

import com.skillwallet.ticketing.model.Category;
import com.skillwallet.ticketing.model.Priority;

public class AiSuggestionDto {

    private Category suggestedCategory;
    private Priority suggestedPriority;
    private double confidenceScore;
    private String reasoning;
    private String draftedResponse;
    private String mode; // "GEMINI_LIVE" or "RULE_BASED_DEMO"
    private boolean urgent;

    public AiSuggestionDto() {
    }

    public AiSuggestionDto(Category suggestedCategory, Priority suggestedPriority, double confidenceScore,
                           String reasoning, String draftedResponse, String mode, boolean urgent) {
        this.suggestedCategory = suggestedCategory;
        this.suggestedPriority = suggestedPriority;
        this.confidenceScore = confidenceScore;
        this.reasoning = reasoning;
        this.draftedResponse = draftedResponse;
        this.mode = mode;
        this.urgent = urgent;
    }

    public Category getSuggestedCategory() {
        return suggestedCategory;
    }

    public void setSuggestedCategory(Category suggestedCategory) {
        this.suggestedCategory = suggestedCategory;
    }

    public Priority getSuggestedPriority() {
        return suggestedPriority;
    }

    public void setSuggestedPriority(Priority suggestedPriority) {
        this.suggestedPriority = suggestedPriority;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getReasoning() {
        return reasoning;
    }

    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }

    public String getDraftedResponse() {
        return draftedResponse;
    }

    public void setDraftedResponse(String draftedResponse) {
        this.draftedResponse = draftedResponse;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }
}
