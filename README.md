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

| Componente | Tecnología | Puerto | Descripción |
|------------|------------|---------|-------------|
| **Ensurance Frontend** | Vue 3 + TypeScript + Vite | 5175 | Interface de usuario para seguros médicos |
| **Pharmacy Frontend** | Vue 3 + Vue CLI | 8089 | Interface de usuario para farmacia |
| **Ensurance Backend (backv4)** | Java + HttpServer | 8081 | API REST para seguros médicos |
| **Pharmacy Backend (backv5)** | Java + HttpServer | 8082 | API REST para farmacia |

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

#### 🔧 DEV (branch: dev)

| Servicio | Tipo | Puerto | URL |
|----------|------|--------|-----|
| Ensurance Backend | API | 8081 | http://localhost:8081/api |
| Pharmacy Backend | API | 8082 | http://localhost:8082/api2 |
| **Ensurance Frontend** | **Web** | **5175** | **http://localhost:5175** |
| **Pharmacy Frontend** | **Web** | **8089** | **http://localhost:8089** |

#### 🧪 UAT (branch: test)

| Servicio | Tipo | Puerto | URL |
|----------|------|--------|-----|
| Ensurance Backend | API | 9081 | http://localhost:9081/api |
| Pharmacy Backend | API | 9082 | http://localhost:9082/api2 |
| **Ensurance Frontend** | **Web** | **6175** | **http://localhost:6175** |
| **Pharmacy Frontend** | **Web** | **9089** | **http://localhost:9089** |

#### 🚀 PROD (branch: main)

| Servicio | Tipo | Puerto | URL |
|----------|------|--------|-----|
| Ensurance Backend | API | 80 | http://localhost/api |
| Pharmacy Backend | API | 81 | http://localhost:81/api2 |
| **Ensurance Frontend** | **Web** | **7175** | **http://localhost:7175** |
| **Pharmacy Frontend** | **Web** | **7089** | **http://localhost:7089** |

### 🔧 Variables de Entorno

#### Frontend Ensurance (Vite)
```bash
VITE_ENSURANCE_API_URL=http://localhost:8081/api
VITE_PHARMACY_API_URL=http://localhost:8082/api2
VITE_IP=localhost
```

#### Frontend Pharmacy (Vue)
```bash
VUE_APP_PHARMACY_API_URL=http://localhost:8082/api2
VUE_APP_ENSURANCE_API_URL=http://localhost:8081/api
VUE_APP_IP=localhost
```

#### Backend Variables (Java)
```bash
# Para Ensurance Backend (backv4)
SERVER_HOST=0.0.0.0
SERVER_PORT=8080
PHARM_BACKEND_API_URL=http://localhost:8082/api2
HOSPITAL_API_URL=http://localhost:8000/api

# Para Pharmacy Backend (backv5)
SERVER_HOST=0.0.0.0
SERVER_PORT=8081
ENS_BACKEND_API_URL=http://localhost:8081/api
HOSPITAL_API_URL=http://localhost:8000/api
```

#### Variables por Ambiente (Jenkins)

**DEV:**
```bash
ENS_BACKEND_HOST_PORT=8081
PHARM_BACKEND_HOST_PORT=8082
ENS_FRONTEND_HOST_PORT=5175
PHARM_FRONTEND_HOST_PORT=8089
DB_SCHEMA_ENSURANCE="USUARIODEV"
DB_SCHEMA_PHARMACY="FARMACIADEV"
```

**UAT:**
```bash
ENS_BACKEND_HOST_PORT=9081
PHARM_BACKEND_HOST_PORT=9082
ENS_FRONTEND_HOST_PORT=6175
PHARM_FRONTEND_HOST_PORT=9089
DB_SCHEMA_ENSURANCE="USUARIOUAT"
DB_SCHEMA_PHARMACY="FARMACIAUAT"
```

**PROD:**
```bash
ENS_BACKEND_HOST_PORT=80
PHARM_BACKEND_HOST_PORT=81
ENS_FRONTEND_HOST_PORT=7175
PHARM_FRONTEND_HOST_PORT=7089
DB_SCHEMA_ENSURANCE="USUARIO"
DB_SCHEMA_PHARMACY="FARMACIA"
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

### Desarrollo Local

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

## 🐳 Docker

### Docker Compose

#### Ensurance System
```bash
# Levantar sistema completo
docker compose -f docker-compose.ensurance.yaml up --build

# Limpieza de contenedores
docker compose -f docker-compose.ensurance.yaml --profile cleanup up
```

#### Pharmacy System
```bash
# Levantar sistema completo
docker compose -f docker-compose.pharmacy.yaml up --build

# Limpieza de contenedores
docker compose -f docker-compose.pharmacy.yaml --profile cleanup up
```

### Puertos Docker

Los contenedores exponen puertos internos que se mapean a puertos del host según las variables de entorno:

- Backend Ensurance: contenedor `8080` → host `${ENS_BACKEND_HOST_PORT:-8081}`
- Backend Pharmacy: contenedor `8081` → host `${PHARM_BACKEND_HOST_PORT:-8082}`
- Frontend Ensurance: contenedor `5173` → host `${ENS_FRONTEND_HOST_PORT:-5175}`
- Frontend Pharmacy: contenedor `8080` → host `${PHARM_FRONTEND_HOST_PORT:-8089}`

---

## 📊 Base de Datos

### Configuración Dual: Oracle/SQLite

El sistema soporta tanto Oracle (producción) como SQLite (desarrollo):

#### BackV4 (Ensurance)
- **Oracle**: Configuración por defecto en `hibernate.cfg.xml`
- **SQLite**: Perfiles Maven disponibles
  - `-Psqlite-dev` → `USUARIODEV.sqlite`
  - `-Psqlite-uat` → `USUARIOUAT.sqlite`
  - `-Psqlite-prod` → `USUARIO.sqlite`

#### BackV5 (Pharmacy)
- **SQLite**: Configuración principal migrada de Oracle
- Base de datos: `backv5/sqlite/USUARIO.sqlite`
- Dialecto: `org.hibernate.community.dialect.SQLiteDialect`

### Esquemas por Ambiente

| Ambiente | Esquema Seguros | Esquema Farmacia |
|----------|-----------------|------------------|
| DEV      | `USUARIODEV`    | `FARMACIADEV`    |
| UAT      | `USUARIOUAT`    | `FARMACIAUAT`    |
| PROD     | `USUARIO`       | `FARMACIA`       |

### Verificación de Datos SQLite

```bash
# Verificar datos en BackV4
sqlite3 backv4/sqlite/USUARIODEV.sqlite "SELECT COUNT(*) FROM USERS;"
sqlite3 backv4/sqlite/USUARIOUAT.sqlite "SELECT COUNT(*) FROM USERS;"
sqlite3 backv4/sqlite/USUARIO.sqlite "SELECT COUNT(*) FROM USERS;"

# Verificar datos en BackV5
sqlite3 backv5/sqlite/USUARIO.sqlite ".tables"
```

---

## 🔗 Integraciones

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

### 🔧 DEV
- 🛡️ **Seguros**: http://localhost:5175
- 💊 **Farmacia**: http://localhost:8089

### 🧪 UAT
- 🛡️ **Seguros**: http://localhost:6175
- 💊 **Farmacia**: http://localhost:9089

### 🚀 PROD
- 🛡️ **Seguros**: http://localhost:7175
- 💊 **Farmacia**: http://localhost:7089

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

- `test-runner.sh` - Script unificado de testing
- `docker-compose.ensurance.yaml` - Orquestación Ensurance
- `docker-compose.pharmacy.yaml` - Orquestación Pharmacy
- `Jenkinsfile` - Pipeline CI/CD
- `sonar-project.properties` - Configuración SonarQube

---

_Última actualización: Enero 2025_

**Desarrollado por el equipo de Ensurance Pharmacy**
