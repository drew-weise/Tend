
package com.drewweise.tend.user.controller;

import com.drewweise.tend.user.model.User;
import com.drewweise.tend.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/users/me")
    public Map<String, Object> currentUser(
            @AuthenticationPrincipal Jwt jwt
    ) {
        User user = userService.getOrCreateUser(jwt.getSubject());

        return Map.of(
                "userId", user.getUserId(),
                "cognitoUserId", user.getCognitoUserId()
        );
    }
}
