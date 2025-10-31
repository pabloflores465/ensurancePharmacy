#!/bin/bash

# ============================================
# START DOCKER FULL - ENSURANCE PHARMACY
# Levanta todos los servicios con Docker
# ============================================

set -e

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
NC='\033[0m' # No Color

# Directorio del script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Funciones de impresión
print_header() {
    echo ""
    echo -e "${CYAN}============================================${NC}"
    echo -e "${CYAN}$1${NC}"
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
    echo -e "${MAGENTA}[INFO]${NC} $1"
}

# Header
clear
print_header "🚀 ENSURANCE PHARMACY - INICIO COMPLETO"

# Verificar Docker
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

# Verificar Docker Compose
print_step "Verificando Docker Compose..."
if ! docker compose version &> /dev/null; then
    print_error "Docker Compose no está disponible"
    exit 1
fi
print_success "Docker Compose está listo"
echo ""

# Opción de limpieza
print_warning "¿Deseas detener y limpiar contenedores existentes? (y/N)"
read -r -n 1 -t 10 response || response='n'
echo ""
if [[ "$response" =~ ^([yY])$ ]]; then
    print_step "Deteniendo y limpiando contenedores existentes..."
    docker compose -f docker-compose.full.yml down -v 2>/dev/null || true
    print_success "Contenedores detenidos y volúmenes limpiados"
else
    print_step "Deteniendo contenedores existentes (manteniendo volúmenes)..."
    docker compose -f docker-compose.full.yml down 2>/dev/null || true
    print_success "Contenedores detenidos"
fi
echo ""

# Build de imágenes
print_step "Construyendo imágenes Docker..."
print_info "Esto puede tomar varios minutos..."
docker compose -f docker-compose.full.yml build --no-cache

if [ $? -eq 0 ]; then
    print_success "Imágenes construidas exitosamente"
else
    print_error "Error construyendo imágenes"
    exit 1
fi
echo ""

# Iniciar servicios
print_step "Iniciando todos los servicios..."
echo ""

print_info "📦 Servicios que se iniciarán:"
echo "  • Aplicación (Frontends + Backends + Métricas)"
echo "  • Monitoreo (Prometheus, Grafana, CheckMK)"
echo "  • CI/CD (Jenkins, SonarQube, Drone)"
echo "  • Herramientas (Portainer, Reportes)"
echo ""

docker compose -f docker-compose.full.yml up -d

if [ $? -eq 0 ]; then
    print_success "Todos los servicios iniciados"
else
    print_error "Error iniciando servicios"
    exit 1
fi
echo ""

# Esperar inicialización
print_step "Esperando inicialización de servicios..."
print_info "Esto puede tomar entre 30-60 segundos..."
echo ""

# Progress bar
for i in {1..30}; do
    echo -ne "${BLUE}▓${NC}"
    sleep 1
done
echo ""
echo ""

# Verificar estado
print_step "Verificando estado de los servicios..."
echo ""

# Función para verificar endpoint
check_endpoint() {
    local url=$1
    local name=$2
    local max_attempts=3
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -sf "$url" > /dev/null 2>&1; then
            echo -e "  ${GREEN}✓${NC} $name: ${GREEN}OK${NC}"
            return 0
        fi
        attempt=$((attempt + 1))
        [ $attempt -le $max_attempts ] && sleep 2
    done
    echo -e "  ${YELLOW}⚠${NC} $name: ${YELLOW}Iniciando...${NC}"
    return 1
}

echo -e "${CYAN}Aplicación:${NC}"
check_endpoint "http://localhost:3100" "Frontend Ensurance"
check_endpoint "http://localhost:3101" "Frontend Pharmacy"
check_endpoint "http://localhost:3102" "Backend Ensurance (backv4)"
check_endpoint "http://localhost:3103" "Backend Pharmacy (backv5)"

echo ""
echo -e "${CYAN}Métricas Prometheus:${NC}"
check_endpoint "http://localhost:9470/metrics" "BackV5 Métricas"
check_endpoint "http://localhost:9471/metrics" "BackV4 Métricas"
check_endpoint "http://localhost:9472/metrics" "Ensurance Frontend Métricas"
check_endpoint "http://localhost:9473/metrics" "Pharmacy Frontend Métricas"

echo ""
echo -e "${CYAN}Monitoreo:${NC}"
check_endpoint "http://localhost:9090" "Prometheus"
check_endpoint "http://localhost:3302" "Grafana"
check_endpoint "http://localhost:5152" "CheckMK"

echo ""
echo -e "${CYAN}CI/CD:${NC}"
check_endpoint "http://localhost:8080" "Jenkins"
check_endpoint "http://localhost:9000" "SonarQube"
check_endpoint "http://localhost:8002" "Drone"

echo ""
echo -e "${CYAN}Herramientas:${NC}"
check_endpoint "https://localhost:60002" "Portainer"
check_endpoint "http://localhost:15674" "RabbitMQ Management"

echo ""

# Mostrar resumen
print_header "✅ TODOS LOS SERVICIOS ESTÁN CORRIENDO"

echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}📱 APLICACIÓN${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo "  • Frontend Ensurance:  http://localhost:3100"
echo "  • Frontend Pharmacy:   http://localhost:3101"
echo "  • Backend Ensurance:   http://localhost:3102/api"
echo "  • Backend Pharmacy:    http://localhost:3103/api2"
echo ""

echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${CYAN}📊 MÉTRICAS PROMETHEUS${NC}"
echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo "  • BackV5 Métricas:     http://localhost:9470/metrics"
echo "  • BackV4 Métricas:     http://localhost:9471/metrics"
echo "  • Ensurance Métricas:  http://localhost:9472/metrics"
echo "  • Pharmacy Métricas:   http://localhost:9473/metrics"
echo ""

echo -e "${MAGENTA}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${MAGENTA}📈 MONITOREO${NC}"
echo -e "${MAGENTA}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo "  • Prometheus:          http://localhost:9090"
echo "  • Grafana:             http://localhost:3302"
echo "    └─ Usuario: admin / Contraseña: changeme"
echo "  • CheckMK:             http://localhost:5152"
echo "    └─ Usuario: cmkadmin / Contraseña: changeme"
echo "  • Pushgateway:         http://localhost:9093"
echo "  • Node Exporter:       http://localhost:9102"
echo ""

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}🔧 CI/CD${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo "  • Jenkins:             http://localhost:8080"
echo "  • SonarQube:           http://localhost:9000"
echo "    └─ Usuario: admin / Contraseña: admin"
echo "  • Drone:               http://localhost:8002"
echo ""

echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}🛠️  HERRAMIENTAS${NC}"
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo "  • Portainer:           https://localhost:60002"
echo "    └─ Puerto Web UI: 60003"
echo "  • RabbitMQ Management:  http://localhost:15674"
echo "    └─ Usuario: admin / Contraseña: changeme"
echo "    └─ AMQP Port: 5674"
echo "  • K6 Reports:          http://localhost:5668"
echo "  • JMeter Reports:      http://localhost:8086"
echo ""

echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}💡 COMANDOS ÚTILES${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo "  • Ver logs:            docker compose -f docker-compose.full.yml logs -f"
echo "  • Ver logs servicio:   docker compose -f docker-compose.full.yml logs -f <servicio>"
echo "  • Estado servicios:    docker compose -f docker-compose.full.yml ps"
echo "  • Detener todo:        docker compose -f docker-compose.full.yml down"
echo "  • Reiniciar servicio:  docker compose -f docker-compose.full.yml restart <servicio>"
echo "  • Rebuilo servicio:    docker compose -f docker-compose.full.yml up -d --build <servicio>"
echo ""

echo -e "${YELLOW}⚠️  NOTAS IMPORTANTES${NC}"
echo "  • Algunos servicios (Grafana, CheckMK, Jenkins) pueden tardar 1-2 minutos en estar listos"
echo "  • Las métricas se exponen en los puertos 9464-9467"
echo "  • Prometheus está configurado para scrape automático de métricas"
echo "  • Los volúmenes persisten los datos entre reinicios"
echo ""

print_success "Sistema completamente operativo"
echo ""
