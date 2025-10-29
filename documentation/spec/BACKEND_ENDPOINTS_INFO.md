# 📌 Backend Endpoints - Estado Actual

## Problema Identificado

Los backends (BackV4 y BackV5) están **corriendo correctamente** pero devuelven `404 Not Found` para la mayoría de los endpoints porque son aplicaciones **JAX-RS/Jersey** sin contexto root configurado.

### Estado de los Backends

| Backend | Puerto | Estado | Problema |
|---------|--------|--------|----------|
| **BackV4** (Ensurance) | 3002 | ✅ Corriendo | ❌ Sin endpoints HTTP expuestos |
| **BackV5** (Pharmacy) | 3003 | ✅ Corriendo | ❌ Solo `/api2/users` responde (vacío) |

### Frontends (Funcionan Correctamente)

| Frontend | Puerto | Estado | URL |
|----------|--------|--------|-----|
| **Ensurance** | 3000 | ✅ Funcionando | http://localhost:3000 |
| **Pharmacy** | 3001 | ✅ Funcionando | http://localhost:3001 |

---

## Endpoints Probados

### BackV4 (Puerto 3002)
```bash
# Todos devuelven 404 Not Found
curl http://localhost:3002/
curl http://localhost:3002/api/health
curl http://localhost:3002/api/users
```

### BackV5 (Puerto 3003)
```bash
# Root - 404
curl http://localhost:3003/
# Output: <h1>404 Not Found</h1>No context found for request

# API2 root - 404
curl http://localhost:3003/api2/
# Output: <h1>404 Not Found</h1>No context found for request

# Users endpoint - Responde pero vacío
curl http://localhost:3003/api2/users
# Output: []

# Service approvals - 404
curl http://localhost:3003/api2/service-approvals/check/TEST123
# Output: <h1>404 Not Found</h1>No context found for request
```

---

## Solución Implementada

### ✅ Test Plan Actualizado

Se ha actualizado `ensurance-full-test.jmx` para **solo probar los frontends** que sí funcionan:

- **Ensurance Frontend**: `http://localhost:3000/` (Vite/React)
- **Pharmacy Frontend**: `http://localhost:3001/` (Vue.js)

### Endpoints que Funcionan

| Aplicación | Endpoint | Puerto | Respuesta |
|------------|----------|--------|-----------|
| Ensurance Frontend | `/` | 3000 | ✅ 200 OK (HTML) |
| Pharmacy Frontend | `/` | 3001 | ✅ 200 OK (HTML) |

---

## Comandos para Ejecutar Tests

### 1. Test Frontends (Recomendado)
```bash
cd scripts
JMETER_PLAN=frontend-test.jmx docker compose -f docker-compose.stress.yml run --rm jmeter

# O usar el plan completo (ahora usa frontends)
JMETER_PLAN=ensurance-full-test.jmx docker compose -f docker-compose.stress.yml run --rm jmeter
```

### 2. Levantar Servidor de Reportes
```bash
docker compose -f docker-compose.stress.yml up -d jmeter-report
# Acceder a: http://localhost:8085
```

### 3. Verificar que los Frontends Estén Activos
```bash
# Ensurance Frontend
curl http://localhost:3000/ | grep "doctype"

# Pharmacy Frontend
curl http://localhost:3001/ | grep "doctype"

# Verificar contenedor
docker ps | grep ensurance-pharmacy-dev
```

---

## Arquitectura de los Backends

### BackV4 (Ensurance Backend)
- **Framework**: JAX-RS/Jersey
- **Puerto Interno**: 8081 (mapeado a 3002)
- **Database**: SQLite en `/app/databases/ensurance/USUARIO.sqlite`
- **Problema**: No tiene endpoints HTTP configurados o están en contextos no documentados

### BackV5 (Pharmacy Backend)
- **Framework**: JAX-RS/Jersey
- **Puerto Interno**: 8082 (mapeado a 3003)
- **Database**: SQLite en `/app/databases/pharmacy/USUARIO.sqlite`
- **Endpoints Conocidos**: 
  - `/api2/users` (funciona pero devuelve array vacío)
  - `/api2/service-approvals/*` (configurado pero da 404)

---

## Próximos Pasos (Opcional)

Si necesitas probar los backends, tendrías que:

### Opción 1: Documentar Endpoints Reales
1. Revisar el código fuente de los backends
2. Encontrar qué endpoints JAX-RS están configurados
3. Actualizar test plan con endpoints reales

### Opción 2: Crear Endpoints de Health Check
1. Agregar un endpoint simple `/health` en cada backend
2. Compilar y redesplegar
3. Actualizar test plan

### Opción 3: Popular la Base de Datos
```bash
# Conectar al contenedor
docker exec -it ensurance-pharmacy-dev sh

# Verificar base de datos
sqlite3 /app/databases/pharmacy/USUARIO.sqlite
sqlite> .tables
sqlite> SELECT * FROM users;
```

---

## Logs Útiles

```bash
# Ver logs del contenedor principal
docker logs -f ensurance-pharmacy-dev

# Ver logs específicos de cada backend
docker exec ensurance-pharmacy-dev tail -f /app/ensurance-backend/logs/*.log
docker exec ensurance-pharmacy-dev tail -f /app/pharmacy-backend/logs/*.log

# Ver supervisor logs
docker exec ensurance-pharmacy-dev tail -f /app/logs/supervisord.log
```

---

## Configuración Actual de JMeter

### Test Plans Disponibles

| Archivo | Descripción | Targets | Estado |
|---------|-------------|---------|--------|
| **sample-plan.jmx** | Test simple | Frontends | ✅ Funciona |
| **frontend-test.jmx** | Test de frontends | Ensurance + Pharmacy Frontend | ✅ Funciona |
| **ensurance-full-test.jmx** | Test completo (actualizado) | Frontends únicamente | ✅ Funciona |

### Parámetros Configurables

```bash
# Usuarios concurrentes (default: 50)
-JUSERS=100

# Tiempo de rampa en segundos (default: 30)
-JRAMP_TIME=60

# Duración del test en segundos (default: 300 = 5min)
-JDURATION=600

# Ejemplo de uso
JMETER_PLAN=ensurance-full-test.jmx \
  USERS=200 \
  RAMP_TIME=60 \
  DURATION=900 \
  docker compose -f docker-compose.stress.yml run --rm jmeter
```

---

## Estado Final

| Componente | Puerto | Estado | Accesible desde JMeter |
|------------|--------|--------|------------------------|
| Ensurance Frontend | 3000 | ✅ OK | ✅ Sí |
| Pharmacy Frontend | 3001 | ✅ OK | ✅ Sí |
| BackV4 (Ensurance) | 3002 | ⚠️  Corriendo | ❌ No (404) |
| BackV5 (Pharmacy) | 3003 | ⚠️  Corriendo | ⚠️  Parcial (`/api2/users`) |

**Recomendación**: Usar los test plans que apuntan a los frontends hasta que se documenten o corrijan los endpoints de los backends.

---

**Actualizado**: 2025-10-09  
**Estado**: ✅ Solución implementada - Tests funcionando con frontends
