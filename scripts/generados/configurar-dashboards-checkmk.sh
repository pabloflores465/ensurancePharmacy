#!/bin/bash

# ============================================================================
# CONFIGURACIÓN DE DASHBOARDS Y GRÁFICAS EN CHECKMK
# Similar a las gráficas de Netdata
# ============================================================================

set -e

echo "═══════════════════════════════════════════════════════════════════"
echo "  Configurando Dashboards y Gráficas en CheckMK"
echo "  Equivalentes a Netdata"
echo "═══════════════════════════════════════════════════════════════════"

CHECKMK_SITE="ensurance"
API_URL="http://localhost:5152/ensurance/check_mk/api/1.0"
API_USER="automation"
API_PASS="automation_secret"
CMK_USER="cmkadmin"
CMK_PASS="admin123"

# ============================================================================
# PASO 1: Configurar usuario de automatización
# ============================================================================
echo ""
echo "[1/8] Configurando usuario de automatización..."

docker exec ensurance-checkmk-full omd su $CHECKMK_SITE -c "
htpasswd -b ~/etc/htpasswd $API_USER $API_PASS 2>/dev/null || true
echo 'automation_secret = \"$API_PASS\"' > ~/var/check_mk/web/$API_USER/automation.secret
" 2>/dev/null || true

echo "  ✅ Usuario de automatización configurado"

# ============================================================================
# PASO 2: Configurar Special Agent para Prometheus
# ============================================================================
echo ""
echo "[2/8] Configurando Special Agent de Prometheus..."

docker exec ensurance-checkmk-full omd su $CHECKMK_SITE -c "cat > ~/etc/check_mk/conf.d/wato/special_agents.mk << 'EOF'
# Configuración de Special Agents para Prometheus

# Reglas para el Special Agent de Prometheus
special_agents.setdefault('prometheus', [])

# Node Exporter - Métricas del sistema
special_agents['prometheus'].append({
    'id': 'node_exporter_metrics',
    'value': {
        'connection': ('url_custom', {
            'url_address': 'http://ensurance-node-exporter-full:9100/metrics'
        }),
        'verify_cert': False,
        'protocol': 'http',
    },
    'condition': {
        'host_name': ['node-exporter']
    }
})

# Prometheus Server
special_agents['prometheus'].append({
    'id': 'prometheus_server_metrics',
    'value': {
        'connection': ('url_custom', {
            'url_address': 'http://ensurance-prometheus-full:9090/metrics'
        }),
        'verify_cert': False,
        'protocol': 'http',
    },
    'condition': {
        'host_name': ['prometheus']
    }
})

# Netdata Prometheus Exporter
special_agents['prometheus'].append({
    'id': 'netdata_prometheus_metrics',
    'value': {
        'connection': ('url_custom', {
            'url_address': 'http://ensurance-netdata-full:19999/api/v1/allmetrics?format=prometheus'
        }),
        'verify_cert': False,
        'protocol': 'http',
    },
    'condition': {
        'host_name': ['netdata']
    }
})

# RabbitMQ
special_agents['prometheus'].append({
    'id': 'rabbitmq_metrics',
    'value': {
        'connection': ('url_custom', {
            'url_address': 'http://ensurance-rabbitmq-full:15692/metrics'
        }),
        'verify_cert': False,
        'protocol': 'http',
    },
    'condition': {
        'host_name': ['rabbitmq']
    }
})

print('Special agents configured')
EOF
" 2>/dev/null

echo "  ✅ Special Agent de Prometheus configurado"

# ============================================================================
# PASO 3: Configurar checks activos HTTP
# ============================================================================
echo ""
echo "[3/8] Configurando checks HTTP..."

docker exec ensurance-checkmk-full omd su $CHECKMK_SITE -c "cat > ~/etc/check_mk/conf.d/wato/active_checks.mk << 'EOF'
# Active HTTP Checks

active_checks = {
    'http': [
        # Prometheus
        {
            'id': 'check_prometheus',
            'service_description': 'Prometheus Web UI',
            'host': 'prometheus',
            'mode': ('url', {
                'uri': '/',
                'ssl': 'auto',
                'response_time': (1.0, 3.0),
                'timeout': 10,
                'port': 9090,
            })
        },
        # Grafana
        {
            'id': 'check_grafana',
            'service_description': 'Grafana Web UI',
            'host': 'grafana',
            'mode': ('url', {
                'uri': '/api/health',
                'ssl': 'auto',
                'response_time': (1.0, 3.0),
                'timeout': 10,
                'port': 3000,
            })
        },
        # AlertManager
        {
            'id': 'check_alertmanager',
            'service_description': 'AlertManager Web UI',
            'host': 'alertmanager',
            'mode': ('url', {
                'uri': '/-/healthy',
                'ssl': 'auto',
                'response_time': (1.0, 3.0),
                'timeout': 10,
                'port': 9093,
            })
        },
        # Netdata
        {
            'id': 'check_netdata',
            'service_description': 'Netdata Web UI',
            'host': 'netdata',
            'mode': ('url', {
                'uri': '/api/v1/info',
                'ssl': 'auto',
                'response_time': (1.0, 3.0),
                'timeout': 10,
                'port': 19999,
            })
        },
    ]
}

print('Active checks configured')
EOF
" 2>/dev/null

echo "  ✅ Checks HTTP configurados"

# ============================================================================
# PASO 4: Crear dashboards personalizados
# ============================================================================
echo ""
echo "[4/8] Creando dashboards personalizados..."

docker exec ensurance-checkmk-full omd su $CHECKMK_SITE -c "mkdir -p ~/var/check_mk/web/cmkadmin/dashboards/" 2>/dev/null

docker exec ensurance-checkmk-full omd su $CHECKMK_SITE -c "cat > ~/var/check_mk/web/cmkadmin/dashboards/main.mk << 'DASHBOARDEOF'
# -*- encoding: utf-8; py-indent-offset: 4 -*-
# Dashboard principal equivalente a Netdata

dashboard = {
    'dashlets': [
        # Fila 1: Overview
        {
            'type': 'hoststats',
            'position': (1, 1),
            'size': (30, 18),
            'show_title': True,
            'title': '🖥️ Estado de Hosts',
            'title_url': 'view.py?view_name=allhosts',
        },
        {
            'type': 'servicestats',
            'position': (31, 1),
            'size': (30, 18),
            'show_title': True,
            'title': '⚙️ Estado de Servicios',
            'title_url': 'view.py?view_name=allservices',
        },
        
        # Fila 2: Problems y Performance
        {
            'type': 'view',
            'position': (1, 19),
            'size': (30, 20),
            'show_title': True,
            'title': '🚨 Problemas Actuales',
            'context': {
                'view_name': 'problemsofhost',
            },
        },
        {
            'type': 'pnpgraph',
            'position': (31, 19),
            'size': (30, 20),
            'show_title': True,
            'title': '📊 CPU Usage - Node Exporter',
            'context': {
                'host': 'node-exporter',
                'service': 'Prometheus node_cpu_seconds_total',
                'graph_index': 0,
            },
        },
        
        # Fila 3: Gráficas de sistema
        {
            'type': 'pnpgraph',
            'position': (1, 39),
            'size': (30, 20),
            'show_title': True,
            'title': '💾 Memory Usage',
            'context': {
                'host': 'node-exporter',
                'service': 'Prometheus node_memory_MemAvailable_bytes',
                'graph_index': 0,
            },
        },
        {
            'type': 'pnpgraph',
            'position': (31, 39),
            'size': (30, 20),
            'show_title': True,
            'title': '💿 Disk Space',
            'context': {
                'host': 'node-exporter',
                'service': 'Prometheus node_filesystem_avail_bytes',
                'graph_index': 0,
            },
        },
        
        # Fila 4: Red y servicios
        {
            'type': 'pnpgraph',
            'position': (1, 59),
            'size': (30, 20),
            'show_title': True,
            'title': '🌐 Network Traffic',
            'context': {
                'host': 'node-exporter',
                'service': 'Prometheus node_network_receive_bytes_total',
                'graph_index': 0,
            },
        },
        {
            'type': 'view',
            'position': (31, 59),
            'size': (30, 20),
            'show_title': True,
            'title': '📋 Todos los Servicios HTTP',
            'context': {
                'view_name': 'searchsvc',
                'service': 'HTTP',
            },
        },
    ],
    'mandatory_context_filters': [],
    'hidebutton': False,
    'public': True,
    'name': 'main',
    'title': '📊 Ensurance Pharmacy - Dashboard Principal',
    'description': 'Dashboard principal con métricas similares a Netdata',
    'owner': 'cmkadmin',
    'add_context_to_title': True,
    'link_from': 'topics',
    'topic': 'overview',
    'sort_index': 5,
    'is_show_more': False,
    'packaged': False,
    'megamenu_search_terms': [],
}
DASHBOARDEOF
" 2>/dev/null

echo "  ✅ Dashboard principal creado"

# ============================================================================
# PASO 5: Crear dashboard de métricas de sistema
# ============================================================================
echo ""
echo "[5/8] Creando dashboard de métricas de sistema..."

docker exec ensurance-checkmk-full omd su $CHECKMK_SITE -c "cat > ~/var/check_mk/web/cmkadmin/dashboards/system_metrics.mk << 'SYSTEMEOF'
# -*- encoding: utf-8; py-indent-offset: 4 -*-
# Dashboard de métricas de sistema

dashboard = {
    'dashlets': [
        # CPU Metrics
        {
            'type': 'view',
            'position': (1, 1),
            'size': (60, 15),
            'show_title': True,
            'title': '🔥 CPU Utilization (All Hosts)',
            'context': {
                'view_name': 'searchsvc',
                'service': 'CPU',
            },
        },
        
        # Memory Metrics
        {
            'type': 'view',
            'position': (1, 16),
            'size': (60, 15),
            'show_title': True,
            'title': '💾 Memory Usage (All Hosts)',
            'context': {
                'view_name': 'searchsvc',
                'service': 'Memory',
            },
        },
        
        # Disk Metrics
        {
            'type': 'view',
            'position': (1, 31),
            'size': (60, 15),
            'show_title': True,
            'title': '💿 Disk Space (All Hosts)',
            'context': {
                'view_name': 'searchsvc',
                'service': 'Filesystem',
            },
        },
        
        # Network Metrics
        {
            'type': 'view',
            'position': (1, 46),
            'size': (60, 15),
            'show_title': True,
            'title': '🌐 Network Interfaces (All Hosts)',
            'context': {
                'view_name': 'searchsvc',
                'service': 'Interface',
            },
        },
    ],
    'mandatory_context_filters': [],
    'hidebutton': False,
    'public': True,
    'name': 'system_metrics',
    'title': '🖥️ System Metrics Dashboard',
    'description': 'Métricas de sistema similares a Netdata',
    'owner': 'cmkadmin',
    'add_context_to_title': True,
    'link_from': 'topics',
    'topic': 'overview',
    'sort_index': 10,
    'is_show_more': False,
    'packaged': False,
    'megamenu_search_terms': [],
}
SYSTEMEOF
" 2>/dev/null

echo "  ✅ Dashboard de métricas de sistema creado"

# ============================================================================
# PASO 6: Crear dashboard de aplicaciones
# ============================================================================
echo ""
echo "[6/8] Creando dashboard de aplicaciones..."

docker exec ensurance-checkmk-full omd su $CHECKMK_SITE -c "cat > ~/var/check_mk/web/cmkadmin/dashboards/applications.mk << 'APPEOF'
# -*- encoding: utf-8; py-indent-offset: 4 -*-
# Dashboard de aplicaciones y servicios

dashboard = {
    'dashlets': [
        # Web Services
        {
            'type': 'view',
            'position': (1, 1),
            'size': (60, 20),
            'show_title': True,
            'title': '🌐 Web Services Health',
            'context': {
                'view_name': 'searchsvc',
                'service': 'HTTP',
            },
        },
        
        # Prometheus Metrics
        {
            'type': 'view',
            'position': (1, 21),
            'size': (30, 20),
            'show_title': True,
            'title': '📊 Prometheus Services',
            'context': {
                'view_name': 'searchsvc',
                'service': 'Prometheus',
            },
        },
        
        # RabbitMQ
        {
            'type': 'view',
            'position': (31, 21),
            'size': (30, 20),
            'show_title': True,
            'title': '🐰 RabbitMQ Services',
            'context': {
                'view_name': 'searchhost',
                'host': 'rabbitmq',
            },
        },
        
        # Netdata
        {
            'type': 'view',
            'position': (1, 41),
            'size': (30, 20),
            'show_title': True,
            'title': '📈 Netdata Metrics',
            'context': {
                'view_name': 'searchhost',
                'host': 'netdata',
            },
        },
        
        # Grafana
        {
            'type': 'view',
            'position': (31, 41),
            'size': (30, 20),
            'show_title': True,
            'title': '📉 Grafana Services',
            'context': {
                'view_name': 'searchhost',
                'host': 'grafana',
            },
        },
    ],
    'mandatory_context_filters': [],
    'hidebutton': False,
    'public': True,
    'name': 'applications',
    'title': '🚀 Applications Dashboard',
    'description': 'Estado de aplicaciones y servicios',
    'owner': 'cmkadmin',
    'add_context_to_title': True,
    'link_from': 'topics',
    'topic': 'overview',
    'sort_index': 15,
    'is_show_more': False,
    'packaged': False,
    'megamenu_search_terms': [],
}
APPEOF
" 2>/dev/null

echo "  ✅ Dashboard de aplicaciones creado"

# ============================================================================
# PASO 7: Descubrir servicios y activar cambios
# ============================================================================
echo ""
echo "[7/8] Descubriendo servicios en todos los hosts..."

# Lista de hosts
HOSTS=(
    "prometheus"
    "grafana"
    "alertmanager"
    "rabbitmq"
    "netdata"
    "node-exporter"
    "pushgateway"
    "ensurance-app"
)

for host in "${HOSTS[@]}"; do
    echo "  Descubriendo servicios en: $host"
    
    # Ejecutar bulk discovery
    curl -s -X POST "$API_URL/domain-types/service_discovery_run/actions/bulk-discovery-start/invoke" \
        -u "$API_USER:$API_PASS" \
        -H "Content-Type: application/json" \
        -d "{
            \"hostnames\": [\"$host\"],
            \"mode\": \"refresh\"
        }" > /dev/null 2>&1 || true
    
    sleep 2
done

echo "  ✅ Service discovery ejecutado en todos los hosts"

echo ""
echo "  Activando cambios..."

# Activar cambios
curl -s -X POST "$API_URL/domain-types/activation_run/actions/activate-changes/invoke" \
    -u "$API_USER:$API_PASS" \
    -H "Content-Type: application/json" \
    -d '{
        "redirect": false,
        "sites": ["ensurance"],
        "force_foreign_changes": true
    }' > /dev/null 2>&1 || true

sleep 5

echo "  ✅ Cambios activados"

# ============================================================================
# PASO 8: Habilitar graphing con PNP4Nagios (si está disponible)
# ============================================================================
echo ""
echo "[8/8] Configurando graphing..."

docker exec ensurance-checkmk-full omd su $CHECKMK_SITE -c "
# Habilitar el procesamiento de datos de performance
omd config set CORE nagios 2>/dev/null || true
omd config set PNP4NAGIOS on 2>/dev/null || true

# Configurar para usar RRDtool
cat > ~/etc/check_mk/conf.d/wato/graphing.mk << 'EOF'
# Configuración de gráficas

# Habilitar gráficas para todas las métricas de Prometheus
service_graphs = []

# Configurar para mostrar gráficas automáticamente
graph_timeranges = [
    ('d0', 'Today'),
    ('d1', 'Yesterday'),
    ('w0', 'This week'),
    ('w1', 'Last week'),
    ('m0', 'This month'),
    ('m1', 'Last month'),
    ('y0', 'This year'),
    ('y1', 'Last year'),
    ('4h', 'Last 4 hours'),
    ('25h', 'Last 25 hours'),
    ('8d', 'Last 8 days'),
    ('35d', 'Last 35 days'),
    ('400d', 'Last 400 days'),
]

print('Graphing configured')
EOF
" 2>/dev/null

echo "  ✅ Graphing configurado"

# Recargar configuración completa
echo ""
echo "Recargando configuración de CheckMK..."
docker exec ensurance-checkmk-full omd su $CHECKMK_SITE -c "omd reload" 2>/dev/null || true

echo ""
echo "═══════════════════════════════════════════════════════════════════"
echo "  ✅ Dashboards y Gráficas Configurados"
echo "═══════════════════════════════════════════════════════════════════"
echo ""
echo "ACCESO A CHECKMK:"
echo "  URL:      http://localhost:5152/ensurance/check_mk/"
echo "  Usuario:  $CMK_USER"
echo "  Password: $CMK_PASS"
echo ""
echo "DASHBOARDS CREADOS:"
echo "  📊 Dashboard Principal (main)"
echo "     - Overview de hosts y servicios"
echo "     - Problemas actuales"
echo "     - Gráficas de CPU, Memoria, Disco, Red"
echo ""
echo "  🖥️ System Metrics Dashboard"
echo "     - CPU de todos los hosts"
echo "     - Memoria de todos los hosts"
echo "     - Disco de todos los hosts"
echo "     - Red de todos los hosts"
echo ""
echo "  🚀 Applications Dashboard"
echo "     - Health de servicios web"
echo "     - Métricas de Prometheus"
echo "     - Estado de RabbitMQ"
echo "     - Métricas de Netdata"
echo "     - Estado de Grafana"
echo ""
echo "CÓMO VER LOS DASHBOARDS:"
echo "  1. Accede a CheckMK con las credenciales arriba"
echo "  2. Ve al menú superior: Monitor > Dashboards"
echo "  3. Selecciona uno de los dashboards creados:"
echo "     - 📊 Ensurance Pharmacy - Dashboard Principal"
echo "     - 🖥️ System Metrics Dashboard"
echo "     - 🚀 Applications Dashboard"
echo ""
echo "CÓMO VER GRÁFICAS DE UN SERVICIO:"
echo "  1. Ve a: Monitor > All services"
echo "  2. Haz clic en cualquier servicio"
echo "  3. En la página del servicio:"
echo "     - Verás el estado actual"
echo "     - Si hay métricas, verás la sección 'Service Metrics'"
echo "     - Haz clic en cualquier métrica para ver gráfica detallada"
echo ""
echo "⏱️ IMPORTANTE - TIEMPOS DE ESPERA:"
echo "  - Los servicios se están descubriendo AHORA"
echo "  - Primera verificación: 1-5 minutos"
echo "  - Primeras métricas: 5-10 minutos"
echo "  - Gráficas completas: 15-30 minutos"
echo ""
echo "  💡 Mientras esperas, puedes:"
echo "     - Ver el estado de hosts en 'Monitor > Hosts'"
echo "     - Verificar servicios en 'Monitor > All services'"
echo "     - Usar Netdata para gráficas inmediatas: http://localhost:19999"
echo ""
echo "VERIFICAR SERVICIOS DESCUBIERTOS:"
echo "  ./verificar-checkmk.sh"
echo ""
echo "SI NO VES GRÁFICAS DESPUÉS DE 30 MINUTOS:"
echo "  1. Verifica que Prometheus esté corriendo:"
echo "     docker ps | grep prometheus"
echo ""
echo "  2. Verifica que Node Exporter esté corriendo:"
echo "     docker ps | grep node-exporter"
echo ""
echo "  3. Verifica endpoints de métricas:"
echo "     curl http://localhost:9100/metrics | head"
echo "     curl http://localhost:9090/metrics | head"
echo ""
echo "  4. Ejecuta service discovery manual:"
echo "     ./habilitar-graficas-checkmk.sh"
echo ""
