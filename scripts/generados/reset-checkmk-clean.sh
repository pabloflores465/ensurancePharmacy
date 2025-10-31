#!/bin/bash

# Script para resetear CheckMK completamente y empezar desde cero

echo "═══════════════════════════════════════════════════════════════════"
echo "  Reset Completo de CheckMK - Empezar desde Cero"
echo "═══════════════════════════════════════════════════════════════════"
echo ""
echo "⚠️  ADVERTENCIA: Esto eliminará TODA la configuración de CheckMK"
echo "    y tendrás que configurar todo desde la web UI manualmente."
echo ""
read -p "¿Estás seguro? (escribe 'SI' para continuar): " confirm

if [ "$confirm" != "SI" ]; then
    echo "Operación cancelada."
    exit 0
fi

echo ""
echo "[1/5] Deteniendo CheckMK..."
docker exec ensurance-checkmk-full omd stop ensurance 2>/dev/null
sleep 2

echo ""
echo "[2/5] Eliminando configuraciones..."
docker exec ensurance-checkmk-full rm -rf /omd/sites/ensurance/etc/check_mk/conf.d/*.mk 2>/dev/null
docker exec ensurance-checkmk-full rm -rf /omd/sites/ensurance/etc/check_mk/conf.d/wato/* 2>/dev/null
docker exec ensurance-checkmk-full rm -rf /omd/sites/ensurance/var/check_mk/core/* 2>/dev/null
docker exec ensurance-checkmk-full rm -rf /omd/sites/ensurance/var/check_mk/wato/* 2>/dev/null

echo "  ✓ Configuraciones eliminadas"

echo ""
echo "[3/5] Creando archivos básicos..."
# Crear archivos mínimos necesarios
docker exec ensurance-checkmk-full bash -c "cat > /omd/sites/ensurance/etc/check_mk/conf.d/mkeventd.mk << 'EOF'
# Event Console configuration
EOF
"

docker exec ensurance-checkmk-full bash -c "cat > /omd/sites/ensurance/etc/check_mk/conf.d/pnp4nagios.mk << 'EOF'
# PNP4Nagios configuration
EOF
"

echo "  ✓ Archivos básicos creados"

echo ""
echo "[4/5] Ajustando permisos..."
docker exec ensurance-checkmk-full chown -R ensurance:ensurance /omd/sites/ensurance/etc/check_mk/ 2>/dev/null
docker exec ensurance-checkmk-full chown -R ensurance:ensurance /omd/sites/ensurance/var/check_mk/ 2>/dev/null

echo "  ✓ Permisos ajustados"

echo ""
echo "[5/5] Reiniciando CheckMK..."
docker exec ensurance-checkmk-full omd start ensurance 2>/dev/null
sleep 5

echo ""
echo "═══════════════════════════════════════════════════════════════════"
echo "  ✅ CheckMK Reseteado Completamente"
echo "═══════════════════════════════════════════════════════════════════"
echo ""
echo "CheckMK está ahora en estado limpio."
echo ""
echo "PRÓXIMOS PASOS:"
echo "  1. Lee la guía: CONFIGURACION-MANUAL-CHECKMK.md"
echo "  2. Accede a: http://localhost:5152/ensurance/check_mk/"
echo "  3. Usuario: cmkadmin / Password: admin123"
echo "  4. Sigue la guía paso a paso para agregar hosts manualmente"
echo ""
echo "IMPORTANTE:"
echo "  - NO uses los scripts automáticos de Prometheus"
echo "  - Configura TODO desde la web UI"
echo "  - Agrega hosts uno por uno"
echo "  - Activa cambios después de cada host"
echo ""
