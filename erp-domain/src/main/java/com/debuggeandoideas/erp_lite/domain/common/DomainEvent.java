package com.debuggeandoideas.erp_lite.domain.common;

/**
 * Marker interface for all domain events. / Interfaz marcador para eventos de dominio.
 *
 * English: Domain events represent something that happened in the domain that domain experts care about.
 * Español: Los eventos de dominio representan sucesos en el dominio que son relevantes para los expertos del dominio.
 *
 * Best practice: domain events should be immutable value objects describing the change.
 *
 * Reference: https://martinfowler.com/articles/201701-event-driven.html
 */
public interface DomainEvent {
}
