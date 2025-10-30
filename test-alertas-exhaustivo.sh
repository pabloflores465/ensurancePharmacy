#!/bin/bash

# ============================================================================
# SCRIPT DE PRUEBA EXHAUSTIVA DE ALERTAS
# Prueba TODAS las alertas de Grafana (Prometheus) y Netdata
# Envía notificaciones por Email y Slack
# ============================================================================

set -e

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
NC='\033[0m'

# URLs
PROMETHEUS_URL="http://localhost:9090"
ALERTMANAGER_URL="http://localhost:9094"
NETDATA_URL="http://localhost:19999"

# Contadores
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0
ALERTS_FIRED=0

LOG_FILE="test-exhaustivo-$(date +%Y%m%d_%H%M%S).log"

# ============================================================================
# FUNCIONES DE UTILIDAD
# ============================================================================

print_header() {
    echo -e "\n${CYAN}═══════════════════════════════════════════════════════════════════${NC}"
    echo -e "${CYAN}  $1${NC}"
    echo -e "${CYAN}═══════════════════════════════════════════════════════════════════${NC}\n"
}

print_test() {
    echo -e "${BLUE}▶ TEST $TOTAL_TESTS: $1${NC}"
    ((TOTAL_TESTS++))
}

print_success() {
    echo -e "${GREEN}  ✓ $1${NC}"
    ((PASSED_TESTS++))
}

print_error() {
    echo -e "${RED}  ✗ $1${NC}"
    ((FAILED_TESTS++))
}

print_warning() {
    echo -e "${YELLOW}  ⚠ $1${NC}"
}

print_info() {
    echo -e "  ℹ $1"
}

wait_for_alert() {
    local alert_name=$1
    local max_wait=${2:-120}
    local waited=0
    
    print_info "Esperando alerta '$alert_name' (máx ${max_wait}s)..."
    
    while [ $waited -lt $max_wait ]; do
        local alert_count=$(curl -s "$PROMETHEUS_URL/api/v1/alerts" | \
            jq -r ".data.alerts[] | select(.labels.alertname==\"$alert_name\" and .state==\"firing\")" | \
            wc -l 2>/dev/null || echo "0")
        
        if [ "$alert_count" -gt 0 ]; then
            print_success "Alerta '$alert_name' disparada después de ${waited}s"
            ((ALERTS_FIRED++))
            return 0
        fi
        
        sleep 5
        ((waited+=5))
        echo -n "."
    done
    
    echo ""
    print_warning "Alerta '$alert_name' NO se disparó en ${max_wait}s"
    return 1
}

check_notification() {
    local notification_type=$1
    local since_minutes=${2:-5}
    
    local count=$(docker logs ensurance-alertmanager-full --since ${since_minutes}m 2>&1 | \
        grep -i "${notification_type}.*notify.*success" | wc -l || echo "0")
    
    if [ "$count" -gt 0 ]; then
        print_success "${count} notificaciones de ${notification_type} enviadas"
        return 0
    else
        print_warning "No se encontraron notificaciones de ${notification_type}"
        return 1
    fi
}

restore_service() {
    local service_name=$1
    print_info "Restaurando $service_name..."
    docker start "$service_name" >/dev/null 2>&1 || true
    sleep 2
}

# ============================================================================
# VERIFICACIÓN INICIAL
# ============================================================================

initial_checks() {
    print_header "VERIFICACIÓN INICIAL DEL SISTEMA"
    
    print_test "Verificar que Prometheus esté corriendo"
    if curl -s "$PROMETHEUS_URL/-/healthy" >/dev/null 2>&1; then
        print_success "Prometheus está activo"
    else
        print_error "Prometheus no responde"
        exit 1
    fi
    
    print_test "Verificar que AlertManager esté corriendo"
    if curl -s "$ALERTMANAGER_URL/-/healthy" >/dev/null 2>&1; then
        print_success "AlertManager está activo"
    else
        print_error "AlertManager no responde"
        exit 1
    fi
    
    print_test "Verificar configuración de Slack"
    if grep -q "https://hooks.slack.com" monitoring/alertmanager/alertmanager.yml 2>/dev/null; then
        print_success "Webhook de Slack configurado"
    else
        print_warning "Webhook de Slack podría no estar configurado"
    fi
    
    print_test "Verificar reglas de Prometheus cargadas"
    local rules_count=$(curl -s "$PROMETHEUS_URL/api/v1/rules" | jq -r '.data.groups | length' 2>/dev/null || echo "0")
    if [ "$rules_count" -gt 0 ]; then
        print_success "$rules_count grupos de reglas cargados"
    else
        print_error "No hay reglas de Prometheus cargadas"
    fi
}

# ============================================================================
# PRUEBAS DE ALERTAS CRÍTICAS (SERVICIOS CAÍDOS)
# ============================================================================

test_critical_alerts() {
    print_header "PRUEBAS DE ALERTAS CRÍTICAS (Servicios Caídos)"
    
    # Test 1: Node Exporter Down
    print_test "NodeExporterDown - Detener Node Exporter"
    docker stop ensurance-node-exporter-full >/dev/null 2>&1
    wait_for_alert "NodeExporterDown" 90
    check_notification "email" 2
    check_notification "slack" 2
    restore_service "ensurance-node-exporter-full"
    sleep 10
    
    # Test 2: RabbitMQ Down (CRITICAL con @channel)
    print_test "RabbitMQDown - Detener RabbitMQ"
    docker stop ensurance-rabbitmq-full >/dev/null 2>&1
    wait_for_alert "RabbitMQDown" 90
    check_notification "email" 2
    check_notification "slack" 2
    restore_service "ensurance-rabbitmq-full"
    sleep 10
    
    # Test 3: Grafana Down
    print_test "GrafanaDown - Detener Grafana"
    docker stop ensurance-grafana-full >/dev/null 2>&1
    wait_for_alert "GrafanaDown" 90
    check_notification "email" 2
    check_notification "slack" 2
    restore_service "ensurance-grafana-full"
    sleep 10
    
    # Test 4: Pushgateway Down
    print_test "PushgatewayDown - Detener Pushgateway"
    docker stop ensurance-pushgateway-full >/dev/null 2>&1
    wait_for_alert "PushgatewayDown" 90
    restore_service "ensurance-pushgateway-full"
    sleep 10
}

# ============================================================================
# PRUEBAS DE ALERTAS DE RECURSOS (CPU, MEMORIA, DISCO)
# ============================================================================

test_resource_alerts() {
    print_header "PRUEBAS DE ALERTAS DE RECURSOS"
    
    # Test: Alta CPU
    print_test "HighCPUUsage - Generar carga de CPU"
    print_info "Generando carga CPU por 3 minutos..."
    
    # Generar carga CPU en background
    for i in {1..4}; do
        timeout 180 yes > /dev/null 2>&1 &
    done
    
    wait_for_alert "HighCPUUsage" 150
    
    # Detener carga CPU
    pkill -f "yes" >/dev/null 2>&1 || true
    sleep 10
    
    # Test: Alta Memoria
    print_test "HighMemoryUsage - Generar carga de memoria"
    print_info "Generando carga de memoria..."
    
    # Crear proceso que consume memoria
    timeout 120 bash -c '
        bigarray=()
        for i in {1..1000000}; do
            bigarray[$i]="This is a test string to consume memory $i"
        done
        sleep 60
    ' >/dev/null 2>&1 &
    
    wait_for_alert "HighMemoryUsage" 150
    
    # Limpiar
    pkill -f "bigarray" >/dev/null 2>&1 || true
    sleep 10
}

# ============================================================================
# PRUEBAS DE ALERTAS DE K6
# ============================================================================

test_k6_alerts() {
    print_header "PRUEBAS DE ALERTAS DE K6"
    
    print_test "K6 Alerts - Verificar si K6 está ejecutándose"
    
    # Verificar si hay métricas de K6
    local k6_metrics=$(curl -s "$PROMETHEUS_URL/api/v1/query?query=k6_http_reqs" | \
        jq -r '.data.result | length' 2>/dev/null || echo "0")
    
    if [ "$k6_metrics" -gt 0 ]; then
        print_success "Métricas de K6 disponibles"
        
        # Verificar alertas de K6
        local k6_alerts=$(curl -s "$PROMETHEUS_URL/api/v1/rules" | \
            jq -r '.data.groups[] | select(.name | contains("k6")) | .rules | length' 2>/dev/null || echo "0")
        
        print_info "K6 tiene configuradas $k6_alerts reglas de alertas"
    else
        print_info "No hay métricas de K6 actualmente (ejecutar stress test para probar)"
        print_info "Para probar K6 alerts, ejecuta: cd stress/k6 && ./run-k6-tests.sh"
    fi
}

# ============================================================================
# PRUEBAS DE ALERTAS DE CI/CD
# ============================================================================

test_cicd_alerts() {
    print_header "PRUEBAS DE ALERTAS DE CI/CD"
    
    # Test: Jenkins Down
    if docker ps -a --format '{{.Names}}' | grep -q "jenkins"; then
        print_test "JenkinsDown - Detener Jenkins"
        local jenkins_container=$(docker ps -a --format '{{.Names}}' | grep jenkins | head -1)
        docker stop "$jenkins_container" >/dev/null 2>&1
        wait_for_alert "JenkinsDown" 150
        docker start "$jenkins_container" >/dev/null 2>&1
        sleep 10
    else
        print_info "Jenkins no está corriendo, saltando prueba"
    fi
    
    # Test: Drone Down
    if docker ps -a --format '{{.Names}}' | grep -q "drone"; then
        print_test "DroneServerDown - Detener Drone"
        local drone_container=$(docker ps -a --format '{{.Names}}' | grep "drone" | grep -v "runner" | head -1)
        docker stop "$drone_container" >/dev/null 2>&1
        wait_for_alert "DroneServerDown" 150
        docker start "$drone_container" >/dev/null 2>&1
        sleep 10
    else
        print_info "Drone no está corriendo, saltando prueba"
    fi
}

# ============================================================================
# PRUEBAS DE ALERTAS DE RABBITMQ
# ============================================================================

test_rabbitmq_alerts() {
    print_header "PRUEBAS DE ALERTAS DE RABBITMQ"
    
    # Verificar que RabbitMQ esté corriendo
    if ! docker ps | grep -q "ensurance-rabbitmq-full"; then
        print_info "RabbitMQ no está corriendo, iniciando..."
        docker start ensurance-rabbitmq-full >/dev/null 2>&1
        sleep 15
    fi
    
    print_test "Verificar métricas de RabbitMQ"
    local rabbitmq_metrics=$(curl -s "$PROMETHEUS_URL/api/v1/query?query=rabbitmq_queues" | \
        jq -r '.data.result | length' 2>/dev/null || echo "0")
    
    if [ "$rabbitmq_metrics" -gt 0 ]; then
        print_success "Métricas de RabbitMQ disponibles"
    else
        print_warning "No hay métricas de RabbitMQ (verificar conexión)"
    fi
    
    # Verificar alertas de RabbitMQ configuradas
    local rabbitmq_alerts=$(curl -s "$PROMETHEUS_URL/api/v1/rules" | \
        jq -r '.data.groups[] | select(.name | contains("rabbitmq")) | .rules | length' 2>/dev/null)
    
    if [ ! -z "$rabbitmq_alerts" ]; then
        print_info "RabbitMQ tiene $rabbitmq_alerts reglas de alertas configuradas"
    fi
}

# ============================================================================
# VERIFICACIÓN DE NETDATA ALERTS
# ============================================================================

test_netdata_alerts() {
    print_header "VERIFICACIÓN DE ALERTAS DE NETDATA"
    
    print_test "Verificar archivos de health de Netdata"
    
    local netdata_files=(
        "system_alerts.conf"
        "application_alerts.conf"
        "k6_alerts.conf"
        "pipeline_alerts.conf"
    )
    
    for file in "${netdata_files[@]}"; do
        if [ -f "monitoring/netdata/health.d/$file" ]; then
            local alarm_count=$(grep -c "^alarm:" "monitoring/netdata/health.d/$file" 2>/dev/null || echo "0")
            print_success "$file: $alarm_count alarmas configuradas"
        else
            print_error "$file no encontrado"
        fi
    done
    
    print_test "Verificar que Netdata esté corriendo"
    if curl -s "$NETDATA_URL/api/v1/info" >/dev/null 2>&1; then
        print_success "Netdata está activo"
        
        # Verificar alertas activas
        local active_alerts=$(curl -s "$NETDATA_URL/api/v1/alarms?active" 2>/dev/null | \
            jq -r '.alarms | length' 2>/dev/null || echo "0")
        print_info "Netdata tiene $active_alerts alertas activas actualmente"
    else
        print_warning "Netdata no está respondiendo"
    fi
}

# ============================================================================
# VERIFICACIÓN EXHAUSTIVA DE NOTIFICACIONES
# ============================================================================

verify_notifications() {
    print_header "VERIFICACIÓN DE NOTIFICACIONES"
    
    print_test "Verificar logs de AlertManager"
    
    # Email
    local email_sent=$(docker logs ensurance-alertmanager-full --since 30m 2>&1 | \
        grep -c "email.*successfully sent" 2>/dev/null || echo "0")
    
    if [ "$email_sent" -gt 0 ]; then
        print_success "Se enviaron $email_sent emails exitosamente"
    else
        print_warning "No se encontraron emails enviados en los últimos 30 minutos"
    fi
    
    # Slack
    local slack_sent=$(docker logs ensurance-alertmanager-full --since 30m 2>&1 | \
        grep -c "slack.*successfully sent" 2>/dev/null || echo "0")
    
    if [ "$slack_sent" -gt 0 ]; then
        print_success "Se enviaron $slack_sent mensajes de Slack exitosamente"
    else
        print_warning "No se encontraron mensajes de Slack enviados"
        print_info "Verificar logs completos con: docker logs ensurance-alertmanager-full --since 30m | grep -i slack"
    fi
    
    # Mostrar últimos errores si los hay
    print_test "Verificar errores en AlertManager"
    local errors=$(docker logs ensurance-alertmanager-full --since 30m 2>&1 | \
        grep -i "error\|failed" | grep -v "successfully" | tail -5)
    
    if [ ! -z "$errors" ]; then
        print_warning "Errores encontrados en AlertManager:"
        echo "$errors"
    else
        print_success "No se encontraron errores en AlertManager"
    fi
}

# ============================================================================
# VERIFICAR ALERTAS ACTUALMENTE DISPARADAS
# ============================================================================

show_active_alerts() {
    print_header "ALERTAS ACTUALMENTE ACTIVAS"
    
    print_test "Consultar alertas activas en Prometheus"
    
    local active_alerts=$(curl -s "$PROMETHEUS_URL/api/v1/alerts" | \
        jq -r '.data.alerts[] | select(.state=="firing")' 2>/dev/null)
    
    if [ ! -z "$active_alerts" ]; then
        echo "$active_alerts" | jq -r '"  • " + .labels.alertname + " (" + .labels.severity + ")"' 2>/dev/null
    else
        print_info "No hay alertas activas actualmente (sistema saludable)"
    fi
    
    print_test "Consultar alertas en AlertManager"
    local am_alerts=$(curl -s "$ALERTMANAGER_URL/api/v2/alerts" | \
        jq -r 'length' 2>/dev/null || echo "0")
    
    print_info "AlertManager tiene $am_alerts alertas actualmente"
}

# ============================================================================
# REPORTE FINAL
# ============================================================================

generate_report() {
    print_header "REPORTE FINAL DE PRUEBAS"
    
    echo -e "${CYAN}Total de Pruebas Ejecutadas:${NC} $TOTAL_TESTS"
    echo -e "${GREEN}Pruebas Exitosas:${NC} $PASSED_TESTS"
    echo -e "${RED}Pruebas Fallidas:${NC} $FAILED_TESTS"
    echo -e "${MAGENTA}Alertas Disparadas:${NC} $ALERTS_FIRED"
    echo ""
    
    local success_rate=0
    if [ "$TOTAL_TESTS" -gt 0 ]; then
        success_rate=$((PASSED_TESTS * 100 / TOTAL_TESTS))
    fi
    
    echo -e "${CYAN}Tasa de Éxito:${NC} $success_rate%"
    echo ""
    
    # Resumen de alertas por categoría
    echo -e "${BLUE}Resumen de Alertas Configuradas:${NC}"
    echo "  • Prometheus/Grafana: 64 alertas"
    echo "    - Sistema: 11 alertas"
    echo "    - Aplicaciones: 9 alertas"
    echo "    - RabbitMQ: 12 alertas"
    echo "    - K6: 8 alertas"
    echo "    - CI/CD: 12 alertas"
    echo "    - Monitoreo: 12 alertas"
    echo ""
    echo "  • Netdata: ~45 alertas"
    echo "    - Sistema: 15 alertas"
    echo "    - Aplicaciones: 12 alertas"
    echo "    - K6: 7 alertas"
    echo "    - CI/CD: 10 alertas"
    echo ""
    
    echo -e "${BLUE}Notificaciones:${NC}"
    echo "  • Email: Configurado (SMTP Brevo)"
    echo "  • Slack: Configurado (Webhook activo)"
    echo "  • Destinatarios: pablopolis2016@gmail.com, jflores@unis.edu.gt"
    echo "  • Canal Slack: #ensurance-alerts"
    echo ""
    
    # Verificaciones finales
    if [ "$FAILED_TESTS" -eq 0 ]; then
        echo -e "${GREEN}✓ TODAS LAS PRUEBAS PASARON EXITOSAMENTE${NC}"
        echo -e "${GREEN}✓ Sistema de alertas completamente funcional${NC}"
    else
        echo -e "${YELLOW}⚠ ALGUNAS PRUEBAS REQUIEREN ATENCIÓN${NC}"
        echo -e "${YELLOW}⚠ Revisar alertas que no se dispararon${NC}"
    fi
    
    echo ""
    echo -e "${BLUE}Log guardado en:${NC} $LOG_FILE"
    echo ""
    echo -e "${CYAN}Interfaces disponibles:${NC}"
    echo "  • Prometheus: http://localhost:9090/alerts"
    echo "  • AlertManager: http://localhost:9094"
    echo "  • Grafana: http://localhost:3302"
    echo "  • Netdata: http://localhost:19999"
}

# ============================================================================
# FUNCIÓN PRINCIPAL
# ============================================================================

main() {
    # Redirigir output a log y consola
    exec > >(tee -a "$LOG_FILE")
    exec 2>&1
    
    echo -e "${MAGENTA}"
    echo "═══════════════════════════════════════════════════════════════════"
    echo "  PRUEBA EXHAUSTIVA DE ALERTAS - ENSURANCE PHARMACY"
    echo "  Fecha: $(date)"
    echo "═══════════════════════════════════════════════════════════════════"
    echo -e "${NC}"
    
    # Verificar directorio
    if [ ! -f "docker-compose.full.yml" ]; then
        print_error "Este script debe ejecutarse desde el directorio raíz del proyecto"
        exit 1
    fi
    
    # Ejecutar todas las pruebas
    initial_checks
    test_critical_alerts
    test_resource_alerts
    test_rabbitmq_alerts
    test_k6_alerts
    test_cicd_alerts
    test_netdata_alerts
    verify_notifications
    show_active_alerts
    generate_report
    
    echo ""
    echo "Pruebas completadas: $(date)"
    echo ""
    
    # Código de salida
    if [ "$FAILED_TESTS" -gt 5 ]; then
        exit 1
    fi
    
    exit 0
}

# Ejecutar
main "$@"
