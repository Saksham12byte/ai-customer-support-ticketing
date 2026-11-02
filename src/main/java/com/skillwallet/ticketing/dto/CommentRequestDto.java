package com.skillwallet.ticketing.dto;

import jakarta.validation.constraints.NotBlank;

public class CommentRequestDto {

    @NotBlank(message = "Author name is required")
    private String authorName;

    private String authorRole = "AGENT"; // "CUSTOMER", "AGENT", "SYSTEM"

    @NotBlank(message = "Message cannot be empty")
    private String message;

    private boolean internalNote = false;

    public CommentRequestDto() {
    }

    public CommentRequestDto(String authorName, String authorRole, String message, boolean internalNote) {
        this.authorName = authorName;
        this.authorRole = authorRole;
        this.message = message;
        this.internalNote = internalNote;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorRole() {
        return authorRole;
    }

    public void setAuthorRole(String authorRole) {
        this.authorRole = authorRole;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isInternalNote() {
        return internalNote;
    }

    public void setInternalNote(boolean internalNote) {
        this.internalNote = internalNote;
    }
}
