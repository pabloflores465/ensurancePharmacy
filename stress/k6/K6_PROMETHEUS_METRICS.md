# K6 Métricas de Prometheus

## Configuración Completa

La integración de k6 con Prometheus está configurada mediante:
1. **Dockerfile personalizado** (`Dockerfile.k6-prometheus`) con extensión `xk6-output-prometheus-remote`
2. **Docker Compose** configurado para enviar métricas a Prometheus vía Remote Write
3. **Prometheus** con Remote Write Receiver habilitado

## Nombres de las Métricas

### Métricas Estándar de k6

Todas las métricas de k6 tienen el prefijo `k6_` cuando se exportan a Prometheus:

#### 1. Métricas de HTTP
- **`k6_http_reqs`** (counter)
  - Total de requests HTTP realizados
  - Labels: `method`, `status`, `name`, `scenario`, `group`

- **`k6_http_req_duration`** (histogram/summary)
  - Duración de requests HTTP en milisegundos
  - Stats: `min`, `max`, `avg`, `med`, `p95`, `p99`
  - Labels: `method`, `status`, `name`, `scenario`, `group`, `expected_response`

- **`k6_http_req_waiting`** (histogram/summary)
  - Tiempo esperando la respuesta (TTFB - Time To First Byte)
  - Stats: `min`, `max`, `avg`, `med`, `p95`, `p99`

- **`k6_http_req_connecting`** (histogram/summary)
  - Tiempo estableciendo conexión TCP
  - Stats: `min`, `max`, `avg`, `med`, `p95`, `p99`

- **`k6_http_req_tls_handshaking`** (histogram/summary)
  - Tiempo en TLS handshake
  - Stats: `min`, `max`, `avg`, `med`, `p95`, `p99`

- **`k6_http_req_sending`** (histogram/summary)
  - Tiempo enviando datos
  - Stats: `min`, `max`, `avg`, `med`, `p95`, `p99`

- **`k6_http_req_receiving`** (histogram/summary)
  - Tiempo recibiendo datos
  - Stats: `min`, `max`, `avg`, `med`, `p95`, `p99`

- **`k6_http_req_blocked`** (histogram/summary)
  - Tiempo bloqueado antes de iniciar request
  - Stats: `min`, `max`, `avg`, `med`, `p95`, `p99`

- **`k6_http_req_failed`** (rate)
  - Ratio de requests fallidos
  - Labels: `method`, `status`, `name`, `scenario`, `group`

#### 2. Métricas de Usuarios Virtuales (VUs)
- **`k6_vus`** (gauge)
  - Número actual de VUs activos

- **`k6_vus_max`** (gauge)
  - Número máximo de VUs en el test

#### 3. Métricas de Iteraciones
- **`k6_iterations`** (counter)
  - Total de iteraciones completadas
  - Labels: `scenario`, `group`

- **`k6_iteration_duration`** (histogram/summary)
  - Duración de cada iteración
  - Stats: `min`, `max`, `avg`, `med`, `p95`, `p99`
  - Labels: `scenario`, `group`

#### 4. Métricas de Datos Transferidos
- **`k6_data_sent`** (counter)
  - Total de bytes enviados

- **`k6_data_received`** (counter)
  - Total de bytes recibidos

#### 5. Métricas de Checks
- **`k6_checks`** (rate)
  - Ratio de checks exitosos
  - Labels: `check`, `group`, `scenario`

### Métricas Personalizadas

Nuestros scripts incluyen métricas personalizadas:

- **`k6_errors`** (rate)
  - Tasa de errores personalizada definida en los scripts
  - Labels: `scenario`, `group`

## Queries PromQL Útiles

### Requests por Segundo
```promql
rate(k6_http_reqs[1m])
```

### Latencia P95
```promql
k6_http_req_duration{stat="p95"}
```

### Latencia P99
```promql
k6_http_req_duration{stat="p99"}
```

### Tasa de Errores
```promql
rate(k6_http_req_failed[1m])
```

### Requests por Método HTTP
```promql
sum by (method) (rate(k6_http_reqs[1m]))
```

### Requests por Status Code
```promql
sum by (status) (rate(k6_http_reqs[1m]))
```

### Usuarios Virtuales Activos
```promql
k6_vus
```

### Duración de Iteración P95 por Scenario
```promql
k6_iteration_duration{stat="p95"} by (scenario)
```

### Throughput (datos recibidos por segundo)
```promql
rate(k6_data_received[1m])
```

### Tasa de Checks Exitosos
```promql
rate(k6_checks[1m])
```

### Latencia por Endpoint
```promql
k6_http_req_duration{stat="p95"} by (name)
```

### Distribución de Status Codes
```promql
sum by (status) (k6_http_reqs)
```

## Uso

### 1. Iniciar Prometheus y Grafana
```bash
cd scripts
docker-compose -f docker-compose.monitor.yml up -d
```

### 2. Ejecutar Test de K6
```bash
# Test básico
docker-compose -f docker-compose.stress.yml up k6

# Test específico
TEST_SCRIPT=load-test.js docker-compose -f docker-compose.stress.yml up k6

# Ver logs
docker-compose -f docker-compose.stress.yml logs -f k6
```

### 3. Verificar Métricas en Prometheus

Accede a Prometheus en `http://localhost:9095` y ejecuta queries como:

```promql
{__name__=~"k6_.*"}
```

Para ver todas las métricas de k6 disponibles.

### 4. Ver en Grafana

1. Accede a Grafana: `http://localhost:3300`
2. Usuario: `admin` / Password: `changeme`
3. Importa dashboard o crea queries con las métricas anteriores

## Configuración Avanzada

### Variables de Entorno K6

En `docker-compose.stress.yml`:

- **`K6_PROMETHEUS_RW_SERVER_URL`**: URL del endpoint Prometheus Remote Write
- **`K6_PROMETHEUS_RW_PUSH_INTERVAL`**: Intervalo de envío (default: `5s`)
- **`K6_PROMETHEUS_RW_TREND_STATS`**: Estadísticas a exportar (p95, p99, min, max, avg, med)
- **`K6_PROMETHEUS_RW_TREND_AS_NATIVE_HISTOGRAM`**: Usar histogramas nativos de Prometheus

### Labels Adicionales

Puedes añadir labels personalizados en tus scripts k6:

```javascript
import { Trend } from 'k6/metrics';

const customTrend = new Trend('custom_metric', true);

export default function() {
  // Tu código aquí
  customTrend.add(100, { custom_label: 'value' });
}
```

## Troubleshooting

### Verificar que k6 envía métricas
```bash
docker logs ensurance-k6 2>&1 | grep -i prometheus
```

### Verificar que Prometheus recibe métricas
```bash
curl 'http://localhost:9095/api/v1/query?query=k6_http_reqs'
```

### Ver métricas en tiempo real
```bash
# En una terminal
docker-compose -f docker-compose.stress.yml up k6

# En otra terminal
watch -n 1 "curl -s 'http://localhost:9095/api/v1/query?query=k6_vus' | jq '.data.result[0].value[1]'"
```

## Notas Importantes

1. **Red Docker**: El servicio k6 está conectado tanto a `stress-test-network` como a `monitoring-network`
2. **Remote Write**: Prometheus tiene habilitado `--web.enable-remote-write-receiver`
3. **Persistencia**: Las métricas se almacenan en el volumen `prometheus_data`
4. **Resolución**: Las métricas se envían cada 5 segundos por defecto
5. **Limpieza**: Al terminar el test, las métricas permanecen en Prometheus según su retention policy
