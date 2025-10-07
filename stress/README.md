# Ensurance Pharmacy - Stress Testing Suite

Configuración completa de stress testing con **Apache JMeter** y **K6 (Grafana)** con dashboards de visualización en tiempo real.

## 📁 Estructura

```
stress/
├── jmeter/
│   ├── test-plans/
│   │   ├── sample-plan.jmx              # Plan simple
│   │   └── ensurance-full-test.jmx      # Plan completo
│   └── README.md
├── k6/
│   ├── scripts/
│   │   ├── sample-script.js             # Script básico
│   │   ├── load-test.js                 # Carga progresiva
│   │   ├── stress-test.js               # Test de estrés
│   │   ├── spike-test.js                # Picos de carga
│   │   └── soak-test.js                 # Test de resistencia
│   └── README.md
├── run-tests.sh                         # 🎯 Menu interactivo
├── view-jmeter-report.sh               # Ver reportes JMeter
├── cleanup-results.sh                   # Limpiar resultados
├── .env.example                         # Variables de entorno
├── QUICKSTART.md                        # 🚀 Guía rápida
├── STRESS_TESTING_GUIDE.md             # 📚 Guía completa
└── README.md                            # Este archivo
```

## 🚀 Inicio Rápido

### Paso 1: Usar el menú interactivo (levanta backends automáticamente)
```bash
cd stress
./run-tests.sh
```

**Nota:** El script detecta si los backends (BackV4:3002, BackV5:3003) están corriendo. Si no lo están, te preguntará si deseas levantarlos automáticamente.

### Paso 2: Ver resultados
- **K6 Real-time**: http://localhost:5665
- **Grafana**: http://localhost:3300 (admin/changeme)
- **JMeter**: Ejecutar `./view-jmeter-report.sh` → http://localhost:8085

## 📖 Documentación

- **[QUICKSTART.md](./QUICKSTART.md)** - Guía rápida de 5 minutos
- **[STRESS_TESTING_GUIDE.md](./STRESS_TESTING_GUIDE.md)** - Guía completa detallada
- **[jmeter/README.md](./jmeter/README.md)** - Documentación de JMeter
- **[k6/README.md](./k6/README.md)** - Documentación de K6

## 🎯 Tests Disponibles

### K6 Tests
1. **load-test.js** - Carga progresiva con múltiples escenarios (5 min)
2. **stress-test.js** - Push hasta 300 usuarios para encontrar límites (8 min)
3. **spike-test.js** - Picos repentinos de 500 usuarios (2.5 min)
4. **soak-test.js** - 50 usuarios constantes por 30 minutos

### JMeter Tests
1. **sample-plan.jmx** - Test simple de verificación
2. **ensurance-full-test.jmx** - Test completo con BackV4 y BackV5

## 🛠️ Herramientas Incluidas

### Apache JMeter 5.6.3
- Configuración vía parámetros (usuarios, ramp-time, duración)
- Generación automática de reportes HTML
- Tests de BackV4 y BackV5

### K6 (Grafana) 0.49.0
- Dashboard web en tiempo real
- Múltiples escenarios (load, stress, spike, soak)
- Métricas exportadas a Prometheus
- Checks y thresholds personalizados

### Grafana + Prometheus
- Dashboard pre-configurado para K6
- Métricas en tiempo real
- Visualización de VUs, throughput, response times, error rates

## 🔧 Configuración

### Variables de Entorno

Copia `.env.example` a `.env` y modifica según necesites:

```bash
cp .env.example .env
```

Variables disponibles:
- `BACKV4_URL` - URL de BackV4 (default: http://host.docker.internal:8081)
- `BACKV5_URL` - URL de BackV5 (default: http://host.docker.internal:8082)
- `TEST_SCRIPT` - Script de K6 a ejecutar
- `JMETER_PLAN` - Plan de JMeter a ejecutar
- `USERS` - Número de usuarios para JMeter
- `RAMP_TIME` - Tiempo de ramp-up para JMeter
- `DURATION` - Duración total para JMeter

## 📊 Dashboards

### Grafana K6 Dashboard
Incluye paneles para:
- Virtual Users activos
- Request Rate (req/sec)
- Response Time Percentiles (p50, p95, p99)
- Error Rate
- Total HTTP Requests
- Failed Checks
- Total Iterations

### JMeter HTML Report
Incluye:
- Statistics (min, max, avg, p90, p95, p99)
- Throughput over time
- Response times over time
- Active threads over time
- Transactions per second

## 🔍 Ver Resultados

### K6
```bash
# Dashboard en tiempo real (mientras corre)
open http://localhost:5665

# Ver summary JSON
docker run --rm -v scripts_k6_results:/results alpine cat /results/summary.json

# Ver todos los archivos
docker run --rm -v scripts_k6_results:/results alpine ls -lh /results
```

### JMeter
```bash
# Servidor HTTP con reporte
./view-jmeter-report.sh
# Abre: http://localhost:8085

# Ver archivos
docker run --rm -v scripts_jmeter_results:/results alpine ls -lh /results
```

## 🧹 Mantenimiento

### Limpiar resultados anteriores
```bash
./cleanup-results.sh
```

### Detener servicios
```bash
cd scripts
docker-compose -f docker-compose.stress.yml down
docker-compose -f docker-compose.monitor.yml down
```

## 📈 Criterios de Éxito

### Umbrales K6

| Test | p95 Response Time | Error Rate |
|------|------------------|------------|
| Load Test | < 500ms | < 1% |
| Stress Test | < 1000ms | < 5% |
| Spike Test | < 2000ms | < 10% |
| Soak Test | < 800ms | < 2% |

### Endpoints Testeados

**BackV4 (8081)**:
- `/api/health`

**BackV5 (8082)**:
- `/api/health`
- `/api/usuarios`
- `/api/medicamentos`
- `/api/polizas`

## 🤝 Contribuir

Para agregar nuevos tests:

1. **K6**: Crear script en `k6/scripts/` basado en los existentes
2. **JMeter**: Abrir JMeter GUI, crear plan, guardar en `jmeter/test-plans/`
3. Actualizar documentación correspondiente

## 📞 Soporte

Si encuentras problemas:

1. Verifica que las apps estén corriendo
2. Revisa logs: `docker logs ensurance-k6` o `docker logs ensurance-jmeter`
3. Consulta [STRESS_TESTING_GUIDE.md](./STRESS_TESTING_GUIDE.md) para troubleshooting
4. Revisa la consola web de K6: http://localhost:5665

## 🎓 Recursos Adicionales

- [K6 Documentation](https://k6.io/docs/)
- [JMeter User Manual](https://jmeter.apache.org/usermanual/index.html)
- [Grafana Documentation](https://grafana.com/docs/)
- [Prometheus Documentation](https://prometheus.io/docs/)

---

**Creado como parte del proyecto Ensurance Pharmacy - Fase III**

Herramientas aprobadas por el catedrático según requisitos del proyecto.
