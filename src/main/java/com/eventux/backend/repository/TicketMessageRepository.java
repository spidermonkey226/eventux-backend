package com.eventux.backend.repository;

import com.eventux.backend.model.TicketMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketMessageRepository extends JpaRepository<TicketMessage, Long> {
    @Query("""
           select m
           from TicketMessage m
           where m.ticket.TicketId = :ticketId
           order by m.createdAt asc
           """)
    List<TicketMessage> findAllByTicketIdOrderByCreatedAtAsc(@Param("ticketId") Long ticketId);
}
