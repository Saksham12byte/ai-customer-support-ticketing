package com.skillwallet.ticketing.model;

public enum Status {
    NEW("New", "badge-new"),
    IN_PROGRESS("In Progress", "badge-in-progress"),
    WAITING_FOR_CUSTOMER("Waiting for Customer", "badge-waiting"),
    RESOLVED("Resolved", "badge-resolved"),
    CLOSED("Closed", "badge-closed");

    private final String displayName;
    private final String badgeClass;

    Status(String displayName, String badgeClass) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}
