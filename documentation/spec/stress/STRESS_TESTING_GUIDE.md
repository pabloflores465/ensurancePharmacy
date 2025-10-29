# Guía de Stress Testing - Ensurance Pharmacy

## Herramientas Configuradas

### 1. Apache JMeter
- **Plan Completo**: `ensurance-full-test.jmx`
- **Plan Simple**: `sample-plan.jmx`

### 2. K6 (Grafana)
- **Load Test**: `load-test.js` - Prueba de carga progresiva
- **Stress Test**: `stress-test.js` - Prueba de estrés hasta 300 usuarios
- **Spike Test**: `spike-test.js` - Picos repentinos de carga
- **Soak Test**: `soak-test.js` - Prueba sostenida por 30 minutos

## Uso Rápido

### Ejecutar con JMeter

```bash
cd scripts

# Test simple
docker-compose -f docker-compose.stress.yml run --rm jmeter

# Test completo con parámetros
JMETER_PLAN=ensurance-full-test.jmx \
BACKV4_URL=http://host.docker.internal:8081 \
BACKV5_URL=http://host.docker.internal:8082 \
USERS=100 \
RAMP_TIME=60 \
DURATION=600 \
docker-compose -f docker-compose.stress.yml run --rm jmeter

# Ver resultados
docker run --rm -v scripts_jmeter_results:/results alpine ls -lh /results/report
```

### Ejecutar con K6

```bash
cd scripts

# Load test
TEST_SCRIPT=load-test.js docker-compose -f docker-compose.stress.yml run --rm k6

# Stress test
TEST_SCRIPT=stress-test.js docker-compose -f docker-compose.stress.yml run --rm k6

# Spike test
TEST_SCRIPT=spike-test.js docker-compose -f docker-compose.stress.yml run --rm k6

# Soak test (30 minutos)
TEST_SCRIPT=soak-test.js docker-compose -f docker-compose.stress.yml run --rm k6

# Con URLs personalizadas
BACKV4_URL=http://10.128.0.2:8081 \
BACKV5_URL=http://10.128.0.2:8082 \
TEST_SCRIPT=load-test.js \
docker-compose -f docker-compose.stress.yml run --rm k6
```

## Visualización con Grafana

### 1. Iniciar Grafana y Prometheus

```bash
cd scripts
docker-compose -f docker-compose.monitor.yml up -d
```

### 2. Acceder al Dashboard
- URL: http://localhost:3300
- Usuario: `admin`
- Password: `changeme`
- Dashboard: "K6 Stress Testing Dashboard"

### 3. Dashboard en tiempo real durante pruebas K6
- URL: http://localhost:5665 (mientras ejecutas K6)

## Métricas Principales

### JMeter
- Response Time (p95, p99)
- Throughput (req/sec)
- Error Rate
- HTML Report en `/results/report`

### K6
- Virtual Users (VUs)
- Request Rate
- Response Time Percentiles (p50, p95, p99)
- Error Rate
- HTTP Request Duration
- Failed Checks

## Umbrales Configurados

### K6 Thresholds
- **Load Test**: p95 < 500ms, error rate < 1%
- **Stress Test**: p95 < 1000ms, error rate < 5%
- **Spike Test**: p95 < 2000ms, error rate < 10%
- **Soak Test**: p95 < 800ms, error rate < 2%

## Escenarios de Prueba

### Load Test (load-test.js)
1. Carga constante: 10 VUs por 2 minutos
2. Carga progresiva: 0 → 20 → 50 VUs
3. Spike: 0 → 100 VUs repentino

### Stress Test (stress-test.js)
- Incremento sostenido hasta 300 usuarios
- Duración: 8 minutos total
- Objetivo: Encontrar el punto de quiebre

### Spike Test (spike-test.js)
- Carga normal → Spike de 500 usuarios → Normal
- Duración: 2.5 minutos
- Objetivo: Probar recuperación

### Soak Test (soak-test.js)
- 50 usuarios constantes por 30 minutos
- Objetivo: Detectar memory leaks

## Endpoints Testeados

### BackV4 (Puerto 8081)
- `/api/health`

### BackV5 (Puerto 8082)
- `/api/health`
- `/api/usuarios`
- `/api/medicamentos`
- `/api/polizas`

## Ver Resultados

### JMeter
```bash
# HTML Report
docker run --rm -v scripts_jmeter_results:/results -p 8085:8085 \
  -w /results/report python:3.9 python -m http.server 8085

# Abrir: http://localhost:8085
```

### K6
```bash
# Ver JSON de resultados
docker run --rm -v scripts_k6_results:/results alpine cat /results/summary.json

# Dashboard exportado
docker run --rm -v scripts_k6_results:/results alpine ls -lh /results/k6-dashboard
```

## Limpieza

```bash
# Detener servicios
docker-compose -f docker-compose.stress.yml down
docker-compose -f docker-compose.monitor.yml down

# Limpiar volúmenes (CUIDADO: elimina resultados)
docker volume rm scripts_k6_results scripts_jmeter_results
```

## Notas Importantes

1. **Asegúrate de que las aplicaciones estén corriendo** antes de ejecutar tests
2. **Grafana tarda ~30 segundos** en iniciar la primera vez
3. **K6 dashboard en tiempo real** solo funciona mientras K6 está ejecutándose
4. **JMeter genera reportes HTML** automáticamente en `/results/report`
5. Para **tests largos** (soak test), considera usar `-d` para ejecutar en background

## Comandos de Verificación

```bash
# Verificar que apps estén corriendo
curl http://localhost:8081/api/health
curl http://localhost:8082/api/health

# Ver logs de stress tests
docker logs ensurance-k6
docker logs ensurance-jmeter

# Ver Prometheus
open http://localhost:9095
```
