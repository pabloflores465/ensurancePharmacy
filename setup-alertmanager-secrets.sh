#!/bin/bash

# Script para configurar secretos de Alertmanager
# Este script NO se sube a Git

echo "=========================================="
echo "🔐 Configuración de Secretos - Alertmanager"
echo "=========================================="
echo ""

# Verificar si existe el archivo con secretos
if [ ! -f ".env.alertmanager" ]; then
    echo "❌ Error: Archivo .env.alertmanager no encontrado"
    echo ""
    echo "Crea el archivo .env.alertmanager con:"
    echo "SMTP_PASSWORD=tu_token_smtp_aqui"
    exit 1
fi

# Cargar variables
source .env.alertmanager

# Crear archivo alertmanager.yml con secretos reales
echo "📝 Generando monitoring/alertmanager/alertmanager.yml con secretos..."

cat > monitoring/alertmanager/alertmanager.yml << EOF
# Alertmanager Configuration - Ensurance Pharmacy
# ARCHIVO GENERADO AUTOMÁTICAMENTE - NO EDITAR MANUALMENTE
# Edita .env.alertmanager y ejecuta ./setup-alertmanager-secrets.sh

global:
  resolve_timeout: 5m
  smtp_smarthost: '${SMTP_SMARTHOST}'
  smtp_from: '${SMTP_FROM}'
  smtp_auth_username: '${SMTP_USERNAME}'
  smtp_auth_password: '${SMTP_PASSWORD}'
  smtp_require_tls: ${SMTP_REQUIRE_TLS}

route:
  receiver: 'default-notifications'
  group_wait: 10s
  group_interval: 10s
  repeat_interval: 3h
  group_by: ['alertname', 'cluster', 'service']
  
  routes:
    - match:
        severity: critical
      receiver: 'critical-notifications'
      group_wait: 0s
      repeat_interval: 5m
      
    - match:
        severity: warning
      receiver: 'warning-notifications'
      repeat_interval: 1h
      
    - match:
        severity: info
      receiver: 'info-notifications'
      repeat_interval: 6h

receivers:
  - name: 'default-notifications'
    email_configs:
      - to: '${ALERT_EMAIL_TO}'
        headers:
          Subject: '[Ensurance Pharmacy] Alerta de Monitoreo'
        html: |
          <h2>Alerta de Monitoreo - Ensurance Pharmacy</h2>
          <p><strong>Estado:</strong> {{ .Status }}</p>
          <p><strong>Severidad:</strong> {{ .CommonLabels.severity }}</p>
          <p><strong>Alerta:</strong> {{ .CommonLabels.alertname }}</p>
          <p><strong>Descripción:</strong> {{ .CommonAnnotations.description }}</p>
          <p><strong>Resumen:</strong> {{ .CommonAnnotations.summary }}</p>
          <p><strong>Tiempo:</strong> {{ .StartsAt }}</p>
          <hr>
          <p><em>Sistema de Monitoreo Automático - Ensurance Pharmacy</em></p>
  
  - name: 'critical-notifications'
    email_configs:
      - to: '${ALERT_EMAIL_TO}'
        headers:
          Subject: '🔴 [CRÍTICO] Alerta Urgente - Ensurance Pharmacy'
        html: |
          <div style="background-color: #ff0000; color: white; padding: 20px;">
            <h1>⚠️ ALERTA CRÍTICA ⚠️</h1>
          </div>
          <h2>Ensurance Pharmacy - Acción Inmediata Requerida</h2>
          <p><strong>🔴 Severidad:</strong> CRÍTICO</p>
          <p><strong>📛 Alerta:</strong> {{ .CommonLabels.alertname }}</p>
          <p><strong>📝 Descripción:</strong> {{ .CommonAnnotations.description }}</p>
          <p><strong>💡 Resumen:</strong> {{ .CommonAnnotations.summary }}</p>
          <p><strong>🕐 Inicio:</strong> {{ .StartsAt }}</p>
          <p><strong>🎯 Servicio:</strong> {{ .CommonLabels.service }}</p>
          <p><strong>🖥️ Instancia:</strong> {{ .CommonLabels.instance }}</p>
          <hr>
          <p style="color: red;"><strong>⚡ ACCIÓN REQUERIDA INMEDIATAMENTE</strong></p>
          <p><em>Sistema de Monitoreo Crítico - Ensurance Pharmacy</em></p>
  
  - name: 'warning-notifications'
    email_configs:
      - to: '${ALERT_EMAIL_TO}'
        headers:
          Subject: '⚠️ [WARNING] Alerta de Monitoreo - Ensurance Pharmacy'
        html: |
          <div style="background-color: #ffa500; color: white; padding: 20px;">
            <h2>⚠️ Advertencia de Monitoreo</h2>
          </div>
          <h2>Ensurance Pharmacy</h2>
          <p><strong>⚠️ Severidad:</strong> WARNING</p>
          <p><strong>📛 Alerta:</strong> {{ .CommonLabels.alertname }}</p>
          <p><strong>📝 Descripción:</strong> {{ .CommonAnnotations.description }}</p>
          <p><strong>💡 Resumen:</strong> {{ .CommonAnnotations.summary }}</p>
          <p><strong>🕐 Inicio:</strong> {{ .StartsAt }}</p>
          <p><strong>🎯 Servicio:</strong> {{ .CommonLabels.service }}</p>
          <hr>
          <p><em>Sistema de Monitoreo - Ensurance Pharmacy</em></p>
  
  - name: 'info-notifications'
    email_configs:
      - to: '${ALERT_EMAIL_TO}'
        headers:
          Subject: 'ℹ️ [INFO] Notificación - Ensurance Pharmacy'
        html: |
          <h2>Notificación Informativa - Ensurance Pharmacy</h2>
          <p><strong>ℹ️ Severidad:</strong> INFO</p>
          <p><strong>📛 Alerta:</strong> {{ .CommonLabels.alertname }}</p>
          <p><strong>📝 Descripción:</strong> {{ .CommonAnnotations.description }}</p>
          <p><strong>🕐 Tiempo:</strong> {{ .StartsAt }}</p>
          <hr>
          <p><em>Sistema de Monitoreo - Ensurance Pharmacy</em></p>

inhibit_rules:
  - source_match:
      severity: 'critical'
      alertname: 'InstanceDown'
    target_match:
      severity: 'warning'
    equal: ['instance']
  
  - source_match:
      severity: 'critical'
      alertname: 'HighCPUUsage'
    target_match:
      alertname: 'HighCPUUsage'
      severity: 'warning'
    equal: ['instance']
EOF

echo "✅ Archivo generado: monitoring/alertmanager/alertmanager.yml"
echo ""
echo "🔄 Reiniciando Alertmanager..."
docker compose -f docker-compose.full.yml restart alertmanager

echo ""
echo "=========================================="
echo "✅ Configuración Completada"
echo "=========================================="
