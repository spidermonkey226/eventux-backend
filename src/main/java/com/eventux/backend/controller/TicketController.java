package com.eventux.backend.controller;


import com.eventux.backend.dto.TicketCreateRequest;
import com.eventux.backend.dto.TicketDTO;
import com.eventux.backend.dto.TicketMessageDTO;
import com.eventux.backend.model.Ticket;
import com.eventux.backend.model.TicketMessage;
import com.eventux.backend.repository.TicketMessageRepository;
import com.eventux.backend.service.TicketService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.eventux.backend.dto.TicketUpdateRequest;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final TicketMessageRepository ticketMessageRepo;

    public TicketController(TicketService ticketService, TicketMessageRepository ticketMessageRepo) {
        this.ticketService = ticketService;
        this.ticketMessageRepo = ticketMessageRepo;
    }

    @GetMapping
    public List<TicketDTO> getAll() {
        return ticketService.getAll().stream().map(TicketDTO::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDTO> getById(@PathVariable Long id) {
        return ticketService.getById(id)
                .map(TicketDTO::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    // CREATE (app OR event): eventId=null => app ticket, else event ticket
    @PostMapping
    public ResponseEntity<?> create(@RequestBody TicketCreateRequest req) {
        try {
            Ticket t = ticketService.createFrom(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(TicketDTO.from(t));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new Message(ex.getMessage()));
        }
    }

    // Convenience: list tickets for an event (for owners)
    @GetMapping("/by-event/{eventId}")
    public List<TicketDTO> byEvent(@PathVariable Long eventId) {
        return ticketService.findByEvent(eventId).stream().map(TicketDTO::from).toList();
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketDTO> updateStatus(@PathVariable Long id,
                                                  @RequestBody Map<String, String> body) {
        String status = body != null ? body.get("status") : null;
        try {
            Ticket updated = ticketService.setStatus(id, status);
            return ResponseEntity.ok(TicketDTO.from(updated));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(null);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketDTO> update(@PathVariable Long id, @RequestBody TicketUpdateRequest req) {
        return ticketService.getById(id).map(t -> {
            if (req.getTicketTitle() != null) t.setTicketTitle(req.getTicketTitle());
            if (req.getTicketContent() != null) t.setTicketContent(req.getTicketContent());
            if (req.getTicketStatus() != null) t.setTicketStatus(req.getTicketStatus().trim().toUpperCase());
            Ticket saved = ticketService.save(t);
            return ResponseEntity.ok(TicketDTO.from(saved));
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PatchMapping("/{id}/reply")
    public ResponseEntity<TicketDTO> reply(@PathVariable Long id,
                                           @RequestBody Map<String, String> body) {
        String reply = body != null ? body.get("reply") : null;
        // For now we just accept and return the same ticket (no thread model yet)
        try {
            Ticket t = ticketService.acceptReply(id, reply);
            return ResponseEntity.ok(TicketDTO.from(t));
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { ticketService.deleteById(id); }
    // List messages for a ticket (for chat view)
    @GetMapping("/{id}/messages")
    public ResponseEntity<List<TicketMessageDTO>> messages(@PathVariable Long id) {
        if (ticketService.getById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        var list = ticketMessageRepo.findAllByTicketIdOrderByCreatedAtAsc(id)
                .stream().map(TicketMessageDTO::from).toList();
        return ResponseEntity.ok(list);
    }

    // Add a message to a ticket (admin or user)
    @PostMapping("/{id}/messages")
    public ResponseEntity<TicketMessageDTO> addMessage(@PathVariable Long id, @RequestBody Map<String, String> body) {
        var ticketOpt = ticketService.getById(id);
        if (ticketOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        String text = body != null ? body.get("text") : null;
        String sender = (body != null && body.get("sender") != null) ? body.get("sender") : "ADMIN";
        if (text == null || text.isBlank()) return ResponseEntity.badRequest().body(null);

        TicketMessage m = new TicketMessage();
        m.setTicket(ticketOpt.get());
        m.setSender(sender.toUpperCase());
        m.setText(text.trim());
        m = ticketMessageRepo.save(m);

        return ResponseEntity.status(HttpStatus.CREATED).body(TicketMessageDTO.from(m));
    }
    @GetMapping("/by-reporter")
    public List<TicketDTO> byReporter(@RequestParam String email) {
        return ticketService.findByReporterEmail(email)
                .stream().map(TicketDTO::from).toList();
    }

    record Message(String message) {}
}