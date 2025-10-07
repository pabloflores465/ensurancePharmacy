#!/bin/bash
# Script para ejecutar diferentes tipos de stress tests

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/../scripts"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
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

# Función para ejecutar K6
run_k6() {
    local test_script=$1
    print_info "Ejecutando K6 test: ${test_script}"
    
    # Limpiar resultados anteriores de K6
    print_info "Limpiando resultados anteriores..."
    docker run --rm -v scripts_k6_results:/results alpine sh -c "rm -rf /results/*" 2>/dev/null || true
    
    TEST_SCRIPT="${test_script}" docker compose -f docker-compose.stress.yml run --rm k6
    
    print_success "K6 test completado!"
    print_info "Dashboard en tiempo real: http://localhost:5665 (mientras corre)"
    print_info "Resultados en volumen: scripts_k6_results"
}

# Función para ejecutar JMeter
run_jmeter() {
    local test_plan=$1
    print_info "Ejecutando JMeter test: ${test_plan}"
    
    # Limpiar resultados anteriores
    print_info "Limpiando resultados anteriores..."
    docker run --rm -v scripts_jmeter_results:/results alpine sh -c "rm -rf /results/*" 2>/dev/null || true
    
    JMETER_PLAN="${test_plan}" docker compose -f docker-compose.stress.yml run --rm jmeter
    
    print_success "JMeter test completado!"
    print_info "Para ver el reporte HTML, ejecuta:"
    print_info "  cd ../stress && ./view-jmeter-report.sh"
    print_info "  Luego abre: http://localhost:8085"
}

# Función para iniciar Grafana
start_grafana() {
    print_info "Iniciando Grafana + Prometheus..."
    docker compose -f docker-compose.monitor.yml up -d
    print_success "Grafana iniciado!"
    print_info "Accede a: http://localhost:3300 (admin/changeme)"
    print_info "Esperando a que Grafana esté listo..."
    sleep 10
}

# Menú principal
show_menu() {
    echo ""
    echo "==================================="
    echo "   ENSURANCE STRESS TEST MENU"
    echo "==================================="
    echo ""
    echo "K6 Tests:"
    echo "  1) Load Test (carga progresiva)"
    echo "  2) Stress Test (hasta límite)"
    echo "  3) Spike Test (picos repentinos)"
    echo "  4) Soak Test (30 min sostenido)"
    echo "JMeter Tests:"
    echo "  5) JMeter Simple Test"
    echo "  6) JMeter Full Test"
    echo ""
    echo "Utilidades:"
    echo "  7) Iniciar Grafana + Prometheus"
    echo "  8) Verificar Backends"
    echo "  9) Ver reportes JMeter"
    echo " 10) Levantar Backends DEV"
    echo " 11) Detener Backends DEV"
    echo "  0) Salir"
    echo ""
}

# Main loop
main() {
    while true; do
        show_menu
        read -p "Selecciona una opción: " choice
        
        case $choice in
            1)
                ensure_backends && run_k6 "load-test.js"
                ;;
            2)
                ensure_backends && run_k6 "stress-test.js"
                ;;
            3)
                ensure_backends && run_k6 "spike-test.js"
                ;;
            4)
                ensure_backends
                print_warning "Este test dura 30 minutos"
                read -p "¿Continuar? (y/n): " confirm
                if [[ $confirm == "y" ]]; then
                    run_k6 "soak-test.js"
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
                print_info "Iniciando servidor HTTP con reportes JMeter..."
                docker run --rm -v scripts_jmeter_results:/results -p 8085:8085 \
                    -w /results/report python:3.9 python -m http.server 8085
                ;;
            10)
                start_backends
                ;;
            11)
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
        
        read -p "Presiona Enter para continuar..."
    done
}

# Ejecutar main
main
