package com.eventux.backend.security;
import com.eventux.backend.model.User;
import com.eventux.backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
@Component
public class AuthFacade {
    private final UserRepository users;

    public AuthFacade(UserRepository users) {
        this.users = users;
    }

    /** Current logged-in user (by JWT principal = email). */
    public User meOrThrow() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("Unauthenticated");
        }
        String email = auth.getName();
        return users.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found: " + email));
    }
}
