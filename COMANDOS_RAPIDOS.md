# 🚀 Comandos Rápidos - Ensurance Pharmacy

Referencia rápida de todos los comandos disponibles en el proyecto.

---

## 📦 Docker Compose - Entornos

### 🔵 Desarrollo (DEV)
```bash
# Levantar entorno DEV (BackV4:3002, BackV5:3003)
cd scripts
docker compose -f docker-compose.dev.yml up -d

# Ver logs
docker compose -f docker-compose.dev.yml logs -f

# Detener
docker compose -f docker-compose.dev.yml down

# Verificar endpoints
curl http://localhost:3002/api/users  # BackV4
curl http://localhost:3003/api2/users  # BackV5
```

### 🟢 QA
```bash
# Levantar entorno QA (BackV4:8086, BackV5:8087)
cd scripts
docker compose -f docker-compose.qa.yml up -d

# Ver logs
docker compose -f docker-compose.qa.yml logs -f

# Detener
docker compose -f docker-compose.qa.yml down
```

### 🔴 Main (Producción)
```bash
# Levantar entorno MAIN (BackV4:8081, BackV5:8082)
cd scripts
docker compose -f docker-compose.main.yml up -d

# Ver logs
docker compose -f docker-compose.main.yml logs -f

# Detener
docker compose -f docker-compose.main.yml down
```

### 📊 Monitoring (Grafana + Prometheus)
```bash
# Levantar monitoring stack
cd scripts
docker compose -f docker-compose.monitor.yml up -d

# Acceder a Grafana
# URL: http://localhost:3300
# Usuario: admin
# Password: changeme

# Ver logs
docker compose -f docker-compose.monitor.yml logs -f

# Detener
docker compose -f docker-compose.monitor.yml down

# Prometheus
# URL: http://localhost:9095
```

### 🔥 Stress Testing
```bash
# Levantar JMeter (ejecuta test y termina)
cd scripts

# Test de frontends (Recomendado - Funciona al 100%)
JMETER_PLAN=frontend-test.jmx docker compose -f docker-compose.stress.yml run --rm jmeter

# Test completo (actualizado para frontends)
JMETER_PLAN=ensurance-full-test.jmx docker compose -f docker-compose.stress.yml run --rm jmeter

# JMeter con plan simple
JMETER_PLAN=sample-plan.jmx docker compose -f docker-compose.stress.yml run --rm jmeter

# Levantar servidor de reportes JMeter (después del test)
docker compose -f docker-compose.stress.yml up -d jmeter-report
# Acceder a: http://localhost:8085

# Detener servidor de reportes
docker compose -f docker-compose.stress.yml stop jmeter-report

# Levantar K6 (con --service-ports para exponer dashboard en puerto 5665)
TEST_SCRIPT=load-test.js docker compose -f docker-compose.stress.yml run --rm --service-ports k6

# K6 - Stress Test
TEST_SCRIPT=stress-test.js docker compose -f docker-compose.stress.yml run --rm --service-ports k6

# K6 - Spike Test
TEST_SCRIPT=spike-test.js docker compose -f docker-compose.stress.yml run --rm --service-ports k6

# K6 - Soak Test (30 min)
TEST_SCRIPT=soak-test.js docker compose -f docker-compose.stress.yml run --rm --service-ports k6

# Ver dashboard K6 (después del test)
# Opción 1: Script helper
cd ../stress && ./view-k6-report.sh

# Opción 2: Manual
docker compose -f docker-compose.stress.yml up -d k6-report
# Acceder a: http://localhost:5666/k6-dashboard

# Nota: El dashboard en tiempo real (http://localhost:5665) solo funciona DURANTE el test
# Después del test, usa http://localhost:5666 para ver el dashboard exportado

# Detener servidores de reportes
docker compose -f docker-compose.stress.yml stop k6-report jmeter-report

# Detener servicios de stress
docker compose -f docker-compose.stress.yml down
```

### 🔧 CI/CD
```bash
# Levantar entorno CI/CD
cd scripts
docker compose -f docker-compose.cicd.yml up -d

# Detener
docker compose -f docker-compose.cicd.yml down
```

---

## 🎯 Scripts de Stress Testing

### Menu Interactivo
```bash
cd stress
./run-tests.sh
```

**Opciones disponibles:**
- **1)** Load Test (carga progresiva)
- **2)** Stress Test (hasta límite)
- **3)** Spike Test (picos repentinos)
- **4)** Soak Test (30 min sostenido)
- **5)** JMeter Simple Test
- **6)** JMeter Full Test
- **7)** Iniciar Grafana + Prometheus
- **8)** Verificar Backends
- **9)** Ver reportes JMeter
- **10)** Levantar Backends DEV
- **11)** Detener Backends DEV

### Validar Setup de Stress Testing
```bash
cd stress
./validate-setup.sh
```

### Ver Reportes JMeter (manual)
```bash
cd stress
./view-jmeter-report.sh
# Abre: http://localhost:8085
```

### Limpiar Resultados de Tests
```bash
cd stress
./cleanup-results.sh
```

---

## 🛠️ Scripts de Deployment

### Iniciar Todos los Servicios
```bash
cd scripts
./start-all.sh
```

### Detener Todos los Servicios
```bash
cd scripts
./stop-all.sh
```

### Deploy General
```bash
cd scripts
./deploy.sh
```

---

## 📊 Scripts de Métricas

### Iniciar Todas las Métricas
```bash
cd scripts
./start-all-metrics.sh
```

### Reiniciar BackV5 Metrics
```bash
cd scripts
./restart-backv5-metrics.sh
```

### Jenkins Metrics
```bash
cd scripts
./jenkins-metrics.sh
```

---

## 🧪 Testing

### Test Runner (CI/CD)
```bash
cd scripts
./test-runner.sh
```

---

## 🌐 Networking

### Tailscale Funnel
```bash
cd scripts
./tailscale-funnel.sh
```

---

## 🐳 Comandos Docker Útiles

### Ver Contenedores Activos
```bash
docker ps
```

### Ver Todos los Contenedores
```bash
docker ps -a
```

### Ver Logs de un Contenedor
```bash
# DEV
docker logs -f ensurance-pharmacy-dev

# JMeter
docker logs -f ensurance-jmeter

# K6
docker logs -f ensurance-k6

# JMeter Report Server
docker logs -f ensurance-jmeter-report

# Grafana
docker logs -f grafana

# Prometheus
docker logs -f prometheus
```

### Volúmenes
```bash
# Listar volúmenes
docker volume ls

# Ver volumen de JMeter
docker volume inspect scripts_jmeter_results

# Ver volumen de K6
docker volume inspect scripts_k6_results

# Limpiar volúmenes huérfanos
docker volume prune
```

### Limpiar Sistema Docker
```bash
# Limpiar contenedores detenidos
docker container prune

# Limpiar imágenes no usadas
docker image prune

# Limpiar todo (¡cuidado!)
docker system prune -a
```

---

## 🔍 Health Checks

### Verificar Backends
```bash
# BackV4 DEV (Puerto 3002)
curl http://localhost:3002/api/users

# BackV5 DEV (Puerto 3003)
curl http://localhost:3003/api2/users

# BackV4 MAIN (Puerto 8081)
curl http://localhost:8081/api/users

# BackV5 MAIN (Puerto 8082)
curl http://localhost:8082/api2/users

# BackV4 QA (Puerto 8086)
curl http://localhost:8086/api/users

# BackV5 QA (Puerto 8087)
curl http://localhost:8087/api2/users
```

### Verificar Servicios de Monitoring
```bash
# Grafana
curl http://localhost:3300/api/health

# Prometheus
curl http://localhost:9095/-/healthy

# Prometheus targets
curl http://localhost:9095/api/v1/targets
```

---

## 📊 Dashboards y URLs

| Servicio | URL | Puerto | Credenciales | Contenedor |
|----------|-----|--------|--------------|------------|
| **Grafana** | http://localhost:3300 | 3300 | admin / changeme | grafana |
| **Prometheus** | http://localhost:9095 | 9095 | - | prometheus |
| **K6 Dashboard** | http://localhost:5665 | 5665 | - (solo durante test) | ensurance-k6 |
| **JMeter Report** | http://localhost:8085 | 8085 | - | ensurance-jmeter-report |
| **BackV4 DEV** | http://localhost:3002 | 3002 | - | ensurance-pharmacy-dev |
| **BackV5 DEV** | http://localhost:3003 | 3003 | - | ensurance-pharmacy-dev |
| **BackV4 MAIN** | http://localhost:8081 | 8081 | - | ensurance-pharmacy-main |
| **BackV5 MAIN** | http://localhost:8082 | 8082 | - | ensurance-pharmacy-main |
| **BackV4 QA** | http://localhost:8086 | 8086 | - | ensurance-pharmacy-qa |
| **BackV5 QA** | http://localhost:8087 | 8087 | - | ensurance-pharmacy-qa |

---

## 🔥 Comandos de Emergencia

### Detener Todo
```bash
# Detener todos los contenedores
docker stop $(docker ps -aq)

# O usar el script
cd scripts
./stop-all.sh
```

### Reiniciar Todo
```bash
# Detener y limpiar
cd scripts
./stop-all.sh
docker system prune -f

# Levantar de nuevo
./start-all.sh
```

### Ver Uso de Recursos
```bash
# Ver stats en tiempo real
docker stats

# Ver uso de disco
docker system df
```

---

## 📝 Variables de Entorno

### Stress Testing
```bash
# JMeter
export JMETER_PLAN=ensurance-full-test.jmx
export USERS=100
export RAMP_TIME=60
export DURATION=600
export BACKV4_URL=http://host.docker.internal:3002
export BACKV5_URL=http://host.docker.internal:3003

# K6
export TEST_SCRIPT=load-test.js
export BACKV4_URL=http://host.docker.internal:3002
export BACKV5_URL=http://host.docker.internal:3003
```

### Usar variables
```bash
cd scripts
JMETER_PLAN=ensurance-full-test.jmx USERS=200 DURATION=900 \
  docker compose -f docker-compose.stress.yml run --rm -p 8085:8085 jmeter
```

---

## 📚 Documentación Adicional

### Stress Testing
- **[stress/README.md](stress/README.md)** - Introducción
- **[stress/QUICKSTART.md](stress/QUICKSTART.md)** - Inicio rápido (5 min)
- **[stress/STRESS_TESTING_GUIDE.md](stress/STRESS_TESTING_GUIDE.md)** - Guía completa
- **[stress/EXAMPLES.md](stress/EXAMPLES.md)** - Ejemplos detallados
- **[stress/IMPLEMENTATION_SUMMARY.md](stress/IMPLEMENTATION_SUMMARY.md)** - Resumen técnico

### Configuración
- **[documentation/STRESS_TESTING_SETUP.md](documentation/STRESS_TESTING_SETUP.md)** - Setup completo

---

## 🎯 Flujos de Trabajo Comunes

### 1. Desarrollo Local
```bash
# 1. Levantar DEV
cd scripts
docker compose -f docker-compose.dev.yml up -d

# 2. Verificar
curl http://localhost:3002/api/users
curl http://localhost:3003/api2/users

# 3. Ver logs
docker compose -f docker-compose.dev.yml logs -f
```

### 2. Testing de Carga
```bash
# 1. Validar setup
cd stress
./validate-setup.sh

# 2. Levantar backends si es necesario
cd ../scripts
docker compose -f docker-compose.dev.yml up -d

# 3. Ejecutar test
cd ../stress
./run-tests.sh
# Seleccionar opción deseada

# 4. Ver métricas
# - K6: http://localhost:5665
# - JMeter: http://localhost:8085
# - Grafana: http://localhost:3300
```

### 3. Monitoreo con Grafana
```bash
# 1. Levantar monitoring
cd scripts
docker compose -f docker-compose.monitor.yml up -d

# 2. Levantar backends
docker compose -f docker-compose.dev.yml up -d

# 3. Acceder a Grafana
# http://localhost:3300 (admin/changeme)

# 4. Ejecutar tests de carga
cd ../stress
./run-tests.sh
```

### 4. Testing Completo (Todos los Entornos)
```bash
# 1. Levantar todos los entornos
cd scripts
docker compose -f docker-compose.dev.yml up -d
docker compose -f docker-compose.qa.yml up -d
docker compose -f docker-compose.main.yml up -d
docker compose -f docker-compose.monitor.yml up -d

# 2. Verificar todos los endpoints
curl http://localhost:3002/api/users   # DEV BackV4
curl http://localhost:3003/api2/users  # DEV BackV5
curl http://localhost:8086/api/users   # QA BackV4
curl http://localhost:8087/api2/users  # QA BackV5
curl http://localhost:8081/api/users   # MAIN BackV4
curl http://localhost:8082/api2/users  # MAIN BackV5

# 3. Ejecutar stress tests contra cada entorno
cd ../stress
# Editar scripts para apuntar a cada entorno según necesidad
```

---

## 🆘 Troubleshooting

### Puerto Ocupado
```bash
# Ver qué proceso usa el puerto
lsof -i :8085

# Matar proceso
kill -9 <PID>

# O detener contenedor
docker stop <container_name>
```

### Contenedor No Inicia
```bash
# Ver logs
docker logs <container_name>

# Inspeccionar contenedor
docker inspect <container_name>

# Reiniciar
docker restart <container_name>
```

### Volumen No Se Actualiza
```bash
# Borrar volumen
docker volume rm scripts_jmeter_results

# Recrear contenedor
cd scripts
docker compose -f docker-compose.stress.yml down
docker compose -f docker-compose.stress.yml up -d
```

### Permisos de Volumen
```bash
# Ver permisos del volumen
docker run --rm -v scripts_jmeter_results:/results alpine ls -la /results

# Arreglar permisos
docker run --rm -v scripts_jmeter_results:/results alpine chmod -R 777 /results
```

---

**Última actualización**: 2025-10-09  
**Versión**: 1.0  
**Proyecto**: Ensurance Pharmacy - Sistema Completo
