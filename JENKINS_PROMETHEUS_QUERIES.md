# 📊 Queries Prometheus para Métricas de Jenkins Pipeline

## 🎯 Queries Básicas

### Ver todas las métricas de Jenkins
```promql
{job="jenkins-pipeline"}
```

### Listar todos los jobs
```promql
count by (job_name) (jenkins_builds_total)
```

### Total de builds ejecutados
```promql
sum(jenkins_builds_total)
```

---

## ⏱️ Duración de Builds

### Duración del último build por job
```promql
jenkins_job_duration_seconds
```

### Duración promedio de builds por job
```promql
avg(jenkins_job_duration_seconds) by (job_name)
```

### Duración máxima de builds por job
```promql
max(jenkins_job_duration_seconds) by (job_name)
```

### Duración mínima de builds por job
```promql
min(jenkins_job_duration_seconds) by (job_name)
```

### Builds que tardaron más de 5 minutos
```promql
jenkins_job_duration_seconds > 300
```

### Top 5 builds más lentos
```promql
topk(5, jenkins_job_duration_seconds)
```

### Comparar duración entre jobs
```promql
sum(jenkins_job_duration_seconds) by (job_name)
```

---

## ✅ Estado de Builds

### Último estado de cada job
```promql
jenkins_job_status
```

### Builds exitosos
```promql
jenkins_job_status{status="success"}
```

### Builds fallidos
```promql
jenkins_job_status{status="failure"}
```

### Tasa de éxito (success rate)
```promql
sum(jenkins_job_status == 1) / count(jenkins_job_status)
```

### Tasa de fallo (failure rate)
```promql
sum(jenkins_job_status == 0) / count(jenkins_job_status)
```

### Porcentaje de éxito por job
```promql
(
  sum(jenkins_job_status == 1) by (job_name) 
  / 
  count(jenkins_job_status) by (job_name)
) * 100
```

### Número de builds fallidos por job
```promql
count(jenkins_job_status{status="failure"}) by (job_name)
```

---

## 🔢 Contadores de Builds

### Total de builds por job
```promql
jenkins_builds_total
```

### Tasa de builds por hora
```promql
rate(jenkins_builds_total[1h]) * 3600
```

### Tasa de builds por día
```promql
rate(jenkins_builds_total[24h]) * 86400
```

### Incremento de builds en las últimas 24 horas
```promql
increase(jenkins_builds_total[24h])
```

---

## ⏲️ Tiempo en Cola

### Tiempo promedio en cola
```promql
avg(jenkins_queue_time_seconds)
```

### Tiempo máximo en cola por job
```promql
max(jenkins_queue_time_seconds) by (job_name)
```

### Builds con alta espera en cola (> 1 minuto)
```promql
jenkins_queue_time_seconds > 60
```

### Tiempo en cola vs duración de ejecución
```promql
jenkins_queue_time_seconds / jenkins_job_duration_seconds
```

---

## 🎭 Métricas por Stage

### Duración de cada stage
```promql
jenkins_stage_duration_seconds
```

### Duración promedio por stage
```promql
avg(jenkins_stage_duration_seconds) by (stage)
```

### Top 5 stages más lentos
```promql
topk(5, 
  avg(jenkins_stage_duration_seconds) by (stage)
)
```

### Stages que tardan más de 2 minutos
```promql
jenkins_stage_duration_seconds > 120
```

### Comparar duración de stages entre jobs
```promql
avg(jenkins_stage_duration_seconds) by (job_name, stage)
```

### Stage más lento por job
```promql
max(jenkins_stage_duration_seconds) by (job_name)
```

---

## 📈 Tendencias y Análisis

### Builds ejecutados hoy
```promql
sum(increase(jenkins_builds_total[24h]))
```

### Duración total de todos los builds
```promql
sum(jenkins_job_duration_seconds)
```

### Tiempo total gastado en testing
```promql
sum(jenkins_stage_duration_seconds{stage="test"})
```

### Tiempo total gastado en build
```promql
sum(jenkins_stage_duration_seconds{stage="build"})
```

### Tiempo total gastado en deploy
```promql
sum(jenkins_stage_duration_seconds{stage="deploy"})
```

### Eficiencia del pipeline (% de tiempo útil vs espera)
```promql
(
  sum(jenkins_job_duration_seconds) 
  / 
  (sum(jenkins_job_duration_seconds) + sum(jenkins_queue_time_seconds))
) * 100
```

---

## 🎯 Métricas Custom

### Cobertura de tests
```promql
test_coverage
```

### Cobertura promedio de tests
```promql
avg(test_coverage)
```

### Número de bugs detectados
```promql
bugs_detected
```

### Total de bugs detectados
```promql
sum(bugs_detected)
```

### Jobs con baja cobertura (< 80%)
```promql
test_coverage < 80
```

---

## 🚨 Alertas Sugeridas

### Build tardando demasiado (> 10 minutos)
```promql
jenkins_job_duration_seconds > 600
```

### Alta tasa de fallo (> 30%)
```promql
(
  sum(jenkins_job_status == 0) 
  / 
  count(jenkins_job_status)
) > 0.3
```

### Mucho tiempo en cola (> 5 minutos)
```promql
jenkins_queue_time_seconds > 300
```

### Builds fallando consecutivamente
```promql
count_over_time(jenkins_job_status{status="failure"}[1h]) >= 3
```

### Duración aumentando (tendencia)
```promql
deriv(jenkins_job_duration_seconds[1h]) > 0
```

---

## 📊 Queries para Dashboards de Grafana

### Panel: Duración de Builds Over Time
```promql
jenkins_job_duration_seconds
```
- **Tipo:** Time series
- **Legend:** `{{job_name}}`

### Panel: Success Rate
```promql
(
  sum(jenkins_job_status == 1) 
  / 
  count(jenkins_job_status)
) * 100
```
- **Tipo:** Stat
- **Unit:** Percent (0-100)

### Panel: Builds per Hour
```promql
rate(jenkins_builds_total[1h]) * 3600
```
- **Tipo:** Graph
- **Legend:** `{{job_name}}`

### Panel: Stage Duration Breakdown
```promql
avg(jenkins_stage_duration_seconds) by (stage)
```
- **Tipo:** Bar chart
- **Legend:** `{{stage}}`

### Panel: Queue Time vs Execution Time
```promql
# Query A (Queue Time)
jenkins_queue_time_seconds

# Query B (Execution Time)
jenkins_job_duration_seconds
```
- **Tipo:** Time series (stacked)
- **Legend:** Queue / Execution

### Panel: Failed Builds Count
```promql
count(jenkins_job_status{status="failure"}) by (job_name)
```
- **Tipo:** Table
- **Columns:** Job Name, Count

### Panel: Test Coverage Trend
```promql
test_coverage
```
- **Tipo:** Time series
- **Legend:** `{{job_name}}`

---

## 🔍 Queries Avanzadas

### Comparar builds de hoy vs ayer
```promql
# Hoy
sum(increase(jenkins_builds_total[24h]))

# Ayer
sum(increase(jenkins_builds_total[24h] offset 24h))
```

### Builds que mejoraron en duración (vs build anterior)
```promql
jenkins_job_duration_seconds 
< 
jenkins_job_duration_seconds offset 1h
```

### Calcular percentil 95 de duración
```promql
quantile(0.95, jenkins_job_duration_seconds)
```

### Varianza de duración (estabilidad del pipeline)
```promql
stddev(jenkins_job_duration_seconds) by (job_name)
```

### Tiempo total de CI/CD por día
```promql
sum(increase(jenkins_job_duration_seconds[24h]))
```

### Promedio móvil de duración (últimos 10 builds)
```promql
avg_over_time(jenkins_job_duration_seconds[10m])
```

---

## 💡 Tips para Usar Estas Queries

1. **En Prometheus**:
   - Ve a http://localhost:9095
   - Pega la query en "Expression"
   - Haz clic en "Execute"
   - Cambia a "Graph" para visualización

2. **En Grafana**:
   - Crea un nuevo panel
   - Selecciona Prometheus como datasource
   - Pega la query
   - Personaliza visualización

3. **Time Ranges**:
   - Ajusta el rango de tiempo arriba
   - Usa selectores como `[1h]`, `[24h]`, `[7d]`

4. **Labels**:
   - `job_name` - Nombre del job de Jenkins
   - `build_number` - Número de build
   - `status` - success/failure/unstable
   - `stage` - Nombre del stage

5. **Funciones útiles**:
   - `sum()` - Suma
   - `avg()` - Promedio
   - `max()` - Máximo
   - `count()` - Contador
   - `rate()` - Tasa por segundo
   - `increase()` - Incremento en período

---

## 🎨 Dashboard Recomendado

Crea un dashboard en Grafana con estos paneles:

1. **Stat**: Success Rate (%)
2. **Graph**: Build Duration Over Time
3. **Graph**: Builds per Hour
4. **Bar Chart**: Average Stage Duration
5. **Table**: Recent Failed Builds
6. **Stat**: Average Queue Time
7. **Graph**: Test Coverage Trend
8. **Table**: Top 5 Slowest Jobs

---

## 📚 Recursos

- Ver métricas actuales: `./jenkins-metrics.sh view`
- Limpiar métricas: `./jenkins-metrics.sh clean`
- Pushgateway UI: http://localhost:9091
- Prometheus UI: http://localhost:9095
- Grafana UI: http://localhost:3300
