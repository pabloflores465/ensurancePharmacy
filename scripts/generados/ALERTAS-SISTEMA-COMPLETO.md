# 🚨 Sistema Completo de Alertas - Ensurance Pharmacy

## 📋 Resumen Ejecutivo

Sistema de alertas integrado con **Email** y **Slack** para Grafana (Prometheus) y Netdata.

**Total de Alertas Configuradas:** ~110 alertas
- **Prometheus/Grafana:** 64 alertas
- **Netdata:** ~45 alertas

---

## 🎯 Alertas Configuradas

### Grafana (Prometheus) - 64 Alertas

#### Sistema (11 alertas) - `system_alerts.yml`
| Alerta | Umbral | Lógica | Severidad |
|--------|---------|--------|-----------|
| HighCPUUsage | CPU > 70% por 2min | Se envía correo y Slack cuando CPU > 70% | WARNING |
| CriticalCPUUsage | CPU > 90% por 1min | Acción inmediata requerida | CRITICAL |
| HighMemoryUsage | Memoria > 80% por 2min | Se envía correo y Slack cuando memoria > 80% | WARNING |
| CriticalMemoryUsage | Memoria > 95% por 1min | Riesgo de OOM killer | CRITICAL |
| HighDiskUsage | Disco > 75% por 5min | Se envía correo y Slack cuando disco > 75% | WARNING |
| CriticalDiskUsage | Disco > 90% por 2min | Expandir disco urgentemente | CRITICAL |
| HighNetworkReceive | > 100MB/s entrante por 5min | Se envía correo y Slack cuando tráfico > 100MB/s | WARNING |
| HighNetworkTransmit | > 100MB/s saliente por 5min | Verificar posible exfiltración | WARNING |
| HighSystemLoad | Load > 2x CPUs por 5min | Se envía correo y Slack cuando load > 2x CPUs | WARNING |

#### K6 (8 alertas) - `k6_alerts.yml`
| Alerta | Umbral | Lógica | Severidad |
|--------|---------|--------|-----------|
| K6HighErrorRate | Errores > 5% por 1min | Se envía correo y Slack cuando errores > 5% | CRITICAL |
| K6HighResponseTimeP95 | P95 > 1000ms por 2min | Se envía correo y Slack cuando p95 > 1s | WARNING |
| K6FailedChecks | Checks fallando | Validaciones fallando en test | WARNING |

#### CI/CD (12 alertas) - `cicd_alerts.yml`
| Alerta | Umbral | Lógica | Severidad |
|--------|---------|--------|-----------|
| JenkinsDown | Jenkins caído por 2min | Se envía correo y Slack inmediatamente | CRITICAL |
| JenkinsBuildFailed | Build fallido | Revisar logs del build | WARNING |

---

### Netdata - ~45 Alertas

#### Sistema (~15 alertas) - `system_alerts.conf`
| Alerta | Umbral | Lógica | Severidad |
|--------|---------|--------|-----------|
| netdata_high_cpu_usage | CPU > 70% por 2min | Se envía correo y Slack cuando CPU > 70% | WARNING |
| netdata_high_memory_usage | Memoria > 80% por 2min | Se envía correo y Slack cuando memoria > 80% | WARNING |
| netdata_high_disk_usage | Disco > 75% por 5min | Se envía correo y Slack cuando disco > 75% | WARNING |
| netdata_high_network_receive | > 100MB/s por 5min | Se envía correo y Slack cuando tráfico > 100MB/s | WARNING |
| netdata_high_system_load | Load > 2x CPUs por 5min | Se envía correo y Slack cuando load > 2x CPUs | WARNING |

#### Aplicaciones (~12 alertas) - `application_alerts.conf`
| Alerta | Umbral | Lógica | Severidad |
|--------|---------|--------|-----------|
| netdata_container_high_cpu | Container CPU > 80% por 3min | Se envía correo y Slack cuando container use > 80% CPU | WARNING |
| netdata_container_high_memory | Container Mem > 85% por 3min | Se envía correo y Slack cuando container use > 85% memoria | WARNING |
| netdata_rabbitmq_high_memory | RabbitMQ > 1GB por 5min | Se envía correo y Slack cuando RabbitMQ use > 1GB | WARNING |
| netdata_high_http_5xx_errors | Errores 5xx > 5% por 2min | Se envía correo y Slack cuando errores 5xx > 5% | WARNING |

#### K6 (7 alertas) - `k6_alerts.conf`
| Alerta | Umbral | Lógica | Severidad |
|--------|---------|--------|-----------|
| netdata_k6_high_error_rate | Errores > 1% por 1min | Se envía correo y Slack cuando errores > 1% | WARNING |
| netdata_k6_high_response_time_p95 | P95 > 500ms por 2min | Se envía correo y Slack cuando p95 > 500ms | WARNING |
| netdata_k6_failed_checks | Checks fallando | Se envía correo y Slack cuando checks fallan | WARNING |

#### CI/CD (~10 alertas) - `pipeline_alerts.conf`
| Alerta | Umbral | Lógica | Severidad |
|--------|---------|--------|-----------|
| netdata_jenkins_high_memory | Jenkins > 2GB por 5min | Se envía correo y Slack cuando Jenkins use > 2GB | WARNING |
| netdata_jenkins_high_cpu | Jenkins CPU > 70% por 3min | Se envía correo y Slack cuando Jenkins use > 70% CPU | WARNING |
| netdata_docker_high_cpu | Docker CPU > 70% por 3min | Se envía correo y Slack cuando Docker use > 70% CPU | WARNING |

---

## 📧 Notificaciones

### Email (SMTP)
- **Proveedor:** Brevo (smtp-relay.brevo.com:587)
- **Destinatarios:** pablopolis2016@gmail.com, jflores@unis.edu.gt
- **Niveles:** CRITICAL, WARNING, INFO

### Slack
- **Canal:** #ensurance-alerts
- **Webhook:** Configurado vía SLACK_WEBHOOK_URL_AQUI
- **Niveles:** CRITICAL, WARNING, INFO
- **Formato:** Mensajes con emoji, colores y menciones @channel para críticas

---

## 🧪 Testing

### Script de Validación
```bash
./test-all-monitoring-alerts.sh
```

### Prueba Manual - Email y Slack
```bash
# Test 1: Detener Node Exporter (WARNING)
docker stop ensurance-node-exporter-full
sleep 120
docker start ensurance-node-exporter-full

# Test 2: Detener RabbitMQ (CRITICAL - con @channel)
docker stop ensurance-rabbitmq-full
sleep 90
docker start ensurance-rabbitmq-full
```

### Verificar Notificaciones
```bash
# Ver emails enviados
docker logs ensurance-alertmanager-full --since 10m | grep -i "email.*notify success"

# Ver Slack enviados
docker logs ensurance-alertmanager-full --since 10m | grep -i "slack.*notify success"
```

---

## 🔧 Configuración

### Configurar Webhook de Slack
```bash
./configure-slack-webhook.sh "https://hooks.slack.com/services/YOUR/WEBHOOK/URL"
```

### Aplicar Cambios
```bash
# Reiniciar Alertmanager
docker compose -f docker-compose.full.yml restart alertmanager

# Recargar Prometheus
docker compose -f docker-compose.full.yml restart prometheus

# Reiniciar Netdata
docker compose -f docker-compose.full.yml restart netdata
```

---

## 📊 Interfaces

- **Prometheus Alerts:** http://localhost:9090/alerts
- **Alertmanager:** http://localhost:9094
- **Grafana:** http://localhost:3302
- **Netdata:** http://localhost:19999

---

## ✅ Checklist de Implementación

- [x] AlertManager configurado con Email y Slack
- [x] 64 alertas de Prometheus configuradas
- [x] ~45 alertas de Netdata configuradas
- [x] Todos los receivers tienen Email y Slack
- [x] Script de validación creado
- [ ] Webhook de Slack configurado (requiere URL del usuario)
- [ ] Pruebas de notificación ejecutadas
- [ ] Documentación revisada

---

**Creado:** $(date +%Y-%m-%d)  
**Sistema:** Ensurance Pharmacy Monitoring  
**Alertas Totales:** ~110 alertas
