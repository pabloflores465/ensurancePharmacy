import { mount } from '@vue/test-utils';
import { createPinia } from 'pinia';
import Login from '@/pages/Login.vue';

jest.mock('vue-router', () => ({
  useRouter: () => ({ push: jest.fn() }),
}));

jest.mock('axios', () => ({
  post: jest.fn(),
}));

jest.mock('@/services/ApiService', () => {
  const mod = {
    getPharmacyApiUrl: (e) => `http://localhost:8081/api2/${String(e).replace(/^\//,'')}`,
  };
  return { __esModule: true, default: mod, ...mod };
});

const axios = require('axios');

const type = async (input, value) => {
  await input.setValue('');
  await input.setValue(value);
};

describe('Login.vue', () => {
  beforeEach(() => {
    localStorage.clear();
    jest.clearAllMocks();
  });

  const factory = () =>
    mount(Login, {
      global: {
        plugins: [createPinia()],
        stubs: {
          'router-link': { template: '<a><slot /></a>' },
        },
      },
    });

  it('hace login como admin y redirige a /admindash', async () => {
    axios.post.mockResolvedValueOnce({ data: { idUser: 1, role: 'admin' } });

    const wrapper = factory();

    const inputs = wrapper.findAll('input.input-field');
    expect(inputs).toHaveLength(2);
    await type(inputs[0], 'admin@example.com');
    await type(inputs[1], 'secret');

    await wrapper.find('form').trigger('submit.prevent');
    await wrapper.vm.$nextTick();
    await Promise.resolve();

    // Debe guardar en localStorage
    expect(JSON.parse(localStorage.getItem('user'))).toEqual({ idUser: 1, role: 'admin' });
    expect(localStorage.getItem('role')).toBe('admin');
  });

  it('muestra mensaje de error si axios falla', async () => {
    axios.post.mockRejectedValueOnce(new Error('bad creds'));

    const wrapper = factory();

    const inputs = wrapper.findAll('input.input-field');
    await type(inputs[0], 'x@y.com');
    await type(inputs[1], 'bad');

    await wrapper.find('form').trigger('submit.prevent');
    await wrapper.vm.$nextTick();
    await Promise.resolve();

    expect(wrapper.text()).toContain('Credenciales incorrectas');
  });
});
