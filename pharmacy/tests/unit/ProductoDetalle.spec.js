import { mount } from '@vue/test-utils';
import { nextTick } from 'vue';
import ProductoDetalle from '@/pages/ProductoDetalle.vue';

jest.mock('axios', () => ({
  get: jest.fn(),
  post: jest.fn(),
  put: jest.fn(),
}));

jest.mock('@/services/ApiService', () => ({
  __esModule: true,
  default: {
    getPharmacyApiUrl: jest.fn((endpoint) => `http://test${endpoint}`),
  },
}));

jest.mock('@/stores/userStore', () => ({
  useUserStore: jest.fn(),
}));

import axios from 'axios';
import { useUserStore } from '@/stores/userStore';

const sampleProducts = [
  {
    idMedicine: 1,
    name: 'Paracetamol 500mg',
    price: 12.5,
    stock: 0,
    activeMedicament: 'Paracetamol',
    concentration: '500mg',
    presentacion: 'Tabletas',
    brand: 'Genfar',
    description: 'Para dolor y fiebre',
    images: [],
  },
  {
    idMedicine: 2,
    name: 'Ibuprofeno 400mg',
    price: 25,
    stock: 5,
    activeMedicament: 'Ibuprofeno',
    concentration: '400mg',
    presentacion: 'Tabletas',
    brand: 'Bayer',
    description: 'Antiinflamatorio',
    images: ['http://example.com/ibup.jpg'],
  },
];

const makeWrapper = async (options = {}) => {
  const { products = sampleProducts, routeId = '2' } = options;
  axios.get.mockResolvedValueOnce({ data: products });

  const push = jest.fn();
  const wrapper = mount(ProductoDetalle, {
    global: {
      stubs: {
        Comentarios: { template: '<div class="stub-comentarios" />' },
      },
      mocks: {
        $route: { params: { id: routeId } },
        $router: { push },
      },
    },
  });

  // allow axios promise to resolve and DOM to update
  await nextTick();
  await Promise.resolve();
  await nextTick();

  return { wrapper, push };
};

describe('ProductoDetalle.vue', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('muestra loading antes de cargar y luego renderiza el producto', async () => {
    axios.get.mockResolvedValueOnce({ data: sampleProducts });
    const push = jest.fn();
    const wrapper = mount(ProductoDetalle, {
      global: {
        stubs: { Comentarios: true },
        mocks: { $route: { params: { id: '2' } }, $router: { push } },
      },
    });

    // initial loading
    expect(wrapper.find('.loading-container').exists()).toBe(true);

    await nextTick();
    await Promise.resolve();
    await nextTick();

    // product details visible
    expect(wrapper.find('.producto-container').exists()).toBe(true);
    expect(wrapper.text()).toContain('Ibuprofeno 400mg');
    expect(wrapper.find('.badge').text()).toContain('Disponible');
    expect(wrapper.find('.precio').text()).toContain('Q25');
    expect(wrapper.text()).toContain('Ibuprofeno'); // ingrediente activo
    expect(wrapper.text()).toContain('Bayer');
  });

  test('controla cantidad con botones y validaciones de stock', async () => {
    const { wrapper } = await makeWrapper();

    const minus = wrapper.find('.qty-btn:nth-of-type(1)');
    const plus = wrapper.find('.qty-btn:nth-of-type(2)');
    const input = wrapper.find('.qty-input');
    const buyBtn = wrapper.find('.comprar-btn');

    // initial
    expect(minus.attributes('disabled')).toBeDefined();
    expect(plus.attributes('disabled')).toBeUndefined();
    expect(buyBtn.attributes('disabled')).toBeUndefined();

    // increment to stock limit
    for (let i = 0; i < 4; i++) {
      await plus.trigger('click');
    }
    expect((input.element).value).toBe('5');
    expect(plus.attributes('disabled')).toBeDefined();

    // set over stock
    await input.setValue('6');
    await nextTick();
    expect(buyBtn.attributes('disabled')).toBeDefined();
  });

  test('redirectToCheckout: redirige a login si no está logueado', async () => {
    useUserStore.mockReturnValue({ user: null });
    const { wrapper, push } = await makeWrapper();

    await wrapper.vm.redirectToCheckout();
    expect(push).toHaveBeenCalledWith('/login');
  });

  test('redirectToCheckout: navega a VerificarCompra si está logueado', async () => {
    useUserStore.mockReturnValue({ user: { idUser: 99 } });
    const { wrapper, push } = await makeWrapper();

    wrapper.vm.quantity = 3;
    await nextTick();
    await wrapper.vm.redirectToCheckout();

    expect(push).toHaveBeenCalledWith({
      name: 'VerificarCompra',
      params: { id: 2 },
      query: { quantity: 3 },
    });
  });
});
