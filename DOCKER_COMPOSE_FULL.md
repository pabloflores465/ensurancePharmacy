# 🐳 Docker Compose Completo - Ensurance Pharmacy

## 📋 Archivos Creados

### 1. `docker-compose.full.yml`
Docker Compose con **TODOS** los servicios:
- Aplicación (Frontends + Backends + Métricas)
- Monitoreo (Prometheus, Grafana, CheckMK)
- CI/CD (Jenkins, SonarQube, Drone)
- Herramientas (Portainer, Reportes)

### 2. `start-docker-full.sh`
Script para levantar todo el sistema.

### 3. `start-docker-metrics.sh`
Script para levantar solo apps con métricas.

---

## 🚀 Uso Rápido

### Opción 1: Sistema Completo
```bash
./start-docker-full.sh
```
Levanta **TODO** (16+ contenedores)

### Opción 2: Solo Apps + Métricas
```bash
./start-docker-metrics.sh
```
Levanta solo aplicaciones con métricas (1 contenedor)

### Opción 3: Manual con Docker Compose
```bash
# Levantar todo
docker compose -f docker-compose.full.yml up -d

# Levantar servicios específicos
docker compose -f docker-compose.full.yml up -d ensurance-pharmacy-apps prometheus grafana

# Ver logs
docker compose -f docker-compose.full.yml logs -f

# Detener
docker compose -f docker-compose.full.yml down
```

---

## 🌐 Puertos Principales

### Aplicación
- **3100** - Frontend Ensurance
- **3101** - Frontend Pharmacy  
- **3102** - Backend Ensurance (API)
- **3103** - Backend Pharmacy (API)

### Métricas Prometheus
- **9470** - BackV5 Métricas
- **9471** - BackV4 Métricas
- **9472** - Ensurance Frontend Métricas
- **9473** - Pharmacy Frontend Métricas

### Monitoreo
- **9090** - Prometheus
- **3302** - Grafana (admin/changeme)
- **5152** - CheckMK (cmkadmin/changeme)
- **9093** - Pushgateway
- **9102** - Node Exporter

### CI/CD
- **8080** - Jenkins
- **9000** - SonarQube (admin/admin)
- **8002** - Drone

### Herramientas
- **60002** - Portainer (HTTPS)
- **60003** - Portainer Web UI
- **5668** - K6 Reports
- **8086** - JMeter Reports

---

## 💾 Volúmenes

### Sistema Completo (_full)
- `ensurance-databases-full`
- `ensurance-logs-full`
- `ensurance-prometheus-data-full`
- `ensurance-grafana-data-full`
- `ensurance-jenkins-home-full`
- Y más... (ver docker-compose.full.yml)

### Sistema Métricas (_metrics)
- `ensurance-databases-metrics`
- `ensurance-logs-metrics`
- `ensurance-prometheus-data-metrics` (opcional)
- `ensurance-grafana-data-metrics` (opcional)

---

## 🛠️ Comandos Útiles

### Ver Estado
```bash
docker ps | grep ensurance
docker compose -f docker-compose.full.yml ps
docker stats
```

### Ver Logs
```bash
# Compose full
docker compose -f docker-compose.full.yml logs -f

# Servicio específico
docker compose -f docker-compose.full.yml logs -f ensurance-pharmacy-apps

# Script métricas
docker logs -f ensurance-pharmacy-metrics
```

### Reiniciar
```bash
# Reiniciar servicio
docker compose -f docker-compose.full.yml restart ensurance-pharmacy-apps

# Rebuild servicio
docker compose -f docker-compose.full.yml up -d --build ensurance-pharmacy-apps
```

### Detener
```bash
# Detener todo (mantiene volúmenes)
docker compose -f docker-compose.full.yml down

# Detener y limpiar volúmenes
docker compose -f docker-compose.full.yml down -v

# Script métricas
docker stop ensurance-pharmacy-metrics
```

### Verificar Métricas
```bash
# Verificar un endpoint
curl http://localhost:9470/metrics | head -20

# Verificar todos
for port in 9470 9471 9472 9473; do
  echo "=== Port $port ==="
  curl -s http://localhost:$port/metrics | head -5
  echo
done

# Contar métricas
curl -s http://localhost:9470/metrics | grep -v "^#" | wc -l
```

---

## 🎯 Diferencias Entre Scripts

| Característica | start-docker-full.sh | start-docker-metrics.sh |
|----------------|---------------------|------------------------|
| **Servicios** | Todos (16+) | Solo apps + opcional monitoring |
| **Contenedores** | 16+ contenedores | 1-3 contenedores |
| **RAM** | 4-8 GB | 2-4 GB |
| **Tiempo inicio** | 2-3 minutos | 1-2 minutos |
| **Uso** | Desarrollo completo | Testing de métricas |
| **CI/CD** | ✅ Incluido | ❌ No incluido |
| **Portainer** | ✅ Incluido | ❌ No incluido |

---

## 📊 Integración con Prometheus

Los endpoints de métricas están en los puertos **9464-9467**.

Para que Prometheus los scrape, asegúrate que `monitoring/prometheus/prometheus.yml` incluya:

```yaml
scrape_configs:
  - job_name: 'backv5'
    static_configs:
      - targets: ['host.docker.internal:9464']
  
  - job_name: 'backv4'
    static_configs:
      - targets: ['host.docker.internal:9465']
  
  - job_name: 'ensurance-frontend'
    static_configs:
      - targets: ['host.docker.internal:9466']
  
  - job_name: 'pharmacy-frontend'
    static_configs:
      - targets: ['host.docker.internal:9467']
```

---

## 🔍 Troubleshooting

### Puertos ocupados
```bash
# Ver qué está usando un puerto
lsof -i :3000

# Matar proceso
kill -9 <PID>
```

### Servicio no inicia
```bash
# Ver logs
docker compose -f docker-compose.full.yml logs ensurance-pharmacy-apps

# Verificar healthcheck
docker inspect ensurance-pharmacy-full | grep -A 10 Health
```

### Limpiar todo
```bash
# Detener y limpiar
docker compose -f docker-compose.full.yml down -v

# Limpiar sistema Docker
docker system prune -a --volumes
```

### Rebuild completo
```bash
# Rebuild sin cache
docker compose -f docker-compose.full.yml build --no-cache

# Levantar
docker compose -f docker-compose.full.yml up -d
```

---

## 📝 Notas Importantes

1. **Primera ejecución:** Puede tardar 5-10 minutos por el build de imágenes
2. **Servicios pesados:** Jenkins, SonarQube y CheckMK tardan 1-2 minutos en estar listos
3. **Volúmenes persistentes:** Los datos se mantienen entre reinicios
4. **Red compartida:** Todos los servicios pueden comunicarse entre sí
5. **Métricas expuestas:** Los 4 endpoints de métricas están disponibles en 9464-9467

---

## 🎓 Recomendaciones de Uso

### Para Desarrollo Diario
```bash
./start-docker-metrics.sh
```
Más ligero, solo lo necesario para desarrollar.

### Para Testing Completo
```bash
./start-docker-full.sh
```
Todo el stack, ideal para pruebas de integración.

### Para CI/CD
```bash
docker compose -f docker-compose.full.yml up -d jenkins sonarqube
```
Solo los servicios de CI/CD.

### Para Monitoreo
```bash
docker compose -f docker-compose.full.yml up -d ensurance-pharmacy-apps prometheus grafana
```
Apps + monitoreo sin CI/CD.

---

## 📞 Soporte

Si encuentras problemas:

1. Revisa los logs: `docker compose -f docker-compose.full.yml logs -f`
2. Verifica el estado: `docker compose -f docker-compose.full.yml ps`
3. Revisa este documento para comandos útiles
4. Considera usar `start-docker-metrics.sh` para una configuración más simple
