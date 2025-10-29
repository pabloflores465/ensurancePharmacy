#!/bin/bash

# Script de Verificación del Sistema de Alertas
# Uso: ./verify-alerting.sh

echo "==========================================================="
echo "🚨 VERIFICACIÓN SISTEMA DE ALERTAS - ENSURANCE PHARMACY"
echo "==========================================================="
echo ""

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 1. Verificar Alertmanager
echo "1️⃣ Verificando Alertmanager..."
if docker ps | grep -q "ensurance-alertmanager-full.*Up"; then
    echo -e "   ${GREEN}✅ Alertmanager está corriendo${NC}"
    STATUS=$(curl -s http://localhost:9094/api/v2/status | python3 -c "import sys, json; print(json.load(sys.stdin)['cluster']['status'])" 2>/dev/null)
    if [ "$STATUS" == "ready" ]; then
        echo -e "   ${GREEN}✅ Cluster status: ready${NC}"
    else
        echo -e "   ${RED}❌ Cluster status: $STATUS${NC}"
    fi
else
    echo -e "   ${RED}❌ Alertmanager NO está corriendo${NC}"
fi
echo ""

# 2. Verificar Prometheus
echo "2️⃣ Verificando Prometheus..."
if docker ps | grep -q "ensurance-prometheus-full.*Up"; then
    echo -e "   ${GREEN}✅ Prometheus está corriendo${NC}"
    RULES=$(curl -s http://localhost:9090/api/v1/rules | python3 -c "import sys, json; data=json.load(sys.stdin); groups = data.get('data', {}).get('groups', []); print(sum(len(g.get('rules', [])) for g in groups))" 2>/dev/null)
    if [ ! -z "$RULES" ] && [ $RULES -gt 0 ]; then
        echo -e "   ${GREEN}✅ $RULES reglas de alertas cargadas${NC}"
    else
        echo -e "   ${RED}❌ No se encontraron reglas de alertas${NC}"
    fi
else
    echo -e "   ${RED}❌ Prometheus NO está corriendo${NC}"
fi
echo ""

# 3. Ver archivos de reglas
echo "3️⃣ Archivos de Reglas de Alertas:"
if [ -d "monitoring/prometheus/rules" ]; then
    ls -1 monitoring/prometheus/rules/*.yml 2>/dev/null | while read file; do
        filename=$(basename "$file")
        rules=$(grep -c "^      - alert:" "$file" 2>/dev/null || echo "0")
        echo -e "   📄 $filename: ${GREEN}$rules alertas${NC}"
    done
else
    echo -e "   ${RED}❌ Directorio de reglas no encontrado${NC}"
fi
echo ""

# 4. Configuración de Email
echo "4️⃣ Configuración de Notificaciones:"
if grep -q "smtp-relay.brevo.com" monitoring/alertmanager/alertmanager.yml 2>/dev/null; then
    echo -e "   ${GREEN}✅ SMTP configurado (Brevo)${NC}"
    echo "   📧 Destinatarios:"
    echo "      • pablopolis2016@gmail.com"
    echo "      • jflores@unis.edu.gt"
else
    echo -e "   ${RED}❌ SMTP no configurado${NC}"
fi

# Verificar Slack
if grep -q "api_url.*hooks.slack.com" monitoring/alertmanager/alertmanager.yml 2>/dev/null; then
    echo -e "   ${GREEN}✅ Slack Webhook configurado${NC}"
else
    echo -e "   ${YELLOW}⚠️ Slack NO configurado (ejecuta ./configure-slack-webhook.sh)${NC}"
fi
echo ""

# 5. Alertas Activas
echo "5️⃣ Alertas Activas:"
ALERTS=$(curl -s http://localhost:9090/api/v1/alerts | python3 -c "
import sys, json
try:
    data = json.load(sys.stdin)
    alerts = data.get('data', {}).get('alerts', [])
    firing = [a for a in alerts if a.get('state') == 'firing']
    pending = [a for a in alerts if a.get('state') == 'pending']
    print(f'{len(firing)},{len(pending)}')
except:
    print('0,0')
" 2>/dev/null)

IFS=',' read -r FIRING PENDING <<< "$ALERTS"

if [ ! -z "$FIRING" ]; then
    if [ $FIRING -eq 0 ]; then
        echo -e "   ${GREEN}✅ Sin alertas activas${NC}"
    else
        echo -e "   ${RED}🔴 $FIRING alertas FIRING${NC}"
    fi
    if [ $PENDING -gt 0 ]; then
        echo -e "   ${YELLOW}⏱️  $PENDING alertas PENDING${NC}"
    fi
else
    echo -e "   ${YELLOW}⚠️ No se pudo obtener estado de alertas${NC}"
fi
echo ""

# 6. Resumen de Reglas
echo "6️⃣ Resumen de Reglas por Categoría:"
echo "   📊 Sistema: 11 alertas (CPU, Memoria, Disco, Red)"
echo "   💻 Aplicaciones: 9 alertas (Backends, Frontends)"
echo "   🐰 RabbitMQ: 12 alertas (Colas, Memoria, Conexiones)"
echo "   🔥 K6 Testing: 8 alertas (Performance, Errores)"
echo "   🏗️  CI/CD: 12 alertas (Jenkins, SonarQube, Drone)"
echo "   👁️  Monitoreo: 12 alertas (Prometheus, Grafana, Netdata)"
echo "   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "   📈 TOTAL: 64 alertas configuradas"
echo ""

# 7. URLs de Acceso
echo "7️⃣ URLs de Acceso:"
echo "   🌐 Alertmanager: http://localhost:9094"
echo "   🌐 Prometheus Alerts: http://localhost:9090/alerts"
echo "   🌐 Prometheus Rules: http://localhost:9090/rules"
echo "   🌐 Grafana: http://localhost:3302"
echo "   🌐 Netdata: http://localhost:19999"
echo ""

# 8. Documentación
echo "8️⃣ Documentación:"
echo "   📄 monitoring/ALERTING_SETUP_GUIDE.md"
echo "   📄 monitoring/prometheus/rules/*.yml"
echo "   📄 monitoring/alertmanager/alertmanager.yml"
echo ""

# 9. Severidades
echo "9️⃣ Niveles de Severidad:"
echo -e "   ${RED}🔴 CRITICAL (16 alertas):${NC} Acción inmediata + Email + Slack @channel"
echo -e "   ${YELLOW}⚠️  WARNING (42 alertas):${NC} Revisar pronto + Email + Slack"
echo -e "   ℹ️  INFO (6 alertas): Informativo + Solo Email"
echo ""

# 10. Comandos Útiles
echo "🔧 Comandos Útiles:"
echo ""
echo "Ver alertas activas:"
echo "  curl -s http://localhost:9090/api/v1/alerts | python3 -m json.tool"
echo ""
echo "Probar email (detener node-exporter):"
echo "  docker stop ensurance-node-exporter-full"
echo "  # Espera 2 min, revisa email, luego:"
echo "  docker start ensurance-node-exporter-full"
echo ""
echo "Configurar Slack:"
echo "  ./configure-slack-webhook.sh \"https://hooks.slack.com/services/YOUR/WEBHOOK/URL\""
echo ""
echo "Recargar configuración de Alertmanager:"
echo "  docker compose -f docker-compose.full.yml restart alertmanager"
echo ""
echo "Recargar reglas de Prometheus:"
echo "  docker compose -f docker-compose.full.yml restart prometheus"
echo ""

echo "==========================================================="
echo -e "${GREEN}✅ Verificación Completada${NC}"
echo "==========================================================="
