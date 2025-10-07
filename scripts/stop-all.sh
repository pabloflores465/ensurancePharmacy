#!/bin/bash
# Script para detener todos los servicios de Ensurance Pharmacy

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

print_header() {
    echo -e "${CYAN}============================================${NC}"
    echo -e "${CYAN}   Ensurance Pharmacy - Stop All Services${NC}"
    echo -e "${CYAN}============================================${NC}"
    echo ""
}

print_step() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[✓]${NC} $1"
}

print_header

# Stop Application
print_step "Deteniendo aplicación..."
docker compose -f docker-compose.dev.yml down 2>/dev/null || true
print_success "Aplicación detenida"

# Stop Monitoring
print_step "Deteniendo stack de monitoreo..."
docker compose -f docker-compose.monitor.yml down 2>/dev/null || true
print_success "Monitoreo detenido"

# Stop Stress Testing
print_step "Deteniendo tests de estrés..."
docker compose -f docker-compose.stress.yml down 2>/dev/null || true
print_success "Tests detenidos"

echo ""
echo -e "${GREEN}✓ Todos los servicios han sido detenidos${NC}"
echo ""
echo -e "${YELLOW}Nota:${NC} CI/CD (Jenkins, SonarQube, Drone) se mantienen corriendo"
echo "      Para detenerlos usa: docker compose -f docker-compose.cicd.yml down"
echo ""
