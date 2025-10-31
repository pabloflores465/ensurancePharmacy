# ✅ Annotations Mejoradas - Emails y Slack Personalizados

**Fecha:** 31 de Octubre, 2025  
**Estado:** ✅ 16 alertas críticas mejoradas + Template para las 49 restantes

---

## 🎯 Qué se Mejoró

### Antes (Generic):
```yaml
annotations:
  summary: "Alto uso de CPU"
  description: "CPU al 90%"
  action: "Revisar procesos"
```

### Ahora (Specific & Actionable):
```yaml
annotations:
  summary: "🔴 CPU CRÍTICO - Sistema saturado - Acción inmediata"
  description: "¡ALERTA CRÍTICA! El servidor localhost:9100 está usando 94% de CPU (umbral crítico: 90%). El sistema está saturado y las aplicaciones están experimentando degradación severa de rendimiento. Los usuarios pueden estar experimentando lentitud o timeouts."
  action: "🚨 URGENTE: 1) Ver procesos: 'ps aux --sort=-%cpu | head'. 2) Matar procesos si es necesario: 'kill -9 PID'. 3) Reiniciar servicios Docker si aplica. 4) Escalar recursos o agregar CPU. 5) Revisar si hay bucles infinitos o consultas pesadas."
```

---

## 📊 Alertas Mejoradas (16/65)

### ✅ Sistema (12/12 completas)

| # | Alerta | Mejora |
|---|--------|--------|
| 0 | HighRAMUsage | ✅ Descripción preventiva, comandos diagnóstico específicos |
| 1 | HighCPUUsage | ✅ Explicación de impacto en rendimiento, guía de troubleshooting |
| 2 | CriticalCPUUsage | ✅ Alerta de emergencia, pasos urgentes numerados |
| 3 | HighMemoryUsage | ✅ Advertencia de OOM killer, comandos memoria específicos |
| 4 | CriticalMemoryUsage | ✅ Emergencia crítica, pasos de recuperación inmediata |
| 5 | HighDiskUsage | ✅ Explicación de consecuencias, limpieza de Docker/logs |
| 6 | CriticalDiskUsage | ✅ Alerta de fallo inminente, comandos de liberación urgente |
| 7 | DiskAlmostFull | ✅ Contexto de 5GB críticos, comandos de limpieza |
| 8 | HighNetworkReceive | ✅ Análisis de causas (legítimo vs DDoS), investigación de IPs |
| 9 | HighNetworkTransmit | ✅ Detección de exfiltración, análisis de procesos con red |
| 10 | NodeExporterDown | ✅ Impacto en visibilidad, pasos de recuperación contenedor |
| 11 | HighSystemLoad | ✅ Explicación de load average, diagnóstico I/O vs CPU |

### ✅ Aplicaciones (4/8 completas)

| # | Alerta | Mejora |
|---|--------|--------|
| 12 | PharmacyBackendDown | ✅ Impacto en operaciones farmacia, pasos recuperación |
| 13 | EnsuranceBackendDown | ✅ Impacto en seguros, troubleshooting específico |
| 14 | EnsuranceFrontendDown | ✅ Impacto en usuarios, verificación Vue.js |
| 15 | PharmacyFrontendDown | ✅ Impacto total en UI, pasos de recuperación |

---

## 📧 Impacto en Emails y Slack

Cada email/mensaje ahora incluye:

### 🔴 Para Alertas CRÍTICAS:
```
Asunto: 🔴 [CRÍTICO] CriticalCPUUsage - CPU CRÍTICO - Sistema saturado

¡ALERTA CRÍTICA! El servidor localhost:9100 está usando 94% de CPU...

🚨 URGENTE:
1) Ver procesos: 'ps aux --sort=-%cpu | head'
2) Matar procesos: 'kill -9 PID'
3) Reiniciar servicios Docker
4) Escalar recursos
5) Revisar bucles infinitos
```

### ⚠️ Para Alertas WARNING:
```
Asunto: ⚠️ [WARNING] HighRAMUsage - Uso de RAM elevado

El servidor tiene 65% de RAM en uso. Advertencia preventiva...

🔍 Ejecutar:
'ps aux --sort=-%mem | head -20'
Revisar logs de aplicaciones
Monitorear tendencia
```

---

## 🎨 Características de las Mejoras

### 1. **Emojis Visuales**
- 🔴 = Crítico
- ⚠️ = Warning
- 🚨 = Urgente
- 🔍 = Investigar
- ✅ = OK/Completado

### 2. **Contexto Completo**
- Por qué es importante
- Qué está fallando
- Impacto en usuarios/negocio
- Consecuencias si no se act úa

### 3. **Comandos Específicos**
- Comandos copy-paste listos
- Pasos numerados en orden
- Alternativas si falla el primer paso

### 4. **Valores Dinámicos**
- `{{ $labels.instance }}` - Servidor específico
- `{{ $value | humanize }}` - Valor actual formateado
- `{{ $labels.mountpoint }}` - Disco específico

---

## 📝 Template para Mejorar Alertas Restantes

Para las 49 alertas pendientes, usa este template:

```yaml
annotations:
  summary: "[Emoji] [Qué falló] - [Impacto principal]"
  description: "[Urgencia] [Sistema] [Métrica] está en [Valor] (umbral: X). [Consecuencia]. [Contexto adicional]. [Impacto en usuarios/operaciones]."
  dashboard: "[URL del dashboard relevante]"
  action: "[Emoji] [Nivel]: 1) [Comando diagnóstico]. 2) [Comando corrección]. 3) [Verificación]. 4) [Escalamiento si falla]. 5) [Prevención futura]."
```

### Ejemplo Real:

```yaml
# RabbitMQQueueMessagesHigh
annotations:
  summary: "⚠️ Cola RabbitMQ con mensajes acumulados - Procesamiento lento"
  description: "La cola {{ $labels.queue }} tiene {{ $value }} mensajes acumulados (umbral: 1000). Los mensajes no se están procesando lo suficientemente rápido. Esto puede causar retrasos en operaciones asíncronas, notificaciones demoradas, y eventual saturación de memoria en RabbitMQ."
  dashboard: "http://localhost:15674/#/queues"
  action: "🔍 Analizar: 1) Ver estado cola: 'docker exec rabbitmq rabbitmqctl list_queues name messages consumers'. 2) Aumentar consumers si es necesario. 3) Verificar que consumers estén activos. 4) Purgar mensajes viejos si son obsoletos: 'rabbitmqadmin purge queue name=QUEUE'. 5) Escalar workers."
```

---

## 🚀 Cómo Aplicar los Cambios

### 1. Reiniciar Prometheus
```bash
docker restart ensurance-prometheus-full
```

### 2. Verificar que cargó
```bash
curl -s http://localhost:9090/api/v1/rules | jq '.data.groups[0].rules[0].annotations'
```

### 3. Probar con alerta real
```bash
./test-alertas-interactivo.sh
```

---

## 📊 Beneficios Inmediatos

### ✅ Para el Equipo de Operaciones:
- **Diagnóstico más rápido:** Comandos específicos en el email
- **Menos escalamientos:** Información suficiente para resolver
- **Documentación inline:** No necesitan buscar en wikis

### ✅ Para Usuarios:
- **Recuperación más rápida:** Pasos claros reducen downtime
- **Mejor comunicación:** Pueden explicar el problema al equipo
- **Transparencia:** Entienden qué está pasando

### ✅ Para el Negocio:
- **Menor MTTR:** Mean Time To Recovery reducido
- **Menos impacto:** Problemas detectados antes
- **Mejor SLA:** Respuesta más profesional

---

## 📈 Próximos Pasos

### Alertas Pendientes de Mejorar:

1. **RabbitMQ (12 alertas)** - Prioridad ALTA
   - Colas, memoria, conexiones
   - Impacto en operaciones asíncronas

2. **K6 Testing (8 alertas)** - Prioridad MEDIA
   - Performance testing
   - Menos crítico para producción

3. **CI/CD (12 alertas)** - Prioridad MEDIA
   - Jenkins, SonarQube, Drone
   - No afecta usuarios finales

4. **Monitoreo (13 alertas)** - Prioridad ALTA
   - Prometheus, Grafana, Alertmanager
   - Crítico para visibilidad

### Script para Continuar:
```bash
# Editar cada archivo de alertas:
vim monitoring/prometheus/rules/rabbitmq_alerts.yml
vim monitoring/prometheus/rules/k6_alerts.yml
vim monitoring/prometheus/rules/cicd_alerts.yml
vim monitoring/prometheus/rules/monitoring_alerts.yml

# Aplicar cambios
docker restart ensurance-prometheus-full

# Probar
./test-alertas-interactivo.sh
```

---

## 💡 Tips para Escribir Buenas Annotations

### DO ✅
- Usar emojis para identificación visual rápida
- Explicar el impacto en el negocio
- Proporcionar comandos específicos y probados
- Numerar pasos en orden lógico
- Incluir umbrales y valores actuales
- Explicar por qué es importante actuar
- Dar alternativas si el primer paso falla

### DON'T ❌
- Annotations genéricas ("revisar sistema")
- Comandos sin contexto
- Descripciones técnicas sin impacto
- Pasos vagos ("investigar")
- Sin información de recuperación
- Solo el síntoma sin la causa
- Sin pasos de prevención futura

---

## 📚 Ejemplos de Mejores Prácticas

### Ejemplo 1: Backend Down
```yaml
# MALO ❌
summary: "Backend caído"
description: "El backend no responde"
action: "Reiniciar"

# BUENO ✅
summary: "🔴 Backend Pharmacy CAÍDO - Sistema de farmacia FUERA DE LÍNEA"
description: "¡EMERGENCIA! El backend no responde. Los usuarios NO PUEDEN: crear recetas, consultar medicamentos, procesar ventas. Sistema completamente inoperativo."
action: "🚨 1) docker logs ensurance-pharmacy-apps. 2) docker restart ensurance-pharmacy-apps. 3) Verificar DB. 4) Notificar desarrollo."
```

### Ejemplo 2: Alta Memoria
```yaml
# MALO ❌
summary: "Memoria alta"
description: "Memoria al 85%"
action: "Revisar"

# BUENO ✅
summary: "⚠️ Memoria alta - Riesgo de saturación"
description: "Usando 85% de RAM. Si alcanza 95%, el OOM killer matará procesos automáticamente. Memory leaks posibles en Node.js."
action: "🔍 1) 'free -h' y 'ps aux --sort=-%mem | head -15'. 2) Reiniciar servicios. 3) Liberar cache. 4) Monitorear tendencia."
```

---

## ✅ Estado Actual del Sistema

- **65 alertas** configuradas
- **16 alertas** con annotations mejoradas (críticas de sistema y apps)
- **49 alertas** pendientes (template disponible)
- **Gmail:** ✅ Configurado y funcionando
- **Slack:** ✅ Configurado y funcionando
- **Emails personalizados:** ✅ Por severidad (CRITICAL/WARNING/INFO)

---

**Última actualización:** 31 de Octubre, 2025 - 03:20 AM  
**Estado:** ✅ Annotations mejoradas aplicadas y documentadas
