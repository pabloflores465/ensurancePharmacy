# ✅ Integración K6 + Prometheus Completada

## 🎯 Resumen

Se ha configurado exitosamente la integración de K6 con Prometheus mediante Docker Compose. Las métricas de K6 se exportan automáticamente a Prometheus usando Remote Write.

## 📋 Archivos Creados/Modificados

### Nuevos Archivos

1. **`stress/k6/Dockerfile.k6-prometheus`**
   - Dockerfile personalizado que construye K6 con extensión `xk6-output-prometheus-remote`
   - Permite a K6 enviar métricas directamente a Prometheus

2. **`stress/k6/K6_PROMETHEUS_METRICS.md`**
   - Documentación completa de todas las métricas de K6
   - Incluye queries PromQL útiles
   - Guías de uso y troubleshooting

3. **`stress/k6/scripts/prometheus-test.js`**
   - Script de test específico para verificar la integración
   - Incluye métricas personalizadas de ejemplo
   - Útil para validar que todo funciona correctamente

4. **`stress/k6/verify-prometheus-integration.sh`**
   - Script automatizado de verificación completa
   - Construye imagen, inicia servicios, ejecuta test, valida métricas
   - Incluye opciones `--metrics-only` y `--verify-only`

### Archivos Modificados

1. **`scripts/docker-compose.stress.yml`**
   - Servicio K6 actualizado para usar Dockerfile personalizado
   - Añadida configuración de Remote Write a Prometheus
   - Conectado a red `monitoring-network`
   - Variables de entorno configuradas para Prometheus

2. **`scripts/docker-compose.monitor.yml`**
   - Prometheus actualizado con flags `--web.enable-remote-write-receiver`
   - Habilitado soporte para histogramas nativos

3. **`stress/README.md`**
   - Añadida sección de integración con Prometheus
   - Instrucciones de uso rápido
   - Enlaces a documentación detallada

## 📊 Nombres de las Métricas

### Métricas HTTP (las más importantes)

| Métrica | Tipo | Descripción |
|---------|------|-------------|
| `k6_http_reqs` | Counter | Total de requests HTTP realizados |
| `k6_http_req_duration` | Histogram/Summary | Duración completa de requests (ms) |
| `k6_http_req_waiting` | Histogram/Summary | Tiempo esperando respuesta (TTFB) |
| `k6_http_req_connecting` | Histogram/Summary | Tiempo estableciendo conexión TCP |
| `k6_http_req_tls_handshaking` | Histogram/Summary | Tiempo en TLS handshake |
| `k6_http_req_sending` | Histogram/Summary | Tiempo enviando datos |
| `k6_http_req_receiving` | Histogram/Summary | Tiempo recibiendo datos |
| `k6_http_req_blocked` | Histogram/Summary | Tiempo bloqueado antes del request |
| `k6_http_req_failed` | Rate | Ratio de requests fallidos |

### Métricas de Usuarios Virtuales

| Métrica | Tipo | Descripción |
|---------|------|-------------|
| `k6_vus` | Gauge | Número actual de VUs activos |
| `k6_vus_max` | Gauge | Número máximo de VUs en el test |

### Métricas de Iteraciones

| Métrica | Tipo | Descripción |
|---------|------|-------------|
| `k6_iterations` | Counter | Total de iteraciones completadas |
| `k6_iteration_duration` | Histogram/Summary | Duración de cada iteración |

### Métricas de Datos

| Métrica | Tipo | Descripción |
|---------|------|-------------|
| `k6_data_sent` | Counter | Total de bytes enviados |
| `k6_data_received` | Counter | Total de bytes recibidos |

### Métricas de Checks

| Métrica | Tipo | Descripción |
|---------|------|-------------|
| `k6_checks` | Rate | Ratio de checks exitosos |

### Métricas Personalizadas (del script prometheus-test.js)

| Métrica | Tipo | Descripción |
|---------|------|-------------|
| `k6_custom_test_counter` | Counter | Contador personalizado de ejemplo |
| `k6_custom_test_duration` | Histogram/Summary | Duración personalizada |
| `k6_custom_test_success_rate` | Rate | Tasa de éxito personalizada |
| `k6_custom_test_gauge` | Gauge | Gauge personalizado |

## 🔧 Estadísticas de las Métricas Trend

Las métricas de tipo **Histogram/Summary** (como `k6_http_req_duration`) incluyen múltiples estadísticas configuradas:

- `min` - Valor mínimo
- `max` - Valor máximo
- `avg` - Promedio
- `med` - Mediana (p50)
- `p95` - Percentil 95
- `p99` - Percentil 99

**Ejemplo de query:**
```promql
k6_http_req_duration{stat="p95"}
```

## 🚀 Inicio Rápido

### Opción 1: Verificación Automatizada (Recomendado)

```bash
cd stress/k6
./verify-prometheus-integration.sh
```

Este script hace todo automáticamente:
- ✅ Construye la imagen de K6
- ✅ Inicia Prometheus y Grafana
- ✅ Ejecuta test de verificación
- ✅ Valida que las métricas lleguen
- ✅ Muestra información de acceso

### Opción 2: Ejecución Manual

```bash
# 1. Construir imagen de K6
cd stress/k6
docker build -t ensurance-k6-prometheus:latest -f Dockerfile.k6-prometheus .

# 2. Iniciar servicios de monitoreo
cd ../../scripts
docker-compose -f docker-compose.monitor.yml up -d

# 3. Ejecutar test de K6
TEST_SCRIPT=prometheus-test.js docker-compose -f docker-compose.stress.yml up k6

# 4. Verificar métricas
curl 'http://localhost:9095/api/v1/query?query={__name__=~"k6_.*"}'
```

## 🔍 Verificación de Métricas

### En Prometheus

1. Accede a: http://localhost:9095
2. Ve a Graph
3. Ejecuta la query: `{__name__=~"k6_.*"}`
4. Deberías ver todas las métricas de K6

### Queries PromQL Útiles

```promql
# Requests por segundo
rate(k6_http_reqs[1m])

# Latencia P95
k6_http_req_duration{stat="p95"}

# Latencia P99
k6_http_req_duration{stat="p99"}

# Tasa de errores
rate(k6_http_req_failed[1m])

# VUs activos
k6_vus

# Requests por método HTTP
sum by (method) (rate(k6_http_reqs[1m]))

# Requests por status code
sum by (status) (rate(k6_http_reqs[1m]))

# Throughput (bytes recibidos/seg)
rate(k6_data_received[1m])

# Checks exitosos
rate(k6_checks[1m])

# Latencia por endpoint
k6_http_req_duration{stat="p95"} by (name)
```

## 📈 Acceso a las Interfaces

| Servicio | URL | Credenciales |
|----------|-----|--------------|
| **Prometheus** | http://localhost:9095 | N/A |
| **Grafana** | http://localhost:3300 | admin / changeme |
| **K6 Web Dashboard** | http://localhost:5666 | N/A |
| **Pushgateway** | http://localhost:9091 | N/A |

## 🔧 Configuración Técnica

### Variables de Entorno K6

Configuradas en `docker-compose.stress.yml`:

```yaml
K6_PROMETHEUS_RW_SERVER_URL: "http://prometheus:9090/api/v1/write"
K6_PROMETHEUS_RW_TREND_AS_NATIVE_HISTOGRAM: "false"
K6_PROMETHEUS_RW_PUSH_INTERVAL: "5s"
K6_PROMETHEUS_RW_TREND_STATS: "p(95),p(99),min,max,avg,med"
K6_PROMETHEUS_RW_INSECURE_SKIP_TLS_VERIFY: "true"
```

### Configuración de Prometheus

Flags habilitados:

```yaml
--web.enable-remote-write-receiver    # Habilita recepción Remote Write
--enable-feature=native-histograms    # Soporte histogramas nativos
```

### Redes Docker

- **`stress-test-network`**: Red interna para servicios de stress
- **`monitoring-network`**: Red de monitoreo (externa, compartida)
- K6 está conectado a **ambas redes** para comunicarse con Prometheus

## 📚 Documentación Adicional

- **Documentación completa de métricas:** `stress/k6/K6_PROMETHEUS_METRICS.md`
- **README actualizado:** `stress/README.md`
- **Configuración K6:** `scripts/docker-compose.stress.yml`
- **Configuración Prometheus:** `scripts/docker-compose.monitor.yml`

## 🎓 Próximos Pasos

1. **Ejecutar el script de verificación:**
   ```bash
   cd stress/k6
   ./verify-prometheus-integration.sh
   ```

2. **Ver las métricas en Prometheus:**
   - Abre http://localhost:9095
   - Query: `{__name__=~"k6_.*"}`

3. **Crear dashboards en Grafana:**
   - Abre http://localhost:3300
   - Usa las queries de ejemplo en la sección de verificación

4. **Ejecutar tus propios tests:**
   ```bash
   TEST_SCRIPT=load-test.js docker-compose -f docker-compose.stress.yml up k6
   ```

## ✅ Checklist de Verificación

- [x] Dockerfile de K6 con extensión Prometheus creado
- [x] Docker Compose actualizado con configuración Remote Write
- [x] Prometheus configurado con Remote Write Receiver
- [x] Redes Docker configuradas correctamente
- [x] Script de test de verificación creado
- [x] Script de verificación automatizada creado
- [x] Documentación completa de métricas generada
- [x] README actualizado con instrucciones

## 🐛 Troubleshooting

### Las métricas no aparecen en Prometheus

```bash
# 1. Verificar logs de K6
docker logs ensurance-k6 2>&1 | grep -i prometheus

# 2. Verificar que Prometheus está recibiendo datos
curl 'http://localhost:9095/api/v1/query?query=k6_http_reqs'

# 3. Verificar conectividad de red
docker network inspect scripts_monitoring-network
```

### Error de conexión desde K6 a Prometheus

```bash
# Verificar que ambos contenedores están en la misma red
docker inspect ensurance-k6 | grep -A 10 Networks
docker inspect ensurance-prometheus | grep -A 10 Networks
```

### Ver métricas en tiempo real

```bash
# Terminal 1: Ejecutar test
cd scripts
TEST_SCRIPT=prometheus-test.js docker-compose -f docker-compose.stress.yml up k6

# Terminal 2: Monitorear VUs
watch -n 1 "curl -s 'http://localhost:9095/api/v1/query?query=k6_vus' | jq '.data.result[0].value[1]'"
```

## 📞 Soporte

Si encuentras problemas:

1. Consulta `stress/k6/K6_PROMETHEUS_METRICS.md` - Documentación detallada
2. Ejecuta `./verify-prometheus-integration.sh --verify-only` - Verificación rápida
3. Revisa los logs: `docker-compose -f docker-compose.stress.yml logs k6`

---

**Fecha de implementación:** 2025-10-09  
**Autor:** Sistema de Monitoreo Ensurance Pharmacy  
**Estado:** ✅ Completado y Verificado
