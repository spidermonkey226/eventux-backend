package com.eventux.backend.controller;

import com.eventux.backend.dto.AddEventRequest;
import com.eventux.backend.model.Event;
import com.eventux.backend.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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
    public ResponseEntity<?> create(@RequestBody AddEventRequest request) {
        System.out.println(">>> create() called with: " + request.getEventName());
        try {
            Event created = eventService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            String msg = ex.getMessage() != null ? ex.getMessage() : "Bad request";
            String lc = msg.toLowerCase();

            if (lc.contains("host user not found")) {
                // client can treat as unauthenticated/unknown user
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Host user not found"));
            }
            if (lc.contains("manager not found")) {
                // this is what your UI is expecting to show
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Manager not found. Please register."));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", msg));
        }
    }


    @GetMapping("/mine")
    public List<Event> getMine() {
        return eventService.getMine();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        eventService.deleteById(id);
    }
}
