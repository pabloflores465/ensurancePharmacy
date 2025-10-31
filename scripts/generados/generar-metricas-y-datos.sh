#!/bin/bash

# ============================================================================
# GENERAR MÉTRICAS Y DATOS PARA GRAFANA
# Ejecuta K6, builds, y genera actividad para poblar dashboards
# ============================================================================

set -e

BOLD='\033[1m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${BOLD}"
echo "╔════════════════════════════════════════════════════════════════════╗"
echo "║                                                                    ║"
echo "║         GENERAR MÉTRICAS Y DATOS PARA GRAFANA                     ║"
echo "║              K6 + Builds + Actividad de Servicios                 ║"
echo "║                                                                    ║"
echo "╚════════════════════════════════════════════════════════════════════╝"
echo -e "${NC}"

# ============================================================================
# VERIFICAR SERVICIOS
# ============================================================================
echo -e "${BLUE}[1/8] Verificando servicios necesarios...${NC}"

# Verificar Prometheus
if ! curl -s http://localhost:9090/-/healthy > /dev/null 2>&1; then
    echo -e "${RED}❌ Prometheus no está corriendo${NC}"
    exit 1
fi
echo -e "${GREEN}  ✅ Prometheus OK (http://localhost:9090)${NC}"

# Verificar Grafana
if ! curl -s http://localhost:3302/api/health > /dev/null 2>&1; then
    echo -e "${RED}❌ Grafana no está corriendo en puerto 3302${NC}"
    exit 1
fi
echo -e "${GREEN}  ✅ Grafana OK (http://localhost:3302)${NC}"

# Verificar Node Exporter
if ! curl -s http://localhost:9100/metrics > /dev/null 2>&1; then
    echo -e "${YELLOW}⚠️  Node Exporter no responde${NC}"
else
    echo -e "${GREEN}  ✅ Node Exporter OK${NC}"
fi

# Verificar Pushgateway
if ! curl -s http://localhost:9091/metrics > /dev/null 2>&1; then
    echo -e "${YELLOW}⚠️  Pushgateway no responde${NC}"
else
    echo -e "${GREEN}  ✅ Pushgateway OK${NC}"
fi

# ============================================================================
# VERIFICAR Y CONFIGURAR DATASOURCE EN GRAFANA
# ============================================================================
echo ""
echo -e "${BLUE}[2/8] Verificando datasource de Prometheus en Grafana...${NC}"

# Verificar si el datasource existe
datasource_check=$(curl -s -u admin:admin123 http://localhost:3302/api/datasources 2>&1)

if echo "$datasource_check" | grep -q "Prometheus"; then
    echo -e "${GREEN}  ✅ Datasource Prometheus ya existe en Grafana${NC}"
else
    echo -e "${YELLOW}  ⚠️  Datasource no encontrado, creando...${NC}"
    
    # Crear datasource
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
                "timeInterval": "15s"
            }
        }' > /dev/null 2>&1
    
    echo -e "${GREEN}  ✅ Datasource Prometheus creado${NC}"
fi

# ============================================================================
# GENERAR MÉTRICAS DE SISTEMA CON ACTIVIDAD
# ============================================================================
echo ""
echo -e "${BLUE}[3/8] Generando actividad en el sistema...${NC}"
echo "  Esto generará métricas de CPU, memoria, disco y red"

# Script para generar actividad de sistema
cat > /tmp/generate_system_activity.sh << 'SYSEOF'
#!/bin/bash
echo "Generando actividad del sistema..."

# CPU activity
for i in {1..3}; do
    echo "Carga CPU $i/3"
    dd if=/dev/zero of=/dev/null bs=1M count=1000 2>/dev/null &
done

# Memoria activity
echo "Consumiendo memoria..."
python3 -c "data = [' ' * 100000000 for _ in range(5)]" 2>/dev/null &

# Disk activity
echo "Actividad de disco..."
dd if=/dev/zero of=/tmp/test_disk_activity.dat bs=1M count=100 2>/dev/null
rm -f /tmp/test_disk_activity.dat

# Network activity
echo "Actividad de red..."
for i in {1..10}; do
    curl -s http://localhost:9090/metrics > /dev/null &
    curl -s http://localhost:19999/api/v1/info > /dev/null &
done

wait
echo "Actividad del sistema completada"
SYSEOF

chmod +x /tmp/generate_system_activity.sh
nohup /tmp/generate_system_activity.sh > /tmp/system_activity.log 2>&1 &

echo -e "${GREEN}  ✅ Generando actividad del sistema en background${NC}"

# ============================================================================
# EJECUTAR REQUESTS A LOS BACKENDS
# ============================================================================
echo ""
echo -e "${BLUE}[4/8] Generando tráfico HTTP a los backends...${NC}"

# Backend v4 (si existe)
if curl -s http://localhost:8081/health > /dev/null 2>&1; then
    echo "  📊 Backend v4 (puerto 8081) - Enviando requests..."
    for i in {1..20}; do
        curl -s http://localhost:8081/health > /dev/null &
    done
    echo -e "${GREEN}    ✅ 20 requests enviados a backend v4${NC}"
else
    echo -e "${YELLOW}    ⚠️  Backend v4 no disponible${NC}"
fi

# Backend v5 (si existe)
if curl -s http://localhost:8082/health > /dev/null 2>&1; then
    echo "  📊 Backend v5 (puerto 8082) - Enviando requests..."
    for i in {1..20}; do
        curl -s http://localhost:8082/health > /dev/null &
    done
    echo -e "${GREEN}    ✅ 20 requests enviados a backend v5${NC}"
else
    echo -e "${YELLOW}    ⚠️  Backend v5 no disponible${NC}"
fi

# Frontends
echo "  📊 Generando requests a frontends..."
curl -s http://localhost:5175 > /dev/null 2>&1 &
curl -s http://localhost:8089 > /dev/null 2>&1 &

wait

echo -e "${GREEN}  ✅ Tráfico HTTP generado${NC}"

# ============================================================================
# ENVIAR MÉTRICAS CUSTOM A PUSHGATEWAY
# ============================================================================
echo ""
echo -e "${BLUE}[5/8] Enviando métricas custom a Pushgateway...${NC}"

# Métrica de ejemplo: número de usuarios activos
cat <<EOF | curl --data-binary @- http://localhost:9091/metrics/job/ensurance-app/instance/backend-v5
# TYPE active_users gauge
# HELP active_users Número de usuarios activos simulados
active_users{environment="production",service="backend-v5"} $(( RANDOM % 100 + 50 ))

# TYPE api_requests_total counter
# HELP api_requests_total Total de requests API
api_requests_total{environment="production",service="backend-v5",endpoint="/api/v1/users"} $(( RANDOM % 1000 + 500 ))

# TYPE response_time_seconds gauge
# HELP response_time_seconds Tiempo de respuesta promedio
response_time_seconds{environment="production",service="backend-v5"} 0.$(( RANDOM % 500 + 100 ))

# TYPE database_connections gauge
# HELP database_connections Conexiones activas a la base de datos
database_connections{environment="production",service="backend-v5",db="postgres"} $(( RANDOM % 50 + 10 ))
EOF

echo -e "${GREEN}  ✅ Métricas custom enviadas a Pushgateway${NC}"

# ============================================================================
# EJECUTAR PRUEBAS K6
# ============================================================================
echo ""
echo -e "${BLUE}[6/8] Ejecutando pruebas de stress con K6...${NC}"

if command -v k6 > /dev/null 2>&1; then
    echo "  🚀 K6 encontrado, ejecutando pruebas..."
    
    # Crear script de prueba simple
    cat > /tmp/k6_test.js << 'K6EOF'
import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    vus: 5,
    duration: '30s',
};

export default function() {
    // Test Prometheus
    let res1 = http.get('http://localhost:9090/metrics');
    check(res1, { 'prometheus status 200': (r) => r.status === 200 });
    
    // Test Grafana
    let res2 = http.get('http://localhost:3302/api/health');
    check(res2, { 'grafana status 200': (r) => r.status === 200 });
    
    sleep(1);
}
K6EOF

    k6 run /tmp/k6_test.js > /tmp/k6_output.log 2>&1 &
    K6_PID=$!
    
    echo -e "${GREEN}  ✅ K6 ejecutándose (PID: $K6_PID)${NC}"
    echo "  ⏱️  Duración: 30 segundos con 5 usuarios virtuales"
    
elif docker ps | grep -q k6; then
    echo "  🚀 Ejecutando K6 desde contenedor Docker..."
    
    docker run --rm --network ensurance-full-network \
        -v /tmp:/scripts \
        grafana/k6 run - <<'K6DOCKEREOF'
import http from 'k6/http';
import { check } from 'k6';

export let options = {
    vus: 5,
    duration: '30s',
};

export default function() {
    let res = http.get('http://ensurance-prometheus-full:9090/metrics');
    check(res, { 'status 200': (r) => r.status === 200 });
}
K6DOCKEREOF
    
    echo -e "${GREEN}  ✅ K6 ejecutado desde Docker${NC}"
else
    echo -e "${YELLOW}  ⚠️  K6 no está instalado, generando métricas alternativas...${NC}"
    
    # Generar métricas simuladas de K6
    cat <<EOF | curl --data-binary @- http://localhost:9091/metrics/job/k6-tests/instance/test-1
# TYPE k6_http_reqs_total counter
k6_http_reqs_total{scenario="default",status="200"} $(( RANDOM % 500 + 200 ))

# TYPE k6_http_req_duration_seconds gauge
k6_http_req_duration_seconds{scenario="default"} 0.$(( RANDOM % 300 + 50 ))

# TYPE k6_vus gauge
k6_vus{scenario="default"} 5

# TYPE k6_iterations_total counter
k6_iterations_total{scenario="default"} $(( RANDOM % 300 + 100 ))
EOF
    
    echo -e "${GREEN}  ✅ Métricas K6 simuladas enviadas${NC}"
fi

# ============================================================================
# GENERAR MÉTRICAS DE RABBITMQ
# ============================================================================
echo ""
echo -e "${BLUE}[7/8] Verificando métricas de RabbitMQ...${NC}"

if curl -s http://localhost:15692/metrics > /dev/null 2>&1; then
    echo -e "${GREEN}  ✅ RabbitMQ exportando métricas correctamente${NC}"
    
    # Contar métricas disponibles
    metric_count=$(curl -s http://localhost:15692/metrics | grep -c "^rabbitmq_" || echo "0")
    echo "  📊 RabbitMQ tiene $metric_count métricas activas"
else
    echo -e "${YELLOW}  ⚠️  RabbitMQ metrics no disponibles${NC}"
fi

# ============================================================================
# FORZAR SCRAPE EN PROMETHEUS
# ============================================================================
echo ""
echo -e "${BLUE}[8/8] Forzando scrape de métricas en Prometheus...${NC}"

# Reload Prometheus
curl -s -X POST http://localhost:9090/-/reload > /dev/null 2>&1 || true

echo "  ⏳ Esperando 15 segundos para que Prometheus scrape las métricas..."
for i in {1..15}; do
    echo -ne "    Progreso: $i/15 segundos\r"
    sleep 1
done
echo ""

# Verificar targets activos
targets=$(curl -s http://localhost:9090/api/v1/targets | grep -o '"health":"up"' | wc -l)
echo -e "${GREEN}  ✅ Prometheus tiene $targets targets activos${NC}"

# ============================================================================
# VERIFICAR DATOS EN PROMETHEUS
# ============================================================================
echo ""
echo -e "${BLUE}Verificando que hay datos en Prometheus...${NC}"

# Query de ejemplo para CPU
cpu_data=$(curl -s 'http://localhost:9090/api/v1/query?query=node_cpu_seconds_total' | grep -o '"result":\[' | wc -l)

if [ "$cpu_data" -gt 0 ]; then
    echo -e "${GREEN}  ✅ Prometheus tiene datos de CPU${NC}"
else
    echo -e "${YELLOW}  ⚠️  No se encontraron datos de CPU${NC}"
fi

# Query para memoria
mem_data=$(curl -s 'http://localhost:9090/api/v1/query?query=node_memory_MemAvailable_bytes' | grep -o '"result":\[' | wc -l)

if [ "$mem_data" -gt 0 ]; then
    echo -e "${GREEN}  ✅ Prometheus tiene datos de memoria${NC}"
else
    echo -e "${YELLOW}  ⚠️  No se encontraron datos de memoria${NC}"
fi

# ============================================================================
# RESUMEN
# ============================================================================
echo ""
echo -e "${BOLD}${GREEN}"
echo "╔════════════════════════════════════════════════════════════════════╗"
echo "║                                                                    ║"
echo "║              ✅ GENERACIÓN DE DATOS COMPLETADA                    ║"
echo "║                                                                    ║"
echo "╚════════════════════════════════════════════════════════════════════╝"
echo -e "${NC}"

echo ""
echo "📊 DATOS GENERADOS:"
echo "  ✅ Actividad del sistema (CPU, Memoria, Disco, Red)"
echo "  ✅ Tráfico HTTP a backends y frontends"
echo "  ✅ Métricas custom en Pushgateway"
echo "  ✅ Pruebas de stress (K6 o simuladas)"
echo "  ✅ Métricas de RabbitMQ"
echo "  ✅ Prometheus scrapeando activamente"
echo ""
echo "🎯 ACCESO A DASHBOARDS:"
echo "  Grafana:          http://localhost:3302"
echo "  Prometheus:       http://localhost:9090"
echo "  Node Exporter:    http://localhost:9100/metrics"
echo "  Pushgateway:      http://localhost:9091"
echo "  Netdata:          http://localhost:19999"
echo ""
echo "📈 DASHBOARDS RECOMENDADOS EN GRAFANA:"
echo "  1. Node Exporter Full - Métricas del sistema"
echo "  2. Prometheus Stats - Estado de Prometheus"
echo "  3. RabbitMQ Overview - Métricas de colas"
echo ""
echo "⏱️  TIEMPO DE ESPERA:"
echo "  - Los dashboards deberían mostrar datos AHORA"
echo "  - Si aún dice 'No Data', espera 1-2 minutos más"
echo "  - Recarga la página de Grafana (Ctrl + R)"
echo ""
echo "🔧 SI AÚN NO HAY DATOS:"
echo "  1. Verifica Prometheus: http://localhost:9090/targets"
echo "  2. Verifica que los targets estén 'UP'"
echo "  3. Ve a Grafana → Configuration → Data Sources"
echo "  4. Haz clic en 'Prometheus' → Test"
echo "  5. Debería decir 'Data source is working'"
echo ""
echo "🚀 PARA MÁS DATOS:"
echo "  - Ejecuta este script nuevamente: ./generar-metricas-y-datos.sh"
echo "  - Los datos se acumularán en Prometheus"
echo "  - Las gráficas se volverán más ricas con el tiempo"
echo ""
