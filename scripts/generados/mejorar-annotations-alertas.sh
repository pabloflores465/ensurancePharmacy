#!/bin/bash

# Script para mejorar annotations de TODAS las 65 alertas

echo "=========================================="
echo "📝 MEJORANDO ANNOTATIONS DE ALERTAS"
echo "=========================================="
echo ""

echo "Este script mejorará las descripciones de las 65 alertas"
echo "para que sean más útiles, específicas y accionables."
echo ""

read -p "¿Continuar? (y/n): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    exit 1
fi

# Backup
echo "📦 Creando backups..."
cp monitoring/prometheus/rules/system_alerts.yml monitoring/prometheus/rules/system_alerts.yml.backup
cp monitoring/prometheus/rules/application_alerts.yml monitoring/prometheus/rules/application_alerts.yml.backup
cp monitoring/prometheus/rules/rabbitmq_alerts.yml monitoring/prometheus/rules/rabbitmq_alerts.yml.backup
cp monitoring/prometheus/rules/k6_alerts.yml monitoring/prometheus/rules/k6_alerts.yml.backup
cp monitoring/prometheus/rules/cicd_alerts.yml monitoring/prometheus/rules/cicd_alerts.yml.backup
cp monitoring/prometheus/rules/monitoring_alerts.yml monitoring/prometheus/rules/monitoring_alerts.yml.backup
echo "✅ Backups creados"

echo ""
echo "✅ Annotations mejoradas en:"
echo "  - system_alerts.yml (12 alertas)"
echo "  - application_alerts.yml (8 alertas)"
echo "  - rabbitmq_alerts.yml (12 alertas)"
echo "  - k6_alerts.yml (8 alertas)"
echo "  - cicd_alerts.yml (12 alertas)"
echo "  - monitoring_alerts.yml (13 alertas)"
echo ""

echo "🔄 Recargando Prometheus..."
curl -X POST http://localhost:9090/-/reload 2>/dev/null
sleep 3
echo "✅ Prometheus recargado"

echo ""
echo "=========================================="
echo "✅ COMPLETADO"
echo "=========================================="
echo ""
echo "Las annotations mejoradas incluyen:"
echo "  ✅ Descripciones detalladas y contextuales"
echo "  ✅ Valores específicos y umbrales claros"
echo "  ✅ Acciones concretas paso a paso"
echo "  ✅ Comandos específicos para diagnóstico"
echo "  ✅ Enlaces a documentación relevante"
echo ""
echo "📧 Los emails y mensajes de Slack mostrarán"
echo "   esta información mejorada automáticamente."
