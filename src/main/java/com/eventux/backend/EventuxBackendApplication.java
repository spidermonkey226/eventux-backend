package com.eventux.backend;

import com.eventux.backend.model.User;
import com.eventux.backend.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
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

}
