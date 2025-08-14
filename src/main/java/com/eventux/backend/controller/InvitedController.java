package com.eventux.backend.controller;

import com.eventux.backend.dto.InviteCreateRequest;
import com.eventux.backend.dto.InvitedDTO;
import com.eventux.backend.model.Event;
import com.eventux.backend.model.Invited;
import com.eventux.backend.model.InvitedId;
import com.eventux.backend.repository.EventRepository;
import com.eventux.backend.service.InvitedService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/inviteds")
public class InvitedController {

    private final InvitedService invitedService;
    private final EventRepository eventRepository;

    public InvitedController(InvitedService invitedService, EventRepository eventRepository) {
        this.invitedService = invitedService;
        this.eventRepository = eventRepository;
    }



    @GetMapping
    public List<InvitedDTO> getAll() {
        return invitedService.getAll().stream().map(InvitedDTO::from).toList();
    }
    @GetMapping(params = "eventId")
    public List<InvitedDTO> byEvent(@RequestParam Integer eventId) {
        return invitedService.findByEventId(eventId).stream().map(InvitedDTO::from).toList();
    }


    @GetMapping("/{eventId}/{email}")
    public ResponseEntity<InvitedDTO> getById(@PathVariable Integer eventId,
                                              @PathVariable String email) {
        return invitedService.getById(new InvitedId(eventId, email))
                .map(InvitedDTO::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody InviteCreateRequest req) {
        if (req.getEventId() == null || req.getEmail() == null || req.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "eventId and email are required"));
        }

        Event event = eventRepository.findById(req.getEventId().longValue())
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        String email = req.getEmail().trim().toLowerCase();

        // prevent duplicates
        if (invitedService.exists(event.getEventID(), email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "This email is already invited to this event"));
        }

        Invited inv = new Invited();
        inv.setId(new InvitedId(event.getEventID(), email));
        inv.setEvent(event);
        inv.setFirstName(req.getFirstName());
        inv.setNote(req.getNote());

        Invited saved = invitedService.save(inv);
        return ResponseEntity.status(HttpStatus.CREATED).body(InvitedDTO.from(saved));
    }
    @PutMapping("/{eventId}/{email}")
    public ResponseEntity<?> update(
            @PathVariable Integer eventId,
            @PathVariable String email,
            @RequestBody Map<String, String> body
    ) {
        InvitedId id = new InvitedId(eventId, email.toLowerCase());
        return invitedService.getById(id)
                .map(inv -> {
                    if (body.containsKey("firstName")) {
                        inv.setFirstName(body.get("firstName"));
                    }
                    if (body.containsKey("note")) {
                        inv.setNote(body.get("note"));
                    }
                    invitedService.save(inv);
                    // return flat DTO shape
                    Map<String,Object> dto = Map.of(
                            "eventId", inv.getId().getEventId(),
                            "email", inv.getId().getEmail(),
                            "firstName", inv.getFirstName(),
                            "note", inv.getNote()
                    );
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{eventId}/{email}")
    public ResponseEntity<Void> delete(@PathVariable Integer eventId, @PathVariable String email) {
        invitedService.deleteById(new InvitedId(eventId, email));
        return ResponseEntity.noContent().build();
    }
}