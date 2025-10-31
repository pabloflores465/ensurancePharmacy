#!/bin/bash

# Test directo para confirmar que los emails funcionan
# Usando alertas que SÍ disparan (HighMemoryUsage)

echo "=========================================="
echo "🧪 TEST DIRECTO DE EMAILS"
echo "=========================================="
echo "Hora: $(date '+%H:%M:%S')"
echo ""

echo "📊 Estrategia:"
echo "1. Usar stress de memoria (funciona inmediatamente)"
echo "2. La alerta pasa a FIRING rápidamente"
echo "3. Alertmanager envía email automáticamente"
echo ""

# Verificar stress-ng
if ! command -v stress-ng &> /dev/null; then
    echo "Instalando stress-ng..."
    sudo apt-get install -y stress-ng -qq
fi

# Calcular memoria
TOTAL_MEM=$(free -m | awk '/^Mem:/{print $2}')
TARGET_MEM=$(( TOTAL_MEM * 85 / 100 ))

echo "💾 Iniciando stress de memoria..."
echo "   Total: ${TOTAL_MEM}MB"
echo "   Target: ${TARGET_MEM}MB (85%)"
echo ""

# Iniciar stress por 3 minutos
stress-ng --vm 3 --vm-bytes ${TARGET_MEM}M --timeout 180s > /dev/null 2>&1 &
STRESS_PID=$!

echo "✅ Stress iniciado (PID: $STRESS_PID)"
echo ""

echo "⏱️  Esperando 30 segundos..."
sleep 30

# Verificar memoria
CURRENT_MEM=$(free | awk '/^Mem:/ {printf "%.0f", $3/$2 * 100}')
echo "   Memoria actual: ${CURRENT_MEM}%"
echo ""

echo "⏱️  Esperando 90 segundos más (total 2 min)..."
sleep 90

echo ""
echo "📊 Verificando alertas de memoria en Prometheus..."
curl -s http://localhost:9090/api/v1/alerts | jq -r '.data.alerts[] | select(.labels.alertname | test("Memory")) | "   \(.state | ascii_upcase): \(.labels.alertname) (Severity: \(.labels.severity))"'
echo ""

echo "⏱️  Esperando 60s para que Alertmanager procese y envíe..."
sleep 60

echo ""
echo "📋 Últimos logs de Alertmanager (email)..."
docker compose -f docker-compose.full.yml logs --tail=50 alertmanager 2>&1 | \
    grep -iE "(memory|email.*success|notify.*success)" | \
    grep -v "WARN.*version" | \
    tail -10
echo ""

echo "=========================================="
echo "📧 VERIFICA TUS EMAILS AHORA"
echo "=========================================="
echo ""
echo "Destinatarios:"
echo "  • rfloresm@unis.edu.gt"
echo "  • jflores@unis.edu.gt"
echo ""
echo "Asunto esperado:"
echo "  [WARNING] Alerta de Monitoreo - Ensurance Pharmacy"
echo "  o"
echo "  [CRÍTICO] Alerta Urgente - Ensurance Pharmacy"
echo ""
echo "Contenido: HighMemoryUsage o CriticalMemoryUsage"
echo ""
echo "⚠️  REVISAR CARPETA SPAM"
echo ""

# Detener stress
if ps -p $STRESS_PID > /dev/null 2>&1; then
    kill $STRESS_PID 2>/dev/null
    echo "✅ Stress detenido"
fi

echo ""
echo "=========================================="
echo "📊 ESTADO FINAL"
echo "=========================================="
echo ""

# Verificar en Alertmanager
AM_COUNT=$(curl -s http://localhost:9094/api/v2/alerts 2>/dev/null | jq '[.[] | select(.labels.alertname | test("Memory"))] | length')
echo "📬 Alertas de memoria en Alertmanager: $AM_COUNT"
echo ""

if [ "$AM_COUNT" -gt 0 ]; then
    echo "Detalles:"
    curl -s http://localhost:9094/api/v2/alerts 2>/dev/null | \
        jq -r '.[] | select(.labels.alertname | test("Memory")) | "   \(.labels.alertname) [\(.status.state)] -> \(.receivers[0].name)"'
    echo ""
fi

echo "🌐 Interfaces:"
echo "   Prometheus: http://localhost:9090/alerts"
echo "   Alertmanager: http://localhost:9094"
echo ""

echo "✅ Si ves 'Notify success' en los logs arriba,"
echo "   el email FUE ENVIADO correctamente."
echo ""
echo "Si no llegó a tu inbox:"
echo "  1. Revisar SPAM"
echo "  2. Buscar: from:pablopolis2016@gmail.com"  
echo "  3. Esperar 2-3 minutos más"
echo ""
