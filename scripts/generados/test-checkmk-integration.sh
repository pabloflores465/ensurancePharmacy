#!/bin/bash

# ============================================================================
# SCRIPT DE PRUEBA DE INTEGRACIÓN CHECKMK
# Verifica que CheckMK esté funcionando y exportando métricas
# ============================================================================

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

echo -e "${CYAN}═══════════════════════════════════════════════════════════════════${NC}"
echo -e "${CYAN}  PRUEBA DE INTEGRACIÓN CHECKMK - ENSURANCE PHARMACY${NC}"
echo -e "${CYAN}═══════════════════════════════════════════════════════════════════${NC}\n"

# Test 1: Verificar que CheckMK está corriendo
echo -e "${BLUE}TEST 1: Verificar contenedor CheckMK${NC}"
if docker ps | grep -q "ensurance-checkmk-full"; then
    echo -e "${GREEN}  ✓ CheckMK está corriendo${NC}"
else
    echo -e "${RED}  ✗ CheckMK NO está corriendo${NC}"
    exit 1
fi

# Test 2: Verificar puertos expuestos
echo -e "\n${BLUE}TEST 2: Verificar puertos expuestos${NC}"
PORTS=$(docker port ensurance-checkmk-full)
echo "$PORTS" | grep -q "5000" && echo -e "${GREEN}  ✓ Puerto 5152 (Web UI) expuesto${NC}" || echo -e "${YELLOW}  ⚠ Puerto 5152 no expuesto${NC}"
echo "$PORTS" | grep -q "6557" && echo -e "${GREEN}  ✓ Puerto 6557 (Agent) expuesto${NC}" || echo -e "${YELLOW}  ⚠ Puerto 6557 no expuesto${NC}"
echo "$PORTS" | grep -q "9999" && echo -e "${GREEN}  ✓ Puerto 9999 (Prometheus) expuesto${NC}" || echo -e "${YELLOW}  ⚠ Puerto 9999 no expuesto${NC}"

# Test 3: Verificar que Prometheus puede scrape CheckMK
echo -e "\n${BLUE}TEST 3: Verificar scrape de Prometheus${NC}"
if curl -s http://localhost:9090/api/v1/targets | jq -r '.data.activeTargets[] | select(.labels.job=="checkmk-exporter")' | grep -q "checkmk"; then
    echo -e "${GREEN}  ✓ Prometheus tiene configurado el target de CheckMK${NC}"
else
    echo -e "${YELLOW}  ⚠ CheckMK no está en los targets de Prometheus (aún)${NC}"
fi

# Test 4: Verificar métricas de CheckMK en Prometheus
echo -e "\n${BLUE}TEST 4: Verificar métricas de CheckMK${NC}"
METRICS=$(curl -s http://localhost:9090/api/v1/label/__name__/values | jq -r '.data[]' | grep -c "checkmk" || echo "0")
if [ "$METRICS" -gt 0 ]; then
    echo -e "${GREEN}  ✓ Se encontraron $METRICS métricas de CheckMK en Prometheus${NC}"
else
    echo -e "${YELLOW}  ⚠ No se encontraron métricas de CheckMK aún (puede tomar unos minutos)${NC}"
fi

# Test 5: Verificar volúmenes montados
echo -e "\n${BLUE}TEST 5: Verificar volúmenes de configuración${NC}"
if docker inspect ensurance-checkmk-full | grep -q "monitoring/checkmk/config"; then
    echo -e "${GREEN}  ✓ Volumen de configuración montado${NC}"
else
    echo -e "${YELLOW}  ⚠ Volumen de configuración no montado${NC}"
fi

# Test 6: Verificar logs de CheckMK
echo -e "\n${BLUE}TEST 6: Verificar logs de CheckMK${NC}"
ERRORS=$(docker logs ensurance-checkmk-full 2>&1 | grep -i "error" | grep -v "Starting" | wc -l)
if [ "$ERRORS" -eq 0 ]; then
    echo -e "${GREEN}  ✓ No se encontraron errores en los logs${NC}"
else
    echo -e "${YELLOW}  ⚠ Se encontraron $ERRORS posibles errores en los logs${NC}"
fi

# Test 7: Comparar con Netdata
echo -e "\n${BLUE}TEST 7: Comparar con Netdata${NC}"
if docker ps | grep -q "ensurance-netdata-full"; then
    echo -e "${GREEN}  ✓ Netdata está corriendo (para comparación)${NC}"
    echo -e "  ${CYAN}Netdata UI:${NC} http://localhost:19999"
    echo -e "  ${CYAN}CheckMK UI:${NC} http://localhost:5152/ensurance/check_mk/"
else
    echo -e "${YELLOW}  ⚠ Netdata no está corriendo${NC}"
fi

# Test 8: Verificar dashboard de Grafana
echo -e "\n${BLUE}TEST 8: Verificar dashboard de CheckMK en Grafana${NC}"
if [ -f "monitoring/grafana/dashboards/checkmk-dashboard.json" ]; then
    echo -e "${GREEN}  ✓ Dashboard de CheckMK creado${NC}"
    echo -e "  ${CYAN}Archivo:${NC} monitoring/grafana/dashboards/checkmk-dashboard.json"
else
    echo -e "${RED}  ✗ Dashboard de CheckMK no encontrado${NC}"
fi

# Resumen
echo -e "\n${CYAN}═══════════════════════════════════════════════════════════════════${NC}"
echo -e "${CYAN}  RESUMEN${NC}"
echo -e "${CYAN}═══════════════════════════════════════════════════════════════════${NC}\n"

echo -e "${BLUE}Servicios de Monitoreo:${NC}"
echo -e "  • Netdata:    http://localhost:19999"
echo -e "  • CheckMK:    http://localhost:5152/ensurance/check_mk/"
echo -e "  • Prometheus: http://localhost:9090"
echo -e "  • Grafana:    http://localhost:3302"

echo -e "\n${BLUE}Credenciales CheckMK:${NC}"
echo -e "  • Usuario: cmkadmin"
echo -e "  • Password: admin123"

echo -e "\n${BLUE}Métricas Monitoreadas (igual que Netdata):${NC}"
echo -e "  • CPU Usage"
echo -e "  • Memory Usage"
echo -e "  • Disk Usage"
echo -e "  • Network Traffic"
echo -e "  • Service States"

echo -e "\n${BLUE}Próximos Pasos:${NC}"
echo -e "  1. Acceder a CheckMK: http://localhost:5152/ensurance/check_mk/"
echo -e "  2. Configurar hosts manualmente desde la UI"
echo -e "  3. Esperar ~5 minutos para que las métricas aparezcan en Prometheus"
echo -e "  4. Ver dashboard en Grafana: http://localhost:3302"

echo -e "\n${GREEN}✓ Prueba de integración completada${NC}\n"
