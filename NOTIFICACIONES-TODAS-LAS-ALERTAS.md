# 📧 Confirmación de Notificaciones - Todas las Alertas

**Total de Alertas:** 65  
**Notificaciones:** ✅ Gmail + 💬 Slack  
**Fecha:** 31 de Octubre, 2025

---

## ✅ Confirmación Global

**TODAS las 65 alertas envían notificaciones a:**
- 📧 **Gmail:** pablopolis2016@gmail.com, jflores@unis.edu.gt
- 💬 **Slack:** Canal #ensurance-alerts

---

## 🔄 Cómo Funciona el Sistema

Alertmanager está configurado para **automáticamente** enviar a ambos canales según la severidad:

### Receivers Configurados:

1. **critical-notifications** (19 alertas)
   - ✅ Gmail: Asunto "🔴 [CRÍTICO] Alerta Urgente"
   - ✅ Slack: Con @channel y emoji :rotating_light:
   - ⏱️ Repetición: Cada 5 minutos

2. **warning-notifications** (43 alertas)
   - ✅ Gmail: Asunto "⚠️ [WARNING] Alerta de Monitoreo"
   - ✅ Slack: Con emoji :warning:
   - ⏱️ Repetición: Cada 1 hora

3. **info-notifications** (3 alertas)
   - ✅ Gmail: Asunto "ℹ️ [INFO] Notificación"
   - ✅ Slack: Con emoji :information_source:
   - ⏱️ Repetición: Cada 6 horas

---

## 📋 Lista Completa de Alertas con Notificaciones

### 1️⃣ ALERTAS DE SISTEMA (12 alertas)

| # | Alerta | Severidad | Receiver | 📧 Gmail | 💬 Slack |
|---|--------|-----------|----------|---------|---------|
| 0 | HighRAMUsage | warning | warning-notifications | ✅ | ✅ |
| 1 | HighCPUUsage | warning | warning-notifications | ✅ | ✅ |
| 2 | CriticalCPUUsage | critical | critical-notifications | ✅ | ✅ |
| 3 | HighMemoryUsage | warning | warning-notifications | ✅ | ✅ |
| 4 | CriticalMemoryUsage | critical | critical-notifications | ✅ | ✅ |
| 5 | HighDiskUsage | warning | warning-notifications | ✅ | ✅ |
| 6 | CriticalDiskUsage | critical | critical-notifications | ✅ | ✅ |
| 7 | DiskAlmostFull | warning | warning-notifications | ✅ | ✅ |
| 8 | HighNetworkReceive | warning | warning-notifications | ✅ | ✅ |
| 9 | HighNetworkTransmit | warning | warning-notifications | ✅ | ✅ |
| 10 | NodeExporterDown | critical | critical-notifications | ✅ | ✅ |
| 11 | HighSystemLoad | warning | warning-notifications | ✅ | ✅ |

**Resumen:** 4 critical (Gmail + Slack), 8 warning (Gmail + Slack)

---

### 2️⃣ ALERTAS DE APLICACIONES (8 alertas)

| # | Alerta | Severidad | Receiver | 📧 Gmail | 💬 Slack |
|---|--------|-----------|----------|---------|---------|
| 12 | PharmacyBackendDown | critical | critical-notifications | ✅ | ✅ |
| 13 | EnsuranceBackendDown | critical | critical-notifications | ✅ | ✅ |
| 14 | EnsuranceFrontendDown | critical | critical-notifications | ✅ | ✅ |
| 15 | PharmacyFrontendDown | critical | critical-notifications | ✅ | ✅ |
| 16 | HighNodeMemoryBackendV5 | warning | warning-notifications | ✅ | ✅ |
| 17 | HighNodeMemoryBackendV4 | warning | warning-notifications | ✅ | ✅ |
| 18 | HighEventLoopLag | warning | warning-notifications | ✅ | ✅ |
| 19 | FrequentGarbageCollection | warning | warning-notifications | ✅ | ✅ |
| 20 | HighHTTPErrorRate | warning | warning-notifications | ✅ | ✅ |

**Resumen:** 4 critical (Gmail + Slack), 5 warning (Gmail + Slack)

---

### 3️⃣ ALERTAS DE RABBITMQ (12 alertas)

| # | Alerta | Severidad | Receiver | 📧 Gmail | 💬 Slack |
|---|--------|-----------|----------|---------|---------|
| 21 | RabbitMQDown | critical | critical-notifications | ✅ | ✅ |
| 22 | RabbitMQNodeDown | critical | critical-notifications | ✅ | ✅ |
| 23 | RabbitMQQueueMessagesHigh | warning | warning-notifications | ✅ | ✅ |
| 24 | RabbitMQQueueMessagesReady | warning | warning-notifications | ✅ | ✅ |
| 25 | RabbitMQUnacknowledgedMessages | warning | warning-notifications | ✅ | ✅ |
| 26 | RabbitMQQueueNoConsumers | warning | warning-notifications | ✅ | ✅ |
| 27 | RabbitMQHighMemory | warning | warning-notifications | ✅ | ✅ |
| 28 | RabbitMQCriticalMemory | critical | critical-notifications | ✅ | ✅ |
| 29 | RabbitMQMemoryAlarm | critical | critical-notifications | ✅ | ✅ |
| 30 | RabbitMQTooManyConnections | warning | warning-notifications | ✅ | ✅ |
| 31 | RabbitMQTooManyChannels | warning | warning-notifications | ✅ | ✅ |
| 32 | RabbitMQDiskAlarm | critical | critical-notifications | ✅ | ✅ |

**Resumen:** 5 critical (Gmail + Slack), 7 warning (Gmail + Slack)

---

### 4️⃣ ALERTAS DE K6 STRESS TESTING (8 alertas)

| # | Alerta | Severidad | Receiver | 📧 Gmail | 💬 Slack |
|---|--------|-----------|----------|---------|---------|
| 33 | K6HighErrorRate | critical | critical-notifications | ✅ | ✅ |
| 34 | K6HighResponseTimeP95 | warning | warning-notifications | ✅ | ✅ |
| 35 | K6CriticalResponseTimeP95 | critical | critical-notifications | ✅ | ✅ |
| 36 | K6HighResponseTimeP99 | warning | warning-notifications | ✅ | ✅ |
| 37 | K6FailedChecks | warning | warning-notifications | ✅ | ✅ |
| 38 | K6HighRequestRate | info | info-notifications | ✅ | ✅ |
| 39 | K6HighVirtualUsers | info | info-notifications | ✅ | ✅ |
| 40 | K6MetricsNotReceived | warning | warning-notifications | ✅ | ✅ |

**Resumen:** 2 critical (Gmail + Slack), 4 warning (Gmail + Slack), 2 info (Gmail + Slack)

---

### 5️⃣ ALERTAS DE CI/CD Y PIPELINES (12 alertas)

| # | Alerta | Severidad | Receiver | 📧 Gmail | 💬 Slack |
|---|--------|-----------|----------|---------|---------|
| 41 | JenkinsDown | critical | critical-notifications | ✅ | ✅ |
| 42 | PushgatewayDown | warning | warning-notifications | ✅ | ✅ |
| 43 | JenkinsBuildFailed | warning | warning-notifications | ✅ | ✅ |
| 44 | JenkinsSlowBuild | warning | warning-notifications | ✅ | ✅ |
| 45 | JenkinsLongQueue | warning | warning-notifications | ✅ | ✅ |
| 46 | JenkinsMultipleBuildFailures | critical | critical-notifications | ✅ | ✅ |
| 47 | JenkinsAllExecutorsBusy | warning | warning-notifications | ✅ | ✅ |
| 48 | JenkinsExecutorOffline | warning | warning-notifications | ✅ | ✅ |
| 49 | SonarQubeDown | warning | warning-notifications | ✅ | ✅ |
| 50 | SonarQubeQualityGateFailed | warning | warning-notifications | ✅ | ✅ |
| 51 | DroneServerDown | warning | warning-notifications | ✅ | ✅ |
| 52 | DroneRunnerDown | warning | warning-notifications | ✅ | ✅ |

**Resumen:** 2 critical (Gmail + Slack), 10 warning (Gmail + Slack)

---

### 6️⃣ ALERTAS DE MONITOREO (13 alertas)

| # | Alerta | Severidad | Receiver | 📧 Gmail | 💬 Slack |
|---|--------|-----------|----------|---------|---------|
| 53 | PrometheusDown | critical | critical-notifications | ✅ | ✅ |
| 54 | TargetDown | warning | warning-notifications | ✅ | ✅ |
| 55 | PrometheusHighMemory | warning | warning-notifications | ✅ | ✅ |
| 56 | PrometheusDroppingSamples | warning | warning-notifications | ✅ | ✅ |
| 57 | PrometheusTooManyTimeSeries | warning | warning-notifications | ✅ | ✅ |
| 58 | PrometheusSlowScrapes | warning | warning-notifications | ✅ | ✅ |
| 59 | GrafanaDown | warning | warning-notifications | ✅ | ✅ |
| 60 | NetdataDown | warning | warning-notifications | ✅ | ✅ |
| 61 | AlertmanagerDown | critical | critical-notifications | ✅ | ✅ |
| 62 | AlertmanagerFailedNotifications | warning | warning-notifications | ✅ | ✅ |
| 63 | AlertmanagerClusterUnsynchronized | warning | warning-notifications | ✅ | ✅ |
| 64 | PortainerDown | info | info-notifications | ✅ | ✅ |

**Resumen:** 2 critical (Gmail + Slack), 10 warning (Gmail + Slack), 1 info (Gmail + Slack)

---

## 📊 Resumen Global de Notificaciones

| Severidad | Cantidad | Gmail | Slack | Repetición |
|-----------|----------|-------|-------|------------|
| **CRITICAL** | **19** | **✅** | **✅** | **5 minutos** |
| **WARNING** | **43** | **✅** | **✅** | **1 hora** |
| **INFO** | **3** | **✅** | **✅** | **6 horas** |
| **TOTAL** | **65** | **✅** | **✅** | - |

---

## 📧 Configuración de Gmail

```yaml
receivers:
  - name: 'critical-notifications'
    email_configs:
      - to: 'pablopolis2016@gmail.com,jflores@unis.edu.gt'
        headers:
          Subject: '🔴 [CRÍTICO] Alerta Urgente - Ensurance Pharmacy'
```

```yaml
  - name: 'warning-notifications'
    email_configs:
      - to: 'pablopolis2016@gmail.com,jflores@unis.edu.gt'
        headers:
          Subject: '⚠️ [WARNING] Alerta de Monitoreo - Ensurance Pharmacy'
```

```yaml
  - name: 'info-notifications'
    email_configs:
      - to: 'pablopolis2016@gmail.com,jflores@unis.edu.gt'
        headers:
          Subject: 'ℹ️ [INFO] Notificación - Ensurance Pharmacy'
```

---

## 💬 Configuración de Slack

```yaml
receivers:
  - name: 'critical-notifications'
    slack_configs:
      - api_url: 'SLACK_WEBHOOK_URL_AQUI'
        channel: '#ensurance-alerts'
        username: 'Alertmanager'
        icon_emoji: ':rotating_light:'
        title: '🔴 ALERTA CRÍTICA - Ensurance Pharmacy'
```

```yaml
  - name: 'warning-notifications'
    slack_configs:
      - api_url: 'SLACK_WEBHOOK_URL_AQUI'
        channel: '#ensurance-alerts'
        username: 'Alertmanager'
        icon_emoji: ':warning:'
        title: '⚠️ WARNING - Ensurance Pharmacy'
```

```yaml
  - name: 'info-notifications'
    slack_configs:
      - api_url: 'SLACK_WEBHOOK_URL_AQUI'
        channel: '#ensurance-alerts'
        username: 'Alertmanager'
        icon_emoji: ':information_source:'
        title: 'ℹ️ INFO - Ensurance Pharmacy'
```

---

## ⚙️ Configuración del Routing

```yaml
route:
  receiver: 'default-notifications'  # Gmail + Slack
  group_by: ['alertname', 'cluster', 'service']
  
  routes:
    # Alertas críticas de aplicaciones
    - match_re:
        alertname: '(PharmacyBackendDown|EnsuranceBackendDown|EnsuranceFrontendDown|PharmacyFrontendDown)'
      receiver: 'critical-notifications'  # Gmail + Slack
      repeat_interval: 2h
    
    # Otras alertas críticas
    - match:
        severity: critical
      receiver: 'critical-notifications'  # Gmail + Slack
      repeat_interval: 5m
    
    # Alertas warning
    - match:
        severity: warning
      receiver: 'warning-notifications'  # Gmail + Slack
      repeat_interval: 1h
    
    # Alertas info
    - match:
        severity: info
      receiver: 'info-notifications'  # Gmail + Slack
      repeat_interval: 6h
```

---

## ✅ Verificación

### Para verificar que las notificaciones funcionan:

1. **Prueba rápida de email:**
```bash
./test-email-rapido.sh
```

2. **Diagnóstico completo:**
```bash
./test-email-alertmanager.sh
```

3. **Probar alertas específicas:**
```bash
./test-alertas-interactivo.sh
```

4. **Ver logs en tiempo real:**
```bash
docker logs -f ensurance-alertmanager-full
```

---

## 🔍 Cómo Saber si una Alerta Envió Notificación

### Ver logs de envío:
```bash
# Ver envíos de email (SMTP)
docker logs ensurance-alertmanager-full --tail 100 | grep -i smtp

# Ver envíos a Slack
docker logs ensurance-alertmanager-full --tail 100 | grep -i slack

# Ver notificaciones exitosas
docker logs ensurance-alertmanager-full --tail 100 | grep "Notify successful"

# Ver notificaciones fallidas
docker logs ensurance-alertmanager-full --tail 100 | grep "notify failed"
```

### Ver alertas activas:
```bash
# En Prometheus
curl -s http://localhost:9090/api/v1/alerts | jq '.data.alerts[] | {alert: .labels.alertname, state: .state}'

# En Alertmanager
curl -s http://localhost:9094/api/v1/alerts | jq '.data[] | {alert: .labels.alertname, status: .status.state}'
```

---

## ⚠️ Importante para Slack

**El webhook de Slack debe estar configurado:**

1. Actualmente en `alertmanager.yml` dice: `SLACK_WEBHOOK_URL_AQUI`
2. Debe ser reemplazado con la URL real del webhook

**Para configurar Slack:**

1. Ve a tu workspace de Slack
2. Crea una aplicación o webhook entrante
3. Copia la URL del webhook
4. Edita `monitoring/alertmanager/alertmanager.yml.template`
5. Reemplaza `SLACK_WEBHOOK_URL_AQUI` con tu URL real
6. Reinicia Alertmanager:
```bash
docker compose -f docker-compose.full.yml restart alertmanager
```

**Probar webhook manualmente:**
```bash
curl -X POST "TU_WEBHOOK_URL" \
  -H 'Content-Type: application/json' \
  -d '{"text":"Prueba de Slack desde Ensurance Pharmacy"}'
```

---

## ⚠️ Importante para Gmail

**Gmail requiere App Password:**

1. Actualmente configurado: `gmxaibrzxakzlnct`
2. Si los correos no llegan, genera nueva App Password:
   - https://myaccount.google.com/apppasswords
3. Actualiza en `monitoring/alertmanager/alertmanager.yml.template`
4. Reinicia Alertmanager

**Verificar configuración:**
```bash
# Ver configuración SMTP
docker exec ensurance-alertmanager-full cat /etc/alertmanager/alertmanager.yml | grep -A 5 "smtp"

# Ver destinatarios
docker exec ensurance-alertmanager-full cat /etc/alertmanager/alertmanager.yml | grep "to:"
```

---

## 📈 Métricas de Notificaciones

**Ver estadísticas de envío:**
```bash
# Métricas de Alertmanager
curl -s http://localhost:9094/metrics | grep alertmanager_notifications

# Total de notificaciones enviadas
curl -s http://localhost:9094/metrics | grep "alertmanager_notifications_total"

# Notificaciones fallidas
curl -s http://localhost:9094/metrics | grep "alertmanager_notifications_failed_total"
```

---

## 🎯 Resumen Ejecutivo

✅ **TODAS las 65 alertas están configuradas para enviar a Gmail Y Slack**

✅ **Gmail:** Funcionará si la App Password es válida

⚠️ **Slack:** Requiere configurar webhook URL (actualmente dice "SLACK_WEBHOOK_URL_AQUI")

✅ **Archivo de configuración:** `/monitoring/alertmanager/alertmanager.yml.template`

✅ **Destinatarios Gmail:**
  - pablopolis2016@gmail.com
  - jflores@unis.edu.gt

✅ **Canal Slack:**
  - #ensurance-alerts

---

## 🚀 Próximos Pasos

1. **Configurar webhook de Slack** (si aún no está configurado)
2. **Verificar App Password de Gmail** (si correos no llegan)
3. **Probar una alerta** con `./test-alertas-interactivo.sh`
4. **Verificar recepción** en email y Slack
5. **Revisar carpeta SPAM** si no llegan emails

---

**Última actualización:** 31 de Octubre, 2025

**Estado de Notificaciones:**
- 📧 Gmail: ✅ Configurado
- 💬 Slack: ⚠️ Requiere webhook URL
- 📊 Total Alertas: 65
- ✅ Todas envían a ambos canales
