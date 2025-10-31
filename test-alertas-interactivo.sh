#!/bin/bash

# Script interactivo para probar alertas específicas del sistema
# Permite seleccionar qué categorías de alertas deseas probar

set -e

PROMETHEUS_URL="http://localhost:9090"
ALERTMANAGER_URL="http://localhost:9093"
PUSHGATEWAY_URL="http://localhost:9091"

# Colores para mejor visualización
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Arrays para tracking de selecciones
declare -a SELECTED_CATEGORIES=()

clear
echo -e "${CYAN}=========================================="
echo "🧪 TEST INTERACTIVO DE ALERTAS"
echo -e "==========================================${NC}"
echo ""
echo "Este script permite seleccionar qué categorías de alertas deseas probar"
echo ""

# Función para esperar y mostrar progreso
wait_with_progress() {
    local duration=$1
    local message=$2
    echo -n "$message"
    for ((i=0; i<duration; i++)); do
        sleep 1
        echo -n "."
    done
    echo " ✓"
}

# Función para enviar métrica a Pushgateway
push_metric() {
    local job=$1
    local metric=$2
    local value=$3
    local labels=$4
    
    if [ -z "$labels" ]; then
        echo "$metric $value" | curl --data-binary @- "$PUSHGATEWAY_URL/metrics/job/$job"
    else
        echo "$metric{$labels} $value" | curl --data-binary @- "$PUSHGATEWAY_URL/metrics/job/$job"
    fi
}

# Función para verificar alertas activas
check_active_alerts() {
    local count=$(curl -s "$PROMETHEUS_URL/api/v1/alerts" 2>/dev/null | jq '.data.alerts | length' 2>/dev/null || echo "0")
    echo -e "${BLUE}📊 Alertas activas: $count${NC}"
}

# Función para mostrar menú
show_menu() {
    clear
    echo -e "${CYAN}=========================================="
    echo "🧪 SELECCIONA CATEGORÍAS DE ALERTAS"
    echo -e "==========================================${NC}"
    echo ""
    echo "Categorías disponibles:"
    echo ""
    echo -e "${GREEN}1)${NC} Alertas de Sistema (12 alertas)"
    echo "   - RAM (60%+), CPU, Memoria, Disco, Red, Load, Node Exporter"
    echo ""
    echo -e "${GREEN}2)${NC} Alertas de Aplicaciones (8 alertas)"
    echo "   - Backend/Frontend Down, Node.js Performance"
    echo ""
    echo -e "${GREEN}3)${NC} Alertas de Netdata (12 alertas)"
    echo "   - Netdata Down, Temperatura CPU, Zombies, Swap, I/O, Network"
    echo ""
    echo -e "${GREEN}4)${NC} Alertas de K6 (8 alertas)"
    echo "   - Error Rate, Response Time, Failed Checks, Load"
    echo ""
    echo -e "${GREEN}5)${NC} Alertas de CI/CD (12 alertas)"
    echo "   - Jenkins, Pushgateway, SonarQube, Drone"
    echo ""
    echo -e "${GREEN}6)${NC} Alertas de Monitoreo (11 alertas)"
    echo "   - Prometheus, Grafana, Alertmanager, Portainer"
    echo ""
    echo -e "${GREEN}7)${NC} ${YELLOW}TODAS LAS CATEGORÍAS${NC}"
    echo ""
    echo -e "${RED}0)${NC} Finalizar selección y comenzar pruebas"
    echo ""
}

# Función para confirmar selección
confirm_selection() {
    echo ""
    echo -e "${YELLOW}=========================================="
    echo "📋 RESUMEN DE SELECCIÓN"
    echo -e "==========================================${NC}"
    if [ ${#SELECTED_CATEGORIES[@]} -eq 0 ]; then
        echo "No has seleccionado ninguna categoría"
        return 1
    fi
    echo "Categorías seleccionadas:"
    for cat in "${SELECTED_CATEGORIES[@]}"; do
        echo -e "${GREEN}✓${NC} $cat"
    done
    echo ""
    echo -e "${YELLOW}Tiempo estimado total: ~$(( ${#SELECTED_CATEGORIES[@]} * 8 ))-$(( ${#SELECTED_CATEGORIES[@]} * 12 )) minutos${NC}"
    echo ""
    read -p "¿Deseas continuar con estas pruebas? (y/n): " -n 1 -r
    echo
    [[ $REPLY =~ ^[Yy]$ ]]
}

# ==========================================
# CATEGORÍA 1: ALERTAS DE SISTEMA
# ==========================================
test_sistema() {
    echo ""
    echo -e "${PURPLE}==========================================="
    echo "CATEGORÍA 1: ALERTAS DE SISTEMA (12)"
    echo -e "==========================================${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 0: HighRAMUsage Alert (Primera alerta - 60%)${NC}"
    echo "Simulando uso moderado de RAM (60%+)..."
    stress-ng --vm 1 --vm-bytes 65% --timeout 120s --quiet &
    STRESS_PID=$!
    wait_with_progress 90 "Esperando HighRAMUsage - Envía a Gmail y Slack (1-2 min)"
    kill -9 $STRESS_PID 2>/dev/null || true
    echo -e "${GREEN}✅ HighRAMUsage test completado${NC}"
    echo -e "${YELLOW}📧 Verifica tu email y Slack para esta alerta${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 1-2: CPU Alerts${NC}"
    echo "Simulando alta carga de CPU..."
    stress-ng --cpu 8 --timeout 180s --quiet &
    STRESS_PID=$!
    wait_with_progress 150 "Esperando HighCPUUsage y CriticalCPUUsage (2-3 min)"
    kill -9 $STRESS_PID 2>/dev/null || true
    echo -e "${GREEN}✅ CPU tests completados${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 3-4: Memory Alerts${NC}"
    echo "Simulando alto uso de memoria..."
    stress-ng --vm 2 --vm-bytes 90% --timeout 180s --quiet &
    STRESS_PID=$!
    wait_with_progress 150 "Esperando HighMemoryUsage y CriticalMemoryUsage (2-3 min)"
    kill -9 $STRESS_PID 2>/dev/null || true
    echo -e "${GREEN}✅ Memory tests completados${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 5-7: Disk Alerts${NC}"
    echo "Creando archivo grande para simular disco lleno..."
    TEMP_FILE="/tmp/disk_test_$(date +%s).bin"
    dd if=/dev/zero of=$TEMP_FILE bs=1M count=5000 2>/dev/null || true
    wait_with_progress 360 "Esperando HighDiskUsage, CriticalDiskUsage, DiskAlmostFull (5-6 min)"
    rm -f $TEMP_FILE
    echo -e "${GREEN}✅ Disk tests completados${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 8-9: Network Alerts${NC}"
    echo "Simulando alto tráfico de red..."
    (for i in {1..100}; do
        curl -s http://localhost:3100 > /dev/null &
        curl -s http://localhost:3101 > /dev/null &
    done) &
    wait_with_progress 360 "Esperando HighNetworkReceive y HighNetworkTransmit (5-6 min)"
    echo -e "${GREEN}✅ Network tests completados${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 10: NodeExporterDown${NC}"
    echo "Deteniendo Node Exporter temporalmente..."
    docker compose -f docker-compose.full.yml stop node-exporter
    wait_with_progress 90 "Esperando NodeExporterDown (1-2 min)"
    docker compose -f docker-compose.full.yml start node-exporter
    echo -e "${GREEN}✅ NodeExporter test completado${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 11: HighSystemLoad${NC}"
    echo "Generando alta carga del sistema..."
    stress-ng --cpu 16 --io 4 --vm 2 --timeout 360s --quiet &
    STRESS_PID=$!
    wait_with_progress 360 "Esperando HighSystemLoad (5-6 min)"
    kill -9 $STRESS_PID 2>/dev/null || true
    echo -e "${GREEN}✅ System load test completado${NC}"
    echo ""
    
    check_active_alerts
}

# ==========================================
# CATEGORÍA 2: ALERTAS DE APLICACIONES
# ==========================================
test_aplicaciones() {
    echo ""
    echo -e "${PURPLE}==========================================="
    echo "CATEGORÍA 2: ALERTAS DE APLICACIONES (8)"
    echo -e "==========================================${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 12-15: Application Down Alerts${NC}"
    echo "Deteniendo aplicaciones temporalmente..."
    
    docker compose -f docker-compose.full.yml stop ensurance-pharmacy-apps
    wait_with_progress 90 "Esperando PharmacyBackendDown, EnsuranceBackendDown (1-2 min)"
    wait_with_progress 90 "Esperando EnsuranceFrontendDown, PharmacyFrontendDown (2-3 min)"
    docker compose -f docker-compose.full.yml start ensurance-pharmacy-apps
    echo -e "${GREEN}✅ Application down tests completados${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 16-19: Node.js Performance Alerts${NC}"
    echo "Nota: Estas alertas requieren que las aplicaciones Node.js expongan métricas"
    echo -e "${YELLOW}ℹ️ Verificar manualmente: HighNodeMemoryBackendV5, HighNodeMemoryBackendV4${NC}"
    echo -e "${YELLOW}ℹ️ Verificar manualmente: HighEventLoopLag, FrequentGarbageCollection${NC}"
    echo ""
    
    check_active_alerts
}

# ==========================================
# CATEGORÍA 3: ALERTAS DE NETDATA
# ==========================================
test_netdata() {
    echo ""
    echo -e "${PURPLE}==========================================="
    echo "CATEGORÍA 3: ALERTAS DE NETDATA (12)"
    echo -e "==========================================${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 21: NetdataDown${NC}"
    echo "Deteniendo Netdata temporalmente..."
    docker compose -f docker-compose.full.yml stop netdata
    wait_with_progress 90 "Esperando NetdataDown (1-2 min)"
    docker compose -f docker-compose.full.yml start netdata
    wait_with_progress 30 "Esperando que Netdata inicie"
    echo -e "${GREEN}✅ Netdata down test completado${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 22: HighCPUTemperature${NC}"
    echo -e "${YELLOW}ℹ️ Verificar manualmente: Requiere sensores de temperatura hardware${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 23: ZombieProcesses${NC}"
    echo "Creando procesos zombie para test..."
    for i in {1..15}; do
        (bash -c 'sleep 0.1 & exec /bin/true') &
    done
    wait_with_progress 650 "Esperando ZombieProcesses (10-11 min)"
    echo -e "${GREEN}✅ Zombie processes test completado${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 24: TooManyProcesses${NC}"
    echo "Generando muchos procesos..."
    for i in {1..300}; do
        (sleep 600 &)
    done
    wait_with_progress 360 "Esperando TooManyProcesses (5-6 min)"
    pkill -f "sleep 600" 2>/dev/null || true
    echo -e "${GREEN}✅ Too many processes test completado${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 25: SwapUsage${NC}"
    echo "Forzando uso de swap..."
    stress-ng --vm 4 --vm-bytes 95% --timeout 600s --quiet &
    STRESS_PID=$!
    wait_with_progress 650 "Esperando SwapUsage (10-11 min)"
    kill -9 $STRESS_PID 2>/dev/null || true
    echo -e "${GREEN}✅ Swap usage test completado${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 26: HighDiskIO${NC}"
    echo "Generando I/O intensivo de disco..."
    stress-ng --io 16 --hdd 4 --timeout 360s --quiet &
    STRESS_PID=$!
    wait_with_progress 360 "Esperando HighDiskIO (5-6 min)"
    kill -9 $STRESS_PID 2>/dev/null || true
    echo -e "${GREEN}✅ High disk I/O test completado${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 27: MemoryFragmentation${NC}"
    echo -e "${YELLOW}ℹ️ Verificar manualmente: Requiere análisis de fragmentación de memoria${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 28: DiskReadErrors${NC}"
    echo -e "${YELLOW}⚠️ CRÍTICO: No se puede forzar errores de disco - verificar manualmente${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 29: SuspiciousNetworkConnections${NC}"
    echo "Generando múltiples conexiones de red..."
    for i in {1..1500}; do
        (curl -s http://localhost:9090 > /dev/null 2>&1 &)
    done
    wait_with_progress 360 "Esperando SuspiciousNetworkConnections (5-6 min)"
    echo -e "${GREEN}✅ Network connections test completado${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 30: FrequentServiceRestarts${NC}"
    echo -e "${YELLOW}ℹ️ Verificar manualmente: Requiere monitoreo de systemd${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 31: RapidLogGrowth${NC}"
    echo "Generando logs rápidamente..."
    for i in {1..10000}; do
        echo "Test log message $i $(date)" >> /tmp/test_rapid_logs.log
    done
    wait_with_progress 650 "Esperando RapidLogGrowth (10-11 min)"
    rm -f /tmp/test_rapid_logs.log
    echo -e "${GREEN}✅ Rapid log growth test completado${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 32: HighNetworkLatency${NC}"
    echo -e "${YELLOW}ℹ️ Verificar manualmente: Requiere configuración de ping targets en Netdata${NC}"
    echo ""
    
    check_active_alerts
}

# ==========================================
# CATEGORÍA 4: ALERTAS DE K6
# ==========================================
test_k6() {
    echo ""
    echo -e "${PURPLE}==========================================="
    echo "CATEGORÍA 4: ALERTAS DE K6 (8)"
    echo -e "==========================================${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 33-40: K6 Stress Testing Alerts${NC}"
    echo "Enviando métricas simuladas de K6 a Pushgateway..."
    
    # Simular alta tasa de errores
    push_metric "k6-stress-test" "k6_http_req_failed" "0.08" ""
    wait_with_progress 90 "Esperando K6HighErrorRate (1-2 min)"
    
    # Simular response times altos
    push_metric "k6-stress-test" "k6_http_req_duration" "1500" "quantile=\"0.95\""
    wait_with_progress 150 "Esperando K6HighResponseTimeP95 (2-3 min)"
    
    push_metric "k6-stress-test" "k6_http_req_duration" "3500" "quantile=\"0.95\""
    wait_with_progress 90 "Esperando K6CriticalResponseTimeP95 (1-2 min)"
    
    push_metric "k6-stress-test" "k6_http_req_duration" "6000" "quantile=\"0.99\""
    wait_with_progress 90 "Esperando K6HighResponseTimeP99 (1-2 min)"
    
    # Simular checks fallidos
    push_metric "k6-stress-test" "k6_checks" "5" "result=\"fail\""
    wait_with_progress 90 "Esperando K6FailedChecks (1-2 min)"
    
    # Simular alta carga
    push_metric "k6-stress-test" "k6_http_reqs" "50000" ""
    push_metric "k6-stress-test" "k6_vus" "150" ""
    wait_with_progress 150 "Esperando K6HighRequestRate, K6HighVirtualUsers (2-3 min)"
    
    echo -e "${GREEN}✅ K6 tests completados${NC}"
    echo ""
    
    check_active_alerts
}

# ==========================================
# CATEGORÍA 5: ALERTAS DE CI/CD
# ==========================================
test_cicd() {
    echo ""
    echo -e "${PURPLE}==========================================="
    echo "CATEGORÍA 5: ALERTAS DE CI/CD (12)"
    echo -e "==========================================${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 41-42: Jenkins/Pushgateway Down${NC}"
    echo "Deteniendo Pushgateway temporalmente..."
    docker compose -f docker-compose.full.yml stop pushgateway
    wait_with_progress 150 "Esperando PushgatewayDown (2-3 min)"
    docker compose -f docker-compose.full.yml start pushgateway
    echo -e "${GREEN}✅ Pushgateway down test completado${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 43-48: Jenkins Build Alerts${NC}"
    echo "Enviando métricas simuladas de Jenkins..."
    
    # Build fallido
    push_metric "jenkins-ci" "jenkins_build_result" "1" "result=\"FAILURE\",job_name=\"test-job\",build_number=\"123\""
    wait_with_progress 90 "Esperando JenkinsBuildFailed (1-2 min)"
    
    # Build lento
    push_metric "jenkins-ci" "jenkins_build_duration_seconds" "2000" "job_name=\"slow-job\""
    wait_with_progress 90 "Esperando JenkinsSlowBuild (1-2 min)"
    
    # Cola larga
    push_metric "jenkins-ci" "jenkins_queue_size" "8" ""
    wait_with_progress 360 "Esperando JenkinsLongQueue (5-6 min)"
    
    # Múltiples builds fallidos
    for i in {1..5}; do
        push_metric "jenkins-ci" "jenkins_build_result" "$i" "result=\"FAILURE\",job_name=\"failing-job\",build_number=\"$((123+i))\""
        sleep 2
    done
    wait_with_progress 90 "Esperando JenkinsMultipleBuildFailures (1-2 min)"
    
    # Executors ocupados
    push_metric "jenkins-ci" "jenkins_executor_busy" "9" ""
    push_metric "jenkins-ci" "jenkins_executor_total" "10" ""
    wait_with_progress 650 "Esperando JenkinsAllExecutorsBusy (10-11 min)"
    
    # Executor offline
    push_metric "jenkins-ci" "jenkins_executor_offline" "2" ""
    wait_with_progress 360 "Esperando JenkinsExecutorOffline (5-6 min)"
    
    echo -e "${GREEN}✅ Jenkins tests completados${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 49-52: SonarQube y Drone${NC}"
    echo -e "${YELLOW}ℹ️ Verificar manualmente: SonarQubeDown, SonarQubeQualityGateFailed${NC}"
    echo -e "${YELLOW}ℹ️ Verificar manualmente: DroneServerDown, DroneRunnerDown${NC}"
    echo ""
    
    check_active_alerts
}

# ==========================================
# CATEGORÍA 6: ALERTAS DE MONITOREO
# ==========================================
test_monitoreo() {
    echo ""
    echo -e "${PURPLE}==========================================="
    echo "CATEGORÍA 6: ALERTAS DE MONITOREO (11)"
    echo -e "==========================================${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 53-58: Prometheus Alerts${NC}"
    echo "Deteniendo Prometheus temporalmente para test..."
    docker compose -f docker-compose.full.yml stop prometheus
    wait_with_progress 90 "Esperando PrometheusDown (solo visible en logs)"
    docker compose -f docker-compose.full.yml start prometheus
    wait_with_progress 30 "Esperando que Prometheus reinicie"
    echo -e "${GREEN}✅ Prometheus test completado${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 59: GrafanaDown${NC}"
    echo "Deteniendo Grafana temporalmente..."
    docker compose -f docker-compose.full.yml stop grafana
    wait_with_progress 150 "Esperando GrafanaDown (2-3 min)"
    docker compose -f docker-compose.full.yml start grafana
    echo -e "${GREEN}✅ Grafana test completado${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 61-63: Alertmanager Alerts${NC}"
    echo "Deteniendo Alertmanager temporalmente..."
    docker compose -f docker-compose.full.yml stop alertmanager
    wait_with_progress 90 "Esperando AlertmanagerDown (1-2 min)"
    docker compose -f docker-compose.full.yml start alertmanager
    echo -e "${GREEN}✅ Alertmanager test completado${NC}"
    echo ""
    
    echo -e "${CYAN}🔹 Test 64: PortainerDown${NC}"
    echo "Deteniendo Portainer temporalmente..."
    docker compose -f docker-compose.full.yml stop portainer
    wait_with_progress 150 "Esperando PortainerDown (2-3 min)"
    docker compose -f docker-compose.full.yml start portainer
    echo -e "${GREEN}✅ Portainer test completado${NC}"
    echo ""
    
    check_active_alerts
}

# ==========================================
# MENÚ PRINCIPAL
# ==========================================
while true; do
    show_menu
    read -p "Selecciona una opción (0-7): " option
    
    case $option in
        1)
            if [[ ! " ${SELECTED_CATEGORIES[@]} " =~ " Sistema " ]]; then
                SELECTED_CATEGORIES+=("Sistema")
                echo -e "${GREEN}✓ Alertas de Sistema agregadas${NC}"
            else
                echo -e "${YELLOW}⚠ Ya seleccionaste esta categoría${NC}"
            fi
            sleep 1
            ;;
        2)
            if [[ ! " ${SELECTED_CATEGORIES[@]} " =~ " Aplicaciones " ]]; then
                SELECTED_CATEGORIES+=("Aplicaciones")
                echo -e "${GREEN}✓ Alertas de Aplicaciones agregadas${NC}"
            else
                echo -e "${YELLOW}⚠ Ya seleccionaste esta categoría${NC}"
            fi
            sleep 1
            ;;
        3)
            if [[ ! " ${SELECTED_CATEGORIES[@]} " =~ " Netdata " ]]; then
                SELECTED_CATEGORIES+=("Netdata")
                echo -e "${GREEN}✓ Alertas de Netdata agregadas${NC}"
            else
                echo -e "${YELLOW}⚠ Ya seleccionaste esta categoría${NC}"
            fi
            sleep 1
            ;;
        4)
            if [[ ! " ${SELECTED_CATEGORIES[@]} " =~ " K6 " ]]; then
                SELECTED_CATEGORIES+=("K6")
                echo -e "${GREEN}✓ Alertas de K6 agregadas${NC}"
            else
                echo -e "${YELLOW}⚠ Ya seleccionaste esta categoría${NC}"
            fi
            sleep 1
            ;;
        5)
            if [[ ! " ${SELECTED_CATEGORIES[@]} " =~ " CI/CD " ]]; then
                SELECTED_CATEGORIES+=("CI/CD")
                echo -e "${GREEN}✓ Alertas de CI/CD agregadas${NC}"
            else
                echo -e "${YELLOW}⚠ Ya seleccionaste esta categoría${NC}"
            fi
            sleep 1
            ;;
        6)
            if [[ ! " ${SELECTED_CATEGORIES[@]} " =~ " Monitoreo " ]]; then
                SELECTED_CATEGORIES+=("Monitoreo")
                echo -e "${GREEN}✓ Alertas de Monitoreo agregadas${NC}"
            else
                echo -e "${YELLOW}⚠ Ya seleccionaste esta categoría${NC}"
            fi
            sleep 1
            ;;
        7)
            SELECTED_CATEGORIES=("Sistema" "Aplicaciones" "Netdata" "K6" "CI/CD" "Monitoreo")
            echo -e "${GREEN}✓ Todas las categorías seleccionadas${NC}"
            sleep 1
            ;;
        0)
            if confirm_selection; then
                break
            fi
            ;;
        *)
            echo -e "${RED}❌ Opción inválida${NC}"
            sleep 1
            ;;
    esac
done

# ==========================================
# EJECUTAR PRUEBAS
# ==========================================
clear
echo -e "${CYAN}=========================================="
echo "🚀 INICIANDO PRUEBAS DE ALERTAS"
echo -e "==========================================${NC}"
echo ""

START_TIME=$(date +%s)

for category in "${SELECTED_CATEGORIES[@]}"; do
    case $category in
        "Sistema")
            test_sistema
            ;;
        "Aplicaciones")
            test_aplicaciones
            ;;
        "Netdata")
            test_netdata
            ;;
        "K6")
            test_k6
            ;;
        "CI/CD")
            test_cicd
            ;;
        "Monitoreo")
            test_monitoreo
            ;;
    esac
done

END_TIME=$(date +%s)
DURATION=$((END_TIME - START_TIME))
MINUTES=$((DURATION / 60))
SECONDS=$((DURATION % 60))

# ==========================================
# RESUMEN FINAL
# ==========================================
echo ""
echo -e "${CYAN}==========================================="
echo "✅ PRUEBAS COMPLETADAS"
echo -e "==========================================${NC}"
echo ""
echo -e "${GREEN}Tiempo total: ${MINUTES}m ${SECONDS}s${NC}"
echo ""

# Resumen final
check_active_alerts
echo ""

echo -e "${BLUE}📊 VERIFICACIONES:${NC}"
echo "1. Prometheus Alerts:"
echo "   curl -s $PROMETHEUS_URL/api/v1/alerts | jq '.data.alerts[] | {alert: .labels.alertname, state: .state}'"
echo ""
echo "2. Alertmanager Alerts:"
echo "   curl -s $ALERTMANAGER_URL/api/v1/alerts | jq '.data[] | {alertname: .labels.alertname, status: .status.state}'"
echo ""
echo "3. Abrir UI:"
echo "   Prometheus: $PROMETHEUS_URL"
echo "   Alertmanager: $ALERTMANAGER_URL"
echo ""

echo -e "${YELLOW}📧 VERIFICAR NOTIFICACIONES:${NC}"
echo "1. Email: pablopolis2016@gmail.com"
echo "2. Email: jflores@unis.edu.gt"
echo "3. Slack: Canal #ensurance-alerts"
echo ""

echo -e "${PURPLE}💡 NOTAS:${NC}"
echo "- Algunas alertas requieren configuración específica"
echo "- Las alertas se repetirán según su configuración"
echo "- Critical: cada 5 min, Warning: cada 1 hora, Info: cada 6 horas"
echo ""

echo -e "${CYAN}=========================================="
echo "FIN DEL TEST"
echo -e "==========================================${NC}"
