#!/bin/bash

# ============================================================================
# GENERAR DATOS PARA GRAFANA - VERSIÓN SIMPLE
# Sin K6, solo métricas y actividad
# ============================================================================

echo "═══════════════════════════════════════════════════════════════════"
echo "  Generando Datos para Grafana - Versión Simple"
echo "═══════════════════════════════════════════════════════════════════"

# ============================================================================
# 1. VERIFICAR Y CREAR DATASOURCE EN GRAFANA
# ============================================================================
echo ""
echo "[1/5] Configurando datasource de Prometheus en Grafana..."

curl -s -X POST http://localhost:3302/api/datasources \
    -u admin:admin123 \
    -H "Content-Type: application/json" \
    -d '{
        "name": "Prometheus",
        "type": "prometheus",
        "url": "http://ensurance-prometheus-full:9090",
        "access": "proxy",
        "isDefault": true,
        "jsonData": {
            "timeInterval": "15s",
            "queryTimeout": "60s"
        }
    }' 2>/dev/null || echo "  (Datasource ya existe o error al crear)"

echo "  ✅ Datasource configurado"

# ============================================================================
# 2. GENERAR ACTIVIDAD DEL SISTEMA
# ============================================================================
echo ""
echo "[2/5] Generando actividad del sistema..."

# CPU load
echo "  📊 Generando carga CPU..."
for i in {1..3}; do
    timeout 10 dd if=/dev/zero of=/dev/null bs=1M count=500 2>/dev/null &
done

# Network activity
echo "  🌐 Generando tráfico de red..."
for i in {1..20}; do
    curl -s http://localhost:9090/metrics > /dev/null 2>&1 &
    curl -s http://localhost:19999/api/v1/info > /dev/null 2>&1 &
done

wait

echo "  ✅ Actividad del sistema generada"

# ============================================================================
# 3. ENVIAR MÉTRICAS A PUSHGATEWAY
# ============================================================================
echo ""
echo "[3/5] Enviando métricas a Pushgateway..."

# Métricas de ejemplo
for service in "backend-v4" "backend-v5" "frontend-ensurance" "frontend-pharmacy"; do
    cat <<EOF | curl --data-binary @- http://localhost:9091/metrics/job/ensurance-app/instance/$service 2>/dev/null
# TYPE http_requests_total counter
http_requests_total{service="$service",method="GET",status="200"} $(( RANDOM % 1000 + 500 ))
http_requests_total{service="$service",method="POST",status="200"} $(( RANDOM % 500 + 200 ))
http_requests_total{service="$service",method="GET",status="500"} $(( RANDOM % 50 + 10 ))

# TYPE http_request_duration_seconds gauge
http_request_duration_seconds{service="$service",quantile="0.5"} 0.$(( RANDOM % 300 + 100 ))
http_request_duration_seconds{service="$service",quantile="0.95"} 0.$(( RANDOM % 800 + 300 ))
http_request_duration_seconds{service="$service",quantile="0.99"} 1.$(( RANDOM % 500 + 200 ))

# TYPE active_users gauge
active_users{service="$service"} $(( RANDOM % 100 + 20 ))

# TYPE database_connections gauge
database_connections{service="$service",state="active"} $(( RANDOM % 30 + 5 ))
database_connections{service="$service",state="idle"} $(( RANDOM % 50 + 10 ))

# TYPE cache_hit_ratio gauge
cache_hit_ratio{service="$service"} 0.$(( RANDOM % 30 + 70 ))

# TYPE memory_usage_bytes gauge
memory_usage_bytes{service="$service"} $(( RANDOM % 500000000 + 100000000 ))
EOF
    
    echo "  ✅ Métricas enviadas para $service"
done

# ============================================================================
# 4. MÉTRICAS DE K6 SIMULADAS
# ============================================================================
echo ""
echo "[4/5] Generando métricas de stress tests (K6 simulado)..."

cat <<EOF | curl --data-binary @- http://localhost:9091/metrics/job/k6-stress-test/instance/test-run-$(date +%s) 2>/dev/null
# TYPE k6_http_reqs_total counter
k6_http_reqs_total{scenario="ramping_vus",status="200"} $(( RANDOM % 2000 + 1000 ))
k6_http_reqs_total{scenario="ramping_vus",status="500"} $(( RANDOM % 50 + 10 ))

# TYPE k6_http_req_duration_seconds histogram
k6_http_req_duration_seconds{scenario="ramping_vus",quantile="0.5"} 0.$(( RANDOM % 300 + 100 ))
k6_http_req_duration_seconds{scenario="ramping_vus",quantile="0.95"} 0.$(( RANDOM % 800 + 400 ))
k6_http_req_duration_seconds{scenario="ramping_vus",quantile="0.99"} 1.$(( RANDOM % 1000 + 500 ))

# TYPE k6_vus gauge
k6_vus{scenario="ramping_vus"} $(( RANDOM % 50 + 10 ))

# TYPE k6_vus_max gauge
k6_vus_max{scenario="ramping_vus"} 100

# TYPE k6_iterations_total counter
k6_iterations_total{scenario="ramping_vus"} $(( RANDOM % 5000 + 2000 ))

# TYPE k6_data_received_bytes counter
k6_data_received_bytes{scenario="ramping_vus"} $(( RANDOM % 50000000 + 10000000 ))

# TYPE k6_data_sent_bytes counter
k6_data_sent_bytes{scenario="ramping_vus"} $(( RANDOM % 10000000 + 1000000 ))

# TYPE k6_checks_total counter
k6_checks_total{scenario="ramping_vus",check="status is 200"} $(( RANDOM % 1800 + 900 ))
k6_checks_total{scenario="ramping_vus",check="response time < 500ms"} $(( RANDOM % 1500 + 800 ))
EOF

echo "  ✅ Métricas K6 simuladas enviadas"

# ============================================================================
# 5. FORZAR SCRAPE Y VERIFICAR
# ============================================================================
echo ""
echo "[5/5] Esperando scrape de Prometheus..."

# Reload Prometheus
curl -s -X POST http://localhost:9090/-/reload 2>/dev/null || true

echo "  ⏳ Esperando 20 segundos para scrape..."
sleep 20

# Verificar que hay datos
echo ""
echo "Verificando datos en Prometheus..."

# Test query
result=$(curl -s 'http://localhost:9090/api/v1/query?query=up' | grep -o '"status":"success"')

if [ ! -z "$result" ]; then
    echo "  ✅ Prometheus respondiendo con datos"
else
    echo "  ⚠️  Prometheus puede no tener datos aún"
fi

# Contar targets UP
targets_up=$(curl -s http://localhost:9090/api/v1/targets | grep -o '"health":"up"' | wc -l)
echo "  📊 Targets activos: $targets_up"

# ============================================================================
# RESUMEN
# ============================================================================
echo ""
echo "═══════════════════════════════════════════════════════════════════"
echo "  ✅ DATOS GENERADOS PARA GRAFANA"
echo "═══════════════════════════════════════════════════════════════════"
echo ""
echo "ACCESO A GRAFANA:"
echo "  URL: http://localhost:3302"
echo "  Usuario: admin"
echo "  Password: admin123"
echo ""
echo "DASHBOARDS DISPONIBLES:"
echo "  1. Node Exporter Full → Métricas del sistema"
echo "  2. Prometheus Stats → Estado de Prometheus"
echo "  3. RabbitMQ Overview → Métricas de colas"
echo ""
echo "SI AÚN DICE 'NO DATA':"
echo "  1. Recarga la página (Ctrl + R)"
echo "  2. Cambia el rango de tiempo a 'Last 5 minutes'"
echo "  3. Ve a Configuration → Data Sources → Prometheus → Test"
echo "  4. Verifica Prometheus directamente: http://localhost:9090/graph"
echo ""
echo "MÉTRICAS GENERADAS:"
echo "  ✅ Actividad del sistema (CPU, Red)"
echo "  ✅ Métricas de aplicaciones (4 servicios)"
echo "  ✅ Métricas de stress tests (K6 simulado)"
echo "  ✅ $targets_up targets activos en Prometheus"
echo ""
echo "PARA MÁS DATOS:"
echo "  Ejecuta este script nuevamente: ./generar-datos-grafana-simple.sh"
echo ""
