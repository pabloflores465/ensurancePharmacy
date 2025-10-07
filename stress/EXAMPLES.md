# Ejemplos de Comandos - Stress Testing

## 🎯 Casos de Uso Comunes

### 1. Test Rápido de Verificación

**JMeter - Test Simple (< 1 minuto):**
```bash
cd scripts
docker-compose -f docker-compose.stress.yml run --rm jmeter
```

**K6 - Test Básico:**
```bash
cd scripts
TEST_SCRIPT=sample-script.js docker-compose -f docker-compose.stress.yml run --rm k6
```

---

### 2. Test de Carga Completo

**Con parámetros personalizados:**
```bash
cd scripts

# JMeter Full Test - 100 usuarios por 10 minutos
JMETER_PLAN=ensurance-full-test.jmx \
BACKV4_URL=http://localhost:8081 \
BACKV5_URL=http://localhost:8082 \
USERS=100 \
RAMP_TIME=120 \
DURATION=600 \
docker-compose -f docker-compose.stress.yml run --rm jmeter
```

**K6 Load Test:**
```bash
cd scripts

# Test de carga progresiva
TEST_SCRIPT=load-test.js \
BACKV4_URL=http://localhost:8081 \
BACKV5_URL=http://localhost:8082 \
docker-compose -f docker-compose.stress.yml run --rm k6
```

---

### 3. Test de Estrés - Encontrar Límites

```bash
cd scripts

# K6 Stress Test - hasta 300 usuarios
TEST_SCRIPT=stress-test.js docker-compose -f docker-compose.stress.yml run --rm k6

# JMeter con alta carga
JMETER_PLAN=ensurance-full-test.jmx \
USERS=500 \
RAMP_TIME=180 \
DURATION=900 \
docker-compose -f docker-compose.stress.yml run --rm jmeter
```

---

### 4. Test de Picos (Spike Test)

```bash
cd scripts

# Simular picos repentinos de tráfico
TEST_SCRIPT=spike-test.js docker-compose -f docker-compose.stress.yml run --rm k6
```

---

### 5. Test de Resistencia (Soak Test)

```bash
cd scripts

# 30 minutos de carga sostenida
TEST_SCRIPT=soak-test.js docker-compose -f docker-compose.stress.yml run --rm k6
```

**⚠️ Nota:** Este test dura 30 minutos. Considera ejecutarlo en segundo plano.

---

### 6. Tests con Grafana Activo

**Paso 1 - Iniciar Grafana:**
```bash
cd scripts
docker-compose -f docker-compose.monitor.yml up -d
```

**Paso 2 - Abrir Dashboard:**
```bash
# Esperar ~30 segundos para que Grafana inicie
open http://localhost:3300  # macOS
# o
xdg-open http://localhost:3300  # Linux
```
- Usuario: `admin`
- Password: `changeme`
- Dashboard: "K6 Stress Testing Dashboard"

**Paso 3 - Ejecutar Test:**
```bash
cd scripts
TEST_SCRIPT=load-test.js docker-compose -f docker-compose.stress.yml run --rm k6
```

**Paso 4 - Ver métricas en tiempo real en Grafana**

---

### 7. Tests en Diferentes Entornos

**Ambiente de Desarrollo:**
```bash
cd scripts
BACKV4_URL=http://localhost:8081 \
BACKV5_URL=http://localhost:8082 \
TEST_SCRIPT=load-test.js \
docker-compose -f docker-compose.stress.yml run --rm k6
```

**Ambiente de QA:**
```bash
cd scripts
BACKV4_URL=http://qa-server:8081 \
BACKV5_URL=http://qa-server:8082 \
TEST_SCRIPT=stress-test.js \
docker-compose -f docker-compose.stress.yml run --rm k6
```

**Servidor Remoto:**
```bash
cd scripts
BACKV4_URL=http://10.128.0.2:8081 \
BACKV5_URL=http://10.128.0.2:8082 \
USERS=200 \
JMETER_PLAN=ensurance-full-test.jmx \
docker-compose -f docker-compose.stress.yml run --rm jmeter
```

---

### 8. Ver Resultados

**K6 Dashboard en Tiempo Real:**
```bash
# Mientras K6 está corriendo
open http://localhost:5665
```

**JMeter HTML Report:**
```bash
cd stress
./view-jmeter-report.sh
# Abre: http://localhost:8080
```

**Ver Resultados Guardados:**
```bash
# K6 Summary
docker run --rm -v ensurance_k6_results:/results alpine cat /results/summary.json | jq

# K6 Resultados completos
docker run --rm -v ensurance_k6_results:/results alpine cat /results/k6-results.json | jq '.metrics'

# JMeter - listar archivos
docker run --rm -v ensurance_jmeter_results:/results alpine ls -lha /results/
```

---

### 9. Análisis de Resultados

**Extraer métricas específicas de K6:**
```bash
# Response time p95
docker run --rm -v ensurance_k6_results:/results alpine cat /results/summary.json | jq '.metrics.http_req_duration.values.p95'

# Error rate
docker run --rm -v ensurance_k6_results:/results alpine cat /results/summary.json | jq '.metrics.http_req_failed.values.rate'

# Total requests
docker run --rm -v ensurance_k6_results:/results alpine cat /results/summary.json | jq '.metrics.http_reqs.values.count'
```

**Copiar resultados a local:**
```bash
# K6 results
docker run --rm -v ensurance_k6_results:/results -v $(pwd):/backup alpine cp -r /results /backup/k6-results

# JMeter report
docker run --rm -v ensurance_jmeter_results:/results -v $(pwd):/backup alpine cp -r /results/report /backup/jmeter-report
```

---

### 10. Comparar Tests

**Ejecutar múltiples tests y comparar:**
```bash
cd scripts

# Test 1 - Carga baja
USERS=50 DURATION=300 JMETER_PLAN=ensurance-full-test.jmx \
docker-compose -f docker-compose.stress.yml run --rm jmeter

# Renombrar resultados
docker run --rm -v ensurance_jmeter_results:/results alpine \
  mv /results/results.jtl /results/test1-50users.jtl

# Test 2 - Carga alta
USERS=200 DURATION=300 JMETER_PLAN=ensurance-full-test.jmx \
docker-compose -f docker-compose.stress.yml run --rm jmeter

# Renombrar resultados
docker run --rm -v ensurance_jmeter_results:/results alpine \
  mv /results/results.jtl /results/test2-200users.jtl
```

---

### 11. Debugging y Troubleshooting

**Ver logs en tiempo real:**
```bash
# K6
docker logs -f ensurance-k6

# JMeter
docker logs -f ensurance-jmeter

# Grafana
docker logs -f ensurance-grafana

# Prometheus
docker logs -f ensurance-prometheus
```

**Verificar conectividad:**
```bash
# Test manual de endpoints
curl -v http://localhost:8081/api/health
curl -v http://localhost:8082/api/health
curl -v http://localhost:8082/api/usuarios
curl -v http://localhost:8082/api/medicamentos
curl -v http://localhost:8082/api/polizas
```

**Inspeccionar volúmenes:**
```bash
# Listar volúmenes
docker volume ls | grep ensurance

# Ver contenido de volumen K6
docker run --rm -v ensurance_k6_results:/results alpine ls -lha /results

# Ver contenido de volumen JMeter
docker run --rm -v ensurance_jmeter_results:/results alpine ls -lha /results
```

---

### 12. Limpieza y Mantenimiento

**Detener servicios:**
```bash
cd scripts
docker-compose -f docker-compose.stress.yml down
docker-compose -f docker-compose.monitor.yml down
```

**Limpiar resultados:**
```bash
cd stress
./cleanup-results.sh
```

**Limpiar todo (incluyendo imágenes):**
```bash
cd scripts
docker-compose -f docker-compose.stress.yml down --volumes --rmi all
docker-compose -f docker-compose.monitor.yml down --volumes --rmi all
```

**Reiniciar desde cero:**
```bash
cd stress
./cleanup-results.sh
cd ../scripts
docker-compose -f docker-compose.monitor.yml up -d
docker-compose -f docker-compose.stress.yml run --rm k6
```

---

### 13. Automatización con Scripts

**Ejecutar suite completa de tests:**
```bash
cd scripts

# Test 1: Load test
TEST_SCRIPT=load-test.js docker-compose -f docker-compose.stress.yml run --rm k6
sleep 10

# Test 2: Stress test
TEST_SCRIPT=stress-test.js docker-compose -f docker-compose.stress.yml run --rm k6
sleep 10

# Test 3: JMeter
JMETER_PLAN=ensurance-full-test.jmx docker-compose -f docker-compose.stress.yml run --rm jmeter
```

**Con archivo de script:**
```bash
#!/bin/bash
# run-all-tests.sh

cd scripts

echo "Iniciando Grafana..."
docker-compose -f docker-compose.monitor.yml up -d
sleep 30

echo "Test 1: K6 Load Test"
TEST_SCRIPT=load-test.js docker-compose -f docker-compose.stress.yml run --rm k6

echo "Test 2: K6 Stress Test"
TEST_SCRIPT=stress-test.js docker-compose -f docker-compose.stress.yml run --rm k6

echo "Test 3: JMeter Full Test"
JMETER_PLAN=ensurance-full-test.jmx docker-compose -f docker-compose.stress.yml run --rm jmeter

echo "Tests completados!"
echo "Ver resultados en: http://localhost:3300"
```

---

### 14. CI/CD Integration

**Jenkins Pipeline:**
```groovy
stage('Stress Testing') {
    steps {
        sh '''
            cd scripts
            TEST_SCRIPT=load-test.js \
            BACKV4_URL=http://backv4:8081 \
            BACKV5_URL=http://backv5:8082 \
            docker-compose -f docker-compose.stress.yml run --rm k6
        '''
    }
}
```

**GitLab CI:**
```yaml
stress-test:
  stage: test
  script:
    - cd scripts
    - TEST_SCRIPT=load-test.js docker-compose -f docker-compose.stress.yml run --rm k6
  only:
    - main
```

---

### 15. Exportar Resultados

**Generar reporte PDF de JMeter:**
```bash
# Primero, asegúrate de tener el reporte HTML
cd stress
./view-jmeter-report.sh &

# En otra terminal, usar wkhtmltopdf o similar
docker run --rm -v $(pwd):/data madnight/docker-wkhtmltopdf \
  wkhtmltopdf http://host.docker.internal:8080 /data/jmeter-report.pdf
```

**Exportar métricas de K6 a CSV:**
```bash
docker run --rm -v ensurance_k6_results:/results -v $(pwd):/export alpine sh -c "
  cat /results/k6-results.json | \
  jq -r '.metrics | to_entries | .[] | [.key, .value.type, .value.values] | @csv' \
  > /export/k6-metrics.csv
"
```

---

## 🎓 Tips y Best Practices

1. **Siempre inicia Grafana primero** si quieres visualización en tiempo real
2. **Usa el menu interactivo** (`./run-tests.sh`) para operación guiada
3. **Valida el setup** antes de tests importantes (`./validate-setup.sh`)
4. **Limpia resultados antiguos** para evitar confusión
5. **Documenta los hallazgos** de cada test importante
6. **Compara resultados** entre diferentes versiones de la app
7. **Ejecuta soak tests en horarios de bajo uso** (30 minutos)
8. **Usa variables de entorno** para diferentes ambientes
9. **Revisa logs** si algo falla
10. **Aumenta carga gradualmente** para encontrar límites reales
