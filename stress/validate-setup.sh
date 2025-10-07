#!/bin/bash
# Script para validar que todo está listo para ejecutar stress tests

set -e

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

ERRORS=0
WARNINGS=0

print_header() {
    echo -e "${BLUE}=================================${NC}"
    echo -e "${BLUE}  Validación de Setup${NC}"
    echo -e "${BLUE}  Ensurance Stress Testing${NC}"
    echo -e "${BLUE}=================================${NC}"
    echo ""
}

check_pass() {
    echo -e "${GREEN}✓${NC} $1"
}

check_fail() {
    echo -e "${RED}✗${NC} $1"
    ERRORS=$((ERRORS + 1))
}

check_warn() {
    echo -e "${YELLOW}⚠${NC} $1"
    WARNINGS=$((WARNINGS + 1))
}

# Header
print_header

# 1. Check Docker
echo -e "${BLUE}[1/8]${NC} Verificando Docker..."
if command -v docker &> /dev/null; then
    check_pass "Docker está instalado"
    if docker ps &> /dev/null; then
        check_pass "Docker daemon está corriendo"
    else
        check_fail "Docker daemon no está corriendo"
    fi
else
    check_fail "Docker no está instalado"
fi
echo ""

# 2. Check docker-compose
echo -e "${BLUE}[2/8]${NC} Verificando Docker Compose..."
if command -v docker-compose &> /dev/null; then
    check_pass "Docker Compose está instalado"
elif docker compose version &> /dev/null; then
    check_pass "Docker Compose (plugin) está instalado"
else
    check_fail "Docker Compose no está instalado"
fi
echo ""

# 3. Check directories and files
echo -e "${BLUE}[3/8]${NC} Verificando estructura de archivos..."
if [ -d "../scripts" ]; then
    check_pass "Directorio scripts existe"
else
    check_fail "Directorio scripts no encontrado"
fi

if [ -f "../scripts/docker-compose.stress.yml" ]; then
    check_pass "docker-compose.stress.yml existe"
else
    check_fail "docker-compose.stress.yml no encontrado"
fi

if [ -f "../scripts/docker-compose.monitor.yml" ]; then
    check_pass "docker-compose.monitor.yml existe"
else
    check_fail "docker-compose.monitor.yml no encontrado"
fi

if [ -d "./k6/scripts" ]; then
    check_pass "Directorio k6/scripts existe"
    
    # Check K6 scripts
    if [ -f "./k6/scripts/load-test.js" ]; then
        check_pass "Script load-test.js existe"
    else
        check_warn "Script load-test.js no encontrado"
    fi
else
    check_fail "Directorio k6/scripts no encontrado"
fi

if [ -d "./jmeter/test-plans" ]; then
    check_pass "Directorio jmeter/test-plans existe"
    
    # Check JMeter plans
    if [ -f "./jmeter/test-plans/ensurance-full-test.jmx" ]; then
        check_pass "Plan ensurance-full-test.jmx existe"
    else
        check_warn "Plan ensurance-full-test.jmx no encontrado"
    fi
else
    check_fail "Directorio jmeter/test-plans no encontrado"
fi
echo ""

# 4. Check backend connectivity
echo -e "${BLUE}[4/8]${NC} Verificando conectividad de backends..."
BACKV4_URL=${BACKV4_URL:-http://localhost:3002}
BACKV5_URL=${BACKV5_URL:-http://localhost:3003}

if curl -sf "${BACKV4_URL}/api/users" &> /dev/null; then
    check_pass "BackV4 responde en ${BACKV4_URL}"
else
    check_warn "BackV4 no responde en ${BACKV4_URL} (puedes configurar BACKV4_URL)"
fi

if curl -sf "${BACKV5_URL}/api2/users" &> /dev/null; then
    check_pass "BackV5 responde en ${BACKV5_URL}"
else
    check_warn "BackV5 no responde en ${BACKV5_URL} (puedes configurar BACKV5_URL)"
fi
echo ""

# 5. Check ports availability
echo -e "${BLUE}[5/8]${NC} Verificando disponibilidad de puertos..."
check_port() {
    local port=$1
    local service=$2
    if lsof -Pi :$port -sTCP:LISTEN -t &> /dev/null; then
        check_warn "Puerto $port (${service}) está en uso"
    else
        check_pass "Puerto $port (${service}) disponible"
    fi
}

check_port 5665 "K6 Dashboard"
check_port 3300 "Grafana"
check_port 9095 "Prometheus"
check_port 9091 "Pushgateway"
echo ""

# 6. Check executable scripts
echo -e "${BLUE}[6/8]${NC} Verificando permisos de scripts..."
if [ -x "./run-tests.sh" ]; then
    check_pass "run-tests.sh es ejecutable"
else
    check_warn "run-tests.sh no es ejecutable (ejecuta: chmod +x run-tests.sh)"
fi

if [ -x "./view-jmeter-report.sh" ]; then
    check_pass "view-jmeter-report.sh es ejecutable"
else
    check_warn "view-jmeter-report.sh no es ejecutable"
fi

if [ -x "./cleanup-results.sh" ]; then
    check_pass "cleanup-results.sh es ejecutable"
else
    check_warn "cleanup-results.sh no es ejecutable"
fi
echo ""

# 7. Check Grafana provisioning
echo -e "${BLUE}[7/8]${NC} Verificando configuración de Grafana..."
if [ -f "../monitoring/grafana/dashboards/k6-dashboard.json" ]; then
    check_pass "Dashboard K6 existe"
else
    check_warn "Dashboard K6 no encontrado"
fi

if [ -d "../monitoring/grafana/provisioning" ]; then
    check_pass "Directorio de provisioning existe"
else
    check_warn "Directorio de provisioning no encontrado"
fi
echo ""

# 8. Check Prometheus config
echo -e "${BLUE}[8/8]${NC} Verificando configuración de Prometheus..."
if [ -f "../monitoring/prometheus/prometheus.yml" ]; then
    check_pass "prometheus.yml existe"
    
    if grep -q "k6-stress-test" "../monitoring/prometheus/prometheus.yml"; then
        check_pass "Job k6-stress-test configurado en Prometheus"
    else
        check_warn "Job k6-stress-test no encontrado en prometheus.yml"
    fi
else
    check_warn "prometheus.yml no encontrado"
fi
echo ""

# Summary
echo -e "${BLUE}=================================${NC}"
echo -e "${BLUE}  Resumen${NC}"
echo -e "${BLUE}=================================${NC}"

if [ $ERRORS -eq 0 ] && [ $WARNINGS -eq 0 ]; then
    echo -e "${GREEN}✓ Todo está listo!${NC}"
    echo ""
    echo "Puedes ejecutar los tests con:"
    echo "  ./run-tests.sh"
    exit 0
elif [ $ERRORS -eq 0 ]; then
    echo -e "${YELLOW}⚠ Setup completo con $WARNINGS advertencias${NC}"
    echo ""
    echo "Puedes continuar, pero revisa las advertencias arriba."
    echo ""
    echo "Para ejecutar tests:"
    echo "  ./run-tests.sh"
    exit 0
else
    echo -e "${RED}✗ Encontrados $ERRORS errores y $WARNINGS advertencias${NC}"
    echo ""
    echo "Por favor corrige los errores antes de continuar."
    exit 1
fi
