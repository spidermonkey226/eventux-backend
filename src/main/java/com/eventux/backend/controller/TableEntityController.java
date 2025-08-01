package com.eventux.backend.controller;

import com.eventux.backend.model.TableEntity;
import com.eventux.backend.service.TableEntityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tableentitys")
public class TableEntityController {

    private final TableEntityService tableEntityService;

    public TableEntityController(TableEntityService tableEntityService) {
        this.tableEntityService = tableEntityService;
    }

    @GetMapping
    public List<TableEntity> getAll() {
        return tableEntityService.getAll();
    }

    @GetMapping("/{id}")
    public Optional<TableEntity> getById(@PathVariable Integer id) {
        return tableEntityService.getById(id);
    }

    @PostMapping
    public TableEntity create(@RequestBody TableEntity obj) {
        return tableEntityService.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        tableEntityService.deleteById(id);
    }
}