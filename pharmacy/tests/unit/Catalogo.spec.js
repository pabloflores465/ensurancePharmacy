import { mount } from '@vue/test-utils';
import { nextTick } from 'vue';
import Catalogo from '@/pages/Catalogo.vue';

jest.mock('axios', () => ({
  get: jest.fn(),
}));

// Mock ApiService default export shape
jest.mock('@/services/ApiService', () => ({
  __esModule: true,
  default: {
    getPharmacyApiUrl: jest.fn((endpoint) => `http://test${endpoint}`),
  },
}));

import axios from 'axios';

const sampleProducts = [
  {
    idMedicine: 1,
    name: 'Paracetamol 500mg',
    activeMedicament: 'Paracetamol',
    brand: 'Genfar',
    price: 12.5,
    prescription: false,
  },
  {
    idMedicine: 2,
    name: 'Ibuprofeno 400mg',
    activeMedicament: 'Ibuprofeno',
    brand: 'Bayer',
    price: 25.0,
    prescription: true,
  },
  {
    idMedicine: 3,
    name: 'Amoxicilina 500mg',
    activeMedicament: 'Amoxicilina',
    brand: 'MK',
    price: 40.0,
    prescription: false,
  },
];

const mountWithData = async (data = sampleProducts) => {
  axios.get.mockResolvedValueOnce({ data });
  const wrapper = mount(Catalogo);
  // Before resolve, loading should show
  expect(wrapper.find('.loading-container').exists()).toBe(true);
  await nextTick();
  // allow promise resolution
  await Promise.resolve();
  await nextTick();
  return wrapper;
};

describe('Catalogo.vue', () => {
  afterEach(() => {
    jest.clearAllMocks();
    // Reset body overflow potentially changed by modal
    document.body.style.overflow = '';
  });

  test('carga y renderiza productos, ocultando el indicador de carga', async () => {
    const wrapper = await mountWithData();

    // Cards rendered
    const cards = wrapper.findAll('.product-card');
    expect(cards.length).toBe(sampleProducts.length);

    // Loading hidden
    expect(wrapper.find('.loading-container').exists()).toBe(false);

    // Router link button exists and shows CTA text
    const buyLink = wrapper.find('.buy-button');
    expect(buyLink.exists()).toBe(true);
    expect(buyLink.text()).toContain('Comprar');
  });

  test('filtra por texto, principio activo, marca y rango de precios', async () => {
    const wrapper = await mountWithData();

    // By default, shows all
    expect(wrapper.findAll('.product-card').length).toBe(3);

    // Filter by name contains "ibu"
    await wrapper.find('input[placeholder="Buscar medicamento..."]').setValue('ibu');
    await nextTick();
    expect(wrapper.findAll('.product-card').length).toBe(1);
    expect(wrapper.text()).toContain('Ibuprofeno');

    // Clear name search, filter by active ingredient 'Paracetamol'
    await wrapper.find('input[placeholder="Buscar medicamento..."]').setValue('');
    await wrapper.find('input[placeholder="Principio activo"]').setValue('parac');
    await nextTick();
    expect(wrapper.findAll('.product-card').length).toBe(1);
    expect(wrapper.text()).toContain('Paracetamol');

    // Filter by brand 'MK'
    await wrapper.find('input[placeholder="Principio activo"]').setValue('');
    await wrapper.find('input[placeholder="Marca"]').setValue('mk');
    await nextTick();
    expect(wrapper.findAll('.product-card').length).toBe(1);
    expect(wrapper.text()).toContain('Amoxicilina');

    // Filter by price range min 20 max 30 -> only Ibuprofeno 25
    await wrapper.find('input[placeholder="Marca"]').setValue('');
    await wrapper.find('input[placeholder="Precio mín"]').setValue('20');
    await wrapper.find('input[placeholder="Precio máx"]').setValue('30');
    await nextTick();
    expect(wrapper.findAll('.product-card').length).toBe(1);
    expect(wrapper.text()).toContain('Ibuprofeno');
  });

  test('abre y cierra el modal de detalles, controlando el scroll del body', async () => {
    const wrapper = await mountWithData();

    // Open modal from first card
    const detailsBtn = wrapper.find('.details-button');
    expect(detailsBtn.exists()).toBe(true);
    await detailsBtn.trigger('click');

    expect(wrapper.find('.modal-content').exists()).toBe(true);
    expect(document.body.style.overflow).toBe('hidden');

    // Close modal
    const closeBtn = wrapper.find('.close-modal');
    expect(closeBtn.exists()).toBe(true);
    await closeBtn.trigger('click');

    expect(wrapper.find('.modal-content').exists()).toBe(false);
    expect(document.body.style.overflow).toBe('auto');
  });
});
