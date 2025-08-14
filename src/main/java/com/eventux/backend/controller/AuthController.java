package com.eventux.backend.controller;

import com.eventux.backend.dto.JwtResponse;
import com.eventux.backend.dto.SignInRequest;
import com.eventux.backend.model.Permision;
import com.eventux.backend.model.User;
import com.eventux.backend.repository.PermisionRepository;
import com.eventux.backend.repository.UserRepository;
import com.eventux.backend.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PermisionRepository permisionRepository; // 👈 add this repo
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // Keep payload clean with a minimal DTO for signup
    public static record SignUpRequest(
            String firstName, String lastName, String email, String phone, String password) {}

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpRequest req) {
        try {
            if (req.email() == null || req.password() == null || req.firstName() == null || req.lastName() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Missing required fields"));
            }
            if (userRepository.findByEmail(req.email()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "User already exists"));
            }

            var perm = permisionRepository.findByRole("guest")
                    .orElseGet(() -> permisionRepository.findById(4)
                            .orElseThrow(() -> new IllegalStateException("Default permission not found (guest/id=4)")));

            User u = new User();
            u.setFirstName(req.firstName());
            u.setLastName(req.lastName());
            u.setEmail(req.email());
            u.setPhone(req.phone());
            u.setPassword(passwordEncoder.encode(req.password()));
            u.setPermision(perm);

            userRepository.save(u);
            return ResponseEntity.ok(Map.of("message", "User registered successfully"));
        } catch (Exception e) {
            // log e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Registration failed", "detail", e.getMessage()));
        }
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
        return ResponseEntity.ok(new JwtResponse("Sign in successful", token, user));
    }
}
