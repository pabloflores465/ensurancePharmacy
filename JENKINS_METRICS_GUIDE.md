# 📊 Guía de Métricas Prometheus para Jenkins Pipeline

## 🎯 Resumen

Este sistema permite que tus pipelines de Jenkins reporten **4 métricas clave** a Prometheus:

1. **`jenkins_job_duration_seconds`** - Duración total del pipeline
2. **`jenkins_job_status`** - Estado del build (1=success, 0=failure, 0.5=unstable)
3. **`jenkins_builds_total`** - Contador total de builds ejecutados
4. **`jenkins_queue_time_seconds`** - Tiempo de espera en cola

Además, puedes reportar:
- **`jenkins_stage_duration_seconds`** - Duración de cada stage
- **Métricas custom** - Cobertura de tests, bugs, vulnerabilities, etc.

---

## 🏗️ Arquitectura

```
┌─────────────┐         ┌──────────────┐         ┌────────────┐
│   Jenkins   │ ──push─→│ Pushgateway  │ ←─pull──│ Prometheus │
│  Pipeline   │         │   :9091      │         │   :9095    │
└─────────────┘         └──────────────┘         └────────────┘
                                                         │
                                                         │
                                                   ┌─────▼──────┐
                                                   │  Grafana   │
                                                   │   :3300    │
                                                   └────────────┘
```

**Flujo:**
1. El pipeline de Jenkins ejecuta `jenkins-metrics.sh`
2. El script envía métricas a Pushgateway (puerto 9091)
3. Prometheus scrapea Pushgateway cada 15 segundos
4. Las métricas están disponibles en Prometheus y Grafana

---

## 🚀 Setup Rápido

### 1. Verificar que Pushgateway está corriendo

```bash
docker ps | grep pushgateway
curl http://localhost:9091/metrics
```

Si no está corriendo:
```bash
docker compose -f docker-compose.monitor.yml up -d pushgateway
```

### 2. Copiar el script a tu repositorio

El archivo `jenkins-metrics.sh` debe estar en la raíz de tu repositorio Git para que Jenkins pueda accederlo.

```bash
# Ya está en tu repositorio en:
# /home/pablopolis2016/Documents/ensurancePharmacy/jenkins-metrics.sh
```

### 3. Usar en tu Jenkinsfile

**Ejemplo mínimo:**

```groovy
pipeline {
    agent any
    
    environment {
        PUSHGATEWAY_URL = 'http://10.128.0.2:9091'
    }
    
    stages {
        stage('Init') {
            steps {
                sh 'chmod +x jenkins-metrics.sh && ./jenkins-metrics.sh start'
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
    }
    
    post {
        success { sh './jenkins-metrics.sh end success' }
        failure { sh './jenkins-metrics.sh end failure' }
    }
}
```

---

## 📝 Uso del Script jenkins-metrics.sh

### Comandos Disponibles

#### 1. **start** - Iniciar tracking
```bash
./jenkins-metrics.sh start
```
- Guarda el timestamp de inicio
- Incrementa el contador de builds
- Debe ejecutarse al inicio del pipeline

#### 2. **end** - Finalizar y reportar
```bash
./jenkins-metrics.sh end <status>
```
- `status`: `success`, `failure`, o `unstable`
- Calcula duración total
- Reporta todas las métricas principales
- Debe ejecutarse al final (en `post` block)

**Ejemplos:**
```bash
./jenkins-metrics.sh end success
./jenkins-metrics.sh end failure
./jenkins-metrics.sh end unstable
```

#### 3. **stage** - Reportar duración de stage
```bash
./jenkins-metrics.sh stage <stage_name> <duration_seconds>
```

**Ejemplo en Jenkinsfile:**
```groovy
stage('Build') {
    steps {
        script {
            def start = System.currentTimeMillis()
            
            sh 'mvn clean package'
            
            def duration = (System.currentTimeMillis() - start) / 1000
            sh "./jenkins-metrics.sh stage build ${duration}"
        }
    }
}
```

#### 4. **custom** - Reportar métrica personalizada
```bash
./jenkins-metrics.sh custom <metric_name> <value> [help_text]
```

**Ejemplos:**
```bash
./jenkins-metrics.sh custom test_coverage 85.5 "Code coverage percentage"
./jenkins-metrics.sh custom bugs_detected 3 "Number of bugs found"
./jenkins-metrics.sh custom deployment_size_mb 245 "Size of deployment artifact"
```

#### 5. **view** - Ver métricas actuales
```bash
./jenkins-metrics.sh view
```

#### 6. **clean** - Limpiar métricas
```bash
./jenkins-metrics.sh clean [job_name]
```

---

## 🔧 Variables de Entorno

El script usa estas variables de entorno (automáticas en Jenkins):

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `JOB_NAME` | Nombre del job de Jenkins | `ensurance-pipeline` |
| `BUILD_NUMBER` | Número de build | `42` |
| `PUSHGATEWAY_URL` | URL del Pushgateway | `http://10.128.0.2:9091` |
| `QUEUE_START_TIME` | Tiempo de entrada a la cola | `1759720000` |

---

## 📊 Métricas Generadas

### 1. jenkins_job_duration_seconds
**Tipo:** Gauge  
**Descripción:** Duración total de la ejecución del job en segundos

**Labels:**
- `job_name` - Nombre del job
- `build_number` - Número de build
- `status` - success/failure/unstable

**Ejemplo:**
```promql
jenkins_job_duration_seconds{job_name="ensurance-pipeline",build_number="42",status="success"} 245
```

### 2. jenkins_job_status
**Tipo:** Gauge  
**Descripción:** Estado del job (1=success, 0=failure, 0.5=unstable)

**Labels:**
- `job_name`
- `build_number`

**Ejemplo:**
```promql
jenkins_job_status{job_name="ensurance-pipeline",build_number="42"} 1
```

### 3. jenkins_builds_total
**Tipo:** Counter  
**Descripción:** Contador total de builds ejecutados

**Labels:**
- `job_name`

**Ejemplo:**
```promql
jenkins_builds_total{job_name="ensurance-pipeline"} 42
```

### 4. jenkins_queue_time_seconds
**Tipo:** Gauge  
**Descripción:** Tiempo de espera en cola antes de ejecutarse

**Labels:**
- `job_name`
- `build_number`

**Ejemplo:**
```promql
jenkins_queue_time_seconds{job_name="ensurance-pipeline",build_number="42"} 15
```

### 5. jenkins_stage_duration_seconds
**Tipo:** Gauge  
**Descripción:** Duración de cada stage individual

**Labels:**
- `job_name`
- `build_number`
- `stage` - Nombre del stage

**Ejemplo:**
```promql
jenkins_stage_duration_seconds{job_name="ensurance-pipeline",build_number="42",stage="build"} 120
```

---

## 📈 Queries PromQL Útiles

### Duración promedio de builds
```promql
avg(jenkins_job_duration_seconds) by (job_name)
```

### Tasa de éxito de builds
```promql
sum(jenkins_job_status{status="success"}) 
/ 
sum(jenkins_builds_total)
```

### Builds por hora
```promql
rate(jenkins_builds_total[1h]) * 3600
```

### Duración p95 de builds
```promql
histogram_quantile(0.95, 
  rate(jenkins_job_duration_seconds[1h])
)
```

### Top 5 stages más lentos
```promql
topk(5, 
  avg(jenkins_stage_duration_seconds) by (stage)
)
```

### Tiempo promedio en cola
```promql
avg(jenkins_queue_time_seconds) by (job_name)
```

### Builds fallidos en las últimas 24h
```promql
sum(jenkins_job_status{status="failure"} offset 24h)
```

### Duración total de todos los builds
```promql
sum(jenkins_job_duration_seconds)
```

---

## 🎨 Ejemplo Completo: Pipeline con Todas las Métricas

```groovy
pipeline {
    agent any
    
    environment {
        PUSHGATEWAY_URL = 'http://10.128.0.2:9091'
    }
    
    stages {
        stage('Initialize') {
            steps {
                sh '''
                    chmod +x jenkins-metrics.sh
                    ./jenkins-metrics.sh start
                '''
            }
        }
        
        stage('Build') {
            steps {
                script {
                    def start = System.currentTimeMillis()
                    
                    sh '''
                        cd backv5
                        mvn clean package -DskipTests
                    '''
                    
                    def duration = (System.currentTimeMillis() - start) / 1000
                    sh "./jenkins-metrics.sh stage build ${duration}"
                }
            }
        }
        
        stage('Test') {
            steps {
                script {
                    def start = System.currentTimeMillis()
                    
                    sh '''
                        cd backv5
                        mvn test jacoco:report
                    '''
                    
                    // Extraer cobertura de tests (ejemplo simplificado)
                    def coverage = 85.5
                    sh "./jenkins-metrics.sh custom test_coverage ${coverage}"
                    
                    def duration = (System.currentTimeMillis() - start) / 1000
                    sh "./jenkins-metrics.sh stage test ${duration}"
                }
            }
        }
        
        stage('Deploy') {
            when {
                branch 'main'
            }
            steps {
                script {
                    def start = System.currentTimeMillis()
                    
                    sh 'docker compose -f docker-compose.main.yml up -d'
                    
                    def duration = (System.currentTimeMillis() - start) / 1000
                    sh "./jenkins-metrics.sh stage deploy ${duration}"
                }
            }
        }
    }
    
    post {
        success {
            sh './jenkins-metrics.sh end success'
        }
        failure {
            sh './jenkins-metrics.sh end failure'
        }
        unstable {
            sh './jenkins-metrics.sh end unstable'
        }
    }
}
```

---

## 🔍 Troubleshooting

### Problema: "curl: command not found"
**Solución:** Instalar curl en el agente de Jenkins
```bash
apt-get update && apt-get install -y curl
```

### Problema: "Connection refused to Pushgateway"
**Solución:** Verificar conectividad
```bash
# Desde Jenkins container
curl http://10.128.0.2:9091/metrics

# Si falla, verificar que Pushgateway esté corriendo
docker ps | grep pushgateway
```

### Problema: "No se ven métricas en Prometheus"
**Solución:** Verificar que Prometheus está scrapeando Pushgateway
```bash
# Ver targets en Prometheus
curl http://localhost:9095/api/v1/targets | jq '.data.activeTargets[] | select(.job=="jenkins-pipeline")'
```

### Problema: "Permission denied: jenkins-metrics.sh"
**Solución:** Agregar chmod en el Jenkinsfile
```groovy
sh 'chmod +x jenkins-metrics.sh && ./jenkins-metrics.sh start'
```

---

## 🎯 Mejores Prácticas

1. **Siempre ejecuta `start` al inicio del pipeline**
   - Preferiblemente en el primer stage

2. **Siempre ejecuta `end` en el bloque `post`**
   - Usa `post { success }`, `post { failure }`, etc.

3. **Reporta duración de stages críticos**
   - Build, Test, Deploy son buenos candidatos

4. **Usa métricas custom para KPIs de negocio**
   - Cobertura de tests
   - Número de bugs detectados
   - Tamaño del artefacto
   - Tiempo de warmup de la aplicación

5. **Mantén nombres de métricas consistentes**
   - Usa snake_case: `test_coverage`, no `TestCoverage`
   - Sé descriptivo: `backend_build_duration`, no `build_time`

6. **Limpia métricas antiguas periódicamente**
   - Pushgateway acumula métricas indefinidamente
   - Usa `./jenkins-metrics.sh clean` en un job cron

---

## 📚 Siguientes Pasos

1. **Crear Dashboard en Grafana**
   - Ve a http://localhost:3300
   - Crea panels con las queries PromQL de arriba

2. **Configurar Alertas**
   - Alertar si duración de build > 10 minutos
   - Alertar si tasa de fallo > 20%
   - Alertar si tiempo en cola > 5 minutos

3. **Optimizar Pipelines**
   - Usa las métricas para identificar stages lentos
   - Optimiza los stages con mayor duración

4. **Reportar a stakeholders**
   - Genera reportes semanales de métricas
   - Muestra tendencias de mejora

---

## 📖 Referencias

- **Pushgateway Docs**: https://github.com/prometheus/pushgateway
- **Prometheus Docs**: https://prometheus.io/docs/
- **Jenkins Pipeline Syntax**: https://www.jenkins.io/doc/book/pipeline/syntax/
- **PromQL Guide**: https://prometheus.io/docs/prometheus/latest/querying/basics/
