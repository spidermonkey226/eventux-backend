package com.eventux.backend.service;

import com.eventux.backend.dto.AddEventRequest;
import com.eventux.backend.model.*;
import com.eventux.backend.repository.AddressRepository;
import com.eventux.backend.repository.EventRepository;
import com.eventux.backend.repository.PermisionRepository;
import com.eventux.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PermisionRepository permisionRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository, AddressRepository addressRepository, PermisionRepository permisionRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.permisionRepository = permisionRepository;
    }
    private int roleRank(String role) {
        if (role == null) return 0;
        return switch (role) {
            case "guest" -> 1;
            case "eventMangment" -> 2;
            case "eventCreater" -> 3;
            case "admin" -> 4;
            default -> 0;
        };
    }
    public List<Event> getMine() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalArgumentException("Not authenticated");
        }

        String email = auth.getName(); // JwtAuthenticationFilter should set this to user email
        var me = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Host user not found"));

        // NOTE: User PK is Integer, so pass Integer here
        Integer uid = me.getIdUser();

        return eventRepository.findAllByHost_IdUserOrManager_IdUser(uid, uid);
    }
    private boolean isAtLeast(Permision current, Permision target) {
        String cur = current != null ? current.getRole() : null;
        return roleRank(cur) >= roleRank(target.getRole());
    }

    private void ensureRole(User user, Permision target) {
        if (!isAtLeast(user.getPermision(), target)) {
            user.setPermision(target);
            userRepository.save(user);
        }
    }
    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    public Optional<Event> getById(Long id) {
        return eventRepository.findById(id);
    }

    public void deleteById(Long id) {
        eventRepository.deleteById(id);
    }

    @Transactional
    public Event create(AddEventRequest req) {
        // 1) Host (creator)
        User host = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Host user not found"));
        Permision creatorPerm = permisionRepository.findById(2) // role: eventCreater
                .orElseThrow(() -> new IllegalStateException("Permission id=2 (eventCreater) missing"));
        Permision managerPerm = permisionRepository.findById(3) // role: eventMangment
                .orElseThrow(() -> new IllegalStateException("Permission id=3 (eventMangment) missing"));
        // Host must be at least eventCreater
        ensureRole(host, creatorPerm);


        // 2) Manager (if applicable)
        User manager = Boolean.TRUE.equals(req.getHasManager())
                ? userRepository.findByEmail(req.getManagerEmail())
                .orElseThrow(() -> new IllegalArgumentException("Manager not found. Please register."))
                : host;
        if (!host.getIdUser().equals(manager.getIdUser())) {
            // Different person: manager must be at least eventMangment
            ensureRole(manager, managerPerm);
        } else {
            // Same person is host & manager → keep the higher role (eventCreater outranks eventMangment)
            // Host already ensured to eventCreater above, so do nothing here (do NOT downgrade).
        }
        // 3) Normalize address input (City is enum; strings trimmed)
        City city = req.getCity(); // already an enum
        String streetName   = req.getStreetName().trim();
        String streetNumber = req.getStreetNumber().trim();
        String postCode     = req.getPostCode().trim();

        // 4) Reuse existing address or create a new one (race-safe)
        Address address = addressRepository
                .findByCityAndStreetNameIgnoreCaseAndStreetNumberAndPostCode(city, streetName, streetNumber, postCode)
                .orElseGet(() -> {
                    Address a = new Address();
                    a.setCity(city);
                    a.setStreetName(streetName);
                    a.setStreetNumber(streetNumber);
                    a.setPostCode(postCode);
                    try {
                        return addressRepository.save(a);
                    } catch (DataIntegrityViolationException ex) {
                        // Unique index hit by a concurrent request → fetch and reuse
                        return addressRepository
                                .findByCityAndStreetNameIgnoreCaseAndStreetNumberAndPostCode(city, streetName, streetNumber, postCode)
                                .orElseThrow(() -> new IllegalStateException(
                                        "Unique constraint hit but address not found on re-fetch", ex));
                    }
                });

        // 5) Create Event
        Event event = new Event();
        event.setEventName(req.getEventName());
        event.setEventCatgory(req.getEventCategory());     // EventCategory enum on the entity
        event.setEventDate(LocalDate.parse(req.getDate())); // yyyy-MM-dd
        event.setExpectedPeople(req.getPeople());
        event.setComments(req.getComments());
        event.setHost(host);
        event.setManager(manager);
        event.setAddress(address);
        event.setFiles(null); // Empty on creation

        return eventRepository.save(event);
    }
}
