# Feature: Gradle Dependencies

## Objetivo
Reorganizar las dependencias de todos los modulos Gradle para que sean
consistentes, sin duplicados y cada modulo declare solo lo que necesita.

**LEE BIEN TODO EL DOCUEMNTO **

## Agente responsable
@senior-dev

## Problemas detectados
1. Lombok, MapStruct, Validation y Security declarados en subprojects
   pero NO todos los modulos los necesitan (erp-domain no debe tenerlos)
2. erp-application tiene spring-boot-starter-data-jpa
   pero esa capa no debe conocer JPA — eso es infrastructura
3. erp-infrastructure usa api() para todo en lugar de implementation()
   lo que expone dependencias transitivas innecesariamente
4. Security y OAuth2 en subprojects afecta a erp-domain y erp-common
   que no deben tener ninguna dependencia de Spring
5. No existe gradle/libs.versions.toml — las versiones estan hardcodeadas
6. Lombok duplicado: version hardcodeada en subprojects y sin consistencia

## Alcance
- Reorganizar build.gradle raiz
- Reorganizar cada build.gradle por modulo
- Crear gradle/libs.versions.toml con todas las versiones centralizadas
- Mover dependencias al modulo correcto
- Cambiar api() por implementation() donde corresponda
- No cambiar logica de negocio
- No agregar dependencias nuevas que no existan ya

## Reglas por modulo

### erp-common
- Solo utilidades generales
- Sin dependencias de Spring en compile scope
- Lombok permitido

### erp-domain
- CERO dependencias de Spring, JPA, Lombok o cualquier framework
- Solo Java puro
- Sin MapStruct (no hay mapeo en el dominio)

### erp-application
- Depende de erp-common y erp-domain
- Sin JPA — eso pertenece a erp-infrastructure
- Lombok permitido
- Sin dependencias de base de datos

### erp-infrastructure
- Depende de erp-common, erp-domain y erp-application
- Usar implementation() no api()
- Aqui viven JPA, MongoDB, Redis, S3, Mail, RabbitMQ
- Lombok y MapStruct permitidos

### erp-api
- Depende de todos los modulos
- Spring Web, SpringDoc, Security y OAuth2 van aqui
- Es el unico modulo con Spring Boot plugin activo

## Criterios de aceptacion
- erp-domain no tiene ninguna dependencia de Spring en compile scope
- erp-application no tiene JPA
- Todas las versiones centralizadas en libs.versions.toml
- ./gradlew clean build pasa sin errores
- Cada modulo declara solo sus dependencias directas

## Referencias
- .claude/skills/gradle-best-practices.md