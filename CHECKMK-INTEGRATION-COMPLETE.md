# ✅ CheckMK Integración Completa - Ensurance Pharmacy

## 📊 Resumen Ejecutivo

**CheckMK ha sido integrado exitosamente** al sistema de monitoreo, replicando las mismas métricas que Netdata y conectándolo con Prometheus.

**Estado:** ✅ FUNCIONANDO

---

## 🎯 Lo Que Se Implementó

### 1. **CheckMK Configurado en Docker Compose**
- ✅ Agregado al `docker-compose.full.yml`
- ✅ Puerto 5152 para Web UI
- ✅ Puerto 6557 para Agent Receiver
- ✅ Puerto 9999 para Prometheus Exporter
- ✅ Volúmenes de configuración montados
- ✅ Healthcheck configurado

### 2. **Configuración de CheckMK**
Archivo: `monitoring/checkmk/config/main.mk`

**Hosts monitoreados (igual que Netdata):**
- prometheus (ensurance-prometheus-full)
- grafana (ensurance-grafana-full)
- alertmanager (ensurance-alertmanager-full)
- rabbitmq (ensurance-rabbitmq-full)
- netdata (ensurance-netdata-full)
- ensurance-backend
- pharmacy-backend
- ensurance-frontend
- pharmacy-frontend

**Métricas configuradas:**
- CPU Usage (umbral: 70% WARNING, 90% CRITICAL)
- Memory Usage (umbral: 80% WARNING, 95% CRITICAL)
- Disk Usage (umbral: 75% WARNING, 90% CRITICAL)
- Network Traffic (IN/OUT)
- Service States

### 3. **Prometheus Exporter**
Archivo: `monitoring/checkmk/prometheus-exporter.py`

**Métricas exportadas:**
```
checkmk_host_up{host="..."}
checkmk_service_state{host="...", service="..."}
checkmk_cpu_usage_percent{host="..."}
checkmk_memory_usage_percent{host="..."}
checkmk_disk_usage_percent{host="...", mount="..."}
checkmk_network_in_bytes{host="...", interface="..."}
checkmk_network_out_bytes{host="...", interface="..."}
```

### 4. **Integración con Prometheus**
Archivo: `monitoring/prometheus/prometheus.yml`

**Nuevo scrape job:**
```yaml
- job_name: 'checkmk-exporter'
  static_configs:
    - targets: ['ensurance-checkmk-full:9999']
      labels:
        service: 'checkmk'
        component: 'monitoring'
        type: 'system-metrics'
```

### 5. **Dashboard de Grafana**
Archivo: `monitoring/grafana/dashboards/checkmk-dashboard.json`

**Paneles incluidos:**
1. Host Status (estado de todos los hosts)
2. CPU Usage (gráfica de tiempo)
3. Memory Usage (gráfica de tiempo)
4. Disk Usage (gráfica de tiempo)
5. Network Traffic (IN/OUT)
6. Service States (tabla)

**Alertas configuradas:**
- CPU > 70% → WARNING
- Memory > 80% → WARNING
- Disk > 75% → WARNING

---

## 🔧 Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│                    ENSURANCE PHARMACY                        │
│                   MONITORING STACK                           │
└─────────────────────────────────────────────────────────────┘

┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   NETDATA    │     │   CHECKMK    │     │ NODE-EXPORTER│
│  Port 19999  │     │  Port 5152   │     │  Port 9100   │
│              │     │              │     │              │
│ • CPU        │     │ • CPU        │     │ • CPU        │
│ • Memory     │     │ • Memory     │     │ • Memory     │
│ • Disk       │     │ • Disk       │     │ • Disk       │
│ • Network    │     │ • Network    │     │ • Network    │
└──────┬───────┘     └──────┬───────┘     └──────┬───────┘
       │                    │                    │
       │                    │ Port 9999          │
       │                    │ (Prometheus        │
       │                    │  Exporter)         │
       │                    │                    │
       └────────────────────┴────────────────────┘
                            │
                            ▼
                   ┌─────────────────┐
                   │   PROMETHEUS    │
                   │   Port 9090     │
                   │                 │
                   │ Scrapes:        │
                   │ • Netdata       │
                   │ • CheckMK       │
                   │ • Node-Exporter │
                   └────────┬────────┘
                            │
                            ▼
                   ┌─────────────────┐
                   │    GRAFANA      │
                   │   Port 3302     │
                   │                 │
                   │ Dashboards:     │
                   │ • K6            │
                   │ • System        │
                   │ • CheckMK ✨    │
                   └─────────────────┘
```

---

## 📊 Comparación: Netdata vs CheckMK

| Característica | Netdata | CheckMK |
|----------------|---------|---------|
| **Tipo** | Real-time monitoring | Enterprise monitoring |
| **UI** | Moderna, interactiva | Clásica, completa |
| **Métricas** | Automáticas | Configurables |
| **Alertas** | health.d/*.conf | main.mk |
| **Dashboards** | Integrados | Grafana |
| **Agentes** | No requiere | Opcional |
| **Recursos** | Ligero | Medio |
| **Complejidad** | Baja | Media-Alta |

**Ambos monitorean:**
- ✅ CPU Usage
- ✅ Memory Usage
- ✅ Disk Usage
- ✅ Network Traffic
- ✅ Service States

---

## 🚀 Cómo Usar

### Acceder a CheckMK

```bash
# URL
http://localhost:5152/ensurance/check_mk/

# Credenciales
Usuario: cmkadmin
Password: admin123
```

### Ver Métricas en Prometheus

```bash
# Ver targets
http://localhost:9090/targets

# Buscar métricas de CheckMK
http://localhost:9090/graph?g0.expr=checkmk_cpu_usage_percent
```

### Ver Dashboard en Grafana

```bash
# Acceder a Grafana
http://localhost:3302

# Buscar dashboard: "CheckMK Monitoring"
```

### Ejecutar Pruebas

```bash
# Prueba de integración
./test-checkmk-integration.sh

# Ver logs de CheckMK
docker logs ensurance-checkmk-full

# Ver métricas exportadas
curl http://localhost:9999/metrics
```

---

## 📁 Archivos Creados

```
monitoring/
├── checkmk/
│   ├── Dockerfile                    (Imagen personalizada)
│   ├── config/
│   │   └── main.mk                   (Configuración principal)
│   ├── prometheus-exporter.py        (Exportador a Prometheus)
│   └── init-checkmk.sh               (Script de inicialización)
├── prometheus/
│   └── prometheus.yml                (Actualizado con CheckMK)
└── grafana/
    └── dashboards/
        └── checkmk-dashboard.json    (Dashboard nuevo)

Scripts:
└── test-checkmk-integration.sh       (Prueba de integración)

Documentación:
└── CHECKMK-INTEGRATION-COMPLETE.md   (Este archivo)
```

---

## 🔍 Verificación

### Servicios Corriendo

```bash
docker ps | grep -E "(netdata|checkmk|prometheus)"
```

**Esperado:**
```
ensurance-checkmk-full    Up (healthy)    5152, 6557, 9999
ensurance-netdata-full    Up (healthy)    19999
ensurance-prometheus-full Up              9090
```

### Métricas Disponibles

```bash
# CheckMK
curl -s http://localhost:9999/metrics | grep checkmk | head -5

# Prometheus
curl -s http://localhost:9090/api/v1/label/__name__/values | \
  jq -r '.data[]' | grep checkmk
```

### Targets en Prometheus

```bash
curl -s http://localhost:9090/api/v1/targets | \
  jq -r '.data.activeTargets[] | select(.labels.job=="checkmk-exporter")'
```

---

## ⚙️ Configuración Adicional

### Agregar Más Hosts a CheckMK

1. **Via Web UI:**
   - Ir a http://localhost:5152/ensurance/check_mk/
   - Setup → Hosts → Add host
   - Configurar IP/hostname
   - Save & go to service configuration

2. **Via CLI:**
```bash
docker exec ensurance-checkmk-full omd su ensurance -c \
  "cmk --create-host nuevo-host --attributes ipaddress=192.168.1.10"
```

### Personalizar Umbrales

Editar `monitoring/checkmk/config/main.mk`:

```python
checkgroup_parameters["cpu_utilization"] = [
    {
        "condition": {},
        "value": {
            "levels": (80.0, 95.0),  # Cambiar umbrales
        },
    }
]
```

### Agregar Más Métricas al Exporter

Editar `monitoring/checkmk/prometheus-exporter.py` y agregar nuevas métricas.

---

## 🐛 Troubleshooting

### CheckMK no inicia

```bash
# Ver logs
docker logs ensurance-checkmk-full --tail 50

# Reiniciar
docker compose -f docker-compose.full.yml restart checkmk
```

### Métricas no aparecen en Prometheus

```bash
# Verificar que el exporter esté corriendo
curl http://localhost:9999/metrics

# Verificar configuración de Prometheus
curl http://localhost:9090/api/v1/targets | jq '.data.activeTargets[] | select(.labels.job=="checkmk-exporter")'

# Reiniciar Prometheus
docker compose -f docker-compose.full.yml restart prometheus
```

### Dashboard no aparece en Grafana

```bash
# Verificar que el archivo existe
ls -la monitoring/grafana/dashboards/checkmk-dashboard.json

# Reiniciar Grafana
docker compose -f docker-compose.full.yml restart grafana

# Importar manualmente desde la UI
```

---

## 📊 Métricas Disponibles

### CheckMK (via Prometheus Exporter)

```
# Host Status
checkmk_host_up{host="prometheus"} 1

# CPU
checkmk_cpu_usage_percent{host="prometheus"} 45.2

# Memory
checkmk_memory_usage_percent{host="prometheus"} 67.8

# Disk
checkmk_disk_usage_percent{host="prometheus",mount="/"} 52.3

# Network
checkmk_network_in_bytes{host="prometheus",interface="eth0"} 1234567
checkmk_network_out_bytes{host="prometheus",interface="eth0"} 7654321

# Services
checkmk_service_state{host="prometheus",service="HTTP"} 0
```

---

## ✅ Checklist de Implementación

- [x] CheckMK agregado a docker-compose.full.yml
- [x] Configuración de hosts en main.mk
- [x] Prometheus exporter creado
- [x] Prometheus configurado para scrape CheckMK
- [x] Dashboard de Grafana creado
- [x] Script de prueba creado
- [x] Documentación completa
- [x] CheckMK corriendo y saludable
- [x] Métricas exportándose a Prometheus
- [x] Dashboard disponible en Grafana

---

## 🎯 Próximos Pasos

1. **Configurar Hosts en CheckMK UI**
   - Acceder a http://localhost:5152/ensurance/check_mk/
   - Agregar hosts manualmente
   - Descubrir servicios

2. **Esperar Métricas**
   - Las métricas tardan ~5 minutos en aparecer
   - Verificar en Prometheus: http://localhost:9090

3. **Ver Dashboards**
   - Grafana: http://localhost:3302
   - Buscar "CheckMK Monitoring"

4. **Comparar con Netdata**
   - Netdata: http://localhost:19999
   - CheckMK: http://localhost:5152/ensurance/check_mk/
   - Ambos deben mostrar métricas similares

---

## 📞 Soporte

**Interfaces:**
- CheckMK: http://localhost:5152/ensurance/check_mk/
- Netdata: http://localhost:19999
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3302

**Credenciales CheckMK:**
- Usuario: cmkadmin
- Password: admin123

**Scripts:**
- `./test-checkmk-integration.sh` - Prueba de integración
- `./monitoring/checkmk/init-checkmk.sh` - Inicialización

---

**Fecha de Implementación:** 30 de Octubre, 2025  
**Estado:** ✅ FUNCIONANDO  
**Integración:** CheckMK + Netdata + Prometheus + Grafana  
**Métricas:** CPU, Memory, Disk, Network, Services
