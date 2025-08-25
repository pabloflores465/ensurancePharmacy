import '@testing-library/jest-dom';
import { config } from '@vue/test-utils';

// Stub router-link to avoid needing a full router
config.global.stubs = {
  ...(config.global.stubs || {}),
  'router-link': {
    props: ['to'],
    template: '<a v-bind="$attrs"><slot /></a>',
  },
};

// JSDOM does not implement window.scrollTo
if (!window.scrollTo) {
  window.scrollTo = () => {};
}

// Basic localStorage polyfill safety (jsdom has it, but guard anyway)
if (!('localStorage' in window)) {
  const store = new Map();
  window.localStorage = {
    getItem: (k) => (store.has(k) ? store.get(k) : null),
    setItem: (k, v) => store.set(k, String(v)),
    removeItem: (k) => store.delete(k),
    clear: () => store.clear(),
  };
}
