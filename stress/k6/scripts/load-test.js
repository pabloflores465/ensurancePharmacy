import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { Rate, Counter, Trend, Gauge } from 'k6/metrics';

// Custom metrics para Prometheus
const errorRate = new Rate('errors');
const backv4ResponseTime = new Trend('backv4_response_time');
const backv5ResponseTime = new Trend('backv5_response_time');
const successfulRequests = new Counter('successful_requests');
const failedRequests = new Counter('failed_requests');
const activeConnections = new Gauge('active_connections');

// Configuration
const BACKV4_URL = __ENV.BACKV4_URL || 'http://host.docker.internal:8081';
const BACKV5_URL = __ENV.BACKV5_URL || 'http://host.docker.internal:8082';

// Test scenarios
export const options = {
  scenarios: {
    // Scenario 1: Constant load
    constant_load: {
      executor: 'constant-vus',
      vus: 10,
      duration: '2m',
      startTime: '0s',
    },
    // Scenario 2: Ramping load
    ramping_load: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '30s', target: 20 },
        { duration: '1m', target: 50 },
        { duration: '30s', target: 0 },
      ],
      startTime: '2m',
    },
    // Scenario 3: Spike test
    spike_test: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '10s', target: 100 },
        { duration: '20s', target: 100 },
        { duration: '10s', target: 0 },
      ],
      startTime: '4m',
    },
  },
  thresholds: {
    // 95% of requests must complete below 500ms
    'http_req_duration': ['p(95)<500'],
    // Error rate must be below 1%
    'errors': ['rate<0.01'],
    // 99% of requests must return 200
    'http_req_failed': ['rate<0.01'],
  },
};

export default function () {
  // BackV4 Tests
  group('BackV4 - API Operations', () => {
    activeConnections.add(1);
    
    const usersRes = http.get(`${BACKV4_URL}/api/users`);
    backv4ResponseTime.add(usersRes.timings.duration);
    const usersCheck = check(usersRes, {
      'BackV4 users status is 200': (r) => r.status === 200,
      'BackV4 users response time < 200ms': (r) => r.timings.duration < 200,
    });
    
    if (usersCheck) {
      successfulRequests.add(1);
    } else {
      failedRequests.add(1);
    }
    errorRate.add(!usersCheck);

    sleep(0.5);

    const policyRes = http.get(`${BACKV4_URL}/api/policy`);
    backv4ResponseTime.add(policyRes.timings.duration);
    const policyCheck = check(policyRes, {
      'BackV4 policy status is 200': (r) => r.status === 200,
      'BackV4 policy response time < 500ms': (r) => r.timings.duration < 500,
    });
    
    if (policyCheck) {
      successfulRequests.add(1);
    } else {
      failedRequests.add(1);
    }
    errorRate.add(!policyCheck);
    
    activeConnections.add(-1);
  });

  sleep(1);

  // BackV5 Tests
  group('BackV5 - API Operations', () => {
    activeConnections.add(1);
    
    // List users
    const usersRes = http.get(`${BACKV5_URL}/api2/users`);
    backv5ResponseTime.add(usersRes.timings.duration);
    const usersCheck = check(usersRes, {
      'List users status is 200': (r) => r.status === 200,
      'List users response time < 500ms': (r) => r.timings.duration < 500,
    });
    if (usersCheck) successfulRequests.add(1); else failedRequests.add(1);
    errorRate.add(!usersCheck);

    sleep(0.5);

    // List medicines
    const medicinesRes = http.get(`${BACKV5_URL}/api2/medicines`);
    backv5ResponseTime.add(medicinesRes.timings.duration);
    const medicinesCheck = check(medicinesRes, {
      'List medicines status is 200': (r) => r.status === 200,
      'List medicines response time < 500ms': (r) => r.timings.duration < 500,
    });
    if (medicinesCheck) successfulRequests.add(1); else failedRequests.add(1);
    errorRate.add(!medicinesCheck);

    sleep(0.5);

    // List orders
    const ordersRes = http.get(`${BACKV5_URL}/api2/orders`);
    backv5ResponseTime.add(ordersRes.timings.duration);
    const ordersCheck = check(ordersRes, {
      'List orders status is 200': (r) => r.status === 200,
      'List orders response time < 500ms': (r) => r.timings.duration < 500,
    });
    if (ordersCheck) successfulRequests.add(1); else failedRequests.add(1);
    errorRate.add(!ordersCheck);
    
    activeConnections.add(-1);
  });

  sleep(2);
}

// Setup function - runs once before the test
export function setup() {
  console.log('Starting stress test...');
  console.log(`BackV4 URL: ${BACKV4_URL}`);
  console.log(`BackV5 URL: ${BACKV5_URL}`);
}

// Teardown function - runs once after the test
export function teardown(data) {
  console.log('Stress test completed');
}
