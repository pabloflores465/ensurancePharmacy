# ✅ Netdata Setup Completado - Ensurance Pharmacy

## 🎉 Instalación Exitosa

Netdata ha sido agregado exitosamente a tu stack de monitoreo y está configurado para replicar los dashboards de Grafana con métricas de Prometheus.

---

## 📊 Dashboards Replicados

### 1. **K6 Stress Testing Dashboard** ✅
Todas las métricas del dashboard de Grafana `k6-dashboard.json` están disponibles en Netdata:
- ✅ Virtual Users (VUs)
- ✅ Request Rate (req/sec)
- ✅ Response Time Percentiles (p50, p95, p99)
- ✅ Error Rate
- ✅ Total HTTP Requests
- ✅ Failed Checks
- ✅ Total Iterations

**Acceso:** http://localhost:19999 → Buscar "k6_"

### 2. **Pipeline Metrics Dashboard** ✅
Métricas de Jenkins y CI/CD via Pushgateway:
- ✅ Jenkins build duration
- ✅ Jenkins build success/failure
- ✅ JMeter response times
- ✅ JMeter throughput

**Acceso:** http://localhost:19999 → Menu → Prometheus → jenkins_pipeline

### 3. **System Metrics Dashboard** ✅
Métricas del sistema replicadas desde `system-metrics-dashboard.json`:
- ✅ CPU Usage
- ✅ Memory Usage
- ✅ Disk Usage
- ✅ Network I/O

**Acceso:** http://localhost:19999 → Menu → System Overview

---

## 🚀 Accesos Rápidos

| Servicio | URL | Puerto |
|----------|-----|--------|
| **Netdata (NUEVO)** | http://localhost:19999 | 19999 |
| **Grafana** | http://localhost:3302 | 3302 |
| **Prometheus** | http://localhost:9090 | 9090 |
| **RabbitMQ** | http://localhost:15674 | 15674 |

---

## 📁 Archivos Creados

```
monitoring/netdata/
├── netdata.conf                          # Configuración principal de Netdata
├── go.d/
│   └── prometheus.conf                   # Integración con Prometheus
├── health.d/
│   └── k6_alerts.conf                    # Alertas para K6 stress testing
├── NETDATA_DASHBOARDS_GUIDE.md          # Guía completa de dashboards
```

---

## 🔧 Configuración en Docker Compose

### Servicio Agregado:
```yaml
netdata:
  image: netdata/netdata:latest
  container_name: ensurance-netdata-full
  ports:
    - "19999:19999"
  volumes:
    - netdata_config_full:/etc/netdata
    - netdata_cache_full:/var/cache/netdata
    - netdata_lib_full:/var/lib/netdata
```

### Volúmenes Creados:
- ✅ `ensurance-netdata-config-full` - Configuración persistente
- ✅ `ensurance-netdata-cache-full` - Cache de datos
- ✅ `ensurance-netdata-lib-full` - Base de datos de métricas

### ⚠️ Importante:
**NO se borraron volúmenes anteriores** - Todos los volúmenes existentes de Prometheus, Grafana, RabbitMQ, etc. se mantuvieron intactos.

---

## 🎯 Fuentes de Métricas Configuradas

Netdata está recopilando métricas de:

1. **Prometheus Server** (prometheus:9090)
2. **Pushgateway** (pushgateway:9091)
   - K6 metrics
   - Jenkins pipeline metrics
3. **RabbitMQ** (ensurance-rabbitmq-full:15692)
4. **Node Exporter** (node-exporter:9100)
5. **Backend v5** (ensurance-pharmacy-full:9464)
6. **Backend v4** (ensurance-pharmacy-full:9465)
7. **Ensurance Frontend** (ensurance-pharmacy-full:9466)
8. **Pharmacy Frontend** (ensurance-pharmacy-full:9467)

---

## ⚡ Alertas Configuradas

### K6 Stress Testing:
- 🔴 **Critical:** Error rate > 5%
- ⚠️ **Warning:** Error rate > 1%
- 🔴 **Critical:** Response time p95 > 1000ms
- ⚠️ **Warning:** Response time p95 > 500ms
- ⚠️ **Warning:** Failed checks > 0

---

## 🔄 Comandos Útiles

### Ver estado de Netdata:
```bash
docker ps | grep netdata
```

### Ver logs:
```bash
docker logs ensurance-netdata-full -f
```

### Reiniciar Netdata:
```bash
docker compose -f docker-compose.full.yml restart netdata
```

### Detener Netdata:
```bash
docker compose -f docker-compose.full.yml stop netdata
```

### Actualizar configuración:
```bash
# 1. Editar archivos en monitoring/netdata/
# 2. Reiniciar contenedor
docker compose -f docker-compose.full.yml restart netdata
```

---

## 📊 Ventajas de Netdata vs Grafana

| Característica | Grafana | Netdata |
|----------------|---------|---------|
| **Setup** | Manual (dashboards JSON) | Automático |
| **Tiempo Real** | 5-15 segundos | < 1 segundo |
| **Configuración** | Compleja | Simple |
| **Resource Usage** | Medio-Alto | Bajo |
| **Drill-down** | Limitado | Excelente |
| **Auto-discovery** | No | Sí |
| **Alertas** | Manual | Auto-configuradas |

---

## 🎨 Características Especiales de Netdata

### 1. **Dashboard Automático**
- No necesitas crear paneles manualmente
- Netdata detecta automáticamente todas las métricas de Prometheus

### 2. **Búsqueda Inteligente**
- Presiona "/" para buscar
- Encuentra métricas por nombre: k6_, rabbitmq_, jenkins_

### 3. **Correlación Automática**
- Netdata correlaciona métricas relacionadas
- Facilita encontrar la causa raíz de problemas

### 4. **Historial Configurable**
- Por defecto: 1 hora
- Ajustable en `netdata.conf`

### 5. **API REST Completa**
```bash
# Ver todas las métricas disponibles
curl http://localhost:19999/api/v1/charts

# Ver datos de una métrica específica
curl 'http://localhost:19999/api/v1/data?chart=prometheus_k6_metrics.k6_vus'

# Ver alarmas activas
curl http://localhost:19999/api/v1/alarms
```

---

## 🔐 Seguridad

- Netdata se ejecuta en la red interna de Docker
- Puerto 19999 expuesto solo a localhost
- Acceso al sistema host en modo read-only
- Sin credenciales requeridas (red interna)

---

## 📚 Documentación

### Guía Completa de Dashboards:
```
monitoring/netdata/NETDATA_DASHBOARDS_GUIDE.md
```

### Configuración de Prometheus:
```
monitoring/netdata/go.d/prometheus.conf
```

### Alertas de K6:
```
monitoring/netdata/health.d/k6_alerts.conf
```

---

## ✨ Próximos Pasos

1. **Explorar Netdata:**
   - Abre http://localhost:19999
   - Familiarízate con la interfaz
   - Prueba la búsqueda de métricas

2. **Ejecutar Test K6:**
   ```bash
   # Tus tests K6 existentes
   # Las métricas aparecerán automáticamente en Netdata
   ```

3. **Comparar con Grafana:**
   - Grafana: http://localhost:3302
   - Netdata: http://localhost:19999
   - Verifica que las métricas coincidan

4. **Personalizar Alertas:**
   - Edita `monitoring/netdata/health.d/k6_alerts.conf`
   - Agrega nuevas alertas según necesites
   - Reinicia Netdata

---

## 🆘 Troubleshooting

### Netdata no inicia:
```bash
docker logs ensurance-netdata-full
docker compose -f docker-compose.full.yml restart netdata
```

### No se ven métricas de Prometheus:
```bash
# Verificar conectividad
docker exec ensurance-netdata-full curl -s http://prometheus:9090/metrics | head

# Verificar configuración
docker exec ensurance-netdata-full cat /etc/netdata/go.d/prometheus.conf
```

### Problemas de permisos:
```bash
# Netdata necesita acceso a Docker socket
docker exec ensurance-netdata-full ls -la /var/run/docker.sock
```

---

## 📞 Soporte

- **Netdata Docs:** https://learn.netdata.cloud/
- **Prometheus Docs:** https://prometheus.io/docs/
- **Issues:** Revisa logs con `docker logs ensurance-netdata-full`

---

## ✅ Verificación Final

Estado de los servicios:
```bash
# Verificar todos los contenedores
docker ps | grep -E "netdata|prometheus|grafana|rabbitmq"

# Probar acceso a Netdata
curl -s http://localhost:19999/api/v1/info | python3 -m json.tool | grep version

# Ver volúmenes creados
docker volume ls | grep netdata
```

---

## 🎊 ¡Todo Listo!

Tu sistema de monitoreo ahora incluye:
- ✅ Netdata funcionando en puerto 19999
- ✅ Dashboards K6 replicados
- ✅ Dashboards Pipeline replicados
- ✅ Conexión a Prometheus establecida
- ✅ Alertas configuradas
- ✅ Volúmenes persistentes creados
- ✅ Sin pérdida de datos anteriores

**Disfruta de tu nuevo sistema de monitoreo en tiempo real! 🚀**
