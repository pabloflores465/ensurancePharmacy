# ✅ Configuración de Monitoreo Completada

## 🎯 Objetivo Cumplido

El sistema de monitoreo ha sido configurado para:
- ✅ **Monitorear las mismas instancias** que los stress tests (puertos 3000-3003)
- ✅ **Detectar automáticamente** si las aplicaciones están corriendo
- ✅ **Levantar instancias propias** si no hay aplicaciones disponibles
- ✅ **Exponer métricas** en los puertos 9464-9467

---

## 📝 Cambios Realizados

### 1. **docker-compose.monitor.yml** ✅
- ✅ Agregado servicio `ensurance-pharmacy-apps` (opcional con profile `with-apps`)
- ✅ Configurado para exponer puertos 3000-3003 (apps) y 9464-9467 (métricas)
- ✅ Agregado `host.docker.internal` a Prometheus para acceder a apps externas
- ✅ Configurada red `monitoring-network` para comunicación entre servicios

### 2. **prometheus.yml** ✅
- ✅ Actualizado para monitorear instancias en puertos correctos (9464-9467)
- ✅ Configuración dual: intenta contenedor interno y `host.docker.internal`
- ✅ Labels agregados para identificar tipo de instancia (`container` vs `host`)

### 3. **Dockerfile** ✅
- ✅ Puertos de métricas expuestos (9464-9467)
- ✅ Servicios de métricas agregados a supervisor:
  - `ensurance-metrics` (puerto 9466)
  - `pharmacy-metrics` (puerto 9467)
- ✅ Variables de entorno `METRICS_HOST` y `METRICS_PORT` configuradas
- ✅ Scripts de inicio actualizados para mostrar endpoints de métricas

### 4. **start-monitoring.sh** ✅ (Nuevo)
- ✅ Script inteligente que detecta instancias corriendo
- ✅ Verifica puertos de aplicación (3000-3003)
- ✅ Verifica endpoints de métricas (9464-9467)
- ✅ Decide automáticamente si levantar apps o solo monitoreo
- ✅ Muestra resumen completo con URLs de acceso

### 5. **Documentación** ✅
- ✅ `COMANDOS_RAPIDOS.md` actualizado con nuevos comandos
- ✅ `MONITORING_INTEGRATION.md` creado con guía completa
- ✅ `SETUP_COMPLETE.md` (este archivo) con resumen de cambios

---

## 🚀 Cómo Usar

### Opción 1: Automático (Recomendado) ⭐

```bash
cd scripts
./start-monitoring.sh
```

**El script detecta automáticamente:**
- Si hay apps en puertos 3000-3003
- Si hay métricas en puertos 9464-9467
- Si faltan, levanta instancias propias

### Opción 2: Monitorear Instancias Existentes

```bash
# 1. Levantar apps DEV
cd scripts
docker compose -f docker-compose.dev.yml up -d

# 2. Levantar solo monitoreo
docker compose -f docker-compose.monitor.yml up -d
```

### Opción 3: Levantar Todo Junto

```bash
cd scripts
docker compose -f docker-compose.monitor.yml --profile with-apps up -d
```

---

## 🔌 Puertos Configurados

### Aplicaciones
| Servicio | Puerto | URL |
|----------|--------|-----|
| Ensurance Frontend | 3000 | http://localhost:3000 |
| Pharmacy Frontend | 3001 | http://localhost:3001 |
| Backend v4 (Ensurance) | 3002 | http://localhost:3002/api |
| Backend v5 (Pharmacy) | 3003 | http://localhost:3003/api2 |

### Métricas
| Servicio | Puerto | URL |
|----------|--------|-----|
| Backend v5 | 9464 | http://localhost:9464/metrics |
| Backend v4 | 9465 | http://localhost:9465/metrics |
| Ensurance Frontend | 9466 | http://localhost:9466/metrics |
| Pharmacy Frontend | 9467 | http://localhost:9467/metrics |

### Monitoreo
| Servicio | Puerto | URL | Credenciales |
|----------|--------|-----|--------------|
| Grafana | 3300 | http://localhost:3300 | admin / changeme |
| Prometheus | 9095 | http://localhost:9095 | - |
| CheckMK | 5150 | http://localhost:5150 | ensurance / changeme |
| Pushgateway | 9091 | http://localhost:9091 | - |

---

## 🎓 Flujo de Integración

```
┌─────────────────────────────────────────────────────────────┐
│                   STRESS TESTS                              │
│                                                             │
│  K6 / JMeter → http://host.docker.internal:3002 (Backend)  │
│             → http://host.docker.internal:3003 (Backend)   │
└─────────────────────────┬───────────────────────────────────┘
                          │
                          │ Mismas instancias
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                   MONITOREO                                 │
│                                                             │
│  Prometheus → host.docker.internal:9464/metrics (Backend)  │
│            → host.docker.internal:9465/metrics (Backend)   │
│            → host.docker.internal:9466/metrics (Frontend)  │
│            → host.docker.internal:9467/metrics (Frontend)  │
│                                                             │
│  Grafana → Visualiza métricas de Prometheus                │
└─────────────────────────────────────────────────────────────┘
```

---

## ✅ Verificación

### 1. Verificar Apps Corriendo

```bash
# Verificar contenedores
docker ps | grep ensurance

# Verificar puertos
curl http://localhost:3002/api/users
curl http://localhost:3003/api2/users
```

### 2. Verificar Métricas Disponibles

```bash
# Backend v5
curl http://localhost:9464/metrics

# Backend v4
curl http://localhost:9465/metrics

# Frontend Ensurance
curl http://localhost:9466/metrics

# Frontend Pharmacy
curl http://localhost:9467/metrics
```

### 3. Verificar Prometheus Targets

Ir a: http://localhost:9095/targets

**Todos los targets deben estar en verde (UP)**

### 4. Verificar Grafana

1. Ir a: http://localhost:3300
2. Login: admin / changeme
3. Ir a Explore
4. Ejecutar query: `http_requests_total`
5. Deberías ver métricas de todos los servicios

---

## 🔥 Caso de Uso: Stress Test con Monitoreo

```bash
# Terminal 1: Levantar sistema completo
cd scripts
./start-monitoring.sh

# Terminal 2: Ejecutar stress test
cd scripts
TEST_SCRIPT=stress-test.js docker compose -f docker-compose.stress.yml run --rm k6

# Browser: Ver métricas en tiempo real
# http://localhost:3300 (Grafana)
# http://localhost:5666/k6-dashboard (K6 Dashboard)
```

---

## 📊 Métricas Disponibles en Prometheus

### Backend Métricas
```promql
# Total de requests
http_requests_total

# Duración de requests (histograma)
http_request_duration_seconds

# Requests activos
http_requests_in_flight

# Requests por endpoint
http_requests_total{endpoint="/api/users"}
```

### Frontend Métricas
```promql
# Requests al dev server
vite_dev_server_requests

# CPU usage
node_process_cpu_seconds_total

# Memoria usada
node_process_memory_bytes
```

---

## 🛠️ Troubleshooting

### Problema: "Prometheus no muestra métricas"

**Solución:**
```bash
# 1. Verificar que las apps están corriendo
curl http://localhost:9464/metrics

# 2. Verificar targets en Prometheus
# http://localhost:9095/targets

# 3. Si targets están DOWN, verificar network
docker network inspect monitoring-network
```

### Problema: "Port already in use"

**Solución:**
```bash
# Ver qué está usando el puerto
lsof -i :3002

# Opción 1: Usar el script (detecta automáticamente)
./start-monitoring.sh

# Opción 2: Detener servicios anteriores
docker compose -f docker-compose.dev.yml down
```

### Problema: "No data in Grafana"

**Solución:**
```bash
# 1. Verificar datasource
# http://localhost:3300/datasources

# 2. Probar query en Prometheus directamente
# http://localhost:9095/graph
# Query: up

# 3. Verificar que Prometheus está scrapeando
docker logs ensurance-prometheus | grep "scrape"
```

---

## 📚 Documentación Adicional

- **Uso general:** `/COMANDOS_RAPIDOS.md`
- **Integración completa:** `/monitoring/MONITORING_INTEGRATION.md`
- **Stress tests:** `/stress/K6_SOLUTION_SUMMARY.md`

---

## 🎉 Estado Final

✅ **Sistema de monitoreo configurado**
✅ **Integración con stress tests completada**
✅ **Detección automática de instancias**
✅ **Documentación completa**
✅ **Scripts de automatización listos**

---

## 📞 Próximos Pasos

1. ✅ Levantar monitoreo: `./start-monitoring.sh`
2. ✅ Verificar métricas: http://localhost:9095/targets
3. ✅ Abrir Grafana: http://localhost:3300
4. ✅ Ejecutar stress test y ver métricas en tiempo real
5. ✅ Explorar dashboards en Grafana

---

**¡Sistema listo para usar! 🚀**
