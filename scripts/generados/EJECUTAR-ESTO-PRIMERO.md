# 🎯 CÓMO VER GRÁFICAS COMO NETDATA

## ⚡ SOLUCIÓN INMEDIATA (1 minuto)

### Opción 1: Grafana (RECOMENDADO) ⭐⭐⭐⭐⭐

```bash
# Abre tu navegador en:
http://localhost:3001

# Credenciales:
Usuario:  admin
Password: admin123
```

**✅ Ya está configurado**  
**✅ Gráficas hermosas idénticas a Netdata**  
**✅ Disponible inmediatamente**  

---

### Opción 2: Netdata (TIEMPO REAL) ⚡

```bash
# Abre tu navegador en:
http://localhost:19999
```

**✅ Actualización cada segundo**  
**✅ Sin contraseña**  
**✅ Todas las métricas del sistema**

---

## 📊 Configurar CheckMK con Gráficas (30 minutos)

Si también quieres ver dashboards en CheckMK (aunque no es su fuerte):

```bash
./activar-graficas-checkmk-completo.sh
```

**⚠️ IMPORTANTE:**
- CheckMK tarda **15-30 minutos** en mostrar gráficas
- Las gráficas son **básicas** comparadas con Grafana
- CheckMK es mejor para **alertas** que para visualización

**Después de ejecutar el script:**
```bash
# Abre CheckMK en:
http://localhost:5152/ensurance/check_mk/

# Credenciales:
Usuario:  cmkadmin
Password: admin123

# Ve a: Monitor → Dashboards
```

---

## 🎨 Comparación Visual

```
┌─────────────────┬──────────────┬──────────────┬──────────────────┐
│   Herramienta   │  Gráficas    │  Velocidad   │  Disponibilidad  │
├─────────────────┼──────────────┼──────────────┼──────────────────┤
│ GRAFANA         │ ⭐⭐⭐⭐⭐    │ 30 seg       │ ✅ AHORA         │
│ NETDATA         │ ⭐⭐⭐⭐⭐    │ 1 segundo    │ ✅ AHORA         │
│ CHECKMK         │ ⭐⭐         │ 1-5 minutos  │ ⏱️ 30 minutos    │
└─────────────────┴──────────────┴──────────────┴──────────────────┘
```

---

## 💡 Uso Recomendado

```
🎨 Ver gráficas bonitas → GRAFANA
⚡ Troubleshooting        → NETDATA
🔔 Gestionar alertas     → CHECKMK
🔍 Consultas avanzadas   → PROMETHEUS
```

---

## 📚 Más Información

- `README-GRAFICAS-CHECKMK.md` - Guía rápida
- `RESUMEN-GRAFICAS-CHECKMK.md` - Resumen completo
- `GUIA-GRAFICAS-CHECKMK.md` - Guía detallada paso a paso

---

## ✅ Resumen Final

**Para ver gráficas como Netdata:**

1. **USA GRAFANA** → http://localhost:3001 ⭐ RECOMENDADO
2. O usa NETDATA → http://localhost:19999 ⚡ TIEMPO REAL
3. CheckMK es para alertas, no para gráficas 📋

**¡Todo ya está configurado y funcionando!**
