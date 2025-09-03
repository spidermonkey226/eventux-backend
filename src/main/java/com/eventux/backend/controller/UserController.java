package com.eventux.backend.controller;

import com.eventux.backend.model.User;
import com.eventux.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // ---------- Basic CRUD ----------

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Integer id) {
        return userService.getById(id);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        // If this endpoint is used by admins, you can hash here:
        // if (user.getPassword() != null) user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userService.save(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userService.deleteById(id);
    }

    // ---------- Auth-aware helpers ----------

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message("Unauthorized"));
        }
        // 👇 type witness so map/orElseGet are both ResponseEntity<?>
        return userService.findByEmail(auth.getName())
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("User not found")));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> byEmail(@PathVariable String email) {
        return userService.findByEmail(email)
                .<ResponseEntity<?>>map(ResponseEntity::ok) // 👈 type witness
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("User not found")));
    }

    // ---------- Profile update (hash password if provided) ----------

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody User patch) {
        return userService.getById(id)
                .<ResponseEntity<?>>map(user -> { // 👈 type witness
                    if (patch.getFirstName() != null) user.setFirstName(patch.getFirstName());
                    if (patch.getLastName()  != null) user.setLastName(patch.getLastName());
                    if (patch.getEmail()     != null) user.setEmail(patch.getEmail());
                    if (patch.getPhone()     != null) user.setPhone(patch.getPhone());
                    if (patch.getPassword()  != null && !patch.getPassword().isBlank()) {
                        user.setPassword(passwordEncoder.encode(patch.getPassword()));
                    }
                    if (patch.getPermision() != null) { // note: 'permision' in your entity
                        user.setPermision(patch.getPermision());
                    }
                    if (patch.getSubscriptionLevel() != null) user.setSubscriptionLevel(patch.getSubscriptionLevel());
                    if (patch.getSubscriptionStart() != null) user.setSubscriptionStart(patch.getSubscriptionStart());
                    if (patch.getSubscriptionEnd() != null) user.setSubscriptionEnd(patch.getSubscriptionEnd());
                    return ResponseEntity.ok(userService.save(user));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("User not found")));
    }

    // ---------- Avatar: by user ID ----------

    @PostMapping(path = "/{id}/avatar", consumes = { "multipart/form-data" })
    public ResponseEntity<?> uploadAvatar(@PathVariable Integer id,
                                          @RequestParam("file") MultipartFile file) {
        return userService.getById(id)
                .<ResponseEntity<?>>map(user -> { // 👈 type witness
                    try {
                        user.setAvatar(file.getBytes());
                        user.setAvatarContentType(file.getContentType() != null ? file.getContentType() : "image/jpeg");
                        userService.save(user);
                        return ResponseEntity.ok(new Message("Avatar uploaded"));
                    } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Message("Invalid image"));
                    }
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("User not found")));
    }

    @GetMapping("/{id}/avatar")
    public ResponseEntity<byte[]> getAvatar(@PathVariable Integer id) {
        return userService.getById(id)
                .<ResponseEntity<byte[]>>map(user -> {                 // 👈 force U
                    byte[] bytes = user.getAvatar();
                    if (bytes == null || bytes.length == 0) {
                        return ResponseEntity.<byte[]>status(HttpStatus.NO_CONTENT).build();
                    }
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.parseMediaType(
                            user.getAvatarContentType() != null ? user.getAvatarContentType() : "image/jpeg"));
                    return ResponseEntity.ok().headers(headers).body(bytes);
                })
                .orElseGet(() -> ResponseEntity.<byte[]>status(HttpStatus.NOT_FOUND).build()); // 👈 force T
    }

    // ---------- Avatar: current signed-in user (/me) ----------

    @PostMapping(path = "/me/avatar", consumes = { "multipart/form-data" })
    public ResponseEntity<?> uploadMyAvatar(@RequestParam("file") MultipartFile file) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message("Unauthorized"));
        }
        return userService.findByEmail(auth.getName())
                .<ResponseEntity<?>>map(u -> { // 👈 type witness
                    try {
                        u.setAvatar(file.getBytes());
                        u.setAvatarContentType(file.getContentType() != null ? file.getContentType() : "image/jpeg");
                        userService.save(u);
                        return ResponseEntity.ok(new Message("Avatar uploaded"));
                    } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Message("Invalid image"));
                    }
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("User not found")));
    }

    @GetMapping("/me/avatar")
    public ResponseEntity<byte[]> getMyAvatar() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.<byte[]>status(HttpStatus.UNAUTHORIZED).build();
        }

        return userService.findByEmail(auth.getName())
                .<ResponseEntity<byte[]>>map(u -> {                      // 👈 force U
                    if (u.getAvatar() == null || u.getAvatar().length == 0) {
                        return ResponseEntity.<byte[]>status(HttpStatus.NO_CONTENT).build();
                    }
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.parseMediaType(
                            u.getAvatarContentType() != null ? u.getAvatarContentType() : "image/jpeg"));
                    return ResponseEntity.ok().headers(headers).body(u.getAvatar());
                })
                .orElseGet(() -> ResponseEntity.<byte[]>status(HttpStatus.NOT_FOUND).build()); // 👈 force T
    }
    // ---------- Helper ----------

    record Message(String message) {}
}
