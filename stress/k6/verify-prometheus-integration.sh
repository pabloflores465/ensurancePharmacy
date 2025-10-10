#!/bin/bash

# Script para verificar la integración de k6 con Prometheus
# Autor: Sistema de Monitoreo Ensurance Pharmacy
# Fecha: 2025-10-09

set -e

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Funciones de utilidad
print_header() {
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

# Verificar dependencias
check_dependencies() {
    print_header "Verificando dependencias"
    
    local missing_deps=()
    
    if ! command -v docker &> /dev/null; then
        missing_deps+=("docker")
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        missing_deps+=("docker-compose")
    fi
    
    if ! command -v curl &> /dev/null; then
        missing_deps+=("curl")
    fi
    
    if ! command -v jq &> /dev/null; then
        print_info "jq no está instalado (opcional, pero recomendado)"
    fi
    
    if [ ${#missing_deps[@]} -ne 0 ]; then
        print_error "Faltan dependencias: ${missing_deps[*]}"
        exit 1
    fi
    
    print_success "Todas las dependencias están instaladas"
    echo ""
}

# Construir imagen de k6
build_k6_image() {
    print_header "Construyendo imagen de k6 con soporte Prometheus"
    
    cd "$(dirname "$0")"
    
    if docker build -t ensurance-k6-prometheus:latest -f Dockerfile.k6-prometheus . ; then
        print_success "Imagen construida exitosamente"
    else
        print_error "Error construyendo la imagen"
        exit 1
    fi
    
    echo ""
}

# Iniciar servicios de monitoreo
start_monitoring() {
    print_header "Iniciando servicios de monitoreo"
    
    cd ../../scripts
    
    print_info "Iniciando Prometheus y Grafana..."
    docker-compose -f docker-compose.monitor.yml up -d prometheus grafana pushgateway
    
    print_info "Esperando a que Prometheus esté listo..."
    sleep 10
    
    # Verificar que Prometheus está corriendo
    if curl -s http://localhost:9095/-/healthy > /dev/null 2>&1; then
        print_success "Prometheus está corriendo en http://localhost:9095"
    else
        print_error "Prometheus no respondió"
        exit 1
    fi
    
    print_success "Grafana está corriendo en http://localhost:3300"
    echo ""
}

# Ejecutar test de k6
run_k6_test() {
    print_header "Ejecutando test de k6"
    
    cd ../../scripts
    
    print_info "Iniciando test de verificación de Prometheus..."
    export TEST_SCRIPT=prometheus-test.js
    
    docker-compose -f docker-compose.stress.yml up --build k6
    
    print_success "Test de k6 completado"
    echo ""
}

# Verificar métricas en Prometheus
verify_metrics() {
    print_header "Verificando métricas en Prometheus"
    
    print_info "Esperando a que las métricas se propaguen..."
    sleep 5
    
    # Lista de métricas a verificar
    local metrics=(
        "k6_http_reqs"
        "k6_http_req_duration"
        "k6_http_req_failed"
        "k6_vus"
        "k6_iterations"
        "k6_data_sent"
        "k6_data_received"
    )
    
    local found_metrics=0
    local total_metrics=${#metrics[@]}
    
    for metric in "${metrics[@]}"; do
        response=$(curl -s "http://localhost:9095/api/v1/query?query=${metric}" | jq -r '.data.result | length' 2>/dev/null || echo "0")
        
        if [ "$response" != "0" ]; then
            print_success "Métrica encontrada: ${metric}"
            ((found_metrics++))
        else
            print_error "Métrica NO encontrada: ${metric}"
        fi
    done
    
    echo ""
    echo -e "${BLUE}Resumen: ${found_metrics}/${total_metrics} métricas encontradas${NC}"
    
    if [ $found_metrics -eq $total_metrics ]; then
        print_success "¡Todas las métricas están disponibles en Prometheus!"
    else
        print_error "Algunas métricas no están disponibles"
    fi
    
    echo ""
}

# Mostrar información de acceso
show_access_info() {
    print_header "Información de Acceso"
    
    echo -e "${GREEN}Prometheus:${NC}"
    echo "  URL: http://localhost:9095"
    echo "  Query para ver todas las métricas de k6: {__name__=~\"k6_.*\"}"
    echo ""
    
    echo -e "${GREEN}Grafana:${NC}"
    echo "  URL: http://localhost:3300"
    echo "  Usuario: admin"
    echo "  Password: changeme"
    echo ""
    
    echo -e "${GREEN}K6 Web Dashboard:${NC}"
    echo "  URL: http://localhost:5666"
    echo ""
    
    echo -e "${YELLOW}Queries PromQL útiles:${NC}"
    echo "  - Requests/seg:     rate(k6_http_reqs[1m])"
    echo "  - Latencia P95:     k6_http_req_duration{stat=\"p95\"}"
    echo "  - Tasa de errores:  rate(k6_http_req_failed[1m])"
    echo "  - VUs activos:      k6_vus"
    echo ""
}

# Mostrar nombres de métricas
show_metric_names() {
    print_header "Nombres de Métricas K6"
    
    echo -e "${GREEN}Métricas HTTP:${NC}"
    echo "  • k6_http_reqs                  - Total de requests"
    echo "  • k6_http_req_duration          - Duración de requests"
    echo "  • k6_http_req_waiting           - Tiempo esperando respuesta (TTFB)"
    echo "  • k6_http_req_connecting        - Tiempo estableciendo conexión"
    echo "  • k6_http_req_tls_handshaking   - Tiempo en TLS handshake"
    echo "  • k6_http_req_sending           - Tiempo enviando datos"
    echo "  • k6_http_req_receiving         - Tiempo recibiendo datos"
    echo "  • k6_http_req_blocked           - Tiempo bloqueado"
    echo "  • k6_http_req_failed            - Ratio de requests fallidos"
    echo ""
    
    echo -e "${GREEN}Métricas de VUs:${NC}"
    echo "  • k6_vus                        - VUs activos actualmente"
    echo "  • k6_vus_max                    - Máximo de VUs"
    echo ""
    
    echo -e "${GREEN}Métricas de Iteraciones:${NC}"
    echo "  • k6_iterations                 - Total de iteraciones"
    echo "  • k6_iteration_duration         - Duración de iteraciones"
    echo ""
    
    echo -e "${GREEN}Métricas de Datos:${NC}"
    echo "  • k6_data_sent                  - Bytes enviados"
    echo "  • k6_data_received              - Bytes recibidos"
    echo ""
    
    echo -e "${GREEN}Métricas de Checks:${NC}"
    echo "  • k6_checks                     - Ratio de checks exitosos"
    echo ""
    
    echo -e "${YELLOW}Nota:${NC} Las métricas de tipo trend (como k6_http_req_duration) incluyen"
    echo "      múltiples estadísticas: min, max, avg, med, p95, p99"
    echo "      Ejemplo: k6_http_req_duration{stat=\"p95\"}"
    echo ""
}

# Main
main() {
    clear
    
    print_header "K6 + Prometheus Integration Verification"
    echo ""
    
    # Verificar si se pasó el argumento --metrics-only
    if [ "$1" == "--metrics-only" ]; then
        show_metric_names
        exit 0
    fi
    
    # Verificar si se pasó el argumento --verify-only
    if [ "$1" == "--verify-only" ]; then
        verify_metrics
        show_access_info
        exit 0
    fi
    
    check_dependencies
    build_k6_image
    start_monitoring
    run_k6_test
    verify_metrics
    show_access_info
    show_metric_names
    
    print_header "¡Verificación Completa!"
    print_info "Consulta K6_PROMETHEUS_METRICS.md para más información"
}

# Ejecutar
main "$@"
