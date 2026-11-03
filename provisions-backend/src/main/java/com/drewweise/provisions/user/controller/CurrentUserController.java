package com.drewweise.provisions.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CurrentUserController {

    @GetMapping("/api/me")
    public Map<String, String> currentUser(
            @AuthenticationPrincipal OidcUser user) {

        return Map.of(
                "subject", user.getSubject(),
                "email", user.getEmail()
        );
    }
}