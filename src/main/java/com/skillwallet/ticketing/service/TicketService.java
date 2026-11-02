package com.skillwallet.ticketing.service;

import com.skillwallet.ticketing.dto.AiSuggestionDto;
import com.skillwallet.ticketing.dto.CommentRequestDto;
import com.skillwallet.ticketing.dto.TicketRequestDto;
import com.skillwallet.ticketing.dto.TicketUpdateDto;
import com.skillwallet.ticketing.model.*;
import com.skillwallet.ticketing.repository.CommentRepository;
import com.skillwallet.ticketing.repository.TicketRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CommentRepository commentRepository;
    private final AiSupportService aiSupportService;

    public TicketService(TicketRepository ticketRepository,
                         CommentRepository commentRepository,
                         AiSupportService aiSupportService) {
        this.ticketRepository = ticketRepository;
        this.commentRepository = commentRepository;
        this.aiSupportService = aiSupportService;
    }

    @Transactional(readOnly = true)
    public List<Ticket> getTickets(Status status, Priority priority, Category category,
                                   String search, String sortBy, String sortDir) {
        String cleanSortBy = (sortBy == null || sortBy.trim().isEmpty()) ? "createdAt" : sortBy;
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, cleanSortBy);

        return ticketRepository.searchTickets(status, priority, category, search, sort);
    }

    @Transactional(readOnly = true)
    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ticket not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Ticket getTicketByNumber(String ticketNumber) {
        return ticketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new NoSuchElementException("Ticket not found with number: " + ticketNumber));
    }

    public Ticket createTicket(TicketRequestDto dto) {
        Ticket ticket = new Ticket(
                dto.getCustomerName(),
                dto.getCustomerEmail(),
                dto.getSubject(),
                dto.getDescription(),
                dto.getCategory(),
                dto.getPriority()
        );

        // Run AI analysis to enrich ticket metadata
        try {
            AiSuggestionDto aiSuggestion = aiSupportService.analyzeAndDraft(
                    dto.getSubject(),
                    dto.getDescription(),
                    dto.getCustomerName()
            );
            ticket.setAiConfidence(aiSuggestion.getConfidenceScore());
            ticket.setAiCategorySuggestion(aiSuggestion.getSuggestedCategory().name());
            ticket.setAiPrioritySuggestion(aiSuggestion.getSuggestedPriority().name());
        } catch (Exception ex) {
            ticket.setAiConfidence(0.85);
            ticket.setAiCategorySuggestion(dto.getCategory().name());
            ticket.setAiPrioritySuggestion(dto.getPriority().name());
        }

        Ticket saved = ticketRepository.save(ticket);

        // Add initial system comment
        Comment initialComment = new Comment(
                saved,
                "System",
                "SYSTEM",
                "Ticket #" + saved.getTicketNumber() + " created by " + saved.getCustomerName() + " (" + saved.getCustomerEmail() + "). Initial status: NEW.",
                false
        );
        commentRepository.save(initialComment);

        return saved;
    }

    public Ticket updateTicket(Long id, TicketUpdateDto dto) {
        Ticket ticket = getTicketById(id);
        StringBuilder auditLog = new StringBuilder("Ticket updated:");

        if (dto.getStatus() != null && dto.getStatus() != ticket.getStatus()) {
            auditLog.append(" Status changed from ").append(ticket.getStatus().getDisplayName())
                    .append(" to ").append(dto.getStatus().getDisplayName()).append(".");
            ticket.setStatus(dto.getStatus());
            if (dto.getStatus() == Status.RESOLVED || dto.getStatus() == Status.CLOSED) {
                ticket.setResolvedAt(LocalDateTime.now());
            }
        }

        if (dto.getPriority() != null && dto.getPriority() != ticket.getPriority()) {
            auditLog.append(" Priority adjusted to ").append(dto.getPriority().getDisplayName()).append(".");
            ticket.setPriority(dto.getPriority());
        }

        if (dto.getAssignedAgent() != null && !dto.getAssignedAgent().trim().isEmpty() &&
                !dto.getAssignedAgent().equals(ticket.getAssignedAgent())) {
            auditLog.append(" Assigned agent updated to ").append(dto.getAssignedAgent()).append(".");
            ticket.setAssignedAgent(dto.getAssignedAgent());
        }

        if (dto.getResolutionNotes() != null) {
            ticket.setResolutionNotes(dto.getResolutionNotes());
        }

        ticket.setUpdatedAt(LocalDateTime.now());
        Ticket updated = ticketRepository.save(ticket);

        // If changes were made, record an audit comment
        if (auditLog.length() > "Ticket updated:".length()) {
            Comment auditComment = new Comment(
                    updated,
                    dto.getAssignedAgent() != null && !dto.getAssignedAgent().equals("Unassigned") ? dto.getAssignedAgent() : "System",
                    "SYSTEM",
                    auditLog.toString(),
                    false
            );
            commentRepository.save(auditComment);
        }

        return updated;
    }

    public Comment addComment(Long ticketId, CommentRequestDto dto) {
        Ticket ticket = getTicketById(ticketId);
        Comment comment = new Comment(
                ticket,
                dto.getAuthorName(),
                dto.getAuthorRole() != null ? dto.getAuthorRole() : "AGENT",
                dto.getMessage(),
                dto.isInternalNote()
        );

        ticket.setUpdatedAt(LocalDateTime.now());
        ticketRepository.save(ticket);
        return commentRepository.save(comment);
    }

    public void deleteTicket(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new NoSuchElementException("Cannot delete. Ticket not found with id: " + id);
        }
        ticketRepository.deleteById(id);
    }
}
