# 🚀 Inicio Rápido - Sistema de Alertas

## ✅ Estado del Sistema

### Servicios Activos
- ✅ Prometheus: 25 grupos de reglas cargados (64 alertas)
- ✅ AlertManager: Configurado con Email + Slack
- ✅ Netdata: 4 archivos de health (~45 alertas)
- ✅ Grafana: Dashboards k6 y system-metrics

### Total de Alertas
- **Prometheus/Grafana:** 64 alertas
- **Netdata:** ~45 alertas
- **TOTAL:** ~110 alertas únicas

---

## 🔧 Configuración Inicial (REQUERIDO)

### 1. Configurar Webhook de Slack

```bash
# Obtener webhook de Slack:
# 1. Ir a https://api.slack.com/apps
# 2. Crear app "Ensurance Alerts"
# 3. Activar "Incoming Webhooks"
# 4. Añadir webhook al canal #ensurance-alerts
# 5. Copiar URL del webhook

# Configurar en el sistema:
./configure-slack-webhook.sh "https://hooks.slack.com/services/T.../B.../XXX..."
```

### 2. Reiniciar Servicios

```bash
# Reiniciar AlertManager para aplicar configuración
docker compose -f docker-compose.full.yml restart alertmanager

# Reiniciar Netdata para cargar health alerts
docker compose -f docker-compose.full.yml restart netdata
```

---

## 🧪 Probar Notificaciones

### Test 1: Email (NodeExporter Down - WARNING)
```bash
# Detener servicio
docker stop ensurance-node-exporter-full

# Esperar 2 minutos (alerta se dispara)
sleep 120

# Verificar email en pablopolis2016@gmail.com y jflores@unis.edu.gt

# Restaurar servicio
docker start ensurance-node-exporter-full
```

### Test 2: Slack (RabbitMQ Down - CRITICAL)
```bash
# Detener servicio
docker stop ensurance-rabbitmq-full

# Esperar 90 segundos (alerta se dispara)
sleep 90

# Verificar mensaje en canal #ensurance-alerts con @channel

# Restaurar servicio
docker start ensurance-rabbitmq-full
```

### Verificar Notificaciones Enviadas
```bash
# Ver emails enviados (últimos 10 minutos)
docker logs ensurance-alertmanager-full --since 10m 2>&1 | grep -i "email.*notify"

# Ver mensajes Slack enviados (últimos 10 minutos)
docker logs ensurance-alertmanager-full --since 10m 2>&1 | grep -i "slack.*notify"
```

---

## 📊 Ver Alertas Activas

### Prometheus
```bash
# Browser
open http://localhost:9090/alerts

# CLI
curl -s http://localhost:9090/api/v1/alerts | jq '.data.alerts[] | select(.state=="firing")'
```

### AlertManager
```bash
# Browser
open http://localhost:9094

# CLI
curl -s http://localhost:9094/api/v2/alerts
```

### Netdata
```bash
# Browser
open http://localhost:19999

# CLI
curl -s http://localhost:19999/api/v1/alarms?active
```

---

## 🔍 Validar Sistema Completo

```bash
# Ejecutar script de validación
./test-all-monitoring-alerts.sh

# Ver reporte
cat alert-testing-*.log
```

---

## 📋 Alertas Principales Configuradas

### Grafana/Prometheus (64 alertas)

#### Sistema (11 alertas)
- **HighCPUUsage:** CPU > 70% → Email + Slack
- **HighMemoryUsage:** Memoria > 80% → Email + Slack
- **HighDiskUsage:** Disco > 75% → Email + Slack
- **HighNetworkReceive/Transmit:** > 100MB/s → Email + Slack
- **HighSystemLoad:** Load > 2x CPUs → Email + Slack

#### K6 Stress Testing (8 alertas)
- **K6HighErrorRate:** Errores > 5% → Email + Slack (@channel)
- **K6HighResponseTimeP95:** P95 > 1s → Email + Slack
- **K6FailedChecks:** Checks fallando → Email + Slack

#### CI/CD (12 alertas)
- **JenkinsDown:** Jenkins caído → Email + Slack (@channel)
- **JenkinsBuildFailed:** Build fallido → Email + Slack
- **DroneServerDown:** Drone caído → Email + Slack

### Netdata (~45 alertas)

#### Sistema (15 alertas)
- **netdata_high_cpu_usage:** CPU > 70% → Alertmanager → Email + Slack
- **netdata_high_memory_usage:** Memoria > 80% → Alertmanager → Email + Slack
- **netdata_high_disk_usage:** Disco > 75% → Alertmanager → Email + Slack
- **netdata_high_network_receive/transmit:** > 100MB/s → Alertmanager → Email + Slack

#### Aplicaciones (12 alertas)
- **netdata_container_high_cpu:** Container CPU > 80% → Email + Slack
- **netdata_rabbitmq_high_memory:** RabbitMQ > 1GB → Email + Slack
- **netdata_high_http_5xx_errors:** Errores 5xx > 5% → Email + Slack

---

## 📧 Formato de Notificaciones

### Email - CRITICAL
```
🔴 [CRÍTICO] Alerta Urgente - Ensurance Pharmacy

⚠️ ALERTA CRÍTICA ⚠️
Alerta: HighCPUUsage
Severidad: CRÍTICO
Descripción: El uso de CPU está en 92%
Resumen: CPU sobrepasa el 70%
Servicio: system
Instancia: node-exporter:9100

⚡ ACCIÓN REQUERIDA INMEDIATAMENTE
```

### Slack - CRITICAL
```
🔴 ALERTA CRÍTICA - Ensurance Pharmacy
<!channel>

⚠️ ALERTA CRÍTICA - ACCIÓN INMEDIATA REQUERIDA ⚠️

Alerta: HighCPUUsage
Severidad: CRÍTICO
Descripción: El uso de CPU está en 92%
Dashboard: http://localhost:19999
Acción: Revisar procesos con alto consumo de CPU
```

---

## 🔧 Troubleshooting

### Alertas no se disparan
```bash
# Verificar reglas cargadas en Prometheus
curl http://localhost:9090/api/v1/rules | jq '.data.groups | length'

# Verificar targets UP
curl http://localhost:9090/api/v1/targets | jq '.data.activeTargets[] | select(.health!="up")'

# Verificar health de Netdata
docker exec ensurance-netdata-full ls -la /etc/netdata/health.d/
```

### Email no llega
```bash
# Ver logs de AlertManager
docker logs ensurance-alertmanager-full --tail 100 | grep -i "email"

# Verificar configuración SMTP
grep "smtp_" monitoring/alertmanager/alertmanager.yml.template
```

### Slack no funciona
```bash
# Ver logs de AlertManager
docker logs ensurance-alertmanager-full --tail 100 | grep -i "slack"

# Probar webhook manualmente
curl -X POST "TU_WEBHOOK_URL" \
  -H 'Content-Type: application/json' \
  -d '{"text":"Test desde Ensurance Pharmacy"}'
```

---

## 📁 Archivos Creados

```
monitoring/
├── alertmanager/
│   └── alertmanager.yml.template  ← Email + Slack configurado (4 receivers)
├── prometheus/rules/
│   ├── system_alerts.yml          ← 11 alertas
│   ├── application_alerts.yml     ← 9 alertas
│   ├── rabbitmq_alerts.yml        ← 12 alertas
│   ├── k6_alerts.yml              ← 8 alertas
│   ├── cicd_alerts.yml            ← 12 alertas
│   └── monitoring_alerts.yml      ← 12 alertas
└── netdata/health.d/
    ├── system_alerts.conf         ← 15 alertas
    ├── application_alerts.conf    ← 12 alertas
    ├── k6_alerts.conf             ← 7 alertas
    └── pipeline_alerts.conf       ← 10 alertas

Scripts:
├── test-all-monitoring-alerts.sh  ← Validación completa
├── configure-slack-webhook.sh     ← Configurar Slack
└── setup-alertmanager-secrets.sh  ← Generar config

Documentación:
├── ALERTAS-SISTEMA-COMPLETO.md    ← Documentación completa
└── INICIO-RAPIDO-ALERTAS.md       ← Esta guía
```

---

## ✅ Checklist Final

- [x] AlertManager template con Email y Slack
- [x] 64 alertas de Prometheus configuradas
- [x] 45 alertas de Netdata configuradas
- [x] Script de validación creado
- [ ] **Webhook de Slack configurado (PENDIENTE)**
- [ ] **Pruebas de Email ejecutadas (PENDIENTE)**
- [ ] **Pruebas de Slack ejecutadas (PENDIENTE)**

---

**Siguiente Paso:** Configurar webhook de Slack y ejecutar pruebas

```bash
./configure-slack-webhook.sh "TU_WEBHOOK_URL"
./test-all-monitoring-alerts.sh
```
