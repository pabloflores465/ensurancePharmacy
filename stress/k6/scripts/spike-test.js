import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { Rate } from 'k6/metrics';

const errorRate = new Rate('errors');

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
  const endpoints = [
    { name: 'BackV4 Users', url: `${BACKV4_URL}/api/users` },
    { name: 'BackV4 Policy', url: `${BACKV4_URL}/api/policy` },
    { name: 'BackV5 Users', url: `${BACKV5_URL}/api2/users` },
    { name: 'BackV5 Medicines', url: `${BACKV5_URL}/api2/medicines` },
    { name: 'BackV5 Orders', url: `${BACKV5_URL}/api2/orders` },
  ];

  // Randomly select an endpoint to simulate real user behavior
  const endpoint = endpoints[Math.floor(Math.random() * endpoints.length)];
  
  const res = http.get(endpoint.url);
  const passed = check(res, {
    [`${endpoint.name} - status is 200`]: (r) => r.status === 200,
  });
  
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
