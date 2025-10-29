# 📚 Índice de Documentación - Ensurance Pharmacy

Este directorio contiene toda la documentación técnica del proyecto, organizada por categorías.

---

## 📁 Estructura de Documentación

```
documentation/spec/
├── INDEX.md (este archivo)
├── [Documentación Raíz] - Configuración general y guías principales
├── monitoring/ - Documentación de monitoreo (Prometheus, Grafana, Netdata)
└── stress/ - Documentación de stress testing (K6, JMeter)
```

---

## 🚀 Documentación Principal (Raíz)

### Configuración Docker
- **[DOCKER_COMPOSE_FULL.md](./DOCKER_COMPOSE_FULL.md)** - Guía completa del docker-compose
- **[DOCKER_QUICK_REFERENCE.md](./DOCKER_QUICK_REFERENCE.md)** - Referencia rápida de comandos Docker
- **[README_DOCKER_SCRIPTS.md](./README_DOCKER_SCRIPTS.md)** - Scripts de Docker disponibles

### Comandos y Scripts
- **[COMANDOS_RAPIDOS.md](./COMANDOS_RAPIDOS.md)** - Comandos rápidos del proyecto
- **[SCRIPTS_CREADOS.md](./SCRIPTS_CREADOS.md)** - Documentación de scripts creados

### Backend y APIs
- **[BACKEND_ENDPOINTS_INFO.md](./BACKEND_ENDPOINTS_INFO.md)** - Información de endpoints del backend

### Implementación y Fixes
- **[IMPLEMENTATION_COMPLETE.md](./IMPLEMENTATION_COMPLETE.md)** - Resumen de implementación completa
- **[FINAL_STRUCTURE.md](./FINAL_STRUCTURE.md)** - Estructura final del proyecto
- **[JENKINS_FIX_SUMMARY.md](./JENKINS_FIX_SUMMARY.md)** - Resumen de fixes de Jenkins
- **[JMETER_FIXES.md](./JMETER_FIXES.md)** - Fixes aplicados a JMeter

---

## 📊 Monitoreo (monitoring/)

### Netdata
- **[monitoring/netdata/NETDATA_DASHBOARDS_GUIDE.md](./monitoring/netdata/NETDATA_DASHBOARDS_GUIDE.md)** - Guía completa de dashboards de Netdata
- **[monitoring/netdata/GRAFANA_VS_NETDATA_COMPARISON.md](./monitoring/netdata/GRAFANA_VS_NETDATA_COMPARISON.md)** - Comparación entre Grafana y Netdata
- **[NETDATA_SETUP_SUMMARY.md](./NETDATA_SETUP_SUMMARY.md)** - Resumen de instalación de Netdata

### Prometheus y Métricas
- **[RESUMEN_METRICAS_K6_PROMETHEUS.md](./RESUMEN_METRICAS_K6_PROMETHEUS.md)** - Resumen de métricas K6 en Prometheus
- **[K6_PROMETHEUS_LISTO.md](./K6_PROMETHEUS_LISTO.md)** - K6 con Prometheus configurado
- **[K6_PROMETHEUS_SETUP_COMPLETE.md](./K6_PROMETHEUS_SETUP_COMPLETE.md)** - Setup completo de K6 con Prometheus

### Sistema
- **[monitoring/SYSTEM_METRICS.md](./monitoring/SYSTEM_METRICS.md)** - Métricas del sistema
- **[monitoring/MONITORING_INTEGRATION.md](./monitoring/MONITORING_INTEGRATION.md)** - Integración de monitoreo
- **[monitoring/SETUP_COMPLETE.md](./monitoring/SETUP_COMPLETE.md)** - Setup completo de monitoreo
- **[monitoring/SETUP_COMPLETE_SYSTEM_METRICS.md](./monitoring/SETUP_COMPLETE_SYSTEM_METRICS.md)** - Setup de métricas del sistema

---

## 🔥 Stress Testing (stress/)

### Guías de Inicio
- **[stress/START_HERE.md](./stress/START_HERE.md)** - ⭐ Comienza aquí para stress testing
- **[stress/QUICKSTART.md](./stress/QUICKSTART.md)** - Guía de inicio rápido
- **[stress/STRESS_TESTING_GUIDE.md](./stress/STRESS_TESTING_GUIDE.md)** - Guía completa de stress testing

### K6 Testing
- **[stress/K6_DASHBOARD_QUICKSTART.md](./stress/K6_DASHBOARD_QUICKSTART.md)** - Quickstart del dashboard K6
- **[stress/K6_DASHBOARD_PROMETHEUS_GUIDE.md](./stress/K6_DASHBOARD_PROMETHEUS_GUIDE.md)** - Guía de K6 con Prometheus
- **[stress/K6_SOLUTION_SUMMARY.md](./stress/K6_SOLUTION_SUMMARY.md)** - Resumen de solución K6
- **[stress/k6/K6_PROMETHEUS_METRICS.md](./stress/k6/K6_PROMETHEUS_METRICS.md)** - Métricas K6 en Prometheus
- **[stress/k6/METRICAS_K6_PROMETHEUS.md](./stress/k6/METRICAS_K6_PROMETHEUS.md)** - Métricas K6 (Español)

### Configuración Avanzada
- **[stress/CUSTOM_USERS_GUIDE.md](./stress/CUSTOM_USERS_GUIDE.md)** - Guía de usuarios personalizados
- **[stress/QUICKSTART_CUSTOM_VUS.md](./stress/QUICKSTART_CUSTOM_VUS.md)** - Quickstart con VUs personalizados
- **[stress/EXAMPLES.md](./stress/EXAMPLES.md)** - Ejemplos de tests
- **[stress/AUTO_START_BACKENDS.md](./stress/AUTO_START_BACKENDS.md)** - Auto-inicio de backends
- **[stress/IMPLEMENTATION_SUMMARY.md](./stress/IMPLEMENTATION_SUMMARY.md)** - Resumen de implementación

---

## 🎯 Guías de Inicio Rápido por Tarea

### Quiero monitorear mi aplicación
1. [NETDATA_SETUP_SUMMARY.md](./NETDATA_SETUP_SUMMARY.md) - Setup de Netdata
2. [monitoring/netdata/NETDATA_DASHBOARDS_GUIDE.md](./monitoring/netdata/NETDATA_DASHBOARDS_GUIDE.md) - Dashboards disponibles
3. [RESUMEN_METRICAS_K6_PROMETHEUS.md](./RESUMEN_METRICAS_K6_PROMETHEUS.md) - Métricas disponibles

### Quiero hacer stress testing
1. [stress/START_HERE.md](./stress/START_HERE.md) - ⭐ Comienza aquí
2. [stress/QUICKSTART.md](./stress/QUICKSTART.md) - Guía rápida
3. [stress/EXAMPLES.md](./stress/EXAMPLES.md) - Ejemplos prácticos

### Quiero configurar Docker
1. [DOCKER_QUICK_REFERENCE.md](./DOCKER_QUICK_REFERENCE.md) - Comandos rápidos
2. [DOCKER_COMPOSE_FULL.md](./DOCKER_COMPOSE_FULL.md) - Configuración completa
3. [COMANDOS_RAPIDOS.md](./COMANDOS_RAPIDOS.md) - Comandos del proyecto

### Quiero comparar herramientas de monitoreo
1. [monitoring/netdata/GRAFANA_VS_NETDATA_COMPARISON.md](./monitoring/netdata/GRAFANA_VS_NETDATA_COMPARISON.md) - Comparación detallada

---

## 📊 Estadísticas de Documentación

- **Total de documentos:** 34 archivos .md
- **Categorías principales:** 3 (Raíz, Monitoring, Stress)
- **Última actualización:** 2025-10-29

---

## 🔍 Búsqueda Rápida

### Por Tecnología
- **Docker:** DOCKER_*.md, README_DOCKER_SCRIPTS.md
- **K6:** K6_*.md, stress/k6/*.md
- **Prometheus:** *PROMETHEUS*.md, RESUMEN_METRICAS_K6_PROMETHEUS.md
- **Netdata:** NETDATA_*.md, monitoring/netdata/*.md
- **Grafana:** GRAFANA_VS_NETDATA_COMPARISON.md
- **Jenkins:** JENKINS_FIX_SUMMARY.md
- **JMeter:** JMETER_FIXES.md

### Por Tipo de Contenido
- **Guías:** *GUIDE*.md, QUICKSTART.md
- **Resúmenes:** *SUMMARY*.md, RESUMEN_*.md
- **Referencias:** *REFERENCE*.md, COMANDOS_RAPIDOS.md
- **Comparaciones:** *COMPARISON*.md
- **Fixes:** *FIXES*.md, *FIX_SUMMARY*.md

---

## 📝 Convenciones de Nomenclatura

- **MAYÚSCULAS_CON_GUIONES.md** - Documentos principales
- **CamelCase.md** - Documentos específicos de tecnología
- **lowercase-with-dashes.md** - Documentos auxiliares (si existen)

---

## 🔗 Enlaces Útiles

### Acceso a Servicios
- **Netdata:** http://localhost:19999
- **Grafana:** http://localhost:3302
- **Prometheus:** http://localhost:9090
- **RabbitMQ:** http://localhost:15674
- **Jenkins:** http://localhost:8080/jenkins
- **SonarQube:** http://localhost:9000/sonar

### Repositorio Principal
- **README.md principal:** [../README.md](../../README.md)

---

## 📞 Soporte

Para más información sobre cada documento, consulta el contenido del archivo correspondiente. Todos los documentos están en formato Markdown y pueden ser visualizados en cualquier editor de texto o navegador.

---

**Última actualización:** 2025-10-29
**Mantenido por:** Equipo Ensurance Pharmacy
