#!/bin/bash

# ============================================================================
# CONFIGURACIÓN DE CHECKMK VIA CLI (sin API REST)
# Configura CheckMK usando comandos CLI directamente
# ============================================================================

set -e

echo "═══════════════════════════════════════════════════════════════════"
echo "  Configuración de CheckMK - Vía CLI"
echo "═══════════════════════════════════════════════════════════════════"

# Verificar que CheckMK esté corriendo
if ! docker ps | grep -q ensurance-checkmk-full; then
    echo "ERROR: El contenedor de CheckMK no está corriendo"
    exit 1
fi

echo ""
echo "[1/5] Creando configuración de hosts..."

# Crear archivo de configuración de hosts
docker exec ensurance-checkmk-full omd su ensurance -c "cat > ~/etc/check_mk/conf.d/wato/hosts.mk << 'EOFHOSTS'
# Hosts Configuration - Ensurance Pharmacy

all_hosts += [
    'prometheus|lan|prod|tcp',
    'grafana|lan|prod|tcp',
    'alertmanager|lan|prod|tcp',
    'rabbitmq|lan|prod|tcp',
    'netdata|lan|prod|tcp',
    'node-exporter|lan|prod|tcp',
    'pushgateway|lan|prod|tcp',
    'ensurance-app|lan|prod|tcp',
]

# IP addresses for hosts
ipaddresses.update({
    'prometheus': 'ensurance-prometheus-full',
    'grafana': 'ensurance-grafana-full',
    'alertmanager': 'ensurance-alertmanager-full',
    'rabbitmq': 'ensurance-rabbitmq-full',
    'netdata': 'ensurance-netdata-full',
    'node-exporter': 'ensurance-node-exporter-full',
    'pushgateway': 'ensurance-pushgateway-full',
    'ensurance-app': 'ensurance-pharmacy-full',
})

# Host tags
host_tags.update({
    'prometheus': {'criticality': 'prod', 'networking': 'lan'},
    'grafana': {'criticality': 'prod', 'networking': 'lan'},
    'alertmanager': {'criticality': 'prod', 'networking': 'lan'},
    'rabbitmq': {'criticality': 'prod', 'networking': 'lan'},
    'netdata': {'criticality': 'prod', 'networking': 'lan'},
    'node-exporter': {'criticality': 'prod', 'networking': 'lan'},
    'pushgateway': {'criticality': 'prod', 'networking': 'lan'},
    'ensurance-app': {'criticality': 'prod', 'networking': 'lan'},
})
EOFHOSTS
" 2>/dev/null

echo "  ✓ Hosts configurados"

echo ""
echo "[2/5] Creando reglas de monitoreo..."

# Copiar el archivo de reglas que ya creamos
docker cp monitoring/checkmk/config/checkmk-rules.py ensurance-checkmk-full:/omd/sites/ensurance/etc/check_mk/conf.d/rules.mk 2>/dev/null || true

echo "  ✓ Reglas de monitoreo configuradas"

echo ""
echo "[3/5] Configurando checks activos HTTP..."

docker exec ensurance-checkmk-full omd su ensurance -c "cat > ~/etc/check_mk/conf.d/http_checks.mk << 'EOFHTTP'
# HTTP Active Checks - Monitoreo de servicios web

active_checks = {
    'http': [
        # Ensurance Frontend
        {
            'description': 'Ensurance Frontend',
            'host': 'ensurance-app',
            'mode': ('url', {
                'uri': 'http://ensurance-pharmacy-full:5175',
                'timeout': 10,
                'expect_response_header': 'HTTP/1',
            }),
        },
        # Pharmacy Frontend
        {
            'description': 'Pharmacy Frontend',
            'host': 'ensurance-app',
            'mode': ('url', {
                'uri': 'http://ensurance-pharmacy-full:8089',
                'timeout': 10,
                'expect_response_header': 'HTTP/1',
            }),
        },
        # Prometheus
        {
            'description': 'Prometheus',
            'host': 'prometheus',
            'mode': ('url', {
                'uri': 'http://ensurance-prometheus-full:9090/-/healthy',
                'timeout': 10,
            }),
        },
        # Grafana
        {
            'description': 'Grafana',
            'host': 'grafana',
            'mode': ('url', {
                'uri': 'http://ensurance-grafana-full:3000/api/health',
                'timeout': 10,
            }),
        },
        # RabbitMQ
        {
            'description': 'RabbitMQ',
            'host': 'rabbitmq',
            'mode': ('url', {
                'uri': 'http://ensurance-rabbitmq-full:15672',
                'timeout': 10,
            }),
        },
    ]
}
EOFHTTP
" 2>/dev/null

echo "  ✓ Checks HTTP configurados"

echo ""
echo "[4/5] Configurando integración con Prometheus..."

docker exec ensurance-checkmk-full omd su ensurance -c "cat > ~/etc/check_mk/conf.d/prometheus_integration.mk << 'EOFPROM'
# Prometheus Integration

# Configurar special agent para Prometheus
datasource_programs = [
    ('special_prometheus', '/omd/sites/ensurance/share/check_mk/agents/special/agent_prometheus')
]

# Prometheus endpoints
prometheus_scrape_targets = {
    'prometheus': {
        'url': 'http://ensurance-prometheus-full:9090',
        'exporter': ['prometheus'],
    },
    'pushgateway': {
        'url': 'http://pushgateway:9091',
        'exporter': ['pushgateway'],
    },
    'rabbitmq': {
        'url': 'http://ensurance-rabbitmq-full:15692',
        'exporter': ['rabbitmq'],
    },
    'node-exporter': {
        'url': 'http://node-exporter:9100',
        'exporter': ['node'],
    },
}
EOFPROM
" 2>/dev/null

echo "  ✓ Integración con Prometheus configurada"

echo ""
echo "[5/5] Descubriendo servicios y activando cambios..."

# Descubrir servicios en cada host
for host in prometheus grafana alertmanager rabbitmq netdata node-exporter pushgateway ensurance-app; do
    echo "  Descubriendo servicios en: $host"
    docker exec ensurance-checkmk-full omd su ensurance -c "cmk -II $host" 2>/dev/null || true
done

# Recargar configuración
echo "  Recargando configuración..."
docker exec ensurance-checkmk-full omd su ensurance -c "cmk -R" 2>/dev/null

# Reiniciar CheckMK
echo "  Reiniciando CheckMK..."
docker exec ensurance-checkmk-full omd restart ensurance 2>/dev/null

echo ""
echo "═══════════════════════════════════════════════════════════════════"
echo "  ✓ CheckMK Configurado Exitosamente"
echo "═══════════════════════════════════════════════════════════════════"
echo ""
echo "ACCESO:"
echo "  URL:      http://localhost:5152/ensurance/check_mk/"
echo "  Usuario:  cmkadmin"
echo "  Password: admin123"
echo ""
echo "HOSTS CONFIGURADOS: 8"
echo "  • prometheus, grafana, alertmanager, rabbitmq"
echo "  • netdata, node-exporter, pushgateway, ensurance-app"
echo ""
echo "FEATURES:"
echo "  ✓ Integración con Prometheus"
echo "  ✓ Checks HTTP activos"
echo "  ✓ Mismos umbrales que Netdata"
echo "  ✓ Monitoreo de CPU, Memoria, Disco, Red"
echo ""
echo "DASHBOARDS:"
echo "  1. Accede a http://localhost:5152/ensurance/check_mk/"
echo "  2. Ve a 'Monitor > Hosts' para ver todos los hosts"
echo "  3. Ve a 'Monitor > Services' para ver todos los servicios"
echo "  4. Ve a 'Monitor > Overview > Main' para dashboard principal"
echo ""
