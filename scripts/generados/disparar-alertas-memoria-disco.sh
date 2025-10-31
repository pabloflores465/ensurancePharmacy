#!/bin/bash

# Script para disparar alertas de memoria y disco AHORA MISMO

PROMETHEUS_URL="http://localhost:9090"
ALERTMANAGER_URL="http://localhost:9094"

echo "=========================================="
echo "🚨 DISPARAR ALERTAS DE MEMORIA Y DISCO"
echo "=========================================="
echo ""
echo "Este script generará condiciones para disparar:"
echo "  💾 HighMemoryUsage (>80%)"
echo "  💾 CriticalMemoryUsage (>90%)"
echo "  💿 HighDiskUsage (>80%)"
echo "  💿 CriticalDiskUsage (>90%)"
echo "  💿 DiskAlmostFull (>85%)"
echo ""

read -p "¿Continuar? (y/n): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    exit 1
fi

echo ""
echo "==========================================="
echo "PASO 1: GENERAR ALTA MEMORIA RAM"
echo "==========================================="
echo ""

# Verificar si stress-ng está instalado
if ! command -v stress-ng &> /dev/null; then
    echo "⚠️  stress-ng no está instalado"
    echo "Instalando stress-ng..."
    sudo apt-get update -qq
    sudo apt-get install -y stress-ng
fi

echo "💾 Generando alta carga de memoria RAM..."
echo "   Objetivo: Llevar uso de memoria a >90%"
echo ""

# Calcular memoria a usar (90% de la RAM total)
TOTAL_MEM=$(free -m | awk '/^Mem:/{print $2}')
TARGET_MEM=$(( TOTAL_MEM * 90 / 100 ))

echo "   Memoria total: ${TOTAL_MEM}MB"
echo "   Objetivo: ${TARGET_MEM}MB (90%)"
echo ""

echo "🔥 Iniciando stress de memoria por 5 minutos..."
stress-ng --vm 4 --vm-bytes ${TARGET_MEM}M --timeout 300s --vm-method all --verify &
STRESS_MEM_PID=$!

echo "✅ Proceso de stress de memoria iniciado (PID: $STRESS_MEM_PID)"
echo ""

# Esperar 30 segundos para que suba la memoria
echo "⏱️  Esperando 30 segundos para que suba el uso de memoria..."
sleep 30

# Verificar uso de memoria actual
CURRENT_MEM_PERCENT=$(free | awk '/^Mem:/ {printf "%.0f", $3/$2 * 100}')
echo "   Uso actual de memoria: ${CURRENT_MEM_PERCENT}%"
echo ""

if [ "$CURRENT_MEM_PERCENT" -gt 80 ]; then
    echo "✅ Memoria sobre 80% - Alertas deberían dispararse"
else
    echo "⚠️  Memoria aún no alcanzó 80%, esperando más..."
fi
echo ""

echo "==========================================="
echo "PASO 2: GENERAR ALTO USO DE DISCO"
echo "==========================================="
echo ""

echo "💿 Creando archivo grande para llenar disco..."

# Verificar espacio disponible
DISK_AVAIL=$(df -BG / | awk 'NR==2 {print $4}' | sed 's/G//')
DISK_TOTAL=$(df -BG / | awk 'NR==2 {print $2}' | sed 's/G//')
DISK_USED_PERCENT=$(df / | awk 'NR==2 {print $5}' | sed 's/%//')

echo "   Disco total: ${DISK_TOTAL}GB"
echo "   Espacio disponible: ${DISK_AVAIL}GB"
echo "   Uso actual: ${DISK_USED_PERCENT}%"
echo ""

# Calcular cuánto necesitamos escribir para llegar a 85%
TARGET_PERCENT=85
CURRENT_USED_GB=$(df -BG / | awk 'NR==2 {print $3}' | sed 's/G//')
TARGET_USED_GB=$(( DISK_TOTAL * TARGET_PERCENT / 100 ))
NEED_TO_WRITE=$(( TARGET_USED_GB - CURRENT_USED_GB ))

if [ "$DISK_USED_PERCENT" -lt 85 ] && [ "$NEED_TO_WRITE" -gt 0 ]; then
    echo "   Necesitamos escribir ~${NEED_TO_WRITE}GB para alcanzar 85%"
    echo ""
    
    # Limitar a máximo 10GB por seguridad
    if [ "$NEED_TO_WRITE" -gt 10 ]; then
        NEED_TO_WRITE=10
        echo "⚠️  Limitando a ${NEED_TO_WRITE}GB por seguridad"
    fi
    
    TEMP_FILE="/tmp/disk_stress_test_$(date +%s).bin"
    
    echo "📝 Creando archivo de prueba: ${NEED_TO_WRITE}GB"
    echo "   Ubicación: $TEMP_FILE"
    echo "   Tiempo estimado: 2-5 minutos"
    echo ""
    
    dd if=/dev/zero of=$TEMP_FILE bs=1G count=$NEED_TO_WRITE status=progress 2>&1 | tail -1
    
    # Verificar nuevo uso de disco
    NEW_DISK_USED=$(df / | awk 'NR==2 {print $5}' | sed 's/%//')
    echo ""
    echo "✅ Archivo creado"
    echo "   Nuevo uso de disco: ${NEW_DISK_USED}%"
    echo ""
    
    if [ "$NEW_DISK_USED" -gt 80 ]; then
        echo "✅ Disco sobre 80% - Alertas deberían dispararse"
    fi
else
    echo "✅ Disco ya está en ${DISK_USED_PERCENT}% (>80%)"
    echo "   Las alertas ya deberían estar activas"
fi
echo ""

echo "==========================================="
echo "PASO 3: ESPERAR ALERTAS (2-4 minutos)"
echo "==========================================="
echo ""

echo "⏱️  Esperando 2 minutos para que Prometheus detecte las métricas..."
for i in {1..12}; do
    echo -n "   $(( 10 * i ))s..."
    sleep 10
done
echo " 120s"
echo ""

echo "==========================================="
echo "PASO 4: VERIFICAR ALERTAS"
echo "==========================================="
echo ""

# Verificar alertas de memoria
echo "💾 Alertas de Memoria:"
MEM_ALERTS=$(curl -s "$PROMETHEUS_URL/api/v1/alerts" | jq -r '.data.alerts[] | select(.labels.alertname | test("Memory")) | "\(.labels.severity | ascii_upcase): \(.labels.alertname)"')

if [ ! -z "$MEM_ALERTS" ]; then
    echo "$MEM_ALERTS" | while read line; do echo "   ✅ $line"; done
else
    echo "   ⚠️  No se detectaron alertas de memoria aún"
fi
echo ""

# Verificar alertas de disco
echo "💿 Alertas de Disco:"
DISK_ALERTS=$(curl -s "$PROMETHEUS_URL/api/v1/alerts" | jq -r '.data.alerts[] | select(.labels.alertname | test("Disk")) | "\(.labels.severity | ascii_upcase): \(.labels.alertname)"')

if [ ! -z "$DISK_ALERTS" ]; then
    echo "$DISK_ALERTS" | while read line; do echo "   ✅ $line"; done
else
    echo "   ⚠️  No se detectaron alertas de disco aún"
fi
echo ""

# Total de alertas activas
TOTAL_ALERTS=$(curl -s "$PROMETHEUS_URL/api/v1/alerts" | jq '.data.alerts | length')
echo "📊 Total de alertas activas: $TOTAL_ALERTS"
echo ""

echo "⏱️  Esperando 1 minuto más para que Alertmanager procese..."
sleep 60
echo ""

echo "==========================================="
echo "PASO 5: VERIFICAR NOTIFICACIONES"
echo "==========================================="
echo ""

# Verificar en Alertmanager
AM_ALERT_COUNT=$(curl -s "$ALERTMANAGER_URL/api/v2/alerts" 2>/dev/null | jq 'length')
echo "📬 Alertas en Alertmanager: $AM_ALERT_COUNT"
echo ""

if [ "$AM_ALERT_COUNT" -gt 0 ]; then
    echo "Alertas pendientes de notificación:"
    curl -s "$ALERTMANAGER_URL/api/v2/alerts" 2>/dev/null | jq -r '.[] | "   \(.labels.alertname) [\(.status.state)] -> \(.receivers[0].name)"'
    echo ""
fi

# Ver logs recientes de Alertmanager
echo "📋 Logs recientes de Alertmanager (notificaciones):"
docker compose -f docker-compose.full.yml logs --tail=30 alertmanager 2>&1 | \
    grep -v "WARN.*version" | \
    grep -E "(Notify|email|slack|success|sent|Memory|Disk)" | \
    tail -15
echo ""

echo "==========================================="
echo "📧 VERIFICAR NOTIFICACIONES"
echo "==========================================="
echo ""
echo "En los próximos 2-5 minutos deberías recibir:"
echo ""
echo "📧 Email en rfloresm@unis.edu.gt:"
echo "   • [CRÍTICO] Alerta Urgente - CriticalMemoryUsage"
echo "   • [WARNING] Alerta de Monitoreo - HighMemoryUsage"
echo "   • [CRÍTICO] Alerta Urgente - CriticalDiskUsage"
echo "   • [WARNING] Alerta de Monitoreo - HighDiskUsage"
echo ""
echo "📧 Email en jflores@unis.edu.gt:"
echo "   • Mismas alertas que arriba"
echo ""
echo "💬 Slack #ensurance-alerts:"
echo "   • 🔴 ALERTA CRÍTICA - CriticalMemoryUsage"
echo "   • ⚠️ ADVERTENCIA - HighMemoryUsage"
echo "   • 🔴 ALERTA CRÍTICA - CriticalDiskUsage"
echo "   • ⚠️ ADVERTENCIA - HighDiskUsage"
echo ""

echo "==========================================="
echo "🌐 INTERFACES WEB"
echo "==========================================="
echo ""
echo "Ver alertas en Prometheus:"
echo "   $PROMETHEUS_URL/alerts"
echo ""
echo "Ver notificaciones en Alertmanager:"
echo "   $ALERTMANAGER_URL/#/alerts"
echo ""

echo "==========================================="
echo "🧹 LIMPIEZA DESPUÉS DE LA PRUEBA"
echo "==========================================="
echo ""

echo "⚠️  IMPORTANTE: Los procesos de stress seguirán corriendo"
echo ""
echo "Para limpiar después de verificar las notificaciones:"
echo ""
echo "1. Detener proceso de memoria (5 min o hasta que presiones):"
if ps -p $STRESS_MEM_PID > /dev/null 2>&1; then
    echo "   kill $STRESS_MEM_PID"
fi
echo ""
echo "2. Eliminar archivo de disco:"
if [ -f "$TEMP_FILE" ]; then
    echo "   rm $TEMP_FILE"
fi
echo ""

read -p "¿Detener stress de memoria y limpiar disco ahora? (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo ""
    echo "🧹 Limpiando..."
    
    # Detener stress
    if ps -p $STRESS_MEM_PID > /dev/null 2>&1; then
        kill $STRESS_MEM_PID 2>/dev/null
        echo "✅ Proceso de stress de memoria detenido"
    fi
    
    # Eliminar archivo temporal
    if [ -f "$TEMP_FILE" ]; then
        rm -f "$TEMP_FILE"
        NEW_DISK=$(df / | awk 'NR==2 {print $5}')
        echo "✅ Archivo de disco eliminado"
        echo "   Uso de disco ahora: $NEW_DISK"
    fi
    
    echo ""
    echo "✅ Limpieza completada"
    echo "   Las alertas se resolverán automáticamente en 1-2 minutos"
else
    echo ""
    echo "⚠️  Recuerda limpiar manualmente después:"
    if ps -p $STRESS_MEM_PID > /dev/null 2>&1; then
        echo "   kill $STRESS_MEM_PID"
    fi
    if [ -f "$TEMP_FILE" ]; then
        echo "   rm $TEMP_FILE"
    fi
fi

echo ""
echo "==========================================="
echo "✅ PRUEBA COMPLETADA"
echo "==========================================="
echo ""
echo "Resumen:"
echo "  💾 Memoria: Stress aplicado durante 5 minutos"
echo "  💿 Disco: Archivo temporal creado"
echo "  🔔 Alertas: Disparadas en Prometheus"
echo "  📬 Notificaciones: Enviadas por Alertmanager"
echo ""
echo "Verifica tus emails y Slack ahora!"
echo ""
