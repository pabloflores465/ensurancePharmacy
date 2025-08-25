import { mount } from '@vue/test-utils';

// Lazily-required mock store object. Using a name starting with `mock` allows Jest to accept it.
const mockStore = { user: null };
jest.mock('@/stores/userStore', () => ({
  useUserStore: jest.fn(() => mockStore),
}));

import Dashboard from '@/pages/Dashboard.vue';

const factory = () => mount(Dashboard);

describe('Dashboard.vue', () => {
  afterEach(() => {
    mockStore.user = null;
  });

  test('muestra panel de administración cuando el usuario es admin', async () => {
    mockStore.user = { role: 'admin', name: 'Admin User' };
    const wrapper = factory();

    expect(wrapper.text()).toContain('Panel de Administración');
    expect(wrapper.text()).toContain('Bienvenido Administrador');
    expect(wrapper.text()).not.toContain('Bienvenido a tu Panel');
    expect(wrapper.text()).not.toContain('Por favor, inicia sesión');
  });

  test('muestra panel de usuario cuando está logueado y no es admin', async () => {
    mockStore.user = { role: 'user', name: 'Regular User' };
    const wrapper = factory();

    expect(wrapper.text()).toContain('Bienvenido a tu Panel');
    expect(wrapper.text()).toContain('tus recetas');
    expect(wrapper.text()).not.toContain('Panel de Administración');
    expect(wrapper.text()).not.toContain('Por favor, inicia sesión');
  });

  test('muestra mensaje para iniciar sesión cuando no hay usuario', async () => {
    mockStore.user = null; // invitado
    const wrapper = factory();

    expect(wrapper.text()).toContain('Bienvenido');
    expect(wrapper.text()).toContain('Por favor, inicia sesión');
    expect(wrapper.text()).not.toContain('Panel de Administración');
    expect(wrapper.text()).not.toContain('Bienvenido a tu Panel');
  });
});
