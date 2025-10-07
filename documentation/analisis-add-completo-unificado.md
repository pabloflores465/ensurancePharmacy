# Análisis ADD Unificado - Ensurance Pharmacy System

## C. Análisis ADD Completo de la Arquitectura

### C.1 Contexto General y Drivers Arquitectónicos
- **Sistema**: Plataforma integrada Ensurance (seguros médicos) + Pharmacy (farmacia digital)
- **Iteraciones ADD**:
  - **Iteración 1**: Definición de microservicios y límites de dominio.
  - **Iteración 2**: Refinamiento interno con arquitectura en capas.
  - **Iteración 3**: Infraestructura DevOps, observabilidad y despliegue multiambiente.
- **Drivers clave**:
  - Requisitos funcionales principales (`RF-01` a `RF-12` en `documentation/analisis-add-resumen.md`).
  - Restricciones tecnológicas (`C-01` a `C-08`): Java 21, Vue 3, SQLite, infraestructura autogestionada.
  - Preocupaciones arquitectónicas: separación de concerns, integrabilidad, evolución continua.

### C.2 Pipeline y Sistema Desarrollado el Semestre Pasado
- **Estrategia multi-pipeline**:
  - **GitHub Actions** (`.github/workflows/ci-cd.yml`): build + tests + SonarQube + deploy condicional.
  - **Drone CI** (`.drone.yml`): ejecución ARM64, asegura paridad con entorno self-hosted.
  - **Jenkins** (`Jenkinsfile`): pipeline declarativo con stages de build, quality gate y despliegue.
- **Cobertura integral del sistema**:
  - **Backends** (`backv4/`, `backv5/`): Maven + JUnit + JaCoCo.
  - **Frontends** (`ensurance/`, `pharmacy/`): Vitest/Jest + análisis ESLint + cobertura LCOV.
  - **Análisis estático**: SonarQube centraliza métricas de mantenibilidad, seguridad y debt.
  - **Artefactos de despliegue**: imágenes Docker construidas desde `Dockerfile` y orquestadas con `docker-compose.*.yml`.
- **Cadena de promoción**:
  1. `dev` → validación funcional y pruebas rápidas.
  2. `qa` → smoke tests, validación con datos controlados.
  3. `main` → publicación para demos y entregables.
- **Monitoreo y retroalimentación**:
  - Prometheus + Grafana (`monitoring/prometheus/prometheus.yml` y dashboards en `documentation/`).
  - Logs persistidos en `backv4/logs/`, `backv5/logs/` y contenedores supervisados.

### C.3 Atributos de Calidad Priorizados
| Prioridad | Atributo | Razonamiento | Tácticas principales |
|-----------|----------|--------------|----------------------|
| 🔴 Crítica | Maintainability | Proyecto académico iterativo, equipo pequeño | Arquitectura en capas, patrones DAO, calidad de código controlada por SonarQube |
| 🔴 Crítica | Testability | Cambios frecuentes en todos los módulos | CI/CD automatizado, DI manual, ambientes aislados |
| 🟡 Alta | Modifiability | Nuevos módulos (hospitales, medicamentos, pólizas) | Microservicios, configuración externa, REST |
| 🟡 Alta | Security | Datos médicos y financieros sensibles | Hashing de contraseñas, RBAC, validaciones front/back |
| 🟡 Alta | Performance | Catálogo grande y dashboards en tiempo real | Paginación, consultas optimizadas, caché frontend |
| 🟢 Media | Availability | Entregable académico pero con sesiones de demo continuas | Supervisor, health checks, monitoreo |
| 🟢 Media | Scalability | Preparación para nuevas cohortes/usuarios | Servicios stateless, separación de bases |
| 🟢 Media | Interoperability | Integraciones internas y con herramientas de calidad | APIs REST, estándares HTTP |
| 🟢 Media | Usability | Usuarios finales diversos (clientes, empleados, admins) | Vue 3 reusable components, validaciones UX |

### C.4 Escenarios de Calidad (9 Escenarios Representativos)
Cada escenario sigue el formato Estímulo → Artefacto → Respuesta → Métrica.

#### C.4.1 Performance
- **Escenario PERF-01**: Búsqueda de medicamentos con filtros.
  - Estímulo: Usuario final aplica filtros múltiples en `pharmacy`.
  - Respuesta: `MedicineDAO` ejecuta consulta paginada (<20 registros).
  - Métrica: Tiempo de respuesta `< 1.5s` en carga media (10 usuarios concurrentes).
- **Escenario PERF-02**: Renderizado de dashboard administrativo.
  - Estímulo: Admin solicita métricas agregadas.
  - Respuesta: `DashboardHandler` ejecuta queries agregadas.
  - Métrica: Tiempo `< 3s`, máximo 10 consultas por petición.

#### C.4.2 Security
- **Escenario SEC-01**: Autenticación con credenciales inválidas.
  - Estímulo: Usuario ingresa password incorrecto.
  - Respuesta: `LoginHandler` rechaza con `HTTP 401`, registra intento.
  - Métrica: 100% de intentos fallidos registrados, 0 exposición de datos sensibles.
- **Escenario SEC-02**: Control de rutas administrativas.
  - Estímulo: Rol `client` intenta acceder a `/admin/users` desde `ensurance`.
  - Respuesta: Guards en `ensurance/src/router.ts` redirigen y muestran mensaje.
  - Métrica: 0 accesos no autorizados detectados en auditorías.

#### C.4.3 Availability
- **Escenario AVAIL-01**: Caída de `backv4` por excepción.
  - Estímulo: Proceso Java termina abruptamente.
  - Respuesta: Supervisor reinicia contenedor (<30s) y notifica a pipeline.
  - Métrica: MTTR `< 30s`, reinicio automático 100%.

#### C.4.4 Scalability
- **Escenario SCAL-01**: Incremento x3 de usuarios concurrentes en Pharmacy.
  - Estímulo: Campaña de descuentos aumenta tráfico.
  - Respuesta: Despliegue de instancia adicional en `docker-compose.main.yml`.
  - Métrica: Throughput escala linealmente, respuesta `< 2s`.

#### C.4.5 Maintainability
- **Escenario MAINT-01**: Nuevo endpoint GET `/api/hospitals/{id}/services`.
  - Estímulo: Solicitud funcional.
  - Respuesta: Se crea handler dedicado reutilizando `HospitalDAO` y se agregan pruebas.
  - Métrica: Implementación `< 2h`, 0 regresiones (tests verdes).

#### C.4.6 Usability
- **Escenario USAB-01**: Registro de usuario en `ensurance`.
  - Estímulo: Nuevo usuario completa formulario.
  - Respuesta: `Register.vue` valida campos y muestra confirmación.
  - Métrica: Tasa de abandono `< 10%`, feedback inmediato (<100ms).

#### C.4.7 Modifiability
- **Escenario MODIF-01**: Actualizar lógica de cálculo de cobertura.
  - Estímulo: Cambio normativo.
  - Respuesta: Ajustes en `CoverageService` sin modificar API pública, pruebas automáticas verifican.
  - Métrica: Cambio aislado a capa de negocio, despliegue en `< 1 día`.

#### C.4.8 Testability
- **Escenario TEST-01**: Pipeline detecta regresión en `UserDAOTest`.
  - Estímulo: Commit con error en consulta SQL.
  - Respuesta: GitHub Actions falla etapa de `mvn test`, notifica a Slack/email.
  - Métrica: Retroalimentación `< 10 min`, calidad gate bloquea merge.

#### C.4.9 Interoperability
- **Escenario INTEROP-01**: Ensurance consulta póliza desde Pharmacy.
  - Estímulo: Pedido en Pharmacy requiere validar cobertura en Ensurance.
  - Respuesta: `pharmacy-backend` invoca endpoint REST `/api/policies/{id}`.
  - Métrica: Respuesta `< 2s`, 100% mensajes JSON válidos.

## D. Detalle de Arquitecturas Utilizadas

### D.1 Arquitectura de Microservicios (Contexto Global)
- **Decisión**: Separar dominios Ensurance y Pharmacy para independencia de despliegue y escalado.
- **Justificación**: Permite evolucionar cada dominio, aislar fallos y asignar equipos especializados.
- **Implicaciones**: Necesidad de contratos REST bien definidos y orquestación de infra.

```plantuml
@startuml MicroservicesContext
skinparam rectangle {
  BackgroundColor #F7F7F7
  BorderColor #4B6A9B
}
skinparam cloud {
  BackgroundColor #E6F2FF
  BorderColor #4B6A9B
}

actor "Usuario Final" as User
actor "Administrador" as Admin

rectangle "Frontends" {
  component "Ensurance UI (Vue 3 TS)" as EnsUI
  component "Pharmacy UI (Vue 3 JS)" as PharUI
}

rectangle "Backends" {
  component "Ensurance Service\n(Java 21, Hibernate)" as EnsSvc
  component "Pharmacy Service\n(Java 21, Hibernate)" as PharSvc
}

cloud "Infraestructura" {
  database "SQLite Ensurance" as EnsDB
  database "SQLite Pharmacy" as PharDB
  component "SonarQube" as Sonar
  component "Prometheus" as Prom
  component "Grafana" as Graf
}

User --> EnsUI
User --> PharUI
Admin --> EnsUI
Admin --> PharUI

EnsUI --> EnsSvc : REST /api/*
PharUI --> PharSvc : REST /api2/*
PharSvc --> EnsSvc : REST /api/policies

EnsSvc --> EnsDB
PharSvc --> PharDB
EnsSvc --> Sonar : Quality Reports
PharSvc --> Sonar
Prom --> EnsSvc : Scrape
Prom --> PharSvc : Scrape
Graf --> Prom : Dashboards
@enduml
```

### D.2 Arquitectura Interna por Servicio (Layered)
- **Decisión**: Arquitectura en capas estricta para mejorar mantenibilidad y testabilidad.
- **Capas**: Presentation (`HttpHandler`), Application (`Service`), Domain (`Repository/DAO`), Data (`Hibernate Session`).

```plantuml
@startuml LayeredBackend
skinparam packageStyle rectangle
skinparam shadowing false

package "Presentation Layer" {
  class "UserHandler" as Handler {
    +handle(exchange)
  }
}

package "Application Layer" {
  class "UserService" as Service {
    +createUser(dto)
    +findUser(id)
  }
}

package "Domain Layer" {
  class "UserDAO" as DAO {
    +save(User)
    +findByEmail(email)
  }
  class "User" {
    -id
    -name
    -role
  }
}

package "Data Layer" {
  class "HibernateSessionFactory" as SessionFactory
}

Handler --> Service
Service --> DAO
DAO --> SessionFactory

note right of Handler
  Valida request, retorna respuesta JSON
end note

note right of Service
  Contiene lógica de negocio y reglas de validación
end note

note right of DAO
  Encapsula operaciones CRUD y queries específicas
end note
@enduml
```

### D.3 Pipeline CI/CD Integrado
- **Decisión**: Ejecutar pipelines redundantes con etapas homogéneas para garantizar calidad.
- **Etapas Clave**: Checkout, build/test, análisis de calidad, publicación de artefactos, despliegue.

```plantuml
@startuml CICDPipeline
skinparam defaultFontName Courier
skinparam ArrowColor #4B6A9B
skinparam activity {
  BackgroundColor #EFF3FB
  BorderColor #4B6A9B
}

start
:Checkout Código (git);
partition "Build & Test" {
  :Compilar Backends (Maven);
  :Tests Frontend (Vitest/Jest);
  :Reportes de Cobertura (JaCoCo/LCOV);
}
partition "Quality" {
  :SonarQube Scan;
  if (Quality Gate OK?) then (Sí)
    :Publicar Reportes;
  else (No)
    stop
  endif
}
partition "Packaging" {
  :Construir Imágenes Docker;
  :Publicar Artefactos en Registry;
}
partition "Deploy" {
  :Despliegue Dev;
  :Aprobación Manual;
  :Despliegue QA/Main;
}
stop
@enduml
```

### D.4 Observabilidad y Operación
- **Decisión**: Integrar monitoreo en tiempo real y orquestación simple con Docker Compose.
- **Elementos**:
  - Health checks (`docker-compose.*.yml`) para detección temprana.
  - Prometheus/Grafana para métricas de infraestructura y negocio.
  - Logs persistentes y supervisados con `Supervisor`.

```plantuml
@startuml Observability
skinparam rectangle {
  BackgroundColor #F5FBF0
  BorderColor #3A7A3A
}

rectangle "Docker Host" {
  frame "Supervisor" {
    component "Ensurance Backend" as EnsB
    component "Pharmacy Backend" as PharB
    component "Frontends" as Fronts
  }

  frame "Monitoring Agents" {
    component "Prometheus Exporters" as Exporters
    component "Healthcheck Scripts" as Healths
  }
}

component "Prometheus" as PromExt
component "Grafana" as GrafExt
component "Alertmanager" as Alert

Healths --> EnsB
Healths --> PharB
Exporters --> EnsB
Exporters --> PharB
PromExt --> Exporters
GrafExt --> PromExt
Alert --> PromExt
@enduml
```

### D.5 Justificación de la Elección Arquitectónica
- **Microservicios + REST**: Balance entre separación de dominios y complejidad operativa manejable mediante Docker Compose. Facilita agregar nuevos dominios sin acoplar el core existente.
- **Arquitectura en Capas**: Favorece mantenibilidad/testabilidad, permite aislar cambios a nivel de DTOs, servicios o persistencia. Soporta migración futura a frameworks como Spring Boot sin reestructurar la lógica.
- **Multi-Pipeline CI/CD**: Multiplica puntos de control de calidad y provee resiliencia frente a fallos de plataforma; cada pipeline refuerza atributos de testabilidad y disponibilidad operativa.
- **Observabilidad Integrada**: Visibilidad temprana de fallos y métricas garantiza disponibilidad y performance aceptables en demostraciones y usos reales.

---

## Conclusión
Este documento consolida el análisis ADD del sistema Ensurance Pharmacy en un único archivo, integrando drivers, atributos, escenarios clave, pipeline CI/CD y vistas arquitectónicas con diagramas PlantUML. Las decisiones reflejan un enfoque centrado en mantenibilidad y testabilidad, sin descuidar seguridad, disponibilidad y preparación para escalabilidad futura.
