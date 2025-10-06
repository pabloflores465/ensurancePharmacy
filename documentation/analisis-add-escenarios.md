# Escenarios de Calidad - Análisis ADD

## Atributos de Calidad: 9 Escenarios Detallados

Este documento presenta escenarios de calidad concretos y medibles para cada uno de los 9 atributos de calidad del sistema **Ensurance Pharmacy**.

**Formato de Escenario**:
- **Estímulo (Source)**: Quién/qué genera el evento
- **Artefacto**: Componente afectado
- **Entorno**: Condiciones del sistema
- **Respuesta**: Comportamiento esperado
- **Medida de Respuesta**: Métrica cuantificable

---

## 1. PERFORMANCE (Rendimiento)

### Escenario PERF-01: Búsqueda de Medicamentos

| Aspecto | Descripción |
|---------|-------------|
| **ID** | PERF-01 |
| **Atributo** | Performance |
| **Estímulo** | Usuario realiza búsqueda de medicamentos con filtros (categoría, precio, nombre) |
| **Fuente** | Usuario final en Pharmacy Frontend |
| **Artefacto** | Pharmacy Backend (MedicineHandler, SearchMedicineHandler) |
| **Entorno** | Operación normal, catálogo con 500+ medicamentos, sistema con carga media (10 usuarios concurrentes) |
| **Respuesta** | El sistema ejecuta query en SQLite, aplica filtros, retorna resultados paginados (20 items) |
| **Medida** | Tiempo de respuesta < 1.5 segundos para búsquedas simples, < 2.5 segundos para filtros complejos |

**Implementación Actual**:
- ✅ Índices en tabla MEDICINE (nombre, categoría)
- ✅ Paginación en backend (limit/offset)
- ✅ Cache de categorías en frontend (Pinia)

---

### Escenario PERF-02: Generación de Dashboard

| Aspecto | Descripción |
|---------|-------------|
| **ID** | PERF-02 |
| **Atributo** | Performance |
| **Estímulo** | Admin accede a dashboard con métricas (usuarios activos, ventas del mes, appointments pendientes) |
| **Fuente** | Usuario admin |
| **Artefacto** | Ensurance Backend (DashboardHandler) |
| **Entorno** | BD con 1000+ usuarios, 500+ transactions, 200+ appointments |
| **Respuesta** | Sistema ejecuta múltiples queries agregadas, calcula métricas, retorna JSON con resultados |
| **Medida** | Tiempo de respuesta < 3 segundos, queries ejecutadas < 10 |

**Implementación Actual**:
- ✅ Queries optimizadas con agregaciones SQL
- ✅ Cálculos en backend (no frontend)
- ⚠️ No hay cache de métricas (mejora futura)

---

## 2. SECURITY (Seguridad)

### Escenario SEC-01: Autenticación de Usuario

| Aspecto | Descripción |
|---------|-------------|
| **ID** | SEC-01 |
| **Atributo** | Security |
| **Estímulo** | Usuario intenta autenticarse con email y password |
| **Fuente** | Usuario no autenticado |
| **Artefacto** | LoginHandler (ambos backends) |
| **Entorno** | Sistema en operación normal, acceso desde internet |
| **Respuesta** | Sistema valida credenciales, hash password, retorna token/sesión o rechaza con 401 |
| **Medida** | 100% de passwords hasheados, intentos de login auditados, bloqueo temporal después de 5 intentos fallidos |

**Implementación Actual**:
- ✅ Password hashing (implementado)
- ✅ Validación de credenciales
- ✅ HTTP 401 en fallo
- ⚠️ No hay rate limiting (mejora futura)
- ⚠️ No hay bloqueo temporal de cuenta

---

### Escenario SEC-02: Control de Acceso por Roles

| Aspecto | Descripción |
|---------|-------------|
| **ID** | SEC-02 |
| **Atributo** | Security |
| **Estímulo** | Usuario con rol "client" intenta acceder a ruta /admin/users |
| **Fuente** | Usuario autenticado con rol insuficiente |
| **Artefacto** | Vue Router Guards (ensurance/src/router.ts) |
| **Entorno** | Usuario autenticado, sesión válida |
| **Respuesta** | Sistema verifica rol en localStorage, redirige a /home con mensaje de acceso denegado |
| **Medida** | 100% de rutas admin protegidas, 0 accesos no autorizados detectados, redirección en < 100ms |

**Implementación Actual**:
- ✅ Route guards (requireAdmin, requireEmployeeOrAdmin)
- ✅ Verificación de rol en frontend
- ✅ Redirección automática
- ⚠️ Falta validación en backend (solo frontend)

---

### Escenario SEC-03: Protección de Datos Sensibles

| Aspecto | Descripción |
|---------|-------------|
| **ID** | SEC-03 |
| **Atributo** | Security |
| **Estímulo** | Sistema almacena información médica de usuario (prescripciones, citas) |
| **Fuente** | Usuario o empleado crea registro |
| **Artefacto** | DAOs (PrescriptionDAO, AppointmentDAO), SQLite DB |
| **Entorno** | Operación normal, datos persistidos |
| **Respuesta** | Datos almacenados en base de datos con permisos restrictivos, sin exposición en logs |
| **Medida** | 0 passwords en logs, archivos SQLite con permisos 600, datos sensibles no en URLs |

**Implementación Actual**:
- ✅ Passwords hasheados en BD
- ✅ SQLite files con permisos restrictivos
- ✅ No hay datos sensibles en query params
- ⚠️ No hay encriptación de BD (SQLite no encriptado)

---

## 3. AVAILABILITY (Disponibilidad)

### Escenario AVAIL-01: Recuperación de Fallo de Backend

| Aspecto | Descripción |
|---------|-------------|
| **ID** | AVAIL-01 |
| **Atributo** | Availability |
| **Estímulo** | Backend Java se cae por excepción no manejada o kill process |
| **Fuente** | Error interno o fallo de hardware |
| **Artefacto** | Docker container con Supervisor |
| **Entorno** | Producción (ambiente MAIN), usuarios activos |
| **Respuesta** | Supervisor detecta proceso caído, reinicia backend automáticamente |
| **Medida** | Tiempo de recuperación < 30 segundos, reinicio automático en 100% de casos |

**Implementación Actual**:
- ✅ Supervisor configurado con autorestart=true
- ✅ Health checks en docker-compose
- ✅ Logs persistidos para debugging

---

### Escenario AVAIL-02: Degradación Graceful en Fallo de BD

| Aspecto | Descripción |
|---------|-------------|
| **ID** | AVAIL-02 |
| **Atributo** | Availability |
| **Estímulo** | SQLite database bloqueada por escritura concurrente |
| **Fuente** | Múltiples requests simultáneos |
| **Artefacto** | Hibernate Session, DAOs |
| **Entorno** | Carga alta, operación normal |
| **Respuesta** | Sistema captura excepción, retorna HTTP 503 con mensaje claro, registra error en logs |
| **Medida** | 0% de crashes por DB lock, 100% de errores con respuesta HTTP apropiada, retry automático |

**Implementación Actual**:
- ✅ Try-catch en todos los DAOs
- ✅ Error handling en Handlers
- ✅ HTTP 503 o 500 con mensaje
- ⚠️ No hay retry automático

---

## 4. SCALABILITY (Escalabilidad)

### Escenario SCAL-01: Escalado Horizontal de Microservicios

| Aspecto | Descripción |
|---------|-------------|
| **ID** | SCAL-01 |
| **Atributo** | Scalability |
| **Estímulo** | Carga de usuarios aumenta 3x (de 30 a 90 usuarios concurrentes) |
| **Fuente** | Crecimiento de adopción, promociones |
| **Artefacto** | Pharmacy Backend (más carga por catálogo) |
| **Entorno** | Producción, servidor con recursos disponibles |
| **Respuesta** | Desplegar segunda instancia de Pharmacy Backend (puerto 8083), load balancer distribuye carga |
| **Medida** | Throughput aumenta proporcionalmente, tiempo de respuesta se mantiene < 2s |

**Preparación Actual**:
- ✅ Arquitectura stateless (sin sesiones en memoria)
- ✅ Microservicios independientes
- ⚠️ No hay load balancer configurado (mejora futura)
- ⚠️ SQLite no soporta escrituras concurrentes (limitación conocida)

---

### Escenario SCAL-02: Particionamiento de Base de Datos

| Aspecto | Descripción |
|---------|-------------|
| **ID** | SCAL-02 |
| **Atributo** | Scalability |
| **Estímulo** | Base de datos supera 100,000 registros, queries lentas |
| **Fuente** | Crecimiento de datos |
| **Artefacto** | SQLite databases (ensurance/USUARIO, pharmacy/USUARIO) |
| **Entorno** | Largo plazo, varios años de operación |
| **Respuesta** | Migrar a PostgreSQL con particionamiento por fecha, sharding por región |
| **Medida** | Migración sin downtime, queries mantienen performance < 2s |

**Preparación Actual**:
- ✅ Hibernate ORM facilita migración de BD
- ✅ Configuración por ambiente (perfiles Maven)
- ✅ Arquitectura permite cambio de BD
- ⚠️ No implementado (SQLite suficiente por ahora)

---

## 5. MAINTAINABILITY (Mantenibilidad)

### Escenario MAINT-01: Agregar Nuevo Endpoint REST

| Aspecto | Descripción |
|---------|-------------|
| **ID** | MAINT-01 |
| **Atributo** | Maintainability |
| **Estímulo** | Desarrollador necesita agregar nuevo endpoint GET /api/hospitals/{id}/services |
| **Fuente** | Nueva feature request |
| **Artefacto** | Ensurance Backend |
| **Entorno** | Desarrollo, branch feature |
| **Respuesta** | 1) Crear HospitalServicesHandler, 2) Registrar en ServerRoutes, 3) Reutilizar HospitalDAO, 4) Escribir tests |
| **Medida** | Tiempo de implementación < 2 horas, líneas de código < 100, 0 cambios en componentes no relacionados |

**Facilitadores Actuales**:
- ✅ Patrón claro (Handler → DAO → Entity)
- ✅ Registro centralizado (ServerRoutes)
- ✅ DAOs reutilizables
- ✅ Tests como guía

---

### Escenario MAINT-02: Refactorización sin Regresiones

| Aspecto | Descripción |
|---------|-------------|
| **ID** | MAINT-02 |
| **Atributo** | Maintainability |
| **Estímulo** | Desarrollador refactoriza UserDAO para mejorar performance |
| **Fuente** | Mejora de código técnico |
| **Artefacto** | UserDAO (ambos backends) |
| **Entorno** | Rama feature, tests existentes |
| **Respuesta** | Ejecutar tests automatizados (mvn test), verificar cobertura no disminuyó, SonarQube sin nuevos issues |
| **Medida** | 100% de tests pasan, cobertura mantiene > 70%, 0 bugs nuevos en SonarQube |

**Facilitadores Actuales**:
- ✅ Suite de tests completa (JUnit)
- ✅ Cobertura con JaCoCo
- ✅ SonarQube en CI/CD
- ✅ Quality Gates automáticos

---

## 6. USABILITY (Usabilidad)

### Escenario USAB-01: Registro de Usuario Intuitivo

| Aspecto | Descripción |
|---------|-------------|
| **ID** | USAB-01 |
| **Atributo** | Usability |
| **Estímulo** | Usuario nuevo intenta registrarse en el sistema |
| **Fuente** | Usuario sin cuenta previa |
| **Artefacto** | Ensurance/Pharmacy Frontend (Register.vue) |
| **Entorno** | Primera visita, dispositivo desktop |
| **Respuesta** | Formulario con validación inline, mensajes de error claros, confirmación visual de éxito |
| **Medida** | < 3 minutos para completar registro, < 10% tasa de abandono, 0 confusión reportada en feedback |

**Implementación Actual**:
- ✅ Validación de formularios (Vue)
- ✅ Mensajes de error descriptivos
- ✅ Confirmación visual
- ⚠️ No hay métricas de abandono (mejora futura)

---

### Escenario USAB-02: Navegación Consistente

| Aspecto | Descripción |
|---------|-------------|
| **ID** | USAB-02 |
| **Atributo** | Usability |
| **Estímulo** | Usuario navega entre diferentes secciones (Catálogo → Carrito → Checkout) |
| **Fuente** | Usuario autenticado |
| **Artefacto** | Pharmacy Frontend (Navigation component) |
| **Entorno** | Operación normal |
| **Respuesta** | Navbar siempre visible, breadcrumbs, indicador de página actual, acceso rápido a funciones comunes |
| **Medida** | 100% de páginas con navbar, < 3 clicks para cualquier función principal, 0 usuarios perdidos |

**Implementación Actual**:
- ✅ Componente Navigation consistente
- ✅ Vue Router con rutas nombradas
- ✅ Pinia state global (cart, user)

---

## 7. MODIFIABILITY (Modificabilidad)

### Escenario MODIF-01: Cambiar Lógica de Cálculo de Cobertura

| Aspecto | Descripción |
|---------|-------------|
| **ID** | MODIF-01 |
| **Atributo** | Modifiability |
| **Estímulo** | Cambio en reglas de negocio: cobertura ahora depende de tipo de medicamento |
| **Fuente** | Stakeholder (cambio de requisitos) |
| **Artefacto** | Pharmacy BillHandler, Ensurance PolicyDAO |
| **Entorno** | Desarrollo |
| **Respuesta** | Modificar solo BillHandler (método calculateCoverage), agregar campo coverage_type en Medicine |
| **Medida** | < 5 archivos modificados, 0 cambios en handlers no relacionados, tests actualizados en < 1 hora |

**Facilitadores**:
- ✅ Lógica de negocio centralizada en handlers
- ✅ Separación clara de responsabilidades
- ✅ ORM facilita cambios en BD

---

### Escenario MODIF-02: Agregar Nuevo Microservicio

| Aspecto | Descripción |
|---------|-------------|
| **ID** | MODIF-02 |
| **Atributo** | Modifiability |
| **Estímulo** | Requerimiento de nuevo módulo "Telemedicina" (videollamadas, historial médico) |
| **Fuente** | Nueva línea de negocio |
| **Artefacto** | Sistema completo |
| **Entorno** | Largo plazo (6+ meses) |
| **Respuesta** | Crear BackV6 independiente, nuevo frontend, integrar vía REST APIs, deploy en puertos 8084/8090 |
| **Medida** | 0 cambios en BackV4/V5 existentes (solo nuevos endpoints de integración), deployment independiente |

**Preparación**:
- ✅ Arquitectura de microservicios permite extensión
- ✅ Patrón consistente (copiar estructura BackV5)
- ✅ Docker Compose fácil de extender

---

## 8. TESTABILITY (Testeabilidad)

### Escenario TEST-01: Test Unitario de DAO

| Aspecto | Descripción |
|---------|-------------|
| **ID** | TEST-01 |
| **Atributo** | Testability |
| **Estímulo** | Desarrollador escribe test para UserDAO.findByEmail() |
| **Fuente** | TDD o testing post-implementación |
| **Artefacto** | UserDAO (backend), JUnit test |
| **Entorno** | Ambiente de testing, BD en memoria |
| **Respuesta** | Test crea usuario de prueba, llama findByEmail(), verifica resultado, cleanup automático |
| **Medida** | Test ejecuta en < 500ms, 100% aislado (no afecta otros tests), cobertura medida por JaCoCo |

**Implementación Actual**:
- ✅ JUnit 5 configurado
- ✅ Mockito para mocks
- ✅ Tests aislados (setup/teardown)
- ✅ JaCoCo en todos los módulos

---

### Escenario TEST-02: Test E2E de Flujo de Compra

| Aspecto | Descripción |
|---------|-------------|
| **ID** | TEST-02 |
| **Atributo** | Testability |
| **Estímulo** | QA Engineer ejecuta test end-to-end de flujo completo de compra |
| **Fuente** | Pipeline CI/CD en ambiente QA |
| **Artefacto** | Sistema completo (Frontend + Backend + BD) |
| **Entorno** | Ambiente QA (puertos 4000-4003) |
| **Respuesta** | Test automatizado: login → buscar medicamento → agregar a carrito → checkout → verificar factura |
| **Medida** | Test completo en < 2 minutos, 100% reproducible, 0 flaky tests |

**Preparación**:
- ⚠️ No hay tests E2E automatizados (mejora futura)
- ✅ Ambiente QA aislado disponible
- ✅ APIs REST fáciles de testear

---

## 9. INTEROPERABILITY (Interoperabilidad)

### Escenario INTEROP-01: Integración Ensurance-Pharmacy (Verificación de Póliza)

| Aspecto | Descripción |
|---------|-------------|
| **ID** | INTEROP-01 |
| **Atributo** | Interoperability |
| **Estímulo** | Usuario con seguro compra medicamentos en Pharmacy, sistema verifica cobertura |
| **Fuente** | Pharmacy Frontend/Backend |
| **Artefacto** | Pharmacy Backend (BillHandler) → Ensurance Backend (PolicyHandler) |
| **Entorno** | Operación normal, ambos servicios disponibles |
| **Respuesta** | Pharmacy llama GET /api/policy/{userId}, recibe JSON con percentage, aplica descuento en factura |
| **Medida** | Integración exitosa en 100% de casos, timeout si Ensurance no responde en 5s, fallback a 0% cobertura |

**Implementación Actual**:
- ✅ APIs REST bien definidas
- ✅ Formato JSON estándar
- ✅ CORS configurado
- ⚠️ No hay circuit breaker (mejora futura)

---

### Escenario INTEROP-02: Integración con SonarQube

| Aspecto | Descripción |
|---------|-------------|
| **ID** | INTEROP-02 |
| **Atributo** | Interoperability |
| **Estímulo** | Pipeline CI/CD ejecuta análisis de calidad |
| **Fuente** | GitHub Actions, Drone, Jenkins |
| **Artefacto** | sonar-scanner, SonarQube server |
| **Entorno** | CI/CD execution |
| **Respuesta** | Scanner envía reportes (JaCoCo XML, LCOV) a SonarQube vía REST API, espera Quality Gate |
| **Medida** | 100% de pipelines integrados, análisis exitoso en < 5 minutos, Quality Gate responde en < 30s |

**Implementación Actual**:
- ✅ 3 pipelines integrados con SonarQube
- ✅ Reportes de cobertura subidos automáticamente
- ✅ Quality Gates configurados
- ✅ Timeout de 5-10 minutos

---

## Resumen de Escenarios

| Atributo | # Escenarios | Prioridad | Cumplimiento Actual |
|----------|--------------|-----------|---------------------|
| Performance | 2 | Alta | 85% ✅ |
| Security | 3 | Alta | 75% ⚠️ |
| Availability | 2 | Media | 80% ✅ |
| Scalability | 2 | Media | 60% ⚠️ |
| Maintainability | 2 | Crítica | 95% ✅ |
| Usability | 2 | Media | 85% ✅ |
| Modifiability | 2 | Alta | 90% ✅ |
| Testability | 2 | Crítica | 90% ✅ |
| Interoperability | 2 | Media | 85% ✅ |
| **TOTAL** | **19** | - | **83%** ✅ |

---

## Próximo Documento

📄 **[analisis-add-decisiones.md](./analisis-add-decisiones.md)**
- Decisiones arquitectónicas detalladas por atributo
- Tácticas aplicadas
- Patrones de diseño
- Trade-offs y justificaciones
