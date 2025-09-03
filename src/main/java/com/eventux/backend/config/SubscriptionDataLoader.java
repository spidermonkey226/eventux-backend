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
            createIfMissing(repo, 0, "FREE",     SubscriptionLevel.Free,     5);
            createIfMissing(repo, 1, "BASIC",    SubscriptionLevel.Basic,    20);
            createIfMissing(repo, 2, "STANDARD", SubscriptionLevel.Standard, 50);
            createIfMissing(repo, 3, "PRO",      SubscriptionLevel.Pro,      200);
            createIfMissing(repo, 4, "ULTIMATE", SubscriptionLevel.Ultimate, -1);
        };
    }

    private void createIfMissing(SubscriptionRepository repo, int id, String name,
                                 SubscriptionLevel level, int maxEvents) {
        repo.findById(id).orElseGet(() ->
                repo.save(new Subscription(id, name, level, maxEvents))
        );
    }
}
