# 📧 Guía de Emails Personalizados por Alerta

## 🎯 Problema Resuelto

**Antes:** Todas las alertas enviaban el mismo email genérico  
**Ahora:** Cada alerta envía un email con información específica y personalizada

---

## ✨ Mejoras Implementadas

### 1. **Asuntos Personalizados**

Cada alerta tiene su propio asunto con el nombre específico:

**Antes:**
```
[Ensurance Pharmacy] Alerta de Monitoreo
```

**Ahora:**
```
🔴 [CRÍTICO] PharmacyBackendDown - Backend de Pharmacy caído
⚠️ [WARNING] HighRAMUsage - Alto uso de RAM en servidor
ℹ️ [INFO] K6HighRequestRate - Alta carga de pruebas K6
```

### 2. **Contenido Específico por Alerta**

Cada email muestra:
- ✅ **Nombre de la alerta**: El nombre exacto (ej: HighRAMUsage, CriticalCPUUsage)
- ✅ **Resumen personalizado**: De la annotation `summary` de cada alerta
- ✅ **Descripción específica**: De la annotation `description` con detalles
- ✅ **Acción recomendada**: De la annotation `action` con pasos a seguir
- ✅ **Servicio afectado**: Sistema, RabbitMQ, Jenkins, etc.
- ✅ **Componente específico**: CPU, RAM, queue, build, etc.
- ✅ **Instancia**: El servidor/contenedor exacto
- ✅ **Dashboard específico**: Link directo al dashboard relevante

### 3. **Diseño Visual Diferenciado**

#### 🔴 Emails CRÍTICOS
- Banner rojo con efecto pulsante
- Texto grande y urgente
- Sección destacada de "ACCIÓN INMEDIATA"
- Timeline detallada
- Enlaces rápidos a todos los dashboards

**Ejemplo: PharmacyBackendDown**
```
┌─────────────────────────────────┐
│  ⚠️ ALERTA CRÍTICA ⚠️          │
│  PharmacyBackendDown           │  ← Banner rojo pulsante
└─────────────────────────────────┘

🚨 Información de la Alerta
ESTADO: FIRING
SEVERIDAD: CRÍTICO
ALERTA: PharmacyBackendDown
SERVICIO AFECTADO: pharmacy

📝 ¿Qué está pasando?
Backend de Pharmacy caído
El backend de Pharmacy no responde desde hace 1 minuto

⚡ ACCIÓN INMEDIATA REQUERIDA
URGENTE: Reiniciar contenedor ensurance-pharmacy-full

🕐 Timeline
INICIO: Monday, 31 Oct 2025 02:55:30 UTC
TIEMPO ACTIVA: 2 minutos

📊 Enlaces Rápidos
🔗 Dashboard de Monitoreo
🔗 Ver en Prometheus
🔗 Alertmanager
🔗 Netdata
🔗 Grafana
```

#### ⚠️ Emails WARNING
- Banner naranja
- Diseño más suave pero claro
- Sección de "Acción Recomendada"
- Información completa pero menos urgente

**Ejemplo: HighRAMUsage**
```
┌─────────────────────────────────┐
│  ⚠️ HighRAMUsage               │  ← Banner naranja
│  Advertencia de Monitoreo      │
└─────────────────────────────────┘

📋 Información
Estado: FIRING
Severidad: WARNING
Alerta: HighRAMUsage
Servicio: system
Componente: ram
Instancia: localhost:9100

💬 Descripción
Alto uso de RAM en localhost:9100
El uso de RAM está en 65% (umbral: 60%)

📝 Acción Recomendada
Monitorear consumo de memoria de aplicaciones

🕐 Timeline
Inicio: 2025-10-31 02:55:30
```

#### ℹ️ Emails INFO
- Banner azul claro
- Diseño simple e informativo
- Solo información esencial

**Ejemplo: K6HighRequestRate**
```
┌─────────────────────────────────┐
│  ℹ️ K6HighRequestRate          │  ← Banner azul
│  Notificación Informativa      │
└─────────────────────────────────┘

Alerta: K6HighRequestRate
Servicio: k6-testing
Alta tasa de requests en K6
Test ejecutándose con alta carga
Inicio: 2025-10-31 02:55:30
```

---

## 📋 Información Incluida en Cada Email

### Todos los emails incluyen:

1. **Encabezado con nombre de alerta**
   - Icono según severidad (🔴 ⚠️ ℹ️)
   - Nombre completo de la alerta

2. **Sección de Información Básica**
   - Estado (FIRING / RESOLVED)
   - Severidad
   - Nombre de alerta
   - Servicio afectado
   - Componente (si aplica)
   - Instancia (si aplica)

3. **Descripción Detallada**
   - Resumen (summary)
   - Descripción completa (description)
   - Contexto específico de la alerta

4. **Acción Recomendada**
   - Pasos específicos a seguir
   - Comandos o acciones concretas

5. **Timeline**
   - Hora de inicio
   - Duración (si está activa)
   - Hora de resolución (si se resolvió)

6. **Enlaces Útiles**
   - Dashboard específico de la alerta
   - Prometheus
   - Alertmanager
   - Otros dashboards relevantes

7. **Detalles Técnicos**
   - Todas las labels de Prometheus
   - Valores exactos
   - Metadatos adicionales

---

## 🎨 Ejemplos Reales

### Ejemplo 1: CriticalMemoryUsage (CRÍTICO)

**Asunto:**
```
🔴 [CRÍTICO] CriticalMemoryUsage - MEMORIA CRÍTICA en localhost:9100
```

**Contenido:**
```html
┌──────────────────────────────────────────┐
│      ⚠️ ALERTA CRÍTICA ⚠️               │
│      CriticalMemoryUsage                │
└──────────────────────────────────────────┘

🚨 ESTADO: FIRING
SEVERIDAD: CRÍTICO
SERVICIO: system
COMPONENTE: memory

📝 ¿Qué está pasando?
MEMORIA CRÍTICA en localhost:9100
El uso de memoria está en 96% (umbral crítico: 95%)

⚡ ACCIÓN INMEDIATA REQUERIDA
URGENTE: Riesgo de OOM killer, reiniciar servicios

🕐 INICIO: Monday, 31 Oct 2025 02:55:30 UTC
TIEMPO ACTIVA: 5 minutos

📊 Ver en:
• Dashboard: http://localhost:19999
• Prometheus: http://localhost:9090/alerts
```

### Ejemplo 2: RabbitMQQueueMessagesHigh (WARNING)

**Asunto:**
```
⚠️ [WARNING] RabbitMQQueueMessagesHigh - Cola con muchos mensajes: orders_queue
```

**Contenido:**
```html
┌──────────────────────────────────────────┐
│    ⚠️ RabbitMQQueueMessagesHigh         │
│    Advertencia de Monitoreo             │
└──────────────────────────────────────────┘

📋 Información
Estado: FIRING
Severidad: WARNING
Servicio: rabbitmq
Componente: queue
Cola: orders_queue

💬 Descripción
Cola con muchos mensajes: orders_queue
La cola tiene 1523 mensajes (umbral: 1000)

📝 Acción Recomendada
Revisar consumers y procesamiento de mensajes

🕐 Inicio: 2025-10-31 02:55:30

📊 Dashboard: http://localhost:15674/#/queues
```

### Ejemplo 3: JenkinsBuildFailed (WARNING)

**Asunto:**
```
⚠️ [WARNING] JenkinsBuildFailed - Build fallido en Jenkins: deploy-production
```

**Contenido:**
```html
┌──────────────────────────────────────────┐
│    ⚠️ JenkinsBuildFailed                │
│    Advertencia de Monitoreo             │
└──────────────────────────────────────────┘

📋 Información
Estado: FIRING
Alerta: JenkinsBuildFailed
Servicio: jenkins-ci
Job: deploy-production
Build: #456

💬 Descripción
Build fallido en Jenkins: deploy-production
El build #456 ha fallado

📝 Acción Recomendada
Revisar logs del build y corregir errores

📊 Dashboard: http://localhost:8080/jenkins/job/deploy-production/456
```

---

## 🚀 Cómo Aplicar

### Paso 1: Aplicar la Nueva Configuración

```bash
chmod +x aplicar-emails-personalizados.sh
./aplicar-emails-personalizados.sh
```

El script:
1. ✅ Crea backup de configuración actual
2. ✅ Aplica nueva configuración personalizada
3. ✅ Reinicia Alertmanager
4. ✅ Verifica que funciona

### Paso 2: Probar los Emails

```bash
# Opción 1: Enviar email de prueba
./test-email-rapido.sh

# Opción 2: Probar alertas específicas
./test-alertas-interactivo.sh
```

### Paso 3: Verificar Recepción

1. **Revisa tu inbox:**
   - pablopolis2016@gmail.com
   - jflores@unis.edu.gt

2. **Verifica el asunto:**
   - Debe incluir el nombre específico de la alerta
   - Ej: "🔴 [CRÍTICO] TestEmailAlert - ..."

3. **Revisa el contenido:**
   - Diseño HTML rico
   - Información específica de la alerta
   - Enlaces funcionales

---

## 📊 Comparación Antes vs Ahora

### ANTES (Email Genérico)

**Asunto:**
```
[Ensurance Pharmacy] Alerta de Monitoreo
```

**Contenido:**
```
Alerta de Monitoreo - Ensurance Pharmacy

Estado: firing
Severidad: warning
Alerta: HighRAMUsage
Descripción: El uso de RAM está en 65%
Resumen: Alto uso de RAM
Tiempo: 2025-10-31T02:55:30Z

Sistema de Monitoreo Automático
```

### AHORA (Email Personalizado)

**Asunto:**
```
⚠️ [WARNING] HighRAMUsage - Alto uso de RAM en localhost:9100
```

**Contenido:**
```html
[Banner naranja con gradiente]

⚠️ HighRAMUsage
Advertencia de Monitoreo

┌─────────────────────────────┐
│ 📋 Información              │
├─────────────────────────────┤
│ Estado: FIRING              │
│ Severidad: WARNING          │
│ Alerta: HighRAMUsage        │
│ Servicio: system            │
│ Componente: ram             │
│ Instancia: localhost:9100   │
└─────────────────────────────┘

┌─────────────────────────────┐
│ 💬 Descripción              │
├─────────────────────────────┤
│ Alto uso de RAM             │
│ El uso de RAM está en 65%   │
│ (umbral: 60%)               │
└─────────────────────────────┘

┌─────────────────────────────┐
│ 📝 Acción Recomendada       │
├─────────────────────────────┤
│ Monitorear consumo de       │
│ memoria de aplicaciones     │
└─────────────────────────────┘

🕐 Inicio: 2025-10-31 02:55:30
📊 Dashboard: http://localhost:19999

[Footer negro con logo]
```

---

## 🔧 Personalización Adicional

### Modificar Templates

Si deseas personalizar aún más los emails, edita:
```
monitoring/alertmanager/alertmanager-personalizado.yml
```

### Cambiar Colores

**Critical (Rojo):**
```css
background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);
```

**Warning (Naranja):**
```css
background: linear-gradient(135deg, #ff9800 0%, #ff6f00 100%);
```

**Info (Azul):**
```css
background: linear-gradient(135deg, #17a2b8 0%, #138496 100%);
```

### Agregar Más Información

Los templates tienen acceso a:
- `{{ .CommonLabels.nombre_label }}` - Cualquier label
- `{{ .CommonAnnotations.nombre_annotation }}` - Cualquier annotation
- `{{ .StartsAt }}` - Hora de inicio
- `{{ .EndsAt }}` - Hora de fin
- `{{ .Status }}` - Estado (firing/resolved)

---

## 💡 Consejos

### 1. Verificar Annotations en Alertas

Asegúrate de que cada alerta en Prometheus tiene:
```yaml
annotations:
  summary: "Resumen corto y claro"
  description: "Descripción detallada con contexto"
  action: "Pasos específicos a seguir"
  dashboard: "http://link-al-dashboard"
```

### 2. Probar con Diferentes Alertas

Prueba con alertas de diferentes severidades:
- CRITICAL: `./test-alertas-interactivo.sh` → Opción 1 → Test de CPU crítica
- WARNING: `./test-alertas-interactivo.sh` → Opción 1 → Test de RAM
- INFO: Espera alertas de K6 durante pruebas de carga

### 3. Revisar Logs

Si algo no funciona:
```bash
docker logs -f ensurance-alertmanager-full | grep -i email
docker logs -f ensurance-alertmanager-full | grep -i smtp
```

---

## 🐛 Solución de Problemas

### Email no llega

1. **Verificar que Alertmanager está corriendo:**
```bash
curl http://localhost:9094/api/v1/status
```

2. **Ver logs de envío:**
```bash
docker logs ensurance-alertmanager-full --tail 50 | grep -i "notify"
```

3. **Verificar App Password de Gmail**
   - Debe ser una "App Password" válida
   - No la contraseña normal

### Email llega pero sin formato

1. **Cliente de email no soporta HTML**
   - Gmail, Outlook web: ✅ Soportan HTML
   - Algunos clientes de terminal: ❌ No soportan

2. **Verificar que el email está en HTML:**
```bash
# Ver configuración
docker exec ensurance-alertmanager-full cat /etc/alertmanager/alertmanager.yml | grep -A 50 "html:"
```

### Slack no muestra formato

Slack tiene limitaciones de formato. Los mensajes de Slack usan Markdown, no HTML.

---

## 📚 Referencias

- **Alertmanager Templates:** https://prometheus.io/docs/alerting/latest/notifications/
- **Template Reference:** https://prometheus.io/docs/alerting/latest/notification_examples/
- **Go Templates:** https://golang.org/pkg/text/template/

---

**Última actualización:** 31 de Octubre, 2025  
**Versión:** 2.0 - Emails Personalizados
