# 💻 Métricas del Sistema - Node Exporter

## 📊 Descripción General

Este documento describe las métricas del sistema recopiladas por **Node Exporter** en el proyecto Ensurance Pharmacy. Node Exporter es el exportador estándar de Prometheus para métricas a nivel de sistema operativo.

## 🚀 Inicio Rápido

### Levantar Monitoreo con Métricas del Sistema

```bash
cd scripts
docker compose -f docker-compose.monitor.yml up -d
```

Esto iniciará:
- **Prometheus** (puerto 9095): Recopilación y almacenamiento de métricas
- **Grafana** (puerto 3300): Visualización de métricas
- **Node Exporter** (puerto 9100): Exportador de métricas del sistema
- **Pushgateway** (puerto 9091): Para métricas push

### Accesos

- **Grafana**: http://localhost:3300 (admin/changeme)
- **Prometheus**: http://localhost:9095
- **Node Exporter**: http://localhost:9100/metrics
- **Dashboard**: "Métricas del Sistema" en Grafana

## 📈 Métricas Recopiladas

### 1. CPU (Procesador)

#### Uso de CPU (%)
```promql
100 - (avg by(instance) (rate(node_cpu_seconds_total{mode="idle"}[5m])) * 100)
```

#### Número de CPUs
```promql
count(node_cpu_seconds_total{mode="idle"})
```

#### Carga del Sistema
```promql
node_load1    # Carga promedio en 1 minuto
node_load5    # Carga promedio en 5 minutos
node_load15   # Carga promedio en 15 minutos
```

### 2. Memoria RAM

#### Uso de RAM (%)
```promql
(1 - (node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes)) * 100
```

#### Memoria Total
```promql
node_memory_MemTotal_bytes
```

#### Memoria Disponible
```promql
node_memory_MemAvailable_bytes
```

#### Memoria Usada
```promql
node_memory_MemTotal_bytes - node_memory_MemAvailable_bytes
```

#### Memoria por Tipo
```promql
node_memory_Buffers_bytes     # Buffers
node_memory_Cached_bytes      # Cache
node_memory_SwapTotal_bytes   # Swap total
node_memory_SwapFree_bytes    # Swap libre
```

### 3. Disco (Almacenamiento)

#### Uso de Disco (%)
```promql
100 - ((node_filesystem_avail_bytes{mountpoint="/",fstype!="rootfs"} * 100) / node_filesystem_size_bytes{mountpoint="/",fstype!="rootfs"})
```

#### Espacio Total
```promql
node_filesystem_size_bytes{mountpoint="/"}
```

#### Espacio Disponible
```promql
node_filesystem_avail_bytes{mountpoint="/"}
```

#### Espacio Usado
```promql
node_filesystem_size_bytes{mountpoint="/"} - node_filesystem_avail_bytes{mountpoint="/"}
```

#### Operaciones de I/O
```promql
rate(node_disk_reads_completed_total[5m])   # Lecturas/segundo
rate(node_disk_writes_completed_total[5m])  # Escrituras/segundo
rate(node_disk_read_bytes_total[5m])        # Bytes leídos/segundo
rate(node_disk_written_bytes_total[5m])     # Bytes escritos/segundo
```

### 4. Red (Network)

#### Tráfico de Red Recibido (RX)
```promql
rate(node_network_receive_bytes_total{device!="lo"}[5m])
```

#### Tráfico de Red Enviado (TX)
```promql
rate(node_network_transmit_bytes_total{device!="lo"}[5m])
```

#### Errores de Red
```promql
rate(node_network_receive_errs_total[5m])   # Errores recibidos
rate(node_network_transmit_errs_total[5m])  # Errores transmitidos
```

#### Paquetes Descartados
```promql
rate(node_network_receive_drop_total[5m])   # Paquetes descartados RX
rate(node_network_transmit_drop_total[5m])  # Paquetes descartados TX
```

### 5. Sistema

#### Uptime (Tiempo de Actividad)
```promql
node_time_seconds - node_boot_time_seconds
```

#### Procesos
```promql
node_procs_running    # Procesos en ejecución
node_procs_blocked    # Procesos bloqueados
```

#### Context Switches
```promql
rate(node_context_switches_total[5m])
```

## 🎨 Dashboard de Grafana

El dashboard "Métricas del Sistema" incluye:

### Fila 1: Indicadores Principales (Gauges)
- **Uso de CPU** (gauge con umbrales: verde <70%, amarillo <90%, rojo ≥90%)
- **Uso de RAM** (gauge con umbrales similares)
- **Uso de Disco** (gauge con umbrales similares)
- **Memoria RAM** (gráfico de tiempo real)

### Fila 2: Gráficos de Tendencias
- **Uso de CPU** (tiempo real, últimos 15 minutos)
- **Espacio en Disco** (usado vs disponible)

### Fila 3: Tráfico de Red
- **Tráfico Recibido** (RX) por interfaz
- **Tráfico Enviado** (TX) por interfaz

### Fila 4: I/O de Disco
- **Operaciones de Lectura** por dispositivo
- **Operaciones de Escritura** por dispositivo

### Fila 5: Estadísticas del Sistema
- **Uptime del Sistema** (tiempo desde el último reinicio)
- **Carga del Sistema** (load average 1 minuto)
- **Número de CPUs**
- **Memoria Total**

## 🔍 Queries de Ejemplo en Prometheus

### Verificar Métricas Disponibles
```bash
# Listar todas las métricas de Node Exporter
curl http://localhost:9100/metrics | grep ^node_

# Ver métricas en Prometheus
curl 'http://localhost:9095/api/v1/label/__name__/values' | jq '.data[] | select(startswith("node_"))'
```

### Consultas Útiles
```bash
# Carga del sistema (1 minuto)
curl 'http://localhost:9095/api/v1/query?query=node_load1'

# Memoria disponible
curl 'http://localhost:9095/api/v1/query?query=node_memory_MemAvailable_bytes'

# Uso de CPU
curl 'http://localhost:9095/api/v1/query?query=100-avg(rate(node_cpu_seconds_total{mode="idle"}[5m]))*100'

# Espacio en disco
curl 'http://localhost:9095/api/v1/query?query=node_filesystem_avail_bytes{mountpoint="/"}'
```

## ⚡ Alertas Recomendadas

### Alertas de CPU
```yaml
- alert: HighCPUUsage
  expr: 100 - (avg by(instance) (rate(node_cpu_seconds_total{mode="idle"}[5m])) * 100) > 80
  for: 5m
  labels:
    severity: warning
  annotations:
    summary: "CPU usage above 80%"
```

### Alertas de RAM
```yaml
- alert: HighMemoryUsage
  expr: (1 - (node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes)) * 100 > 85
  for: 5m
  labels:
    severity: warning
  annotations:
    summary: "Memory usage above 85%"
```

### Alertas de Disco
```yaml
- alert: DiskSpaceLow
  expr: 100 - ((node_filesystem_avail_bytes{mountpoint="/"} * 100) / node_filesystem_size_bytes{mountpoint="/"}) > 80
  for: 5m
  labels:
    severity: warning
  annotations:
    summary: "Disk usage above 80%"
```

## 🛠️ Configuración

### Docker Compose
El servicio Node Exporter está configurado en `scripts/docker-compose.monitor.yml`:

```yaml
node-exporter:
  image: prom/node-exporter:v1.8.2
  container_name: ensurance-node-exporter
  command:
    - '--path.rootfs=/host'
    - '--path.procfs=/host/proc'
    - '--path.sysfs=/host/sys'
    - '--collector.filesystem.mount-points-exclude=^/(sys|proc|dev|host|etc)($$|/)'
  ports:
    - "9100:9100"
  volumes:
    - /proc:/host/proc:ro
    - /sys:/host/sys:ro
    - /:/host:ro,rslave
  restart: unless-stopped
  privileged: true
```

### Prometheus Scrape Config
Configurado en `monitoring/prometheus/prometheus.yml`:

```yaml
- job_name: 'node-exporter'
  static_configs:
    - targets: ['node-exporter:9100']
      labels:
        service: 'system-metrics'
        component: 'node-exporter'
```

## 📚 Referencias

- **Node Exporter**: https://github.com/prometheus/node_exporter
- **Métricas de Node Exporter**: https://prometheus.io/docs/guides/node-exporter/
- **Prometheus Query Language (PromQL)**: https://prometheus.io/docs/prometheus/latest/querying/basics/
- **Grafana Dashboards**: https://grafana.com/grafana/dashboards/?search=node+exporter

## 🐛 Troubleshooting

### Node Exporter no responde
```bash
# Verificar que el contenedor está corriendo
docker ps | grep node-exporter

# Ver logs
docker logs ensurance-node-exporter

# Reiniciar
docker restart ensurance-node-exporter
```

### No hay datos en Grafana
```bash
# Verificar que Prometheus está scrapeando
curl http://localhost:9095/api/v1/targets | jq '.data.activeTargets[] | select(.labels.job=="node-exporter")'

# Verificar métricas en Prometheus
curl 'http://localhost:9095/api/v1/query?query=up{job="node-exporter"}'
```

### Permisos insuficientes
Node Exporter requiere acceso privilegiado para leer métricas del sistema. Asegúrate de que:
- El contenedor tiene `privileged: true`
- Los volúmenes `/proc`, `/sys` y `/` están montados correctamente

## 🎯 Mejores Prácticas

1. **Monitoreo Continuo**: Mantén el monitoreo activo 24/7
2. **Umbrales**: Configura alertas antes de llegar a límites críticos
3. **Tendencias**: Revisa regularmente las tendencias para planificar capacidad
4. **Retención**: Configura la retención de Prometheus según tus necesidades
5. **Backups**: Haz backup de los dashboards de Grafana importantes
6. **Documentación**: Mantén documentados los umbrales y acciones de respuesta

## ✅ Verificación

Para verificar que todo está funcionando:

```bash
# 1. Verificar Node Exporter
curl -s http://localhost:9100/metrics | head -n 20

# 2. Verificar Prometheus
curl -s 'http://localhost:9095/api/v1/query?query=up{job="node-exporter"}' | jq '.data.result[0].value[1]'
# Debe retornar "1"

# 3. Verificar métricas específicas
curl -s 'http://localhost:9095/api/v1/query?query=node_load1' | jq '.'

# 4. Acceder a Grafana y buscar el dashboard "Métricas del Sistema"
```

Si todos los pasos anteriores funcionan, el sistema de métricas está configurado correctamente. 🎉
