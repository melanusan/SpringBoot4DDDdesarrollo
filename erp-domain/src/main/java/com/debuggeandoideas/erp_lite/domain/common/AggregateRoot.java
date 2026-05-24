package com.debuggeandoideas.erp_lite.domain.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for all Aggregate Roots. / Clase base para las raíces de agregado.
 *
 * Concepts:
 * - Aggregate Root: the entry point for a consistency boundary in DDD.
 * - It holds and manages domain events that describe state changes inside the
 * aggregate.
 *
 * English: Aggregate Roots are entities that serve as the entry point to an
 * aggregate.
 * Español: Las Aggregate Roots son entidades que sirven como punto de entrada
 * al agregado.
 *
 * Note: infrastructure should read events via {@link #getDomainEvents()} and
 * call {@link #clearDomainEvents()}
 * after successful persistence and publication.
 *
 * Reference: https://martinfowler.com/bliki/DomainEvent.html and
 * https://dddcommunity.org/
 *
 * @param <ID> the type of the aggregate root identifier / tipo del
 *             identificador
 */
public abstract class AggregateRoot<ID> extends Entity<ID> {

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected AggregateRoot(ID id) {
        super(id);
    }

    /**
     * Registers a domain event to be published.
     *
     * @param event the domain event to register
     */
    protected void registerEvent(DomainEvent event) {
        if (event != null) {
            this.domainEvents.add(event);
        }
    }

    /**
     * Returns all domain events and clears the internal list.
     * This method should be called by the infrastructure layer after persisting the
     * aggregate.
     *
     * @return an unmodifiable list of domain events
     */
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    /**
     * Clears all domain events.
     * This method should be called by the infrastructure layer after publishing
     * events.
     */
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }
}
