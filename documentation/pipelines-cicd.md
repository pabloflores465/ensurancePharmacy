# Pipelines CI/CD - Ensurance Pharmacy

## Índice

- [1. Resumen de Pipelines](#1-resumen-de-pipelines)
- [2. GitHub Actions Pipeline](#2-github-actions-pipeline)
- [3. Drone CI Pipeline](#3-drone-ci-pipeline)
- [4. Jenkins Pipeline](#4-jenkins-pipeline)
- [5. Comparativa de Pipelines](#5-comparativa-de-pipelines)
- [6. Diagramas](#6-diagramas)

---

## 1. Resumen de Pipelines

El sistema **Ensurance Pharmacy** implementa **3 pipelines CI/CD** paralelos para máxima flexibilidad y redundancia:

| Pipeline | Plataforma | Archivo | Trigger | Características Principales |
|----------|-----------|---------|---------|----------------------------|
| **GitHub Actions** | GitHub | `.github/workflows/ci-cd.yml` | Push/PR a main, dev, qa | Cloud-native, fácil configuración, email notifications |
| **Drone CI** | Auto-hosted | `.drone.yml` | Push a main, dev, qa | Container-native, ARM64 support, pipeline as code |
| **Jenkins** | Auto-hosted | `Jenkinsfile` | Multi-branch | Tradicional, flexible, extensible, GUI-based |

### Características Comunes

Todos los pipelines ejecutan:

✅ **Tests Unitarios**
- Backend V4 (Ensurance): JUnit + JaCoCo
- Backend V5 (Pharmacy): JUnit + JaCoCo  
- Frontend Ensurance: Vitest
- Frontend Pharmacy: Jest

✅ **Análisis de Calidad**
- SonarQube para 4 proyectos (2 backends + 2 frontends)
- Quality Gates automáticos
- Cobertura de código (JaCoCo + LCOV)

✅ **Configuración Multi-Ambiente**
- DEV: branches dev/develop
- QA: branches qa/test/staging
- MAIN: branches main/master

✅ **Notificaciones**
- Email (GitHub Actions)
- Logs persistentes (Drone/Jenkins)

---

## 2. GitHub Actions Pipeline

**Archivo**: `.github/workflows/ci-cd.yml`

### 2.1 Triggers

```yaml
on:
  push:
    branches: [main, develop, qa, dev]
  pull_request:
    branches: [main, develop, qa, dev]
```

### 2.2 Jobs (11 jobs en total)

#### **Stage 1: Testing (4 jobs paralelos)**

| Job | Descripción | Runner | Tiempo Aprox |
|-----|-------------|--------|--------------|
| `test-backend-v4` | Tests BackV4 + JaCoCo | ubuntu-latest | ~3-5 min |
| `test-backend-v5` | Tests BackV5 + JaCoCo | ubuntu-latest | ~3-5 min |
| `test-ensurance-frontend` | Tests Ensurance + Vitest | ubuntu-latest | ~2-3 min |
| `test-pharmacy-frontend` | Tests Pharmacy + Jest | ubuntu-latest | ~2-3 min |

**Configuración Backend:**
```yaml
- name: Set up JDK 23
  uses: actions/setup-java@v4
  with:
    java-version: "23"
    distribution: "temurin"

- name: Cache Maven packages
  uses: actions/cache@v4
  with:
    path: ~/.m2
    key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}

- name: Run Tests with Coverage
  run: mvn -B clean test jacoco:report
  working-directory: ./backv4
```

**Configuración Frontend:**
```yaml
- name: Setup Node.js
  uses: actions/setup-node@v4
  with:
    node-version: "18"
    cache: "npm"

- name: Run Tests with Coverage
  run: npm run test:coverage
  working-directory: ./ensurance
```

#### **Stage 2: SonarQube Analysis (4 jobs secuenciales)**

| Job | Proyecto | Depende de | Quality Gate |
|-----|----------|-----------|--------------|
| `sonarqube-ensurance-backend-analysis` | ensurance-backend-{env} | test-backend-v4 | ✅ |
| `sonarqube-ensurance-frontend-analysis` | ensurance-frontend-{env} | test-ensurance-frontend | ✅ |
| `sonarqube-pharmacy-backend-analysis` | pharmacy-backend-{env} | test-backend-v5 | ✅ |
| `sonarqube-pharmacy-frontend-analysis` | pharmacy-frontend-{env} | test-pharmacy-frontend | ✅ |

**Selección Dinámica de Proyecto:**
```yaml
-Dsonar.projectKey=${{ 
  github.ref == 'refs/heads/main' && 'ensurance-backend-main' || 
  github.ref == 'refs/heads/qa' && 'ensurance-backend-qa' || 
  'ensurance-backend-dev' 
}}
```

**Parámetros SonarQube:**
- `sonar.qualitygate.wait=true`: Espera Quality Gate
- `sonar.qualitygate.timeout=300`: Timeout de 5 minutos
- Análisis de cobertura (JaCoCo XML / LCOV)

#### **Stage 3: Deployment (3 jobs condicionales)**

| Job | Ambiente | Condición | Secretos |
|-----|----------|-----------|----------|
| `deploy-dev` | Development | `refs/heads/dev` | ENSURANCE_BACK_DEV, etc. |
| `deploy-qa` | QA/Testing | `refs/heads/qa` | ENSURANCE_BACK_QA, etc. |
| `deploy-main` | Production | `refs/heads/main` | ENSURANCE_BACK_MAIN, etc. |

**Dependencias**: Requieren que todos los análisis SonarQube sean exitosos.

#### **Stage 4: Notifications (1 job)**

**Job**: `notify-status`
- **Ejecuta**: Siempre (`if: always()`)
- **Depende de**: Todos los jobs de testing y SonarQube
- **Acción**: Envío de email con resultados

**Email Template:**
```html
<h2>🏥 EnsurancePharmacy CI/CD Pipeline</h2>
<p><strong>Status:</strong> ✅ SUCCESS / ❌ FAILED</p>
<p><strong>Environment:</strong> PRODUCTION/QA/DEV</p>
<p><strong>Branch:</strong> main</p>

<h3>📊 Job Results:</h3>
<ul>
  <li>Backend V4 Tests: ✅ success</li>
  <li>SonarQube Analysis: ✅ success</li>
  ...
</ul>
```

### 2.3 Secretos Requeridos

**SonarQube:**
- `SONAR_HOST_URL`: URL del servidor SonarQube
- `GLOBAL_SONAR`: Token global de SonarQube

**Email SMTP:**
- `SMTP_SERVER`: smtp.gmail.com
- `SMTP_PORT`: 587
- `SMTP_USERNAME`: usuario SMTP
- `SMTP_PASSWORD`: contraseña/app password
- `SMTP_FROM_EMAIL`: email remitente
- `NOTIFICATION_EMAIL`: destinatarios

**Deployment:**
- `ENSURANCE_BACK_DEV/QA/MAIN`
- `ENSURANCE_FRONT_DEV/QA/MAIN`
- `PHARMACY_BACK_DEV/QA/MAIN`
- `PHARMACY_FRONT_DEV/QA/MAIN`

### 2.4 Características Especiales

✅ **Caché Inteligente**: Maven y npm cacheados
✅ **Ejecución Paralela**: Tests en paralelo
✅ **Codecov Integration**: Upload automático de cobertura
✅ **Email Notifications**: HTML formateado con resultados
✅ **Quality Gates**: Bloqueo en fallo de calidad

---

## 3. Drone CI Pipeline

**Archivo**: `.drone.yml`

### 3.1 Configuración General

```yaml
kind: pipeline
type: docker
name: ensurance-pharmacy-pipeline
platform:
  os: linux
  arch: arm64  # Soporte ARM64 nativo
```

**Workspace:**
```yaml
workspace:
  base: /drone
  path: src
```

### 3.2 Triggers

```yaml
trigger:
  event: [push]
  branch: [main, master, dev, qa]

clone:
  depth: 0  # Full clone para análisis SonarQube
```

### 3.3 Steps (Secuencial)

#### **Step 1: Unit Tests**

```yaml
- name: unit-tests
  image: maven:3.9-eclipse-temurin-23
  environment:
    MAVEN_OPTS: -Dmaven.repo.local=/tmp/.m2
  commands:
    - mvn -B clean test jacoco:report -f backv4/pom.xml
    - mvn -B clean test jacoco:report -f backv5/pom.xml
  volumes:
    - name: maven-cache
      path: /tmp/.m2
```

**Volumen Persistente**: Cache de Maven reutilizable entre builds

#### **Step 2: Wait for SonarQube**

```yaml
- name: wait-for-sonar
  image: curlimages/curl:8.9.1
  depends_on: [unit-tests]
  commands:
    - |
      for i in $(seq 1 60); do
        if curl -fsS "$SONAR_HOST_URL/api/system/status" | grep -q '"status":"UP"'; then
          echo "SonarQube listo"; exit 0
        fi
        sleep 5
      done
      echo "Timeout"; exit 8
```

**Health Check**: Verifica que SonarQube esté disponible antes de análisis

#### **Step 3-14: SonarQube Analysis (por ambiente)**

**Patrón para cada proyecto y ambiente:**

```yaml
- name: sonarqube-ensurance-backend-analysis-dev
  image: sonarsource/sonar-scanner-cli:latest
  user: root
  environment:
    SONAR_TOKEN:
      from_secret: GLOBAL_TOKEN
  depends_on: [wait-for-sonar]
  commands:
    - |
      sonar-scanner \
        -Dsonar.host.url="$SONAR_HOST_URL" \
        -Dsonar.token="$SONAR_TOKEN" \
        -Dsonar.projectKey=ensurance-backend-dev \
        -Dsonar.projectVersion=${DRONE_BUILD_NUMBER} \
        -Dsonar.qualitygate.wait=true
  when:
    branch: [dev]
```

**Análisis por Ambiente:**

| Ambiente | Steps | Branches | Proyectos |
|----------|-------|----------|-----------|
| **DEV** | 4 (Ens Back/Front, Pharm Back/Front) | dev | *-dev |
| **QA** | 4 (Ens Back/Front, Pharm Back/Front) | qa | *-qa |
| **MAIN** | 4 (Ens Back/Front, Pharm Back/Front) | main, master | *-main |

**Total**: 12 steps de análisis SonarQube (condicionales por rama)

#### **Step 15-17: Email Notifications**

```yaml
- name: notify-success-dev
  image: drillster/drone-email
  settings:
    host: smtp.gmail.com
    port: 587
    username:
      from_secret: email_username
    password:
      from_secret: email_password
    from: noreply@ensurancepharmacy.com
    recipients:
      - pablopolis2016@gmail.com
    subject: "✅ Pipeline SUCCESS - DEV"
  when:
    branch: [dev]
    status: [success]
```

### 3.4 Volúmenes

```yaml
volumes:
  - name: maven-cache
    host:
      path: /tmp/drone-maven-cache
```

**Persistencia**: Cache de dependencias Maven entre builds

### 3.5 Características Especiales

✅ **ARM64 Native**: Soporte nativo para Apple Silicon
✅ **Container-Native**: Cada step en su propio contenedor
✅ **Conditional Execution**: Steps ejecutan según rama
✅ **Health Checks**: Verificación de SonarQube antes de análisis
✅ **SONAR_USER_HOME**: Directorios temporales aislados

---

## 4. Jenkins Pipeline

**Archivo**: `Jenkinsfile`

### 4.1 Configuración General

```groovy
pipeline {
  agent any
  
  environment {
    SONARQUBE_SERVER = 'SonarQube'
    EMAIL_TO = "jflores@unis.edu.gt"
    DOCKER_HOST = 'tcp://docker:2376'
    DOCKER_CERT_PATH = '/certs/client'
    DOCKER_TLS_VERIFY = '1'
  }
  
  options { 
    timestamps() 
  }
}
```

### 4.2 Stages

#### **Stage 1: Checkout**

```groovy
stage('Checkout') {
  steps {
    deleteDir()        // Limpia workspace
    checkout scm       // Checkout del SCM configurado
    sh 'git rev-parse HEAD'
  }
}
```

#### **Stage 2: Unit Tests & Coverage**

```groovy
stage('Unit Tests & Coverage') {
  steps {
    sh 'set -e'
    dir('backv4') { sh 'mvn -B clean test jacoco:report' }
    dir('backv5') { sh 'mvn -B clean test jacoco:report' }
  }
  post {
    always {
      junit testResults: '**/target/surefire-reports/*.xml', 
            allowEmptyResults: true
      archiveArtifacts artifacts: '**/target/site/jacoco/jacoco.xml', 
                       allowEmptyArchive: true
    }
  }
}
```

**Post Actions:**
- ✅ Publica resultados JUnit
- ✅ Archiva reportes JaCoCo

#### **Stage 3-6: SonarQube Analysis (4 stages)**

**Patrón para cada proyecto:**

```groovy
stage('SonarQube Ensurance Backend Analysis') {
  steps {
    script {
      def scannerHome = tool 'Scanner'
      def projectKey = ''
      def projectName = ''

      if (BRANCH_NAME == 'main' || BRANCH_NAME == 'master') {
        projectKey = 'ENSURANCE_BACK_MAIN'
        projectName = 'Ensurance Backend MAIN'
      } else if (BRANCH_NAME == 'qa') {
        projectKey = 'ENSURANCE_BACK_QA'
        projectName = 'Ensurance Backend QA'
      } else {
        projectKey = 'ENSURANCE_BACK_DEV'
        projectName = 'Ensurance Backend DEV'
      }

      withSonarQubeEnv("${SONARQUBE_SERVER}") {
        sh """
          "${scannerHome}/bin/sonar-scanner" \
            -Dsonar.projectKey=${projectKey} \
            -Dsonar.projectName="${projectName}" \
            -Dsonar.projectVersion=${BUILD_NUMBER} \
            -Dsonar.sources=backv4/src/main \
            -Dsonar.java.binaries=backv4/target/classes \
            -Dsonar.coverage.jacoco.xmlReportPaths=backv4/target/site/jacoco/jacoco.xml
        """
      }
    }
  }
}
```

**Frontend Analysis**: Incluye generación de cobertura antes de análisis

```groovy
cd ensurance && npm ci && npm run test:coverage || true && cd ..
```

#### **Stage 7: Quality Gate**

```groovy
stage('Quality Gate') {
  steps {
    timeout(time: 10, unit: 'MINUTES') {
      waitForQualityGate abortPipeline: true
    }
  }
}
```

**Comportamiento:**
- Espera hasta 10 minutos
- Aborta pipeline si Quality Gate falla

#### **Stage 8: Build Docker Images**

```groovy
stage('Build Docker Images') {
  when {
    anyOf {
      branch 'main'
      branch 'master'
      branch 'qa'
      branch 'dev'
    }
  }
  steps {
    script {
      sh """
        docker build -t ensurance-pharmacy:${BRANCH_NAME}-${BUILD_NUMBER} .
        docker tag ensurance-pharmacy:${BRANCH_NAME}-${BUILD_NUMBER} \
                   ensurance-pharmacy:${BRANCH_NAME}-latest
      """
    }
  }
}
```

#### **Stage 9: Deploy**

```groovy
stage('Deploy') {
  steps {
    script {
      if (BRANCH_NAME == 'dev') {
        sh './deploy.sh deploy dev'
      } else if (BRANCH_NAME == 'qa') {
        sh './deploy.sh deploy qa'
      } else if (BRANCH_NAME == 'main' || BRANCH_NAME == 'master') {
        sh './deploy.sh deploy main'
      }
    }
  }
}
```

### 4.3 Post Pipeline Actions

```groovy
post {
  always {
    junit testResults: '**/target/surefire-reports/*.xml', 
          allowEmptyResults: true
  }
  success {
    emailext(
      subject: "✅ Jenkins Pipeline SUCCESS - ${BRANCH_NAME}",
      body: """
        Build: ${BUILD_NUMBER}
        Branch: ${BRANCH_NAME}
        Status: SUCCESS
        
        View: ${BUILD_URL}
      """,
      to: "${EMAIL_TO}"
    )
  }
  failure {
    emailext(
      subject: "❌ Jenkins Pipeline FAILED - ${BRANCH_NAME}",
      body: """
        Build: ${BUILD_NUMBER}
        Branch: ${BRANCH_NAME}
        Status: FAILED
        
        Check: ${BUILD_URL}console
      """,
      to: "${EMAIL_TO}"
    )
  }
}
```

### 4.4 Características Especiales

✅ **Multi-Branch Pipeline**: Automático para todas las ramas
✅ **Quality Gate Integration**: Bloqueo automático
✅ **Docker-in-Docker**: Build de imágenes dentro de Jenkins
✅ **Artifact Archiving**: JUnit reports + JaCoCo
✅ **Email Notifications**: Success/Failure
✅ **GUI-Based**: Fácil configuración visual

---

## 5. Comparativa de Pipelines

### 5.1 Tabla Comparativa

| Característica | GitHub Actions | Drone CI | Jenkins |
|----------------|----------------|----------|---------|
| **Hosting** | Cloud (GitHub) | Self-hosted | Self-hosted |
| **Configuración** | YAML | YAML | Groovy DSL |
| **Paralelización** | Nativa (jobs) | Secuencial (steps) | Configurable |
| **ARM64 Support** | ❌ | ✅ | ✅ (con agentes) |
| **Cache** | GitHub Cache | Volume mounts | Workspace |
| **UI** | GitHub UI | Drone UI | Jenkins UI (rico) |
| **Secretos** | GitHub Secrets | Drone Secrets | Jenkins Credentials |
| **Notifications** | Email (action) | Email (plugin) | Email (nativo) |
| **Marketplace** | ✅ Grande | ❌ Limitado | ✅ Enorme |
| **Learning Curve** | Baja | Media | Alta |
| **Deployment** | Condicional | Condicional | Script-based |
| **Quality Gates** | Timeout manual | Timeout manual | Native waitForQualityGate |

### 5.2 Ventajas por Pipeline

**GitHub Actions:**
- ✅ Integración nativa con GitHub
- ✅ No requiere infraestructura
- ✅ Fácil de configurar
- ✅ Marketplace extenso
- ✅ Logs persistentes en GitHub
- ❌ Costo en repositorios privados
- ❌ Menos control sobre runners

**Drone CI:**
- ✅ Lightweight y rápido
- ✅ Container-native
- ✅ ARM64 support nativo
- ✅ Pipeline as code puro
- ✅ Self-hosted (control total)
- ❌ Menos plugins que Jenkins
- ❌ UI básica

**Jenkins:**
- ✅ Ecosistema maduro
- ✅ Plugins para todo
- ✅ UI rica y configurable
- ✅ Quality Gate nativo
- ✅ Build history completo
- ❌ Configuración compleja
- ❌ Requiere mantenimiento

### 5.3 Tiempos de Ejecución Estimados

| Pipeline | Tests | SonarQube | Total |
|----------|-------|-----------|-------|
| **GitHub Actions** | 8-12 min | 10-15 min | 20-30 min |
| **Drone CI** | 6-10 min | 12-18 min | 20-30 min |
| **Jenkins** | 8-12 min | 10-15 min | 25-35 min |

**Nota**: Tiempos varían según cache, red, y carga del servidor SonarQube.

---

## 6. Diagramas

Los siguientes diagramas PlantUML visualizan el flujo de cada pipeline:

### 6.1 GitHub Actions Pipeline

**Archivo**: [pipeline-github-actions.plantuml](./pipeline-github-actions.plantuml)

Muestra:
- 11 jobs con dependencias
- Ejecución paralela de tests
- Análisis SonarQube secuencial
- Deployment condicional
- Notificaciones

### 6.2 Drone CI Pipeline

**Archivo**: [pipeline-drone.plantuml](./pipeline-drone.plantuml)

Muestra:
- Steps secuenciales
- Condicionales por rama
- Health checks
- Análisis por ambiente
- Email notifications

### 6.3 Jenkins Pipeline

**Archivo**: [pipeline-jenkins.plantuml](./pipeline-jenkins.plantuml)

Muestra:
- 9 stages
- Lógica de branching
- Quality Gate
- Docker build
- Post actions

---

## Conclusión

El sistema **Ensurance Pharmacy** utiliza un enfoque **multi-pipeline** que proporciona:

✅ **Redundancia**: 3 pipelines independientes
✅ **Flexibilidad**: Elección de plataforma según necesidades
✅ **Control de Calidad**: SonarQube en todos los pipelines
✅ **Multi-Ambiente**: DEV, QA, MAIN en todos
✅ **Visibilidad**: Notificaciones y logs completos

Esta estrategia garantiza que el código siempre pase por múltiples validaciones antes de llegar a producción.
