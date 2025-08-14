package com.eventux.backend.repository;

import com.eventux.backend.model.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilesRepository extends JpaRepository<Files, Integer> {
    List<Files> findByEvent_EventID(Integer eventId);
}