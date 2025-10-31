#!/bin/bash

# Script para descubrir servicios y habilitar gráficas en CheckMK

echo "═══════════════════════════════════════════════════════════════════"
echo "  Habilitando Servicios y Gráficas en CheckMK"
echo "═══════════════════════════════════════════════════════════════════"

URL="http://localhost:5152/ensurance/check_mk/api/1.0"
USER="cmkadmin"
PASS="admin123"

echo ""
echo "[1/4] Verificando hosts configurados..."

hosts=$(curl -s -X GET "$URL/domain-types/host_config/collections/all" \
    -u "$USER:$PASS" \
    -H "Accept: application/json" 2>&1)

host_count=$(echo "$hosts" | grep -o '"id"' | wc -l)
echo "  ✅ Hosts encontrados: $host_count"

if [ "$host_count" -lt 8 ]; then
    echo "  ⚠️  Deberían haber 8 hosts. Ejecuta primero: ./agregar-hosts-api.sh"
    exit 1
fi

echo ""
echo "[2/4] Ejecutando Service Discovery en todos los hosts..."

# Lista de hosts
declare -a HOSTS=(
    "prometheus"
    "grafana"
    "alertmanager"
    "rabbitmq"
    "netdata"
    "node-exporter"
    "pushgateway"
    "ensurance-app"
)

for hostname in "${HOSTS[@]}"; do
    echo ""
    echo "  Descubriendo servicios en: $hostname"
    
    # Iniciar service discovery
    discovery=$(curl -s -X POST "$URL/objects/host/$hostname/actions/discover_services/invoke" \
        -u "$USER:$PASS" \
        -H "Content-Type: application/json" \
        -d '{
            "mode": "new"
        }' 2>&1)
    
    if echo "$discovery" | grep -q '"title"'; then
        # Extraer servicios encontrados
        services=$(echo "$discovery" | grep -o '"value"' | wc -l)
        echo "    ✅ Servicios encontrados: $services"
    else
        echo "    ⚠️  No se encontraron servicios automáticamente"
    fi
    
    sleep 1
done

echo ""
echo "[3/4] Activando cambios..."

# Activar cambios
activation=$(curl -s -X POST "$URL/domain-types/activation_run/actions/activate-changes/invoke" \
    -u "$USER:$PASS" \
    -H "Content-Type: application/json" \
    -d '{
        "redirect": false,
        "sites": ["ensurance"],
        "force_foreign_changes": true
    }' 2>&1)

if echo "$activation" | grep -q '"id"'; then
    activation_id=$(echo "$activation" | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
    echo "  ✅ Activación iniciada (ID: $activation_id)"
    
    # Esperar que termine
    echo "  ⏳ Esperando activación..."
    for i in {1..60}; do
        status=$(curl -s -X GET "$URL/objects/activation_run/$activation_id" \
            -u "$USER:$PASS" \
            -H "Accept: application/json" 2>&1)
        
        if echo "$status" | grep -q '"state":"finished"'; then
            echo "  ✅ Activación completada"
            break
        else
            echo -ne "    Esperando... $i segundos\r"
            sleep 1
        fi
    done
else
    echo "  ⚠️  Activa los cambios manualmente desde la UI"
fi

echo ""
echo "[4/4] Verificando servicios activos..."

# Listar servicios activos
services=$(curl -s -X GET "$URL/domain-types/service/collections/all" \
    -u "$USER:$PASS" \
    -H "Accept: application/json" 2>&1)

service_count=$(echo "$services" | grep -o '"service_description"' | wc -l)
echo "  ✅ Servicios activos: $service_count"

echo ""
echo "═══════════════════════════════════════════════════════════════════"
echo "  ✅ Servicios Descubiertos y Activados"
echo "═══════════════════════════════════════════════════════════════════"
echo ""
echo "AHORA PARA VER LAS GRÁFICAS:"
echo ""
echo "1. Accede a CheckMK:"
echo "   http://localhost:5152/ensurance/check_mk/"
echo ""
echo "2. Ve a 'Monitor > All services'"
echo "   - Deberías ver $service_count servicios"
echo ""
echo "3. Haz clic en cualquier servicio"
echo "   - Verás detalles del servicio"
echo "   - Las gráficas aparecerán en la sección 'Metrics'"
echo ""
echo "⚠️  IMPORTANTE SOBRE LAS GRÁFICAS:"
echo ""
echo "Las gráficas TARDAN en aparecer porque:"
echo ""
echo "  📊 CheckMK necesita DATOS HISTÓRICOS:"
echo "     - Primer check: Inmediato"
echo "     - Primera gráfica: 15-30 minutos"
echo "     - Gráficas completas: 1-2 horas"
echo ""
echo "  📈 Los servicios se monitorean cada:"
echo "     - PING: 1 minuto"
echo "     - HTTP checks: 1-2 minutos"
echo "     - Otros checks: 5 minutos"
echo ""
echo "  💡 MIENTRAS TANTO:"
echo "     - Usa NETDATA para gráficas en tiempo real"
echo "     - http://localhost:19999"
echo "     - Netdata tiene gráficas inmediatas"
echo ""
echo "  🎯 CheckMK ES PARA:"
echo "     - Monitoreo enterprise"
echo "     - Alertas organizadas"
echo "     - Historial extendido"
echo "     - Reportes y SLAs"
echo ""
echo "  🎯 NETDATA ES PARA:"
echo "     - Gráficas en tiempo real"
echo "     - Métricas cada segundo"
echo "     - Troubleshooting inmediato"
echo ""
echo "VERIFICA AHORA:"
echo "  1. Ve a 'Monitor > All services'"
echo "  2. Verifica que hay servicios con estado OK"
echo "  3. Espera 15-30 minutos para las primeras gráficas"
echo "  4. O usa Netdata para gráficas inmediatas"
echo ""
