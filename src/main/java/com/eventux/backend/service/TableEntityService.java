package com.eventux.backend.service;

import com.eventux.backend.model.TableEntity;
import com.eventux.backend.repository.TableEntityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TableEntityService {

    private final TableEntityRepository tableEntityRepository;

    public TableEntityService(TableEntityRepository tableEntityRepository) {
        this.tableEntityRepository = tableEntityRepository;
    }

    public List<TableEntity> getAll() {
        return tableEntityRepository.findAll();
    }

    public Optional<TableEntity> getById(Integer id) {
        return tableEntityRepository.findById(id);
    }

    public TableEntity save(TableEntity obj) {
        return tableEntityRepository.save(obj);
    }

    public void deleteById(Integer id) {
        tableEntityRepository.deleteById(id);
    }
}
