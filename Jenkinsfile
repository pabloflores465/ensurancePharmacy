pipeline {
  agent any
  tools {
    nodejs 'NodeJS'         // <-- Global Tool: NodeJS
    maven 'Maven_3_9_11'
  }

  environment {
    // Nombre del servidor SonarQube que configuraste en Jenkins → System
    SONARQUBE_SERVER = 'sonarqube'
    // Direcciones para notificaciones (ajústalas)
    EMAIL_TO = 'pablopolis2016@gmail.com'
  }

  options { timestamps() }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Unit Tests & Coverage (auto-detect)') {
      steps {
        sh '''
          set -e
          # Frontend (Node) - si existe package.json en ./ensurance o ./front ajusta al path que uses
          if [ -f "./ensurance/package.json" ]; then
            echo "[frontend] npm ci && npm test"
            cd ./ensurance
            npm ci || true
            npm test -- --ci || true
            cd -
          fi

          # Backend (Node/Java/Python) – ejecuta cada stack si existe
          if [ -f "./pharmacy/package.json" ]; then
            echo "[backend] npm ci && npm test"
            cd ./pharmacy
            npm ci || true
            npm test -- --ci || true
            cd -
          fi

          if [ -d "backv4" ]; then
            echo "[backv4] mvn verify con ignore de fallos para generar jacoco.xml"
            mvn -B -f backv4/pom.xml -DtestFailureIgnore=true verify || true
          fi
          if [ -d "backv5" ]; then
            echo "[backv5] mvn verify con ignore de fallos para generar jacoco.xml"
            mvn -B -f backv5/pom.xml -DtestFailureIgnore=true verify || true
          fi

          if [ -f "pom.xml" ]; then
            echo "[backend-root] mvn -B -DskipTests=false test"
            mvn -B -DskipTests=false test || true
          fi

          if [ -f "build.gradle" ] || [ -f "gradlew" ]; then
            echo "[backend] gradle test"
            ./gradlew test || true
          fi

          if [ -f "requirements.txt" ] && command -v pytest >/dev/null 2>&1; then
            echo "[backend] pytest"
            pytest || true
          fi
        '''
      }
      post {
        unsuccessful {
          emailext to: "${env.EMAIL_TO}",
                   subject: "❌ Tests fallaron: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                   body: "Revisa la consola: ${env.BUILD_URL}"
        }
      }
    }

    stage('SonarQube Analysis') {
      steps {
        script {
          // Toma la ruta real del SonarQube Scanner instalado en "Global Tool Configuration"
          def scannerHome = tool 'Scanner'   // <-- el nombre debe coincidir con el que pusiste
          withSonarQubeEnv("${SONARQUBE_SERVER}") {
            sh """
              "${scannerHome}/bin/sonar-scanner" \
              -Dsonar.projectVersion=${BUILD_NUMBER} \
              -Dsonar.java.binaries=backv4/target/classes,backv5/target/classes \
              -Dsonar.coverage.jacoco.xmlReportPaths=backv4/target/site/jacoco/jacoco.xml,backv5/target/site/jacoco/jacoco.xml
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
                   body: "Se bloquea el merge por incumplir el gate (no nueva deuda técnica). ${env.BUILD_URL}"
        }
      }
    }

    stage('Deploy DEV (branch dev)') {
      when { branch 'dev' }
      steps {
        sh '''
          echo "Deploy DEV → backend1/frontend1"
          docker compose -f docker-compose.ensurance.yaml up -d --build ensurance-db ensurance-backend1 ensurance-frontend1
        '''
      }
    }

    stage('Deploy UAT (branch test)') {
      when { branch 'test' }
      steps {
        sh '''
          echo "Deploy UAT → backend2/frontend2"
          docker compose -f docker-compose.ensurance.yaml up -d --build ensurance-db ensurance-backend2 ensurance-frontend2
        '''
      }
    }

    stage('Deploy PROD (branch main)') {
      when { branch 'main' }
      steps {
        sh '''
          echo "Deploy PROD → backend3/frontend3"
          docker compose -f docker-compose.ensurance.yaml up -d --build ensurance-db ensurance-backend3 ensurance-frontend3
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
  }
}
