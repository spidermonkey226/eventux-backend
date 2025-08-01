package com.eventux.backend.repository;

import com.eventux.backend.model.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableEntityRepository extends JpaRepository<TableEntity, Integer> {

}