#!/bin/bash

# Script para iniciar el sistema de monitoreo
# Detecta si las aplicaciones ya están corriendo y decide si levantar instancias propias

set -e

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Banner
echo -e "${BLUE}╔════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║   Ensurance Pharmacy - Monitoring System Starter      ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════╝${NC}"
echo ""

# Función para verificar si un puerto está en uso
check_port() {
    local port=$1
    if nc -z localhost "$port" 2>/dev/null || lsof -i:"$port" >/dev/null 2>&1; then
        return 0  # Puerto en uso
    else
        return 1  # Puerto libre
    fi
}

# Función para verificar si un servicio de métricas está respondiendo
check_metrics_endpoint() {
    local port=$1
    if curl -s "http://localhost:$port/metrics" >/dev/null 2>&1; then
        return 0  # Endpoint responde
    else
        return 1  # No responde
    fi
}

echo -e "${YELLOW}📊 Verificando estado de las aplicaciones...${NC}"
echo ""

# Verificar puertos de aplicación
APP_BACKV4_RUNNING=false
APP_BACKV5_RUNNING=false
APP_ENSURANCE_RUNNING=false
APP_PHARMACY_RUNNING=false

# Verificar puertos de métricas
METRICS_BACKV4_RUNNING=false
METRICS_BACKV5_RUNNING=false
METRICS_ENSURANCE_RUNNING=false
METRICS_PHARMACY_RUNNING=false

# Verificar backends
if check_port 3002; then
    echo -e "${GREEN}✅ Backend v4 (Ensurance) detectado en puerto 3002${NC}"
    APP_BACKV4_RUNNING=true
fi

if check_port 3003; then
    echo -e "${GREEN}✅ Backend v5 (Pharmacy) detectado en puerto 3003${NC}"
    APP_BACKV5_RUNNING=true
fi

# Verificar frontends
if check_port 3000; then
    echo -e "${GREEN}✅ Frontend Ensurance detectado en puerto 3000${NC}"
    APP_ENSURANCE_RUNNING=true
fi

if check_port 3001; then
    echo -e "${GREEN}✅ Frontend Pharmacy detectado en puerto 3001${NC}"
    APP_PHARMACY_RUNNING=true
fi

echo ""
echo -e "${YELLOW}📈 Verificando endpoints de métricas...${NC}"
echo ""

# Verificar métricas
if check_metrics_endpoint 9465; then
    echo -e "${GREEN}✅ Métricas de Backend v4 disponibles en puerto 9465${NC}"
    METRICS_BACKV4_RUNNING=true
fi

if check_metrics_endpoint 9464; then
    echo -e "${GREEN}✅ Métricas de Backend v5 disponibles en puerto 9464${NC}"
    METRICS_BACKV5_RUNNING=true
fi

if check_metrics_endpoint 9466; then
    echo -e "${GREEN}✅ Métricas de Frontend Ensurance disponibles en puerto 9466${NC}"
    METRICS_ENSURANCE_RUNNING=true
fi

if check_metrics_endpoint 9467; then
    echo -e "${GREEN}✅ Métricas de Frontend Pharmacy disponibles en puerto 9467${NC}"
    METRICS_PHARMACY_RUNNING=true
fi

echo ""
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

# Decidir modo de inicio basado en si las APPS están corriendo (no las métricas)
APPS_NEEDED=true

if [ "$APP_BACKV4_RUNNING" = true ] && [ "$APP_BACKV5_RUNNING" = true ] && \
   [ "$APP_ENSURANCE_RUNNING" = true ] && [ "$APP_PHARMACY_RUNNING" = true ]; then
    APPS_NEEDED=false
fi

# Mostrar resumen y decisión
if [ "$APPS_NEEDED" = true ]; then
    echo -e "${YELLOW}⚠️  No todas las instancias están corriendo${NC}"
    echo -e "${BLUE}📦 Se levantarán instancias propias para monitoreo${NC}"
    echo ""
    echo -e "${YELLOW}Puertos que se expondrán:${NC}"
    echo -e "  - ${BLUE}3000${NC}: Ensurance Frontend"
    echo -e "  - ${BLUE}3001${NC}: Pharmacy Frontend"
    echo -e "  - ${BLUE}3002${NC}: Backend v4 (Ensurance)"
    echo -e "  - ${BLUE}3003${NC}: Backend v5 (Pharmacy)"
    echo -e "  - ${BLUE}9464-9467${NC}: Endpoints de métricas"
    echo ""
    
    # Preguntar confirmación
    read -p "$(echo -e ${YELLOW}¿Continuar con el levantamiento? [Y/n]: ${NC})" -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]] && [[ ! -z $REPLY ]]; then
        echo -e "${RED}❌ Cancelado por el usuario${NC}"
        exit 1
    fi
    
    echo ""
    echo -e "${GREEN}🚀 Iniciando monitoreo CON aplicaciones...${NC}"
    docker compose -f docker-compose.monitor.yml --profile with-apps up -d
else
    echo -e "${GREEN}✅ Todas las instancias necesarias están corriendo${NC}"
    echo -e "${BLUE}📊 Prometheus monitoreará las instancias existentes${NC}"
    echo ""
    echo -e "${YELLOW}Instancias detectadas:${NC}"
    [ "$APP_BACKV4_RUNNING" = true ] && echo -e "  - ${GREEN}✓${NC} Backend v4 (puerto 3002)"
    [ "$APP_BACKV5_RUNNING" = true ] && echo -e "  - ${GREEN}✓${NC} Backend v5 (puerto 3003)"
    [ "$APP_ENSURANCE_RUNNING" = true ] && echo -e "  - ${GREEN}✓${NC} Frontend Ensurance (puerto 3000)"
    [ "$APP_PHARMACY_RUNNING" = true ] && echo -e "  - ${GREEN}✓${NC} Frontend Pharmacy (puerto 3001)"
    echo ""
    echo -e "${GREEN}🚀 Iniciando monitoreo SIN aplicaciones...${NC}"
    docker compose -f docker-compose.monitor.yml up -d
fi

echo ""
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

# Esperar a que los servicios inicien
echo -e "${YELLOW}⏳ Esperando a que los servicios inicien...${NC}"
sleep 5

# Verificar servicios levantados
echo ""
echo -e "${GREEN}✅ Servicios de monitoreo iniciados!${NC}"
echo ""
echo -e "${BLUE}📊 Acceso a los servicios:${NC}"
echo -e "  - ${GREEN}Grafana${NC}:     http://localhost:3300"
echo -e "    ${YELLOW}User${NC}: admin / ${YELLOW}Pass${NC}: changeme"
echo ""
echo -e "  - ${GREEN}Prometheus${NC}:  http://localhost:9095"
echo -e "  - ${GREEN}CheckMK${NC}:     http://localhost:5150"
echo -e "    ${YELLOW}Site${NC}: ensurance / ${YELLOW}Pass${NC}: changeme"
echo ""
echo -e "  - ${GREEN}Pushgateway${NC}: http://localhost:9091"
echo ""

if [ "$APPS_NEEDED" = true ]; then
    echo -e "${BLUE}🎯 Aplicaciones levantadas:${NC}"
    echo -e "  - ${GREEN}Ensurance Frontend${NC}: http://localhost:3000"
    echo -e "  - ${GREEN}Pharmacy Frontend${NC}:  http://localhost:3001"
    echo -e "  - ${GREEN}Backend v4${NC}:         http://localhost:3002"
    echo -e "  - ${GREEN}Backend v5${NC}:         http://localhost:3003"
    echo ""
fi

echo -e "${BLUE}📈 Endpoints de métricas:${NC}"
echo -e "  - ${GREEN}Backend v5${NC}:           http://localhost:9464/metrics"
echo -e "  - ${GREEN}Backend v4${NC}:           http://localhost:9465/metrics"
echo -e "  - ${GREEN}Ensurance Frontend${NC}:   http://localhost:9466/metrics"
echo -e "  - ${GREEN}Pharmacy Frontend${NC}:    http://localhost:9467/metrics"
echo ""
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo -e "${GREEN}🎉 Sistema de monitoreo listo!${NC}"
echo ""
echo -e "${YELLOW}💡 Comandos útiles:${NC}"
echo -e "  - Ver logs:    ${BLUE}docker compose -f docker-compose.monitor.yml logs -f${NC}"
echo -e "  - Detener:     ${BLUE}docker compose -f docker-compose.monitor.yml down${NC}"
echo -e "  - Reiniciar:   ${BLUE}./start-monitoring.sh${NC}"
echo ""
