#!/bin/bash

# ============================================================================
# INTEGRACIÓN DE CHECKMK CON PROMETHEUS
# Obtiene las mismas métricas que Netdata desde Prometheus
# ============================================================================

set -e

echo "═══════════════════════════════════════════════════════════════════"
echo "  Integrando CheckMK con Prometheus"
echo "═══════════════════════════════════════════════════════════════════"

URL="http://localhost:5152/ensurance/check_mk/api/1.0"
USER="cmkadmin"
PASS="admin123"

# ============================================================================
# PASO 1: Configurar Special Agent Prometheus
# ============================================================================
echo ""
echo "[1/5] Configurando Special Agent Prometheus..."

# Crear configuración del Special Agent
docker exec ensurance-checkmk-full omd su ensurance -c "cat > ~/etc/check_mk/conf.d/prometheus_integration.mk << 'EOF'
# ============================================================================
# PROMETHEUS INTEGRATION CONFIGURATION
# ============================================================================

# Configurar Special Agent Prometheus
datasource_programs = []
for host in ['prometheus', 'node-exporter', 'rabbitmq', 'netdata', 'ensurance-app']:
    datasource_programs.append((host, 'special_prometheus'))

# Configurar endpoints de Prometheus para cada host
special_agent_prometheus = {
    # Prometheus Server - métricas del servidor y targets
    'prometheus': {
        'url': 'http://ensurance-prometheus-full:9090/metrics',
        'protocol': 'http',
        'timeout': 30,
        'exporter': ['prometheus'],
        'job': 'prometheus',
        'instance': 'prometheus',
    },
    
    # Node Exporter - métricas del sistema (CPU, Memoria, Disco, Red)
    'node-exporter': {
        'url': 'http://ensurance-node-exporter-full:9100/metrics',
        'protocol': 'http',
        'timeout': 30,
        'exporter': ['node'],
        'job': 'node-exporter',
        'instance': 'ensurance-node-exporter-full',
    },
    
    # RabbitMQ - métricas del message broker
    'rabbitmq': {
        'url': 'http://ensurance-rabbitmq-full:15692/metrics',
        'protocol': 'http',
        'timeout': 30,
        'exporter': ['rabbitmq'],
        'job': 'rabbitmq',
        'instance': 'ensurance-rabbitmq-full',
    },
    
    # Netdata - métricas de monitoreo (equivalente a Netdata web)
    'netdata': {
        'url': 'http://ensurance-netdata-full:19999/api/v1/allmetrics?format=prometheus',
        'protocol': 'http',
        'timeout': 30,
        'exporter': ['netdata'],
        'job': 'netdata',
        'instance': 'ensurance-netdata-full',
    },
    
    # Ensurance App - métricas de la aplicación (backends y frontends)
    'ensurance-app': {
        'url': 'http://ensurance-pharmacy-full:9464/metrics',
        'protocol': 'http',
        'timeout': 30,
        'exporter': ['application'],
        'job': 'ensurance-app',
        'instance': 'ensurance-pharmacy-full',
    },
}

print("Prometheus integration configured")
EOF
" 2>/dev/null

echo "  ✓ Special Agent Prometheus configurado"

# ============================================================================
# PASO 2: Configurar reglas de monitoreo basadas en métricas de Prometheus
# ============================================================================
echo ""
echo "[2/5] Configurando reglas de monitoreo..."

docker exec ensurance-checkmk-full omd su ensurance -c "cat > ~/etc/check_mk/conf.d/prometheus_rules.mk << 'EOF'
# ============================================================================
# PROMETHEUS MONITORING RULES - Equivalentes a Netdata
# ============================================================================

# CPU Usage Rules (equivalente a netdata_high_cpu_usage)
checkgroup_parameters.setdefault('prometheus_cpu_utilization', [])
checkgroup_parameters['prometheus_cpu_utilization'] = [
    ({
        'metric': 'node_cpu_seconds_total',
        'levels': (70.0, 90.0),  # WARNING: 70%, CRITICAL: 90%
        'period': 300,  # 5 minutos
    }, ['node-exporter'], ALL_HOSTS, {}),
]

# Memory Usage Rules (equivalente a netdata_high_memory_usage)
checkgroup_parameters.setdefault('prometheus_memory_usage', [])
checkgroup_parameters['prometheus_memory_usage'] = [
    ({
        'metric': 'node_memory_MemAvailable_bytes',
        'levels': (80.0, 95.0),  # WARNING: 80%, CRITICAL: 95%
        'period': 300,
    }, ['node-exporter'], ALL_HOSTS, {}),
]

# Disk Space Rules (equivalente a netdata_high_disk_usage)
checkgroup_parameters.setdefault('prometheus_disk_space', [])
checkgroup_parameters['prometheus_disk_space'] = [
    ({
        'metric': 'node_filesystem_avail_bytes',
        'levels': (75.0, 90.0),  # WARNING: 75%, CRITICAL: 90%
        'period': 600,  # 10 minutos
    }, ['node-exporter'], ALL_HOSTS, {}),
]

# Network Traffic Rules (equivalente a netdata_high_network_receive)
checkgroup_parameters.setdefault('prometheus_network_traffic', [])
checkgroup_parameters['prometheus_network_traffic'] = [
    ({
        'metric': 'node_network_receive_bytes_total',
        'levels': (104857600, 209715200),  # WARNING: 100MB/s, CRITICAL: 200MB/s
        'period': 300,
    }, ['node-exporter'], ALL_HOSTS, {}),
]

# Disk I/O Rules (equivalente a netdata_high_disk_io)
checkgroup_parameters.setdefault('prometheus_disk_io', [])
checkgroup_parameters['prometheus_disk_io'] = [
    ({
        'metric': 'node_disk_io_time_seconds_total',
        'levels': (50000, 100000),  # WARNING: 50MB/s, CRITICAL: 100MB/s
        'period': 300,
    }, ['node-exporter'], ALL_HOSTS, {}),
]

# RabbitMQ Queue Size Rules
checkgroup_parameters.setdefault('prometheus_rabbitmq_queues', [])
checkgroup_parameters['prometheus_rabbitmq_queues'] = [
    ({
        'metric': 'rabbitmq_queue_messages',
        'levels': (1000, 5000),  # WARNING: 1000, CRITICAL: 5000 mensajes
        'period': 300,
    }, ['rabbitmq'], ALL_HOSTS, {}),
]

# Application Response Time Rules (equivalente a netdata_slow_web_response)
checkgroup_parameters.setdefault('prometheus_response_time', [])
checkgroup_parameters['prometheus_response_time'] = [
    ({
        'metric': 'http_request_duration_seconds',
        'levels': (1.0, 3.0),  # WARNING: 1s, CRITICAL: 3s
        'period': 300,
    }, ['ensurance-app'], ALL_HOSTS, {}),
]

# Error Rate Rules (equivalente a netdata_high_http_5xx_errors)
checkgroup_parameters.setdefault('prometheus_error_rate', [])
checkgroup_parameters['prometheus_error_rate'] = [
    ({
        'metric': 'http_requests_total{status=~\"5..\"}',
        'levels': (5.0, 20.0),  # WARNING: 5%, CRITICAL: 20%
        'period': 300,
    }, ['ensurance-app'], ALL_HOSTS, {}),
]

print("Prometheus monitoring rules configured")
EOF
" 2>/dev/null

echo "  ✓ Reglas de monitoreo configuradas"

# ============================================================================
# PASO 3: Configurar servicios de Prometheus
# ============================================================================
echo ""
echo "[3/5] Configurando servicios Prometheus..."

docker exec ensurance-checkmk-full omd su ensurance -c "cat > ~/etc/check_mk/conf.d/prometheus_services.mk << 'EOF'
# ============================================================================
# PROMETHEUS SERVICES CONFIGURATION
# ============================================================================

# Configurar check groups para métricas de Prometheus
check_groups = {
    'prometheus_cpu_utilization': 'CPU Utilization (Prometheus)',
    'prometheus_memory_usage': 'Memory Usage (Prometheus)',
    'prometheus_disk_space': 'Disk Space (Prometheus)',
    'prometheus_network_traffic': 'Network Traffic (Prometheus)',
    'prometheus_disk_io': 'Disk I/O (Prometheus)',
    'prometheus_rabbitmq_queues': 'RabbitMQ Queues (Prometheus)',
    'prometheus_response_time': 'Response Time (Prometheus)',
    'prometheus_error_rate': 'Error Rate (Prometheus)',
}

# Configurar descripciones de servicios
service_descriptions = {
    'prometheus_cpu_utilization': 'CPU Usage',
    'prometheus_memory_usage': 'Memory Usage',
    'prometheus_disk_space': 'Disk Space',
    'prometheus_network_traffic': 'Network Traffic',
    'prometheus_disk_io': 'Disk I/O',
    'prometheus_rabbitmq_queues': 'Queue Messages',
    'prometheus_response_time': 'HTTP Response Time',
    'prometheus_error_rate': 'HTTP Error Rate',
}

print("Prometheus services configured")
EOF
" 2>/dev/null

echo "  ✓ Servicios Prometheus configurados"

# ============================================================================
# PASO 4: Actualizar configuración de hosts para usar Prometheus
# ============================================================================
echo ""
echo "[4/5] Actualizando configuración de hosts..."

# Actualizar el archivo de hosts para incluir la configuración de Prometheus
docker exec ensurance-checkmk-full omd su ensurance -c "cat > ~/etc/check_mk/conf.d/ensurance_hosts.mk << 'EOF'
# Ensurance Pharmacy Hosts Configuration with Prometheus Integration

all_hosts = ['prometheus', 'grafana', 'alertmanager', 'rabbitmq', 'netdata', 'node-exporter', 'pushgateway', 'ensurance-app']

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

host_attributes = {
    'prometheus': {
        'alias': 'Prometheus Server', 
        'ipaddress': 'ensurance-prometheus-full',
        'datasource_programs': [('special_prometheus', '/omd/sites/ensurance/share/check_mk/agents/special/agent_prometheus')]
    },
    'grafana': {'alias': 'Grafana Dashboard', 'ipaddress': 'ensurance-grafana-full'},
    'alertmanager': {'alias': 'Alert Manager', 'ipaddress': 'ensurance-alertmanager-full'},
    'rabbitmq': {
        'alias': 'RabbitMQ', 
        'ipaddress': 'ensurance-rabbitmq-full',
        'datasource_programs': [('special_prometheus', '/omd/sites/ensurance/share/check_mk/agents/special/agent_prometheus')]
    },
    'netdata': {
        'alias': 'Netdata Monitoring', 
        'ipaddress': 'ensurance-netdata-full',
        'datasource_programs': [('special_prometheus', '/omd/sites/ensurance/share/check_mk/agents/special/agent_prometheus')]
    },
    'node-exporter': {
        'alias': 'Node Exporter', 
        'ipaddress': 'ensurance-node-exporter-full',
        'datasource_programs': [('special_prometheus', '/omd/sites/ensurance/share/check_mk/agents/special/agent_prometheus')]
    },
    'pushgateway': {'alias': 'Pushgateway', 'ipaddress': 'ensurance-pushgateway-full'},
    'ensurance-app': {
        'alias': 'Ensurance App', 
        'ipaddress': 'ensurance-pharmacy-full',
        'datasource_programs': [('special_prometheus', '/omd/sites/ensurance/share/check_mk/agents/special/agent_prometheus')]
    },
}
EOF
" 2>/dev/null

echo "  ✓ Configuración de hosts actualizada"

# ============================================================================
# PASO 5: Recargar configuración y descubrir servicios
# ============================================================================
echo ""
echo "[5/5] Recargando configuración y descubriendo servicios..."

# Recargar configuración
echo "  Recargando configuración..."
docker exec ensurance-checkmk-full omd su ensurance -c "cmk -R" 2>/dev/null

# Descubrir servicios en hosts con Prometheus
echo "  Descubriendo servicios Prometheus..."
for host in prometheus node-exporter rabbitmq netdata ensurance-app; do
    echo "    Descubriendo: $host"
    docker exec ensurance-checkmk-full omd su ensurance -c "cmk -I $host" 2>/dev/null || true
done

# Activar cambios
echo "  Activando cambios..."
docker exec ensurance-checkmk-full omd su ensurance -c "cmk -O" 2>/dev/null

# Reiniciar CheckMK para aplicar todos los cambios
echo "  Reiniciando CheckMK..."
docker exec ensurance-checkmk-full omd restart ensurance 2>/dev/null

echo ""
echo "═══════════════════════════════════════════════════════════════════"
echo "  ✓ Integración con Prometheus Completada"
echo "═══════════════════════════════════════════════════════════════════"
echo ""
echo "MÉTRICAS CONFIGURADAS (equivalentes a Netdata):"
echo "  📊 Sistema (desde node-exporter):"
echo "    • CPU Usage > 70%/90%"
echo "    • Memory Usage > 80%/95%"
echo "    • Disk Space > 75%/90%"
echo "    • Network Traffic > 100MB/s/200MB/s"
echo "    • Disk I/O > 50MB/s/100MB/s"
echo ""
echo "  🐰 RabbitMQ:"
echo "    • Queue Messages > 1000/5000"
echo ""
echo "  🌐 Aplicación:"
echo "    • HTTP Response Time > 1s/3s"
echo "    • HTTP Error Rate > 5%/20%"
echo ""
echo "  📈 Netdata:"
echo "    • Todas las métricas de Netdata vía Prometheus"
echo ""
echo "PRÓXIMOS PASOS:"
echo "  1. Espera 2-3 minutos"
echo "  2. Actualiza la página de CheckMK (F5)"
echo "  3. Ve a 'Monitor > All Services'"
echo "  4. Busca servicios con '(Prometheus)' en el nombre"
echo "  5. Activa los cambios pendientes si es necesario"
echo ""
echo "URL: http://localhost:5152/ensurance/check_mk/"
echo "Usuario: cmkadmin / Password: admin123"
echo ""
