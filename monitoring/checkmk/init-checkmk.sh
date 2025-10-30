#!/bin/bash

# ============================================================================
# SCRIPT DE INICIALIZACIÓN DE CHECKMK
# Configura CheckMK para monitorear el mismo stack que Netdata
# ============================================================================

set -e

echo "═══════════════════════════════════════════════════════════════════"
echo "  Inicializando CheckMK - Ensurance Pharmacy"
echo "═══════════════════════════════════════════════════════════════════"

# Esperar a que CheckMK esté listo
echo "Esperando a que CheckMK esté listo..."
sleep 30

# Variables
CHECKMK_URL="http://localhost:5152/ensurance/check_mk/api/1.0"
USERNAME="cmkadmin"
PASSWORD="admin123"

# Crear usuario de automatización
echo "Creando usuario de automatización..."
docker exec ensurance-checkmk-full omd su ensurance -c "htpasswd -b ~/etc/htpasswd automation automation_secret" 2>/dev/null || true

# Agregar hosts mediante API
echo "Agregando hosts al monitoreo..."

# Host: Prometheus
curl -X POST "$CHECKMK_URL/domain-types/host_config/collections/all" \
  -u "automation:automation_secret" \
  -H "Content-Type: application/json" \
  -d '{
    "folder": "/",
    "host_name": "prometheus",
    "attributes": {
      "ipaddress": "ensurance-prometheus-full",
      "tag_agent": "no-agent",
      "tag_criticality": "prod",
      "tag_networking": "lan"
    }
  }' 2>/dev/null || echo "  Host prometheus ya existe"

# Host: Grafana
curl -X POST "$CHECKMK_URL/domain-types/host_config/collections/all" \
  -u "automation:automation_secret" \
  -H "Content-Type: application/json" \
  -d '{
    "folder": "/",
    "host_name": "grafana",
    "attributes": {
      "ipaddress": "ensurance-grafana-full",
      "tag_agent": "no-agent",
      "tag_criticality": "prod",
      "tag_networking": "lan"
    }
  }' 2>/dev/null || echo "  Host grafana ya existe"

# Host: AlertManager
curl -X POST "$CHECKMK_URL/domain-types/host_config/collections/all" \
  -u "automation:automation_secret" \
  -H "Content-Type: application/json" \
  -d '{
    "folder": "/",
    "host_name": "alertmanager",
    "attributes": {
      "ipaddress": "ensurance-alertmanager-full",
      "tag_agent": "no-agent",
      "tag_criticality": "prod",
      "tag_networking": "lan"
    }
  }' 2>/dev/null || echo "  Host alertmanager ya existe"

# Host: RabbitMQ
curl -X POST "$CHECKMK_URL/domain-types/host_config/collections/all" \
  -u "automation:automation_secret" \
  -H "Content-Type: application/json" \
  -d '{
    "folder": "/",
    "host_name": "rabbitmq",
    "attributes": {
      "ipaddress": "ensurance-rabbitmq-full",
      "tag_agent": "no-agent",
      "tag_criticality": "prod",
      "tag_networking": "lan"
    }
  }' 2>/dev/null || echo "  Host rabbitmq ya existe"

# Host: Netdata
curl -X POST "$CHECKMK_URL/domain-types/host_config/collections/all" \
  -u "automation:automation_secret" \
  -H "Content-Type: application/json" \
  -d '{
    "folder": "/",
    "host_name": "netdata",
    "attributes": {
      "ipaddress": "ensurance-netdata-full",
      "tag_agent": "no-agent",
      "tag_criticality": "prod",
      "tag_networking": "lan"
    }
  }' 2>/dev/null || echo "  Host netdata ya existe"

echo ""
echo "Descubriendo servicios en los hosts..."
docker exec ensurance-checkmk-full omd su ensurance -c "cmk -I prometheus" 2>/dev/null || true
docker exec ensurance-checkmk-full omd su ensurance -c "cmk -I grafana" 2>/dev/null || true
docker exec ensurance-checkmk-full omd su ensurance -c "cmk -I alertmanager" 2>/dev/null || true
docker exec ensurance-checkmk-full omd su ensurance -c "cmk -I rabbitmq" 2>/dev/null || true
docker exec ensurance-checkmk-full omd su ensurance -c "cmk -I netdata" 2>/dev/null || true

echo ""
echo "Recargando configuración..."
docker exec ensurance-checkmk-full omd su ensurance -c "cmk -R" 2>/dev/null || true

echo ""
echo "Activando cambios..."
curl -X POST "$CHECKMK_URL/domain-types/activation_run/actions/activate-changes/invoke" \
  -u "automation:automation_secret" \
  -H "Content-Type: application/json" \
  -d '{"sites": ["ensurance"], "force_foreign_changes": true}' 2>/dev/null || true

echo ""
echo "Iniciando exportador de Prometheus..."
docker exec -d ensurance-checkmk-full omd su ensurance -c "python3 /opt/omd/sites/ensurance/local/bin/prometheus-exporter.py" 2>/dev/null || true

echo ""
echo "═══════════════════════════════════════════════════════════════════"
echo "  ✓ CheckMK inicializado correctamente"
echo "═══════════════════════════════════════════════════════════════════"
echo ""
echo "Acceso a CheckMK:"
echo "  URL: http://localhost:5152/ensurance/check_mk/"
echo "  Usuario: cmkadmin"
echo "  Password: admin123"
echo ""
echo "Prometheus Exporter:"
echo "  URL: http://localhost:9999/metrics"
echo ""
echo "Hosts monitoreados:"
echo "  • prometheus (ensurance-prometheus-full)"
echo "  • grafana (ensurance-grafana-full)"
echo "  • alertmanager (ensurance-alertmanager-full)"
echo "  • rabbitmq (ensurance-rabbitmq-full)"
echo "  • netdata (ensurance-netdata-full)"
echo ""
