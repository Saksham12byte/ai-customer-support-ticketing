package com.skillwallet.ticketing.model;

public enum Priority {
    LOW("Low", 1, "priority-low"),
    MEDIUM("Medium", 2, "priority-medium"),
    HIGH("High", 3, "priority-high"),
    CRITICAL("Critical", 4, "priority-critical");

    private final String displayName;
    private final int level;
    private final String badgeClass;

    Priority(String displayName, int level, String badgeClass) {
        this.displayName = displayName;
        this.level = level;
        this.badgeClass = badgeClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}
