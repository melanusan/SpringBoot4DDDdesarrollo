# Diagrama de secuencia — AuthController

Descripción: Flujo de autenticación `login` usando `AuthenticationManager` y `JWTService`. Capas: `erp-api` (controller), `erp-application`/security, `erp-infrastructure` (user repository / user details), `erp-common` (DTOs), `erp-domain` (User entity).

```mermaid
sequenceDiagram
    autonumber
    participant Client as Cliente
    participant API as "erp-api\nAuthController"
    participant Sec as "erp-application/security\nAuthenticationManager"
    participant UserSrv as "erp-infrastructure\nUserDetailsService / UserRepository"
    participant JWT as "erp-infrastructure\nJWTService"
    participant Domain as "erp-domain\nUserEntity"
    participant Common as "erp-common\nAuthRequest / AuthResponse / AppUserDetails"

    Client->>API: POST /auth/login { username, password }
    API->>Sec: authenticationManager.authenticate(UsernamePasswordAuthenticationToken)
    Sec->>UserSrv: loadUserByUsername(username)
    UserSrv->>Domain: fetch user record
    Domain-->>UserSrv: user data
    UserSrv-->>Sec: UserDetails (AppUserDetails)
    Sec-->>API: Authentication(principal=AppUserDetails)
    API->>JWT: jwtService.generateToken(AppUserDetails)
    JWT-->>API: token string
    API-->>Client: 200 OK { token }
```
