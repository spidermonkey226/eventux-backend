package com.eventux.backend.controller;

import com.eventux.backend.model.Ticket;
import com.eventux.backend.service.TicketService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public List<Ticket> getAll() {
        return ticketService.getAll();
    }

    @GetMapping("/{id}")
    public Optional<Ticket> getById(@PathVariable Long id) {
        return ticketService.getById(id);
    }

    @PostMapping
    public Ticket create(@RequestBody Ticket obj) {
        return ticketService.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        ticketService.deleteById(id);
    }
}