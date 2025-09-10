package com.eventux.backend.service;

import com.eventux.backend.dto.TicketCreateRequest;
import com.eventux.backend.model.Event;
import com.eventux.backend.model.Ticket;
import com.eventux.backend.model.User;
import com.eventux.backend.repository.EventRepository;
import com.eventux.backend.repository.TicketRepository;
import com.eventux.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;

@Service
public class TicketService {


    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public TicketService(TicketRepository ticketRepository,
                         UserRepository userRepository,
                         EventRepository eventRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    public List<Ticket> getAll() { return ticketRepository.findAll(); }

    public Optional<Ticket> getById(Long id) { return ticketRepository.findById(id); }

    public Ticket save(Ticket obj) { return ticketRepository.save(obj); }

    public void deleteById(Long id) { ticketRepository.deleteById(id); }

    // ------- New helpers -------
    public Ticket createFrom(TicketCreateRequest req) {
        if (req.getTicketTitle() == null || req.getTicketTitle().isBlank())
            throw new IllegalArgumentException("ticketTitle is required");
        if (req.getTicketContent() == null || req.getTicketContent().isBlank())
            throw new IllegalArgumentException("ticketContent is required");

        Ticket t = new Ticket();
        t.setTicketTitle(req.getTicketTitle());
        t.setTicketContent(req.getTicketContent());
        t.setTicketStatus("OPEN");

        // reporter: prefer email, fallback to id, else null is allowed (anonymous)
        User reporter = null;
        if (req.getReporterEmail() != null && !req.getReporterEmail().isBlank()) {
            reporter = userRepository.findByEmail(req.getReporterEmail()).orElse(null);
        } else if (req.getReporterId() != null) {
            reporter = userRepository.findById(req.getReporterId().intValue()).orElse(null);
        }
        t.setReporter(reporter);

        // event: null for app tickets
        if (req.getEventId() != null) {
            Event ev = eventRepository.findById(req.getEventId()).orElseThrow(() ->
                    new IllegalArgumentException("Event not found"));
            t.setEvent(ev);
        }

        return ticketRepository.save(t);
    }
    public Ticket setStatus(Long id, String status) {
        if (status == null || status.isBlank()) throw new IllegalArgumentException("status is required");
        Ticket t = ticketRepository.findById(id).orElseThrow(NoSuchElementException::new);
        t.setTicketStatus(status.trim().toUpperCase());
        return ticketRepository.save(t);
    }

    public Ticket acceptReply(Long id, String reply) {
        // placeholder to keep your current PATCH /reply working (no thread yet)
        Ticket t = ticketRepository.findById(id).orElseThrow(NoSuchElementException::new);
        return ticketRepository.save(t);
    }
    public List<Ticket> findByReporterEmail(String email) {
        return ticketRepository.findByReporter_EmailIgnoreCase(email);
    }
    public List<Ticket> findByEvent(Long eventId) {
        return ticketRepository.findByEventId(eventId);
    }

}
