import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('errors');
const customResponseTime = new Trend('custom_response_time');

// Configuration
const BACKV4_URL = __ENV.BACKV4_URL || 'http://host.docker.internal:8081';
const BACKV5_URL = __ENV.BACKV5_URL || 'http://host.docker.internal:8082';

// Stress test configuration - push system beyond normal capacity
export const options = {
  stages: [
    { duration: '1m', target: 50 },   // Ramp up to normal load
    { duration: '2m', target: 100 },  // Increase to high load
    { duration: '2m', target: 200 },  // Push to stress level
    { duration: '2m', target: 300 },  // Maximum stress
    { duration: '1m', target: 0 },    // Ramp down
  ],
  thresholds: {
    'http_req_duration': ['p(95)<1000', 'p(99)<2000'],
    'errors': ['rate<0.05'], // Allow up to 5% errors during stress
    'http_req_failed': ['rate<0.05'],
  },
};

export default function () {
  // Test BackV4
  group('BackV4 - Stress Test', () => {
    const res = http.get(`${BACKV4_URL}/api/users`);
    const passed = check(res, {
      'status is 200': (r) => r.status === 200,
      'response time < 1s': (r) => r.timings.duration < 1000,
    });
    errorRate.add(!passed);
    customResponseTime.add(res.timings.duration);
  });

  sleep(0.5);

  // Test BackV5 with multiple endpoints
  group('BackV5 - Stress Test', () => {
    const batch = http.batch([
      ['GET', `${BACKV5_URL}/api2/users`],
      ['GET', `${BACKV5_URL}/api2/medicines`],
      ['GET', `${BACKV5_URL}/api2/orders`],
      ['GET', `${BACKV5_URL}/api2/categories`],
    ]);

    batch.forEach((res, index) => {
      const passed = check(res, {
        [`Batch request ${index} status is 200`]: (r) => r.status === 200,
      });
      errorRate.add(!passed);
      customResponseTime.add(res.timings.duration);
    });
  });

  sleep(1);
}

export function setup() {
  console.log('=== STRESS TEST STARTING ===');
  console.log(`Target: ${BACKV4_URL} and ${BACKV5_URL}`);
  console.log('This test will push the system to its limits');
}

export function teardown(data) {
  console.log('=== STRESS TEST COMPLETED ===');
}
