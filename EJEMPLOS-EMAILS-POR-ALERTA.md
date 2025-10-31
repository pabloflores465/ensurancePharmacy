# 📧 Ejemplos de Emails Personalizados por Alerta

Este documento muestra cómo se verán los emails para diferentes alertas específicas.

---

## 🔴 Ejemplos CRITICAL

### 1. PharmacyBackendDown

**Asunto:**
```
🔴 [CRÍTICO] PharmacyBackendDown - ⚠️ Backend de Pharmacy CAÍDO
```

**Email (HTML):**
```
┌────────────────────────────────────────────────────┐
│                                                    │
│          ⚠️ ALERTA CRÍTICA ⚠️                    │
│       PharmacyBackendDown                         │
│                                                    │
└────────────────────────────────────────────────────┘

🚨 Información de la Alerta
───────────────────────────────────────────────────
ESTADO: FIRING
SEVERIDAD: CRÍTICO
ALERTA: PharmacyBackendDown
SERVICIO AFECTADO: pharmacy
PUERTO: 9464
INSTANCIA: localhost:9464

📝 ¿Qué está pasando?
───────────────────────────────────────────────────
⚠️ Backend de Pharmacy CAÍDO

El backend de Pharmacy (backv5) no responde 
desde hace 1 minuto. Las operaciones de farmacia 
están fuera de línea.

⚡ ACCIÓN INMEDIATA REQUERIDA
───────────────────────────────────────────────────
URGENTE: Reiniciar contenedor ensurance-pharmacy-full

docker compose -f docker-compose.full.yml restart ensurance-pharmacy-apps

🕐 Timeline
───────────────────────────────────────────────────
INICIO: Monday, 31 Oct 2025 02:55:30 UTC
TIEMPO ACTIVA: 3 minutos y contando...

📊 Enlaces Rápidos
───────────────────────────────────────────────────
🔗 Netdata: http://localhost:19999
🔗 Prometheus: http://localhost:9090/alerts
🔗 Alertmanager: http://localhost:9094
🔗 Grafana: http://localhost:3302
```

---

### 2. CriticalCPUUsage

**Asunto:**
```
🔴 [CRÍTICO] CriticalCPUUsage - CPU CRÍTICO en servidor
```

**Email:**
```
┌────────────────────────────────────────────────────┐
│          ⚠️ ALERTA CRÍTICA ⚠️                    │
│          CriticalCPUUsage                         │
└────────────────────────────────────────────────────┘

ESTADO: FIRING
SEVERIDAD: CRÍTICO
SERVICIO: system
COMPONENTE: cpu
INSTANCIA: localhost:9100

📝 ¿Qué está pasando?
───────────────────────────────────────────────────
⚠️ CPU CRÍTICO en localhost:9100
El uso de CPU está en 94% (umbral crítico: 90%)

⚡ ACCIÓN INMEDIATA
───────────────────────────────────────────────────
URGENTE: Escalar recursos o detener procesos

# Ver procesos con más CPU
top -o %CPU

# Detener proceso específico
kill -9 <PID>

🕐 INICIO: 2025-10-31 02:55:30
TIEMPO ACTIVA: 2 minutos
```

---

### 3. RabbitMQMemoryAlarm

**Asunto:**
```
🔴 [CRÍTICO] RabbitMQMemoryAlarm - ⚠️ Alarma de memoria activada en RabbitMQ
```

**Email:**
```
┌────────────────────────────────────────────────────┐
│          ⚠️ ALERTA CRÍTICA ⚠️                    │
│        RabbitMQMemoryAlarm                        │
└────────────────────────────────────────────────────┘

ESTADO: FIRING
SEVERIDAD: CRÍTICO
SERVICIO: rabbitmq
COMPONENTE: alarm

📝 ¿Qué está pasando?
───────────────────────────────────────────────────
⚠️ Alarma de memoria activada en RabbitMQ
RabbitMQ ha activado la alarma de memoria.
Los publishers están bloqueados.

⚡ ACCIÓN INMEDIATA
───────────────────────────────────────────────────
URGENTE: Liberar memoria, publishers bloqueados

# Purgar colas no esenciales
docker exec ensurance-rabbitmq-full rabbitmqadmin purge queue name=logs_queue

# Ver uso de memoria
docker exec ensurance-rabbitmq-full rabbitmqctl status

📊 Dashboard: http://localhost:15674/#/nodes
```

---

## ⚠️ Ejemplos WARNING

### 4. HighRAMUsage

**Asunto:**
```
⚠️ [WARNING] HighRAMUsage - Alto uso de RAM en localhost:9100
```

**Email:**
```
┌────────────────────────────────────────────────────┐
│              ⚠️ HighRAMUsage                      │
│          Advertencia de Monitoreo                 │
└────────────────────────────────────────────────────┘

📋 Información
───────────────────────────────────────────────────
Estado: FIRING
Severidad: WARNING
Alerta: HighRAMUsage
Servicio: system
Componente: ram
Instancia: localhost:9100

💬 Descripción
───────────────────────────────────────────────────
Alto uso de RAM en localhost:9100

El uso de RAM está en 65% (umbral: 60%)
Es recomendable monitorear el consumo de memoria
para evitar que alcance niveles críticos.

📝 Acción Recomendada
───────────────────────────────────────────────────
Monitorear consumo de memoria de aplicaciones

# Ver procesos con más memoria
ps aux --sort=-%mem | head -10

# Ver uso de memoria
free -h

🕐 Timeline
───────────────────────────────────────────────────
Inicio: 2025-10-31 02:55:30 UTC

📊 Dashboard: http://localhost:19999
```

---

### 5. JenkinsBuildFailed

**Asunto:**
```
⚠️ [WARNING] JenkinsBuildFailed - Build fallido en Jenkins: deploy-production
```

**Email:**
```
┌────────────────────────────────────────────────────┐
│           ⚠️ JenkinsBuildFailed                   │
│          Advertencia de Monitoreo                 │
└────────────────────────────────────────────────────┘

📋 Información
───────────────────────────────────────────────────
Estado: FIRING
Severidad: WARNING
Alerta: JenkinsBuildFailed
Servicio: jenkins-ci
Componente: build
Job: deploy-production
Build: #456

💬 Descripción
───────────────────────────────────────────────────
Build fallido en Jenkins: deploy-production

El build #456 del job deploy-production ha fallado.
Resultado: FAILURE

📝 Acción Recomendada
───────────────────────────────────────────────────
Revisar logs del build y corregir errores

1. Revisar console output en Jenkins
2. Verificar cambios recientes en el código
3. Comprobar tests fallidos
4. Reintentar build si fue error temporal

🕐 Inicio: 2025-10-31 02:55:30

📊 Ver Build: http://localhost:8080/jenkins/job/deploy-production/456/console
```

---

### 6. RabbitMQQueueMessagesHigh

**Asunto:**
```
⚠️ [WARNING] RabbitMQQueueMessagesHigh - Cola con muchos mensajes: orders_queue
```

**Email:**
```
┌────────────────────────────────────────────────────┐
│      ⚠️ RabbitMQQueueMessagesHigh                 │
│          Advertencia de Monitoreo                 │
└────────────────────────────────────────────────────┘

📋 Información
───────────────────────────────────────────────────
Estado: FIRING
Severidad: WARNING
Servicio: rabbitmq
Componente: queue
Cola: orders_queue
Vhost: /

💬 Descripción
───────────────────────────────────────────────────
Cola con muchos mensajes: orders_queue

La cola tiene 1523 mensajes acumulados (umbral: 1000)
Los mensajes no están siendo consumidos lo 
suficientemente rápido.

📝 Acción Recomendada
───────────────────────────────────────────────────
Revisar consumers y procesamiento de mensajes

# Ver estado de la cola
docker exec ensurance-rabbitmq-full rabbitmqadmin list queues name messages consumers

# Aumentar consumers si es necesario
# Verificar que los consumers están procesando correctamente

📊 Dashboard: http://localhost:15674/#/queues/%2F/orders_queue
```

---

### 7. HighDiskUsage

**Asunto:**
```
⚠️ [WARNING] HighDiskUsage - Alto uso de disco en /dev/sda1
```

**Email:**
```
┌────────────────────────────────────────────────────┐
│            ⚠️ HighDiskUsage                       │
│          Advertencia de Monitoreo                 │
└────────────────────────────────────────────────────┘

📋 Información
───────────────────────────────────────────────────
Estado: FIRING
Severidad: WARNING
Servicio: system
Componente: disk
Dispositivo: /dev/sda1
Punto de montaje: /

💬 Descripción
───────────────────────────────────────────────────
Alto uso de disco en localhost:9100:/

El uso de disco está en 78% (umbral: 75%)
Se recomienda liberar espacio antes de que
alcance el umbral crítico (90%).

📝 Acción Recomendada
───────────────────────────────────────────────────
Limpiar archivos temporales o logs antiguos

# Ver uso de disco
df -h

# Encontrar directorios más grandes
du -h / | sort -rh | head -20

# Limpiar logs antiguos
find /var/log -type f -name "*.log" -mtime +30 -delete

# Limpiar Docker
docker system prune -a --volumes

📊 Dashboard: http://localhost:19999
```

---

## ℹ️ Ejemplos INFO

### 8. K6HighRequestRate

**Asunto:**
```
ℹ️ [INFO] K6HighRequestRate - Alta tasa de requests en K6
```

**Email:**
```
┌────────────────────────────────────────────────────┐
│           ℹ️ K6HighRequestRate                    │
│          Notificación Informativa                 │
└────────────────────────────────────────────────────┘

ℹ️ Información
───────────────────────────────────────────────────
Alerta: K6HighRequestRate
Servicio: k6-testing
Estado: FIRING

💬 Descripción
───────────────────────────────────────────────────
Alta tasa de requests en K6

Test ejecutándose con alta carga.
Rate: 1250 requests/s (umbral: 1000 req/s)

Esto es informativo - el sistema está bajo
pruebas de carga planificadas.

🕐 Inicio: 2025-10-31 02:55:30

📊 Dashboard: http://localhost:5668
```

---

### 9. PortainerDown

**Asunto:**
```
ℹ️ [INFO] PortainerDown - Portainer caído
```

**Email:**
```
┌────────────────────────────────────────────────────┐
│            ℹ️ PortainerDown                       │
│          Notificación Informativa                 │
└────────────────────────────────────────────────────┘

Alerta: PortainerDown
Servicio: portainer
Estado: FIRING

Portainer no responde desde hace 2 minutos.
La interfaz de gestión de Docker no está disponible,
pero los contenedores siguen funcionando normalmente.

Acción sugerida: Reiniciar contenedor de Portainer
cuando sea conveniente.

Inicio: 2025-10-31 02:55:30
```

---

## 🎨 Resumen de Diferencias Visuales

### 🔴 CRITICAL
- ✅ Banner rojo intenso con gradiente
- ✅ Efecto pulsante (animación)
- ✅ Texto grande "ALERTA CRÍTICA"
- ✅ Sección "ACCIÓN INMEDIATA" destacada
- ✅ Información completa con todos los detalles
- ✅ Enlaces a múltiples dashboards
- ✅ Timeline detallada

### ⚠️ WARNING  
- ✅ Banner naranja con gradiente
- ✅ Diseño profesional pero no alarmante
- ✅ Sección "Acción Recomendada"
- ✅ Información completa
- ✅ Enlaces relevantes
- ✅ Timeline básica

### ℹ️ INFO
- ✅ Banner azul claro
- ✅ Diseño simple y limpio
- ✅ Solo información esencial
- ✅ Sin urgencia
- ✅ Timeline básica

---

## 💡 Características Únicas de Cada Email

### Información Dinámica por Alerta

Cada email muestra información específica basada en:

1. **Labels de Prometheus:**
   - `service`: Sistema, RabbitMQ, Jenkins, etc.
   - `component`: CPU, RAM, queue, build, etc.
   - `instance`: Servidor/contenedor específico
   - `job`: Job name de Jenkins, etc.

2. **Annotations:**
   - `summary`: Resumen específico de la alerta
   - `description`: Descripción detallada con valores
   - `action`: Pasos específicos a seguir
   - `dashboard`: Link al dashboard correcto

3. **Valores Dinámicos:**
   - Porcentajes actuales
   - Números de mensajes
   - Duraciones
   - Nombres de colas, jobs, etc.

### Ejemplos de Valores Dinámicos

**HighRAMUsage:**
```
El uso de RAM está en 65% (umbral: 60%)
```

**RabbitMQQueueMessagesHigh:**
```
La cola orders_queue tiene 1523 mensajes (umbral: 1000)
```

**JenkinsBuildFailed:**
```
El build #456 del job deploy-production ha fallado
```

---

## 🚀 Cómo Probar

### 1. Aplicar Configuración

```bash
./aplicar-emails-personalizados.sh
```

### 2. Disparar Alertas Específicas

```bash
# Para ver email CRITICAL
./test-alertas-interactivo.sh
# Selecciona: 1 (Sistema)
# El test de CPU crítica disparará email CRITICAL

# Para ver email WARNING
./test-alertas-interactivo.sh
# Selecciona: 1 (Sistema)
# El test de HighRAMUsage disparará email WARNING
```

### 3. Verificar Emails

Revisa tu bandeja de entrada:
- pablopolis2016@gmail.com
- jflores@unis.edu.gt

Verás que cada email es diferente y contiene
información específica de la alerta que se disparó.

---

**Última actualización:** 31 de Octubre, 2025  
**Total de alertas con emails personalizados:** 65
