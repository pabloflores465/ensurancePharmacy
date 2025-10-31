# 🔧 Cómo Configurar CheckMK desde la WebUI

## ⚠️ IMPORTANTE: Lee Esto Primero

**CheckMK tiene problemas para mostrar gráficas debido a:**
1. Requiere configuración manual compleja
2. Tarda 15-30 minutos en recolectar datos
3. Las gráficas son básicas

**MEJOR OPCIÓN:** 
- **Grafana** → http://localhost:3001 (admin/admin123) ⭐ RECOMENDADO
- **Netdata** → http://localhost:19999 ⚡ TIEMPO REAL

---

## 📊 Si Aún Quieres Configurar CheckMK

### Paso 1: Acceder a CheckMK

```
URL:      http://localhost:5152/ensurance/check_mk/
Usuario:  cmkadmin
Password: admin123
```

### Paso 2: Agregar Hosts Manualmente

1. En el menú superior, haz clic en **"Setup"**
2. Ve a **"Hosts" → "Hosts"**
3. Haz clic en **"Add host"**
4. Configura el primer host:
   - **Hostname:** `prometheus`
   - **Alias:** `Prometheus Server`
   - **IPv4 Address:** `ensurance-prometheus-full`
   - **Monitoring agents:** Selecciona **"No API integrations, no Checkmk agent"**
5. Haz clic en **"Save & view folder"**

### Paso 3: Repetir para Cada Host

Agrega estos hosts con sus respectivas direcciones:

| Hostname | Alias | IPv4 Address |
|----------|-------|--------------|
| `prometheus` | Prometheus Server | `ensurance-prometheus-full` |
| `grafana` | Grafana Dashboard | `ensurance-grafana-full` |
| `alertmanager` | Alert Manager | `ensurance-alertmanager-full` |
| `rabbitmq` | RabbitMQ | `ensurance-rabbitmq-full` |
| `netdata` | Netdata Monitoring | `ensurance-netdata-full` |
| `node-exporter` | Node Exporter | `ensurance-node-exporter-full` |
| `pushgateway` | Pushgateway | `ensurance-pushgateway-full` |
| `ensurance-app` | Ensurance App | `ensurance-pharmacy-full` |

### Paso 4: Activar Cambios

1. Verás un banner amarillo que dice **"X changes"**
2. Haz clic en el banner
3. Haz clic en **"Activate on selected sites"**
4. Espera a que termine

### Paso 5: Ver Hosts

1. Ve a **"Monitor" → "All hosts"**
2. Deberías ver los 8 hosts listados
3. Algunos pueden estar en estado **PENDING** (esto es normal)

### Paso 6: Descubrir Servicios

Para cada host:

1. Ve a **"Setup" → "Hosts" → "Hosts"**
2. Encuentra tu host en la lista
3. Haz clic en el icono **"Services"** (botón con engranaje)
4. Haz clic en **"Full scan"**
5. Espera a que termine
6. Haz clic en **"Accept all"**
7. Haz clic en **"Activate changes"**

### Paso 7: Ver Servicios

1. Ve a **"Monitor" → "All services"**
2. Deberías ver servicios como:
   - PING
   - Check_MK
   - HTTP (si configuraste checks activos)

### Paso 8: Ver Dashboards

1. Ve a **"Monitor" → "Dashboards"**
2. Selecciona uno de los dashboards creados
3. Espera 15-30 minutos para que aparezcan gráficas

---

## ⏱️ Tiempos de Espera

- **Hosts visibles:** Inmediato después de agregarlos
- **Servicios descubiertos:** 1-5 minutos
- **Primeras métricas:** 5-10 minutos
- **Gráficas completas:** **15-30 minutos**

---

## 💡 Recomendación Final

### En Lugar de Esperar 30 Minutos...

**USA GRAFANA AHORA:**
```
http://localhost:3001
Usuario: admin
Password: admin123
```

**Ventajas:**
- ✅ Gráficas disponibles inmediatamente
- ✅ Más hermosas que CheckMK
- ✅ Mismas métricas que Netdata
- ✅ Ya está configurado

**CheckMK es mejor para:**
- Gestión de alertas enterprise
- Inventario de infraestructura
- Reportes de SLA
- **NO es su fuerte: visualización de gráficas**

---

## 🎯 Resumen

1. **Para gráficas bonitas** → Grafana (http://localhost:3001)
2. **Para troubleshooting** → Netdata (http://localhost:19999)
3. **Para alertas** → CheckMK (pero configúralo manualmente desde la WebUI)

**No pierdas tiempo configurando CheckMK si solo quieres ver gráficas. Grafana ya tiene todo funcionando.**
