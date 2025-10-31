# ⏰ Frecuencia de Notificaciones - Ensurance Pharmacy

**Última actualización:** $(date '+%Y-%m-%d %H:%M:%S')

---

## ✅ Configuración Actual Aplicada

### 🔴 **CRÍTICAS - Aplicaciones Caídas** (Solo estas 4 alertas)
**Repeat Interval:** Cada **2 horas**

Alertas afectadas:
- `PharmacyBackendDown`
- `EnsuranceBackendDown`
- `PharmacyFrontendDown`
- `EnsuranceFrontendDown`

**Razón:** Estas alertas se disparan frecuentemente cuando los servicios están caídos. 
Para evitar spam de emails, solo se notifica cada 2 horas.

---

### 🔴 **CRÍTICAS - Todas las Demás**
**Repeat Interval:** Cada **5 minutos**

Ejemplos:
- `CriticalMemoryUsage` (>90% memoria)
- `CriticalDiskUsage` (>90% disco)
- `CriticalCPUUsage` (>90% CPU)
- `K6HighErrorRate` (>5% errores)
- `RabbitMQDown`
- Etc.

**Total:** ~15 alertas críticas adicionales

---

### ⚠️ **WARNING**
**Repeat Interval:** Cada **1 hora**

Ejemplos:
- `HighMemoryUsage` (>80% memoria)
- `HighDiskUsage` (>80% disco)
- `DiskAlmostFull` (>85% disco)
- `TargetDown` (servicio no responde)
- `PrometheusSlowScrapes`
- `JenkinsBuildFailed`
- Etc.

**Total:** ~42 alertas

---

### ℹ️ **INFO**
**Repeat Interval:** Cada **6 horas**

Ejemplos:
- `K6HighVirtualUsers` (>100 VUs)
- `K6HighRequestRate` (>10k req/min)

**Total:** ~3 alertas

---

## 📊 Resumen por Tiempo

| Severidad | Alertas Específicas | Repeat Interval | Cantidad |
|-----------|---------------------|-----------------|----------|
| 🔴 CRITICAL | Aplicaciones caídas | **2 horas** | 4 |
| 🔴 CRITICAL | Otras críticas | **5 minutos** | ~15 |
| ⚠️ WARNING | Todas | **1 hora** | ~42 |
| ℹ️ INFO | Todas | **6 horas** | ~3 |

---

## 🔍 Cómo Funciona

### Ejemplo 1: Aplicación Caída (2 horas)

```
00:00 - EnsuranceBackendDown se dispara
00:00 - 📧 Email + 💬 Slack ENVIADOS (Primera notificación)
00:05 - La alerta sigue activa, pero NO se envía nada
01:00 - La alerta sigue activa, pero NO se envía nada
02:00 - 📧 Email + 💬 Slack ENVIADOS (Segunda notificación)
04:00 - 📧 Email + 💬 Slack ENVIADOS (Tercera notificación)
```

### Ejemplo 2: Memoria Crítica (5 minutos)

```
00:00 - CriticalMemoryUsage se dispara
00:00 - 📧 Email + 💬 Slack ENVIADOS (Primera notificación)
00:05 - 📧 Email + 💬 Slack ENVIADOS (Segunda notificación)
00:10 - 📧 Email + 💬 Slack ENVIADOS (Tercera notificación)
00:15 - 📧 Email + 💬 Slack ENVIADOS (Cuarta notificación)
...continúa cada 5 minutos mientras esté activa
```

### Ejemplo 3: Disco Lleno (1 hora)

```
00:00 - HighDiskUsage se dispara
00:00 - 📧 Email + 💬 Slack ENVIADOS (Primera notificación)
00:30 - La alerta sigue activa, pero NO se envía nada
01:00 - 📧 Email + 💬 Slack ENVIADOS (Segunda notificación)
02:00 - 📧 Email + 💬 Slack ENVIADOS (Tercera notificación)
```

---

## 🎯 Tu Situación Actual

**Problema anterior:** Todas las alertas críticas se enviaban cada 2 horas.

**Solución aplicada:** ✅
- Solo las 4 alertas de aplicaciones caídas se envían cada 2 horas
- Todas las demás alertas críticas se envían cada 5 minutos
- Warnings cada 1 hora
- Info cada 6 horas

---

## 📧 Próximas Notificaciones Esperadas

Basado en las alertas activas actuales:

### Aplicaciones Caídas (Cada 2 horas)
- PharmacyBackendDown
- EnsuranceBackendDown
- PharmacyFrontendDown
- EnsuranceFrontendDown

**Última notificación:** ~00:24 UTC  
**Próxima notificación:** ~02:24 UTC (en ~1h 20min)

### Warnings (Cada 1 hora)
- TargetDown (5 instancias)
- PrometheusSlowScrapes
- DiskAlmostFull

**Última notificación:** ~00:24 UTC  
**Próxima notificación:** ~01:24 UTC (en ~20 min)

---

## 🧪 Cómo Probar

### Generar alerta crítica que se envíe CADA 5 MINUTOS:

```bash
# Crear alta carga de memoria (no es aplicación caída)
stress-ng --vm 4 --vm-bytes 90% --timeout 600s &

# Esperar 2-3 minutos para que se dispare
# Recibirás notificaciones cada 5 minutos
```

### Generar alerta de aplicación caída (cada 2 horas):

```bash
# Detener un servicio
docker compose -f docker-compose.full.yml stop ensurance-pharmacy-monitoring

# Esperar 1-2 minutos para que se dispare
# Solo recibirás 1 notificación, la próxima en 2 horas
```

---

## 📋 Configuración Técnica

### Archivo de Configuración
`monitoring/alertmanager/alertmanager.yml.template`

```yaml
routes:
  # Aplicaciones caídas - Solo estas 4
  - match_re:
      alertname: '(PharmacyBackendDown|EnsuranceBackendDown|EnsuranceFrontendDown|PharmacyFrontendDown)'
    receiver: 'critical-notifications'
    repeat_interval: 2h  # ← Solo estas 4 alertas
    continue: false      # ← No evalúa siguientes rutas
  
  # Todas las demás críticas
  - match:
      severity: critical
    receiver: 'critical-notifications'
    repeat_interval: 5m  # ← Resto de críticas
  
  # Warnings
  - match:
      severity: warning
    receiver: 'warning-notifications'
    repeat_interval: 1h
  
  # Info
  - match:
      severity: info
    receiver: 'info-notifications'
    repeat_interval: 6h
```

### Aplicar Cambios

```bash
# Si modificas la configuración
./setup-alertmanager-secrets.sh
```

---

## 💡 Recomendaciones

### Para Testing:
Si quieres probar que los emails llegan rápido, puedes reducir temporalmente los intervalos:

```yaml
repeat_interval: 2h  → repeat_interval: 5m   # Para aplicaciones caídas
repeat_interval: 5m  → repeat_interval: 2m   # Para otras críticas  
repeat_interval: 1h  → repeat_interval: 5m   # Para warnings
```

**Recuerda volver a los valores normales después del testing.**

### Para Producción:
Los valores actuales son apropiados:
- ✅ Aplicaciones caídas cada 2h (evita spam)
- ✅ Otras críticas cada 5min (respuesta rápida)
- ✅ Warnings cada 1h (balance)
- ✅ Info cada 6h (solo informativo)

---

## 🔍 Verificar Configuración Activa

```bash
# Ver configuración en Alertmanager
docker compose -f docker-compose.full.yml exec alertmanager \
  cat /etc/alertmanager/alertmanager.yml | grep -A 25 "routes:"

# Ver alertas activas
curl -s http://localhost:9090/api/v1/alerts | \
  jq '.data.alerts[] | {alert: .labels.alertname, severity: .labels.severity, state: .state}'

# Ver logs de notificaciones
docker compose -f docker-compose.full.yml logs -f alertmanager | \
  grep -i "notify"
```

---

## ✅ Confirmación

**Estado:** Configuración aplicada correctamente ✓  
**Fecha:** $(date)

**Próximos pasos:**
1. Las nuevas alertas críticas (que no sean aplicaciones caídas) se enviarán cada 5 minutos
2. Las alertas de aplicaciones caídas se enviarán cada 2 horas
3. Puedes probar generando una alerta de memoria o disco para confirmar
