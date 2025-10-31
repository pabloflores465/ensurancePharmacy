#!/bin/bash

# ============================================================================
# FORZAR DESCUBRIMIENTO DE SERVICIOS EN CHECKMK
# ============================================================================

echo "═══════════════════════════════════════════════════════════════════"
echo "  Forzando Descubrimiento de Servicios en CheckMK"
echo "═══════════════════════════════════════════════════════════════════"

API_URL="http://localhost:5152/ensurance/check_mk/api/1.0"
USER="cmkadmin"
PASS="admin123"

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

echo ""
echo "[1/3] Ejecutando Full Scan en todos los hosts..."

for host in "${HOSTS[@]}"; do
    echo "  🔍 Escaneando: $host"
    
    # Ejecutar full discovery
    curl -s -X POST "$API_URL/objects/host/$host/actions/discover_services/invoke" \
        -u "$USER:$PASS" \
        -H "Content-Type: application/json" \
        -d '{
            "mode": "refresh"
        }' > /dev/null 2>&1
    
    sleep 2
done

echo ""
echo "[2/3] Activando todos los cambios..."

# Activar cambios
activation=$(curl -s -X POST "$API_URL/domain-types/activation_run/actions/activate-changes/invoke" \
    -u "$USER:$PASS" \
    -H "Content-Type: application/json" \
    -d '{
        "redirect": false,
        "sites": ["ensurance"],
        "force_foreign_changes": true
    }')

if echo "$activation" | grep -q '"id"'; then
    echo "  ✅ Activación iniciada"
    echo "  ⏳ Esperando que termine (60 segundos)..."
    
    for i in {1..60}; do
        echo -ne "     Progreso: $i/60 segundos\r"
        sleep 1
    done
    echo ""
    echo "  ✅ Activación completada"
else
    echo "  ⚠️  No había cambios pendientes o error en activación"
fi

echo ""
echo "[3/3] Verificando servicios descubiertos..."

# Esperar un poco más
sleep 5

# Contar servicios
service_count=$(curl -s -u "$USER:$PASS" "$API_URL/domain-types/service/collections/all" 2>/dev/null | grep -o '"service_description"' | wc -l)

echo "  📊 Servicios encontrados: $service_count"

if [ "$service_count" -gt 0 ]; then
    echo ""
    echo "  ✅ ¡Servicios descubiertos exitosamente!"
    echo ""
    echo "  Lista de algunos servicios:"
    curl -s -u "$USER:$PASS" "$API_URL/domain-types/service/collections/all" 2>/dev/null | \
        grep -o '"service_description":"[^"]*"' | head -10 | sed 's/"service_description":"/  - /' | sed 's/"$//'
else
    echo ""
    echo "  ⚠️  Aún no hay servicios descubiertos"
    echo ""
    echo "  POSIBLES CAUSAS:"
    echo "    1. Los hosts no están respondiendo a PING"
    echo "    2. CheckMK necesita más tiempo (espera 5-10 minutos)"
    echo "    3. Los contenedores no son accesibles desde CheckMK"
fi

echo ""
echo "═══════════════════════════════════════════════════════════════════"
echo "  Verificación Completa"
echo "═══════════════════════════════════════════════════════════════════"
echo ""
echo "ACCEDE AHORA A CHECKMK:"
echo "  URL: http://localhost:5152/ensurance/check_mk/"
echo "  Usuario: cmkadmin"
echo "  Password: admin123"
echo ""
echo "QUÉ VERIFICAR EN LA WEBUI:"
echo "  1. Ve a: Monitor → All hosts"
echo "     - Verás 8 hosts listados"
echo "     - Algunos pueden estar en estado PENDING (normal al inicio)"
echo ""
echo "  2. Ve a: Monitor → All services"
echo "     - Deberías ver servicios (PING, Check_MK, etc.)"
echo "     - Algunos pueden estar en estado PENDING (normal al inicio)"
echo ""
echo "  3. Para ver dashboards:"
echo "     - Monitor → Dashboards"
echo "     - Selecciona cualquier dashboard creado"
echo ""
echo "⏱️  IMPORTANTE:"
echo "  - Los hosts pueden estar en estado PENDING por 1-5 minutos"
echo "  - Los servicios se descubren gradualmente"
echo "  - Las GRÁFICAS tardan 15-30 minutos en aparecer"
echo ""
echo "💡 MIENTRAS TANTO:"
echo "  - USA GRAFANA para gráficas: http://localhost:3001"
echo "  - USA NETDATA para tiempo real: http://localhost:19999"
echo ""
