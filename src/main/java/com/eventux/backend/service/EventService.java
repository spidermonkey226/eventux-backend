package com.eventux.backend.service;

import com.eventux.backend.dto.AddEventRequest;
import com.eventux.backend.model.*;
import com.eventux.backend.repository.AddressRepository;
import com.eventux.backend.repository.EventRepository;
import com.eventux.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository, AddressRepository addressRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
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
        // Host (creator)
        User host = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Host user not found"));

        // Manager (if applicable)
        User manager;
        if (Boolean.TRUE.equals(req.getHasManager())) {
            manager = userRepository.findByEmail(req.getManagerEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Manager not found. Please register."));
        } else {
            manager = host;
        }

        // Create Address
        Address address = new Address();
        address.setCity(req.getCity());
        address.setStreetName(req.getStreetName());
        address.setStreetNumber(req.getStreetNumber());
        address.setPostCode(req.getPostCode());
        Address savedAddress = addressRepository.save(address);

        // Create Event
        Event event = new Event();
        event.setEventName(req.getEventName());
        event.setEventCatgory(req.getEventCategory());
        event.setEventDate(LocalDate.parse(req.getDate()));
        event.setExpectedPeople(req.getPeople());
        event.setComments(req.getComments());
        event.setHost(host);
        event.setManager(manager);
        event.setAddress(savedAddress);
        event.setFiles(null); // Empty on creation

        return eventRepository.save(event);
    }
}
