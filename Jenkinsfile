pipeline {
  agent any

  environment {
    SONARQUBE_SERVER = 'SonarQube'
    EMAIL_TO = "\${EMAIL_TO}"
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
    stage('SonarQube Ensurance Analysis') {
      steps {
        script {
          def scannerHome = tool 'Scanner'
          def projectKey = ""
          def projectName = ""
          
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
          set -e
          echo "🔍 SonarQube Ensurance Analysis for branch: ${BRANCH_NAME}"
          echo "Project: ${projectKey}"
          echo "Sonar host: $SONAR_HOST_URL"
          echo "Version: ${BUILD_NUMBER}"

          # Generar coverage de ensurance frontend
          if [ -d "ensurance" ]; then
            cd ensurance && npm ci && npm run test:coverage || true && cd ..
          fi

          "${scannerHome}/bin/sonar-scanner" \
            -Dsonar.projectKey=${projectKey} \
            -Dsonar.projectName="${projectName}" \
            -Dsonar.projectVersion=${BUILD_NUMBER} \
            -Dsonar.sources=backv4/src/main,backv5/src/main,ensurance/src \
            -Dsonar.tests=backv4/src/test,backv5/src/test,ensurance/tests \
            -Dsonar.java.binaries=backv4/target/classes,backv5/target/classes \
            -Dsonar.coverage.jacoco.xmlReportPaths=backv4/target/site/jacoco/jacoco.xml,backv5/target/site/jacoco/jacoco.xml \
            -Dsonar.javascript.lcov.reportPaths=ensurance/coverage/lcov.info
        """
          }
        }
      }
    }

    stage('SonarQube Pharmacy Analysis') {
      steps {
        script {
          def scannerHome = tool 'Scanner'
          def backendProjectKey = ""
          def frontendProjectKey = ""
          def backendProjectName = ""
          def frontendProjectName = ""
          
          if (BRANCH_NAME == 'main' || BRANCH_NAME == 'master') {
            backendProjectKey = 'PHARMACY_BACK_MAIN'
            frontendProjectKey = 'PHARMACY_FRONT_MAIN'
            backendProjectName = 'Pharmacy Backend MAIN'
            frontendProjectName = 'Pharmacy Frontend MAIN'
          } else if (BRANCH_NAME == 'qa') {
            backendProjectKey = 'PHARMACY_BACK_QA'
            frontendProjectKey = 'PHARMACY_FRONT_QA'
            backendProjectName = 'Pharmacy Backend QA'
            frontendProjectName = 'Pharmacy Frontend QA'
          } else {
            backendProjectKey = 'PHARMACY_BACK_DEV'
            frontendProjectKey = 'PHARMACY_FRONT_DEV'
            backendProjectName = 'Pharmacy Backend DEV'
            frontendProjectName = 'Pharmacy Frontend DEV'
          }
          
          withSonarQubeEnv("${SONARQUBE_SERVER}") {
            sh """
          set -e
          echo "🔍 SonarQube Pharmacy Analysis for branch: ${BRANCH_NAME}"
          echo "Backend Project: ${backendProjectKey}"
          echo "Frontend Project: ${frontendProjectKey}"
          echo "Sonar host: $SONAR_HOST_URL"
          echo "Version: ${BUILD_NUMBER}"

          # Generar coverage de pharmacy frontend
          if [ -d "pharmacy" ]; then
            cd pharmacy && npm ci && npm run test:unit:coverage || true && cd ..
          fi

          # Análisis Backend Pharmacy
          "${scannerHome}/bin/sonar-scanner" \
            -Dsonar.projectKey=${backendProjectKey} \
            -Dsonar.projectName="${backendProjectName}" \
            -Dsonar.projectVersion=${BUILD_NUMBER} \
            -Dsonar.sources=pharmacy/src \
            -Dsonar.tests=pharmacy/tests \
            -Dsonar.javascript.lcov.reportPaths=pharmacy/coverage/lcov.info
            
          # Análisis Frontend Pharmacy
          "${scannerHome}/bin/sonar-scanner" \
            -Dsonar.projectKey=${frontendProjectKey} \
            -Dsonar.projectName="${frontendProjectName}" \
            -Dsonar.projectVersion=${BUILD_NUMBER} \
            -Dsonar.sources=pharmacy/src \
            -Dsonar.tests=pharmacy/tests \
            -Dsonar.javascript.lcov.reportPaths=pharmacy/coverage/lcov.info
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

    stage('Deploy DEV') {
      when { anyOf { branch 'dev'; branch 'develop'; branch 'development' } }
      steps {
        sh '''
          echo "🚀 Deploy DEV Environment (puertos 3000-3003)"

          # Usar el script unificado de despliegue
          chmod +x deploy.sh
          ./deploy.sh deploy dev --rebuild

          echo "✅ DEV desplegado en:"
          echo "   - Ensurance Frontend: http://localhost:3000"
          echo "   - Pharmacy Frontend: http://localhost:3001"
          echo "   - Ensurance Backend: http://localhost:3002/api"
          echo "   - Pharmacy Backend: http://localhost:3003/api2"
        '''
      }
    }

    stage('Deploy QA') {
      when { anyOf { branch 'qa'; branch 'test'; branch 'testing'; branch 'staging' } }
      steps {
        sh '''
          echo "🧪 Deploy QA Environment (puertos 4000-4003)"

          # Usar el script unificado de despliegue
          chmod +x deploy.sh
          ./deploy.sh deploy qa --rebuild

          echo "✅ QA desplegado en:"
          echo "   - Ensurance Frontend: http://localhost:4000"
          echo "   - Pharmacy Frontend: http://localhost:4001"
          echo "   - Ensurance Backend: http://localhost:4002/api"
          echo "   - Pharmacy Backend: http://localhost:4003/api2"
        '''
      }
    }

    stage('Deploy MAIN') {
      when { anyOf { branch 'main'; branch 'master' } }
      steps {
        sh '''
          echo "🚀 Deploy MAIN Environment (puertos 5175, 8089, 8081, 8082)"

          # Usar el script unificado de despliegue
          chmod +x deploy.sh
          ./deploy.sh deploy main --rebuild

          echo "✅ MAIN desplegado en:"
          echo "   - Ensurance Frontend: http://localhost:5175"
          echo "   - Pharmacy Frontend: http://localhost:8089"
          echo "   - Ensurance Backend: http://localhost:8081/api"
          echo "   - Pharmacy Backend: http://localhost:8082/api2"
        '''
      }
    }
  }

  post {
    always {
      script {
        // Mostrar estado de contenedores después del despliegue
        sh './deploy.sh status || true'
      }
    }
    success {
      echo '✅ Pipeline OK - Sistema desplegado correctamente'
      emailext to: "${env.EMAIL_TO}",
               subject: "✅ Deploy exitoso: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
               body: """Deploy completado exitosamente en rama ${env.BRANCH_NAME}.

Detalles: ${env.BUILD_URL}

Verificar servicios con: ./deploy.sh status"""
    }
    unsuccessful {
      emailext to: "${env.EMAIL_TO}",
               subject: "⚠️ Pipeline fallido: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
               body: """Pipeline falló en rama ${env.BRANCH_NAME}.

Detalle: ${env.BUILD_URL}

Revisar logs: ./deploy.sh logs <ambiente>"""
    }
  }
}
