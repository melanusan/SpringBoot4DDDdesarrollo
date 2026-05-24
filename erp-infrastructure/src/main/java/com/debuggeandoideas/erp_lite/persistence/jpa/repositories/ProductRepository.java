/*
 * ProductRepository (Spring Data JPA)
 *
 * English: JPA repository interface used by the JPA adapter to perform CRUD operations.
 * Español: Interfaz de repositorio JPA usada por el adaptador JPA para operaciones CRUD.
 */
package com.debuggeandoideas.erp_lite.persistence.jpa.repositories;

import com.debuggeandoideas.erp_lite.persistence.jpa.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {

    Optional<ProductEntity> findBySku(String sku);
}
