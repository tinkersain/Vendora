package com.vendora.helpdesk.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vendora.helpdesk.entity.Ticket;
import com.vendora.helpdesk.entity.TicketCategory;
import com.vendora.helpdesk.entity.TicketStatus;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByOrderByCreatedAtDesc();

    List<Ticket> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Ticket> findByAssignedStaffIdOrderByCreatedAtDesc(Long assignedStaffId);

    List<Ticket> findByStatusOrderByCreatedAtDesc(TicketStatus status);

    List<Ticket> findByCategoryOrderByCreatedAtDesc(TicketCategory category);

    Optional<Ticket> findByTicketNumber(String ticketNumber);
}
