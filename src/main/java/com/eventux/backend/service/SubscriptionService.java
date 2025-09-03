package com.eventux.backend.service;

import com.eventux.backend.model.Subscription;
import com.eventux.backend.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {
    private final SubscriptionRepository repository;

    public SubscriptionService(SubscriptionRepository repository) {
        this.repository = repository;
    }

    public List<Subscription> getAll() { return repository.findAll(); }

    public Optional<Subscription> getById(Integer id) { return repository.findById(id); }

    public Subscription save(Subscription s) { return repository.save(s); }

    public void deleteById(Integer id) { repository.deleteById(id); }
}
