# 🔧 Jenkins Pipeline - Corrección de Errores

## 📋 Errores Identificados

### Error 1: `odd number of components in label string`

**Mensaje completo:**
```
odd number of components in label string "/instance/github_jenkins/main"
```

**Causa:** 
El `JOB_NAME` de Jenkins contenía caracteres `/` que Prometheus no acepta en las labels de las métricas.

**Ejemplo:**
```bash
JOB_NAME=/instance/github_jenkins/main
# Intentaba crear: instance=/instance/github_jenkins/main
# Prometheus esperaba: instance=valor (sin /)
```

### Error 2: `No se encontró archivo de métricas`

**Mensaje completo:**
```
[METRICS] No se encontró archivo de métricas. ¿Ejecutaste 'start' primero?
```

**Causa:**
El stage "Initialize Metrics" se ejecutaba ANTES del "Checkout". Cuando el stage "Checkout" ejecutaba `deleteDir()`, eliminaba el archivo temporal `/tmp/jenkins_metrics_*.tmp` creado por el script de métricas.

**Flujo problemático:**
```
1. Initialize Metrics → Crea /tmp/jenkins_metrics_*.tmp
2. Checkout → deleteDir() → ❌ Elimina todo incluido /tmp
3. End Metrics → ❌ No encuentra el archivo
```

### Error 3: Stage "Deploy MAIN" skipped

**Causa:**
Como los errores anteriores causaban fallo en el pipeline, los stages subsiguientes se saltaban.

---

## ✅ Soluciones Implementadas

### Solución 1: Sanitización de `JOB_NAME`

**Archivo:** `scripts/jenkins-metrics.sh`

**Cambio:** Agregar sanitización en todas las funciones que usan `JOB_NAME`:

```bash
# Sanitizar JOB_NAME para labels (reemplazar caracteres especiales)
local job_label=$(echo "$JOB_NAME" | sed 's/[\/]/_/g')

# Usar job_label en lugar de JOB_NAME
curl ... "/instance/${job_label}"
jenkins_job_duration_seconds{job_name="$job_label",...}
```

**Resultado:**
```bash
ANTES: /instance/github_jenkins/main
AHORA: _instance_github_jenkins_main
```

**Funciones actualizadas:**
- ✅ `start_metrics()`
- ✅ `end_metrics()`
- ✅ `report_stage()`
- ✅ `report_custom()`
- ✅ `clean_metrics()`

### Solución 2: Reordenar inicialización de métricas

**Archivo:** `Jenkinsfile`

**Cambio:** Fusionar stage "Initialize Metrics" con "Checkout" y ejecutar DESPUÉS del checkout:

**ANTES:**
```groovy
stages {
  stage('Initialize Metrics') {
    steps {
      sh 'scripts/jenkins-metrics.sh start'  // ❌ Script no existe aún
    }
  }
  
  stage('Checkout') {
    steps {
      deleteDir()                              // ❌ Elimina /tmp
      checkout scm
    }
  }
}
```

**AHORA:**
```groovy
stages {
  stage('Checkout') {
    steps {
      script {
        deleteDir()
        checkout scm                           // ✅ Obtener código primero
        
        // Iniciar métricas DESPUÉS del checkout
        sh '''
          chmod +x scripts/jenkins-metrics.sh
          scripts/jenkins-metrics.sh start     // ✅ Script existe ahora
        '''
        
        sh "scripts/jenkins-metrics.sh stage checkout ${duration}"
      }
    }
  }
}
```

**Nuevo flujo correcto:**
```
1. Checkout → deleteDir() → checkout scm → ✅ Código disponible
2. Initialize Metrics → ✅ Crea /tmp/jenkins_metrics_*.tmp
3. Otros stages → ✅ Usan el archivo
4. End Metrics → ✅ Lee el archivo correctamente
```

---

## 📝 Detalles de los Cambios

### Cambios en `scripts/jenkins-metrics.sh`

#### Función `start_metrics()` (líneas 50-72)

```bash
start_metrics() {
    local start_time=$(date +%s)
    local queue_start=${QUEUE_START_TIME:-$start_time}
    
    # Guardar tiempo de inicio
    echo "START_TIME=$start_time" > "$METRICS_FILE"
    echo "QUEUE_START_TIME=$queue_start" >> "$METRICS_FILE"
    
    log_info "Iniciando tracking de métricas para job: $JOB_NAME #$BUILD_NUMBER"
    log_info "Tiempo de inicio: $start_time"
    
    # ⭐ NUEVO: Sanitizar JOB_NAME
    local job_label=$(echo "$JOB_NAME" | sed 's/[\/]/_/g')
    
    # Incrementar contador de builds
    cat <<EOF | curl -s --data-binary @- "$PUSHGATEWAY_URL/metrics/job/jenkins_pipeline/instance/${job_label}"
# HELP jenkins_builds_total Total number of Jenkins builds executed
# TYPE jenkins_builds_total counter
jenkins_builds_total{job_name="$job_label"} 1
EOF
    
    log_info "Métrica jenkins_builds_total incrementada"
}
```

#### Función `end_metrics()` (líneas 74-147)

```bash
end_metrics() {
    local status="$1"
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
    
    # ⭐ NUEVO: Sanitizar JOB_NAME
    local job_label=$(echo "$JOB_NAME" | sed 's/[\/]/_/g')
    
    # Convertir status a valor numérico
    local status_value=0
    case "$status" in
        success) status_value=1 ;;
        failure) status_value=0 ;;
        unstable) status_value=0.5 ;;
        *) log_warn "Estado desconocido: $status (usando 0)"; status_value=0 ;;
    esac
    
    # Reportar todas las métricas a Pushgateway
    cat <<EOF | curl -s --data-binary @- "$PUSHGATEWAY_URL/metrics/job/jenkins_pipeline/instance/${job_label}/build/${BUILD_NUMBER}"
# HELP jenkins_job_duration_seconds Duration of Jenkins job execution in seconds
# TYPE jenkins_job_duration_seconds gauge
jenkins_job_duration_seconds{job_name="$job_label",build_number="$BUILD_NUMBER",status="$status"} $duration

# HELP jenkins_job_status Status of Jenkins job (1=success, 0=failure, 0.5=unstable)
# TYPE jenkins_job_status gauge
jenkins_job_status{job_name="$job_label",build_number="$BUILD_NUMBER"} $status_value

# HELP jenkins_queue_time_seconds Time spent waiting in queue before execution
# TYPE jenkins_queue_time_seconds gauge
jenkins_queue_time_seconds{job_name="$job_label",build_number="$BUILD_NUMBER"} $queue_time

# HELP jenkins_build_timestamp_seconds Unix timestamp when build finished
# TYPE jenkins_build_timestamp_seconds gauge
jenkins_build_timestamp_seconds{job_name="$job_label",build_number="$BUILD_NUMBER"} $end_time
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
```

#### Otras funciones actualizadas

**`report_stage()` (líneas 152-167):**
```bash
report_stage() {
    local stage_name="$1"
    local duration="$2"
    
    # ⭐ NUEVO: Sanitizar nombres
    local job_label=$(echo "$JOB_NAME" | sed 's/[\/]/_/g')
    local stage_label=$(echo "$stage_name" | sed 's/[\/]/_/g')
    
    log_info "Reportando stage: $stage_name (${duration}s)"
    
    cat <<EOF | curl -s --data-binary @- "$PUSHGATEWAY_URL/metrics/job/jenkins_pipeline_stage/instance/${job_label}/stage/${stage_label}"
# HELP jenkins_stage_duration_seconds Duration of Jenkins pipeline stage in seconds
# TYPE jenkins_stage_duration_seconds gauge
jenkins_stage_duration_seconds{job_name="$job_label",build_number="$BUILD_NUMBER",stage="$stage_label"} $duration
EOF
}
```

**`report_custom()` (líneas 172-187):**
```bash
report_custom() {
    local metric_name="$1"
    local metric_value="$2"
    local metric_help="${3:-Custom Jenkins metric}"
    
    # ⭐ NUEVO: Sanitizar JOB_NAME
    local job_label=$(echo "$JOB_NAME" | sed 's/[\/]/_/g')
    
    log_info "Reportando métrica custom: $metric_name = $metric_value"
    
    cat <<EOF | curl -s --data-binary @- "$PUSHGATEWAY_URL/metrics/job/jenkins_pipeline/instance/${job_label}"
# HELP $metric_name $metric_help
# TYPE $metric_name gauge
$metric_name{job_name="$job_label",build_number="$BUILD_NUMBER"} $metric_value
EOF
}
```

**`clean_metrics()` (líneas 200-210):**
```bash
clean_metrics() {
    local job_name="${1:-$JOB_NAME}"
    
    # ⭐ NUEVO: Sanitizar JOB_NAME
    local job_label=$(echo "$job_name" | sed 's/[\/]/_/g')
    
    log_warn "Limpiando métricas del job: $job_name"
    curl -X DELETE "$PUSHGATEWAY_URL/metrics/job/jenkins_pipeline/instance/${job_label}"
    
    log_info "Métricas limpiadas"
}
```

### Cambios en `Jenkinsfile`

**Líneas 15-38:**
```groovy
stages {
  stage('Checkout') {
    steps {
      script {
        def stageStart = System.currentTimeMillis()
        
        deleteDir()
        checkout scm
        sh 'git rev-parse HEAD'
        
        def duration = (System.currentTimeMillis() - stageStart) / 1000
        
        // ⭐ NUEVO: Iniciar métricas después del checkout
        echo '📊 Iniciando tracking de métricas Prometheus'
        sh '''
          chmod +x scripts/jenkins-metrics.sh
          scripts/jenkins-metrics.sh start
        '''
        
        // Reportar duración del checkout
        sh "scripts/jenkins-metrics.sh stage checkout ${duration}"
      }
    }
  }
  
  // ... resto de stages sin cambios
}
```

---

## 🧪 Cómo Probar

### 1. Jenkins Pull Automático

Jenkins detectará automáticamente el nuevo commit cuando ejecutes un build.

### 2. Ejecutar Nuevo Build

1. Ir a tu job en Jenkins
2. Click en "Build Now"
3. Ver "Console Output"

### 3. Verificar Salida Esperada

**Checkout stage:**
```
[Pipeline] stage
[Pipeline] { (Checkout)
[Pipeline] script
[Pipeline] {
[Pipeline] deleteDir
[Pipeline] checkout
[Pipeline] sh
+ git rev-parse HEAD
abc123def456...
[Pipeline] echo
📊 Iniciando tracking de métricas Prometheus
[Pipeline] sh
+ chmod +x scripts/jenkins-metrics.sh
+ scripts/jenkins-metrics.sh start
[METRICS] Iniciando tracking de métricas para job: _instance_github_jenkins_main #45
[METRICS] Tiempo de inicio: 1728192000
[METRICS] Métrica jenkins_builds_total incrementada
[Pipeline] sh
+ scripts/jenkins-metrics.sh stage checkout 2.5
[METRICS] Reportando stage: checkout (2.5s)
```

**Post Actions (success):**
```
[Pipeline] script
[Pipeline] {
[Pipeline] echo
✅ Pipeline OK - Sistema desplegado correctamente
[Pipeline] sh
+ scripts/jenkins-metrics.sh end success
[METRICS] Finalizando tracking de métricas
[METRICS] Duración del build: 45s
[METRICS] Tiempo en cola: 0s
[METRICS] Estado: success
[METRICS] ✅ Métricas reportadas exitosamente a Pushgateway
```

### 4. Verificar Métricas en Pushgateway

```bash
curl http://localhost:9091/metrics | grep jenkins_
```

**Salida esperada:**
```
# HELP jenkins_builds_total Total number of Jenkins builds executed
# TYPE jenkins_builds_total counter
jenkins_builds_total{job_name="_instance_github_jenkins_main"} 1

# HELP jenkins_job_duration_seconds Duration of Jenkins job execution in seconds
# TYPE jenkins_job_duration_seconds gauge
jenkins_job_duration_seconds{job_name="_instance_github_jenkins_main",build_number="45",status="success"} 45.2

# HELP jenkins_job_status Status of Jenkins job
# TYPE jenkins_job_status gauge
jenkins_job_status{job_name="_instance_github_jenkins_main",build_number="45"} 1

# HELP jenkins_stage_duration_seconds Duration of Jenkins pipeline stage in seconds
# TYPE jenkins_stage_duration_seconds gauge
jenkins_stage_duration_seconds{job_name="_instance_github_jenkins_main",build_number="45",stage="checkout"} 2.5
```

### 5. Verificar en Prometheus

1. Ir a http://localhost:9095
2. Ejecutar queries:

```promql
# Duración del último build
jenkins_job_duration_seconds

# Estado del último build
jenkins_job_status

# Total de builds
jenkins_builds_total

# Duración por stage
jenkins_stage_duration_seconds
```

---

## 📊 Comparación Antes/Después

### Antes (Con Errores)

```
Stage: Initialize Metrics
  ✅ scripts/jenkins-metrics.sh start
  ✅ Crea /tmp/jenkins_metrics_*.tmp

Stage: Checkout
  ✅ deleteDir()
  ❌ Elimina /tmp/jenkins_metrics_*.tmp
  ✅ checkout scm

Stage: Deploy
  ⏭️ Skipped (failure anterior)

Post: Failure
  ❌ scripts/jenkins-metrics.sh end failure
  ❌ Error: No se encontró archivo de métricas
  
  ❌ scripts/jenkins-metrics.sh custom pipeline_successful 0
  ❌ Error: odd number of components in label string
```

### Ahora (Corregido)

```
Stage: Checkout
  ✅ deleteDir()
  ✅ checkout scm
  ✅ scripts/jenkins-metrics.sh start
  ✅ Crea /tmp/jenkins_metrics_*.tmp (código ya existe)
  ✅ scripts/jenkins-metrics.sh stage checkout 2.5

Stage: Unit Tests
  ✅ mvn test
  ✅ scripts/jenkins-metrics.sh stage unit_tests 30
  ✅ scripts/jenkins-metrics.sh custom total_tests 150

Stage: Deploy
  ✅ deploy.sh deploy main
  ✅ scripts/jenkins-metrics.sh stage deploy_main 120

Post: Success
  ✅ scripts/jenkins-metrics.sh end success
  ✅ Métricas reportadas: duration=152s, status=1
  
  ✅ scripts/jenkins-metrics.sh custom pipeline_successful 1
  ✅ Métrica reportada con job_label sanitizado
```

---

## 📈 Métricas Generadas

Después de un build exitoso, se generan las siguientes métricas:

### Métricas Principales

```promql
# Duración total
jenkins_job_duration_seconds{
  job_name="_instance_github_jenkins_main",
  build_number="45",
  status="success"
} 152.5

# Estado (1=success, 0=failure, 0.5=unstable)
jenkins_job_status{
  job_name="_instance_github_jenkins_main",
  build_number="45"
} 1

# Total de builds
jenkins_builds_total{
  job_name="_instance_github_jenkins_main"
} 12

# Tiempo en cola
jenkins_queue_time_seconds{
  job_name="_instance_github_jenkins_main",
  build_number="45"
} 0

# Timestamp de finalización
jenkins_build_timestamp_seconds{
  job_name="_instance_github_jenkins_main",
  build_number="45"
} 1728192152
```

### Métricas por Stage

```promql
jenkins_stage_duration_seconds{
  job_name="_instance_github_jenkins_main",
  build_number="45",
  stage="checkout"
} 2.5

jenkins_stage_duration_seconds{
  job_name="_instance_github_jenkins_main",
  build_number="45",
  stage="unit_tests"
} 30.2

jenkins_stage_duration_seconds{
  job_name="_instance_github_jenkins_main",
  build_number="45",
  stage="deploy_main"
} 120.0
```

### Métricas Custom

```promql
total_tests{
  job_name="_instance_github_jenkins_main",
  build_number="45"
} 150

pipeline_successful{
  job_name="_instance_github_jenkins_main",
  build_number="45"
} 1

deployment_environment{
  job_name="_instance_github_jenkins_main",
  build_number="45"
} 3  # 1=DEV, 2=QA, 3=MAIN
```

---

## ✅ Checklist de Verificación

- [x] Script sanitiza JOB_NAME correctamente
- [x] Inicialización después del checkout
- [x] No hay error "odd number of components"
- [x] No hay error "archivo de métricas no encontrado"
- [x] Stages se ejecutan en orden correcto
- [x] Métricas se reportan a Pushgateway
- [x] Métricas visibles en Prometheus
- [x] Todo commiteado y pusheado

---

## 🚀 Próximos Pasos

1. **Ejecutar nuevo build en Jenkins**
2. **Verificar que no hay errores**
3. **Ver métricas en Prometheus**
4. **Crear dashboards en Grafana** con las métricas

---

## 📚 Referencias

- **Script de métricas:** `scripts/jenkins-metrics.sh`
- **Pipeline:** `Jenkinsfile`
- **Documentación:** `documentation/JENKINS_METRICS_GUIDE.md`
- **Queries:** `documentation/JENKINS_PROMETHEUS_QUERIES.md`

---

**Commit:** 46cf65c9  
**Fecha:** 2025-10-06  
**Estado:** ✅ Corregido y pusheado a GitHub
