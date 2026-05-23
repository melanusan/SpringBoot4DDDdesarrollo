# Comando: generate-unit-tests

## Agente
@qa-expert

## Antes de ejecutar
Lee estos archivos en orden:
1. ia-specs/feature-unit-tests.md
2. .claude/skills/unit-testing-best-practices.md

## Scope
Unicamente clases dentro de:
erp-application/src/main/java/**/usecases/
erp-application/src/main/java/**/queries/
erp-application/src/main/java/**/commands/helpers

## Pasos

### Paso 1 — Identificar clases
Ejecuta:
find erp-application/src/main -name "*.java" | grep -E "usecases|queries"
Lista todas las clases encontradas antes de continuar.

### Paso 2 — Analizar cada clase
Por cada clase identificada:
- Lee el archivo completo
- Identifica los metodos publicos
- Identifica las dependencias (ports, services) que necesitan mock
- Lista los casos de prueba antes de escribir codigo

### Paso 3 — Generar tests
Por cada clase genera su archivo de test en:
erp-application/src/test/java/.../usecases/
erp-application/src/test/java/.../queries/

Reglas:
- Un archivo de test por clase
- @ExtendWith(MockitoExtension.class) obligatorio
- Minimo un test por metodo publico
- Cubrir happy path, nulls y excepciones

### Paso 4 — Ejecutar tests
./gradlew :erp-application:test
Si hay errores corrige antes de continuar.
No avances al paso 5 si hay tests en rojo.

### Paso 5 — Verificar cobertura
./gradlew :erp-application:test jacocoTestReport
Reporta el porcentaje de cobertura obtenido.
Si es menor al 80% identifica que metodos faltan cubrir y agrega los tests faltantes.

### Paso 6 — Reporte final
Entrega:
- Total de clases testeadas
- Total de tests generados
- Porcentaje de cobertura alcanzado
- Clases que no alcanzaron 80% si las hay
```

---

## Como ejecutar en Claude Code
```
@qa-expert lee ia-specs/feature-unit-tests.md
y sigue paso a paso .claude/commands/generate-unit-tests.md
no saltes ningun paso, reporta el listado de clases antes de generar tests