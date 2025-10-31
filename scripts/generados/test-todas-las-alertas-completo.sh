#!/bin/bash

# Script para probar TODAS las alertas del sistema
# Genera condiciones que disparan cada una de las 65 alertas configuradas

set -e

PROMETHEUS_URL="http://localhost:9090"
ALERTMANAGER_URL="http://localhost:9093"
PUSHGATEWAY_URL="http://localhost:9091"

echo "=========================================="
echo "🧪 TEST COMPLETO DE ALERTAS - 65 ALERTAS"
echo "=========================================="
echo ""
echo "Este script probará TODAS las alertas configuradas"
echo "Verifica emails y Slack después de ejecutar"
echo ""
read -p "¿Continuar? (y/n): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    exit 1
fi

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
    local count=$(curl -s "$PROMETHEUS_URL/api/v1/alerts" | jq '.data.alerts | length')
    echo "📊 Alertas activas: $count"
}

echo ""
echo "==========================================="
echo "CATEGORÍA 1: ALERTAS DE SISTEMA (12)"
echo "==========================================="
echo ""

echo "🔹 Test 0: HighRAMUsage Alert (Primera alerta - 60%)"
echo "Simulando uso moderado de RAM (60%+)..."
stress-ng --vm 1 --vm-bytes 65% --timeout 120s --quiet &
STRESS_PID=$!
wait_with_progress 90 "Esperando HighRAMUsage - Envía a Gmail y Slack (1-2 min)"
kill -9 $STRESS_PID 2>/dev/null || true
echo "✅ HighRAMUsage test completado"
echo "📧 Verifica tu email y Slack para esta alerta"
echo ""

echo "🔹 Test 1-2: CPU Alerts"
echo "Simulando alta carga de CPU..."
stress-ng --cpu 8 --timeout 180s --quiet &
STRESS_PID=$!
wait_with_progress 150 "Esperando que se disparen HighCPUUsage y CriticalCPUUsage (2-3 min)"
kill -9 $STRESS_PID 2>/dev/null || true
echo "✅ CPU tests completados"
echo ""

echo "🔹 Test 3-4: Memory Alerts"
echo "Simulando alto uso de memoria..."
stress-ng --vm 2 --vm-bytes 90% --timeout 180s --quiet &
STRESS_PID=$!
wait_with_progress 150 "Esperando HighMemoryUsage y CriticalMemoryUsage (2-3 min)"
kill -9 $STRESS_PID 2>/dev/null || true
echo "✅ Memory tests completados"
echo ""

echo "🔹 Test 5-7: Disk Alerts"
echo "Creando archivo grande para simular disco lleno..."
TEMP_FILE="/tmp/disk_test_$(date +%s).bin"
dd if=/dev/zero of=$TEMP_FILE bs=1M count=5000 2>/dev/null || true
wait_with_progress 360 "Esperando HighDiskUsage, CriticalDiskUsage, DiskAlmostFull (5-6 min)"
rm -f $TEMP_FILE
echo "✅ Disk tests completados"
echo ""

echo "🔹 Test 8-9: Network Alerts"
echo "Simulando alto tráfico de red..."
# Generar tráfico de red intenso
(for i in {1..100}; do
    curl -s http://localhost:3100 > /dev/null &
    curl -s http://localhost:3101 > /dev/null &
done) &
wait_with_progress 360 "Esperando HighNetworkReceive y HighNetworkTransmit (5-6 min)"
echo "✅ Network tests completados"
echo ""

echo "🔹 Test 10: NodeExporterDown"
echo "Deteniendo Node Exporter temporalmente..."
docker compose -f docker-compose.full.yml stop node-exporter
wait_with_progress 90 "Esperando NodeExporterDown (1-2 min)"
docker compose -f docker-compose.full.yml start node-exporter
echo "✅ NodeExporter test completado"
echo ""

echo "🔹 Test 11: HighSystemLoad"
echo "Generando alta carga del sistema..."
stress-ng --cpu 16 --io 4 --vm 2 --timeout 360s --quiet &
STRESS_PID=$!
wait_with_progress 360 "Esperando HighSystemLoad (5-6 min)"
kill -9 $STRESS_PID 2>/dev/null || true
echo "✅ System load test completado"
echo ""

check_active_alerts
echo ""

echo "==========================================="
echo "CATEGORÍA 2: ALERTAS DE APLICACIONES (8)"
echo "==========================================="
echo ""

echo "🔹 Test 12-15: Application Down Alerts"
echo "Deteniendo aplicaciones temporalmente..."

docker compose -f docker-compose.full.yml stop ensurance-pharmacy-monitoring
wait_with_progress 90 "Esperando PharmacyBackendDown, EnsuranceBackendDown (1-2 min)"
wait_with_progress 90 "Esperando EnsuranceFrontendDown, PharmacyFrontendDown (2-3 min)"
docker compose -f docker-compose.full.yml start ensurance-pharmacy-monitoring
echo "✅ Application down tests completados"
echo ""

echo "🔹 Test 16-19: Node.js Performance Alerts"
echo "Nota: Estas alertas requieren que las aplicaciones Node.js expongan métricas"
echo "Si las apps no tienen prom-client configurado, estas alertas no se dispararán"
echo "ℹ️ Verificar manualmente: HighNodeMemoryBackendV5, HighNodeMemoryBackendV4"
echo "ℹ️ Verificar manualmente: HighEventLoopLag, FrequentGarbageCollection"
echo ""

check_active_alerts
echo ""

echo "==========================================="
echo "CATEGORÍA 3: ALERTAS DE RABBITMQ (12)"
echo "==========================================="
echo ""

echo "🔹 Test 21-22: RabbitMQ Down Alerts"
echo "Deteniendo RabbitMQ temporalmente..."
docker compose -f docker-compose.full.yml stop rabbitmq
wait_with_progress 90 "Esperando RabbitMQDown y RabbitMQNodeDown (1-2 min)"
docker compose -f docker-compose.full.yml start rabbitmq
wait_with_progress 30 "Esperando que RabbitMQ inicie"
echo "✅ RabbitMQ down tests completados"
echo ""

echo "🔹 Test 23-26: RabbitMQ Queue Alerts"
echo "Publicando muchos mensajes a RabbitMQ..."
docker exec ensurance-rabbitmq-full rabbitmqadmin -u admin -p changeme declare queue name=test_queue durable=true
for i in {1..1200}; do
    docker exec ensurance-rabbitmq-full rabbitmqadmin -u admin -p changeme publish exchange=amq.default routing_key=test_queue payload="test_message_$i" 2>/dev/null
done
wait_with_progress 360 "Esperando RabbitMQQueueMessagesHigh, RabbitMQQueueMessagesReady (5-6 min)"
wait_with_progress 360 "Esperando RabbitMQUnacknowledgedMessages, RabbitMQQueueNoConsumers (5-6 min)"
docker exec ensurance-rabbitmq-full rabbitmqadmin -u admin -p changeme delete queue name=test_queue
echo "✅ RabbitMQ queue tests completados"
echo ""

echo "🔹 Test 27-29: RabbitMQ Memory Alerts"
echo "Nota: Requiere configuración de memory limits en RabbitMQ"
echo "ℹ️ Verificar manualmente: RabbitMQHighMemory, RabbitMQCriticalMemory, RabbitMQMemoryAlarm"
echo ""

echo "🔹 Test 30-31: RabbitMQ Connection Alerts"
echo "Creando múltiples conexiones a RabbitMQ..."
for i in {1..120}; do
    (docker exec ensurance-rabbitmq-full rabbitmqctl list_connections > /dev/null 2>&1 &)
done
wait_with_progress 360 "Esperando RabbitMQTooManyConnections, RabbitMQTooManyChannels (5-6 min)"
echo "✅ RabbitMQ connection tests completados"
echo ""

check_active_alerts
echo ""

echo "==========================================="
echo "CATEGORÍA 4: ALERTAS DE K6 (8)"
echo "==========================================="
echo ""

echo "🔹 Test 33-40: K6 Stress Testing Alerts"
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

echo "✅ K6 tests completados"
echo ""

check_active_alerts
echo ""

echo "==========================================="
echo "CATEGORÍA 5: ALERTAS DE CI/CD (12)"
echo "==========================================="
echo ""

echo "🔹 Test 41-42: Jenkins/Pushgateway Down"
echo "Deteniendo Pushgateway temporalmente..."
docker compose -f docker-compose.full.yml stop pushgateway
wait_with_progress 150 "Esperando PushgatewayDown (2-3 min)"
docker compose -f docker-compose.full.yml start pushgateway
echo "✅ Pushgateway down test completado"
echo ""

echo "🔹 Test 43-48: Jenkins Build Alerts"
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

echo "✅ Jenkins tests completados"
echo ""

echo "🔹 Test 49-52: SonarQube y Drone"
echo "Nota: Requiere que SonarQube y Drone estén configurados y exponiendo métricas"
echo "ℹ️ Verificar manualmente: SonarQubeDown, SonarQubeQualityGateFailed"
echo "ℹ️ Verificar manualmente: DroneServerDown, DroneRunnerDown"
echo ""

check_active_alerts
echo ""

echo "==========================================="
echo "CATEGORÍA 6: ALERTAS DE MONITOREO (13)"
echo "==========================================="
echo ""

echo "🔹 Test 53-58: Prometheus Alerts"
echo "Deteniendo Prometheus temporalmente para test..."
docker compose -f docker-compose.full.yml stop prometheus
wait_with_progress 90 "Esperando PrometheusDown (solo visible en logs, Prometheus no puede auto-detectar)"
docker compose -f docker-compose.full.yml start prometheus
wait_with_progress 30 "Esperando que Prometheus reinicie"
echo "✅ Prometheus test completado"
echo ""

echo "🔹 Test 54: TargetDown"
echo "Nota: Ya se disparó con los tests anteriores de servicios down"
echo ""

echo "🔹 Test 59-60: Grafana y Netdata Down"
echo "Deteniendo Grafana temporalmente..."
docker compose -f docker-compose.full.yml stop grafana
wait_with_progress 150 "Esperando GrafanaDown (2-3 min)"
docker compose -f docker-compose.full.yml start grafana
echo "✅ Grafana test completado"
echo ""

echo "Deteniendo Netdata temporalmente..."
docker compose -f docker-compose.full.yml stop netdata
wait_with_progress 150 "Esperando NetdataDown (2-3 min)"
docker compose -f docker-compose.full.yml start netdata
echo "✅ Netdata test completado"
echo ""

echo "🔹 Test 61-63: Alertmanager Alerts"
echo "Deteniendo Alertmanager temporalmente..."
docker compose -f docker-compose.full.yml stop alertmanager
wait_with_progress 90 "Esperando AlertmanagerDown (1-2 min)"
docker compose -f docker-compose.full.yml start alertmanager
echo "✅ Alertmanager test completado"
echo ""

echo "🔹 Test 64: PortainerDown"
echo "Deteniendo Portainer temporalmente..."
docker compose -f docker-compose.full.yml stop portainer
wait_with_progress 150 "Esperando PortainerDown (2-3 min)"
docker compose -f docker-compose.full.yml start portainer
echo "✅ Portainer test completado"
echo ""

echo ""
echo "==========================================="
echo "✅ PRUEBA COMPLETA FINALIZADA"
echo "==========================================="
echo ""

# Resumen final
check_active_alerts
echo ""

echo "📊 VERIFICACIONES:"
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

echo "📧 VERIFICAR NOTIFICACIONES:"
echo "1. Email: pablopolis2016@gmail.com"
echo "2. Email: jflores@unis.edu.gt"
echo "3. Slack: Canal #ensurance-alerts"
echo ""

echo "⏱️ TIEMPO TOTAL ESTIMADO: ~45-60 minutos"
echo ""

echo "💡 NOTAS:"
echo "- Algunas alertas requieren configuración específica de aplicaciones"
echo "- Las alertas se repetirán según su configuración de repeat_interval"
echo "- Critical: cada 5 min, Warning: cada 1 hora, Info: cada 6 horas"
echo ""

echo "📋 Alertas que requieren verificación manual:"
echo "- Alertas de Node.js (16-19): Requieren prom-client en apps"
echo "- Alertas de RabbitMQ memory (27-29): Requieren memory limits"
echo "- Alertas de SonarQube (49-50): Requieren SonarQube activo"
echo "- Alertas de Drone (51-52): Requieren Drone activo"
echo "- Alertas de Prometheus internals (55-58): Difíciles de simular"
echo ""

echo "=========================================="
echo "FIN DEL TEST"
echo "=========================================="
