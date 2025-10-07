#!/bin/bash
# Script para levantar todos los servicios de Ensurance Pharmacy

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

# Functions
print_header() {
    echo -e "${CYAN}============================================${NC}"
    echo -e "${CYAN}   Ensurance Pharmacy - Start All Services${NC}"
    echo -e "${CYAN}============================================${NC}"
    echo ""
}

print_step() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[✓]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[!]${NC} $1"
}

print_error() {
    echo -e "${RED}[✗]${NC} $1"
}

print_info() {
    echo -e "${CYAN}[INFO]${NC} $1"
}

# Header
print_header

# Check Docker
print_step "Verificando Docker..."
if ! command -v docker &> /dev/null; then
    print_error "Docker no está instalado"
    exit 1
fi

if ! docker ps &> /dev/null; then
    print_error "Docker daemon no está corriendo"
    exit 1
fi
print_success "Docker está listo"
echo ""

# Stop any existing containers
print_step "Deteniendo contenedores existentes..."
docker compose -f docker-compose.dev.yml down 2>/dev/null || true
docker compose -f docker-compose.monitor.yml down 2>/dev/null || true
print_success "Contenedores detenidos"
echo ""

# Start Application (Development)
print_step "Iniciando aplicación (Desarrollo)..."
print_info "  - BackV4 (Ensurance Backend): Puerto 3002"
print_info "  - BackV5 (Pharmacy Backend): Puerto 3003"
print_info "  - Frontend Ensurance: Puerto 3000"
print_info "  - Frontend Pharmacy: Puerto 3001"
echo ""

docker compose -f docker-compose.dev.yml up -d

# Wait for health check
print_info "Esperando que la aplicación esté lista..."
sleep 10

# Check health
print_step "Verificando salud de la aplicación..."
if docker ps | grep -q "ensurance-pharmacy-dev.*healthy"; then
    print_success "Aplicación está saludable"
else
    print_warning "Aplicación iniciando... (puede tardar unos segundos)"
fi
echo ""

# Start Monitoring Stack
print_step "Iniciando stack de monitoreo..."
print_info "  - Grafana: Puerto 3300"
print_info "  - Prometheus: Puerto 9095"
print_info "  - CheckMK: Puerto 5150"
print_info "  - Pushgateway: Puerto 9091"
echo ""

docker compose -f docker-compose.monitor.yml up -d

print_info "Esperando que Grafana esté listo..."
sleep 5

print_success "Stack de monitoreo iniciado"
echo ""

# Summary
echo -e "${CYAN}============================================${NC}"
echo -e "${CYAN}   ✓ Todos los servicios están corriendo${NC}"
echo -e "${CYAN}============================================${NC}"
echo ""
echo -e "${GREEN}📱 APLICACIÓN:${NC}"
echo "  • Frontend Ensurance: http://localhost:3000"
echo "  • Frontend Pharmacy:  http://localhost:3001"
echo "  • BackV4 API:         http://localhost:3002/api"
echo "  • BackV5 API:         http://localhost:3003/api2"
echo ""
echo -e "${GREEN}📊 MONITOREO:${NC}"
echo "  • Grafana:            http://localhost:3300 (admin/changeme)"
echo "  • Prometheus:         http://localhost:9095"
echo "  • CheckMK:            http://localhost:5150"
echo ""
echo -e "${GREEN}🔧 CI/CD (Ya corriendo):${NC}"
echo "  • Jenkins:            http://localhost:8080"
echo "  • SonarQube:          http://localhost:9000"
echo "  • Drone:              http://localhost:8000"
echo ""
echo -e "${CYAN}💡 COMANDOS ÚTILES:${NC}"
echo "  • Ver logs:           docker compose -f docker-compose.dev.yml logs -f"
echo "  • Detener todo:       ./stop-all.sh"
echo "  • Ver estado:         docker ps"
echo "  • Tests de stress:    cd ../stress && ./run-tests.sh"
echo ""
echo -e "${YELLOW}⏳ Nota:${NC} Grafana y CheckMK pueden tardar ~30 segundos en estar completamente listos"
echo ""
