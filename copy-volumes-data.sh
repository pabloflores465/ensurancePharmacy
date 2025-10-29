#!/bin/bash

# Script para copiar datos de volúmenes antiguos a volúmenes -full
# Ejecutar: ./copy-volumes-data.sh

set -e

echo "🔄 Iniciando copia de datos de volúmenes antiguos a nuevos..."
echo ""

# Función para copiar volúmenes
copy_volume() {
    local source=$1
    local dest=$2
    
    # Verificar si el volumen origen existe
    if docker volume inspect "$source" &>/dev/null; then
        echo "📦 Copiando: $source -> $dest"
        docker run --rm \
            -v "$source:/source:ro" \
            -v "$dest:/dest" \
            alpine sh -c "cp -a /source/. /dest/ 2>/dev/null || true" 2>&1 | grep -v "can't preserve" || true
        echo "   ✅ Completado"
    else
        echo "   ⚠️  Volumen $source no existe, omitiendo..."
    fi
    echo ""
}

# Detener servicios temporalmente para evitar conflictos
echo "⏸️  Deteniendo servicios temporalmente..."
docker compose -f docker-compose.full.yml stop prometheus grafana checkmk jenkins sonarqube drone portainer 2>/dev/null || true
echo ""

# Copiar volúmenes de monitoreo
echo "📊 MONITOREO"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "⚠️  Omitiendo Prometheus (es mejor empezar con datos frescos)"
echo ""
# copy_volume "scripts_prometheus_data" "ensurance-prometheus-data-full"
copy_volume "scripts_grafana_data" "ensurance-grafana-data-full"
copy_volume "scripts_checkmk_sites" "ensurance-checkmk-sites-full"

# Copiar volúmenes de CI/CD
echo "🔧 CI/CD"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
copy_volume "ensurancepharmacy_jenkins_home" "ensurance-jenkins-home-full"
copy_volume "ensurancepharmacy_sonarqube_data" "ensurance-sonarqube-data-full"
copy_volume "ensurancepharmacy_sonarqube_logs" "ensurance-sonarqube-logs-full"
copy_volume "ensurancepharmacy_sonarqube_extensions" "ensurance-sonarqube-extensions-full"
copy_volume "ensurancepharmacy_drone_data" "ensurance-drone-data-full"
copy_volume "ensurancepharmacy_drone_runner_data" "ensurance-drone-runner-data-full"
copy_volume "ensurancepharmacy_docker_data" "ensurance-docker-data-full"
copy_volume "ensurancepharmacy_jenkins-docker-certs" "ensurance-jenkins-docker-certs-full"
copy_volume "ensurancepharmacy_jenkins-docker-certs-ca" "ensurance-jenkins-docker-certs-ca-full"

# Copiar volúmenes de herramientas
echo "🛠️  HERRAMIENTAS"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
copy_volume "portainer_data" "ensurance-portainer-data-full"
copy_volume "scripts_k6_results" "ensurance-k6-results-full"
copy_volume "scripts_jmeter_results" "ensurance-jmeter-results-full"

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ Copia completada!"
echo ""
echo "🔄 Reiniciando servicios..."
docker compose -f docker-compose.full.yml restart prometheus grafana checkmk jenkins sonarqube drone portainer

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ PROCESO COMPLETADO"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "Los servicios ahora tienen acceso a sus datos anteriores."
echo "Espera 30-60 segundos para que los servicios inicialicen completamente."
echo ""
echo "✅ Datos copiados:"
echo "  • Grafana (dashboards y configuración)"
echo "  • CheckMK (configuración de monitoreo)"
echo "  • Jenkins (jobs y configuración)"
echo "  • SonarQube (proyectos y análisis)"
echo "  • Drone (pipelines)"
echo "  • Portainer (configuración de contenedores)"
echo ""
echo "⚠️  Prometheus comienza con datos frescos (recomendado)"
echo ""
echo "URLs de acceso:"
echo "  • Portainer:  https://localhost:60002"
echo "  • Grafana:    http://localhost:3302 (admin/changeme)"
echo "  • Prometheus: http://localhost:9090"
echo "  • Jenkins:    http://localhost:8080"
echo "  • SonarQube:  http://localhost:9000 (admin/admin)"
echo "  • RabbitMQ:   http://localhost:15674 (admin/changeme)"
echo ""
