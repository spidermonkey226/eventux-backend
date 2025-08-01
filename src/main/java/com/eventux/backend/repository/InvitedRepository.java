package com.eventux.backend.repository;

import com.eventux.backend.model.Invited;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.eventux.backend.model.InvitedId;


@Repository
public interface InvitedRepository extends JpaRepository<Invited, InvitedId> {

}