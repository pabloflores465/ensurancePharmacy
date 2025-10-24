#!/bin/bash

# ============================================
# START DOCKER METRICS - ENSURANCE PHARMACY
# Levanta aplicaciones con métricas en Docker
# Conecta a puertos y usa volúmenes existentes
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
print_header "📊 ENSURANCE PHARMACY - MODO MÉTRICAS"

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
echo ""

# Verificar si hay servicios corriendo
print_step "Verificando servicios existentes..."
RUNNING_CONTAINERS=$(docker ps --filter "name=ensurance-pharmacy-metrics" -q)
if [ ! -z "$RUNNING_CONTAINERS" ]; then
    print_warning "Encontrados contenedores en ejecución"
    print_info "Deteniendo contenedores existentes..."
    docker stop $RUNNING_CONTAINERS
    docker rm $RUNNING_CONTAINERS
    print_success "Contenedores detenidos"
fi
echo ""

# Verificar/Crear volúmenes
print_step "Verificando volúmenes..."
print_info "Usando volúmenes existentes o creando nuevos si no existen"

# Crear volúmenes si no existen
docker volume create ensurance-databases-metrics 2>/dev/null || true
docker volume create ensurance-logs-metrics 2>/dev/null || true

print_success "Volúmenes listos"
echo ""

# Verificar/Crear red
print_step "Configurando red Docker..."
docker network create ensurance-metrics-network 2>/dev/null || true
print_success "Red configurada"
echo ""

# Build de la imagen
print_step "Construyendo imagen con soporte de métricas..."
print_info "Esto puede tomar varios minutos en la primera ejecución..."

docker build \
    --build-arg ENVIRONMENT=dev \
    -t ensurance-pharmacy-metrics:latest \
    -f Dockerfile \
    .

if [ $? -eq 0 ]; then
    print_success "Imagen construida exitosamente"
else
    print_error "Error construyendo imagen"
    exit 1
fi
echo ""

# Iniciar contenedor con métricas
print_step "Iniciando aplicación con métricas habilitadas..."
echo ""

print_info "Configuración de puertos:"
echo "  • Frontend Ensurance:  3000  (interno: 5175)"
echo "  • Frontend Pharmacy:   3001  (interno: 8089)"
echo "  • Backend Ensurance:   3002  (interno: 8081)"
echo "  • Backend Pharmacy:    3003  (interno: 8082)"
echo "  • Métricas BackV5:     9464"
echo "  • Métricas BackV4:     9465"
echo "  • Métricas Ensurance:  9466"
echo "  • Métricas Pharmacy:   9467"
echo ""

docker run -d \
    --name ensurance-pharmacy-metrics \
    --network ensurance-metrics-network \
    -p 3000:5175 \
    -p 3001:8089 \
    -p 3002:8081 \
    -p 3003:8082 \
    -p 9464:9464 \
    -p 9465:9465 \
    -p 9466:9466 \
    -p 9467:9467 \
    -e NODE_ENV=production \
    -e METRICS_HOST=0.0.0.0 \
    -e METRICS_PORT_BACKV5=9464 \
    -e METRICS_PORT_BACKV4=9465 \
    -e METRICS_PORT_ENSURANCE=9466 \
    -e METRICS_PORT_PHARMACY=9467 \
    -e ENVIRONMENT=dev \
    -e JAVA_OPTS="-Xmx512m -Xms256m" \
    -v ensurance-databases-metrics:/app/databases \
    -v ensurance-logs-metrics:/app/logs \
    --restart unless-stopped \
    --health-cmd="curl -f http://localhost:5175 && curl -f http://localhost:8089" \
    --health-interval=30s \
    --health-timeout=10s \
    --health-retries=3 \
    --health-start-period=90s \
    ensurance-pharmacy-metrics:latest

if [ $? -eq 0 ]; then
    print_success "Contenedor iniciado"
else
    print_error "Error iniciando contenedor"
    exit 1
fi
echo ""

# Opción: Iniciar Prometheus y Grafana
print_warning "¿Deseas iniciar también Prometheus y Grafana? (Y/n)"
read -r -n 1 -t 10 response || response='y'
echo ""

if [[ "$response" =~ ^([yY]|^$)$ ]]; then
    print_step "Iniciando stack de monitoreo..."
    echo ""
    
    # Prometheus
    print_info "Iniciando Prometheus..."
    docker run -d \
        --name ensurance-prometheus-metrics \
        --network ensurance-metrics-network \
        -p 9090:9090 \
        -v ensurance-prometheus-data-metrics:/prometheus \
        -v "$SCRIPT_DIR/monitoring/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml:ro" \
        --restart unless-stopped \
        --add-host host.docker.internal:host-gateway \
        prom/prometheus:v2.53.0 \
        --config.file=/etc/prometheus/prometheus.yml \
        --storage.tsdb.path=/prometheus \
        --web.enable-remote-write-receiver \
        --enable-feature=native-histograms
    
    # Grafana
    print_info "Iniciando Grafana..."
    docker run -d \
        --name ensurance-grafana-metrics \
        --network ensurance-metrics-network \
        -p 3300:3000 \
        -e GF_SECURITY_ADMIN_USER=admin \
        -e GF_SECURITY_ADMIN_PASSWORD=changeme \
        -v ensurance-grafana-data-metrics:/var/lib/grafana \
        -v "$SCRIPT_DIR/monitoring/grafana/provisioning:/etc/grafana/provisioning:ro" \
        -v "$SCRIPT_DIR/monitoring/grafana/dashboards:/etc/grafana/provisioning/dashboards:ro" \
        --restart unless-stopped \
        grafana/grafana:11.3.0
    
    print_success "Stack de monitoreo iniciado"
    echo ""
fi

# Esperar inicialización
print_step "Esperando inicialización de servicios..."
print_info "La aplicación puede tomar 30-60 segundos en estar completamente lista..."
echo ""

# Progress bar
for i in {1..20}; do
    echo -ne "${BLUE}▓${NC}"
    sleep 2
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
    local max_attempts=5
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -sf "$url" > /dev/null 2>&1; then
            echo -e "  ${GREEN}✓${NC} $name: ${GREEN}OK${NC}"
            return 0
        fi
        attempt=$((attempt + 1))
        [ $attempt -le $max_attempts ] && sleep 3
    done
    echo -e "  ${YELLOW}⚠${NC} $name: ${YELLOW}Iniciando o no disponible${NC}"
    return 1
}

echo -e "${CYAN}Aplicación:${NC}"
check_endpoint "http://localhost:3000" "Frontend Ensurance"
check_endpoint "http://localhost:3001" "Frontend Pharmacy"
check_endpoint "http://localhost:3002" "Backend Ensurance (backv4)"
check_endpoint "http://localhost:3003" "Backend Pharmacy (backv5)"

echo ""
echo -e "${CYAN}Métricas Prometheus:${NC}"
check_endpoint "http://localhost:9464/metrics" "BackV5 Métricas"
check_endpoint "http://localhost:9465/metrics" "BackV4 Métricas"
check_endpoint "http://localhost:9466/metrics" "Ensurance Frontend Métricas"
check_endpoint "http://localhost:9467/metrics" "Pharmacy Frontend Métricas"

if [[ "$response" =~ ^([yY]|^$)$ ]]; then
    echo ""
    echo -e "${CYAN}Monitoreo:${NC}"
    check_endpoint "http://localhost:9090" "Prometheus"
    check_endpoint "http://localhost:3300" "Grafana"
fi

echo ""

# Ver logs en tiempo real
print_warning "¿Deseas ver los logs en tiempo real? (y/N)"
read -r -n 1 -t 5 log_response || log_response='n'
echo ""

# Mostrar resumen
print_header "✅ SISTEMA CON MÉTRICAS ACTIVO"

echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}📱 APLICACIÓN${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo "  • Frontend Ensurance:  http://localhost:3000"
echo "  • Frontend Pharmacy:   http://localhost:3001"
echo "  • Backend Ensurance:   http://localhost:3002/api"
echo "  • Backend Pharmacy:    http://localhost:3003/api2"
echo ""

echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${CYAN}📊 ENDPOINTS DE MÉTRICAS PROMETHEUS${NC}"
echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo "  • BackV5 (Pharmacy):   http://localhost:9464/metrics"
echo "  • BackV4 (Ensurance):  http://localhost:9465/metrics"
echo "  • Ensurance Frontend:  http://localhost:9466/metrics"
echo "  • Pharmacy Frontend:   http://localhost:9467/metrics"
echo ""

if [[ "$response" =~ ^([yY]|^$)$ ]]; then
    echo -e "${MAGENTA}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${MAGENTA}📈 MONITOREO${NC}"
    echo -e "${MAGENTA}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo "  • Prometheus:          http://localhost:9090"
    echo "    └─ Targets: http://localhost:9090/targets"
    echo "  • Grafana:             http://localhost:3300"
    echo "    └─ Usuario: admin / Contraseña: changeme"
    echo ""
fi

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}💾 VOLÚMENES${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo "  • Databases:           ensurance-databases-metrics"
echo "  • Logs:                ensurance-logs-metrics"
if [[ "$response" =~ ^([yY]|^$)$ ]]; then
    echo "  • Prometheus Data:     ensurance-prometheus-data-metrics"
    echo "  • Grafana Data:        ensurance-grafana-data-metrics"
fi
echo ""

echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}💡 COMANDOS ÚTILES${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo "  • Ver logs aplicación:    docker logs -f ensurance-pharmacy-metrics"
if [[ "$response" =~ ^([yY]|^$)$ ]]; then
    echo "  • Ver logs Prometheus:    docker logs -f ensurance-prometheus-metrics"
    echo "  • Ver logs Grafana:       docker logs -f ensurance-grafana-metrics"
fi
echo "  • Estado contenedores:    docker ps | grep ensurance"
echo "  • Reiniciar aplicación:   docker restart ensurance-pharmacy-metrics"
echo "  • Detener todo:           docker stop ensurance-pharmacy-metrics"
if [[ "$response" =~ ^([yY]|^$)$ ]]; then
    echo "  • Detener monitoreo:      docker stop ensurance-prometheus-metrics ensurance-grafana-metrics"
fi
echo "  • Ver volúmenes:          docker volume ls | grep ensurance"
echo "  • Verificar métricas:     curl http://localhost:9464/metrics"
echo ""

echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}🔍 VERIFICACIÓN RÁPIDA DE MÉTRICAS${NC}"
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo "  • Todas las métricas:     for port in 9464 9465 9466 9467; do echo \"Port \$port:\"; curl -s http://localhost:\$port/metrics | head -5; echo; done"
echo "  • Contar métricas:        curl -s http://localhost:9464/metrics | wc -l"
echo ""

echo -e "${CYAN}⚠️  INTEGRACIÓN CON PROMETHEUS${NC}"
echo "  Las métricas están expuestas en los puertos 9464-9467."
echo "  Asegúrate de que tu prometheus.yml incluye estos targets:"
echo ""
echo "  - job_name: 'backv5'"
echo "    static_configs:"
echo "      - targets: ['host.docker.internal:9464']"
echo ""
echo "  - job_name: 'backv4'"
echo "    static_configs:"
echo "      - targets: ['host.docker.internal:9465']"
echo ""
echo "  (Similar para 9466 y 9467)"
echo ""

print_success "Sistema con métricas completamente operativo"
echo ""

# Mostrar logs si el usuario lo solicitó
if [[ "$log_response" =~ ^([yY])$ ]]; then
    print_info "Mostrando logs en tiempo real (Ctrl+C para salir)..."
    echo ""
    docker logs -f ensurance-pharmacy-metrics
fi
