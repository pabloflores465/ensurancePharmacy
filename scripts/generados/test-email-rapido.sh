#!/bin/bash

# Script rápido para enviar un email de prueba a través de Alertmanager
# Sin diagnóstico, solo envío directo

ALERTMANAGER_URL="http://localhost:9094"

echo "📧 Enviando email de prueba a través de Alertmanager..."
echo ""

# Verificar que Alertmanager está corriendo
if ! curl -s "$ALERTMANAGER_URL/api/v1/status" > /dev/null 2>&1; then
    echo "❌ Error: Alertmanager no está accesible"
    echo "Ejecuta: docker compose -f docker-compose.full.yml up -d alertmanager"
    exit 1
fi

# Enviar alerta de prueba
cat << 'EOF' | curl -XPOST -d @- "$ALERTMANAGER_URL/api/v1/alerts"
[
  {
    "labels": {
      "alertname": "TestEmailRapido",
      "severity": "critical",
      "service": "test",
      "instance": "localhost"
    },
    "annotations": {
      "summary": "🧪 EMAIL DE PRUEBA - Sistema de Alertas",
      "description": "Este es un correo de prueba del sistema de alertas Ensurance Pharmacy. Si recibes este mensaje, la configuración de email está funcionando correctamente."
    },
    "startsAt": "$(date -u +%Y-%m-%dT%H:%M:%S.000Z)",
    "endsAt": "$(date -u -d '+10 minutes' +%Y-%m-%dT%H:%M:%S.000Z)"
  }
]
EOF

echo ""
echo ""
echo "✅ Alerta enviada a Alertmanager"
echo ""
echo "📬 Verifica tus correos en:"
echo "   - pablopolis2016@gmail.com"
echo "   - jflores@unis.edu.gt"
echo ""
echo "⏱️  El correo debería llegar en 10-30 segundos"
echo "⚠️  Si no llega, revisa la carpeta de SPAM"
echo ""
echo "Para ver logs de envío:"
echo "  docker logs ensurance-alertmanager-full --tail 50 | grep -i smtp"
echo ""
