import http from 'k6/http';
import { check, sleep } from 'k6';

const targetUrl = __ENV.TARGET_URL || 'http://host.docker.internal:5175';

export const options = {
  vus: 1,
  duration: '30s',
};

export default function () {
  const response = http.get(targetUrl);
  check(response, {
    'status is 200': (r) => r.status === 200,
  });
  sleep(1);
}
