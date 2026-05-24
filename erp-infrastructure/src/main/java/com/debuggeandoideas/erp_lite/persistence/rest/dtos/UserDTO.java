/*
 * UserDTO
 *
 * English: Data transfer object used for mapping external user payloads.
 * Español: DTO usado para mapear payloads de usuario desde proveedores externos.
 */
package com.debuggeandoideas.erp_lite.persistence.rest.dtos;

public record UserDTO(
        Long id,
        String name,
        String username,
        String email,
        AddressDTO address,
        String phone,
        String website,
        CompanyDTO company
) {}