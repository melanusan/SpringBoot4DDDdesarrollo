/*
 * CatalogItemMetadata
 *
 * English: Metadata for catalog items (e.g., tags, display info).
 * Español: Metadatos para items de catálogo (etiquetas, info de presentación).
 */
package com.debuggeandoideas.erp_lite.persistence.mongo.documents;

import java.math.BigDecimal;
import java.util.List;

public record CatalogItemMetadata(
                String icon,
                String color,
                List<String> nextStatuses,
                BigDecimal fee) {
}