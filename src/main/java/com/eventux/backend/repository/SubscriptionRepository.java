package com.eventux.backend.repository;

import com.eventux.backend.model.Subscription;
import com.eventux.backend.model.SubscriptionLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer>{
    Optional<Subscription> findByLevel(SubscriptionLevel level);
    Optional<Subscription> findByName(String name);
}
