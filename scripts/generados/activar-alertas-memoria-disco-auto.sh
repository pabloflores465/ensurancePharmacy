#!/bin/bash

# Script para disparar alertas de memoria y disco AUTOMÁTICAMENTE
# Sin pedir confirmación - Ejecución directa

PROMETHEUS_URL="http://localhost:9090"
ALERTMANAGER_URL="http://localhost:9094"

echo "=========================================="
echo "🚨 ACTIVANDO ALERTAS DE MEMORIA Y DISCO"
echo "=========================================="
echo "$(date '+%Y-%m-%d %H:%M:%S')"
echo ""

# Verificar si stress-ng está instalado
if ! command -v stress-ng &> /dev/null; then
    echo "⚠️  Instalando stress-ng..."
    sudo apt-get update -qq && sudo apt-get install -y stress-ng -qq
fi

echo "==========================================="
echo "PASO 1: GENERANDO ALTA MEMORIA RAM"
echo "==========================================="
echo ""

# Calcular memoria a usar (92% de la RAM total)
TOTAL_MEM=$(free -m | awk '/^Mem:/{print $2}')
TARGET_MEM=$(( TOTAL_MEM * 92 / 100 ))

echo "💾 Memoria total: ${TOTAL_MEM}MB"
echo "💾 Objetivo: ${TARGET_MEM}MB (92%)"
echo ""

echo "🔥 Iniciando stress de memoria..."
stress-ng --vm 4 --vm-bytes ${TARGET_MEM}M --timeout 240s --vm-method all --verify > /dev/null 2>&1 &
STRESS_PID=$!

echo "✅ Stress de memoria iniciado (PID: $STRESS_PID)"
echo "   Duración: 4 minutos"
echo ""

# Esperar 20 segundos
echo "⏱️  Esperando 20s para que suba la memoria..."
sleep 20

CURRENT_MEM=$(free | awk '/^Mem:/ {printf "%.0f", $3/$2 * 100}')
echo "   Uso actual: ${CURRENT_MEM}%"
echo ""

echo "==========================================="
echo "PASO 2: GENERANDO ALTO USO DE DISCO"
echo "==========================================="
echo ""

DISK_USED=$(df / | awk 'NR==2 {print $5}' | sed 's/%//')
DISK_AVAIL=$(df -BG / | awk 'NR==2 {print $4}' | sed 's/G//')
DISK_TOTAL=$(df -BG / | awk 'NR==2 {print $2}' | sed 's/G//')

echo "💿 Disco: ${DISK_USED}% usado, ${DISK_AVAIL}GB disponibles"
echo ""

# Crear archivo para alcanzar 85% de uso
if [ "$DISK_USED" -lt 85 ]; then
    TARGET_PERCENT=86
    CURRENT_USED_GB=$(df -BG / | awk 'NR==2 {print $3}' | sed 's/G//')
    TARGET_USED_GB=$(( DISK_TOTAL * TARGET_PERCENT / 100 ))
    NEED_GB=$(( TARGET_USED_GB - CURRENT_USED_GB ))
    
    # Limitar a máximo 8GB por seguridad
    if [ "$NEED_GB" -gt 8 ]; then
        NEED_GB=8
    fi
    
    if [ "$NEED_GB" -gt 0 ]; then
        TEMP_FILE="/tmp/disk_test_$(date +%s).bin"
        
        echo "📝 Creando archivo: ${NEED_GB}GB en $TEMP_FILE"
        dd if=/dev/zero of=$TEMP_FILE bs=1G count=$NEED_GB status=none 2>&1
        
        NEW_DISK=$(df / | awk 'NR==2 {print $5}')
        echo "✅ Archivo creado - Nuevo uso: $NEW_DISK"
        echo ""
    fi
else
    echo "✅ Disco ya está en ${DISK_USED}%"
    TEMP_FILE=""
fi

echo "==========================================="
echo "PASO 3: ESPERANDO ALERTAS (90 segundos)"
echo "==========================================="
echo ""

for i in {1..9}; do
    echo -n "   $(( 10 * i ))s..."
    sleep 10
done
echo " 90s ✓"
echo ""

echo "==========================================="
echo "PASO 4: ALERTAS ACTIVAS"
echo "==========================================="
echo ""

# Alertas de memoria
echo "💾 Alertas de Memoria:"
curl -s "$PROMETHEUS_URL/api/v1/alerts" | jq -r '.data.alerts[] | select(.labels.alertname | test("Memory")) | "   ✅ \(.labels.severity | ascii_upcase): \(.labels.alertname)"' 2>/dev/null
echo ""

# Alertas de disco
echo "💿 Alertas de Disco:"
curl -s "$PROMETHEUS_URL/api/v1/alerts" | jq -r '.data.alerts[] | select(.labels.alertname | test("Disk")) | "   ✅ \(.labels.severity | ascii_upcase): \(.labels.alertname)"' 2>/dev/null
echo ""

TOTAL=$(curl -s "$PROMETHEUS_URL/api/v1/alerts" | jq '.data.alerts | length')
echo "📊 Total alertas activas: $TOTAL"
echo ""

echo "⏱️  Esperando 60s más para que Alertmanager procese..."
sleep 60
echo ""

echo "==========================================="
echo "PASO 5: NOTIFICACIONES"
echo "==========================================="
echo ""

AM_COUNT=$(curl -s "$ALERTMANAGER_URL/api/v2/alerts" 2>/dev/null | jq 'length')
echo "📬 Alertas en Alertmanager: $AM_COUNT"
echo ""

echo "📋 Últimos logs de notificaciones:"
docker compose -f docker-compose.full.yml logs --tail=20 alertmanager 2>&1 | \
    grep -v "WARN.*version" | \
    grep -iE "(notify|email|slack|memory|disk|sent)" | \
    tail -10
echo ""

echo "==========================================="
echo "📧 VERIFICAR AHORA"
echo "==========================================="
echo ""
echo "✅ Deberías recibir notificaciones en:"
echo ""
echo "📧 rfloresm@unis.edu.gt"
echo "📧 jflores@unis.edu.gt"
echo "💬 Slack #ensurance-alerts"
echo ""
echo "Alertas esperadas:"
echo "  🔴 CriticalMemoryUsage (>90%)"
echo "  ⚠️  HighMemoryUsage (>80%)"
echo "  🔴 CriticalDiskUsage (>90%)"
echo "  ⚠️  HighDiskUsage (>80%)"
echo "  ⚠️  DiskAlmostFull (>85%)"
echo ""

echo "🌐 Ver en:"
echo "   Prometheus: $PROMETHEUS_URL/alerts"
echo "   Alertmanager: $ALERTMANAGER_URL"
echo ""

echo "==========================================="
echo "🧹 AUTO-LIMPIEZA EN 2 MINUTOS"
echo "==========================================="
echo ""
echo "⏱️  Esperando 120 segundos antes de limpiar..."
echo "   (Tiempo para verificar que llegaron las notificaciones)"
echo ""

for i in {1..12}; do
    echo -n "   $(( 10 * i ))s..."
    sleep 10
done
echo " 120s ✓"
echo ""

echo "🧹 Limpiando recursos..."

# Detener stress
if ps -p $STRESS_PID > /dev/null 2>&1; then
    kill $STRESS_PID 2>/dev/null
    echo "✅ Stress de memoria detenido"
fi

# Eliminar archivo
if [ ! -z "$TEMP_FILE" ] && [ -f "$TEMP_FILE" ]; then
    rm -f "$TEMP_FILE"
    NEW_DISK=$(df / | awk 'NR==2 {print $5}')
    echo "✅ Archivo de disco eliminado (Uso: $NEW_DISK)"
fi

echo ""
echo "✅ Recursos liberados"
echo "   Las alertas se resolverán en 1-2 minutos"
echo ""

echo "==========================================="
echo "✅ PRUEBA COMPLETADA"
echo "==========================================="
echo "$(date '+%Y-%m-%d %H:%M:%S')"
echo ""
echo "Revisa tus emails y Slack!"
echo ""
