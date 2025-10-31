#!/bin/bash

# ============================================================================
# SCRIPT PARA PROBAR TODAS LAS ALERTAS
# Envía alertas de prueba directamente a AlertManager
# ============================================================================

set -e

BOLD='\033[1m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${BOLD}"
echo "╔════════════════════════════════════════════════════════════════════╗"
echo "║                                                                    ║"
echo "║              PROBAR TODAS LAS ALERTAS                             ║"
echo "║         AlertManager + Slack + Email + Prometheus                 ║"
echo "║                                                                    ║"
echo "╚════════════════════════════════════════════════════════════════════╝"
echo -e "${NC}"

# ============================================================================
# VERIFICAR SERVICIOS
# ============================================================================
echo -e "${BLUE}[0/10] Verificando servicios...${NC}"

if ! curl -s http://localhost:9093/-/healthy > /dev/null 2>&1; then
    echo -e "${RED}❌ AlertManager no está corriendo${NC}"
    exit 1
fi
echo -e "${GREEN}  ✅ AlertManager OK${NC}"

if ! curl -s http://localhost:9090/-/healthy > /dev/null 2>&1; then
    echo -e "${RED}❌ Prometheus no está corriendo${NC}"
    exit 1
fi
echo -e "${GREEN}  ✅ Prometheus OK${NC}"

echo ""
echo -e "${YELLOW}Este script enviará alertas de prueba que generarán:${NC}"
echo "  📧 Emails a: pablopolis2016@gmail.com, jflores@unis.edu.gt"
echo "  💬 Mensajes Slack a: #ensurance-alerts"
echo ""
read -p "¿Continuar? (y/n): " -n 1 -r
echo ""
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Cancelado"
    exit 0
fi

# ============================================================================
# FUNCIÓN PARA ENVIAR ALERTAS
# ============================================================================
send_alert() {
    local alert_name=$1
    local severity=$2
    local summary=$3
    local description=$4
    local service=$5
    
    echo -e "${BLUE}  Enviando: $alert_name ($severity)${NC}"
    
    curl -s -X POST http://localhost:9093/api/v1/alerts -H "Content-Type: application/json" -d "[
      {
        \"labels\": {
          \"alertname\": \"$alert_name\",
          \"severity\": \"$severity\",
          \"service\": \"$service\",
          \"instance\": \"test-instance\",
          \"job\": \"test-job\"
        },
        \"annotations\": {
          \"summary\": \"$summary\",
          \"description\": \"$description\",
          \"dashboard\": \"http://localhost:9090/alerts\",
          \"action\": \"Esta es una alerta de PRUEBA - No requiere acción\"
        },
        \"startsAt\": \"$(date -u +%Y-%m-%dT%H:%M:%SZ)\",
        \"endsAt\": \"$(date -u -d '+5 minutes' +%Y-%m-%dT%H:%M:%SZ)\"
      }
    ]" > /dev/null 2>&1
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}    ✅ Alerta enviada correctamente${NC}"
    else
        echo -e "${RED}    ❌ Error al enviar alerta${NC}"
    fi
    
    sleep 2
}

# ============================================================================
# PRUEBA 1: ALERTA CRÍTICA
# ============================================================================
echo ""
echo -e "${RED}[1/10] Probando ALERTA CRÍTICA...${NC}"
send_alert \
    "TestAlertaCritica" \
    "critical" \
    "🔴 PRUEBA DE ALERTA CRÍTICA" \
    "Esta es una alerta crítica de prueba del sistema de monitoreo Ensurance Pharmacy. Debe notificar inmediatamente por email y Slack con alta prioridad." \
    "sistema-pruebas"

# ============================================================================
# PRUEBA 2: ALERTA WARNING
# ============================================================================
echo ""
echo -e "${YELLOW}[2/10] Probando ALERTA WARNING...${NC}"
send_alert \
    "TestAlertaWarning" \
    "warning" \
    "⚠️ PRUEBA DE ALERTA WARNING" \
    "Esta es una alerta de advertencia de prueba. Debe notificar por email y Slack con prioridad media." \
    "sistema-pruebas"

# ============================================================================
# PRUEBA 3: ALERTA INFO
# ============================================================================
echo ""
echo -e "${BLUE}[3/10] Probando ALERTA INFO...${NC}"
send_alert \
    "TestAlertaInfo" \
    "info" \
    "ℹ️ PRUEBA DE ALERTA INFORMATIVA" \
    "Esta es una alerta informativa de prueba. Debe notificar por email y Slack con prioridad baja." \
    "sistema-pruebas"

# ============================================================================
# PRUEBA 4: ALERTA DE CPU ALTA
# ============================================================================
echo ""
echo -e "${BLUE}[4/10] Probando alerta de CPU alta...${NC}"
send_alert \
    "HighCPUUsage" \
    "critical" \
    "🔥 CPU al 95% en servidor de aplicaciones" \
    "El uso de CPU ha superado el 95% durante más de 5 minutos. Esto puede afectar el rendimiento de las aplicaciones." \
    "ensurance-backend"

# ============================================================================
# PRUEBA 5: ALERTA DE MEMORIA ALTA
# ============================================================================
echo ""
echo -e "${BLUE}[5/10] Probando alerta de memoria alta...${NC}"
send_alert \
    "HighMemoryUsage" \
    "warning" \
    "💾 Memoria al 85% en servidor de base de datos" \
    "El uso de memoria ha superado el 85%. Se recomienda revisar procesos y posibles memory leaks." \
    "database-server"

# ============================================================================
# PRUEBA 6: ALERTA DE DISCO LLENO
# ============================================================================
echo ""
echo -e "${BLUE}[6/10] Probando alerta de disco lleno...${NC}"
send_alert \
    "HighDiskUsage" \
    "warning" \
    "💿 Disco al 80% en servidor de aplicaciones" \
    "El espacio en disco ha superado el 80%. Se recomienda limpiar logs antiguos y archivos temporales." \
    "ensurance-backend"

# ============================================================================
# PRUEBA 7: ALERTA DE RED SATURADA
# ============================================================================
echo ""
echo -e "${BLUE}[7/10] Probando alerta de red saturada...${NC}"
send_alert \
    "HighNetworkTraffic" \
    "warning" \
    "🌐 Tráfico de red alto: 150MB/s" \
    "El tráfico de red ha superado los 100MB/s durante más de 5 minutos. Posible ataque DDoS o tráfico inusual." \
    "network-infrastructure"

# ============================================================================
# PRUEBA 8: ALERTA DE SERVICIO CAÍDO
# ============================================================================
echo ""
echo -e "${RED}[8/10] Probando alerta de servicio caído...${NC}"
send_alert \
    "ServiceDown" \
    "critical" \
    "🔴 Servicio Backend V5 CAÍDO" \
    "El servicio ensurance-backend-v5 no responde. Las transacciones de farmacia están interrumpidas. Acción inmediata requerida." \
    "ensurance-backv5"

# ============================================================================
# PRUEBA 9: ALERTA DE BASE DE DATOS
# ============================================================================
echo ""
echo -e "${YELLOW}[9/10] Probando alerta de base de datos...${NC}"
send_alert \
    "DatabaseConnectionPoolExhausted" \
    "warning" \
    "⚠️ Pool de conexiones de BD agotado" \
    "El pool de conexiones de la base de datos está al 90%. Esto puede causar timeouts en las aplicaciones." \
    "postgresql"

# ============================================================================
# PRUEBA 10: ALERTA DE RABBITMQ
# ============================================================================
echo ""
echo -e "${YELLOW}[10/10] Probando alerta de RabbitMQ...${NC}"
send_alert \
    "RabbitMQQueueBacklog" \
    "warning" \
    "🐰 Cola de mensajes RabbitMQ con retraso" \
    "La cola 'pharmacy-orders' tiene más de 1000 mensajes pendientes. Los pedidos pueden experimentar retrasos." \
    "rabbitmq"

# ============================================================================
# RESUMEN
# ============================================================================
echo ""
echo -e "${BOLD}${GREEN}"
echo "╔════════════════════════════════════════════════════════════════════╗"
echo "║                                                                    ║"
echo "║              ✅ PRUEBAS DE ALERTAS COMPLETADAS                    ║"
echo "║                                                                    ║"
echo "╚════════════════════════════════════════════════════════════════════╝"
echo -e "${NC}"

echo ""
echo "📊 RESULTADOS:"
echo "  ✅ 10 alertas de prueba enviadas a AlertManager"
echo "  📧 Deberías recibir emails en: pablopolis2016@gmail.com, jflores@unis.edu.gt"
echo "  💬 Deberías ver mensajes en Slack: #ensurance-alerts"
echo ""
echo "🔍 VERIFICAR ALERTAS:"
echo "  AlertManager:     http://localhost:9093/#/alerts"
echo "  Prometheus:       http://localhost:9090/alerts"
echo "  Grafana:          http://localhost:3302/alerting/list"
echo "  Netdata:          http://localhost:19999/#menu_netdata_submenu_alarms"
echo ""
echo "📋 ALERTAS ENVIADAS:"
echo "  1. 🔴 CRÍTICA:  TestAlertaCritica"
echo "  2. ⚠️  WARNING:  TestAlertaWarning"
echo "  3. ℹ️  INFO:     TestAlertaInfo"
echo "  4. 🔴 CRÍTICA:  HighCPUUsage"
echo "  5. ⚠️  WARNING:  HighMemoryUsage"
echo "  6. ⚠️  WARNING:  HighDiskUsage"
echo "  7. ⚠️  WARNING:  HighNetworkTraffic"
echo "  8. 🔴 CRÍTICA:  ServiceDown"
echo "  9. ⚠️  WARNING:  DatabaseConnectionPoolExhausted"
echo "  10. ⚠️  WARNING: RabbitMQQueueBacklog"
echo ""
echo "⏱️  Las notificaciones pueden tardar 10-30 segundos en llegar"
echo ""
echo "🧹 LIMPIAR ALERTAS:"
echo "  Las alertas de prueba se resolverán automáticamente en 5 minutos"
echo "  O puedes silenciarlas manualmente en: http://localhost:9093/#/silences"
echo ""
