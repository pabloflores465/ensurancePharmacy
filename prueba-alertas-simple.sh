#!/bin/bash

# ============================================================================
# SCRIPT SIMPLE DE PRUEBA DE ALERTAS
# Prueba las alertas principales y verifica Email + Slack
# ============================================================================

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

echo -e "${CYAN}═══════════════════════════════════════════════════════${NC}"
echo -e "${CYAN}  PRUEBA SIMPLE DE ALERTAS - ENSURANCE PHARMACY${NC}"
echo -e "${CYAN}═══════════════════════════════════════════════════════${NC}\n"

# ============================================================================
# Test 1: NodeExporter Down (WARNING)
# ============================================================================

echo -e "${BLUE}TEST 1: NodeExporterDown (WARNING)${NC}"
echo "  Deteniendo Node Exporter..."
docker stop ensurance-node-exporter-full >/dev/null 2>&1

echo "  Esperando 90 segundos para que la alerta se dispare..."
sleep 90

# Verificar alerta
ALERT=$(curl -s http://localhost:9090/api/v1/alerts | jq -r '.data.alerts[] | select(.labels.alertname=="NodeExporterDown" and .state=="firing")' | wc -l)

if [ "$ALERT" -gt 0 ]; then
    echo -e "${GREEN}  ✓ Alerta NodeExporterDown disparada${NC}"
else
    echo -e "${YELLOW}  ⚠ Alerta NodeExporterDown no se disparó aún (puede tomar más tiempo)${NC}"
fi

# Verificar notificaciones
EMAIL_COUNT=$(docker logs ensurance-alertmanager-full --since 2m 2>&1 | grep -c "email.*Notify success" || echo "0")
SLACK_COUNT=$(docker logs ensurance-alertmanager-full --since 2m 2>&1 | grep -c "slack.*Notify success" || echo "0")

echo -e "${CYAN}  Notificaciones enviadas:${NC}"
echo "    • Email: $EMAIL_COUNT"
echo "    • Slack: $SLACK_COUNT"

echo "  Restaurando Node Exporter..."
docker start ensurance-node-exporter-full >/dev/null 2>&1
sleep 5

echo ""

# ============================================================================
# Test 2: RabbitMQ Down (CRITICAL - con @channel)
# ============================================================================

echo -e "${BLUE}TEST 2: RabbitMQDown (CRITICAL con @channel)${NC}"
echo "  Deteniendo RabbitMQ..."
docker stop ensurance-rabbitmq-full >/dev/null 2>&1

echo "  Esperando 90 segundos para que la alerta se dispare..."
sleep 90

# Verificar alerta
ALERT=$(curl -s http://localhost:9090/api/v1/alerts | jq -r '.data.alerts[] | select(.labels.alertname=="RabbitMQDown" and .state=="firing")' | wc -l)

if [ "$ALERT" -gt 0 ]; then
    echo -e "${GREEN}  ✓ Alerta RabbitMQDown disparada${NC}"
else
    echo -e "${YELLOW}  ⚠ Alerta RabbitMQDown no se disparó aún${NC}"
fi

# Verificar notificaciones
EMAIL_COUNT=$(docker logs ensurance-alertmanager-full --since 2m 2>&1 | grep -c "email.*Notify success" || echo "0")
SLACK_COUNT=$(docker logs ensurance-alertmanager-full --since 2m 2>&1 | grep -c "slack.*Notify success" || echo "0")

echo -e "${CYAN}  Notificaciones enviadas (últimos 2 min):${NC}"
echo "    • Email: $EMAIL_COUNT"
echo "    • Slack: $SLACK_COUNT (debería incluir @channel)"

echo "  Restaurando RabbitMQ..."
docker start ensurance-rabbitmq-full >/dev/null 2>&1
sleep 5

echo ""

# ============================================================================
# Test 3: Grafana Down (WARNING)
# ============================================================================

echo -e "${BLUE}TEST 3: GrafanaDown (WARNING)${NC}"
echo "  Deteniendo Grafana..."
docker stop ensurance-grafana-full >/dev/null 2>&1

echo "  Esperando 90 segundos para que la alerta se dispare..."
sleep 90

# Verificar alerta
ALERT=$(curl -s http://localhost:9090/api/v1/alerts | jq -r '.data.alerts[] | select(.labels.alertname=="GrafanaDown" and .state=="firing")' | wc -l)

if [ "$ALERT" -gt 0 ]; then
    echo -e "${GREEN}  ✓ Alerta GrafanaDown disparada${NC}"
else
    echo -e "${YELLOW}  ⚠ Alerta GrafanaDown no se disparó aún${NC}"
fi

echo "  Restaurando Grafana..."
docker start ensurance-grafana-full >/dev/null 2>&1
sleep 5

echo ""

# ============================================================================
# RESUMEN Y VERIFICACIÓN FINAL
# ============================================================================

echo -e "${CYAN}═══════════════════════════════════════════════════════${NC}"
echo -e "${CYAN}  RESUMEN DE PRUEBAS${NC}"
echo -e "${CYAN}═══════════════════════════════════════════════════════${NC}\n"

# Alertas actualmente activas
echo -e "${BLUE}Alertas actualmente activas:${NC}"
curl -s http://localhost:9090/api/v1/alerts | jq -r '.data.alerts[] | select(.state=="firing") | "  • " + .labels.alertname + " (" + .labels.severity + ")"' | sort -u

echo ""

# Total de notificaciones en últimos 10 minutos
EMAIL_TOTAL=$(docker logs ensurance-alertmanager-full --since 10m 2>&1 | grep -c "email.*Notify success" || echo "0")
SLACK_TOTAL=$(docker logs ensurance-alertmanager-full --since 10m 2>&1 | grep -c "slack.*Notify success" || echo "0")

echo -e "${BLUE}Notificaciones enviadas (últimos 10 minutos):${NC}"
echo "  • Email: $EMAIL_TOTAL notificaciones"
echo "  • Slack: $SLACK_TOTAL notificaciones"

echo ""

# Verificar configuración
echo -e "${BLUE}Estado de la configuración:${NC}"

if grep -q "https://hooks.slack.com" monitoring/alertmanager/alertmanager.yml 2>/dev/null; then
    echo -e "  ${GREEN}✓ Webhook de Slack configurado${NC}"
else
    echo -e "  ${RED}✗ Webhook de Slack NO configurado${NC}"
fi

RULES=$(curl -s http://localhost:9090/api/v1/rules | jq -r '.data.groups | length' 2>/dev/null || echo "0")
echo "  • Grupos de reglas en Prometheus: $RULES"

RECEIVERS=$(curl -s http://localhost:9094/api/v2/status 2>/dev/null | jq -r '.config.receivers | length' 2>/dev/null || echo "0")
echo "  • Receivers en AlertManager: $RECEIVERS"

echo ""

# Verificar si hay errores en AlertManager
ERRORS=$(docker logs ensurance-alertmanager-full --since 10m 2>&1 | grep -i "error" | grep -v "successfully" | wc -l)

if [ "$ERRORS" -eq 0 ]; then
    echo -e "${GREEN}✓ No se encontraron errores en AlertManager${NC}"
else
    echo -e "${YELLOW}⚠ Se encontraron $ERRORS posibles errores en AlertManager${NC}"
    echo "  Ver con: docker logs ensurance-alertmanager-full --since 10m | grep -i error"
fi

echo ""

# Recomendaciones
echo -e "${CYAN}═══════════════════════════════════════════════════════${NC}"
echo -e "${CYAN}  RECOMENDACIONES${NC}"
echo -e "${CYAN}═══════════════════════════════════════════════════════${NC}\n"

echo "1. Verificar mensajes en Slack:"
echo "   → Canal: #ensurance-alerts"
echo "   → Deberías ver alertas con formato y colores"
echo ""

echo "2. Verificar emails:"
echo "   → pablopolis2016@gmail.com"
echo "   → jflores@unis.edu.gt"
echo "   → Revisar carpeta Spam si no aparecen"
echo ""

echo "3. Ver alertas activas:"
echo "   → Prometheus: http://localhost:9090/alerts"
echo "   → AlertManager: http://localhost:9094"
echo "   → Grafana: http://localhost:3302"
echo ""

echo "4. Ver logs de notificaciones:"
echo "   → docker logs ensurance-alertmanager-full --since 10m | grep -i notify"
echo ""

echo -e "${GREEN}✓ Prueba completada${NC}\n"
