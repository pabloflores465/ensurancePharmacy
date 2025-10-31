#!/bin/bash

echo "═══════════════════════════════════════════════════════════════════"
echo "  Configurando CheckMK vía API Web"
echo "═══════════════════════════════════════════════════════════════════"

URL="http://localhost:5152/ensurance/check_mk/api/1.0"
USER="cmkadmin"
PASS="admin123"

echo "Probando conexión a la API..."
response=$(curl -s -u "$USER:$PASS" "$URL/version")
echo "Respuesta: $response"

if echo "$response" | grep -q "versions"; then
    echo "✓ API funcionando correctamente"
else
    echo "✗ Error con la API"
    echo "Intentando crear usuario de automatización manual..."
    
    # Crear usuario automation via CLI
    docker exec ensurance-checkmk-full bash -c "cd /omd/sites/ensurance && htpasswd -b etc/htpasswd automation automation123"
    echo "✓ Usuario automation creado"
fi

echo ""
echo "Agregando hosts vía API..."

# Array de hosts
declare -a HOSTS=(
    "prometheus:ensurance-prometheus-full"
    "grafana:ensurance-grafana-full"
    "alertmanager:ensurance-alertmanager-full"
    "rabbitmq:ensurance-rabbitmq-full"
    "netdata:ensurance-netdata-full"
    "node-exporter:ensurance-node-exporter-full"
    "pushgateway:ensurance-pushgateway-full"
    "ensurance-app:ensurance-pharmacy-full"
)

for host_entry in "${HOSTS[@]}"; do
    IFS=':' read -r hostname ipaddr <<< "$host_entry"
    
    echo "Agregando host: $hostname ($ipaddr)"
    
    curl -X POST "$URL/domain-types/host_config/collections/all" \
        -u "$USER:$PASS" \
        -H "Accept: application/json" \
        -H "Content-Type: application/json" \
        -d "{
            \"folder\": \"/\",
            \"host_name\": \"$hostname\",
            \"attributes\": {
                \"ipaddress\": \"$ipaddr\",
                \"alias\": \"$hostname\"
            }
        }" 2>&1 | head -1
done

echo ""
echo "Activando cambios..."
curl -X POST "$URL/domain-types/activation_run/actions/activate-changes/invoke" \
    -u "$USER:$PASS" \
    -H "Content-Type: application/json" \
    -d '{"redirect": false, "sites": ["ensurance"], "force_foreign_changes": true}' 2>&1 | head -5

echo ""
echo "═══════════════════════════════════════════════════════════════════"
echo "  Configuración Completada"
echo "═══════════════════════════════════════════════════════════════════"
echo ""
echo "IMPORTANTE:"
echo "  1. Actualiza la página en el navegador (F5)"
echo "  2. Ve a 'Setup > Hosts' para ver los hosts"
echo "  3. Haz clic en 'Save & run service discovery' en cada host"
echo "  4. Luego ve a 'Monitor > All Hosts'"
echo ""
