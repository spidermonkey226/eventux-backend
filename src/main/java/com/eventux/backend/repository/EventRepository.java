package com.eventux.backend.repository;

import com.eventux.backend.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByHost_IdUserOrManager_IdUser(Integer hostIdUser, Integer managerIdUser);
}