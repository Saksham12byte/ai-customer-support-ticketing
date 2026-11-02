package com.skillwallet.ticketing.repository;

import com.skillwallet.ticketing.model.Category;
import com.skillwallet.ticketing.model.Priority;
import com.skillwallet.ticketing.model.Status;
import com.skillwallet.ticketing.model.Ticket;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByTicketNumber(String ticketNumber);

    long countByStatus(Status status);

    long countByPriority(Priority priority);

    long countByCategory(Category category);

    @Query("SELECT t FROM Ticket t WHERE " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:category IS NULL OR t.category = :category) AND " +
           "(:search IS NULL OR :search = '' OR " +
           " LOWER(t.subject) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(t.customerName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(t.ticketNumber) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Ticket> searchTickets(
            @Param("status") Status status,
            @Param("priority") Priority priority,
            @Param("category") Category category,
            @Param("search") String search,
            Sort sort
    );
}
