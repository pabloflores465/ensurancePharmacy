# 🎯 Guía: K6 con Dashboard HTML y Métricas en Prometheus

## ✅ Cambios Implementados

### 1. **Dashboard HTML Interactivo**
- ✅ Agregada extensión `xk6-dashboard` al Dockerfile
- ✅ Configurado output `web-dashboard=export=/results/k6-report.html`
- ✅ Reporte HTML completo con gráficos interactivos

### 2. **Métricas a Prometheus**
- ✅ Extensión `xk6-output-prometheus-remote` ya estaba configurada
- ✅ Script `run-full-stress-test.sh` ahora levanta Prometheus automáticamente
- ✅ Verificación automática de servicios de monitoreo

### 3. **Servidor de Reportes Mejorado**
- ✅ Página index.html con enlaces a todos los reportes
- ✅ Redirección automática al dashboard interactivo

---

## 🚀 Inicio Rápido

### Paso 1: Reconstruir Imagen K6
```bash
cd /home/pablopolis2016/Documents/ensurancePharmacy/stress/k6
docker build -t ensurance-k6-prometheus:latest -f Dockerfile.k6-prometheus .
```

### Paso 2: Ejecutar Tests
```bash
cd /home/pablopolis2016/Documents/ensurancePharmacy/stress
./run-full-stress-test.sh
```

El script ahora:
1. ✅ Verifica si Prometheus está corriendo
2. ✅ Pregunta si quieres levantarlo (si no está corriendo)
3. ✅ Ejecuta el test K6
4. ✅ Genera reporte HTML interactivo
5. ✅ Exporta métricas a Prometheus en tiempo real

---

## 📊 Acceso a Reportes y Métricas

### Reportes K6
| Tipo | URL | Descripción |
|------|-----|-------------|
| **Dashboard HTML** | http://localhost:5666 | Reporte interactivo con gráficos |
| **JSON Results** | http://localhost:5666/k6-results.json | Datos crudos en JSON |
| **Summary** | http://localhost:5666/summary.json | Resumen del test |

### Métricas y Monitoreo
| Servicio | URL | Credenciales |
|----------|-----|--------------|
| **Prometheus** | http://localhost:9095 | N/A |
| **Grafana** | http://localhost:3300 | admin / changeme |
| **Pushgateway** | http://localhost:9091 | N/A |

---

## 🔍 Verificar Métricas en Prometheus

### Queries Útiles

```promql
# Ver todas las métricas K6
{__name__=~"k6_.*"}

# Requests por segundo
rate(k6_http_reqs[1m])

# Latencia P95
k6_http_req_duration{stat="p95"}

# Latencia P99
k6_http_req_duration{stat="p99"}

# Tasa de errores
rate(k6_http_req_failed[1m])

# VUs activos durante el test
k6_vus

# Requests por endpoint
sum by (name) (rate(k6_http_reqs[1m]))

# Requests por método HTTP
sum by (method) (rate(k6_http_reqs[1m]))

# Requests por status code
sum by (status) (rate(k6_http_reqs[1m]))

# Throughput (bytes recibidos/seg)
rate(k6_data_received[1m])

# Métricas personalizadas de BackV4
backv4_response_time{stat="p95"}

# Métricas personalizadas de BackV5
backv5_response_time{stat="p95"}

# Requests exitosos vs fallidos
successful_requests
failed_requests
```

---

## 📈 Dashboard HTML - Qué Esperar

El nuevo dashboard HTML (`k6-report.html`) incluye:

- ✅ **Gráficos de VUs**: Visualización de usuarios virtuales en el tiempo
- ✅ **Latencias**: P50, P95, P99 con gráficos de línea
- ✅ **Requests/seg**: Throughput del test
- ✅ **Tasa de errores**: Checks y validaciones
- ✅ **Timeline interactivo**: Navegación temporal
- ✅ **Tablas de detalles**: Métricas por endpoint
- ✅ **Checks y validaciones**: Estado de cada verificación

---

## 🔧 Configuración Técnica

### Variables de Entorno (docker-compose.stress.yml)

```yaml
K6_PROMETHEUS_RW_SERVER_URL: "http://prometheus:9090/api/v1/write"
K6_PROMETHEUS_RW_TREND_AS_NATIVE_HISTOGRAM: "false"
K6_PROMETHEUS_RW_PUSH_INTERVAL: "5s"
K6_PROMETHEUS_RW_TREND_STATS: "p(95),p(99),min,max,avg,med"
K6_PROMETHEUS_RW_INSECURE_SKIP_TLS_VERIFY: "true"
```

### Outputs Configurados

```bash
k6 run \
  --out json=/results/k6-results.json \
  --out experimental-prometheus-rw \
  --out web-dashboard=export=/results/k6-report.html \
  --summary-export=/results/summary.json \
  script.js
```

---

## 🎨 Métricas Personalizadas en los Scripts

Todos los scripts K6 custom (`*-test-custom.js`) incluyen métricas adicionales:

```javascript
// Métricas personalizadas
const errorRate = new Rate('errors');
const backv4ResponseTime = new Trend('backv4_response_time');
const backv5ResponseTime = new Trend('backv5_response_time');
const successfulRequests = new Counter('successful_requests');
const failedRequests = new Counter('failed_requests');
const activeConnections = new Gauge('active_connections');
```

Estas métricas también se exportan a Prometheus con el prefijo `k6_`.

---

## 🐛 Troubleshooting

### El dashboard HTML está vacío o muestra error

**Solución:**
```bash
# Reconstruir la imagen K6 con la extensión xk6-dashboard
cd stress/k6
docker build -t ensurance-k6-prometheus:latest -f Dockerfile.k6-prometheus .
```

### Las métricas no aparecen en Prometheus

**Verificar:**
```bash
# 1. Prometheus está corriendo
docker ps | grep prometheus

# 2. Ver logs de K6
docker logs ensurance-k6

# 3. Verificar métricas en Prometheus
curl 'http://localhost:9095/api/v1/query?query=k6_http_reqs'
```

### Error de conexión K6 -> Prometheus

**Verificar redes:**
```bash
# Verificar que K6 está en monitoring-network
docker inspect ensurance-k6 | grep -A 10 Networks

# Verificar que Prometheus está en monitoring-network
docker inspect ensurance-prometheus | grep -A 10 Networks
```

---

## 📚 Flujo Completo de Trabajo

### Opción 1: Usando el Script Interactivo

```bash
cd stress
./run-full-stress-test.sh

# Menú interactivo:
# 1. Seleccionar tipo de test K6 (1-4)
# 2. El script verifica Prometheus
# 3. Pregunta si quieres levantarlo
# 4. Ejecuta el test
# 5. Muestra URLs de reportes
```

### Opción 2: Manual con Prometheus

```bash
# 1. Levantar Prometheus + Grafana
cd scripts
docker-compose -f docker-compose.monitor.yml up -d

# 2. Ejecutar test K6
TEST_SCRIPT=load-test-custom.js \
VUS=100 \
docker-compose -f docker-compose.stress.yml run --rm k6

# 3. Ver reporte HTML
docker-compose -f docker-compose.stress.yml up -d k6-report
# Abrir: http://localhost:5666

# 4. Ver métricas en Prometheus
# Abrir: http://localhost:9095
# Query: {__name__=~"k6_.*"}
```

### Opción 3: Manual sin Prometheus

```bash
# Ejecutar test K6 (sin métricas a Prometheus)
cd scripts
TEST_SCRIPT=load-test-custom.js VUS=50 \
docker-compose -f docker-compose.stress.yml run --rm k6

# Ver solo reporte HTML
docker-compose -f docker-compose.stress.yml up -d k6-report
# Abrir: http://localhost:5666
```

---

## ✅ Checklist de Verificación

- [ ] Imagen K6 reconstruida con `xk6-dashboard`
- [ ] Prometheus corriendo antes del test
- [ ] Test K6 ejecutado exitosamente
- [ ] Archivo `k6-report.html` generado en volumen
- [ ] Dashboard HTML accesible en http://localhost:5666
- [ ] Métricas visibles en Prometheus http://localhost:9095
- [ ] Grafana puede visualizar métricas K6

---

## 🎯 Próximos Pasos Recomendados

### 1. Crear Dashboard en Grafana

```bash
# Acceder a Grafana
# URL: http://localhost:3300
# Usuario: admin
# Password: changeme

# Importar dashboard K6 oficial:
# Dashboard ID: 2587 (K6 Prometheus)
```

### 2. Configurar Alertas

Puedes configurar alertas en Grafana basadas en:
- `k6_http_req_duration{stat="p95"} > 500` - Latencia alta
- `rate(k6_http_req_failed[1m]) > 0.05` - Tasa de errores > 5%
- `k6_vus > 1000` - Demasiados usuarios virtuales

### 3. Guardar Métricas Históricas

Por defecto, Prometheus guarda métricas por 15 días. Para cambiar:

```yaml
# En docker-compose.monitor.yml
command:
  - '--storage.tsdb.retention.time=30d'
```

---

## 📞 Soporte

Para más información:
- **Documentación K6**: https://k6.io/docs/
- **K6 Dashboard**: https://github.com/grafana/xk6-dashboard
- **K6 Prometheus**: https://github.com/grafana/xk6-output-prometheus-remote
- **Documentación completa**: `K6_PROMETHEUS_SETUP_COMPLETE.md`

---

**Fecha:** 2025-10-10  
**Versión:** 2.0  
**Estado:** ✅ Implementado y Documentado
