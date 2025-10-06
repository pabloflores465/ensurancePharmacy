# 📊 Queries PromQL Útiles para tus Métricas

## 🎯 Cómo Usar Estas Queries

1. Ve a **Prometheus** en http://localhost:9095
2. En la barra de búsqueda (donde dice "Expression"), pega la query
3. Haz clic en **Execute** (botón azul)
4. Cambia a la pestaña **Graph** para ver gráficos

---

## 🔥 Queries Básicas - Empezar Aquí

### Ver todas las métricas de backv5
```promql
{job="backv5-pharmacy"}
```

### Ver todas las métricas de backv4
```promql
{job="backv4-ensurance"}
```

### Ver todas las métricas de frontends
```promql
{job=~".*-frontend"}
```

---

## 🌐 Backend backv5 (Pharmacy) - HTTP Metrics

### Total de peticiones HTTP por endpoint
```promql
ensurance_http_requests_total
```

### Peticiones por segundo en los últimos 5 minutos
```promql
rate(ensurance_http_requests_total[5m])
```

### Peticiones por segundo agrupadas por endpoint
```promql
sum(rate(ensurance_http_requests_total[5m])) by (path)
```

### Peticiones por segundo agrupadas por método HTTP
```promql
sum(rate(ensurance_http_requests_total[5m])) by (method)
```

### Peticiones por segundo agrupadas por código de estado
```promql
sum(rate(ensurance_http_requests_total[5m])) by (status)
```

### Tasa de errores (5xx)
```promql
sum(rate(ensurance_http_requests_total{status=~"5.."}[5m])) 
/ 
sum(rate(ensurance_http_requests_total[5m]))
```

### Tasa de errores 4xx
```promql
sum(rate(ensurance_http_requests_total{status=~"4.."}[5m]))
```

### Peticiones en curso (inflight)
```promql
ensurance_http_inflight_requests
```

### Latencia promedio por endpoint
```promql
rate(ensurance_http_request_duration_seconds_sum[5m])
/
rate(ensurance_http_request_duration_seconds_count[5m])
```

### Latencia p50 (mediana)
```promql
histogram_quantile(0.50, 
  rate(ensurance_http_request_duration_seconds_bucket[5m]))
```

### Latencia p95
```promql
histogram_quantile(0.95, 
  rate(ensurance_http_request_duration_seconds_bucket[5m]))
```

### Latencia p99
```promql
histogram_quantile(0.99, 
  rate(ensurance_http_request_duration_seconds_bucket[5m]))
```

### Tamaño promedio de payload
```promql
rate(ensurance_http_request_size_bytes_sum[5m])
/
rate(ensurance_http_request_size_bytes_count[5m])
```

---

## 🗄️ Backend backv4 (Ensurance) - Database Metrics

### Total de consultas a base de datos
```promql
ensurance_db_queries_total
```

### Consultas por segundo
```promql
rate(ensurance_db_queries_total[5m])
```

### Consultas por segundo agrupadas por operación
```promql
sum(rate(ensurance_db_queries_total[5m])) by (operation)
```

### Consultas por segundo agrupadas por entidad
```promql
sum(rate(ensurance_db_queries_total[5m])) by (entity)
```

### Consultas fallidas
```promql
sum(rate(ensurance_db_queries_total{status="error"}[5m]))
```

---

## ☕ Métricas JVM (backv4 y backv5)

### Uso de memoria heap
```promql
jvm_memory_bytes_used{area="heap"}
```

### Uso de memoria heap en MB
```promql
jvm_memory_bytes_used{area="heap"} / 1024 / 1024
```

### Porcentaje de uso de heap
```promql
(jvm_memory_bytes_used{area="heap"} / jvm_memory_bytes_max{area="heap"}) * 100
```

### Memoria no-heap (metaspace, code cache)
```promql
jvm_memory_bytes_used{area="nonheap"}
```

### Threads activos
```promql
jvm_threads_current
```

### Threads en estado RUNNABLE
```promql
jvm_threads_state{state="RUNNABLE"}
```

### Clases cargadas
```promql
jvm_classes_loaded
```

### Recolecciones de basura por segundo
```promql
rate(jvm_gc_collection_seconds_count[5m])
```

### Tiempo gastado en GC (segundos)
```promql
rate(jvm_gc_collection_seconds_sum[5m])
```

### Comparar uso de memoria entre backends
```promql
jvm_memory_bytes_used{area="heap", job=~"backv.*"}
```

---

## 🎨 Frontend Metrics (ensurance y pharmacy)

### Vistas de página en ensurance
```promql
ensurance_frontend_page_views_total
```

### Búsquedas de medicamentos en pharmacy
```promql
pharmacy_frontend_medicine_searches_total
```

### Búsquedas por segundo
```promql
rate(pharmacy_frontend_medicine_searches_total[5m])
```

### CPU usado por frontends (%)
```promql
rate(process_cpu_seconds_total{job=~".*-frontend"}[5m]) * 100
```

### Memoria RSS usada por frontends (MB)
```promql
process_resident_memory_bytes{job=~".*-frontend"} / 1024 / 1024
```

### Heap size de Node.js (MB)
```promql
nodejs_heap_size_total_bytes{job=~".*-frontend"} / 1024 / 1024
```

### Heap usado de Node.js (MB)
```promql
nodejs_heap_size_used_bytes{job=~".*-frontend"} / 1024 / 1024
```

### Porcentaje de heap usado en Node.js
```promql
(nodejs_heap_size_used_bytes / nodejs_heap_size_total_bytes) * 100
```

---

## 📈 Queries Comparativas

### Comparar throughput entre todos los servicios
```promql
sum(rate(ensurance_http_requests_total[5m])) by (job)
```

### Uso total de memoria (backends + frontends)
```promql
sum(jvm_memory_bytes_used{area="heap"}) + sum(process_resident_memory_bytes{job=~".*-frontend"})
```

### Top 5 endpoints más lentos
```promql
topk(5, 
  rate(ensurance_http_request_duration_seconds_sum[5m])
  /
  rate(ensurance_http_request_duration_seconds_count[5m])
) by (path)
```

### Top 5 endpoints con más peticiones
```promql
topk(5, sum(rate(ensurance_http_requests_total[5m])) by (path))
```

---

## 🚨 Queries para Alertas

### Latencia alta (> 1 segundo)
```promql
histogram_quantile(0.95, 
  rate(ensurance_http_request_duration_seconds_bucket[5m])
) > 1
```

### Tasa de errores alta (> 5%)
```promql
(
  sum(rate(ensurance_http_requests_total{status=~"5.."}[5m]))
  /
  sum(rate(ensurance_http_requests_total[5m]))
) > 0.05
```

### Uso de heap JVM > 80%
```promql
(jvm_memory_bytes_used{area="heap"} / jvm_memory_bytes_max{area="heap"}) * 100 > 80
```

### Muchas peticiones en curso (> 50)
```promql
ensurance_http_inflight_requests > 50
```

### GC excesivo (> 10% del tiempo)
```promql
rate(jvm_gc_collection_seconds_sum[5m]) > 0.1
```

---

## 💡 Tips para Usar Prometheus

1. **Time Range**: Usa el selector de tiempo arriba para cambiar el rango (1h, 6h, 1d, etc.)

2. **Rate vs Increase**:
   - `rate()` - Tasa por segundo (mejor para gráficos continuos)
   - `increase()` - Total incremental en el período

3. **Labels útiles**:
   - `job` - El nombre del servicio
   - `path` - Endpoint HTTP
   - `method` - GET, POST, etc.
   - `status` - Código HTTP (200, 404, 500, etc.)

4. **Agregaciones**:
   - `sum()` - Suma
   - `avg()` - Promedio
   - `max()` - Máximo
   - `min()` - Mínimo
   - `count()` - Conteo

5. **Funciones de tiempo**:
   - `[5m]` - Últimos 5 minutos
   - `[1h]` - Última hora
   - `[1d]` - Último día

---

## 🎯 Queries Recomendadas para Empezar

Si no sabes por dónde empezar, prueba estas en orden:

1. **Ver todas las métricas disponibles**:
   ```promql
   {job=~"backv.*|.*-frontend"}
   ```

2. **Peticiones por segundo en backv5**:
   ```promql
   rate(ensurance_http_requests_total[5m])
   ```

3. **Uso de memoria JVM**:
   ```promql
   jvm_memory_bytes_used{area="heap"} / 1024 / 1024
   ```

4. **Latencia p95**:
   ```promql
   histogram_quantile(0.95, rate(ensurance_http_request_duration_seconds_bucket[5m]))
   ```

---

## 📊 Siguiente Paso: Grafana

Una vez que hayas probado estas queries en Prometheus, puedes crear **dashboards visuales en Grafana**:

1. Ve a http://localhost:3300 (admin/changeme)
2. Agrega Prometheus como datasource (http://prometheus:9090)
3. Crea un nuevo dashboard
4. Usa estas mismas queries en los paneles de Grafana

¡Grafana hace que las métricas se vean mucho mejor con gráficos interactivos!
