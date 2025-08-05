package com.eventux.backend;

import com.eventux.backend.model.User;
import com.eventux.backend.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication(
        exclude = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
        }
)
public class EventuxBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventuxBackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner testDatabase(UserService userService) {
        return args -> {
            System.out.println("---- User Info ----");
            List<User> users = userService.getAll();
            users.forEach(user -> {
                System.out.println("User: " + user.getFirstName() + " " + user.getLastName());
                System.out.println("Email: " + user.getEmail());
                System.out.println("Phone: " + user.getPhone());
                if (user.getPermision() != null) {
                    System.out.println("Permission: " + user.getPermision().getPermisionName());
                } else {
                    System.out.println("Permission: null");
                }
                System.out.println("-----------------------------");
            });
        };
    }
    @Bean
    public CommandLineRunner testPasswordHash(PasswordEncoder passwordEncoder) {
        return args -> {
            String rawPassword = "Alaa123";
            String encoded = passwordEncoder.encode(rawPassword);
            System.out.println("Encoded password: " + encoded);
            System.out.println("Matches: " + passwordEncoder.matches(rawPassword, "$2a$10$viHg3466pRTYdBJVQCx65ugBdXmQYE1.73XE4MqIis2uNLkaD9cCO"));
        };
    }
    @Bean
    public CommandLineRunner encodePassword(PasswordEncoder passwordEncoder) {
        return args -> {
            String password = "Alaa123";
            System.out.println("Hashed: " + passwordEncoder.encode(password));
        };
    }

}
