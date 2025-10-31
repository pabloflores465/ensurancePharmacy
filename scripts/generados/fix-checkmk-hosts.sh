#!/bin/bash

# Script para crear hosts manualmente en CheckMK

echo "═══════════════════════════════════════════════════════════════════"
echo "  Creando Hosts en CheckMK - Método Manual"
echo "═══════════════════════════════════════════════════════════════════"

# Crear archivo main.mk con los hosts
docker exec ensurance-checkmk-full omd su ensurance -c "cat > ~/etc/check_mk/main.mk << 'EOF'
# Hosts configurados manualmente

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

# Configurar que no usen agente (son checks remotos)
datasource_programs = []
for host in all_hosts:
    datasource_programs.append((host, 'ping'))

# Configurar checks TCP para verificar que los servicios estén activos
active_checks = {
    'tcp': [
        # Prometheus
        {
            'description': 'Prometheus Port',
            'port': 9090,
            'hostname': 'prometheus',
        },
        # Grafana
        {
            'description': 'Grafana Port',
            'port': 3000,
            'hostname': 'grafana',
        },
        # AlertManager
        {
            'description': 'AlertManager Port',
            'port': 9093,
            'hostname': 'alertmanager',
        },
        # RabbitMQ Management
        {
            'description': 'RabbitMQ Management',
            'port': 15672,
            'hostname': 'rabbitmq',
        },
        # RabbitMQ AMQP
        {
            'description': 'RabbitMQ AMQP',
            'port': 5672,
            'hostname': 'rabbitmq',
        },
        # Netdata
        {
            'description': 'Netdata Port',
            'port': 19999,
            'hostname': 'netdata',
        },
        # Node Exporter
        {
            'description': 'Node Exporter',
            'port': 9100,
            'hostname': 'node-exporter',
        },
        # Pushgateway
        {
            'description': 'Pushgateway',
            'port': 9091,
            'hostname': 'pushgateway',
        },
        # Ensurance Frontend
        {
            'description': 'Ensurance Frontend',
            'port': 5175,
            'hostname': 'ensurance-app',
        },
        # Pharmacy Frontend
        {
            'description': 'Pharmacy Frontend',
            'port': 8089,
            'hostname': 'ensurance-app',
        },
        # Ensurance Backend
        {
            'description': 'Ensurance Backend',
            'port': 8081,
            'hostname': 'ensurance-app',
        },
        # Pharmacy Backend
        {
            'description': 'Pharmacy Backend',
            'port': 8082,
            'hostname': 'ensurance-app',
        },
    ],
    'http': [
        # Prometheus Health
        {
            'name': 'Prometheus Health',
            'host': ['prometheus'],
            'mode': ('url', {
                'uri': 'http://ensurance-prometheus-full:9090/-/healthy',
                'timeout': 10,
                'onredirect': 'ok',
            }),
        },
        # Grafana Health
        {
            'name': 'Grafana API Health',
            'host': ['grafana'],
            'mode': ('url', {
                'uri': 'http://ensurance-grafana-full:3000/api/health',
                'timeout': 10,
                'onredirect': 'ok',
            }),
        },
        # RabbitMQ Management
        {
            'name': 'RabbitMQ Management UI',
            'host': ['rabbitmq'],
            'mode': ('url', {
                'uri': 'http://ensurance-rabbitmq-full:15672/',
                'timeout': 10,
                'onredirect': 'ok',
            }),
        },
        # Netdata Dashboard
        {
            'name': 'Netdata Dashboard',
            'host': ['netdata'],
            'mode': ('url', {
                'uri': 'http://ensurance-netdata-full:19999/api/v1/info',
                'timeout': 10,
                'onredirect': 'ok',
            }),
        },
    ],
}

# Extra service conf para personalizar los checks
extra_service_conf['check_interval'] = [
    ('1', [], ALL_HOSTS, ['TCP']),  # Check TCP cada minuto
    ('2', [], ALL_HOSTS, ['HTTP']),  # Check HTTP cada 2 minutos
    ('1', [], ALL_HOSTS, ['PING']),  # Check PING cada minuto
]

# Configurar umbrales de alerta
extra_service_conf['max_check_attempts'] = [
    ('3', [], ALL_HOSTS, ALL_SERVICES),  # 3 intentos antes de alerta
]

print("CheckMK main.mk configuration loaded")
EOF
"

echo "✓ Archivo de configuración creado"

# Recargar configuración
echo "Recargando configuración de CheckMK..."
docker exec ensurance-checkmk-full omd su ensurance -c "cmk -R"

# Descubrir servicios para todos los hosts
echo "Descubriendo servicios en hosts..."
docker exec ensurance-checkmk-full omd su ensurance -c "cmk --discover-marked-hosts"

# Listar hosts para verificar
echo ""
echo "Hosts configurados:"
docker exec ensurance-checkmk-full omd su ensurance -c "cmk --list-hosts"

# Listar servicios descubiertos
echo ""
echo "Servicios descubiertos:"
docker exec ensurance-checkmk-full omd su ensurance -c "cmk -II" 2>/dev/null || true

# Activar cambios (bakear configuración)
echo ""
echo "Activando cambios..."
docker exec ensurance-checkmk-full omd su ensurance -c "cmk -O"

# Reiniciar CheckMK para aplicar cambios
echo ""
echo "Reiniciando CheckMK..."
docker exec ensurance-checkmk-full omd restart ensurance

echo ""
echo "═══════════════════════════════════════════════════════════════════"
echo "  ✓ Hosts Creados en CheckMK"
echo "═══════════════════════════════════════════════════════════════════"
echo ""
echo "IMPORTANTE: Espera 2-3 minutos y luego:"
echo "  1. Actualiza la página en el navegador (F5)"
echo "  2. Ve a 'Monitor > All Hosts'"
echo "  3. Deberías ver 8 hosts listados"
echo "  4. Los servicios aparecerán automáticamente después del primer check"
echo ""
echo "Si aún no aparecen, ejecuta manualmente:"
echo "  docker exec ensurance-checkmk-full omd su ensurance -c 'cmk -II'"
echo ""
