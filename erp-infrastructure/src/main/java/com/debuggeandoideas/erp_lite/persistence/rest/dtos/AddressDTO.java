/*
 * AddressDTO
 *
 * English: DTO for address information from REST providers.
 * Español: DTO para información de direcciones desde proveedores REST.
 */
package com.debuggeandoideas.erp_lite.persistence.rest.dtos;

public record AddressDTO(
                String street,
                String suite,
                String city,
                String zipcode,
                GeoDTO geo) {
}
