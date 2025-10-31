#!/bin/bash

# Script para enviar alertas de prueba de TODAS las categorías rápidamente
# Envía 1-2 alertas representativas de cada categoría

ALERTMANAGER_URL="http://localhost:9094"

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║  🧪 TEST RÁPIDO DE TODAS LAS CATEGORÍAS DE ALERTAS            ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

# ==========================================
# CATEGORÍA 1: SISTEMA (2 alertas)
# ==========================================
echo "📊 CATEGORÍA 1: SISTEMA"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

echo "📧 1.1) HighRAMUsage (WARNING)..."
curl -s -X POST $ALERTMANAGER_URL/api/v2/alerts -H 'Content-Type: application/json' -d '[{
  "labels": {
    "alertname": "HighRAMUsage",
    "severity": "warning",
    "service": "node-exporter",
    "component": "ram",
    "instance": "localhost:9100"
  },
  "annotations": {
    "summary": "⚠️ RAM alta - 75% usado - Monitorear de cerca",
    "description": "El servidor localhost:9100 tiene 75% de RAM utilizada (umbral advertencia: 60%). La RAM está comenzando a llenarse. Si continúa creciendo, podría causar: swap activado (lentitud), aplicaciones sin memoria para crecer, OOM killer matando procesos. Aún hay tiempo para actuar antes de que sea crítico.",
    "dashboard": "http://localhost:19999/#menu_system_submenu_ram",
    "action": "🔍 Monitorear: 1) Ver uso actual: free -h. 2) Ver procesos top RAM: ps aux --sort=-%mem | head -20. 3) Identificar memory leaks. 4) Reiniciar servicios si es necesario. 5) Considerar aumentar RAM si uso es consistente. 6) Configurar alertas si crece más."
  },
  "startsAt": "'$(date -u +%Y-%m-%dT%H:%M:%S.000Z)'",
  "endsAt": "'$(date -u -d '+1 hour' +%Y-%m-%dT%H:%M:%S.000Z)'"
}]'
echo " ✅"
sleep 1

echo "📧 1.2) CriticalCPUUsage (CRITICAL)..."
curl -s -X POST $ALERTMANAGER_URL/api/v2/alerts -H 'Content-Type: application/json' -d '[{
  "labels": {
    "alertname": "CriticalCPUUsage",
    "severity": "critical",
    "service": "node-exporter",
    "component": "cpu",
    "instance": "localhost:9100"
  },
  "annotations": {
    "summary": "🔴 CPU CRITICO - Sistema saturado - Accion inmediata",
    "description": "ALERTA CRITICA: El servidor localhost:9100 esta usando 94% de CPU (umbral critico: 90%). El sistema esta saturado y las aplicaciones estan experimentando degradacion severa de rendimiento. Los usuarios pueden estar experimentando lentitud o timeouts.",
    "dashboard": "http://localhost:19999/#menu_system_submenu_cpu",
    "action": "🚨 URGENTE: 1) Ver procesos: ps aux --sort=-%cpu | head. 2) Matar procesos: kill -9 PID. 3) Reiniciar servicios Docker. 4) Escalar recursos. 5) Revisar bucles infinitos."
  },
  "startsAt": "'$(date -u +%Y-%m-%dT%H:%M:%S.000Z)'",
  "endsAt": "'$(date -u -d '+1 hour' +%Y-%m-%dT%H:%M:%S.000Z)'"
}]'
echo " ✅"
echo ""
sleep 2

# ==========================================
# CATEGORÍA 2: APLICACIONES (2 alertas)
# ==========================================
echo "💻 CATEGORÍA 2: APLICACIONES"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

echo "📧 2.1) PharmacyBackendDown (CRITICAL)..."
curl -s -X POST $ALERTMANAGER_URL/api/v2/alerts -H 'Content-Type: application/json' -d '[{
  "labels": {
    "alertname": "PharmacyBackendDown",
    "severity": "critical",
    "service": "pharmacy-backend",
    "component": "api",
    "instance": "localhost:3100"
  },
  "annotations": {
    "summary": "🔴 Backend Pharmacy CAIDO - Sistema de farmacia FUERA DE LINEA",
    "description": "EMERGENCIA: El backend de Pharmacy no responde. TODO el sistema de farmacia esta CAIDO. Usuarios NO pueden: consultar medicamentos, procesar recetas, verificar inventario, realizar ventas, registrar pacientes. Negocio completamente detenido en farmacia.",
    "dashboard": "http://localhost:3100/health",
    "action": "🚨 MAXIMA URGENCIA: 1) Ver contenedor: docker ps | grep pharmacy. 2) Ver logs: docker logs ensurance-pharmacy-apps --tail 100. 3) Reiniciar: docker restart ensurance-pharmacy-apps. 4) Verificar DB conectividad. 5) Verificar puertos. 6) Notificar a usuarios del problema."
  },
  "startsAt": "'$(date -u +%Y-%m-%dT%H:%M:%S.000Z)'",
  "endsAt": "'$(date -u -d '+1 hour' +%Y-%m-%dT%H:%M:%S.000Z)'"
}]'
echo " ✅"
sleep 1

echo "📧 2.2) HighNodeMemoryBackendV5 (WARNING)..."
curl -s -X POST $ALERTMANAGER_URL/api/v2/alerts -H 'Content-Type: application/json' -d '[{
  "labels": {
    "alertname": "HighNodeMemoryBackendV5",
    "severity": "warning",
    "service": "ensurance-backend-v5",
    "component": "nodejs",
    "instance": "localhost:3001"
  },
  "annotations": {
    "summary": "⚠️ Backend V5 usando mucha memoria Node.js - 450MB - Riesgo de crash",
    "description": "El proceso Node.js del backend V5 esta consumiendo 450MB de heap memory (umbral: 400MB). Node.js tiene limite de memoria. Si continua creciendo: OutOfMemory crash, aplicacion se reinicia sola, requests perdidos, usuarios desconectados. Probable memory leak.",
    "dashboard": "http://localhost:3001/metrics",
    "action": "🔍 Investigar leak: 1) Ver heap: node --inspect. 2) Heap snapshot: tomar con Chrome DevTools. 3) Identificar objetos creciendo. 4) Buscar closures, event listeners no limpiados, caches sin limite. 5) Fix y redeploy. 6) Reiniciar temporalmente."
  },
  "startsAt": "'$(date -u +%Y-%m-%dT%H:%M:%S.000Z)'",
  "endsAt": "'$(date -u -d '+1 hour' +%Y-%m-%dT%H:%M:%S.000Z)'"
}]'
echo " ✅"
echo ""
sleep 2

# ==========================================
# CATEGORÍA 3: NETDATA (2 alertas)
# ==========================================
echo "🌐 CATEGORÍA 3: NETDATA"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

echo "📧 3.1) TooManyProcesses (WARNING)..."
curl -s -X POST $ALERTMANAGER_URL/api/v2/alerts -H 'Content-Type: application/json' -d '[{
  "labels": {
    "alertname": "TooManyProcesses",
    "severity": "warning",
    "service": "netdata",
    "component": "processes",
    "instance": "localhost:19999"
  },
  "annotations": {
    "summary": "⚠️ Demasiados procesos corriendo - 850 procesos - Posible fork bomb",
    "description": "Hay 850 procesos corriendo en el sistema (umbral: 500). Muchos procesos indican: fork bomb (proceso creando infinitos hijos), script mal hecho en bucle, ataque, o muchos servicios. Demasiados procesos agotan: PIDs disponibles, RAM, CPU scheduler overhead.",
    "dashboard": "http://localhost:19999/#menu_system_submenu_processes",
    "action": "🔍 Identificar origen: 1) Ver procesos: ps aux | wc -l. 2) Agrupar por nombre: ps -eo comm | sort | uniq -c | sort -rn. 3) Identificar proceso creando muchos hijos. 4) Matar proceso padre: kill -9 PID. 5) Prevenir fork bombs: limites en /etc/security/limits.conf."
  },
  "startsAt": "'$(date -u +%Y-%m-%dT%H:%M:%S.000Z)'",
  "endsAt": "'$(date -u -d '+1 hour' +%Y-%m-%dT%H:%M:%S.000Z)'"
}]'
echo " ✅"
sleep 1

echo "📧 3.2) DiskReadErrors (CRITICAL)..."
curl -s -X POST $ALERTMANAGER_URL/api/v2/alerts -H 'Content-Type: application/json' -d '[{
  "labels": {
    "alertname": "DiskReadErrors",
    "severity": "critical",
    "service": "netdata",
    "component": "disk",
    "instance": "localhost:19999"
  },
  "annotations": {
    "summary": "🔴 ERRORES DE LECTURA EN DISCO - Hardware fallando - URGENTE BACKUP",
    "description": "CRITICO: El disco esta reportando errores de lectura. Esto indica FALLO DE HARDWARE inminente. El disco esta muriendo fisicamente. Puede haber: sectores dañados, cabezal desalineado, platos rayados. Si no se actua: perdida total de datos, disco muerto, sistema inoperable.",
    "dashboard": "http://localhost:19999/#menu_disk",
    "action": "🚨 EMERGENCIA DATOS: 1) HACER BACKUP INMEDIATO de todo. 2) Ver errores: dmesg | grep -i error. 3) SMART status: smartctl -a /dev/sda. 4) REEMPLAZAR DISCO urgente. 5) NO esperar - el disco puede morir en cualquier momento. 6) Planear migracion a nuevo disco YA."
  },
  "startsAt": "'$(date -u +%Y-%m-%dT%H:%M:%S.000Z)'",
  "endsAt": "'$(date -u -d '+1 hour' +%Y-%m-%dT%H:%M:%S.000Z)'"
}]'
echo " ✅"
echo ""
sleep 2

# ==========================================
# CATEGORÍA 4: K6 TESTING (2 alertas)
# ==========================================
echo "🧪 CATEGORÍA 4: K6 TESTING"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

echo "📧 4.1) K6HighErrorRate (CRITICAL)..."
curl -s -X POST $ALERTMANAGER_URL/api/v2/alerts -H 'Content-Type: application/json' -d '[{
  "labels": {
    "alertname": "K6HighErrorRate",
    "severity": "critical",
    "service": "k6-stress-testing",
    "component": "load-test"
  },
  "annotations": {
    "summary": "🔴 K6 Test: Alta tasa de errores - Aplicacion fallando bajo carga",
    "description": "El test de carga K6 esta detectando 8% de errores (umbral critico: 5%). Mas del 5% de requests estan fallando. Esto indica que la aplicacion NO soporta la carga actual - puede estar: crasheando, con timeouts, retornando 500s, o DB saturada. El sistema FALLARIA en produccion con esta carga.",
    "dashboard": "http://localhost:3302/d/k6-stress-test",
    "action": "🚨 NO DESPLEGAR: 1) Ver logs de aplicacion durante test. 2) Reducir carga de K6 para encontrar limite. 3) Identificar endpoint que falla. 4) Optimizar codigo/queries. 5) Escalar recursos antes de produccion. 6) Repetir test hasta lograr <1% errores."
  },
  "startsAt": "'$(date -u +%Y-%m-%dT%H:%M:%S.000Z)'",
  "endsAt": "'$(date -u -d '+1 hour' +%Y-%m-%dT%H:%M:%S.000Z)'"
}]'
echo " ✅"
sleep 1

echo "📧 4.2) K6HighVirtualUsers (INFO)..."
curl -s -X POST $ALERTMANAGER_URL/api/v2/alerts -H 'Content-Type: application/json' -d '[{
  "labels": {
    "alertname": "K6HighVirtualUsers",
    "severity": "info",
    "service": "k6-stress-testing",
    "component": "load"
  },
  "annotations": {
    "summary": "ℹ️ K6 Test: 150 usuarios virtuales activos - Test en progreso",
    "description": "INFO: K6 esta simulando 150 usuarios virtuales concurrentes (threshold informativo: >100). Esta es una notificacion de que hay un test de carga con muchos VUs ejecutandose. Es NORMAL durante stress testing. Cada VU simula un usuario real haciendo requests. Mas VUs = mas carga.",
    "dashboard": "http://localhost:3302/d/k6-stress-test",
    "action": "✅ Normal durante testing. 1) Verificar test programado. 2) Monitorear CPU/RAM de aplicacion bajo esta carga. 3) Documentar comportamiento. 4) Verificar que sea un test legitimo. 5) Usar resultados para capacity planning."
  },
  "startsAt": "'$(date -u +%Y-%m-%dT%H:%M:%S.000Z)'",
  "endsAt": "'$(date -u -d '+1 hour' +%Y-%m-%dT%H:%M:%S.000Z)'"
}]'
echo " ✅"
echo ""
sleep 2

# ==========================================
# CATEGORÍA 5: CI/CD (2 alertas)
# ==========================================
echo "🔧 CATEGORÍA 5: CI/CD"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

echo "📧 5.1) JenkinsBuildFailed (WARNING)..."
curl -s -X POST $ALERTMANAGER_URL/api/v2/alerts -H 'Content-Type: application/json' -d '[{
  "labels": {
    "alertname": "JenkinsBuildFailed",
    "severity": "warning",
    "service": "jenkins-ci",
    "component": "build",
    "job_name": "pharmacy-backend-deploy",
    "build_number": "456"
  },
  "annotations": {
    "summary": "⚠️ Build pharmacy-backend-deploy #456 FALLIDO",
    "description": "El build pharmacy-backend-deploy #456 ha fallado. Posibles causas: tests unitarios fallando, error de compilacion, lint errors, dependency issues, timeout. El codigo NO puede desplegarse. Developers deben corregir antes de merge/deploy.",
    "dashboard": "http://localhost:8080/jenkins/job/pharmacy-backend-deploy/456",
    "action": "🔍 Depurar build: 1) Ver logs en Jenkins. 2) Identificar step que fallo. 3) Tests? Fix codigo. 4) Compilacion? Revisar dependencias. 5) Lint? Corregir estilo. 6) Re-ejecutar build despues de fix. 7) Notificar a developer responsable."
  },
  "startsAt": "'$(date -u +%Y-%m-%dT%H:%M:%S.000Z)'",
  "endsAt": "'$(date -u -d '+1 hour' +%Y-%m-%dT%H:%M:%S.000Z)'"
}]'
echo " ✅"
sleep 1

echo "📧 5.2) JenkinsMultipleBuildFailures (CRITICAL)..."
curl -s -X POST $ALERTMANAGER_URL/api/v2/alerts -H 'Content-Type: application/json' -d '[{
  "labels": {
    "alertname": "JenkinsMultipleBuildFailures",
    "severity": "critical",
    "service": "jenkins-ci",
    "component": "build"
  },
  "annotations": {
    "summary": "🔴 Multiples builds fallando - 5 en 15 min - Problema sistemico",
    "description": "CRITICO: 5 builds han fallado en los ultimos 15 minutos. Multiples fallos indican problema sistemico, NO errores aislados. Posibles causas: bug introducido en main/master, servicio externo caido (DB, API), Jenkins mal configurado, infraestructura con problemas. TODO el equipo afectado.",
    "dashboard": "http://localhost:8080/jenkins",
    "action": "🚨 URGENTE: 1) Identificar commit que rompio builds. 2) Revert si es necesario. 3) Verificar servicios externos (DB, APIs). 4) Revisar cambios en Jenkins/pipeline. 5) Comunicar al equipo. 6) Bloquear merges hasta resolver. 7) Hotfix prioritario."
  },
  "startsAt": "'$(date -u +%Y-%m-%dT%H:%M:%S.000Z)'",
  "endsAt": "'$(date -u -d '+1 hour' +%Y-%m-%dT%H:%M:%S.000Z)'"
}]'
echo " ✅"
echo ""
sleep 2

# ==========================================
# CATEGORÍA 6: MONITOREO (2 alertas)
# ==========================================
echo "📊 CATEGORÍA 6: MONITOREO"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

echo "📧 6.1) AlertmanagerDown (CRITICAL)..."
curl -s -X POST $ALERTMANAGER_URL/api/v2/alerts -H 'Content-Type: application/json' -d '[{
  "labels": {
    "alertname": "AlertmanagerDown",
    "severity": "critical",
    "service": "alertmanager",
    "component": "notifications"
  },
  "annotations": {
    "summary": "🔴 Alertmanager CAIDO - CERO notificaciones enviandose",
    "description": "CRITICO: Alertmanager esta caido. Sin Alertmanager: NO se envian emails, NO se envian mensajes a Slack, alertas se disparan pero NADIE es notificado. Equipo NO sabra de problemas. Es como tener alarma de incendio sin campana. Prometheus sigue alertando pero notificaciones bloqueadas.",
    "dashboard": "http://localhost:9094",
    "action": "🚨 URGENTE: 1) docker ps | grep alertmanager. 2) docker logs ensurance-alertmanager-full --tail 100. 3) docker restart ensurance-alertmanager-full. 4) Verificar config: /monitoring/alertmanager/alertmanager.yml. 5) Probar: http://localhost:9094. 6) PRIORIDAD ALTA."
  },
  "startsAt": "'$(date -u +%Y-%m-%dT%H:%M:%S.000Z)'",
  "endsAt": "'$(date -u -d '+1 hour' +%Y-%m-%dT%H:%M:%S.000Z)'"
}]'
echo " ✅"
sleep 1

echo "📧 6.2) PrometheusHighMemory (WARNING)..."
curl -s -X POST $ALERTMANAGER_URL/api/v2/alerts -H 'Content-Type: application/json' -d '[{
  "labels": {
    "alertname": "PrometheusHighMemory",
    "severity": "warning",
    "service": "prometheus",
    "component": "memory"
  },
  "annotations": {
    "summary": "⚠️ Prometheus con memoria alta - 2.5GB - Riesgo de OOM",
    "description": "Prometheus esta usando 2.5GB de RAM (umbral: 2GB). Prometheus consume mucha memoria almacenando time series en memoria. Si continua creciendo: OOM killer puede matarlo, perdida total de monitoreo, datos en memoria perdidos. Causas: demasiadas metricas, alta cardinalidad, retencion muy larga.",
    "dashboard": "http://localhost:9090",
    "action": "🔍 Reducir consumo: 1) Ver uso: http://localhost:9090/tsdb-status. 2) Reducir retencion: --storage.tsdb.retention.time=7d. 3) Reducir metricas: eliminar jobs innecesarios. 4) Reducir cardinalidad: simplificar labels. 5) Aumentar RAM del contenedor. 6) Implementar remote storage."
  },
  "startsAt": "'$(date -u +%Y-%m-%dT%H:%M:%S.000Z)'",
  "endsAt": "'$(date -u -d '+1 hour' +%Y-%m-%dT%H:%M:%S.000Z)'"
}]'
echo " ✅"
echo ""
sleep 2

# ==========================================
# RESUMEN
# ==========================================
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║  ✅ 12 ALERTAS ENVIADAS - 2 POR CADA CATEGORÍA                ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""
echo "📊 RESUMEN:"
echo "   • Sistema: HighRAMUsage, CriticalCPUUsage"
echo "   • Aplicaciones: PharmacyBackendDown, HighNodeMemoryBackendV5"
echo "   • Netdata: TooManyProcesses, DiskReadErrors"
echo "   • K6: K6HighErrorRate, K6HighVirtualUsers"
echo "   • CI/CD: JenkinsBuildFailed, JenkinsMultipleBuildFailures"
echo "   • Monitoreo: AlertmanagerDown, PrometheusHighMemory"
echo ""
echo "📧 VERIFICA TUS CORREOS EN:"
echo "   → pablopolis2016@gmail.com"
echo "   → jflores@unis.edu.gt"
echo ""
echo "💬 VERIFICA SLACK:"
echo "   → Canal: #ensurance-alerts"
echo ""
echo "🌐 VER EN WEB:"
echo "   → Alertmanager: http://localhost:9094/#/alerts"
echo "   → Prometheus: http://localhost:9090/alerts"
echo ""
echo "⏱️ Los correos deberían llegar en 10-60 segundos"
echo ""
