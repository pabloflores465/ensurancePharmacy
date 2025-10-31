#!/bin/bash

# Script para aplicar la nueva alerta HighRAMUsage

echo "=========================================="
echo "🔔 APLICANDO NUEVA ALERTA: HighRAMUsage"
echo "=========================================="
echo ""

echo "📋 Cambios realizados:"
echo "  ✅ Nueva alerta agregada: HighRAMUsage (RAM > 60%)"
echo "  ✅ Posición: #0 (Primera alerta del sistema)"
echo "  ✅ Notificaciones: Gmail + Slack"
echo "  ✅ Scripts actualizados"
echo "  ✅ Documentación actualizada"
echo ""

echo "🔄 Verificando servicios..."

# Verificar que Prometheus está corriendo
if ! docker ps | grep -q "ensurance-prometheus-full"; then
    echo "❌ Prometheus no está corriendo"
    echo "Ejecuta: docker compose -f docker-compose.full.yml up -d prometheus"
    exit 1
fi

echo "✅ Prometheus está corriendo"

# Recargar configuración de Prometheus
echo ""
echo "🔄 Recargando configuración de Prometheus..."
RELOAD_RESULT=$(curl -s -o /dev/null -w "%{http_code}" -X POST http://localhost:9090/-/reload)

if [ "$RELOAD_RESULT" -eq 200 ]; then
    echo "✅ Configuración de Prometheus recargada exitosamente"
else
    echo "⚠️  No se pudo recargar, reiniciando contenedor..."
    docker compose -f docker-compose.full.yml restart prometheus
    sleep 10
    echo "✅ Prometheus reiniciado"
fi

# Verificar que la alerta está cargada
echo ""
echo "🔍 Verificando que la alerta HighRAMUsage está cargada..."
sleep 5

ALERT_COUNT=$(curl -s http://localhost:9090/api/v1/rules | jq '.data.groups[] | select(.name=="system_ram_alerts") | .rules[] | select(.name=="HighRAMUsage")' | wc -l)

if [ "$ALERT_COUNT" -gt 0 ]; then
    echo "✅ Alerta HighRAMUsage encontrada en Prometheus"
    echo ""
    echo "📊 Detalles de la alerta:"
    curl -s http://localhost:9090/api/v1/rules | jq '.data.groups[] | select(.name=="system_ram_alerts") | .rules[] | select(.name=="HighRAMUsage")'
else
    echo "❌ Alerta HighRAMUsage no encontrada"
    echo "Verifica el archivo: monitoring/prometheus/rules/system_alerts.yml"
    exit 1
fi

echo ""
echo "=========================================="
echo "✅ CONFIGURACIÓN APLICADA"
echo "=========================================="
echo ""

echo "📊 Resumen:"
echo "  • Total de alertas: 65 (era 64)"
echo "  • Alertas de sistema: 12 (era 11)"
echo "  • Nueva alerta: HighRAMUsage (#0)"
echo "  • Umbral: RAM > 60%"
echo "  • Tiempo disparo: 1 minuto"
echo "  • Notificaciones: Gmail + Slack"
echo ""

echo "🧪 Para probar la alerta:"
echo ""
echo "Opción 1 - Script Interactivo:"
echo "  ./test-alertas-interactivo.sh"
echo ""
echo "Opción 2 - Script Completo:"
echo "  ./test-todas-las-alertas-completo.sh"
echo ""
echo "Opción 3 - Prueba Manual:"
echo "  stress-ng --vm 1 --vm-bytes 65% --timeout 120s"
echo ""

echo "📧 Notificaciones se enviarán a:"
echo "  • pablopolis2016@gmail.com"
echo "  • jflores@unis.edu.gt"
echo "  • Slack: #ensurance-alerts"
echo ""

echo "📖 Documentación:"
echo "  • ALERTA-HIGHRAMUSAGE.md - Guía completa de la nueva alerta"
echo "  • TODAS_LAS_ALERTAS_COMPLETO.md - Lista actualizada (65 alertas)"
echo "  • GUIA-PRUEBA-CORREOS-ALERTAS.md - Cómo verificar notificaciones"
echo ""

echo "🔍 Monitoreo en tiempo real:"
echo "  • Prometheus: http://localhost:9090"
echo "  • Alertmanager: http://localhost:9094"
echo "  • Grafana: http://localhost:3302"
echo "  • Netdata: http://localhost:19999"
echo ""

echo "=========================================="
echo "¡TODO LISTO!"
echo "=========================================="
