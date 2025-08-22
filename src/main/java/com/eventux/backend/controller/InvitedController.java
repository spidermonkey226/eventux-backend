package com.eventux.backend.controller;

import com.eventux.backend.dto.InviteCreateRequest;
import com.eventux.backend.dto.InvitedDTO;
import com.eventux.backend.dto.RsvpInvitedDTO;
import com.eventux.backend.model.Event;
import com.eventux.backend.model.Invited;
import com.eventux.backend.model.InvitedId;
import com.eventux.backend.repository.EventRepository;
import com.eventux.backend.service.InvitedService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

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
        return invitedService.getById(new InvitedId(eventId, email.toLowerCase()))
                .map(InvitedDTO::from)
                .map(ResponseEntity::ok)
                // NOTE the typed body(null) to satisfy ResponseEntity<InvitedDTO>
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).<InvitedDTO>body(null));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody InviteCreateRequest req) {
        if (req.getEventId() == null || req.getEmail() == null || req.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "eventId and email are required"));
        }

        Event event = eventRepository.findById(req.getEventId().longValue())
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        String email = req.getEmail().trim().toLowerCase();

        if (invitedService.exists(event.getEventID(), email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "This email is already invited to this event"));
        }

        Invited inv = new Invited();
        inv.setId(new InvitedId(event.getEventID(), email));
        inv.setEvent(event);
        inv.setFirstName(req.getFirstName());
        inv.setNote(req.getNote());

        Invited saved = invitedService.createInviteAndEmail(inv, event.getEventName());
        return ResponseEntity.status(HttpStatus.CREATED).body(InvitedDTO.from(saved));
    }

    @PutMapping("/{eventId}/{email}")
    public ResponseEntity<?> update(@PathVariable Integer eventId,
                                    @PathVariable String email,
                                    @RequestBody Map<String, String> body) {
        InvitedId id = new InvitedId(eventId, email.toLowerCase());
        return invitedService.getById(id)
                .map(inv -> {
                    if (body.containsKey("firstName")) inv.setFirstName(body.get("firstName"));
                    if (body.containsKey("note")) inv.setNote(body.get("note"));
                    invitedService.save(inv);
                    Map<String, Object> dto = Map.of(
                            "eventId", inv.getId().getEventId(),
                            "email", inv.getId().getEmail(),
                            "firstName", inv.getFirstName(),
                            "note", inv.getNote()
                    );
                    return ResponseEntity.ok(dto);
                })
                // The method returns ResponseEntity<?>, so notFound().build() is fine here
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-token")
    public ResponseEntity<RsvpInvitedDTO> getByToken(@RequestParam String token) {
        return invitedService.getByToken(token)
                .filter(i -> i.getTokenExpiresAt() == null || i.getTokenExpiresAt().isAfter(Instant.now()))
                .map(RsvpInvitedDTO::from)
                .map(ResponseEntity::ok)
                // typed body(null) to match ResponseEntity<RsvpInvitedDTO>
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).<RsvpInvitedDTO>body(null));
    }

    @PutMapping("/{eventId}/{email}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Integer eventId,
                                             @PathVariable String email,
                                             @RequestBody Map<String, Object> body) {
        InvitedId id = new InvitedId(eventId, email.toLowerCase());

        // ✅ Avoid Optional.orElse(...) generic inference entirely
        var opt = invitedService.getById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // ResponseEntity<Void>
        }

        var inv = opt.get();

        Object raw = body.get("coming");
        Boolean coming = (raw instanceof Boolean b) ? b
                : (raw instanceof String s ? Boolean.parseBoolean(s) : null);
        inv.setComing(coming);

        if (body.containsKey("note")) {
            inv.setNote((String) body.get("note"));
        }

        invitedService.save(inv);
        return ResponseEntity.noContent().build(); // 204, ResponseEntity<Void>
    }

    @DeleteMapping("/{eventId}/{email}")
    public ResponseEntity<Void> delete(@PathVariable Integer eventId, @PathVariable String email) {
        invitedService.deleteById(new InvitedId(eventId, email.toLowerCase()));
        return ResponseEntity.noContent().build();
    }
}
