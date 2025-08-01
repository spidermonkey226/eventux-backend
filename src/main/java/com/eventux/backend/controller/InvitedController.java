package com.eventux.backend.controller;

import com.eventux.backend.model.Invited;
import com.eventux.backend.model.InvitedId;
import com.eventux.backend.service.InvitedService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inviteds")
public class InvitedController {

    private final InvitedService invitedService;

    public InvitedController(InvitedService invitedService) {
        this.invitedService = invitedService;
    }

    @GetMapping
    public List<Invited> getAll() {
        return invitedService.getAll();
    }

    @GetMapping("/{id}")
    public Optional<Invited> getById(@PathVariable InvitedId id) {
        return invitedService.getById(id);
    }

    @PostMapping
    public Invited create(@RequestBody Invited obj) {
        return invitedService.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable InvitedId id) {
        invitedService.deleteById(id);
    }
}