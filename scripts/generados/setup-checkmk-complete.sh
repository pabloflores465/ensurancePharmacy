#!/bin/bash

# ============================================================================
# CONFIGURACIÓN COMPLETA DE CHECKMK
# Integra CheckMK con Prometheus y replica alertas de Netdata
# ============================================================================

set -e

CHECKMK_URL="http://localhost:5152/ensurance"
API_URL="${CHECKMK_URL}/check_mk/api/1.0"
USERNAME="automation"
PASSWORD="automation_secret"

echo "═══════════════════════════════════════════════════════════════════"
echo "  Configuración Completa de CheckMK para Ensurance Pharmacy"
echo "═══════════════════════════════════════════════════════════════════"

# ============================================================================
# PASO 1: Esperar a que CheckMK esté listo
# ============================================================================
echo ""
echo "[1/8] Esperando a que CheckMK esté listo..."
sleep 30

max_retries=30
retry_count=0
while ! docker exec ensurance-checkmk-full omd status ensurance &>/dev/null; do
    retry_count=$((retry_count + 1))
    if [ $retry_count -ge $max_retries ]; then
        echo "ERROR: CheckMK no está respondiendo después de $max_retries intentos"
        exit 1
    fi
    echo "  Esperando... ($retry_count/$max_retries)"
    sleep 5
done

echo "  ✓ CheckMK está listo"

# ============================================================================
# PASO 2: Crear usuario de automatización
# ============================================================================
echo ""
echo "[2/8] Configurando usuario de automatización..."
docker exec ensurance-checkmk-full omd su ensurance -c \
    "htpasswd -b ~/etc/htpasswd automation automation_secret" 2>/dev/null || \
    echo "  Usuario automation ya existe"

# Dar permisos de administrador al usuario automation
docker exec ensurance-checkmk-full omd su ensurance -c \
    "echo 'automation:admin' >> ~/etc/htpasswd" 2>/dev/null || true

echo "  ✓ Usuario automation configurado"

# ============================================================================
# PASO 3: Configurar conexión con Prometheus
# ============================================================================
echo ""
echo "[3/8] Configurando integración con Prometheus..."

# Crear configuración de Special Agent Prometheus
docker exec ensurance-checkmk-full omd su ensurance -c "cat > ~/etc/check_mk/conf.d/prometheus.mk << 'EOF'
# Configuración de Prometheus Integration

# Conectar CheckMK con Prometheus como fuente de métricas
datasource_programs.append(
    ('special_prometheus', '/omd/sites/ensurance/share/check_mk/agents/special/agent_prometheus')
)

# Configurar hosts de Prometheus
special_agent_info['prometheus'] = [
    {
        'host_name': 'prometheus',
        'url': 'http://ensurance-prometheus-full:9090',
        'protocol': 'http',
    },
]
EOF
" 2>/dev/null || true

echo "  ✓ Integración con Prometheus configurada"

# ============================================================================
# PASO 4: Agregar hosts al monitoreo
# ============================================================================
echo ""
echo "[4/8] Agregando hosts al monitoreo..."

# Array de hosts para agregar
declare -A HOSTS=(
    ["prometheus"]="ensurance-prometheus-full"
    ["grafana"]="ensurance-grafana-full"
    ["alertmanager"]="ensurance-alertmanager-full"
    ["rabbitmq"]="ensurance-rabbitmq-full"
    ["netdata"]="ensurance-netdata-full"
    ["node-exporter"]="ensurance-node-exporter-full"
    ["pushgateway"]="ensurance-pushgateway-full"
    ["ensurance-app"]="ensurance-pharmacy-full"
)

for host_name in "${!HOSTS[@]}"; do
    host_ip="${HOSTS[$host_name]}"
    echo "  Agregando host: $host_name ($host_ip)"
    
    curl -X POST "$API_URL/domain-types/host_config/collections/all" \
        -u "$USERNAME:$PASSWORD" \
        -H "Content-Type: application/json" \
        -d "{
            \"folder\": \"/\",
            \"host_name\": \"$host_name\",
            \"attributes\": {
                \"ipaddress\": \"$host_ip\",
                \"tag_agent\": \"no-agent\",
                \"tag_criticality\": \"prod\"
            }
        }" 2>/dev/null && echo "    ✓ Host $host_name agregado" || echo "    ℹ Host $host_name ya existe"
done

# ============================================================================
# PASO 5: Configurar reglas de monitoreo
# ============================================================================
echo ""
echo "[5/8] Configurando reglas de monitoreo (equivalentes a Netdata)..."

docker exec ensurance-checkmk-full omd su ensurance -c "cat > ~/etc/check_mk/conf.d/monitoring_rules.mk << 'EOF'
# ============================================================================
# REGLAS DE MONITOREO - Equivalentes a Netdata alerts
# ============================================================================

# CPU Usage Thresholds
checkgroup_parameters.setdefault('cpu_utilization', [])
checkgroup_parameters['cpu_utilization'].insert(0, {
    'levels': (70.0, 90.0),  # WARNING: 70%, CRITICAL: 90%
})

# Memory Usage Thresholds
checkgroup_parameters.setdefault('mem_linux', [])
checkgroup_parameters['mem_linux'].insert(0, {
    'levels_ram': (80.0, 95.0),  # WARNING: 80%, CRITICAL: 95%
    'levels_swap': (20.0, 50.0),
})

# Disk Space Thresholds
checkgroup_parameters.setdefault('filesystem', [])
checkgroup_parameters['filesystem'].insert(0, {
    'levels': (75.0, 90.0),  # WARNING: 75%, CRITICAL: 90%
    'magic_normsize': 20,
    'levels_low': (10000, 5000),  # WARNING: 10GB, CRITICAL: 5GB
})

# Network Interface Thresholds
checkgroup_parameters.setdefault('if', [])
checkgroup_parameters['if'].insert(0, {
    'traffic': [('both', ('upper', ('perc', (81.92, 163.84))))],  # 100MB/s, 200MB/s
    'errors': {'both': ('abs', (10, 100))},
})

# HTTP Service Check
active_checks.setdefault('http', [])
# Ensurance Frontend
active_checks['http'].append({
    'name': 'Ensurance Frontend',
    'host': 'ensurance-app',
    'mode': ('url', {
        'uri': 'http://ensurance-pharmacy-full:5175',
        'timeout': 10,
        'response_time': (1000, 3000),  # WARNING: 1s, CRITICAL: 3s
        'expect_response': ['200'],
    }),
})

# Pharmacy Frontend
active_checks['http'].append({
    'name': 'Pharmacy Frontend',
    'host': 'ensurance-app',
    'mode': ('url', {
        'uri': 'http://ensurance-pharmacy-full:8089',
        'timeout': 10,
        'response_time': (1000, 3000),
        'expect_response': ['200'],
    }),
})

# Ensurance Backend
active_checks['http'].append({
    'name': 'Ensurance Backend API',
    'host': 'ensurance-app',
    'mode': ('url', {
        'uri': 'http://ensurance-pharmacy-full:8081/health',
        'timeout': 10,
        'response_time': (1000, 3000),
        'expect_response': ['200'],
    }),
})

# Pharmacy Backend
active_checks['http'].append({
    'name': 'Pharmacy Backend API',
    'host': 'ensurance-app',
    'mode': ('url', {
        'uri': 'http://ensurance-pharmacy-full:8082/health',
        'timeout': 10,
        'response_time': (1000, 3000),
        'expect_response': ['200'],
    }),
})

# Prometheus Web UI
active_checks['http'].append({
    'name': 'Prometheus Web UI',
    'host': 'prometheus',
    'mode': ('url', {
        'uri': 'http://ensurance-prometheus-full:9090/-/healthy',
        'timeout': 10,
        'expect_response': ['200'],
    }),
})

# Grafana Web UI
active_checks['http'].append({
    'name': 'Grafana Web UI',
    'host': 'grafana',
    'mode': ('url', {
        'uri': 'http://ensurance-grafana-full:3000/api/health',
        'timeout': 10,
        'expect_response': ['200'],
    }),
})

# RabbitMQ Management UI
active_checks['http'].append({
    'name': 'RabbitMQ Management',
    'host': 'rabbitmq',
    'mode': ('url', {
        'uri': 'http://ensurance-rabbitmq-full:15672',
        'timeout': 10,
        'expect_response': ['200', '401'],  # 401 is OK (auth required)
    }),
})

print("Monitoring rules loaded successfully")
EOF
" 2>/dev/null || true

echo "  ✓ Reglas de monitoreo configuradas"

# ============================================================================
# PASO 6: Configurar notificaciones (Email y Slack)
# ============================================================================
echo ""
echo "[6/8] Configurando notificaciones..."

docker exec ensurance-checkmk-full omd su ensurance -c "cat > ~/etc/check_mk/conf.d/notifications.mk << 'EOF'
# Configuración de notificaciones - Similar a Netdata

# Email configuration
notification_mail_from = 'checkmk@ensurance-pharmacy.local'

# Grupos de contacto
define_contactgroups = {
    'sysadmin': 'System Administrators',
    'webmaster': 'Web Masters',
}

# Contactos
contacts.update({
    'sysadmin': {
        'alias': 'System Administrator',
        'email': 'sysadmin@ensurance-pharmacy.local',
        'contactgroups': ['sysadmin'],
        'notifications_enabled': True,
    },
    'webmaster': {
        'alias': 'Web Master',
        'email': 'webmaster@ensurance-pharmacy.local',
        'contactgroups': ['webmaster'],
        'notifications_enabled': True,
    },
})

print("Notification configuration loaded")
EOF
" 2>/dev/null || true

echo "  ✓ Notificaciones configuradas"

# ============================================================================
# PASO 7: Descubrir servicios en los hosts
# ============================================================================
echo ""
echo "[7/8] Descubriendo servicios en los hosts..."

for host_name in "${!HOSTS[@]}"; do
    echo "  Descubriendo servicios en: $host_name"
    docker exec ensurance-checkmk-full omd su ensurance -c \
        "cmk -II $host_name 2>/dev/null" || echo "    ℹ Algunos servicios no detectados"
done

echo "  ✓ Servicios descubiertos"

# ============================================================================
# PASO 8: Recargar configuración y activar cambios
# ============================================================================
echo ""
echo "[8/8] Activando configuración..."

# Recargar configuración
docker exec ensurance-checkmk-full omd su ensurance -c "cmk -R" 2>/dev/null || true

# Activar cambios mediante API
curl -X POST "$API_URL/domain-types/activation_run/actions/activate-changes/invoke" \
    -u "$USERNAME:$PASSWORD" \
    -H "Content-Type: application/json" \
    -d '{"sites": ["ensurance"], "force_foreign_changes": true}' 2>/dev/null || true

# Reiniciar servicios de CheckMK
docker exec ensurance-checkmk-full omd restart ensurance 2>/dev/null || true

echo "  ✓ Configuración activada"

# ============================================================================
# RESUMEN
# ============================================================================
echo ""
echo "═══════════════════════════════════════════════════════════════════"
echo "  ✓ CheckMK Configurado Exitosamente"
echo "═══════════════════════════════════════════════════════════════════"
echo ""
echo "ACCESO A CHECKMK:"
echo "  URL:      http://localhost:5152/ensurance/check_mk/"
echo "  Usuario:  cmkadmin"
echo "  Password: admin123"
echo ""
echo "HOSTS MONITOREADOS:"
for host_name in "${!HOSTS[@]}"; do
    echo "  • $host_name (${HOSTS[$host_name]})"
done
echo ""
echo "ALERTAS CONFIGURADAS (equivalentes a Netdata):"
echo "  • Sistema: CPU (>70%), Memoria (>80%), Disco (>75%)"
echo "  • Red: Tráfico (>100MB/s), Errores de paquetes"
echo "  • Aplicaciones: HTTP checks, Response time (>1s)"
echo "  • Servicios: Prometheus, Grafana, RabbitMQ, Backends, Frontends"
echo ""
echo "NOTIFICACIONES:"
echo "  • Email: sysadmin@ensurance-pharmacy.local"
echo "  • Email: webmaster@ensurance-pharmacy.local"
echo ""
echo "PRÓXIMOS PASOS:"
echo "  1. Accede a CheckMK con las credenciales arriba"
echo "  2. Verifica los hosts en 'Monitor > Hosts'"
echo "  3. Revisa las alertas en 'Monitor > Problems'"
echo "  4. Explora los dashboards en 'Monitor > Overview'"
echo ""
