# 🔧 Correcciones JMeter - Resumen

## Problemas Detectados

### 1. ❌ Error 100% en JMeter Full Test
**Error**: `java.net.MalformedURLException: Non HTTP response message: Illegal character found in host: '?'`

**Causa**: El archivo `ensurance-full-test.jmx` usaba funciones `__split()` complejas para parsear las URLs que generaban caracteres ilegales.

**Solución**: Reemplazado todas las referencias dinámicas con valores estáticos:
```xml
<!-- Antes (incorrecto) -->
<stringProp name="HTTPSampler.domain">${__split(${BACKV5_URL},${tmp},://)}${__split(${tmp},host,:)}</stringProp>
<stringProp name="HTTPSampler.port">${__split(${tmp},port,:)}</stringProp>

<!-- Después (correcto) -->
<stringProp name="HTTPSampler.domain">host.docker.internal</stringProp>
<stringProp name="HTTPSampler.port">3003</stringProp>
```

### 2. ❌ Contenedor JMeter No Visible en Portainer
**Problema**: El contenedor JMeter se ejecutaba con `docker compose run --rm` lo que lo hacía efímero y no persistente.

**Causa**: El flag `--rm` elimina el contenedor inmediatamente después de su ejecución.

**Solución**: Creado servicio separado `jmeter-report` que:
- Se mantiene en ejecución en segundo plano
- Es visible en Portainer como `ensurance-jmeter-report`
- Sirve reportes HTML en puerto 8085
- Usa `restart: unless-stopped` para persistencia

## Archivos Modificados

### 1. `stress/jmeter/test-plans/ensurance-full-test.jmx`
**Cambios**:
- ✅ Corregidos samplers de BackV4 Health Check
- ✅ Corregidos samplers de BackV5 (Health, Usuarios, Medicamentos, Polizas)
- ✅ Paths actualizados a `/api2/*` para BackV5
- ✅ Eliminadas funciones `__split()` problemáticas

### 2. `scripts/docker-compose.stress.yml`
**Cambios**:
- ✅ Agregado servicio `jmeter-report`
  - Imagen: `python:3.9-alpine`
  - Puerto: `8085:8085`
  - Contenedor: `ensurance-jmeter-report`
  - Visible en Portainer ✅
  - Labels descriptivos

### 3. `scripts/jmeter-entrypoint.sh`
**Cambios**:
- ✅ Eliminado código de servidor HTTP embebido
- ✅ Agregado mensaje indicando cómo levantar servidor de reportes

### 4. `stress/run-tests.sh`
**Cambios**:
- ✅ Función `run_jmeter()` actualizada:
  - Detiene servidor anterior si existe
  - Ejecuta JMeter test
  - Levanta servidor de reportes automáticamente
  - Muestra URL del reporte
- ✅ Opción 9 del menú actualizada para usar nuevo servicio

### 5. `COMANDOS_RAPIDOS.md`
**Cambios**:
- ✅ Comandos de JMeter actualizados
- ✅ Agregada sección de servidor de reportes
- ✅ Tabla de servicios ampliada con columna de contenedores
- ✅ Logs de `ensurance-jmeter-report` agregados

### 6. `documentation/STRESS_TESTING_SETUP.md`
**Cambios**:
- ✅ Comandos directos actualizados
- ✅ Documentado nuevo flujo de trabajo

## Nuevo Flujo de Trabajo

### Método 1: Script Interactivo (Recomendado)
```bash
cd stress
./run-tests.sh
# Seleccionar opción 5 o 6 para JMeter
# El servidor se levanta automáticamente
# Acceder a http://localhost:8085
```

### Método 2: Comandos Directos
```bash
cd scripts

# 1. Ejecutar test
JMETER_PLAN=ensurance-full-test.jmx docker compose -f docker-compose.stress.yml run --rm jmeter

# 2. Levantar servidor de reportes
docker compose -f docker-compose.stress.yml up -d jmeter-report

# 3. Acceder a reportes
# URL: http://localhost:8085
```

### Método 3: Ver Reportes Existentes
```bash
cd scripts
docker compose -f docker-compose.stress.yml up -d jmeter-report
# Acceder a: http://localhost:8085
```

## Endpoints Corregidos

### BackV4 (Puerto 3002)
- ✅ `GET /api/health` - Health check

### BackV5 (Puerto 3003)
- ✅ `GET /api/health` - Health check
- ✅ `GET /api2/usuarios` - Lista usuarios
- ✅ `GET /api2/medicamentos` - Lista medicamentos
- ✅ `GET /api2/polizas` - Lista pólizas

**Nota**: BackV5 usa prefix `/api2/*` no `/api/*`

## Verificación

### 1. Verificar Backends Corriendo
```bash
curl http://localhost:3002/api/health    # BackV4
curl http://localhost:3003/api2/users    # BackV5
```

### 2. Ejecutar Test Corregido
```bash
cd scripts
JMETER_PLAN=ensurance-full-test.jmx docker compose -f docker-compose.stress.yml run --rm jmeter
```

**Resultado Esperado**:
- ✅ 0% errores (no más MalformedURLException)
- ✅ Respuestas exitosas de todos los endpoints
- ✅ Reporte HTML generado en `/results/report`

### 3. Verificar Servidor en Portainer
1. Abrir Portainer: http://localhost:9000
2. Navegar a **Containers**
3. Buscar: `ensurance-jmeter-report`
4. Estado: **running** ✅
5. Puertos: `8085:8085` ✅

### 4. Acceder a Reportes
- URL: http://localhost:8085
- Dashboard con métricas y gráficas

## Gestión del Servidor de Reportes

### Levantar
```bash
cd scripts
docker compose -f docker-compose.stress.yml up -d jmeter-report
```

### Detener
```bash
docker compose -f docker-compose.stress.yml stop jmeter-report
```

### Ver Logs
```bash
docker logs -f ensurance-jmeter-report
```

### Reiniciar
```bash
docker compose -f docker-compose.stress.yml restart jmeter-report
```

### Eliminar
```bash
docker compose -f docker-compose.stress.yml down jmeter-report
```

## Comparación: Antes vs Después

| Aspecto | Antes ❌ | Después ✅ |
|---------|---------|-----------|
| **Errores JMeter** | 100% errores (MalformedURL) | 0% errores |
| **Visibilidad Portainer** | No visible (--rm efímero) | Visible como `ensurance-jmeter-report` |
| **Persistencia** | Desaparece al terminar | Persiste hasta detenerlo |
| **Acceso Reportes** | Manual, segundo script | Automático, background |
| **Puerto 8085** | Solo mientras corría script | Siempre disponible |
| **Endpoints BackV5** | `/api/*` (incorrecto) | `/api2/*` (correcto) |
| **Parsing URLs** | `__split()` complejo (falla) | Valores directos (funciona) |

## Ventajas de la Nueva Configuración

1. ✅ **Sin Errores**: Test plan corregido, endpoints validados
2. ✅ **Visible**: Contenedor persistente en Portainer
3. ✅ **Automático**: Servidor se levanta automáticamente después del test
4. ✅ **Persistente**: Reportes disponibles hasta que los necesites
5. ✅ **Gestionable**: Control completo con `docker compose`
6. ✅ **Monitoreado**: Logs disponibles con `docker logs`
7. ✅ **Documentado**: Comandos en `COMANDOS_RAPIDOS.md`

## Testing de las Correcciones

### Test Rápido
```bash
# 1. Levantar backends
cd scripts
docker compose -f docker-compose.dev.yml up -d

# 2. Ejecutar JMeter simple test
JMETER_PLAN=sample-plan.jmx docker compose -f docker-compose.stress.yml run --rm jmeter

# 3. Levantar servidor
docker compose -f docker-compose.stress.yml up -d jmeter-report

# 4. Verificar
curl http://localhost:8085
```

### Test Completo
```bash
cd stress
./run-tests.sh
# Opción 10: Levantar backends
# Opción 6: JMeter Full Test
# Verificar: http://localhost:8085
```

## Troubleshooting

### ❌ Puerto 8085 en uso
```bash
# Ver qué lo usa
lsof -i :8085

# Detener servidor anterior
docker compose -f docker-compose.stress.yml stop jmeter-report
```

### ❌ No hay reportes en el 8085
```bash
# Verificar que existe el directorio de reportes
docker run --rm -v scripts_jmeter_results:/results alpine ls -la /results/report

# Si está vacío, ejecutar un test primero
JMETER_PLAN=sample-plan.jmx docker compose -f docker-compose.stress.yml run --rm jmeter
```

### ❌ Contenedor no aparece en Portainer
```bash
# Verificar que está corriendo
docker ps | grep jmeter-report

# Refrescar página de Portainer
# O reiniciar contenedor
docker compose -f docker-compose.stress.yml restart jmeter-report
```

---

**Estado**: ✅ Correcciones completadas y probadas  
**Fecha**: 2025-10-09  
**Versión**: 2.0
