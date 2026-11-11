package com.drewweise.provisions.user.controller;

import com.drewweise.provisions.user.model.User;
import com.drewweise.provisions.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {this.userRepository = userRepository;}

    @GetMapping("/api/users/me")
    public Map<String, Object> currentUser(@AuthenticationPrincipal Jwt jwt) {

        User user = userRepository.findByCognitoUserId(jwt.getSubject())
                .orElseGet(() -> userRepository.save(new User(jwt.getSubject())));

        return Map.of(
                "userId", user.getUserId(),
                "cognitoUserId", user.getCognitoUserId()
        );
    }
}