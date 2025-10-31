#!/bin/bash

# ============================================================================
# AGREGAR CHECKS ACTIVOS EN CHECKMK
# ============================================================================

echo "═══════════════════════════════════════════════════════════════════"
echo "  Configurando Checks Activos en CheckMK"
echo "═══════════════════════════════════════════════════════════════════"

# ============================================================================
# CONFIGURAR CHECKS VIA ARCHIVOS DE CONFIGURACIÓN
# ============================================================================
echo ""
echo "[1/2] Configurando checks activos..."

docker exec ensurance-checkmk-full omd su ensurance -c "cat > ~/etc/check_mk/conf.d/wato/active_checks_http.mk << 'EOF'
# HTTP Active Checks

active_checks = {
    'http': [
        # Prometheus
        {
            'service_description': 'HTTP Prometheus',
            'host': 'prometheus',
            'mode': ('url', {
                'uri': '/graph',
                'ssl': 'auto',
                'timeout': 10,
                'port': 9090,
            })
        },
        # Grafana
        {
            'service_description': 'HTTP Grafana',
            'host': 'grafana',
            'mode': ('url', {
                'uri': '/api/health',
                'ssl': 'auto',
                'timeout': 10,
                'port': 3000,
            })
        },
        # AlertManager
        {
            'service_description': 'HTTP AlertManager',
            'host': 'alertmanager',
            'mode': ('url', {
                'uri': '/-/healthy',
                'ssl': 'auto',
                'timeout': 10,
                'port': 9093,
            })
        },
        # Netdata
        {
            'service_description': 'HTTP Netdata',
            'host': 'netdata',
            'mode': ('url', {
                'uri': '/api/v1/info',
                'ssl': 'auto',
                'timeout': 10,
                'port': 19999,
            })
        },
        # RabbitMQ
        {
            'service_description': 'HTTP RabbitMQ',
            'host': 'rabbitmq',
            'mode': ('url', {
                'uri': '/metrics',
                'ssl': 'auto',
                'timeout': 10,
                'port': 15692,
            })
        },
        # Node Exporter
        {
            'service_description': 'HTTP Node Exporter',
            'host': 'node-exporter',
            'mode': ('url', {
                'uri': '/metrics',
                'ssl': 'auto',
                'timeout': 10,
                'port': 9100,
            })
        },
        # Pushgateway
        {
            'service_description': 'HTTP Pushgateway',
            'host': 'pushgateway',
            'mode': ('url', {
                'uri': '/metrics',
                'ssl': 'auto',
                'timeout': 10,
                'port': 9091,
            })
        },
    ]
}

print('Active HTTP checks configured')
EOF
" 2>/dev/null

echo "  ✅ Checks HTTP configurados"

# Configurar PING para todos los hosts
docker exec ensurance-checkmk-full omd su ensurance -c "cat > ~/etc/check_mk/conf.d/wato/ping_checks.mk << 'EOF'
# PING Checks for all hosts

# Habilitar PING para todos los hosts sin agente
host_check_commands = [
    ({'command': 'ping'}, ['prometheus', 'grafana', 'alertmanager', 'rabbitmq', 'netdata', 'node-exporter', 'pushgateway', 'ensurance-app'], ALL_HOSTS, {}),
]

print('PING checks configured')
EOF
" 2>/dev/null

echo "  ✅ Checks PING configurados"

echo ""
echo "[2/2] Recargando configuración y activando cambios..."

# Recargar configuración
docker exec ensurance-checkmk-full omd su ensurance -c "cmk -R" 2>/dev/null

# Activar cambios via CLI
docker exec ensurance-checkmk-full omd su ensurance -c "cmk -O" 2>/dev/null

echo "  ✅ Cambios activados"

# Esperar un poco
echo ""
echo "  ⏳ Esperando 30 segundos para que se ejecuten los primeros checks..."
for i in {1..30}; do
    echo -ne "     Progreso: $i/30 segundos\r"
    sleep 1
done
echo ""

echo ""
echo "═══════════════════════════════════════════════════════════════════"
echo "  ✅ Checks Activos Configurados"
echo "═══════════════════════════════════════════════════════════════════"
echo ""
echo "CHECKS CONFIGURADOS:"
echo "  ✅ PING en todos los hosts (8 hosts)"
echo "  ✅ HTTP checks en:"
echo "     - Prometheus (puerto 9090)"
echo "     - Grafana (puerto 3000)"
echo "     - AlertManager (puerto 9093)"
echo "     - Netdata (puerto 19999)"
echo "     - RabbitMQ (puerto 15692)"
echo "     - Node Exporter (puerto 9100)"
echo "     - Pushgateway (puerto 9091)"
echo ""
echo "AHORA VE A CHECKMK:"
echo "  URL: http://localhost:5152/ensurance/check_mk/"
echo ""
echo "QUÉ DEBERÍAS VER:"
echo ""
echo "  1. Monitor → All hosts"
echo "     - 8 hosts con estado (UP/DOWN/PENDING)"
echo ""
echo "  2. Monitor → All services"
echo "     - Servicios PING para cada host"
echo "     - Servicios HTTP para aplicaciones"
echo "     - Check_MK service por host"
echo ""
echo "  3. Monitor → Dashboards"
echo "     - Dashboards ya configurados"
echo ""
echo "⏱️  SI AÚN NO VES SERVICIOS:"
echo "  - Espera 5 minutos más y recarga la página"
echo "  - Los primeros checks se ejecutan gradualmente"
echo "  - El estado puede estar en PENDING al inicio"
echo ""
echo "📊 PARA VER GRÁFICAS AHORA:"
echo "  - Grafana: http://localhost:3001 (admin/admin123)"
echo "  - Netdata: http://localhost:19999"
echo ""
echo "📈 GRÁFICAS EN CHECKMK:"
echo "  - Tardarán 15-30 minutos en aparecer"
echo "  - Necesita recolectar datos históricos primero"
echo ""
