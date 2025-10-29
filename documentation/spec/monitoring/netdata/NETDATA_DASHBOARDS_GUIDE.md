# 📊 Guía de Dashboards Netdata - Ensurance Pharmacy

## 🚀 Acceso a Netdata

**URL Principal:** http://localhost:19999

Netdata proporciona monitoreo en tiempo real con dashboards interactivos que replican y mejoran las visualizaciones de Grafana.

---

## 📈 Dashboard 1: K6 Stress Testing Metrics

### Ubicación en Netdata
Navega a: **Menu → Prometheus → k6_metrics**

### Métricas Principales Replicadas

#### 1. **Virtual Users (VUs)**
- **Métrica Prometheus:** `k6_vus`
- **En Netdata:** Buscar "k6_vus" en el panel de búsqueda
- **Descripción:** Usuarios virtuales activos durante el test
- **Dashboard Original:** Virtual Users panel (Grafana)

#### 2. **Request Rate (Req/sec)**
- **Métrica Prometheus:** `rate(k6_http_reqs[1m])`
- **En Netdata:** Buscar "k6_http_reqs"
- **Descripción:** Tasa de requests HTTP por segundo
- **Dashboard Original:** Request Rate panel (Grafana)
- **Cálculo:** Netdata muestra automáticamente la tasa por segundo

#### 3. **Response Time Percentiles**
- **Métricas Prometheus:**
  - `k6_http_req_duration{quantile="0.95"}` - P95
  - `k6_http_req_duration{quantile="0.99"}` - P99
  - `k6_http_req_duration{quantile="0.5"}` - P50 (median)
- **En Netdata:** Buscar "k6_http_req_duration"
- **Unidad:** Milisegundos (ms)
- **Umbrales:**
  - ✅ Verde: < 500ms
  - ⚠️ Amarillo: 500-1000ms
  - 🔴 Rojo: > 1000ms

#### 4. **Error Rate**
- **Métrica Prometheus:** `k6_http_req_failed`
- **En Netdata:** Buscar "k6_http_req_failed"
- **Descripción:** Porcentaje de requests fallidos
- **Umbrales:**
  - ✅ Verde: < 1%
  - ⚠️ Amarillo: 1-5%
  - 🔴 Rojo: > 5%

#### 5. **Total HTTP Requests**
- **Métrica Prometheus:** `k6_http_reqs`
- **En Netdata:** Buscar "k6_http_reqs"
- **Descripción:** Contador total de requests HTTP

#### 6. **Failed Checks**
- **Métrica Prometheus:** `k6_checks{result="fail"}`
- **En Netdata:** Buscar "k6_checks"
- **Descripción:** Número de checks que fallaron
- **Umbral Crítico:** > 0 checks fallidos

#### 7. **Total Iterations**
- **Métrica Prometheus:** `k6_iterations`
- **En Netdata:** Buscar "k6_iterations"
- **Descripción:** Total de iteraciones completadas por los VUs

### 🎯 Cómo Visualizar en Netdata

1. **Abrir Netdata:** http://localhost:19999
2. **Buscar métricas K6:**
   - Usa el campo de búsqueda superior
   - Escribe "k6_" para ver todas las métricas de K6
3. **Crear Vista Personalizada:**
   - Click en el ícono de favoritos ⭐ en cada gráfico
   - Agrupa los gráficos de K6 juntos
4. **Alertas Configuradas:**
   - High Error Rate: > 5% crítico, > 1% warning
   - High Response Time: > 1000ms crítico, > 500ms warning
   - Failed Checks: > 0 warning

---

## 🏗️ Dashboard 2: Pipeline Metrics (Jenkins/CI-CD)

### Ubicación en Netdata
Navega a: **Menu → Prometheus → jenkins_pipeline**

### Métricas de Pipeline vía Pushgateway

Las métricas de Jenkins se envían al Pushgateway y Netdata las recopila desde allí.

#### Métricas Disponibles:

1. **jenkins_build_duration_seconds**
   - Duración de builds de Jenkins
   - Buscar: "jenkins_build_duration"

2. **jenkins_build_success_total**
   - Total de builds exitosos
   - Buscar: "jenkins_build_success"

3. **jenkins_build_failure_total**
   - Total de builds fallidos
   - Buscar: "jenkins_build_failure"

4. **jmeter_response_time**
   - Tiempos de respuesta de JMeter
   - Buscar: "jmeter_response"

5. **jmeter_throughput**
   - Throughput de JMeter
   - Buscar: "jmeter_throughput"

### 📊 Visualización Pipeline Metrics

```
Menu → Prometheus → pushgateway → jenkins_pipeline job
```

Los dashboards mostrarán:
- Duración de pipelines por proyecto
- Tasa de éxito/fallo de builds
- Métricas de performance de tests JMeter
- Trends históricos

---

## 🖥️ Dashboard 3: System Metrics (Node Exporter)

### Ubicación en Netdata
Navega a: **Menu → Prometheus → node_exporter**

### Métricas del Sistema Replicadas

#### 1. **CPU Usage**
- **Métrica Grafana:** `100 - (avg by(instance) (rate(node_cpu_seconds_total{mode="idle"}[5m])) * 100)`
- **En Netdata:** Menu → CPU → CPU Usage
- **También en:** Prometheus → node_exporter → cpu
- **Descripción:** Uso de CPU del sistema host

#### 2. **Memory Usage**
- **Métricas Prometheus:**
  - `node_memory_MemTotal_bytes`
  - `node_memory_MemAvailable_bytes`
  - `node_memory_Buffers_bytes`
  - `node_memory_Cached_bytes`
- **En Netdata:** Menu → Memory
- **También en:** Prometheus → node_exporter → memory

#### 3. **Disk Usage**
- **Métricas Prometheus:**
  - `node_filesystem_avail_bytes`
  - `node_filesystem_size_bytes`
- **En Netdata:** Menu → Disks
- **También en:** Prometheus → node_exporter → filesystem

#### 4. **Network I/O**
- **Métricas Prometheus:**
  - `node_network_receive_bytes_total`
  - `node_network_transmit_bytes_total`
- **En Netdata:** Menu → Network Interfaces

---

## 🐰 Dashboard 4: RabbitMQ Metrics

### Ubicación en Netdata
Navega a: **Menu → Prometheus → rabbitmq**

### Métricas de RabbitMQ

#### Métricas Principales:

1. **rabbitmq_build_info**
   - Información de versión de RabbitMQ

2. **rabbitmq_queue_messages**
   - Mensajes en colas

3. **rabbitmq_queue_messages_ready**
   - Mensajes listos para consumir

4. **rabbitmq_queue_consumers**
   - Número de consumers por cola

5. **rabbitmq_channel_messages_published_total**
   - Total de mensajes publicados

6. **rabbitmq_channel_messages_delivered_total**
   - Total de mensajes entregados

7. **erlang_vm_memory_bytes_total**
   - Uso de memoria de Erlang VM

---

## 🎨 Características Avanzadas de Netdata

### 1. **Dashboards Dinámicos**
- Netdata crea automáticamente dashboards para cada fuente de Prometheus
- No necesitas configuración manual de paneles

### 2. **Alertas en Tiempo Real**
Las alertas configuradas en `/monitoring/netdata/health.d/k6_alerts.conf`:
- ⚠️ **k6_high_error_rate**: Error rate > 1% warning, > 5% crítico
- ⚠️ **k6_high_response_time_p95**: Response time > 500ms warning, > 1000ms crítico
- ⚠️ **k6_failed_checks**: Checks fallidos > 0

### 3. **Comparación con Grafana**

| Característica | Grafana | Netdata |
|----------------|---------|---------|
| Configuración | Manual (JSON) | Automática |
| Tiempo Real | 5-15s delay | < 1s delay |
| Alertas | Requiere configuración | Auto-detectadas |
| Drill-down | Limitado | Excelente |
| Correlación | Manual | Automática |
| Resource Usage | Medio | Bajo |

### 4. **Navegación Rápida**

#### Búsqueda Global:
```
Presiona "/" o click en el search bar
Escribe: k6, rabbitmq, jenkins, node, etc.
```

#### Filtros por Servicio:
```
Menu → Filter → Service: k6-stress-testing
Menu → Filter → Service: rabbitmq
Menu → Filter → Service: jenkins-ci
```

### 5. **Exportar Datos**

Netdata API para exportar métricas:
```bash
# Obtener datos de k6_vus últimos 5 minutos
curl 'http://localhost:19999/api/v1/data?chart=prometheus_k6_metrics.k6_vus&after=-300'

# Obtener lista de todos los charts disponibles
curl 'http://localhost:19999/api/v1/charts'

# Obtener alarmas activas
curl 'http://localhost:19999/api/v1/alarms'
```

---

## 🔧 Configuración Personalizada

### Agregar Nuevas Fuentes de Métricas

Editar: `/monitoring/netdata/go.d/prometheus.conf`

```yaml
jobs:
  - name: mi_aplicacion
    url: http://mi-servicio:9090/metrics
    update_every: 5
    selector:
      allow:
        - mi_metrica_*
```

### Crear Nuevas Alertas

Crear archivo en: `/monitoring/netdata/health.d/mi_alerta.conf`

```conf
alarm: mi_alerta_personalizada
   on: prometheus_mi_metrica
 calc: $this
units: requests
every: 10s
 warn: $this > 100
 crit: $this > 1000
 info: Mi métrica personalizada está alta
```

---

## 📱 Accesos Rápidos

| Dashboard | URL Directa |
|-----------|-------------|
| **Netdata Principal** | http://localhost:19999 |
| **Grafana (comparación)** | http://localhost:3302 |
| **Prometheus (fuente)** | http://localhost:9090 |
| **RabbitMQ Management** | http://localhost:15674 |
| **Pushgateway** | http://localhost:9093 |

---

## 🎯 Workflows Recomendados

### Durante Tests de Carga (K6):
1. Abrir Netdata: http://localhost:19999
2. Buscar "k6_" en el search
3. Observar en tiempo real:
   - Virtual Users (VUs)
   - Request Rate
   - Response Time P95/P99
   - Error Rate
4. Verificar alertas en la sección "Alarms"

### Monitoreo de Pipeline CI/CD:
1. Navegar a Menu → Prometheus → jenkins_pipeline
2. Observar duración de builds
3. Revisar tasa de éxito/fallo
4. Comparar con métricas históricas

### Diagnóstico de Sistema:
1. Menu → System Overview
2. Verificar CPU, Memory, Disk
3. Correlacionar con spikes en aplicación
4. Drill-down en procesos específicos

---

## 💡 Tips y Trucos

1. **Performance:** Netdata es más ligero que Grafana, ideal para monitoring en tiempo real
2. **Alertas:** Netdata envía notificaciones automáticas (configurable)
3. **Historial:** Por defecto guarda 1 hora, ajustable en `netdata.conf`
4. **Multi-host:** Puedes conectar múltiples instancias de Netdata
5. **API REST:** Todas las métricas están disponibles vía API

---

## 🔗 Recursos Adicionales

- **Netdata Docs:** https://learn.netdata.cloud/
- **Prometheus Integration:** https://learn.netdata.cloud/docs/data-collection/apm/openmetrics/prometheus
- **Alertas Personalizadas:** https://learn.netdata.cloud/docs/alerts-and-notifications/
- **API Reference:** https://learn.netdata.cloud/docs/rest-api/

---

## 📝 Mantenimiento

### Reiniciar Netdata:
```bash
docker compose -f docker-compose.full.yml restart netdata
```

### Ver Logs:
```bash
docker logs ensurance-netdata-full -f
```

### Limpiar Cache:
```bash
docker compose -f docker-compose.full.yml down netdata
docker volume rm ensurance-netdata-cache-full
docker compose -f docker-compose.full.yml up -d netdata
```

---

**Última actualización:** $(date)
**Versión Netdata:** v2.7.0-nightly
**Puerto:** 19999
