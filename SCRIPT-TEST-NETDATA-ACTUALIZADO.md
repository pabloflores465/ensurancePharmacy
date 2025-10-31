# ✅ Script test-alertas-interactivo.sh Actualizado

**Fecha:** 31 de Octubre, 2025 - 04:05 AM  
**Cambio:** Reemplazado RabbitMQ por Netdata

---

## 🔄 Cambios Realizados

### 1. Menú Principal Actualizado

**Antes:**
```
3) Alertas de RabbitMQ (12 alertas)
   - RabbitMQ Down, Queues, Memory, Connections
```

**Ahora:**
```
3) Alertas de Netdata (12 alertas)
   - Netdata Down, Temperatura CPU, Zombies, Swap, I/O, Network
```

### 2. Nueva Función: test_netdata()

Reemplaza completamente la función `test_rabbitmq()` con 12 nuevos tests:

#### Tests Implementados

| ID | Alerta | Tipo de Test | Duración |
|----|--------|-------------|----------|
| 21 | NetdataDown | Detener contenedor | 2 min |
| 22 | HighCPUTemperature | Manual (sensores hardware) | - |
| 23 | ZombieProcesses | Crear 15 procesos zombie | 11 min |
| 24 | TooManyProcesses | Crear 300 procesos sleep | 6 min |
| 25 | SwapUsage | stress-ng vm 95% | 11 min |
| 26 | HighDiskIO | stress-ng io+hdd | 6 min |
| 27 | MemoryFragmentation | Manual | - |
| 28 | DiskReadErrors | Manual (no forzable) | - |
| 29 | SuspiciousNetworkConnections | 1500 conexiones curl | 6 min |
| 30 | FrequentServiceRestarts | Manual (systemd) | - |
| 31 | RapidLogGrowth | 10000 líneas de log | 11 min |
| 32 | HighNetworkLatency | Manual (ping config) | - |

**Tiempo estimado:** ~50-60 minutos (tests automáticos)

### 3. Función test_monitoreo() Actualizada

- ❌ Eliminado test de NetdataDown (movido a categoría Netdata)
- ✅ Actualizado contador de 13 a 11 alertas
- ✅ Actualizada descripción del menú

### 4. Sistema de Selección Actualizado

- ✅ Opción 3 ahora selecciona "Netdata" en lugar de "RabbitMQ"
- ✅ Opción 7 (TODAS) incluye Netdata en lugar de RabbitMQ
- ✅ Switch de ejecución actualizado para llamar `test_netdata()`

---

## 🧪 Tests por Categoría (Actualizado)

### Resumen Total

| Categoría | Alertas | Tests Automáticos | Tests Manuales | Tiempo |
|-----------|---------|-------------------|----------------|--------|
| 1. Sistema | 12 | 11 | 1 | ~25 min |
| 2. Aplicaciones | 8 | 4 | 4 | ~10 min |
| 3. **Netdata** | **12** | **6** | **6** | **~50 min** |
| 4. K6 | 8 | 8 | 0 | ~15 min |
| 5. CI/CD | 12 | 8 | 4 | ~45 min |
| 6. Monitoreo | 11 | 5 | 6 | ~15 min |
| **TOTAL** | **63** | **42** | **21** | **~160 min** |

---

## 🚀 Cómo Usar el Script Actualizado

### Ejecutar el Script

```bash
cd /home/pablopolis2016/Documents/ensurancePharmacy
./test-alertas-interactivo.sh
```

### Menú Interactivo

```
🧪 SELECCIONA CATEGORÍAS DE ALERTAS
==========================================

1) Alertas de Sistema (12 alertas)
2) Alertas de Aplicaciones (8 alertas)
3) Alertas de Netdata (12 alertas)      ← NUEVO
4) Alertas de K6 (8 alertas)
5) Alertas de CI/CD (12 alertas)
6) Alertas de Monitoreo (11 alertas)    ← Actualizado
7) TODAS LAS CATEGORÍAS
0) Finalizar selección y comenzar pruebas
```

### Probar Solo Netdata

1. Ejecutar script: `./test-alertas-interactivo.sh`
2. Seleccionar opción `3` (Netdata)
3. Seleccionar opción `0` (Comenzar)
4. Confirmar con `y`
5. Esperar ~50-60 minutos

---

## 📋 Detalles de los Tests de Netdata

### Tests Automáticos (6)

#### Test 21: NetdataDown
```bash
docker compose -f docker-compose.full.yml stop netdata
# Esperar 90 segundos
docker compose -f docker-compose.full.yml start netdata
```
**Resultado:** Alerta NetdataDown se dispara

#### Test 23: ZombieProcesses
```bash
for i in {1..15}; do
    (bash -c 'sleep 0.1 & exec /bin/true') &
done
```
**Resultado:** Crea 15 procesos zombie, alerta tras 10 minutos

#### Test 24: TooManyProcesses
```bash
for i in {1..300}; do
    (sleep 600 &)
done
```
**Resultado:** 300 procesos en background, alerta tras 5 minutos

#### Test 25: SwapUsage
```bash
stress-ng --vm 4 --vm-bytes 95% --timeout 600s
```
**Resultado:** Fuerza uso de swap, alerta tras 10 minutos

#### Test 26: HighDiskIO
```bash
stress-ng --io 16 --hdd 4 --timeout 360s
```
**Resultado:** I/O intensivo de disco, alerta tras 5 minutos

#### Test 29: SuspiciousNetworkConnections
```bash
for i in {1..1500}; do
    (curl -s http://localhost:9090 > /dev/null 2>&1 &)
done
```
**Resultado:** 1500 conexiones concurrentes, alerta tras 5 minutos

#### Test 31: RapidLogGrowth
```bash
for i in {1..10000}; do
    echo "Test log message $i $(date)" >> /tmp/test_rapid_logs.log
done
```
**Resultado:** 10000 líneas de log rápidamente, alerta tras 10 minutos

### Tests Manuales (6)

Estos requieren configuración especial o no se pueden simular de forma segura:

- **HighCPUTemperature:** Requiere sensores de temperatura hardware
- **MemoryFragmentation:** Requiere análisis específico de fragmentación
- **DiskReadErrors:** NO se puede forzar (daño de hardware)
- **FrequentServiceRestarts:** Requiere monitoreo de systemd activo
- **HighNetworkLatency:** Requiere configuración de ping targets en Netdata

---

## ⚠️ Advertencias Importantes

### 1. Tests Intensivos
Los tests de Netdata son **más intensivos** que los de RabbitMQ:
- Uso alto de RAM (95%)
- Creación de muchos procesos
- I/O intensivo de disco
- Múltiples conexiones de red

**Recomendación:** Ejecutar en entorno de test, no en producción

### 2. Limpieza Automática
El script hace limpieza automática:
- ✅ Mata procesos stress-ng
- ✅ Mata procesos sleep
- ✅ Elimina archivos de log temporales

### 3. Tiempo de Espera
Los tests de Netdata toman más tiempo:
- RabbitMQ: ~30-40 minutos
- **Netdata: ~50-60 minutos**

### 4. Recursos del Sistema
Durante los tests, el sistema experimentará:
- Alta carga de CPU
- Uso alto de RAM
- Swap activo
- I/O de disco saturado
- Muchas conexiones de red

**Normal y esperado** - El sistema debería recuperarse al finalizar

---

## 📊 Comparación: RabbitMQ vs Netdata

| Aspecto | RabbitMQ | Netdata |
|---------|----------|---------|
| Alertas | 12 | 12 |
| Tests auto | 8 | 6 |
| Tests manual | 4 | 6 |
| Tiempo | ~35 min | ~55 min |
| Intensidad | Media | Alta |
| Dependencias | RabbitMQ container | Sensores, systemd |
| Utilidad | Mensajería | Sistema completo |

### Por Qué el Cambio

✅ **Netdata es más útil porque:**
- Monitorea el sistema real (CPU, RAM, disco, red)
- Detecta problemas de hardware
- Identifica amenazas de seguridad
- No depende de un servicio específico
- Cubre más aspectos del sistema

❌ **RabbitMQ era limitado:**
- Solo útil si usas RabbitMQ
- No teníamos RabbitMQ en uso activo
- Alertas muy específicas de colas
- Menos relevante para monitoreo general

---

## 🔍 Verificar Resultados

### Durante los Tests

```bash
# Ver alertas activas en Prometheus
curl -s http://localhost:9090/api/v1/alerts | jq '.data.alerts | length'

# Ver alertas específicas de Netdata
curl -s http://localhost:9090/api/v1/alerts | \
  jq '.data.alerts[] | select(.labels.alertname | contains("Netdata"))'
```

### Después de los Tests

```bash
# Verificar emails recibidos
# - Inbox: pablopolis2016@gmail.com
# - Inbox: jflores@unis.edu.gt

# Verificar Slack
# - Canal: #ensurance-alerts
# - Buscar mensajes de las últimas 2 horas
```

### Ver Métricas en Netdata

```bash
# Abrir Netdata UI
xdg-open http://localhost:19999

# Verificar:
# - System > Processes (zombies, total)
# - Memory > Swap
# - Disk > I/O
# - Network > Connections
```

---

## 🛠️ Troubleshooting

### Problema: Tests no generan alertas

**Causa:** Umbrales no alcanzados o tiempo insuficiente

**Solución:**
```bash
# Verificar métricas actuales en Prometheus
curl -s http://localhost:9090/api/v1/query?query=netdata_system_processes_zombies_processes

# Verificar reglas de alertas
curl -s http://localhost:9090/api/v1/rules | jq '.data.groups[] | select(.name=="netdata_system_health")'
```

### Problema: Sistema muy lento durante tests

**Causa:** Tests de Netdata son intensivos

**Solución:**
- Normal y esperado
- Esperar a que terminen los tests
- El sistema se recuperará automáticamente
- No interrumpir mid-test

### Problema: Netdata no inicia después del test

**Solución:**
```bash
docker logs ensurance-netdata-full --tail 50
docker restart ensurance-netdata-full
```

---

## ✅ Checklist de Actualización

- [x] Función `test_rabbitmq()` reemplazada por `test_netdata()`
- [x] Menú actualizado (opción 3)
- [x] Sistema de selección actualizado
- [x] Switch de ejecución actualizado
- [x] NetdataDown eliminado de test_monitoreo()
- [x] Contador de alertas de monitoreo corregido (11)
- [x] Script hecho ejecutable
- [x] Documentación creada

---

## 📚 Archivos Relacionados

- **Script:** `/home/pablopolis2016/Documents/ensurancePharmacy/test-alertas-interactivo.sh`
- **Alertas Netdata:** `/home/pablopolis2016/Documents/ensurancePharmacy/monitoring/prometheus/rules/netdata_alerts.yml`
- **Config Prometheus:** `/home/pablopolis2016/Documents/ensurancePharmacy/monitoring/prometheus/prometheus.yml`

---

**Script actualizado y listo para usar!** 🎉  
**Total de alertas en el sistema: 63**  
**Categorías: 6**  
**Tests disponibles: 42 automáticos + 21 manuales**
