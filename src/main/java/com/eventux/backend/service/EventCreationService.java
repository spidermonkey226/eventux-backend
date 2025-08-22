package com.eventux.backend.service;

import com.eventux.backend.dto.EventCreationRequest;
import com.eventux.backend.model.*;
import com.eventux.backend.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventCreationService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final FilesRepository filesRepository;
    private final TableEntityRepository tableRepository;
    private final InvitedService invitedService;

    public EventCreationService(
            EventRepository eventRepository,
            UserRepository userRepository,
            AddressRepository addressRepository,
            FilesRepository filesRepository,
            TableEntityRepository tableRepository,
            InvitedService invitedService
    ) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.filesRepository = filesRepository;
        this.tableRepository = tableRepository;
        this.invitedService = invitedService;
    }

    public Event createEvent(EventCreationRequest request) {
        // 🔐 Get the host from the JWT-authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User host = userRepository.findByEmail(userEmail).orElseThrow();

        // 👥 Manager logic
        User manager;
        if (request.getManagerEmail() != null && !request.getManagerEmail().isEmpty()) {
            manager = userRepository.findByEmail(request.getManagerEmail())
                    .orElseThrow(() -> new RuntimeException("Manager not found")); // or create new user
        } else {
            manager = host;
        }

        // 🏡 Save address
        Address address = new Address();
        if (request.getAddress().getCity() == null) {
            throw new IllegalArgumentException("Invalid or missing city");
        }

        address.setStreetName(request.getAddress().getStreet_name());
        address.setStreetNumber(request.getAddress().getStreet_number());
        address.setPostCode(request.getAddress().getPost_code());
        address = addressRepository.save(address);

        // 📅 Create event
        Event event = new Event();
        event.setEventName(request.getEventName());
        event.setEventCatgory(EventCategory.valueOf(request.getEventCatgory()));
        event.setHost(host);
        event.setManager(manager);
        event.setAddress(address);
        event.setFiles(null); // set later from manage-event
        event = eventRepository.save(event);

        // 🪑 Save invited guests
        List<Invited> inviteds = new ArrayList<>();
        if (request.getInviteList() != null) {
            for (EventCreationRequest.InviteDTO dto : request.getInviteList()) {
                Invited inv = new Invited();
                inv.setId(new InvitedId(event.getEventID(), dto.getEmail().trim().toLowerCase()));
                inv.setFirstName(dto.getFirstName());
                inv.setNote(dto.getNote());
                inv.setEvent(event);
                Invited saved = invitedService.createInviteAndEmail(inv, event.getEventName());
                inviteds.add(saved);
            }
        }


        // 🪑 Save tables
        if (request.getTables() != null) {
            for (EventCreationRequest.TableDTO tableDTO : request.getTables()) {
                TableEntity table = new TableEntity(tableDTO.getTable_number(), tableDTO.getChair_count());
                tableRepository.save(table);
            }
        }

        return event;
    }
}
