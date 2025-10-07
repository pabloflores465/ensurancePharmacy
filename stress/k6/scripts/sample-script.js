import http from 'k6/http';
import { check, sleep } from 'k6';

const BACKV4_URL = __ENV.BACKV4_URL || 'http://host.docker.internal:3002';
const BACKV5_URL = __ENV.BACKV5_URL || 'http://host.docker.internal:3003';

export const options = {
  vus: 1,
  duration: '30s',
};

export default function () {
  // Test BackV4
  const backv4Response = http.get(`${BACKV4_URL}/api/users`);
  check(backv4Response, {
    'BackV4 status is 200': (r) => r.status === 200,
  });
  
  sleep(0.5);
  
  // Test BackV5
  const backv5Response = http.get(`${BACKV5_URL}/api2/users`);
  check(backv5Response, {
    'BackV5 status is 200': (r) => r.status === 200,
  });
  
  sleep(1);
}
