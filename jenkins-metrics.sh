#!/bin/bash

# ============================================================================
# Jenkins Pipeline Metrics Reporter for Prometheus Pushgateway
# ============================================================================
# 
# Este script reporta 4 métricas clave de performance del pipeline de Jenkins:
# 1. jenkins_job_duration_seconds - Duración de la ejecución del job
# 2. jenkins_job_status - Estado del job (1=success, 0=failure)
# 3. jenkins_builds_total - Contador total de builds
# 4. jenkins_queue_time_seconds - Tiempo de espera en cola
#
# Uso en Jenkinsfile:
#   sh './jenkins-metrics.sh start'
#   # ... tu pipeline ...
#   sh './jenkins-metrics.sh end success'
# ============================================================================

# Configuración
PUSHGATEWAY_URL="${PUSHGATEWAY_URL:-http://localhost:9091}"
JOB_NAME="${JOB_NAME:-unknown}"
BUILD_NUMBER="${BUILD_NUMBER:-0}"
METRICS_FILE="/tmp/jenkins_metrics_${JOB_NAME}_${BUILD_NUMBER}.tmp"

# Colores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# ============================================================================
# Funciones Auxiliares
# ============================================================================

log_info() {
    echo -e "${GREEN}[METRICS]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[METRICS]${NC} $1"
}

log_error() {
    echo -e "${RED}[METRICS]${NC} $1"
}

# ============================================================================
# Función: Iniciar Tracking de Métricas
# ============================================================================
start_metrics() {
    local start_time=$(date +%s)
    local queue_start=${QUEUE_START_TIME:-$start_time}
    
    # Guardar tiempo de inicio
    echo "START_TIME=$start_time" > "$METRICS_FILE"
    echo "QUEUE_START_TIME=$queue_start" >> "$METRICS_FILE"
    
    log_info "Iniciando tracking de métricas para job: $JOB_NAME #$BUILD_NUMBER"
    log_info "Tiempo de inicio: $start_time"
    
    # Incrementar contador de builds
    cat <<EOF | curl -s --data-binary @- "$PUSHGATEWAY_URL/metrics/job/jenkins_pipeline/instance/${JOB_NAME}"
# HELP jenkins_builds_total Total number of Jenkins builds executed
# TYPE jenkins_builds_total counter
jenkins_builds_total{job_name="$JOB_NAME"} 1
EOF
    
    log_info "Métrica jenkins_builds_total incrementada"
}

# ============================================================================
# Función: Finalizar y Reportar Métricas
# ============================================================================
end_metrics() {
    local status="$1"  # success, failure, unstable
    local end_time=$(date +%s)
    
    # Leer tiempo de inicio
    if [ ! -f "$METRICS_FILE" ]; then
        log_error "No se encontró archivo de métricas. ¿Ejecutaste 'start' primero?"
        return 1
    fi
    
    source "$METRICS_FILE"
    
    # Calcular duración
    local duration=$((end_time - START_TIME))
    local queue_time=$((START_TIME - QUEUE_START_TIME))
    
    log_info "Finalizando tracking de métricas"
    log_info "Duración del build: ${duration}s"
    log_info "Tiempo en cola: ${queue_time}s"
    log_info "Estado: $status"
    
    # Convertir status a valor numérico
    local status_value=0
    case "$status" in
        success)
            status_value=1
            ;;
        failure)
            status_value=0
            ;;
        unstable)
            status_value=0.5
            ;;
        *)
            log_warn "Estado desconocido: $status (usando 0)"
            status_value=0
            ;;
    esac
    
    # Reportar todas las métricas a Pushgateway
    cat <<EOF | curl -s --data-binary @- "$PUSHGATEWAY_URL/metrics/job/jenkins_pipeline/instance/${JOB_NAME}/build/${BUILD_NUMBER}"
# HELP jenkins_job_duration_seconds Duration of Jenkins job execution in seconds
# TYPE jenkins_job_duration_seconds gauge
jenkins_job_duration_seconds{job_name="$JOB_NAME",build_number="$BUILD_NUMBER",status="$status"} $duration

# HELP jenkins_job_status Status of Jenkins job (1=success, 0=failure, 0.5=unstable)
# TYPE jenkins_job_status gauge
jenkins_job_status{job_name="$JOB_NAME",build_number="$BUILD_NUMBER"} $status_value

# HELP jenkins_queue_time_seconds Time spent waiting in queue before execution
# TYPE jenkins_queue_time_seconds gauge
jenkins_queue_time_seconds{job_name="$JOB_NAME",build_number="$BUILD_NUMBER"} $queue_time

# HELP jenkins_build_timestamp_seconds Unix timestamp when build finished
# TYPE jenkins_build_timestamp_seconds gauge
jenkins_build_timestamp_seconds{job_name="$JOB_NAME",build_number="$BUILD_NUMBER"} $end_time
EOF
    
    if [ $? -eq 0 ]; then
        log_info "✅ Métricas reportadas exitosamente a Pushgateway"
    else
        log_error "❌ Error al reportar métricas a Pushgateway"
        return 1
    fi
    
    # Limpiar archivo temporal
    rm -f "$METRICS_FILE"
}

# ============================================================================
# Función: Reportar Métrica de Stage
# ============================================================================
report_stage() {
    local stage_name="$1"
    local duration="$2"
    
    log_info "Reportando stage: $stage_name (${duration}s)"
    
    cat <<EOF | curl -s --data-binary @- "$PUSHGATEWAY_URL/metrics/job/jenkins_pipeline_stage/instance/${JOB_NAME}/stage/${stage_name}"
# HELP jenkins_stage_duration_seconds Duration of Jenkins pipeline stage in seconds
# TYPE jenkins_stage_duration_seconds gauge
jenkins_stage_duration_seconds{job_name="$JOB_NAME",build_number="$BUILD_NUMBER",stage="$stage_name"} $duration
EOF
}

# ============================================================================
# Función: Reportar Métrica Custom
# ============================================================================
report_custom() {
    local metric_name="$1"
    local metric_value="$2"
    local metric_help="${3:-Custom Jenkins metric}"
    
    log_info "Reportando métrica custom: $metric_name = $metric_value"
    
    cat <<EOF | curl -s --data-binary @- "$PUSHGATEWAY_URL/metrics/job/jenkins_pipeline/instance/${JOB_NAME}"
# HELP $metric_name $metric_help
# TYPE $metric_name gauge
$metric_name{job_name="$JOB_NAME",build_number="$BUILD_NUMBER"} $metric_value
EOF
}

# ============================================================================
# Función: Ver Métricas Actuales
# ============================================================================
view_metrics() {
    log_info "Consultando métricas en Pushgateway..."
    curl -s "$PUSHGATEWAY_URL/metrics" | grep jenkins_
}

# ============================================================================
# Función: Limpiar Métricas
# ============================================================================
clean_metrics() {
    local job_name="${1:-$JOB_NAME}"
    
    log_warn "Limpiando métricas del job: $job_name"
    curl -X DELETE "$PUSHGATEWAY_URL/metrics/job/jenkins_pipeline/instance/${job_name}"
    
    log_info "Métricas limpiadas"
}

# ============================================================================
# Main
# ============================================================================

case "$1" in
    start)
        start_metrics
        ;;
    end)
        if [ -z "$2" ]; then
            log_error "Uso: $0 end <success|failure|unstable>"
            exit 1
        fi
        end_metrics "$2"
        ;;
    stage)
        if [ -z "$2" ] || [ -z "$3" ]; then
            log_error "Uso: $0 stage <stage_name> <duration_seconds>"
            exit 1
        fi
        report_stage "$2" "$3"
        ;;
    custom)
        if [ -z "$2" ] || [ -z "$3" ]; then
            log_error "Uso: $0 custom <metric_name> <value> [help_text]"
            exit 1
        fi
        report_custom "$2" "$3" "$4"
        ;;
    view)
        view_metrics
        ;;
    clean)
        clean_metrics "$2"
        ;;
    *)
        echo "Uso: $0 {start|end|stage|custom|view|clean}"
        echo ""
        echo "Comandos:"
        echo "  start                          - Iniciar tracking de métricas"
        echo "  end <status>                   - Finalizar y reportar (status: success|failure|unstable)"
        echo "  stage <name> <duration>        - Reportar duración de un stage"
        echo "  custom <name> <value> [help]   - Reportar métrica custom"
        echo "  view                           - Ver métricas actuales"
        echo "  clean [job_name]               - Limpiar métricas de un job"
        echo ""
        echo "Ejemplos:"
        echo "  $0 start"
        echo "  $0 end success"
        echo "  $0 stage build 120"
        echo "  $0 custom test_coverage 85.5 'Code coverage percentage'"
        exit 1
        ;;
esac

exit 0
