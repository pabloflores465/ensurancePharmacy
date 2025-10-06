# Análisis ADD Completo - Ensurance Pharmacy System

## Attribute-Driven Design (ADD) - Documento Consolidado

**Sistema**: Ensurance Pharmacy (Seguros Médicos + Farmacia)  
**Metodología**: Attribute-Driven Design (ADD)  
**Atributos de Calidad**: 9 atributos principales  
**Escenarios**: 19 escenarios detallados  
**Decisiones**: 40+ tácticas arquitectónicas aplicadas  
**Cumplimiento Global**: 83% ✅

---

## 📋 TABLA DE CONTENIDOS

### PARTE I: RESUMEN EJECUTIVO Y METODOLOGÍA
1. [Introducción](#1-introducción)
   - 1.1 Contexto del Sistema
   - 1.2 Objetivo del Análisis ADD
   - 1.3 Alcance
2. [Metodología ADD](#2-metodología-add)
   - 2.1 Proceso ADD
   - 2.2 Aplicación al Sistema Ensurance Pharmacy
3. [Drivers Arquitectónicos](#3-drivers-arquitectónicos)
   - 3.1 Requisitos Funcionales Principales
   - 3.2 Restricciones (Constraints)
   - 3.3 Preocupaciones Arquitectónicas (Concerns)
4. [Atributos de Calidad Priorizados](#4-atributos-de-calidad-priorizados)
   - 4.1 Priorización de Atributos
   - 4.2 Trade-offs Identificados
5. [Decisiones Arquitectónicas de Alto Nivel](#5-decisiones-arquitectónicas-de-alto-nivel)
   - 5.1 Estilo Arquitectónico Principal
   - 5.2 Arquitectura Interna (Por Microservicio)
   - 5.3 Comunicación entre Servicios
   - 5.4 Persistencia de Datos
   - 5.5 Frontend Framework
6. [Pipeline CI/CD como Driver Arquitectónico](#6-pipeline-cicd-como-driver-arquitectónico)
   - 6.1 Decisión: Multi-Pipeline Strategy
   - 6.2 Impacto en Atributos de Calidad
   - 6.3 Características Comunes
7. [Ambientes Multi-Tenant](#7-ambientes-multi-tenant)
   - 7.1 Decisión: 3 Ambientes Aislados
   - 7.2 Impacto en Atributos
8. [Monitoreo y Observabilidad](#8-monitoreo-y-observabilidad)
   - 8.1 Stack de Monitoreo
   - 8.2 Impacto en Availability
9. [Resumen de Decisiones Clave](#9-resumen-de-decisiones-clave)
10. [Métricas de Éxito del Sistema](#10-métricas-de-éxito-del-sistema)
    - 10.1 Métricas Funcionales
    - 10.2 Métricas de Calidad

### PARTE II: ESCENARIOS DE CALIDAD (19 ESCENARIOS)
1. [PERFORMANCE - Rendimiento](#1-performance-rendimiento)
   - Escenario PERF-01: Búsqueda de Medicamentos
   - Escenario PERF-02: Generación de Dashboard
2. [SECURITY - Seguridad](#2-security-seguridad)
   - Escenario SEC-01: Autenticación de Usuario
   - Escenario SEC-02: Control de Acceso por Roles
   - Escenario SEC-03: Protección de Datos Sensibles
3. [AVAILABILITY - Disponibilidad](#3-availability-disponibilidad)
   - Escenario AVAIL-01: Recuperación de Fallo de Backend
   - Escenario AVAIL-02: Degradación Graceful en Fallo de BD
4. [SCALABILITY - Escalabilidad](#4-scalability-escalabilidad)
   - Escenario SCAL-01: Escalado Horizontal de Microservicios
   - Escenario SCAL-02: Particionamiento de Base de Datos
5. [MAINTAINABILITY - Mantenibilidad](#5-maintainability-mantenibilidad)
   - Escenario MAINT-01: Agregar Nuevo Endpoint REST
   - Escenario MAINT-02: Refactorización sin Regresiones
6. [USABILITY - Usabilidad](#6-usability-usabilidad)
   - Escenario USAB-01: Registro de Usuario Intuitivo
   - Escenario USAB-02: Navegación Consistente
7. [MODIFIABILITY - Modificabilidad](#7-modifiability-modificabilidad)
   - Escenario MODIF-01: Cambiar Lógica de Cálculo de Cobertura
   - Escenario MODIF-02: Agregar Nuevo Microservicio
8. [TESTABILITY - Testeabilidad](#8-testability-testeabilidad)
   - Escenario TEST-01: Test Unitario de DAO
   - Escenario TEST-02: Test E2E de Flujo de Compra
9. [INTEROPERABILITY - Interoperabilidad](#9-interoperability-interoperabilidad)
   - Escenario INTEROP-01: Integración Ensurance-Pharmacy
   - Escenario INTEROP-02: Integración con SonarQube

### PARTE III: DECISIONES ARQUITECTÓNICAS Y TÁCTICAS (40+ TÁCTICAS)
1. [PERFORMANCE - Rendimiento](#1-performance-rendimiento-1)
   - Decisión PERF-D01: Arquitectura Stateless
   - Decisión PERF-D02: Paginación de Resultados
   - Tácticas de Performance Aplicadas
2. [SECURITY - Seguridad](#2-security-seguridad-1)
   - Decisión SEC-D01: Autenticación Basada en Credenciales + Roles
   - Decisión SEC-D02: CORS Configurado para Desarrollo
   - Tácticas de Security Aplicadas
   - Mejoras de Seguridad Futuras
3. [AVAILABILITY - Disponibilidad](#3-availability-disponibilidad-1)
   - Decisión AVAIL-D01: Supervisor para Auto-Restart
   - Decisión AVAIL-D02: Health Checks en Docker
   - Tácticas de Availability Aplicadas
4. [SCALABILITY - Escalabilidad](#4-scalability-escalabilidad-1)
   - Decisión SCAL-D01: Microservicios Independientes
   - Decisión SCAL-D02: Hibernate ORM para Flexibilidad de BD
   - Tácticas de Scalability Aplicadas
5. [MAINTAINABILITY - Mantenibilidad](#5-maintainability-mantenibilidad-1)
   - Decisión MAINT-D01: Layered Architecture Estricta
   - Decisión MAINT-D02: Repository Pattern para Acceso a Datos
   - Tácticas de Maintainability Aplicadas
6. [USABILITY - Usabilidad](#6-usability-usabilidad-1)
   - Decisión USAB-D01: Vue 3 con Componentes Reutilizables
   - Decisión USAB-D02: Tailwind CSS para Styling
   - Tácticas de Usability Aplicadas
7. [MODIFIABILITY - Modificabilidad](#7-modifiability-modificabilidad-1)
   - Decisión MODIF-D01: Separación Frontend-Backend
   - Decisión MODIF-D02: Configuration External
   - Tácticas de Modifiability Aplicadas
8. [TESTABILITY - Testeabilidad](#8-testability-testeabilidad-1)
   - Decisión TEST-D01: CI/CD con Tests Automatizados
   - Decisión TEST-D02: Dependency Injection Manual
   - Tácticas de Testability Aplicadas
9. [INTEROPERABILITY - Interoperabilidad](#9-interoperability-interoperabilidad-1)
   - Decisión INTEROP-D01: REST APIs Estándar
   - Decisión INTEROP-D02: SonarQube Integration en 3 Pipelines
   - Tácticas de Interoperability Aplicadas

### PARTE IV: DIAGRAMAS ADD (PlantUML)
1. Diagrama: Drivers Arquitectónicos
2. Diagrama: Refinamiento Iteración 1 (Microservices)
3. Diagrama: Refinamiento Iteración 2 (Layered Architecture)
4. Diagrama: Tácticas por Atributo
5. Diagrama: Mapping Decisiones → Atributos
6. Diagrama: Pipeline CI/CD como Driver de Calidad
7. Diagrama: Trade-offs Principales
8. Diagrama: Cumplimiento de Escenarios
9. Diagrama: Arquitectura Final Completa

---

# PARTE I: RESUMEN EJECUTIVO Y METODOLOGÍA

---

## 1. Introducción

### 1.1 Contexto del Sistema

**Ensurance Pharmacy** es un sistema integrado desarrollado durante el semestre pasado que combina:

- **Sistema de Seguros Médicos (Ensurance)**: Gestión de pólizas, citas médicas, hospitales y aprobaciones
- **Sistema de Farmacia (Pharmacy)**: Catálogo de medicamentos, pedidos, facturación y prescripciones

El sistema fue diseñado con una arquitectura de microservicios, implementando prácticas modernas de DevOps y CI/CD.

### 1.2 Objetivo del Análisis ADD

Este análisis utiliza la metodología **Attribute-Driven Design (ADD)** para:

1. **Identificar drivers arquitectónicos**: Requisitos funcionales, restricciones y atributos de calidad
2. **Definir escenarios de calidad**: Especificaciones medibles para cada atributo
3. **Documentar decisiones arquitectónicas**: Justificación de elecciones técnicas
4. **Validar tácticas aplicadas**: Patrones y estrategias implementadas

### 1.3 Alcance

El análisis cubre:

✅ **9 Atributos de Calidad principales**:
1. Performance (Rendimiento)
2. Security (Seguridad)
3. Availability (Disponibilidad)
4. Scalability (Escalabilidad)
5. Maintainability (Mantenibilidad)
6. Usability (Usabilidad)
7. Modifiability (Modificabilidad)
8. Testability (Testeabilidad)
9. Interoperability (Interoperabilidad)

✅ **Pipeline CI/CD completo**: GitHub Actions, Drone CI, Jenkins

✅ **Sistema desarrollado**: Arquitectura, componentes, integraciones

---

## 2. Metodología ADD

### 2.1 Proceso ADD

La metodología ADD sigue un proceso iterativo de 7 pasos:

```
1. Review Inputs
   ↓
2. Establish Iteration Goal
   ↓
3. Choose Elements to Refine
   ↓
4. Choose Design Concepts
   ↓
5. Instantiate Architectural Elements
   ↓
6. Sketch Views and Record Design Decisions
   ↓
7. Perform Analysis of Current Design
   ↓
Iterate →
```

### 2.2 Aplicación al Sistema Ensurance Pharmacy

#### **Iteración 1: Arquitectura de Alto Nivel**
- **Goal**: Establecer estructura general del sistema
- **Inputs**: Requisitos funcionales, restricciones tecnológicas
- **Output**: Arquitectura de microservicios con 2 servicios principales

#### **Iteración 2: Refinamiento de Componentes**
- **Goal**: Definir estructura interna de cada microservicio
- **Inputs**: Atributos de calidad prioritarios
- **Output**: Arquitectura en capas (Layered Architecture)

#### **Iteración 3: Infraestructura y DevOps**
- **Goal**: Diseñar pipeline CI/CD y deployment
- **Inputs**: Requisitos de disponibilidad, escalabilidad, mantenibilidad
- **Output**: Multi-pipeline CI/CD, Docker multi-ambiente, monitoreo

---

## 3. Drivers Arquitectónicos

### 3.1 Requisitos Funcionales Principales

| ID | Requisito Funcional | Prioridad | Sistema |
|----|---------------------|-----------|---------|
| RF-01 | Gestión de usuarios (CRUD, autenticación, roles) | Alta | Ambos |
| RF-02 | Gestión de pólizas de seguro | Alta | Ensurance |
| RF-03 | Registro y búsqueda de hospitales | Alta | Ensurance |
| RF-04 | Sistema de citas médicas | Alta | Ensurance |
| RF-05 | Catálogo de medicamentos con categorización | Alta | Pharmacy |
| RF-06 | Carrito de compras y pedidos | Alta | Pharmacy |
| RF-07 | Facturación con integración de seguros | Alta | Pharmacy |
| RF-08 | Gestión de prescripciones médicas | Alta | Ambos |
| RF-09 | Workflow de aprobaciones | Media | Ambos |
| RF-10 | Dashboard con métricas y estadísticas | Media | Ambos |
| RF-11 | Sistema de comentarios y ratings | Baja | Pharmacy |
| RF-12 | Integración entre Ensurance y Pharmacy | Alta | Integración |

### 3.2 Restricciones (Constraints)

| ID | Restricción | Tipo | Impacto |
|----|-------------|------|---------|
| C-01 | Tecnología Backend: Java 21 | Técnica | Alto |
| C-02 | Tecnología Frontend: Vue 3 | Técnica | Alto |
| C-03 | Base de datos: SQLite (desarrollo y producción) | Técnica | Alto |
| C-04 | Sin infraestructura cloud (self-hosted) | Infraestructura | Alto |
| C-05 | Presupuesto limitado (proyecto académico) | Económica | Medio |
| C-06 | Tiempo de desarrollo: 1 semestre | Temporal | Alto |
| C-07 | Equipo pequeño (1-3 desarrolladores) | Organizacional | Medio |
| C-08 | Soporte ARM64 (MacBook) | Técnica | Medio |

### 3.3 Preocupaciones Arquitectónicas (Concerns)

#### **Separación de Concerns**
- Microservicios independientes (Ensurance vs Pharmacy)
- Arquitectura en capas (Presentation, Application, Business, Data)
- Separación Frontend/Backend

#### **Integrabilidad**
- APIs REST para comunicación
- Integración entre servicios (verificación de pólizas, prescripciones)
- Integración con SonarQube, Codecov

#### **Evolución**
- Sistema debe soportar nuevos módulos
- Nuevos tipos de servicios médicos
- Nuevas categorías de medicamentos

---

## 4. Atributos de Calidad Priorizados

### 4.1 Priorización de Atributos

| Atributo | Prioridad | Razón | Driver Principal |
|----------|-----------|-------|------------------|
| **Maintainability** | 🔴 Crítica | Proyecto académico con evolución continua | Código limpio, patrones |
| **Testability** | 🔴 Crítica | Calidad del código, CI/CD automático | Cobertura, tests automatizados |
| **Modifiability** | 🟡 Alta | Requisitos cambiantes, nuevas features | Arquitectura flexible |
| **Security** | 🟡 Alta | Datos médicos sensibles, autenticación | Protección de datos |
| **Performance** | 🟡 Alta | Experiencia de usuario, catálogos grandes | Respuesta < 2s |
| **Availability** | 🟢 Media | Sistema académico, no crítico 24/7 | Uptime 95% |
| **Scalability** | 🟢 Media | Crecimiento futuro, carga variable | Arquitectura escalable |
| **Interoperability** | 🟢 Media | Integración entre servicios | APIs REST estándar |
| **Usability** | 🟢 Media | Usuarios finales diversos | UI intuitiva |

### 4.2 Trade-offs Identificados

#### **Performance vs Maintainability**
- **Decisión**: Priorizar mantenibilidad sobre optimización prematura
- **Razón**: Sistema académico, más importante código limpio que performance extremo
- **Implementación**: Patrones claros, código legible, optimización solo cuando necesario

#### **Scalability vs Complexity**
- **Decisión**: Arquitectura escalable sin over-engineering
- **Razón**: Balancear preparación futura con simplicidad actual
- **Implementación**: Microservicios (2), no más división innecesaria

#### **Security vs Usability**
- **Decisión**: Seguridad básica sin fricciones excesivas
- **Razón**: Balance entre protección y experiencia de usuario
- **Implementación**: Autenticación simple, permisos por roles

---

## 5. Decisiones Arquitectónicas de Alto Nivel

### 5.1 Estilo Arquitectónico Principal

**Decisión**: **Microservices Architecture**

**Razón**:
- ✅ Separación de dominios (Seguros vs Farmacia)
- ✅ Despliegue independiente
- ✅ Escalabilidad selectiva
- ✅ Desarrollo en paralelo
- ✅ Tecnologías heterogéneas posibles

**Trade-offs**:
- ❌ Mayor complejidad operacional
- ❌ Requiere orquestación (Docker Compose)
- ❌ Overhead de comunicación (REST)

### 5.2 Arquitectura Interna (Por Microservicio)

**Decisión**: **Layered Architecture (4 capas)**

**Capas**:
1. **Presentation Layer**: Vue 3 Frontend
2. **Application Layer**: HTTP Handlers
3. **Business Logic Layer**: DAOs + Domain Logic
4. **Data Access Layer**: Hibernate ORM + SQLite

**Razón**:
- ✅ Separación de responsabilidades clara
- ✅ Testabilidad por capa
- ✅ Mantenibilidad alta
- ✅ Patrón bien conocido

### 5.3 Comunicación entre Servicios

**Decisión**: **REST APIs sobre HTTP**

**Implementación**:
- Ensurance Backend: `/api/*` (puerto 8081)
- Pharmacy Backend: `/api2/*` (puerto 8082)
- Formato: JSON
- Protocolo: HTTP/1.1

**Razón**:
- ✅ Estándar universal
- ✅ Stateless
- ✅ Fácil debugging
- ✅ Compatible con cualquier cliente

### 5.4 Persistencia de Datos

**Decisión**: **SQLite con Hibernate ORM**

**Justificación**:
- ✅ No requiere servidor separado (self-contained)
- ✅ File-based (fácil backup y migración)
- ✅ Suficiente para carga esperada
- ✅ Zero-configuration
- ✅ Soporte ACID completo

**Limitaciones Conocidas**:
- ❌ No soporta alta concurrencia (lock de base de datos)
- ❌ No escalable horizontalmente
- ❌ Menos robusto que PostgreSQL/MySQL

**Mitigación**:
- 🔧 Separar bases de datos por servicio
- 🔧 Separar por ambiente (dev/qa/main)
- 🔧 Diseñado para migración futura a PostgreSQL si es necesario

### 5.5 Frontend Framework

**Decisión**: **Vue 3 con diferentes enfoques**

**Ensurance Frontend**:
- Vue 3 Composition API
- TypeScript
- Vite (build tool)

**Pharmacy Frontend**:
- Vue 3 Options API
- JavaScript
- Vue CLI

**Razón**:
- ✅ Consistencia de framework (ambos Vue 3)
- ✅ Experimentación con diferentes APIs
- ✅ Flexibilidad en tooling
- ✅ Ecosistema rico (Pinia, Vue Router)

---

## 6. Pipeline CI/CD como Driver Arquitectónico

### 6.1 Decisión: Multi-Pipeline Strategy

**Implementación**: 3 pipelines paralelos

1. **GitHub Actions** (Cloud-based)
2. **Drone CI** (Self-hosted, ARM64)
3. **Jenkins** (Self-hosted, tradicional)

**Razón**:
- ✅ Redundancia (si uno falla, otros funcionan)
- ✅ Aprendizaje de diferentes plataformas
- ✅ Flexibilidad según necesidades
- ✅ Comparación de enfoques

### 6.2 Impacto en Atributos de Calidad

| Atributo | Impacto | Cómo |
|----------|---------|------|
| **Testability** | Alto | Tests automatizados en cada push |
| **Maintainability** | Alto | SonarQube en cada pipeline |
| **Availability** | Medio | Deployment automatizado |
| **Security** | Medio | Análisis de vulnerabilidades |
| **Modifiability** | Alto | Feedback rápido en cambios |

### 6.3 Características Comunes

Todos los pipelines ejecutan:
- ✅ Tests unitarios (JUnit, Vitest, Jest)
- ✅ Cobertura de código (JaCoCo, LCOV)
- ✅ Análisis SonarQube (4 proyectos)
- ✅ Quality Gates
- ✅ Notificaciones (email)
- ✅ Deployment condicional (por ambiente)

---

## 7. Ambientes Multi-Tenant

### 7.1 Decisión: 3 Ambientes Aislados

**Implementación**:
- **DEV**: Puertos 3000-3003, rama `dev`
- **QA**: Puertos 4000-4003, rama `qa`
- **MAIN**: Puertos 5175, 8089, 8081, 8082, rama `main`

**Razón**:
- ✅ Aislamiento de datos por ambiente
- ✅ Testing sin afectar producción
- ✅ Deployment gradual (dev → qa → main)
- ✅ Configuración específica por ambiente

### 7.2 Impacto en Atributos

| Atributo | Beneficio |
|----------|-----------|
| **Testability** | Tests en QA sin riesgo |
| **Availability** | Producción no afectada por pruebas |
| **Modifiability** | Cambios validados antes de producción |
| **Security** | Datos de producción aislados |

---

## 8. Monitoreo y Observabilidad

### 8.1 Stack de Monitoreo

**Implementación**:
- **Checkmk**: Monitoreo de infraestructura
- **Prometheus**: Scraping de métricas
- **Grafana**: Visualización de dashboards

**Razón**:
- ✅ Visibilidad del sistema en tiempo real
- ✅ Detección temprana de problemas
- ✅ Análisis histórico de métricas

### 8.2 Impacto en Availability

- 🔍 Detectar caídas de servicio
- 🔍 Monitorear uso de recursos
- 🔍 Alertas proactivas

---

## 9. Resumen de Decisiones Clave

| # | Decisión | Atributos Impactados | Trade-off |
|---|----------|---------------------|-----------|
| 1 | Microservices (2 servicios) | Scalability, Modifiability, Maintainability | Complejidad vs Flexibilidad |
| 2 | Layered Architecture interna | Maintainability, Testability | Simplicidad vs Separación |
| 3 | REST APIs | Interoperability, Modifiability | Performance vs Estándar |
| 4 | SQLite + Hibernate | Usability (setup), Modifiability | Scalability vs Simplicidad |
| 5 | Vue 3 | Usability, Maintainability | Curva aprendizaje vs Productividad |
| 6 | Multi-Pipeline CI/CD | Testability, Maintainability, Availability | Costo setup vs Calidad |
| 7 | Multi-Ambiente (dev/qa/main) | Testability, Availability, Security | Recursos vs Aislamiento |
| 8 | Docker Compose | Availability, Scalability | Orquestación simple vs Kubernetes |
| 9 | Monitoreo (Prometheus/Grafana) | Availability, Performance | Overhead vs Visibilidad |
| 10 | SonarQube Quality Gates | Maintainability, Security | Tiempo build vs Calidad |

---

## 10. Métricas de Éxito del Sistema

### 10.1 Métricas Funcionales

| Métrica | Objetivo | Actual |
|---------|----------|--------|
| Endpoints REST | 80+ | 90+ ✅ |
| Tablas de BD | 20+ | 25 ✅ |
| Cobertura Backend | >70% | ~75% ✅ |
| Cobertura Frontend | >60% | ~65% ✅ |
| Quality Gates Pass | 100% | 100% ✅ |

### 10.2 Métricas de Calidad

| Atributo | Métrica | Target | Actual |
|----------|---------|--------|--------|
| Performance | Response time | <2s | ~1.5s ✅ |
| Availability | Uptime | >95% | ~98% ✅ |
| Maintainability | Bugs SonarQube | <50 | ~30 ✅ |
| Security | Vulnerabilidades | 0 críticas | 0 ✅ |
| Testability | Tests automatizados | >200 | ~250 ✅ |

---

## 11. Próximos Pasos

Para el análisis detallado de cada aspecto, continuar con:

📄 **[analisis-add-escenarios.md](./analisis-add-escenarios.md)**
- Escenarios detallados para los 9 atributos de calidad
- Formato: Estímulo, Entorno, Respuesta, Métrica

📄 **[analisis-add-decisiones.md](./analisis-add-decisiones.md)**
- Decisiones arquitectónicas por atributo
- Tácticas aplicadas
- Patrones implementados
- Trade-offs justificados

📊 **[analisis-add-diagramas.plantuml](./analisis-add-diagramas.plantuml)**
- Diagramas ADD
- Vistas arquitectónicas
- Refinamiento de componentes

---

## 12. Conclusión

El sistema **Ensurance Pharmacy** fue diseñado con una aproximación consciente de atributos de calidad, priorizando:

🎯 **Mantenibilidad y Testabilidad** como drivers principales (proyecto académico evolutivo)

🎯 **Arquitectura clara y bien documentada** (microservicios + capas)

🎯 **CI/CD robusto** (3 pipelines paralelos)

🎯 **Multi-ambiente** (dev/qa/main)

Las decisiones arquitectónicas están **justificadas por atributos de calidad específicos** y documentadas con sus **trade-offs explícitos**.

El siguiente documento detalla los **escenarios de calidad concretos y medibles** para cada uno de los 9 atributos.


---

# PARTE II: ESCENARIOS DE CALIDAD (19 ESCENARIOS)

---

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


---

# PARTE III: DECISIONES ARQUITECTÓNICAS Y TÁCTICAS (40+ TÁCTICAS)

---

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


---

# PARTE IV: DIAGRAMAS ADD (9 DIAGRAMAS PlantUML)

---

# DIAGRAMAS ADD (PlantUML)

A continuación se presentan 9 diagramas PlantUML que visualizan el análisis ADD completo. Cada diagrama puede ser renderizado usando herramientas como PlantUML online, extensiones de VS Code, o plugins de IntelliJ.

```plantuml
@startuml Análisis ADD - Diagramas Arquitectónicos

title Análisis ADD - Ensurance Pharmacy System\nRefinamiento Arquitectónico y Tácticas

' ========================================
' Diagrama 1: Drivers Arquitectónicos
' ========================================

package "Drivers Arquitectónicos" as drivers {
  
  rectangle "Requisitos Funcionales" as rf {
    - Gestión de usuarios (CRUD)
    - Gestión de pólizas
    - Sistema de citas médicas
    - Catálogo de medicamentos
    - Carrito y pedidos
    - Facturación con seguros
    - Prescripciones
    - Aprobaciones
    - Dashboards
    - Integración Ensurance-Pharmacy
  }
  
  rectangle "Restricciones" as constraints {
    - Java 21 (backend)
    - Vue 3 (frontend)
    - SQLite (database)
    - Self-hosted
    - Presupuesto limitado
    - 1 semestre
    - ARM64 support
  }
  
  rectangle "Atributos de Calidad" as qa {
    **CRÍTICOS:**
    - Maintainability
    - Testability
    
    **ALTOS:**
    - Modifiability
    - Security
    - Performance
    
    **MEDIOS:**
    - Availability
    - Scalability
    - Interoperability
    - Usability
  }
}

note right of qa
  **Priorización basada en:**
  - Proyecto académico evolutivo
  - Calidad de código crítica
  - Requisitos cambiantes
  - Datos médicos sensibles
end note

@enduml

@startuml Refinamiento Iteración 1

title ADD Iteración 1: Estructura de Alto Nivel

skinparam componentStyle rectangle

package "Decisión: Microservices Architecture" as decision1 {
  
  component "Ensurance Service" as ens_service {
    [Frontend Vue 3 + TypeScript]
    [Backend Java 21]
    database "SQLite\nensurance/USUARIO"
  }
  
  component "Pharmacy Service" as pharm_service {
    [Frontend Vue 3 + JavaScript]
    [Backend Java 21 ]
    database "SQLite\npharmacy/USUARIO"
  }
  
  ens_service <--> pharm_service : REST APIs\nJSON over HTTP
}

note right of decision1
  **Atributos Impactados:**
  ✅ Scalability (escalar independiente)
  ✅ Modifiability (servicios separados)
  ✅ Maintainability (dominios claros)
  
  **Trade-off:**
  + Flexibilidad y escalabilidad
  - Complejidad operacional
  - Overhead de comunicación
end note

@enduml

@startuml Refinamiento Iteración 2

title ADD Iteración 2: Arquitectura Interna (Layered)

skinparam componentStyle rectangle

package "Backend Service (BackV4/BackV5)" {
  
  package "Presentation Layer" as presentation {
    rectangle "HttpServer\n(Java)" as httpserver
    rectangle "HTTP Handlers (28/21)" as handlers
  }
  
  package "Business Logic Layer" as business {
    rectangle "DAOs (23/19)" as daos
    rectangle "Business Rules" as rules
  }
  
  package "Data Access Layer" as data {
    rectangle "Hibernate ORM" as hibernate
    rectangle "JPA Entities (25/20)" as entities
  }
  
  package "Database Layer" as db {
    database "SQLite" as sqlite
  }
  
  httpserver --> handlers
  handlers --> daos
  handlers --> rules
  daos --> hibernate
  hibernate --> entities
  entities --> sqlite
}

note right of presentation
  **Atributos Impactados:**
  ✅ Maintainability (separación clara)
  ✅ Testability (capas aisladas)
  ✅ Modifiability (cambios localizados)
  
  **Tácticas:**
  - Increase Semantic Coherence
  - Reduce Coupling
  - Abstract Common Services
end note

@enduml

@startuml Tácticas por Atributo

title Tácticas Arquitectónicas Aplicadas por Atributo

left to right direction

package "PERFORMANCE" as perf {
  rectangle "Stateless Services" as t_stateless
  rectangle "Paginación" as t_pagination
  rectangle "Concurrency (CI/CD)" as t_concurrency
  rectangle "Query Optimization" as t_query
}

package "SECURITY" as sec {
  rectangle "Authenticate Users" as t_auth
  rectangle "Authorize (RBAC)" as t_authz
  rectangle "Password Hashing" as t_hash
  rectangle "CORS Config" as t_cors
  rectangle "Input Validation" as t_validate
}

package "AVAILABILITY" as avail {
  rectangle "Exception Handling" as t_exception
  rectangle "Auto-restart (Supervisor)" as t_restart
  rectangle "Health Checks" as t_health
  rectangle "Multiple Environments" as t_env
  rectangle "Logging" as t_log
}

package "SCALABILITY" as scal {
  rectangle "Service Partitioning" as t_partition
  rectangle "Stateless Design" as t_stateless2
  rectangle "ORM Abstraction" as t_orm
  rectangle "Data Separation" as t_data_sep
}

package "MAINTAINABILITY" as maint {
  rectangle "Layered Architecture" as t_layers
  rectangle "Repository Pattern" as t_repo
  rectangle "Dependency Injection" as t_di
  rectangle "Configuration Files" as t_config
  rectangle "Documentation" as t_doc
}

package "TESTABILITY" as test {
  rectangle "CI/CD Automation" as t_cicd
  rectangle "Unit Tests (JUnit)" as t_junit
  rectangle "Test Isolation" as t_isolation
  rectangle "Mocking (Mockito)" as t_mock
  rectangle "Code Coverage (JaCoCo)" as t_coverage
}

package "INTEROPERABILITY" as interop {
  rectangle "REST APIs" as t_rest
  rectangle "JSON Standard" as t_json
  rectangle "HTTP Protocols" as t_http
  rectangle "SonarQube Integration" as t_sonar
}

package "MODIFIABILITY" as modif {
  rectangle "Frontend/Backend Separation" as t_separation
  rectangle "External Config" as t_ext_config
  rectangle "Encapsulation" as t_encaps
  rectangle "Defer Binding" as t_defer
}

package "USABILITY" as usab {
  rectangle "Vue Components" as t_vue
  rectangle "Form Validation" as t_form_val
  rectangle "Consistent Navigation" as t_nav
  rectangle "Feedback Messages" as t_feedback
}

note bottom
  **Total: 40+ tácticas aplicadas**
  distribuidas entre 9 atributos de calidad
end note

@enduml

@startuml Mapping Decisiones a Atributos

title Mapping: Decisiones Arquitectónicas → Atributos de Calidad

skinparam backgroundColor #FEFEFE

rectangle "Decisión 1:\nMicroservices" as d1
rectangle "Decisión 2:\nLayered Architecture" as d2
rectangle "Decisión 3:\nREST APIs" as d3
rectangle "Decisión 4:\nSQLite + Hibernate" as d4
rectangle "Decisión 5:\nVue 3" as d5
rectangle "Decisión 6:\nMulti-Pipeline CI/CD" as d6
rectangle "Decisión 7:\nMulti-Ambiente" as d7
rectangle "Decisión 8:\nDocker Compose" as d8
rectangle "Decisión 9:\nSupervisor" as d9
rectangle "Decisión 10:\nRBAC" as d10

rectangle "Scalability" as qa_scal
rectangle "Modifiability" as qa_modif
rectangle "Maintainability" as qa_maint
rectangle "Interoperability" as qa_interop
rectangle "Usability" as qa_usab
rectangle "Testability" as qa_test
rectangle "Availability" as qa_avail
rectangle "Performance" as qa_perf
rectangle "Security" as qa_sec

d1 --> qa_scal : ++
d1 --> qa_modif : ++
d1 --> qa_maint : +

d2 --> qa_maint : +++
d2 --> qa_test : ++
d2 --> qa_modif : +

d3 --> qa_interop : +++
d3 --> qa_modif : ++

d4 --> qa_modif : ++
d4 --> qa_scal : +

d5 --> qa_usab : +++
d5 --> qa_maint : +

d6 --> qa_test : +++
d6 --> qa_maint : ++
d6 --> qa_avail : +

d7 --> qa_test : ++
d7 --> qa_avail : ++
d7 --> qa_sec : +

d8 --> qa_avail : ++
d8 --> qa_scal : +

d9 --> qa_avail : +++

d10 --> qa_sec : +++

note bottom
  **Leyenda:**
  +++ = Impacto muy alto
  ++ = Impacto alto
  + = Impacto medio
end note

@enduml

@startuml Pipeline como Driver Arquitectónico

title Pipeline CI/CD como Driver de Calidad

left to right direction

package "3 Pipelines CI/CD" {
  rectangle "GitHub Actions" as gh
  rectangle "Drone CI" as drone
  rectangle "Jenkins" as jenkins
}

rectangle "Tests Automatizados" as tests {
  - Backend V4 (JUnit)
  - Backend V5 (JUnit)
  - Frontend Ensurance (Vitest)
  - Frontend Pharmacy (Jest)
}

rectangle "SonarQube Analysis" as sonar {
  - 4 proyectos
  - Quality Gates
  - Cobertura >70%
}

rectangle "Deployment" as deploy {
  - DEV (auto)
  - QA (auto)
  - MAIN (auto)
}

gh --> tests
drone --> tests
jenkins --> tests

tests --> sonar
sonar --> deploy

rectangle "Atributos Mejorados" as improved {
  **Testability** (crítico)
  - 100% de código testeado en CI
  - Cobertura medida automáticamente
  
  **Maintainability** (crítico)
  - Quality Gates bloquean código malo
  - Análisis en cada push
  
  **Availability** (medio)
  - Deployment automatizado
  - Menos errores humanos
  
  **Security** (alto)
  - Análisis de vulnerabilidades
  - Dependency scanning
}

deploy --> improved

note right of improved
  **Pipeline = Enabler de Calidad**
  
  Sin CI/CD:
  - Tests manuales (error-prone)
  - Deployment manual (riesgoso)
  - Sin métricas de calidad
  
  Con CI/CD:
  - Tests automáticos (confiables)
  - Deployment seguro (reproducible)
  - Calidad medida (SonarQube)
end note

@enduml

@startuml Trade-offs Principales

title Trade-offs en Decisiones Arquitectónicas

left to right direction

rectangle "Microservices\nvs\nMonolito" as to1 {
  **Elegido: Microservices**
  
  ✅ Ventajas:
  - Escalabilidad independiente
  - Despliegue por servicio
  - Tecnologías heterogéneas
  
  ❌ Desventajas:
  - Más complejidad operacional
  - Overhead de comunicación
  - Requiere orquestación
  
  **Justificación:**
  2 dominios claramente separados
  (Seguros vs Farmacia)
}

rectangle "SQLite\nvs\nPostgreSQL" as to2 {
  **Elegido: SQLite**
  
  ✅ Ventajas:
  - Zero configuration
  - File-based (fácil backup)
  - Suficiente para carga actual
  - No requiere servidor
  
  ❌ Desventajas:
  - Limitaciones de concurrencia
  - No escalable horizontalmente
  - Menos robusto
  
  **Justificación:**
  Proyecto académico, self-hosted,
  Hibernate permite migración futura
}

rectangle "Multi-Pipeline\nvs\nSingle Pipeline" as to3 {
  **Elegido: 3 Pipelines**
  
  ✅ Ventajas:
  - Redundancia (fallo de uno)
  - Aprendizaje de plataformas
  - Flexibilidad
  
  ❌ Desventajas:
  - Mantenimiento 3x
  - Configuración duplicada
  - Más complejidad
  
  **Justificación:**
  Proyecto académico (aprendizaje),
  Cada pipeline tiene ventajas únicas
}

rectangle "Tailwind\nvs\nBootstrap" as to4 {
  **Elegido: Tailwind (Ensurance)**
  
  ✅ Ventajas:
  - Utility-first (rápido)
  - Customizable
  - Moderno
  
  ❌ Desventajas:
  - Curva de aprendizaje
  - HTML más verboso
  
  **Justificación:**
  Desarrollo rápido,
  Design system consistente
}

note bottom
  **Filosofía de Trade-offs:**
  
  Priorizamos:
  1. Mantenibilidad > Performance prematura
  2. Simplicidad > Over-engineering
  3. Aprendizaje > Solución única
  4. Flexibilidad futura > Optimización actual
end note

@enduml

@startuml Cumplimiento de Escenarios

title Cumplimiento de Escenarios de Calidad

left to right direction

rectangle "Performance" as perf_qa {
  **Escenarios: 2**
  - PERF-01: Búsqueda < 1.5s ✅
  - PERF-02: Dashboard < 3s ✅
  
  **Cumplimiento: 85%**
  Falta: Cache de métricas
}

rectangle "Security" as sec_qa {
  **Escenarios: 3**
  - SEC-01: Autenticación ✅
  - SEC-02: RBAC (frontend) ⚠️
  - SEC-03: Datos sensibles ✅
  
  **Cumplimiento: 75%**
  Falta: Rate limiting, RBAC backend
}

rectangle "Availability" as avail_qa {
  **Escenarios: 2**
  - AVAIL-01: Auto-restart ✅
  - AVAIL-02: Error handling ✅
  
  **Cumplimiento: 80%**
  Falta: Retry automático
}

rectangle "Scalability" as scal_qa {
  **Escenarios: 2**
  - SCAL-01: Escalado horizontal ⚠️
  - SCAL-02: Migración BD ⚠️
  
  **Cumplimiento: 60%**
  Preparado pero no implementado
}

rectangle "Maintainability" as maint_qa {
  **Escenarios: 2**
  - MAINT-01: Nuevo endpoint ✅
  - MAINT-02: Refactoring seguro ✅
  
  **Cumplimiento: 95%**
  Excelente estructura
}

rectangle "Testability" as test_qa {
  **Escenarios: 2**
  - TEST-01: Unit tests ✅
  - TEST-02: E2E tests ⚠️
  
  **Cumplimiento: 90%**
  Falta: Tests E2E automatizados
}

rectangle "Usability" as usab_qa {
  **Escenarios: 2**
  - USAB-01: Registro intuitivo ✅
  - USAB-02: Navegación consistente ✅
  
  **Cumplimiento: 85%**
  Falta: Métricas de UX
}

rectangle "Modifiability" as modif_qa {
  **Escenarios: 2**
  - MODIF-01: Cambio de lógica ✅
  - MODIF-02: Nuevo microservicio ✅
  
  **Cumplimiento: 90%**
  Arquitectura flexible
}

rectangle "Interoperability" as interop_qa {
  **Escenarios: 2**
  - INTEROP-01: Integración E-P ✅
  - INTEROP-02: SonarQube ✅
  
  **Cumplimiento: 85%**
  Falta: Circuit breaker
}

note bottom
  **Cumplimiento Global: 83%**
  
  Fortalezas:
  ✅ Maintainability (95%)
  ✅ Testability (90%)
  ✅ Modifiability (90%)
  
  Áreas de mejora:
  ⚠️ Scalability (60%)
  ⚠️ Security (75%)
end note

@enduml

@startuml Arquitectura Final

title Arquitectura Final - Vista Completa

skinparam componentStyle rectangle
skinparam backgroundColor #FEFEFE

actor "Usuario" as user

package "Ambiente DEV (3000-3003)" as dev {
  [Ensurance Frontend :3000]
  [Pharmacy Frontend :3001]
  [Ensurance Backend :3002]
  [Pharmacy Backend :3003]
  database "SQLite DEV"
}

package "Ambiente QA (4000-4003)" as qa {
  [Ensurance Frontend :4000]
  [Pharmacy Frontend :4001]
  [Ensurance Backend :4002]
  [Pharmacy Backend :4003]
  database "SQLite QA"
}

package "Ambiente MAIN (5175, 8089, 8081, 8082)" as main {
  [Ensurance Frontend :5175]
  [Pharmacy Frontend :8089]
  [Ensurance Backend :8081]
  [Pharmacy Backend :8082]
  database "SQLite MAIN"
}

cloud "CI/CD" as cicd {
  [GitHub Actions]
  [Drone CI]
  [Jenkins]
  [SonarQube]
}

cloud "Monitoreo" as monitor {
  [Prometheus]
  [Grafana]
  [Checkmk]
}

user --> dev
user --> qa
user --> main

cicd --> dev : Deploy
cicd --> qa : Deploy
cicd --> main : Deploy

monitor --> dev : Metrics
monitor --> qa : Metrics
monitor --> main : Metrics

note right of dev
  **Atributos Logrados:**
  
  ✅ Maintainability (95%)
  - Código limpio y estructurado
  - Patrones consistentes
  - Documentación completa
  
  ✅ Testability (90%)
  - CI/CD con tests automáticos
  - Cobertura >70%
  - Quality Gates
  
  ✅ Modifiability (90%)
  - Arquitectura flexible
  - Configuración externa
  - Separación de concerns
  
  ✅ Performance (85%)
  - Respuestas < 2s
  - Stateless
  - Paginación
  
  ✅ Security (75%)
  - Autenticación + RBAC
  - Password hashing
  - CORS configurado
end note

@enduml

' Fin de diagramas ADD
```


---

# CONCLUSIÓN DEL ANÁLISIS ADD

## Resumen Ejecutivo Final

Este documento consolidado presenta el **Análisis ADD (Attribute-Driven Design) completo** del sistema **Ensurance Pharmacy**, desarrollado durante el semestre pasado.

### 📊 Estadísticas del Análisis

- **Total de páginas**: 2,353 líneas
- **Total de palabras**: ~9,846 palabras
- **Atributos de calidad analizados**: 9
- **Escenarios detallados**: 19
- **Decisiones arquitectónicas**: 20+
- **Tácticas aplicadas**: 40+
- **Diagramas PlantUML**: 9
- **Cumplimiento global**: 83% ✅

### 🎯 Fortalezas Identificadas

1. **Maintainability (95%)** 🏆 - Excelente estructura en capas y patrones consistentes
2. **Testability (90%)** 🥈 - CI/CD robusto con cobertura >70%
3. **Modifiability (90%)** 🥉 - Arquitectura flexible y configuración externa

### ⚠️ Áreas de Mejora Identificadas

1. **Scalability (60%)** - Preparado pero no implementado (load balancer, PostgreSQL)
2. **Security (75%)** - Falta rate limiting, JWT tokens, RBAC backend

### 🏗️ Decisiones Arquitectónicas Clave

1. ✅ **Microservices Architecture** - 2 servicios independientes
2. ✅ **Layered Architecture** - 4 capas bien definidas
3. ✅ **Multi-Pipeline CI/CD** - GitHub Actions + Drone + Jenkins
4. ✅ **Multi-Ambiente** - DEV + QA + MAIN
5. ✅ **REST APIs** - Comunicación estándar
6. ✅ **SQLite + Hibernate** - Persistencia flexible
7. ✅ **Vue 3** - Frontend moderno
8. ✅ **Supervisor** - Auto-restart
9. ✅ **Docker Compose** - Orquestación
10. ✅ **SonarQube** - Quality Gates

### 📈 Cumplimiento por Atributo

| Atributo | Cumplimiento | Prioridad | Estado |
|----------|--------------|-----------|--------|
| Maintainability | 95% | 🔴 Crítica | ✅ Excelente |
| Testability | 90% | 🔴 Crítica | ✅ Excelente |
| Modifiability | 90% | 🟡 Alta | ✅ Excelente |
| Performance | 85% | 🟡 Alta | ✅ Bueno |
| Usability | 85% | 🟢 Media | ✅ Bueno |
| Interoperability | 85% | 🟢 Media | ✅ Bueno |
| Availability | 80% | 🟢 Media | ✅ Bueno |
| Security | 75% | 🟡 Alta | ⚠️ Aceptable |
| Scalability | 60% | 🟢 Media | ⚠️ Preparado |

### 🎓 Aplicación de Metodología ADD

El sistema **Ensurance Pharmacy** demuestra una aplicación exitosa de la metodología **ADD (Attribute-Driven Design)** a través de:

1. **Identificación clara de drivers** - Requisitos funcionales, restricciones y atributos priorizados
2. **Iteraciones documentadas** - 3 iteraciones (Microservices → Layered → CI/CD)
3. **Decisiones justificadas** - Cada decisión arquitectónica respaldada por atributos específicos
4. **Trade-offs explícitos** - Ventajas y desventajas documentadas
5. **Escenarios medibles** - Métricas cuantificables para cada atributo
6. **Tácticas concretas** - 40+ tácticas arquitectónicas aplicadas y verificables

### 🚀 Valor del Análisis

Este análisis ADD proporciona:

- ✅ **Trazabilidad** - Desde requisitos hasta decisiones técnicas
- ✅ **Justificación** - Cada decisión con su razón de ser
- ✅ **Validación** - Escenarios medibles y verificables
- ✅ **Documentación** - Completa y estructurada
- ✅ **Evolución** - Preparado para cambios futuros

### 📚 Uso del Documento

**Para Desarrolladores**: Consultar decisiones técnicas y tácticas aplicadas

**Para Arquitectos**: Analizar trade-offs y validar cumplimiento de atributos

**Para QA/Testers**: Usar escenarios como casos de prueba

**Para Stakeholders**: Entender fortalezas y áreas de mejora del sistema

### 🎉 Conclusión Final

El sistema **Ensurance Pharmacy** fue diseñado y desarrollado aplicando conscientemente principios de **calidad de software**, priorizando **Mantenibilidad y Testabilidad** como drivers principales. Las decisiones arquitectónicas están **documentadas, justificadas y validadas** mediante escenarios medibles.

El cumplimiento global del **83%** demuestra un sistema bien diseñado, con áreas claras de fortaleza y oportunidades de mejora identificadas.

---

**Fin del Análisis ADD Completo**

**Ensurance Pharmacy System**  
**Metodología: Attribute-Driven Design (ADD)**  
**Fecha**: Diciembre 2024  
**Versión**: 1.0 Consolidada

