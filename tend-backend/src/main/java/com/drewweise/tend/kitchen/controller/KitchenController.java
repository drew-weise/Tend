
package com.drewweise.tend.kitchen.controller;

import com.drewweise.tend.kitchen.dto.KitchenItemResponse;
import com.drewweise.tend.kitchen.service.KitchenService;
import com.drewweise.tend.user.model.User;
import com.drewweise.tend.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/kitchen")
public class KitchenController {

    private final KitchenService kitchenService;
    private final UserService userService;

    public KitchenController(
            KitchenService kitchenService,
            UserService userService
    ) {
        this.kitchenService = kitchenService;
        this.userService = userService;
    }

    @GetMapping
    public List<KitchenItemResponse> getKitchen(
            @AuthenticationPrincipal Jwt jwt
    ) {
        User user = userService.getOrCreateUser(jwt.getSubject());

        return kitchenService.getKitchenItems(user.getUserId());
    }
}
