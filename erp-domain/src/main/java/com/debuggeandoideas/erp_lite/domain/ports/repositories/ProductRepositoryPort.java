package com.debuggeandoideas.erp_lite.domain.ports.repositories;

import com.debuggeandoideas.erp_lite.domain.entities.product.ProductRoot;
import com.debuggeandoideas.erp_lite.domain.entities.product.ProductId;

import java.util.Optional;

/**
 * Port (interface) for product persistence operations / Puerto para operaciones
 * de persistencia de productos.
 *
 * English: This is a domain port that defines persistence operations the
 * application expects.
 * Español: Es un puerto del dominio que define las operaciones de persistencia
 * que la aplicación espera.
 *
 * Implementations (adapters) live in the infrastructure layer (e.g., JPA,
 * Mongo, remote HTTP).
 *
 * Reference: Hexagonal Architecture (Ports & Adapters) -
 * https://alistair.cockburn.us/hexagonal-architecture/
 */
public interface ProductRepositoryPort {

    ProductRoot save(ProductRoot product);

    Optional<ProductRoot> findAllById(ProductId id);

    Optional<ProductRoot> findBySku(String sku);

    void delete(ProductRoot product);
}
