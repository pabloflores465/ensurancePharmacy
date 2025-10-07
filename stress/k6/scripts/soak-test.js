import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { Rate, Counter } from 'k6/metrics';

const errorRate = new Rate('errors');
const totalRequests = new Counter('total_requests');

const BACKV4_URL = __ENV.BACKV4_URL || 'http://host.docker.internal:8081';
const BACKV5_URL = __ENV.BACKV5_URL || 'http://host.docker.internal:8082';

// Soak test - sustained load over time to detect memory leaks, degradation
export const options = {
  stages: [
    { duration: '2m', target: 50 },   // Ramp up
    { duration: '30m', target: 50 },  // Stay at 50 users for 30 minutes
    { duration: '2m', target: 0 },    // Ramp down
  ],
  thresholds: {
    'http_req_duration': ['p(95)<800'],
    'errors': ['rate<0.02'],
  },
};

export default function () {
  totalRequests.add(1);

  // Simulate realistic user journey
  group('User Journey - BackV4', () => {
    const res = http.get(`${BACKV4_URL}/api/users`);
    check(res, {
      'BackV4 users check passed': (r) => r.status === 200,
    });

    sleep(1);

    const policyRes = http.get(`${BACKV4_URL}/api/policy`);
    check(policyRes, {
      'BackV4 policy check passed': (r) => r.status === 200,
    });
  });

  sleep(2);

  group('User Journey - BackV5', () => {
    // Browse users
    let res = http.get(`${BACKV5_URL}/api2/users`);
    const usersCheck = check(res, {
      'BackV5 users check passed': (r) => r.status === 200,
    });

    sleep(1);

    // Browse medicines
    res = http.get(`${BACKV5_URL}/api2/medicines`);
    const medicinesCheck = check(res, {
      'Browse medicines passed': (r) => r.status === 200,
    });

    sleep(2);

    // Browse orders
    res = http.get(`${BACKV5_URL}/api2/orders`);
    const ordersCheck = check(res, {
      'Browse orders passed': (r) => r.status === 200,
    });

    sleep(2);

    // Browse categories
    res = http.get(`${BACKV5_URL}/api2/categories`);
    const categoriesCheck = check(res, {
      'Browse categories passed': (r) => r.status === 200,
    });

    errorRate.add(!(usersCheck && medicinesCheck && ordersCheck && categoriesCheck));
  });

  sleep(3);
}

export function setup() {
  console.log('=== SOAK TEST STARTING ===');
  console.log('Running sustained load for 30 minutes');
  console.log('Monitoring for memory leaks and performance degradation');
}

export function teardown(data) {
  console.log('=== SOAK TEST COMPLETED ===');
}
