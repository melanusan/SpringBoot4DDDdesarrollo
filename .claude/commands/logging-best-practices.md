# Skill: Logging Best Practices

## Cuando usar este skill
Cuando vayas a agregar o mejorar logs en cualquier capa del proyecto.

## Declaracion del Logger

@Slf4j
public class MiClase { }

// Nunca
System.out.println("algo");
e.printStackTrace();

## Niveles

log.info  - inicio y fin de use cases, cambios de estado de Order/Product
log.warn  - situaciones recuperables, stock bajo, reintentos
log.error - excepciones no controladas, fallos de integracion externa
log.debug - datos de entrada/salida, solo para desarrollo

## Formato de mensaje
"[ClassName] accion - contexto={}, resultado={}"

## MDC

MDC.put("orderId", orderId.value().toString());
MDC.put("userId", createdBy);
try {
    // logica
} finally {
    MDC.clear();
}

## Anti-patterns

// Nunca exponer datos sensibles
log.info("password={}", password);
log.info("token={}", token);

// Nunca concatenar strings
log.info("Orden creada: " + orderId);

// Nunca loggear excepcion sin stack trace
log.error("Error: " + ex.getMessage());

// Nunca log sin contexto
log.info("OK");

// Correcto para excepciones
log.error("[Clase] descripcion - contexto={}", valor, ex);

## Patron por capa

// Controller
log.info("[{}] Request recibido - {}", getClass().getSimpleName(), parametros);
log.info("[{}] Response enviado - status={}", getClass().getSimpleName(), status);

// Use Case
log.info("[{}] Iniciando - input={}", getClass().getSimpleName(), input);
log.info("[{}] Completado - resultado={}", getClass().getSimpleName(), resultado);

// Adapter
log.debug("[{}] Ejecutando operacion - params={}", getClass().getSimpleName(), params);
log.info("[{}] Operacion exitosa - id={}", getClass().getSimpleName(), id);