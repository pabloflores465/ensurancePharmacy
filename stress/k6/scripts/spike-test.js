import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { Rate, Counter, Trend, Gauge } from 'k6/metrics';

// Custom metrics para Prometheus
const errorRate = new Rate('errors');
const spikeResponseTime = new Trend('spike_response_time');
const backv4ResponseTime = new Trend('backv4_response_time');
const backv5ResponseTime = new Trend('backv5_response_time');
const successfulRequests = new Counter('successful_requests');
const failedRequests = new Counter('failed_requests');
const concurrentUsers = new Gauge('concurrent_users');

const BACKV4_URL = __ENV.BACKV4_URL || 'http://host.docker.internal:8081';
const BACKV5_URL = __ENV.BACKV5_URL || 'http://host.docker.internal:8082';

// Spike test - sudden increase in load
export const options = {
  stages: [
    { duration: '30s', target: 10 },    // Normal load
    { duration: '10s', target: 500 },   // Sudden spike!
    { duration: '1m', target: 500 },    // Maintain spike
    { duration: '10s', target: 10 },    // Drop to normal
    { duration: '30s', target: 0 },     // Ramp down
  ],
  thresholds: {
    'http_req_duration': ['p(95)<2000'], // More lenient during spike
    'errors': ['rate<0.1'], // Allow up to 10% errors during spike
  },
};

export default function () {
  // Track concurrent users
  concurrentUsers.add(__VU);
  
  const endpoints = [
    { name: 'BackV4 Users', url: `${BACKV4_URL}/api/users`, backend: 'backv4' },
    { name: 'BackV4 Policy', url: `${BACKV4_URL}/api/policy`, backend: 'backv4' },
    { name: 'BackV5 Users', url: `${BACKV5_URL}/api2/users`, backend: 'backv5' },
    { name: 'BackV5 Medicines', url: `${BACKV5_URL}/api2/medicines`, backend: 'backv5' },
    { name: 'BackV5 Orders', url: `${BACKV5_URL}/api2/orders`, backend: 'backv5' },
  ];

  // Randomly select an endpoint to simulate real user behavior
  const endpoint = endpoints[Math.floor(Math.random() * endpoints.length)];
  
  const res = http.get(endpoint.url);
  spikeResponseTime.add(res.timings.duration);
  
  // Track response time per backend
  if (endpoint.backend === 'backv4') {
    backv4ResponseTime.add(res.timings.duration);
  } else {
    backv5ResponseTime.add(res.timings.duration);
  }
  
  const passed = check(res, {
    [`${endpoint.name} - status is 200`]: (r) => r.status === 200,
  });
  
  if (passed) successfulRequests.add(1); else failedRequests.add(1);
  errorRate.add(!passed);
  
  sleep(0.1); // Minimal sleep during spike test
}

export function setup() {
  console.log('=== SPIKE TEST STARTING ===');
  console.log('Testing system recovery from sudden traffic spike');
}

export function teardown(data) {
  console.log('=== SPIKE TEST COMPLETED ===');
}
