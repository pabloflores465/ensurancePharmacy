# ✅ Gráficas en CheckMK - Resumen Completo

## 🎯 Solución Implementada

Se han creado scripts y configuraciones para mostrar dashboards con gráficas en CheckMK similares a Netdata. **Sin embargo, es importante entender que Grafana es la mejor opción para visualización de gráficas.**

---

## 🚀 Ejecución Rápida (Un Solo Comando)

```bash
./activar-graficas-checkmk-completo.sh
```

Este script ejecuta automáticamente:
1. ✅ Configuración de dashboards en CheckMK
2. ✅ Configuración de plugins de gráficas
3. ✅ Habilitación de servicios y métricas
4. ✅ Verificación de la configuración
5. ✅ Creación de página de resumen

---

## 📊 Comparación de Herramientas

| Característica | Netdata | Grafana | CheckMK | Prometheus |
|---------------|---------|---------|---------|------------|
| **Gráficas Hermosas** | ✅ Excelente | ✅ Excelente | ⚠️ Básicas | ⚠️ Básicas |
| **Facilidad de Uso** | ✅ Automático | ✅ Fácil | ⚠️ Requiere config | ⚠️ Requiere PromQL |
| **Actualización** | ✅ 1 segundo | ⚠️ 30 seg - 1 min | ⚠️ 1-5 minutos | ⚠️ 15 seg - 1 min |
| **Personalización** | ❌ Limitada | ✅ Total | ✅ Media | ⚠️ Limitada |
| **Histórico** | ⚠️ 3 días | ✅ Meses/Años | ✅ Configurable | ✅ Semanas |
| **Alertas** | ✅ Básicas | ✅ Avanzadas | ✅ Enterprise | ✅ Básicas |

---

## 🏆 Recomendación Principal

### Para Gráficas: USA GRAFANA

**¿Por qué Grafana?**
- ✅ Dashboards con gráficas hermosas (idénticas a Netdata)
- ✅ Usa las mismas métricas de Prometheus que Netdata
- ✅ Dashboards ya pre-configurados
- ✅ Personalización total
- ✅ Exportación de dashboards

**Acceso a Grafana:**
```
URL:      http://localhost:3001
Usuario:  admin
Password: admin123
```

**Dashboards disponibles en Grafana:**
- 📊 Node Exporter Full (CPU, Memoria, Disco, Red)
- 📈 Prometheus Stats
- 🐰 RabbitMQ Overview
- ⚡ Netdata Dashboard

---

## 📋 Acceso a Todas las Herramientas

### 1. Grafana (Gráficas Hermosas)
```
URL:      http://localhost:3001
Usuario:  admin
Password: admin123
Uso:      Dashboards profesionales, análisis visual
```

### 2. Netdata (Tiempo Real)
```
URL:      http://localhost:19999
Uso:      Troubleshooting inmediato, métricas cada segundo
```

### 3. CheckMK (Gestión y Alertas)
```
URL:      http://localhost:5152/ensurance/check_mk/
Usuario:  cmkadmin
Password: admin123
Uso:      Alertas enterprise, gestión de servicios
```

### 4. Prometheus (Consultas)
```
URL:      http://localhost:9090
Uso:      Consultas PromQL, exploración de métricas
```

### 5. Página de Acceso Rápido (NUEVO)
```
URL:      http://localhost:5152/ensurance/grafana/
Uso:      Enlaces centralizados a todas las herramientas
```

---

## 🖥️ Dashboards Creados en CheckMK

### Dashboard 1: Principal (main)
- 🖥️ Estado de todos los hosts
- ⚙️ Estado de todos los servicios
- 🚨 Problemas actuales
- 📊 Gráficas de CPU, Memoria, Disco, Red

### Dashboard 2: System Metrics
- 🔥 CPU de todos los hosts
- 💾 Memoria de todos los hosts
- 💿 Disco de todos los hosts
- 🌐 Red de todos los hosts

### Dashboard 3: Applications
- 🌐 Health de servicios web
- 📊 Servicios de Prometheus
- 🐰 Estado de RabbitMQ
- 📈 Métricas de Netdata
- 📉 Estado de Grafana

**Acceso a los dashboards:**
```
CheckMK → Monitor → Dashboards → Selecciona un dashboard
```

---

## ⏱️ Tiempos de Espera

### CheckMK (Requiere tiempo para recolectar datos)
- Primera verificación: **1-5 minutos**
- Primeras métricas: **5-10 minutos**
- Gráficas completas: **15-30 minutos**

### Grafana (Inmediato)
- Gráficas disponibles: **Inmediato** ✅
- Datos históricos: Desde que Prometheus inició

### Netdata (Inmediato)
- Gráficas disponibles: **Inmediato** ✅
- Actualización: Cada segundo

---

## 🎨 Cómo Ver Gráficas en CheckMK

### Método 1: Desde Dashboards
1. Accede a CheckMK
2. Ve a: **Monitor → Dashboards**
3. Selecciona:
   - 📊 Ensurance Pharmacy - Dashboard Principal
   - 🖥️ System Metrics Dashboard
   - 🚀 Applications Dashboard

### Método 2: Desde un Servicio
1. Ve a: **Monitor → All services**
2. Busca servicios con "Prometheus" en el nombre
3. Haz clic en cualquier servicio
4. Busca la sección **"Service Metrics"**
5. Haz clic en **"Show graph"**

### Método 3: Vista de Performance
1. Ve a: **Monitor → Hosts**
2. Haz clic en un host (ejemplo: `node-exporter`)
3. En el menú del host, busca **"Service metrics"**
4. Verás todas las gráficas del host

### Método 4: Vista Personalizada
```
URL directa: http://localhost:5152/ensurance/check_mk/view.py?view_name=prometheus_metrics
```

---

## 📁 Scripts Creados

| Script | Descripción | Comando |
|--------|-------------|---------|
| `activar-graficas-checkmk-completo.sh` | **Script maestro** - Ejecuta todo | `./activar-graficas-checkmk-completo.sh` |
| `configurar-dashboards-checkmk.sh` | Crea dashboards personalizados | `./configurar-dashboards-checkmk.sh` |
| `configurar-graficas-prometheus-checkmk.sh` | Configura plugins de métricas | `./configurar-graficas-prometheus-checkmk.sh` |
| `habilitar-graficas-checkmk.sh` | Descubre servicios y activa gráficas | `./habilitar-graficas-checkmk.sh` |
| `verificar-checkmk.sh` | Verifica configuración | `./verificar-checkmk.sh` |

---

## 📚 Documentación

| Documento | Contenido |
|-----------|-----------|
| `GUIA-GRAFICAS-CHECKMK.md` | Guía completa paso a paso |
| `RESUMEN-GRAFICAS-CHECKMK.md` | Este resumen ejecutivo |
| `CHECKMK-NETDATA-EQUIVALENCIAS.md` | Mapeo de alertas y métricas |
| `RESUMEN-CHECKMK-PROMETHEUS.md` | Integración con Prometheus |

---

## 🔧 Troubleshooting

### Problema: No veo gráficas en CheckMK

**Solución 1: Espera el tiempo necesario**
```bash
# CheckMK necesita 15-30 minutos para recolectar datos
# Mientras tanto, usa Grafana:
http://localhost:3001
```

**Solución 2: Ejecuta service discovery**
```bash
./habilitar-graficas-checkmk.sh
```

**Solución 3: Verifica que los servicios estén corriendo**
```bash
docker ps | grep -E "prometheus|node-exporter|checkmk"
```

### Problema: Los dashboards no aparecen

```bash
# Re-ejecutar configuración
./configurar-dashboards-checkmk.sh

# Limpiar caché del navegador (Ctrl + Shift + R)
```

### Problema: Error de conexión a CheckMK

```bash
# Verificar que CheckMK esté corriendo
docker ps | grep checkmk

# Reiniciar CheckMK
docker restart ensurance-checkmk-full

# Esperar 2-3 minutos y volver a intentar
```

---

## 💡 Mejores Prácticas

### Usa Cada Herramienta para su Propósito

1. **Grafana** → Dashboards visuales, análisis de tendencias, reportes
2. **Netdata** → Troubleshooting inmediato, incidentes en tiempo real
3. **CheckMK** → Gestión de alertas, inventario, notificaciones
4. **Prometheus** → Consultas avanzadas con PromQL, métricas raw

### Workflow Recomendado

```
1. Monitoreo día a día → Grafana
2. Alerta disparada → CheckMK (notificación)
3. Investigación → Netdata (tiempo real)
4. Análisis profundo → Prometheus (PromQL)
```

---

## 🎯 Resumen Ejecutivo

### ✅ Lo que se ha configurado:

1. **Dashboards en CheckMK** con gráficas de sistema
2. **Plugins personalizados** para métricas de Prometheus
3. **Vistas personalizadas** para servicios y métricas
4. **Integración con Grafana** (enlaces directos)
5. **Página HTML** de acceso rápido a todas las herramientas

### ⚠️ Limitaciones de CheckMK para gráficas:

- ❌ No es su fuerte (es para gestión, no visualización)
- ⏱️ Requiere 15-30 minutos para mostrar gráficas
- 📊 Las gráficas son básicas comparadas con Grafana
- 🔄 Actualización lenta (1-5 minutos vs 1 segundo de Netdata)

### ✅ Solución Real:

**USA GRAFANA** para gráficas → http://localhost:3001
- Dashboards hermosos iguales a Netdata
- Mismas métricas de Prometheus
- Disponible inmediatamente
- Totalmente personalizable

**USA CHECKMK** para gestión → http://localhost:5152/ensurance/check_mk/
- Alertas enterprise
- Gestión de servicios
- Notificaciones por email/Slack
- Inventario de infraestructura

---

## 📞 Comandos Útiles

```bash
# Ejecutar configuración completa
./activar-graficas-checkmk-completo.sh

# Verificar estado
./verificar-checkmk.sh

# Ver logs de CheckMK
docker logs ensurance-checkmk-full --tail 100

# Reiniciar CheckMK
docker restart ensurance-checkmk-full

# Ver logs internos
docker exec ensurance-checkmk-full omd su ensurance -c "tail -f ~/var/log/cmc.log"

# Listar hosts
docker exec ensurance-checkmk-full omd su ensurance -c "cmk --list-hosts"

# Descubrir servicios en un host
docker exec ensurance-checkmk-full omd su ensurance -c "cmk -II node-exporter"
```

---

## 🎉 Conclusión

### Para Gráficas Hermosas como Netdata:
🏆 **GANADOR: GRAFANA** → http://localhost:3001

### Para Monitoreo en Tiempo Real:
🏆 **GANADOR: NETDATA** → http://localhost:19999

### Para Gestión y Alertas Enterprise:
🏆 **GANADOR: CHECKMK** → http://localhost:5152/ensurance/check_mk/

**Los tres sistemas están integrados y comparten las mismas métricas de Prometheus. Usa cada uno según tu necesidad actual.**

---

**Última actualización:** Octubre 2024  
**Versión:** 1.0  
**Estado:** ✅ Configuración Completa
