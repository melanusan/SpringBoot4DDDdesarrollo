# Skill: Gradle Best Practices

## Cuando usar este skill
Cuando vayas a reorganizar o modificar dependencias en modulos Gradle.

## Version Catalog — libs.versions.toml

Todas las versiones van en gradle/libs.versions.toml:

[versions]
spring-boot           = "4.0.4"
dependency-management = "1.1.7"
lombok                = "1.18.42"
mapstruct             = "1.6.3"
lombok-mapstruct      = "0.2.0"
postgresql            = "42.7.8"
aws-s3                = "2.41.0"
springdoc             = "2.8.3"
java                  = "25"

[libraries]
lombok               = { module = "org.projectlombok:lombok", version.ref = "lombok" }
mapstruct            = { module = "org.mapstruct:mapstruct", version.ref = "mapstruct" }
mapstruct-processor  = { module = "org.mapstruct:mapstruct-processor", version.ref = "mapstruct" }
lombok-mapstruct     = { module = "org.projectlombok:lombok-mapstruct-binding", version.ref = "lombok-mapstruct" }
postgresql           = { module = "org.postgresql:postgresql", version.ref = "postgresql" }
aws-s3               = { module = "software.amazon.awssdk:s3", version.ref = "aws-s3" }
springdoc            = { module = "org.springdoc:springdoc-openapi-starter-webmvc-ui", version.ref = "springdoc" }

[plugins]
spring-boot           = { id = "org.springframework.boot", version.ref = "spring-boot" }
dependency-management = { id = "io.spring.dependency-management", version.ref = "dependency-management" }

## Regla api() vs implementation()

// implementation() — la dependencia NO se expone a otros modulos
// Usar por defecto siempre

// api() — la dependencia SI se expone transitivamente
// Usar cuando el modulo es java-library y otros modulos necesitan esa dependencia

// Ejemplos de cuando usar api()
// erp-common expone validation para que erp-application y erp-infrastructure lo hereden
api 'org.springframework.boot:spring-boot-starter-validation'

// erp-application expone oauth2 para que erp-api lo herede sin redeclararlo
api 'org.springframework.boot:spring-boot-starter-security'
api 'org.springframework.security:spring-security-oauth2-authorization-server'
api 'org.springframework.boot:spring-boot-starter-oauth2-resource-server'

## Que va en subprojects (build.gradle raiz)
Solo lo que aplica al 100% de los modulos:
- java toolchain
- dependencyManagement con BOM
- task test con JUnitPlatform
- repositories

Lo que NO va en subprojects:
- Lombok
- MapStruct
- Security y OAuth2
- JPA
- Validation

## Dependencias por modulo

### erp-common
// Requiere java-library para poder usar api()
plugins {
    id 'java-library'
}

dependencies {
    compileOnly libs.lombok
    annotationProcessor libs.lombok

    // api() expone validation transitivamente a erp-application y erp-infrastructure
    api 'org.springframework.boot:spring-boot-starter-validation'
}

### erp-domain
dependencies {
    // depende commons
    // solo usa lombok
}

### erp-application
// Requiere java-library para poder usar api()
plugins {
    id 'java-library'
}

dependencies {
    implementation project(':erp-common')
    implementation project(':erp-domain')

    compileOnly libs.lombok
    annotationProcessor libs.lombok

    // Transacciones sin traer JPA completo
    implementation 'org.springframework:spring-tx'

    // api() expone oauth2 transitivamente a erp-api
    api 'org.springframework.boot:spring-boot-starter-security'
    api 'org.springframework.security:spring-security-oauth2-authorization-server'
    api 'org.springframework.boot:spring-boot-starter-oauth2-resource-server'
}

### erp-infrastructure
plugins {
    id 'java-library'
}

dependencies {
    implementation project(':erp-common')
    implementation project(':erp-domain')
    implementation project(':erp-application')

    compileOnly libs.lombok
    annotationProcessor libs.lombok
    implementation libs.mapstruct
    annotationProcessor libs.mapstruct.processor
    implementation libs.lombok.mapstruct

    // SQL
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation libs.postgresql

    // MongoDB
    implementation 'org.springframework.boot:spring-boot-starter-data-mongodb'

    // Redis
    implementation 'org.springframework.boot:spring-boot-starter-data-redis'
    implementation 'org.springframework.boot:spring-boot-starter-cache'

    // AWS
    implementation libs.aws.s3

    // REST client
    implementation 'org.springframework.boot:spring-boot-starter-web'

    // Mail y HTML
    implementation 'org.springframework.boot:spring-boot-starter-mail'
    implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'

    // RabbitMQ
    implementation 'org.springframework.boot:spring-boot-starter-amqp'
}

### erp-api
plugins {
    id 'org.springframework.boot'
}

dependencies {
    implementation project(':erp-common')
    implementation project(':erp-domain')
    implementation project(':erp-application')
    implementation project(':erp-infrastructure')

    compileOnly libs.lombok
    annotationProcessor libs.lombok
    implementation libs.mapstruct
    annotationProcessor libs.mapstruct.processor
    implementation libs.lombok.mapstruct

    // Web
    implementation 'org.springframework.boot:spring-boot-starter-web'

    // OpenAPI
    implementation libs.springdoc

    // security, oauth2 y validation llegan transitivamente
    // desde erp-application y erp-common — no se redeclaran aqui
}

## Verificacion post-cambios
./gradlew clean build
./gradlew :erp-domain:dependencies | grep spring
./gradlew :erp-api:dependencies | grep -E "oauth|security|validation"
./gradlew :erp-application:dependencies | grep validation