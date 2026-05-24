/*
 * CacheConstants
 *
 * English: Constants used for cache keys and TTL values.
 * Español: Constantes usadas para keys de cache y valores TTL.
 */
package com.debuggeandoideas.erp_lite.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CacheConstants {

    // Products
    public static final String CACHE_PRODUCTS_BY_ID = "products:byId::";
    public static final String CACHE_PRODUCTS_BY_SKU = "products:bySku::";
    public static final String CACHE_PRODUCTS_BY_CATEGORY = "products:byCategory::";
    public static final String CACHE_PRODUCTS_ACTIVE = "products:active::all";

    // Catalogs
    public static final String CACHE_CATALOGS_BY_TYPE = "catalogs:byType::";
    public static final String CACHE_CATALOGS_ITEMS = "catalogs:items::";

}
