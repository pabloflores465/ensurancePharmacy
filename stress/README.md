# Stress Tests

Guía rápida para ejecutar los escenarios de estrés de `Ensurance Pharmacy` utilizando los scripts automatizados disponibles en este módulo.

## Scripts Clave

- `run-tests.sh`: menú interactivo para ejecutar pruebas K6 o JMeter y levantar servicios auxiliares.
- `view-jmeter-report.sh`: expone el reporte HTML generado por JMeter en `http://localhost:8085`.
- `cleanup-results.sh`: limpia volúmenes y resultados previos de K6 y JMeter.
- `validate-setup.sh`: verifica dependencias y conectividad antes de lanzar los tests.

## Ejecución Rápida

```bash
./run-tests.sh
```

1. Selecciona la herramienta (`K6` o `JMeter`).
2. Elige el escenario o plan disponible.
3. Sigue las instrucciones para levantar backends si aún no corren.

## Ejecución Manual

### K6

```bash
cd k6/scripts
TEST_SCRIPT=load-test.js ../../run-tests.sh --k6-only
```

### JMeter

```bash
JMETER_PLAN=ensurance-full-test.jmx ./run-tests.sh --jmeter-only
```

## Resultados

- Resultados de K6: volumen Docker `scripts_k6_results`.
- Resultados de JMeter: volumen Docker `scripts_jmeter_results`.
- Usa `view-jmeter-report.sh` para abrir el dashboard JMeter.

## Documentación Detallada

Consulta la documentación ampliada en `documentation/stress/` para encontrar:

- Guía general de la suite.
- Detalles de cada script de K6 y plan de JMeter.
- Buenas prácticas y criterios de éxito.
