package com.vendora.helpdesk.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vendora.helpdesk.entity.TicketComment;

@Repository
public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {

    List<TicketComment> findByTicketIdOrderByCreatedAtAsc(Long ticketId);

    List<TicketComment> findByTicketIdAndIsInternalNoteFalseOrderByCreatedAtAsc(Long ticketId);

    int countByTicketId(Long ticketId);
}
