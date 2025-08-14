pipeline {
  agent any
  tools {
    nodejs 'NodeJS_24_5_0'
    maven  'Maven_3_9_11'
    jdk    'JDK_23'
  }

  environment {
    SONARQUBE_SERVER = 'sonarqube'
    EMAIL_TO = 'pablopolis2016@gmail.com,jflores@unis.edu.gt'
  }

  options { timestamps() }

  stages {
    stage('Checkout') {
      steps {
        deleteDir()
        checkout scm
        sh 'git rev-parse HEAD'
      }
    }

    stage('Unit Tests & Coverage') {
      steps {
        sh 'set -e'
        dir('backv4') { sh 'mvn -B clean test jacoco:report' }
        dir('backv5') { sh 'mvn -B clean test jacoco:report' }
      }
      post {
        always {
          junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
          archiveArtifacts artifacts: '**/target/site/jacoco/jacoco.xml', allowEmptyArchive: true
        }
      }
    }
    stage('SonarQube Analysis') {
      steps {
        script {
          // Debe coincidir con el nombre del SonarScanner configurado en Jenkins > Global Tool Configuration
          def scannerHome = tool 'Scanner'
          withSonarQubeEnv("${SONARQUBE_SERVER}") {
            sh """
          set -e
          echo "Sonar host: $SONAR_HOST_URL"
          echo "Version: ${BUILD_NUMBER}"

          "${scannerHome}/bin/sonar-scanner" \
            -Dsonar.projectVersion=${BUILD_NUMBER} \
            -Dsonar.java.binaries=backv4/target/classes,backv5/target/classes \
            -Dsonar.coverage.jacoco.xmlReportPaths="**/target/site/jacoco/jacoco.xml"
        """
          }
        }
      }
    }

    stage('Quality Gate') {
      steps {
        timeout(time: 10, unit: 'MINUTES') {
          waitForQualityGate abortPipeline: true
        }
      }
      post {
        unsuccessful {
          emailext to: "${env.EMAIL_TO}",
                   subject: "⛔ Quality Gate no aprobado: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                   body: "Se bloquea el pipeline por Quality Gate. Ver: ${env.BUILD_URL}"
        }
      }
    }

    stage('Deploy DEV (branch dev)') {
      when { branch 'dev' }
      steps {
        sh '''
          # Variables de entorno para DEV
          export ENS_BACKEND_HOST_PORT=8081
          export PHARM_BACKEND_HOST_PORT=8082
          export VITE_ENSURANCE_API_URL="http://localhost:${ENS_BACKEND_HOST_PORT}/api"
          export VITE_PHARMACY_API_URL="http://localhost:${PHARM_BACKEND_HOST_PORT}/api2"
          export VUE_APP_PHARMACY_API_URL="${VITE_PHARMACY_API_URL}"
          export VUE_APP_ENSURANCE_API_URL="${VITE_ENSURANCE_API_URL}"
          export NUXT_PUBLIC_ENSURANCE_API_URL="${VITE_ENSURANCE_API_URL}"

          echo "Deploy DEV → backend1/frontend1"
          docker compose -f docker-compose.ensurance.yaml up -d --build
          docker compose -f docker-compose.pharmacy.yaml up -d --build
        '''
      }
    }

    stage('Deploy UAT (branch test)') {
      when { branch 'test' }
      steps {
        sh '''
          # Variables de entorno para UAT
          export ENS_BACKEND_HOST_PORT=9081
          export PHARM_BACKEND_HOST_PORT=9082
          export VITE_ENSURANCE_API_URL="http://localhost:${ENS_BACKEND_HOST_PORT}/api"
          export VITE_PHARMACY_API_URL="http://localhost:${PHARM_BACKEND_HOST_PORT}/api2"
          export VUE_APP_PHARMACY_API_URL="${VITE_PHARMACY_API_URL}"
          export VUE_APP_ENSURANCE_API_URL="${VITE_ENSURANCE_API_URL}"
          export NUXT_PUBLIC_ENSURANCE_API_URL="${VITE_ENSURANCE_API_URL}"

          echo "Deploy UAT → backend2/frontend2"
          docker compose -f docker-compose.ensurance.yaml up -d --build
          docker compose -f docker-compose.pharmacy.yaml up -d --build
        '''
      }
    }

    stage('Deploy PROD (branch main)') {
      when { branch 'main' }
      steps {
        sh '''
          # Variables de entorno para PROD
          export ENS_BACKEND_HOST_PORT=80
          export PHARM_BACKEND_HOST_PORT=81
          export VITE_ENSURANCE_API_URL="http://localhost:${ENS_BACKEND_HOST_PORT}/api"
          export VITE_PHARMACY_API_URL="http://localhost:${PHARM_BACKEND_HOST_PORT}/api2"
          export VUE_APP_PHARMACY_API_URL="${VITE_PHARMACY_API_URL}"
          export VUE_APP_ENSURANCE_API_URL="${VITE_ENSURANCE_API_URL}"
          export NUXT_PUBLIC_ENSURANCE_API_URL="${VITE_ENSURANCE_API_URL}"

          echo "Deploy PROD → backend3/frontend3"
          docker compose -f docker-compose.ensurance.yaml up -d --build
          docker compose -f docker-compose.pharmacy.yaml up -d --build
        '''
      }
    }
  }

  post {
    success {
      echo '✅ Pipeline OK'
    }
    unsuccessful {
      emailext to: "${env.EMAIL_TO}",
               subject: "⚠️ Pipeline fallido: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
               body: "Detalle: ${env.BUILD_URL}"
    }
  }
}
