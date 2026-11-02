package com.skillwallet.ticketing.dto;

import jakarta.validation.constraints.NotBlank;

public class AiAnalyzeRequestDto {

    @NotBlank(message = "Subject cannot be empty for AI analysis")
    private String subject;

    @NotBlank(message = "Description cannot be empty for AI analysis")
    private String description;

    private String customerName;

    public AiAnalyzeRequestDto() {
    }

    public AiAnalyzeRequestDto(String subject, String description, String customerName) {
        this.subject = subject;
        this.description = description;
        this.customerName = customerName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
