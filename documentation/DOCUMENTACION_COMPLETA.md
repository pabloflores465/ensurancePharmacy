# Documentación Completa — Ensurance Pharmacy

Proyecto integrado de Seguros y Farmacia basado en microservicios, con frontends en Vue y backends en Java, empaquetado en un contenedor unificado y orquestado via Docker Compose por ambiente.

---

## 1) Visión General

- Objetivo: Gestionar procesos de seguros médicos (Ensurance) y farmacia (Pharmacy) de forma integrada.
- Alcance: 2 frontends, 2 backends, bases de datos SQLite por ambiente, despliegue multi‑ambiente (dev, qa, main), CI/CD con Jenkins y análisis de calidad SonarQube.
- Estados de ambiente: Desarrollo (DEV), Testing (QA), Producción (MAIN).

---

## 2) Arquitectura del Sistema

Componentes principales:

- Ensurance Frontend: Vue 3 + Vite (TS). Sirve en puerto 5175 dentro del contenedor.
- Pharmacy Frontend: Vue 3 + Vue CLI (JS). Sirve en puerto 8089 dentro del contenedor.
- Ensurance Backend (BackV4): Java + HttpServer + Hibernate. Expone `/api` en 8081.
- Pharmacy Backend (BackV5): Java + HttpServer + Hibernate. Expone `/api2` en 8082.
- Nginx: Sirve frontends y hace proxy a APIs.
- Supervisor: Orquesta Nginx y ambos backends dentro del contenedor.

Contenedor unificado (Dockerfile): build multi‑stage de ambos frontends y backends, empaquetados en una imagen runtime (Temurin JRE + Nginx + Supervisor + SQLite).

---

## 3) Estructura del Repositorio (clave)

- `ensurance/`: Frontend de seguros (Vue 3 + Vite)
- `pharmacy/`: Frontend de farmacia (Vue 3 + Vue CLI)
- `backv4/`: Backend de seguros (Java, Hibernate, SQLite/Oracle)
- `backv5/`: Backend de farmacia (Java, Hibernate, SQLite)
- `databases/<env>/`: Datos persistentes por ambiente (`ensurance/USUARIO.sqlite`, `pharmacy/USUARIO.sqlite`)
- `logs/<env>/`: Logs por ambiente
- `Dockerfile`: Imagen unificada multi‑stage
- `docker-compose.dev.yml|qa.yml|main.yml`: Orquestación por ambiente
- `deploy.sh`: Script de despliegue multi‑ambiente
- `test-runner.sh`: Script unificado de testing y utilidades
- `Jenkinsfile`: Pipeline CI/CD por rama
- `sonar-project.properties`: Configuración SonarQube
- `documentation/`: Diagramas (PDF/JPEG) y este documento
- `README.md`: Guía de uso rápida (resumen)

---

## 4) Ambientes y Puertos

Puertos externos (host) mapeados por Compose:  
- DEV: 3000 (Ens F.E.), 3001 (Phar F.E.), 3002 (Ens API), 3003 (Phar API)
- QA:  4000, 4001, 4002, 4003
- MAIN: 5175, 8089, 8081, 8082

Puertos internos (contenedor): 5175 (Ens F.E.), 8089 (Phar F.E.), 8081 (Ens API), 8082 (Phar API).

Mapeo por archivo:
- `docker-compose.dev.yml`: `3000:5175`, `3001:8089`, `3002:8081`, `3003:8082`
- `docker-compose.qa.yml`:  `4000:5175`, `4001:8089`, `4002:8081`, `4003:8082`
- `docker-compose.main.yml`: `5175:5175`, `8089:8089`, `8081:8081`, `8082:8082`

---

## 5) Requisitos y Configuración

Requisitos:
- Node.js 18+ (frontends), Java 23 + Maven (backends), Docker y Docker Compose, SQLite, curl.

Variables de entorno típicas:
- Frontends: `VITE_ENSURANCE_API_URL`, `VITE_PHARMACY_API_URL`, `VUE_APP_ENSURANCE_API_URL`, `VUE_APP_PHARMACY_API_URL`, `VITE_IP`/`VUE_APP_IP`.
- Backends: `SERVER_HOST=0.0.0.0`, `SERVER_PORT`, `ENVIRONMENT` (`dev|qa|main`), `JAVA_OPTS`.

Los `.env` base para contenedor se generan en el Dockerfile durante el build (modo unificado). Para desarrollo local, declarar `.env` en `ensurance/` y `pharmacy/` según sea necesario.

---

## 6) Puesta en Marcha con Docker (recomendado)

Despliegue automático por rama git:

```bash
./deploy.sh auto
./deploy.sh auto --rebuild   # Fuerza reconstrucción sin caché
```

Despliegue manual por ambiente:

```bash
./deploy.sh deploy dev
./deploy.sh deploy qa
./deploy.sh deploy main
```

Utilidades:

```bash
./deploy.sh status      # Estado de contenedores
./deploy.sh logs dev    # Logs en tiempo real
./deploy.sh stop dev    # Detener ambiente
./deploy.sh clean       # Bajar y eliminar imágenes (confirmación)
```

Qué hace `deploy.sh`:
- Detecta entorno por rama (`main/master→main`, `dev/develop→dev`, `qa/test/staging→qa`).
- Libera puertos del entorno matando procesos en conflicto.
- Crea `databases/<env>` y `logs/<env>` y copia SQLite base si falta.
- Ejecuta `docker-compose -f docker-compose.<env>.yml build [--no-cache]` y `up -d`.
- Muestra URLs y ofrece logs.

---

## 7) Desarrollo Local (sin Docker)

Frontends:

```bash
# Ensurance (Vite)
cd ensurance && npm install && npm run dev   # http://localhost:5173 (por defecto de Vite)

# Pharmacy (Vue CLI)
cd pharmacy && npm install && npm run serve  # http://localhost:8080 (por defecto de Vue CLI)
```

Backends:

```bash
# Ensurance Backend (BackV4)
cd backv4 && mvn exec:java -Dexec.mainClass="com.sources.app.App"  # http://localhost:8081/api

# Pharmacy Backend (BackV5)
cd backv5 && mvn exec:java -Dexec.mainClass="com.sources.app.App"  # http://localhost:8082/api2
```

Ajustar variables de entorno de frontends para apuntar a `http://localhost:8081/api` y `http://localhost:8082/api2` cuando se ejecuten backends locales.

---

## 8) Bases de Datos

- Motor: SQLite para todos los ambientes (desarrollo y ejecución unificada en contenedor).  
- Ubicación por ambiente:
  - `databases/dev/ensurance/USUARIO.sqlite`
  - `databases/dev/pharmacy/USUARIO.sqlite`
  - `databases/qa/...`, `databases/main/...` análogo.
- Inicialización: scripts en `backv4/sqlite/` y `backv5/sqlite/`. El Dockerfile ejecuta SQL inicial si no existe el archivo.

Cambio de motor (Oracle) en BackV4: usar configs de Hibernate/Perfiles Maven provistos en `backv4` cuando corresponda.

---

## 9) CI/CD y Calidad

- Jenkins: `Jenkinsfile` define pipeline por rama (dev/qa/main) con pruebas, cobertura, análisis SonarQube y despliegue automático.
- SonarQube: configurado vía `sonar-project.properties` con cobertura combinada (JaCoCo y LCOV).
- `test-runner.sh`: menú interactivo para ejecutar pruebas, instalar dependencias y lanzar análisis.

Ejecución rápida:

```bash
./test-runner.sh           # Abrir menú interactivo
```

---

## 10) Testing

Frontends:
- Ensurance (Vitest): `npm run test`, `npm run test:run`, `npm run test:ui`.
- Pharmacy (Jest): `npm run test:unit`, `npm run test:unit:watch`, `npm run test:unit:file`.

Backends:
- BackV4: `mvn -f backv4 test` (perfiles: `-Psqlite-dev|uat|prod`, opciones de logging con `-Dsurefire.useFile=false`).
- BackV5: `mvn -f backv5 clean test jacoco:report` (reporte en `target/site/jacoco/index.html`).

Cobertura consolidada disponible vía SonarQube (local o en pipeline).

---

## 11) Troubleshooting

- Puerto ocupado: el script lo intenta resolver; manualmente `lsof -ti:3000 | xargs kill -9` (cambie el puerto según ambiente).
- Contenedor no inicia: `docker logs ensurance-pharmacy-<env>` y/o `./deploy.sh deploy <env> --rebuild`.
- BD corrupta o desactualizada: eliminar carpeta `databases/<env>` y redeploy.
- APIs no responden: validar que 8081/8082 estén levantados dentro del contenedor (supervisor y logs en `logs/<env>`).

---

## 12) Seguridad y Buenas Prácticas

- Nunca versionar secretos en `.env`; usar variables de entorno/secretos del sistema de CI.
- Limitar permisos de volúmenes `databases/` y `logs/` si se comparten en servidores.
- Validar CORS y `proxy_pass` en Nginx si se cambian orígenes.
- Revisar límites de memoria `JAVA_OPTS` por ambiente (Docker/Compose).

---

## 13) Convenciones y Flujo de Trabajo

- Ramas: `develop`/feature → `qa` → `main`.
- Despliegue: `./deploy.sh auto` según rama activa.
- Versionado: SemVer para releases en `main`.
- Estilo: linters y formateadores definidos en cada frontend; convenciones Java estándar en backends.

---

## 14) Referencias y Anexos

- Guía rápida: `README.md`
- Script despliegue: `deploy.sh`
- Orquestación: `docker-compose.dev.yml`, `docker-compose.qa.yml`, `docker-compose.main.yml`
- Imagen unificada: `Dockerfile`
- CI/CD: `Jenkinsfile`
- Calidad: `sonar-project.properties`
- Diagramas y documentos: carpeta `documentation/` (PDF/JPEG listados)

---

Última actualización: Agosto 2025

---

## 15) Comandos Esenciales

### Despliegue y Orquestación
- `./deploy.sh auto`: Detecta rama git y despliega al entorno correspondiente.
- `./deploy.sh deploy dev|qa|main`: Despliega al entorno indicado.
- `./deploy.sh deploy dev --rebuild`: Fuerza rebuild sin caché.
- `./deploy.sh status`: Muestra estado de contenedores del sistema.
- `./deploy.sh logs dev|qa|main`: Sigue logs del entorno.
- `./deploy.sh stop dev|qa|main`: Detiene el entorno.
- `./deploy.sh clean`: Baja contenedores e imágenes (confirmación interactiva).

### Docker Compose directo
- `docker-compose -f docker-compose.dev.yml up -d`: Levantar DEV.
- `docker-compose -f docker-compose.qa.yml up -d`: Levantar QA.
- `docker-compose -f docker-compose.main.yml up -d`: Levantar MAIN.
- `docker-compose -f docker-compose.dev.yml down`: Bajar DEV.

### Frontends
- `cd ensurance && npm install && npm run dev`: Ensurance (Vite) en local.
- `cd pharmacy && npm install && npm run serve`: Pharmacy (Vue CLI) en local.
- `npm run build` (en cada frontend): Build de producción.
- `npm run test` / `npm run test:unit`: Tests de frontends.

### Backends (Maven)
- `mvn -f backv4 exec:java -Dexec.mainClass="com.sources.app.App"`: Ejecutar Ensurance API local.
- `mvn -f backv5 exec:java -Dexec.mainClass="com.sources.app.App"`: Ejecutar Pharmacy API local.
- `mvn -f backv4 test`: Tests BackV4 (perfiles: `-Psqlite-dev|uat|prod`).
- `mvn -f backv5 clean test jacoco:report`: Tests + cobertura BackV5.

### Testing unificado y calidad
- `./test-runner.sh`: Menú de tests, instalación y Sonar.
- Sonar local (desde menú): opción 10.

### Salud, logs y puertos
- `curl -f http://localhost:5175 || echo "down"`: Check frontend Ensurance.
- `curl -f http://localhost:8089 || echo "down"`: Check frontend Pharmacy.
- `lsof -ti:3000 | xargs kill -9`: Liberar puerto (cambiar el número según ambiente).
- `docker logs -f ensurance-pharmacy-dev`: Ver logs del contenedor DEV.

### Git (flujo sugerido)
- `git checkout -b feature/nombre`: Nueva feature.
- `git checkout qa && git merge feature/nombre`: Promover a QA.
- `git checkout main && git merge qa`: Promover a main.
