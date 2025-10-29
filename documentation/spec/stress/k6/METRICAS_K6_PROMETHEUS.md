# 📊 Métricas K6 que se Reportan Automáticamente a Prometheus

## ✅ Estado: CONFIGURADO Y ACTIVO

Todos los scripts de K6 ahora reportan métricas automáticamente a Prometheus vía Remote Write.

---

## 📈 MÉTRICAS ESTÁNDAR DE K6 (Automáticas)

Estas métricas se generan automáticamente en todos los tests:

### HTTP Requests
- **`k6_http_reqs`** (Counter) - Total de requests HTTP
- **`k6_http_req_duration`** (Trend) - Duración total de requests (con p95, p99, avg, min, max, med)
- **`k6_http_req_waiting`** (Trend) - Tiempo esperando respuesta (TTFB)
- **`k6_http_req_connecting`** (Trend) - Tiempo estableciendo conexión TCP
- **`k6_http_req_tls_handshaking`** (Trend) - Tiempo en TLS handshake
- **`k6_http_req_sending`** (Trend) - Tiempo enviando datos
- **`k6_http_req_receiving`** (Trend) - Tiempo recibiendo datos
- **`k6_http_req_blocked`** (Trend) - Tiempo bloqueado antes del request
- **`k6_http_req_failed`** (Rate) - Ratio de requests fallidos

### Virtual Users (VUs)
- **`k6_vus`** (Gauge) - Número actual de VUs activos
- **`k6_vus_max`** (Gauge) - Número máximo de VUs

### Iteraciones
- **`k6_iterations`** (Counter) - Total de iteraciones completadas
- **`k6_iteration_duration`** (Trend) - Duración de cada iteración

### Datos
- **`k6_data_sent`** (Counter) - Total de bytes enviados
- **`k6_data_received`** (Counter) - Total de bytes recibidos

### Checks
- **`k6_checks`** (Rate) - Ratio de checks exitosos

---

## 🎯 MÉTRICAS PERSONALIZADAS (Por Script)

### ✅ TODAS LOS SCRIPTS (load-test.js, stress-test.js, spike-test.js, soak-test.js)

#### Métricas Comunes en Todos:
- **`k6_errors`** (Rate) - Tasa de errores general
- **`k6_backv4_response_time`** (Trend) - Tiempo de respuesta específico de BackV4
- **`k6_backv5_response_time`** (Trend) - Tiempo de respuesta específico de BackV5
- **`k6_successful_requests`** (Counter) - Total de requests exitosos
- **`k6_failed_requests`** (Counter) - Total de requests fallidos

---

### 📊 load-test.js

**Métricas adicionales:**
- **`k6_active_connections`** (Gauge) - Conexiones activas en tiempo real

**Escenarios:**
- Constant load (10 VUs por 2 min)
- Ramping load (0→50 VUs)
- Spike test (0→100 VUs)

**Labels disponibles:**
- `scenario`: constant_load, ramping_load, spike_test
- `group`: BackV4 - API Operations, BackV5 - API Operations
- `method`: GET
- `status`: 200, 404, 500, etc.

---

### 💪 stress-test.js

**Métricas adicionales:**
- **`k6_custom_response_time`** (Trend) - Tiempo de respuesta personalizado
- **`k6_stress_level`** (Gauge) - Nivel de estrés basado en VUs

**Carga máxima:**
- Ramp up: 50 → 100 → 200 → 300 VUs
- Duración: 8 minutos total

**Endpoints testeados:**
- BackV4: `/api/users`
- BackV5: `/api2/users`, `/api2/medicines`, `/api2/orders`, `/api2/categories` (batch)

**Labels disponibles:**
- `group`: BackV4 - Stress Test, BackV5 - Stress Test

---

### ⚡ spike-test.js

**Métricas adicionales:**
- **`k6_spike_response_time`** (Trend) - Tiempo de respuesta durante spike
- **`k6_concurrent_users`** (Gauge) - Usuarios concurrentes

**Patrón de carga:**
- Normal: 10 VUs
- Spike repentino: 500 VUs en 10 segundos
- Mantener: 500 VUs por 1 minuto
- Recuperación: 10 VUs

**Endpoints aleatorios:**
- BackV4: `/api/users`, `/api/policy`
- BackV5: `/api2/users`, `/api2/medicines`, `/api2/orders`

**Labels disponibles:**
- `endpoint`: BackV4 Users, BackV4 Policy, BackV5 Users, etc.
- `backend`: backv4, backv5

---

### 🕐 soak-test.js

**Métricas adicionales:**
- **`k6_total_requests`** (Counter) - Total acumulado de requests
- **`k6_soak_duration_minutes`** (Gauge) - Duración del test en minutos
- **`k6_memory_leak_indicator`** (Trend) - Indicador de degradación/memory leaks

**Duración:**
- Ramp up: 2 minutos → 50 VUs
- Soak: 30 minutos @ 50 VUs
- Ramp down: 2 minutos → 0 VUs

**Journey simulado:**
- BackV4: users → policy
- BackV5: users → medicines → orders → categories

**Labels disponibles:**
- `group`: User Journey - BackV4, User Journey - BackV5

---

## 🔍 QUERIES PROMQL POR SCRIPT

### load-test.js

```promql
# Requests exitosos vs fallidos
sum(rate(k6_successful_requests[1m]))
sum(rate(k6_failed_requests[1m]))

# Conexiones activas
k6_active_connections

# Response time por backend
k6_backv4_response_time{stat="p95"}
k6_backv5_response_time{stat="p95"}

# Tasa de errores por scenario
rate(k6_errors[1m]) by (scenario)
```

### stress-test.js

```promql
# Nivel de estrés actual
k6_stress_level

# Response time bajo estrés
k6_custom_response_time{stat="p99"}

# Requests exitosos durante stress
rate(k6_successful_requests[1m])

# Comparar backends bajo estrés
k6_backv4_response_time{stat="p95"} vs k6_backv5_response_time{stat="p95"}
```

### spike-test.js

```promql
# Response time durante spike
k6_spike_response_time{stat="p99"}

# Usuarios concurrentes
k6_concurrent_users

# Tasa de errores durante spike
rate(k6_errors[1m])

# Performance por backend durante spike
k6_backv4_response_time{stat="max"} vs k6_backv5_response_time{stat="max"}
```

### soak-test.js

```promql
# Total de requests acumulados
k6_total_requests

# Duración del soak test
k6_soak_duration_minutes

# Indicador de memory leak
k6_memory_leak_indicator{stat="avg"}

# Degradación de performance over time
rate(k6_backv4_response_time{stat="avg"}[5m])
rate(k6_backv5_response_time{stat="avg"}[5m])

# Tasa de errores sostenida
rate(k6_errors[5m])
```

---

## 📊 DASHBOARDS RECOMENDADOS EN GRAFANA

### Panel 1: Overview
- VUs activos: `k6_vus`
- Total requests: `sum(rate(k6_http_reqs[1m]))`
- Error rate: `rate(k6_http_req_failed[1m])`
- Success rate: `rate(k6_successful_requests[1m]) / rate(k6_http_reqs[1m])`

### Panel 2: Performance
- P95 latency: `k6_http_req_duration{stat="p95"}`
- P99 latency: `k6_http_req_duration{stat="p99"}`
- Avg latency: `k6_http_req_duration{stat="avg"}`
- Max latency: `k6_http_req_duration{stat="max"}`

### Panel 3: Backend Comparison
- BackV4 P95: `k6_backv4_response_time{stat="p95"}`
- BackV5 P95: `k6_backv5_response_time{stat="p95"}`
- BackV4 requests: `sum(rate(k6_http_reqs{url=~".*8081.*"}[1m]))`
- BackV5 requests: `sum(rate(k6_http_reqs{url=~".*8082.*"}[1m]))`

### Panel 4: Errors y Checks
- Failed requests: `rate(k6_failed_requests[1m])`
- Check success rate: `rate(k6_checks[1m])`
- Error rate: `rate(k6_errors[1m])`

### Panel 5: Throughput
- Data sent: `rate(k6_data_sent[1m])`
- Data received: `rate(k6_data_received[1m])`
- Requests/sec: `rate(k6_http_reqs[1m])`

---

## 🚀 USO RÁPIDO

### 1. Iniciar Prometheus
```bash
cd scripts
docker compose -f docker-compose.monitor.yml up -d
```

### 2. Ejecutar cualquier test
```bash
# Load test
TEST_SCRIPT=load-test.js docker compose -f docker-compose.stress.yml up k6

# Stress test
TEST_SCRIPT=stress-test.js docker compose -f docker-compose.stress.yml up k6

# Spike test
TEST_SCRIPT=spike-test.js docker compose -f docker-compose.stress.yml up k6

# Soak test
TEST_SCRIPT=soak-test.js docker compose -f docker-compose.stress.yml up k6
```

### 3. Ver métricas en Prometheus
```bash
# Abrir Prometheus
open http://localhost:9095

# Query de ejemplo
{__name__=~"k6_.*"}
```

### 4. Ver métricas específicas
```bash
curl 'http://localhost:9095/api/v1/query?query=k6_http_reqs'
curl 'http://localhost:9095/api/v1/query?query=k6_backv4_response_time'
curl 'http://localhost:9095/api/v1/query?query=k6_successful_requests'
```

---

## ✅ VERIFICACIÓN

Para verificar que las métricas se están reportando correctamente:

```bash
# Durante la ejecución de un test, en otra terminal:
watch -n 1 "curl -s 'http://localhost:9095/api/v1/query?query=k6_vus' | jq '.data.result[0].value[1]'"

# O verificar todas las métricas:
curl -s 'http://localhost:9095/api/v1/label/__name__/values' | jq '.data[] | select(startswith("k6_"))'
```

---

## 📝 NOTAS TÉCNICAS

### Configuración
- **Push interval**: 5 segundos
- **Retention**: Según configuración de Prometheus (default: 15 días)
- **Stats exportadas**: p95, p99, min, max, avg, med
- **Transport**: Remote Write vía HTTP

### Labels Automáticos
Todas las métricas incluyen labels útiles para filtrar:
- `scenario`: Nombre del escenario K6
- `group`: Nombre del grupo K6
- `method`: Método HTTP (GET, POST, etc.)
- `status`: HTTP status code
- `url`: URL del request
- `name`: Nombre descriptivo del request
- `expected_response`: true/false

### Persistencia
Las métricas persisten en Prometheus según su retention policy. Se recomienda:
- Development: 7 días
- Production: 30-90 días
- Backup regular de volumen `prometheus_data`

---

**Última actualización**: 2025-10-09  
**Autor**: Sistema de Monitoreo Ensurance Pharmacy  
**Estado**: ✅ ACTIVO Y VERIFICADO
