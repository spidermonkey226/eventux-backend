package com.eventux.backend.service;

import com.eventux.backend.dto.ChargeRequest;
import com.eventux.backend.model.Subscription;
import com.eventux.backend.model.SubscriptionLevel;
import com.eventux.backend.model.User;
import com.eventux.backend.repository.SubscriptionRepository;
import com.eventux.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PaymentService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    public PaymentService(UserRepository userRepository,
                          SubscriptionRepository subscriptionRepository) {
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    public User chargeAndActivate(User user, ChargeRequest req) {
        // 1) Basic validation (mock gateway)
        if (req.getPlanId() == null) {
            throw new IllegalArgumentException("Missing planId");
        }
        if (req.getCardNumber() == null || req.getCardNumber().replaceAll("\\s+", "").length() < 12) {
            throw new IllegalArgumentException("Invalid card");
        }
        if (req.getCvv() == null || req.getCvv().length() < 3) {
            throw new IllegalArgumentException("Invalid CVV");
        }

        // 2) Lookup plan (stable IDs 0..4)
        Subscription plan = subscriptionRepository.findById(req.getPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Unknown plan"));

        // 3) "Charge" (mock)
        // In a real integration, call provider; throw on failure
        boolean charged = true; // simulate success
        if (!charged) {
            throw new IllegalStateException("Payment gateway declined");
        }

        // 4) Activate subscription on user (server chooses dates)
        LocalDate start = LocalDate.now();
        LocalDate end = (plan.getLevel() == SubscriptionLevel.Ultimate) ? null : start.plusDays(30);

        user.setSubscriptionLevel(plan.getLevel());
        user.setSubscriptionStart(start);
        user.setSubscriptionEnd(end);

        return userRepository.save(user);
    }
}
