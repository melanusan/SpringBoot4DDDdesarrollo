# Diagrama de secuencia — QueryProductsControllersV1

Descripción: Secuencia para búsquedas y consultas de productos: obtener por id, por sku, activos, búsqueda por texto, y por categoría. Capas: `erp-api`, `erp-application` (queries), `erp-domain` (views), `erp-infrastructure` (repos), `erp-common`.

```mermaid
sequenceDiagram
    autonumber
    participant Client as Cliente
    participant API as "erp-api\nQueryProductsControllersV1"
    participant App as "erp-application\nFindProductByIdQuery / FindProductBySkuQuery / FindProductActiveQuery / FindProductByTextQuery / FindProductByCategory"
    participant Domain as "erp-domain\nProductView"
    participant Infra as "erp-infrastructure\nProductRepository"
    participant Common as "erp-common\nDTOs / Exceptions"

    Note over API,App: Obtener producto por ID (GET /{id})
    Client->>API: GET /queries/products/{id}
    API->>App: FindProductByIdQuery.execute(id)
    App->>Infra: ProductRepository.findById(id)
    Infra-->>App: Optional<ProductView>
    App-->>API: ProductView or throw QueryException
    API-->>Client: 200 OK { ProductView }

    Note over API,App: Obtener por SKU (GET ?sku=...)
    Client->>API: GET /queries/products?sku=SKU-001
    API->>App: FindProductBySkuQuery.execute(sku)
    App->>Infra: ProductRepository.findBySku(sku)
    Infra-->>App: Optional<ProductView>
    App-->>API: ProductView
    API-->>Client: 200 OK { ProductView }

    Note over API,App: Listar productos activos (GET /active)
    Client->>API: GET /queries/products/active
    API->>App: FindProductActiveQuery.execute()
    App->>Infra: ProductRepository.findActive()
    Infra-->>App: List<ProductView>
    App-->>API: List<ProductView> (or empty -> 204 No Content)
    API-->>Client: 200 OK / 204 No Content

    Note over API,App: Búsqueda por texto (GET /search?text=...)
    Client->>API: GET /queries/products/search?text=laptop
    API->>App: FindProductByTextQuery.execute(text)
    App->>Infra: ProductRepository.searchByText(text)
    Infra-->>App: List<ProductView>
    App-->>API: List<ProductView>
    API-->>Client: 200 OK [ ProductView... ]

    Note over API,App: Filtrar por categoría (GET ?category=...)
    Client->>API: GET /queries/products?category=ELECTRONICS
    API->>App: FindProductByCategory.execute(category)
    App->>Infra: ProductRepository.findByCategory(category)
    Infra-->>App: List<ProductView>
    App-->>API: List<ProductView>
    API-->>Client: 200 OK [ ProductView... ]
```
