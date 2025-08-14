package com.eventux.backend.repository;

import com.eventux.backend.model.Permision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermisionRepository extends JpaRepository<Permision, Integer> {
    Optional<Permision> findByRole(String role);                 // e.g. "guest"
    Optional<Permision> findByPermisionName(String permisionName); // e.g. "Guest User"
}