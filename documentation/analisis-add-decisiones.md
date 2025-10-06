# Decisiones Arquitectónicas y Tácticas - Análisis ADD

## Decisiones por Atributo de Calidad

Este documento detalla las **decisiones arquitectónicas**, **tácticas aplicadas** y **trade-offs** para cada uno de los 9 atributos de calidad del sistema **Ensurance Pharmacy**.

---

## 1. PERFORMANCE (Rendimiento)

### 1.1 Decisiones Arquitectónicas

#### Decisión PERF-D01: Arquitectura Stateless

**Contexto**: Necesidad de soportar múltiples usuarios concurrentes sin degradación de performance.

**Decisión**: Implementar backends stateless (sin estado en memoria compartido).

**Tácticas Aplicadas**:
- **Stateless Services**: Cada request es independiente
- **Session Management**: Estado de usuario en localStorage (frontend) o tokens

**Implementación**:
```java
// No hay sesiones en memoria
public class LoginHandler implements HttpHandler {
    public void handle(HttpExchange exchange) {
        // Cada request valida credenciales independientemente
        // No hay HttpSession o estado compartido
    }
}
```

**Beneficios**:
- ✅ Escalabilidad horizontal simple
- ✅ No hay sincronización de estado
- ✅ Failover sin pérdida de sesiones

**Trade-offs**:
- ❌ Cada request puede ser más pesado (re-validación)
- ❌ Más datos en el cliente

---

#### Decisión PERF-D02: Paginación de Resultados

**Contexto**: Catálogo de medicamentos con 500+ items, dashboard con muchas transacciones.

**Decisión**: Implementar paginación en backend para todas las queries grandes.

**Tácticas Aplicadas**:
- **Resource Pooling**: Limitar resultados por página
- **Lazy Loading**: Cargar datos bajo demanda

**Implementación**:
```java
public List<Medicine> findAll(int page, int size) {
    return session.createQuery("FROM Medicine", Medicine.class)
        .setFirstResult(page * size)
        .setMaxResults(size)
        .getResultList();
}
```

**Métricas**:
- 20 items por página (configurable)
- Tiempo de respuesta < 1.5s

---

### 1.2 Tácticas de Performance Aplicadas

| Táctica | Implementación | Ubicación |
|---------|----------------|-----------|
| **Introduce Concurrency** | Procesamiento paralelo de tests en CI/CD | GitHub Actions (jobs paralelos) |
| **Bound Execution Times** | Timeouts en Quality Gates (5 min) | SonarQube, Pipeline |
| **Increase Computational Efficiency** | Queries SQL optimizadas con índices | SQLite, DAOs |
| **Reduce Computational Overhead** | Paginación, lazy loading | MedicineDAO, SearchHandler |
| **Manage Event Rate** | Rate limiting planificado (no implementado) | LoginHandler (mejora futura) |

---

## 2. SECURITY (Seguridad)

### 2.1 Decisiones Arquitectónicas

#### Decisión SEC-D01: Autenticación Basada en Credenciales + Roles

**Contexto**: Sistema con múltiples tipos de usuarios (admin, employee, client).

**Decisión**: Implementar autenticación con email/password + sistema de roles para autorización.

**Tácticas Aplicadas**:
- **Authenticate Users**: Validación de credenciales
- **Authorize Users**: Control de acceso basado en roles (RBAC)
- **Maintain Data Confidentiality**: Password hashing

**Implementación**:
```java
// Autenticación
public User authenticate(String email, String password) {
    User user = userDAO.findByEmail(email);
    if (user != null && verifyPassword(password, user.getPassword())) {
        return user;
    }
    return null;
}

// Autorización en frontend
const requireAdmin = (to, from, next) => {
    const user = JSON.parse(localStorage.getItem("user"));
    if (user.role === "admin") {
        next();
    } else {
        next('/home');
    }
};
```

**Roles Implementados**:
- `admin`: Acceso total
- `employee`: Aprobaciones, registro de clientes
- `client`: Funciones básicas de usuario

---

#### Decisión SEC-D02: CORS Configurado para Desarrollo

**Contexto**: Frontends en puertos diferentes a backends, necesidad de comunicación cross-origin.

**Decisión**: Configurar CORS permisivo en desarrollo, restrictivo en producción.

**Tácticas Aplicadas**:
- **Limit Access**: CORS headers para controlar acceso
- **Detect Attacks**: Validación de origin

**Implementación**:
```java
exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
```

**Trade-offs**:
- ✅ Facilita desarrollo local
- ❌ Menos seguro en desarrollo (origin *)
- 🔧 Mejora futura: Restringir origins en producción

---

### 2.2 Tácticas de Security Aplicadas

| Táctica | Implementación | Ubicación |
|---------|----------------|-----------|
| **Authenticate Users** | Login con email/password | LoginHandler |
| **Authorize Users** | Role-based access control (RBAC) | Vue Router Guards |
| **Maintain Data Confidentiality** | Password hashing | UserDAO |
| **Limit Access** | CORS configuration | Handlers |
| **Validate Input** | Form validation en frontend | Vue components |
| **Detect Intrusion** | Logging de intentos de login | LoginHandler (logs) |

---

### 2.3 Mejoras de Seguridad Futuras

| Mejora | Prioridad | Táctica |
|--------|-----------|---------|
| Rate limiting en login | Alta | Resist Attacks |
| JWT tokens con expiración | Alta | Limit Exposure |
| Encriptación de BD SQLite | Media | Encrypt Data |
| Validación backend de roles | Alta | Authorize Users |
| HTTPS en producción | Alta | Encrypt Data in Transit |

---

## 3. AVAILABILITY (Disponibilidad)

### 3.1 Decisiones Arquitectónicas

#### Decisión AVAIL-D01: Supervisor para Auto-Restart

**Contexto**: Necesidad de recuperación automática en caso de fallos de procesos.

**Decisión**: Usar Supervisor para gestionar procesos Java y Node.js con auto-restart.

**Tácticas Aplicadas**:
- **Exception Handling**: Try-catch en todos los handlers
- **Retry**: Supervisor reinicia proceso automáticamente
- **Active Redundancy (Warm Spare)**: Múltiples ambientes (dev/qa/main)

**Implementación** (supervisord.conf):
```ini
[program:ensurance-backend]
command=java -jar /app/backv4-1.0.jar
autostart=true
autorestart=true
startsecs=10
startretries=3
```

**Métricas**:
- Tiempo de recuperación < 30 segundos
- 100% auto-restart en fallos

---

#### Decisión AVAIL-D02: Health Checks en Docker

**Contexto**: Necesidad de detectar servicios no saludables.

**Decisión**: Configurar health checks en docker-compose.

**Tácticas Aplicadas**:
- **Ping/Echo**: Health check endpoint
- **Monitor**: Docker monitorea continuamente

**Implementación**:
```yaml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8081/api/health"]
  interval: 30s
  timeout: 10s
  retries: 3
  start_period: 40s
```

---

### 3.2 Tácticas de Availability Aplicadas

| Táctica | Implementación | Ubicación |
|---------|----------------|-----------|
| **Exception Handling** | Try-catch en DAOs y Handlers | Todo el backend |
| **Retry** | Supervisor autorestart | supervisord.conf |
| **Active Redundancy** | 3 ambientes aislados | docker-compose (dev/qa/main) |
| **Passive Redundancy** | Logs persistidos para debugging | Docker volumes |
| **Health Monitoring** | Health checks | docker-compose |
| **Escalation** | Email notifications en fallo | CI/CD pipelines |

---

## 4. SCALABILITY (Escalabilidad)

### 4.1 Decisiones Arquitectónicas

#### Decisión SCAL-D01: Microservicios Independientes

**Contexto**: Necesidad de escalar componentes de forma independiente.

**Decisión**: Separar sistema en 2 microservicios (Ensurance + Pharmacy).

**Tácticas Aplicadas**:
- **Increase Resources**: Escalar servicio específico
- **Introduce Concurrency**: Múltiples instancias en paralelo
- **Service Partitioning**: Separación por dominio de negocio

**Implementación**:
- Ensurance: Puerto 8081
- Pharmacy: Puerto 8082
- Bases de datos separadas
- APIs REST para comunicación

**Escalado Posible**:
```bash
# Escalar solo Pharmacy (más carga)
docker-compose -f docker-compose.main.yml scale pharmacy-backend=3
```

**Preparación**:
- ✅ Stateless (permite múltiples instancias)
- ✅ Base de datos por servicio
- ⚠️ Requiere load balancer (no implementado)

---

#### Decisión SCAL-D02: Hibernate ORM para Flexibilidad de BD

**Contexto**: SQLite tiene limitaciones de escalabilidad, necesidad de migración futura.

**Decisión**: Usar Hibernate ORM para abstraer capa de base de datos.

**Tácticas Aplicadas**:
- **Abstract Data Source**: ORM abstrae BD específica
- **Multiple Copies of Data**: Facilita replicación futura

**Beneficio**:
- Migración a PostgreSQL/MySQL sin cambiar código de negocio
- Solo cambiar dialecto y connection string

```xml
<!-- Cambio simple en pom.xml -->
<hibernate.dialect>org.hibernate.dialect.PostgreSQLDialect</hibernate.dialect>
```

---

### 4.2 Tácticas de Scalability Aplicadas

| Táctica | Implementación | Estado |
|---------|----------------|--------|
| **Increase Resources** | Microservicios escalables | ✅ Preparado |
| **Introduce Concurrency** | Procesamiento paralelo en CI/CD | ✅ Implementado |
| **Maintain Multiple Copies** | 3 ambientes independientes | ✅ Implementado |
| **Load Balancing** | Distribución de carga | ⚠️ Planificado |
| **Caching** | Pinia stores en frontend | ✅ Implementado |
| **Data Partitioning** | Sharding por ambiente | ✅ Implementado (dev/qa/main) |

---

## 5. MAINTAINABILITY (Mantenibilidad)

### 5.1 Decisiones Arquitectónicas

#### Decisión MAINT-D01: Layered Architecture Estricta

**Contexto**: Proyecto académico que evoluciona constantemente, necesidad de código mantenible.

**Decisión**: Implementar arquitectura en capas estricta con separación clara.

**Capas**:
1. **Presentation**: Vue Frontend
2. **Application**: HTTP Handlers
3. **Business Logic**: DAOs
4. **Data Access**: Hibernate + SQLite

**Tácticas Aplicadas**:
- **Increase Semantic Coherence**: Cada capa con responsabilidad única
- **Abstract Common Services**: DAOs reutilizables
- **Use Interfaces**: Separation of concerns

**Beneficios**:
- ✅ Fácil localizar código (por capa)
- ✅ Testear capas independientemente
- ✅ Cambios aislados (modificar una capa sin afectar otras)

---

#### Decisión MAINT-D02: Repository Pattern para Acceso a Datos

**Contexto**: Múltiples entidades (25+ en BackV4), necesidad de consistencia en acceso a datos.

**Decisión**: Implementar patrón Repository (DAOs) con interfaz consistente.

**Tácticas Aplicadas**:
- **Increase Cohesion**: DAOs agrupan operaciones de una entidad
- **Reduce Coupling**: Handlers no conocen detalles de persistencia
- **Encapsulate**: Lógica de BD oculta

**Interfaz Común**:
```java
public class GenericDAO<T> {
    public void save(T entity) { ... }
    public void update(T entity) { ... }
    public void delete(Long id) { ... }
    public T findById(Long id) { ... }
    public List<T> findAll() { ... }
}

public class UserDAO extends GenericDAO<User> {
    // Métodos específicos
    public User findByEmail(String email) { ... }
}
```

**Beneficios**:
- ✅ Patrón predecible
- ✅ Código reutilizable
- ✅ Fácil agregar nuevas entidades

---

### 5.2 Tácticas de Maintainability Aplicadas

| Táctica | Implementación | Ubicación |
|---------|----------------|-----------|
| **Increase Semantic Coherence** | Layered Architecture | Todo el sistema |
| **Abstract Common Services** | Repository Pattern (DAOs) | Backend |
| **Reduce Coupling** | Dependency Injection manual | Handlers, DAOs |
| **Defer Binding** | Hibernate ORM, configuration files | pom.xml, hibernate.cfg.xml |
| **Use Configuration Files** | Maven profiles, environment variables | pom.xml, docker-compose |
| **Record/Playback** | Logging extensivo | Logback |
| **Maintain Component Documentation** | JavaDoc, comments | Todo el código |

---

## 6. USABILITY (Usabilidad)

### 6.1 Decisiones Arquitectónicas

#### Decisión USAB-D01: Vue 3 con Componentes Reutilizables

**Contexto**: Necesidad de UI consistente y fácil de usar.

**Decisión**: Usar Vue 3 con arquitectura de componentes.

**Tácticas Aplicadas**:
- **Support User Initiative**: Feedback inmediato, validación
- **Maintain Task Model**: Componentes reflejan tareas del usuario
- **Maintain System Model**: Navegación intuitiva

**Componentes Clave**:
- Navigation: Navbar consistente
- Forms: Validación inline
- Tables: Paginación y filtros
- Modals: Confirmaciones

**Beneficios**:
- ✅ Reutilización (DRY)
- ✅ Consistencia visual
- ✅ Mantenimiento centralizado

---

#### Decisión USAB-D02: Tailwind CSS para Styling

**Contexto**: Necesidad de UI moderna y responsive.

**Decisión**: Usar Tailwind CSS en Ensurance Frontend.

**Tácticas Aplicadas**:
- **Support User Initiative**: Responsive design
- **Maintain System Model**: Design system consistente

**Beneficios**:
- ✅ Desarrollo rápido
- ✅ Consistency (utility classes)
- ✅ Responsive por defecto

---

### 6.2 Tácticas de Usability Aplicadas

| Táctica | Implementación | Ubicación |
|---------|----------------|-----------|
| **Support User Initiative** | Validación en tiempo real | Vue forms |
| **Maintain Task Model** | Flujos guiados (checkout, registro) | Frontend |
| **Maintain System Model** | Navegación consistente | Navigation component |
| **Provide Feedback** | Mensajes de éxito/error | Toast notifications |
| **Cancel Operations** | Botones de cancelar | Formularios |
| **Aggregate Data** | Dashboards con métricas | AdminDash, Dashboard pages |

---

## 7. MODIFIABILITY (Modificabilidad)

### 7.1 Decisiones Arquitectónicas

#### Decisión MODIF-D01: Separación Frontend-Backend

**Contexto**: Necesidad de evolución independiente de UI y lógica.

**Decisión**: Arquitectura completamente separada Frontend/Backend.

**Tácticas Aplicadas**:
- **Reduce Coupling**: Comunicación solo vía REST APIs
- **Increase Cohesion**: Frontend maneja UI, Backend maneja lógica
- **Defer Binding**: Cambios en uno no afectan al otro

**Beneficios**:
- ✅ Cambiar UI sin tocar backend
- ✅ Cambiar lógica sin tocar UI
- ✅ Teams paralelos (frontend/backend devs)

---

#### Decisión MODIF-D02: Configuration External

**Contexto**: Diferentes configuraciones por ambiente (dev/qa/main).

**Decisión**: Externalizar configuración en archivos y variables de entorno.

**Tácticas Aplicadas**:
- **Defer Binding**: Configuración en tiempo de deployment
- **Use Configuration Files**: docker-compose per environment
- **Parameterize Module**: Environment variables

**Implementación**:
```yaml
# docker-compose.dev.yml
environment:
  - SERVER_PORT=3002
  - DB_PATH=/app/databases/dev/ensurance/USUARIO.sqlite
```

**Beneficios**:
- ✅ Mismo código, múltiples configuraciones
- ✅ No recompilar para cambiar puerto/BD
- ✅ Fácil agregar nuevo ambiente

---

### 7.2 Tácticas de Modifiability Aplicadas

| Táctica | Implementación | Ubicación |
|---------|----------------|-----------|
| **Reduce Coupling** | Microservicios, REST APIs | Arquitectura general |
| **Increase Cohesion** | Handlers por endpoint, DAOs por entidad | Backend |
| **Defer Binding** | Configuration files | docker-compose, pom.xml |
| **Restrict Dependencies** | Layered architecture | Todo el sistema |
| **Abstract Common Services** | Base DAOs, utility classes | Backend |
| **Use Interfaces** | HttpHandler interface | Handlers |
| **Encapsulate** | DAOs ocultan SQL, Handlers ocultan lógica | Backend |

---

## 8. TESTABILITY (Testeabilidad)

### 8.1 Decisiones Arquitectónicas

#### Decisión TEST-D01: CI/CD con Tests Automatizados

**Contexto**: Proyecto académico con cambios frecuentes, necesidad de calidad.

**Decisión**: 3 pipelines CI/CD con tests automatizados en cada push.

**Tácticas Aplicadas**:
- **Record/Playback**: Tests registran comportamiento esperado
- **Separate Interface from Implementation**: Interfaces mockables
- **Control and Observe State**: JUnit assertions, test isolation

**Cobertura por Componente**:
| Componente | Framework | Cobertura Target | Actual |
|------------|-----------|------------------|--------|
| BackV4 | JUnit + JaCoCo | >70% | ~75% ✅ |
| BackV5 | JUnit + JaCoCo | >70% | ~75% ✅ |
| Ensurance Frontend | Vitest | >60% | ~65% ✅ |
| Pharmacy Frontend | Jest | >60% | ~65% ✅ |

---

#### Decisión TEST-D02: Dependency Injection Manual

**Contexto**: Necesidad de mockear DAOs en tests de Handlers.

**Decisión**: Pasar DAOs como parámetros a Handlers.

**Tácticas Aplicadas**:
- **Separate Interface from Implementation**: DAOs inyectables
- **Limit Complexity**: Sin framework de DI (simplicidad)

**Implementación**:
```java
// Handler recibe DAO en constructor
public class UserHandler implements HttpHandler {
    private final UserDAO userDAO;
    
    public UserHandler(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
}

// En test, usar mock
@Test
public void testUserHandler() {
    UserDAO mockDAO = Mockito.mock(UserDAO.class);
    UserHandler handler = new UserHandler(mockDAO);
    // Test con mock
}
```

---

### 8.2 Tácticas de Testability Aplicadas

| Táctica | Implementación | Ubicación |
|---------|----------------|-----------|
| **Control and Observe State** | JUnit assertions, test setup/teardown | Tests |
| **Limit Complexity** | Métodos pequeños, single responsibility | Handlers, DAOs |
| **Limit Non-Determinism** | BD en memoria para tests | Test setup |
| **Separate Interface from Implementation** | Dependency injection | Handlers |
| **Record/Playback** | Tests documentan comportamiento | Suite de tests |
| **Specialize Access Routes** | Test-specific endpoints (health checks) | Handlers |
| **Abstract Common Services** | Test utilities | Test helpers |

---

## 9. INTEROPERABILITY (Interoperabilidad)

### 9.1 Decisiones Arquitectónicas

#### Decisión INTEROP-D01: REST APIs Estándar

**Contexto**: Comunicación entre Ensurance y Pharmacy, integración con herramientas externas.

**Decisión**: Usar REST sobre HTTP con JSON.

**Tácticas Aplicadas**:
- **Orchestrate**: APIs bien definidas para coordinar servicios
- **Tailor Interface**: REST estándar, fácil de consumir
- **Adhere to Protocols**: HTTP/1.1, JSON format

**Estándares Aplicados**:
- HTTP methods: GET, POST, PUT, DELETE
- Status codes: 200, 201, 400, 401, 404, 500, 503
- Content-Type: application/json
- CORS headers

**Beneficios**:
- ✅ Universal (cualquier cliente HTTP)
- ✅ Fácil debugging (curl, Postman)
- ✅ Stateless (cacheable)

---

#### Decisión INTEROP-D02: SonarQube Integration en 3 Pipelines

**Contexto**: Necesidad de análisis de calidad consistente en múltiples plataformas CI/CD.

**Decisión**: Integrar SonarQube en GitHub Actions, Drone y Jenkins.

**Tácticas Aplicadas**:
- **Orchestrate**: CI/CD coordina test + análisis
- **Tailor Interface**: sonar-scanner CLI estándar
- **Discover Service**: Health check antes de análisis (Drone)

**Implementación Consistente**:
- Formato de reportes: JaCoCo XML, LCOV
- Quality Gates configurados
- Timeout de 5-10 minutos
- Project keys dinámicos por ambiente

---

### 9.2 Tácticas de Interoperability Aplicadas

| Táctica | Implementación | Ubicación |
|---------|----------------|-----------|
| **Orchestrate** | APIs REST coordinan servicios | Ensurance ↔ Pharmacy |
| **Tailor Interface** | JSON estándar, REST conventions | Todos los endpoints |
| **Adhere to Protocols** | HTTP/1.1, REST, JSON | APIs |
| **Discover Service** | Health checks, endpoints conocidos | Health endpoints |
| **Manage Interfaces** | Versionado implícito (/api vs /api2) | URLs |
| **Exchange Data** | JSON format estándar | Request/Response bodies |

---

## Resumen de Decisiones por Atributo

| Atributo | Decisiones Clave | Tácticas Principales | Trade-offs |
|----------|------------------|----------------------|------------|
| **Performance** | Stateless, Paginación | Concurrency, Resource Pooling | Simplicidad vs Optimización |
| **Security** | RBAC, Password Hashing | Authenticate, Authorize | Seguridad vs Usabilidad |
| **Availability** | Supervisor, Health Checks | Exception Handling, Retry | Complejidad vs Robustez |
| **Scalability** | Microservicios, Hibernate | Increase Resources, Partitioning | Overhead vs Flexibilidad |
| **Maintainability** | Layered, Repository Pattern | Cohesion, Coupling | Estructura vs Performance |
| **Usability** | Vue 3, Tailwind CSS | User Initiative, Feedback | Curva aprendizaje vs Productividad |
| **Modifiability** | Config Externa, Separación F/B | Defer Binding, Encapsulation | Configuración vs Simplicidad |
| **Testability** | CI/CD, DI Manual | Control State, Separation | Tests completos vs Tiempo |
| **Interoperability** | REST APIs, SonarQube | Orchestrate, Protocols | Estándares vs Custom |

---

## Próximo Documento

📊 **[analisis-add-diagramas.plantuml](./analisis-add-diagramas.plantuml)**
- Diagramas ADD visualizando decisiones
- Vistas de refinamiento arquitectónico
- Tácticas aplicadas por componente
