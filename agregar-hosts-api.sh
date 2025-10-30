#!/bin/bash

# Script para agregar hosts a CheckMK usando la API REST

echo "═══════════════════════════════════════════════════════════════════"
echo "  Agregando Hosts a CheckMK vía API REST"
echo "═══════════════════════════════════════════════════════════════════"

URL="http://localhost:5152/ensurance/check_mk/api/1.0"
USER="cmkadmin"
PASS="admin123"

# Array de hosts
declare -a HOSTS=(
    "prometheus:Prometheus Server:ensurance-prometheus-full"
    "grafana:Grafana Dashboard:ensurance-grafana-full"
    "alertmanager:Alert Manager:ensurance-alertmanager-full"
    "rabbitmq:RabbitMQ:ensurance-rabbitmq-full"
    "netdata:Netdata Monitoring:ensurance-netdata-full"
    "node-exporter:Node Exporter:ensurance-node-exporter-full"
    "pushgateway:Pushgateway:ensurance-pushgateway-full"
    "ensurance-app:Ensurance App:ensurance-pharmacy-full"
)

echo ""
echo "Agregando hosts..."

count=0
for host_entry in "${HOSTS[@]}"; do
    IFS=':' read -r hostname alias ipaddr <<< "$host_entry"
    
    ((count++))
    echo ""
    echo "[$count/8] Agregando: $hostname ($alias)"
    
    response=$(curl -s -X POST "$URL/domain-types/host_config/collections/all" \
        -u "$USER:$PASS" \
        -H "Accept: application/json" \
        -H "Content-Type: application/json" \
        -d "{
            \"folder\": \"/\",
            \"host_name\": \"$hostname\",
            \"attributes\": {
                \"ipaddress\": \"$ipaddr\",
                \"alias\": \"$alias\",
                \"tag_agent\": \"no-agent\"
            }
        }" 2>&1)
    
    if echo "$response" | grep -q "\"id\": \"$hostname\""; then
        echo "  ✅ Host agregado correctamente"
    elif echo "$response" | grep -q "already exists"; then
        echo "  ⚠️  Host ya existe (OK)"
    else
        echo "  ⚠️  Respuesta: $(echo $response | head -c 100)"
    fi
    
    sleep 0.5
done

echo ""
echo "═══════════════════════════════════════════════════════════════════"
echo "  Hosts Agregados - Ahora Activando Cambios"
echo "═══════════════════════════════════════════════════════════════════"

echo ""
echo "Obteniendo cambios pendientes..."
changes=$(curl -s -X GET "$URL/domain-types/activation_run/collections/pending_changes" \
    -u "$USER:$PASS" \
    -H "Accept: application/json" 2>&1)

if echo "$changes" | grep -q "\"id\""; then
    echo "  ✅ Hay cambios pendientes para activar"
    
    echo ""
    echo "Activando cambios (esto puede tomar 30-60 segundos)..."
    
    activation=$(curl -s -X POST "$URL/domain-types/activation_run/actions/activate-changes/invoke" \
        -u "$USER:$PASS" \
        -H "Content-Type: application/json" \
        -d '{
            "redirect": false,
            "sites": ["ensurance"],
            "force_foreign_changes": true
        }' 2>&1)
    
    if echo "$activation" | grep -q "\"id\""; then
        activation_id=$(echo "$activation" | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
        echo "  ✅ Activación iniciada (ID: $activation_id)"
        
        echo ""
        echo "Esperando que termine la activación..."
        
        # Esperar hasta 60 segundos
        for i in {1..60}; do
            status=$(curl -s -X GET "$URL/objects/activation_run/$activation_id" \
                -u "$USER:$PASS" \
                -H "Accept: application/json" 2>&1)
            
            if echo "$status" | grep -q '"state":"finished"'; then
                echo "  ✅ Activación completada exitosamente"
                break
            elif echo "$status" | grep -q '"state":"running"'; then
                echo -ne "  ⏳ Activando... $i segundos\r"
                sleep 1
            else
                echo "  ⚠️  Estado: $(echo $status | grep -o '"state":"[^"]*"' | cut -d'"' -f4)"
                sleep 1
            fi
        done
        
    else
        echo "  ⚠️  No se pudo activar. Respuesta:"
        echo "$activation" | head -c 200
        echo ""
        echo ""
        echo "SOLUCIÓN: Activa los cambios manualmente desde la UI:"
        echo "  1. Ve a http://localhost:5152/ensurance/check_mk/"
        echo "  2. Haz clic en el banner amarillo 'X changes'"
        echo "  3. Haz clic en 'Activate affected'"
    fi
else
    echo "  ℹ️  No hay cambios pendientes (los hosts ya estaban configurados)"
fi

echo ""
echo "═══════════════════════════════════════════════════════════════════"
echo "  ✅ Proceso Completado"
echo "═══════════════════════════════════════════════════════════════════"
echo ""
echo "VERIFICA EN LA WEB UI:"
echo "  1. Abre http://localhost:5152/ensurance/check_mk/"
echo "  2. Ve a 'Setup > Hosts'"
echo "  3. Deberías ver los 8 hosts"
echo ""
echo "SI LOS VES:"
echo "  ✅ Continúa con Service Discovery:"
echo "     - Selecciona todos los hosts"
echo "     - Haz clic en 'Bulk discovery'"
echo "     - Selecciona 'Full scan' y 'Start'"
echo ""
echo "SI NO LOS VES:"
echo "  ⚠️  Refresca la página (F5) y vuelve a intentar"
echo ""
