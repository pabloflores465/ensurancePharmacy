# 🚀 Quick Start: Stress Tests con Usuarios Customizables

## ¿Qué es esto?

Script con **menú interactivo** que te permite ejecutar **K6 y JMeter tests** configurando el **número de usuarios** antes de cada ejecución.

## Inicio Rápido

```bash
cd stress/
./run-full-stress-test.sh
```

## Menú Principal

```
═══════════════════════════════════════════════
     ENSURANCE STRESS TEST MENU (Custom VUS)
═══════════════════════════════════════════════

K6 Tests (con usuarios configurables):
  1) Load Test (carga progresiva)
  2) Stress Test (hasta límite)
  3) Spike Test (picos repentinos)
  4) Soak Test (30 min sostenido)

JMeter Tests (con usuarios configurables):
  5) JMeter Simple Test
  6) JMeter Full Test

Utilidades:
  7) Iniciar Grafana + Prometheus
  8) Verificar Backends
  9) Ver reportes JMeter
 10) Ver reportes K6
 11) Levantar Backends DEV
 12) Detener Backends DEV
  0) Salir
```

## 📝 Cómo Funciona

### Tests K6 (Opciones 1-4)

1. Selecciona el tipo de test (load, stress, spike, soak)
2. El script te pregunta: **"Número de usuarios virtuales (default 50):"**
3. Ingresa el número (ej: 100) o presiona Enter para usar 50
4. El test se ejecuta con ese número de usuarios
5. El dashboard K6 se levanta automáticamente en http://localhost:5666

**Ejemplo:**
```
Selecciona una opción: 2
Número de usuarios virtuales (default 50): 500
```
→ Ejecuta stress test con 500 usuarios base (escala hasta 1500)

### Tests JMeter (Opciones 5-6)

1. Selecciona el test (simple o full)
2. El script pregunta:
   - **Número de usuarios (default 50):**
   - **Tiempo de rampa en segundos (default 30):**
   - **Duración del test en segundos (default 300):**
3. Ingresa los valores o presiona Enter para defaults
4. El test se ejecuta con esa configuración
5. El reporte JMeter se levanta automáticamente en http://localhost:8085

**Ejemplo:**
```
Selecciona una opción: 6
Número de usuarios (default 50): 200
Tiempo de rampa en segundos (default 30): 60
Duración del test en segundos (default 300): 600
```
→ Ejecuta full test con 200 usuarios, 60s rampa, 600s duración

## 🎯 Tipos de Test K6

| Test | Usuarios Ingresados | Comportamiento Real |
|------|---------------------|---------------------|
| **Load Test** | 100 | Constant: 20, Ramping: 0→50→100, Spike: 200 |
| **Stress Test** | 100 | Stage1: 50, Stage2: 100, Stage3: 200, Stage4: 300 |
| **Spike Test** | 500 | Normal: 50, SPIKE: 500, Maintain: 500 |
| **Soak Test** | 50 | Sustained: 50 durante 30 minutos |

## 📊 Acceso a Reportes

Después de ejecutar tests, los reportes están disponibles en:

- **K6 Dashboard:** http://localhost:5666
- **JMeter Report:** http://localhost:8085
- **Grafana:** http://localhost:3300 (opción 7 del menú)

También puedes usar las opciones del menú:
- **Opción 9:** Levantar servidor reportes JMeter
- **Opción 10:** Levantar servidor reportes K6

## 💡 Tips Rápidos

### Valores Recomendados K6
- **Load Test:** 50-100 usuarios (pruebas normales)
- **Stress Test:** 100-300 usuarios (identificar límites)
- **Spike Test:** 500-1000 usuarios (picos repentinos)
- **Soak Test:** 30-50 usuarios (pruebas prolongadas)

### Valores Recomendados JMeter
- **Usuarios:** 50-200 (pruebas normales)
- **Ramp Time:** 30-60 segundos (calentamiento)
- **Duration:** 300-600 segundos (5-10 minutos)

## 🔧 Scripts K6 Customizables

El script usa versiones especiales de los scripts K6 que leen la variable `VUS`:

- `k6/scripts/load-test-custom.js`
- `k6/scripts/stress-test-custom.js`
- `k6/scripts/spike-test-custom.js`
- `k6/scripts/soak-test-custom.js`

Estos scripts ajustan **dinámicamente** sus stages y targets según el número de usuarios que ingreses.

## 🆚 vs run-tests.sh Original

| Característica | run-tests.sh | run-full-stress-test.sh |
|----------------|--------------|-------------------------|
| Usuarios K6 | ❌ Fijos | ✅ Configurable |
| Usuarios JMeter | ❌ Fijos | ✅ Configurable |
| Ramp Time | ❌ Fijo | ✅ Configurable |
| Duration | ❌ Fijo | ✅ Configurable |
| K6 Reports Server | ❌ No | ✅ Opción 10 |

## 🔄 Flujo Completo

1. **Ejecutar script:** `./run-full-stress-test.sh`
2. **Seleccionar test:** Opción 1-6
3. **Configurar usuarios:** Ingresar número o usar default
4. **Test se ejecuta:** Con la configuración especificada
5. **Reportes automáticos:** Se levantan servidores
6. **Ver resultados:** Abrir URLs de reportes
7. **Repetir:** Presionar Enter para volver al menú

## 📚 Documentación Completa

Para información detallada, consulta:
- **CUSTOM_USERS_GUIDE.md** - Guía completa con todos los detalles
- **README.md** - Documentación principal de stress tests
- **run-tests.sh** - Script original (usuarios fijos)

## 🐛 Troubleshooting

### Backends no disponibles
- El script te preguntará si quieres levantarlos automáticamente
- O usa la opción 11 del menú

### Reportes no se muestran
- Usa la opción 9 (JMeter) o 10 (K6) del menú
- Verifica con: `docker ps | grep report`

### Error en K6
- Asegúrate de usar los scripts `-custom.js`
- Verifica logs con: `docker logs ensurance-k6`

## ✅ Ventajas

- ✅ Misma interfaz familiar que `run-tests.sh`
- ✅ Flexibilidad total en usuarios
- ✅ Sin necesidad de editar código
- ✅ Perfecto para pruebas rápidas con diferentes cargas
- ✅ Reportes automáticos después de cada test
- ✅ Scripts K6 que se adaptan dinámicamente

---

**¡Empieza ahora!** 
```bash
cd stress && ./run-full-stress-test.sh
```
