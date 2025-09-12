package com.eventux.backend.service;

import com.eventux.backend.model.Invited;
import com.eventux.backend.model.InvitedId;
import com.eventux.backend.repository.InvitedRepository;
import com.eventux.backend.util.TokenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class InvitedService {

    private final InvitedRepository invitedRepository;
    private final MailService mailService;
    private final long ttlHours;

    public InvitedService(InvitedRepository invitedRepository,
                          MailService mailService,
                          @Value("${app.rsvpTokenTtlHours:168}") long ttlHours) {
        this.invitedRepository = invitedRepository;
        this.mailService = mailService;
        this.ttlHours = ttlHours;
    }

    public List<Invited> getAll() {
        return invitedRepository.findAll();
    }

    public List<Invited> findByEventId(Integer eventId) {
        return invitedRepository.findById_EventId(eventId);
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

    @Transactional
    public Invited createInviteAndEmail(Invited inv, String eventName) {
        String token = TokenUtil.newUrlSafeToken(32);
        inv.setToken(token);
        inv.setTokenExpiresAt(Instant.now().plus(Duration.ofHours(ttlHours)));
        inv.setComing(null);
        Invited saved = invitedRepository.save(inv);

        mailService.sendInviteEmail(
                saved.getId().getEmail(),
                saved.getFirstName(),
                saved.getId().getEventId(),
                eventName,
                token
        );
        return saved;
    }

    public Optional<Invited> getByToken(String token) {
        return invitedRepository.findByToken(token);
    }

    @Transactional
    public Optional<Invited> updateStatus(InvitedId id, Boolean coming, String note) {
        return invitedRepository.findById(id).map(inv -> {
            inv.setComing(coming);
            if (note != null) inv.setNote(note);
            return invitedRepository.save(inv);
        });
    }
    @Transactional
    public Optional<Invited> resendInviteEmail(Integer eventId, String email, String eventName) {
        InvitedId id = new InvitedId(eventId, email.toLowerCase());

        return invitedRepository.findById(id).map(inv -> {
            // generate a new token + expiry (just like create)
            String token = TokenUtil.newUrlSafeToken(32);
            inv.setToken(token);
            inv.setTokenExpiresAt(Instant.now().plus(Duration.ofHours(ttlHours)));
            inv.setComing(null); // keep as pending

            Invited saved = invitedRepository.save(inv);

            // ✅ reuse the same MailService logic
            mailService.sendInviteEmail(
                    saved.getId().getEmail(),
                    saved.getFirstName(),
                    saved.getId().getEventId(),
                    eventName,
                    token
            );

            return saved;
        });
    }

}
