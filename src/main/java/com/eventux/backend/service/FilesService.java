package com.eventux.backend.service;

import com.eventux.backend.model.Files;
import com.eventux.backend.repository.FilesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FilesService {

    private final FilesRepository filesRepository;

    public FilesService(FilesRepository filesRepository) {
        this.filesRepository = filesRepository;
    }

    public List<Files> getAll() {
        return filesRepository.findAll();
    }

    public Optional<Files> getById(Integer id) {
        return filesRepository.findById(id);
    }

    public Files save(Files obj) {
        return filesRepository.save(obj);
    }

    public void deleteById(Integer id) {
        filesRepository.deleteById(id);
    }
}
