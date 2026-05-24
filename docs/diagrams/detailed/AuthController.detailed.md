# Diagrama detallado — AuthController

Incluye: textos del flujo original, nombres de variables usados en el código, y clases base relevantes.

Variables relevantes en `AuthController`:

- `authRequest` : `AuthRequest` (record with `username`, `password`)
- `authentication` : `Authentication` (result of AuthenticationManager)
- `userDetails` : `AppUserDetails` (principal)
- `token` : `String` (JWT returned)

Clases base y estructuras importantes:

- `AuthRequest`, `AuthResponse` : `record`
- `AppUserDetails` : implements `UserDetails` (Spring Security)
- `JWTService` : service that generates JWT tokens
- `AuthenticationManager` : Spring Security interface
- `UserDetailsService` / `UserRepository` in infra

```mermaid
sequenceDiagram
    autonumber
    participant Client as Cliente
    participant API as "erp-api\nAuthController\n(vars: authRequest:AuthRequest, authentication:Authentication, userDetails:AppUserDetails, token:String)"
    participant Sec as "erp-application/security\nAuthenticationManager (Spring Security)"
    participant UserSrv as "erp-infrastructure\nUserDetailsService / UserRepository\n(loadUserByUsername -> returns AppUserDetails)"
    participant JWT as "erp-infrastructure\nJWTService (generateToken)"
    participant Domain as "erp-domain\nUserEntity (extends Entity<UserId>?)"
    participant Common as "erp-common\nAuthRequest/AuthResponse/AppUserDetails"

    Client->>API: POST /auth/login { authRequest: AuthRequest(username,password) }
    API->>Sec: authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()))
    Sec->>UserSrv: loadUserByUsername(username)
    UserSrv->>Domain: fetch user record (UserEntity)
    Domain-->>UserSrv: user data
    UserSrv-->>Sec: AppUserDetails (implements UserDetails)
    Sec-->>API: Authentication(principal=AppUserDetails) -> authentication
    API->>JWT: jwtService.generateToken(userDetails /* AppUserDetails */)
    JWT-->>API: token (String)
    API-->>Client: 200 OK { AuthResponse(jwt: token) }
```
