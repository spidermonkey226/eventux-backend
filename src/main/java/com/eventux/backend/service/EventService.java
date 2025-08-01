package com.eventux.backend.service;

import com.eventux.backend.model.Event;
import com.eventux.backend.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    public Optional<Event> getById(Long id) {
        return eventRepository.findById(id);
    }

    public Event save(Event obj) {
        return eventRepository.save(obj);
    }

    public void deleteById(Long id) {
        eventRepository.deleteById(id);
    }
}
