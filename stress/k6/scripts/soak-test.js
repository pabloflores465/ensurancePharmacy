import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { Rate, Counter, Trend, Gauge } from 'k6/metrics';

// Custom metrics para Prometheus
const errorRate = new Rate('errors');
const totalRequests = new Counter('total_requests');
const backv4ResponseTime = new Trend('backv4_response_time');
const backv5ResponseTime = new Trend('backv5_response_time');
const successfulRequests = new Counter('successful_requests');
const failedRequests = new Counter('failed_requests');
const soakDuration = new Gauge('soak_duration_minutes');
const memoryLeakIndicator = new Trend('memory_leak_indicator');

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
  
  // Track soak test duration (approximation based on iterations)
  soakDuration.add(__ITER / 60); // Rough estimate in minutes
  
  // Track response time trend to detect degradation
  const startTime = Date.now();

  // Simulate realistic user journey
  group('User Journey - BackV4', () => {
    const res = http.get(`${BACKV4_URL}/api/users`);
    backv4ResponseTime.add(res.timings.duration);
    
    const usersCheck = check(res, {
      'BackV4 users check passed': (r) => r.status === 200,
    });
    if (usersCheck) successfulRequests.add(1); else failedRequests.add(1);

    sleep(1);

    const policyRes = http.get(`${BACKV4_URL}/api/policy`);
    backv4ResponseTime.add(policyRes.timings.duration);
    
    const policyCheck = check(policyRes, {
      'BackV4 policy check passed': (r) => r.status === 200,
    });
    if (policyCheck) successfulRequests.add(1); else failedRequests.add(1);
  });

  sleep(2);

  group('User Journey - BackV5', () => {
    // Browse users
    let res = http.get(`${BACKV5_URL}/api2/users`);
    backv5ResponseTime.add(res.timings.duration);
    const usersCheck = check(res, {
      'BackV5 users check passed': (r) => r.status === 200,
    });
    if (usersCheck) successfulRequests.add(1); else failedRequests.add(1);

    sleep(1);

    // Browse medicines
    res = http.get(`${BACKV5_URL}/api2/medicines`);
    backv5ResponseTime.add(res.timings.duration);
    const medicinesCheck = check(res, {
      'Browse medicines passed': (r) => r.status === 200,
    });
    if (medicinesCheck) successfulRequests.add(1); else failedRequests.add(1);

    sleep(2);

    // Browse orders
    res = http.get(`${BACKV5_URL}/api2/orders`);
    backv5ResponseTime.add(res.timings.duration);
    const ordersCheck = check(res, {
      'Browse orders passed': (r) => r.status === 200,
    });
    if (ordersCheck) successfulRequests.add(1); else failedRequests.add(1);

    sleep(2);

    // Browse categories
    res = http.get(`${BACKV5_URL}/api2/categories`);
    backv5ResponseTime.add(res.timings.duration);
    const categoriesCheck = check(res, {
      'Browse categories passed': (r) => r.status === 200,
    });
    if (categoriesCheck) successfulRequests.add(1); else failedRequests.add(1);

    errorRate.add(!(usersCheck && medicinesCheck && ordersCheck && categoriesCheck));
  });
  
  // Calculate memory leak indicator based on response time degradation
  const totalTime = Date.now() - startTime;
  memoryLeakIndicator.add(totalTime);

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
