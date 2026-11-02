package com.skillwallet.ticketing.model;

public enum Category {
    TECHNICAL("Technical Support"),
    BILLING("Billing & Invoices"),
    ACCOUNT_ACCESS("Account & Login Access"),
    FEATURE_REQUEST("Feature Request"),
    GENERAL_INQUIRY("General Inquiry");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
