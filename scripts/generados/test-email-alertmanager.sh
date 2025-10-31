#!/bin/bash

# Script para verificar que Alertmanager está enviando correos correctamente

set -e

ALERTMANAGER_URL="http://localhost:9094"
PROMETHEUS_URL="http://localhost:9090"

echo "=========================================="
echo "📧 TEST DE ENVÍO DE CORREOS"
echo "=========================================="
echo ""

# Verificar que Alertmanager está corriendo
echo "🔍 Verificando que Alertmanager está activo..."
if ! curl -s "$ALERTMANAGER_URL/api/v1/status" > /dev/null 2>&1; then
    echo "❌ Error: Alertmanager no está accesible en $ALERTMANAGER_URL"
    echo "Ejecuta: docker compose -f docker-compose.full.yml up -d alertmanager"
    exit 1
fi
echo "✅ Alertmanager está activo"
echo ""

# Verificar configuración de email
echo "🔍 Verificando configuración de Alertmanager..."
docker exec ensurance-alertmanager-full cat /etc/alertmanager/alertmanager.yml | grep -A 5 "smtp"
echo ""

# Verificar logs de Alertmanager para ver intentos de envío
echo "📋 Últimos 20 logs de Alertmanager (buscando actividad SMTP):"
echo "======================================================================"
docker logs ensurance-alertmanager-full --tail 20 2>&1 | grep -i -E "(smtp|email|notification|sent|failed|error)" || echo "No hay logs de SMTP recientes"
echo ""

# Enviar una alerta de prueba directamente a Alertmanager
echo "📨 Enviando alerta de prueba a Alertmanager..."
cat << 'EOF' | curl -XPOST -d @- "$ALERTMANAGER_URL/api/v1/alerts"
[
  {
    "labels": {
      "alertname": "TestEmailAlert",
      "severity": "warning",
      "service": "test",
      "instance": "test-instance"
    },
    "annotations": {
      "summary": "🧪 Prueba de envío de correo desde Alertmanager",
      "description": "Este es un email de prueba para verificar que el sistema de alertas está funcionando correctamente. Si recibes este correo, la configuración es correcta."
    },
    "startsAt": "$(date -u +%Y-%m-%dT%H:%M:%S.000Z)",
    "endsAt": "$(date -u -d '+5 minutes' +%Y-%m-%dT%H:%M:%S.000Z)"
  }
]
EOF
echo ""
echo ""

# Esperar un poco y verificar
echo "⏳ Esperando 10 segundos para que se procese la alerta..."
sleep 10
echo ""

# Verificar que la alerta fue recibida
echo "📊 Verificando alertas activas en Alertmanager..."
curl -s "$ALERTMANAGER_URL/api/v1/alerts" | jq '.data[] | {alertname: .labels.alertname, status: .status.state, receivers: .receivers}'
echo ""

# Ver logs más recientes después de enviar la alerta
echo "📋 Logs de Alertmanager después de enviar la prueba (últimos 30):"
echo "======================================================================"
docker logs ensurance-alertmanager-full --tail 30 2>&1
echo ""

echo "=========================================="
echo "📧 DIAGNÓSTICO DE EMAIL"
echo "=========================================="
echo ""

# Verificar la configuración SMTP actual
echo "🔍 Configuración SMTP actual:"
docker exec ensurance-alertmanager-full cat /etc/alertmanager/alertmanager.yml | grep -A 15 "global:" | head -20
echo ""

echo "✉️ Destinatarios configurados:"
docker exec ensurance-alertmanager-full cat /etc/alertmanager/alertmanager.yml | grep "to:" | head -5
echo ""

# Probar conectividad SMTP directamente
echo "🔌 Probando conectividad SMTP a Gmail..."
docker exec ensurance-alertmanager-full sh -c "timeout 5 nc -zv smtp.gmail.com 587 2>&1" || echo "❌ No se puede conectar a smtp.gmail.com:587"
echo ""

echo "=========================================="
echo "📋 INSTRUCCIONES"
echo "=========================================="
echo ""
echo "1. ✅ Si ves 'level=info msg=\"Notify successful\"' en los logs, el email se envió"
echo "2. ❌ Si ves 'level=error' con SMTP, hay un problema de configuración"
echo ""
echo "Posibles problemas:"
echo "- ❌ Contraseña de aplicación incorrecta o expirada"
echo "- ❌ Autenticación de 2 pasos no habilitada en Gmail"
echo "- ❌ Correo bloqueado por políticas de Gmail"
echo "- ❌ Firewall bloqueando puerto 587"
echo ""
echo "Soluciones:"
echo "1. Verifica que la contraseña en alertmanager.yml es una 'App Password' válida"
echo "2. Revisa la carpeta de SPAM en: pablopolis2016@gmail.com y jflores@unis.edu.gt"
echo "3. Habilita 'Acceso de apps menos seguras' si usas contraseña normal"
echo "4. Genera una nueva App Password en: https://myaccount.google.com/apppasswords"
echo ""
echo "Para ver logs en tiempo real:"
echo "  docker logs -f ensurance-alertmanager-full"
echo ""
echo "Para recargar configuración sin reiniciar:"
echo "  docker exec ensurance-alertmanager-full killall -HUP alertmanager"
echo ""
