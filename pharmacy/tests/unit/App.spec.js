import { mount } from '@vue/test-utils';
import { createPinia } from 'pinia';
import App from '@/App.vue';

jest.mock('@/services/ApiService', () => {
  const mod = {
    configureApiPorts: jest.fn(),
    loadPortConfiguration: jest.fn(),
    getPharmacyApiUrl: jest.fn((e) => `http://localhost:8081/api2/${String(e).replace(/^\//,'')}`),
    getEnsuranceApiUrl: jest.fn(),
    defaultPortConfig: { pharmacy: '8081', ensurance: '8080' },
  };
  return { __esModule: true, default: mod, ...mod };
});

// Mock child components to avoid loading their SFCs (e.g., Header.vue uses assets)
jest.mock('@/components/Header.vue', () => ({
  __esModule: true,
  default: { name: 'Header', template: '<div data-test="header" @click="$emit(\'open-port-selector\')"></div>' },
}));
jest.mock('@/components/PortSelector.vue', () => ({
  __esModule: true,
  default: { name: 'PortSelector', template: '<div data-test="port-selector">PortSelector</div>' },
}));

// Stub vue-router composables used by child components if needed
jest.mock('vue-router', () => ({
  useRouter: () => ({ push: jest.fn() }),
}));

describe('App.vue', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('muestra PortSelector cuando skipPortSelector es false', () => {
    localStorage.setItem('skipPortSelector', 'false');

    const wrapper = mount(App, {
      global: {
        plugins: [createPinia()],
        stubs: { 'router-view': { template: '<div />' } },
      },
    });

    // En created(), showPortSelector = !skipPortSelector => true
    expect(wrapper.find('[data-test="port-selector"]').exists()).toBe(true);
  });

  it('oculta PortSelector cuando skipPortSelector es true y lo abre al emitir evento', async () => {
    localStorage.setItem('skipPortSelector', 'true');

    const wrapper = mount(App, {
      global: {
        plugins: [createPinia()],
        stubs: { 'router-view': { template: '<div />' } },
      },
    });

    // Inicialmente oculto
    expect(wrapper.find('[data-test="port-selector"]').exists()).toBe(false);

    // Emitir evento desde Header mockeado
    await wrapper.find('[data-test="header"]').trigger('click');
    await wrapper.vm.$nextTick();

    expect(wrapper.find('[data-test="port-selector"]').exists()).toBe(true);
  });
});
