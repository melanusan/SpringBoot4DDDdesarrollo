/*
 * CompanyDTO
 *
 * English: DTO representing company data from external provider.
 * Español: DTO que representa datos de la compañía desde el proveedor externo.
 */
package com.debuggeandoideas.erp_lite.persistence.rest.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CompanyDTO(
                String name,
                @JsonProperty("catchPhrase") String cp,
                String bs) {
}