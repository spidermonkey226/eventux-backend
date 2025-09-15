package com.eventux.backend.config;

import com.eventux.backend.model.Subscription;
import com.eventux.backend.model.SubscriptionLevel;
import com.eventux.backend.repository.SubscriptionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SubscriptionDataLoader {

    @Bean
    CommandLineRunner seedSubscriptions(SubscriptionRepository repo) {
        return args -> {
            createIfMissing(repo, 0, "FREE",     SubscriptionLevel.Free,     5,   0.00);
            createIfMissing(repo, 1, "BASIC",    SubscriptionLevel.Basic,    20,  4.99);
            createIfMissing(repo, 2, "STANDARD", SubscriptionLevel.Standard, 50,  9.99);
            createIfMissing(repo, 3, "PRO",      SubscriptionLevel.Pro,      200, 19.99);
            createIfMissing(repo, 4, "ULTIMATE", SubscriptionLevel.Ultimate, -1,  49.99);
        };
    }

    private void createIfMissing(SubscriptionRepository repo, int id, String name,
                                 SubscriptionLevel level, int maxEvents, double price) {
        repo.findById(id).orElseGet(() ->
                repo.save(new Subscription(id, name, level, maxEvents,price))
        );
    }
}
