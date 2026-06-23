# Alert Service - Microservicio de Alertas de Emergencia

Spring Boot 3.4.x para la emisión de alertas públicas del sistema de emergencias Valle del Sol.

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/alertas` | Listar todas las alertas |
| GET | `/api/alertas/{id}` | Obtener alerta por ID |
| POST | `/api/alertas` | Crear alerta desde reporte |
| DELETE | `/api/alertas/{id}` | Eliminar alerta |

## Strategy Pattern

El cálculo del nivel de riesgo usa el patrón **Strategy**:

- `EnCombateStrategy` → `CATASTROFE`
- `PreventivoStrategy` → `PREVENTIVO` (observación)
- `DesconocidoStrategy` → `PREVENTIVO` (sin conexión, fallback)
- `NivelRiesgoContext` → Fábrica de estrategias con `ConcurrentHashMap` y fallback

## Tecnologías

- Spring Boot 3.4.4 / Java 17
- Spring Data JPA (MySQL)
- Spring Cloud Circuit Breaker (Resilience4j)
- Eureka Client
- JaCoCo (cobertura ≥ 60%)

## Tests

```bash
mvnw test
```
