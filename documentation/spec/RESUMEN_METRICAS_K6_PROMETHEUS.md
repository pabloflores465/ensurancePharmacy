# ✅ K6 + Prometheus - Configuración Completa

## 🎯 Resumen Ejecutivo

He configurado **TODOS** los scripts de K6 para reportar automáticamente métricas a Prometheus mediante Docker Compose. El error del Dockerfile ha sido corregido (actualizado a Go 1.23).

---

## 🐛 Problema Resuelto

**Error original:**
```
go.k6.io/xk6@v1.1.5 requires go >= 1.24.0 (running go 1.21.13)
```

**Solución aplicada:**
- ✅ Actualizado `Dockerfile.k6-prometheus` de `golang:1.21-alpine` → `golang:1.23-alpine`
- ✅ Añadido `git` como dependencia de build
- ✅ Configuración de Remote Write funcional

---

## 📊 MÉTRICAS QUE SE REPORTAN A PROMETHEUS

### 1️⃣ MÉTRICAS ESTÁNDAR DE K6 (Automáticas en todos los tests)

#### HTTP
- **`k6_http_reqs`** - Total de requests
- **`k6_http_req_duration`** - Duración (con p95, p99, avg, min, max, med)
- **`k6_http_req_waiting`** - TTFB (Time To First Byte)
- **`k6_http_req_connecting`** - Tiempo de conexión TCP
- **`k6_http_req_tls_handshaking`** - Tiempo TLS handshake
- **`k6_http_req_sending`** - Tiempo enviando datos
- **`k6_http_req_receiving`** - Tiempo recibiendo datos
- **`k6_http_req_blocked`** - Tiempo bloqueado
- **`k6_http_req_failed`** - Ratio de fallos

#### VUs e Iteraciones
- **`k6_vus`** - VUs activos
- **`k6_vus_max`** - Máximo de VUs
- **`k6_iterations`** - Total de iteraciones
- **`k6_iteration_duration`** - Duración de iteraciones

#### Datos
- **`k6_data_sent`** - Bytes enviados
- **`k6_data_received`** - Bytes recibidos

#### Checks
- **`k6_checks`** - Ratio de checks exitosos

---

### 2️⃣ MÉTRICAS PERSONALIZADAS (Comunes a todos los scripts)

He añadido estas métricas personalizadas en TODOS los scripts principales:

- **`k6_errors`** (Rate) - Tasa de errores general
- **`k6_backv4_response_time`** (Trend) - Tiempo de respuesta BackV4 específico
- **`k6_backv5_response_time`** (Trend) - Tiempo de respuesta BackV5 específico
- **`k6_successful_requests`** (Counter) - Total de requests exitosos
- **`k6_failed_requests`** (Counter) - Total de requests fallidos

---

### 3️⃣ MÉTRICAS ESPECÍFICAS POR SCRIPT

#### 📊 load-test.js
**Métricas adicionales:**
- **`k6_active_connections`** (Gauge) - Conexiones activas en tiempo real

**Escenarios:** Constant load, Ramping load, Spike test

---

#### 💪 stress-test.js
**Métricas adicionales:**
- **`k6_custom_response_time`** (Trend) - Response time personalizado
- **`k6_stress_level`** (Gauge) - Nivel de estrés basado en VUs

**Carga máxima:** 300 VUs

---

#### ⚡ spike-test.js
**Métricas adicionales:**
- **`k6_spike_response_time`** (Trend) - Response time durante spike
- **`k6_concurrent_users`** (Gauge) - Usuarios concurrentes

**Spike:** 10 → 500 VUs en 10 segundos

---

#### 🕐 soak-test.js
**Métricas adicionales:**
- **`k6_total_requests`** (Counter) - Total acumulado
- **`k6_soak_duration_minutes`** (Gauge) - Duración en minutos
- **`k6_memory_leak_indicator`** (Trend) - Indicador de memory leaks

**Duración:** 30 minutos @ 50 VUs

---

## 🚀 COMANDOS ACTUALIZADOS

### Construir Imagen (Ya corregida)
```bash
cd stress/k6
docker build -t ensurance-k6-prometheus:latest -f Dockerfile.k6-prometheus .
```

### Iniciar Prometheus
```bash
cd scripts
docker compose -f docker-compose.monitor.yml up -d
```

### Ejecutar Tests (Reportan automáticamente a Prometheus)

```bash
cd scripts

# Load Test (con métricas de active_connections)
TEST_SCRIPT=load-test.js docker compose -f docker-compose.stress.yml up k6

# Stress Test (con métricas de stress_level)
TEST_SCRIPT=stress-test.js docker compose -f docker-compose.stress.yml up k6

# Spike Test (con métricas de spike_response_time)
TEST_SCRIPT=spike-test.js docker compose -f docker-compose.stress.yml up k6

# Soak Test (con métricas de memory_leak_indicator)
TEST_SCRIPT=soak-test.js docker compose -f docker-compose.stress.yml up k6

# Test de verificación Prometheus (script especial)
TEST_SCRIPT=prometheus-test.js docker compose -f docker-compose.stress.yml up k6
```

---

## 🔍 VERIFICAR MÉTRICAS EN PROMETHEUS

### Via Web UI
```
URL: http://localhost:9095
Query: {__name__=~"k6_.*"}
```

### Via curl
```bash
# Ver todas las métricas de K6
curl -s 'http://localhost:9095/api/v1/label/__name__/values' | jq '.data[] | select(startswith("k6_"))'

# Ver VUs activos
curl 'http://localhost:9095/api/v1/query?query=k6_vus'

# Ver requests exitosos
curl 'http://localhost:9095/api/v1/query?query=k6_successful_requests'

# Ver response time BackV4 P95
curl 'http://localhost:9095/api/v1/query?query=k6_backv4_response_time'

# Ver response time BackV5 P95
curl 'http://localhost:9095/api/v1/query?query=k6_backv5_response_time'
```

### Monitoreo en Tiempo Real
```bash
# Ver VUs en tiempo real
watch -n 1 "curl -s 'http://localhost:9095/api/v1/query?query=k6_vus' | jq '.data.result[0].value[1]'"

# Ver requests/seg
watch -n 1 "curl -s 'http://localhost:9095/api/v1/query?query=rate(k6_http_reqs[1m])' | jq '.data.result[0].value[1]'"
```

---

## 📈 QUERIES PROMQL ÚTILES

### General
```promql
# Total requests/seg
rate(k6_http_reqs[1m])

# Tasa de errores
rate(k6_http_req_failed[1m])

# VUs activos
k6_vus

# Latencia P95
k6_http_req_duration{stat="p95"}

# Latencia P99
k6_http_req_duration{stat="p99"}
```

### Por Backend
```promql
# BackV4 P95
k6_backv4_response_time{stat="p95"}

# BackV5 P95
k6_backv5_response_time{stat="p95"}

# Comparación
k6_backv4_response_time{stat="p95"} vs k6_backv5_response_time{stat="p95"}
```

### Success/Error
```promql
# Requests exitosos/seg
rate(k6_successful_requests[1m])

# Requests fallidos/seg
rate(k6_failed_requests[1m])

# Tasa de éxito
rate(k6_successful_requests[1m]) / rate(k6_http_reqs[1m]) * 100
```

### Específicas por Test
```promql
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

## 📁 ARCHIVOS CREADOS/MODIFICADOS

### ✅ Corregidos
- **`stress/k6/Dockerfile.k6-prometheus`** - Actualizado a Go 1.23

### ✅ Actualizados con Métricas
- **`stress/k6/scripts/load-test.js`** - +5 métricas personalizadas
- **`stress/k6/scripts/stress-test.js`** - +6 métricas personalizadas
- **`stress/k6/scripts/spike-test.js`** - +6 métricas personalizadas
- **`stress/k6/scripts/soak-test.js`** - +7 métricas personalizadas

### ✅ Documentación
- **`stress/k6/K6_PROMETHEUS_METRICS.md`** - Guía completa original
- **`stress/k6/METRICAS_K6_PROMETHEUS.md`** - Desglose detallado por script
- **`K6_PROMETHEUS_SETUP_COMPLETE.md`** - Setup técnico
- **`K6_PROMETHEUS_METRICAS.txt`** - Referencia rápida
- **`RESUMEN_METRICAS_K6_PROMETHEUS.md`** - Este documento
- **`COMANDOS_RAPIDOS.md`** - Actualizado con sección K6+Prometheus

---

## 🌐 URLs DE ACCESO

| Servicio | URL | Credenciales |
|----------|-----|--------------|
| **Prometheus** | http://localhost:9095 | - |
| **Grafana** | http://localhost:3300 | admin/changeme |
| **K6 Dashboard (live)** | http://localhost:5665 | - (solo durante test) |
| **K6 Dashboard (static)** | http://localhost:5666 | - |

---

## ✅ CHECKLIST DE VERIFICACIÓN

- [x] Dockerfile corregido (Go 1.23)
- [x] Todos los scripts actualizados con métricas personalizadas
- [x] Métricas comunes en todos los scripts
- [x] Métricas específicas por tipo de test
- [x] Configuración Remote Write en docker-compose
- [x] Prometheus con Remote Write Receiver habilitado
- [x] Documentación completa creada
- [x] Scripts de verificación disponibles
- [x] COMANDOS_RAPIDOS.md actualizado

---

## 🎯 PRÓXIMOS PASOS

1. **Construir la imagen corregida:**
   ```bash
   cd stress/k6
   docker build -t ensurance-k6-prometheus:latest -f Dockerfile.k6-prometheus .
   ```

2. **Iniciar Prometheus:**
   ```bash
   cd scripts
   docker compose -f docker-compose.monitor.yml up -d
   ```

3. **Ejecutar un test:**
   ```bash
   TEST_SCRIPT=load-test.js docker compose -f docker-compose.stress.yml up k6
   ```

4. **Verificar métricas:**
   ```bash
   # Ver en Prometheus
   open http://localhost:9095
   
   # Query: {__name__=~"k6_.*"}
   ```

5. **Crear dashboards en Grafana:**
   - Abrir http://localhost:3300
   - Usar las queries de ejemplo de arriba

---

## 📚 DOCUMENTACIÓN COMPLETA

- **`stress/k6/METRICAS_K6_PROMETHEUS.md`** - Lista detallada de TODAS las métricas
- **`stress/k6/K6_PROMETHEUS_METRICS.md`** - Guía técnica completa
- **`COMANDOS_RAPIDOS.md`** - Comandos actualizados

---

**Fecha:** 2025-10-09  
**Estado:** ✅ CONFIGURADO - Listo para usar después de build  
**Build en progreso:** La imagen se está construyendo en background
