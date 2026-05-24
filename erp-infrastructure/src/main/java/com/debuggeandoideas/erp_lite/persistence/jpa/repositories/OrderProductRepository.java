/*
 * OrderProductRepository
 *
 * English: Repository for the relationship between orders and products (join table/entity).
 * Español: Repositorio para la relación entre órdenes y productos (tabla de unión/entidad).
 */
package com.debuggeandoideas.erp_lite.persistence.jpa.repositories;

import com.debuggeandoideas.erp_lite.persistence.jpa.entities.OrderProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderProductRepository extends JpaRepository<OrderProductEntity, UUID> {
}
