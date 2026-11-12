
package com.drewweise.tend.kitchen.dto;

public record KitchenItemResponse(
        int id,
        String type,
        int itemId,
        String name
) {
}
