#!/bin/bash

# Script para recrear completamente CheckMK desde cero

echo "═══════════════════════════════════════════════════════════════════"
echo "  Recreando CheckMK Completamente"
echo "═══════════════════════════════════════════════════════════════════"
echo ""
echo "⚠️  ADVERTENCIA: Esto eliminará TODOS los datos de CheckMK"
echo "    El contenedor y volumen se recrearán desde cero."
echo ""
read -p "¿Continuar? (escribe 'SI' para confirmar): " confirm

if [ "$confirm" != "SI" ]; then
    echo "Operación cancelada."
    exit 0
fi

echo ""
echo "[1/6] Deteniendo CheckMK..."
docker compose -f docker-compose.full.yml stop checkmk
sleep 3

echo ""
echo "[2/6] Eliminando contenedor..."
docker compose -f docker-compose.full.yml rm -f checkmk

echo ""
echo "[3/6] Eliminando volumen corrupto..."
docker volume rm ensurance-checkmk-sites-full 2>/dev/null || echo "  (Volumen no existía o no se pudo eliminar)"

echo ""
echo "[4/6] Recreando CheckMK desde cero..."
docker compose -f docker-compose.full.yml up -d checkmk

echo ""
echo "[5/6] Esperando que CheckMK inicie (esto puede tomar 60-90 segundos)..."
echo "  Inicializando sitio 'ensurance'..."

# Esperar 90 segundos para que CheckMK se inicialice completamente
for i in {90..1}; do
    echo -ne "  Esperando... $i segundos restantes\r"
    sleep 1
done
echo -e "\n"

echo ""
echo "[6/6] Verificando estado..."
docker ps | grep checkmk

echo ""
echo "Verificando que el sitio esté activo..."
docker exec ensurance-checkmk-full omd status ensurance 2>/dev/null || echo "  (Aún inicializando...)"

echo ""
echo "═══════════════════════════════════════════════════════════════════"
echo "  ✅ CheckMK Recreado"
echo "═══════════════════════════════════════════════════════════════════"
echo ""
echo "ACCESO A CHECKMK:"
echo "  URL:      http://localhost:5152/ensurance/check_mk/"
echo "  Usuario:  cmkadmin"
echo "  Password: admin123"
echo ""
echo "PRÓXIMOS PASOS:"
echo "  1. Espera 2-3 minutos adicionales"
echo "  2. Accede a la URL de arriba"
echo "  3. Inicia sesión"
echo "  4. Ve a 'Setup > Hosts'"
echo "  5. Haz clic en 'Add host'"
echo "  6. Agrega hosts UNO POR UNO:"
echo ""
echo "     Hostname: prometheus"
echo "     Alias: Prometheus Server"
echo "     IPv4: ensurance-prometheus-full"
echo "     Agent: No agent"
echo ""
echo "  7. Guarda y repite para:"
echo "     - grafana (ensurance-grafana-full)"
echo "     - alertmanager (ensurance-alertmanager-full)"
echo "     - rabbitmq (ensurance-rabbitmq-full)"
echo "     - netdata (ensurance-netdata-full)"
echo "     - node-exporter (ensurance-node-exporter-full)"
echo "     - pushgateway (ensurance-pushgateway-full)"
echo "     - ensurance-app (ensurance-pharmacy-full)"
echo ""
echo "IMPORTANTE:"
echo "  - NO uses scripts automáticos"
echo "  - Agrega hosts manualmente desde la UI"
echo "  - Después de agregar TODOS los hosts, haz service discovery"
echo "  - Activa cambios al final"
echo ""
