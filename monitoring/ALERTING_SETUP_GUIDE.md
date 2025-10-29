# 🚨 Sistema de Alertas - Ensurance Pharmacy

## 📋 Resumen

Sistema completo de alertas con 64 reglas de monitoreo que envían notificaciones por **Email (SMTP)** y **Slack**.

---

## 🎯 Configuración de Notificaciones

### Email (SMTP) - ✅ CONFIGURADO

**Proveedor:** Brevo (ex-Sendinblue)

| Configuración | Valor |
|---------------|-------|
| **SMTP Server** | `smtp-relay.brevo.com` |
| **Port** | `587` (TLS) |
| **Username** | `945b13001@smtp-brevo.com` |
| **Password** | `${SMTP_PASSWORD}` (configurado en alertmanager.yml) |
| **From** | `945b13001@smtp-brevo.com` |
| **Destinatarios** | `pablopolis2016@gmail.com`, `jflores@unis.edu.gt` |

### Slack - ⚠️ REQUIERE CONFIGURACIÓN

Necesitas crear un **Incoming Webhook** en Slack:

#### Pasos para Configurar Slack:

1. **Crear Workspace (si no existe)**
   - Ve a https://slack.com/create
   - Nombre sugerido: "Ensurance Pharmacy"

2. **Crear Canal de Alertas**
   - En tu workspace, crear canal: `#ensurance-alerts`
   - Descripción: "Alertas automáticas del sistema de monitoreo"

3. **Crear Incoming Webhook**
   - Ve a: https://api.slack.com/apps
   - Click en "Create New App" → "From scratch"
   - Nombre: "Ensurance Alerting"
   - Workspace: Tu workspace
   
4. **Activar Incoming Webhooks**
   - En la app, ir a "Incoming Webhooks"
   - Toggle "Activate Incoming Webhooks" a ON
   - Click "Add New Webhook to Workspace"
   - Seleccionar canal: `#ensurance-alerts`
   - Click "Allow"

5. **Copiar Webhook URL**
   - Recibirás una URL como: `https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX`
   - **IMPORTANTE:** Guarda esta URL

6. **Configurar en Alertmanager**
   ```bash
   # Editar el archivo de configuración
   nano monitoring/alertmanager/alertmanager.yml
   
   # Buscar todas las líneas que digan:
   api_url: 'SLACK_WEBHOOK_URL_AQUI'
   
   # Reemplazar con tu webhook URL:
   api_url: 'https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX'
   ```

7. **Reiniciar Alertmanager**
   ```bash
   docker compose -f docker-compose.full.yml restart alertmanager
   ```

---

## 📊 Resumen de Alertas (64 Total)

### Sistema (11 alertas) - `system_alerts.yml`
| # | Alerta | Umbral | Severidad |
|---|--------|---------|-----------|
| 1 | `HighCPUUsage` | CPU > 70% por 2min | warning |
| 2 | `CriticalCPUUsage` | CPU > 90% por 1min | **critical** |
| 3 | `HighMemoryUsage` | Memoria > 80% por 2min | warning |
| 4 | `CriticalMemoryUsage` | Memoria > 95% por 1min | **critical** |
| 5 | `HighDiskUsage` | Disco > 75% por 5min | warning |
| 6 | `CriticalDiskUsage` | Disco > 90% por 2min | **critical** |
| 7 | `DiskAlmostFull` | < 5GB disponibles | warning |
| 8 | `HighNetworkReceive` | > 100MB/s entrante | warning |
| 9 | `HighNetworkTransmit` | > 100MB/s saliente | warning |
| 10 | `NodeExporterDown` | Node Exporter caído | **critical** |
| 11 | `HighSystemLoad` | Load > 2x CPUs | warning |

### Aplicaciones (9 alertas) - `application_alerts.yml`
| # | Alerta | Umbral | Severidad |
|---|--------|---------|-----------|
| 12 | `PharmacyBackendDown` | Backend v5 caído | **critical** |
| 13 | `EnsuranceBackendDown` | Backend v4 caído | **critical** |
| 14 | `EnsuranceFrontendDown` | Frontend caído | **critical** |
| 15 | `PharmacyFrontendDown` | Frontend caído | **critical** |
| 16 | `HighNodeMemoryBackendV5` | Heap > 85% por 5min | warning |
| 17 | `HighNodeMemoryBackendV4` | Heap > 85% por 5min | warning |
| 18 | `HighEventLoopLag` | Lag > 0.5s por 3min | warning |
| 19 | `FrequentGarbageCollection` | GC > 10/s por 5min | warning |
| 20 | `HighHTTPErrorRate` | Errores 5xx > 5% | warning |

### RabbitMQ (12 alertas) - `rabbitmq_alerts.yml`
| # | Alerta | Umbral | Severidad |
|---|--------|---------|-----------|
| 21 | `RabbitMQDown` | RabbitMQ caído | **critical** |
| 22 | `RabbitMQNodeDown` | Nodo caído | **critical** |
| 23 | `RabbitMQQueueMessagesHigh` | > 1000 mensajes en cola | warning |
| 24 | `RabbitMQQueueMessagesReady` | > 500 mensajes sin procesar | warning |
| 25 | `RabbitMQUnacknowledgedMessages` | > 100 sin ACK | warning |
| 26 | `RabbitMQQueueNoConsumers` | Cola sin consumers | warning |
| 27 | `RabbitMQHighMemory` | Memoria > 80% | warning |
| 28 | `RabbitMQCriticalMemory` | Memoria > 95% | **critical** |
| 29 | `RabbitMQMemoryAlarm` | Alarma activada | **critical** |
| 30 | `RabbitMQTooManyConnections` | > 100 conexiones | warning |
| 31 | `RabbitMQTooManyChannels` | > 500 channels | warning |
| 32 | `RabbitMQDiskAlarm` | Alarma de disco | **critical** |

### K6 Stress Testing (8 alertas) - `k6_alerts.yml`
| # | Alerta | Umbral | Severidad |
|---|--------|---------|-----------|
| 33 | `K6HighErrorRate` | Errores > 5% por 1min | **critical** |
| 34 | `K6HighResponseTimeP95` | P95 > 1000ms por 2min | warning |
| 35 | `K6CriticalResponseTimeP95` | P95 > 3000ms por 1min | **critical** |
| 36 | `K6HighResponseTimeP99` | P99 > 5000ms por 1min | warning |
| 37 | `K6FailedChecks` | Checks fallando | warning |
| 38 | `K6HighRequestRate` | > 1000 req/s | info |
| 39 | `K6HighVirtualUsers` | > 100 VUs | info |
| 40 | `K6MetricsNotReceived` | Sin métricas por 5min | warning |

### CI/CD (12 alertas) - `cicd_alerts.yml`
| # | Alerta | Umbral | Severidad |
|---|--------|---------|-----------|
| 41 | `JenkinsDown` | Jenkins caído | **critical** |
| 42 | `PushgatewayDown` | Pushgateway caído | warning |
| 43 | `JenkinsBuildFailed` | Build fallido | warning |
| 44 | `JenkinsSlowBuild` | > 30 minutos | warning |
| 45 | `JenkinsLongQueue` | > 5 builds en cola | warning |
| 46 | `JenkinsMultipleBuildFailures` | > 3 fallos en 15min | **critical** |
| 47 | `JenkinsAllExecutorsBusy` | > 90% ocupados por 10min | warning |
| 48 | `JenkinsExecutorOffline` | Executor offline | warning |
| 49 | `SonarQubeDown` | SonarQube caído | warning |
| 50 | `SonarQubeQualityGateFailed` | Quality Gate ERROR | warning |
| 51 | `DroneServerDown` | Drone caído | warning |
| 52 | `DroneRunnerDown` | Drone Runner caído | warning |

### Monitoreo (12 alertas) - `monitoring_alerts.yml`
| # | Alerta | Umbral | Severidad |
|---|--------|---------|-----------|
| 53 | `PrometheusDown` | Prometheus caído | **critical** |
| 54 | `TargetDown` | Cualquier target caído | warning |
| 55 | `PrometheusHighMemory` | > 2GB por 5min | warning |
| 56 | `PrometheusDroppingSamples` | Descartando samples | warning |
| 57 | `PrometheusTooManyTimeSeries` | > 100k series | warning |
| 58 | `PrometheusSlowScrapes` | P99 > 10s | warning |
| 59 | `GrafanaDown` | Grafana caído | warning |
| 60 | `NetdataDown` | Netdata caído | warning |
| 61 | `AlertmanagerDown` | Alertmanager caído | **critical** |
| 62 | `AlertmanagerFailedNotifications` | > 0.1 fallos/s | warning |
| 63 | `AlertmanagerClusterUnsynchronized` | Cluster desincronizado | warning |
| 64 | `PortainerDown` | Portainer caído | info |

---

## 🎨 Niveles de Severidad

### 🔴 CRITICAL (16 alertas)
- **Notificación:** Email + Slack con @channel
- **Acción:** Inmediata
- **Ejemplos:** Servicios caídos, recursos críticos (CPU>90%, Memoria>95%)

### ⚠️ WARNING (42 alertas)
- **Notificación:** Email + Slack
- **Acción:** Revisar en 1-3 horas
- **Ejemplos:** Alto uso de recursos, colas largas, builds lentos

### ℹ️ INFO (6 alertas)
- **Notificación:** Solo Email
- **Acción:** Informativo, sin acción requerida
- **Ejemplos:** Tests ejecutándose, métricas informativas

---

## 🚀 Despliegue del Sistema de Alertas

### 1. Levantar Alertmanager y Prometheus

```bash
# Asegurarse de tener la configuración de Slack
cd /home/pablopolis2016/Documents/ensurancePharmacy

# Verificar que las reglas están en su lugar
ls -la monitoring/prometheus/rules/

# Levantar Alertmanager
docker compose -f docker-compose.full.yml up -d alertmanager

# Esperar 10 segundos
sleep 10

# Recargar Prometheus (para que lea las nuevas reglas)
docker compose -f docker-compose.full.yml restart prometheus
```

### 2. Verificar que Alertmanager está funcionando

```bash
# Ver logs
docker logs ensurance-alertmanager-full -f

# Verificar API
curl http://localhost:9093/api/v1/status

# Ver alertas activas
curl http://localhost:9093/api/v1/alerts
```

### 3. Verificar que Prometheus carga las reglas

```bash
# Ver reglas cargadas
curl http://localhost:9090/api/v1/rules | python3 -m json.tool

# Ver alertas activas en Prometheus
curl http://localhost:9090/api/v1/alerts | python3 -m json.tool
```

### 4. Probar el Sistema

```bash
# Probar alerta de test (simular alta CPU)
# Esta alerta se disparará automáticamente si hay problemas
```

---

## 🧪 Prueba de Notificaciones

### Prueba de Email

1. **Forzar una alerta simple:**
   ```bash
   # Detener node-exporter temporalmente
   docker stop ensurance-node-exporter-full
   
   # Esperar 2 minutos para que se dispare NodeExporterDown
   # Deberías recibir un email
   
   # Reiniciar
   docker start ensurance-node-exporter-full
   ```

2. **Verificar que el email llegó a:**
   - ✉️ pablopolis2016@gmail.com
   - ✉️ jflores@unis.edu.gt

### Prueba de Slack

1. **Después de configurar el webhook, probar:**
   ```bash
   # Detener RabbitMQ temporalmente
   docker stop ensurance-rabbitmq-full
   
   # Esperar 1 minuto para RabbitMQDown (critical)
   # Deberías recibir notificación en Slack con @channel
   
   # Reiniciar
   docker start ensurance-rabbitmq-full
   ```

2. **Verificar en Slack:**
   - Canal: `#ensurance-alerts`
   - Mensaje con formato de alerta crítica
   - Mención @channel

---

## 📱 Acceso a Interfaces

| Servicio | URL | Descripción |
|----------|-----|-------------|
| **Alertmanager UI** | http://localhost:9093 | Ver alertas activas y silenciadas |
| **Prometheus Alerts** | http://localhost:9090/alerts | Ver reglas y estado de alertas |
| **Grafana** | http://localhost:3302 | Dashboards con alertas visuales |
| **Netdata** | http://localhost:19999 | Monitoreo en tiempo real |

---

## 📧 Formato de Emails

### Email CRÍTICO (ejemplo)
```
Asunto: 🔴 [CRÍTICO] Alerta Urgente - Ensurance Pharmacy

⚠️ ALERTA CRÍTICA ⚠️
Ensurance Pharmacy - Acción Inmediata Requerida

🔴 Severidad: CRÍTICO
📛 Alerta: HighCPUUsage
📝 Descripción: El uso de CPU está en 92%
💡 Resumen: CPU sobrepasa el 70%
🕐 Inicio: 2025-10-29 06:30:00
🎯 Servicio: system
🖥️ Instancia: node-exporter:9100

⚡ ACCIÓN REQUERIDA INMEDIATAMENTE
```

### Email WARNING (ejemplo)
```
Asunto: ⚠️ [WARNING] Alerta de Monitoreo - Ensurance Pharmacy

⚠️ Advertencia de Monitoreo
Ensurance Pharmacy

⚠️ Severidad: WARNING
📛 Alerta: HighMemoryUsage
📝 Descripción: El uso de memoria está en 82%
💡 Resumen: Memoria sobrepasa el 80%
🕐 Inicio: 2025-10-29 06:30:00
🎯 Servicio: system
```

---

## 💡 Mejores Prácticas

### Gestión de Alertas

1. **No Silenciar Alertas Críticas** sin resolver el problema
2. **Revisar Alertas WARNING** al menos 2 veces al día
3. **Ajustar Umbrales** según patrones reales de uso
4. **Documentar Resoluciones** para futuras referencias

### Mantenimiento

```bash
# Ver alertas activas
curl -s http://localhost:9093/api/v1/alerts | python3 -m json.tool

# Silenciar una alerta (por ID)
curl -X POST http://localhost:9093/api/v1/silences \
  -d '{"matchers":[{"name":"alertname","value":"HighCPUUsage"}],"startsAt":"2025-10-29T00:00:00Z","endsAt":"2025-10-29T23:59:59Z","createdBy":"admin","comment":"Mantenimiento programado"}'

# Ver silencios activos
curl -s http://localhost:9093/api/v1/silences | python3 -m json.tool
```

---

## 🔧 Troubleshooting

### Email no llega

1. **Verificar configuración SMTP:**
   ```bash
   docker logs ensurance-alertmanager-full | grep -i "smtp\|email"
   ```

2. **Probar SMTP manualmente:**
   ```bash
   docker exec ensurance-alertmanager-full sh -c '
   echo "Subject: Test Email
   
   This is a test" | sendmail -v -f 945b13001@smtp-brevo.com pablopolis2016@gmail.com
   '
   ```

3. **Revisar spam/promotions** en Gmail

### Slack no funciona

1. **Verificar webhook URL en alertmanager.yml**
2. **Probar webhook manualmente:**
   ```bash
   curl -X POST "TU_WEBHOOK_URL" \
     -H 'Content-Type: application/json' \
     -d '{"text":"Test desde Ensurance Pharmacy"}'
   ```

3. **Ver logs de Alertmanager:**
   ```bash
   docker logs ensurance-alertmanager-full | grep -i slack
   ```

### Alertas no se disparan

1. **Verificar reglas en Prometheus:**
   ```bash
   curl http://localhost:9090/api/v1/rules | python3 -m json.tool | grep -A 20 "HighCPUUsage"
   ```

2. **Ver estado de alertas:**
   ```bash
   curl http://localhost:9090/api/v1/alerts | python3 -m json.tool
   ```

3. **Verificar métricas:**
   ```bash
   curl -s "http://localhost:9090/api/v1/query?query=up" | python3 -m json.tool
   ```

---

## 📝 Configuración de Grafana (Opcional)

Grafana también puede enviar alertas. Para configurar:

1. **Ir a Grafana:** http://localhost:3302
2. **Login:** admin / changeme
3. **Alerting → Contact points**
4. **Add contact point:**
   - **Name:** Ensurance Email
   - **Type:** Email
   - **Addresses:** pablopolis2016@gmail.com;jflores@unis.edu.gt
   - **SMTP Host:** smtp-relay.brevo.com:587
   - **Username:** 945b13001@smtp-brevo.com
   - **Password:** (el token de Brevo)
   - **From:** 945b13001@smtp-brevo.com

5. **Add contact point (Slack):**
   - **Name:** Ensurance Slack
   - **Type:** Slack
   - **Webhook URL:** (tu webhook de Slack)
   - **Channel:** #ensurance-alerts

---

## 📊 Dashboard de Alertas

Puedes crear un dashboard en Grafana para visualizar alertas:

```promql
# Panel 1: Alertas Activas
ALERTS{alertstate="firing"}

# Panel 2: Alertas por Severidad
count by (severity) (ALERTS{alertstate="firing"})

# Panel 3: Histórico de Alertas
rate(prometheus_notifications_total[5m])
```

---

**Última actualización:** 2025-10-29  
**Mantenido por:** Equipo Ensurance Pharmacy  
**Contacto:** pablopolis2016@gmail.com
