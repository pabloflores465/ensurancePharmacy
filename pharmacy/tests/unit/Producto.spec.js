import { mount } from '@vue/test-utils';
import { nextTick } from 'vue';
import Producto from '@/pages/Producto.vue';

jest.mock('axios', () => ({
  get: jest.fn(),
}));

jest.mock('@/services/ApiService', () => ({
  __esModule: true,
  default: {
    getPharmacyApiUrl: jest.fn((endpoint) => `http://test${endpoint}`),
  },
}));

// Mock route param id as string
jest.mock('vue-router', () => ({
  useRoute: jest.fn(() => ({ params: { id: '2' } })),
}));

import axios from 'axios';

const sampleMedicines = [
  { idMedicine: 1, name: 'Paracetamol 500mg', description: 'Dolor y fiebre', comments: [] },
  { idMedicine: 2, name: 'Ibuprofeno 400mg', description: 'Antiinflamatorio', stock: 5, activeMedicament: 'Ibuprofeno', comments: [] },
  { idMedicine: 3, name: 'Amoxicilina 500mg', description: 'Antibiótico', comments: [] },
];

const mountWithData = async (data = sampleMedicines) => {
  axios.get.mockResolvedValueOnce({ data });
  const wrapper = mount(Producto, {
    global: {
      stubs: {
        Header: { template: '<div class="stub-header" />' },
      },
    },
  });
  await nextTick();
  await Promise.resolve();
  await nextTick();
  return wrapper;
};

describe('Producto.vue', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });

  test('carga el producto por id de ruta y lo muestra', async () => {
    const wrapper = await mountWithData();

    expect(wrapper.find('.product-title').text()).toContain('Ibuprofeno');
    expect(wrapper.find('.product-description').text()).toContain('Antiinflamatorio');
  });

  test('abre y cierra el modal de detalles', async () => {
    const wrapper = await mountWithData();

    expect(wrapper.find('.modal-overlay').exists()).toBe(false);

    await wrapper.find('.open-modal-btn').trigger('click');
    expect(wrapper.find('.modal-overlay').exists()).toBe(true);

    await wrapper.find('.close-modal-btn').trigger('click');
    expect(wrapper.find('.modal-overlay').exists()).toBe(false);
  });

  test('envía un comentario y aparece en la lista', async () => {
    const wrapper = await mountWithData();

    const textarea = wrapper.find('textarea.comment-input');
    await textarea.setValue('Excelente medicamento');

    await wrapper.find('form.comment-form').trigger('submit.prevent');

    // After submit, textarea should clear and comment appear
    expect(wrapper.find('textarea.comment-input').element.value).toBe('');
    expect(wrapper.text()).toContain('Excelente medicamento');
  });
});
