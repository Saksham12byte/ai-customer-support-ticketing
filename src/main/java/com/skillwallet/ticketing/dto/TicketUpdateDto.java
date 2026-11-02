package com.skillwallet.ticketing.dto;

import com.skillwallet.ticketing.model.Priority;
import com.skillwallet.ticketing.model.Status;

public class TicketUpdateDto {

    private Status status;
    private Priority priority;
    private String assignedAgent;
    private String resolutionNotes;

    public TicketUpdateDto() {
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getAssignedAgent() {
        return assignedAgent;
    }

    public void setAssignedAgent(String assignedAgent) {
        this.assignedAgent = assignedAgent;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }
}
