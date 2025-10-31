#!/bin/bash

# Script para configurar Slack Webhook en Alertmanager
# Uso: ./configure-slack-webhook.sh "https://hooks.slack.com/services/YOUR/WEBHOOK/URL"

echo "=========================================="
echo "🔧 Configuración de Slack Webhook"
echo "=========================================="
echo ""

# Verificar argumento
if [ -z "$1" ]; then
    echo "❌ Error: Debes proporcionar la URL del webhook de Slack"
    echo ""
    echo "Uso: ./configure-slack-webhook.sh \"https://hooks.slack.com/services/YOUR/WEBHOOK/URL\""
    echo ""
    echo "📚 Instrucciones para obtener el webhook:"
    echo "1. Ve a https://api.slack.com/apps"
    echo "2. Crea una nueva app o usa una existente"
    echo "3. Activa 'Incoming Webhooks'"
    echo "4. Añade webhook al canal #ensurance-alerts"
    echo "5. Copia la URL del webhook"
    echo ""
    exit 1
fi

WEBHOOK_URL="$1"
CONFIG_FILE="monitoring/alertmanager/alertmanager.yml"

# Verificar que el archivo de configuración existe
if [ ! -f "$CONFIG_FILE" ]; then
    echo "❌ Error: No se encuentra el archivo $CONFIG_FILE"
    exit 1
fi

# Backup del archivo original
cp "$CONFIG_FILE" "$CONFIG_FILE.backup.$(date +%Y%m%d_%H%M%S)"
echo "✅ Backup creado: $CONFIG_FILE.backup.$(date +%Y%m%d_%H%M%S)"

# Reemplazar el placeholder con la URL real
sed -i "s|api_url: 'SLACK_WEBHOOK_URL_AQUI'|api_url: '$WEBHOOK_URL'|g" "$CONFIG_FILE"

# Verificar que se hizo el reemplazo
if grep -q "SLACK_WEBHOOK_URL_AQUI" "$CONFIG_FILE"; then
    echo "❌ Error: No se pudo actualizar el webhook URL"
    echo "Revisa manualmente el archivo $CONFIG_FILE"
    exit 1
fi

echo "✅ Webhook URL configurada exitosamente"
echo ""

# Verificar si Alertmanager está corriendo
if docker ps | grep -q "ensurance-alertmanager-full"; then
    echo "🔄 Reiniciando Alertmanager para aplicar cambios..."
    docker compose -f docker-compose.full.yml restart alertmanager
    
    # Esperar a que Alertmanager inicie
    sleep 5
    
    if docker ps | grep -q "ensurance-alertmanager-full"; then
        echo "✅ Alertmanager reiniciado exitosamente"
    else
        echo "❌ Error: Alertmanager no pudo reiniciar"
        echo "Ejecuta manualmente: docker compose -f docker-compose.full.yml up -d alertmanager"
    fi
else
    echo "ℹ️ Alertmanager no está corriendo"
    echo "Levántalo con: docker compose -f docker-compose.full.yml up -d alertmanager"
fi

echo ""
echo "=========================================="
echo "✅ Configuración Completada"
echo "=========================================="
echo ""
echo "📋 Siguiente paso: Probar las notificaciones"
echo ""
echo "Prueba 1 - Email:"
echo "  docker stop ensurance-node-exporter-full"
echo "  # Espera 2 minutos, deberías recibir email"
echo "  docker start ensurance-node-exporter-full"
echo ""
echo "Prueba 2 - Slack (requiere webhook configurado):"
echo "  docker stop ensurance-rabbitmq-full"
echo "  # Espera 1 minuto, deberías ver alerta en #ensurance-alerts"
echo "  docker start ensurance-rabbitmq-full"
echo ""
echo "📊 Interfaces:"
echo "  • Alertmanager: http://localhost:9093"
echo "  • Prometheus Alerts: http://localhost:9090/alerts"
echo ""
