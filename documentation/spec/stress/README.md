# Stress Tests

Guía rápida para ejecutar los escenarios de estrés de `Ensurance Pharmacy` utilizando los scripts automatizados disponibles en este módulo.

## Scripts Clave

- **`run-full-stress-test.sh`**: ⭐ **NUEVO** - Menú interactivo completo con usuarios configurables, integración Prometheus automática y reportes HTML
- `run-tests.sh`: menú interactivo para ejecutar pruebas K6 o JMeter y levantar servicios auxiliares.
- `view-jmeter-report.sh`: expone el reporte HTML generado por JMeter en `http://localhost:8085`.
- `view-k6-report.sh`: expone el dashboard HTML de K6 en `http://localhost:5666`.
- `cleanup-results.sh`: limpia volúmenes y resultados previos de K6 y JMeter.
- `validate-setup.sh`: verifica dependencias y conectividad antes de lanzar los tests.
- `k6/verify-prometheus-integration.sh`: verifica e inicia la integración de K6 con Prometheus.

## Ejecución Rápida (RECOMENDADO)

```bash
./run-full-stress-test.sh
```

**Características:**
- ✅ Configuración interactiva de usuarios virtuales
- ✅ Verificación automática de Prometheus
- ✅ Pregunta si quieres levantar monitoreo
- ✅ Genera reporte HTML interactivo
- ✅ Exporta métricas a Prometheus en tiempo real
- ✅ Muestra URLs de acceso a reportes

**Tipos de test disponibles:**
1. Load Test - Carga progresiva
2. Stress Test - Hasta el límite
3. Spike Test - Picos repentinos
4. Soak Test - 30 min sostenido
5. JMeter Simple Test
6. JMeter Full Test

## Ejecución Básica (Script Original)

```bash
./run-tests.sh
```

1. Selecciona la herramienta (`K6` o `JMeter`).
2. Elige el escenario o plan disponible.
3. Sigue las instrucciones para levantar backends si aún no corren.

## Ejecución Manual

### K6

```bash
cd k6/scripts
TEST_SCRIPT=load-test.js ../../run-tests.sh --k6-only
```

### JMeter

```bash
JMETER_PLAN=ensurance-full-test.jmx ./run-tests.sh --jmeter-only
```

## Resultados

- Resultados de K6: volumen Docker `scripts_k6_results`.
- Resultados de JMeter: volumen Docker `scripts_jmeter_results`.
- Usa `view-jmeter-report.sh` para abrir el dashboard JMeter.

## Integración con Prometheus (K6)

### 🎯 Reportes y Métricas - TODO EN UNO

El script `run-full-stress-test.sh` ahora incluye:

**📊 Reporte HTML Interactivo:**
- Dashboard visual con gráficos de VUs, latencias, requests/seg
- Timeline interactivo navegable
- Tablas de detalles por endpoint
- URL: http://localhost:5666

**📈 Métricas en Tiempo Real a Prometheus:**
- Exportación automática durante el test
- Métricas disponibles en Prometheus: http://localhost:9095
- Visualización en Grafana: http://localhost:3300

### 🚀 Inicio Rápido

```bash
# 1. Reconstruir imagen K6 (solo la primera vez)
cd stress/k6
docker build -t ensurance-k6-prometheus:latest -f Dockerfile.k6-prometheus .

# 2. Ejecutar tests con el script mejorado
cd ..
./run-full-stress-test.sh

# El script automáticamente:
# - Verifica si Prometheus está corriendo
# - Pregunta si quieres levantarlo
# - Ejecuta el test K6
# - Genera reporte HTML interactivo
# - Exporta métricas a Prometheus
```

### 📊 Nombres de Métricas

**Métricas principales en Prometheus:**
- `k6_http_reqs` - Total de requests HTTP
- `k6_http_req_duration{stat="p95"}` - Latencia P95
- `k6_http_req_duration{stat="p99"}` - Latencia P99
- `k6_http_req_failed` - Tasa de fallos
- `k6_vus` - Usuarios virtuales activos
- `k6_iterations` - Total de iteraciones
- `k6_data_sent` / `k6_data_received` - Throughput
- `backv4_response_time{stat="p95"}` - Métricas personalizadas BackV4
- `backv5_response_time{stat="p95"}` - Métricas personalizadas BackV5
- `successful_requests` / `failed_requests` - Contadores personalizados

📖 **Guías completas:**
- `K6_DASHBOARD_PROMETHEUS_GUIDE.md` - Guía completa con ejemplos
- `k6/K6_PROMETHEUS_METRICS.md` - Referencia de métricas
- `K6_PROMETHEUS_SETUP_COMPLETE.md` - Configuración técnica

### 🔍 Acceso Rápido

| Servicio | URL | Descripción |
|----------|-----|-------------|
| **K6 Dashboard HTML** | http://localhost:5666 | Reporte interactivo con gráficos |
| **Prometheus** | http://localhost:9095 | Métricas en tiempo real |
| **Grafana** | http://localhost:3300 | Dashboards (admin/changeme) |
| **JMeter Report** | http://localhost:8085 | Reportes JMeter |

### 📈 Queries PromQL Útiles

```promql
# Ver todas las métricas K6
{__name__=~"k6_.*"}

# Requests por segundo
rate(k6_http_reqs[1m])

# Latencia P95
k6_http_req_duration{stat="p95"}

# Tasa de errores
rate(k6_http_req_failed[1m])

# VUs activos
k6_vus

# Métricas por endpoint
k6_http_req_duration{stat="p95"} by (name)
```

### 🎯 Verificación Automática (Opcional)

Si prefieres verificar la integración manualmente:

```bash
cd stress/k6
./verify-prometheus-integration.sh
```

Este script independiente:
1. ✅ Construye la imagen de K6
2. ✅ Inicia Prometheus y Grafana
3. ✅ Ejecuta un test de verificación
4. ✅ Valida que las métricas lleguen

## Documentación Detallada

Consulta la documentación ampliada en `documentation/stress/` para encontrar:

- Guía general de la suite.
- Detalles de cada script de K6 y plan de JMeter.
- Buenas prácticas y criterios de éxito.
- **NUEVO:** `k6/K6_PROMETHEUS_METRICS.md` - Guía completa de métricas Prometheus.
