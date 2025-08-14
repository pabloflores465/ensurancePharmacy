# 🌐 Configuración de Puertos por Ambiente

## 📋 Tabla de Puertos por Rama/Ambiente

### 🔧 DEV (branch: dev)

| Servicio               | Tipo    | Puerto   | URL                        |
| ---------------------- | ------- | -------- | -------------------------- |
| Ensurance Backend      | API     | 8081     | http://localhost:8081/api  |
| Pharmacy Backend       | API     | 8082     | http://localhost:8082/api2 |
| **Ensurance Frontend** | **Web** | **5175** | **http://localhost:5175**  |
| **Pharmacy Frontend**  | **Web** | **8089** | **http://localhost:8089**  |

### 🧪 UAT (branch: test)

| Servicio               | Tipo    | Puerto   | URL                        |
| ---------------------- | ------- | -------- | -------------------------- |
| Ensurance Backend      | API     | 9081     | http://localhost:9081/api  |
| Pharmacy Backend       | API     | 9082     | http://localhost:9082/api2 |
| **Ensurance Frontend** | **Web** | **6175** | **http://localhost:6175**  |
| **Pharmacy Frontend**  | **Web** | **9089** | **http://localhost:9089**  |

### 🚀 PROD (branch: main)

| Servicio               | Tipo    | Puerto   | URL                       |
| ---------------------- | ------- | -------- | ------------------------- |
| Ensurance Backend      | API     | 80       | http://localhost/api      |
| Pharmacy Backend       | API     | 81       | http://localhost:81/api2  |
| **Ensurance Frontend** | **Web** | **7175** | **http://localhost:7175** |
| **Pharmacy Frontend**  | **Web** | **7089** | **http://localhost:7089** |

## 🐳 Comando Docker Run Actualizado

Para exponer todos los puertos necesarios:

```bash
docker run \
  --name jenkins-docker \
  --rm \
  --detach \
  --privileged \
  --network jenkins \
  --network-alias docker \
  --env DOCKER_TLS_CERTDIR=/certs \
  --volume jenkins-docker-certs:/certs/client \
  --volume jenkins-data:/var/jenkins_home \
  --publish 2376:2376 \
  --publish 80:80 \
  --publish 81:81 \
  --publish 8081:8081 \
  --publish 8082:8082 \
  --publish 9081:9081 \
  --publish 9082:9082 \
  --publish 5175:5175 \
  --publish 6175:6175 \
  --publish 7175:7175 \
  --publish 8089:8089 \
  --publish 9089:9089 \
  --publish 7089:7089 \
  docker:dind \
  --storage-driver overlay2
```

## 🔧 Variables de Entorno por Ambiente

### DEV

```bash
ENS_BACKEND_HOST_PORT=8081
PHARM_BACKEND_HOST_PORT=8082
ENS_FRONTEND_HOST_PORT=5175
PHARM_FRONTEND_HOST_PORT=8089
DB_SCHEMA_ENSURANCE="USUARIODEV"
DB_SCHEMA_PHARMACY="FARMACIADEV"
```

### UAT

```bash
ENS_BACKEND_HOST_PORT=9081
PHARM_BACKEND_HOST_PORT=9082
ENS_FRONTEND_HOST_PORT=6175
PHARM_FRONTEND_HOST_PORT=9089
DB_SCHEMA_ENSURANCE="USUARIOUAT"
DB_SCHEMA_PHARMACY="FARMACIAUAT"
```

### PROD

```bash
ENS_BACKEND_HOST_PORT=80
PHARM_BACKEND_HOST_PORT=81
ENS_FRONTEND_HOST_PORT=7175
PHARM_FRONTEND_HOST_PORT=7089
DB_SCHEMA_ENSURANCE="USUARIO"
DB_SCHEMA_PHARMACY="FARMACIA"
```

## 🎯 Acceso Rápido por Ambiente

### 🔧 DEV

- 🛡️ Seguros: http://localhost:5175
- 💊 Farmacia: http://localhost:8089

### 🧪 UAT

- 🛡️ Seguros: http://localhost:6175
- 💊 Farmacia: http://localhost:9089

### 🚀 PROD

- 🛡️ Seguros: http://localhost:7175
- 💊 Farmacia: http://localhost:7089

## 📊 Esquemas de Base de Datos

| Ambiente | Esquema Seguros | Esquema Farmacia |
| -------- | --------------- | ---------------- |
| DEV      | `USUARIODEV`    | `FARMACIADEV`    |
| UAT      | `USUARIOUAT`    | `FARMACIAUAT`    |
| PROD     | `USUARIO`       | `FARMACIA`       |

---

_Última actualización: Enero 2025_
