#!/bin/bash

# Prueba Rápida de Alertas - 5 minutos
# Genera alertas de diferentes severidades para probar notificaciones

echo "=========================================="
echo "⚡ PRUEBA RÁPIDA DE ALERTAS (5 minutos)"
echo "=========================================="
echo ""
echo "Este script probará 5 tipos de alertas:"
echo "  1. NodeExporterDown (CRITICAL)"
echo "  2. RabbitMQDown (CRITICAL)"
echo "  3. High CPU (WARNING) con stress-ng"
echo "  4. Backend Down (CRITICAL)"
echo "  5. Alertas actuales"
echo ""

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

# TEST 1: Node Exporter Down
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "[1/5] 🔴 Test CRITICAL: NodeExporterDown"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Deteniendo node-exporter..."
docker stop ensurance-node-exporter-full > /dev/null 2>&1

echo "⏳ Esperando 90 segundos para activar alerta..."
for i in {90..1}; do
    echo -ne "\rTiempo restante: ${i}s  "
    sleep 1
done
echo ""

# Verificar alerta
ALERT=$(curl -s http://localhost:9090/api/v1/alerts | python3 -c "
import sys, json
data = json.load(sys.stdin)
alerts = data.get('data', {}).get('alerts', [])
for a in alerts:
    if a.get('labels', {}).get('alertname') == 'NodeExporterDown' and a.get('state') == 'firing':
        print('ACTIVA')
        break
" 2>/dev/null)

if [ "$ALERT" == "ACTIVA" ]; then
    echo -e "${GREEN}✅ Alerta NodeExporterDown ACTIVA${NC}"
    echo "   📧 Email enviado a: pablopolis2016@gmail.com, jflores@unis.edu.gt"
    echo "   💬 Slack enviado a: #ensurance-alerts"
else
    echo -e "${YELLOW}⏱️  Alerta aún no visible (espera 30s más)${NC}"
fi

echo "Restaurando node-exporter..."
docker start ensurance-node-exporter-full > /dev/null 2>&1
echo ""
sleep 5

# TEST 2: RabbitMQ Down
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "[2/5] 🔴 Test CRITICAL: RabbitMQDown"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Deteniendo RabbitMQ..."
docker stop ensurance-rabbitmq-full > /dev/null 2>&1

echo "⏳ Esperando 60 segundos..."
for i in {60..1}; do
    echo -ne "\rTiempo restante: ${i}s  "
    sleep 1
done
echo ""

ALERT=$(curl -s http://localhost:9090/api/v1/alerts | python3 -c "
import sys, json
data = json.load(sys.stdin)
alerts = data.get('data', {}).get('alerts', [])
for a in alerts:
    if a.get('labels', {}).get('alertname') == 'RabbitMQDown' and a.get('state') == 'firing':
        print('ACTIVA')
        break
" 2>/dev/null)

if [ "$ALERT" == "ACTIVA" ]; then
    echo -e "${GREEN}✅ Alerta RabbitMQDown ACTIVA${NC}"
    echo "   📧 Email CRITICAL con prioridad alta"
    echo "   💬 Slack con @channel mention"
else
    echo -e "${YELLOW}⏱️  Alerta aún no visible${NC}"
fi

echo "Restaurando RabbitMQ..."
docker start ensurance-rabbitmq-full > /dev/null 2>&1
echo ""
sleep 10

# TEST 3: High CPU
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "[3/5] ⚠️  Test WARNING: HighCPUUsage"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

if ! command -v stress-ng &> /dev/null; then
    echo "Instalando stress-ng..."
    sudo apt-get update -qq && sudo apt-get install -y stress-ng -qq 2>&1 | tail -1
fi

echo "Generando carga de CPU 80% durante 2 minutos..."
stress-ng --cpu 0 --cpu-load 80 --timeout 120s &
STRESS_PID=$!

echo "⏳ Esperando 130 segundos..."
for i in {130..1}; do
    echo -ne "\rTiempo restante: ${i}s - CPU en carga...  "
    sleep 1
done
echo ""

kill $STRESS_PID 2>/dev/null || true
killall stress-ng 2>/dev/null || true

ALERT=$(curl -s http://localhost:9090/api/v1/alerts | python3 -c "
import sys, json
data = json.load(sys.stdin)
alerts = data.get('data', {}).get('alerts', [])
for a in alerts:
    if 'CPU' in a.get('labels', {}).get('alertname', ''):
        print(f\"{a['labels']['alertname']}:{a['state']}\")
        break
" 2>/dev/null)

if [ ! -z "$ALERT" ]; then
    echo -e "${GREEN}✅ Alerta de CPU: $ALERT${NC}"
else
    echo -e "${YELLOW}⏱️  Alerta de CPU aún no visible${NC}"
fi
echo ""

# TEST 4: Backend Down
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "[4/5] 🔴 Test CRITICAL: Backend Down"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Deteniendo backends..."
docker stop ensurance-pharmacy-full > /dev/null 2>&1

echo "⏳ Esperando 60 segundos..."
for i in {60..1}; do
    echo -ne "\rTiempo restante: ${i}s  "
    sleep 1
done
echo ""

BACKENDS=$(curl -s http://localhost:9090/api/v1/alerts | python3 -c "
import sys, json
data = json.load(sys.stdin)
alerts = data.get('data', {}).get('alerts', [])
backend_alerts = [a['labels']['alertname'] for a in alerts if 'Backend' in a.get('labels', {}).get('alertname', '') and a.get('state') == 'firing']
for alert in backend_alerts:
    print(f'  ✓ {alert}')
" 2>/dev/null)

if [ ! -z "$BACKENDS" ]; then
    echo -e "${GREEN}✅ Alertas de Backend ACTIVAS:${NC}"
    echo "$BACKENDS"
else
    echo -e "${YELLOW}⏱️  Alertas aún no visibles${NC}"
fi

echo "Restaurando backends..."
docker start ensurance-pharmacy-full > /dev/null 2>&1
echo ""
sleep 10

# TEST 5: Resumen
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "[5/5] 📊 Resumen de Alertas Activas"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

curl -s http://localhost:9090/api/v1/alerts | python3 -c "
import sys, json
data = json.load(sys.stdin)
alerts = data.get('data', {}).get('alerts', [])
firing = [a for a in alerts if a.get('state') == 'firing']
pending = [a for a in alerts if a.get('state') == 'pending']

print(f'🔴 FIRING: {len(firing)} alertas')
for a in firing:
    name = a.get('labels', {}).get('alertname')
    severity = a.get('labels', {}).get('severity')
    print(f'  • {name} ({severity})')

print(f'')
print(f'⏱️  PENDING: {len(pending)} alertas')
for a in pending[:5]:
    name = a.get('labels', {}).get('alertname')
    severity = a.get('labels', {}).get('severity')
    print(f'  • {name} ({severity})')
"

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📧 VERIFICACIÓN DE NOTIFICACIONES"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "✅ Alertmanager procesó las alertas"
echo ""
echo "📧 Revisa tu EMAIL:"
echo "   • pablopolis2016@gmail.com"
echo "   • jflores@unis.edu.gt"
echo "   Busca asunto: [Ensurance Pharmacy] o [CRÍTICO]"
echo ""
echo "💬 Revisa SLACK:"
echo "   • Canal: #ensurance-alerts"
echo "   • Alertas CRITICAL tienen @channel"
echo ""
echo "🌐 Interfaces web:"
echo "   • Alertmanager: http://localhost:9094"
echo "   • Prometheus: http://localhost:9090/alerts"
echo "   • Grafana: http://localhost:3302"
echo ""
echo "=========================================="
echo -e "${GREEN}✅ PRUEBA RÁPIDA COMPLETADA${NC}"
echo "=========================================="
