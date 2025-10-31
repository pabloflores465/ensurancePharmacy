#!/bin/bash

# Crear hosts usando WATO (método estándar de CheckMK)

echo "═══════════════════════════════════════════════════════════════════"
echo "  Creando Hosts en CheckMK usando WATO"
echo "═══════════════════════════════════════════════════════════════════"

# Crear directorio WATO si no existe
docker exec ensurance-checkmk-full omd su ensurance -c "mkdir -p ~/etc/check_mk/conf.d/wato"

# Crear archivo .wato con la configuración del folder
docker exec ensurance-checkmk-full omd su ensurance -c "cat > ~/etc/check_mk/conf.d/wato/.wato << 'EOF'
{'attributes': {}, 'num_hosts': 8, 'title': 'Main directory'}
EOF
"

# Crear archivo hosts.mk con la configuración de hosts
docker exec ensurance-checkmk-full omd su ensurance -c "cat > ~/etc/check_mk/conf.d/wato/hosts.mk << 'EOF'
# Written by WATO
# encoding: utf-8

all_hosts += ['prometheus', 'grafana', 'alertmanager', 'rabbitmq', 'netdata', 'node-exporter', 'pushgateway', 'ensurance-app']

host_tags.update({
    'prometheus': {},
    'grafana': {},
    'alertmanager': {},
    'rabbitmq': {},
    'netdata': {},
    'node-exporter': {},
    'pushgateway': {},
    'ensurance-app': {},
})

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

host_attributes.update({
    'prometheus': {'ipaddress': 'ensurance-prometheus-full'},
    'grafana': {'ipaddress': 'ensurance-grafana-full'},
    'alertmanager': {'ipaddress': 'ensurance-alertmanager-full'},
    'rabbitmq': {'ipaddress': 'ensurance-rabbitmq-full'},
    'netdata': {'ipaddress': 'ensurance-netdata-full'},
    'node-exporter': {'ipaddress': 'ensurance-node-exporter-full'},
    'pushgateway': {'ipaddress': 'ensurance-pushgateway-full'},
    'ensurance-app': {'ipaddress': 'ensurance-pharmacy-full'},
})
EOF
"

# Crear checks activos
docker exec ensurance-checkmk-full omd su ensurance -c "cat > ~/etc/check_mk/conf.d/wato/rules.mk << 'EOF'
# Active checks configuration

checkgroup_parameters.setdefault('tcp', [])

# Prometheus
checkgroup_parameters['tcp'] = [
    ({'port': 9090, 'svc_description': 'Prometheus', 'hostname': 'prometheus'}, ['prometheus'], ALL_HOSTS, {}),
    ({'port': 3000, 'svc_description': 'Grafana', 'hostname': 'grafana'}, ['grafana'], ALL_HOSTS, {}),
    ({'port': 9093, 'svc_description': 'AlertManager', 'hostname': 'alertmanager'}, ['alertmanager'], ALL_HOSTS, {}),
    ({'port': 15672, 'svc_description': 'RabbitMQ Mgmt', 'hostname': 'rabbitmq'}, ['rabbitmq'], ALL_HOSTS, {}),
    ({'port': 5672, 'svc_description': 'RabbitMQ AMQP', 'hostname': 'rabbitmq'}, ['rabbitmq'], ALL_HOSTS, {}),
    ({'port': 19999, 'svc_description': 'Netdata', 'hostname': 'netdata'}, ['netdata'], ALL_HOSTS, {}),
    ({'port': 9100, 'svc_description': 'Node Exporter', 'hostname': 'node-exporter'}, ['node-exporter'], ALL_HOSTS, {}),
    ({'port': 9091, 'svc_description': 'Pushgateway', 'hostname': 'pushgateway'}, ['pushgateway'], ALL_HOSTS, {}),
    ({'port': 5175, 'svc_description': 'Ensurance Frontend', 'hostname': 'ensurance-app'}, ['ensurance-app'], ALL_HOSTS, {}),
    ({'port': 8089, 'svc_description': 'Pharmacy Frontend', 'hostname': 'ensurance-app'}, ['ensurance-app'], ALL_HOSTS, {}),
    ({'port': 8081, 'svc_description': 'Ensurance Backend', 'hostname': 'ensurance-app'}, ['ensurance-app'], ALL_HOSTS, {}),
    ({'port': 8082, 'svc_description': 'Pharmacy Backend', 'hostname': 'ensurance-app'}, ['ensurance-app'], ALL_HOSTS, {}),
] + checkgroup_parameters['tcp']
EOF
"

echo "✓ Archivos de configuración WATO creados"

# Verificar que los archivos se crearon
echo ""
echo "Verificando archivos:"
docker exec ensurance-checkmk-full omd su ensurance -c "ls -lh ~/etc/check_mk/conf.d/wato/"

# Recargar configuración
echo ""
echo "Recargando configuración..."
docker exec ensurance-checkmk-full omd su ensurance -c "cmk -R"

# Listar hosts
echo ""
echo "Hosts detectados:"
docker exec ensurance-checkmk-full omd su ensurance -c "cmk --list-hosts"

# Descubrir servicios en todos los hosts
echo ""
echo "Descubriendo servicios..."
for host in prometheus grafana alertmanager rabbitmq netdata node-exporter pushgateway ensurance-app; do
    echo "  Descubriendo: $host"
    docker exec ensurance-checkmk-full omd su ensurance -c "cmk -I $host" 2>/dev/null || echo "    (algunos servicios no detectados)"
done

# Activar todos los cambios pendientes
echo ""
echo "Activando cambios..."
docker exec ensurance-checkmk-full omd su ensurance -c "cmk -O"

# Reiniciar
echo ""
echo "Reiniciando CheckMK..."
docker exec ensurance-checkmk-full omd restart ensurance

echo ""
echo "═══════════════════════════════════════════════════════════════════"
echo "  ✓ Configuración Completada"
echo "═══════════════════════════════════════════════════════════════════"
echo ""
echo "ESPERA 2-3 MINUTOS y luego:"
echo "  1. Actualiza la página web (F5)"
echo "  2. Ve a 'Monitor > All Hosts'"
echo "  3. Deberías ver los 8 hosts"
echo ""
echo "Acceso:"
echo "  http://localhost:5152/ensurance/check_mk/"
echo "  Usuario: cmkadmin / Password: admin123"
echo ""
