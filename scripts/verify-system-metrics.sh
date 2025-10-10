#!/bin/bash

# Script de verificación de métricas del sistema con Node Exporter
# Este script verifica que Node Exporter, Prometheus y Grafana estén recopilando métricas del sistema correctamente

set -e

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# URLs
NODE_EXPORTER_URL="http://localhost:9100/metrics"
PROMETHEUS_URL="http://localhost:9095"
GRAFANA_URL="http://localhost:3300"

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  Verificación de Métricas del Sistema${NC}"
echo -e "${BLUE}========================================${NC}\n"

# Función para verificar si un servicio está disponible
check_service() {
    local name=$1
    local url=$2
    local timeout=5
    
    echo -n "Verificando $name... "
    if curl -s --max-time $timeout "$url" > /dev/null 2>&1; then
        echo -e "${GREEN}✓ OK${NC}"
        return 0
    else
        echo -e "${RED}✗ NO DISPONIBLE${NC}"
        return 1
    fi
}

# Función para obtener valor de métrica
get_metric_value() {
    local query=$1
    curl -s "${PROMETHEUS_URL}/api/v1/query?query=${query}" | \
        grep -o '"value":\[[^]]*\]' | \
        sed 's/.*,"\([^"]*\)".*/\1/' | \
        head -n 1
}

# 1. Verificar que Docker Compose está corriendo
echo -e "${YELLOW}[1/6] Verificando contenedores Docker...${NC}"
if docker ps | grep -q "ensurance-node-exporter"; then
    echo -e "${GREEN}✓ Node Exporter está corriendo${NC}"
else
    echo -e "${RED}✗ Node Exporter NO está corriendo${NC}"
    echo -e "${YELLOW}Iniciando servicios de monitoreo...${NC}"
    docker compose -f docker-compose.monitor.yml up -d
    echo "Esperando 10 segundos para que los servicios inicien..."
    sleep 10
fi

if docker ps | grep -q "ensurance-prometheus"; then
    echo -e "${GREEN}✓ Prometheus está corriendo${NC}"
else
    echo -e "${RED}✗ Prometheus NO está corriendo${NC}"
    exit 1
fi

if docker ps | grep -q "ensurance-grafana"; then
    echo -e "${GREEN}✓ Grafana está corriendo${NC}"
else
    echo -e "${RED}✗ Grafana NO está corriendo${NC}"
    exit 1
fi

echo ""

# 2. Verificar acceso a servicios
echo -e "${YELLOW}[2/6] Verificando acceso a servicios...${NC}"
check_service "Node Exporter" "$NODE_EXPORTER_URL"
check_service "Prometheus" "$PROMETHEUS_URL/api/v1/status/config"
check_service "Grafana" "$GRAFANA_URL/api/health"
echo ""

# 3. Verificar que Prometheus está scrapeando Node Exporter
echo -e "${YELLOW}[3/6] Verificando scrape de Prometheus...${NC}"
TARGET_STATUS=$(curl -s "${PROMETHEUS_URL}/api/v1/targets" 2>/dev/null | \
    jq -r '.data.activeTargets[] | select(.labels.job=="node-exporter") | .health' 2>/dev/null | head -n 1)

if [ "$TARGET_STATUS" = "up" ]; then
    echo -e "${GREEN}✓ Prometheus está scrapeando Node Exporter correctamente${NC}"
else
    echo -e "${RED}✗ Prometheus NO está scrapeando Node Exporter (status: $TARGET_STATUS)${NC}"
    echo -e "${YELLOW}Intentando verificar sin jq...${NC}"
    # Fallback sin jq
    if curl -s "${PROMETHEUS_URL}/api/v1/targets" 2>/dev/null | grep -q '"job":"node-exporter".*"health":"up"'; then
        echo -e "${GREEN}✓ Prometheus está scrapeando Node Exporter correctamente${NC}"
    else
        exit 1
    fi
fi
echo ""

# 4. Verificar métricas específicas
echo -e "${YELLOW}[4/6] Verificando métricas del sistema...${NC}"

# CPU
echo -n "  CPU... "
cpu_idle=$(get_metric_value 'node_cpu_seconds_total{mode="idle"}')
if [ -n "$cpu_idle" ]; then
    echo -e "${GREEN}✓ OK${NC}"
else
    echo -e "${RED}✗ NO DISPONIBLE${NC}"
fi

# Memoria
echo -n "  RAM... "
mem_total=$(get_metric_value 'node_memory_MemTotal_bytes')
if [ -n "$mem_total" ]; then
    mem_total_gb=$(echo "scale=2; $mem_total / 1024 / 1024 / 1024" | bc)
    echo -e "${GREEN}✓ OK (Total: ${mem_total_gb} GB)${NC}"
else
    echo -e "${RED}✗ NO DISPONIBLE${NC}"
fi

# Disco
echo -n "  Disco... "
disk_size=$(get_metric_value 'node_filesystem_size_bytes{mountpoint="/"}')
if [ -n "$disk_size" ]; then
    disk_size_gb=$(echo "scale=2; $disk_size / 1024 / 1024 / 1024" | bc)
    echo -e "${GREEN}✓ OK (Tamaño: ${disk_size_gb} GB)${NC}"
else
    echo -e "${RED}✗ NO DISPONIBLE${NC}"
fi

# Red
echo -n "  Red... "
net_rx=$(get_metric_value 'node_network_receive_bytes_total')
if [ -n "$net_rx" ]; then
    echo -e "${GREEN}✓ OK${NC}"
else
    echo -e "${RED}✗ NO DISPONIBLE${NC}"
fi

# Carga del sistema
echo -n "  Carga del sistema... "
load1=$(get_metric_value 'node_load1')
if [ -n "$load1" ]; then
    echo -e "${GREEN}✓ OK (Load: ${load1})${NC}"
else
    echo -e "${RED}✗ NO DISPONIBLE${NC}"
fi

# Uptime
echo -n "  Uptime... "
boot_time=$(get_metric_value 'node_boot_time_seconds')
if [ -n "$boot_time" ]; then
    current_time=$(date +%s)
    uptime_seconds=$((current_time - ${boot_time%.*}))
    uptime_days=$((uptime_seconds / 86400))
    uptime_hours=$(((uptime_seconds % 86400) / 3600))
    echo -e "${GREEN}✓ OK (${uptime_days}d ${uptime_hours}h)${NC}"
else
    echo -e "${RED}✗ NO DISPONIBLE${NC}"
fi

echo ""

# 5. Verificar cantidad de métricas de Node Exporter
echo -e "${YELLOW}[5/6] Estadísticas de métricas...${NC}"
metric_count=$(curl -s "$NODE_EXPORTER_URL" | grep -c "^node_" || echo "0")
echo -e "  Total de métricas de Node Exporter: ${GREEN}$metric_count${NC}"

if [ "$metric_count" -gt 0 ]; then
    echo -e "${GREEN}✓ Node Exporter está exportando métricas${NC}"
else
    echo -e "${RED}✗ No se encontraron métricas${NC}"
fi
echo ""

# 6. Mostrar valores actuales del sistema
echo -e "${YELLOW}[6/6] Valores actuales del sistema:${NC}"

# CPU Usage
cpu_usage=$(curl -s "${PROMETHEUS_URL}/api/v1/query?query=100-avg(rate(node_cpu_seconds_total{mode=\"idle\"}[5m]))*100" | \
    grep -o '"value":\[[^]]*\]' | \
    sed 's/.*,"\([^"]*\)".*/\1/' | \
    head -n 1)
if [ -n "$cpu_usage" ]; then
    cpu_usage_int=${cpu_usage%.*}
    if [ "$cpu_usage_int" -lt 70 ]; then
        color=$GREEN
    elif [ "$cpu_usage_int" -lt 90 ]; then
        color=$YELLOW
    else
        color=$RED
    fi
    echo -e "  Uso de CPU: ${color}${cpu_usage}%${NC}"
fi

# Memory Usage
mem_usage=$(curl -s "${PROMETHEUS_URL}/api/v1/query?query=(1-(node_memory_MemAvailable_bytes/node_memory_MemTotal_bytes))*100" | \
    grep -o '"value":\[[^]]*\]' | \
    sed 's/.*,"\([^"]*\)".*/\1/' | \
    head -n 1)
if [ -n "$mem_usage" ]; then
    mem_usage_int=${mem_usage%.*}
    if [ "$mem_usage_int" -lt 70 ]; then
        color=$GREEN
    elif [ "$mem_usage_int" -lt 90 ]; then
        color=$YELLOW
    else
        color=$RED
    fi
    echo -e "  Uso de RAM: ${color}${mem_usage}%${NC}"
fi

# Disk Usage
disk_usage=$(curl -s "${PROMETHEUS_URL}/api/v1/query?query=100-((node_filesystem_avail_bytes{mountpoint=\"/\"}/node_filesystem_size_bytes{mountpoint=\"/\"})*100)" | \
    grep -o '"value":\[[^]]*\]' | \
    sed 's/.*,"\([^"]*\)".*/\1/' | \
    head -n 1)
if [ -n "$disk_usage" ]; then
    disk_usage_int=${disk_usage%.*}
    if [ "$disk_usage_int" -lt 70 ]; then
        color=$GREEN
    elif [ "$disk_usage_int" -lt 90 ]; then
        color=$YELLOW
    else
        color=$RED
    fi
    echo -e "  Uso de Disco: ${color}${disk_usage}%${NC}"
fi

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}✓ Verificación completada${NC}"
echo -e "${BLUE}========================================${NC}\n"

echo -e "${YELLOW}Accesos:${NC}"
echo -e "  • Grafana:       ${BLUE}http://localhost:3300${NC} (admin/changeme)"
echo -e "  • Prometheus:    ${BLUE}http://localhost:9095${NC}"
echo -e "  • Node Exporter: ${BLUE}http://localhost:9100/metrics${NC}"
echo ""
echo -e "${YELLOW}Dashboard:${NC}"
echo -e "  Busca '${GREEN}Métricas del Sistema${NC}' en Grafana"
echo ""
echo -e "${YELLOW}Queries de ejemplo:${NC}"
echo -e "  ${BLUE}# CPU Usage${NC}"
echo -e "  100 - (avg(rate(node_cpu_seconds_total{mode=\"idle\"}[5m])) * 100)"
echo ""
echo -e "  ${BLUE}# RAM Usage${NC}"
echo -e "  (1 - (node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes)) * 100"
echo ""
echo -e "  ${BLUE}# Disk Usage${NC}"
echo -e "  100 - ((node_filesystem_avail_bytes{mountpoint=\"/\"} * 100) / node_filesystem_size_bytes{mountpoint=\"/\"})"
echo ""
