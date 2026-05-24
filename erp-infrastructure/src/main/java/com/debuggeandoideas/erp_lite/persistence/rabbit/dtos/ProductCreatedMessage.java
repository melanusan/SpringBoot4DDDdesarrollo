/*
 * ProductCreatedMessage
 *
 * English: DTO used for messaging (RabbitMQ) when a product is created.
 * Español: DTO usado en mensajería (RabbitMQ) cuando se crea un producto.
 */
package com.debuggeandoideas.erp_lite.persistence.rabbit.dtos;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductCreatedMessage(
        String productId,
        String sku,
        String name,
        BigDecimal price,
        String currency,
        Instant timestamp,
        String description,
        Integer stock,
        String categoryId,
        String imageUrl,
        boolean active
) {
}
