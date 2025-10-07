import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('errors');

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
    const usersRes = http.get(`${BACKV4_URL}/api/users`);
    const usersCheck = check(usersRes, {
      'BackV4 users status is 200': (r) => r.status === 200,
      'BackV4 users response time < 200ms': (r) => r.timings.duration < 200,
    });
    errorRate.add(!usersCheck);

    sleep(0.5);

    const policyRes = http.get(`${BACKV4_URL}/api/policy`);
    const policyCheck = check(policyRes, {
      'BackV4 policy status is 200': (r) => r.status === 200,
      'BackV4 policy response time < 500ms': (r) => r.timings.duration < 500,
    });
    errorRate.add(!policyCheck);
  });

  sleep(1);

  // BackV5 Tests
  group('BackV5 - API Operations', () => {
    // List users
    const usersRes = http.get(`${BACKV5_URL}/api2/users`);
    const usersCheck = check(usersRes, {
      'List users status is 200': (r) => r.status === 200,
      'List users response time < 500ms': (r) => r.timings.duration < 500,
    });
    errorRate.add(!usersCheck);

    sleep(0.5);

    // List medicines
    const medicinesRes = http.get(`${BACKV5_URL}/api2/medicines`);
    const medicinesCheck = check(medicinesRes, {
      'List medicines status is 200': (r) => r.status === 200,
      'List medicines response time < 500ms': (r) => r.timings.duration < 500,
    });
    errorRate.add(!medicinesCheck);

    sleep(0.5);

    // List orders
    const ordersRes = http.get(`${BACKV5_URL}/api2/orders`);
    const ordersCheck = check(ordersRes, {
      'List orders status is 200': (r) => r.status === 200,
      'List orders response time < 500ms': (r) => r.timings.duration < 500,
    });
    errorRate.add(!ordersCheck);
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
