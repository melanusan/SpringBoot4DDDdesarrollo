# Skill: Unit Testing Best Practices

## Cuando usar este skill
Cuando vayas a generar unit tests para clases Java con JUnit 5 y Mockito.

## Setup requerido por clase de test

@ExtendWith(MockitoExtension.class)
class MiClaseTest {

    @Mock
    private MiDependencia dependencia;

    @InjectMocks
    private MiClase miClase;
}

## Nomenclatura
should_[resultado]_when_[condicion]

should_createOrder_when_validDataProvided
should_throwException_when_customerNotFound
should_returnEmpty_when_productNotExists

## Estructura interna obligatoria

@Test
void should_resultado_when_condicion() {
    // Given
    var input = ...;
    when(dependencia.metodo()).thenReturn(valor);

    // When
    var result = miClase.ejecutar(input);

    // Then
    assertThat(result).isNotNull();
    verify(dependencia).metodo();
}

## Cobertura minima obligatoria: 80%
Cada clase debe cubrir:
- Happy path
- Valores nulos o vacios
- Excepciones esperadas con assertThrows()
- Verificacion de llamadas con verify()
- Casos limite (stock cero, precio minimo, lista vacia)

## Verificar cobertura
./gradlew :erp-application:test jacocoTestReport

## Anti-patterns

// Nunca usar @SpringBootTest en tests de dominio o application
@SpringBootTest   // incorrecto

// Nunca hacer multiples comportamientos en un test
@Test
void testTodo() {
    // incorrecto — esto prueba dos comportamientos distintos
}

// Nunca ignorar el verify
// Si el use case llama a un port, verificarlo siempre
verify(orderRepository).save(any());