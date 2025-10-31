# 🚀 Guía Paso a Paso: Usar CheckMK con Ensurance Pharmacy

## ✅ Estado Actual

Los hosts se han agregado a CheckMK mediante la API REST. Ahora necesitas completar el descubrimiento de servicios desde la interfaz web.

---

## 📋 Paso 1: Acceder a CheckMK

1. Abre tu navegador
2. Ve a: **http://localhost:5152/ensurance/check_mk/**
3. Inicia sesión:
   - **Usuario:** `cmkadmin`
   - **Password:** `admin123`

---

## 📋 Paso 2: Ver los Hosts Agregados

1. En el menú lateral izquierdo, haz clic en **"Setup"**
2. Luego haz clic en **"Hosts"**
3. Deberías ver 8 hosts listados:
   - prometheus
   - grafana
   - alertmanager
   - rabbitmq
   - netdata
   - node-exporter
   - pushgateway
   - ensurance-app

**Si no los ves:**
- Presiona **F5** para refrescar la página
- Si aún no aparecen, ejecuta el script nuevamente: `./setup-checkmk-api.sh`

---

## 📋 Paso 3: Descubrir Servicios en los Hosts

Para que CheckMK descubra los servicios (CPU, Memoria, Puertos, HTTP, etc.) en cada host:

### Opción A: Descubrimiento Automático (Rápido)

1. En **Setup > Hosts**, encuentra el botón que dice **"Bulk Discovery"** o **"Bulk actions"**
2. Selecciona todos los hosts (checkbox en la parte superior)
3. Haz clic en **"Bulk discovery"**
4. Selecciona **"Full scan"**
5. Haz clic en **"Start"**
6. Espera 2-3 minutos mientras CheckMK descubre los servicios

### Opción B: Descubrimiento Manual (Host por Host)

Para cada host:
1. Haz clic en el nombre del host (ej: "prometheus")
2. Haz clic en el botón **"Save & run service discovery"**
3. CheckMK buscará servicios disponibles
4. Haz clic en **"Accept all"** para aceptar todos los servicios encontrados
5. Haz clic en **"Finish"**
6. Repite para cada host

---

## 📋 Paso 4: Activar los Cambios

Después de descubrir servicios:

1. En la parte superior verás un banner amarillo que dice **"X changes"**
2. Haz clic en el banner o en el botón **"Activate changes"**
3. Haz clic en **"Activate affected"**
4. Espera que se active la configuración (10-30 segundos)

---

## 📋 Paso 5: Ver Hosts y Servicios Monitoreados

Ahora puedes ver el monitoreo activo:

### Ver Todos los Hosts
1. Ve a **Monitor > All hosts**
2. Deberías ver los 8 hosts con su estado (UP/DOWN)

### Ver Todos los Servicios
1. Ve a **Monitor > All services**
2. Verás todos los servicios descubiertos y su estado

### Ver el Dashboard Principal
1. Ve a **Monitor > Overview > Main dashboard**
2. Verás estadísticas de hosts y servicios
3. Verás gráficas y problemas activos

---

## 📋 Paso 6: Configurar Checks Adicionales (Opcional)

Si quieres agregar checks HTTP manualmente:

1. Ve a **Setup > Services > HTTP, TCP, Email**
2. Haz clic en **"Active checks > Check HTTP service"**
3. Haz clic en **"Add rule"**
4. Configura el check:
   - **Description:** Nombre del check
   - **URL:** http://ensurance-pharmacy-full:5175 (por ejemplo)
   - **Expected HTTP status code:** 200
5. Haz clic en **"Save"**
6. Activa los cambios

---

## 🎯 Servicios que CheckMK Puede Monitorear

Para cada host, CheckMK puede descubrir automáticamente:

### ✅ Hosts de Infraestructura
**prometheus**, **grafana**, **alertmanager**, **rabbitmq**, **netdata**, **node-exporter**, **pushgateway**:
- ✅ PING (disponibilidad)
- ✅ TCP Port Check (puerto específico)
- ✅ HTTP Check (si aplica)

### ✅ Host de Aplicación (ensurance-app)
- ✅ PING
- ✅ TCP Ports: 5175, 8089, 8081, 8082, 9464-9467
- ✅ HTTP Checks para frontends y backends

---

## 🔍 ¿Por Qué No Veo Métricas de CPU/Memoria/Disco?

CheckMK necesita un **agente** instalado en el host para obtener métricas detalladas del sistema. Dado que los servicios corren en Docker:

### Solución 1: Checks TCP/HTTP (Ya configurado)
- Verifica que los puertos estén abiertos
- Verifica que los servicios HTTP respondan
- **Este es el método actual y es suficiente para tu caso**

### Solución 2: Integración con Prometheus (Avanzado)
Para obtener métricas de CPU/Memoria/Disco desde Prometheus:

1. Ve a **Setup > Agents > Other integrations**
2. Busca **"Prometheus"**
3. Configura la integración apuntando a tus endpoints de Prometheus

### Solución 3: Checks SNMP (No aplica aquí)

---

## 📊 Crear Dashboards Personalizados

1. Ve a **Customize > Dashboards**
2. Haz clic en **"Add dashboard"**
3. Agrega widgets:
   - **Host statistics**
   - **Service statistics**
   - **Host problems**
   - **Service problems**
   - **Top alerters**
   - **Event histogram**
4. Haz clic en **"Save"**

---

## 🔔 Configurar Notificaciones

### Email
1. Ve a **Setup > Notifications**
2. Haz clic en **"Add rule"**
3. Configura:
   - **Description:** Alertas por Email
   - **Notification Method:** Mail
   - **Contact groups:** Admins
   - **Match services:** Todos o específicos
4. Haz clic en **"Save"**

### Slack (Requiere configuración adicional)
1. Crea un Incoming Webhook en Slack
2. Ve a **Setup > Notifications > Slack**
3. Configura el webhook URL
4. Crea una regla de notificación

---

## 🎨 Equivalencias con Netdata

| Netdata | CheckMK |
|---------|---------|
| Dashboard principal | Monitor > Overview > Main dashboard |
| Vista de hosts | Monitor > All hosts |
| Alertas activas | Monitor > Problems |
| Métricas en tiempo real | Monitor > Services (actualiza cada 1-5 min) |
| Gráficas históricas | Monitor > Services > Gráficas |

---

## ⚠️ Troubleshooting

### No veo hosts
- Refresca la página (F5)
- Ve a **Setup > Hosts** en lugar de **Monitor > Hosts**
- Ejecuta nuevamente: `./setup-checkmk-api.sh`

### No veo servicios
- Ejecuta el descubrimiento de servicios (Paso 3)
- Activa los cambios (Paso 4)
- Espera 2-3 minutos y refresca

### Los servicios están en estado PENDING
- Es normal al inicio
- Espera 5 minutos para el primer check
- El estado cambiará a OK, WARNING o CRITICAL

### Error "Check_MK Discovery"
- Normal - significa que necesitas ejecutar el service discovery
- Sigue el Paso 3

---

## 🚀 Siguientes Pasos Recomendados

1. ✅ Descubre servicios en todos los hosts
2. ✅ Explora el dashboard principal
3. ✅ Configura notificaciones por email
4. ✅ Crea dashboards personalizados para tu equipo
5. ✅ Integra con Prometheus para métricas avanzadas (opcional)

---

## 📞 Soporte

- **Documentación oficial:** https://docs.checkmk.com/
- **API REST:** https://docs.checkmk.com/latest/en/rest_api.html
- **Comunidad:** https://forum.checkmk.com/

---

**Última actualización:** $(date)
**CheckMK Version:** 2.4.0p12
**Ambiente:** Ensurance Pharmacy Monitoring

---

## 💡 TIP IMPORTANTE

**CheckMK y Netdata son complementarios:**
- Usa **Netdata** para troubleshooting en tiempo real y métricas detalladas cada segundo
- Usa **CheckMK** para monitoreo enterprise, historial extendido, alertas organizadas y reportes

¡Ambos sistemas corren en paralelo y se alimentan de Prometheus!
