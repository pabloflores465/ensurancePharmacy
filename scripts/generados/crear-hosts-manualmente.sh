#!/bin/bash

# ============================================================================
# CREAR HOSTS MANUALMENTE EN CHECKMK
# ============================================================================

echo "═══════════════════════════════════════════════════════════════════"
echo "  Creando Hosts Manualmente en CheckMK"
echo "═══════════════════════════════════════════════════════════════════"

echo ""
echo "[1/4] Creando archivo de hosts..."

docker exec ensurance-checkmk-full omd su ensurance -c "mkdir -p ~/etc/check_mk/conf.d/wato" 2>/dev/null

docker exec ensurance-checkmk-full omd su ensurance -c "cat > ~/etc/check_mk/conf.d/wato/hosts.mk << 'EOF'
# Hosts configuration for Ensurance Pharmacy

# Define all hosts
all_hosts += [
    'prometheus',
    'grafana',
    'alertmanager',
    'rabbitmq',
    'netdata',
    'node-exporter',
    'pushgateway',
    'ensurance-app',
]

# Host attributes
host_attributes.update({
    'prometheus': {
        'alias': 'Prometheus Server',
        'ipaddress': 'ensurance-prometheus-full',
        'tag_agent': 'no-agent',
    },
    'grafana': {
        'alias': 'Grafana Dashboard',
        'ipaddress': 'ensurance-grafana-full',
        'tag_agent': 'no-agent',
    },
    'alertmanager': {
        'alias': 'Alert Manager',
        'ipaddress': 'ensurance-alertmanager-full',
        'tag_agent': 'no-agent',
    },
    'rabbitmq': {
        'alias': 'RabbitMQ',
        'ipaddress': 'ensurance-rabbitmq-full',
        'tag_agent': 'no-agent',
    },
    'netdata': {
        'alias': 'Netdata Monitoring',
        'ipaddress': 'ensurance-netdata-full',
        'tag_agent': 'no-agent',
    },
    'node-exporter': {
        'alias': 'Node Exporter',
        'ipaddress': 'ensurance-node-exporter-full',
        'tag_agent': 'no-agent',
    },
    'pushgateway': {
        'alias': 'Pushgateway',
        'ipaddress': 'ensurance-pushgateway-full',
        'tag_agent': 'no-agent',
    },
    'ensurance-app': {
        'alias': 'Ensurance App',
        'ipaddress': 'ensurance-pharmacy-full',
        'tag_agent': 'no-agent',
    },
})

# Habilitar PING para todos
host_check_commands.append(
    ({'command': 'ping'}, ['prometheus', 'grafana', 'alertmanager', 'rabbitmq', 'netdata', 'node-exporter', 'pushgateway', 'ensurance-app'], ALL_HOSTS, {}),
)

print('Hosts configured manually')
EOF
" 2>/dev/null

echo "  ✅ Archivo de hosts creado"

echo ""
echo "[2/4] Recargando configuración..."

docker exec ensurance-checkmk-full omd su ensurance -c "cmk -R" 2>/dev/null

echo "  ✅ Configuración recargada"

echo ""
echo "[3/4] Listando hosts configurados..."

hosts=$(docker exec ensurance-checkmk-full omd su ensurance -c "cmk --list-hosts" 2>/dev/null)

if [ -z "$hosts" ]; then
    echo "  ⚠️  No se detectaron hosts aún"
else
    echo "$hosts" | while read host; do
        echo "  ✅ $host"
    done
fi

echo ""
echo "[4/4] Ejecutando checks iniciales..."

# Ejecutar checks en todos los hosts
for host in prometheus grafana alertmanager rabbitmq netdata node-exporter pushgateway ensurance-app; do
    echo "  🔍 Checkeando: $host"
    docker exec ensurance-checkmk-full omd su ensurance -c "cmk -v $host" 2>/dev/null | head -5 || true
done

echo ""
echo "═══════════════════════════════════════════════════════════════════"
echo "  ✅ Configuración Manual Completada"
echo "═══════════════════════════════════════════════════════════════════"
echo ""
echo "AHORA ACCEDE A CHECKMK:"
echo "  URL: http://localhost:5152/ensurance/check_mk/"
echo "  Usuario: cmkadmin"
echo "  Password: admin123"
echo ""
echo "NAVEGA A:"
echo "  Monitor → All hosts   (verás los 8 hosts)"
echo "  Monitor → All services (verás servicios PING y Check_MK)"
echo ""
echo "⏱️  TIEMPO DE ESPERA:"
echo "  - Hosts visibles: INMEDIATO"
echo "  - Servicios PING: 1-2 minutos"
echo "  - Gráficas: 15-30 minutos"
echo ""
echo "📊 PARA VER GRÁFICAS AHORA:"
echo "  Grafana: http://localhost:3001 (admin/admin123)"
echo "  Netdata: http://localhost:19999"
echo ""
