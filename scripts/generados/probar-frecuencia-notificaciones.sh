#!/bin/bash

# Script para confirmar que las frecuencias de notificación funcionan correctamente

echo "=========================================="
echo "⏰ PRUEBA DE FRECUENCIA DE NOTIFICACIONES"
echo "=========================================="
echo "Hora: $(date '+%H:%M:%S')"
echo ""

echo "Este script probará que:"
echo "  🔴 Aplicaciones caídas → cada 2 horas"
echo "  🔴 Otras críticas → cada 5 minutos"
echo "  ⚠️  Warnings → cada 1 hora"
echo ""

read -p "¿Continuar? (y/n): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    exit 1
fi

echo ""
echo "==========================================="
echo "TEST: Alerta Crítica (NO aplicación caída)"
echo "==========================================="
echo ""
echo "Vamos a disparar: K6HighErrorRate"
echo "Esta alerta debería repetirse cada 5 minutos"
echo ""

# Limpiar métricas anteriores
curl -s -X DELETE http://localhost:9093/metrics/job/test-frecuencia > /dev/null 2>&1
sleep 2

# Enviar métrica que dispare alerta CRÍTICA (pero no de aplicación caída)
echo "📤 Enviando métrica: 20% error rate (umbral: 5%)"
echo "k6_http_req_failed 0.20" | curl --data-binary @- http://localhost:9093/metrics/job/test-frecuencia 2>/dev/null
echo "✅ Métrica enviada"
echo ""

echo "⏱️  Esperando 90 segundos para que se dispare..."
sleep 90

# Verificar alerta
ALERT_STATE=$(curl -s http://localhost:9090/api/v1/alerts | jq -r '.data.alerts[] | select(.labels.alertname=="K6HighErrorRate") | .state')

if [ "$ALERT_STATE" == "firing" ]; then
    echo "✅ K6HighErrorRate está FIRING"
    echo ""
    
    echo "⏱️  Esperando 60s para que Alertmanager procese..."
    sleep 60
    
    echo ""
    echo "📊 Verificando en Alertmanager..."
    AM_ALERT=$(curl -s http://localhost:9094/api/v2/alerts 2>/dev/null | jq -r '.[] | select(.labels.alertname=="K6HighErrorRate") | {alertname, state: .status.state, inhibited: .status.inhibitedBy}')
    
    if [ ! -z "$AM_ALERT" ] && [ "$AM_ALERT" != "null" ]; then
        echo "$AM_ALERT" | jq .
        echo ""
        echo "✅ Alerta en Alertmanager"
    else
        echo "⚠️  Alerta no encontrada en Alertmanager aún"
    fi
    
    echo ""
    echo "📋 Logs de notificación:"
    docker compose -f docker-compose.full.yml logs --tail=50 alertmanager 2>&1 | \
        grep -iE "(K6HighErrorRate|notify.*success)" | \
        grep -v "WARN.*version" | \
        tail -10
    
else
    echo "⚠️  Alerta aún no está en FIRING"
    echo "   Estado actual: $ALERT_STATE"
fi

echo ""
echo "==========================================="
echo "📧 PRIMERA NOTIFICACIÓN"
echo "==========================================="
echo ""
echo "Si la alerta está activa, deberías recibir:"
echo "  📧 Email en: rfloresm@unis.edu.gt, jflores@unis.edu.gt"
echo "  💬 Slack en: #ensurance-alerts"
echo "  📛 Asunto: [CRÍTICO] Alerta Urgente - Ensurance Pharmacy"
echo "  📝 Contenido: K6HighErrorRate"
echo ""
echo "⏰ SEGUNDA NOTIFICACIÓN en 5 minutos (01:$(date -d '+5 minutes' '+%M'))"
echo "⏰ TERCERA NOTIFICACIÓN en 10 minutos (01:$(date -d '+10 minutes' '+%M'))"
echo ""

read -p "¿Esperar 5 minutos para confirmar segunda notificación? (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo ""
    echo "⏱️  Esperando 5 minutos..."
    for i in {1..30}; do
        echo -n "   $(( 10 * i ))s..."
        sleep 10
    done
    echo " 300s ✓"
    echo ""
    
    echo "📋 Logs de SEGUNDA notificación:"
    docker compose -f docker-compose.full.yml logs --tail=50 alertmanager 2>&1 | \
        grep -iE "(K6HighErrorRate|notify.*success)" | \
        grep -v "WARN.*version" | \
        tail -10
    echo ""
    
    echo "✅ Si ves 'Notify success' arriba, la SEGUNDA notificación se envió"
    echo "   Confirma en tu email y Slack"
fi

echo ""
echo "==========================================="
echo "📊 RESUMEN"
echo "==========================================="
echo ""

# Contar alertas activas
TOTAL_ALERTS=$(curl -s http://localhost:9090/api/v1/alerts | jq '.data.alerts | length')
K6_ALERTS=$(curl -s http://localhost:9090/api/v1/alerts | jq -r '[.data.alerts[] | select(.labels.alertname | test("K6"))] | length')

echo "Alertas activas totales: $TOTAL_ALERTS"
echo "Alertas K6 activas: $K6_ALERTS"
echo ""

echo "🔍 Alertas de aplicaciones caídas (repeat: 2h):"
curl -s http://localhost:9090/api/v1/alerts | \
    jq -r '.data.alerts[] | select(.labels.alertname | test("BackendDown|FrontendDown")) | "   • \(.labels.alertname)"'
echo ""

echo "🔍 Otras alertas críticas (repeat: 5m):"
curl -s http://localhost:9090/api/v1/alerts | \
    jq -r '.data.alerts[] | select(.labels.severity=="critical" and (.labels.alertname | test("BackendDown|FrontendDown") | not)) | "   • \(.labels.alertname)"'
echo ""

echo "🔍 Alertas warning (repeat: 1h):"
curl -s http://localhost:9090/api/v1/alerts | \
    jq -r '.data.alerts[] | select(.labels.severity=="warning") | "   • \(.labels.alertname)"' | head -5
echo ""

echo "==========================================="
echo "✅ CONFIRMACIÓN"
echo "==========================================="
echo ""
echo "Configuración correcta si:"
echo "  ✅ K6HighErrorRate envió notificación inmediatamente"
echo "  ✅ Segunda notificación llegó después de 5 minutos"
echo "  ✅ Aplicaciones caídas NO enviaron nuevas notificaciones"
echo ""
echo "Próximas notificaciones esperadas:"
echo "  📧 K6HighErrorRate: Cada 5 minutos mientras esté activa"
echo "  📧 Aplicaciones caídas: No hasta ~02:24 UTC"
echo ""

echo "🌐 Interfaces útiles:"
echo "   Prometheus: http://localhost:9090/alerts"
echo "   Alertmanager: http://localhost:9094"
echo ""

echo "📄 Ver documento completo:"
echo "   cat FRECUENCIA_NOTIFICACIONES.md"
echo ""
