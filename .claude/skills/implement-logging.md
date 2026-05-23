# Comando: implement-logging

## Descripción
Analiza y mejora los logs del proyecto completo siguiendo buenas prácticas.

## Agente
@senior-dev

## Pasos de ejecución

### Paso 1 — Leer contexto
Lee estos archivos antes de escribir cualquier código:
- ia-specs/feature-logging.md
- .claude/skills/implement-logging.md

### Paso 2 — Analizar estado actual
Busca todos los logs existentes en el proyecto:
- Ejecuta: grep -r "log\." --include="*.java" .
- Ejecuta: grep -r "System.out" --include="*.java" .
- Ejecuta: grep -r "printStackTrace" --include="*.java" .
Reporta qué encontraste antes de modificar.

### Paso 3 — Identificar gaps
Para cada clase en erp-application y erp-infrastructure verifica:
- ¿Tiene logger declarado?
- ¿Loggea inicio y fin de operaciones?
- ¿Loggea errores con stack trace?
- ¿Usa concatenación en lugar de placeholders?
- ¿Expone información sensible?

### Paso 4 — Implementar mejoras
En este orden:
1. erp-application → Use Cases
2. erp-infrastructure → Adapters de persistencia
3. erp-infrastructure → Adapters externos
4. erp-api → Controllers

### Paso 5 — Agregar MDC
En los flujos de Order y Product:
- Agregar MDC.put() al inicio del use case
- Agregar MDC.clear() en bloque finally

### Paso 6 — Verificar
- Ejecuta: grep -r "password\|token\|cardNumber\|secret" --include="*.java" .
- Confirma que ningún log expone datos sensibles
- Reporta resumen de cambios realizados