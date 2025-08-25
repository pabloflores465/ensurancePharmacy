# backv5 — Pharmacy Backend (Java)

Backend HTTP ligero basado en `com.sun.net.httpserver.HttpServer` que expone la API de Farmacia bajo el prefijo `/api2`.

## Requisitos

- Java 23 (compilación con preview features habilitado en Surefire)
- Maven 3.8+

## Ejecución

- Desde IDE: ejecuta la clase `com.sources.app.App` (método `main`).
- Variables de entorno soportadas por `App.java`:
  - `SERVER_HOST` (default `0.0.0.0`)
  - `SERVER_PORT` o `PORT` (default `8081`)

Al iniciar verás en consola: `Servidor iniciado en http://{host}:{port}/api2`.

### Endpoints base

Se crean contextos bajo `/api2`, por ejemplo:

- `POST /api2/login`
- `GET/POST/PUT /api2/users`
- `GET/POST/PUT /api2/medicines`
- `GET /api2/medicines/search`
- `GET/POST/PUT /api2/orders`
- `GET/POST/PUT /api2/prescriptions`
- `GET/POST/PUT /api2/comments`
- `GET /api2/verification`

(Revisa `src/main/java/com/sources/app/App.java` para la lista completa de contextos y handlers.)

## Tests y cobertura

```bash
mvn clean test
mvn jacoco:report
```

- Reporte JaCoCo: `target/site/jacoco/index.html` (HTML), `jacoco.csv` (CSV).

## Notas de integración y variables

Cuando se ejecuta en Docker (ver `docker-compose.pharmacy.yaml` a nivel del monorepo), el servicio "pharmacy-backend" expone el contenedor 8081 en el host `${PHARM_BACKEND_HOST_PORT:-8082}` y puede recibir:

- `ENS_BACKEND_API_URL` (default `http://localhost:8081/api`)
- `HOSPITAL_API_URL` (default `http://localhost:8000/api`)
- `DB_SCHEMA_PHARMACY` (default `FARMACIA`)

Estas variables afectan integraciones externas y/o configuraciones internas.

## Build de artefacto

```bash
mvn -DskipTests package
```

Genera `target/backv5-1.0-SNAPSHOT.jar` y clases en `target/classes/`.

## Troubleshooting

- Si el puerto ya está en uso, ajusta `SERVER_PORT`.
- Si no hay DB disponible, `App.java` continúa levantando el servidor y loguea el error de conexión.
- Asegura Java 23 en tu entorno para alinear con `maven-compiler-plugin`.
