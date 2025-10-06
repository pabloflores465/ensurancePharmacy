# 📊 Configuración de Métricas Prometheus

Este documento describe cómo activar y utilizar las métricas de Prometheus implementadas en los 4 componentes del sistema.

## 🎯 Resumen de Implementación

Se han implementado métricas Prometheus en:
- **backv5 (Pharmacy Backend)** - Puerto 9464
- **backv4 (Ensurance Backend)** - Puerto 9465
- **ensurance (Frontend)** - Puerto 9466
- **pharmacy (Frontend)** - Puerto 9467

## 📦 Instalación de Dependencias

### 1. Frontend Ensurance
```bash
cd ensurance
npm install
```

### 2. Frontend Pharmacy
```bash
cd pharmacy
npm install
```

### 3. Backend backv5 (Pharmacy)
```bash
cd backv5
mvn clean install -DskipTests
```

### 4. Backend backv4 (Ensurance)
```bash
cd backv4
mvn clean install -DskipTests
```

## 🚀 Ejecución de Servidores de Métricas

### Opción 1: Ejecutar cada componente individualmente

#### Backend backv5 (Pharmacy)
```bash
cd backv5
# Compilar
mvn clean package -DskipTests
# Ejecutar con métricas en puerto 9464
METRICS_HOST=0.0.0.0 METRICS_PORT=9464 java --enable-preview -jar target/backv5-1.0-SNAPSHOT.jar
```

#### Backend backv4 (Ensurance)
```bash
cd backv4
# Compilar
mvn clean package -DskipTests
# Ejecutar con métricas en puerto 9465
METRICS_HOST=0.0.0.0 METRICS_PORT=9465 java --enable-preview -jar target/backv4-1.0-SNAPSHOT.jar
```

#### Frontend Ensurance
```bash
cd ensurance
# Terminal 1: Servidor de aplicación
npm run dev

# Terminal 2: Servidor de métricas (puerto 9466)
npm run metrics
```

#### Frontend Pharmacy
```bash
cd pharmacy
# Terminal 1: Servidor de aplicación
npm run serve

# Terminal 2: Servidor de métricas (puerto 9467)
npm run metrics
```

### Opción 2: Script de ejecución en paralelo

Crea un archivo `start-all-metrics.sh`:

```bash
#!/bin/bash

# Compilar backends
echo "📦 Compilando backends..."
(cd backv5 && mvn clean package -DskipTests) &
(cd backv4 && mvn clean package -DskipTests) &
wait

# Iniciar backends con métricas
echo "🚀 Iniciando backends..."
METRICS_HOST=0.0.0.0 METRICS_PORT=9464 java --enable-preview -jar backv5/target/backv5-1.0-SNAPSHOT.jar &
METRICS_HOST=0.0.0.0 METRICS_PORT=9465 java --enable-preview -jar backv4/target/backv4-1.0-SNAPSHOT.jar &

# Iniciar servidores de métricas de frontend
echo "📊 Iniciando servidores de métricas frontend..."
(cd ensurance && npm run metrics) &
(cd pharmacy && npm run metrics) &

# Iniciar aplicaciones frontend
echo "🌐 Iniciando aplicaciones frontend..."
(cd ensurance && npm run dev) &
(cd pharmacy && npm run serve) &

echo "✅ Todos los servicios están iniciando..."
echo ""
echo "📊 Endpoints de métricas:"
echo "  - backv5:     http://localhost:9464/metrics"
echo "  - backv4:     http://localhost:9465/metrics"
echo "  - ensurance:  http://localhost:9466/metrics"
echo "  - pharmacy:   http://localhost:9467/metrics"
echo ""
echo "Presiona Ctrl+C para detener todos los servicios"
wait
```

Hacer ejecutable y correr:
```bash
chmod +x start-all-metrics.sh
./start-all-metrics.sh
```

## 🔍 Verificación de Métricas

Verificar que los endpoints de métricas están funcionando:

```bash
# backv5 (Pharmacy Backend)
curl http://localhost:9464/metrics

# backv4 (Ensurance Backend)
curl http://localhost:9465/metrics

# ensurance (Frontend)
curl http://localhost:9466/metrics

# pharmacy (Frontend)
curl http://localhost:9467/metrics
```

## 🐳 Prometheus

El archivo `monitoring/prometheus/prometheus.yml` ya está configurado con los 4 targets:

```yaml
scrape_configs:
  - job_name: 'backv5-pharmacy'
    static_configs:
      - targets: ['host.docker.internal:9464']
  
  - job_name: 'backv4-ensurance'
    static_configs:
      - targets: ['host.docker.internal:9465']
  
  - job_name: 'ensurance-frontend'
    static_configs:
      - targets: ['host.docker.internal:9466']
  
  - job_name: 'pharmacy-frontend'
    static_configs:
      - targets: ['host.docker.internal:9467']
```

### Iniciar Prometheus y Grafana

```bash
docker compose -f docker-compose.monitor.yml up -d
```

Acceder a:
- **Prometheus**: http://localhost:9095
- **Grafana**: http://localhost:3300 (usuario: `admin`, contraseña: `changeme`)

## 📈 Métricas Disponibles

### backv5 (Pharmacy Backend)
- `ensurance_http_requests_total` - Total de peticiones HTTP
- `ensurance_http_request_duration_seconds` - Latencia de peticiones
- `ensurance_http_inflight_requests` - Peticiones en curso
- `ensurance_http_request_size_bytes` - Tamaño de payloads
- Métricas JVM (memoria, CPU, threads, GC)

### backv4 (Ensurance Backend)
- `ensurance_db_queries_total` - Total de consultas a base de datos
- Métricas JVM (memoria, CPU, threads, GC)

### ensurance (Frontend)
- `ensurance_frontend_page_views_total` - Vistas de página
- Métricas de proceso Node.js

### pharmacy (Frontend)
- `pharmacy_frontend_medicine_searches_total` - Búsquedas de medicamentos
- Métricas de proceso Node.js

## 🛠️ Variables de Entorno

Puedes personalizar los puertos de métricas:

```bash
# Backend backv5
METRICS_HOST=0.0.0.0
METRICS_PORT=9464

# Backend backv4
METRICS_HOST=0.0.0.0
METRICS_PORT=9465

# Frontend ensurance
METRICS_HOST=0.0.0.0
METRICS_PORT=9466

# Frontend pharmacy
METRICS_HOST=0.0.0.0
METRICS_PORT=9467
```

## 🎯 Queries PromQL Útiles

En Prometheus, prueba estas consultas:

```promql
# Rate de peticiones HTTP en backv5 por endpoint
rate(ensurance_http_requests_total[5m])

# Latencia p95 de peticiones HTTP
histogram_quantile(0.95, rate(ensurance_http_request_duration_seconds_bucket[5m]))

# Peticiones en curso
ensurance_http_inflight_requests

# Uso de memoria JVM
jvm_memory_used_bytes

# Rate de búsquedas en pharmacy frontend
rate(pharmacy_frontend_medicine_searches_total[5m])
```

## 📝 Notas Importantes

1. **Compilación requerida**: Los backends Java requieren compilación antes de ejecutar
2. **Puertos independientes**: Cada componente expone métricas en un puerto diferente
3. **JVM metrics**: Los backends Java incluyen métricas automáticas de JVM
4. **Frontend metrics**: Los frontends necesitan ejecutar dos procesos: app + metrics server
5. **Docker networking**: Si usas Docker, asegúrate que `host.docker.internal` resuelve correctamente

## 🔧 Troubleshooting

### Maven no encontrado
```bash
# Verificar instalación
mvn --version

# Alternativa: usar wrapper si existe
./mvnw clean package -DskipTests
```

### Puerto en uso
```bash
# Verificar qué proceso usa un puerto
lsof -i :9464

# Matar proceso si es necesario
kill -9 <PID>
```

### Métricas no aparecen en Prometheus
1. Verificar que los servicios están corriendo
2. Verificar endpoints con `curl`
3. Revisar logs de Prometheus: `docker logs prometheus`
4. Verificar configuración en `prometheus.yml`
