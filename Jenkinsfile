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
          echo "🔍 SonarQube Analysis for branch: ${BRANCH_NAME}"
          echo "Sonar host: $SONAR_HOST_URL"
          echo "Version: ${BUILD_NUMBER}"

          # Generar coverage de frontend si existe
          if [ -d "ensurance" ]; then
            cd ensurance && npm run test:coverage || true && cd ..
          fi
          if [ -d "pharmacy" ]; then
            cd pharmacy && npm run test:unit:coverage || true && cd ..
          fi

          "${scannerHome}/bin/sonar-scanner" \
            -Dsonar.projectVersion=${BUILD_NUMBER} \
            -Dsonar.branch.name=${BRANCH_NAME} \
            -Dsonar.java.binaries=backv4/target/classes,backv5/target/classes \
            -Dsonar.coverage.jacoco.xmlReportPaths="**/target/site/jacoco/jacoco.xml" \
            -Dsonar.javascript.lcov.reportPaths="ensurance/coverage/lcov.info,pharmacy/coverage/lcov.info"
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
