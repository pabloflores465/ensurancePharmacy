# Guía: Full Stress Test Suite con Usuarios Customizables

## 📋 Descripción

Script mejorado con **menú interactivo** que permite ejecutar todos los tipos de stress tests (K6 + JMeter) con **número de usuarios totalmente configurable**. Los resultados se muestran automáticamente en servidores de reportes en contenedores.

## 🚀 Uso Rápido

```bash
# Desde la carpeta stress/
./run-full-stress-test.sh

# El script mostrará un menú interactivo
# Selecciona el test que deseas ejecutar
# Te pedirá el número de usuarios antes de ejecutar
```

## 📝 Menú de Opciones

### Opciones del Menú Interactivo

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

### Configuración de Tests

| Test | Parámetros Solicitados | Default |
|------|------------------------|---------|
| **K6 Tests (1-4)** | Número de usuarios virtuales | 50 |
| **JMeter Tests (5-6)** | Usuarios, Ramp Time, Duration | 50, 30s, 300s |

## 🎯 Tipos de Test K6

### 1. Load Test (load)
Prueba de carga progresiva con múltiples escenarios.

**Configuración dinámica basada en VUS:**
- **Constant Load:** 20% del VUS durante 2 minutos
- **Ramping Load:** Escala de 0 → 50% → 100% del VUS
- **Spike Test:** Pico de 200% del VUS

```bash
./run-full-stress-test.sh --k6-users 100 --test-type load
```

### 2. Stress Test (stress)
Empuja el sistema más allá de su capacidad normal.

**Configuración dinámica:**
- Stage 1: 50% del VUS (1 min)
- Stage 2: 100% del VUS (2 min)
- Stage 3: 200% del VUS (2 min)
- Stage 4: 300% del VUS (2 min)
- Ramp down: 0 (1 min)

```bash
./run-full-stress-test.sh --k6-users 100 --test-type stress
```

### 3. Spike Test (spike)
Prueba de picos repentinos de tráfico.

**Configuración dinámica:**
- Normal: 10% del VUS (30s)
- **SPIKE:** 100% del VUS en 10 segundos
- Mantiene: 100% del VUS (1 min)
- Drop: Regresa a 10% (10s)
- Ramp down: 0 (30s)

```bash
./run-full-stress-test.sh --k6-users 500 --test-type spike
```

### 4. Soak Test (soak)
Prueba de carga sostenida durante 30 minutos.

**Configuración:**
- Ramp up: 0 → VUS (2 min)
- **Sustained:** VUS durante 30 minutos
- Ramp down: VUS → 0 (2 min)

```bash
./run-full-stress-test.sh --k6-users 50 --test-type soak
```

## 📊 Flujo de Uso

### Ejemplo 1: Load Test con 100 usuarios
```
1. Ejecutar: ./run-full-stress-test.sh
2. Seleccionar opción: 1
3. Cuando pregunte usuarios: 100
4. El test se ejecutará con 100 VUS
```

### Ejemplo 2: Stress Test Intensivo
```
1. Ejecutar: ./run-full-stress-test.sh
2. Seleccionar opción: 2
3. Cuando pregunte usuarios: 500
4. El test escala progresivamente hasta 1500 usuarios (300%)
```

### Ejemplo 3: JMeter Full Test Customizado
```
1. Ejecutar: ./run-full-stress-test.sh
2. Seleccionar opción: 6
3. Número de usuarios: 200
4. Tiempo de rampa: 60
5. Duración: 600
6. El test corre con esta configuración
```

### Ejemplo 4: Spike Test con 1000 usuarios
```
1. Ejecutar: ./run-full-stress-test.sh
2. Seleccionar opción: 3
3. Cuando pregunte usuarios: 1000
4. El test genera un spike súbito de 1000 usuarios
```

### Ejemplo 5: Verificar Sistema
```
1. Ejecutar: ./run-full-stress-test.sh
2. Seleccionar opción: 8 (Verificar Backends)
3. Ver estado de BackV4 y BackV5
```

### Ejemplo 6: Ver Reportes Después del Test
```
1. Después de ejecutar un test K6: opción 10
2. Después de ejecutar un test JMeter: opción 9
3. Los servidores se levantan automáticamente
```

## 🖥️ Acceso a Reportes

Después de ejecutar los tests, accede a los reportes en:

| Servicio | URL | Descripción |
|----------|-----|-------------|
| **K6 Dashboard** | http://localhost:5666 | Dashboard HTML interactivo de K6 |
| **JMeter Report** | http://localhost:8085 | Reporte HTML detallado de JMeter |
| **Grafana** | http://localhost:3300 | Métricas en tiempo real (si está corriendo) |
| **Prometheus** | http://localhost:9090 | Métricas raw de Prometheus |

## 🔧 Arquitectura

### Scripts K6 Customizables

Los siguientes scripts soportan la variable de entorno `VUS`:

- `load-test-custom.js` - Test de carga progresiva
- `stress-test-custom.js` - Test de estrés escalado
- `spike-test-custom.js` - Test de picos con configuración dinámica
- `soak-test-custom.js` - Test de carga sostenida

### Cómo Funciona

1. **Input del Usuario:** El script bash recibe los parámetros de usuarios
2. **Variable de Entorno:** Pasa `VUS` como variable de entorno a Docker
3. **Script K6:** Lee `__ENV.VUS` y ajusta dinámicamente:
   - Número de usuarios virtuales
   - Stages de ramping
   - Targets de carga
4. **JMeter:** Usa variables `-JUSERS`, `-JRAMP_TIME`, `-JDURATION`
5. **Reportes:** Levanta automáticamente servidores HTTP para visualización

## 🎨 Características del Script

### ✅ Verificación Automática de Backends
- Detecta si BackV4 y BackV5 están corriendo
- Ofrece levantarlos automáticamente si no están disponibles
- Espera hasta que estén listos antes de comenzar

### 📊 Servidores de Reportes Automáticos
- K6: Dashboard HTML interactivo en puerto 5666
- JMeter: Reporte HTML completo en puerto 8085
- Se levantan automáticamente después de cada test

### 🎨 Interfaz Colorida
- Información en azul
- Éxito en verde
- Advertencias en amarillo
- Errores en rojo
- Títulos destacados en magenta/cyan

### 🧹 Limpieza Automática
- Limpia resultados anteriores antes de cada ejecución
- Evita mezclar datos de múltiples tests

## 🆚 Comparación con `run-tests.sh`

| Característica | `run-tests.sh` | `run-full-stress-test.sh` |
|----------------|----------------|---------------------------|
| Usuarios K6 | Fijos en scripts | **✅ Configurable (prompt antes del test)** |
| Usuarios JMeter | Fijos (50, 30, 300) | **✅ Configurable (usuarios, ramp, duration)** |
| Interfaz | Menú interactivo | **Menú interactivo mejorado** |
| Scripts K6 | Originales | **Versiones -custom con VUS dinámico** |
| Reportes | Manual | **Automático después de cada test** |
| Servidor K6 Report | No incluido | **✅ Opción 10 del menú** |
| Opciones | 11 opciones | **12 opciones (+ K6 reports)** |

### Ventajas del Nuevo Script

- ✅ **Flexibilidad Total:** Configura usuarios en cada ejecución
- ✅ **Misma Interfaz:** Mantiene el menú conocido
- ✅ **Reportes Automáticos:** Se levantan después de cada test
- ✅ **K6 Dashboard:** Nuevo servidor de reportes K6
- ✅ **Scripts Dinámicos:** Los tests K6 escalan según VUS
- ✅ **Sin Modificar Código:** Todo configurable por input

## 🛑 Detener Servidores de Reportes

```bash
cd scripts
docker compose -f docker-compose.stress.yml down
```

## 📚 Archivos Relacionados

- `run-full-stress-test.sh` - Script principal
- `k6/scripts/load-test-custom.js` - K6 load test con VUS configurable
- `k6/scripts/stress-test-custom.js` - K6 stress test con VUS configurable
- `k6/scripts/spike-test-custom.js` - K6 spike test con VUS configurable
- `k6/scripts/soak-test-custom.js` - K6 soak test con VUS configurable
- `../scripts/docker-compose.stress.yml` - Configuración de contenedores

## 💡 Tips y Mejores Prácticas

### Para K6

- **Load Test:** Usa 50-100 usuarios para pruebas normales
- **Stress Test:** Empieza con 100 usuarios, incrementa gradualmente
- **Spike Test:** 500-1000 usuarios para picos realistas
- **Soak Test:** 30-50 usuarios para pruebas prolongadas

### Para JMeter

- **Usuarios:** 50-200 para pruebas normales
- **Ramp Time:** 30-60s para calentamiento gradual
- **Duration:** Mínimo 300s (5 min) para resultados confiables

### Monitoreo

- Abre Grafana antes de ejecutar los tests
- Monitorea métricas del sistema durante la ejecución
- Compara resultados entre diferentes configuraciones

## 🐛 Troubleshooting

### Los backends no se levantan
```bash
docker logs ensurance-pharmacy-dev
docker compose -f scripts/docker-compose.dev.yml up -d
```

### Los reportes no se muestran
```bash
# Verificar que los contenedores estén corriendo
docker ps | grep -E "k6-report|jmeter-report"

# Revisar logs
docker logs ensurance-k6-report
docker logs ensurance-jmeter-report
```

### K6 no reconoce VUS
Asegúrate de usar los scripts `-custom.js`, no los originales.

## 📞 Soporte

Para más información, consulta:
- `QUICKSTART.md` - Guía de inicio rápido
- `README.md` - Documentación principal de stress tests
- `run-tests.sh` - Script original con menú interactivo
