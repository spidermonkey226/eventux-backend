package com.eventux.backend.controller;

import com.eventux.backend.model.Subscription;
import com.eventux.backend.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
    private final SubscriptionService service;

    public SubscriptionController(SubscriptionService service) {
        this.service = service;
    }

    @GetMapping
    public List<Subscription> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return service.getById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Subscription> create(@RequestBody Subscription s) {
        return ResponseEntity.ok(service.save(s));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Subscription patch) {
        return service.getById(id)
                .<ResponseEntity<?>>map(existing -> {
                    if (patch.getName() != null) existing.setName(patch.getName());
                    if (patch.getLevel() != null) existing.setLevel(patch.getLevel());
                    if (patch.getMaxEvents() != null) existing.setMaxEvents(patch.getMaxEvents());
                    if (patch.getPrice() != null) existing.setPrice(patch.getPrice());
                    return ResponseEntity.ok(service.save(existing));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
