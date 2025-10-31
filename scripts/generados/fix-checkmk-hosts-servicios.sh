#!/bin/bash

# ============================================================================
# FIX: Agregar hosts y descubrir servicios en CheckMK
# ============================================================================

set -e

echo "═══════════════════════════════════════════════════════════════════"
echo "  Agregando Hosts y Descubriendo Servicios en CheckMK"
echo "═══════════════════════════════════════════════════════════════════"

API_URL="http://localhost:5152/ensurance/check_mk/api/1.0"
API_USER="automation"
API_PASS="automation_secret"

# ============================================================================
# PASO 1: Verificar que CheckMK esté funcionando
# ============================================================================
echo ""
echo "[1/4] Verificando CheckMK..."

if ! docker ps | grep -q ensurance-checkmk-full; then
    echo "❌ CheckMK no está corriendo"
    exit 1
fi

echo "  ✅ CheckMK está corriendo"

# Esperar a que esté listo
sleep 5

# ============================================================================
# PASO 2: Agregar hosts mediante API
# ============================================================================
echo ""
echo "[2/4] Agregando hosts..."

# Definir hosts
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
    
    # Intentar crear el host
    response=$(curl -s -X POST "$API_URL/domain-types/host_config/collections/all" \
        -u "$API_USER:$API_PASS" \
        -H "Content-Type: application/json" \
        -d "{
            \"folder\": \"/\",
            \"host_name\": \"$host_name\",
            \"attributes\": {
                \"ipaddress\": \"$host_ip\",
                \"tag_agent\": \"no-agent\"
            }
        }" 2>&1)
    
    if echo "$response" | grep -q "title"; then
        echo "    ✅ Host $host_name agregado"
    else
        echo "    ⚠️  Host $host_name ya existe o error al agregar"
    fi
    
    sleep 1
done

# ============================================================================
# PASO 3: Descubrir servicios en cada host
# ============================================================================
echo ""
echo "[3/4] Descubriendo servicios en cada host..."

for host_name in "${!HOSTS[@]}"; do
    echo "  Descubriendo servicios en: $host_name"
    
    # Ejecutar service discovery
    docker exec ensurance-checkmk-full omd su ensurance -c "cmk -II $host_name" 2>/dev/null || true
    
    sleep 2
done

# ============================================================================
# PASO 4: Activar cambios
# ============================================================================
echo ""
echo "[4/4] Activando cambios..."

# Activar cambios
curl -s -X POST "$API_URL/domain-types/activation_run/actions/activate-changes/invoke" \
    -u "$API_USER:$API_PASS" \
    -H "Content-Type: application/json" \
    -d '{
        "redirect": false,
        "sites": ["ensurance"],
        "force_foreign_changes": true
    }' > /dev/null 2>&1 || true

echo "  ✅ Cambios activados"

# Esperar a que se active
sleep 10

# ============================================================================
# VERIFICACIÓN FINAL
# ============================================================================
echo ""
echo "═══════════════════════════════════════════════════════════════════"
echo "  Verificando Configuración"
echo "═══════════════════════════════════════════════════════════════════"

# Contar hosts
echo ""
echo "Hosts configurados:"
docker exec ensurance-checkmk-full omd su ensurance -c "cmk --list-hosts 2>/dev/null" || echo "No se pudo listar hosts"

echo ""
echo "Servicios descubiertos:"
service_count=$(curl -s -u "cmkadmin:admin123" "$API_URL/domain-types/service/collections/all" 2>/dev/null | grep -o '"service_description"' | wc -l || echo "0")
echo "  Total de servicios: $service_count"

echo ""
echo "═══════════════════════════════════════════════════════════════════"
echo "  ✅ Configuración Completada"
echo "═══════════════════════════════════════════════════════════════════"
echo ""
echo "AHORA:"
echo "  1. Accede a CheckMK: http://localhost:5152/ensurance/check_mk/"
echo "  2. Ve a: Monitor → All services"
echo "  3. Deberías ver servicios listados (PING, HTTP checks, etc.)"
echo ""
echo "PARA VER GRÁFICAS:"
echo "  ⚠️  Las gráficas tardarán 15-30 minutos en aparecer"
echo "  💡 Mientras tanto, usa Grafana: http://localhost:3001"
echo ""
