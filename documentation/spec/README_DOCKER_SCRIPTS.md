# 🐳 Scripts Docker - Ensurance Pharmacy

## 📝 Resumen

Se han creado **2 scripts principales** y **1 docker-compose completo** para facilitar el inicio del sistema:

### Archivos Creados

1. **`docker-compose.full.yml`** - Compose con todos los servicios
2. **`start-docker-full.sh`** - Script para levantar todo
3. **`start-docker-metrics.sh`** - Script para levantar apps con métricas

---

## 🎯 ¿Cuál Script Usar?

### Usa `start-docker-full.sh` si:
- ✅ Necesitas todo el stack completo
- ✅ Vas a trabajar con CI/CD (Jenkins, SonarQube, Drone)
- ✅ Necesitas Portainer para gestionar contenedores
- ✅ Tienes suficiente RAM (4-8 GB)
- ✅ Quieres tener todo disponible de una vez

**Servicios incluidos:**
- Aplicaciones (Frontends + Backends)
- Métricas Prometheus (4 endpoints)
- Monitoreo (Prometheus, Grafana, CheckMK, Node Exporter, Pushgateway)
- CI/CD (Jenkins, SonarQube, Drone + Runner)
- Herramientas (Portainer, Servidores de reportes K6/JMeter)

**Comando:**
```bash
./start-docker-full.sh
```

---

### Usa `start-docker-metrics.sh` si:
- ✅ Solo necesitas las aplicaciones
- ✅ Quieres exponer métricas para Prometheus
- ✅ Prefieres un entorno más ligero
- ✅ Tienes RAM limitada (2-4 GB es suficiente)
- ✅ Solo te interesa desarrollo/testing de las apps

**Servicios incluidos:**
- Aplicaciones (Frontends + Backends)
- Métricas Prometheus (4 endpoints: 9464-9467)
- Opcionalmente: Prometheus y Grafana

**Comando:**
```bash
./start-docker-metrics.sh
```

---

## 🚀 Inicio Rápido

### Opción 1: Sistema Completo (Recomendado para setup completo)

```bash
# Dar permisos de ejecución (solo la primera vez)
chmod +x start-docker-full.sh

# Ejecutar
./start-docker-full.sh

# Responder las preguntas:
# - ¿Limpiar volúmenes? → y/n (elige 'n' para mantener datos)
# 
# Espera 2-3 minutos...
# ¡Listo! Todos los servicios están corriendo
```

### Opción 2: Solo Apps con Métricas (Recomendado para desarrollo)

```bash
# Dar permisos de ejecución (solo la primera vez)
chmod +x start-docker-metrics.sh

# Ejecutar
./start-docker-metrics.sh

# Responder las preguntas:
# - ¿Iniciar Prometheus/Grafana? → Y/n (elige 'Y' si quieres monitoreo)
# - ¿Ver logs en tiempo real? → y/N (elige 'y' si quieres ver logs)
#
# Espera 1-2 minutos...
# ¡Listo! Apps con métricas corriendo
```

---

## 📊 Características Principales

### `start-docker-full.sh`

✨ **Funcionalidades:**
- Verifica que Docker esté instalado y corriendo
- Opción de limpiar contenedores/volúmenes existentes
- Build automático de todas las imágenes
- Inicio de 16+ servicios
- Verificación de health de cada servicio
- Progress bar visual durante la espera
- Resumen completo con URLs y credenciales
- Muestra comandos útiles al finalizar

📦 **Lo que levanta:**
- 1 contenedor de aplicaciones (todas las apps en uno)
- 5 contenedores de monitoreo (Prometheus, Grafana, CheckMK, etc.)
- 5 contenedores de CI/CD (Jenkins, SonarQube, Drone, etc.)
- 3 contenedores de herramientas (Portainer, reportes)

🔗 **URLs importantes:**
- Aplicación: http://localhost:3000-3003
- Métricas: http://localhost:9464-9467
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3300
- Jenkins: http://localhost:8080
- SonarQube: http://localhost:9000
- Portainer: https://localhost:60000

---

### `start-docker-metrics.sh`

✨ **Funcionalidades:**
- Verifica Docker
- Crea/usa volúmenes existentes (no los borra)
- Build de imagen con soporte de métricas
- Inicia 1 contenedor con todas las apps
- Opción de iniciar Prometheus y Grafana
- Verificación de endpoints de métricas
- Opción de ver logs en tiempo real
- Instrucciones para integración con Prometheus

📦 **Lo que levanta:**
- 1 contenedor de aplicaciones con métricas habilitadas
- Opcionalmente: Prometheus y Grafana (2 contenedores adicionales)

🔗 **URLs importantes:**
- Aplicación: http://localhost:3000-3003
- Métricas: http://localhost:9464-9467
- Prometheus (opcional): http://localhost:9090
- Grafana (opcional): http://localhost:3300

---

## 💡 Ventajas de estos Scripts

### vs. Levantar Manualmente con npm/mvn

**Antes (Manual):**
```bash
# Terminal 1
cd backv5 && mvn clean package && java -jar target/...

# Terminal 2
cd backv4 && mvn clean package && java -jar target/...

# Terminal 3
cd ensurance && npm install && npm run dev

# Terminal 4
cd pharmacy && npm install && npm run serve

# Terminal 5, 6, 7, 8... para métricas
# 😰 Muchas terminales, difícil de manejar
```

**Ahora (Con Script):**
```bash
./start-docker-metrics.sh
# ✅ Todo en 1 comando, 1 contenedor, fácil de manejar
```

### vs. Docker Compose Manual

**Antes:**
```bash
cd scripts
docker compose -f docker-compose.dev.yml up -d
docker compose -f docker-compose.monitor.yml up -d
docker compose -f docker-compose.cicd.yml up -d
# Recordar todos los archivos, configuraciones...
```

**Ahora:**
```bash
./start-docker-full.sh
# ✅ Todo incluido, con verificaciones y guía visual
```

---

## 🎨 Características Visuales

Ambos scripts incluyen:
- 🎨 **Colores** para fácil lectura
- ✓ **Símbolos visuales** (✓, ✗, ⚠)
- 📊 **Progress bars** durante esperas
- 📋 **Resúmenes organizados** con URLs
- 💡 **Comandos útiles** sugeridos
- 🔍 **Verificación automática** de endpoints

---

## 🛠️ Comandos Post-Inicio

### Ver logs
```bash
# Script full
docker compose -f docker-compose.full.yml logs -f

# Script metrics
docker logs -f ensurance-pharmacy-metrics
```

### Detener servicios
```bash
# Script full
docker compose -f docker-compose.full.yml down

# Script metrics
docker stop ensurance-pharmacy-metrics
docker stop ensurance-prometheus-metrics ensurance-grafana-metrics
```

### Reiniciar un servicio
```bash
# Script full
docker compose -f docker-compose.full.yml restart ensurance-pharmacy-apps

# Script metrics
docker restart ensurance-pharmacy-metrics
```

---

## 📁 Volúmenes Persistentes

### Sistema Full
Todos los volúmenes con sufijo `_full`:
- `ensurance-databases-full`
- `ensurance-logs-full`
- `ensurance-prometheus-data-full`
- `ensurance-grafana-data-full`
- Y más...

### Sistema Metrics
Volúmenes con sufijo `_metrics`:
- `ensurance-databases-metrics`
- `ensurance-logs-metrics`
- `ensurance-prometheus-data-metrics` (opcional)
- `ensurance-grafana-data-metrics` (opcional)

**Ventaja:** Los datos persisten entre reinicios. No pierdes información.

---

## 🔍 Verificación de Métricas

Ambos scripts exponen métricas en **puertos 9464-9467**.

### Verificar que funcionan:
```bash
# Verificar un endpoint
curl http://localhost:9464/metrics

# Verificar todos
for port in 9464 9465 9466 9467; do
  echo "=== Puerto $port ==="
  curl -s http://localhost:$port/metrics | head -5
  echo
done

# Contar métricas disponibles
curl -s http://localhost:9464/metrics | grep -v "^#" | wc -l
```

### Integración con Prometheus

Si usas Prometheus externo o el incluido, configura estos targets en `prometheus.yml`:

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

## 🆚 Comparación Rápida

| Aspecto | start-docker-full.sh | start-docker-metrics.sh |
|---------|---------------------|------------------------|
| **Contenedores** | 16+ | 1-3 |
| **RAM necesaria** | 4-8 GB | 2-4 GB |
| **Tiempo inicio** | 2-3 min | 1-2 min |
| **Apps incluidas** | ✅ | ✅ |
| **Métricas** | ✅ | ✅ |
| **Prometheus** | ✅ | ⚠️ Opcional |
| **Grafana** | ✅ | ⚠️ Opcional |
| **CI/CD** | ✅ | ❌ |
| **Portainer** | ✅ | ❌ |
| **Reportes** | ✅ | ❌ |
| **Uso recomendado** | Setup completo | Desarrollo diario |

---

## 📚 Documentación Adicional

- **`DOCKER_COMPOSE_FULL.md`** - Documentación completa y detallada
- **`DOCKER_QUICK_REFERENCE.md`** - Referencia rápida de comandos
- **`COMANDOS_RAPIDOS.md`** - Actualizado con referencias a estos scripts

---

## 💪 Ventajas de usar Docker

1. **Portabilidad** - Funciona igual en cualquier máquina
2. **Aislamiento** - No conflictos con otros proyectos
3. **Reproducibilidad** - Mismo entorno para todos
4. **Fácil cleanup** - Un comando para detener todo
5. **Persistencia** - Los datos se guardan en volúmenes
6. **Escalabilidad** - Fácil agregar más servicios

---

## 🎓 Recomendaciones

### Para empezar
```bash
./start-docker-metrics.sh
```
Es más ligero y suficiente para desarrollo.

### Para testing completo
```bash
./start-docker-full.sh
```
Cuando necesites todo el ecosistema.

### Para producción
Usa los docker-compose específicos:
- `docker-compose.main.yml` para producción
- `docker-compose.qa.yml` para QA

---

## ❓ FAQ

**Q: ¿Por qué mis volúmenes no se limpian?**  
A: Por defecto, los scripts mantienen los volúmenes para no perder datos. Usa `docker compose down -v` para limpiar.

**Q: ¿Puedo usar ambos scripts al mismo tiempo?**  
A: No recomendado. Los puertos se solaparían. Usa uno u otro.

**Q: ¿Cómo actualizo las aplicaciones?**  
A: Haz cambios en tu código, luego ejecuta el script nuevamente. Docker rebuild automáticamente.

**Q: ¿Las métricas funcionan sin Prometheus?**  
A: Sí, los endpoints están expuestos y puedes consultarlos con curl o apuntarles un Prometheus externo.

**Q: ¿Qué pasa si un puerto está ocupado?**  
A: El script fallará. Usa `lsof -i :<puerto>` para ver qué lo usa y ciérralo.

---

## 🎉 ¡Listo para Usar!

```bash
# Prueba primero el ligero
./start-docker-metrics.sh

# Luego, cuando necesites todo
./start-docker-full.sh
```

¡Disfruta del desarrollo sin complicaciones! 🚀
