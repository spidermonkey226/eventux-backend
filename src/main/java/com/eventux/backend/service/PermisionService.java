package com.eventux.backend.service;

import com.eventux.backend.model.Permision;
import com.eventux.backend.repository.PermisionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PermisionService {

    private final PermisionRepository permisionRepository;

    public PermisionService(PermisionRepository permisionRepository) {
        this.permisionRepository = permisionRepository;
    }

    public List<Permision> getAll() {
        return permisionRepository.findAll();
    }

    public Optional<Permision> getById(Long id) {
        return permisionRepository.findById(id);
    }

    public Permision save(Permision obj) {
        return permisionRepository.save(obj);
    }

    public void deleteById(Long id) {
        permisionRepository.deleteById(id);
    }
}
