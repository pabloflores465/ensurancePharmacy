import { mount } from '@vue/test-utils';
import { createPinia } from 'pinia';
import Header from '@/components/Header.vue';

// Shared push mock to assert navigations
const mockPush = jest.fn();
jest.mock('vue-router', () => ({
  useRouter: () => ({ push: mockPush }),
}));

describe('Header.vue', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  const factory = () =>
    mount(Header, {
      global: {
        plugins: [createPinia()],
      },
    });

  it('muestra botón de Iniciar Sesión cuando no está loggeado', () => {
    const wrapper = factory();
    const loginBtn = wrapper.find('.login-button');
    expect(loginBtn.exists()).toBe(true);
    expect(loginBtn.text()).toContain('Iniciar Sesión');
  });

  it('muestra enlaces de admin cuando el usuario es admin', () => {
    // Simular usuario admin en localStorage (usado por getUser())
    localStorage.setItem('session', JSON.stringify({ idUser: 1, role: 'admin' }));

    const wrapper = factory();
    const adminLink = wrapper.find('.admin-link');
    expect(adminLink.exists()).toBe(true);
    expect(adminLink.text()).toContain('Dashboard Admin');
  });

  it('emite open-port-selector al pulsar el botón de configuración', async () => {
    const wrapper = factory();

    const btn = wrapper.find('button.admin-config-button');
    expect(btn.exists()).toBe(true);

    await btn.trigger('click');
    expect(wrapper.emitted('open-port-selector')).toBeTruthy();
  });

  it('togglea menú móvil con el botón hamburguesa', async () => {
    const wrapper = factory();
    const menuBtn = wrapper.find('.menu-button');
    expect(menuBtn.exists()).toBe(true);

    expect(wrapper.find('.mobile-menu').exists()).toBe(false);
    await menuBtn.trigger('click');
    expect(wrapper.find('.mobile-menu').exists()).toBe(true);

    await menuBtn.trigger('click');
    expect(wrapper.find('.mobile-menu').exists()).toBe(false);
  });

  it('logout limpia localStorage y navega al inicio', async () => {
    // logged-in state
    localStorage.setItem('session', JSON.stringify({ idUser: 10, role: 'user' }));
    localStorage.setItem('user', JSON.stringify({ idUser: 10, role: 'user' }));
    localStorage.setItem('role', 'user');

    const wrapper = factory();

    const logoutBtn = wrapper.find('button.login-button');
    expect(logoutBtn.exists()).toBe(true);

    await logoutBtn.trigger('click');

    expect(localStorage.getItem('session')).toBeNull();
    expect(localStorage.getItem('user')).toBeNull();
    expect(localStorage.getItem('role')).toBeNull();
    expect(mockPush).toHaveBeenCalledWith('/');
  });

  it('desde menú móvil emite open-port-selector y cierra menú', async () => {
    const wrapper = factory();
    await wrapper.find('.menu-button').trigger('click');
    expect(wrapper.find('.mobile-menu').exists()).toBe(true);

    const mobileCfg = wrapper.find('.mobile-item.admin-config-button');
    expect(mobileCfg.exists()).toBe(true);
    await mobileCfg.trigger('click');

    expect(wrapper.emitted('open-port-selector')).toBeTruthy();
    // toggleMenu también debe cerrar el menú
    await wrapper.vm.$nextTick();
    expect(wrapper.find('.mobile-menu').exists()).toBe(false);
  });
});
