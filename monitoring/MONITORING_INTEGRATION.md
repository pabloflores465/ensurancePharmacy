# 🔗 Integración de Monitoreo con Stress Tests

## 📋 Resumen

El sistema de monitoreo ha sido actualizado para **monitorear las mismas instancias** que utilizan los stress tests. Ahora existe una integración inteligente que detecta si las aplicaciones ya están corriendo y decide automáticamente si levanta instancias propias o monitorea las existentes.

---

## 🎯 Problema Resuelto

**Antes:** 
- Los stress tests apuntaban a `host.docker.internal:3002` y `:3003`
- El monitoreo apuntaba a `10.128.0.2:9464-9467` (servidor remoto)
- No había conexión entre ambos sistemas

**Ahora:**
- ✅ Monitoreo y stress tests usan las **mismas instancias**
- ✅ Prometheus monitorea puertos `9464-9467` (métricas)
- ✅ Stress tests consumen puertos `3000-3003` (aplicaciones)
- ✅ Detección automática de instancias corriendo

---

## 🏗️ Arquitectura

### Puertos Unificados

| Servicio | Puerto App | Puerto Métricas | Uso |
|----------|-----------|----------------|-----|
| **Backend v4** (Ensurance) | 3002 | 9465 | Stress tests + Monitoreo |
| **Backend v5** (Pharmacy) | 3003 | 9464 | Stress tests + Monitoreo |
| **Frontend Ensurance** | 3000 | 9466 | Stress tests + Monitoreo |
| **Frontend Pharmacy** | 3001 | 9467 | Stress tests + Monitoreo |

### Flujo de Detección

```
┌─────────────────────────────────────────────────────────────────┐
│                    START MONITORING                              │
│                    ./start-monitoring.sh                         │
└──────────────────────────┬───────────────────────────────────────┘
                           │
                           ▼
            ┌──────────────────────────────┐
            │ ¿Apps corriendo en puertos?  │
            │ 3000, 3001, 3002, 3003       │
            └──────────────┬───────────────┘
                           │
           ┌───────────────┴────────────────┐
           │                                │
           ▼ NO                            ▼ SÍ
┌──────────────────────┐       ┌──────────────────────┐
│ Levantar apps        │       │ Monitorear           │
│ --profile with-apps  │       │ instancias existentes│
└──────┬───────────────┘       └──────┬───────────────┘
       │                              │
       └──────────────┬───────────────┘
                      │
                      ▼
       ┌───────────────────────────────┐
       │  Prometheus monitorea ambas:  │
       │  - Container interno          │
       │  - host.docker.internal       │
       └───────────────────────────────┘
```

---

## 🚀 Modos de Uso

### Modo 1: Automático (Recomendado) ⭐

```bash
cd scripts
./start-monitoring.sh
```

**El script hace:**
1. ✅ Verifica puertos 3000-3003 (apps)
2. ✅ Verifica puertos 9464-9467 (métricas)
3. ✅ Decide automáticamente:
   - Si faltan instancias → Levanta con `--profile with-apps`
   - Si existen → Monitorea las existentes
4. ✅ Muestra resumen y URLs de acceso

---

### Modo 2: Manual - Monitorear Instancias Existentes

**Caso de uso:** Ya tienes `docker-compose.dev.yml` corriendo

```bash
# 1. Levantar apps DEV primero
cd scripts
docker compose -f docker-compose.dev.yml up -d

# 2. Levantar solo monitoreo
docker compose -f docker-compose.monitor.yml up -d
```

**Prometheus monitoreará:**
- `host.docker.internal:9464` (Backend v5)
- `host.docker.internal:9465` (Backend v4)
- `host.docker.internal:9466` (Frontend Ensurance)
- `host.docker.internal:9467` (Frontend Pharmacy)

---

### Modo 3: Manual - Con Aplicaciones Propias

**Caso de uso:** No hay instancias corriendo, quieres levantar todo junto

```bash
cd scripts
docker compose -f docker-compose.monitor.yml --profile with-apps up -d
```

**Levanta:**
- ✅ Contenedor `ensurance-pharmacy-monitoring` con todas las apps
- ✅ Grafana, Prometheus, CheckMK, Pushgateway
- ✅ Expone puertos 3000-3003 (apps) y 9464-9467 (métricas)

---

## 📊 Configuración de Prometheus

Prometheus está configurado para intentar **ambas fuentes**:

```yaml
- job_name: 'backv5-pharmacy'
  static_configs:
    # 1. Intenta contenedor interno
    - targets: ['ensurance-pharmacy-monitoring:9464']
      labels:
        instance_type: 'container'
    
    # 2. Intenta host.docker.internal (apps externas)
    - targets: ['host.docker.internal:9464']
      labels:
        instance_type: 'host'
```

**Resultado:** Prometheus recolecta métricas de la fuente disponible.

---

## 🔄 Integración con Stress Tests

### Configuración de K6 y JMeter

Ambos apuntan a:
```bash
BACKV4_URL: "http://host.docker.internal:3002"  # Backend v4
BACKV5_URL: "http://host.docker.internal:3003"  # Backend v5
```

### Configuración de Prometheus

Monitorea métricas en:
```bash
host.docker.internal:9464  # Métricas Backend v5
host.docker.internal:9465  # Métricas Backend v4
host.docker.internal:9466  # Métricas Frontend Ensurance
host.docker.internal:9467  # Métricas Frontend Pharmacy
```

**✅ Mismas instancias, diferentes puertos según propósito**

---

## 🎓 Casos de Uso Comunes

### Caso 1: Desarrollo + Monitoreo + Stress Testing

```bash
# Terminal 1: Levantar apps DEV
cd scripts
docker compose -f docker-compose.dev.yml up -d

# Terminal 2: Levantar monitoreo (usa instancias de Terminal 1)
cd scripts
./start-monitoring.sh
# Output: "✅ Todas las instancias necesarias están corriendo"

# Terminal 3: Ejecutar stress test (usa instancias de Terminal 1)
cd scripts
TEST_SCRIPT=load-test.js docker compose -f docker-compose.stress.yml run --rm k6

# Ver métricas en tiempo real en Grafana
# http://localhost:3300
```

### Caso 2: Solo Monitoreo con Apps Integradas

```bash
# Levanta todo en uno
cd scripts
./start-monitoring.sh
# Output: "⚠️ No todas las instancias están corriendo"
# Output: "📦 Se levantarán instancias propias para monitoreo"

# Acceder a Grafana
# http://localhost:3300
```

### Caso 3: Stress Test + Monitoreo Automático

```bash
# 1. Levantar monitoreo con apps
cd scripts
docker compose -f docker-compose.monitor.yml --profile with-apps up -d

# 2. Ejecutar stress test
TEST_SCRIPT=stress-test.js docker compose -f docker-compose.stress.yml run --rm k6

# 3. Ver impacto en tiempo real
# Grafana: http://localhost:3300
# K6 Report: http://localhost:5666/k6-dashboard
```

---

## 📈 Métricas Disponibles

### Backend Métricas (puertos 9464, 9465)

```
# HELP http_requests_total Total de requests HTTP
# TYPE http_requests_total counter

# HELP http_request_duration_seconds Duración de requests
# TYPE http_request_duration_seconds histogram

# HELP http_requests_in_flight Requests activos
# TYPE http_requests_in_flight gauge
```

### Frontend Métricas (puertos 9466, 9467)

```
# HELP vite_dev_server_requests Total de requests al dev server
# TYPE vite_dev_server_requests counter

# HELP node_process_cpu_seconds_total CPU usage
# TYPE node_process_cpu_seconds_total counter

# HELP node_process_memory_bytes Memoria usada
# TYPE node_process_memory_bytes gauge
```

### Acceso a Métricas Raw

```bash
# Backend v5 (Pharmacy)
curl http://localhost:9464/metrics

# Backend v4 (Ensurance)
curl http://localhost:9465/metrics

# Frontend Ensurance
curl http://localhost:9466/metrics

# Frontend Pharmacy
curl http://localhost:9467/metrics
```

---

## 🌐 URLs de Acceso

### Monitoreo

| Servicio | URL | Credenciales |
|----------|-----|--------------|
| **Grafana** | http://localhost:3300 | admin / changeme |
| **Prometheus** | http://localhost:9095 | - |
| **CheckMK** | http://localhost:5150 | ensurance / changeme |
| **Pushgateway** | http://localhost:9091 | - |

### Aplicaciones (cuando se levantan con monitoring)

| App | URL |
|-----|-----|
| **Ensurance Frontend** | http://localhost:3000 |
| **Pharmacy Frontend** | http://localhost:3001 |
| **Backend v4 (Ensurance)** | http://localhost:3002/api |
| **Backend v5 (Pharmacy)** | http://localhost:3003/api2 |

### Métricas

| Servicio | URL |
|----------|-----|
| **Backend v5** | http://localhost:9464/metrics |
| **Backend v4** | http://localhost:9465/metrics |
| **Frontend Ensurance** | http://localhost:9466/metrics |
| **Frontend Pharmacy** | http://localhost:9467/metrics |

---

## 🛠️ Comandos Útiles

### Verificar Estado

```bash
# Ver qué contenedores están corriendo
docker ps | grep ensurance

# Verificar métricas disponibles
curl http://localhost:9464/metrics | grep "# HELP"

# Ver targets en Prometheus
# http://localhost:9095/targets
```

### Logs

```bash
# Ver logs de monitoreo
cd scripts
docker compose -f docker-compose.monitor.yml logs -f

# Ver logs solo de Prometheus
docker compose -f docker-compose.monitor.yml logs -f prometheus

# Ver logs de apps (si levantadas con monitoreo)
docker logs ensurance-pharmacy-monitoring
```

### Detener Servicios

```bash
# Detener solo monitoreo
cd scripts
docker compose -f docker-compose.monitor.yml down

# Detener monitoreo + apps
docker compose -f docker-compose.monitor.yml --profile with-apps down

# Detener apps DEV
docker compose -f docker-compose.dev.yml down

# Detener TODO
docker compose -f docker-compose.dev.yml down
docker compose -f docker-compose.monitor.yml --profile with-apps down
```

---

## 🆘 Troubleshooting

### Problema: "Prometheus no muestra métricas"

**Verificar:**
```bash
# 1. ¿Apps están corriendo?
curl http://localhost:3002/api/users
curl http://localhost:3003/api2/users

# 2. ¿Métricas están disponibles?
curl http://localhost:9464/metrics
curl http://localhost:9465/metrics

# 3. ¿Prometheus puede ver los targets?
# Ir a: http://localhost:9095/targets
# Todos deben estar "UP" (verde)
```

### Problema: "Port already in use"

**Causa:** Ya hay instancias corriendo en los puertos

**Solución:**
```bash
# Opción 1: Usar el script (detecta automáticamente)
./start-monitoring.sh

# Opción 2: Detener instancias anteriores
docker compose -f docker-compose.dev.yml down
docker compose -f docker-compose.monitor.yml --profile with-apps down
```

### Problema: "No data in Grafana"

**Verificar flujo completo:**
```bash
# 1. Apps corriendo
docker ps | grep "ensurance\|pharmacy"

# 2. Métricas respondiendo
curl http://localhost:9464/metrics

# 3. Prometheus targets UP
# http://localhost:9095/targets

# 4. Query en Prometheus funciona
# http://localhost:9095/graph
# Query: http_requests_total

# 5. Datasource en Grafana configurado
# http://localhost:3300/datasources
```

---

## ✅ Checklist de Integración

- [x] Docker compose actualizado con servicio de apps opcional
- [x] Prometheus configurado para monitorear instancias correctas
- [x] Script de inicio automático con detección de instancias
- [x] Documentación actualizada en COMANDOS_RAPIDOS.md
- [x] Mismo conjunto de puertos para stress tests y monitoreo
- [x] Networks compartidas para comunicación entre servicios
- [x] Extra hosts configurados para host.docker.internal
- [x] Profiles para levantar apps opcionalmente

---

## 🎉 Beneficios

1. **✅ Unificación:** Stress tests y monitoreo usan las mismas instancias
2. **✅ Flexibilidad:** Detecta instancias existentes o levanta propias
3. **✅ Simplicidad:** Un solo comando (`./start-monitoring.sh`)
4. **✅ Eficiencia:** No duplica recursos innecesariamente
5. **✅ Observabilidad:** Métricas en tiempo real durante stress tests
6. **✅ Consistencia:** Mismo comportamiento en todos los entornos

---

## 📚 Referencias

- `/scripts/docker-compose.monitor.yml` - Configuración de monitoreo
- `/scripts/start-monitoring.sh` - Script de inicio inteligente
- `/monitoring/prometheus/prometheus.yml` - Configuración de Prometheus
- `/COMANDOS_RAPIDOS.md` - Referencia rápida de comandos
- `/stress/K6_SOLUTION_SUMMARY.md` - Documentación de stress tests

---

**Happy Monitoring! 📊**
