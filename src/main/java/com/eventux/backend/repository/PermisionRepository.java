package com.eventux.backend.repository;

import com.eventux.backend.model.Permision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermisionRepository extends JpaRepository<Permision, Long> {

}