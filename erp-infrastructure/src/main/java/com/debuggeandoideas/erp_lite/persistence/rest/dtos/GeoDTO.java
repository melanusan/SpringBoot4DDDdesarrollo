/*
 * GeoDTO
 *
 * English: Simple DTO for geo location returned by external APIs.
 * Español: DTO simple para geolocalización devuelta por APIs externas.
 */
package com.debuggeandoideas.erp_lite.persistence.rest.dtos;

public record GeoDTO(
        String lat,
        String lng
) {}