
package com.drewweise.tend.kitchen.controller;

import com.drewweise.tend.kitchen.dto.KitchenItemResponse;
import com.drewweise.tend.kitchen.service.KitchenService;
import com.drewweise.tend.user.model.User;
import com.drewweise.tend.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

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

    /**
     * Retrieves the authenticated user's Kitchen.
     */
    @GetMapping
    public List<KitchenItemResponse> getKitchen(
            @AuthenticationPrincipal Jwt jwt
    ) {
        User user = userService.getOrCreateUser(jwt.getSubject());

        return kitchenService.getKitchenItems(user.getUserId());
    }

    /**
     * Saves an existing Food to the authenticated
     * user's Kitchen.
     */
    @PostMapping("/{foodId}")
    @ResponseStatus(HttpStatus.CREATED)
    public KitchenItemResponse addKitchenItem(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer foodId
    ) {
        User user = userService.getOrCreateUser(jwt.getSubject());

        return kitchenService.addKitchenItem(
                user.getUserId(),
                foodId
        );
    }

    /**
     * Removes a saved KitchenItem.
     */
    @DeleteMapping("/{kitchenItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeKitchenItem(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer kitchenItemId
    ) {
        User user = userService.getOrCreateUser(jwt.getSubject());

        kitchenService.removeKitchenItem(
                user.getUserId(),
                kitchenItemId
        );
    }
}
