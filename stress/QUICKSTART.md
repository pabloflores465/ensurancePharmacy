# Quickstart - Stress Testing

## 🚀 Inicio Rápido (5 minutos)

### 1. Verificar que las apps estén corriendo

```bash
curl http://localhost:3002/api/users  # BackV4
curl http://localhost:3003/api2/users  # BackV5
```

### 2. Iniciar Grafana (Opcional pero recomendado)

```bash
cd scripts
docker compose -f docker-compose.monitor.yml up -d
```

Accede a: http://localhost:3300 (admin/changeme)

### 3. Ejecutar un test

**Opción A - Menu Interactivo:**
```bash
cd stress
./run-tests.sh
```

**Opción B - Comandos directos:**
```bash
cd scripts

# K6 Load Test (5 min)
TEST_SCRIPT=load-test.js docker-compose -f docker-compose.stress.yml run --rm k6

# JMeter Full Test (configurable)
JMETER_PLAN=ensurance-full-test.jmx docker-compose -f docker-compose.stress.yml run --rm jmeter
```

### 4. Ver resultados

**K6 Dashboard (en tiempo real):**
- http://localhost:5665 (mientras corre el test)

**Grafana Dashboard:**
- http://localhost:3300 → "K6 Stress Testing Dashboard"

**JMeter HTML Report:**
```bash
cd stress
./view-jmeter-report.sh
```
Abre: http://localhost:8080

---

## 📊 Tests Disponibles

| Test | Herramienta | Duración | Objetivo |
|------|-------------|----------|----------|
| Load Test | K6 | 5 min | Carga progresiva |
| Stress Test | K6 | 8 min | Encontrar límites |
| Spike Test | K6 | 2.5 min | Picos repentinos |
| Soak Test | K6 | 30 min | Resistencia |
| JMeter Simple | JMeter | < 1 min | Verificación básica |
| JMeter Full | JMeter | Configurable | Test completo |

---

## 🎯 Comandos Útiles

### Ver logs
```bash
docker logs ensurance-k6
docker logs ensurance-jmeter
```

### Limpiar resultados
```bash
cd stress
./cleanup-results.sh
```

### Detener todo
```bash
cd scripts
docker-compose -f docker-compose.stress.yml down
docker-compose -f docker-compose.monitor.yml down
```

### Ver resultados guardados
```bash
# K6
docker run --rm -v scripts_k6_results:/results alpine ls -lh /results

# JMeter
docker run --rm -v scripts_jmeter_results:/results alpine ls -lh /results
```

---

## 🔧 Personalización Rápida

### Cambiar URLs de backend

```bash
BACKV4_URL=http://10.128.0.2:8081 \
BACKV5_URL=http://10.128.0.2:8082 \
TEST_SCRIPT=load-test.js \
docker-compose -f docker-compose.stress.yml run --rm k6
```

### Cambiar parámetros de JMeter

```bash
JMETER_PLAN=ensurance-full-test.jmx \
USERS=200 \
RAMP_TIME=120 \
DURATION=900 \
docker-compose -f docker-compose.stress.yml run --rm jmeter
```

---

## 📈 Métricas Clave

**Busca estos valores en los dashboards:**

✅ **Buenos indicadores:**
- Response Time p95 < 500ms
- Error Rate < 1%
- Throughput estable

⚠️ **Señales de alerta:**
- Response Time p95 > 1000ms
- Error Rate > 5%
- Throughput decreciente
- Memory usage creciente (soak test)

---

## 🆘 Troubleshooting

**Error: Cannot connect to backends**
```bash
# Verifica que estén corriendo
docker ps | grep -E "backv4|backv5"

# O inicia las apps
cd scripts
./start-apps.sh  # (si existe)
```

**Error: Port already in use**
```bash
# Liberar puertos
docker-compose -f docker-compose.stress.yml down
docker-compose -f docker-compose.monitor.yml down
```

**No veo resultados en Grafana**
- Espera 30 segundos después de iniciar Grafana
- Verifica que Prometheus esté corriendo: http://localhost:9095
- Recarga el dashboard

**JMeter no genera reporte**
- Asegúrate de limpiar el volumen antes: `./cleanup-results.sh`
- El puerto cambió de 8080 a 8085 (Jenkins usa 8080)
- Asegúrate que completó sin errores: `docker logs ensurance-jmeter`
- Verifica volumen: `docker volume ls | grep jmeter`

---

## 📚 Documentación Completa

- [Guía Completa de Stress Testing](./STRESS_TESTING_GUIDE.md)
- [JMeter README](./jmeter/README.md)
- [K6 README](./k6/README.md)
