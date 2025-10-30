# 🚀 Guía Rápida: Configurar CheckMK Correctamente

## ✅ CheckMK ha sido recreado completamente y está limpio

El problema de "Connection reset" se resolvió eliminando toda la configuración corrupta.

---

## 📝 Configuración Paso a Paso (Web UI)

### 1️⃣ Acceder a CheckMK

```
URL:      http://localhost:5152/ensurance/check_mk/
Usuario:  cmkadmin
Password: admin123
```

### 2️⃣ Ir a la Configuración de Hosts

1. En el menú lateral izquierdo → **"Setup"**
2. En la sección "Hosts" → **"Hosts"**
3. Haz clic en el botón **"Add host"** (arriba a la derecha)

---

## 🏠 Agregar los 8 Hosts (UNO POR UNO)

### Host 1: Prometheus
```
Hostname:           prometheus
Alias:              Prometheus Server
IPv4 Address:       ensurance-prometheus-full
Monitoring agents:  No agent
```
Click "Save & view folder"

### Host 2: Grafana
```
Hostname:           grafana
Alias:              Grafana Dashboard
IPv4 Address:       ensurance-grafana-full
Monitoring agents:  No agent
```
Click "Save & view folder"

### Host 3: AlertManager
```
Hostname:           alertmanager
Alias:              Alert Manager
IPv4 Address:       ensurance-alertmanager-full
Monitoring agents:  No agent
```
Click "Save & view folder"

### Host 4: RabbitMQ
```
Hostname:           rabbitmq
Alias:              RabbitMQ Message Broker
IPv4 Address:       ensurance-rabbitmq-full
Monitoring agents:  No agent
```
Click "Save & view folder"

### Host 5: Netdata
```
Hostname:           netdata
Alias:              Netdata Monitoring
IPv4 Address:       ensurance-netdata-full
Monitoring agents:  No agent
```
Click "Save & view folder"

### Host 6: Node Exporter
```
Hostname:           node-exporter
Alias:              Node Exporter
IPv4 Address:       ensurance-node-exporter-full
Monitoring agents:  No agent
```
Click "Save & view folder"

### Host 7: Pushgateway
```
Hostname:           pushgateway
Alias:              Prometheus Pushgateway
IPv4 Address:       ensurance-pushgateway-full
Monitoring agents:  No agent
```
Click "Save & view folder"

### Host 8: Ensurance App
```
Hostname:           ensurance-app
Alias:              Ensurance Pharmacy Application
IPv4 Address:       ensurance-pharmacy-full
Monitoring agents:  No agent
```
Click "Save & view folder"

---

## 🔍 Descubrir Servicios en Todos los Hosts

### Después de agregar los 8 hosts:

1. Ve a **"Setup > Hosts"**
2. Verás los 8 hosts listados
3. Marca el **checkbox en la parte superior** (selecciona todos)
4. Haz clic en **"Bulk discovery"** (botón arriba)
5. Selecciona **"Full scan"**
6. Haz clic en **"Start"**
7. **Espera 2-3 minutos** mientras CheckMK descubre servicios

---

## ✅ Activar los Cambios

### IMPORTANTE: Este es el momento crítico

1. Verás un **banner amarillo arriba** que dice **"8 changes"**
2. Haz clic en el banner
3. Revisa los cambios (deberías ver "Created new host ..." para cada host)
4. Haz clic en **"Activate affected"**
5. **ESPERA PACIENTEMENTE** (1-2 minutos)
   - NO refresques la página
   - NO hagas clic en otros lugares
   - Deja que termine

### Si todo va bien:
- ✅ Verás "Success"
- ✅ El banner amarillo desaparecerá
- ✅ No habrá error "Connection reset"

---

## 📊 Verificar que Funciona

### Ver los Hosts
1. Ve a **"Monitor > All hosts"**
2. Deberías ver los 8 hosts
3. Estado: **UP** (verde) o **PENDING** (gris - esperando)

### Ver los Servicios
1. Ve a **"Monitor > All services"**
2. Deberías ver servicios como:
   - **PING** para cada host
   - Otros servicios descubiertos

### Ver el Dashboard
1. Ve a **"Monitor > Overview > Main dashboard"**
2. Verás:
   - **Host Statistics:** 8 hosts
   - **Service Statistics:** X servicios

---

## ⚠️ Si Aparece el Error Nuevamente

### Síntomas:
- "Connection reset by peer"
- La página se congela
- No puedes navegar en CheckMK

### Solución Inmediata:
```bash
# Reiniciar CheckMK
docker restart ensurance-checkmk-full

# Esperar 30 segundos
sleep 30

# Recargar la página web
```

### Si el problema persiste:
El problema NO es CheckMK, es la configuración que estamos intentando agregar.

**SOLUCIÓN: Configuración Mínima**
1. NO agregues todos los hosts a la vez
2. Agrega solo 2-3 hosts
3. Activa cambios
4. Si funciona, agrega 2-3 más
5. Repite hasta tener todos

---

## 💡 Recomendaciones Importantes

### ✅ LO QUE SÍ DEBES HACER:
- Agregar hosts manualmente desde la UI
- Usar "No agent" en todos los hosts
- Hacer service discovery después de agregar todos
- Activar cambios al final
- Esperar pacientemente al activar

### ❌ LO QUE NO DEBES HACER:
- NO uses scripts automáticos de configuración
- NO intentes configurar Prometheus integration todavía
- NO agregues reglas complejas
- NO agregues checks HTTP manualmente (aún)
- NO refresques la página mientras activa cambios

---

## 🎯 Resultado Final

Después de completar estos pasos tendrás:

✅ **8 Hosts monitoreados**
- prometheus, grafana, alertmanager, rabbitmq
- netdata, node-exporter, pushgateway, ensurance-app

✅ **Servicios básicos**
- PING para verificar disponibilidad
- Otros servicios que CheckMK descubra automáticamente

✅ **Sistema estable**
- Sin errores "Connection reset"
- Navegación fluida en la UI
- Activación de cambios funcional

---

## 📈 Próximos Pasos (Después de que funcione lo básico)

### Después de tener los 8 hosts funcionando:

1. **Agregar HTTP Checks** (uno por uno desde la UI)
2. **Configurar notificaciones por email**
3. **Crear dashboards personalizados**
4. **Explorar métricas disponibles**

### Para métricas avanzadas:
- **Usa Netdata** → Métricas detalladas en tiempo real
- **Usa CheckMK** → Disponibilidad y alertas enterprise

---

## 🔧 Comandos Útiles

```bash
# Ver estado de CheckMK
docker ps | grep checkmk

# Ver logs de CheckMK
docker logs ensurance-checkmk-full --tail 50

# Reiniciar CheckMK
docker restart ensurance-checkmk-full

# Verificar que está healthy
docker inspect ensurance-checkmk-full | grep -A 5 Health

# Si necesitas recrear desde cero nuevamente
./recreate-checkmk-auto.sh
```

---

## ✨ Tips de Navegación en CheckMK

### Para evitar el error "Connection reset":
1. **NO hagas múltiples acciones simultáneas**
2. **Espera que cada acción termine antes de la siguiente**
3. **Si la página tarda, NO refresques - espera**
4. **Guarda configuraciones pequeñas, no grandes**

### Si la UI se congela:
1. **Espera 30 segundos**
2. **Si sigue congelada, abre otra pestaña**
3. **Reinicia CheckMK si es necesario**

---

## 🎉 ¡Listo!

CheckMK está ahora **limpio y funcional**.

Sigue esta guía **paso a paso** y NO tendrás problemas.

**URL:** http://localhost:5152/ensurance/check_mk/  
**Usuario:** cmkadmin  
**Password:** admin123

---

**Última actualización:** Hoy  
**CheckMK Version:** 2.4.0p12  
**Estado:** Recreado y funcionando correctamente
