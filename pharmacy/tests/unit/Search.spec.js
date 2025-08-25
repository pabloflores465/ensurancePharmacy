import { mount } from '@vue/test-utils';
import Search from '@/components/search.vue';

describe('search.vue', () => {
  const items = [
    { id: 1, name: 'Aspirina', category: 'Analgesico', brand: 'Bayer' },
    { id: 2, name: 'Paracetamol', category: 'Analgesico', brand: 'Genfar' },
    { id: 3, name: 'Amoxicilina', category: 'Antibiotico', brand: 'MK' },
  ];

  const fieldNames = ['Nombre', 'Categoría', 'Marca'];
  const searchFields = ['name', 'category', 'brand'];

  const mountCmp = () =>
    mount(Search, {
      props: {
        fieldNames,
        searchFields,
        output: items,
      },
    });

  it('renderiza checkboxes de filtros según props.searchFields', () => {
    const wrapper = mountCmp();
    const checkboxes = wrapper.findAll('input[type="checkbox"]');
    expect(checkboxes.length).toBe(searchFields.length);
  });

  it('emite lista filtrada por texto cuando no hay filtros activos', async () => {
    const wrapper = mountCmp();

    const input = wrapper.find('input#search');
    await input.setValue('asp');

    const emits = wrapper.emitted('update:output');
    expect(emits).toBeTruthy();
    const last = emits[emits.length - 1][0];
    expect(last.map((i) => i.id)).toEqual([1]);
  });

  it('emite lista filtrada por campos activos', async () => {
    const wrapper = mountCmp();

    // Activar filtro por categoría solamente
    const checkboxes = wrapper.findAll('input[type="checkbox"]');
    await checkboxes[1].setValue(true);

    const input = wrapper.find('input#search');
    await input.setValue('analg');

    const emits = wrapper.emitted('update:output');
    expect(emits).toBeTruthy();
    const last = emits[emits.length - 1][0];
    // Ambos primeros pertenecen a Analgesico
    expect(last.map((i) => i.id)).toEqual([1, 2]);
  });

  it('restaura lista inicial al limpiar búsqueda', async () => {
    const wrapper = mountCmp();

    const input = wrapper.find('input#search');
    await input.setValue('amo');
    await input.setValue('');

    const emits = wrapper.emitted('update:output');
    const last = emits[emits.length - 1][0];
    expect(last).toEqual(items);
  });
});
