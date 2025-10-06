# 📝 Comandos Git para Push de Cambios

## ✅ Cambios Realizados

Se reorganizó completamente el proyecto:
- ✅ Creadas carpetas `scripts/` y `documentation/`
- ✅ Agregadas métricas de Jenkins al pipeline
- ✅ Actualizado README con nueva estructura
- ✅ Agregado Pushgateway para métricas de Jenkins
- ✅ Documentación completa de métricas (62 KB)

---

## 🚀 Comandos para Commit y Push

### Opción 1: Commit Todo junto (Recomendado)

```bash
# Ver archivos modificados
git status

# Agregar todos los cambios
git add .

# Commit con mensaje descriptivo
git commit -m "feat: Reorganize project structure and add Jenkins metrics

- Move all scripts to scripts/ directory
  * docker-compose.*.yml files
  * deploy.sh, test-runner.sh
  * jenkins-metrics.sh (NEW)
  * start-all-metrics.sh

- Move all documentation to documentation/ directory
  * JENKINS_METRICS_GUIDE.md (NEW)
  * JENKINS_METRICS_SUMMARY.md (NEW)
  * JENKINS_PROMETHEUS_QUERIES.md (NEW)
  * METRICS_SETUP.md (NEW)
  * METRICS_STATUS.md (NEW)
  * PROMETHEUS_QUERIES.md (NEW)
  * Jenkinsfile examples (NEW)

- Update Jenkinsfile with Prometheus metrics
  * Add Initialize Metrics stage
  * Track duration per stage
  * Report custom metrics
  * Update paths to scripts/*

- Update README.md
  * Add Project Structure section
  * Add Documentation section
  * Update all docker-compose paths
  * Update all script paths

- Add Pushgateway to monitoring stack
  * scripts/docker-compose.monitor.yml updated
  * Port 9091 exposed

- Update Prometheus configuration
  * Add jenkins-pipeline target
  * Update IPs to 10.128.0.2 (Linux compatible)

Implements 4 key Jenkins metrics:
- jenkins_job_duration_seconds
- jenkins_job_status
- jenkins_builds_total
- jenkins_queue_time_seconds"

# Push a la rama actual
git push origin $(git branch --show-current)
```

### Opción 2: Commit Separado por Feature

```bash
# 1. Reorganización de estructura
git add scripts/ documentation/
git commit -m "refactor: reorganize project structure into scripts/ and documentation/"

# 2. Métricas de Jenkins
git add scripts/jenkins-metrics.sh scripts/docker-compose.monitor.yml monitoring/prometheus/prometheus.yml
git commit -m "feat: add Jenkins pipeline metrics with Prometheus"

# 3. Actualizar Jenkinsfile
git add Jenkinsfile
git commit -m "feat: instrument Jenkinsfile with Prometheus metrics"

# 4. Actualizar documentación
git add README.md documentation/*.md
git commit -m "docs: update README and add comprehensive metrics documentation"

# 5. Push todos los commits
git push origin $(git branch --show-current)
```

---

## 🔍 Verificar Antes del Push

```bash
# Ver archivos que se van a commitear
git status

# Ver diferencias
git diff

# Ver archivos en staging
git diff --staged

# Ver estructura de carpetas
tree -L 2 scripts/ documentation/

# Verificar que los scripts son ejecutables
ls -la scripts/*.sh
```

---

## 📊 Verificar Métricas Después del Push

### 1. Pull del repositorio en Jenkins

```bash
# Jenkins hará pull automáticamente, pero puedes forzarlo
cd /var/jenkins_home/workspace/<tu-job>
git pull
```

### 2. Verificar que Pushgateway está corriendo

```bash
docker ps | grep pushgateway
curl http://localhost:9091/metrics
```

### 3. Ejecutar un build de prueba

```bash
# Desde Jenkins UI o CLI
jenkins-cli build <tu-job>
```

### 4. Ver métricas en Prometheus

```bash
# Verificar targets
curl http://localhost:9095/api/v1/targets | grep jenkins

# Ver métricas
curl http://localhost:9095/api/v1/query?query=jenkins_job_duration_seconds
```

---

## ⚠️ Troubleshooting

### Problema: Archivos no se movieron correctamente

```bash
# Ver historial de movimientos
git log --follow -- scripts/deploy.sh

# Forzar add de archivos movidos
git add -A
```

### Problema: Conflictos de merge

```bash
# Ver archivos en conflicto
git status

# Resolver manualmente y luego
git add <archivo-resuelto>
git commit
```

### Problema: Olvidaste agregar un archivo

```bash
# Agregar archivo olvidado al último commit
git add <archivo-olvidado>
git commit --amend --no-edit

# Push con force (solo si no has compartido el commit)
git push --force-with-lease
```

---

## 📝 Mensaje de Commit Alternativo (Corto)

Si prefieres un mensaje más corto:

```bash
git commit -m "Reorganize: scripts/ and documentation/ folders + Jenkins metrics

- Create scripts/ folder for all automation scripts
- Create documentation/ folder for all docs
- Add Jenkins metrics with Pushgateway
- Update Jenkinsfile with metric tracking
- Update README with new structure"
```

---

## 🎯 Checklist Pre-Push

- [ ] Todos los archivos están en las carpetas correctas
- [ ] README actualizado con nuevas rutas
- [ ] Jenkinsfile actualizado con rutas a `scripts/`
- [ ] Documentación completa en `documentation/`
- [ ] Scripts tienen permisos de ejecución
- [ ] Pushgateway configurado
- [ ] Prometheus configurado con target Jenkins
- [ ] No hay archivos sensibles (passwords, keys)
- [ ] Mensaje de commit es descriptivo

---

## 🚀 Comando Final (Copy-Paste Ready)

```bash
git add . && \
git commit -m "feat: Reorganize project structure and add Jenkins metrics

- Move scripts to scripts/ directory
- Move documentation to documentation/ directory  
- Add Jenkins metrics with Prometheus
- Update Jenkinsfile with metric tracking
- Update README with new structure

Implements 4 key metrics: duration, status, builds total, queue time" && \
git push origin $(git branch --show-current)
```

---

## 📚 Referencias

- **Jenkins Metrics Guide**: `documentation/JENKINS_METRICS_GUIDE.md`
- **Reorganization Summary**: `REORGANIZATION_SUMMARY.md`
- **Updated README**: `README.md`
- **Jenkinsfile Example**: `documentation/Jenkinsfile.metrics.example`

---

**¡Todo listo para hacer push! 🚀**
