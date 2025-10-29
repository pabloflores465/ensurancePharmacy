# Auto-Start Backends Feature

## 🚀 Nueva Funcionalidad

El script `run-tests.sh` ahora **levanta automáticamente** los backends BackV4 y BackV5 si no están corriendo.

## 🎯 Cómo Funciona

### 1. Detección Automática
Cuando seleccionas un test (K6 o JMeter), el script:
- Verifica si BackV4 (puerto 3002) está respondiendo
- Verifica si BackV5 (puerto 3003) está respondiendo

### 2. Pregunta Interactiva
Si algún backend no responde, verás:
```
[WARNING] Backends no están disponibles
¿Deseas levantarlos automáticamente? (y/n):
```

### 3. Levantamiento Automático
Si respondes `y`:
- Levanta el contenedor `ensurance-pharmacy-dev` con `docker compose`
- Espera hasta 60 segundos a que los backends estén listos
- Muestra puntos de progreso mientras espera
- Continúa automáticamente cuando ambos backends respondan

## 📋 Opciones del Menú

### Nuevas Opciones
- **Opción 10**: Levantar Backends DEV manualmente
- **Opción 11**: Detener Backends DEV

### Opciones Existentes (Ahora con Auto-Start)
- **1-4**: K6 Tests → Verifican/levantan backends automáticamente
- **5-6**: JMeter Tests → Verifican/levantan backends automáticamente
- **8**: Solo verifica backends (sin levantar)

## 🔧 Configuración

### URLs por Defecto
```bash
BACKV4_URL=http://localhost:3002  # Ensurance Backend (BackV4)
BACKV5_URL=http://localhost:3003  # Pharmacy Backend (BackV5)
```

### Puertos Expuestos
El contenedor `ensurance-pharmacy-dev` expone:
- `3000` → Ensurance Frontend
- `3001` → Pharmacy Frontend
- `3002` → Ensurance Backend (BackV4)
- `3003` → Pharmacy Backend (BackV5)

## 💡 Ejemplos de Uso

### Ejemplo 1: Test con Backends Ya Corriendo
```bash
./run-tests.sh
# Selecciona: 5 (JMeter Simple Test)
[INFO] Verificando conectividad de backends...
[SUCCESS] BackV4 está corriendo en http://localhost:3002
[SUCCESS] BackV5 está corriendo en http://localhost:3003
[INFO] Ejecutando JMeter test: sample-plan.jmx
...
```

### Ejemplo 2: Test sin Backends (Auto-Start)
```bash
./run-tests.sh
# Selecciona: 6 (JMeter Full Test)
[INFO] Verificando conectividad de backends...
[WARNING] BackV4 no responde en http://localhost:3002
[WARNING] BackV5 no responde en http://localhost:3003
[WARNING] Backends no están disponibles
¿Deseas levantarlos automáticamente? (y/n): y
[INFO] Levantando backends DEV (BackV4:3002, BackV5:3003)...
[INFO] Esperando a que los backends estén listos (máximo 60s)...
......
[SUCCESS] ¡Backends listos!
[INFO] Ejecutando JMeter test: ensurance-full-test.jmx
...
```

### Ejemplo 3: Levantar Backends Manualmente
```bash
./run-tests.sh
# Selecciona: 10 (Levantar Backends DEV)
[INFO] Levantando backends DEV (BackV4:3002, BackV5:3003)...
[SUCCESS] Contenedor ensurance-pharmacy-dev ya está corriendo
[INFO] Esperando a que los backends estén listos (máximo 60s)...
[SUCCESS] ¡Backends listos!
```

### Ejemplo 4: Detener Backends
```bash
./run-tests.sh
# Selecciona: 11 (Detener Backends DEV)
[INFO] Deteniendo backends DEV...
[SUCCESS] Backends detenidos
```

## 🐛 Troubleshooting

### Timeout Esperando Backends
Si ves:
```
[ERROR] Timeout esperando backends. Verifica los logs:
[INFO]   docker logs ensurance-pharmacy-dev
```

**Solución:**
```bash
# Ver logs del contenedor
docker logs ensurance-pharmacy-dev

# Verificar estado
docker ps -a | grep ensurance-pharmacy-dev

# Reiniciar contenedor
docker restart ensurance-pharmacy-dev
```

### Puertos Ocupados
Si los puertos 3002/3003 están ocupados por otro proceso:
```bash
# Verificar qué está usando el puerto
ss -tuln | grep -E ":(3002|3003)"

# O con lsof
lsof -i :3002
lsof -i :3003
```

### Backends No Responden Después de Levantar
```bash
# Verificar healthcheck
docker inspect ensurance-pharmacy-dev | grep -A 10 Health

# Verificar conectividad
curl http://localhost:3002/api/users
curl http://localhost:3003/api2/users
```

## 🔒 Seguridad

- El script solo levanta el contenedor DEV (puerto 3002/3003)
- No modifica configuraciones de producción
- Usa `docker compose -f docker-compose.dev.yml`
- Requiere confirmación interactiva antes de levantar backends

## 📊 Flujo Completo

```
┌─────────────────────────┐
│ Seleccionar Test (1-6)  │
└───────────┬─────────────┘
            │
            ▼
    ┌───────────────┐
    │ Check Backends│
    └───────┬───────┘
            │
      ┌─────┴─────┐
      │   ¿OK?    │
      └─────┬─────┘
       No   │   Sí
    ┌───────┴──────┐
    │              │
    ▼              ▼
┌──────────┐  ┌──────────┐
│ Preguntar│  │ Ejecutar │
│ Levantar?│  │   Test   │
└─────┬────┘  └──────────┘
      │
  ┌───┴───┐
  │  Sí?  │
  └───┬───┘
   No │ Sí
  ┌───┴───────────┐
  │               │
  ▼               ▼
┌────────┐  ┌────────────┐
│ Abortar│  │ Levantar   │
│        │  │ Backends   │
└────────┘  └─────┬──────┘
                  │
            ┌─────┴─────┐
            │  Esperar  │
            │  (60s max)│
            └─────┬─────┘
                  │
            ┌─────┴─────┐
            │  Listo?   │
            └─────┬─────┘
              Sí  │  No
          ┌───────┴────────┐
          │                │
          ▼                ▼
    ┌──────────┐     ┌─────────┐
    │ Ejecutar │     │ Timeout │
    │   Test   │     │  Error  │
    └──────────┘     └─────────┘
```

## 🎓 Ventajas

✅ **Automatización**: No necesitas levantar backends manualmente  
✅ **Seguridad**: Pide confirmación antes de levantar  
✅ **Feedback**: Muestra progreso mientras espera  
✅ **Timeout**: No se queda colgado si falla  
✅ **Flexibilidad**: Puedes levantar/detener desde el menú  
✅ **Verificación**: Prueba conectividad real antes de continuar  

---

**Creado**: 2025-10-07  
**Versión**: 1.0
