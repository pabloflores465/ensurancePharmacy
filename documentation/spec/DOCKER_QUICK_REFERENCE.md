# 🚀 Docker Quick Reference - Ensurance Pharmacy

## Comandos de Inicio Rápido

```bash
# TODO el sistema (16+ servicios)
./start-docker-full.sh

# Solo apps + métricas (ligero)
./start-docker-metrics.sh

# Manual - Todo
docker compose -f docker-compose.full.yml up -d

# Manual - Solo apps
docker compose -f docker-compose.full.yml up -d ensurance-pharmacy-apps
```

## URLs Esenciales

```
Aplicación:
http://localhost:3100  → Frontend Ensurance
http://localhost:3101  → Frontend Pharmacy
http://localhost:3102  → API Ensurance
http://localhost:3103  → API Pharmacy

Métricas:
http://localhost:9470/metrics  → BackV5
http://localhost:9471/metrics  → BackV4
http://localhost:9472/metrics  → Ensurance Frontend
http://localhost:9473/metrics  → Pharmacy Frontend

Monitoreo:
http://localhost:9090  → Prometheus
http://localhost:3302  → Grafana (admin/changeme)

CI/CD:
http://localhost:8080  → Jenkins
http://localhost:9000  → SonarQube (admin/admin)
http://localhost:8002  → Drone

Herramientas:
https://localhost:60002  → Portainer
http://localhost:15674  → RabbitMQ Management (admin/changeme)
http://localhost:5668   → K6 Reports
http://localhost:8086   → JMeter Reports
```

## Comandos de Gestión

```bash
# VER ESTADO
docker ps | grep ensurance
docker compose -f docker-compose.full.yml ps

# VER LOGS
docker compose -f docker-compose.full.yml logs -f
docker logs -f ensurance-pharmacy-metrics

# REINICIAR
docker compose -f docker-compose.full.yml restart
docker restart ensurance-pharmacy-metrics

# DETENER
docker compose -f docker-compose.full.yml down
docker stop ensurance-pharmacy-metrics

# LIMPIAR TODO
docker compose -f docker-compose.full.yml down -v
```

## Verificación de Métricas

```bash
# Ver métricas de un servicio
curl http://localhost:9464/metrics | head -20

# Ver todas las métricas
for port in 9464 9465 9466 9467; do
  echo "=== Port $port ==="
  curl -s http://localhost:$port/metrics | head -5
done

# Contar métricas disponibles
curl -s http://localhost:9464/metrics | grep -v "^#" | wc -l
```

## Troubleshooting Rápido

```bash
# Puerto ocupado
lsof -i :3000
kill -9 <PID>

# Rebuild servicio
docker compose -f docker-compose.full.yml up -d --build ensurance-pharmacy-apps

# Limpiar todo Docker
docker system prune -a --volumes

# Ver health check
docker inspect ensurance-pharmacy-full | grep -A 10 Health
```

## Scripts Disponibles

| Script | Descripción | Contenedores | RAM |
|--------|-------------|--------------|-----|
| `start-docker-full.sh` | Sistema completo | 16+ | 4-8 GB |
| `start-docker-metrics.sh` | Apps + métricas | 1-3 | 2-4 GB |

## Volúmenes

```bash
# Listar volúmenes
docker volume ls | grep ensurance

# Ver detalles
docker volume inspect ensurance-databases-full

# Backup volumen
docker run --rm -v ensurance-databases-full:/data -v $(pwd):/backup alpine tar czf /backup/databases-backup.tar.gz /data
```

## Diferencia Entre Modos

**Full** → Desarrollo completo con CI/CD
**Metrics** → Solo apps con métricas para testing
