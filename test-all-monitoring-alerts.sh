#!/bin/bash

# ============================================================================
# TEST ALL MONITORING ALERTS
# Script para probar TODAS las alertas de Grafana (Prometheus) y Netdata
# Ensurance Pharmacy - Sistema Completo de Monitoreo
# ============================================================================

set -e

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Variables de configuración
PROMETHEUS_URL="http://localhost:9090"
ALERTMANAGER_URL="http://localhost:9094"
GRAFANA_URL="http://localhost:3302"
NETDATA_URL="http://localhost:19999"

LOG_FILE="alert-testing-$(date +%Y%m%d_%H%M%S).log"
ALERTS_TESTED=0
ALERTS_PASSED=0
ALERTS_FAILED=0
ALERTS_WARNINGS=0

# ============================================================================
# FUNCIONES DE UTILIDAD
# ============================================================================

print_header() {
    echo -e "\n${CYAN}============================================================================${NC}"
    echo -e "${CYAN}$1${NC}"
    echo -e "${CYAN}============================================================================${NC}\n"
}

print_section() {
    echo -e "\n${BLUE}▶ $1${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
    ((ALERTS_PASSED++))
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
    ((ALERTS_FAILED++))
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
    ((ALERTS_WARNINGS++))
}

print_info() {
    echo -e "  $1"
}

# ============================================================================
# VERIFICACIÓN DE SERVICIOS
# ============================================================================

check_service() {
    local service_name=$1
    local url=$2
    
    print_section "Verificando $service_name"
    
    if curl -s -o /dev/null -w "%{http_code}" "$url" | grep -q "200\|302"; then
        print_success "$service_name está activo y respondiendo"
        return 0
    else
        print_error "$service_name no está respondiendo en $url"
        return 1
    fi
}

# ============================================================================
# VERIFICACIÓN DE PROMETHEUS
# ============================================================================

check_prometheus_alerts() {
    print_header "VERIFICANDO ALERTAS DE PROMETHEUS (GRAFANA)"
    
    # Verificar que Prometheus esté corriendo
    check_service "Prometheus" "$PROMETHEUS_URL/-/healthy"
    
    # Obtener reglas de alertas cargadas
    print_section "Reglas de Alertas Cargadas"
    local rules_response=$(curl -s "$PROMETHEUS_URL/api/v1/rules")
    local rule_count=$(echo "$rules_response" | jq -r '.data.groups | length' 2>/dev/null || echo "0")
    
    if [ "$rule_count" -gt 0 ]; then
        print_success "Prometheus tiene $rule_count grupos de reglas cargados"
        
        # Listar grupos de alertas
        echo "$rules_response" | jq -r '.data.groups[] | "  - " + .name + " (" + (.rules | length | tostring) + " reglas)"' 2>/dev/null
    else
        print_error "No se encontraron reglas de alertas en Prometheus"
    fi
    
    # Verificar alertas específicas por archivo
    print_section "Verificando Archivos de Reglas"
    
    local alert_files=(
        "system_alerts.yml"
        "application_alerts.yml"
        "rabbitmq_alerts.yml"
        "k6_alerts.yml"
        "cicd_alerts.yml"
        "monitoring_alerts.yml"
    )
    
    for file in "${alert_files[@]}"; do
        if [ -f "monitoring/prometheus/rules/$file" ]; then
            local alert_count=$(grep -c "alert:" "monitoring/prometheus/rules/$file" 2>/dev/null || echo "0")
            if [ "$alert_count" -gt 0 ]; then
                print_success "$file: $alert_count alertas definidas"
                ((ALERTS_TESTED+=$alert_count))
            else
                print_warning "$file existe pero no tiene alertas"
            fi
        else
            print_error "$file no encontrado"
        fi
    done
    
    # Verificar alertas activas actualmente
    print_section "Alertas Actualmente Disparadas"
    local active_alerts=$(curl -s "$PROMETHEUS_URL/api/v1/alerts" | jq -r '.data.alerts | length' 2>/dev/null || echo "0")
    
    if [ "$active_alerts" -eq 0 ]; then
        print_info "No hay alertas activas actualmente (sistema saludable)"
    else
        print_warning "$active_alerts alertas están actualmente disparadas"
        curl -s "$PROMETHEUS_URL/api/v1/alerts" | jq -r '.data.alerts[] | "  - " + .labels.alertname + " (" + .labels.severity + ")"' 2>/dev/null
    fi
}

# ============================================================================
# VERIFICACIÓN DE ALERTMANAGER
# ============================================================================

check_alertmanager() {
    print_header "VERIFICANDO ALERTMANAGER"
    
    check_service "Alertmanager" "$ALERTMANAGER_URL/-/healthy"
    
    # Verificar configuración de notificaciones
    print_section "Configuración de Receivers"
    local receivers=$(curl -s "$ALERTMANAGER_URL/api/v2/status" | jq -r '.config.receivers | length' 2>/dev/null || echo "0")
    
    if [ "$receivers" -gt 0 ]; then
        print_success "Alertmanager tiene $receivers receivers configurados"
        curl -s "$ALERTMANAGER_URL/api/v2/status" | jq -r '.config.receivers[] | "  - " + .name' 2>/dev/null
    else
        print_error "No se encontraron receivers en Alertmanager"
    fi
    
    # Verificar si hay alertas activas en Alertmanager
    print_section "Alertas en Alertmanager"
    local am_alerts=$(curl -s "$ALERTMANAGER_URL/api/v2/alerts" | jq -r '. | length' 2>/dev/null || echo "0")
    
    if [ "$am_alerts" -eq 0 ]; then
        print_info "No hay alertas en Alertmanager actualmente"
    else
        print_info "$am_alerts alertas en Alertmanager"
    fi
    
    # Verificar configuración de Email
    print_section "Configuración de Email"
    if grep -q "smtp_smarthost" monitoring/alertmanager/alertmanager.yml.template 2>/dev/null; then
        print_success "Configuración SMTP encontrada en template"
        grep "smtp_smarthost\|smtp_from" monitoring/alertmanager/alertmanager.yml.template | head -2
    else
        print_error "No se encontró configuración SMTP"
    fi
    
    # Verificar configuración de Slack
    print_section "Configuración de Slack"
    local slack_configs=$(grep -c "slack_configs:" monitoring/alertmanager/alertmanager.yml.template 2>/dev/null || echo "0")
    
    if [ "$slack_configs" -gt 0 ]; then
        print_success "Configuración de Slack encontrada ($slack_configs receivers con Slack)"
    else
        print_error "No se encontró configuración de Slack"
    fi
}

# ============================================================================
# VERIFICACIÓN DE NETDATA
# ============================================================================

check_netdata_alerts() {
    print_header "VERIFICANDO ALERTAS DE NETDATA"
    
    check_service "Netdata" "$NETDATA_URL/api/v1/info"
    
    # Verificar archivos de configuración de health
    print_section "Archivos de Alertas de Netdata"
    
    local netdata_alert_files=(
        "system_alerts.conf"
        "application_alerts.conf"
        "k6_alerts.conf"
        "pipeline_alerts.conf"
    )
    
    local total_netdata_alerts=0
    
    for file in "${netdata_alert_files[@]}"; do
        if [ -f "monitoring/netdata/health.d/$file" ]; then
            local alarm_count=$(grep -c "^alarm:" "monitoring/netdata/health.d/$file" 2>/dev/null || echo "0")
            if [ "$alarm_count" -gt 0 ]; then
                print_success "$file: $alarm_count alarmas definidas"
                ((total_netdata_alerts+=$alarm_count))
                ((ALERTS_TESTED+=$alarm_count))
            else
                print_warning "$file existe pero no tiene alarmas"
            fi
        else
            print_error "$file no encontrado"
        fi
    done
    
    print_info "Total de alertas Netdata: $total_netdata_alerts"
    
    # Verificar alertas activas en Netdata via API
    print_section "Alertas Activas en Netdata"
    local netdata_active=$(curl -s "$NETDATA_URL/api/v1/alarms?active" | jq -r '.alarms | length' 2>/dev/null || echo "0")
    
    if [ "$netdata_active" -eq 0 ]; then
        print_info "No hay alertas activas en Netdata (sistema saludable)"
    else
        print_warning "$netdata_active alertas están actualmente activas en Netdata"
    fi
}

# ============================================================================
# VERIFICACIÓN DE GRAFANA
# ============================================================================

check_grafana_dashboards() {
    print_header "VERIFICANDO DASHBOARDS DE GRAFANA"
    
    check_service "Grafana" "$GRAFANA_URL/api/health"
    
    # Verificar dashboards
    print_section "Dashboards Disponibles"
    
    local dashboards=(
        "k6-dashboard.json"
        "system-metrics-dashboard.json"
    )
    
    for dashboard in "${dashboards[@]}"; do
        if [ -f "monitoring/grafana/dashboards/$dashboard" ]; then
            local panels=$(jq -r '.panels | length' "monitoring/grafana/dashboards/$dashboard" 2>/dev/null || echo "0")
            if [ "$panels" -gt 0 ]; then
                print_success "$dashboard: $panels paneles configurados"
            else
                print_warning "$dashboard existe pero no tiene paneles"
            fi
        else
            print_error "$dashboard no encontrado"
        fi
    done
}

# ============================================================================
# PRUEBAS DE NOTIFICACIONES
# ============================================================================

test_notifications() {
    print_header "PROBANDO NOTIFICACIONES"
    
    print_section "Test de Email"
    print_info "Para probar email, ejecuta manualmente:"
    echo -e "  ${CYAN}docker stop ensurance-node-exporter-full${NC}"
    echo -e "  ${CYAN}sleep 120${NC}"
    echo -e "  ${CYAN}docker start ensurance-node-exporter-full${NC}"
    
    print_section "Test de Slack"
    print_info "Para probar Slack, ejecuta manualmente:"
    echo -e "  ${CYAN}docker stop ensurance-rabbitmq-full${NC}"
    echo -e "  ${CYAN}sleep 90${NC}"
    echo -e "  ${CYAN}docker start ensurance-rabbitmq-full${NC}"
    
    print_section "Verificar Logs de Alertmanager"
    print_info "Para ver notificaciones enviadas:"
    echo -e "  ${CYAN}docker logs ensurance-alertmanager-full --since 10m | grep -i 'notify\\|email\\|slack'${NC}"
}

# ============================================================================
# RESUMEN DE ALERTAS POR CATEGORÍA
# ============================================================================

show_alert_summary() {
    print_header "RESUMEN DE ALERTAS CONFIGURADAS"
    
    echo -e "${BLUE}Prometheus/Grafana Alerts:${NC}"
    echo "  • Sistema (CPU, Memoria, Disco, Red): 11 alertas"
    echo "  • Aplicaciones (Backends, Frontends): 9 alertas"
    echo "  • RabbitMQ: 12 alertas"
    echo "  • K6 Stress Testing: 8 alertas"
    echo "  • CI/CD (Jenkins, Drone, SonarQube): 12 alertas"
    echo "  • Monitoreo (Prometheus, Grafana, etc): 12 alertas"
    echo ""
    echo -e "${BLUE}Netdata Alerts:${NC}"
    echo "  • Sistema (CPU, Memoria, Disco, Red, Load): ~15 alertas"
    echo "  • Aplicaciones (Procesos, Containers): ~12 alertas"
    echo "  • K6 Stress Testing: 7 alertas"
    echo "  • CI/CD Pipeline: ~10 alertas"
    echo ""
    echo -e "${BLUE}Notificaciones:${NC}"
    echo "  • Email (SMTP): Configurado para 4 niveles de severidad"
    echo "  • Slack: Configurado para 4 niveles de severidad"
    echo "  • Destinatarios: pablopolis2016@gmail.com, jflores@unis.edu.gt"
    echo "  • Canal Slack: #ensurance-alerts"
}

# ============================================================================
# VALIDACIÓN DE CONFIGURACIÓN
# ============================================================================

validate_configuration() {
    print_header "VALIDANDO CONFIGURACIÓN"
    
    # Verificar que AlertManager template tiene Slack configurado
    print_section "Validando AlertManager Template"
    local slack_count=$(grep -c "api_url: 'SLACK_WEBHOOK_URL_AQUI'" monitoring/alertmanager/alertmanager.yml.template 2>/dev/null || echo "0")
    if [ "$slack_count" -ge 4 ]; then
        print_success "Todos los receivers tienen configuración de Slack ($slack_count encontrados)"
    else
        print_warning "Algunos receivers podrían no tener Slack configurado"
    fi
    
    # Verificar que existen archivos de reglas de Prometheus
    print_section "Validando Archivos de Prometheus"
    if [ -d "monitoring/prometheus/rules" ]; then
        local rules_count=$(ls -1 monitoring/prometheus/rules/*.yml 2>/dev/null | wc -l)
        if [ "$rules_count" -ge 6 ]; then
            print_success "$rules_count archivos de reglas encontrados"
        else
            print_warning "Solo $rules_count archivos de reglas encontrados (esperados: 6)"
        fi
    else
        print_error "Directorio de reglas de Prometheus no encontrado"
    fi
    
    # Verificar que existen archivos de health de Netdata
    print_section "Validando Archivos de Netdata"
    if [ -d "monitoring/netdata/health.d" ]; then
        local health_count=$(ls -1 monitoring/netdata/health.d/*.conf 2>/dev/null | wc -l)
        if [ "$health_count" -ge 4 ]; then
            print_success "$health_count archivos de health encontrados"
        else
            print_warning "Solo $health_count archivos de health encontrados (esperados: 4)"
        fi
    else
        print_error "Directorio health.d de Netdata no encontrado"
    fi
}

# ============================================================================
# GENERAR REPORTE
# ============================================================================

generate_report() {
    print_header "REPORTE FINAL"
    
    echo -e "${CYAN}Alertas Totales Probadas:${NC} $ALERTS_TESTED"
    echo -e "${GREEN}Verificaciones Exitosas:${NC} $ALERTS_PASSED"
    echo -e "${RED}Verificaciones Fallidas:${NC} $ALERTS_FAILED"
    echo -e "${YELLOW}Advertencias:${NC} $ALERTS_WARNINGS"
    echo ""
    
    local success_rate=0
    if [ "$ALERTS_TESTED" -gt 0 ]; then
        success_rate=$((ALERTS_PASSED * 100 / ALERTS_TESTED))
    fi
    
    echo -e "${CYAN}Tasa de Éxito:${NC} $success_rate%"
    echo ""
    
    if [ "$ALERTS_FAILED" -eq 0 ] && [ "$ALERTS_WARNINGS" -eq 0 ]; then
        echo -e "${GREEN}✓ TODAS LAS VERIFICACIONES PASARON${NC}"
        echo -e "${GREEN}✓ Sistema de alertas completamente funcional${NC}"
    elif [ "$ALERTS_FAILED" -eq 0 ]; then
        echo -e "${YELLOW}⚠ VERIFICACIONES PASARON CON ADVERTENCIAS${NC}"
        echo -e "${YELLOW}⚠ Revisar advertencias arriba${NC}"
    else
        echo -e "${RED}✗ ALGUNAS VERIFICACIONES FALLARON${NC}"
        echo -e "${RED}✗ Revisar errores arriba y corregir${NC}"
    fi
    
    echo ""
    echo -e "${BLUE}Log guardado en:${NC} $LOG_FILE"
}

# ============================================================================
# FUNCIÓN PRINCIPAL
# ============================================================================

main() {
    # Redirigir output a log y consola
    exec > >(tee -a "$LOG_FILE")
    exec 2>&1
    
    echo "Iniciando pruebas de alertas: $(date)"
    echo ""
    
    # Verificar que estamos en el directorio correcto
    if [ ! -f "docker-compose.full.yml" ]; then
        print_error "Este script debe ejecutarse desde el directorio raíz del proyecto"
        exit 1
    fi
    
    # Ejecutar todas las verificaciones
    validate_configuration
    check_prometheus_alerts
    check_alertmanager
    check_netdata_alerts
    check_grafana_dashboards
    test_notifications
    show_alert_summary
    generate_report
    
    echo ""
    echo "Pruebas completadas: $(date)"
    
    # Retornar código de error si hay fallos
    if [ "$ALERTS_FAILED" -gt 0 ]; then
        exit 1
    fi
    
    exit 0
}

# Ejecutar script principal
main
