# 🔔 Sistema Completo de Alertas - Ensurance Pharmacy

**Total de Alertas:** 65  
**Fecha:** 31 de Octubre, 2025

---

## 📊 Resumen por Categoría

| Categoría | Cantidad | Archivo | Critical | Warning | Info |
|-----------|----------|---------|----------|---------|------|
| Sistema | 12 | `system_alerts.yml` | 4 | 8 | 0 |
| Aplicaciones | 8 | `application_alerts.yml` | 4 | 4 | 0 |
| RabbitMQ | 12 | `rabbitmq_alerts.yml` | 5 | 7 | 0 |
| K6 Testing | 8 | `k6_alerts.yml` | 2 | 4 | 2 |
| CI/CD | 12 | `cicd_alerts.yml` | 2 | 10 | 0 |
| Monitoreo | 13 | `monitoring_alerts.yml` | 2 | 10 | 1 |
| **TOTAL** | **65** | 6 archivos | **19** | **43** | **3** |

---

## 🎯 Configuración de Notificaciones

### Canales Activos
- ✉️ **Email:** pablopolis2016@gmail.com, jflores@unis.edu.gt
- 💬 **Slack:** Canal `#ensurance-alerts` (requiere configuración)

### Intervalos de Repetición
- 🔴 **CRITICAL:** Cada 5 minutos
- ⚠️ **WARNING:** Cada 1 hora  
- ℹ️ **INFO:** Cada 6 horas

---

## 📋 Lista Completa de Alertas

### 1️⃣ ALERTAS DE SISTEMA (12)

| # | Alerta | Umbral | For | Severidad | Notificación |
|---|--------|---------|-----|-----------|--------------|
| 0 | **HighRAMUsage** | **RAM > 60%** | **1m** | **warning** | **📧 Gmail + 💬 Slack** |
| 1 | HighCPUUsage | CPU > 70% | 2m | warning | 📧 Gmail + 💬 Slack |
| 2 | CriticalCPUUsage | CPU > 90% | 1m | critical | 📧 Gmail + 💬 Slack |
| 3 | HighMemoryUsage | RAM > 80% | 2m | warning | 📧 Gmail + 💬 Slack |
| 4 | CriticalMemoryUsage | RAM > 95% | 1m | critical | 📧 Gmail + 💬 Slack |
| 5 | HighDiskUsage | Disco > 75% | 5m | warning | 📧 Gmail + 💬 Slack |
| 6 | CriticalDiskUsage | Disco > 90% | 2m | critical | 📧 Gmail + 💬 Slack |
| 7 | DiskAlmostFull | < 5GB libre | 5m | warning | 📧 Gmail + 💬 Slack |
| 8 | HighNetworkReceive | > 100MB/s | 5m | warning | 📧 Gmail + 💬 Slack |
| 9 | HighNetworkTransmit | > 100MB/s | 5m | warning | 📧 Gmail + 💬 Slack |
| 10 | NodeExporterDown | up == 0 | 1m | critical | 📧 Gmail + 💬 Slack |
| 11 | HighSystemLoad | load > 2x CPU | 5m | warning | 📧 Gmail + 💬 Slack |

### 2️⃣ ALERTAS DE APLICACIONES (8)

| # | Alerta | Umbral | For | Severidad | Puerto | Notificación |
|---|--------|---------|-----|-----------|--------|--------------|
| 12 | PharmacyBackendDown | up == 0 | 1m | critical | 9464 | 📧 Gmail + 💬 Slack |
| 13 | EnsuranceBackendDown | up == 0 | 1m | critical | 9465 | 📧 Gmail + 💬 Slack |
| 14 | EnsuranceFrontendDown | up == 0 | 2m | critical | 9466 | 📧 Gmail + 💬 Slack |
| 15 | PharmacyFrontendDown | up == 0 | 2m | critical | 9467 | 📧 Gmail + 💬 Slack |
| 16 | HighNodeMemoryBackendV5 | heap > 85% | 5m | warning | - | 📧 Gmail + 💬 Slack |
| 17 | HighNodeMemoryBackendV4 | heap > 85% | 5m | warning | - | 📧 Gmail + 💬 Slack |
| 18 | HighEventLoopLag | lag > 0.5s | 3m | warning | - | 📧 Gmail + 💬 Slack |
| 19 | FrequentGarbageCollection | GC > 10/s | 5m | warning | - | 📧 Gmail + 💬 Slack |

### 3️⃣ ALERTAS DE RABBITMQ (12)

| # | Alerta | Umbral | For | Severidad | Notificación |
|---|--------|---------|-----|-----------|--------------|
| 21 | RabbitMQDown | up == 0 | 1m | critical | 📧 Gmail + 💬 Slack |
| 22 | RabbitMQNodeDown | node_up == 0 | 1m | critical | 📧 Gmail + 💬 Slack |
| 23 | RabbitMQQueueMessagesHigh | > 1000 msgs | 5m | warning | 📧 Gmail + 💬 Slack |
| 24 | RabbitMQQueueMessagesReady | > 500 ready | 5m | warning | 📧 Gmail + 💬 Slack |
| 25 | RabbitMQUnacknowledgedMessages | > 100 unacked | 5m | warning | 📧 Gmail + 💬 Slack |
| 26 | RabbitMQQueueNoConsumers | 0 consumers | 5m | warning | 📧 Gmail + 💬 Slack |
| 27 | RabbitMQHighMemory | > 80% | 5m | warning | 📧 Gmail + 💬 Slack |
| 28 | RabbitMQCriticalMemory | > 95% | 2m | critical | 📧 Gmail + 💬 Slack |
| 29 | RabbitMQMemoryAlarm | alarm activa | 1m | critical | 📧 Gmail + 💬 Slack |
| 30 | RabbitMQTooManyConnections | > 100 | 5m | warning | 📧 Gmail + 💬 Slack |
| 31 | RabbitMQTooManyChannels | > 500 | 5m | warning | 📧 Gmail + 💬 Slack |
| 32 | RabbitMQDiskAlarm | alarm activa | 1m | critical | 📧 Gmail + 💬 Slack |

### 4️⃣ ALERTAS DE K6 STRESS TESTING (8)

| # | Alerta | Umbral | For | Severidad | Notificación |
|---|--------|---------|-----|-----------|--------------|
| 33 | K6HighErrorRate | > 5% errores | 1m | critical | 📧 Gmail + 💬 Slack |
| 34 | K6HighResponseTimeP95 | p95 > 1s | 2m | warning | 📧 Gmail + 💬 Slack |
| 35 | K6CriticalResponseTimeP95 | p95 > 3s | 1m | critical | 📧 Gmail + 💬 Slack |
| 36 | K6HighResponseTimeP99 | p99 > 5s | 1m | warning | 📧 Gmail + 💬 Slack |
| 37 | K6FailedChecks | checks fail | 1m | warning | 📧 Gmail + 💬 Slack |
| 38 | K6HighRequestRate | > 1000 req/s | 2m | info | 📧 Gmail + 💬 Slack |
| 39 | K6HighVirtualUsers | > 100 VUs | 1m | info | 📧 Gmail + 💬 Slack |
| 40 | K6MetricsNotReceived | sin métricas 5m | 1m | warning | 📧 Gmail + 💬 Slack |

### 5️⃣ ALERTAS DE CI/CD Y PIPELINES (12)

**Jenkins:**
| # | Alerta | Umbral | For | Severidad |
|---|--------|---------|-----|-----------|
| 41 | JenkinsDown | up == 0 | 2m | critical |
| 42 | PushgatewayDown | up == 0 | 2m | warning |
| 43 | JenkinsBuildFailed | FAILURE | 1m | warning |
| 44 | JenkinsSlowBuild | > 30min | 1m | warning |
| 45 | JenkinsLongQueue | > 5 builds | 5m | warning |
| 46 | JenkinsMultipleBuildFailures | > 3 en 15m | 1m | critical |
| 47 | JenkinsAllExecutorsBusy | > 90% | 10m | warning |
| 48 | JenkinsExecutorOffline | offline | 5m | warning |

**SonarQube:**
| # | Alerta | Umbral | For | Severidad |
|---|--------|---------|-----|-----------|
| 49 | SonarQubeDown | up == 0 | 2m | warning |
| 50 | SonarQubeQualityGateFailed | ERROR | 1m | warning |

**Drone:**
| # | Alerta | Umbral | For | Severidad |
|---|--------|---------|-----|-----------|
| 51 | DroneServerDown | up == 0 | 2m | warning |
| 52 | DroneRunnerDown | up == 0 | 2m | warning |

### 6️⃣ ALERTAS DE MONITOREO (13)

| # | Alerta | Umbral | For | Severidad |
|---|--------|---------|-----|-----------|
| 53 | PrometheusDown | up == 0 | 1m | critical |
| 54 | TargetDown | up == 0 | 2m | warning |
| 55 | PrometheusHighMemory | > 2GB | 5m | warning |
| 56 | PrometheusDroppingSamples | dropping | 2m | warning |
| 57 | PrometheusTooManyTimeSeries | > 100k | 5m | warning |
| 58 | PrometheusSlowScrapes | p99 > 10s | 5m | warning |
| 59 | GrafanaDown | up == 0 | 2m | warning |
| 60 | NetdataDown | up == 0 | 2m | warning |
| 61 | AlertmanagerDown | up == 0 | 1m | critical |
| 62 | AlertmanagerFailedNotifications | > 0.1/s | 5m | warning |
| 63 | AlertmanagerClusterUnsynchronized | desync | 5m | warning |
| 64 | PortainerDown | up == 0 | 2m | info |

---

## 🔍 Cómo Funcionan las Alertas de Pipelines (CI/CD)

### Flujo de Datos
```
Jenkins Build → Push métricas → Pushgateway → Prometheus scrape → Evalúa reglas → Alertmanager → Email/Slack
```

### Métricas Monitoreadas
1. **jenkins_build_result{result="FAILURE"}** - Builds fallidos
2. **jenkins_build_duration_seconds** - Duración del build
3. **jenkins_queue_size** - Cola de builds
4. **jenkins_executor_busy/total** - Uso de executors
5. **jenkins_executor_offline** - Executors caídos

### Ejemplo: Alerta de Build Fallido

**1. Detección:**
```promql
jenkins_build_result{result="FAILURE"} > 0
```

**2. Espera confirmación:** 1 minuto

**3. Dispara alerta:** severity=warning

**4. Notificación:**
- ✉️ Email a ambos destinatarios
- 💬 Slack a #ensurance-alerts

**5. Repetición:** Cada 1 hora si persiste

**6. Información incluida:**
- Nombre del job
- Número de build
- Timestamp
- Link al dashboard
- Acción recomendada

---

## ⚙️ Configurar Notificaciones de Slack

### Pasos para Activar Slack

1. **Crear Webhook en Slack:**
   ```
   https://api.slack.com/messaging/webhooks
   ```

2. **Crear archivo de configuración:**
   ```bash
   cat > .env.alertmanager << 'EOF'
   SMTP_SMARTHOST=smtp-relay.brevo.com:587
   SMTP_FROM=945b13001@smtp-brevo.com
   SMTP_USERNAME=945b13001@smtp-brevo.com
   SMTP_PASSWORD=tu_token_smtp
   SMTP_REQUIRE_TLS=true
   ALERT_EMAIL_TO=pablopolis2016@gmail.com,jflores@unis.edu.gt
   SLACK_WEBHOOK_URL=https://hooks.slack.com/services/TU/WEBHOOK/REAL
   EOF
   ```

3. **Ejecutar script de configuración:**
   ```bash
   chmod +x setup-alertmanager-secrets.sh
   ./setup-alertmanager-secrets.sh
   ```

4. **Verificar:**
   ```bash
   docker compose -f docker-compose.full.yml logs alertmanager
   ```

---

## 🧪 Probar Todas las Alertas

Ver script: `test-todas-las-alertas-completo.sh`

```bash
# Ejecutar prueba completa
chmod +x test-todas-las-alertas-completo.sh
./test-todas-las-alertas-completo.sh

# Verificar alertas activas
curl -s http://localhost:9090/api/v1/alerts | jq '.data.alerts[] | {alert: .labels.alertname, state: .state}'

# Ver notificaciones en Alertmanager
curl -s http://localhost:9093/api/v1/alerts | jq '.data[] | {alertname: .labels.alertname, status: .status.state}'
```

---

## 📧 Verificar Notificaciones

### Email
1. Revisar bandeja de entrada: pablopolis2016@gmail.com
2. Revisar bandeja de entrada: jflores@unis.edu.gt
3. Verificar carpeta de spam si no aparecen

### Slack
1. Abrir canal #ensurance-alerts
2. Verificar mensajes del bot Alertmanager
3. Buscar alertas por severidad (🔴 critical, ⚠️ warning, ℹ️ info)

### Alertmanager UI
```bash
# Abrir en navegador
http://localhost:9093

# Ver alertas activas
http://localhost:9093/#/alerts

# Ver silences
http://localhost:9093/#/silences
```

---

## 🔧 Troubleshooting

### Alertas no se disparan
```bash
# Verificar targets
curl http://localhost:9090/api/v1/targets

# Verificar reglas cargadas
curl http://localhost:9090/api/v1/rules

# Ver logs de Prometheus
docker compose -f docker-compose.full.yml logs prometheus
```

### Notificaciones no llegan
```bash
# Verificar Alertmanager
curl http://localhost:9093/api/v1/status

# Ver logs de Alertmanager
docker compose -f docker-compose.full.yml logs alertmanager

# Test SMTP
telnet smtp-relay.brevo.com 587
```

### Reiniciar sistema completo
```bash
docker compose -f docker-compose.full.yml restart prometheus alertmanager
```

---

## 📚 Dashboards y URLs

| Servicio | URL | Descripción |
|----------|-----|-------------|
| Prometheus | http://localhost:9090 | Métricas y alertas |
| Alertmanager | http://localhost:9093 | Gestión de alertas |
| Grafana | http://localhost:3302 | Visualización |
| Netdata | http://localhost:19999 | Monitoreo sistema |
| Pushgateway | http://localhost:9091 | Métricas push |
| RabbitMQ | http://localhost:15674 | Management UI |
| Jenkins | http://localhost:8080 | CI/CD |

---

**Documentación generada automáticamente**  
**Proyecto:** Ensurance Pharmacy Monitoring System  
**Versión:** 1.0.0
