package com.debuggeandoideas.erp_lite.domain.common;

import java.util.Objects;

/**
 * Base class for all domain entities. / Clase base para todas las entidades del dominio.
 *
 * Concepts:
 * - In DDD an Entity is defined by its identity rather than its attributes.
 * - Entities should be immutable regarding their identity once created.
 *
 * English: Entities are identified by their ID, not by their attributes.
 * Español: Las entidades se identifican por su ID, no por sus atributos.
 *
 * Reference: https://dddcommunity.org/ (Domain-Driven Design) and
 * https://alistair.cockburn.us/hexagonal-architecture/ (Hexagonal / Ports & Adapters)
 *
 * @param <ID> the type of the entity identifier / tipo del identificador de la entidad
 */
public abstract class Entity<ID> {

    protected final ID id;

    protected Entity(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("Entity ID cannot be null");
        }
        this.id = id;
    }

    public ID getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
