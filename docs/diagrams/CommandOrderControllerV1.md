# Diagrama de secuencia — CommandOrderControllerV1

Descripción: Secuencia de la operación `postOrder` y las rutas para `patchOrderCancel` y `patchOrderStatus`. Incluye capas y componentes: `erp-api`, `erp-application`, `erp-domain`, `erp-infrastructure`, `erp-common`.

```mermaid
sequenceDiagram
    autonumber
    participant Client as Cliente
    participant API as "erp-api\nCommandOrderControllerV1"
    participant App as "erp-application\nCreateOrderUseCase / UpdateOrderStatusUseCase / CancelOrderUseCase"
    participant Domain as "erp-domain\nOrderAggregate / OrderFactory"
    participant Infra as "erp-infrastructure\nOrderRepository"
    participant Common as "erp-common\nDTOs / Commands"

    Note over API,App: Crear orden (POST)
    Client->>API: POST /commands/orders { CreateOrderCommand }
    API->>Common: validar/request mapping
    API->>App: CreateOrderUseCase.execute(CreateOrderCommand)
    App->>Domain: OrderFactory.create(...) -> OrderAggregate
    Domain->>Infra: OrderRepository.save(OrderAggregate)
    Infra-->>Domain: persisted Order id
    Domain-->>App: OrderAggregate (con id)
    App-->>API: retorna id
    API-->>Client: 201 Created Location: /commands/orders/{id}

    Note over API,App: Cancelar orden (PATCH /{id}/cancel)
    Client->>API: PATCH /commands/orders/{id}/cancel?reason=...
    API->>App: CancelOrderUseCase.execute(CancelOrderCommand)
    App->>Infra: OrderRepository.findById(id)
    Infra-->>App: OrderAggregate
    App->>Domain: OrderAggregate.cancel(reason)
    Domain->>Infra: OrderRepository.save(OrderAggregate)
    App-->>API: void
    API-->>Client: 204 No Content

    Note over API,App: Actualizar estado (PATCH /{id}/status)
    Client->>API: PATCH /commands/orders/{id}/status?status=SHIPPED
    API->>App: UpdateOrderStatusUseCase.execute(UpdateOrderStatusCommand)
    App->>Infra: OrderRepository.findById(id)
    Infra-->>App: OrderAggregate
    App->>Domain: OrderAggregate.updateStatus(newStatus)
    Domain->>Infra: OrderRepository.save(OrderAggregate)
    App-->>API: void
    API-->>Client: 204 No Content
```
