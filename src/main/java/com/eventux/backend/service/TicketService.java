package com.eventux.backend.service;

import com.eventux.backend.model.Ticket;
import com.eventux.backend.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public List<Ticket> getAll() {
        return ticketRepository.findAll();
    }

    public Optional<Ticket> getById(Long id) {
        return ticketRepository.findById(id);
    }

    public Ticket save(Ticket obj) {
        return ticketRepository.save(obj);
    }

    public void deleteById(Long id) {
        ticketRepository.deleteById(id);
    }
}
