package com.eventux.backend.service;

import com.eventux.backend.model.Invited;
import com.eventux.backend.model.InvitedId;
import com.eventux.backend.repository.InvitedRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvitedService {

    private final InvitedRepository invitedRepository;

    public InvitedService(InvitedRepository invitedRepository) {
        this.invitedRepository = invitedRepository;
    }

    public List<Invited> getAll() {
        return invitedRepository.findAll();
    }
    public List<Invited> findByEventId(Integer eventId) {
        return invitedRepository.findById_EventId(eventId); // or your existing query method
    }
    public Optional<Invited> getById(InvitedId id) {
        return invitedRepository.findById(id);
    }

    public Invited save(Invited obj) {
        return invitedRepository.save(obj);
    }
    public boolean exists(Integer eventId, String emailLower) {
        return invitedRepository.existsByEvent_EventIDAndId_EmailIgnoreCase(eventId, emailLower);
    }
    public void deleteById(InvitedId id) {
        invitedRepository.deleteById(id);
    }
}