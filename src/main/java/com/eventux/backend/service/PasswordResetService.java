package com.eventux.backend.service;

import com.eventux.backend.model.PasswordResetToken;
import com.eventux.backend.model.User;
import com.eventux.backend.repository.PasswordResetTokenRepository;
import com.eventux.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    // You can keep this if you want to build links here; currently MailService builds the link.
    @Value("${app.resetTokenTtlMinutes:30}")
    private int ttlMinutes;

    public PasswordResetService(UserRepository userRepository,
                                PasswordResetTokenRepository tokenRepository,
                                PasswordEncoder passwordEncoder,
                                MailService mailService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    @Transactional
    public void requestReset(String email) {
        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return; // no user enumeration

        User user = userOpt.get();
        String token = generateToken();

        PasswordResetToken prt = new PasswordResetToken();
        prt.setUser(user);
        prt.setToken(token);
        prt.setExpiresAt(LocalDateTime.now().plusMinutes(ttlMinutes));
        tokenRepository.save(prt);

        // 👉 Use your HTML email method
        mailService.sendPasswordResetEmail(user.getEmail(), user.getFirstName(), token, ttlMinutes);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken prt = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

        if (prt.isUsed() || prt.isExpired()) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        User user = prt.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        prt.setUsedAt(LocalDateTime.now());
        tokenRepository.save(prt);
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
