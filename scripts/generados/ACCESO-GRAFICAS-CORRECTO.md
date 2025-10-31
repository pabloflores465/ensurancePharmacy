# ✅ ACCESO CORRECTO A GRÁFICAS

## 🎯 El Problema con CheckMK

CheckMK no muestra hosts ni servicios porque la versión Raw requiere configuración manual desde la WebUI. Los scripts automáticos tienen limitaciones.

## ✅ SOLUCIÓN: Usa las Herramientas que SÍ Funcionan

---

## 📊 OPCIÓN 1: GRAFANA (RECOMENDADO) ⭐

### URL Correcta:
```
http://localhost:3302
```

### Credenciales:
```
Usuario: admin
Password: admin123
```

### ¿Qué Verás?
- ✅ Dashboards profesionales con gráficas hermosas
- ✅ Métricas de CPU, Memoria, Disco, Red
- ✅ Datos históricos
- ✅ Idéntico a Netdata pero más completo

### Dashboards Disponibles:
1. **Node Exporter Full** → Métricas del sistema completas
2. **Prometheus Stats** → Estado del servidor Prometheus  
3. **RabbitMQ Overview** → Métricas de RabbitMQ
4. **Y más...**

---

## ⚡ OPCIÓN 2: NETDATA (TIEMPO REAL)

### URL:
```
http://localhost:19999
```

### Sin credenciales necesarias

### ¿Qué Verás?
- ✅ Gráficas en tiempo real (actualización cada segundo)
- ✅ Todas las métricas del sistema
- ✅ Ideal para troubleshooting inmediato
- ✅ Interfaz automática y completa

---

## 📋 OPCIÓN 3: CHECKMK (Solo para Alertas)

### URL:
```
http://localhost:5152/ensurance/check_mk/
```

### Credenciales:
```
Usuario: cmkadmin
Password: admin123
```

### Estado Actual:
❌ **No hay hosts configurados visibles**

### Para Configurarlo (Opcional):
1. Accede a la URL arriba
2. Ve a: **Setup → Hosts → Add host**
3. Agrega cada host manualmente:
   - `prometheus` → `ensurance-prometheus-full`
   - `grafana` → `ensurance-grafana-full`
   - `alertmanager` → `ensurance-alertmanager-full`
   - `rabbitmq` → `ensurance-rabbitmq-full`
   - `netdata` → `ensurance-netdata-full`
   - `node-exporter` → `ensurance-node-exporter-full`
   - `pushgateway` → `ensurance-pushgateway-full`
   - `ensurance-app` → `ensurance-pharmacy-full`
4. Activa los cambios
5. Espera 30 minutos para ver gráficas

**⚠️ No Recomendado:** CheckMK tarda mucho y las gráficas son básicas.

---

## 🎯 Comparación

```
┌──────────────┬─────────────┬────────────┬──────────────┐
│ Herramienta  │  Gráficas   │ Disponible │ Facilidad    │
├──────────────┼─────────────┼────────────┼──────────────┤
│ GRAFANA      │ ⭐⭐⭐⭐⭐   │ ✅ AHORA   │ ✅ Fácil     │
│   :3302      │ Profesional │            │ Ya config.   │
├──────────────┼─────────────┼────────────┼──────────────┤
│ NETDATA      │ ⭐⭐⭐⭐⭐   │ ✅ AHORA   │ ✅ Automático│
│   :19999     │ Tiempo real │            │ Sin config.  │
├──────────────┼─────────────┼────────────┼──────────────┤
│ CHECKMK      │ ⭐⭐        │ ⏱️ 30 min  │ ❌ Manual    │
│   :5152      │ Básico      │            │ Complejo     │
└──────────────┴─────────────┴────────────┴──────────────┘
```

---

## 🚀 Guía Rápida de Acceso

### 1. Abrir Grafana (Gráficas Completas)

```bash
# En tu navegador, ve a:
http://localhost:3302

# Login:
admin / admin123

# Navega a:
☰ (menú hamburguesa) → Dashboards → Browse

# Selecciona cualquier dashboard, por ejemplo:
- Node Exporter Full
```

### 2. Abrir Netdata (Tiempo Real)

```bash
# En tu navegador, ve a:
http://localhost:19999

# Explora:
- Overview → Resumen de todas las métricas
- CPU → Uso de CPU en tiempo real
- Memory → Uso de memoria
- Disk → I/O de disco
- Network → Tráfico de red
```

---

## 💡 Recomendación Final

### Para tu caso de uso (gráficas como Netdata):

**🏆 USA GRAFANA**
- Gráficas más hermosas
- Datos históricos completos
- Dashboards profesionales
- **URL:** http://localhost:3302

### Si necesitas troubleshooting inmediato:

**⚡ USA NETDATA**
- Actualización cada segundo
- Sin configuración
- **URL:** http://localhost:19999

### Olvídate de CheckMK para gráficas

CheckMK es excelente para:
- ✅ Alertas enterprise
- ✅ Gestión de servicios
- ✅ Reportes de SLA

Pero **NO es bueno** para:
- ❌ Visualización de gráficas
- ❌ Monitoreo en tiempo real
- ❌ Exploración visual de métricas

---

## ✅ Verificar que Todo Funciona

```bash
# Verificar Grafana
curl -s http://localhost:3302/api/health

# Verificar Netdata
curl -s http://localhost:19999/api/v1/info | head -5

# Verificar CheckMK
curl -s http://localhost:5152/ensurance/check_mk/ | grep -q "Check_MK"
```

---

## 📞 Conclusión

**No necesitas configurar más nada. Las gráficas ya están disponibles:**

1. **Grafana (puerto 3302)** → Para gráficas profesionales
2. **Netdata (puerto 19999)** → Para tiempo real

**Ambas herramientas muestran las mismas métricas que querías ver en CheckMK, pero de forma más hermosa y sin configuración adicional.**

🎉 **¡Disfruta de tus gráficas!**
