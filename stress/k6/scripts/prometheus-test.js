import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend, Rate, Gauge } from 'k6/metrics';

// Custom metrics para verificar que se exportan correctamente
const customCounter = new Counter('custom_test_counter');
const customTrend = new Trend('custom_test_duration');
const customRate = new Rate('custom_test_success_rate');
const customGauge = new Gauge('custom_test_gauge');

// Configuration
const BACKV4_URL = __ENV.BACKV4_URL || 'http://host.docker.internal:8081';
const BACKV5_URL = __ENV.BACKV5_URL || 'http://host.docker.internal:8082';

export const options = {
  scenarios: {
    prometheus_verification: {
      executor: 'constant-vus',
      vus: 5,
      duration: '2m',
    },
  },
  thresholds: {
    'http_req_duration': ['p(95)<1000'],
    'http_req_failed': ['rate<0.05'],
    'custom_test_success_rate': ['rate>0.95'],
  },
};

export default function () {
  // Test BackV4
  const backv4Response = http.get(`${BACKV4_URL}/api/users`);
  const backv4Success = check(backv4Response, {
    'BackV4 status is 200': (r) => r.status === 200,
    'BackV4 response time OK': (r) => r.timings.duration < 1000,
  });

  // Actualizar métricas personalizadas
  customCounter.add(1);
  customTrend.add(backv4Response.timings.duration);
  customRate.add(backv4Success);
  customGauge.add(backv4Response.timings.duration);

  sleep(1);

  // Test BackV5
  const backv5Response = http.get(`${BACKV5_URL}/api2/users`);
  const backv5Success = check(backv5Response, {
    'BackV5 status is 200': (r) => r.status === 200,
    'BackV5 response time OK': (r) => r.timings.duration < 1000,
  });

  // Actualizar métricas personalizadas
  customCounter.add(1);
  customTrend.add(backv5Response.timings.duration);
  customRate.add(backv5Success);
  customGauge.add(backv5Response.timings.duration);

  sleep(2);
}

export function setup() {
  console.log('='.repeat(60));
  console.log('K6 PROMETHEUS INTEGRATION TEST');
  console.log('='.repeat(60));
  console.log(`BackV4 URL: ${BACKV4_URL}`);
  console.log(`BackV5 URL: ${BACKV5_URL}`);
  console.log('');
  console.log('Este test verifica que las métricas se envían a Prometheus.');
  console.log('');
  console.log('Métricas que se exportarán:');
  console.log('  - k6_http_reqs (counter)');
  console.log('  - k6_http_req_duration (histogram/summary)');
  console.log('  - k6_http_req_failed (rate)');
  console.log('  - k6_vus (gauge)');
  console.log('  - k6_iterations (counter)');
  console.log('  - k6_data_sent (counter)');
  console.log('  - k6_data_received (counter)');
  console.log('  - k6_custom_test_counter (counter)');
  console.log('  - k6_custom_test_duration (histogram/summary)');
  console.log('  - k6_custom_test_success_rate (rate)');
  console.log('  - k6_custom_test_gauge (gauge)');
  console.log('');
  console.log('Verifica en Prometheus: http://localhost:9095');
  console.log('Query: {__name__=~"k6_.*"}');
  console.log('='.repeat(60));
}

export function teardown(data) {
  console.log('');
  console.log('='.repeat(60));
  console.log('Test completado!');
  console.log('');
  console.log('Próximos pasos:');
  console.log('1. Accede a Prometheus: http://localhost:9095');
  console.log('2. Ejecuta query: {__name__=~"k6_.*"}');
  console.log('3. Verifica las métricas listadas en setup()');
  console.log('4. Importa dashboard en Grafana: http://localhost:3300');
  console.log('='.repeat(60));
}
