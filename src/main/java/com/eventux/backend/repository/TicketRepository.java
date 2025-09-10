package com.eventux.backend.repository;

import com.eventux.backend.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByReporter_EmailIgnoreCase(String email);
    @Query("select t from Ticket t where t.event is not null and t.event.eventID = :eventId")
    List<Ticket> findByEventId(@Param("eventId") Long eventId);
}