package com.eventux.backend.controller;

import com.eventux.backend.model.Files;
import com.eventux.backend.service.FilesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/filess")
public class FilesController {

    private final FilesService filesService;

    public FilesController(FilesService filesService) {
        this.filesService = filesService;
    }

    @GetMapping
    public List<Files> getAll() {
        return filesService.getAll();
    }

    @GetMapping("/{id}")
    public Optional<Files> getById(@PathVariable Long id) {
        return filesService.getById(id);
    }

    @PostMapping
    public Files create(@RequestBody Files obj) {
        return filesService.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        filesService.deleteById(id);
    }
}