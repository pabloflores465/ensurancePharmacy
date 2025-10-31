#!/bin/bash

# ============================================================================
# CONFIGURACIÓN DE GRÁFICAS DE PROMETHEUS EN CHECKMK
# Configurar visualización de métricas similares a Netdata
# ============================================================================

set -e

echo "═══════════════════════════════════════════════════════════════════"
echo "  Configurando Gráficas de Prometheus en CheckMK"
echo "═══════════════════════════════════════════════════════════════════"

CHECKMK_SITE="ensurance"

# ============================================================================
# PASO 1: Configurar plugins personalizados para métricas de Prometheus
# ============================================================================
echo ""
echo "[1/5] Creando plugins personalizados de CheckMK..."

docker exec ensurance-checkmk-full omd su $CHECKMK_SITE -c "
mkdir -p ~/local/share/check_mk/checks
mkdir -p ~/local/share/check_mk/web/plugins/metrics
mkdir -p ~/local/share/check_mk/web/plugins/perfometer
" 2>/dev/null

# Plugin para CPU
docker exec ensurance-checkmk-full omd su $CHECKMK_SITE -c "cat > ~/local/share/check_mk/web/plugins/metrics/prometheus_cpu.py << 'EOF'
#!/usr/bin/env python3
# -*- encoding: utf-8; py-indent-offset: 4 -*-

# Métricas de CPU desde Prometheus
metric_info['node_cpu_utilization'] = {
    'title': 'CPU Utilization',
    'unit': '%',
    'color': '#ff6b6b',
}

graph_info['node_cpu_usage'] = {
    'title': 'CPU Usage Over Time',
    'metrics': [
        ('node_cpu_utilization', 'area'),
    ],
    'scalars': [
        ('node_cpu_utilization:warn', 'Warning'),
        ('node_cpu_utilization:crit', 'Critical'),
    ],
    'range': (0, 100),
}

perfometer_info.append({
    'type': 'linear',
    'segments': ['node_cpu_utilization'],
    'total': 100.0,
})
EOF
" 2>/dev/null

echo "  ✅ Plugin de CPU creado"

# Plugin para Memoria
docker exec ensurance-checkmk-full omd su $CHECKMK_SITE -c "cat > ~/local/share/check_mk/web/plugins/metrics/prometheus_memory.py << 'EOF'
#!/usr/bin/env python3
# -*- encoding: utf-8; py-indent-offset: 4 -*-

# Métricas de Memoria desde Prometheus
metric_info['node_memory_usage'] = {
    'title': 'Memory Usage',
    'unit': '%',
    'color': '#4ecdc4',
}

metric_info['node_memory_available'] = {
    'title': 'Memory Available',
    'unit': 'bytes',
    'color': '#95e1d3',
}

graph_info['node_memory_overview'] = {
    'title': 'Memory Usage Overview',
    'metrics': [
        ('node_memory_usage', 'area'),
    ],
    'scalars': [
        ('node_memory_usage:warn', 'Warning'),
        ('node_memory_usage:crit', 'Critical'),
    ],
    'range': (0, 100),
}

perfometer_info.append({
    'type': 'linear',
    'segments': ['node_memory_usage'],
    'total': 100.0,
})
EOF
" 2>/dev/null

echo "  ✅ Plugin de Memoria creado"

# Plugin para Disco
docker exec ensurance-checkmk-full omd su $CHECKMK_SITE -c "cat > ~/local/share/check_mk/web/plugins/metrics/prometheus_disk.py << 'EOF'
#!/usr/bin/env python3
# -*- encoding: utf-8; py-indent-offset: 4 -*-

# Métricas de Disco desde Prometheus
metric_info['node_disk_usage'] = {
    'title': 'Disk Usage',
    'unit': '%',
    'color': '#f38181',
}

metric_info['node_disk_available'] = {
    'title': 'Disk Available',
    'unit': 'bytes',
    'color': '#aa96da',
}

graph_info['node_disk_overview'] = {
    'title': 'Disk Space Overview',
    'metrics': [
        ('node_disk_usage', 'area'),
    ],
    'scalars': [
        ('node_disk_usage:warn', 'Warning'),
        ('node_disk_usage:crit', 'Critical'),
    ],
    'range': (0, 100),
}

perfometer_info.append({
    'type': 'linear',
    'segments': ['node_disk_usage'],
    'total': 100.0,
})
EOF
" 2>/dev/null

echo "  ✅ Plugin de Disco creado"

# Plugin para Red
docker exec ensurance-checkmk-full omd su $CHECKMK_SITE -c "cat > ~/local/share/check_mk/web/plugins/metrics/prometheus_network.py << 'EOF'
#!/usr/bin/env python3
# -*- encoding: utf-8; py-indent-offset: 4 -*-

# Métricas de Red desde Prometheus
metric_info['node_network_receive'] = {
    'title': 'Network Receive',
    'unit': 'bytes/s',
    'color': '#00b894',
}

metric_info['node_network_transmit'] = {
    'title': 'Network Transmit',
    'unit': 'bytes/s',
    'color': '#fdcb6e',
}

graph_info['node_network_traffic'] = {
    'title': 'Network Traffic',
    'metrics': [
        ('node_network_receive', 'area'),
        ('node_network_transmit', '-area'),
    ],
}

perfometer_info.append({
    'type': 'dual',
    'perfometers': [
        {
            'type': 'linear',
            'segments': ['node_network_receive'],
            'total': 1000000000.0,  # 1 GB/s
        },
        {
            'type': 'linear',
            'segments': ['node_network_transmit'],
            'total': 1000000000.0,  # 1 GB/s
        },
    ],
})
EOF
" 2>/dev/null

echo "  ✅ Plugin de Red creado"

# ============================================================================
# PASO 2: Configurar integración con Grafana para gráficas avanzadas
# ============================================================================
echo ""
echo "[2/5] Configurando integración con Grafana..."

docker exec ensurance-checkmk-full omd su $CHECKMK_SITE -c "cat > ~/etc/check_mk/conf.d/wato/grafana_integration.mk << 'EOF'
# Integración con Grafana para gráficas avanzadas

# Configurar enlace a Grafana
custom_service_attributes.setdefault('_GRAFANA_URL', {})
custom_service_attributes['_GRAFANA_URL'] = {
    'prometheus': 'http://localhost:3001/d/prometheus-stats/prometheus-stats',
    'node-exporter': 'http://localhost:3001/d/node-exporter-full/node-exporter-full',
    'netdata': 'http://localhost:3001/d/netdata/netdata',
    'rabbitmq': 'http://localhost:3001/d/rabbitmq-overview/rabbitmq-overview',
}

print('Grafana integration configured')
EOF
" 2>/dev/null

echo "  ✅ Integración con Grafana configurada"

# ============================================================================
# PASO 3: Crear vistas personalizadas con métricas
# ============================================================================
echo ""
echo "[3/5] Creando vistas personalizadas..."

docker exec ensurance-checkmk-full omd su $CHECKMK_SITE -c "
mkdir -p ~/var/check_mk/web/cmkadmin/views
cat > ~/var/check_mk/web/cmkadmin/views/prometheus_metrics.mk << 'VIEWEOF'
# -*- encoding: utf-8; py-indent-offset: 4 -*-
# Vista de métricas de Prometheus

view = {
    'datasource': 'services',
    'painters': [
        ('host', None),
        ('service_description', None),
        ('service_state', None),
        ('svc_plugin_output', None),
        ('svc_perf_val01', None),
        ('svc_perf_val02', None),
        ('svc_metrics', None),
        ('service_icons', None),
    ],
    'sorters': [
        ('svcoutput', False),
    ],
    'title': '📊 Prometheus Metrics - All Services',
    'description': 'Todos los servicios con métricas de Prometheus',
    'public': True,
    'name': 'prometheus_metrics',
    'topic': 'services',
    'sort_index': 20,
    'is_show_more': True,
    'icon': 'services',
    'hidden': False,
    'hidebutton': False,
    'mustsearch': False,
    'force_checkboxes': False,
    'mobile': False,
    'group_painters': [],
    'num_columns': 1,
    'browser_reload': 30,
    'layout': 'table',
    'owner': 'cmkadmin',
    'add_context_to_title': True,
    'link_from': {},
    'single_infos': [],
    'context': {
        'service_regex': {
            'service_regex': '.*Prometheus.*'
        }
    },
    'megamenu_search_terms': [],
}
VIEWEOF
" 2>/dev/null

echo "  ✅ Vistas personalizadas creadas"

# ============================================================================
# PASO 4: Configurar Custom Graphs
# ============================================================================
echo ""
echo "[4/5] Configurando Custom Graphs..."

docker exec ensurance-checkmk-full omd su $CHECKMK_SITE -c "
mkdir -p ~/var/check_mk/web/cmkadmin/custom_graphs
cat > ~/var/check_mk/web/cmkadmin/custom_graphs/system_overview.mk << 'GRAPHEOF'
# -*- encoding: utf-8; py-indent-offset: 4 -*-
# Custom Graph: System Overview

custom_graph = {
    'id': 'system_overview',
    'title': '🖥️ System Overview - CPU, Memory, Disk',
    'unit': '%',
    'consolidation_function': 'max',
    'explicit_vertical_range': (0.0, 100.0),
    'metrics': [
        ('node_cpu_utilization,node-exporter,Prometheus node_cpu_seconds_total', 'CPU Usage'),
        ('node_memory_usage,node-exporter,Prometheus node_memory_MemAvailable_bytes', 'Memory Usage'),
        ('node_disk_usage,node-exporter,Prometheus node_filesystem_avail_bytes', 'Disk Usage'),
    ],
}
GRAPHEOF
" 2>/dev/null

echo "  ✅ Custom Graphs configurados"

# ============================================================================
# PASO 5: Crear página HTML personalizada con enlaces a Grafana
# ============================================================================
echo ""
echo "[5/5] Creando página de acceso rápido a gráficas..."

docker exec ensurance-checkmk-full omd su $CHECKMK_SITE -c "
mkdir -p ~/var/check_mk/web/htdocs/grafana
cat > ~/var/check_mk/web/htdocs/grafana/index.html << 'HTMLEOF'
<!DOCTYPE html>
<html lang=\"es\">
<head>
    <meta charset=\"UTF-8\">
    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">
    <title>Gráficas - Ensurance Pharmacy</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            padding: 20px;
            min-height: 100vh;
        }
        .container {
            max-width: 1400px;
            margin: 0 auto;
        }
        h1 {
            color: white;
            text-align: center;
            margin-bottom: 30px;
            font-size: 2.5em;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
        }
        .dashboard-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        .dashboard-card {
            background: white;
            border-radius: 15px;
            padding: 25px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
            transition: transform 0.3s ease, box-shadow 0.3s ease;
        }
        .dashboard-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 15px 40px rgba(0,0,0,0.3);
        }
        .dashboard-card h2 {
            color: #667eea;
            margin-bottom: 15px;
            font-size: 1.5em;
        }
        .dashboard-card p {
            color: #666;
            margin-bottom: 20px;
            line-height: 1.6;
        }
        .dashboard-card a {
            display: inline-block;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 12px 30px;
            text-decoration: none;
            border-radius: 25px;
            font-weight: bold;
            transition: all 0.3s ease;
        }
        .dashboard-card a:hover {
            background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
            transform: scale(1.05);
        }
        .icon {
            font-size: 3em;
            margin-bottom: 15px;
        }
        .info-box {
            background: white;
            border-radius: 15px;
            padding: 25px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
            margin-top: 20px;
        }
        .info-box h3 {
            color: #667eea;
            margin-bottom: 15px;
        }
        .info-box ul {
            list-style: none;
            padding-left: 0;
        }
        .info-box li {
            color: #666;
            padding: 10px 0;
            border-bottom: 1px solid #eee;
        }
        .info-box li:last-child {
            border-bottom: none;
        }
        .metric-badge {
            display: inline-block;
            background: #667eea;
            color: white;
            padding: 5px 15px;
            border-radius: 20px;
            font-size: 0.9em;
            margin: 5px;
        }
    </style>
</head>
<body>
    <div class=\"container\">
        <h1>📊 Gráficas y Dashboards - Ensurance Pharmacy</h1>
        
        <div class=\"dashboard-grid\">
            <!-- Grafana -->
            <div class=\"dashboard-card\">
                <div class=\"icon\">📈</div>
                <h2>Grafana</h2>
                <p>Dashboards completos con gráficas de todas las métricas de Prometheus, similares a Netdata</p>
                <span class=\"metric-badge\">CPU</span>
                <span class=\"metric-badge\">Memoria</span>
                <span class=\"metric-badge\">Disco</span>
                <span class=\"metric-badge\">Red</span>
                <a href=\"http://localhost:3001\" target=\"_blank\">Abrir Grafana →</a>
            </div>
            
            <!-- Netdata -->
            <div class=\"dashboard-card\">
                <div class=\"icon\">⚡</div>
                <h2>Netdata</h2>
                <p>Gráficas en tiempo real con actualización cada segundo. Ideal para troubleshooting inmediato</p>
                <span class=\"metric-badge\">Tiempo Real</span>
                <span class=\"metric-badge\">Todas las Métricas</span>
                <a href=\"http://localhost:19999\" target=\"_blank\">Abrir Netdata →</a>
            </div>
            
            <!-- Prometheus -->
            <div class=\"dashboard-card\">
                <div class=\"icon\">🔍</div>
                <h2>Prometheus</h2>
                <p>Consulta directa de métricas y exploración de datos históricos</p>
                <span class=\"metric-badge\">PromQL</span>
                <span class=\"metric-badge\">Métricas</span>
                <a href=\"http://localhost:9090\" target=\"_blank\">Abrir Prometheus →</a>
            </div>
            
            <!-- CheckMK Services -->
            <div class=\"dashboard-card\">
                <div class=\"icon\">⚙️</div>
                <h2>CheckMK - Servicios</h2>
                <p>Lista de todos los servicios monitoreados con sus métricas y estado actual</p>
                <span class=\"metric-badge\">Servicios</span>
                <span class=\"metric-badge\">Alertas</span>
                <a href=\"/ensurance/check_mk/view.py?view_name=allservices\" target=\"_blank\">Ver Servicios →</a>
            </div>
            
            <!-- CheckMK Prometheus Metrics -->
            <div class=\"dashboard-card\">
                <div class=\"icon\">📊</div>
                <h2>CheckMK - Métricas Prometheus</h2>
                <p>Servicios de Prometheus con métricas de sistema similar a Netdata</p>
                <span class=\"metric-badge\">Prometheus</span>
                <span class=\"metric-badge\">Node Exporter</span>
                <a href=\"/ensurance/check_mk/view.py?view_name=prometheus_metrics\" target=\"_blank\">Ver Métricas →</a>
            </div>
            
            <!-- CheckMK Hosts -->
            <div class=\"dashboard-card\">
                <div class=\"icon\">🖥️</div>
                <h2>CheckMK - Hosts</h2>
                <p>Estado de todos los hosts monitoreados en la infraestructura</p>
                <span class=\"metric-badge\">Hosts</span>
                <span class=\"metric-badge\">Estado</span>
                <a href=\"/ensurance/check_mk/view.py?view_name=allhosts\" target=\"_blank\">Ver Hosts →</a>
            </div>
        </div>
        
        <div class=\"info-box\">
            <h3>💡 Información Importante</h3>
            <ul>
                <li><strong>🎯 Para gráficas detalladas:</strong> Usa Grafana - tiene dashboards pre-configurados con las mismas métricas que Netdata</li>
                <li><strong>⚡ Para monitoreo en tiempo real:</strong> Usa Netdata - actualización cada segundo</li>
                <li><strong>🔍 Para consultas avanzadas:</strong> Usa Prometheus - lenguaje PromQL para queries personalizados</li>
                <li><strong>📋 Para alertas y gestión:</strong> Usa CheckMK - sistema enterprise de monitoreo</li>
                <li><strong>⏱️ Tiempo de espera:</strong> Las métricas en CheckMK tardan 15-30 minutos en mostrar gráficas completas</li>
                <li><strong>📊 Credenciales CheckMK:</strong> cmkadmin / admin123</li>
                <li><strong>📊 Credenciales Grafana:</strong> admin / admin123</li>
            </ul>
        </div>
    </div>
</body>
</html>
HTMLEOF
" 2>/dev/null

echo "  ✅ Página de acceso rápido creada"

# Recargar CheckMK
echo ""
echo "Recargando CheckMK..."
docker exec ensurance-checkmk-full omd su $CHECKMK_SITE -c "omd reload" 2>/dev/null || true

echo ""
echo "═══════════════════════════════════════════════════════════════════"
echo "  ✅ Gráficas de Prometheus Configuradas"
echo "═══════════════════════════════════════════════════════════════════"
echo ""
echo "ACCESO A GRÁFICAS:"
echo ""
echo "  🌐 Página de Acceso Rápido (HTML):"
echo "     http://localhost:5152/ensurance/grafana/"
echo "     - Enlaces a todas las herramientas de gráficas"
echo "     - Información sobre cada sistema"
echo ""
echo "  📈 GRAFANA (Recomendado para gráficas):"
echo "     http://localhost:3001"
echo "     Usuario: admin"
echo "     Password: admin123"
echo "     ✨ Dashboards con gráficas idénticas a Netdata"
echo ""
echo "  ⚡ NETDATA (Tiempo Real):"
echo "     http://localhost:19999"
echo "     ✨ Gráficas en tiempo real cada segundo"
echo ""
echo "  📊 CHECKMK (Gestión y Alertas):"
echo "     http://localhost:5152/ensurance/check_mk/"
echo "     Usuario: cmkadmin"
echo "     Password: admin123"
echo ""
echo "VISTAS PERSONALIZADAS EN CHECKMK:"
echo ""
echo "  1. Métricas de Prometheus:"
echo "     http://localhost:5152/ensurance/check_mk/view.py?view_name=prometheus_metrics"
echo ""
echo "  2. Todos los servicios:"
echo "     http://localhost:5152/ensurance/check_mk/view.py?view_name=allservices"
echo ""
echo "  3. Todos los hosts:"
echo "     http://localhost:5152/ensurance/check_mk/view.py?view_name=allhosts"
echo ""
echo "CÓMO VER GRÁFICAS EN CHECKMK:"
echo ""
echo "  Opción 1 - Desde un servicio:"
echo "    1. Ve a: Monitor > All services"
echo "    2. Haz clic en cualquier servicio con métricas"
echo "    3. En la página del servicio, busca la sección 'Service Metrics'"
echo "    4. Haz clic en 'Show graph' o en el icono de gráfica"
echo ""
echo "  Opción 2 - Vista de gráficas múltiples:"
echo "    1. Ve a: Monitor > Performance"
echo "    2. Selecciona el host que quieres ver"
echo "    3. Verás todas las gráficas disponibles"
echo ""
echo "  Opción 3 - Custom Graphs:"
echo "    1. Ve a: Monitor > Performance > Custom graphs"
echo "    2. Selecciona 'System Overview'"
echo "    3. Verás gráficas combinadas de CPU, Memoria y Disco"
echo ""
echo "COMPARACIÓN DE SISTEMAS:"
echo ""
echo "  ┌─────────────┬──────────────┬─────────────┬────────────────┐"
echo "  │   Sistema   │   Gráficas   │ Tiempo Real │  Histórico     │"
echo "  ├─────────────┼──────────────┼─────────────┼────────────────┤"
echo "  │ Netdata     │ ✅ Excelente │ ✅ Sí (1s)  │ ⚠️  3 días     │"
echo "  │ Grafana     │ ✅ Excelente │ ⚠️  30s-1m  │ ✅ Meses/Años  │"
echo "  │ CheckMK     │ ⚠️  Básico   │ ❌ 1-5min   │ ✅ Configurable│"
echo "  │ Prometheus  │ ⚠️  Básico   │ ⚠️  15s-1m  │ ✅ Semanas     │"
echo "  └─────────────┴──────────────┴─────────────┴────────────────┘"
echo ""
echo "RECOMENDACIÓN:"
echo "  🎯 Usa GRAFANA para gráficas similares a Netdata"
echo "  🎯 Usa NETDATA para troubleshooting en tiempo real"
echo "  🎯 Usa CHECKMK para alertas y gestión enterprise"
echo ""
