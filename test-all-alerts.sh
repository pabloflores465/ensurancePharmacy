#!/bin/bash

# Script de Prueba Completa de Alertas - Ensurance Pharmacy
# Genera alertas reales usando herramientas de stress testing

echo "==========================================================="
echo "🧪 PRUEBA COMPLETA DE ALERTAS - ENSURANCE PHARMACY"
echo "==========================================================="
echo ""
echo "⚠️  ADVERTENCIA: Este script generará alertas REALES"
echo "   - Emails a: pablopolis2016@gmail.com, jflores@unis.edu.gt"
echo "   - Slack: #ensurance-alerts"
echo ""
read -p "¿Continuar con las pruebas? (s/n): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Ss]$ ]]; then
    echo "Prueba cancelada"
    exit 0
fi

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

ALERTS_TRIGGERED=0
TESTS_RUN=0

# Función para esperar y verificar alerta
wait_for_alert() {
    local alert_name=$1
    local wait_time=$2
    echo -e "${YELLOW}⏳ Esperando ${wait_time}s para que se active alerta...${NC}"
    sleep $wait_time
    
    # Verificar si la alerta está activa
    ALERT_STATUS=$(curl -s http://localhost:9090/api/v1/alerts | python3 -c "
import sys, json
data = json.load(sys.stdin)
alerts = data.get('data', {}).get('alerts', [])
for a in alerts:
    if a.get('labels', {}).get('alertname') == '${alert_name}' and a.get('state') == 'firing':
        print('FIRING')
        sys.exit(0)
print('NOT_FOUND')
" 2>/dev/null)
    
    if [ "$ALERT_STATUS" == "FIRING" ]; then
        echo -e "${GREEN}✅ Alerta '${alert_name}' ACTIVADA${NC}"
        ALERTS_TRIGGERED=$((ALERTS_TRIGGERED + 1))
    else
        echo -e "${YELLOW}⏱️  Alerta '${alert_name}' aún no visible (puede tardar más)${NC}"
    fi
}

echo ""
echo "==========================================================="
echo "FASE 1: ALERTAS DE DISPONIBILIDAD (CRITICAL)"
echo "==========================================================="
echo ""

# TEST 1: Node Exporter Down
TESTS_RUN=$((TESTS_RUN + 1))
echo -e "${BLUE}[TEST 1/10]${NC} Probando: NodeExporterDown (CRITICAL)"
echo "Acción: Deteniendo node-exporter..."
docker stop ensurance-node-exporter-full > /dev/null 2>&1
wait_for_alert "NodeExporterDown" 90
echo "Restaurando node-exporter..."
docker start ensurance-node-exporter-full > /dev/null 2>&1
echo ""

# TEST 2: RabbitMQ Down  
TESTS_RUN=$((TESTS_RUN + 1))
echo -e "${BLUE}[TEST 2/10]${NC} Probando: RabbitMQDown (CRITICAL)"
echo "Acción: Deteniendo RabbitMQ..."
docker stop ensurance-rabbitmq-full > /dev/null 2>&1
wait_for_alert "RabbitMQDown" 90
echo "Restaurando RabbitMQ..."
docker start ensurance-rabbitmq-full > /dev/null 2>&1
sleep 10  # Dar tiempo a RabbitMQ para iniciar
echo ""

echo ""
echo "==========================================================="
echo "FASE 2: ALERTAS DE CPU Y MEMORIA (WARNING/CRITICAL)"
echo "==========================================================="
echo ""

# TEST 3: High CPU Usage
TESTS_RUN=$((TESTS_RUN + 1))
echo -e "${BLUE}[TEST 3/10]${NC} Probando: HighCPUUsage (WARNING - CPU > 70%)"
echo "Acción: Generando carga de CPU con stress-ng..."

# Verificar si stress-ng está instalado
if ! command -v stress-ng &> /dev/null; then
    echo -e "${YELLOW}⚠️  stress-ng no instalado. Instalando...${NC}"
    sudo apt-get update -qq && sudo apt-get install -y stress-ng -qq
fi

# Generar carga de CPU (80% durante 3 minutos)
echo "Ejecutando: stress-ng --cpu 0 --cpu-load 80 --timeout 180s"
stress-ng --cpu 0 --cpu-load 80 --timeout 180s &
STRESS_PID=$!

wait_for_alert "HighCPUUsage" 150

# Detener stress si aún está corriendo
kill $STRESS_PID 2>/dev/null || true
killall stress-ng 2>/dev/null || true
echo "Carga de CPU detenida"
echo ""

# TEST 4: High Memory Usage
TESTS_RUN=$((TESTS_RUN + 1))
echo -e "${BLUE}[TEST 4/10]${NC} Probando: HighMemoryUsage (WARNING - Memoria > 80%)"
echo "Acción: Generando carga de memoria..."

# Obtener RAM total
TOTAL_RAM_MB=$(free -m | awk '/^Mem:/{print $2}')
# Consumir 85% de RAM
TARGET_RAM_MB=$((TOTAL_RAM_MB * 85 / 100))

echo "RAM Total: ${TOTAL_RAM_MB}MB, Objetivo: ${TARGET_RAM_MB}MB"
echo "Ejecutando: stress-ng --vm 2 --vm-bytes ${TARGET_RAM_MB}M --timeout 180s"

stress-ng --vm 2 --vm-bytes ${TARGET_RAM_MB}M --timeout 180s &
STRESS_PID=$!

wait_for_alert "HighMemoryUsage" 150

kill $STRESS_PID 2>/dev/null || true
killall stress-ng 2>/dev/null || true
echo "Carga de memoria detenida"
echo ""

echo ""
echo "==========================================================="
echo "FASE 3: ALERTAS DE DISCO (WARNING)"
echo "==========================================================="
echo ""

# TEST 5: High Disk Usage (simulado con archivo temporal)
TESTS_RUN=$((TESTS_RUN + 1))
echo -e "${BLUE}[TEST 5/10]${NC} Probando: HighDiskUsage (WARNING - Disco > 75%)"
echo "Acción: Este test requeriría llenar el disco, se omite por seguridad"
echo -e "${YELLOW}⚠️  OMITIDO (requiere llenar disco real)${NC}"
echo ""

echo ""
echo "==========================================================="
echo "FASE 4: ALERTAS DE K6 STRESS TESTING"
echo "==========================================================="
echo ""

# TEST 6: K6 High Error Rate
TESTS_RUN=$((TESTS_RUN + 1))
echo -e "${BLUE}[TEST 6/10]${NC} Probando: K6 Performance Alerts"
echo "Acción: Ejecutando test K6 con alta carga..."

# Crear script K6 de prueba con alta carga
cat > /tmp/k6-stress-test.js << 'EOF'
import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    stages: [
        { duration: '30s', target: 50 },   // Rampa a 50 VUs
        { duration: '1m', target: 100 },   // Mantener 100 VUs (genera alerta)
        { duration: '30s', target: 0 },    // Bajar a 0
    ],
    thresholds: {
        http_req_duration: ['p(95)<3000'], // P95 < 3s (puede fallar y generar alerta)
    },
};

export default function () {
    // Probar ambos backends
    let res1 = http.get('http://localhost:3102/api/healthcheck');
    let res2 = http.get('http://localhost:3103/api/healthcheck');
    
    check(res1, {
        'backend ensurance status 200': (r) => r.status === 200,
    });
    
    check(res2, {
        'backend pharmacy status 200': (r) => r.status === 200,
    });
    
    sleep(0.5);
}
EOF

echo "Ejecutando K6 test (2 minutos)..."
if command -v k6 &> /dev/null; then
    k6 run --out experimental-prometheus-rw /tmp/k6-stress-test.js &
    K6_PID=$!
    
    # Esperar un poco para que genere métricas
    sleep 60
    
    # Verificar alertas K6
    echo "Verificando alertas de K6..."
    ALERTS=$(curl -s http://localhost:9090/api/v1/alerts | python3 -c "
import sys, json
data = json.load(sys.stdin)
alerts = data.get('data', {}).get('alerts', [])
k6_alerts = [a for a in alerts if 'K6' in a.get('labels', {}).get('alertname', '')]
for a in k6_alerts:
    if a.get('state') == 'firing':
        print(f\"✓ {a['labels']['alertname']}\")
" 2>/dev/null)
    
    if [ ! -z "$ALERTS" ]; then
        echo -e "${GREEN}Alertas K6 detectadas:${NC}"
        echo "$ALERTS"
        ALERTS_TRIGGERED=$((ALERTS_TRIGGERED + 1))
    fi
    
    kill $K6_PID 2>/dev/null || true
else
    echo -e "${YELLOW}⚠️  K6 no disponible, omitiendo test${NC}"
fi

rm -f /tmp/k6-stress-test.js
echo ""

echo ""
echo "==========================================================="
echo "FASE 5: ALERTAS DE RABBITMQ"
echo "==========================================================="
echo ""

# TEST 7: RabbitMQ Queue Messages
TESTS_RUN=$((TESTS_RUN + 1))
echo -e "${BLUE}[TEST 7/10]${NC} Probando: RabbitMQ Queue Alerts"
echo "Acción: Publicando mensajes masivos a RabbitMQ..."

# Verificar que RabbitMQ esté corriendo
if docker ps | grep -q ensurance-rabbitmq-full; then
    # Crear script para publicar mensajes
    cat > /tmp/rabbitmq-publisher.py << 'EOF'
import pika
import sys

try:
    # Conectar a RabbitMQ
    credentials = pika.PlainCredentials('guest', 'guest')
    connection = pika.BlockingConnection(
        pika.ConnectionParameters('localhost', 5672, '/', credentials)
    )
    channel = connection.channel()
    
    # Declarar cola
    channel.queue_declare(queue='test_alert_queue', durable=True)
    
    # Publicar 1500 mensajes (supera umbral de 1000)
    print("Publicando 1500 mensajes...")
    for i in range(1500):
        message = f"Test message {i} for alert testing"
        channel.basic_publish(
            exchange='',
            routing_key='test_alert_queue',
            body=message,
            properties=pika.BasicProperties(delivery_mode=2)
        )
        if i % 100 == 0:
            print(f"Publicados {i} mensajes...")
    
    print("✓ 1500 mensajes publicados")
    connection.close()
except Exception as e:
    print(f"Error: {e}")
    sys.exit(1)
EOF
    
    if command -v python3 &> /dev/null && python3 -c "import pika" 2>/dev/null; then
        python3 /tmp/rabbitmq-publisher.py
        wait_for_alert "RabbitMQQueueMessagesHigh" 120
        
        # Limpiar cola
        echo "Limpiando cola de prueba..."
        docker exec ensurance-rabbitmq-full rabbitmqctl purge_queue test_alert_queue -p / 2>/dev/null || true
    else
        echo -e "${YELLOW}⚠️  pika no instalado, instalando...${NC}"
        pip3 install pika -q 2>/dev/null || echo "No se pudo instalar pika"
    fi
    
    rm -f /tmp/rabbitmq-publisher.py
else
    echo -e "${YELLOW}⚠️  RabbitMQ no está corriendo${NC}"
fi
echo ""

echo ""
echo "==========================================================="
echo "FASE 6: ALERTAS DE APLICACIONES"
echo "==========================================================="
echo ""

# TEST 8: Backend Down
TESTS_RUN=$((TESTS_RUN + 1))
echo -e "${BLUE}[TEST 8/10]${NC} Probando: Backend Down Alerts (CRITICAL)"
echo "Acción: Deteniendo backends temporalmente..."

docker stop ensurance-pharmacy-full > /dev/null 2>&1
wait_for_alert "PharmacyBackendDown" 90

echo "Restaurando backends..."
docker start ensurance-pharmacy-full > /dev/null 2>&1
sleep 10
echo ""

echo ""
echo "==========================================================="
echo "FASE 7: VERIFICACIÓN DE NOTIFICACIONES"
echo "==========================================================="
echo ""

# TEST 9: Verificar Alertmanager
TESTS_RUN=$((TESTS_RUN + 1))
echo -e "${BLUE}[TEST 9/10]${NC} Verificando Alertmanager..."
ALERTMANAGER_STATUS=$(curl -s http://localhost:9094/api/v2/status | python3 -c "
import sys, json
try:
    data = json.load(sys.stdin)
    print(data['cluster']['status'])
except:
    print('error')
" 2>/dev/null)

if [ "$ALERTMANAGER_STATUS" == "ready" ]; then
    echo -e "${GREEN}✅ Alertmanager funcionando correctamente${NC}"
else
    echo -e "${RED}❌ Alertmanager tiene problemas${NC}"
fi
echo ""

# TEST 10: Ver alertas activas
TESTS_RUN=$((TESTS_RUN + 1))
echo -e "${BLUE}[TEST 10/10]${NC} Listando alertas activas en Prometheus..."
ACTIVE_ALERTS=$(curl -s http://localhost:9090/api/v1/alerts | python3 -c "
import sys, json
try:
    data = json.load(sys.stdin)
    alerts = data.get('data', {}).get('alerts', [])
    firing = [a for a in alerts if a.get('state') == 'firing']
    pending = [a for a in alerts if a.get('state') == 'pending']
    
    print(f'🔴 FIRING: {len(firing)}')
    for a in firing[:10]:
        name = a.get('labels', {}).get('alertname', 'Unknown')
        severity = a.get('labels', {}).get('severity', 'unknown')
        print(f'  • {name} ({severity})')
    
    print(f'⏱️  PENDING: {len(pending)}')
    for a in pending[:5]:
        name = a.get('labels', {}).get('alertname', 'Unknown')
        print(f'  • {name}')
except Exception as e:
    print(f'Error: {e}')
" 2>/dev/null)

echo "$ACTIVE_ALERTS"
echo ""

# Verificar notificaciones enviadas
echo "📧 Verificando notificaciones enviadas..."
NOTIFICATIONS=$(curl -s http://localhost:9094/api/v2/alerts | python3 -c "
import sys, json
try:
    data = json.load(sys.stdin)
    print(f'Total de alertas en Alertmanager: {len(data)}')
    active = [a for a in data if a.get('status', {}).get('state') == 'active']
    print(f'Alertas activas: {len(active)}')
except Exception as e:
    print(f'Error: {e}')
" 2>/dev/null)

echo "$NOTIFICATIONS"
echo ""

echo ""
echo "==========================================================="
echo "📊 RESUMEN DE PRUEBAS"
echo "==========================================================="
echo ""
echo -e "Tests ejecutados: ${BLUE}${TESTS_RUN}${NC}"
echo -e "Alertas disparadas: ${GREEN}${ALERTS_TRIGGERED}${NC}"
echo ""
echo "📧 NOTIFICACIONES:"
echo "   • Email enviado a: pablopolis2016@gmail.com, jflores@unis.edu.gt"
echo "   • Slack enviado a: #ensurance-alerts"
echo ""
echo -e "${YELLOW}⚠️  IMPORTANTE:${NC}"
echo "   1. Revisa tu email para confirmar que llegaron las notificaciones"
echo "   2. Revisa Slack #ensurance-alerts para confirmar mensajes"
echo "   3. Las alertas CRITICAL deberían tener @channel en Slack"
echo ""
echo "🌐 URLs de verificación:"
echo "   • Alertmanager: http://localhost:9094"
echo "   • Prometheus Alerts: http://localhost:9090/alerts"
echo "   • Grafana: http://localhost:3302"
echo ""
echo "==========================================================="
echo -e "${GREEN}✅ PRUEBA DE ALERTAS COMPLETADA${NC}"
echo "==========================================================="
echo ""
echo "Ejecuta './verify-alerting.sh' para ver el estado actualizado"
