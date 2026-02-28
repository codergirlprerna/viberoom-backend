package com.viberoom.backend.controller;

import com.google.firebase.auth.FirebaseToken;
import com.viberoom.backend.model.User;
import com.viberoom.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/sync")
    public ResponseEntity<User> syncUser(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();

        FirebaseToken token = (FirebaseToken) auth.getDetails();
        String uid   = auth.getName();
        String email = token != null ? token.getEmail() : "";
        String name  = token != null ? token.getName()  : "User";

        User user = userRepository.findById(uid).orElseGet(() -> {
            User newUser = new User();
            newUser.setUid(uid);
            newUser.setEmail(email != null ? email : "");
            newUser.setDisplayName(name);
            newUser.setAvatarColor("#b8f724");
            newUser.setCreatedAt(LocalDateTime.now());
            return newUser;
        });

        if (name != null) user.setDisplayName(name);
        return ResponseEntity.ok(userRepository.save(user));
    }

    @GetMapping("/me")
    public ResponseEntity<User> getMe(Authentication auth) {
        // Return 401 instead of crashing when no token
        if (auth == null) return ResponseEntity.status(401).build();
        return userRepository.findById(auth.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
