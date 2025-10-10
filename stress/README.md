# Stress Tests

Guía rápida para ejecutar los escenarios de estrés de `Ensurance Pharmacy` utilizando los scripts automatizados disponibles en este módulo.

## Scripts Clave

- `run-tests.sh`: menú interactivo para ejecutar pruebas K6 o JMeter y levantar servicios auxiliares.
- `view-jmeter-report.sh`: expone el reporte HTML generado por JMeter en `http://localhost:8085`.
- `cleanup-results.sh`: limpia volúmenes y resultados previos de K6 y JMeter.
- `validate-setup.sh`: verifica dependencias y conectividad antes de lanzar los tests.
- `k6/verify-prometheus-integration.sh`: **NUEVO** - Verifica e inicia la integración de K6 con Prometheus.

## Ejecución Rápida

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

### 🎯 Verificación Automática

La forma más rápida de verificar que K6 está enviando métricas a Prometheus:

```bash
cd stress/k6
./verify-prometheus-integration.sh
```

Este script:
1. ✅ Construye la imagen de K6 con soporte Prometheus
2. ✅ Inicia Prometheus y Grafana
3. ✅ Ejecuta un test de verificación
4. ✅ Valida que las métricas lleguen a Prometheus

### 📊 Nombres de Métricas

Para ver solo la lista de métricas disponibles:

```bash
./verify-prometheus-integration.sh --metrics-only
```

**Métricas principales:**
- `k6_http_reqs` - Total de requests HTTP
- `k6_http_req_duration` - Duración de requests (p95, p99, avg, etc.)
- `k6_http_req_failed` - Tasa de fallos
- `k6_vus` - Usuarios virtuales activos
- `k6_iterations` - Total de iteraciones
- `k6_data_sent` / `k6_data_received` - Throughput

📖 **Documentación completa:** `k6/K6_PROMETHEUS_METRICS.md`

### 🔍 Acceso Rápido

- **Prometheus:** http://localhost:9095
  - Query: `{__name__=~"k6_.*"}`
- **Grafana:** http://localhost:3300
  - Usuario: `admin` / Password: `changeme`
- **K6 Dashboard:** http://localhost:5666

### 🚀 Ejecución Manual con Prometheus

```bash
# 1. Iniciar monitoreo
cd ../scripts
docker-compose -f docker-compose.monitor.yml up -d

# 2. Ejecutar test de K6
TEST_SCRIPT=load-test.js docker-compose -f docker-compose.stress.yml up k6

# 3. Verificar métricas
curl 'http://localhost:9095/api/v1/query?query=k6_http_reqs'
```

## Documentación Detallada

Consulta la documentación ampliada en `documentation/stress/` para encontrar:

- Guía general de la suite.
- Detalles de cada script de K6 y plan de JMeter.
- Buenas prácticas y criterios de éxito.
- **NUEVO:** `k6/K6_PROMETHEUS_METRICS.md` - Guía completa de métricas Prometheus.
