package com.eventux.backend.repository;

import com.eventux.backend.model.Invited;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.eventux.backend.model.InvitedId;

import java.util.List;


@Repository
public interface InvitedRepository extends JpaRepository<Invited, InvitedId> {
    boolean existsByEvent_EventIDAndId_EmailIgnoreCase(Integer eventId, String email);
    List<Invited> findById_EventId(Integer eventId); // here "eventId" must match InvitedId field name
}
