# Equivalencias entre Alertas de Netdata y CheckMK

Este documento mapea las alertas configuradas en Netdata con sus equivalentes en CheckMK para Ensurance Pharmacy.

## 📊 Resumen de Integración

CheckMK se ha configurado para:
- ✅ Conectarse directamente a Prometheus (misma fuente que Netdata)
- ✅ Monitorear los mismos hosts y servicios
- ✅ Usar los mismos umbrales de alerta
- ✅ Enviar notificaciones por Email (como Netdata)

---

## 🖥️ ALERTAS DE SISTEMA (system_alerts.conf)

### CPU Alerts

| Alerta Netdata | Equivalente CheckMK | Umbrales |
|----------------|---------------------|----------|
| `netdata_high_cpu_usage` | CPU Utilization Check | WARNING: >70%, CRITICAL: >90% |
| `netdata_cpu_saturation` | CPU Utilization Check | WARNING: >95%, CRITICAL: >98% |

**Delay:** Up 2m, Down 5m

### Memory Alerts

| Alerta Netdata | Equivalente CheckMK | Umbrales |
|----------------|---------------------|----------|
| `netdata_high_memory_usage` | Memory Usage Check | WARNING: >80%, CRITICAL: >95% |
| `netdata_low_available_memory` | Memory Available Check | WARNING: <10%, CRITICAL: <5% |
| `netdata_swap_usage` | Swap Usage Check | WARNING: >20%, CRITICAL: >50% |

**Delay:** Up 2m, Down 5m

### Disk Alerts

| Alerta Netdata | Equivalente CheckMK | Umbrales |
|----------------|---------------------|----------|
| `netdata_high_disk_usage` | Filesystem Check | WARNING: >75%, CRITICAL: >90% |
| `netdata_low_disk_space_gb` | Filesystem Check | WARNING: <10GB, CRITICAL: <5GB |
| `netdata_high_disk_io` | Disk I/O Check | WARNING: >50MB/s, CRITICAL: >100MB/s |

**Delay:** Up 5m, Down 15m

### Network Alerts

| Alerta Netdata | Equivalente CheckMK | Umbrales |
|----------------|---------------------|----------|
| `netdata_high_network_receive` | Interface Traffic Check | WARNING: >100MB/s, CRITICAL: >200MB/s |
| `netdata_high_network_transmit` | Interface Traffic Check | WARNING: >100MB/s, CRITICAL: >200MB/s |
| `netdata_network_packet_drops` | Interface Errors Check | WARNING: >10 packets/s, CRITICAL: >100 packets/s |

**Delay:** Up 5m, Down 10m

### Load Average

| Alerta Netdata | Equivalente CheckMK | Umbrales |
|----------------|---------------------|----------|
| `netdata_high_system_load` | CPU Load Check | WARNING: >2x CPUs, CRITICAL: >4x CPUs |

**Delay:** Up 5m, Down 10m

### Processes

| Alerta Netdata | Equivalente CheckMK | Umbrales |
|----------------|---------------------|----------|
| `netdata_too_many_processes` | Process Count Check | WARNING: >1000, CRITICAL: >5000 |

---

## 🌐 ALERTAS DE APLICACIONES (application_alerts.conf)

### Web Service Health

| Alerta Netdata | Equivalente CheckMK | Configuración |
|----------------|---------------------|---------------|
| `netdata_web_service_down` | HTTP Check - Active Check | Timeout: 10s, Check interval: 30s |
| `netdata_high_http_5xx_errors` | HTTP Status Check | WARNING: >5%, CRITICAL: >20% |
| `netdata_high_http_4xx_errors` | HTTP Status Check | WARNING: >10%, CRITICAL: >30% |
| `netdata_slow_web_response` | HTTP Response Time | WARNING: >1s, CRITICAL: >3s |

**Servicios monitoreados:**
- ✅ Ensurance Frontend (http://localhost:5175)
- ✅ Pharmacy Frontend (http://localhost:8089)
- ✅ Ensurance Backend (http://localhost:8081/health)
- ✅ Pharmacy Backend (http://localhost:8082/health)

### Docker Container Alerts

| Alerta Netdata | Equivalente CheckMK | Umbrales |
|----------------|---------------------|----------|
| `netdata_container_high_cpu` | Docker Container CPU | WARNING: >80%, CRITICAL: >95% |
| `netdata_container_high_memory` | Docker Container Memory | WARNING: >85%, CRITICAL: >95% |

**Delay:** Up 3m, Down 5m

### Application Process Alerts

| Alerta Netdata | Equivalente CheckMK | Umbrales |
|----------------|---------------------|----------|
| `netdata_java_process_high_memory` | Process Memory Check (backv4/v5) | WARNING: >2GB, CRITICAL: >4GB |
| `netdata_java_process_high_cpu` | Process CPU Check (backv4/v5) | WARNING: >70%, CRITICAL: >90% |
| `netdata_nodejs_process_high_memory` | Process Memory Check (frontends) | WARNING: >1GB, CRITICAL: >2GB |
| `netdata_nodejs_process_high_cpu` | Process CPU Check (frontends) | WARNING: >60%, CRITICAL: >80% |
| `netdata_rabbitmq_high_memory` | Process Memory Check (rabbitmq) | WARNING: >1GB, CRITICAL: >2GB |
| `netdata_rabbitmq_high_cpu` | Process CPU Check (rabbitmq) | WARNING: >70%, CRITICAL: >90% |

---

## 🔄 ALERTAS DE CI/CD (pipeline_alerts.conf)

### Jenkins Monitoring

| Alerta Netdata | Equivalente CheckMK | Umbrales |
|----------------|---------------------|----------|
| `netdata_jenkins_high_memory` | Process Memory Check | WARNING: >2GB, CRITICAL: >4GB |
| `netdata_jenkins_high_cpu` | Process CPU Check | WARNING: >70%, CRITICAL: >90% |
| `netdata_jenkins_too_many_workers` | Process Count Check | WARNING: >20, CRITICAL: >50 |

### SonarQube Monitoring

| Alerta Netdata | Equivalente CheckMK | Umbrales |
|----------------|---------------------|----------|
| `netdata_sonarqube_high_memory` | Process Memory Check | WARNING: >2GB, CRITICAL: >4GB |
| `netdata_sonarqube_high_cpu` | Process CPU Check | WARNING: >70%, CRITICAL: >90% |

### Docker Daemon (for builds)

| Alerta Netdata | Equivalente CheckMK | Umbrales |
|----------------|---------------------|----------|
| `netdata_docker_high_cpu` | Process CPU Check (dockerd) | WARNING: >70%, CRITICAL: >90% |
| `netdata_docker_high_memory` | Process Memory Check (dockerd) | WARNING: >2GB, CRITICAL: >4GB |

### Monitoring Tools

| Alerta Netdata | Equivalente CheckMK | Umbrales |
|----------------|---------------------|----------|
| `netdata_prometheus_high_memory` | Process Memory Check | WARNING: >2GB, CRITICAL: >4GB |
| `netdata_grafana_high_memory` | Process Memory Check | WARNING: >1GB, CRITICAL: >2GB |

---

## ⚡ ALERTAS DE K6 (k6_alerts.conf)

### K6 Stress Testing

| Alerta Netdata | Equivalente CheckMK | Umbrales |
|----------------|---------------------|----------|
| `netdata_k6_high_error_rate` | K6 Error Rate Check (via Prometheus) | WARNING: >1%, CRITICAL: >5% |
| `netdata_k6_high_response_time_p95` | K6 Response Time p95 Check | WARNING: >500ms, CRITICAL: >1000ms |
| `netdata_k6_high_response_time_p99` | K6 Response Time p99 Check | WARNING: >2s, CRITICAL: >5s |
| `netdata_k6_failed_checks` | K6 Checks Failed Counter | WARNING: >0, CRITICAL: >5 |

**Nota:** Las métricas de K6 se obtienen desde Pushgateway (puerto 9091)

---

## 📬 CONFIGURACIÓN DE NOTIFICACIONES

### Grupos de Contacto

| Netdata `to:` | CheckMK Contact Group | Email |
|---------------|----------------------|-------|
| `sysadmin` | sysadmin | sysadmin@ensurance-pharmacy.local |
| `webmaster` | webmaster | webmaster@ensurance-pharmacy.local |

### Canales de Notificación

| Canal | Netdata | CheckMK | Estado |
|-------|---------|---------|--------|
| Email | ✅ Configurado | ✅ Configurado | Listo |
| Slack | ✅ Configurado | ⚠️ Requiere Webhook URL | Pendiente configuración manual |

---

## 🔗 INTEGRACIÓN CON PROMETHEUS

CheckMK se conecta a las mismas fuentes de métricas que Netdata:

| Fuente | URL | Descripción |
|--------|-----|-------------|
| Prometheus | http://prometheus:9090/metrics | Servidor principal de métricas |
| Pushgateway | http://pushgateway:9091/metrics | K6 y Jenkins metrics |
| RabbitMQ | http://rabbitmq:15692/metrics | Métricas de message broker |
| Node Exporter | http://node-exporter:9100/metrics | Métricas del sistema host |
| Backv5 | http://ensurance-pharmacy-full:9464/metrics | Pharmacy Backend |
| Backv4 | http://ensurance-pharmacy-full:9465/metrics | Ensurance Backend |
| Frontend Ensurance | http://ensurance-pharmacy-full:9466/metrics | Ensurance Frontend |
| Frontend Pharmacy | http://ensurance-pharmacy-full:9467/metrics | Pharmacy Frontend |

---

## 🚀 CÓMO USAR CHECKMK

### 1. Acceder a CheckMK

```
URL:      http://localhost:5152/ensurance/check_mk/
Usuario:  cmkadmin
Password: admin123
```

### 2. Ver Hosts Monitoreados

1. Ir a **Monitor > Hosts**
2. Verás todos los hosts configurados:
   - prometheus
   - grafana
   - alertmanager
   - rabbitmq
   - netdata
   - node-exporter
   - pushgateway
   - ensurance-app

### 3. Ver Servicios por Host

1. Click en cualquier host
2. Verás todos los servicios descubiertos (CPU, Memory, Disk, HTTP, etc.)

### 4. Ver Problemas Actuales

1. Ir a **Monitor > Problems**
2. Aquí verás todas las alertas activas (equivalente al panel de alertas de Netdata)

### 5. Crear Dashboards

1. Ir a **Customize > Dashboards**
2. Crear dashboards personalizados con las métricas que necesites

### 6. Configurar Slack (Opcional)

1. Ir a **Setup > Notifications**
2. Agregar Slack webhook URL
3. Configurar reglas de notificación para Slack

---

## 📝 DIFERENCIAS PRINCIPALES

| Aspecto | Netdata | CheckMK |
|---------|---------|---------|
| **Tipo** | Monitoreo en tiempo real | Monitoreo enterprise con checks periódicos |
| **Interfaz** | Dashboard automático | Dashboard configurable |
| **Alertas** | Basadas en archivos .conf | Basadas en reglas Python |
| **Granularidad** | Cada segundo | Cada 1-5 minutos (configurable) |
| **Historial** | Limitado (3 días) | Extenso (configurable, semanas/meses) |
| **Notificaciones** | Email + Slack | Email + Slack + SMS + PagerDuty + más |
| **API** | REST API básica | REST API completa y extensa |
| **Gestión** | Automática | Requiere configuración |

---

## ✅ VENTAJAS DE TENER AMBOS

1. **Netdata**: Monitoreo en tiempo real, ideal para troubleshooting inmediato
2. **CheckMK**: Monitoreo enterprise, ideal para análisis histórico y reportes
3. **Complementarios**: Netdata captura todo, CheckMK organiza y gestiona
4. **Redundancia**: Si uno falla, el otro sigue funcionando

---

## 🔧 COMANDOS ÚTILES

### Verificar status de CheckMK
```bash
docker exec ensurance-checkmk-full omd status ensurance
```

### Ver logs de CheckMK
```bash
docker exec ensurance-checkmk-full omd su ensurance -c "tail -f ~/var/log/cmc.log"
```

### Recargar configuración
```bash
docker exec ensurance-checkmk-full omd su ensurance -c "cmk -R"
```

### Descubrir servicios en un host
```bash
docker exec ensurance-checkmk-full omd su ensurance -c "cmk -II hostname"
```

### Activar cambios
```bash
docker exec ensurance-checkmk-full omd su ensurance -c "cmk -O"
```

---

## 📚 RECURSOS ADICIONALES

- [Documentación oficial de CheckMK](https://docs.checkmk.com/)
- [CheckMK REST API](https://docs.checkmk.com/latest/en/rest_api.html)
- [Integración con Prometheus](https://docs.checkmk.com/latest/en/monitoring_prometheus.html)

---

**Última actualización:** $(date)
**Versión CheckMK:** 2.4.0p12
**Ambiente:** Ensurance Pharmacy - Production Monitoring
