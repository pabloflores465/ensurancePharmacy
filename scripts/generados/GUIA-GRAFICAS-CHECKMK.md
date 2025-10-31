# Guía Completa: Gráficas en CheckMK similares a Netdata

## 📊 Resumen

Esta guía te muestra cómo configurar CheckMK para visualizar gráficas similares a las de Netdata. Aunque CheckMK es principalmente una herramienta de **gestión y alertas enterprise**, puedes visualizar las métricas de Prometheus de forma efectiva.

---

## 🎯 Solución Recomendada: GRAFANA

**⚠️ IMPORTANTE:** Para gráficas detalladas y hermosas similares a Netdata, **usa Grafana** en lugar de CheckMK:

```
URL: http://localhost:3001
Usuario: admin
Password: admin123
```

### ¿Por qué Grafana?

| Característica | Netdata | Grafana | CheckMK |
|---------------|---------|---------|---------|
| **Gráficas Hermosas** | ✅ Excelente | ✅ Excelente | ⚠️ Básicas |
| **Actualización** | ✅ 1 segundo | ⚠️ 30 seg - 1 min | ⚠️ 1-5 minutos |
| **Personalización** | ❌ Limitada | ✅ Total | ⚠️ Media |
| **Histórico** | ⚠️ 3 días | ✅ Meses/Años | ✅ Configurable |
| **Facilidad de Uso** | ✅ Automático | ✅ Fácil | ⚠️ Requiere configuración |

---

## 🚀 Configuración Rápida

### Paso 1: Ejecutar Scripts de Configuración

```bash
# Script 1: Configurar dashboards en CheckMK
./configurar-dashboards-checkmk.sh

# Script 2: Configurar plugins de gráficas
./configurar-graficas-prometheus-checkmk.sh
```

### Paso 2: Esperar Recolección de Datos

⏱️ **Tiempos de espera:**
- Primera verificación: 1-5 minutos
- Primeras métricas: 5-10 minutos
- Gráficas completas: **15-30 minutos**

**Mientras esperas**, usa Netdata o Grafana para ver gráficas inmediatas.

---

## 📈 Acceso a las Herramientas de Gráficas

### 1. Página de Acceso Rápido (NUEVO)

```
URL: http://localhost:5152/ensurance/grafana/
```

Esta página HTML incluye:
- 🔗 Enlaces directos a todas las herramientas
- 📋 Información sobre cada sistema
- 💡 Guía rápida de uso

### 2. Grafana (Recomendado)

```
URL: http://localhost:3001
Usuario: admin
Password: admin123
```

**Dashboards disponibles:**
- Node Exporter Full (Métricas del sistema)
- Prometheus Stats
- RabbitMQ Overview
- Netdata Dashboard

### 3. Netdata (Tiempo Real)

```
URL: http://localhost:19999
```

**Características:**
- ⚡ Actualización cada segundo
- 📊 Todas las métricas del sistema
- 🎨 Gráficas automáticas

### 4. CheckMK (Gestión)

```
URL: http://localhost:5152/ensurance/check_mk/
Usuario: cmkadmin
Password: admin123
```

---

## 🖥️ Cómo Ver Gráficas en CheckMK

### Método 1: Desde un Servicio Individual

1. Accede a CheckMK
2. Ve a: **Monitor → All services**
3. Busca servicios que contengan "Prometheus" en el nombre
4. Haz clic en cualquier servicio
5. En la página del servicio:
   - Busca la sección **"Service Metrics"** o **"Performance Data"**
   - Verás minigráficas (sparklines)
   - Haz clic en **"Show graph"** para gráfica detallada

### Método 2: Vista de Performance por Host

1. Ve a: **Monitor → Hosts**
2. Haz clic en un host (ejemplo: `node-exporter`)
3. En el menú del host, busca **"Service metrics"** o **"Performance graphs"**
4. Verás todas las gráficas del host

### Método 3: Dashboards Personalizados

1. Ve a: **Monitor → Dashboards**
2. Selecciona uno de estos dashboards:
   - **📊 Ensurance Pharmacy - Dashboard Principal**
   - **🖥️ System Metrics Dashboard**
   - **🚀 Applications Dashboard**

### Método 4: Vista de Métricas de Prometheus

```
URL directa: http://localhost:5152/ensurance/check_mk/view.py?view_name=prometheus_metrics
```

---

## 📊 Dashboards Creados en CheckMK

### Dashboard 1: Principal (main)

**Contenido:**
- 🖥️ Estado de todos los hosts
- ⚙️ Estado de todos los servicios
- 🚨 Problemas actuales
- 📊 Gráficas de CPU (Node Exporter)
- 💾 Gráficas de Memoria
- 💿 Gráficas de Disco
- 🌐 Gráficas de Red

### Dashboard 2: System Metrics

**Contenido:**
- 🔥 CPU de todos los hosts
- 💾 Memoria de todos los hosts
- 💿 Disco de todos los hosts
- 🌐 Red de todos los hosts

### Dashboard 3: Applications

**Contenido:**
- 🌐 Health de servicios web (HTTP checks)
- 📊 Servicios de Prometheus
- 🐰 Estado de RabbitMQ
- 📈 Métricas de Netdata
- 📉 Estado de Grafana

---

## 🔧 Tipos de Métricas Disponibles

### Métricas de Sistema (Node Exporter)

| Métrica | Descripción | Unidad |
|---------|-------------|--------|
| `node_cpu_seconds_total` | Uso de CPU por core | segundos |
| `node_memory_MemAvailable_bytes` | Memoria disponible | bytes |
| `node_filesystem_avail_bytes` | Espacio en disco | bytes |
| `node_network_receive_bytes_total` | Tráfico de red recibido | bytes |
| `node_network_transmit_bytes_total` | Tráfico de red enviado | bytes |
| `node_disk_io_time_seconds_total` | Tiempo de I/O de disco | segundos |

### Métricas de Aplicaciones

| Servicio | Métricas Disponibles |
|----------|---------------------|
| **RabbitMQ** | Queue messages, connections, channels |
| **Prometheus** | Targets, series, samples |
| **HTTP Checks** | Response time, status codes |
| **Netdata** | Todas las métricas vía Prometheus format |

---

## 🎨 Personalizar Gráficas en CheckMK

### Opción 1: Crear Custom Graph

1. Ve a: **Customize → Custom graphs**
2. Haz clic en **"Add graph"**
3. Configura:
   - **Title:** Nombre de la gráfica
   - **Metrics:** Selecciona las métricas que quieres
   - **Time range:** Rango de tiempo (4h, 24h, 7d, etc.)
4. Guarda

### Opción 2: Modificar Dashboards Existentes

1. Ve a: **Monitor → Dashboards**
2. Selecciona el dashboard que quieres modificar
3. Haz clic en **"Edit"** (icono de lápiz)
4. Arrastra y suelta elementos para reorganizar
5. Haz clic en **"Add dashlet"** para agregar nuevos widgets
6. Guarda cambios

### Opción 3: Crear Nuevo Dashboard

1. Ve a: **Customize → Dashboards**
2. Haz clic en **"Add dashboard"**
3. Configura:
   - **Name:** ID interno
   - **Title:** Título visible
   - **Description:** Descripción
4. Agrega dashlets:
   - **Host stats:** Estado de hosts
   - **Service stats:** Estado de servicios
   - **View:** Lista de servicios/hosts
   - **PNP graph:** Gráficas de métricas
5. Guarda

---

## 🔍 Troubleshooting

### Problema 1: No veo servicios con métricas de Prometheus

**Solución:**

```bash
# Ejecutar service discovery
./habilitar-graficas-checkmk.sh

# O manualmente:
docker exec ensurance-checkmk-full omd su ensurance -c "cmk -II node-exporter"
docker exec ensurance-checkmk-full omd su ensurance -c "cmk -II prometheus"
docker exec ensurance-checkmk-full omd su ensurance -c "cmk -O"  # Activar cambios
```

### Problema 2: Los servicios existen pero no hay gráficas

**Causa:** CheckMK necesita recolectar datos históricos

**Solución:**
1. Espera 15-30 minutos
2. Verifica que las métricas existan en Prometheus:
   ```bash
   curl http://localhost:9100/metrics | grep node_cpu
   curl http://localhost:9090/metrics | grep prometheus
   ```
3. Mientras tanto, usa **Grafana** para ver gráficas inmediatas

### Problema 3: Las gráficas se ven muy básicas

**Solución:** CheckMK no es la mejor herramienta para visualización. Usa:

1. **Grafana** para gráficas hermosas y personalizables
2. **Netdata** para gráficas en tiempo real
3. **CheckMK** para alertas y gestión

### Problema 4: Error "No RRD files found"

**Causa:** CheckMK aún no ha creado los archivos RRD

**Solución:**
1. Espera 10-15 minutos
2. Verifica que los servicios se estén ejecutando:
   ```bash
   docker exec ensurance-checkmk-full omd status ensurance
   ```
3. Reinicia CheckMK si es necesario:
   ```bash
   docker restart ensurance-checkmk-full
   ```

### Problema 5: Los dashboards no aparecen

**Solución:**

```bash
# Re-ejecutar configuración de dashboards
./configurar-dashboards-checkmk.sh

# Verificar que se crearon
docker exec ensurance-checkmk-full ls -la /omd/sites/ensurance/var/check_mk/web/cmkadmin/dashboards/

# Limpiar caché del navegador
# Ctrl + Shift + R (o Cmd + Shift + R en Mac)
```

---

## 💡 Mejores Prácticas

### Para Visualización de Datos

1. **Usa Grafana** para:
   - Dashboards profesionales
   - Gráficas personalizadas
   - Análisis de tendencias
   - Reportes visuales

2. **Usa Netdata** para:
   - Troubleshooting inmediato
   - Monitoreo en tiempo real
   - Investigación de incidentes
   - Análisis de performance

3. **Usa CheckMK** para:
   - Gestión de alertas
   - Inventario de servicios
   - Notificaciones estructuradas
   - Reportes de disponibilidad (SLA)

### Para Rendimiento Óptimo

1. **No agregues demasiadas gráficas** en un solo dashboard de CheckMK
2. **Usa rangos de tiempo apropiados** (4h, 24h para monitoreo activo)
3. **Filtra servicios relevantes** en lugar de mostrar todo
4. **Configura refresh automático** con moderación (30-60 segundos)

---

## 📚 Recursos Adicionales

### Documentación Oficial

- [CheckMK Dashboards](https://docs.checkmk.com/latest/en/dashboards.html)
- [CheckMK Custom Graphs](https://docs.checkmk.com/latest/en/graphing.html)
- [CheckMK Views](https://docs.checkmk.com/latest/en/views.html)
- [Prometheus Integration](https://docs.checkmk.com/latest/en/monitoring_prometheus.html)

### Scripts Útiles

| Script | Descripción |
|--------|-------------|
| `configurar-dashboards-checkmk.sh` | Crea dashboards personalizados |
| `configurar-graficas-prometheus-checkmk.sh` | Configura plugins de métricas |
| `habilitar-graficas-checkmk.sh` | Descubre servicios y activa gráficas |
| `verificar-checkmk.sh` | Verifica configuración |

### URLs de Acceso Rápido

```bash
# Página de acceso centralizada
http://localhost:5152/ensurance/grafana/

# CheckMK
http://localhost:5152/ensurance/check_mk/

# Grafana (Recomendado para gráficas)
http://localhost:3001

# Netdata (Tiempo real)
http://localhost:19999

# Prometheus (Consultas)
http://localhost:9090
```

---

## 🎯 Resumen Final

### Para Gráficas Hermosas como Netdata

**🏆 GANADOR: Grafana**
- ✅ Gráficas profesionales
- ✅ Dashboards pre-configurados
- ✅ Mismas métricas que Netdata
- ✅ Personalización total

**Acceso:** http://localhost:3001 (admin / admin123)

### Para Monitoreo en Tiempo Real

**🏆 GANADOR: Netdata**
- ✅ Actualización cada segundo
- ✅ Sin configuración
- ✅ Todas las métricas automáticas

**Acceso:** http://localhost:19999

### Para Gestión y Alertas

**🏆 GANADOR: CheckMK**
- ✅ Alertas enterprise
- ✅ Gestión de servicios
- ✅ Notificaciones estructuradas
- ⚠️ Gráficas básicas (no es su fuerte)

**Acceso:** http://localhost:5152/ensurance/check_mk/ (cmkadmin / admin123)

---

## 📞 Soporte

Si tienes problemas:

1. **Ejecuta el script de verificación:**
   ```bash
   ./verificar-checkmk.sh
   ```

2. **Revisa los logs:**
   ```bash
   docker logs ensurance-checkmk-full --tail 100
   docker exec ensurance-checkmk-full omd su ensurance -c "tail -f ~/var/log/cmc.log"
   ```

3. **Reinicia el contenedor:**
   ```bash
   docker restart ensurance-checkmk-full
   ```

---

**Última actualización:** Octubre 2024  
**Versión:** 1.0  
**Autor:** Sistema de Monitoreo Ensurance Pharmacy
