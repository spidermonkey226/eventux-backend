package com.eventux.backend.controller;

import com.eventux.backend.dto.JwtResponse;
import com.eventux.backend.dto.SignInRequest;
import com.eventux.backend.model.User;
import com.eventux.backend.repository.UserRepository;
import com.eventux.backend.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody SignInRequest request) {
        System.out.println("SIGNIN called with email: " + request.getEmail());
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            System.out.println("User not found for email: " + request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        User user = optionalUser.get();
        System.out.println("User found. Email: " + user.getEmail());
        System.out.println("Encoded DB password: " + user.getPassword());
        System.out.println("Raw password from form: " + request.getPassword());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            System.out.println("Password does not match!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        System.out.println("Password matches. Generating token...");
        String token = jwtService.generateToken(user.getEmail());
        return ResponseEntity.ok(new JwtResponse(token));
    }


}
