# 🚨 Sistema de Alertas - Guía de Pruebas

## 📋 Configuración Actual

### Email (Gmail SMTP)
- **Servidor**: smtp.gmail.com:587 (TLS)
- **Remitente**: rfloresm@unis.edu.gt
- **Destinatarios**: 
  - jflores@unis.edu.gt
  - pablopolis2016@gmail.com

### Slack
- **Canal**: #ensurance-alerts
- **Webhook**: Configurado vía variable de entorno

### Alertas Configuradas
- **Total**: 64 reglas de alertas
- **Severidades**: critical, warning, info
- **Categorías**:
  - Servicios críticos (Down alerts)
  - Aplicaciones (Frontend/Backend)
  - Recursos del sistema (CPU, Memory, Disk)
  - RabbitMQ específicas
  - K6 performance
  - CI/CD (Jenkins, Drone, SonarQube)
  - Prometheus internal

## 🧪 Scripts de Prueba

### 1. Prueba Rápida (4 alertas)
```bash
./test-multiple-alerts.sh
```
**Duración**: ~5 minutos  
**Prueba**: RabbitMQ, Node Exporter, Grafana, Pushgateway

### 2. Prueba Completa (14+ alertas)
```bash
./test-all-alerts-complete.sh
```
**Duración**: ~30-40 minutos  
**Prueba**: Todas las alertas de servicios críticos y recursos

### 3. Prueba Manual Individual
```bash
# Detener un servicio
docker stop ensurance-rabbitmq-full

# Esperar 90 segundos para que la alerta se active
sleep 90

# Verificar alertas
curl -s http://localhost:9090/api/v1/alerts | jq '.data.alerts[] | select(.state=="firing")'

# Restaurar servicio
docker start ensurance-rabbitmq-full
```

## 📊 Verificación de Notificaciones

### Verificar Alertas Activas en Prometheus
```bash
curl -s http://localhost:9090/api/v1/alerts | \
  jq '.data.alerts[] | select(.state=="firing") | {name: .labels.alertname, severity: .labels.severity}'
```

### Verificar Alertas en Alertmanager
```bash
curl -s http://localhost:9094/api/v2/alerts | \
  jq '.[] | {name: .labels.alertname, state: .status.state}'
```

### Verificar Notificaciones Enviadas
```bash
# Emails enviados (últimos 10 minutos)
docker logs ensurance-alertmanager-full --since 10m 2>&1 | \
  grep -i "email.*notify success" | wc -l

# Mensajes Slack enviados (últimos 10 minutos)
docker logs ensurance-alertmanager-full --since 10m 2>&1 | \
  grep -i "slack.*notify success" | wc -l
```

## 🔧 Mantenimiento

### Actualizar Destinatarios de Email
1. Editar `.env.alertmanager`:
   ```bash
   ALERT_EMAIL_TO=nuevo@email.com,otro@email.com
   ```
2. Regenerar configuración:
   ```bash
   ./setup-alertmanager-secrets.sh
   ```

### Cambiar Servidor SMTP
1. Editar `.env.alertmanager`:
   ```bash
   SMTP_SMARTHOST=smtp.example.com:587
   SMTP_FROM=alerts@example.com
   SMTP_USERNAME=user@example.com
   SMTP_PASSWORD=password_here
   ```
2. Regenerar y reiniciar:
   ```bash
   ./setup-alertmanager-secrets.sh
   ```

### Cambiar Webhook de Slack
1. Editar `.env.alertmanager`:
   ```bash
   SLACK_WEBHOOK_URL=https://hooks.slack.com/services/YOUR/NEW/WEBHOOK
   ```
2. Regenerar:
   ```bash
   ./setup-alertmanager-secrets.sh
   ```

## 🌐 Interfaces Web

- **Prometheus Alerts**: http://localhost:9090/alerts
- **Alertmanager**: http://localhost:9094
- **Grafana**: http://localhost:3302

## 📝 Logs y Debugging

### Ver logs de Alertmanager en tiempo real
```bash
docker logs -f ensurance-alertmanager-full
```

### Ver solo notificaciones
```bash
docker logs ensurance-alertmanager-full --since 1h 2>&1 | \
  grep -i "notify\|email\|slack"
```

### Ver alertas con errores
```bash
docker logs ensurance-alertmanager-full --since 1h 2>&1 | \
  grep -i "error\|failed"
```

## ⚠️ Troubleshooting

### Los emails no llegan
1. Verificar credenciales en `.env.alertmanager`
2. Verificar que la contraseña de aplicación de Gmail es válida
3. Revisar carpeta de Spam
4. Verificar logs: `docker logs ensurance-alertmanager-full | grep email`

### Los mensajes de Slack no llegan
1. Verificar webhook URL en `.env.alertmanager`
2. Verificar que el canal #ensurance-alerts existe
3. Verificar logs: `docker logs ensurance-alertmanager-full | grep slack`

### Las alertas no se activan
1. Verificar que Prometheus está scrapeando las métricas:
   ```bash
   curl -s http://localhost:9090/api/v1/targets
   ```
2. Verificar las reglas de alertas:
   ```bash
   curl -s http://localhost:9090/api/v1/rules
   ```

## 🎯 Alertas por Categoría

### Críticas (Requieren acción inmediata)
- RabbitMQDown
- PrometheusDown  
- NodeExporterDown
- EnsuranceBackendDown
- EnsuranceFrontendDown
- PharmacyBackendDown
- PharmacyFrontendDown
- JenkinsDown
- DroneServerDown
- CriticalCPUUsage
- CriticalMemoryUsage
- CriticalDiskUsage

### Warning (Requieren atención)
- GrafanaDown
- PushgatewayDown
- PortainerDown
- NetdataDown
- DroneRunnerDown
- SonarQubeDown
- HighCPUUsage
- HighMemoryUsage
- HighDiskUsage
- RabbitMQHighMemory
- RabbitMQQueueMessagesHigh

### Info (Informativas)
- K6MetricsNotReceived
- PrometheusSlowScrapes

## 📧 Formato de Notificaciones

### Email
```
Asunto: [CRÍTICO] Alerta - RabbitMQDown
From: rfloresm@unis.edu.gt
To: jflores@unis.edu.gt, pablopolis2016@gmail.com

🔴 ALERTA CRÍTICA - Ensurance Pharmacy

Alerta: RabbitMQDown
Severidad: CRITICAL
Descripción: RabbitMQ is down
Inicio: 2025-10-29 22:30:00
Servicio: rabbitmq
Instancia: ensurance-rabbitmq-full:15672
```

### Slack
```
🔴 ALERTA CRÍTICA - Ensurance Pharmacy

Alerta: RabbitMQDown
Severidad: CRITICAL
Descripción: RabbitMQ is down
Resumen: RabbitMQ service is not responding
Inicio: 2025-10-29 22:30:00
```

## ✅ Checklist de Verificación

- [ ] Email de prueba recibido
- [ ] Mensaje de Slack de prueba recibido
- [ ] Alertas se activan al detener servicios
- [ ] Notificaciones llegan en < 2 minutos
- [ ] Logs de Alertmanager sin errores
- [ ] `.env.alertmanager` en .gitignore
- [ ] Secretos no comprometidos en Git

---

**Última actualización**: 2025-10-29  
**Sistema funcionando**: ✅ Email + Slack  
**Alertas configuradas**: 64 reglas
