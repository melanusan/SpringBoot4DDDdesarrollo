/*
 * CatalogItem
 *
 * English: Embedded item representation inside CatalogDocument.
 * Español: Representación embebida de un item dentro de CatalogDocument.
 */
package com.debuggeandoideas.erp_lite.persistence.mongo.documents;

public record CatalogItem(
        String id,
        String code,
        String value,
        String description,
        Integer displayOrder,
        CatalogItemMetadata metadata
) {}
