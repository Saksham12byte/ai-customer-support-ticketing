package com.skillwallet.ticketing;

import com.skillwallet.ticketing.model.*;
import com.skillwallet.ticketing.repository.CommentRepository;
import com.skillwallet.ticketing.repository.TicketRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class TicketingApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketingApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDemoData(TicketRepository ticketRepository, CommentRepository commentRepository) {
        return args -> {
            if (ticketRepository.count() > 0) {
                return;
            }

            System.out.println(">>> Seeding realistic demo tickets for SkillWallet Capstone evaluation...");

            // Ticket 1: Critical Production Issue
            Ticket t1 = new Ticket(
                    "David Miller",
                    "david.miller@fintechcloud.io",
                    "Production Payment Gateway returning 500 Internal Error",
                    "Our customers cannot checkout on our web store. Every charge request to /api/v1/charge is failing with HTTP 500. This is an immediate emergency affecting revenue.",
                    Category.TECHNICAL,
                    Priority.CRITICAL
            );
            t1.setTicketNumber("TCK-1001");
            t1.setStatus(Status.IN_PROGRESS);
            t1.setAssignedAgent("Alex Rivera");
            t1.setAiConfidence(0.96);
            t1.setAiCategorySuggestion("TECHNICAL");
            t1.setAiPrioritySuggestion("CRITICAL");
            ticketRepository.save(t1);

            commentRepository.save(new Comment(t1, "David Miller", "CUSTOMER", "We are losing customers every minute. Please escalate urgently.", false));
            commentRepository.save(new Comment(t1, "Alex Rivera", "AGENT", "On it! Tracing the root cause in the Stripe connection pool. Preliminary logs indicate thread exhaustion. Applying hotfix now.", false));

            // Ticket 2: Billing Double Charge
            Ticket t2 = new Ticket(
                    "Elena Rostova",
                    "elena.rostova@designworks.com",
                    "Double charge on annual Pro subscription invoice #INV-9281",
                    "I noticed two charges of $240 on my credit card on Nov 1st for the same annual subscription. Please check and process a refund for the duplicate transaction.",
                    Category.BILLING,
                    Priority.HIGH
            );
            t2.setTicketNumber("TCK-1002");
            t2.setStatus(Status.WAITING_FOR_CUSTOMER);
            t2.setAssignedAgent("Sarah Chen");
            t2.setAiConfidence(0.94);
            t2.setAiCategorySuggestion("BILLING");
            t2.setAiPrioritySuggestion("HIGH");
            ticketRepository.save(t2);

            commentRepository.save(new Comment(t2, "Sarah Chen", "AGENT", "Hi Elena, we identified the duplicate billing glitch. A refund of $240 has been queued. Could you please confirm if your card ending in 4118 is still active?", false));

            // Ticket 3: Account Access
            Ticket t3 = new Ticket(
                    "Marcus Vance",
                    "marcus.vance@vancemedia.org",
                    "Reset password link not received on corporate email domain",
                    "I have clicked forgot password 3 times in the last hour, but no reset email is arriving in my inbox or spam folder. My team is waiting on me to approve publishing drafts.",
                    Category.ACCOUNT_ACCESS,
                    Priority.HIGH
            );
            t3.setTicketNumber("TCK-1003");
            t3.setStatus(Status.NEW);
            t3.setAssignedAgent("Unassigned");
            t3.setAiConfidence(0.91);
            t3.setAiCategorySuggestion("ACCOUNT_ACCESS");
            t3.setAiPrioritySuggestion("HIGH");
            ticketRepository.save(t3);

            // Ticket 4: Feature Request
            Ticket t4 = new Ticket(
                    "Priya Sharma",
                    "priya.sharma@analyticsplus.in",
                    "Feature Request: Export monthly audit logs and metrics to CSV and PDF",
                    "It would be very helpful if we could schedule and download automated monthly compliance logs in CSV and PDF formats directly from the admin dashboard.",
                    Category.FEATURE_REQUEST,
                    Priority.LOW
            );
            t4.setTicketNumber("TCK-1004");
            t4.setStatus(Status.NEW);
            t4.setAssignedAgent("Unassigned");
            t4.setAiConfidence(0.88);
            t4.setAiCategorySuggestion("FEATURE_REQUEST");
            t4.setAiPrioritySuggestion("LOW");
            ticketRepository.save(t4);

            // Ticket 5: Technical Permission Issue
            Ticket t5 = new Ticket(
                    "Carlos Gomez",
                    "carlos.gomez@healthtech.es",
                    "Cannot invite secondary team members - 403 Forbidden error",
                    "Whenever an organization admin tries to send an invite to a new member, the UI displays '403 Forbidden: Organization member quota exceeded' even though we only have 3 active seats.",
                    Category.TECHNICAL,
                    Priority.MEDIUM
            );
            t5.setTicketNumber("TCK-1005");
            t5.setStatus(Status.IN_PROGRESS);
            t5.setAssignedAgent("Alex Rivera");
            t5.setAiConfidence(0.89);
            t5.setAiCategorySuggestion("TECHNICAL");
            t5.setAiPrioritySuggestion("MEDIUM");
            ticketRepository.save(t5);

            // Ticket 6: General Inquiry (Resolved)
            Ticket t6 = new Ticket(
                    "Amina Al-Mansoor",
                    "amina@logisticsdubai.ae",
                    "Inquiry regarding Enterprise SLA and guaranteed uptime commitments",
                    "Our procurement department requires written verification of your platform uptime SLA and data residency guarantees before signing the master service agreement.",
                    Category.GENERAL_INQUIRY,
                    Priority.LOW
            );
            t6.setTicketNumber("TCK-1006");
            t6.setStatus(Status.RESOLVED);
            t6.setAssignedAgent("Sarah Chen");
            t6.setResolutionNotes("Dispatched standard Enterprise SLA document detailing 99.95% uptime guarantee, multi-region failover, and EU/GCC data center compliance.");
            t6.setAiConfidence(0.85);
            t6.setAiCategorySuggestion("GENERAL_INQUIRY");
            t6.setAiPrioritySuggestion("LOW");
            t6.setResolvedAt(LocalDateTime.now().minusHours(4));
            ticketRepository.save(t6);

            commentRepository.save(new Comment(t6, "Sarah Chen", "AGENT", "Sent the legal SLA packet and compliance certifications. The customer confirmed receipt.", false));

            // Ticket 7: Technical Latency
            Ticket t7 = new Ticket(
                    "Liam O'Connor",
                    "liam@dublindevs.ie",
                    "Intermittent latency spike and API rate limit triggering prematurely",
                    "We are seeing latency spikes exceeding 3200ms on the /api/tickets endpoint and 429 Too Many Requests even though we are sending under 60 requests/minute.",
                    Category.TECHNICAL,
                    Priority.HIGH
            );
            t7.setTicketNumber("TCK-1007");
            t7.setStatus(Status.IN_PROGRESS);
            t7.setAssignedAgent("Alex Rivera");
            t7.setAiConfidence(0.92);
            t7.setAiCategorySuggestion("TECHNICAL");
            t7.setAiPrioritySuggestion("HIGH");
            ticketRepository.save(t7);

            // Ticket 8: Billing Tax ID (Closed)
            Ticket t8 = new Ticket(
                    "Klaus Schmidt",
                    "klaus@berlinfin.de",
                    "Update company EU VAT tax ID on past invoice #INV-8104",
                    "Please update our official VAT ID DE298371902 on invoice #INV-8104 so our accounting team can file for input tax deduction with the German tax authorities.",
                    Category.BILLING,
                    Priority.MEDIUM
            );
            t8.setTicketNumber("TCK-1008");
            t8.setStatus(Status.CLOSED);
            t8.setAssignedAgent("Sarah Chen");
            t8.setResolutionNotes("Updated company tax profile with VAT ID DE298371902 and reissued corrected PDF invoice.");
            t8.setAiConfidence(0.93);
            t8.setAiCategorySuggestion("BILLING");
            t8.setAiPrioritySuggestion("MEDIUM");
            t8.setResolvedAt(LocalDateTime.now().minusDays(1));
            ticketRepository.save(t8);

            System.out.println(">>> Demo tickets seeded successfully! Total tickets: " + ticketRepository.count());
        };
    }
}
