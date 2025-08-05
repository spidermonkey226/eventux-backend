package com.eventux.backend.controller;

import com.eventux.backend.dto.AddEventRequest;
import com.eventux.backend.model.Event;
import com.eventux.backend.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<Event> getAll() {
        return eventService.getAll();
    }

    @GetMapping("/{id}")
    public Optional<Event> getById(@PathVariable Long id) {
        return eventService.getById(id);
    }

    @PostMapping
    public Event create(@RequestBody AddEventRequest request) {
        System.out.println(">>> create() called with: " + request.getEventName());
        return eventService.create(request);
    }




    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        eventService.deleteById(id);
    }
}
