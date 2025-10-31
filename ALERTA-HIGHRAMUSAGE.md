# 🔔 Alerta HighRAMUsage - Primera Alerta del Sistema

## 📋 Información General

- **Nombre:** HighRAMUsage
- **Posición:** #0 (Primera alerta del sistema)
- **Umbral:** RAM > 60%
- **Duración:** 1 minuto
- **Severidad:** Warning
- **Componente:** RAM

## 🎯 Objetivo

Esta alerta se diseñó para ejecutarse **PRIMERO** en el sistema, con un umbral más bajo (60%) que las otras alertas de memoria, permitiendo:

1. ✅ Detectar uso de RAM tempranamente
2. ✅ Enviar notificaciones antes de que sea crítico
3. ✅ Dar tiempo para tomar acción preventiva
4. ✅ Probar que las notificaciones funcionan correctamente

## 📧 Canales de Notificación

Esta alerta envía notificaciones a **AMBOS** canales:

### 1. Gmail (Email)
- **Destinatarios:** 
  - pablopolis2016@gmail.com
  - jflores@unis.edu.gt
- **Asunto:** `⚠️ [WARNING] Alerta de Monitoreo - Ensurance Pharmacy`
- **Contenido:** HTML formateado con detalles de la alerta

### 2. Slack
- **Canal:** #ensurance-alerts
- **Formato:** Mensaje estructurado con emojis
- **Notificación:** Desktop + Mobile (según configuración)

## 🧪 Cómo Probar

### Opción 1: Script Interactivo (Recomendado)
```bash
./test-alertas-interactivo.sh
# Selecciona: 1 (Sistema)
# La primera prueba será HighRAMUsage
```

### Opción 2: Script Completo
```bash
./test-todas-las-alertas-completo.sh
# La primera alerta en ejecutarse será HighRAMUsage
```

### Opción 3: Manual con stress-ng
```bash
# Consumir 65% de RAM por 2 minutos
stress-ng --vm 1 --vm-bytes 65% --timeout 120s

# Esperar 90 segundos
# Verificar alerta en Prometheus: http://localhost:9090
```

## ⏱️ Tiempo Esperado

1. **Inicio de stress-ng:** 0 segundos
2. **RAM > 60%:** 5-10 segundos
3. **Alerta dispara (for: 1m):** 60 segundos
4. **Alertmanager procesa:** 70 segundos
5. **Email/Slack enviado:** 80-90 segundos

**Total:** ~1.5-2 minutos desde inicio hasta recibir notificación

## 🔍 Verificar Resultado

### 1. Verificar en Prometheus
```bash
curl -s http://localhost:9090/api/v1/alerts | jq '.data.alerts[] | select(.labels.alertname=="HighRAMUsage")'
```

### 2. Verificar en Alertmanager
```bash
curl -s http://localhost:9094/api/v1/alerts | jq '.data[] | select(.labels.alertname=="HighRAMUsage")'
```

### 3. Ver logs de envío
```bash
# Ver logs de Alertmanager
docker logs ensurance-alertmanager-full --tail 50 | grep -i "HighRAMUsage"

# Ver logs de envío de email
docker logs ensurance-alertmanager-full --tail 50 | grep -i "smtp"

# Ver logs de envío a Slack
docker logs ensurance-alertmanager-full --tail 50 | grep -i "slack"
```

### 4. Verificar Email
- ✉️ Revisa inbox de pablopolis2016@gmail.com
- ✉️ Revisa inbox de jflores@unis.edu.gt
- ⚠️ **IMPORTANTE:** Revisa carpeta de SPAM

### 5. Verificar Slack
- 💬 Abre Slack
- 💬 Ve al canal #ensurance-alerts
- 💬 Busca mensaje con título "⚠️ WARNING - Ensurance Pharmacy"

## 📊 Expresión de Prometheus

```yaml
expr: (1 - (node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes)) * 100 > 60
```

**Explicación:**
- `node_memory_MemAvailable_bytes`: Memoria disponible (incluye cache/buffers)
- `node_memory_MemTotal_bytes`: Memoria total del sistema
- Calcula porcentaje usado: `(1 - disponible/total) * 100`
- Se dispara cuando > 60%

## 🔧 Configuración

### Archivo de Alerta
`/monitoring/prometheus/rules/system_alerts.yml`

```yaml
- name: system_ram_alerts
  interval: 30s
  rules:
    - alert: HighRAMUsage
      expr: (1 - (node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes)) * 100 > 60
      for: 1m
      labels:
        severity: warning
        service: system
        component: ram
      annotations:
        summary: "Alto uso de RAM en {{ $labels.instance }}"
        description: "El uso de RAM está en {{ $value | humanize }}% (umbral: 60%)"
        dashboard: "http://localhost:19999"
        action: "Monitorear consumo de memoria de aplicaciones"
```

### Configuración de Alertmanager
`/monitoring/alertmanager/alertmanager.yml`

- **Receiver:** warning-notifications
- **Repeat interval:** 1 hora
- **Group by:** alertname, cluster, service
- **Group wait:** 10 segundos

## 🚨 Si No Llegan Notificaciones

### Problema 1: Email no llega

**Diagnóstico:**
```bash
./test-email-alertmanager.sh
```

**Soluciones:**
1. Verificar App Password de Gmail
2. Revisar carpeta SPAM
3. Ver logs de Alertmanager para errores SMTP
4. Verificar conectividad: `telnet smtp.gmail.com 587`

### Problema 2: Slack no llega

**Diagnóstico:**
```bash
# Ver configuración de Slack
docker exec ensurance-alertmanager-full cat /etc/alertmanager/alertmanager.yml | grep -A 10 "slack_configs"
```

**Soluciones:**
1. Verificar que `SLACK_WEBHOOK_URL_AQUI` esté reemplazado con URL real
2. Verificar que el canal #ensurance-alerts exista
3. Probar webhook manualmente:
   ```bash
   curl -X POST "TU_SLACK_WEBHOOK_URL" \
     -H 'Content-Type: application/json' \
     -d '{"text":"Prueba de webhook"}'
   ```

### Problema 3: Alerta no se dispara

**Verificar:**
```bash
# Ver si Prometheus está scrapeando node-exporter
curl -s http://localhost:9090/api/v1/targets | jq '.data.activeTargets[] | select(.labels.job=="node-exporter")'

# Ver valor actual de memoria
curl -s http://localhost:9090/api/v1/query?query=\(1-\(node_memory_MemAvailable_bytes/node_memory_MemTotal_bytes\)\)*100

# Recargar configuración de Prometheus
curl -X POST http://localhost:9090/-/reload
```

## 📈 Diferencia con Otras Alertas de Memoria

| Alerta | Umbral | For | Severidad | Objetivo |
|--------|---------|-----|-----------|----------|
| **HighRAMUsage** | **60%** | **1m** | **warning** | **Detección temprana** |
| HighMemoryUsage | 80% | 2m | warning | Memoria alta |
| CriticalMemoryUsage | 95% | 1m | critical | Memoria crítica |

## 🔄 Flujo de Alerta

```
1. Uso RAM > 60%
   ↓
2. Prometheus detecta (cada 30s)
   ↓
3. Espera 1 minuto (for: 1m)
   ↓
4. Si persiste, dispara alerta
   ↓
5. Envía a Alertmanager
   ↓
6. Alertmanager agrupa (10s)
   ↓
7. Envía a receivers:
   - Gmail (SMTP)
   - Slack (Webhook)
   ↓
8. Usuario recibe notificación
```

## 📝 Notas Importantes

1. ✅ Esta alerta es la **#0** - se ejecuta PRIMERO
2. ✅ Umbral bajo (60%) para detección temprana
3. ✅ Envía a Gmail Y Slack simultáneamente
4. ✅ Se repite cada 1 hora si persiste
5. ⚠️ Slack requiere configurar webhook URL
6. ⚠️ Gmail requiere App Password válida

## 🚀 Quick Start

Para probar rápidamente esta alerta:

```bash
# 1. Ejecutar script interactivo
./test-alertas-interactivo.sh

# 2. Seleccionar opción 1 (Sistema)

# 3. Esperar ~2 minutos

# 4. Verificar email y Slack

# 5. Ver logs si no llega
docker logs -f ensurance-alertmanager-full
```

## 📞 Soporte

Si la alerta no funciona:
1. Ejecuta: `./test-email-alertmanager.sh`
2. Revisa logs: `docker logs ensurance-alertmanager-full --tail 100`
3. Verifica Slack webhook está configurado
4. Consulta: GUIA-PRUEBA-CORREOS-ALERTAS.md

---

**Última actualización:** 31 de Octubre, 2025
