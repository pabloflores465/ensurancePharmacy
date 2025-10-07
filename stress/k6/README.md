# K6 - Scripts de Stress Testing

## Scripts Disponibles

### 1. sample-script.js
Script básico de ejemplo:
- 1 usuario
- 30 segundos de duración
- GET request simple

### 2. load-test.js
Test de carga con múltiples escenarios:
- **Scenario 1**: Carga constante (10 VUs por 2 min)
- **Scenario 2**: Carga progresiva (0→20→50 VUs)
- **Scenario 3**: Spike test (100 VUs)
- Duración total: ~5 minutos

**Umbrales**:
- p95 < 500ms
- Error rate < 1%

### 3. stress-test.js
Test de estrés progresivo:
- Incremento: 50 → 100 → 200 → 300 usuarios
- Duración: 8 minutos
- Objetivo: Encontrar límites del sistema

**Umbrales**:
- p95 < 1000ms
- Error rate < 5%

### 4. spike-test.js
Test de picos repentinos:
- Normal (10 VUs) → Spike (500 VUs) → Normal
- Duración: 2.5 minutos
- Objetivo: Probar recuperación

**Umbrales**:
- p95 < 2000ms
- Error rate < 10%

### 5. soak-test.js
Test de resistencia:
- 50 usuarios constantes
- Duración: 30 minutos
- Objetivo: Detectar memory leaks y degradación

**Umbrales**:
- p95 < 800ms
- Error rate < 2%

## Uso

### Ejecución Básica

```bash
cd scripts

# Load test
TEST_SCRIPT=load-test.js docker-compose -f docker-compose.stress.yml run --rm k6

# Stress test
TEST_SCRIPT=stress-test.js docker-compose -f docker-compose.stress.yml run --rm k6
```

### Con Variables de Entorno

```bash
BACKV4_URL=http://10.128.0.2:8081 \
BACKV5_URL=http://10.128.0.2:8082 \
TEST_SCRIPT=load-test.js \
docker-compose -f docker-compose.stress.yml run --rm k6
```

### Dashboard en Tiempo Real

Mientras ejecutas K6, accede a:
- **Dashboard Web**: http://localhost:5665

## Métricas Personalizadas

Todos los scripts incluyen:
- `errors`: Rate de errores personalizados
- `custom_response_time`: Trend de tiempos de respuesta
- `total_requests`: Contador de requests totales (soak test)

## Outputs

Los scripts generan:
1. **JSON Results**: `/results/k6-results.json`
2. **Summary JSON**: `/results/summary.json`
3. **Web Dashboard**: `/results/k6-dashboard/`

## Ver Resultados

```bash
# Ver summary
docker run --rm -v scripts_k6_results:/results alpine cat /results/summary.json | jq

# Ver resultados detallados
docker run --rm -v scripts_k6_results:/results alpine cat /results/k6-results.json | jq
```

## Modificar Scripts

### Cambiar Duración

```javascript
export const options = {
  stages: [
    { duration: '5m', target: 100 },  // Cambiar aquí
  ],
};
```

### Agregar Endpoints

```javascript
const res = http.get(`${BACKV5_URL}/api/nuevo-endpoint`);
check(res, {
  'status is 200': (r) => r.status === 200,
});
```

### Modificar Umbrales

```javascript
thresholds: {
  'http_req_duration': ['p(95)<1000'],  // Cambiar aquí
  'errors': ['rate<0.02'],              // Cambiar aquí
}
```

## Ejecutores de K6

K6 soporta diferentes ejecutores:

- **constant-vus**: Número fijo de VUs
- **ramping-vus**: VUs que aumentan/disminuyen
- **constant-arrival-rate**: Rate fijo de requests
- **ramping-arrival-rate**: Rate variable de requests

## Integración con Grafana

Los scripts están configurados para exportar métricas a Prometheus vía Pushgateway.

Ver dashboard: http://localhost:3300 (después de iniciar monitoring stack)

## Crear Nuevos Scripts

Template básico:

```javascript
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '1m', target: 10 },
  ],
  thresholds: {
    'http_req_duration': ['p(95)<500'],
  },
};

export default function () {
  const res = http.get('http://localhost:8081/api/health');
  check(res, {
    'is status 200': (r) => r.status === 200,
  });
  sleep(1);
}
```

## Tips

- **VUs**: Empezar con pocos y aumentar gradualmente
- **Think time**: Usar `sleep()` para simular usuarios reales
- **Checks**: No son assertions, el test continúa si fallan
- **Thresholds**: Definen criterios de éxito/fallo del test
- **Groups**: Organizan requests relacionados
