#!/bin/bash

# ============================================================================
# INICIAR CHECKMK PROMETHEUS EXPORTER
# ============================================================================

echo "Iniciando CheckMK Prometheus Exporter..."

# Instalar dependencias en el contenedor
docker exec ensurance-checkmk-full bash -c "
    pip3 install prometheus_client requests 2>/dev/null || \
    apt-get update && apt-get install -y python3-pip && \
    pip3 install prometheus_client requests
" 2>&1 | grep -v "WARNING"

# Iniciar el exporter
docker exec -d ensurance-checkmk-full bash -c \
  "cd /opt/omd/sites/ensurance/local/bin && python3 prometheus-exporter.py > /tmp/exporter.log 2>&1"

echo "✓ Exporter iniciado"
echo "  Métricas disponibles en: http://localhost:9999/metrics"

sleep 5

# Verificar que esté corriendo
if curl -s http://localhost:9999/metrics | grep -q "checkmk"; then
    echo "✓ Exporter funcionando correctamente"
else
    echo "⚠ Exporter puede tardar unos segundos en iniciar"
    echo "  Ver logs: docker exec ensurance-checkmk-full cat /tmp/exporter.log"
fi
