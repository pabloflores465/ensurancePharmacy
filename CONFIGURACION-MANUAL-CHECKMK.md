# 🔧 Configuración Manual de CheckMK - Guía Paso a Paso

**Problema resuelto:** Configuración problemática de Prometheus que causa errores al activar cambios.

**Solución:** Configuración manual desde la interfaz web (método más simple y confiable).

---

## 🎯 Paso 1: Limpiar Configuración Actual

Ejecuta estos comandos para limpiar la configuración problemática:

```bash
cd /home/pablopolis2016/Documents/ensurancePharmacy

# Detener CheckMK
docker exec ensurance-checkmk-full omd stop ensurance

# Limpiar configuraciones
docker exec ensurance-checkmk-full rm -f /omd/sites/ensurance/etc/check_mk/conf.d/prometheus_*.mk
docker exec ensurance-checkmk-full rm -f /omd/sites/ensurance/etc/check_mk/conf.d/ensurance_hosts.mk
docker exec ensurance-checkmk-full rm -rf /omd/sites/ensurance/var/check_mk/core/*

# Reiniciar CheckMK
docker exec ensurance-checkmk-full omd start ensurance
```

---

## 🏠 Paso 2: Agregar Hosts Manualmente desde la Web UI

### 2.1 Acceder a CheckMK
1. Abre tu navegador
2. Ve a: **http://localhost:5152/ensurance/check_mk/**
3. Inicia sesión con:
   - **Usuario:** `cmkadmin`
   - **Contraseña:** `admin123`

### 2.2 Ir a la Configuración de Hosts
1. En el menú lateral izquierdo, haz clic en **"Setup"**
2. En la sección "Hosts", haz clic en **"Hosts"**

### 2.3 Agregar el Primer Host (Prometheus)
1. Haz clic en el botón **"Add host"** (arriba a la derecha)
2. Completa los campos:
   - **Hostname:** `prometheus`
   - **Alias:** `Prometheus Server`
   - **IPv4 Address:** `ensurance-prometheus-full`
3. En la sección **"Monitoring agents"**, selecciona:
   - **Check_MK Agent:** Deja "No agent"
4. Haz clic en **"Save & view folder"**

### 2.4 Repetir para Todos los Hosts

Repite el paso 2.3 para cada uno de estos hosts:

| Hostname | Alias | IPv4 Address |
|----------|-------|--------------|
| `grafana` | Grafana Dashboard | `ensurance-grafana-full` |
| `alertmanager` | Alert Manager | `ensurance-alertmanager-full` |
| `rabbitmq` | RabbitMQ | `ensurance-rabbitmq-full` |
| `netdata` | Netdata Monitoring | `ensurance-netdata-full` |
| `node-exporter` | Node Exporter | `ensurance-node-exporter-full` |
| `pushgateway` | Pushgateway | `ensurance-pushgateway-full` |
| `ensurance-app` | Ensurance App | `ensurance-pharmacy-full` |

**IMPORTANTE:** Para cada host, deja "No agent" en la configuración de agentes.

---

## 🔍 Paso 3: Descubrir Servicios

### 3.1 Para Cada Host Individual

1. En la lista de hosts, haz clic en el nombre del host (ej: `prometheus`)
2. Haz clic en el botón **"Save & run service discovery"**
3. CheckMK buscará servicios disponibles
4. Haz clic en **"Accept all"** para aceptar todos los servicios encontrados
5. Haz clic en **"Finish"**

### 3.2 O Usar Descubrimiento Masivo (Más Rápido)

1. En la lista de hosts (**Setup > Hosts**)
2. Marca el checkbox en la parte superior para seleccionar todos los hosts
3. Haz clic en **"Bulk discovery"** (arriba)
4. Selecciona **"Full scan"**
5. Haz clic en **"Start"**
6. Espera 2-3 minutos mientras CheckMK descubre servicios

---

## ✅ Paso 4: Activar Cambios

1. En la parte superior de la página verás un **banner amarillo** que dice **"X changes"**
2. Haz clic en el banner o en el botón **"Activate changes"**
3. Revisa los cambios
4. Haz clic en **"Activate affected"**
5. **Espera 30-60 segundos** mientras se activan los cambios

**IMPORTANTE:** Si el error "Connection reset" aparece nuevamente:
- Refresca la página (F5)
- Los cambios probablemente se aplicaron correctamente
- Verifica en **Monitor > All Hosts**

---

## 📊 Paso 5: Configurar Checks HTTP (Opcional)

Para monitorear los servicios web activamente:

### 5.1 Ir a la Configuración de Checks
1. Ve a **Setup > Services > HTTP, TCP, Email**
2. Busca **"Check HTTP service"**
3. Haz clic en **"Add rule"**

### 5.2 Configurar Check para Prometheus
1. **Description:** `Prometheus Health Check`
2. **Conditions:**
   - **Explicit hosts:** `prometheus`
3. **HTTP URL:** `http://ensurance-prometheus-full:9090/-/healthy`
4. **Timeout:** `10` segundos
5. Haz clic en **"Save"**

### 5.3 Repetir para Otros Servicios

| Service | Host | URL |
|---------|------|-----|
| Grafana | grafana | http://ensurance-grafana-full:3000/api/health |
| RabbitMQ | rabbitmq | http://ensurance-rabbitmq-full:15672 |
| Netdata | netdata | http://ensurance-netdata-full:19999 |
| Ensurance Frontend | ensurance-app | http://ensurance-pharmacy-full:5175 |
| Pharmacy Frontend | ensurance-app | http://ensurance-pharmacy-full:8089 |

**Después de agregar cada regla:**
- Activa los cambios
- Ve a **Setup > Hosts**, selecciona el host
- Ejecuta **"Save & run service discovery"** nuevamente

---

## 📈 Paso 6: Verificar que Todo Funciona

### 6.1 Ver Hosts
1. Ve a **Monitor > All hosts**
2. Deberías ver tus 8 hosts
3. Estado: **UP** (verde)

### 6.2 Ver Servicios
1. Ve a **Monitor > All services**
2. Deberías ver:
   - **PING** para cada host
   - **HTTP** checks si los configuraste
3. Estados: **OK** (verde), **PENDING** (gris - esperando primer check)

### 6.3 Ver Dashboard
1. Ve a **Monitor > Overview > Main dashboard**
2. Verás estadísticas de hosts y servicios
3. **Host Statistics:** 8 UP, 0 Down
4. **Service Statistics:** X OK, Y PENDING

---

## 🎯 Paso 7: Integración con Prometheus (SIMPLIFICADA)

Para obtener métricas de Prometheus SIN configuración compleja:

### Método 1: PING y HTTP (Ya lo tienes)
- ✅ Verifica disponibilidad
- ✅ Verifica que los puertos respondan
- ✅ Verifica tiempos de respuesta HTTP

### Método 2: Usar Netdata para Métricas Detalladas
- ✅ Netdata ya tiene todas las métricas (CPU, memoria, disco, red)
- ✅ Netdata se alimenta de Prometheus
- ✅ CheckMK verifica que Netdata esté UP

### Método 3: Queries de Prometheus (Avanzado - Opcional)

Si realmente necesitas métricas de Prometheus en CheckMK:

1. Ve a **Setup > Agents > Other integrations**
2. Busca **"Prometheus"**
3. Selecciona **"Check via HTTP"** (más simple que Special Agent)
4. Configura:
   - **URL:** `http://ensurance-prometheus-full:9090/api/v1/query`
   - **Query:** `up{job="node-exporter"}`
   - **Expected result:** `1`

---

## 🚨 Troubleshooting

### Problema: Error "Connection reset" al activar cambios

**Solución:**
```bash
# Reiniciar CheckMK completamente
docker exec ensurance-checkmk-full omd restart ensurance

# Esperar 30 segundos
sleep 30

# Recargar la página web
# Los cambios probablemente ya se aplicaron
```

### Problema: No veo los hosts

**Solución:**
1. Refresca la página (F5)
2. Ve a **Setup > Hosts** (no **Monitor > Hosts**)
3. Si aún no están, agrégalos manualmente (Paso 2)

### Problema: No veo servicios

**Solución:**
1. Ejecuta **"Save & run service discovery"** en cada host
2. Acepta todos los servicios
3. Activa los cambios
4. Espera 2-3 minutos
5. Refresca la página

### Problema: Servicios en estado PENDING

**Solución:**
- Es normal al inicio
- Espera 5 minutos para el primer check
- El estado cambiará a OK, WARNING o CRITICAL

### Problema: No hay gráficas

**Solución:**
- Las gráficas tardan 15-30 minutos en aparecer
- Necesitas datos históricos
- Espera y refresca después

---

## 💡 Recomendaciones

### Para Monitoreo Básico (Suficiente para la mayoría)
✅ Usa CheckMK para:
- Verificar disponibilidad (PING)
- Verificar servicios HTTP
- Recibir alertas cuando algo está DOWN

✅ Usa Netdata para:
- Métricas detalladas en tiempo real
- Gráficas de CPU, memoria, disco, red
- Análisis de rendimiento

### Para Monitoreo Avanzado
Si necesitas TODO en CheckMK:
- Configura la integración con Prometheus DESPUÉS de tener lo básico funcionando
- No configures todo a la vez
- Ve paso a paso

---

## ✅ Checklist de Verificación

- [ ] CheckMK está corriendo: `docker ps | grep checkmk`
- [ ] Puedo acceder a la web UI: http://localhost:5152/ensurance/check_mk/
- [ ] He agregado 8 hosts en **Setup > Hosts**
- [ ] He ejecutado service discovery en cada host
- [ ] He activado los cambios SIN errores
- [ ] Veo los hosts en **Monitor > All hosts**
- [ ] Veo servicios en **Monitor > All services**
- [ ] Los servicios PING están OK

---

## 🎉 Resultado Final

Después de completar estos pasos tendrás:

✅ **8 Hosts monitoreados:**
- prometheus, grafana, alertmanager, rabbitmq
- netdata, node-exporter, pushgateway, ensurance-app

✅ **Servicios monitoreados:**
- PING (disponibilidad)
- HTTP checks (si los configuraste)
- Estado de servicios

✅ **Sistema de alertas:**
- Email cuando un host está DOWN
- Email cuando un servicio está CRITICAL

✅ **Dashboards:**
- Vista general de hosts y servicios
- Problemas actuales
- Historial de eventos

---

## 📚 Comandos Útiles

```bash
# Ver estado de CheckMK
docker exec ensurance-checkmk-full omd status ensurance

# Reiniciar CheckMK
docker exec ensurance-checkmk-full omd restart ensurance

# Ver logs
docker exec ensurance-checkmk-full tail -f /omd/sites/ensurance/var/log/web.log

# Limpiar configuración problemática
docker exec ensurance-checkmk-full rm -rf /omd/sites/ensurance/var/check_mk/core/*
docker exec ensurance-checkmk-full omd restart ensurance
```

---

**Última actualización:** $(date)
**CheckMK Version:** 2.4.0p12
**Método:** Configuración manual desde Web UI (recomendado)
