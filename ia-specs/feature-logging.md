# Feature: Logging

## Objetivo
Mejorar la visibilidad del sistema agregando logs estructurados con SLF4J
en todas las capas, sin exponer información sensible.

## Agente responsable
@senior-dev

## Alcance
- Analizar logs existentes en todos los módulos
- Mejorar logs existentes siguiendo buenas prácticas
- Agregar logs faltantes en use cases, adapters y controllers
-  Agregar MDC para trazabilidad (orderId, productId, userId)
- No cambiar lógica de negocio
- No modificar clases en erp-domain
- No agregar dependencias nuevas

## Reglas de negocio
- Usar @Slf4j(lombok) únicamente, nunca System.out.println
- NUNCA loggear: passwords, tokens, datos personales, números de tarjeta
- log.info  → inicio y fin de use cases, cambios de estado
- log.warn  → situaciones recuperables, stock bajo, reintentos
- log.error → excepciones no controladas, fallos de integración externa
- log.debug → datos de entrada/salida (solo desarrollo)
- Formato: "[ClassName] acción - contexto={}, resultado={}"
- MDC obligatorio: orderId, productId, userId donde aplique

## Criterios de aceptación
- Cada use case loggea inicio y fin
- Cada adapter loggea operaciones de persistencia
- Cada controller loggea request entrante y respuesta
- Cero logs con información sensible
- MDC presente en flujos de Order y Product

## Referencias
- .claude/skills/logging-best-practices.md
- .claude/commands/implement-logging.md