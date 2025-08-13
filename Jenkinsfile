pipeline {
  agent any
  tools {
    nodejs 'NodeJS_24_5_0'       // Global Tool: NodeJS
    maven  'Maven_3_9_11'        // Global Tool: Maven
    jdk    'JDK_23'              // Global Tool: JDK
  }

  environment {
    SONARQUBE_SERVER = 'sonarqube'                     // Nombre del server en Jenkins > System
    EMAIL_TO = 'pablopolis2016@gmail.com'
  }

  options { timestamps() }

  stages {

    stage('Checkout') {
      steps {
        deleteDir()                          // limpia workspace para evitar artefactos viejos
        checkout scm
        sh 'git rev-parse HEAD'              // log del commit exacto
      }
    }

    stage('Unit Tests & Coverage') {
  steps {
    sh 'set -e'

    // --- FRONTENDS (igual que ya tenías) ---
    sh '''
      if [ -f "./ensurance/package.json" ]; then
        echo "[frontend/ensurance] npm ci && npm test"
        cd ./ensurance && npm ci || true
        npm test --if-present -- --ci || true
        cd -
      fi
      if [ -f "./pharmacy/package.json" ]; then
        echo "[frontend/pharmacy] npm ci && npm test"
        cd ./pharmacy && npm ci || true
        npm test --if-present -- --ci || true
        cd -
      fi
    '''

    // --- JAVA por módulo (sin reactor) ---
    dir('backv4') { sh 'mvn -B clean test jacoco:report' }
    dir('backv5') { sh 'mvn -B clean test jacoco:report' }

    // Verificación rápida
    sh '''
      ls -l backv4/target/surefire-reports || true
      ls -l backv4/target/site/jacoco/jacoco.xml || true
      ls -l backv5/target/surefire-reports || true
      ls -l backv5/target/site/jacoco/jacoco.xml || true
    '''
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
          echo "Deploy DEV → backend1/frontend1"
          docker compose -f docker-compose.ensurance.yaml up -d --build
        '''
      }
    }

    stage('Deploy UAT (branch test)') {
      when { branch 'test' }
      steps {
        sh '''
          echo "Deploy UAT → backend2/frontend2"
          docker compose -f docker-compose.ensurance.yaml up -d --build
        '''
      }
    }

    stage('Deploy PROD (branch main)') {
      when { branch 'main' }
      steps {
        sh '''
          echo "Deploy PROD → backend3/frontend3"
          docker compose -f docker-compose.ensurance.yaml up -d --build
        '''
      }
    }
  }

  post {
    success {
      echo "✅ Pipeline OK"
    }
    unsuccessful {
      emailext to: "${env.EMAIL_TO}",
               subject: "⚠️ Pipeline fallido: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
               body: "Detalle: ${env.BUILD_URL}"
    }
    // Si tienes instalado el plugin "Workspace Cleanup":
    // always { cleanWs() }
  }
}
