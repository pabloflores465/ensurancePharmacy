#!/bin/bash
# Script interactivo para ejecutar stress tests con usuarios configurables
# Mantiene todas las opciones del menu original pero permite customizar usuarios

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/../scripts"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
NC='\033[0m' # No Color

# Función para imprimir con color
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_title() {
    echo -e "${MAGENTA}╔═══════════════════════════════════════════════════════╗${NC}"
    echo -e "${MAGENTA}║${NC}  ${CYAN}$1${NC}${MAGENTA}║${NC}"
    echo -e "${MAGENTA}╚═══════════════════════════════════════════════════════╝${NC}"
}

# Función para verificar que los backends estén corriendo
check_backends() {
    print_info "Verificando conectividad de backends..."
    
    BACKV4_URL=${BACKV4_URL:-http://localhost:3002}
    BACKV5_URL=${BACKV5_URL:-http://localhost:3003}
    
    local backv4_ok=false
    local backv5_ok=false
    
    if curl -sf "${BACKV4_URL}/api/users" > /dev/null 2>&1; then
        print_success "BackV4 está corriendo en ${BACKV4_URL}"
        backv4_ok=true
    else
        print_warning "BackV4 no responde en ${BACKV4_URL}"
    fi
    
    if curl -sf "${BACKV5_URL}/api2/users" > /dev/null 2>&1; then
        print_success "BackV5 está corriendo en ${BACKV5_URL}"
        backv5_ok=true
    else
        print_warning "BackV5 no responde en ${BACKV5_URL}"
    fi
    
    if [ "$backv4_ok" = false ] || [ "$backv5_ok" = false ]; then
        return 1
    fi
    return 0
}

# Función para levantar backends automáticamente
start_backends() {
    print_info "Levantando backends DEV (BackV4:3002, BackV5:3003)..."
    
    # Verificar si el contenedor ya existe
    if docker ps -a --format '{{.Names}}' | grep -q "ensurance-pharmacy-dev"; then
        if docker ps --format '{{.Names}}' | grep -q "ensurance-pharmacy-dev"; then
            print_success "Contenedor ensurance-pharmacy-dev ya está corriendo"
        else
            print_info "Iniciando contenedor existente..."
            docker start ensurance-pharmacy-dev
        fi
    else
        print_info "Creando y levantando contenedor..."
        docker compose -f docker-compose.dev.yml up -d
    fi
    
    # Esperar a que los backends estén listos
    print_info "Esperando a que los backends estén listos (máximo 60s)..."
    local max_attempts=30
    local attempt=0
    
    while [ $attempt -lt $max_attempts ]; do
        if check_backends > /dev/null 2>&1; then
            print_success "¡Backends listos!"
            return 0
        fi
        attempt=$((attempt + 1))
        echo -n "."
        sleep 2
    done
    
    echo ""
    print_error "Timeout esperando backends. Verifica los logs:"
    print_info "  docker logs ensurance-pharmacy-dev"
    return 1
}

# Función para verificar y levantar backends si es necesario
ensure_backends() {
    if ! check_backends; then
        print_warning "Backends no están disponibles"
        read -p "¿Deseas levantarlos automáticamente? (y/n): " response
        if [[ "$response" == "y" ]]; then
            if ! start_backends; then
                print_error "No se pudieron levantar los backends"
                return 1
            fi
        else
            print_error "No se puede continuar sin backends"
            return 1
        fi
    fi
    return 0
}

# Función para verificar si Prometheus está corriendo
check_prometheus() {
    if docker ps --format '{{.Names}}' | grep -q "ensurance-prometheus"; then
        return 0
    fi
    return 1
}

# Función para verificar si Grafana está corriendo
check_grafana() {
    if docker ps --format '{{.Names}}' | grep -q "ensurance-grafana"; then
        return 0
    fi
    return 1
}

# Función para ejecutar K6 con usuarios customizados
run_k6() {
    local test_type=$1
    local test_script="${test_type}-test-custom.js"
    
    # Verificar si Prometheus está corriendo
    if ! check_prometheus; then
        print_warning "Prometheus no está corriendo. Las métricas K6 no se exportarán."
        read -p "¿Deseas levantar Prometheus + Grafana ahora? (y/n): " start_monitoring
        if [[ "$start_monitoring" == "y" ]]; then
            start_grafana
        else
            print_warning "Continuando sin monitoreo. Las métricas no se guardarán en Prometheus."
        fi
    else
        print_success "Prometheus está corriendo. Métricas K6 se exportarán a Prometheus."
    fi
    
    echo ""
    print_title "CONFIGURACIÓN K6 ${test_type^^} TEST"
    echo ""
    read -p "Número de usuarios virtuales (default 50): " users
    users=${users:-50}
    
    echo ""
    print_title "EJECUTANDO K6 ${test_type^^} TEST CON ${users} USUARIOS"
    print_info "Script: ${test_script}"
    print_info "Usuarios: ${users}"
    if check_prometheus; then
        print_info "📊 Métricas exportándose a: Prometheus (http://localhost:9095)"
    fi
    
    # Limpiar resultados anteriores
    print_info "Limpiando resultados anteriores..."
    docker run --rm -v scripts_k6_results:/results alpine sh -c "rm -rf /results/*" 2>/dev/null || true
    
    # Ejecutar test
    TEST_SCRIPT="${test_script}" \
    docker compose -f docker-compose.stress.yml run --rm \
        -e VUS=${users} \
        k6
    
    if [ $? -eq 0 ]; then
        print_success "K6 test completado!"
        echo ""
        print_title "📊 REPORTES DISPONIBLES"
        
        # Levantar servidor de reportes
        print_info "Levantando servidor de reportes K6..."
        docker compose -f docker-compose.stress.yml up -d k6-report
        sleep 2
        
        echo ""
        print_success "✅ Reporte HTML Interactivo: ${CYAN}http://localhost:5666${NC}"
        if check_prometheus; then
            print_success "✅ Métricas en Prometheus: ${CYAN}http://localhost:9095${NC}"
        fi
        if check_grafana; then
            print_success "✅ Dashboards en Grafana: ${CYAN}http://localhost:3300${NC} (admin/changeme)"
        fi
        echo ""
    else
        print_error "K6 test falló"
        return 1
    fi
}

# Función para ejecutar JMeter con configuración customizable
run_jmeter() {
    local test_plan=$1
    
    echo ""
    print_title "CONFIGURACIÓN JMETER TEST"
    echo ""
    read -p "Número de usuarios (default 50): " users
    read -p "Tiempo de rampa en segundos (default 30): " ramp_time
    read -p "Duración del test en segundos (default 300): " duration
    
    users=${users:-50}
    ramp_time=${ramp_time:-30}
    duration=${duration:-300}
    
    echo ""
    print_title "EJECUTANDO JMETER TEST"
    print_info "Plan: ${test_plan}"
    print_info "Usuarios: ${users}"
    print_info "Ramp Time: ${ramp_time}s"
    print_info "Duration: ${duration}s"
    
    # Detener servidor de reportes si está corriendo
    docker compose -f docker-compose.stress.yml stop jmeter-report 2>/dev/null || true
    
    # Limpiar resultados anteriores
    print_info "Limpiando resultados anteriores..."
    docker run --rm -v scripts_jmeter_results:/results alpine sh -c "rm -rf /results/*" 2>/dev/null || true
    
    # Ejecutar test
    JMETER_PLAN="${test_plan}" \
    USERS="${users}" \
    RAMP_TIME="${ramp_time}" \
    DURATION="${duration}" \
    docker compose -f docker-compose.stress.yml run --rm jmeter
    
    if [ $? -eq 0 ]; then
        print_success "JMeter test completado!"
        
        # Levantar servidor de reportes
        print_info "Levantando servidor de reportes JMeter..."
        docker compose -f docker-compose.stress.yml up -d jmeter-report
        sleep 2
        print_success "Reporte JMeter disponible en: ${CYAN}http://localhost:8085${NC}"
    else
        print_error "JMeter test falló"
        return 1
    fi
}

# Función para iniciar Grafana + Prometheus
start_grafana() {
    print_title "INICIANDO MONITOREO (PROMETHEUS + GRAFANA)"
    print_info "Levantando Prometheus + Grafana..."
    docker compose -f docker-compose.monitor.yml up -d
    
    if [ $? -eq 0 ]; then
        print_success "✅ Servicios de monitoreo iniciados!"
        echo ""
        print_info "📊 Prometheus: ${CYAN}http://localhost:9095${NC}"
        print_info "📈 Grafana: ${CYAN}http://localhost:3300${NC} (admin/changeme)"
        print_info "🔄 Pushgateway: ${CYAN}http://localhost:9091${NC}"
        echo ""
        print_info "Esperando a que los servicios estén listos..."
        sleep 10
        print_success "Servicios listos para recibir métricas K6!"
    else
        print_error "Error al iniciar servicios de monitoreo"
        return 1
    fi
}

# Menú principal
show_menu() {
    clear
    echo -e "${CYAN}═══════════════════════════════════════════════${NC}"
    echo -e "${CYAN}     ENSURANCE STRESS TEST MENU (Custom VUS)${NC}"
    echo -e "${CYAN}═══════════════════════════════════════════════${NC}"
    echo ""
    echo -e "${GREEN}K6 Tests (con usuarios configurables):${NC}"
    echo -e "  ${YELLOW}1)${NC} Load Test (carga progresiva)"
    echo -e "  ${YELLOW}2)${NC} Stress Test (hasta límite)"
    echo -e "  ${YELLOW}3)${NC} Spike Test (picos repentinos)"
    echo -e "  ${YELLOW}4)${NC} Soak Test (30 min sostenido)"
    echo ""
    echo -e "${GREEN}JMeter Tests (con usuarios configurables):${NC}"
    echo -e "  ${YELLOW}5)${NC} JMeter Simple Test"
    echo -e "  ${YELLOW}6)${NC} JMeter Full Test"
    echo ""
    echo -e "${GREEN}Utilidades:${NC}"
    echo -e "  ${YELLOW}7)${NC} Iniciar Grafana + Prometheus"
    echo -e "  ${YELLOW}8)${NC} Verificar Backends"
    echo -e "  ${YELLOW}9)${NC} Ver reportes JMeter"
    echo -e " ${YELLOW}10)${NC} Ver reportes K6"
    echo -e " ${YELLOW}11)${NC} Levantar Backends DEV"
    echo -e " ${YELLOW}12)${NC} Detener Backends DEV"
    echo -e "  ${YELLOW}0)${NC} Salir"
    echo ""
    echo -e "${CYAN}═══════════════════════════════════════════════${NC}"
}

# Main loop
main() {
    while true; do
        show_menu
        read -p "Selecciona una opción: " choice
        
        case $choice in
            1)
                ensure_backends && run_k6 "load"
                ;;
            2)
                ensure_backends && run_k6 "stress"
                ;;
            3)
                ensure_backends && run_k6 "spike"
                ;;
            4)
                ensure_backends
                print_warning "Este test dura 30 minutos"
                read -p "¿Continuar? (y/n): " confirm
                if [[ $confirm == "y" ]]; then
                    run_k6 "soak"
                fi
                ;;
            5)
                ensure_backends && run_jmeter "sample-plan.jmx"
                ;;
            6)
                ensure_backends && run_jmeter "ensurance-full-test.jmx"
                ;;
            7)
                start_grafana
                ;;
            8)
                check_backends
                ;;
            9)
                print_info "Iniciando servidor de reportes JMeter..."
                docker compose -f docker-compose.stress.yml up -d jmeter-report
                print_success "Servidor levantado!"
                print_info "Reporte disponible en: http://localhost:8085"
                ;;
            10)
                print_info "Iniciando servidor de reportes K6..."
                docker compose -f docker-compose.stress.yml up -d k6-report
                sleep 2
                print_success "Servidor levantado!"
                echo ""
                print_info "📊 Reporte HTML Interactivo: ${CYAN}http://localhost:5666${NC}"
                print_info "📄 Archivos JSON: ${CYAN}http://localhost:5666/k6-results.json${NC}"
                ;;
            11)
                start_backends
                ;;
            12)
                print_info "Deteniendo backends DEV..."
                docker stop ensurance-pharmacy-dev 2>/dev/null || print_warning "Contenedor no está corriendo"
                print_success "Backends detenidos"
                ;;
            0)
                print_info "Saliendo..."
                exit 0
                ;;
            *)
                print_error "Opción inválida"
                ;;
        esac
        
        echo ""
        read -p "Presiona Enter para continuar..."
    done
}

# Ejecutar main
main
