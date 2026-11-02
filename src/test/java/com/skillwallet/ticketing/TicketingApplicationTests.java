package com.skillwallet.ticketing;

import com.skillwallet.ticketing.dto.AiSuggestionDto;
import com.skillwallet.ticketing.dto.TicketRequestDto;
import com.skillwallet.ticketing.model.Category;
import com.skillwallet.ticketing.model.Priority;
import com.skillwallet.ticketing.model.Status;
import com.skillwallet.ticketing.model.Ticket;
import com.skillwallet.ticketing.service.AiSupportService;
import com.skillwallet.ticketing.service.TicketService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TicketingApplicationTests {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private AiSupportService aiSupportService;

    @Test
    @DisplayName("Application Context Loads Successfully")
    void contextLoads() {
        assertNotNull(ticketService, "TicketService must be injected");
        assertNotNull(aiSupportService, "AiSupportService must be injected");
    }

    @Test
    @DisplayName("Verify AI Classification for Billing Issue")
    void testAiClassificationForBilling() {
        AiSuggestionDto suggestion = aiSupportService.analyzeAndDraft(
                "Charged twice for my monthly subscription",
                "I see an extra charge on my credit card statement for invoice 1042. Please refund the duplicate payment.",
                "Samantha Ray"
        );

        assertNotNull(suggestion);
        assertEquals(Category.BILLING, suggestion.getSuggestedCategory());
        assertTrue(suggestion.getConfidenceScore() >= 0.70);
        assertNotNull(suggestion.getDraftedResponse());
        assertTrue(suggestion.getDraftedResponse().contains("Samantha Ray"));
    }

    @Test
    @DisplayName("Verify AI Classification for Critical Production Outage")
    void testAiClassificationForOutage() {
        AiSuggestionDto suggestion = aiSupportService.analyzeAndDraft(
                "System down - Production outage emergency",
                "Our production servers crashed with 500 error and customers cannot place orders. Urgent assistance required!",
                "John Doe"
        );

        assertNotNull(suggestion);
        assertEquals(Category.TECHNICAL, suggestion.getSuggestedCategory());
        assertEquals(Priority.CRITICAL, suggestion.getSuggestedPriority());
        assertTrue(suggestion.isUrgent());
    }

    @Test
    @DisplayName("Verify Ticket Creation and Search Pipeline")
    void testTicketCreationAndSearch() {
        TicketRequestDto request = new TicketRequestDto(
                "Integration Tester",
                "tester@skillwallet.io",
                "Integration Test Ticket for Automated Verification",
                "Detailed description verifying ticket creation and retrieval pipeline.",
                Category.TECHNICAL,
                Priority.MEDIUM
        );

        Ticket created = ticketService.createTicket(request);
        assertNotNull(created.getId());
        assertNotNull(created.getTicketNumber());
        assertEquals(Status.NEW, created.getStatus());

        List<Ticket> results = ticketService.getTickets(null, null, null, "Automated Verification", "createdAt", "desc");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(t -> t.getId().equals(created.getId())));
    }
}
