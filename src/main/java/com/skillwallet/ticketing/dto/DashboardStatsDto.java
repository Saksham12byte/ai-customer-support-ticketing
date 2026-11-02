package com.skillwallet.ticketing.dto;

import java.util.Map;

public class DashboardStatsDto {

    private long totalTickets;
    private long openTickets;
    private long pendingTickets;
    private long resolvedTickets;
    private long closedTickets;
    private long criticalTickets;

    private Map<String, Long> statusBreakdown;
    private Map<String, Long> priorityBreakdown;
    private Map<String, Long> categoryBreakdown;

    public DashboardStatsDto() {
    }

    public DashboardStatsDto(long totalTickets, long openTickets, long pendingTickets,
                             long resolvedTickets, long closedTickets, long criticalTickets,
                             Map<String, Long> statusBreakdown, Map<String, Long> priorityBreakdown,
                             Map<String, Long> categoryBreakdown) {
        this.totalTickets = totalTickets;
        this.openTickets = openTickets;
        this.pendingTickets = pendingTickets;
        this.resolvedTickets = resolvedTickets;
        this.closedTickets = closedTickets;
        this.criticalTickets = criticalTickets;
        this.statusBreakdown = statusBreakdown;
        this.priorityBreakdown = priorityBreakdown;
        this.categoryBreakdown = categoryBreakdown;
    }

    public long getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(long totalTickets) {
        this.totalTickets = totalTickets;
    }

    public long getOpenTickets() {
        return openTickets;
    }

    public void setOpenTickets(long openTickets) {
        this.openTickets = openTickets;
    }

    public long getPendingTickets() {
        return pendingTickets;
    }

    public void setPendingTickets(long pendingTickets) {
        this.pendingTickets = pendingTickets;
    }

    public long getResolvedTickets() {
        return resolvedTickets;
    }

    public void setResolvedTickets(long resolvedTickets) {
        this.resolvedTickets = resolvedTickets;
    }

    public long getClosedTickets() {
        return closedTickets;
    }

    public void setClosedTickets(long closedTickets) {
        this.closedTickets = closedTickets;
    }

    public long getCriticalTickets() {
        return criticalTickets;
    }

    public void setCriticalTickets(long criticalTickets) {
        this.criticalTickets = criticalTickets;
    }

    public Map<String, Long> getStatusBreakdown() {
        return statusBreakdown;
    }

    public void setStatusBreakdown(Map<String, Long> statusBreakdown) {
        this.statusBreakdown = statusBreakdown;
    }

    public Map<String, Long> getPriorityBreakdown() {
        return priorityBreakdown;
    }

    public void setPriorityBreakdown(Map<String, Long> priorityBreakdown) {
        this.priorityBreakdown = priorityBreakdown;
    }

    public Map<String, Long> getCategoryBreakdown() {
        return categoryBreakdown;
    }

    public void setCategoryBreakdown(Map<String, Long> categoryBreakdown) {
        this.categoryBreakdown = categoryBreakdown;
    }
}
