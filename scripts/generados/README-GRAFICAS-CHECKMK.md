# 🎯 Gráficas en CheckMK - Guía Rápida

## ⚡ Ejecución Inmediata

```bash
./activar-graficas-checkmk-completo.sh
```

---

## 🎨 ¿Dónde Ver Gráficas como Netdata?

### 🏆 OPCIÓN 1: GRAFANA (RECOMENDADO)

```
URL:      http://localhost:3001
Usuario:  admin
Password: admin123
```

**✅ Ventajas:**
- Gráficas idénticas a Netdata
- Dashboards ya configurados
- Disponible inmediatamente
- Totalmente personalizable

**📊 Dashboards Disponibles:**
- Node Exporter Full (CPU, Memoria, Disco, Red)
- Prometheus Stats
- RabbitMQ Overview
- Netdata Dashboard

---

### ⚡ OPCIÓN 2: NETDATA (TIEMPO REAL)

```
URL: http://localhost:19999
```

**✅ Ventajas:**
- Actualización cada segundo
- Todas las métricas automáticas
- Ideal para troubleshooting
- Sin configuración necesaria

---

### 📊 OPCIÓN 3: CHECKMK (GESTIÓN)

```
URL:      http://localhost:5152/ensurance/check_mk/
Usuario:  cmkadmin
Password: admin123
```

**✅ Ventajas:**
- Alertas enterprise
- Gestión de servicios
- Notificaciones estructuradas

**⚠️ Limitaciones:**
- Gráficas básicas (no es su fuerte)
- Requiere 15-30 minutos para mostrar datos
- Actualización cada 1-5 minutos

**📊 Cómo Ver Gráficas:**
1. Monitor → Dashboards → Selecciona un dashboard
2. Monitor → All services → Click en servicio → "Service Metrics"
3. Monitor → Performance → Selecciona host

---

## 📋 Comparación Rápida

| Sistema | Gráficas | Tiempo Real | Mejor Para |
|---------|----------|-------------|------------|
| **Grafana** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | Visualización y análisis |
| **Netdata** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | Troubleshooting inmediato |
| **CheckMK** | ⭐⭐ | ⭐ | Alertas y gestión |
| **Prometheus** | ⭐⭐ | ⭐⭐ | Consultas avanzadas |

---

## 🎯 Recomendación Final

### Para Gráficas Hermosas:
👉 **USA GRAFANA** - http://localhost:3001

### Para Troubleshooting:
👉 **USA NETDATA** - http://localhost:19999

### Para Alertas:
👉 **USA CHECKMK** - http://localhost:5152/ensurance/check_mk/

---

## 📚 Documentación Completa

- `RESUMEN-GRAFICAS-CHECKMK.md` - Resumen ejecutivo
- `GUIA-GRAFICAS-CHECKMK.md` - Guía paso a paso detallada
- `CHECKMK-NETDATA-EQUIVALENCIAS.md` - Mapeo de métricas

---

## ❓ FAQ

**P: ¿Por qué no veo gráficas en CheckMK?**  
R: CheckMK tarda 15-30 minutos en recolectar datos. Usa Grafana mientras esperas.

**P: ¿Cuál es la mejor herramienta para gráficas?**  
R: Grafana - tiene las mismas métricas que Netdata con mejor visualización.

**P: ¿CheckMK es útil entonces?**  
R: Sí, para alertas enterprise y gestión de servicios, no para visualización.

**P: ¿Puedo usar las tres herramientas?**  
R: Sí, están integradas y comparten las mismas métricas de Prometheus.

---

## 🚀 Inicio Rápido (30 segundos)

```bash
# 1. Configura todo
./activar-graficas-checkmk-completo.sh

# 2. Abre Grafana (gráficas inmediatas)
http://localhost:3001

# 3. O abre Netdata (tiempo real)
http://localhost:19999

# 4. O espera 30 minutos y usa CheckMK
http://localhost:5152/ensurance/check_mk/
```

---

**✅ TODO CONFIGURADO Y LISTO PARA USAR**
