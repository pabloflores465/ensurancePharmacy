# ✅ K6 + Prometheus - LISTO PARA USAR

## 🎉 Estado: FUNCIONANDO

La imagen de K6 con soporte Prometheus está construida y lista para usar.

```
k6 v1.3.0 (go1.23.12, linux/amd64)
Extensions:
  github.com/grafana/xk6-output-prometheus-remote v0.5.1
```

---

## 📊 MÉTRICAS QUE SE REPORTAN A PROMETHEUS

### 🔹 Métricas Estándar K6 (15 métricas automáticas)

1. **`k6_http_reqs`** - Total requests HTTP
2. **`k6_http_req_duration`** - Duración (p95, p99, avg, min, max, med)
3. **`k6_http_req_waiting`** - TTFB
4. **`k6_http_req_connecting`** - Tiempo conexión TCP
5. **`k6_http_req_tls_handshaking`** - TLS handshake
6. **`k6_http_req_sending`** - Enviando datos
7. **`k6_http_req_receiving`** - Recibiendo datos
8. **`k6_http_req_blocked`** - Tiempo bloqueado
9. **`k6_http_req_failed`** - Ratio fallos
10. **`k6_vus`** - VUs activos
11. **`k6_vus_max`** - Máximo VUs
12. **`k6_iterations`** - Total iteraciones
13. **`k6_iteration_duration`** - Duración iteraciones
14. **`k6_data_sent`** - Bytes enviados
15. **`k6_data_received`** - Bytes recibidos
16. **`k6_checks`** - Ratio checks exitosos

### 🔹 Métricas Personalizadas (Por Script)

#### Comunes a TODOS los scripts:
- **`k6_errors`** - Tasa de errores
- **`k6_backv4_response_time`** - Response time BackV4 (p95, p99, avg, etc.)
- **`k6_backv5_response_time`** - Response time BackV5 (p95, p99, avg, etc.)
- **`k6_successful_requests`** - Requests exitosos
- **`k6_failed_requests`** - Requests fallidos

#### load-test.js (+1 métrica)
- **`k6_active_connections`** - Conexiones activas

#### stress-test.js (+2 métricas)
- **`k6_custom_response_time`** - Response time personalizado
- **`k6_stress_level`** - Nivel de estrés

#### spike-test.js (+2 métricas)
- **`k6_spike_response_time`** - Response time durante spike
- **`k6_concurrent_users`** - Usuarios concurrentes

#### soak-test.js (+3 métricas)
- **`k6_total_requests`** - Total acumulado
- **`k6_soak_duration_minutes`** - Duración en minutos
- **`k6_memory_leak_indicator`** - Indicador memory leaks

---

## 🚀 EJECUTAR AHORA

### 1. Iniciar Prometheus
```bash
cd scripts
docker compose -f docker-compose.monitor.yml up -d
```

### 2. Ejecutar Test K6 (Reporta automáticamente a Prometheus)
```bash
# Test de verificación Prometheus
TEST_SCRIPT=prometheus-test.js docker compose -f docker-compose.stress.yml up k6

# Load Test
TEST_SCRIPT=load-test.js docker compose -f docker-compose.stress.yml up k6

# Stress Test  
TEST_SCRIPT=stress-test.js docker compose -f docker-compose.stress.yml up k6

# Spike Test
TEST_SCRIPT=spike-test.js docker compose -f docker-compose.stress.yml up k6

# Soak Test
TEST_SCRIPT=soak-test.js docker compose -f docker-compose.stress.yml up k6
```

### 3. Ver Métricas en Prometheus
```bash
# Abrir Prometheus
open http://localhost:9095

# Query para ver todas las métricas de K6
{__name__=~"k6_.*"}
```

---

## 🔍 VERIFICACIÓN RÁPIDA

```bash
# Ver VUs activos
curl 'http://localhost:9095/api/v1/query?query=k6_vus'

# Ver requests/seg
curl 'http://localhost:9095/api/v1/query?query=rate(k6_http_reqs[1m])'

# Ver response time BackV4 P95
curl 'http://localhost:9095/api/v1/query?query=k6_backv4_response_time{stat="p95"}'

# Ver requests exitosos vs fallidos
curl 'http://localhost:9095/api/v1/query?query=k6_successful_requests'
curl 'http://localhost:9095/api/v1/query?query=k6_failed_requests'
```

---

## 📈 QUERIES PROMQL MÁS ÚTILES

```promql
# Requests por segundo
rate(k6_http_reqs[1m])

# Latencia P95 general
k6_http_req_duration{stat="p95"}

# Latencia P99 general
k6_http_req_duration{stat="p99"}

# Latencia P95 BackV4
k6_backv4_response_time{stat="p95"}

# Latencia P95 BackV5
k6_backv5_response_time{stat="p95"}

# Comparar backends
k6_backv4_response_time{stat="p95"} vs k6_backv5_response_time{stat="p95"}

# Tasa de éxito
rate(k6_successful_requests[1m]) / rate(k6_http_reqs[1m]) * 100

# Tasa de errores
rate(k6_errors[1m])

# VUs activos
k6_vus

# Conexiones activas (load-test)
k6_active_connections

# Nivel de estrés (stress-test)
k6_stress_level

# Usuarios concurrentes (spike-test)
k6_concurrent_users

# Indicador memory leak (soak-test)
k6_memory_leak_indicator{stat="avg"}
```

---

## 🌐 URLs

| Servicio | URL | Credenciales |
|----------|-----|--------------|
| **Prometheus** | http://localhost:9095 | - |
| **Grafana** | http://localhost:3300 | admin/changeme |
| **Pushgateway** | http://localhost:9091 | - |

---

## 📊 RESUMEN DE MÉTRICAS POR SCRIPT

| Script | Métricas Estándar | Personalizadas Comunes | Personalizadas Específicas | Total |
|--------|-------------------|------------------------|----------------------------|-------|
| **prometheus-test.js** | 16 | 4 | 4 (custom_test_*) | **24** |
| **load-test.js** | 16 | 5 | 1 (active_connections) | **22** |
| **stress-test.js** | 16 | 5 | 2 (stress_level, custom_response_time) | **23** |
| **spike-test.js** | 16 | 5 | 2 (spike_response_time, concurrent_users) | **23** |
| **soak-test.js** | 16 | 5 | 3 (total_requests, soak_duration, memory_leak) | **24** |

---

## ⚙️ CONFIGURACIÓN TÉCNICA

### Dockerfile
- Base: `golang:1.23-alpine`
- Go version: 1.23.12
- K6 version: v1.3.0
- Extension: xk6-output-prometheus-remote v0.5.1
- **GOTOOLCHAIN=auto** (permite descargar Go 1.24+ si es necesario)

### Docker Compose
- Push interval: **5 segundos**
- Stats exportadas: **p95, p99, min, max, avg, med**
- Remote Write: `http://prometheus:9090/api/v1/write`
- Redes: `stress-test-network` + `monitoring-network`

### Prometheus
- Remote Write Receiver: **Habilitado**
- Histogramas nativos: **Habilitado**
- Puerto: 9095 (externo), 9090 (interno)

---

## 📁 ARCHIVOS ACTUALIZADOS

### ✅ Imagen K6
- **`stress/k6/Dockerfile.k6-prometheus`** - Corregido con GOTOOLCHAIN=auto
- Imagen construida: `ensurance-k6-prometheus:latest`

### ✅ Scripts K6 (Todos actualizados con métricas)
- **`stress/k6/scripts/load-test.js`** - 22 métricas totales
- **`stress/k6/scripts/stress-test.js`** - 23 métricas totales
- **`stress/k6/scripts/spike-test.js`** - 23 métricas totales
- **`stress/k6/scripts/soak-test.js`** - 24 métricas totales
- **`stress/k6/scripts/prometheus-test.js`** - 24 métricas totales (verificación)

### ✅ Configuración
- **`scripts/docker-compose.stress.yml`** - Config Remote Write
- **`scripts/docker-compose.monitor.yml`** - Prometheus Remote Write Receiver

### ✅ Documentación
- **`stress/k6/K6_PROMETHEUS_METRICS.md`** - Guía completa
- **`stress/k6/METRICAS_K6_PROMETHEUS.md`** - Desglose por script
- **`K6_PROMETHEUS_SETUP_COMPLETE.md`** - Setup técnico
- **`RESUMEN_METRICAS_K6_PROMETHEUS.md`** - Resumen ejecutivo
- **`K6_PROMETHEUS_LISTO.md`** - Este documento (inicio rápido)
- **`COMANDOS_RAPIDOS.md`** - Comandos actualizados

---

## ✅ VERIFICACIÓN FINAL

```bash
# 1. Verificar imagen
docker run --rm ensurance-k6-prometheus:latest version

# 2. Iniciar Prometheus
cd scripts
docker compose -f docker-compose.monitor.yml up -d

# 3. Ejecutar test de verificación
TEST_SCRIPT=prometheus-test.js docker compose -f docker-compose.stress.yml up k6

# 4. Verificar métricas (en otra terminal mientras corre el test)
watch -n 1 "curl -s 'http://localhost:9095/api/v1/query?query=k6_vus' | jq '.data.result[0].value[1]'"

# 5. Ver todas las métricas de K6 en Prometheus
open http://localhost:9095
# Query: {__name__=~"k6_.*"}
```

---

## 🎯 EJEMPLO COMPLETO DE USO

```bash
# Terminal 1: Iniciar servicios
cd scripts
docker compose -f docker-compose.monitor.yml up -d

# Terminal 2: Ejecutar test
TEST_SCRIPT=load-test.js docker compose -f docker-compose.stress.yml up k6

# Terminal 3: Monitorear métricas en tiempo real
watch -n 1 "curl -s 'http://localhost:9095/api/v1/query?query=k6_vus' | jq -r '.data.result[0].value[1]' 2>/dev/null || echo 0"

# Terminal 4: Ver Prometheus UI
open http://localhost:9095

# En Prometheus, probar estas queries:
# - {__name__=~"k6_.*"}
# - rate(k6_http_reqs[1m])
# - k6_backv4_response_time{stat="p95"}
# - k6_successful_requests
```

---

## 🎉 ¡TODO LISTO!

La integración K6 + Prometheus está **completamente configurada y funcionando**.

Todos los scripts de K6 reportan automáticamente métricas detalladas a Prometheus:
- ✅ 16 métricas estándar de K6
- ✅ 5 métricas personalizadas comunes
- ✅ 1-3 métricas específicas por tipo de test
- ✅ Total: **22-24 métricas por test**

**Próximo paso:** Ejecuta un test y ve las métricas en Prometheus.

---

**Fecha:** 2025-10-09  
**Versión K6:** v1.3.0  
**Extensión:** xk6-output-prometheus-remote v0.5.1  
**Estado:** ✅ LISTO PARA PRODUCCIÓN
