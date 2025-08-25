import { mount, flushPromises } from '@vue/test-utils';
import { createPinia } from 'pinia';
import Cart from '@/components/Cart.vue';

jest.mock('@/services/ApiService', () => {
  const mod = {
    getPharmacyApiUrl: (e) => `http://localhost:8081/api2/${String(e).replace(/^\//,'')}`,
  };
  return { __esModule: true, default: mod, ...mod };
});

jest.mock('axios', () => ({
  get: jest.fn(),
  delete: jest.fn(),
  put: jest.fn(),
}));

const axios = require('axios');

const factory = async () => {
  const wrapper = mount(Cart, {
    global: {
      plugins: [createPinia()],
    },
  });
  await flushPromises();
  return wrapper;
};

describe('Cart.vue', () => {
  let alertSpy;
  let originalLocation;

  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();

    // Fake user in session for store.getUser()
    localStorage.setItem('session', JSON.stringify({ idUser: 123, role: 'user' }));

    // Mock window.alert and window.location
    alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {});
    originalLocation = window.location;
    Object.defineProperty(window, 'location', {
      writable: true,
      value: { href: '' },
    });
  });

  afterEach(() => {
    alertSpy.mockRestore();
    window.location = originalLocation;
  });

  it('muestra mensaje cuando no hay orden en progreso', async () => {
    axios.get.mockResolvedValueOnce({ data: [] }); // /orders

    const wrapper = await factory();
    expect(wrapper.text()).toContain('No tienes una orden en progreso.');
  });

  it('lista ítems cuando hay orden en progreso', async () => {
    // /orders
    axios.get.mockResolvedValueOnce({
      data: [
        { idOrder: 1, status: 'En progreso', user: { idUser: 123 } },
        { idOrder: 2, status: 'Completado', user: { idUser: 999 } },
      ],
    });
    // /order_medicines
    axios.get.mockResolvedValueOnce({
      data: [
        { orders: { idOrder: 1 }, medicine: { idMedicine: 77, name: 'Paracetamol' }, quantity: 2, cost: 10, total: 20 },
        { orders: { idOrder: 2 }, medicine: { idMedicine: 88, name: 'Otro' }, quantity: 1, cost: 5, total: 5 },
      ],
    });

    const wrapper = await factory();
    await flushPromises();

    // Debe mostrar la tabla y el item filtrado
    expect(wrapper.find('table').exists()).toBe(true);
    expect(wrapper.text()).toContain('Paracetamol');
    expect(wrapper.text()).toContain('Completar compra');
  });

  it('completa la compra con datos de tarjeta válidos', async () => {
    // /orders
    axios.get.mockResolvedValueOnce({
      data: [
        { idOrder: 3, status: 'En progreso', user: { idUser: 123 } },
      ],
    });
    // /order_medicines
    axios.get.mockResolvedValueOnce({ data: [] });

    axios.put.mockResolvedValueOnce({});

    const wrapper = await factory();
    await flushPromises();

    // Abrir modal
    await wrapper.find('button').trigger('click'); // Completar compra

    const inputs = wrapper.findAll('input');
    // cardNumber, cardExpiry, cardCvv en ese orden
    await inputs[0].setValue('4111111111111111');
    await inputs[1].setValue('12/29');
    await inputs[2].setValue('123');

    // Confirmar compra
    const confirmBtn = wrapper.find('.modal-content button');
    await confirmBtn.trigger('click');
    await flushPromises();

    expect(axios.put).toHaveBeenCalledWith(
      expect.stringContaining('/orders'),
      expect.objectContaining({ idOrder: 3, status: 'Completado' })
    );
    expect(alertSpy).toHaveBeenCalled();
    expect(window.location.href).toBe('/');
  });
});
