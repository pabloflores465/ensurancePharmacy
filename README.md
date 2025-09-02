# 🏥 Ensurance Pharmacy - Sistema Integrado de Seguros y Farmacia

Sistema completo que integra gestión de seguros médicos y farmacia, desarrollado con arquitectura de microservicios.

## 📋 Índice

- [🏗️ Arquitectura del Sistema](#️-arquitectura-del-sistema)
- [🌐 Configuración de Puertos y Ambientes](#-configuración-de-puertos-y-ambientes)
- [🔧 Instalación y Configuración](#-instalación-y-configuración)
- [🚀 Ejecución](#-ejecución)
- [🧪 Testing](#-testing)
- [🐳 Docker](#-docker)
- [📊 Base de Datos](#-base-de-datos)
- [🔗 Integraciones](#-integraciones)
- [📚 Documentación por Componente](#-documentación-por-componente)

---

## 🏗️ Arquitectura del Sistema

### Componentes Principales

| Componente                     | Tecnología                | Puerto | Descripción                               |
| ------------------------------ | ------------------------- | ------ | ----------------------------------------- |
| **Ensurance Frontend**         | Vue 3 + TypeScript + Vite | 5175   | Interface de usuario para seguros médicos |
| **Pharmacy Frontend**          | Vue 3 + Vue CLI           | 8089   | Interface de usuario para farmacia        |
| **Ensurance Backend (backv4)** | Java + HttpServer         | 8081   | API REST para seguros médicos             |
| **Pharmacy Backend (backv5)**  | Java + HttpServer         | 8082   | API REST para farmacia                    |

### Stack Tecnológico

**Frontend:**

- Vue 3 con Composition API
- TypeScript (Ensurance) / JavaScript (Pharmacy)
- Vite (Ensurance) / Vue CLI (Pharmacy)
- Tailwind CSS / CSS personalizado
- Pinia para gestión de estado
- Vitest / Jest para testing

**Backend:**

- Java 23 con preview features
- `com.sun.net.httpserver.HttpServer`
- Hibernate ORM
- SQLite (desarrollo) / Oracle (producción)
- Maven para gestión de dependencias
- JaCoCo para cobertura de código

---

## 🌐 Configuración de Puertos y Ambientes

### 📋 Tabla de Puertos por Ambiente

#### 🔧 DEV (branches: dev, develop, development)

| Servicio               | Tipo    | Puerto   | URL                        |
| ---------------------- | ------- | -------- | -------------------------- |
| **Ensurance Frontend** | **Web** | **3000** | **http://localhost:3000**  |
| **Pharmacy Frontend**  | **Web** | **3001** | **http://localhost:3001**  |
| Ensurance Backend      | API     | 3002     | http://localhost:3002/api  |
| Pharmacy Backend       | API     | 3003     | http://localhost:3003/api2 |

#### 🧪 QA (branches: qa, test, testing, staging)

| Servicio               | Tipo    | Puerto   | URL                        |
| ---------------------- | ------- | -------- | -------------------------- |
| **Ensurance Frontend** | **Web** | **4000** | **http://localhost:4000**  |
| **Pharmacy Frontend**  | **Web** | **4001** | **http://localhost:4001**  |
| Ensurance Backend      | API     | 4002     | http://localhost:4002/api  |
| Pharmacy Backend       | API     | 4003     | http://localhost:4003/api2 |

#### 🚀 MAIN (branches: main, master)

| Servicio               | Tipo    | Puerto   | URL                        |
| ---------------------- | ------- | -------- | -------------------------- |
| **Ensurance Frontend** | **Web** | **5175** | **http://localhost:5175**  |
| **Pharmacy Frontend**  | **Web** | **8089** | **http://localhost:8089**  |
| Ensurance Backend      | API     | 8081     | http://localhost:8081/api  |
| Pharmacy Backend       | API     | 8082     | http://localhost:8082/api2 |

### 🔧 Variables de Entorno por Ambiente

#### DEV Environment

```bash
# Frontend Ensurance (Vite)
VITE_ENSURANCE_API_URL=http://localhost:8081/api
VITE_PHARMACY_API_URL=http://localhost:8082/api2
VITE_IP=localhost

# Frontend Pharmacy (Vue)
VUE_APP_PHARMACY_API_URL=http://localhost:8082/api2
VUE_APP_ENSURANCE_API_URL=http://localhost:8081/api
VUE_APP_IP=localhost

# Backend Variables
SERVER_HOST=0.0.0.0
ENVIRONMENT=dev
NODE_ENV=development
JAVA_OPTS="-Xmx256m -Xms128m -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
```

#### QA Environment

```bash
# Mismas variables base pero con:
ENVIRONMENT=qa
NODE_ENV=test
JAVA_OPTS="-Xmx384m -Xms192m"
TEST_MODE=true
```

#### MAIN Environment

```bash
# Mismas variables base pero con:
ENVIRONMENT=main
NODE_ENV=production
JAVA_OPTS="-Xmx512m -Xms256m"
```

---

## 🔧 Instalación y Configuración

### Requisitos del Sistema

- **Node.js** LTS 18+ (para frontends)
- **Java 23** (para backends)
- **Maven 3.8+** (para backends)
- **Python 3** (para scripts de IP)
- **Docker y Docker Compose** (opcional)
- **SQLite3** (para desarrollo local)

### Instalación de Dependencias

```bash
# Instalar dependencias de todos los sistemas
./test-runner.sh
# Seleccionar opción 9: Install all dependencies

# O manualmente:
cd ensurance && npm install && cd ..
cd pharmacy && npm install && cd ..
```

---

## 🚀 Ejecución

### Desarrollo con Docker (Recomendado)

#### Despliegue Automático

```bash
# Detecta automáticamente la rama git y despliega
./deploy.sh auto

# Despliegue específico por ambiente
./deploy.sh deploy dev    # Puertos 3000-3003
./deploy.sh deploy qa     # Puertos 4000-4003
./deploy.sh deploy main   # Puertos 5175, 8089, 8081, 8082
```

#### Gestión de Contenedores

```bash
# Ver estado de todos los ambientes
./deploy.sh status

# Ver logs en tiempo real
./deploy.sh logs dev
./deploy.sh logs qa
./deploy.sh logs main

# Detener ambiente específico
./deploy.sh stop dev

# Limpiar todos los contenedores
./deploy.sh clean
```

### Desarrollo Local (Sin Docker)

#### Frontends

```bash
# Ensurance Frontend
cd ensurance
npm run dev
# Disponible en: http://localhost:5173

# Pharmacy Frontend
cd pharmacy
npm run serve
# Disponible en: http://localhost:8080
```

#### Backends

```bash
# Ensurance Backend (backv4)
cd backv4
mvn exec:java -Dexec.mainClass="com.sources.app.App"
# Disponible en: http://localhost:8081/api

# Pharmacy Backend (backv5)
cd backv5
mvn exec:java -Dexec.mainClass="com.sources.app.App"
# Disponible en: http://localhost:8082/api2
```

### Script Unificado de Testing

El proyecto incluye un script bash interactivo para ejecutar tests y coverage:

```bash
./test-runner.sh
```

**Opciones disponibles:**

- **1-4**: Tests de frontend (Ensurance/Pharmacy)
- **5-6**: Tests de backend (BackV5/BackV4)
- **7-8**: Ejecutar todos los sistemas
- **9**: Instalar dependencias
- **10**: Análisis SonarQube
- **0**: Salir

---

## 🧪 Testing

### Frontend Testing

#### Ensurance (Vitest)

```bash
npm run test        # Modo interactivo
npm run test:run    # Modo headless (CI)
npm run test:ui     # Interfaz UI de Vitest
```

#### Pharmacy (Jest)

```bash
npm run test:unit         # Ejecuta una vez
npm run test:unit:watch   # Observa cambios
npm run test:unit:file    # Archivo específico
```

### Backend Testing

#### BackV4 (Ensurance Backend)

```bash
# Oracle (por defecto)
mvn -f backv4 test

# SQLite (desarrollo)
mvn -f backv4 -Psqlite-dev test
mvn -f backv4 -Psqlite-uat test
mvn -f backv4 -Psqlite-prod test

# Test específico
mvn -f backv4 -Dtest=TransactionPolicyTest test

# Con logs en consola
mvn -f backv4 -Dsurefire.useFile=false test
```

#### BackV5 (Pharmacy Backend)

```bash
# Tests con cobertura
mvn -f backv5 clean test jacoco:report

# Reporte en: target/site/jacoco/index.html
```

### Cobertura de Código

```bash
# Generar reportes de cobertura para todos los sistemas
./test-runner.sh
# Seleccionar opción 8: Run ALL tests with coverage

# SonarQube (requiere servidor local en puerto 9000)
./test-runner.sh
# Seleccionar opción 10: Run SonarQube analysis
```

---

## 🐳 Docker - Sistema Unificado Multi-Ambiente

El sistema incluye un Dockerfile unificado que ejecuta ambos sistemas (Ensurance y Pharmacy) con configuración multi-ambiente que detecta automáticamente la rama git.

### ▶️ Docker Compose por archivo (ejecución separada)

Puedes levantar cada archivo `docker-compose` por separado según el ambiente o herramientas CI/CD:

```bash
# DEV (puertos 3000-3003)
docker compose -f docker-compose.dev.yml up -d --build
docker compose -f docker-compose.dev.yml logs -f
docker compose -f docker-compose.dev.yml down

# QA (puertos 4000-4003)
docker compose -f docker-compose.qa.yml up -d --build
docker compose -f docker-compose.qa.yml logs -f
docker compose -f docker-compose.qa.yml down

# MAIN/Producción local (puertos 5175, 8089, 8081, 8082)
docker compose -f docker-compose.main.yml up -d --build
docker compose -f docker-compose.main.yml logs -f
docker compose -f docker-compose.main.yml down

# CI/CD stack (Jenkins, SonarQube, Drone, Docker-in-Docker)
docker compose -f docker-compose.cicd.yml up -d
docker compose -f docker-compose.cicd.yml logs -f
docker compose -f docker-compose.cicd.yml down
```

Accesos rápidos del stack CI/CD levantado con `docker-compose.cicd.yml`:

- Jenkins: `http://localhost:8080/jenkins`
- SonarQube: `http://localhost:9000/sonnar` (contexto configurado en `SONAR_WEB_CONTEXT`)
- Drone: `http://localhost:8000` (HTTP) / `https://localhost:8443` (HTTPS)

Notas:

- El servicio `docker` (dind) requiere Docker con privilegios; en Desktop suele funcionar por defecto.
- Jenkins primera ejecución: obtener contraseña inicial con `docker exec -it jenkins cat /var/jenkins_home/secrets/initialAdminPassword`.
- Los volúmenes declarados se crean automáticamente; para borrar datos agrega `-v` al comando `down`.

### 🏗️ Arquitectura del Contenedor

El Dockerfile unificado incluye:

**Frontends:**

- **Ensurance Frontend**: Vue 3 + TypeScript + Vite
- **Pharmacy Frontend**: Vue 3 + Vue CLI

**Backends:**

- **Ensurance Backend (BackV4)**: Java + HttpServer
- **Pharmacy Backend (BackV5)**: Java + HttpServer

**Bases de Datos:**

- **SQLite** para ambos sistemas (migrado desde Oracle)
- Ubicación: `/app/databases/`

### 🌍 Configuración Multi-Ambiente

| Ambiente | Puertos                | Rama Git                                          | Uso              |
| -------- | ---------------------- | ------------------------------------------------- | ---------------- |
| **DEV**  | 3000-3003              | `develop`, `dev`, `development`, feature branches | Desarrollo local |
| **MAIN** | 5175, 8089, 8081, 8082 | `main`, `master`                                  | Producción       |
| **QA**   | 4000-4003              | `qa`, `test`, `testing`, `staging`                | Testing/QA       |

#### Mapeo de Puertos por Ambiente

**🔧 DEV (Desarrollo)**

- **Ensurance Frontend**: `3000`
- **Pharmacy Frontend**: `3001`
- **Ensurance Backend**: `3002`
- **Pharmacy Backend**: `3003`

**🚀 MAIN (Producción)**

- **Ensurance Frontend**: `5175`
- **Pharmacy Frontend**: `8089`
- **Ensurance Backend**: `8081`
- **Pharmacy Backend**: `8082`

**🧪 QA (Testing)**

- **Ensurance Frontend**: `4000`
- **Pharmacy Frontend**: `4001`
- **Ensurance Backend**: `4002`
- **Pharmacy Backend**: `4003`

### 🚀 Uso del Script de Despliegue

#### Despliegue Automático (Recomendado)

```bash
# Detecta automáticamente la rama git y despliega
./deploy.sh auto

# Con reconstrucción forzada
./deploy.sh auto --rebuild
```

#### Despliegue Manual

```bash
# Desplegar ambiente específico
./deploy.sh deploy dev
./deploy.sh deploy main
./deploy.sh deploy qa

# Con reconstrucción forzada
./deploy.sh deploy dev --rebuild
```

#### Gestión de Ambientes

```bash
# Detener ambiente
./deploy.sh stop dev

# Ver logs en tiempo real
./deploy.sh logs main

# Ver estado de todos los contenedores
./deploy.sh status

# Limpiar todos los contenedores e imágenes
./deploy.sh clean
```

### 🔧 Características del Sistema

**✅ Detección Automática de Puertos**

- El script verifica automáticamente si los puertos están en uso
- Mata procesos que ocupen los puertos necesarios
- Garantiza despliegue limpio sin conflictos

**🔄 Gestión de Procesos**

```bash
# El script automáticamente:
# 1. Detecta procesos usando puertos objetivo
# 2. Mata procesos conflictivos
# 3. Verifica que los puertos estén libres
# 4. Despliega el ambiente correspondiente
```

**📁 Estructura de Directorios por Ambiente**

```
./databases/
├── dev/
│   ├── ensurance/USUARIO.sqlite
│   └── pharmacy/USUARIO.sqlite
├── main/
│   ├── ensurance/USUARIO.sqlite
│   └── pharmacy/USUARIO.sqlite
└── qa/
    ├── ensurance/USUARIO.sqlite
    └── pharmacy/USUARIO.sqlite

./logs/
├── dev/
├── main/
└── qa/
```

### 🎯 Configuración por Ambiente

**DEV (Desarrollo)**

- **Node.js**: `development`
- **Java**: Debug habilitado, menor memoria
- **Volúmenes**: Código fuente montado para hot-reload
- **Logs**: Verbose para debugging

**MAIN (Producción)**

- **Node.js**: `production`
- **Java**: Optimizado para producción
- **Volúmenes**: Solo datos persistentes
- **Logs**: Nivel INFO

**QA (Testing)**

- **Node.js**: `test`
- **Java**: Configuración de testing
- **Volúmenes**: Datos de prueba
- **Logs**: Nivel DEBUG para testing

### 🔍 Monitoreo y Debugging

#### Ver Estado del Sistema

```bash
# Estado de contenedores
./deploy.sh status

# Logs específicos por ambiente
./deploy.sh logs dev
./deploy.sh logs main
./deploy.sh logs qa
```

#### Verificar Puertos

```bash
# Ver qué procesos usan puertos específicos
lsof -i :3000  # DEV frontend ensurance
lsof -i :4000  # QA frontend ensurance
lsof -i :5175  # MAIN frontend ensurance
```

#### Acceso a Contenedores

```bash
# Entrar al contenedor
docker exec -it ensurance-pharmacy-dev sh
docker exec -it ensurance-pharmacy-main sh
docker exec -it ensurance-pharmacy-qa sh
```

### 🚨 Troubleshooting Docker

#### Problema: Puerto Ocupado

El script automáticamente mata procesos, pero si persiste:

```bash
# Verificar manualmente
lsof -ti:3000 | xargs kill -9
```

#### Problema: Contenedor No Inicia

```bash
# Ver logs detallados
docker logs ensurance-pharmacy-dev

# Reconstruir desde cero
./deploy.sh deploy dev --rebuild
```

#### Problema: Base de Datos

```bash
# Recrear bases de datos
rm -rf databases/dev
./deploy.sh deploy dev
```

### 📝 Flujo de Trabajo Recomendado

1. **Desarrollo**: Trabaja en rama `develop` o feature branch

   ```bash
   git checkout develop
   ./deploy.sh auto  # Despliega en puertos 3000-3003
   ```

2. **Testing**: Merge a rama `qa`

   ```bash
   git checkout qa
   ./deploy.sh auto  # Despliega en puertos 4000-4003
   ```

3. **Producción**: Merge a rama `main`
   ```bash
   git checkout main
   ./deploy.sh auto  # Despliega en puertos 5175, 8089, 8081, 8082
   ```

---

## 📊 Base de Datos

### Configuración SQLite Unificada

El sistema usa SQLite para todos los ambientes, con bases de datos separadas por contenedor:

#### Estructura por Ambiente

```
./databases/
├── dev/
│   ├── ensurance/USUARIO.sqlite
│   └── pharmacy/USUARIO.sqlite
├── main/
│   ├── ensurance/USUARIO.sqlite
│   └── pharmacy/USUARIO.sqlite
└── qa/
    ├── ensurance/USUARIO.sqlite
    └── pharmacy/USUARIO.sqlite
```

#### BackV4 (Ensurance Backend)

- **SQLite**: Configuración principal
- **Migración**: Scripts en `backv4/sqlite/`
- **Dialecto**: `org.hibernate.community.dialect.SQLiteDialect`

#### BackV5 (Pharmacy Backend)

- **SQLite**: Configuración principal migrada de Oracle
- **Migración**: Scripts en `backv5/sqlite/`
- **Dialecto**: `org.hibernate.community.dialect.SQLiteDialect`

### Scripts de Migración

#### BackV4 (Ensurance Backend)

```bash
cd backv4/sqlite/

# Aplicar esquema inicial
sqlite3 USUARIO.sqlite < 01_initial_schema.sql

# Aplicar migraciones adicionales
sqlite3 USUARIO.sqlite < 02_add_missing_tables.sql
```

**Scripts disponibles:**

- `01_initial_schema.sql` - Esquema completo inicial con todas las tablas
- `02_add_missing_tables.sql` - Agrega tabla SUBCATEGORY y actualiza SYSTEM_CONFIG

#### BackV5 (Pharmacy Backend)

```bash
cd backv5/sqlite/

# Aplicar esquema inicial
sqlite3 USUARIO.sqlite < 01_initial_schema.sql

# Aplicar migraciones adicionales
sqlite3 USUARIO.sqlite < 02_add_missing_columns.sql
```

**Scripts disponibles:**

- `01_initial_schema.sql` - Esquema completo inicial del sistema de farmacia
- `02_add_missing_columns.sql` - Agrega columnas ID_CATEGORY, ID_SUBCATEGORY a MEDICINE

### Estructura de Base de Datos

#### BackV4 (Ensurance) - Tablas Principales

- **USERS** - Gestión de usuarios y autenticación
- **POLICY** - Pólizas de seguros médicos
- **HOSPITALS** - Información de hospitales
- **MEDICINE** - Catálogo de medicinas
- **PRESCRIPTION** - Recetas médicas
- **TRANSACTIONS** - Transacciones financieras
- **APPOINTMENTS** - Citas médicas
- **SYSTEM_CONFIG** - Parámetros de configuración del sistema
- **SUBCATEGORY** - Subcategorías de medicinas

#### BackV5 (Pharmacy) - Tablas Principales

- **USERS** - Gestión de usuarios y autenticación
- **MEDICINE** - Catálogo de medicinas con categorías jerárquicas
- **PRESCRIPTION** - Recetas médicas con detalles de medicinas
- **ORDERS** - Órdenes de clientes
- **BILL** - Información de facturación con cobertura de seguros
- **COMMENTS** - Comentarios de usuarios sobre medicinas
- **SERVICE_APPROVALS** - Flujo de trabajo de aprobación de servicios
- **SYSTEM_CONFIG** - Parámetros de configuración del sistema
- **CATEGORY** - Categorías de medicinas
- **SUBCATEGORY** - Subcategorías de medicinas

### Características Clave

**BackV4 (Ensurance):**

- Sistema de seguros médicos completo
- Gestión de pólizas y transacciones
- Integración con hospitales
- Sistema de citas médicas

**BackV5 (Pharmacy):**

- Categorización jerárquica de medicinas
- Gestión detallada de recetas con dosificación, frecuencia, duración
- Flujo completo de órdenes desde creación hasta facturación
- Integración con seguros y cálculos de cobertura
- Sistema de comentarios comunitarios

### Verificación de Datos

```bash
# Verificar en contenedores Docker
docker exec -it ensurance-pharmacy-dev sqlite3 /app/databases/ensurance/USUARIO.sqlite ".tables"
docker exec -it ensurance-pharmacy-main sqlite3 /app/databases/pharmacy/USUARIO.sqlite "SELECT COUNT(*) FROM MEDICINE;"

# Verificar localmente
sqlite3 databases/dev/ensurance/USUARIO.sqlite "SELECT COUNT(*) FROM USERS;"
sqlite3 databases/main/pharmacy/USUARIO.sqlite ".schema MEDICINE"

# Verificar estructura de tablas
sqlite3 backv4/sqlite/USUARIO.sqlite ".schema POLICY"
sqlite3 backv5/sqlite/USUARIO.sqlite ".schema PRESCRIPTION"
```

### Notas Técnicas

- Todos los scripts usan `CREATE TABLE IF NOT EXISTS` para prevenir errores en re-ejecuciones
- BackV5 tiene `PRAGMA foreign_keys = ON` habilitado
  -#### ⚠️ Consideraciones de Seguridad

- Configurar autenticación robusta en todos los servicios antes de exponerlos
- Usar tokens de acceso específicos para integraciones externas
- Revisar logs de acceso regularmente
- Considerar usar Tailscale ACLs para restringir acceso por usuario/grupo

### 🔐 Configuración de Secretos

#### 🚁 Drone CI/CD Secrets

Para configurar los secretos en Drone UI (http://localhost:8000):

```bash
# Secretos requeridos para el pipeline Drone
sonar_token          # Token de SonarQube para análisis de código
email_username       # Usuario SMTP para notificaciones
email_password       # Password SMTP para notificaciones
environment_var      # Variable de ambiente (opcional)
```

**Pasos para configurar:**

1. Acceder a Drone UI: http://localhost:8000
2. Ir a Repository Settings → Secrets
3. Agregar cada secreto con su valor correspondiente
4. Marcar como "Pull Request" si se necesita en PRs

#### 🐙 GitHub Actions Secrets

**Secretos ya configurados:**

```bash
# SonarQube
SONAR_TOKEN                    # Token para análisis SonarQube
SONAR_HOST_URL                 # URL del servidor SonarQube

# Deployment por ambiente
ENSURANCE_BACKEND_DEV          # Credenciales backend DEV
ENSURANCE_BACKEND_QA           # Credenciales backend QA
ENSURANCE_BACKEND_MAIN         # Credenciales backend MAIN
ENSURANCE_FRONTEND_DEV         # Credenciales frontend DEV
ENSURANCE_FRONTEND_QA          # Credenciales frontend QA
ENSURANCE_FRONTEND_MAIN        # Credenciales frontend MAIN
```

**Secretos faltantes por configurar:**

```bash
# Pharmacy Backend (BackV5)
PHARMACY_BACKEND_DEV           # Credenciales pharmacy backend DEV
PHARMACY_BACKEND_QA            # Credenciales pharmacy backend QA
PHARMACY_BACKEND_MAIN          # Credenciales pharmacy backend MAIN

# Pharmacy Frontend
PHARMACY_FRONTEND_DEV          # Credenciales pharmacy frontend DEV
PHARMACY_FRONTEND_QA           # Credenciales pharmacy frontend QA
PHARMACY_FRONTEND_MAIN         # Credenciales pharmacy frontend MAIN

# 📧 Notificaciones por Email (REQUERIDO)
SMTP_SERVER                    # Servidor SMTP (ej: smtp.gmail.com)
SMTP_PORT                      # Puerto SMTP (ej: 587 para TLS, 465 para SSL)
SMTP_USERNAME                  # Usuario SMTP para autenticación
SMTP_PASSWORD                  # Password/App Password SMTP
SMTP_FROM_EMAIL                # Email remitente (ej: noreply@company.com)
NOTIFICATION_EMAIL             # Email(s) destinatario(s) separados por coma

# Base de datos
DATABASE_DEV_URL               # URL base de datos DEV
DATABASE_QA_URL                # URL base de datos QA
DATABASE_MAIN_URL              # URL base de datos MAIN
```

**Configuración en GitHub:**

1. Ir a Repository → Settings → Secrets and variables → Actions
2. Click "New repository secret"
3. Agregar nombre y valor del secreto
4. Los workflows automáticamente usarán estos secretos

#### 📊 Valores de Ejemplo para Secretos

**SonarQube:**

```bash
SONAR_TOKEN=squ_1234567890abcdef...
SONAR_HOST_URL=https://macbook-air-de-gp.tail5d54f7.ts.net/sonar
```

**Email SMTP:**

```bash
SMTP_SERVER=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=your-email@gmail.com
SMTP_PASSWORD=your-app-password
SMTP_FROM_EMAIL=noreply@ensurancepharmacy.com
NOTIFICATION_EMAIL=pablopolis2016@gmail.com,jflores@unis.edu.gt
```

**Base de datos SQLite:**

```bash
DATABASE_DEV_URL=sqlite:///app/databases/dev/USUARIO.sqlite
DATABASE_QA_URL=sqlite:///app/databases/qa/USUARIO.sqlite
DATABASE_MAIN_URL=sqlite:///app/databases/main/USUARIO.sqlite
```

### 📧 Configuración SMTP para Notificaciones por Email

Los workflows de GitHub Actions envían notificaciones automáticas por email después de ejecutar tests y análisis SonarQube.

#### 🔧 Configuración Requerida

**1. Configurar Secretos en GitHub:**

Ir a `Repository → Settings → Secrets and variables → Actions` y agregar:

```bash
SMTP_SERVER=smtp.gmail.com              # Servidor SMTP
SMTP_PORT=587                           # Puerto (587 para TLS, 465 para SSL)
SMTP_USERNAME=your-email@gmail.com      # Usuario SMTP
SMTP_PASSWORD=your-app-password         # App Password de Gmail
SMTP_FROM_EMAIL=noreply@company.com     # Email remitente
NOTIFICATION_EMAIL=team@company.com     # Destinatarios (separados por coma)
```

#### 📨 Proveedores SMTP Comunes

**Gmail:**

```bash
SMTP_SERVER=smtp.gmail.com
SMTP_PORT=587
# Requiere App Password: https://support.google.com/accounts/answer/185833
```

**Outlook/Hotmail:**

```bash
SMTP_SERVER=smtp-mail.outlook.com
SMTP_PORT=587
```

**Yahoo:**

```bash
SMTP_SERVER=smtp.mail.yahoo.com
SMTP_PORT=587
```

**SendGrid:**

```bash
SMTP_SERVER=smtp.sendgrid.net
SMTP_PORT=587
SMTP_USERNAME=apikey
SMTP_PASSWORD=your-sendgrid-api-key
```

#### 📬 Tipos de Notificaciones

**1. CI/CD Pipeline (ci-cd.yml):**

- Se envía después de todos los tests y análisis SonarQube
- Incluye estado de cada job (✅/❌)
- Detecta automáticamente el ambiente (DEV/QA/MAIN)
- Enlace directo al workflow run

**2. Pull Request Analysis (sonarqube-pr-analysis.yml):**

- Se envía después del análisis SonarQube en PRs
- Incluye estado del Quality Gate
- Enlaces al PR y al análisis
- Detecta ambiente objetivo del PR

#### 🎨 Formato de Emails

Los emails incluyen:

- **Estado visual** con colores (verde/rojo)
- **Información del commit** (autor, rama, hash)
- **Resultados detallados** de cada job
- **Enlaces directos** a GitHub y workflows
- **Diseño responsive** HTML

#### 🔍 Troubleshooting SMTP

**Error de autenticación:**

- Verificar que SMTP_USERNAME y SMTP_PASSWORD sean correctos
- Para Gmail, usar App Password en lugar de contraseña normal
- Verificar que 2FA esté habilitado en Gmail

**Error de conexión:**

- Verificar SMTP_SERVER y SMTP_PORT
- Algunos proveedores requieren TLS (puerto 587) vs SSL (puerto 465)

**Emails no llegan:**

- Verificar carpeta de spam/junk
- Confirmar que NOTIFICATION_EMAIL esté bien formateado
- Verificar límites de rate del proveedor SMTP

---

## 🔗 Integraciones

### Exponer CI/CD con Tailscale Funnel

Para exponer de forma segura los servicios de CI/CD (Jenkins, SonarQube y Drone) a través de tu tailnet, usa Tailscale Funnel. Requisitos: haber iniciado sesión con `tailscale up` y tener Funnel habilitado en tu tailnet.

Comandos para levantar los túneles:

```bash
# Jenkins en /jenkins → http://127.0.0.1:8080/jenkins
tailscale funnel --https=443 --set-path=/jenkins --bg http://127.0.0.1:8080/jenkins

# SonarQube en /sonar → http://127.0.0.1:9000/sonar
tailscale funnel --https=443 --set-path=/sonar --bg http://127.0.0.1:9000/sonar

# Drone en / → http://127.0.0.1:8000 Nota: Drone no tiene path, porque no lo soporta
tailscale funnel --https=443 --set-path=/ --bg http://127.0.0.1:8000
```

Notas:

- Asegúrate de que los servicios estén corriendo localmente (ver `docker-compose.cicd.yml`).
- La URL pública tendrá el formato `https://<tu-nodo>.ts.net/<path>`.
- Para ver estado o detener: `tailscale funnel status` y `tailscale funnel stop`.

### APIs Externas

- **Hospital API**: `http://localhost:8000/api`
- **Servicios específicos**: `http://localhost:5050/api`

### Comunicación Entre Servicios

- **Ensurance ↔ Pharmacy**: Comunicación bidireccional para verificación de recetas y políticas
- **Frontend ↔ Backend**: APIs REST con autenticación JWT
- **Backend ↔ Hospital**: Integración para citas médicas

### Funciones de API Configurables

#### Frontend Ensurance

```javascript
import { getInsuranceApiUrl, getPharmacyApiUrl } from "~/utils/api";
const response = await axios.get(getInsuranceApiUrl("hospital"));
```

#### Frontend Pharmacy

```javascript
import ApiService from "@/services/ApiService";
const response = await axios.get(ApiService.getPharmacyApiUrl("/medicines"));
```

---

## 📚 Documentación por Componente

### 🛡️ Ensurance Frontend (Vue 3 + TypeScript + Vite)

**Características:**

- Vue 3 con `<script setup>` y Composition API
- TypeScript para type safety
- Vite para desarrollo rápido
- Tailwind CSS para estilos
- Pinia para gestión de estado
- Vitest para testing unitario

**Comandos principales:**

```bash
npm run dev      # Desarrollo con hot-reload
npm run build    # Build de producción
npm run preview  # Preview del build
npm run test     # Tests unitarios
```

**Estructura:**

- `src/components/` - Componentes reutilizables
- `src/pages/` - Páginas de la aplicación
- `src/utils/` - Utilidades y helpers
- `tests/` - Tests unitarios

### 💊 Pharmacy Frontend (Vue 3 + Vue CLI)

**Características:**

- Vue 3 con Options API
- Vue CLI para tooling
- Jest para testing
- CSS personalizado
- Integración con APIs de farmacia

**Comandos principales:**

```bash
npm run serve    # Desarrollo
npm run build    # Build de producción
npm run lint     # Linting
npm run test:unit # Tests unitarios
```

### 🔧 Ensurance Backend (BackV4 - Java)

**Características:**

- Java con `HttpServer` nativo
- Hibernate ORM con soporte Oracle/SQLite
- Endpoints bajo `/api`
- Perfiles Maven para diferentes entornos

**Endpoints principales:**

- `POST /api/login`
- `GET/POST/PUT /api/users`
- `GET/POST/PUT /api/policies`
- `GET/POST/PUT /api/transactions`

**Configuración Hibernate:**

- Oracle: `hibernate.cfg.xml` (por defecto)
- SQLite: `hibernate-sqlite-{ENVIRONMENT}.cfg.xml`

### 🏥 Pharmacy Backend (BackV5 - Java)

**Características:**

- Java 23 con preview features
- HttpServer ligero
- Endpoints bajo `/api2`
- Migrado completamente a SQLite

**Endpoints principales:**

- `POST /api2/login`
- `GET/POST/PUT /api2/medicines`
- `GET /api2/medicines/search`
- `GET/POST/PUT /api2/orders`
- `GET/POST/PUT /api2/prescriptions`

**Variables de entorno:**

- `SERVER_HOST` (default: `0.0.0.0`)
- `SERVER_PORT` (default: `8081`)
- `ENS_BACKEND_API_URL`
- `HOSPITAL_API_URL`

---

## 🎯 Acceso Rápido por Ambiente

### 🔧 DEV (Desarrollo)

- 🛡️ **Seguros**: http://localhost:3000
- 💊 **Farmacia**: http://localhost:3001
- 🔌 **API Seguros**: http://localhost:8081/api
- 🔌 **API Farmacia**: http://localhost:8082/api2

### 🧪 QA (Testing)

- 🛡️ **Seguros**: http://localhost:4000
- 💊 **Farmacia**: http://localhost:4001
- 🔌 **API Seguros**: http://localhost:4002/api
- 🔌 **API Farmacia**: http://localhost:4003/api2

### 🚀 MAIN (Producción)

- 🛡️ **Seguros**: http://localhost:5175
- 💊 **Farmacia**: http://localhost:8089
- 🔌 **API Seguros**: http://localhost:8081/api
- 🔌 **API Farmacia**: http://localhost:8082/api2

---

## 👤 Credenciales de Administrador

Para facilitar el testing y acceso a todos los sistemas, se ha creado un usuario administrador en todas las bases de datos:

### 🔐 Credenciales de Login

| Campo        | Valor                         |
| ------------ | ----------------------------- |
| **Email**    | `admin@ensurancepharmacy.com` |
| **Password** | `admin123`                    |
| **Rol**      | `ADMIN`                       |
| **Nombre**   | `Administrator`               |
| **CUI**      | `1234567890123`               |
| **Estado**   | `Activo (ENABLED = 1)`        |

### 🌐 Disponibilidad por Ambiente

**✅ Usuario disponible en:**

- **DEV**: Ensurance + Pharmacy (puertos 3000-3003)
- **QA**: Ensurance + Pharmacy (puertos 4000-4003)
- **MAIN**: Ensurance + Pharmacy (puertos 5175, 8089, 8081, 8082)

### 📊 Bases de Datos Configuradas

**Ensurance (BackV4):**

- `databases/dev/ensurance/USUARIO.sqlite`
- `databases/qa/ensurance/USUARIO.sqlite`
- `databases/main/ensurance/USUARIO.sqlite`
- `backv4/sqlite/*.sqlite` (todas las variantes)

**Pharmacy (BackV5):**

- `databases/dev/pharmacy/USUARIO.sqlite`
- `databases/qa/pharmacy/USUARIO.sqlite`
- `databases/main/pharmacy/USUARIO.sqlite`
- `backv5/sqlite/*.sqlite` (todas las variantes)

### 🔍 Verificación de Login

```bash
# Verificar usuario en cualquier base de datos
sqlite3 databases/dev/ensurance/USUARIO.sqlite "SELECT EMAIL, NAME, ROL FROM USERS WHERE EMAIL = 'admin@ensurancepharmacy.com';"

# Resultado esperado:
# admin@ensurancepharmacy.com|Administrator|ADMIN
```

**⚠️ Nota de Seguridad:** Estas credenciales son para desarrollo y testing. En producción real, cambiar la contraseña y usar autenticación más robusta.

---

## 🛠️ Troubleshooting

### Problemas Comunes

1. **Puerto en uso**: Ajustar variables de entorno `SERVER_PORT`
2. **Base de datos no disponible**: Verificar archivos SQLite en `sqlite/`
3. **APIs no responden**: Verificar que backends estén iniciados
4. **Tests fallan**: Ejecutar `./test-runner.sh` opción 9 para instalar dependencias

### Configuración de Puertos

- **Frontend**: Los puertos se configuran desde la UI y se guardan en `localStorage`
- **Backend**: Se configuran via variables de entorno
- **Docker**: Se mapean según variables `*_HOST_PORT`

### Logs y Debugging

```bash
# Ver logs de tests
mvn -f backv4 -Dsurefire.useFile=false test

# Verificar conectividad SQLite
mvn -f backv4 -Psqlite-dev -Dtest=SQLiteConnectivityTest test
```

---

## 📄 Archivos de Configuración Importantes

### 🐳 Docker y Despliegue

- `deploy.sh` - Script unificado de despliegue multi-ambiente
- `Dockerfile` - Contenedor unificado multi-stage
- `docker-compose.dev.yml` - Configuración ambiente desarrollo (puertos 3000-3003)
- `docker-compose.qa.yml` - Configuración ambiente testing (puertos 4000-4003)
- `docker-compose.main.yml` - Configuración ambiente producción (puertos 5175, 8089, 8081, 8082)
- `.dockerignore` - Exclusiones para build de Docker

### 🔧 CI/CD y Calidad

- `Jenkinsfile` - Pipeline CI/CD multi-ambiente
- `sonar-project.properties` - Configuración SonarQube con análisis por rama
- `test-runner.sh` - Script unificado de testing

### 📊 Base de Datos

- `backv4/sqlite/` - Scripts de migración Ensurance
- `backv5/sqlite/` - Scripts de migración Pharmacy

### 🌐 Frontend

- `ensurance/.env.example` - Variables de entorno Ensurance
- `pharmacy/.env.example` - Variables de entorno Pharmacy

---

## 🔄 CI/CD Pipeline

### Flujo Automático por Rama

**Ramas DEV** (`dev`, `develop`, `development`):

- ✅ Tests unitarios y cobertura
- 🔍 Análisis SonarQube
- 🚀 Deploy automático en puertos 3000-3003

**Ramas QA** (`qa`, `test`, `testing`, `staging`):

- ✅ Tests unitarios y cobertura
- 🔍 Análisis SonarQube
- 🚀 Deploy automático en puertos 4000-4003

**Ramas MAIN** (`main`, `master`):

- ✅ Tests unitarios y cobertura
- 🔍 Análisis SonarQube
- 🚀 Deploy automático en puertos 5175, 8089, 8081, 8082

### SonarQube

```bash
# Análisis local
./test-runner.sh
# Seleccionar opción 10: Run SonarQube analysis

# El pipeline ejecuta automáticamente:
# - Cobertura de backends (JaCoCo)
# - Cobertura de frontends (LCOV)
# - Análisis por rama
# - Quality Gate
```

---

_Última actualización: Agosto 2025_

**Desarrollado por el equipo de Ensurance Pharmacy**
