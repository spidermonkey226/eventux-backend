package com.eventux.backend.controller;

import com.eventux.backend.model.Permision;
import com.eventux.backend.service.PermisionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/permisions")
public class PermisionController {

    private final PermisionService permisionService;

    public PermisionController(PermisionService permisionService) {
        this.permisionService = permisionService;
    }

    @GetMapping
    public List<Permision> getAll() {
        return permisionService.getAll();
    }

    @GetMapping("/{id}")
    public Optional<Permision> getById(@PathVariable Long id) {
        return permisionService.getById(id);
    }

    @PostMapping
    public Permision create(@RequestBody Permision obj) {
        return permisionService.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        permisionService.deleteById(id);
    }
}