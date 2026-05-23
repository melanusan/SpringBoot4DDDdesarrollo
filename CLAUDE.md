# ERP Lite

Stack: Spring Boot 4.x · Java 25 · Gradle Multi-Module
Arquitectura: Hexagonal (Ports & Adapters) + DDD

## Módulos
erp-common · erp-domain · erp-application · erp-infrastructure · erp-api
Paquete base: com.debuggeandoideas.erp_lite

## Reglas de Oro
- erp-domain no depende de ningún framework externo
- Los ports (interfaces) viven en erp-domain
- Los adapters (implementaciones) viven en erp-infrastructure
- Money usa BigDecimal, nunca double

## Estructura de Contexto

Para el detalle de cada feature, ver:
- ia-specs/*

Para skills reutilizables de cada dominio, ver:
- .claude/skills/

Para comandos de ejecución por feature, ver:
- .claude/commands/

Para agentes disponibles, ver:
- .claude/agents/