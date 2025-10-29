#!/bin/bash

# Script de Verificación de Netdata - Ensurance Pharmacy
# Uso: ./verify-netdata.sh

echo "=================================================="
echo "🔍 VERIFICACIÓN DE NETDATA - ENSURANCE PHARMACY"
echo "=================================================="
echo ""

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Verificar si Netdata está corriendo
echo "1️⃣ Estado del Contenedor Netdata:"
NETDATA_STATUS=$(docker ps --filter "name=ensurance-netdata-full" --format "{{.Status}}")
if [[ $NETDATA_STATUS == *"Up"* ]]; then
    echo -e "   ${GREEN}✅ Netdata está corriendo${NC}"
    echo "   📊 Status: $NETDATA_STATUS"
else
    echo -e "   ${RED}❌ Netdata NO está corriendo${NC}"
    exit 1
fi
echo ""

# Verificar puerto
echo "2️⃣ Acceso HTTP:"
if curl -s http://localhost:19999/api/v1/info > /dev/null 2>&1; then
    echo -e "   ${GREEN}✅ Puerto 19999 accesible${NC}"
    VERSION=$(curl -s http://localhost:19999/api/v1/info | python3 -c "import sys, json; print(json.load(sys.stdin).get('version', 'unknown'))" 2>/dev/null)
    echo "   📦 Versión: $VERSION"
else
    echo -e "   ${RED}❌ No se puede acceder a Netdata en puerto 19999${NC}"
fi
echo ""

# Verificar volúmenes
echo "3️⃣ Volúmenes Docker:"
VOLUMES=$(docker volume ls | grep netdata | wc -l)
if [ $VOLUMES -eq 3 ]; then
    echo -e "   ${GREEN}✅ Los 3 volúmenes están creados${NC}"
    docker volume ls | grep netdata | awk '{print "   📁", $2}'
else
    echo -e "   ${YELLOW}⚠️ Se esperaban 3 volúmenes, encontrados: $VOLUMES${NC}"
fi
echo ""

# Verificar métricas de K6
echo "4️⃣ Métricas de K6:"
K6_CHARTS=$(curl -s http://localhost:19999/api/v1/charts 2>/dev/null | python3 -c "import sys, json; data=json.load(sys.stdin); print(len([k for k in data['charts'].keys() if 'k6' in k.lower()]))" 2>/dev/null)
if [ ! -z "$K6_CHARTS" ] && [ $K6_CHARTS -gt 0 ]; then
    echo -e "   ${GREEN}✅ K6 charts encontrados: $K6_CHARTS${NC}"
else
    echo -e "   ${YELLOW}⚠️ No se encontraron charts de K6${NC}"
fi
echo ""

# Verificar métricas de RabbitMQ
echo "5️⃣ Métricas de RabbitMQ:"
RABBITMQ_CHARTS=$(curl -s http://localhost:19999/api/v1/charts 2>/dev/null | python3 -c "import sys, json; data=json.load(sys.stdin); print(len([k for k in data['charts'].keys() if 'rabbitmq' in k.lower()]))" 2>/dev/null)
if [ ! -z "$RABBITMQ_CHARTS" ] && [ $RABBITMQ_CHARTS -gt 0 ]; then
    echo -e "   ${GREEN}✅ RabbitMQ charts encontrados: $RABBITMQ_CHARTS${NC}"
else
    echo -e "   ${YELLOW}⚠️ No se encontraron charts de RabbitMQ${NC}"
fi
echo ""

# Verificar alarmas
echo "6️⃣ Estado de Alarmas:"
ALARM_INFO=$(curl -s http://localhost:19999/api/v1/alarms 2>/dev/null | python3 -c "
import sys, json
try:
    data = json.load(sys.stdin)
    alarms = data.get('alarms', {})
    total = len(alarms)
    normal = sum(1 for a in alarms.values() if a.get('status') == 'CLEAR')
    warning = sum(1 for a in alarms.values() if a.get('status') == 'WARNING')
    critical = sum(1 for a in alarms.values() if a.get('status') == 'CRITICAL')
    print(f'{total},{normal},{warning},{critical}')
except:
    print('0,0,0,0')
" 2>/dev/null)

IFS=',' read -r TOTAL NORMAL WARNING CRITICAL <<< "$ALARM_INFO"

if [ ! -z "$TOTAL" ] && [ $TOTAL -gt 0 ]; then
    echo "   📊 Total de alarmas: $TOTAL"
    echo -e "   ${GREEN}✅ Normal: $NORMAL${NC}"
    if [ $WARNING -gt 0 ]; then
        echo -e "   ${YELLOW}⚠️ Warning: $WARNING${NC}"
    fi
    if [ $CRITICAL -gt 0 ]; then
        echo -e "   ${RED}🔴 Critical: $CRITICAL${NC}"
    fi
else
    echo -e "   ${YELLOW}⚠️ No se pudieron obtener alarmas${NC}"
fi
echo ""

# Verificar integración con Prometheus
echo "7️⃣ Conexión con Prometheus:"
if curl -s http://prometheus:9090/metrics > /dev/null 2>&1; then
    echo -e "   ${GREEN}✅ Prometheus accesible desde red Docker${NC}"
elif curl -s http://localhost:9090/metrics > /dev/null 2>&1; then
    echo -e "   ${GREEN}✅ Prometheus accesible en localhost${NC}"
else
    echo -e "   ${RED}❌ No se puede conectar a Prometheus${NC}"
fi
echo ""

# Resumen
echo "=================================================="
echo "📋 RESUMEN"
echo "=================================================="
echo ""
echo "🌐 URLs de Acceso:"
echo "   • Netdata:    http://localhost:19999"
echo "   • Grafana:    http://localhost:3302"
echo "   • Prometheus: http://localhost:9090"
echo "   • RabbitMQ:   http://localhost:15674"
echo ""
echo "📁 Archivos de Configuración:"
echo "   • monitoring/netdata/netdata.conf"
echo "   • monitoring/netdata/go.d/prometheus.conf"
echo "   • monitoring/netdata/health.d/k6_alerts.conf"
echo ""
echo "📚 Documentación:"
echo "   • monitoring/netdata/NETDATA_DASHBOARDS_GUIDE.md"
echo "   • NETDATA_SETUP_SUMMARY.md"
echo ""
echo "=================================================="
echo -e "${GREEN}✅ Verificación completada${NC}"
echo "=================================================="
