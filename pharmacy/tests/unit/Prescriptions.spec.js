import { mount } from '@vue/test-utils';
import { nextTick } from 'vue';
import Prescriptions from '@/pages/Prescriptions.vue';

jest.mock('axios', () => ({
  get: jest.fn(),
}));

jest.mock('@/services/ApiService', () => ({
  __esModule: true,
  default: {
    getPharmacyApiUrl: jest.fn((endpoint) => `http://test${endpoint}`),
  },
}));

jest.mock('vue-router', () => ({
  useRouter: jest.fn(),
}));

import axios from 'axios';
import { useRouter } from 'vue-router';

const flush = async () => {
  await nextTick();
  await Promise.resolve();
  await nextTick();
};

const makeRecipes = () => ([
  {
    _id: 'r1',
    created_at: '2024-01-01',
    has_insurance: true,
    patient: { username: 'Juan', email: 'juan@example.com' },
    medicines: [
      {
        _id: 'm1',
        principioActivo: 'Ibuprofeno',
        concentracion: '400mg',
        presentacion: 'Tabletas',
        dosis: '1',
        frecuencia: 'cada 8h',
        duracion: '5 días',
      },
    ],
  },
]);

describe('Prescriptions.vue', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    // Ensure a default user in localStorage
    localStorage.setItem('user', JSON.stringify({ email: 'juan@example.com' }));
  });

  test('muestra loader y luego renderiza lista de recetas', async () => {
    axios.get.mockResolvedValueOnce({ data: makeRecipes() });
    const push = jest.fn();
    useRouter.mockReturnValue({ push });

    const wrapper = mount(Prescriptions);

    // Initially loading
    expect(wrapper.find('.loading-container').exists()).toBe(true);

    await flush();

    // List rendered
    expect(wrapper.find('.prescriptions-list').exists()).toBe(true);
    expect(wrapper.findAll('.prescription-card').length).toBe(1);
    expect(wrapper.text()).toContain('Juan');
    expect(wrapper.text()).toContain('Ibuprofeno');
  });

  test('muestra estado vacío cuando no hay recetas', async () => {
    axios.get.mockResolvedValueOnce({ data: [] });
    const push = jest.fn();
    useRouter.mockReturnValue({ push });

    const wrapper = mount(Prescriptions);
    await flush();

    expect(wrapper.find('.empty-state').exists()).toBe(true);
    expect(wrapper.text()).toContain('No hay recetas disponibles');
  });

  test('goToVerification navega a VerificarCompra cuando encuentra el medicamento', async () => {
    // First call: fetch prescriptions
    axios.get.mockResolvedValueOnce({ data: makeRecipes() });
    // Second call: search medicine by active ingredient
    axios.get.mockResolvedValueOnce({ data: [{ idMedicine: 77 }] });

    const push = jest.fn();
    useRouter.mockReturnValue({ push });

    const wrapper = mount(Prescriptions);
    await flush();

    // Click buy on first medicine
    const buy = wrapper.find('.buy-button');
    expect(buy.exists()).toBe(true);
    await buy.trigger('click');

    await flush();

    expect(push).toHaveBeenCalledWith({
      name: 'VerificarCompra',
      params: { id: '77' },
      query: { recipeId: 'r1' },
    });
  });

  test('goToVerification muestra error cuando no encuentra el medicamento', async () => {
    // First call: fetch prescriptions
    axios.get.mockResolvedValueOnce({ data: makeRecipes() });
    // Second call: search returns empty
    axios.get.mockResolvedValueOnce({ data: [] });

    const push = jest.fn();
    useRouter.mockReturnValue({ push });

    const wrapper = mount(Prescriptions);
    await flush();

    const buy = wrapper.find('.buy-button');
    await buy.trigger('click');
    await flush();

    // Error message displayed
    expect(wrapper.find('.error-message').exists()).toBe(true);
    expect(wrapper.text()).toContain('No se encontró el medicamento');
  });
});
