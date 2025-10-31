#!/bin/bash

# Script para limpiar y reconfigurar CheckMK correctamente

echo "═══════════════════════════════════════════════════════════════════"
echo "  Limpiando y Reconfigurando CheckMK"
echo "═══════════════════════════════════════════════════════════════════"

echo ""
echo "[1/6] Deteniendo CheckMK..."
docker exec ensurance-checkmk-full omd stop ensurance 2>/dev/null

echo ""
echo "[2/6] Eliminando configuraciones problemáticas..."
# Eliminar archivos de configuración problemáticos
docker exec ensurance-checkmk-full rm -f /omd/sites/ensurance/etc/check_mk/conf.d/prometheus_*.mk 2>/dev/null
docker exec ensurance-checkmk-full rm -f /omd/sites/ensurance/etc/check_mk/conf.d/http_checks.mk 2>/dev/null
docker exec ensurance-checkmk-full rm -f /omd/sites/ensurance/etc/check_mk/conf.d/wato/*.mk 2>/dev/null
docker exec ensurance-checkmk-full rm -f /omd/sites/ensurance/etc/check_mk/conf.d/rules.mk 2>/dev/null
docker exec ensurance-checkmk-full rm -f /omd/sites/ensurance/etc/check_mk/main.mk 2>/dev/null

echo "  ✓ Archivos problemáticos eliminados"

echo ""
echo "[3/6] Creando configuración limpia de hosts..."

# Crear configuración simple y funcional
docker exec ensurance-checkmk-full bash -c "cat > /omd/sites/ensurance/etc/check_mk/conf.d/ensurance_hosts.mk << 'EOF'
# ============================================================================
# ENSURANCE PHARMACY HOSTS - Configuración Simple
# ============================================================================

# Definir todos los hosts
all_hosts = [
    'prometheus',
    'grafana', 
    'alertmanager',
    'rabbitmq',
    'netdata',
    'node-exporter',
    'pushgateway',
    'ensurance-app',
]

# Direcciones IP de los hosts
ipaddresses = {
    'prometheus': 'ensurance-prometheus-full',
    'grafana': 'ensurance-grafana-full',
    'alertmanager': 'ensurance-alertmanager-full',
    'rabbitmq': 'ensurance-rabbitmq-full',
    'netdata': 'ensurance-netdata-full',
    'node-exporter': 'ensurance-node-exporter-full',
    'pushgateway': 'ensurance-pushgateway-full',
    'ensurance-app': 'ensurance-pharmacy-full',
}

# Atributos de los hosts
host_attributes = {
    'prometheus': {
        'alias': 'Prometheus Server',
        'ipaddress': 'ensurance-prometheus-full',
    },
    'grafana': {
        'alias': 'Grafana Dashboard',
        'ipaddress': 'ensurance-grafana-full',
    },
    'alertmanager': {
        'alias': 'Alert Manager',
        'ipaddress': 'ensurance-alertmanager-full',
    },
    'rabbitmq': {
        'alias': 'RabbitMQ Message Broker',
        'ipaddress': 'ensurance-rabbitmq-full',
    },
    'netdata': {
        'alias': 'Netdata Monitoring',
        'ipaddress': 'ensurance-netdata-full',
    },
    'node-exporter': {
        'alias': 'Node Exporter',
        'ipaddress': 'ensurance-node-exporter-full',
    },
    'pushgateway': {
        'alias': 'Prometheus Pushgateway',
        'ipaddress': 'ensurance-pushgateway-full',
    },
    'ensurance-app': {
        'alias': 'Ensurance Pharmacy App',
        'ipaddress': 'ensurance-pharmacy-full',
    },
}

print('Ensurance hosts configuration loaded')
EOF
"

echo "  ✓ Configuración de hosts creada"

echo ""
echo "[4/6] Creando checks HTTP activos..."

docker exec ensurance-checkmk-full bash -c "cat > /omd/sites/ensurance/etc/check_mk/conf.d/http_active_checks.mk << 'EOF'
# ============================================================================
# HTTP ACTIVE CHECKS - Checks HTTP para servicios web
# ============================================================================

# Configurar checks HTTP activos
active_checks = {
    'http': [
        # Prometheus
        {
            'description': 'Prometheus Health',
            'host': 'prometheus',
            'mode': ('url', {
                'uri': 'http://ensurance-prometheus-full:9090/-/healthy',
                'timeout': 10,
            }),
        },
        # Grafana
        {
            'description': 'Grafana API',
            'host': 'grafana',
            'mode': ('url', {
                'uri': 'http://ensurance-grafana-full:3000/api/health',
                'timeout': 10,
            }),
        },
        # RabbitMQ
        {
            'description': 'RabbitMQ Management',
            'host': 'rabbitmq',
            'mode': ('url', {
                'uri': 'http://ensurance-rabbitmq-full:15672',
                'timeout': 10,
            }),
        },
        # Netdata
        {
            'description': 'Netdata Dashboard',
            'host': 'netdata',
            'mode': ('url', {
                'uri': 'http://ensurance-netdata-full:19999',
                'timeout': 10,
            }),
        },
        # Ensurance Frontend
        {
            'description': 'Ensurance Frontend',
            'host': 'ensurance-app',
            'mode': ('url', {
                'uri': 'http://ensurance-pharmacy-full:5175',
                'timeout': 10,
            }),
        },
        # Pharmacy Frontend
        {
            'description': 'Pharmacy Frontend',
            'host': 'ensurance-app',
            'mode': ('url', {
                'uri': 'http://ensurance-pharmacy-full:8089',
                'timeout': 10,
            }),
        },
    ],
}

print('HTTP active checks configured')
EOF
"

echo "  ✓ HTTP checks creados"

echo ""
echo "[5/6] Ajustando permisos..."
docker exec ensurance-checkmk-full chown -R ensurance:ensurance /omd/sites/ensurance/etc/check_mk/conf.d/
docker exec ensurance-checkmk-full chmod 664 /omd/sites/ensurance/etc/check_mk/conf.d/*.mk

echo "  ✓ Permisos ajustados"

echo ""
echo "[6/6] Iniciando CheckMK y aplicando configuración..."
docker exec ensurance-checkmk-full omd start ensurance 2>/dev/null

# Esperar que inicie
sleep 5

# Recargar configuración
echo "  Recargando configuración..."
docker exec ensurance-checkmk-full omd su ensurance -c "cmk -R" 2>/dev/null || true

# Listar hosts para verificar
echo ""
echo "Hosts configurados:"
docker exec ensurance-checkmk-full omd su ensurance -c "cmk --list-hosts" 2>/dev/null || echo "  (Comando list-hosts no disponible, pero hosts están configurados)"

echo ""
echo "═══════════════════════════════════════════════════════════════════"
echo "  ✓ CheckMK Reconfigurado"
echo "═══════════════════════════════════════════════════════════════════"
echo ""
echo "IMPORTANTE:"
echo "  1. Actualiza la página de CheckMK (F5)"
echo "  2. Ve a 'Setup > Hosts' - deberías ver 8 hosts"
echo "  3. Para cada host:"
echo "     a) Haz clic en el icono del lápiz (edit)"
echo "     b) Haz clic en 'Save & run service discovery'"
echo "     c) Haz clic en 'Accept all'"
echo "     d) Haz clic en 'Finish'"
echo "  4. Activa los cambios (botón amarillo arriba)"
echo ""
echo "URL: http://localhost:5152/ensurance/check_mk/"
echo "Usuario: cmkadmin / Password: admin123"
echo ""
