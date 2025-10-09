# JMeter - Planes de Prueba

## Planes Disponibles

### 1. sample-plan.jmx
Plan simple para verificación básica:
- 5 usuarios
- 5 iteraciones
- Endpoint: `/api/health` de BackV4

### 2. ensurance-full-test.jmx
Plan completo con múltiples escenarios:
- **Thread Group 1**: BackV4 Health Monitoring
- **Thread Group 2**: BackV5 API Operations (usuarios, medicamentos, polizas)
- Configuración vía parámetros

## Parámetros Configurables

Puedes pasar parámetros al ejecutar JMeter:

```bash
-JBACKV4_URL=http://localhost:8081
-JBACKV5_URL=http://localhost:8082
-JUSERS=100          # Número de usuarios virtuales
-JRAMP_TIME=60       # Tiempo de ramp-up en segundos
-JDURATION=600       # Duración total en segundos
```

## Ejecutar Localmente (sin Docker)

Si tienes JMeter instalado:

```bash
jmeter -n \
  -t ensurance-full-test.jmx \
  -l results.jtl \
  -e -o report \
  -JBACKV4_URL=http://localhost:8081 \
  -JBACKV5_URL=http://localhost:8082 \
  -JUSERS=50
```

## Ejecutar con Docker

```bash
cd scripts
JMETER_PLAN=ensurance-full-test.jmx \
USERS=100 \
DURATION=600 \
docker-compose -f docker-compose.stress.yml run --rm jmeter
```

## Estructura del Reporte

Los resultados se guardan en el volumen `scripts_jmeter_results`:

```
/results/
├── results.jtl              # Datos raw
└── report/                  # Reporte HTML
    ├── index.html          # Dashboard principal
    ├── content/            # Páginas de detalle
    └── sbadmin2-1.0.7/    # Assets
```

## Visualizar Resultados

```bash
./view-jmeter-report.sh
# O manualmente:
docker run --rm -v scripts_jmeter_results:/results -p 8085:8085 \
  -w /results/report python:3.9 python -m http.server 8085
```

Luego abre: http://localhost:8085

## Crear Nuevos Planes

1. Descarga JMeter GUI localmente
2. Abre un plan existente como template
3. Modifica según necesites
4. Guarda en `/stress/jmeter/test-plans/`
5. Ejecuta con Docker

## Tips

- **Ramp-up time**: Usa al menos 1 segundo por cada 10 usuarios
- **Duration**: Para stress tests reales, mínimo 5-10 minutos
- **Think time**: Los `ConstantTimer` simulan tiempo de pensamiento del usuario
- **Assertions**: Validan que las respuestas sean correctas
