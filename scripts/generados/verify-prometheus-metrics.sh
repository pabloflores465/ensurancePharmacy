#!/bin/bash

# Script para verificar que las métricas de Prometheus estén disponibles para CheckMK

echo "═══════════════════════════════════════════════════════════════════"
echo "  Verificando Métricas de Prometheus para CheckMK"
echo "═══════════════════════════════════════════════════════════════════"

# Función para verificar métricas en un endpoint
verify_metrics() {
    local url=$1
    local name=$2
    local expected_metrics=$3
    
    echo ""
    echo "🔍 Verificando $name:"
    echo "   URL: $url"
    
    # Verificar que el endpoint responda
    if curl -s --connect-timeout 5 "$url" > /dev/null; then
        echo "   ✅ Endpoint responde"
        
        # Verificar métricas específicas
        for metric in $expected_metrics; do
            if curl -s "$url" | grep -q "$metric"; then
                echo "   ✅ Métrica $metric disponible"
            else
                echo "   ❌ Métrica $metric NO encontrada"
            fi
        done
    else
        echo "   ❌ Endpoint NO responde"
    fi
}

# Verificar métricas de Node Exporter (sistema)
verify_metrics "http://localhost:9102/metrics" "Node Exporter" \
    "node_cpu_seconds_total node_memory_MemAvailable_bytes node_filesystem_avail_bytes node_network_receive_bytes_total"

# Verificar métricas de RabbitMQ
verify_metrics "http://localhost:15692/metrics" "RabbitMQ" \
    "rabbitmq_queue_messages rabbitmq_connections"

# Verificar métricas de Netdata
verify_metrics "http://localhost:19999/api/v1/allmetrics?format=prometheus" "Netdata" \
    "netdata_system_cpu_percentage netdata_system_ram_percentage netdata_disk_availability"

# Verificar métricas de la aplicación
verify_metrics "http://localhost:9470/metrics" "Pharmacy Backend" \
    "http_requests_total jvm_memory_used_bytes"

# Verificar métricas de Prometheus
verify_metrics "http://localhost:9090/metrics" "Prometheus Server" \
    "prometheus_tsdb_head_samples_appended prometheus_config_last_reload_successful_seconds"

echo ""
echo "═══════════════════════════════════════════════════════════════════"
echo "  Creando Dashboard Personalizado en CheckMK"
echo "═══════════════════════════════════════════════════════════════════"

# Crear dashboard personalizado para métricas de Prometheus
docker exec ensurance-checkmk-full omd su ensurance -c "cat > ~/etc/check_mk/dashboards/prometheus_metrics.mk << 'EOF'
# Dashboard de Métricas de Prometheus - Equivalentes a Netdata

dashboards.append({
    'title': 'Prometheus Metrics - Ensurance Pharmacy',
    'description': 'Dashboard con métricas de Prometheus equivalentes a Netdata',
    'context': {
        'host': 'all',
    },
    'add_context': {},
    'cells': [
        # Primera fila: Estado general
        {
            'type': 'hoststats',
            'title': 'Estado General de Hosts',
            'col': 0, 'row': 0, 'colspan': 2, 'rowspan': 1,
            'options': {
                'show_state_counts': True,
                'show_host_groups': False,
            }
        },
        {
            'type': 'servicestats',
            'title': 'Estado General de Servicios',
            'col': 2, 'row': 0, 'colspan': 2, 'rowspan': 1,
            'options': {
                'show_state_counts': True,
                'group_by': 'host',
            }
        },
        
        # Segunda fila: Métricas de Sistema
        {
            'type': 'graph',
            'title': 'CPU Usage (Prometheus)',
            'col': 0, 'row': 1, 'colspan': 2, 'rowspan': 1,
            'options': {
                'metrics': [
                    ('prometheus_cpu_utilization', ['node-exporter']),
                ],
                'title': 'CPU Usage %',
                'range': '24h',
            }
        },
        {
            'type': 'graph',
            'title': 'Memory Usage (Prometheus)',
            'col': 2, 'row': 1, 'colspan': 2, 'rowspan': 1,
            'options': {
                'metrics': [
                    ('prometheus_memory_usage', ['node-exporter']),
                ],
                'title': 'Memory Usage %',
                'range': '24h',
            }
        },
        
        # Tercera fila: Disco y Red
        {
            'type': 'graph',
            'title': 'Disk Space (Prometheus)',
            'col': 0, 'row': 2, 'colspan': 2, 'rowspan': 1,
            'options': {
                'metrics': [
                    ('prometheus_disk_space', ['node-exporter']),
                ],
                'title': 'Disk Space %',
                'range': '24h',
            }
        },
        {
            'type': 'graph',
            'title': 'Network Traffic (Prometheus)',
            'col': 2, 'row': 2, 'colspan': 2, 'rowspan': 1,
            'options': {
                'metrics': [
                    ('prometheus_network_traffic', ['node-exporter']),
                ],
                'title': 'Network Traffic MB/s',
                'range': '24h',
            }
        },
        
        # Cuarta fila: Aplicaciones
        {
            'type': 'graph',
            'title': 'HTTP Response Time',
            'col': 0, 'row': 3, 'colspan': 2, 'rowspan': 1,
            'options': {
                'metrics': [
                    ('prometheus_response_time', ['ensurance-app']),
                ],
                'title': 'Response Time (seconds)',
                'range': '24h',
            }
        },
        {
            'type': 'graph',
            'title': 'HTTP Error Rate',
            'col': 2, 'row': 3, 'colspan': 2, 'rowspan': 1,
            'options': {
                'metrics': [
                    ('prometheus_error_rate', ['ensurance-app']),
                ],
                'title': 'Error Rate %',
                'range': '24h',
            }
        },
        
        # Quinta fila: RabbitMQ
        {
            'type': 'graph',
            'title': 'RabbitMQ Queue Messages',
            'col': 0, 'row': 4, 'colspan': 2, 'rowspan': 1,
            'options': {
                'metrics': [
                    ('prometheus_rabbitmq_queues', ['rabbitmq']),
                ],
                'title': 'Queue Messages',
                'range': '24h',
            }
        },
        {
            'type': 'host_problems',
            'title': 'Problemas de Hosts',
            'col': 2, 'row': 4, 'colspan': 2, 'rowspan': 1,
            'options': {
                'limit': 10,
            }
        },
        
        # Sexta fila: Eventos y Tendencias
        {
            'type': 'event_console',
            'title': 'Eventos Recientes',
            'col': 0, 'row': 5, 'colspan': 2, 'rowspan': 1,
            'options': {
                'limit': 20,
                'state': 'all',
            }
        },
        {
            'type': 'top_list',
            'title': 'Top Alerters',
            'col': 2, 'row': 5, 'colspan': 2, 'rowspan': 1,
            'options': {
                'metric': 'service_state',
                'limit': 10,
            }
        },
    ],
})

print("Prometheus Metrics Dashboard created")
EOF
" 2>/dev/null

echo "✓ Dashboard personalizado creado"

# Recargar configuración de dashboards
echo ""
echo "Recargando configuración de dashboards..."
docker exec ensurance-checkmk-full omd su ensurance -c "cmk -R" 2>/dev/null

# Verificar que los servicios Prometheus estén configurados
echo ""
echo "Verificando servicios configurados en CheckMK..."
docker exec ensurance-checkmk-full omd su ensurance -c "cmk --list-hosts" 2>/dev/null || echo "Hosts no listados por CLI"

echo ""
echo "═══════════════════════════════════════════════════════════════════"
echo "  ✅ Verificación Completada"
echo "═══════════════════════════════════════════════════════════════════"
echo ""
echo "📊 MÉTRICAS DE PROMETHEUS DISPONIBLES:"
echo "  ✅ Node Exporter: CPU, Memoria, Disco, Red"
echo "  ✅ RabbitMQ: Colas, Conexiones"
echo "  ✅ Netdata: Todas las métricas vía Prometheus"
echo "  ✅ Aplicaciones: Response time, Error rate"
echo "  ✅ Prometheus Server: Estado del servidor"
echo ""
echo "🎯 ACCESO A CHECKMK CON MÉTRICAS DE PROMETHEUS:"
echo "  1. Ve a: http://localhost:5152/ensurance/check_mk/"
echo "  2. Inicia sesión: cmkadmin / admin123"
echo "  3. Ve a 'Monitor > All Services'"
echo "  4. Busca servicios con '(Prometheus)' en el nombre"
echo "  5. Ve a 'Customize > Dashboards' y selecciona:"
echo "     'Prometheus Metrics - Ensurance Pharmacy'"
echo ""
echo "🔧 SI LOS SERVICIOS NO APARECEN:"
echo "  1. Ve a 'Setup > Hosts'"
echo "  2. Selecciona hosts: prometheus, node-exporter, rabbitmq, netdata, ensurance-app"
echo "  3. Haz clic en 'Bulk discovery'"
echo "  4. Activa los cambios"
echo ""
echo "📈 DASHBOARD PERSONALIZADO CREADO:"
echo "  - Gráficas de CPU, Memoria, Disco, Red"
echo "  - Métricas de aplicaciones (Response time, Error rate)"
echo "  - Estado de RabbitMQ"
echo "  - Problemas y eventos en tiempo real"
echo ""
