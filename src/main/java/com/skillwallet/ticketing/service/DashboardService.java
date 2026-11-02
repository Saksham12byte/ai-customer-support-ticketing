package com.skillwallet.ticketing.service;

import com.skillwallet.ticketing.dto.DashboardStatsDto;
import com.skillwallet.ticketing.model.Category;
import com.skillwallet.ticketing.model.Priority;
import com.skillwallet.ticketing.model.Status;
import com.skillwallet.ticketing.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final TicketRepository ticketRepository;

    public DashboardService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public DashboardStatsDto getDashboardStatistics() {
        long total = ticketRepository.count();

        Map<String, Long> statusBreakdown = new LinkedHashMap<>();
        for (Status s : Status.values()) {
            statusBreakdown.put(s.name(), ticketRepository.countByStatus(s));
        }

        Map<String, Long> priorityBreakdown = new LinkedHashMap<>();
        for (Priority p : Priority.values()) {
            priorityBreakdown.put(p.name(), ticketRepository.countByPriority(p));
        }

        Map<String, Long> categoryBreakdown = new LinkedHashMap<>();
        for (Category c : Category.values()) {
            categoryBreakdown.put(c.name(), ticketRepository.countByCategory(c));
        }

        long openCount = statusBreakdown.getOrDefault(Status.NEW.name(), 0L)
                + statusBreakdown.getOrDefault(Status.IN_PROGRESS.name(), 0L);
        long pendingCount = statusBreakdown.getOrDefault(Status.WAITING_FOR_CUSTOMER.name(), 0L);
        long resolvedCount = statusBreakdown.getOrDefault(Status.RESOLVED.name(), 0L);
        long closedCount = statusBreakdown.getOrDefault(Status.CLOSED.name(), 0L);
        long criticalCount = priorityBreakdown.getOrDefault(Priority.CRITICAL.name(), 0L);

        return new DashboardStatsDto(
                total,
                openCount,
                pendingCount,
                resolvedCount,
                closedCount,
                criticalCount,
                statusBreakdown,
                priorityBreakdown,
                categoryBreakdown
        );
    }
}
