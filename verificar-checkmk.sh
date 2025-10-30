#!/bin/bash

# Script para verificar que CheckMK está funcionando correctamente

echo "═══════════════════════════════════════════════════════════════════"
echo "  Verificando CheckMK"
echo "═══════════════════════════════════════════════════════════════════"

echo ""
echo "[1/5] Verificando contenedor..."
if docker ps | grep -q ensurance-checkmk-full; then
    echo "  ✅ Contenedor está corriendo"
    
    # Verificar health status
    health=$(docker inspect ensurance-checkmk-full --format='{{.State.Health.Status}}' 2>/dev/null)
    if [ "$health" = "healthy" ]; then
        echo "  ✅ Estado: HEALTHY"
    elif [ "$health" = "starting" ]; then
        echo "  ⚠️  Estado: STARTING (espera 1-2 minutos más)"
    else
        echo "  ⚠️  Estado: $health"
    fi
else
    echo "  ❌ Contenedor NO está corriendo"
    echo "     Ejecuta: docker compose -f docker-compose.full.yml up -d checkmk"
    exit 1
fi

echo ""
echo "[2/5] Verificando puerto web..."
if curl -s -o /dev/null -w "%{http_code}" http://localhost:5152 | grep -q "200\|302"; then
    echo "  ✅ Puerto 5152 responde correctamente"
else
    echo "  ⚠️  Puerto 5152 no responde aún (espera 1-2 minutos)"
fi

echo ""
echo "[3/5] Verificando sitio OMD..."
site_status=$(docker exec ensurance-checkmk-full omd status ensurance 2>&1 | grep "Overall state")
if echo "$site_status" | grep -q "running"; then
    echo "  ✅ Sitio 'ensurance' está corriendo"
else
    echo "  ⚠️  Sitio 'ensurance' no está listo"
    echo "     Estado: $site_status"
fi

echo ""
echo "[4/5] Verificando servicios internos..."
docker exec ensurance-checkmk-full omd status ensurance 2>&1 | grep -E "apache|nagios|redis" | while read line; do
    if echo "$line" | grep -q "running"; then
        echo "  ✅ $line"
    else
        echo "  ⚠️  $line"
    fi
done

echo ""
echo "[5/5] Verificando acceso a la API REST..."
api_response=$(curl -s -u "cmkadmin:admin123" http://localhost:5152/ensurance/check_mk/api/1.0/version 2>&1)
if echo "$api_response" | grep -q "checkmk"; then
    echo "  ✅ API REST responde correctamente"
else
    echo "  ⚠️  API REST no responde (puede ser normal si acaba de iniciar)"
fi

echo ""
echo "═══════════════════════════════════════════════════════════════════"
echo "  Resumen de Verificación"
echo "═══════════════════════════════════════════════════════════════════"

# Contar verificaciones exitosas
checks_ok=0
checks_total=5

if docker ps | grep -q ensurance-checkmk-full; then
    ((checks_ok++))
fi

if curl -s -o /dev/null -w "%{http_code}" http://localhost:5152 | grep -q "200\|302"; then
    ((checks_ok++))
fi

if docker exec ensurance-checkmk-full omd status ensurance 2>&1 | grep -q "Overall state.*running"; then
    ((checks_ok++))
fi

if docker exec ensurance-checkmk-full omd status ensurance 2>&1 | grep apache | grep -q running; then
    ((checks_ok++))
fi

if curl -s -u "cmkadmin:admin123" http://localhost:5152/ensurance/check_mk/api/1.0/version 2>&1 | grep -q "checkmk"; then
    ((checks_ok++))
fi

echo ""
echo "Verificaciones exitosas: $checks_ok/$checks_total"

if [ $checks_ok -eq $checks_total ]; then
    echo ""
    echo "🎉 ¡CheckMK está 100% funcional!"
    echo ""
    echo "✅ LISTO PARA CONFIGURAR"
    echo ""
    echo "Próximos pasos:"
    echo "  1. Abre http://localhost:5152/ensurance/check_mk/"
    echo "  2. Inicia sesión: cmkadmin / admin123"
    echo "  3. Sigue la guía: GUIA-RAPIDA-CHECKMK.md"
    echo "  4. Agrega los 8 hosts manualmente"
    echo ""
elif [ $checks_ok -ge 3 ]; then
    echo ""
    echo "⚠️  CheckMK está iniciando"
    echo ""
    echo "ESPERA 2-3 MINUTOS MÁS y luego ejecuta:"
    echo "  ./verificar-checkmk.sh"
    echo ""
else
    echo ""
    echo "❌ CheckMK tiene problemas"
    echo ""
    echo "Intenta recrear CheckMK:"
    echo "  ./recreate-checkmk-auto.sh"
    echo ""
fi

echo "═══════════════════════════════════════════════════════════════════"
